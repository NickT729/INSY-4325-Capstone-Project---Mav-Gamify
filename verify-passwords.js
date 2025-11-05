// Script to verify all users have password hashes
// Run: node scripts/verify-passwords.js

import { initDatabase, getDatabase } from '../db.js';
import bcrypt from 'bcryptjs';

async function verifyPasswords() {
  try {
    await initDatabase();
    const db = getDatabase();

    console.log('🔍 Checking all users for password hashes...\n');

    const users = db.prepare('SELECT id, email, password_hash FROM users').all();

    if (users.length === 0) {
      console.log('✅ No users found in database');
      return;
    }

    console.log(`Found ${users.length} user(s):\n`);

    let issues = 0;
    users.forEach((user, index) => {
      console.log(`${index + 1}. ${user.email}`);
      console.log(`   User ID: ${user.id}`);
      
      if (!user.password_hash || user.password_hash.trim() === '') {
        console.log(`   ❌ CRITICAL: No password hash!`);
        issues++;
      } else if (user.password_hash.length < 10) {
        console.log(`   ⚠️  WARNING: Password hash seems too short (${user.password_hash.length} chars)`);
        issues++;
      } else {
        console.log(`   ✅ Password hash exists (${user.password_hash.length} chars)`);
      }
      console.log('');
    });

    if (issues > 0) {
      console.log(`\n⚠️  Found ${issues} user(s) with password issues!`);
      console.log('These users need to be fixed or deleted.');
      console.log('\nTo delete all users and start fresh:');
      console.log('  npm run db:delete-all-users');
      console.log('\nTo fix existing users (requires knowing their passwords),');
      console.log('you will need to manually update the password_hash field.');
    } else {
      console.log('\n✅ All users have valid password hashes!');
    }
  } catch (error) {
    console.error('Error:', error);
    process.exit(1);
  }
}

verifyPasswords();






