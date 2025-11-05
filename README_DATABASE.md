# MavPal Database Migration Guide

## Overview

This project has been migrated from localStorage-only to a full database-backed architecture with a REST API backend.

## Architecture

- **Frontend**: React/TypeScript app (existing)
- **Backend**: Node.js/Express API server (`server/`)
- **Database**: SQLite (development) or PostgreSQL (production)

## Setup Instructions

### 1. Install Backend Dependencies

```bash
cd server
npm install
```

### 2. Set Up Database

**Option A: SQLite (Recommended for development)**

```bash
# From server directory
npm run db:setup

# Or manually:
cd ../database
sqlite3 mavpal.db < schema.sqlite.sql
```

**Option B: PostgreSQL (For production)**

```bash
createdb mavpal
psql -U postgres -d mavpal < schema.sql
```

### 3. Configure Environment

Create `server/.env` file:

```env
PORT=3001
DB_PATH=../database/mavpal.db
NODE_ENV=development
```

### 4. Start Backend Server

```bash
cd server
npm run dev
```

Server will run on `http://localhost:3001`

### 5. Configure Frontend

Create `.env` file in root directory:

```env
VITE_API_URL=http://localhost:3001/api
VITE_USE_API=true
```

### 6. Start Frontend

```bash
# From root directory
npm run dev
```

## Features

### Database Features
- ✅ User authentication and profiles
- ✅ Notifications system
- ✅ Quizzes and quiz completions
- ✅ Flashcard sets and completions
- ✅ Challenges and progress tracking
- ✅ Leaderboards
- ✅ XP and leveling system

### API Endpoints

**Authentication**
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

**Users**
- `GET /api/users/profile/:userId` - Get user profile
- `PUT /api/users/profile/:userId` - Update user profile

**Notifications**
- `GET /api/notifications/:userId` - Get all notifications
- `POST /api/notifications` - Add notification
- `PUT /api/notifications/:userId/read-all` - Mark all as read
- `DELETE /api/notifications/:userId` - Clear all notifications

**Quizzes**
- `GET /api/quizzes` - Get all quizzes
- `GET /api/quizzes/:quizId` - Get quiz by ID
- `POST /api/quizzes` - Create quiz
- `POST /api/quizzes/:quizId/complete` - Record completion

**Flashcards**
- `GET /api/flashcards` - Get all flashcard sets
- `GET /api/flashcards/:setId` - Get flashcard set by ID
- `POST /api/flashcards` - Create flashcard set
- `POST /api/flashcards/:setId/complete` - Record completion

**Challenges**
- `GET /api/challenges` - Get all challenges
- `GET /api/challenges/user/:userId` - Get user's challenges
- `POST /api/challenges` - Create challenge
- `POST /api/challenges/:challengeId/join` - Join challenge

**Leaderboard**
- `GET /api/leaderboard` - Get leaderboard (with filters)

## Fallback Behavior

The frontend is designed to gracefully fall back to localStorage if:
- The API server is not running
- API requests fail
- `VITE_USE_API=false` is set

This ensures the app continues to work even without the backend.

## Reverting to localStorage-only

If you need to revert to the localStorage-only version:

1. Set `VITE_USE_API=false` in `.env`
2. Or use the backup folder: `uta-gamify-backup-localStorage`

## Database Schema

See `database/schema.sql` (PostgreSQL) or `database/schema.sqlite.sql` (SQLite) for full schema details.

## Troubleshooting

**Database not found:**
```bash
cd server
npm run db:setup
```

**API connection errors:**
- Check that backend server is running (`npm run dev` in server directory)
- Verify `VITE_API_URL` in frontend `.env` matches server port
- Check CORS settings if accessing from different origin

**Port conflicts:**
- Change `PORT` in `server/.env` if 3001 is taken
- Update `VITE_API_URL` in frontend `.env` to match

## Next Steps

1. Add authentication tokens (JWT)
2. Add password hashing for real authentication
3. Add data validation and sanitization
4. Add rate limiting
5. Add database migrations system
6. Deploy to production

