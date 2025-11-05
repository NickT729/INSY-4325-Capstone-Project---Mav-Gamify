import initSqlJs from 'sql.js';
import { readFileSync, writeFileSync, existsSync } from 'fs';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const dbPath = join(__dirname, '../../database/mavpal.db');
const schemaPath = join(__dirname, '../../database/schema.sqlite.sql');

async function setupDatabase() {
  try {
    console.log('📊 Setting up MavPal database...');
    console.log(`Database path: ${dbPath}`);
    console.log(`Schema path: ${schemaPath}`);

    // Initialize sql.js - use local WASM file from node_modules
    // __dirname is server/scripts, so go up one level to server/node_modules
    const sqlJsPath = join(__dirname, '../node_modules/sql.js/dist');
    const SQL = await initSqlJs({
      locateFile: (file) => {
        return join(sqlJsPath, file);
      }
    });

    // Create new database
    const db = new SQL.Database();

    // Read and execute schema
    const schema = readFileSync(schemaPath, 'utf8');
    console.log('Executing schema...');
    db.run(schema);

    // Save database to file
    const data = db.export();
    const buffer = Buffer.from(data);
    writeFileSync(dbPath, buffer);

    console.log('✅ Database setup complete!');
    console.log(`Database created at: ${dbPath}`);
    
    db.close();
  } catch (error) {
    console.error('Error setting up database:', error);
    process.exit(1);
  }
}

setupDatabase();

