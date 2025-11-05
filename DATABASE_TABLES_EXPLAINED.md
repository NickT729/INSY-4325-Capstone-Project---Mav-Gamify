# 📊 Database Tables for XP and Levels

## ✅ Yes, There IS a SQL Table!

The **`user_profiles`** table is the main table that tracks XP and levels for each user.

## 📋 Table Structure

### `user_profiles` Table

This table stores:
- `user_id` - Links to the `users` table
- `xp` - Current XP points (starts at 0)
- `level` - Current level (starts at 1)
- `display_name` - User's display name
- `nickname` - User's nickname
- `major`, `college`, `class_year` - User info
- `avatar_url` - Profile picture
- `created_at`, `updated_at` - Timestamps

## 🔄 How It Works

1. **User Registration:**
   - New user created in `users` table
   - New profile created in `user_profiles` table
   - **XP: 0**, **Level: 1**

2. **XP Updates:**
   - When user completes quiz/flashcard/challenge
   - Backend calculates new XP
   - Backend calculates new level based on XP
   - **Database updated** with both XP and level

3. **Level Calculation:**
   - Uses `levelCalculator.js` utility
   - Automatically determines level based on XP
   - Levels 1-20 with progressive XP requirements

## 📍 View in DB Browser SQLite

1. Open `database/mavpal.db` in DB Browser
2. Go to **"Browse Data"** tab
3. Select **`user_profiles`** table
4. See columns:
   - `user_id` - User ID
   - `xp` - Current XP (should be 0 for new users)
   - `level` - Current level (should be 1 for new users)
   - `display_name` - Email username
   - `nickname` - Display name

## 🔍 Verify It's Working

After creating a new account:
1. Check `user_profiles` table
2. Find your user (by `user_id` or `display_name`)
3. Verify:
   - `xp` = 0
   - `level` = 1

After earning XP:
1. Complete a quiz or flashcard
2. Refresh DB Browser (F5)
3. Check `user_profiles` table again
4. See updated `xp` and `level` values!

## ✅ Tables Created

- ✅ `users` - User accounts
- ✅ `user_profiles` - XP and levels (**THIS IS THE ONE!**)
- ✅ `quiz_completions` - Quiz completion records (tracks XP earned)
- ✅ `flashcard_completions` - Flashcard completion records (tracks XP earned)
- ✅ `challenge_progress` - Challenge progress (tracks XP earned)

All XP and level changes are **saved to the database** and **persist** across sessions!








