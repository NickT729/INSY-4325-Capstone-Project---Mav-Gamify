# 🚀 How to Restart Servers

## ✅ Servers Stopped

Both servers have been stopped. You can now restart them fresh.

## 📋 Step-by-Step Restart Instructions

### 1. Start Backend Server

Open a **new terminal window** and run:

```bash
cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify\server"
npm run dev
```

**Expected output:**
```
✅ Loaded existing database
🔍 Checking database schema...
✅ Database schema is up to date.
🚀 MavPal API server running on http://localhost:3001
```

**Keep this terminal open!** The backend must stay running.

---

### 2. Start Frontend Server

Open **another new terminal window** and run:

```bash
cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify"
npm run dev
```

**Expected output:**
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

**Keep this terminal open too!** The frontend must stay running.

---

## 🧪 Testing Account Creation & Login

### Step 1: Create New Account
1. Open browser to: `http://localhost:5173`
2. Navigate to "Sign Up" or go to: `http://localhost:5173/signup`
3. Fill in:
   - First Name: `Test`
   - Last Name: `User`
   - Email: `test@mavs.uta.edu` (or any @mavs.uta.edu email)
   - Password: `password123` (at least 8 characters)
   - Confirm Password: `password123`
4. Click "Create Account"

**Watch the backend terminal** - you should see:
```
📝 Registration request: { email: 'test@mavs.uta.edu', ... }
🔐 Hashing password...
✅ User created with ID: X
✅ User profile created successfully
✅ Registration complete: { userId: X, email: '...', ... }
```

### Step 2: Login
1. You should be automatically logged in after registration
2. If not, go to: `http://localhost:5173/login`
3. Enter the same email and password
4. Click "Sign In"

**Watch the backend terminal** - you should see:
```
🔐 Login attempt: { email: 'test@mavs.uta.edu', ... }
🔍 User found: { id: X, email: '...', hasPasswordHash: true, ... }
🔐 Comparing password...
✅ Password verified for user: test@mavs.uta.edu
✅ User profile found: { userId: X, xp: 0, level: 1, ... }
✅ Sending login response: { id: X, email: '...', xp: 0, level: 1, ... }
```

---

## 🐛 If Something Goes Wrong

### Backend Not Starting
- Check if port 3001 is already in use
- Make sure you're in the `server` directory
- Check for errors in the terminal

### Frontend Not Starting
- Check if port 5173 is already in use
- Make sure you're in the `uta-gamify` directory (not `server`)
- Check for errors in the terminal

### Registration Fails
- Check backend terminal for error messages
- Verify email ends with `@mavs.uta.edu`
- Verify password is at least 8 characters
- Check if email already exists

### Login Fails
- Check backend terminal for detailed logs
- Verify password is correct
- Check if account was created successfully
- Look for "User profile not found" errors

---

## 📊 Verify in Database

After creating an account, you can verify in the database:

1. Open `database/mavpal.db` in DB Browser for SQLite
2. Check `users` table:
   - Should see your email
   - Should have a `password_hash` value
3. Check `user_profiles` table:
   - Should have a row with `user_id` matching the `users.id`
   - Should have `first_name` and `last_name`
   - Should have `xp = 0` and `level = 1`

---

## ✅ Success Indicators

- ✅ Backend shows "🚀 MavPal API server running"
- ✅ Frontend shows "ready in xxx ms"
- ✅ Registration creates user and profile
- ✅ Login returns user data with xp and level
- ✅ You can navigate the app after login

Good luck! 🎉

