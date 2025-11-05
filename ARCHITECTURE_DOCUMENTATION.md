# MavPal - Architecture Documentation
## Expert Lecture on Software Architecture & Component Design

---

## 📚 **INTRODUCTION**

This is a **JavaScript/React** application (using TypeScript for type safety), built as a gamified learning platform for University of Texas at Arlington students. The TypeScript files (`.tsx`/`.ts`) compile to JavaScript - they ARE JavaScript code with type annotations.

**Key Point**: TypeScript compiles to JavaScript. The `.tsx` and `.ts` files are JavaScript modules that get compiled to `.js` files during the build process (see `package.json` line 8: `"build": "tsc -b && vite build"`).

In this JavaScript/React application, we have:
- **JavaScript Modules/Components** (equivalent to classes in OOP)
- **JavaScript Objects & Interfaces** (data structures)
- **Context Providers** (JavaScript functions that manage state)
- **Custom Hooks** (JavaScript utility functions)

---

## 🏗️ **ARCHITECTURAL OVERVIEW**

The application follows a **Component-Based Architecture** with:
- **Presentation Layer**: React components (pages, layouts)
- **State Management Layer**: React Context API
- **Routing Layer**: React Router
- **Data Persistence**: LocalStorage (browser storage)

---

## 📋 **COMPONENT BREAKDOWN**

### **1. Entry Point: `main.tsx`**
**Location**: `src/main.tsx` (Lines 1-13)

**Purpose**: Application bootstrap - the entry point that initializes React and renders the root component.

**Key Responsibilities**:
- Creates the React root using `createRoot()`
- Wraps the app in `StrictMode` for development checks
- Provides `BrowserRouter` for routing
- Renders the `App` component

**Code Reference**:
```typescript
Line 7-13: Root initialization and rendering
```

---

### **2. Root Component: `App.tsx`**
**Location**: `src/App.tsx` (Lines 1-42)

**Purpose**: Main application orchestrator - defines routing structure and provides context providers.

**Key Responsibilities**:
- Wraps application with `AuthProvider` (authentication context)
- Wraps application with `NotificationsProvider` (notifications context)
- Defines all application routes using React Router
- Implements `RequireAuth` guard component (Lines 15-19) to protect authenticated routes
- Routes definitions:
  - `/login` → Login page (public)
  - `/` → Dashboard (protected)
  - `/study` → Study page (protected)
  - `/challenges` → Challenges page (protected)
  - `/leaderboards` → Leaderboards page (protected)
  - `/profile` → Profile page (protected)
  - `/notifications` → Notifications page (protected)

**Dependencies**:
- `AuthProvider` from `./auth` (Line 2)
- `NotificationsProvider` from `./notifications` (Line 3)
- `AppLayout` from `./layouts/AppLayout` (Line 4)
- All page components (Lines 7-13)

**Code Reference**:
- Lines 15-19: `RequireAuth` component (Route Guard)
- Lines 21-42: Main `App` component with routing

---

### **3. Authentication Module: `auth.tsx`**
**Location**: `src/auth.tsx` (Lines 1-85)

**Purpose**: Manages user authentication state and profile data.

**Type Definitions**:
- **`UserProfile`** (Lines 3-13): User data structure
  - `email: string`
  - `displayName: string`
  - `nickname?: string` (optional)
  - `major?: string`
  - `college?: string`
  - `classYear?: string`
  - `avatarUrl?: string`
  - `xp: number` (experience points)
  - `level: number` (user level)

- **`AuthContextValue`** (Lines 15-20): Authentication context interface
  - `user: UserProfile | null`
  - `login(email, password): Promise<string | null>`
  - `logout(): void`
  - `updateProfile(updates): void`

**Components/Functions**:
- **`AuthProvider`** (Lines 26-75): Context provider component
  - Manages user state (Line 27)
  - Loads user from localStorage on mount (Lines 29-38)
  - Persists user to localStorage on changes (Lines 40-43)
  - `login()` function (Lines 45-61): Validates email domain, creates user profile
  - `logout()` function (Line 63): Clears user state
  - `updateProfile()` function (Lines 65-67): Updates user profile fields
  - Provides context value (Lines 69-72)

