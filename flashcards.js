import express from 'express';
import { calculateLevel } from '../utils/levelCalculator.js';
import { saveDatabase } from '../db.js';

const router = express.Router();

// Get all flashcard sets
router.get('/', (req, res) => {
  const db = req.app.locals.db;

  try {
    const sets = db.prepare(`
      SELECT fs.*,
        (SELECT COUNT(*) FROM flashcard_cards WHERE flashcard_set_id = fs.id) as card_count
      FROM flashcard_sets fs
      WHERE fs.is_public = 1
      ORDER BY fs.created_at DESC
    `).all();

    const setsWithCards = sets.map(set => {
      const cards = db.prepare(`
        SELECT * FROM flashcard_cards
        WHERE flashcard_set_id = ?
        ORDER BY card_order
      `).all(set.id);

      return {
        id: set.id.toString(),
        title: set.title,
        cards: cards.map(c => ({
          id: c.id.toString(),
          front: c.front_text,
          back: c.back_text,
        })),
        completedBy: [],
      };
    });

    res.json(setsWithCards);
  } catch (error) {
    console.error('Get flashcard sets error:', error);
    res.status(500).json({ error: 'Failed to get flashcard sets' });
  }
});

// Get flashcard set by ID
router.get('/:setId', (req, res) => {
  const { setId } = req.params;
  const db = req.app.locals.db;

  try {
    const set = db.prepare('SELECT * FROM flashcard_sets WHERE id = ?').get(setId);
    if (!set) {
      return res.status(404).json({ error: 'Flashcard set not found' });
    }

    const cards = db.prepare(`
      SELECT * FROM flashcard_cards
      WHERE flashcard_set_id = ?
      ORDER BY card_order
    `).all(setId);

    res.json({
      id: set.id.toString(),
      title: set.title,
      cards: cards.map(c => ({
        id: c.id.toString(),
        front: c.front_text,
        back: c.back_text,
      })),
      completedBy: [],
    });
  } catch (error) {
    console.error('Get flashcard set error:', error);
    res.status(500).json({ error: 'Failed to get flashcard set' });
  }
});

// Create flashcard set
router.post('/', (req, res) => {
  const { title, cards, createdBy } = req.body;
  const db = req.app.locals.db;

  try {
    if (!title || !cards || cards.length === 0) {
      return res.status(400).json({ error: 'Title and cards are required' });
    }

    const insertSet = db.prepare('INSERT INTO flashcard_sets (title, created_by, is_public) VALUES (?, ?, 1)');
    const result = insertSet.run(title, createdBy || null);
    const setId = result.lastInsertRowid;

    const insertCard = db.prepare(`
      INSERT INTO flashcard_cards (flashcard_set_id, front_text, back_text, card_order)
      VALUES (?, ?, ?, ?)
    `);

    cards.forEach((card, index) => {
      insertCard.run(setId, card.front, card.back, index);
    });

    res.json({ id: setId.toString(), message: 'Flashcard set created successfully' });
  } catch (error) {
    console.error('Create flashcard set error:', error);
    res.status(500).json({ error: 'Failed to create flashcard set' });
  }
});

// Record flashcard completion
router.post('/:setId/complete', (req, res) => {
  const { setId } = req.params;
  const { userId, xpEarned } = req.body;
  const db = req.app.locals.db;

  try {
    // Check if already completed
    const existing = db.prepare('SELECT * FROM flashcard_completions WHERE flashcard_set_id = ? AND user_id = ?')
      .get(setId, userId);

    if (existing) {
      return res.status(400).json({ error: 'Flashcard set already completed' });
    }

    // Record completion
    db.prepare(`
      INSERT INTO flashcard_completions (flashcard_set_id, user_id, xp_earned)
      VALUES (?, ?, ?)
    `).run(setId, userId, xpEarned || 0);

    // Update user XP if earned
    let updatedUser = null;
    if (xpEarned && xpEarned > 0) {
      console.log(`🎯 Updating XP for user ${userId}: +${xpEarned} XP`);
      const profile = db.prepare('SELECT xp FROM user_profiles WHERE user_id = ?').get(userId);
      const currentXP = profile?.xp || 0;
      const newXP = currentXP + xpEarned;
      const newLevel = calculateLevel(newXP);
      
      console.log(`📊 XP Update: ${currentXP} + ${xpEarned} = ${newXP} → Level ${newLevel}`);
      
      // Update only XP and level - created_at remains unchanged (immutable)
      db.prepare('UPDATE user_profiles SET xp = ?, level = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?')
        .run(newXP, newLevel, userId);
      
      // Save database immediately
      saveDatabase();
      console.log('✅ Database saved after flashcard XP update');
      
      // Return updated user data
      const updatedProfile = db.prepare('SELECT xp, level FROM user_profiles WHERE user_id = ?').get(userId);
      updatedUser = {
        xp: updatedProfile.xp,
        level: updatedProfile.level
      };
      console.log('✅ Returning updated user:', updatedUser);
    }

    res.json({ 
      message: 'Flashcard completion recorded',
      user: updatedUser
    });
  } catch (error) {
    console.error('Complete flashcard error:', error);
    res.status(500).json({ error: 'Failed to record flashcard completion' });
  }
});

export default router;

