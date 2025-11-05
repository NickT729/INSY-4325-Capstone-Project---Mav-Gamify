# 📊 Database Structure - Where Data is Stored

## 🗂️ Table Structure Overview

### `users` Table
**Purpose:** Authentication and account information only

**Columns:**
- `id` - Primary key
- `email` - User's email (must be @mavs.uta.edu)
- `password_hash` - Hashed password
- `created_at` - Account creation timestamp (IMMUTABLE)
- `updated_at` - Last update timestamp
- `last_login` - Last login timestamp

**What it does NOT contain:**
- ❌ First name
- ❌ Last name
- ❌ Display name
- ❌ XP
- ❌ Level
- ❌ Profile information

### `user_profiles` Table
**Purpose:** All user profile data, gamification data, and personal information

**Columns:**
- `id` - Primary key
- `user_id` - Foreign key to `users.id`
- `first_name` - ✅ **User's first name** (stored here)
- `last_name` - ✅ **User's last name** (stored here)
- `display_name` - Full display name (usually "First Last")
- `nickname` - User's nickname
- `major` - Academic major
- `college` - College/school
- `class_year` - Graduation year
- `avatar_url` - Profile picture URL
- `xp` - ✅ **Current XP points** (stored here)
- `level` - ✅ **Current level** (stored here)
- `created_at` - ✅ **Profile creation timestamp** (IMMUTABLE - never changes)
- `updated_at` - Last update timestamp (changes when XP/level/profile updates)

## 🔍 Where to Find Specific Data

### First Name and Last Name
**Location:** `user_profiles` table
**Columns:** `first_name`, `last_name`

```sql
SELECT first_name, last_name 
FROM user_profiles 
WHERE user_id = 1;
```

### XP and Level
**Location:** `user_profiles` table
**Columns:** `xp`, `level`

```sql
SELECT xp, level 
FROM user_profiles 
WHERE user_id = 1;
```

### Account Creation Date
**Location:** 
- `users.created_at` - Account creation (when email/password was created)
- `user_profiles.created_at` - Profile creation (when profile was created)

**Both should be the same** (created simultaneously during registration)

```sql
SELECT 
    u.created_at AS account_created,
    up.created_at AS profile_created
FROM users u
JOIN user_profiles up ON u.id = up.user_id
WHERE u.id = 1;
```

## 🔒 Data Protection

### `created_at` is IMMUTABLE
- **Never updated** after initial creation
- **Protected in all UPDATE queries** - only `updated_at` changes
- **Your original account creation date is preserved forever**

### XP and Level
- **Updated incrementally** when you complete activities
- **Never reset** unless explicitly changed
- **Both updated together** (level calculated from XP)

## 📝 Example Query - Complete User View

```sql
SELECT 
    -- From users table
    u.id,
    u.email,
    u.created_at AS account_created,
    u.last_login,
    
    -- From user_profiles table
    up.first_name,
    up.last_name,
    up.display_name,
    up.xp,
    up.level,
    up.major,
    up.college,
    up.class_year,
    up.created_at AS profile_created,
    up.updated_at AS profile_last_updated
FROM users u
JOIN user_profiles up ON u.id = up.user_id
WHERE u.email = 'your-email@mavs.uta.edu';
```

## ✅ Quick Reference

| Data | Table | Column |
|------|-------|--------|
| First Name | `user_profiles` | `first_name` |
| Last Name | `user_profiles` | `last_name` |
| Email | `users` | `email` |
| Password | `users` | `password_hash` |
| XP | `user_profiles` | `xp` |
| Level | `user_profiles` | `level` |
| Account Created | `users` | `created_at` |
| Profile Created | `user_profiles` | `created_at` |

## 🛡️ Protection Mechanisms

1. **All UPDATE queries explicitly exclude `created_at`**
2. **Only specified fields are updated** (never all fields)
3. **`updated_at` automatically updated** on changes
4. **`created_at` only set on INSERT** (never on UPDATE)

## 🔧 If `created_at` Was Accidentally Changed

If you notice `created_at` has been updated incorrectly:

1. **Check the database directly:**
   ```sql
   SELECT created_at, updated_at FROM user_profiles WHERE user_id = 1;
   ```

2. **Verify it's actually changed** (not just a display issue)

3. **Restore from backup** if you have one

4. **The fix is now in place** - future updates will preserve `created_at`

