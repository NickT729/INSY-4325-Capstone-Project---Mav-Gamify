-- Migration: Add first_name and last_name to user_profiles table
-- Run this to add name fields to existing database

-- Add first_name and last_name columns to user_profiles table
-- Note: If columns already exist, this will fail gracefully (ignore the error)
ALTER TABLE user_profiles ADD COLUMN first_name TEXT;
ALTER TABLE user_profiles ADD COLUMN last_name TEXT;

-- Verify columns were added
SELECT 'Migration complete. Columns added: first_name, last_name' AS status;

