# Signup & Registration Setup Guide

## ✅ What's Been Added

### 1. Signup Page (`/signup`)
- Email validation (must be @mavs.uta.edu)
- Password field with show/hide toggle
- Confirm password field with show/hide toggle
- Password strength meter with visual feedback
- Real-time password matching validation

### 2. Password Strength Meter
- Visual progress bar
- Strength levels: Weak, Fair, Good, Strong
- Checks for:
  - 8+ characters
  - Lowercase letters
  - Uppercase letters
  - Numbers
  - Special characters
  - 12+ characters (bonus)

### 3. Backend Registration
- `/api/auth/register` endpoint
- Password hashing with bcrypt
- Email validation
- Duplicate email prevention
- Users start at **Level 0** with **0 XP**

### 4. Database Updates
- Updated schema to start users at level 0
- Proper password hashing storage
- XP persistence in database

## 🚀 How to Use

### 1. Setup Database (First Time)

```bash
# Delete old database if exists
rm database/mavpal.db

# Create new database with updated schema
cd server
npm run db:setup
```

Or manually:
```bash
cd database
sqlite3 mavpal.db < schema.sqlite.sql
```

### 2. Start Backend

```bash
cd server
npm install  # If not already done
npm run dev
```

### 3. Start Frontend

```bash
# From root directory
npm run dev
```

### 4. Create Account

1. Go to `http://localhost:5173/signup`
2. Enter your @mavs.uta.edu email
3. Create a password (watch the strength meter!)
4. Confirm your password
5. Click "Create Account"
6. You'll be automatically logged in

### 5. Verify in DB Browser SQLite

1. Open DB Browser for SQLite
2. Open `database/mavpal.db`
3. Go to "Browse Data" tab
4. Select `users` table - you should see your email
5. Select `user_profiles` table - you should see:
   - Your display name
   - `xp: 0`
   - `level: 0`

## 🎯 Features

### Password Requirements
- Minimum 8 characters
- Password strength meter shows:
  - Weak (red) - 0-2 criteria met
  - Fair (orange) - 3 criteria met
  - Good (blue) - 4 criteria met
  - Strong (green) - 5-6 criteria met

### XP & Level System
- New users start at **Level 0** with **0 XP**
- XP is **persisted in database** and **retained**
- Level is calculated: `floor(XP / 1000) + 1`
- Level updates automatically when XP changes

### Daily Challenges
- Challenges refresh daily (reset progress)
- **XP earned is retained** - never lost
- User's total XP and level persist across days

## 🔍 Testing in DB Browser SQLite

### Check User Creation
```sql
SELECT * FROM users WHERE email = 'your@mavs.uta.edu';
```

### Check User Profile
```sql
SELECT * FROM user_profiles WHERE user_id = (SELECT id FROM users WHERE email = 'your@mavs.uta.edu');
```

### Check XP After Activity
```sql
-- After completing a quiz
SELECT up.xp, up.level, up.display_name 
FROM user_profiles up
JOIN users u ON up.user_id = u.id
WHERE u.email = 'your@mavs.uta.edu';
```

### See All Users
```sql
SELECT 
  u.email,
  up.display_name,
  up.xp,
  up.level,
  u.created_at
FROM users u
JOIN user_profiles up ON u.id = up.user_id
ORDER BY up.xp DESC;
```

## 📝 Notes

- Passwords are hashed with bcrypt (never stored in plain text)
- Users cannot login without first signing up
- Email must end with @mavs.uta.edu
- XP and levels are stored in `user_profiles` table
- All changes are automatically saved to database

## 🐛 Troubleshooting

**"Email already registered"**
- User already exists in database
- Try a different email or delete the user from database

**"Registration failed"**
- Check backend server is running
- Check database exists at `database/mavpal.db`
- Check console for errors

**Can't see user in DB Browser**
- Make sure you're looking at the correct database file
- Refresh DB Browser (close and reopen)
- Check that registration actually succeeded (check console)

**XP not updating**
- Check backend server logs
- Verify API calls are working (check Network tab in browser)
- Check database directly in DB Browser

