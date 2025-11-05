-- MavPal Database Schema
-- SQL Script for PostgreSQL (can be adapted for MySQL/SQLite)
-- Created for University of Texas at Arlington Gamification Platform

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS challenge_participants CASCADE;
DROP TABLE IF EXISTS challenge_progress CASCADE;
DROP TABLE IF EXISTS challenges CASCADE;
DROP TABLE IF EXISTS flashcard_completions CASCADE;
DROP TABLE IF EXISTS flashcard_cards CASCADE;
DROP TABLE IF EXISTS flashcard_sets CASCADE;
DROP TABLE IF EXISTS quiz_completions CASCADE;
DROP TABLE IF EXISTS quiz_questions CASCADE;
DROP TABLE IF EXISTS quiz_choices CASCADE;
DROP TABLE IF EXISTS quizzes CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS user_profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- For future authentication
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    CONSTRAINT email_format CHECK (email LIKE '%@mavs.uta.edu')
);

-- User Profiles Table (extended user information)
CREATE TABLE user_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    display_name VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    major VARCHAR(100),
    college VARCHAR(100),
    class_year VARCHAR(10),
    avatar_url TEXT,
  xp INTEGER DEFAULT 0 NOT NULL,
  level INTEGER DEFAULT 1 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notifications Table
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('Quiz', 'Flashcards', 'Challenge', 'Ranking', 'System')),
    text TEXT NOT NULL,
    read BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_notifications (user_id),
    INDEX idx_unread_notifications (user_id, read)
);

-- Quizzes Table
CREATE TABLE quizzes (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    created_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_public BOOLEAN DEFAULT TRUE,
    INDEX idx_quiz_creator (created_by)
);

-- Quiz Questions Table
CREATE TABLE quiz_questions (
    id SERIAL PRIMARY KEY,
    quiz_id INTEGER NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    correct_answer_index INTEGER NOT NULL CHECK (correct_answer_index >= 0 AND correct_answer_index <= 3),
    question_order INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_quiz_questions (quiz_id),
    UNIQUE(quiz_id, question_order)
);

-- Quiz Choices Table (for multiple choice answers)
CREATE TABLE quiz_choices (
    id SERIAL PRIMARY KEY,
    question_id INTEGER NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    choice_text TEXT NOT NULL,
    choice_index INTEGER NOT NULL CHECK (choice_index >= 0 AND choice_index <= 3),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_question_choices (question_id),
    UNIQUE(question_id, choice_index)
);

-- Quiz Completions Table (track which users completed which quizzes)
CREATE TABLE quiz_completions (
    id SERIAL PRIMARY KEY,
    quiz_id INTEGER NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score INTEGER NOT NULL,
    total_questions INTEGER NOT NULL,
    passed BOOLEAN NOT NULL,
    xp_earned INTEGER DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_quiz_completions (user_id),
    INDEX idx_quiz_completions (quiz_id),
    UNIQUE(quiz_id, user_id) -- One completion per user per quiz
);

-- Flashcard Sets Table
CREATE TABLE flashcard_sets (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    created_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_public BOOLEAN DEFAULT TRUE,
    INDEX idx_flashcard_creator (created_by)
);

-- Flashcard Cards Table
CREATE TABLE flashcard_cards (
    id SERIAL PRIMARY KEY,
    flashcard_set_id INTEGER NOT NULL REFERENCES flashcard_sets(id) ON DELETE CASCADE,
    front_text TEXT NOT NULL,
    back_text TEXT NOT NULL,
    card_order INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_flashcard_cards (flashcard_set_id),
    UNIQUE(flashcard_set_id, card_order)
);

