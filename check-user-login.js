// Script to check if a user can log in
// Usage: node scripts/check-user-login.js <email>
// Example: node scripts/check-user-login.js test@mavs.uta.edu

import { initDatabase, getDatabase } from '../db.js';
import bcrypt from 'bcryptjs';

async function checkUserLogin(email) {
  try {
    await initDatabase();
    const db = getDatabase();

    console.log(`🔍 Checking login capability for: ${email}\n`);

    // Check if user exists
    const user = db.prepare('SELECT * FROM users WHERE email = ?').get(email);

    if (!user) {
      console.log('❌ User not found in database');
      console.log('💡 User needs to sign up first');
      return;
    }

    console.log('✅ User found:');
    console.log(`   ID: ${user.id}`);
    console.log(`   Email: ${user.email}`);
    console.log(`   Created: ${user.created_at}`);
    console.log(`   Last Login: ${user.last_login || 'Never'}`);
    
    // Check password hash
    if (!user.password_hash || user.password_hash.trim() === '') {
      console.log('\n❌ CRITICAL: User has no password hash!');
      console.log('💡 This user cannot log in. They need to reset their password or be deleted.');
      return;
    }

    console.log(`\n✅ Password hash exists (${user.password_hash.length} chars)`);

    // Check user profile
    const profile = db.prepare('SELECT * FROM user_profiles WHERE user_id = ?').get(user.id);

    if (!profile) {
      console.log('\n❌ CRITICAL: User profile not found!');
      console.log('💡 Profile needs to be created for this user');
      return;
    }

    console.log('\n✅ User profile found:');
    console.log(`   Display Name: ${profile.display_name}`);
    console.log(`   Nickname: ${profile.nickname || 'Not set'}`);
    console.log(`   XP: ${profile.xp}`);
    console.log(`   Level: ${profile.level}`);
    console.log(`   Major: ${profile.major || 'Not set'}`);
    console.log(`   College: ${profile.college || 'Not set'}`);

    console.log('\n✅ User is ready to log in!');
    console.log('💡 Make sure you use the correct password when logging in.');

  } catch (error) {
    console.error('Error:', error);
    process.exit(1);
  }
}

const email = process.argv[2];

if (!email) {
  console.log('Usage: node scripts/check-user-login.js <email>');
  console.log('Example: node scripts/check-user-login.js test@mavs.uta.edu');
  process.exit(1);
}

if (!email.endsWith('@mavs.uta.edu')) {
  console.error('❌ Email must end with @mavs.uta.edu');
  process.exit(1);
}

checkUserLogin(email);






