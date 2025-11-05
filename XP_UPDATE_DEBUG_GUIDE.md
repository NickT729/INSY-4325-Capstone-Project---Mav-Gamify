# XP Update Debug Guide

## Overview
This guide helps debug why XP is not updating after completing quizzes.

## Complete Flow (What Should Happen)

1. **User completes quiz** → Frontend calculates earned XP
2. **Quiz completion API called** → `POST /api/quizzes/:quizId/complete`
   - Updates `quiz_completions` table
   - Adds XP to user profile
   - Saves database
   - Returns updated user (xp, level)
3. **Profile update** → Frontend updates user state with returned XP
4. **Refresh from server** → Fetches latest profile to ensure sync
5. **Dashboard updates** → React re-renders with new XP/level

## Debugging Steps

### 1. Check Browser Console
Open DevTools (F12) → Console tab, then complete a quiz. Look for:

**Expected logs:**
```
🎯 Quiz completed! Current XP: 0 Earned: 300 New XP: 300
📡 Calling quiz completion API...
✅ Quiz completion API response: {message: "...", user: {xp: 300, level: 1}}
✅ Using XP from quiz completion API: {xp: 300, level: 1}
🔄 updateProfile called with: {xp: 300, level: 1}
📡 Calling API to update profile for user 1
✅ API returned updated profile: {xp: 300, level: 1, ...}
✅ XP changed from 0 to 300
✅ Level changed from 1 to 1
🔄 Refreshing user profile from server...
✅ Refreshed profile from server: {xp: 300, level: 1}
✅ User state synced with server
✅ Quiz completion flow completed successfully
```

**If you see errors:**
- `❌ Update profile API error` → Check server console
- `⚠️ Quiz completion API failed` → Quiz might not exist in DB
- `❌ CRITICAL: Failed to update XP` → Check network tab

### 2. Check Server Console
Look for these logs when completing a quiz:

**Expected logs:**
```
🎯 Updating XP for user 1: +300 XP
📊 XP Update: 0 + 300 = 300 → Level 1
✅ Database saved after quiz XP update
✅ Returning updated user: {xp: 300, level: 1}
🔄 Update profile request: {userId: 1, xp: 300, level: 1, ...}
📊 XP: 300 → Level: 1
📝 Executing SQL: UPDATE user_profiles SET xp = ?, level = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?
📝 Parameters: [300, 1, 1]
✅ Update result: {changes: 1, ...}
✅ Updated profile from DB: {xp: 300, level: 1}
✅ Database saved after profile update
```

**If you see errors:**
- `❌ Update profile error` → Check SQL syntax
- `❌ Profile not found after update!` → User ID issue
- Database save errors → Check file permissions

### 3. Check Network Tab
Open DevTools → Network tab, then complete a quiz:

**Expected requests:**
1. `POST /api/quizzes/:id/complete` → Status 200
   - Response should have `{message: "...", user: {xp: 300, level: 1}}`
2. `PUT /api/users/profile/:userId` → Status 200
   - Response should have updated `xp` and `level`
3. `GET /api/users/profile/:userId` → Status 200
   - Response should have latest `xp` and `level`

**If requests fail:**
- 404 → Endpoint not found (check server routes)
- 400 → Bad request (check request body)
- 500 → Server error (check server console)

### 4. Check Database
Open DB Browser SQLite → Open `database/mavpal.db`:

**Check user_profiles table:**
```sql
SELECT user_id, xp, level, updated_at 
FROM user_profiles 
WHERE user_id = YOUR_USER_ID;
```

**After completing quiz:**
- `xp` should increase
- `level` should update if XP threshold crossed
- `updated_at` should be recent timestamp

**Check quiz_completions table:**
```sql
SELECT * FROM quiz_completions 
WHERE user_id = YOUR_USER_ID 
ORDER BY completed_at DESC;
```

- Should have a new row for the completed quiz
- `xp_earned` should match the quiz reward

### 5. Verify Environment Variables

**Frontend `.env` file (if exists):**
```env
VITE_API_URL=http://localhost:3001/api
VITE_USE_API=true
```

**Backend `.env` file (server/.env):**
```env
PORT=3001
DB_PATH=../database/mavpal.db
NODE_ENV=development
```

## Common Issues & Fixes

### Issue 1: Quiz completion API fails silently
**Symptom:** `⚠️ Quiz completion API failed` in console
**Cause:** Quiz doesn't exist in database
**Fix:** The code falls back to profile update API, which should still work

### Issue 2: XP updates but dashboard doesn't refresh
**Symptom:** Database shows correct XP, but UI doesn't update
**Cause:** React state not updating
**Fix:** Check that `updateProfile` is actually calling `setUser` with new values

### Issue 3: XP resets after page refresh
**Symptom:** XP appears correct, but resets on refresh
**Cause:** Database not saving properly
**Fix:** Check `saveDatabase()` is being called after updates

### Issue 4: "Quiz already completed" error
**Symptom:** Can't complete same quiz twice
**Cause:** Duplicate prevention in database
**Fix:** This is expected behavior - each quiz can only be completed once per user

## Manual Test Script

1. **Start backend:**
   ```bash
   cd server
   npm run dev
   ```

2. **Start frontend:**
   ```bash
   npm run dev
   ```

3. **Login to account**

4. **Note current XP/level** (from dashboard)

5. **Complete a quiz:**
   - Go to Study page
   - Select "Algorithms Basic" quiz
   - Answer all questions
   - Pass the quiz (70%+)

6. **Check browser console** for logs

7. **Check server console** for logs

8. **Refresh dashboard** - XP should update

9. **Check database** - XP should be saved

10. **Refresh page** - XP should persist

## Expected Behavior

- ✅ XP increases immediately after quiz completion
- ✅ Level increases if XP threshold is crossed
- ✅ Database is updated and saved
- ✅ Dashboard reflects new XP/level
- ✅ XP persists after page refresh
- ✅ Multiple quiz completions accumulate XP

## If Still Not Working

1. **Clear browser cache and localStorage**
2. **Restart both servers**
3. **Check database file permissions**
4. **Verify user ID is correct** (check login response)
5. **Check for JavaScript errors** in console
6. **Verify API is reachable** (test with curl or Postman)

## Test API Endpoints Manually

**Test quiz completion:**
```bash
curl -X POST http://localhost:3001/api/quizzes/1/complete \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "score": 3, "totalQuestions": 3, "passed": true, "xpEarned": 300}'
```

**Test profile update:**
```bash
curl -X PUT http://localhost:3001/api/users/profile/1 \
  -H "Content-Type: application/json" \
  -d '{"xp": 300}'
```

**Check profile:**
```bash
curl http://localhost:3001/api/users/profile/1
```




