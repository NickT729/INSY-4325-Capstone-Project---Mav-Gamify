-- MavPal Database Schema for SQLite
-- This file is copied from root schema.sql for Spring Boot resource access

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uta_id TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    password_hash TEXT NOT NULL,
    xp INTEGER NOT NULL DEFAULT 0,
    level INTEGER NOT NULL DEFAULT 1,
    created_at TEXT DEFAULT (datetime('now')),
    last_login TEXT
);

CREATE TABLE IF NOT EXISTS sets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    subject TEXT,
    visibility TEXT NOT NULL DEFAULT 'private',
    type TEXT NOT NULL,
    created_by INTEGER NOT NULL,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS flashcards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    set_id INTEGER NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    hint TEXT,
    ord INTEGER DEFAULT 0,
    FOREIGN KEY (set_id) REFERENCES sets(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_questions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    set_id INTEGER NOT NULL,
    type TEXT NOT NULL,
    question_text TEXT NOT NULL,
    choices TEXT,
    correct_index INTEGER,
    hint TEXT,
    ord INTEGER DEFAULT 0,
    FOREIGN KEY (set_id) REFERENCES sets(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_attempts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    set_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    score REAL,
    xp_earned INTEGER,
    started_at TEXT,
    completed_at TEXT,
    duration_ms INTEGER,
    details TEXT,
    FOREIGN KEY (set_id) REFERENCES sets(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS xp_events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    event_type TEXT NOT NULL,
    xp_amount INTEGER NOT NULL,
    source_set INTEGER,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (source_set) REFERENCES sets(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS daily_tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    task_text TEXT NOT NULL,
    is_default INTEGER DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS daily_task_status (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    task_id INTEGER NOT NULL,
    date TEXT NOT NULL,
    completed INTEGER DEFAULT 0,
    completed_at TEXT,
    FOREIGN KEY (task_id) REFERENCES daily_tasks(id) ON DELETE CASCADE,
    UNIQUE (task_id, date)
);

CREATE INDEX IF NOT EXISTS idx_users_uta_id ON users(uta_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_sets_created_by ON sets(created_by);
CREATE INDEX IF NOT EXISTS idx_sets_visibility ON sets(visibility);
CREATE INDEX IF NOT EXISTS idx_flashcards_set_id ON flashcards(set_id);
CREATE INDEX IF NOT EXISTS idx_quiz_questions_set_id ON quiz_questions(set_id);
CREATE INDEX IF NOT EXISTS idx_quiz_attempts_user_id ON quiz_attempts(user_id);
CREATE INDEX IF NOT EXISTS idx_xp_events_user_id ON xp_events(user_id);
CREATE INDEX IF NOT EXISTS idx_daily_tasks_user_id ON daily_tasks(user_id);
CREATE INDEX IF NOT EXISTS idx_daily_task_status_task_date ON daily_task_status(task_id, date);
