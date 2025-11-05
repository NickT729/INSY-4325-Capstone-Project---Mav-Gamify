# 🗑️ How to Delete a Test Account

## 📋 Method 1: Using DB Browser for SQLite (Recommended)

### Step 1: Find Your Test Account

1. **Open DB Browser for SQLite**
2. **Open database:** `uta-gamify/database/mavpal.db`
3. **Go to "Execute SQL" tab**
4. **Run this query to find your account:**

```sql
SELECT 
    u.id,
    u.email,
    up.first_name,
    up.last_name,
    up.display_name
FROM users u
LEFT JOIN user_profiles up ON u.id = up.user_id
ORDER BY u.id;
```

This will show all accounts with their IDs. Find your test account and note the `id`.

### Step 2: Delete the Account

**Option A: Delete by Email (Easiest)**

```sql
-- Replace 'your-email@mavs.uta.edu' with your test account email
DELETE FROM users WHERE email = 'your-email@mavs.uta.edu';
```

**Option B: Delete by User ID**

```sql
-- Replace 1 with the actual user ID from Step 1
DELETE FROM users WHERE id = 1;
```

**Note:** Because of the foreign key constraint with `ON DELETE CASCADE`, deleting from `users` will automatically delete the corresponding row in `user_profiles`.

### Step 3: Verify Deletion

```sql
-- Check that the account is gone
SELECT * FROM users WHERE email = 'your-email@mavs.uta.edu';
-- Should return no rows

SELECT * FROM user_profiles WHERE user_id = 1;
-- Should return no rows (replace 1 with your user ID)
```

## 📋 Method 2: Delete from Both Tables Manually

If you want to be extra careful, delete from both tables:

```sql
-- First, delete from user_profiles
DELETE FROM user_profiles WHERE user_id = 1;

-- Then, delete from users
DELETE FROM users WHERE id = 1;
```

## ✅ After Deletion

1. **Close DB Browser** (or refresh if you keep it open)
2. **Go to the signup page** in your app
3. **Create a new account** with the same or different email
4. **Check the database** - the new account should have `first_name` and `last_name` populated!

## 🔍 Verify New Account Has Names

After creating the new account, check:

```sql
SELECT 
    u.id,
    u.email,
    up.first_name,
    up.last_name,
    up.display_name,
    up.xp,
    up.level
FROM users u
JOIN user_profiles up ON u.id = up.user_id
WHERE u.email = 'your-new-email@mavs.uta.edu';
```

You should see `first_name` and `last_name` populated!

## ⚠️ Important Notes

- **Deleting a user will also delete:**
  - Their profile in `user_profiles`
  - Any quiz completions
  - Any flashcard completions
  - Any challenge progress
  - Any notifications

- **This is permanent** - make sure you want to delete the account!

- **If you want to keep the data but just test with a new email:**
  - Just create a new account with a different email
  - No need to delete the old one

## 🎯 Quick Delete Script

If you want a one-liner to delete by email:

```sql
-- Replace with your test email
DELETE FROM users WHERE email = 'test@mavs.uta.edu';
```

That's it! The cascade will handle the rest.

