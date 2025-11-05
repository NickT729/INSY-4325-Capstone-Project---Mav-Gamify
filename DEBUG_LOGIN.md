# 🔍 Debug Login Issues

## What to Check

When login fails, check **BOTH** console logs:

### 1. Browser Console (F12 → Console)
Look for:
- `🔐 Attempting login...`
- `🌐 API Request: POST http://localhost:3001/api/auth/login`
- `📡 API Response: 401` or `500` or `200`
- `❌ API Error:` messages

### 2. Backend Console (Terminal running server)
Look for:
- `🔐 Login attempt:`
- `🔍 Looking up user in database...`
- `🔍 User found:` (shows if user exists and password_hash status)
- `🔐 Comparing password...`
- `✅ Password verified` or `❌ Login failed: Invalid password`

## Common Issues

### Issue 1: "Account security error"
- **Backend shows:** `❌ User has no password hash!`
- **Fix:** The user account was created without a password hash
- **Solution:** Delete the user and create a new account

### Issue 2: "Invalid email or password"
- **Backend shows:** `❌ Login failed: Invalid password`
- **Possible causes:**
  - Wrong password entered
  - Password hash in database doesn't match
  - Password wasn't saved correctly during registration

### Issue 3: "Login failed" (generic)
- **Check browser console:** What's the actual API error?
- **Check backend console:** Is the request reaching the server?

## Quick Test

1. **Check if user exists in database:**
   ```sql
   SELECT id, email, LENGTH(password_hash) as hash_length FROM users WHERE email = 'your-email@mavs.uta.edu';
   ```
   - If no rows: User doesn't exist
   - If hash_length is 0 or NULL: Password wasn't saved

2. **Check backend logs when you try to login**
   - You should see the detailed logs showing what's happening

## If Still Not Working

1. **Restart backend server**
2. **Clear browser cache/localStorage**
3. **Try creating a NEW account** (different email)
4. **Check both console logs** for the detailed error messages

