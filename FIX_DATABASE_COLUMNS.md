# 🔧 FIX: Database Missing Columns

## ✅ AUTOMATIC FIX (Recommended)

**Just restart your backend server!** The migration will run automatically on startup.

1. Stop your backend server (Ctrl+C)
2. Start it again:
   ```bash
   cd server
   npm run dev
   ```
3. Look for these messages in the console:
   ```
   🔍 Checking database schema...
   ➕ Adding missing column: first_name
   ✅ first_name column added
   ➕ Adding missing column: last_name
   ✅ last_name column added
   ✅ Migration complete! Added 2 column(s).
   💾 Database saved after migration
   ```

## 🔧 Manual Fix (If Automatic Doesn't Work)

### Option 1: Using DB Browser for SQLite

1. **Open DB Browser for SQLite**
2. **Open database:** `uta-gamify/database/mavpal.db`
3. **Go to "Execute SQL" tab**
4. **Run this SQL:**
   ```sql
   ALTER TABLE user_profiles ADD COLUMN first_name TEXT;
   ALTER TABLE user_profiles ADD COLUMN last_name TEXT;
   ```
5. **Click "Execute SQL" (▶️)**
6. **Save the database** (File → Write Changes)

### Option 2: Quick Verification

After running the migration (automatic or manual), verify:

```sql
PRAGMA table_info(user_profiles);
```

You should see `first_name` and `last_name` in the results.

## ✅ Test Registration

After the migration:

1. **Restart backend server** (if not already done)
2. **Go to signup page**
3. **Create a new account**
4. **It should work!** ✅

## 🐛 If Still Having Issues

If you still get errors:

1. **Check backend console** for migration messages
2. **Verify columns exist:**
   ```sql
   SELECT sql FROM sqlite_master WHERE name='user_profiles';
   ```
3. **Delete any orphaned users** (users without profiles):
   ```sql
   DELETE FROM users WHERE id NOT IN (SELECT user_id FROM user_profiles);
   ```

The automatic migration runs every time the server starts, so you should be good to go after restarting!

