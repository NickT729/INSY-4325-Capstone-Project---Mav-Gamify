import express from 'express';
import { calculateLevel } from '../utils/levelCalculator.js';
import { saveDatabase } from '../db.js';

const router = express.Router();

// Helper function to check if a date is today
function isToday(dateString) {
  if (!dateString) return false;
  const date = new Date(dateString);
  const today = new Date();
  return date.toDateString() === today.toDateString();
}

// Get all challenges
router.get('/', (req, res) => {
  const db = req.app.locals.db;

  try {
    const challenges = db.prepare(`
      SELECT c.*, 
        up.display_name as created_by_name,
        (SELECT COUNT(*) FROM challenge_progress WHERE challenge_id = c.id) as participants
      FROM challenges c
      LEFT JOIN user_profiles up ON c.created_by = up.user_id
      WHERE c.end_date >= DATE('now')
      ORDER BY c.created_at DESC
    `).all();

    const challengesWithProgress = challenges.map(challenge => {
      // Get participant count
      const participantCount = db.prepare(`
        SELECT COUNT(*) as count FROM challenge_progress WHERE challenge_id = ?
      `).get(challenge.id).count;

      return {
        id: challenge.id,
        title: challenge.title,
        description: challenge.description,
        progress: 0, // Will be set per user
        maxProgress: challenge.max_progress,
        xpReward: challenge.xp_reward,
        createdBy: challenge.created_by_name || 'Unknown',
        participants: participantCount,
        endDate: challenge.end_date,
        category: challenge.category,
      };
    });

    res.json(challengesWithProgress);
  } catch (error) {
    console.error('Get challenges error:', error);
    res.status(500).json({ error: 'Failed to get challenges' });
  }
});

// Get user's challenges (with progress)
router.get('/user/:userId', (req, res) => {
  const { userId } = req.params;
  const db = req.app.locals.db;

  try {
    const challenges = db.prepare(`
      SELECT c.*, 
        cp.progress,
        cp.completed,
        cp.completed_at,
        cp.xp_earned,
        up.display_name as created_by_name
      FROM challenges c
      LEFT JOIN challenge_progress cp ON c.id = cp.challenge_id AND cp.user_id = ?
      LEFT JOIN user_profiles up ON c.created_by = up.user_id
      WHERE c.end_date >= DATE('now') OR cp.user_id IS NOT NULL
      ORDER BY c.created_at DESC
    `).all(userId);

    const challengesWithParticipants = challenges.map(challenge => {
      const participantCount = db.prepare(`
        SELECT COUNT(*) as count FROM challenge_progress WHERE challenge_id = ?
      `).get(challenge.id).count;

      // Reset progress if completed yesterday or earlier
      let progress = challenge.progress || 0;
      let completed = challenge.completed === 1;
      
      if (completed && challenge.completed_at && !isToday(challenge.completed_at)) {
        // Challenge was completed but not today - reset it
        progress = 0;
        completed = false;
        // Update database to reflect reset
        db.prepare(`
          UPDATE challenge_progress 
          SET progress = 0, completed = 0, completed_at = NULL, xp_earned = 0
          WHERE challenge_id = ? AND user_id = ?
        `).run(challenge.id, userId);
      } else if (completed && !isToday(challenge.completed_at)) {
        // Completed flag is set but no date or old date - reset
        progress = 0;
        completed = false;
      }

      return {
        id: challenge.id,
        title: challenge.title,
        description: challenge.description,
        progress: progress,
        maxProgress: challenge.max_progress,
        xpReward: challenge.xp_reward,
        createdBy: challenge.created_by_name || 'Unknown',
        participants: participantCount,
        endDate: challenge.end_date,
        category: challenge.category,
      };
    });

    res.json(challengesWithParticipants);
  } catch (error) {
    console.error('Get user challenges error:', error);
    res.status(500).json({ error: 'Failed to get user challenges' });
  }
});

// Create challenge
router.post('/', (req, res) => {
  const { title, description, category, xpReward, maxProgress, createdBy, endDate } = req.body;
  const db = req.app.locals.db;

  try {
    if (!title || !createdBy) {
      return res.status(400).json({ error: 'Title and createdBy are required' });
    }

    const result = db.prepare(`
      INSERT INTO challenges (title, description, category, xp_reward, max_progress, created_by, end_date)
      VALUES (?, ?, ?, ?, ?, ?, ?)
    `).run(
      title,
      description || 'No description',
      category || 'Study',
      xpReward || 500,
      maxProgress || 10,
      createdBy,
      endDate || new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    );

    // Auto-join creator to challenge
    db.prepare(`
      INSERT INTO challenge_progress (challenge_id, user_id, progress, completed)
      VALUES (?, ?, 0, 0)
    `).run(result.lastInsertRowid, createdBy);

    res.json({ id: result.lastInsertRowid, message: 'Challenge created successfully' });
  } catch (error) {
    console.error('Create challenge error:', error);
    res.status(500).json({ error: 'Failed to create challenge' });
  }
});

