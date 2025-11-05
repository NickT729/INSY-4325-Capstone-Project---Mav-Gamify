// Script to check database relationships and identify any mismatches
import { initDatabase, createDatabaseWrapper } from '../db.js';

async function checkDatabaseRelationships() {
  try {
    console.log('🔍 Checking database relationships...\n');
    
    const dbInstance = await initDatabase();
    const db = createDatabaseWrapper(dbInstance);
    
    // Get all users
    const users = db.prepare('SELECT id, email FROM users ORDER BY id').all();
    console.log(`📊 Found ${users.length} user(s) in users table:\n`);
    
    for (const user of users) {
      console.log(`   User ID: ${user.id}, Email: ${user.email}`);
      
      // Check for corresponding profile
      const profile = db.prepare('SELECT id, user_id, display_name, first_name, last_name FROM user_profiles WHERE user_id = ?').get(user.id);
      
      if (profile) {
        console.log(`   ✅ Profile found:`);
        console.log(`      Profile ID: ${profile.id} (this is just the profile row ID)`);
        console.log(`      User ID (foreign key): ${profile.user_id} (should match user.id = ${user.id})`);
        console.log(`      Display Name: ${profile.display_name || 'N/A'}`);
        console.log(`      First Name: ${profile.first_name || 'N/A'}`);
        console.log(`      Last Name: ${profile.last_name || 'N/A'}`);
        
        // Verify relationship
        if (profile.user_id === user.id) {
          console.log(`      ✅ Relationship is CORRECT: user_profiles.user_id (${profile.user_id}) = users.id (${user.id})\n`);
        } else {
          console.log(`      ❌ MISMATCH: user_profiles.user_id (${profile.user_id}) ≠ users.id (${user.id})\n`);
        }
      } else {
        console.log(`   ❌ NO PROFILE FOUND for user ID ${user.id}\n`);
      }
    }
    
    // Check for orphaned profiles (profiles without users)
    console.log('\n🔍 Checking for orphaned profiles (profiles without users)...\n');
    const allProfiles = db.prepare('SELECT id, user_id, display_name FROM user_profiles ORDER BY id').all();
    
    for (const profile of allProfiles) {
      const user = db.prepare('SELECT id, email FROM users WHERE id = ?').get(profile.user_id);
      if (!user) {
        console.log(`   ❌ ORPHANED PROFILE: Profile ID ${profile.id} references user_id ${profile.user_id} which doesn't exist!`);
      }
    }
    
    console.log('\n✅ Database relationship check complete!');
    console.log('\n📝 Note:');
    console.log('   - user_profiles.id is the PRIMARY KEY of the profile row');
    console.log('   - user_profiles.user_id is the FOREIGN KEY that links to users.id');
    console.log('   - These are different values and that\'s correct!');
    console.log('   - Example: Profile row #3 can belong to User #10');
    
  } catch (error) {
    console.error('❌ Error:', error);
    process.exit(1);
  }
}

checkDatabaseRelationships();

