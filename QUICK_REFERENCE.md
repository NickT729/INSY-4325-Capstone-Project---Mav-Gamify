# MavPal - Quick Reference Guide
## Component & Line Number Quick Lookup

---

## 🚀 **QUICK START**

This is a **JavaScript/React** application (using TypeScript for type annotations). The TypeScript files compile to JavaScript - they ARE JavaScript modules. The components serve similar purposes to classes in object-oriented programming.

**Note**: All `.tsx` and `.ts` files are JavaScript code that compiles to `.js` during build.

---

## 📁 **FILE STRUCTURE & LINE NUMBERS**

### **Core Application Files**

| File | Lines | Component/Type | Description |
|------|-------|----------------|-------------|
| `src/main.tsx` | 1-13 | `main.tsx` | Entry point - renders App |
| `src/App.tsx` | 1-42 | `App` | Root component with routing |
| `src/App.tsx` | 15-19 | `RequireAuth` | Route guard |

### **Context Providers (State Management)**

| File | Lines | Component/Type | Description |
|------|-------|----------------|-------------|
| `src/auth.tsx` | 3-13 | `UserProfile` | User data type |
| `src/auth.tsx` | 15-20 | `AuthContextValue` | Auth context interface |
| `src/auth.tsx` | 26-75 | `AuthProvider` | Authentication provider |
| `src/auth.tsx` | 77-81 | `useAuth` | Auth hook |
| `src/notifications.tsx` | 3-9 | `AppNotification` | Notification type |
| `src/notifications.tsx` | 11-17 | `NotificationsContextValue` | Notifications context interface |
| `src/notifications.tsx` | 23-72 | `NotificationsProvider` | Notifications provider |
| `src/notifications.tsx` | 74-78 | `useNotifications` | Notifications hook |

### **Layout Components**

| File | Lines | Component/Type | Description |
|------|-------|----------------|-------------|
| `src/layouts/AppLayout.tsx` | 1-75 | `AppLayout` | Main layout with navigation |

### **Page Components**

| File | Lines | Component/Type | Description |
|------|-------|----------------|-------------|
| `src/pages/Login.tsx` | 1-153 | `Login` | Login page |
| `src/pages/Dashboard.tsx` | 1-162 | `Dashboard` | Dashboard page |
| `src/pages/Profile.tsx` | 1-147 | `Profile` | Profile page |
| `src/pages/Profile.tsx` | 134-141 | `ReadOnly` | Helper component |
| `src/pages/Study.tsx` | 1-1064 | `Study` | Study page (largest file) |
| `src/pages/Challenges.tsx` | 1-332 | `Challenges` | Challenges page |
| `src/pages/Leaderboards.tsx` | 1-58 | `Leaderboards` | Leaderboards page |
| `src/pages/Notifications.tsx` | 1-40 | `Notifications` | Notifications page |

### **Study Page Types (within Study.tsx)**

| Line | Type | Description |
|------|------|-------------|
| 5 | `Question` | Quiz question structure |
| 6 | `Quiz` | Quiz data structure |
| 7 | `Flashcard` | Flashcard structure |
| 8 | `FlashcardSet` | Flashcard set structure |
| 10-31 | `SAMPLE_QUIZZES` | Sample quiz data |
| 33-54 | `SAMPLE_FLASHCARDS` | Sample flashcard data |

### **Challenges Page Types**

| Line | Type | Description |
|------|------|-------------|
| 4-15 | `Challenge` | Challenge data structure |

### **Leaderboards Page Types**

| Line | Type | Description |
|------|------|-------------|
| 3 | `Row` | Leaderboard row structure |
| 4-9 | `DATA` | Sample leaderboard data |

---

## 🔍 **KEY FUNCTIONS BY LINE NUMBER**

### **Authentication (`auth.tsx`)**
- **Line 45-61**: `login()` - Handles user login
- **Line 63**: `logout()` - Handles user logout
- **Line 65-67**: `updateProfile()` - Updates user profile

### **Notifications (`notifications.tsx`)**
- **Line 41-52**: `addNotification()` - Adds new notification
- **Line 54-56**: `markAllRead()` - Marks all as read
- **Line 58**: `clearAll()` - Clears all notifications

