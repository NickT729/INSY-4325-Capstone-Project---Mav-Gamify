import express from 'express';

const router = express.Router();

// Get leaderboard
router.get('/', (req, res) => {
  const { filter = 'overall', college, major, classYear } = req.query;
  const db = req.app.locals.db;

  try {
    let query;
    const params = [];

    // Build query based on filter
    if (filter === 'college' && college) {
      query = `
        SELECT 
          up.user_id as userId,
          up.display_name as name,
          up.xp,
          up.level,
          up.major,
          up.college,
          up.class_year as classYear
        FROM user_profiles up
        WHERE up.college = ?
        ORDER BY up.xp DESC, up.display_name ASC
      `;
      params.push(college);
    } else if (filter === 'major' && major) {
      query = `
        SELECT 
          up.user_id as userId,
          up.display_name as name,
          up.xp,
          up.level,
          up.major,
          up.college,
          up.class_year as classYear
        FROM user_profiles up
        WHERE up.major = ?
        ORDER BY up.xp DESC, up.display_name ASC
      `;
      params.push(major);
    } else if (filter === 'class' && classYear) {
      query = `
        SELECT 
          up.user_id as userId,
          up.display_name as name,
          up.xp,
          up.level,
          up.major,
          up.college,
          up.class_year as classYear
        FROM user_profiles up
        WHERE up.class_year = ?
        ORDER BY up.xp DESC, up.display_name ASC
      `;
      params.push(classYear);
    } else {
      // Overall leaderboard - include all users
      query = `
        SELECT 
          up.user_id as userId,
          up.display_name as name,
          up.xp,
          up.level,
          up.major,
          up.college,
          up.class_year as classYear
        FROM user_profiles up
        ORDER BY up.xp DESC, up.display_name ASC
      `;
    }

    const rows = db.prepare(query).all(...params);

    // Add rank numbers manually (SQLite doesn't support ROW_NUMBER() well)
    const rowsWithRank = rows.map((row, index) => ({
      ...row,
      rank: index + 1
    }));

    res.json(rowsWithRank);
  } catch (error) {
    console.error('Get leaderboard error:', error);
    res.status(500).json({ error: 'Failed to get leaderboard' });
  }
});

// Get user's rankings
router.get('/user/:userId', (req, res) => {
  const { userId } = req.params;
  const db = req.app.locals.db;

  try {
    // Get user profile
    const user = db.prepare('SELECT * FROM user_profiles WHERE user_id = ?').get(userId);
    
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    // Calculate overall rank
    const overallRank = db.prepare(`
      SELECT COUNT(*) + 1 as rank
      FROM user_profiles
      WHERE xp > ? OR (xp = ? AND display_name < ?)
    `).get(user.xp, user.xp, user.display_name).rank;

    // Calculate college rank
    const collegeRank = user.college ? db.prepare(`
      SELECT COUNT(*) + 1 as rank
      FROM user_profiles
      WHERE college = ? AND (xp > ? OR (xp = ? AND display_name < ?))
    `).get(user.college, user.xp, user.xp, user.display_name).rank : null;

    // Calculate major rank
    const majorRank = user.major ? db.prepare(`
      SELECT COUNT(*) + 1 as rank
      FROM user_profiles
      WHERE major = ? AND (xp > ? OR (xp = ? AND display_name < ?))
    `).get(user.major, user.xp, user.xp, user.display_name).rank : null;

    // Calculate class rank
    const classRank = user.class_year ? db.prepare(`
      SELECT COUNT(*) + 1 as rank
      FROM user_profiles
      WHERE class_year = ? AND (xp > ? OR (xp = ? AND display_name < ?))
    `).get(user.class_year, user.xp, user.xp, user.display_name).rank : null;

    // Get total users in each category
    const totalOverall = db.prepare('SELECT COUNT(*) as count FROM user_profiles').get().count;
    const totalCollege = user.college ? db.prepare('SELECT COUNT(*) as count FROM user_profiles WHERE college = ?').get(user.college).count : 0;
    const totalMajor = user.major ? db.prepare('SELECT COUNT(*) as count FROM user_profiles WHERE major = ?').get(user.major).count : 0;
    const totalClass = user.class_year ? db.prepare('SELECT COUNT(*) as count FROM user_profiles WHERE class_year = ?').get(user.class_year).count : 0;

    res.json({
      overall: {
        rank: overallRank,
        total: totalOverall
      },
      college: user.college ? {
        rank: collegeRank,
        total: totalCollege
      } : null,
      major: user.major ? {
        rank: majorRank,
        total: totalMajor
      } : null,
      class: user.class_year ? {
        rank: classRank,
        total: totalClass
      } : null
    });
  } catch (error) {
    console.error('Get user rankings error:', error);
    res.status(500).json({ error: 'Failed to get user rankings' });
  }
});

export default router;

