import express from 'express';
import { saveDatabase } from '../db.js';
import { calculateLevel } from '../utils/levelCalculator.js';

const router = express.Router();

// Get user profile
router.get('/profile/:userId', (req, res) => {
  const { userId } = req.params;
  const db = req.app.locals.db;

  try {
    const user = db.prepare('SELECT * FROM users WHERE id = ?').get(userId);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    const profile = db.prepare('SELECT * FROM user_profiles WHERE user_id = ?').get(userId);

    res.json({
      id: user.id,
      email: user.email,
      displayName: profile.display_name,
      nickname: profile.nickname,
      major: profile.major,
      college: profile.college,
      classYear: profile.class_year,
      avatarUrl: profile.avatar_url,
      xp: profile.xp,
      level: profile.level,
    });
  } catch (error) {
    console.error('Get profile error:', error);
    res.status(500).json({ error: 'Failed to get profile' });
  }
});

// Update user profile
router.put('/profile/:userId', (req, res) => {
  const { userId } = req.params;
  const { nickname, avatarUrl, major, college, classYear, xp, level } = req.body;
  const db = req.app.locals.db;

  console.log('🔄 Update profile request:', { userId, xp, level, nickname, avatarUrl, major, college, classYear });

  try {
    const updates = {};
    const values = [];

    if (nickname !== undefined) {
      updates.nickname = nickname;
      values.push(`nickname = ?`);
    }
    if (avatarUrl !== undefined) {
      updates.avatar_url = avatarUrl;
      values.push(`avatar_url = ?`);
    }
    if (major !== undefined) {
      updates.major = major;
      values.push(`major = ?`);
    }
    if (college !== undefined) {
      updates.college = college;
      values.push(`college = ?`);
    }
    if (classYear !== undefined) {
      updates.class_year = classYear;
      values.push(`class_year = ?`);
    }
    if (xp !== undefined) {
      updates.xp = xp;
      values.push(`xp = ?`);
      // Auto-calculate level using level calculator
      updates.level = calculateLevel(xp);
      values.push(`level = ?`);
      console.log(`📊 XP: ${xp} → Level: ${updates.level}`);
    }
    if (level !== undefined && xp === undefined) {
      updates.level = level;
      values.push(`level = ?`);
    }

    if (values.length === 0) {
      console.warn('⚠️ No valid fields to update');
      return res.status(400).json({ error: 'No valid fields to update' });
    }

    // Ensure created_at is NEVER updated - only update specified fields and updated_at
    // created_at must remain immutable from the original creation date
    const sql = `UPDATE user_profiles SET ${values.join(', ')}, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?`;
    const params = Object.values(updates);
    params.push(userId);

    console.log('📝 Executing SQL:', sql);
    console.log('📝 Parameters:', params);

    const result = db.prepare(sql).run(...params);
    console.log('✅ Update result:', result);

    // Get updated profile
    const profile = db.prepare('SELECT * FROM user_profiles WHERE user_id = ?').get(userId);
    const user = db.prepare('SELECT * FROM users WHERE id = ?').get(userId);

    if (!profile) {
      console.error('❌ Profile not found after update!');
      return res.status(404).json({ error: 'Profile not found' });
    }

    if (!user) {
      console.error('❌ User not found after update!');
      return res.status(404).json({ error: 'User not found' });
    }

    console.log('✅ Updated profile from DB:', { xp: profile.xp, level: profile.level });

    const response = {
      id: user.id,
      email: user.email,
      displayName: profile.display_name,
      nickname: profile.nickname,
      major: profile.major,
      college: profile.college,
      classYear: profile.class_year,
      avatarUrl: profile.avatar_url,
      xp: profile.xp,
      level: profile.level,
    };

    console.log('✅ Sending response:', response);
    
    // Ensure database is saved before responding
    saveDatabase();
    console.log('✅ Database saved after profile update');
    
    res.json(response);
  } catch (error) {
    console.error('❌ Update profile error:', error);
    res.status(500).json({ error: 'Failed to update profile' });
  }
});

export default router;