### **Study Page (`Study.tsx`)**
- **Line 112-120**: `resetQuizRunner()` - Resets quiz state
- **Line 122-127**: `resetFlashcardRunner()` - Resets flashcard state
- **Line 129-134**: `startQuiz()` - Starts a quiz
- **Line 136-141**: `startFlashcards()` - Starts flashcard set
- **Line 156-160**: `submitAnswer()` - Submits quiz answer
- **Line 162-203**: `nextQuestion()` - Advances quiz, awards XP
- **Line 209-241**: `nextCard()` - Advances flashcard, awards XP
- **Line 247-261**: `addDraftQuestion()` - Adds question to builder
- **Line 263-280**: `createQuiz()` - Creates new quiz
- **Line 282-288**: `addDraftCard()` - Adds card to builder
- **Line 290-303**: `createFlashcardSet()` - Creates flashcard set
- **Line 305-309**: `markDailyTaskComplete()` - Marks task complete
- **Line 311-320**: `claimDailyBonus()` - Claims daily bonus XP

### **Challenges Page (`Challenges.tsx`)**
- **Line 65-85**: `create()` - Creates new challenge
- **Line 87-94**: `joinChallenge()` - Joins existing challenge

### **Profile Page (`Profile.tsx`)**
- **Line 13-16**: `saveSettings()` - Saves profile changes

### **Login Page (`Login.tsx`)**
- **Line 13-18**: `onSubmit()` - Handles form submission

---

## 📊 **DATA STRUCTURES**

### **UserProfile** (`auth.tsx:3-13`)
```typescript
{
  email: string
  displayName: string
  nickname?: string
  major?: string
  college?: string
  classYear?: string
  avatarUrl?: string
  xp: number
  level: number
}
```

### **AppNotification** (`notifications.tsx:3-9`)
```typescript
{
  id: string
  type: 'Quiz' | 'Flashcards' | 'Challenge' | 'Ranking' | 'System'
  text: string
  createdAt: number
  read: boolean
}
```

### **Quiz** (`Study.tsx:6`)
```typescript
{
  id: string
  title: string
  questions: Question[]
  completedBy?: string[]
}
```

### **Challenge** (`Challenges.tsx:4-15`)
```typescript
{
  id: number
  title: string
  description: string
  progress: number
  maxProgress: number
  xpReward: number
  createdBy: string
  participants: number
  endDate: string
  category: string
}
```

---

## 🎯 **XP REWARD SYSTEM**

| Activity | XP Amount | Location |
|----------|-----------|----------|
| Quiz (per question) | 25 XP | `Study.tsx:183` |
| Flashcard (per card) | 15 XP | `Study.tsx:221` |
| Daily Quiz Task | 50 XP | `Study.tsx:104` |
| Daily Flashcard Task | 30 XP | `Study.tsx:105` |
| Daily Create Task | 40 XP | `Study.tsx:106` |
| Daily Challenge Task | 25 XP | `Study.tsx:107` |

---

## 🔗 **DEPENDENCY CHAIN**

```
main.tsx (1-13)
  └── App.tsx (1-42)
      ├── AuthProvider (auth.tsx:26-75)
      ├── NotificationsProvider (notifications.tsx:23-72)
      └── AppLayout (AppLayout.tsx:1-75)
          ├── Dashboard (Dashboard.tsx:1-162)
          ├── Study (Study.tsx:1-1064)
          ├── Challenges (Challenges.tsx:1-332)
          ├── Leaderboards (Leaderboards.tsx:1-58)
          ├── Profile (Profile.tsx:1-147)
          └── Notifications (Notifications.tsx:1-40)
      └── Login (Login.tsx:1-153)
```

---

## 📝 **STORAGE KEYS**

| Key | Purpose | Location |
|-----|---------|----------|
| `'uta-gamify:user'` | User profile | `auth.tsx:24` |
| `'uta-gamify:notifications'` | Notifications | `notifications.tsx:21` |

---

## 🎓 **FOR YOUR SCHOOL PROJECT**

**Important Note**: This is a **TypeScript/React** project, not Java. However, the architectural concepts are similar:

- **React Components** ≈ Java Classes
- **TypeScript Interfaces** ≈ Java Interfaces
- **Context Providers** ≈ Singleton Services
- **Hooks** ≈ Utility Methods
- **Props** ≈ Constructor Parameters
- **State** ≈ Instance Variables

The component diagram in `CLASS_DIAGRAM.md` shows the architecture using UML-style notation adapted for React/TypeScript.

---

## 📚 **FULL DOCUMENTATION**

- **Architecture Documentation**: See `ARCHITECTURE_DOCUMENTATION.md`
- **Class Diagram**: See `CLASS_DIAGRAM.md`
- **This Quick Reference**: See `QUICK_REFERENCE.md`

---

**Total Files Analyzed**: 12 JavaScript modules/components
**Total Lines of Code**: ~2,500+ lines
**Language**: JavaScript (React) - written in TypeScript which compiles to JavaScript
**Framework**: React 19.1.1, React Router 7.9.1
**Compilation**: TypeScript → JavaScript (via `tsc` compiler)