-- Flashcard Completions Table
CREATE TABLE flashcard_completions (
    id SERIAL PRIMARY KEY,
    flashcard_set_id INTEGER NOT NULL REFERENCES flashcard_sets(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    xp_earned INTEGER DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_flashcard_completions (user_id),
    INDEX idx_flashcard_completions (flashcard_set_id),
    UNIQUE(flashcard_set_id, user_id) -- One completion per user per set
);

-- Challenges Table
CREATE TABLE challenges (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL CHECK (category IN ('Study', 'Practice', 'Quiz', 'Project')),
    xp_reward INTEGER NOT NULL DEFAULT 100,
    max_progress INTEGER NOT NULL DEFAULT 10,
    created_by INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_challenge_creator (created_by),
    INDEX idx_active_challenges (end_date)
);

-- Challenge Progress Table (track individual user progress)
CREATE TABLE challenge_progress (
    id SERIAL PRIMARY KEY,
    challenge_id INTEGER NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    progress INTEGER DEFAULT 0 NOT NULL,
    completed BOOLEAN DEFAULT FALSE NOT NULL,
    xp_earned INTEGER DEFAULT 0,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    INDEX idx_user_challenge_progress (user_id),
    INDEX idx_challenge_progress (challenge_id),
    UNIQUE(challenge_id, user_id) -- One progress record per user per challenge
);

-- Challenge Participants Count (denormalized for quick lookup)
-- This is maintained via triggers or application logic
-- Alternatively, we can count from challenge_progress table

-- Create indexes for better query performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, read, created_at DESC);
CREATE INDEX idx_quizzes_public ON quizzes(is_public, created_at DESC);
CREATE INDEX idx_flashcard_sets_public ON flashcard_sets(is_public, created_at DESC);
CREATE INDEX idx_challenges_active ON challenges(end_date, created_at DESC);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_profiles_updated_at BEFORE UPDATE ON user_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_quizzes_updated_at BEFORE UPDATE ON quizzes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_flashcard_sets_updated_at BEFORE UPDATE ON flashcard_sets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_challenges_updated_at BEFORE UPDATE ON challenges
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Function to calculate user level based on XP
CREATE OR REPLACE FUNCTION calculate_level(xp INTEGER)
RETURNS INTEGER AS $$
BEGIN
    -- Level calculation: every 1000 XP = 1 level, starting at level 1
    RETURN GREATEST(1, FLOOR(xp / 1000) + 1);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- View for leaderboard data
CREATE OR REPLACE VIEW leaderboard_view AS
SELECT 
    u.id,
    u.email,
    up.display_name,
    up.nickname,
    up.major,
    up.college,
    up.class_year,
    up.xp,
    up.level,
    RANK() OVER (ORDER BY up.xp DESC) as overall_rank,
    RANK() OVER (PARTITION BY up.college ORDER BY up.xp DESC) as college_rank,
    RANK() OVER (PARTITION BY up.major ORDER BY up.xp DESC) as major_rank,
    RANK() OVER (PARTITION BY up.class_year ORDER BY up.xp DESC) as class_rank
FROM users u
JOIN user_profiles up ON u.id = up.user_id
WHERE up.xp > 0;

-- Insert sample data (optional - for testing)
-- Note: In production, you'd want to use proper password hashing
INSERT INTO users (email, password_hash) VALUES
    ('test@mavs.uta.edu', 'demo_password_hash'),
    ('sam@mavs.uta.edu', 'demo_password_hash'),
    ('alex@mavs.uta.edu', 'demo_password_hash'),
    ('manmeet@mavs.uta.edu', 'demo_password_hash');

INSERT INTO user_profiles (user_id, display_name, nickname, major, college, class_year, xp, level) VALUES
    ((SELECT id FROM users WHERE email = 'test@mavs.uta.edu'), 'test', 'test', 'IS', 'Engineering', '2025', 420, 3),
    ((SELECT id FROM users WHERE email = 'sam@mavs.uta.edu'), 'Sam', 'Sam', 'IS', 'Engineering', '2025', 600, 3),
    ((SELECT id FROM users WHERE email = 'alex@mavs.uta.edu'), 'Alex', 'Alex', 'Math', 'Science', '2025', 480, 3),
    ((SELECT id FROM users WHERE email = 'manmeet@mavs.uta.edu'), 'Manmeet', 'Manmeet', 'BA', 'Business', '2025', 720, 4);

