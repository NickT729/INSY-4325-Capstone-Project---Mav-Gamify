# How to View the Database - User Accounts and XP Data

## Database Location

The SQLite database file is located at:
```
C:\Users\Torre\OneDrive\Desktop\coding\MavPal_V.4.3\mavpal.db
```

**Note:** The database file (`mavpal.db`) is automatically created when you start the backend server for the first time.

---

## Step 1: Install DB Browser for SQLite

If you don't have it installed:

1. **Download DB Browser for SQLite:**
   - Visit: https://sqlitebrowser.org/
   - Download the Windows installer
   - Install it

2. **Alternative:** You can also use any SQLite browser tool or VS Code extensions

---

## Step 2: Open the Database

1. **Start the backend server** (if not already running):
   ```powershell
   cd backend
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-22"
   .\gradlew.bat bootRun
   ```
   Wait for: `Started MavPalApplication`

2. **Open DB Browser for SQLite:**
   - Launch the application
   - Click "Open Database"
   - Navigate to: `C:\Users\Torre\OneDrive\Desktop\coding\MavPal_V.4.3\`
   - Select `mavpal.db`
   - Click "Open"

---

## Step 3: View User Accounts and XP Data

### View All Users

1. Click on the **"Browse Data"** tab
2. Select **"users"** from the Table dropdown
3. You'll see:
   - `id` - User ID
   - `uta_id` - UTA ID (10 digits)
   - `email` - User email
   - `first_name` - First name
   - `last_name` - Last name
   - `password_hash` - Hashed password (for security)
   - **`xp`** - **Current XP total** ⭐
   - **`level`** - **Current level** ⭐
   - `created_at` - Account creation date
   - `last_login` - Last login timestamp

### View XP Events (Audit Trail)

1. Select **"xp_events"** from the Table dropdown
2. You'll see all XP transactions:
   - `id` - Event ID
   - `user_id` - Which user earned XP
   - **`event_type`** - Type of event:
     - `"flashcard_review"` - Reviewed flashcards
     - `"quiz_complete"` - Completed a quiz
     - `"daily_bonus"` - Completed all daily tasks
   - **`xp_amount`** - XP earned in this event
   - `source_set` - Which set (if applicable)
   - `created_at` - When the XP was earned

### View Daily Task Completions

1. Select **"daily_task_status"** from the Table dropdown
2. Shows:
   - `task_id` - Which task
   - `date` - Date completed
   - `completed` - 1 if completed, 0 if not
   - `completed_at` - Timestamp

### View Quiz Attempts

1. Select **"quiz_attempts"** from the Table dropdown
2. Shows:
   - `user_id` - Who took the quiz
   - `set_id` - Which quiz set
   - `score` - Percentage score
   - **`xp_earned`** - XP earned from this quiz
   - `completed_at` - When completed

---

## Step 4: Run SQL Queries (Advanced)

Click on the **"Execute SQL"** tab to run custom queries:

### See All Users with Their XP
```sql
SELECT 
    id,
    uta_id,
    email,
    first_name,
    last_name,
    xp,
    level,
    created_at
FROM users
ORDER BY xp DESC;
```

### See XP Events for a Specific User
```sql
SELECT 
    e.id,
    e.event_type,
    e.xp_amount,
    e.created_at,
    s.title AS set_title
FROM xp_events e
LEFT JOIN sets s ON e.source_set = s.id
WHERE e.user_id = 1  -- Replace 1 with your user ID
ORDER BY e.created_at DESC;
```

### See Total XP Earned by Event Type
```sql
SELECT 
    event_type,
    COUNT(*) AS event_count,
    SUM(xp_amount) AS total_xp
FROM xp_events
WHERE user_id = 1  -- Replace 1 with your user ID
GROUP BY event_type;
```

### See Daily Task Completions
```sql
SELECT 
    dts.date,
    dt.task_text,
    dts.completed,
    dts.completed_at
FROM daily_task_status dts
JOIN daily_tasks dt ON dts.task_id = dt.id
WHERE dt.user_id = 1  -- Replace 1 with your user ID
ORDER BY dts.date DESC, dt.id;
```

### See All Quiz Attempts with Scores
```sql
SELECT 
    u.email,
    s.title AS quiz_title,
    qa.score,
    qa.xp_earned,
    qa.completed_at
FROM quiz_attempts qa
JOIN users u ON qa.user_id = u.id
JOIN sets s ON qa.set_id = s.id
ORDER BY qa.completed_at DESC;
```

### See XP History Timeline for a User
```sql
SELECT 
    created_at,
    event_type,
    xp_amount,
    (SELECT SUM(xp_amount) 
     FROM xp_events 
     WHERE user_id = 1 
     AND created_at <= e.created_at) AS running_total
FROM xp_events e
WHERE user_id = 1  -- Replace 1 with your user ID
ORDER BY created_at ASC;
```

---

## Quick Reference: Key Tables

| Table | What It Contains |
|-------|-----------------|
| **`users`** | User accounts, current XP, level |
| **`xp_events`** | Complete audit trail of all XP earned |
| **`daily_task_status`** | Daily checklist completions |
| **`quiz_attempts`** | Quiz scores and XP earned |
| **`sets`** | Flashcard/quiz sets created |
| **`flashcards`** | Individual flashcard content |
| **`quiz_questions`** | Quiz questions and answers |

---

## Tips

1. **Refresh Data:** After completing tasks in the app, click the "Refresh" button in DB Browser to see updates
2. **Filter Data:** Use the filter box at the bottom to search for specific users or dates
3. **Export Data:** Right-click on any table → "Export" to save as CSV
4. **Backup Database:** File → Export → Database to SQL file (creates a backup)

---

## Example: Tracking a User's Progress

1. **Find your user ID:**
   ```sql
   SELECT id, email, xp, level FROM users WHERE email = 'your@mavs.uta.edu';
   ```

2. **See all XP events:**
   ```sql
   SELECT * FROM xp_events WHERE user_id = YOUR_USER_ID ORDER BY created_at DESC;
   ```

3. **See current stats:**
   ```sql
   SELECT xp, level FROM users WHERE id = YOUR_USER_ID;
   ```

---

## Troubleshooting

### Database file doesn't exist
- Make sure the backend server has been started at least once
- Check the project root directory: `C:\Users\Torre\OneDrive\Desktop\coding\MavPal_V.4.3\`

### Can't see updates
- Make sure the backend server is running
- Click "Refresh" in DB Browser
- Close and reopen the database file

### Database is locked
- Close DB Browser
- Stop the backend server
- Reopen the database

---

## Visual Guide

When you open the database, you'll see:

```
DB Browser for SQLite
├── Database Structure (left panel)
│   ├── users
│   ├── sets
│   ├── flashcards
│   ├── quiz_questions
│   ├── quiz_attempts
│   ├── xp_events          ← Check here for XP history!
│   ├── daily_tasks
│   └── daily_task_status  ← Check here for task completions!
│
└── Browse Data (tab)
    └── Select table from dropdown
        └── View all rows and columns
```

**Most Important Tables for XP Tracking:**
- **`users`** - Current XP and level
- **`xp_events`** - Complete history of all XP earned

