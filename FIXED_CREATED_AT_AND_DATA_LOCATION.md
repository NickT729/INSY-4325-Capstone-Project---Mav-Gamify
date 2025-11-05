# ✅ Fixed: Data Location and `created_at` Protection

## 🔍 Issues Identified

1. **User couldn't find first_name and last_name** - Expected them in both `users` and `user_profiles` tables
2. **`created_at` was being updated** - Should remain immutable after account creation
3. **Need to ensure data integrity** - XP, levels, and creation dates must be protected

## ✅ Solutions Implemented

### 1. Data Location Clarification

**First Name and Last Name:**
- **Stored in:** `user_profiles` table (columns: `first_name`, `last_name`)
- **NOT stored in:** `users` table (only contains email, password_hash, timestamps)
- **Why:** `users` table is for authentication only, `user_profiles` is for all profile data

**XP and Level:**
- **Stored in:** `user_profiles` table (columns: `xp`, `level`)
- **Updated when:** Quizzes, flashcards, or challenges are completed

**Account Creation Date:**
- **Stored in TWO places:**
  - `users.created_at` - When account was created
  - `user_profiles.created_at` - When profile was created
- **Both should be identical** (created simultaneously during registration)

### 2. `created_at` Protection

**Problem:** `created_at` was potentially being updated when it should remain immutable.

**Solution:** 
- ✅ Added explicit comments in all UPDATE queries
- ✅ Ensured `created_at` is NEVER included in UPDATE statements
- ✅ Only `updated_at` is updated on changes
- ✅ Added `updated_at = CURRENT_TIMESTAMP` to all XP/level updates

**Files Updated:**
- `server/routes/users.js` - Profile update endpoint
- `server/routes/quizzes.js` - Quiz completion XP updates
- `server/routes/flashcards.js` - Flashcard completion XP updates
- `server/routes/challenges.js` - Challenge completion XP updates

### 3. Data Integrity Protection

**All UPDATE queries now:**
- ✅ Explicitly exclude `created_at`
- ✅ Only update specified fields (xp, level, nickname, etc.)
- ✅ Update `updated_at` timestamp
- ✅ Never overwrite immutable data

## 📊 How to View Your Data

### View First and Last Name

```sql
SELECT first_name, last_name, display_name
FROM user_profiles
WHERE user_id = 1;
```

### View Complete User Info

```sql
SELECT 
    u.id,
    u.email,
    u.created_at AS account_created,
    up.first_name,
    up.last_name,
    up.display_name,
    up.xp,
    up.level,
    up.created_at AS profile_created,
    up.updated_at AS profile_updated
FROM users u
JOIN user_profiles up ON u.id = up.user_id
WHERE u.email = 'your-email@mavs.uta.edu';
```

### Verify `created_at` is Protected

```sql
-- Check if created_at matches account creation
SELECT 
    u.created_at AS account_created,
    up.created_at AS profile_created,
    up.updated_at AS last_updated
FROM users u
JOIN user_profiles up ON u.id = up.user_id
WHERE u.id = 1;

-- If created_at = updated_at, it means the account was never updated
-- If created_at < updated_at, it means updates happened but created_at was preserved
```

## 🛡️ Protection Guarantees

1. **`created_at` is IMMUTABLE**
   - Set only on INSERT (account creation)
   - Never updated by any UPDATE query
   - Preserves original account creation date forever

2. **XP and Level are Preserved**
   - Updated incrementally (added to, not replaced)
   - Both updated together (level calculated from XP)
   - Never reset unless explicitly changed

3. **First and Last Name**
   - Stored in `user_profiles.first_name` and `user_profiles.last_name`
   - Can be updated via profile update endpoint
   - Used to generate `display_name` on registration

## 📝 Database Tables Summary

### `users` Table
- Authentication data only
- Columns: `id`, `email`, `password_hash`, `created_at`, `updated_at`, `last_login`
- Does NOT contain: first_name, last_name, xp, level

### `user_profiles` Table
- All profile and gamification data
- Columns: `id`, `user_id`, `first_name`, `last_name`, `display_name`, `nickname`, `major`, `college`, `class_year`, `avatar_url`, `xp`, `level`, `created_at`, `updated_at`
- Contains: first_name, last_name, xp, level, and all profile information

## ✅ Verification Steps

1. **Check data location:**
   ```sql
   SELECT first_name, last_name FROM user_profiles WHERE user_id = 1;
   ```

2. **Verify `created_at` is preserved:**
   ```sql
   SELECT created_at, updated_at FROM user_profiles WHERE user_id = 1;
   -- created_at should be older than or equal to updated_at
   ```

3. **Check XP and level:**
   ```sql
   SELECT xp, level FROM user_profiles WHERE user_id = 1;
   ```

## 🎯 Quick Reference

| What | Where | Column |
|------|-------|--------|
| First Name | `user_profiles` | `first_name` |
| Last Name | `user_profiles` | `last_name` |
| XP | `user_profiles` | `xp` |
| Level | `user_profiles` | `level` |
| Account Created | `users` | `created_at` |
| Profile Created | `user_profiles` | `created_at` |

**All future updates will preserve `created_at` and maintain data integrity!** ✅

