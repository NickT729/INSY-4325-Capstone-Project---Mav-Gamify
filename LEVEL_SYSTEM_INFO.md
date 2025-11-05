# 📊 Level System - How It Works

## ✅ Fixed: All New Accounts Start Correctly

**New accounts now:**
- Start at **Level 1** (not Level 0)
- Start with **0 XP**
- Must earn XP to level up

## 🎯 Level System (1-20) - Fast Leveling!

### XP Requirements for Each Level:

| Level | XP Required | XP for Next Level | Example Activities |
|-------|-------------|-------------------|-------------------|
| 1     | 0           | 100               | Start here! |
| 2     | 100         | 150               | 1-2 quizzes or 1 flashcard set |
| 3     | 250         | 200               | 2-3 quizzes |
| 4     | 450         | 250               | 3-4 quizzes |
| 5     | 700         | 300               | Complete daily tasks + 1 quiz |
| 6     | 1000        | 350               | ~2 days of daily tasks |
| 7     | 1350        | 400               | ~2 days of daily tasks |
| 8     | 1750        | 450               | ~2 days of daily tasks |
| 9     | 2200        | 500               | ~2 days of daily tasks |
| 10    | 2700        | 550               | ~3 days of daily tasks |
| 11    | 3250        | 600               | ~3 days of daily tasks |
| 12    | 3850        | 650               | ~4 days of daily tasks |
| 13    | 4500        | 700               | ~4 days of daily tasks |
| 14    | 5200        | 750               | ~5 days of daily tasks |
| 15    | 5950        | 800               | ~6 days of daily tasks |
| 16    | 6750        | 850               | ~6 days of daily tasks |
| 17    | 7600        | 900               | ~7 days of daily tasks |
| 18    | 8500        | 950               | ~8 days of daily tasks |
| 19    | 9450        | 550               | ~9 days of daily tasks |
| 20    | 10000       | MAX LEVEL         | ~10 days of daily tasks! |

**💡 Tip:** Complete all daily tasks each day to earn **1,050 bonus XP** - that's enough to level up every 1-2 days in the early levels!

### XP Earning Rates (Increased for Faster Leveling):

- **Quiz (per question):** 75 XP ⬆️
  - 5-question quiz = 375 XP
  - 10-question quiz = 750 XP
  
- **Flashcard (per card):** 40 XP ⬆️
  - 10-card set = 400 XP
  - 20-card set = 800 XP

- **Daily Challenges:** Varies by challenge (typically 200-500 XP)

- **Daily Task Bonuses:**
  - Complete 1 Quiz: +300 XP bonus
  - Study 1 Flashcard Set: +250 XP bonus
  - Create 1 Quiz/Flashcard: +300 XP bonus
  - Join 1 Challenge: +200 XP bonus
  - **Complete All Daily Tasks:** Earn all bonus XP (1,050 XP total per day!)

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