// Join challenge
router.post('/:challengeId/join', (req, res) => {
  const { challengeId } = req.params;
  const { userId } = req.body;
  const db = req.app.locals.db;

  try {
    // Check if already joined
    const existing = db.prepare('SELECT * FROM challenge_progress WHERE challenge_id = ? AND user_id = ?')
      .get(challengeId, userId);

    if (existing) {
      return res.status(400).json({ error: 'Already joined this challenge' });
    }

    // Join challenge
    db.prepare(`
      INSERT INTO challenge_progress (challenge_id, user_id, progress, completed)
      VALUES (?, ?, 0, 0)
    `).run(challengeId, userId);

    res.json({ message: 'Successfully joined the challenge!' });
  } catch (error) {
    console.error('Join challenge error:', error);
    res.status(500).json({ error: 'Failed to join challenge' });
  }
});

// Update challenge progress
router.post('/:challengeId/progress', (req, res) => {
  const { challengeId } = req.params;
  const { userId, progress } = req.body;
  const db = req.app.locals.db;

  try {
    // Get challenge details
    const challenge = db.prepare('SELECT * FROM challenges WHERE id = ?').get(challengeId);
    if (!challenge) {
      return res.status(404).json({ error: 'Challenge not found' });
    }

    // Get or create progress
    let challengeProgress = db.prepare('SELECT * FROM challenge_progress WHERE challenge_id = ? AND user_id = ?')
      .get(challengeId, userId);

    if (!challengeProgress) {
      // Auto-join if not already joined
      db.prepare(`
        INSERT INTO challenge_progress (challenge_id, user_id, progress, completed)
        VALUES (?, ?, ?, 0)
      `).run(challengeId, userId, progress || 0);
      challengeProgress = db.prepare('SELECT * FROM challenge_progress WHERE challenge_id = ? AND user_id = ?')
        .get(challengeId, userId);
    }

    // Check if challenge was completed today - if not, reset it
    const wasCompletedToday = challengeProgress.completed === 1 && isToday(challengeProgress.completed_at);
    
    // If completed yesterday or earlier, reset for today
    if (challengeProgress.completed === 1 && !wasCompletedToday) {
      db.prepare(`
        UPDATE challenge_progress 
        SET progress = 0, completed = 0, completed_at = NULL, xp_earned = 0
        WHERE challenge_id = ? AND user_id = ?
      `).run(challengeId, userId);
      challengeProgress = {
        ...challengeProgress,
        progress: 0,
        completed: 0,
        completed_at: null,
        xp_earned: 0
      };
    }

    const newProgress = progress !== undefined ? progress : challengeProgress.progress + 1;
    const isCompleted = newProgress >= challenge.max_progress;
    const wasCompleted = wasCompletedToday;

    // Update progress
    db.prepare(`
      UPDATE challenge_progress 
      SET progress = ?, completed = ?, completed_at = ?
      WHERE challenge_id = ? AND user_id = ?
    `).run(
      newProgress,
      isCompleted ? 1 : 0,
      isCompleted && !wasCompleted ? new Date().toISOString() : challengeProgress.completed_at,
      challengeId,
      userId
    );

    // Award XP if just completed (and not already completed today)
    if (isCompleted && !wasCompleted) {
      console.log(`🎯 Updating XP for user ${userId}: +${challenge.xp_reward} XP`);
      const profile = db.prepare('SELECT xp FROM user_profiles WHERE user_id = ?').get(userId);
      const currentXP = profile?.xp || 0;
      const newXP = currentXP + challenge.xp_reward;
      const newLevel = calculateLevel(newXP);
      
      console.log(`📊 XP Update: ${currentXP} + ${challenge.xp_reward} = ${newXP} → Level ${newLevel}`);
      
      // Update only XP and level - created_at remains unchanged (immutable)
      db.prepare('UPDATE user_profiles SET xp = ?, level = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?')
        .run(newXP, newLevel, userId);

      // Update xp_earned in challenge_progress
      db.prepare('UPDATE challenge_progress SET xp_earned = ? WHERE challenge_id = ? AND user_id = ?')
        .run(challenge.xp_reward, challengeId, userId);

      // Save database immediately
      saveDatabase();
      console.log('✅ Database saved after challenge XP update');

      // Return updated user data
      const updatedProfile = db.prepare('SELECT * FROM user_profiles WHERE user_id = ?').get(userId);
      console.log('✅ Returning updated user:', { xp: updatedProfile.xp, level: updatedProfile.level });
      
      return res.json({
        message: 'Challenge completed!',
        completed: true,
        xpEarned: challenge.xp_reward,
        user: {
          xp: updatedProfile.xp,
          level: updatedProfile.level
        }
      });
    }

    res.json({
      message: 'Progress updated',
      completed: wasCompletedToday, // Return true if already completed today
      progress: newProgress,
      maxProgress: challenge.max_progress
    });
  } catch (error) {
    console.error('Update challenge progress error:', error);
    res.status(500).json({ error: 'Failed to update challenge progress' });
  }
});

export default router;

