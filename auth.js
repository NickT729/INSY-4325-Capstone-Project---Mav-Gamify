import express from 'express';
import bcrypt from 'bcryptjs';
import { saveDatabase } from '../db.js';

const router = express.Router();

// Register endpoint
router.post('/register', async (req, res) => {
  const { email, password, firstName, lastName, major, college, classYear } = req.body;
  const db = req.app.locals.db;
  const dbInstance = req.app.locals.dbInstance;

  console.log('📝 Registration:', email);

  try {
    // Basic validation
    if (!email || !email.endsWith('@mavs.uta.edu')) {
      return res.status(400).json({ error: 'Email must end with @mavs.uta.edu' });
    }

    if (!password || password.length < 8) {
      return res.status(400).json({ error: 'Password must be at least 8 characters long' });
    }

    if (!firstName || !lastName) {
      return res.status(400).json({ error: 'First name and last name are required' });
    }

    if (!major || !college || !classYear) {
      return res.status(400).json({ error: 'Major, College, and Graduation Year are required' });
    }

    // Check if user exists
    const existing = db.prepare('SELECT id FROM users WHERE email = ?').get(email);
    if (existing) {
      return res.status(400).json({ error: 'Email already registered' });
    }

    // Hash password
    const passwordHash = await bcrypt.hash(password, 10);
    console.log('✅ Password hashed, length:', passwordHash.length);

    // Insert user
    const insertUser = db.prepare('INSERT INTO users (email, password_hash) VALUES (?, ?)');
    const userResult = insertUser.run(email, passwordHash);
    const userId = userResult.lastInsertRowid;

    if (!userId) {
      throw new Error('Failed to create user');
    }

    console.log('✅ User created, ID:', userId);
    console.log('✅ Password hash saved (length:', passwordHash.length, ')');

    // Create profile
    const displayName = `${firstName} ${lastName}`;
    const insertProfile = db.prepare(`
      INSERT INTO user_profiles (user_id, first_name, last_name, display_name, nickname, major, college, class_year, xp, level)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 1)
    `);
    
    try {
      insertProfile.run(userId, firstName, lastName, displayName, displayName, major, college, classYear);
      console.log('✅ Profile created with major, college, and class year');
    } catch (profileError) {
      // If profile fails, delete user to maintain consistency
      console.error('❌ Profile creation failed:', profileError);
      db.prepare('DELETE FROM users WHERE id = ?').run(userId);
      saveDatabase();
      throw new Error('Failed to create user profile: ' + profileError.message);
    }

    // Save database
    saveDatabase();

    // Return success
    res.status(201).json({
      id: userId,
      email: email,
      displayName: displayName,
      xp: 0,
      level: 1,
      message: 'Account created successfully'
    });

  } catch (error) {
    console.error('❌ Registration error:', error);
    res.status(500).json({ error: error.message || 'Registration failed' });
  }
});

