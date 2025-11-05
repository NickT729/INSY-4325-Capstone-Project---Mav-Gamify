// Script to delete all users and their associated data
import initSqlJs from 'sql.js';
import { readFileSync, writeFileSync, existsSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const dbPath = join(__dirname, '../../database/mavpal.db');

async function deleteAllUsers() {
  try {
    console.log('🗑️  Deleting all users and their data...');
    
    if (!existsSync(dbPath)) {
      console.error('❌ Database file not found:', dbPath);
      process.exit(1);
    }

    // Initialize sql.js
    const sqlJsPath = join(__dirname, '../node_modules/sql.js/dist');
    const SQL = await initSqlJs({
      locateFile: (file) => {
        return join(sqlJsPath, file);
      }
    });

    // Load database
    const buffer = readFileSync(dbPath);
    const db = new SQL.Database(buffer);

    // Get count of users before deletion
    const userCount = db.exec('SELECT COUNT(*) as count FROM users');
    const count = userCount[0]?.values[0]?.[0] || 0;
    
    console.log(`Found ${count} users to delete...`);

    if (count === 0) {
      console.log('✅ No users found. Database is already empty.');
      db.close();
      return;
    }

    // Delete in order (respecting foreign key constraints)
    // Note: SQLite doesn't enforce foreign keys by default, but we'll delete in safe order
    
    console.log('Deleting notifications...');
    db.run('DELETE FROM notifications');
    
    console.log('Deleting challenge progress...');
    db.run('DELETE FROM challenge_progress');
    
    console.log('Deleting flashcard completions...');
    db.run('DELETE FROM flashcard_completions');
    
    console.log('Deleting quiz completions...');
    db.run('DELETE FROM quiz_completions');
    
    console.log('Deleting user profiles...');
    db.run('DELETE FROM user_profiles');
    
    console.log('Deleting users...');
    db.run('DELETE FROM users');

    // Verify deletion
    const remainingUsers = db.exec('SELECT COUNT(*) as count FROM users');
    const remaining = remainingUsers[0]?.values[0]?.[0] || 0;
    
    // Save database
    const data = db.export();
    const bufferOut = Buffer.from(data);
    writeFileSync(dbPath, bufferOut);
    
    console.log(`✅ Deleted ${count} users and all associated data`);
    console.log(`✅ Remaining users: ${remaining}`);
    console.log('✅ Database cleaned!');
    
    db.close();
  } catch (error) {
    console.error('❌ Error deleting users:', error);
    process.exit(1);
  }
}

deleteAllUsers();








