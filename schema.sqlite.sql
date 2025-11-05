-- MavPal Database Schema for SQLite
-- Alternative SQL script for SQLite (simpler, no SERIAL, uses INTEGER PRIMARY KEY)

-- Drop existing tables if they exist
DROP TABLE IF EXISTS challenge_progress;
DROP TABLE IF EXISTS challenges;
DROP TABLE IF EXISTS flashcard_completions;
DROP TABLE IF EXISTS flashcard_cards;
DROP TABLE IF EXISTS flashcard_sets;
DROP TABLE IF EXISTS quiz_completions;
DROP TABLE IF EXISTS quiz_choices;
DROP TABLE IF EXISTS quiz_questions;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS users;

-- Users Table
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    CHECK (email LIKE '%@mavs.uta.edu')
);

-- User Profiles Table
CREATE TABLE user_profiles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name TEXT,
    last_name TEXT,
    display_name TEXT NOT NULL,
    nickname TEXT,
    major TEXT,
    college TEXT,
    class_year TEXT,
    avatar_url TEXT,
    xp INTEGER DEFAULT 0 NOT NULL,
    level INTEGER DEFAULT 1 NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Notifications Table
CREATE TABLE notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type TEXT NOT NULL CHECK (type IN ('Quiz', 'Flashcards', 'Challenge', 'Ranking', 'System')),
    text TEXT NOT NULL,
    read INTEGER DEFAULT 0 NOT NULL, -- SQLite uses INTEGER for booleans (0=false, 1=true)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_notifications ON notifications(user_id);
CREATE INDEX idx_unread_notifications ON notifications(user_id, read);

-- Quizzes Table
CREATE TABLE quizzes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    created_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_public INTEGER DEFAULT 1 -- 1=true, 0=false
);

CREATE INDEX idx_quiz_creator ON quizzes(created_by);

-- Quiz Questions Table
CREATE TABLE quiz_questions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    quiz_id INTEGER NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    correct_answer_index INTEGER NOT NULL CHECK (correct_answer_index >= 0 AND correct_answer_index <= 3),
    question_order INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(quiz_id, question_order)
);

CREATE INDEX idx_quiz_questions ON quiz_questions(quiz_id);

-- Quiz Choices Table
CREATE TABLE quiz_choices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    question_id INTEGER NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    choice_text TEXT NOT NULL,
    choice_index INTEGER NOT NULL CHECK (choice_index >= 0 AND choice_index <= 3),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(question_id, choice_index)
);

CREATE INDEX idx_question_choices ON quiz_choices(question_id);

-- Quiz Completions Table
CREATE TABLE quiz_completions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    quiz_id INTEGER NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score INTEGER NOT NULL,
    total_questions INTEGER NOT NULL,
    passed INTEGER NOT NULL, -- 1=true, 0=false
    xp_earned INTEGER DEFAULT 0,
    completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(quiz_id, user_id)
);

CREATE INDEX idx_user_quiz_completions ON quiz_completions(user_id);
CREATE INDEX idx_quiz_completions ON quiz_completions(quiz_id);

-- Flashcard Sets Table
CREATE TABLE flashcard_sets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    created_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_public INTEGER DEFAULT 1
);

CREATE INDEX idx_flashcard_creator ON flashcard_sets(created_by);

-- Flashcard Cards Table
CREATE TABLE flashcard_cards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    flashcard_set_id INTEGER NOT NULL REFERENCES flashcard_sets(id) ON DELETE CASCADE,
    front_text TEXT NOT NULL,
    back_text TEXT NOT NULL,
    card_order INTEGER NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(flashcard_set_id, card_order)
);

CREATE INDEX idx_flashcard_cards ON flashcard_cards(flashcard_set_id);

-- Flashcard Completions Table
CREATE TABLE flashcard_completions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    flashcard_set_id INTEGER NOT NULL REFERENCES flashcard_sets(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    xp_earned INTEGER DEFAULT 0,
    completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(flashcard_set_id, user_id)
);

CREATE INDEX idx_user_flashcard_completions ON flashcard_completions(user_id);
CREATE INDEX idx_flashcard_completions ON flashcard_completions(flashcard_set_id);

-- Challenges Table
CREATE TABLE challenges (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL CHECK (category IN ('Study', 'Practice', 'Quiz', 'Project')),
    xp_reward INTEGER NOT NULL DEFAULT 100,
    max_progress INTEGER NOT NULL DEFAULT 10,
    created_by INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    end_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_challenge_creator ON challenges(created_by);
CREATE INDEX idx_active_challenges ON challenges(end_date);

-- Challenge Progress Table
CREATE TABLE challenge_progress (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    challenge_id INTEGER NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    progress INTEGER DEFAULT 0 NOT NULL,
    completed INTEGER DEFAULT 0 NOT NULL, -- 1=true, 0=false
    xp_earned INTEGER DEFAULT 0,
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME,
    UNIQUE(challenge_id, user_id)
);

CREATE INDEX idx_user_challenge_progress ON challenge_progress(user_id);
CREATE INDEX idx_challenge_progress ON challenge_progress(challenge_id);

-- Additional indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, read, created_at DESC);
CREATE INDEX idx_quizzes_public ON quizzes(is_public, created_at DESC);
CREATE INDEX idx_flashcard_sets_public ON flashcard_sets(is_public, created_at DESC);
CREATE INDEX idx_challenges_active ON challenges(end_date, created_at DESC);

-- Sample data (for testing)
INSERT INTO users (email, password_hash) VALUES
    ('test@mavs.uta.edu', 'demo_password_hash'),
    ('sam@mavs.uta.edu', 'demo_password_hash'),
    ('alex@mavs.uta.edu', 'demo_password_hash'),
    ('manmeet@mavs.uta.edu', 'demo_password_hash');

-- Sample users (optional - remove if you want to start fresh)
-- INSERT INTO user_profiles (user_id, display_name, nickname, major, college, class_year, xp, level) VALUES
--     (1, 'test', 'test', 'IS', 'Engineering', '2025', 420, 3),
--     (2, 'Sam', 'Sam', 'IS', 'Engineering', '2025', 600, 3),
--     (3, 'Alex', 'Alex', 'Math', 'Science', '2025', 480, 3),
--     (4, 'Manmeet', 'Manmeet', 'BA', 'Business', '2025', 720, 4);

