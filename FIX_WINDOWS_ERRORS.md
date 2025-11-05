# Fixing Windows Installation Errors

## The Problem
`better-sqlite3` requires Visual Studio Build Tools to compile on Windows. You're getting this error because the build tools aren't installed.

## Solution: Install Visual Studio Build Tools

### Option 1: Install Build Tools (Recommended)

1. **Download Visual Studio Build Tools:**
   - Go to: https://visualstudio.microsoft.com/downloads/
   - Scroll down to "All Downloads" → "Tools for Visual Studio"
   - Download "Build Tools for Visual Studio 2022"

2. **Install with the right workload:**
   - Run the installer
   - Select "Desktop development with C++" workload
   - Click "Install"
   - Wait for installation (this may take 10-20 minutes)

3. **After installation, try again:**
   ```powershell
   cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify\server"
   npm install
   ```

### Option 2: Use SQLite via Node-SQLite (Alternative)

If you don't want to install Build Tools, I can switch the code to use a different SQLite library that doesn't require compilation. Let me know if you prefer this option.

### Option 3: Use Pre-built Database File

We could also create the database file manually using DB Browser SQLite, then the server would just need to read it. But this is less flexible.

## Quick Fix Steps

1. **Install Visual Studio Build Tools** (see Option 1 above)
2. **Restart your terminal/command prompt**
3. **Run:**
   ```powershell
   cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify\server"
   npm install
   npm run db:setup
   npm run dev
   ```

## What You'll See After Fix

Once installed, `npm install` should complete successfully and you'll see:
```
added 50 packages, and audited 50 packages in 10s
```

Then you can run:
```
npm run db:setup
npm run dev
```

The server should start without errors!

