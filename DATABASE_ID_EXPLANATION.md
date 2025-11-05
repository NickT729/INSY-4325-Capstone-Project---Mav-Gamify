# 📊 Database ID Structure Explained

## ✅ This is CORRECT - Not a Problem!

You're seeing **two different IDs** in the `user_profiles` table, and that's **exactly how it should be**!

## 🔑 Understanding the Two IDs

### `user_profiles.id` (Primary Key)
- This is the **row ID** of the profile record itself
- Auto-increments: 1, 2, 3, 4...
- Just identifies which profile row it is
- **Not related to the user ID**

### `user_profiles.user_id` (Foreign Key)
- This is the **link to the users table**
- References `users.id`
- This is what connects the profile to the user account

## 📋 Example

If you see:
```
users table:
  id = 10
  email = "test@mavs.uta.edu"

user_profiles table:
  id = 3          ← This is just the profile row number
  user_id = 10    ← This links to users.id = 10
```

**This is CORRECT!** ✅

The profile row #3 belongs to user #10.

## 🔍 How Login Works

1. Login finds user by email: `SELECT * FROM users WHERE email = ?`
   - Gets: `id = 10`

2. Then finds profile: `SELECT * FROM user_profiles WHERE user_id = ?`
   - Uses: `user_id = 10`
   - Finds: Profile with `user_id = 10` (which is profile `id = 3`)

## ❌ Actual Problems to Look For

### Problem 1: Missing Profile
```
users: id=10 exists
user_profiles: NO row with user_id=10
```
**Solution:** Profile wasn't created during registration

### Problem 2: Orphaned Profile
```
users: NO id=10
user_profiles: user_id=10 exists
```
**Solution:** User was deleted but profile wasn't (shouldn't happen with CASCADE)

### Problem 3: Mismatched user_id
```
users: id=10
user_profiles: user_id=15 (different number!)
```
**Solution:** Wrong user_id was saved during registration

## ✅ What You Should See

For a correctly created account:
- `users.id` = some number (e.g., 10)
- `user_profiles.user_id` = same number (e.g., 10)
- `user_profiles.id` = can be any number (e.g., 3) - doesn't matter!

## 🧪 Quick Check

Run this SQL to verify relationships:
```sql
SELECT 
  u.id AS user_id,
  u.email,
  up.id AS profile_id,
  up.user_id AS profile_user_id,
  up.display_name
FROM users u
LEFT JOIN user_profiles up ON u.id = up.user_id;
```

**Expected Result:**
- `user_id` should match `profile_user_id`
- `profile_id` can be different (that's fine!)

## 🎯 If Login Still Fails

The issue is NOT the ID structure. Check:
1. ✅ Password hash exists in `users.password_hash`
2. ✅ Profile exists with matching `user_id`
3. ✅ Email format is correct (@mavs.uta.edu)
4. ✅ Password is being hashed correctly during registration

