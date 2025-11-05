// Database migration utility
// Automatically adds missing columns to existing databases

export function migrateDatabase(db) {
  console.log('🔍 Checking database schema...');
  
  try {
    // Check if user_profiles table exists using sql.js compatible syntax
    const tableCheck = db.exec(`
      SELECT name FROM sqlite_master 
      WHERE type='table' AND name='user_profiles'
    `);
    
    if (!tableCheck || !tableCheck[0] || tableCheck[0].values.length === 0) {
      console.log('⚠️ user_profiles table does not exist. Please run schema.sqlite.sql');
      return 0;
    }
    
    // Get current table structure using PRAGMA
    const tableInfoResult = db.exec('PRAGMA table_info(user_profiles)');
    let columns = [];
    
    if (tableInfoResult && tableInfoResult[0] && tableInfoResult[0].values) {
      // sql.js returns results as [values, columns]
      // values is array of arrays, columns is array of column names
      const values = tableInfoResult[0].values;
      // Find the index of 'name' column
      const columnsInfo = tableInfoResult[0].columns;
      const nameIndex = columnsInfo ? columnsInfo.indexOf('name') : 1;
      
      columns = values.map(row => row[nameIndex]).filter(Boolean);
    }
    
    console.log('📋 Current columns:', columns.join(', ') || 'none found');
    
    let migrationsRun = 0;
    
    // Check and add first_name if missing
    if (!columns.includes('first_name')) {
      console.log('➕ Adding missing column: first_name');
      try {
        db.exec('ALTER TABLE user_profiles ADD COLUMN first_name TEXT');
        migrationsRun++;
        console.log('   ✅ first_name column added');
      } catch (e) {
        if (e.message && e.message.includes('duplicate column')) {
          console.log('   ⚠️ first_name column already exists (ignoring)');
        } else {
          throw e;
        }
      }
    }
    
    // Check and add last_name if missing
    if (!columns.includes('last_name')) {
      console.log('➕ Adding missing column: last_name');
      try {
        db.exec('ALTER TABLE user_profiles ADD COLUMN last_name TEXT');
        migrationsRun++;
        console.log('   ✅ last_name column added');
      } catch (e) {
        if (e.message && e.message.includes('duplicate column')) {
          console.log('   ⚠️ last_name column already exists (ignoring)');
        } else {
          throw e;
        }
      }
    }
    
    if (migrationsRun > 0) {
      console.log(`✅ Migration complete! Added ${migrationsRun} column(s).`);
    } else {
      console.log('✅ Database schema is up to date.');
    }
    
    return migrationsRun;
  } catch (error) {
    console.error('❌ Migration error:', error);
    console.error('Error details:', error.message);
    // Don't throw - let the server continue even if migration fails
    // The user can manually run the migration
    return 0;
  }
}

