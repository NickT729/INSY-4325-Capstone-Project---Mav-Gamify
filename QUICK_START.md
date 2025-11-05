# Quick Start Guide - Testing MavPal

## 🚀 Step-by-Step Setup

### Step 1: Install Dependencies

**Backend:**
```bash
cd server
npm install
```

**Frontend (if not already done):**
```bash
# From root directory (uta-gamify)
npm install
```

### Step 2: Setup Database

**Option A: Using the setup script (Recommended)**
```bash
cd server
npm run db:setup
```

**Option B: Manual setup**
```bash
cd database
sqlite3 mavpal.db < schema.sqlite.sql
```

**Note:** If `mavpal.db` already exists and you want a fresh start:
```bash
# Delete old database
rm database/mavpal.db
# Or on Windows:
del database\mavpal.db

# Then create new one
cd server
npm run db:setup
```

### Step 3: Start Backend Server

Open a terminal/command prompt:

```bash
cd server
npm run dev
```

You should see:
```
🚀 MavPal API server running on http://localhost:3001
📊 Database: ../database/mavpal.db
```

**Keep this terminal open!**

### Step 4: Start Frontend

Open a **NEW** terminal/command prompt:

```bash
# From root directory (uta-gamify)
npm run dev
```

You should see:
```
VITE v7.x.x ready in xxx ms

➜  Local:   http://localhost:5173/
```

### Step 5: Test Registration

1. **Open your browser** and go to: `http://localhost:5173/signup`

2. **Fill out the form:**
   - Email: `yourname@mavs.uta.edu` (must end with @mavs.uta.edu)
   - Password: Create a password (watch the strength meter!)
   - Confirm Password: Retype your password

3. **Click "Create Account"**

4. **You should be automatically logged in** and redirected to the dashboard

### Step 6: Verify in DB Browser SQLite

1. **Open DB Browser for SQLite**

2. **Open Database:**
   - Click "Open Database"
   - Navigate to: `uta-gamify/database/mavpal.db`
   - Click Open

3. **View Users:**
   - Click "Browse Data" tab
   - Select `users` table from dropdown
   - You should see your email

4. **View User Profile:**
   - Select `user_profiles` table from dropdown
   - You should see:
     - `display_name`: Your email username
     - `xp`: 0
     - `level`: 0

5. **Check Password Hash:**
   - In `users` table, you'll see `password_hash` (hashed, not plain text)

### Step 7: Test Login

1. **Logout** (click avatar in top right → Logout)

2. **Go to login page:** `http://localhost:5173/login`

3. **Enter your credentials:**
   - Email: `yourname@mavs.uta.edu`
   - Password: Your password

4. **Click "Sign In"**

5. **You should be logged in successfully**

### Step 8: Test XP Earning

1. **Complete a quiz** or study flashcards
2. **Check your XP** in the profile or dashboard
3. **Verify in DB Browser:**
   - Open `user_profiles` table
   - Find your user
   - Check `xp` and `level` values - they should have increased!

## 🔍 Troubleshooting

### "Cannot find module" errors
```bash
# Make sure you installed dependencies
cd server
npm install

cd ..
npm install
```

### "Database not found" error
```bash
# Make sure database exists
cd server
npm run db:setup
```

### Backend won't start
- Check if port 3001 is already in use
- Make sure you're in the `server` directory
- Check for error messages in the terminal

### Frontend can't connect to backend
- Make sure backend is running on port 3001
- Check browser console for errors
- Verify `VITE_API_URL` in `.env` (if you created one)

### "Email already registered"
- User already exists in database
- Delete the user from database or use a different email

### Can't see user in DB Browser
- Make sure you're looking at the correct database file
- Refresh DB Browser (close and reopen)
- Check that registration actually succeeded (check backend terminal for errors)

## 📝 Quick Test Checklist

- [ ] Backend server running on port 3001
- [ ] Frontend running on port 5173
- [ ] Can access signup page
- [ ] Can create account successfully
- [ ] User appears in `users` table in DB Browser
- [ ] User profile appears with `xp: 0` and `level: 0`
- [ ] Can logout and login again
- [ ] XP updates after completing activities

## 🎯 Expected Results

### After Registration:
- User in `users` table with email and hashed password
- User in `user_profiles` table with:
  - `xp: 0`
  - `level: 0`
  - `display_name`: Your email username

### After Earning XP:
- `xp` value increases
- `level` value updates automatically (level = floor(XP / 1000) + 1)

## 💡 Tips

- **Keep both terminals open** (backend and frontend)
- **Check browser console** (F12) for any errors
- **Check backend terminal** for API request logs
- **Refresh DB Browser** after making changes to see updates

## 🆘 Still Having Issues?

1. Check all terminal windows for error messages
2. Verify database file exists: `database/mavpal.db`
3. Make sure backend is running before testing frontend
4. Check browser console (F12) for errors
5. Try deleting `database/mavpal.db` and running setup again

