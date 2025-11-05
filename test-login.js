// Test script to verify login works
import { initDatabase, getDatabase, createDatabaseWrapper } from '../db.js';
import bcrypt from 'bcryptjs';

async function testLogin() {
  try {
    console.log('🔍 Testing login functionality...\n');
    
    const dbInstance = await initDatabase();
    const db = createDatabaseWrapper(dbInstance);
    
    // Get all users
    const users = db.prepare('SELECT id, email, password_hash FROM users').all();
    
    console.log(`Found ${users.length} user(s) in database:\n`);
    
    for (const user of users) {
      console.log(`User ID: ${user.id}`);
      console.log(`Email: ${user.email}`);
      console.log(`Has password_hash: ${!!user.password_hash}`);
      console.log(`Password hash type: ${typeof user.password_hash}`);
      console.log(`Password hash length: ${user.password_hash ? user.password_hash.length : 0}`);
      console.log(`Password hash preview: ${user.password_hash ? user.password_hash.substring(0, 30) + '...' : 'null'}`);
      console.log(`All keys in user object:`, Object.keys(user));
      console.log(`User object:`, user);
      console.log('---\n');
    }
    
    // Test password hash retrieval
    if (users.length > 0) {
      const testUser = users[0];
      console.log(`\n🔐 Testing password hash retrieval for: ${testUser.email}`);
      console.log(`Direct access: password_hash =`, testUser.password_hash);
      console.log(`CamelCase: passwordHash =`, testUser.passwordHash);
      console.log(`Bracket notation: ['password_hash'] =`, testUser['password_hash']);
    }
    
  } catch (error) {
    console.error('❌ Test error:', error);
    process.exit(1);
  }
}

testLogin();

