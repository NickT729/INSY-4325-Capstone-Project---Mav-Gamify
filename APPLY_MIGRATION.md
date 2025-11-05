# 🔧 How to Add first_name and last_name to Existing Database

## ⚠️ Important

If your database was created **before** we added first_name and last_name columns, you need to run this migration to add them.

## 📋 Step-by-Step Instructions

### Option 1: Using DB Browser for SQLite (Recommended)

1. **Open DB Browser for SQLite**
   - Open your database: `database/mavpal.db`

2. **Go to "Execute SQL" tab**

3. **Copy and paste this SQL:**
   ```sql
   -- Add first_name and last_name columns to user_profiles table
   ALTER TABLE user_profiles ADD COLUMN first_name TEXT;
   ALTER TABLE user_profiles ADD COLUMN last_name TEXT;
   ```

4. **Click "Execute SQL" button (▶️)**

5. **Verify the columns were added:**
   - Go to "Browse Data" tab
   - Select `user_profiles` table
   - You should now see `first_name` and `last_name` columns

### Option 2: Using Command Line

**Windows PowerShell:**
```powershell
cd "uta-gamify\database"
sqlite3 mavpal.db "ALTER TABLE user_profiles ADD COLUMN first_name TEXT;"
sqlite3 mavpal.db "ALTER TABLE user_profiles ADD COLUMN last_name TEXT;"
```

**Or using the migration file:**
```powershell
cd "uta-gamify\database"
sqlite3 mavpal.db < migration_add_names.sql
```

### Option 3: Verify Columns Exist

After running the migration, verify it worked:

```sql
-- Check if columns exist
SELECT sql FROM sqlite_master WHERE type='table' AND name='user_profiles';
```

You should see `first_name TEXT` and `last_name TEXT` in the output.

## ✅ Verification

After running the migration:

1. **Check columns exist:**
   ```sql
   PRAGMA table_info(user_profiles);
   ```
   Look for `first_name` and `last_name` in the results.

2. **Check existing data:**
   ```sql
   SELECT user_id, first_name, last_name, display_name 
   FROM user_profiles;
   ```
   Existing users will have `NULL` for first_name and last_name (this is expected).

3. **New accounts will have names:**
   - Create a new test account
   - Check the database - it should have first_name and last_name populated

## 🆕 For New Databases

If you're creating a **new database** from scratch, the columns are already in the schema:
- `database/schema.sqlite.sql` - Already includes first_name and last_name
- No migration needed for new databases

## ⚠️ Important Notes

- **Existing users** will have `NULL` for first_name and last_name (they were created before this feature)
- **New users** will have first_name and last_name populated during registration
- The migration is **safe** - it only adds columns, doesn't modify existing data

