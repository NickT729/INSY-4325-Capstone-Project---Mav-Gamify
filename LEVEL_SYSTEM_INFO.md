# 📊 Level System - How It Works

## ✅ Fixed: All New Accounts Start Correctly

**New accounts now:**
- Start at **Level 1** (not Level 0)
- Start with **0 XP**
- Must earn XP to level up

## 🎯 Level System (1-20)

### XP Requirements for Each Level:

| Level | XP Required | XP for Next Level |
|-------|-------------|-------------------|
| 1     | 0           | 100               |
| 2     | 100         | 150               |
| 3     | 250         | 200               |
| 4     | 450         | 250               |
| 5     | 700         | 300               |
| 6     | 1000        | 350               |
| 7     | 1350        | 400               |
| 8     | 1750        | 450               |
| 9     | 2200        | 500               |
| 10    | 2700        | 550               |
| 11    | 3250        | 600               |
| 12    | 3850        | 650               |
| 13    | 4500        | 700               |
| 14    | 5200        | 750               |
| 15    | 5950        | 800               |
| 16    | 6750        | 850               |
| 17    | 7600        | 900               |
| 18    | 8500        | 950               |
| 19    | 9450        | 550               |
| 20    | 10000       | MAX LEVEL         |

### XP Earning Rates:

- **Quiz (per question):** 25 XP
  - 5-question quiz = 125 XP
  - 10-question quiz = 250 XP
  
- **Flashcard (per card):** 15 XP
  - 10-card set = 150 XP
  - 20-card set = 300 XP

- **Daily Challenges:** Varies by challenge

## 🔄 How Level Updates Work

1. **User earns XP** (completes quiz, flashcard, challenge)
2. **Frontend calls** `updateProfile({ xp: newXP })`
3. **Backend receives** XP update via API
4. **Level calculator** automatically determines new level based on XP
5. **Database updated** with both XP and calculated level
6. **User sees** updated level immediately

## ✅ Database Updates

- **Every XP change** is saved to database
- **Level is auto-calculated** and saved
- **Database updates** happen immediately
- **Can verify** in DB Browser SQLite

## 🛠️ Fix Existing Users

If you have existing users with incorrect XP/levels, run:

```powershell
cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify\server"
npm run db:fix-users
```

This will:
- Set all users to **Level 1**
- Set all users to **0 XP**
- Ready for fresh start!

## 📍 Verification

1. **Create new account** → Should be Level 1, 0 XP
2. **Complete a quiz** → XP increases, level may increase
3. **Check DB Browser** → `user_profiles` table shows updated XP and level
4. **Dashboard** → Shows correct level and progress bar

## 🎯 Max Level

- **Maximum Level:** 20
- **XP Cap:** 10,000 XP
- **After Level 20:** Users stay at Level 20, but can continue earning XP