- **`useAuth()`** (Lines 77-81): Custom hook to access auth context

**Storage Key**: `'uta-gamify:user'` (Line 24)

**Code Reference**:
- Lines 3-13: `UserProfile` type
- Lines 15-20: `AuthContextValue` type
- Lines 26-75: `AuthProvider` component
- Lines 77-81: `useAuth` hook

---

### **4. Notifications Module: `notifications.tsx`**
**Location**: `src/notifications.tsx` (Lines 1-81)

**Purpose**: Manages application-wide notification system.

**Type Definitions**:
- **`AppNotification`** (Lines 3-9): Notification data structure
  - `id: string`
  - `type: 'Quiz' | 'Flashcards' | 'Challenge' | 'Ranking' | 'System'`
  - `text: string`
  - `createdAt: number` (timestamp)
  - `read: boolean`

- **`NotificationsContextValue`** (Lines 11-17): Notifications context interface
  - `notifications: AppNotification[]`
  - `unreadCount: number`
  - `addNotification(n)`
  - `markAllRead()`
  - `clearAll()`

**Components/Functions**:
- **`NotificationsProvider`** (Lines 23-72): Context provider component
  - Manages notifications array (Line 24)
  - Loads from localStorage on mount (Lines 26-35)
  - Persists to localStorage on changes (Lines 37-39)
  - `addNotification()` function (Lines 41-52): Creates new notification with UUID
  - `markAllRead()` function (Lines 54-56): Marks all notifications as read
  - `clearAll()` function (Line 58): Removes all notifications
  - `unreadCount` computed value (Line 60)
  - Provides context value (Lines 62-65)

- **`useNotifications()`** (Lines 74-78): Custom hook to access notifications context

**Storage Key**: `'uta-gamify:notifications'` (Line 21)

**Code Reference**:
- Lines 3-9: `AppNotification` type
- Lines 11-17: `NotificationsContextValue` type
- Lines 23-72: `NotificationsProvider` component
- Lines 74-78: `useNotifications` hook

---

### **5. Layout Component: `AppLayout.tsx`**
**Location**: `src/layouts/AppLayout.tsx` (Lines 1-75)

**Purpose**: Main application shell - provides consistent navigation and header across all pages.

**Key Responsibilities**:
- Renders top navigation bar with logo and menu items
- Provides user avatar menu with dropdown
- Shows notification badge with unread count
- Handles logout functionality
- Renders `<Outlet />` for child routes (Line 71)

**State Management**:
- `menuOpen` (Line 11): Controls avatar dropdown menu visibility
- `avatarRef` (Line 12): Reference to avatar element for click-outside detection

**Navigation Links** (Lines 37-41):
- Dashboard (`/`)
- Study (`/study`)
- Challenges (`/challenges`)
- Leaderboards (`/leaderboards`)
- Profile (`/profile`)

**User Menu** (Lines 61-65):
- Profile link
- Settings link
- Logout button

**Dependencies**:
- `useAuth()` hook (Line 8)
- `useNotifications()` hook (Line 10)
- React Router hooks (`Link`, `NavLink`, `Outlet`, `useNavigate`)

**Code Reference**:
- Lines 7-75: `AppLayout` component
- Lines 14-21: Click-outside detection for menu
- Lines 23-26: Logout handler
- Lines 30-68: Header/navigation structure
- Line 71: Content outlet

---

### **6. Login Page: `Login.tsx`**
**Location**: `src/pages/Login.tsx` (Lines 1-153)

**Purpose**: User authentication interface.

**Key Responsibilities**:
- Displays login form with email and password fields
- Validates email format (must end with `@mavs.uta.edu`)
- Handles form submission and authentication
- Shows error messages
- Redirects to dashboard on successful login

**State Management**:
- `email` (Line 9): Email input value
- `password` (Line 10): Password input value
- `error` (Line 11): Error message state

**Functions**:
- `onSubmit()` (Lines 13-18): Handles form submission, calls `login()`, navigates on success

**UI Features**:
- Gradient background (Line 26)
- Centered card layout (Lines 28-35)
- MavPal branding (Lines 36-60)
- Form validation (Lines 62-149)
- Error display (Lines 110-121)

