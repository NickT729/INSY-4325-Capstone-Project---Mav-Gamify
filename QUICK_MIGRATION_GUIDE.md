# 🔧 Quick Guide: Add first_name and last_name Columns

## ⚠️ The Problem

Your database was created **before** we added the `first_name` and `last_name` columns. The schema files are updated, but your existing `mavpal.db` file doesn't have these columns yet.

## ✅ The Solution

You need to run a migration to add the columns to your existing database.

## 📋 Easy Steps (Using DB Browser for SQLite)

1. **Open DB Browser for SQLite**
   - If you don't have it: Download from https://sqlitebrowser.org/

2. **Open your database**
   - File → Open Database
   - Navigate to: `uta-gamify/database/mavpal.db`
   - Click Open

3. **Execute the migration**
   - Click the **"Execute SQL"** tab (at the top)
   - Copy and paste this SQL:

   ```sql
   ALTER TABLE user_profiles ADD COLUMN first_name TEXT;
   ALTER TABLE user_profiles ADD COLUMN last_name TEXT;
   ```

4. **Run it**
   - Click the **"Execute SQL"** button (▶️ play icon) or press F5
   - You should see: "Query executed successfully"

5. **Verify it worked**
   - Go to **"Browse Data"** tab
   - Select `user_profiles` from the table dropdown
   - You should now see `first_name` and `last_name` columns
   - They may be empty (NULL) for existing users - that's normal!

## ✅ Verify It Worked

After running the migration, check:

1. Go to **"Browse Data"** tab
2. Select `user_profiles` table
3. Look for columns: `first_name` and `last_name`
4. They should be there (even if empty for existing users)

## 🆕 What Happens Next

- **Existing users**: Will have `NULL` for first_name and last_name (they were created before this feature)
- **New users**: Will have first_name and last_name populated when they create an account

## 🔍 Test It

1. Create a new test account through the signup page
2. Check the database - the new user should have `first_name` and `last_name` populated

## ⚠️ Troubleshooting

**If you get an error "duplicate column name":**
- The columns already exist! You're all set.

**If you don't see the columns after running:**
- Make sure you clicked "Execute SQL" (not just typed it)
- Refresh the "Browse Data" view (F5)
- Check that you're looking at the `user_profiles` table (not `users`)

## 📝 Alternative: Using Command Line

If you have sqlite3 command line tool:

```bash
cd uta-gamify/database
sqlite3 mavpal.db < add_names_columns.sql
```

Or manually:
```bash
sqlite3 mavpal.db "ALTER TABLE user_profiles ADD COLUMN first_name TEXT;"
sqlite3 mavpal.db "ALTER TABLE user_profiles ADD COLUMN last_name TEXT;"
```

