# 📊 How to View XP and Levels in SQLite Database

## 📍 Database Location

**Table:** `user_profiles`  
**File:** `database/mavpal.db`

## 🔍 Columns to Look At

In the `user_profiles` table, you'll find:

- **`xp`** - Current XP points for the user
- **`level`** - Current level (1-20)
- **`user_id`** - Links to the `users` table
- **`display_name`** - User's display name

## 📋 Step-by-Step Instructions

### Using DB Browser for SQLite

1. **Open DB Browser for SQLite**
   - Download from: https://sqlitebrowser.org/

2. **Open the database file**
   - Click "Open Database"
   - Navigate to: `uta-gamify/database/mavpal.db`
   - Click "Open"

3. **View the `user_profiles` table**
   - Click on the **"Browse Data"** tab
   - Select **`user_profiles`** from the "Table" dropdown

4. **See XP and Level columns**
   - You'll see columns:
     - `user_id` - User ID
     - `display_name` - User's name
     - `xp` - Current XP (e.g., 500, 1000, 2500)
     - `level` - Current level (e.g., 1, 2, 3, ... 20)
     - `nickname`, `major`, `college`, etc.

### Using SQL Query

You can also run SQL queries:

```sql
-- View all users with XP and levels
SELECT 
    user_id,
    display_name,
    xp,
    level,
    major,
    college
FROM user_profiles
ORDER BY xp DESC;
```

```sql
-- View a specific user
SELECT 
    u.email,
    up.display_name,
    up.xp,
    up.level
FROM users u
JOIN user_profiles up ON u.id = up.user_id
WHERE u.email = 'your-email@mavs.uta.edu';
```

## 📊 Example Data

| user_id | display_name | xp   | level | major | college        |
|---------|--------------|------|-------|-------|----------------|
| 1       | john         | 500  | 2     | IS    | Engineering    |
| 2       | jane         | 1250 | 4     | Math  | Science        |
| 3       | bob          | 0    | 1     | BA    | Business       |

## 🔄 When XP Updates

XP and level are updated in `user_profiles` when:
- ✅ User completes a quiz
- ✅ User completes flashcards
- ✅ User completes a challenge
- ✅ User profile is updated via API

## ⚠️ Important Notes

- **Level is calculated automatically** from XP by the backend
- **Both XP and level are stored** in the database
- **Updates happen immediately** when activities are completed
- **Refresh DB Browser** (F5) to see latest changes

## 🎯 Quick Reference

**Table:** `user_profiles`  
**XP Column:** `xp`  
**Level Column:** `level`  
**Database File:** `database/mavpal.db`

**That's it!** The XP and levels are right there in the `user_profiles` table. 🎉

