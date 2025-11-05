// Script to fix existing users - set them to level 1 with 0 XP
import initSqlJs from 'sql.js';
import { readFileSync, writeFileSync, existsSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const dbPath = join(__dirname, '../../database/mavpal.db');

async function fixUsers() {
  try {
    console.log('🔧 Fixing existing users...');
    
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

    // Update all users to level 1 with 0 XP
    db.run('UPDATE user_profiles SET xp = 0, level = 1 WHERE xp > 0 OR level > 1');
    
    // Get count of updated users
    const result = db.exec('SELECT COUNT(*) as count FROM user_profiles WHERE xp = 0 AND level = 1');
    const count = result[0]?.values[0]?.[0] || 0;
    
    // Save database
    const data = db.export();
    const bufferOut = Buffer.from(data);
    writeFileSync(dbPath, bufferOut);
    
    console.log(`✅ Fixed ${count} users - all set to Level 1 with 0 XP`);
    console.log('✅ Database updated!');
    
    db.close();
  } catch (error) {
    console.error('Error fixing users:', error);
    process.exit(1);
  }
}

fixUsers();








