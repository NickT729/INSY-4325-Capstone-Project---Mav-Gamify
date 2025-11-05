// Script to fix orphaned users (users without profiles)
// Run this to clean up any users that were created but profiles failed

import { initDatabase, getDatabase, saveDatabase, createDatabaseWrapper } from '../db.js';

async function fixOrphanedUsers() {
  try {
    console.log('🔍 Checking for orphaned users...');
    
    const dbInstance = await initDatabase();
    const db = createDatabaseWrapper(dbInstance);
    
    // Find users without profiles
    const orphanedUsers = db.prepare(`
      SELECT u.id, u.email, u.created_at
      FROM users u
      LEFT JOIN user_profiles up ON u.id = up.user_id
      WHERE up.user_id IS NULL
    `).all();
    
    if (orphanedUsers.length === 0) {
      console.log('✅ No orphaned users found!');
      return;
    }
    
    console.log(`⚠️ Found ${orphanedUsers.length} orphaned user(s):`);
    orphanedUsers.forEach(user => {
      console.log(`  - ID: ${user.id}, Email: ${user.email}, Created: ${user.created_at}`);
    });
    
    // Delete orphaned users
    console.log('\n🗑️ Deleting orphaned users...');
    for (const user of orphanedUsers) {
      db.prepare('DELETE FROM users WHERE id = ?').run(user.id);
      console.log(`  ✅ Deleted user: ${user.email} (ID: ${user.id})`);
    }
    
    saveDatabase();
    console.log('\n✅ Cleanup complete! Orphaned users have been removed.');
    console.log('💡 You can now try registering again.');
    
  } catch (error) {
    console.error('❌ Error fixing orphaned users:', error);
    process.exit(1);
  }
}

fixOrphanedUsers();