// Login endpoint - BULLETPROOF VERSION
router.post('/login', async (req, res) => {
  const { email, password } = req.body;
  const db = req.app.locals.db;
  const dbInstance = req.app.locals.dbInstance;

  console.log('🔐 Login attempt:', email);

  try {
    // Validate
    if (!email || !email.endsWith('@mavs.uta.edu')) {
      return res.status(400).json({ error: 'Email must end with @mavs.uta.edu' });
    }

    if (!password) {
      return res.status(400).json({ error: 'Password is required' });
    }

    // Get user - use direct SQL query to avoid wrapper issues
    let userId = null;
    let passwordHash = null;
    let userEmail = email;

    // Use direct SQL query to get user data reliably
    if (dbInstance) {
      try {
        const userQuery = dbInstance.exec(`SELECT id, email, password_hash FROM users WHERE email = '${email.replace(/'/g, "''")}'`);
        if (userQuery && userQuery[0] && userQuery[0].values && userQuery[0].values[0]) {
          const row = userQuery[0].values[0];
          const columns = userQuery[0].columns || ['id', 'email', 'password_hash'];
          
          // Find column indices
          const idIndex = columns.indexOf('id');
          const emailIndex = columns.indexOf('email');
          const hashIndex = columns.indexOf('password_hash');
          
          userId = row[idIndex];
          userEmail = row[emailIndex] || email;
          passwordHash = row[hashIndex];
          
          console.log('✅ User found via direct query:', { id: userId, email: userEmail, hasHash: !!passwordHash });
        }
      } catch (e) {
        console.error('❌ Direct query failed:', e);
      }
    }

    // Fallback: try wrapper if direct query didn't work
    if (!userId && !passwordHash) {
      const user = db.prepare('SELECT id, email, password_hash FROM users WHERE email = ?').get(email);
      if (user) {
        userId = user.id;
        userEmail = user.email || email;
        passwordHash = user.password_hash || user.passwordHash || user['password_hash'];
        console.log('✅ User found via wrapper:', { id: userId, email: userEmail, hasHash: !!passwordHash });
      }
    }

    if (!userId || !passwordHash) {
      console.log('❌ User not found or no password hash:', email);
      return res.status(401).json({ error: 'Invalid email or password' });
    }

    // Convert to string and validate
    passwordHash = passwordHash ? String(passwordHash) : null;

    if (!passwordHash || passwordHash.length < 10) {
      console.error('❌ NO PASSWORD HASH FOUND!');
      return res.status(500).json({ error: 'Account security error. Please contact support.' });
    }

    console.log('✅ Password hash found, length:', passwordHash.length);

    // Compare password
    const passwordMatch = await bcrypt.compare(password, passwordHash);

    if (!passwordMatch) {
      console.log('❌ Password does not match');
      return res.status(401).json({ error: 'Invalid email or password' });
    }

    console.log('✅ Password verified successfully');

    // Get profile using direct SQL query to avoid wrapper issues
    let profile = null;
    
    if (dbInstance && userId) {
      try {
        const profileQuery = dbInstance.exec(`SELECT * FROM user_profiles WHERE user_id = ${userId}`);
        if (profileQuery && profileQuery[0] && profileQuery[0].values && profileQuery[0].values[0]) {
          const row = profileQuery[0].values[0];
          const columns = profileQuery[0].columns || [];
          
          profile = {};
          for (let i = 0; i < columns.length; i++) {
            profile[columns[i]] = row[i];
          }
          
          console.log('✅ Profile found via direct query:', { xp: profile.xp, level: profile.level });
        }
      } catch (e) {
        console.error('❌ Direct profile query failed:', e);
      }
    }

    // Fallback: try wrapper
    if (!profile && userId) {
      try {
        profile = db.prepare('SELECT * FROM user_profiles WHERE user_id = ?').get(userId);
        if (profile) {
          console.log('✅ Profile found via wrapper:', { xp: profile.xp, level: profile.level });
        }
      } catch (e) {
        console.error('❌ Profile wrapper query failed:', e);
      }
    }

    if (!profile) {
      console.error('❌ Profile not found for user:', userId);
      return res.status(500).json({ error: 'User profile not found' });
    }

    // Update last login using direct SQL
    if (dbInstance && userId) {
      try {
        dbInstance.exec(`UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ${userId}`);
        saveDatabase();
      } catch (e) {
        console.error('❌ Update last_login failed:', e);
      }
    } else if (userId) {
      try {
        db.prepare('UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?').run(userId);
        saveDatabase();
      } catch (e) {
        console.error('❌ Update last_login via wrapper failed:', e);
      }
    }

    // Return user data
    res.json({
      id: userId,
      email: userEmail,
      displayName: profile.display_name || profile.displayName || email.split('@')[0],
      nickname: profile.nickname || profile.display_name || profile.displayName || email.split('@')[0],
      major: profile.major || null,
      college: profile.college || null,
      classYear: profile.class_year || profile.classYear || null,
      avatarUrl: profile.avatar_url || profile.avatarUrl || null,
      xp: profile.xp || 0,
      level: profile.level || 1,
    });

    console.log('✅ Login successful for:', email);

  } catch (error) {
    console.error('❌ Login error:', error);
    console.error('   Stack:', error.stack);
    res.status(500).json({ error: 'Login failed' });
  }
});

export default router;