**Dependencies**:
- `useAuth()` hook (Line 7)
- React Router `useNavigate()` hook (Line 8)

**Code Reference**:
- Lines 6-153: `Login` component
- Lines 13-18: Form submission handler
- Lines 62-149: Form JSX

---

### **7. Dashboard Page: `Dashboard.tsx`**
**Location**: `src/pages/Dashboard.tsx` (Lines 1-162)

**Purpose**: Main landing page after login - shows user overview and quick actions.

**Key Responsibilities**:
- Displays welcome message with user level and XP progress
- Shows quick action buttons (Start Quiz, Create/Join Challenge)
- Displays user rankings (Class, College, Major, Overall)
- Shows today's schedule/upcoming tasks

**UI Sections**:
1. **Welcome Card** (Lines 7-45):
   - User greeting
   - Level display
   - XP progress bar
   - XP to next level

2. **Quick Actions** (Lines 47-79):
   - Start Quiz button
   - Create Challenge button
   - Join Challenge button

3. **Rankings** (Lines 80-104):
   - Class ranking (#7)
   - College ranking (#23)
   - Major ranking (#11)
   - Overall ranking (#119)

4. **Today's Schedule** (Lines 106-158):
   - Data Structures Quiz (2:00 PM)
   - Earn 100 XP goal
   - Challenge "CS Study Sprint"

**Dependencies**:
- React Router `useNavigate()` hook (Line 4)

**Code Reference**:
- Lines 3-162: `Dashboard` component
- Lines 7-45: Welcome section
- Lines 47-79: Quick actions
- Lines 80-104: Rankings display
- Lines 106-158: Schedule display

---

### **8. Profile Page: `Profile.tsx`**
**Location**: `src/pages/Profile.tsx` (Lines 1-147)

**Purpose**: User profile management and settings.

**Key Responsibilities**:
- Displays and edits user profile information
- Manages avatar upload/selection
- Shows user statistics (XP, Level)
- Handles notification preferences
- Manages privacy settings

**State Management**:
- `nickname` (Line 8): User's nickname input
- `avatarUrl` (Line 9): Avatar image URL
- `avatarEmoji` (Line 10): Selected emoji avatar
- `fileInputRef` (Line 11): Reference to file input element

**Constants**:
- `AVATARS` (Line 4): Array of emoji options

**Functions**:
- `saveSettings()` (Lines 13-16): Saves profile changes, calls `updateProfile()`

**UI Sections**:
1. **Profile Settings** (Lines 20-84):
   - Avatar display and selection
   - Basic information (read-only): Name, Email, Major, College, Class Year
   - Profile settings: Nickname input, Save/Reset buttons

2. **Stats & Preferences** (Lines 86-129):
   - XP and Level display
   - Notification preferences (Email, Push)
   - Privacy settings (Show on leaderboards, Allow friend requests)

**Sub-components**:
- **`ReadOnly`** (Lines 134-141): Helper component for read-only form fields

**Dependencies**:
- `useAuth()` hook (Line 7)

**Code Reference**:
- Lines 4: `AVATARS` constant
- Lines 6-132: `Profile` component
- Lines 13-16: `saveSettings` function
- Lines 134-141: `ReadOnly` helper component

---

### **9. Study Page: `Study.tsx`**
**Location**: `src/pages/Study.tsx` (Lines 1-1064)

**Purpose**: Core study functionality - quizzes and flashcards.

**Type Definitions** (Lines 5-8):
- **`Question`**: `{ text: string; choices: string[]; answer: number }`
- **`Quiz`**: `{ id: string; title: string; questions: Question[]; completedBy?: string[] }`
- **`Flashcard`**: `{ id: string; front: string; back: string }`
- **`FlashcardSet`**: `{ id: string; title: string; cards: Flashcard[]; completedBy?: string[] }`

**Sample Data**:
- `SAMPLE_QUIZZES` (Lines 10-31): Pre-defined quiz data
- `SAMPLE_FLASHCARDS` (Lines 33-54): Pre-defined flashcard sets

**State Management** (Multiple state variables):
- Study mode: `studyMode` (Line 61)
- Quiz state: `quizzes`, `selectedQuizId`, `questionIndex`, `selectedChoice`, `score`, `quizCompleted`, `quizResult`, `answerSubmitted` (Lines 64-79)
- Flashcard state: `flashcardSets`, `selectedFlashcardId`, `cardIndex`, `showAnswer`, `flashcardCompleted` (Lines 68-84)
- Builder state: `builderTitle`, `builderQuestion`, `builderC1-C4`, `builderAns`, `draftQuestions` (Lines 86-94)
- Flashcard builder state: `flashcardTitle`, `flashcardFront`, `flashcardBack`, `draftCards` (Lines 96-100)
- Daily tasks: `dailyTasks`, `dailyBonusClaimed` (Lines 103-110)

**Key Functions**:
- `resetQuizRunner()` (Lines 112-120): Resets quiz state
- `resetFlashcardRunner()` (Lines 122-127): Resets flashcard state
- `startQuiz()` (Lines 129-134): Starts a quiz
- `startFlashcards()` (Lines 136-141): Starts a flashcard set
- `submitAnswer()` (Lines 156-160): Submits quiz answer
- `nextQuestion()` (Lines 162-203): Advances to next question, calculates score, awards XP
- `retryQuiz()` (Lines 205-207): Resets and restarts quiz
- `nextCard()` (Lines 209-241): Advances to next flashcard, awards XP on completion
- `addDraftQuestion()` (Lines 247-261): Adds question to quiz builder
- `createQuiz()` (Lines 263-280): Creates new quiz from draft questions
- `addDraftCard()` (Lines 282-288): Adds card to flashcard builder
- `createFlashcardSet()` (Lines 290-303): Creates new flashcard set
- `markDailyTaskComplete()` (Lines 305-309): Marks daily task as done
- `claimDailyBonus()` (Lines 311-320): Claims daily bonus XP

**Helper Functions**:
- `hasUserCompletedQuiz()` (Lines 146-149): Checks if user completed a quiz
- `hasUserCompletedFlashcards()` (Lines 151-154): Checks if user completed flashcard set

**UI Sections**:
1. **Study Mode Selector** (Lines 326-373): Toggle between Quiz and Flashcards
2. **Quiz/Flashcard Library** (Lines 375-464): Lists available quizzes or flashcard sets
3. **Creation Forms** (Lines 466-619): Forms to create quizzes or flashcard sets
4. **Quiz Runner** (Lines 623-793): Interactive quiz interface
5. **Flashcard Runner** (Lines 794-936): Interactive flashcard study interface
6. **Daily Checklist** (Lines 952-1061): Daily task tracker with bonus XP

**XP Rewards**:
- Quiz: 25 XP per question (Line 183)
- Flashcard: 15 XP per card (Line 221)
- Daily tasks: 50 XP (quiz), 30 XP (flashcards), 40 XP (create), 25 XP (challenge) (Lines 104-107)

**Code Reference**:
- Lines 5-8: Type definitions
- Lines 10-54: Sample data
- Lines 56-1064: `Study` component
- Lines 112-320: Core functions
- Lines 324-1061: UI sections

---

### **10. Challenges Page: `Challenges.tsx`**
**Location**: `src/pages/Challenges.tsx` (Lines 1-332)

**Purpose**: Challenge creation and participation system.

**Type Definition**:
- **`Challenge`** (Lines 4-15): Challenge data structure
  - `id: number`
  - `title: string`
  - `description: string`
  - `progress: number`
  - `maxProgress: number`
  - `xpReward: number`
  - `createdBy: string`
  - `participants: number`
  - `endDate: string`
  - `category: string`

**State Management**:
- `challenges` (Lines 19-56): Array of challenge objects
- `title`, `desc`, `category`, `xpReward`, `maxProgress` (Lines 58-62): Form inputs for creating challenges
- `activeTab` (Line 63): Toggle between 'create' and 'join'

**Functions**:
- `create()` (Lines 65-85): Creates a new challenge and adds to list
- `joinChallenge()` (Lines 87-94): Increments participant count for a challenge

**UI Sections**:
1. **Tab Selector** (Lines 104-144): Toggle between Create and Join
2. **Create Challenge Form** (Lines 148-229): Form to create new challenges
3. **Available Challenges** (Lines 230-278): List of challenges to join
4. **My Challenges** (Lines 281-328): List of user's created/joined challenges with progress

**Code Reference**:
- Lines 4-15: `Challenge` type
- Lines 17-332: `Challenges` component
- Lines 65-85: `create` function
- Lines 87-94: `joinChallenge` function

---

### **11. Leaderboards Page: `Leaderboards.tsx`**
**Location**: `src/pages/Leaderboards.tsx` (Lines 1-58)

**Purpose**: Displays rankings across different categories.

**Type Definition**:
- **`Row`** (Line 3): Leaderboard row data
  - `name: string`
  - `xp: number`
  - `major: string`
  - `college: string`
  - `classYear: string`

**Sample Data**:
- `DATA` (Lines 4-9): Hardcoded leaderboard data

**State Management**:
- `tab` (Line 12): Current leaderboard category ('overall' | 'college' | 'major' | 'class' | 'friends')
- `filter` (Line 13): Filter object with college, major, classYear, friends

**Functions**:
- `rows` (Lines 15-21): Computed filtered and sorted leaderboard rows

**UI Sections**:
1. **Tab Buttons** (Lines 25-31): Category selection
2. **Filter Inputs** (Lines 33-37): Filter by college, major, class year
3. **Table** (Lines 39-55): Leaderboard table display

**Code Reference**:
- Lines 3: `Row` type
- Lines 4-9: Sample data
- Lines 11-58: `Leaderboards` component
- Lines 15-21: Filtering/sorting logic

---

### **12. Notifications Page: `Notifications.tsx`**
**Location**: `src/pages/Notifications.tsx` (Lines 1-40)

**Purpose**: Displays all notifications to the user.

**Key Responsibilities**:
- Lists all notifications
- Auto-marks notifications as read when page is viewed
- Shows notification count

**Functions**:
- `useEffect()` (Lines 7-9): Auto-marks all notifications as read when component mounts

**UI Sections**:
1. **Header** (Lines 13-16): Title and notification count
2. **Notification List** (Lines 17-25): List of all notifications or empty state

**Dependencies**:
- `useNotifications()` hook (Line 5)

**Code Reference**:
- Lines 4-28: `Notifications` component
- Lines 7-9: Auto-mark as read effect

---

## 🔗 **DEPENDENCY GRAPH**

```
main.tsx
  └── App.tsx
      ├── AuthProvider (auth.tsx)
      ├── NotificationsProvider (notifications.tsx)
      └── AppLayout.tsx
          ├── Dashboard.tsx
          ├── Study.tsx
          ├── Challenges.tsx
          ├── Leaderboards.tsx
          ├── Profile.tsx
          └── Notifications.tsx
      └── Login.tsx
```

---

## 📊 **DATA FLOW**

1. **Authentication Flow**:
   - User enters credentials in `Login.tsx`
   - `auth.tsx` validates and creates `UserProfile`
   - Profile stored in localStorage
   - `App.tsx` `RequireAuth` guards routes

2. **State Management Flow**:
   - Context providers (`AuthProvider`, `NotificationsProvider`) wrap app
   - Components use hooks (`useAuth`, `useNotifications`) to access state
   - State persisted in localStorage

3. **XP/Level System**:
   - Activities in `Study.tsx` and `Challenges.tsx` award XP
   - `updateProfile()` in `auth.tsx` updates user XP
   - `Dashboard.tsx` and `Profile.tsx` display XP/Level

---

## 🎯 **KEY ARCHITECTURAL PATTERNS**

1. **Context API Pattern**: Used for global state (auth, notifications)
2. **Provider Pattern**: Wrapping app with context providers
3. **Custom Hooks Pattern**: `useAuth()`, `useNotifications()` for reusable logic
4. **Route Guard Pattern**: `RequireAuth` component protects routes
5. **Component Composition**: Layout components wrap page components

---

## 📝 **CONCLUSION**

This application demonstrates a well-structured React/TypeScript architecture with:
- Clear separation of concerns
- Reusable context providers
- Type-safe TypeScript interfaces
- Component-based UI architecture
- Local storage persistence

While not traditional Java classes, these components serve similar purposes in the React ecosystem, demonstrating object-oriented principles adapted for functional programming paradigms.

