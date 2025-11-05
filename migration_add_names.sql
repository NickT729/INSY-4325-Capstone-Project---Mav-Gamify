-- Migration: Add first_name and last_name to user_profiles table
-- Run this to add name fields to existing database

-- Add first_name and last_name columns to user_profiles table
ALTER TABLE user_profiles ADD COLUMN first_name TEXT;
ALTER TABLE user_profiles ADD COLUMN last_name TEXT;

