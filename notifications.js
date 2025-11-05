import express from 'express';

const router = express.Router();

// Get all notifications for a user
router.get('/:userId', (req, res) => {
  const { userId } = req.params;
  const db = req.app.locals.db;

  try {
    const notifications = db.prepare(`
      SELECT * FROM notifications 
      WHERE user_id = ? 
      ORDER BY created_at DESC
    `).all(userId);

    res.json(notifications.map(n => ({
      id: n.id.toString(),
      type: n.type,
      text: n.text,
      createdAt: new Date(n.created_at).getTime(),
      read: n.read === 1 || n.read === true,
    })));
  } catch (error) {
    console.error('Get notifications error:', error);
    res.status(500).json({ error: 'Failed to get notifications' });
  }
});

// Add notification
router.post('/', (req, res) => {
  const { userId, type = 'System', text } = req.body;
  const db = req.app.locals.db;

  try {
    if (!userId || !text) {
      return res.status(400).json({ error: 'userId and text are required' });
    }

    const result = db.prepare(`
      INSERT INTO notifications (user_id, type, text, read)
      VALUES (?, ?, ?, 0)
    `).run(userId, type, text);

    const notification = db.prepare('SELECT * FROM notifications WHERE id = ?').get(result.lastInsertRowid);

    res.json({
      id: notification.id.toString(),
      type: notification.type,
      text: notification.text,
      createdAt: new Date(notification.created_at).getTime(),
      read: false,
    });
  } catch (error) {
    console.error('Add notification error:', error);
    res.status(500).json({ error: 'Failed to add notification' });
  }
});

// Mark all notifications as read
router.put('/:userId/read-all', (req, res) => {
  const { userId } = req.params;
  const db = req.app.locals.db;

  try {
    db.prepare('UPDATE notifications SET read = 1 WHERE user_id = ?').run(userId);
    res.json({ message: 'All notifications marked as read' });
  } catch (error) {
    console.error('Mark read error:', error);
    res.status(500).json({ error: 'Failed to mark notifications as read' });
  }
});

// Clear all notifications
router.delete('/:userId', (req, res) => {
  const { userId } = req.params;
  const db = req.app.locals.db;

  try {
    db.prepare('DELETE FROM notifications WHERE user_id = ?').run(userId);
    res.json({ message: 'All notifications cleared' });
  } catch (error) {
    console.error('Clear notifications error:', error);
    res.status(500).json({ error: 'Failed to clear notifications' });
  }
});

export default router;

