# 🔐 Login Debugging Guide

## Issue Fixed
The login system was not properly recognizing user information from the database. This has been fixed with better error handling and null checks.

## What Was Fixed

### 1. Backend Login Endpoint (`server/routes/auth.js`)
- ✅ Added check for missing user profiles
- ✅ Added fallbacks for null/undefined profile fields
- ✅ Enhanced logging for debugging
- ✅ Better error messages

### 2. Frontend Login (`src/auth.tsx`)
- ✅ Better handling of API responses
- ✅ Fallbacks for missing user data fields
- ✅ Enhanced logging to track login flow
- ✅ Proper error extraction from API errors

### 3. Login Component (`src/pages/Login.tsx`)
- ✅ Fixed loading state handling
- ✅ Better error display

## How to Debug Login Issues

### Step 1: Check Browser Console
Open DevTools (F12) → Console tab, then try to login. Look for:

**Expected logs on successful login:**
```
🔐 Attempting login for: your@mavs.uta.edu
🌐 API Request: POST http://localhost:3001/api/auth/login
📦 Request body: {email: "...", password: "..."}
📡 API Response: 200 OK
✅ API Success: {id: 1, email: "...", xp: 0, level: 1, ...}
📥 Received user data from API: {id: 1, ...}
✅ Login successful, setting user data: {id: 1, ...}
```

**If login fails, you'll see:**
```
❌ Login failed: Invalid email or password
```

### Step 2: Check Server Console
Look for these logs:

**Successful login:**
```
✅ Password verified for user: your@mavs.uta.edu
✅ User profile found: {userId: 1, xp: 0, level: 1, ...}
✅ Sending login response: {id: 1, ...}
```

**Failed login:**
```
❌ Login failed: Invalid password for user: your@mavs.uta.edu
```

**Missing profile:**
```
❌ User profile not found for user ID: 1
```

### Step 3: Check Database

**Check if user exists:**
```bash
cd server
npm run db:check-user your@mavs.uta.edu
```

This will show:
- ✅ If user exists
- ✅ If password hash exists
- ✅ If user profile exists
- ✅ User's XP and level

**Or check directly in DB Browser SQLite:**
```sql
-- Check user
SELECT id, email, password_hash IS NOT NULL as has_password 
FROM users 
WHERE email = 'your@mavs.uta.edu';

-- Check profile
SELECT * FROM user_profiles 
WHERE user_id = (SELECT id FROM users WHERE email = 'your@mavs.uta.edu');
```

### Step 4: Test API Directly

**Test login with curl:**
```bash
curl -X POST http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"your@mavs.uta.edu","password":"yourpassword"}'
```

**Expected response (success):**
```json
{
  "id": 1,
  "email": "your@mavs.uta.edu",
  "displayName": "your",
  "nickname": "your",
  "xp": 0,
  "level": 1,
  ...
}
```

**Expected response (failure):**
```json
{
  "error": "Invalid email or password"
}
```

## Common Issues & Solutions

### Issue 1: "User profile not found"
**Cause:** User exists but profile doesn't
**Fix:** Run this to check:
```bash
cd server
npm run db:check-user your@mavs.uta.edu
```

If profile is missing, the user needs to be recreated or the profile needs to be created manually.

### Issue 2: "Invalid email or password"
**Possible causes:**
1. Wrong password
2. User doesn't exist
3. Password hash is missing/corrupted

**Fix:**
1. Verify password is correct
2. Check if user exists: `npm run db:check-user your@mavs.uta.edu`
3. If password hash is missing, user needs to be deleted and recreated

### Issue 3: "Login failed: No user data received from server"
**Cause:** API returned empty response or error
**Fix:**
1. Check server console for errors
2. Check if backend is running on port 3001
3. Check network tab in browser DevTools

### Issue 4: User data not showing on dashboard
**Cause:** User state not set properly
**Fix:**
1. Check browser console for "✅ Login successful" message
2. Check if user object has `id` property
3. Refresh page - user should be loaded from localStorage

## Verification Steps

### 1. Verify User Exists
```bash
cd server
npm run db:check-user your@mavs.uta.edu
```

### 2. Verify Password Hash
```bash
cd server
npm run db:verify-passwords
```

### 3. Test Login
1. Open browser console (F12)
2. Try to login
3. Check console logs
4. Check server logs

### 4. Verify User State
After successful login:
1. Open browser console
2. Type: `localStorage.getItem('uta-gamify:user')`
3. Should see user JSON with id, email, xp, level

## Creating a Test Account

If you need to create a fresh account:

1. **Sign up via UI:**
   - Go to signup page
   - Enter email ending with @mavs.uta.edu
   - Enter password (8+ characters)
   - Submit

2. **Verify account created:**
   ```bash
   cd server
   npm run db:check-user your@mavs.uta.edu
   ```

3. **Test login:**
   - Use the email and password you just created
   - Should work immediately

## If Still Not Working

1. **Check both servers are running:**
   - Backend: `http://localhost:3001`
   - Frontend: `http://localhost:5173`

2. **Check environment variables:**
   - Frontend: `VITE_USE_API` should be `true` (or not set, defaults to true)
   - Backend: Database path is correct

3. **Check database file:**
   - Location: `database/mavpal.db`
   - File exists and is readable/writable

4. **Clear browser cache:**
   - Clear localStorage
   - Hard refresh (Ctrl+Shift+R)

5. **Check network tab:**
   - Open DevTools → Network tab
   - Try login
   - Check if `/api/auth/login` request is made
   - Check response status and body

## Logs to Watch For

### Successful Login Flow:
```
[Browser] 🔐 Attempting login for: your@mavs.uta.edu
[Browser] 🌐 API Request: POST http://localhost:3001/api/auth/login
[Server] ✅ Password verified for user: your@mavs.uta.edu
[Server] ✅ User profile found: {userId: 1, xp: 0, level: 1, ...}
[Server] ✅ Sending login response: {id: 1, ...}
[Browser] ✅ API Success: {id: 1, ...}
[Browser] 📥 Received user data from API: {id: 1, ...}
[Browser] ✅ Login successful, setting user data: {id: 1, ...}
```

If you see all these logs, login is working correctly!




