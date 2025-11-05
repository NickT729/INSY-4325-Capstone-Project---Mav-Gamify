# 🎯 How to Run the App & View Database in DB Browser

## 📍 Database File Location

The database file is located at:

```
C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify\database\mavpal.db
```

### How to Open in DB Browser SQLite:

1. **Open DB Browser for SQLite** (the app you have installed)
2. Click **"Open Database"** button
3. Navigate to:
   ```
   C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify\database\
   ```
4. Select the file: **`mavpal.db`**
5. Click **"Open"**

You should now see all the tables in the left sidebar!

## 🚀 How to Run the App (Step-by-Step)

### Step 1: Start Backend Server

**Open Terminal/Command Prompt 1:**

```powershell
cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify\server"
npm run dev
```

**Wait until you see:**
```
🚀 MavPal API server running on http://localhost:3001
📊 Database: ../database/mavpal.db
```

**Keep this terminal open!** ✅

### Step 2: Start Frontend

**Open a NEW Terminal/Command Prompt 2:**

```powershell
cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify"
npm run dev
```

**Wait until you see:**
```
Local:   http://localhost:5173/
```

**Keep this terminal open too!** ✅

### Step 3: Open in Browser

1. Open your web browser
2. Go to: **`http://localhost:5173`**
3. You'll be redirected to the login page

### Step 4: Test Registration

1. Click **"Create one"** link (or go to `/signup`)
2. Fill out the form:
   - **Email:** `test@mavs.uta.edu` (or your @mavs.uta.edu email)
   - **Password:** Create a password (watch the strength meter!)
   - **Confirm Password:** Retype it
3. Click **"Create Account"**
4. You'll be automatically logged in! ✅

## 🔍 View Changes in DB Browser SQLite

### After Creating an Account:

1. **Open DB Browser SQLite** (if not already open)
2. **Refresh the database:**
   - Click the **"Refresh"** button (or close and reopen the file)
   - Or press `F5`
3. **View Users:**
   - Click **"Browse Data"** tab
   - Select **`users`** from the dropdown
   - You should see your email in the table
4. **View User Profile:**
   - Select **`user_profiles`** from the dropdown
   - You should see:
     - `display_name`: Your email username
     - `xp`: 0
     - `level`: 0
     - `nickname`: Your email username

### After Completing Activities (Earning XP):

1. **Complete a quiz** or study flashcards
2. **Go back to DB Browser**
3. **Refresh** (F5 or close/reopen)
4. **View `user_profiles`** table again
5. **See your XP and level updated!** 🎉

## 📊 Database Tables You Can View

In DB Browser, you can browse these tables:

- **`users`** - User accounts (email, password hash)
- **`user_profiles`** - User info (XP, level, nickname, etc.)
- **`notifications`** - User notifications
- **`quizzes`** - Quiz definitions
- **`quiz_questions`** - Quiz questions
- **`quiz_completions`** - Quiz completion records
- **`flashcard_sets`** - Flashcard sets
- **`flashcard_cards`** - Individual flashcards
- **`flashcard_completions`** - Flashcard completion records
- **`challenges`** - Challenge definitions
- **`challenge_progress`** - Challenge progress tracking

## 🎯 Quick Test Checklist

- [ ] Backend server running (Terminal 1)
- [ ] Frontend server running (Terminal 2)
- [ ] Browser open to `http://localhost:5173`
- [ ] DB Browser SQLite open with `mavpal.db`
- [ ] Created test account
- [ ] Can see user in `users` table
- [ ] Can see profile in `user_profiles` table with `xp: 0`, `level: 0`

## 💡 Tips

- **Keep both terminals open** while testing
- **Refresh DB Browser** (F5) after making changes to see updates
- **Database auto-saves** every 5 seconds and after each request
- **XP updates** appear in database immediately after earning

## 🐛 Troubleshooting

**Can't find database file?**
- Make sure you're in the right folder: `uta-gamify/database/`
- The file should be named exactly: `mavpal.db`

**Database not updating?**
- Make sure backend server is running
- Refresh DB Browser (F5 or close/reopen)
- Check backend terminal for errors

**Server won't start?**
- Make sure you're in the correct directory
- Check that `npm install` completed successfully
- Look for error messages in the terminal

---

**That's it!** Run both servers, create an account, and watch the database update in real-time! 🎉

