// Database wrapper for sql.js
import initSqlJs from 'sql.js';
import { readFileSync, writeFileSync, existsSync } from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { dirname } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

let SQL;
let db = null;

export async function initDatabase() {
  try {
    // Initialize sql.js - use local WASM file from node_modules
    // __dirname is server/, so node_modules is in server/node_modules
    const sqlJsPath = path.join(__dirname, 'node_modules/sql.js/dist');
    SQL = await initSqlJs({
      locateFile: (file) => {
        return path.join(sqlJsPath, file);
      }
    });

    const dbPath = process.env.DB_PATH || path.join(__dirname, '../database/mavpal.db');
    const schemaPath = path.join(__dirname, '../database/schema.sqlite.sql');

    // Check if database exists
    if (existsSync(dbPath)) {
      // Load existing database
      const buffer = readFileSync(dbPath);
      db = new SQL.Database(buffer);
      console.log('✅ Loaded existing database');
      
      // Run migrations on existing database (directly on db instance, not wrapper)
      const { migrateDatabase } = await import('./utils/migrateDatabase.js');
      const migrationsRun = migrateDatabase(db);
      
      // Save after migration if changes were made
      if (migrationsRun > 0) {
        const data = db.export();
        const bufferAfterMigration = Buffer.from(data);
        writeFileSync(dbPath, bufferAfterMigration);
        console.log('💾 Database saved after migration');
      }
    } else {
      // Create new database
      console.log('📊 Creating new database...');
      db = new SQL.Database();
      
      // Read and execute schema
      const schema = readFileSync(schemaPath, 'utf8');
      // Split by semicolons and execute each statement
      const statements = schema.split(';').filter(s => s.trim().length > 0);
      for (const statement of statements) {
        if (statement.trim()) {
          try {
            db.run(statement);
          } catch (e) {
            // Ignore errors for statements like CREATE INDEX if table doesn't exist yet
            if (!e.message.includes('no such table')) {
              console.warn('Schema statement warning:', e.message);
            }
          }
        }
      }
      
      // Save database to file
      const data = db.export();
      const buffer = Buffer.from(data);
      writeFileSync(dbPath, buffer);
      console.log('✅ Database created successfully');
    }

    return db;
  } catch (error) {
    console.error('Database initialization error:', error);
    throw error;
  }
}

export function getDatabase() {
  if (!db) {
    throw new Error('Database not initialized. Call initDatabase() first.');
  }
  return db;
}

export function saveDatabase() {
  if (!db) return;
  
  try {
    const dbPath = process.env.DB_PATH || path.join(__dirname, '../database/mavpal.db');
    const data = db.export();
    const buffer = Buffer.from(data);
    writeFileSync(dbPath, buffer);
  } catch (error) {
    console.error('Error saving database:', error);
  }
}

// Helper functions to match better-sqlite3 API
export function createDatabaseWrapper(dbInstance) {
  let lastInsertRowid = 0;
  
  return {
    prepare: (sql) => {
      // sql.js uses :param, @param, or $param syntax
      // Convert ? placeholders to $1, $2, etc.
      let paramCount = 0;
      const sqlWithParams = sql.replace(/\?/g, () => {
        paramCount++;
        return `$${paramCount}`;
      });
      
      const stmt = dbInstance.prepare(sqlWithParams);
      
      return {
        get: (...params) => {
          if (params.length > 0) {
            stmt.bind(params);
          }
          if (!stmt.step()) {
            stmt.reset();
            stmt.free();
            return null;
          }
          
          // Get column names and count
          const columnNames = stmt.getColumnNames();
          const columnCount = columnNames.length;
          
          // Build result by accessing values directly by index
          // After step(), we can access values using stmt.get(columnIndex)
          const result = {};
          for (let i = 0; i < columnCount; i++) {
            try {
              // Access value by column index
              const value = stmt.get(i);
              result[columnNames[i]] = value;
            } catch (e) {
              // If direct access fails, try getAsObject as fallback
              try {
                const objResult = stmt.getAsObject({});
                result[columnNames[i]] = objResult[columnNames[i]] || null;
              } catch (e2) {
                result[columnNames[i]] = null;
              }
            }
          }
          
          stmt.reset();
          stmt.free();
          return result;
        },
        all: (...params) => {
          if (params.length > 0) {
            stmt.bind(params);
          }
          const results = [];
          while (stmt.step()) {
            // Use getAsObject directly for each row
            results.push(stmt.getAsObject({}));
          }
          stmt.reset();
          stmt.free();
          return results;
        },
        run: (...params) => {
          if (params.length > 0) {
            stmt.bind(params);
          }
          stmt.step();
          // Get last insert rowid
          const rowidResult = dbInstance.exec('SELECT last_insert_rowid() as id');
          if (rowidResult && rowidResult[0] && rowidResult[0].values) {
            lastInsertRowid = rowidResult[0].values[0]?.[0] || 0;
          }
          stmt.reset();
          stmt.free();
          return {
            lastInsertRowid: lastInsertRowid
          };
        }
      };
    },
    exec: (sql) => {
      dbInstance.run(sql);
    }
  };
}

