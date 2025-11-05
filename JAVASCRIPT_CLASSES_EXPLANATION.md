# MavPal - JavaScript Classes & Functions Explained
## Complete JavaScript Module Documentation

---

## 🎯 **IMPORTANT: This IS JavaScript!**

The files use `.tsx` and `.ts` extensions, but **TypeScript compiles to JavaScript**. These are JavaScript modules with type annotations that get stripped during compilation. The actual runtime code is **100% JavaScript**.

**Build Process**: `tsc -b && vite build` (see `package.json` line 8)
- TypeScript compiler (`tsc`) converts `.tsx`/`.ts` → `.js`
- Vite bundles the JavaScript files

---

## 📋 **JAVASCRIPT MODULES (Classes/Functions)**

### **1. main.js (main.tsx)**
**Location**: `src/main.tsx` (Lines 1-13)
**JavaScript Equivalent**: Entry point module

```javascript
// This is the JavaScript that runs
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App.tsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>
)
```

**Purpose**: Initializes the React application and renders the root component.

---

### **2. App.js (App.tsx)**
**Location**: `src/App.tsx` (Lines 1-42)
**JavaScript Equivalent**: Main application router module

**JavaScript Functions**:
- **`RequireAuth()`** (Lines 15-19): Route guard function
  ```javascript
  function RequireAuth() {
    const { user } = useAuth()
    if (!user) return <Navigate to="/login" replace />
    return <Outlet />
  }
  ```

- **`App()`** (Lines 21-42): Root component function
  ```javascript
  export default function App() {
    return (
      <AuthProvider>
        <NotificationsProvider>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route element={<RequireAuth />}>
              <Route element={<AppLayout />}>
                <Route index element={<Dashboard />} />
                <Route path="study" element={<Study />} />
                {/* ... more routes */}
              </Route>
            </Route>
          </Routes>
        </NotificationsProvider>
      </AuthProvider>
    )
  }
  ```

---

### **3. auth.js (auth.tsx) - Authentication Module**
**Location**: `src/auth.tsx` (Lines 1-85)

**JavaScript Data Structure** (UserProfile object):
```javascript
// Lines 3-13: User data structure
const UserProfile = {
  email: String,
  displayName: String,
  nickname: String,  // optional
  major: String,     // optional
  college: String,   // optional
  classYear: String, // optional
  avatarUrl: String, // optional
  xp: Number,
  level: Number
}
```

**JavaScript Functions**:

1. **`AuthProvider()`** (Lines 26-75): Context provider function
   ```javascript
   export function AuthProvider({ children }) {
     const [user, setUser] = useState(null)
     
     // Load from localStorage (Lines 29-38)
     useEffect(() => {
       const raw = localStorage.getItem('uta-gamify:user')
       if (raw) {
         setUser(JSON.parse(raw))
       }
     }, [])
     
     // Save to localStorage (Lines 40-43)
     useEffect(() => {
       if (user) localStorage.setItem('uta-gamify:user', JSON.stringify(user))
       else localStorage.removeItem('uta-gamify:user')
     }, [user])
     
     // Login function (Lines 45-61)
     const login = async (email, password) => {
       const isMavs = email.trim().toLowerCase().endsWith('@mavs.uta.edu')
       if (!isMavs) return 'Email must end with @mavs.uta.edu'
       const displayName = email.split('@')[0]
       setUser({
         email,
         displayName,
         nickname: displayName,
         major: 'Information Systems',
         college: 'College of Engineering',
         classYear: '2025',
         avatarUrl: undefined,
         xp: 420,
         level: 3,
       })
       return null
     }
     
     // Logout function (Line 63)
     const logout = () => setUser(null)
     
     // Update profile function (Lines 65-67)
     const updateProfile = (updates) => {
       setUser((prev) => (prev ? { ...prev, ...updates } : prev))
     }
     
     return <AuthContext.Provider value={{ user, login, logout, updateProfile }}>
       {children}
     </AuthContext.Provider>
   }
   ```

2. **`useAuth()`** (Lines 77-81): Custom hook function
   ```javascript
   export function useAuth() {
     const ctx = useContext(AuthContext)
     if (!ctx) throw new Error('useAuth must be used within AuthProvider')
     return ctx
   }
   ```

---

### **4. notifications.js (notifications.tsx) - Notifications Module**
**Location**: `src/notifications.tsx` (Lines 1-81)

**JavaScript Data Structure** (AppNotification object):
```javascript
// Lines 3-9: Notification data structure
const AppNotification = {
  id: String,
  type: String,  // 'Quiz' | 'Flashcards' | 'Challenge' | 'Ranking' | 'System'
  text: String,
  createdAt: Number,  // timestamp
  read: Boolean
}
```

**JavaScript Functions**:

1. **`NotificationsProvider()`** (Lines 23-72): Context provider function
   ```javascript
   export function NotificationsProvider({ children }) {
     const [notifications, setNotifications] = useState([])
     
     // Load from localStorage (Lines 26-35)
     useEffect(() => {
       const raw = localStorage.getItem('uta-gamify:notifications')
       if (raw) {
         setNotifications(JSON.parse(raw))
       }
     }, [])
     
     // Save to localStorage (Lines 37-39)
     useEffect(() => {
       localStorage.setItem('uta-gamify:notifications', JSON.stringify(notifications))
     }, [notifications])
     
     // Add notification function (Lines 41-52)
     const addNotification = ({ text, type = 'System' }) => {
       setNotifications(prev => [
         {
           id: crypto.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random()}`,
           type,
           text,
           createdAt: Date.now(),
           read: false,
         },
         ...prev,
       ])
     }
     
     // Mark all read function (Lines 54-56)
     const markAllRead = () => {
       setNotifications(prev => prev.map(n => ({ ...n, read: true })))
     }
     
     // Clear all function (Line 58)
     const clearAll = () => setNotifications([])
     
     // Unread count (Line 60)
     const unreadCount = useMemo(() => 
       notifications.filter(n => !n.read).length, 
       [notifications]
     )
     
     return <NotificationsContext.Provider value={{ 
       notifications, 
       unreadCount, 
       addNotification, 
       markAllRead, 
       clearAll 
     }}>
       {children}
     </NotificationsContext.Provider>
   }
   ```

2. **`useNotifications()`** (Lines 74-78): Custom hook function
   ```javascript
   export function useNotifications() {
     const ctx = useContext(NotificationsContext)
     if (!ctx) throw new Error('useNotifications must be used within NotificationsProvider')
     return ctx
   }
   ```

---

### **5. AppLayout.js (AppLayout.tsx)**
**Location**: `src/layouts/AppLayout.tsx` (Lines 1-75)

**JavaScript Function**:
```javascript
export default function AppLayout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const { unreadCount } = useNotifications()
  const [menuOpen, setMenuOpen] = useState(false)
  const avatarRef = useRef(null)
  
  // Click outside handler (Lines 14-21)
  useEffect(() => {
    const onDocClick = (e) => {
      if (!avatarRef.current) return
      if (!avatarRef.current.contains(e.target)) setMenuOpen(false)
    }
    document.addEventListener('click', onDocClick)
    return () => document.removeEventListener('click', onDocClick)
  }, [])
  
  // Logout handler (Lines 23-26)
  const handleLogout = () => {
    logout()
    navigate('/login')
  }
  
  // Render JSX (Lines 28-74)
  return (
    <div className="app-shell">
      <header className="topbar">
        {/* Navigation structure */}
      </header>
      <main className="content">
        <Outlet />
      </main>
    </div>
  )
}
```

---

### **6. Login.js (Login.tsx)**
**Location**: `src/pages/Login.tsx` (Lines 1-153)

**JavaScript Function**:
```javascript
export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  
  // Form submission handler (Lines 13-18)
  const onSubmit = async (e) => {
    e.preventDefault()
    const err = await login(email, password)
    if (err) setError(err)
    else navigate('/')
  }
  
  // Render login form (Lines 20-149)
  return (
    <div>
      {/* Login form JSX */}
    </div>
  )
}
```

---

### **7. Dashboard.js (Dashboard.tsx)**
**Location**: `src/pages/Dashboard.tsx` (Lines 1-162)

**JavaScript Function**:
```javascript
export default function Dashboard() {
  const navigate = useNavigate()
  
  // Render dashboard (Lines 5-162)
  return (
    <div className="page">
      {/* Welcome card (Lines 7-45) */}
      {/* Quick actions (Lines 47-79) */}
      {/* Rankings (Lines 80-104) */}
      {/* Schedule (Lines 106-158) */}
    </div>
  )
}
```

---

### **8. Profile.js (Profile.tsx)**
**Location**: `src/pages/Profile.tsx` (Lines 1-147)

**JavaScript Constants**:
```javascript
// Line 4: Avatar options
const AVATARS = ['🙂','😀','😎','🦊','🐯','🐼','🐵','🐱','🐶','🦉']
```

**JavaScript Functions**:

1. **`Profile()`** (Lines 6-132): Main profile component
   ```javascript
   export default function Profile() {
     const { user, updateProfile } = useAuth()
     const [nickname, setNickname] = useState(user?.nickname ?? user?.displayName ?? '')
     const [avatarUrl, setAvatarUrl] = useState(user?.avatarUrl)
     const [avatarEmoji, setAvatarEmoji] = useState(AVATARS[0])
     const fileInputRef = useRef(null)
     
     // Save settings function (Lines 13-16)
     const saveSettings = () => {
       updateProfile({ nickname, avatarUrl })
       alert('Settings saved')
     }
     
     // Render profile UI (Lines 18-132)
     return (
       <div className="grid cols-2">
         {/* Profile settings JSX */}
       </div>
     )
   }
   ```

2. **`ReadOnly()`** (Lines 134-141): Helper component function
   ```javascript
   function ReadOnly({ label, value }) {
     return (
       <label style={{ display: 'grid', gap: 6 }}>
         <span className="muted">{label}</span>
         <input value={value} disabled />
       </label>
     )
   }
   ```

---

### **9. Study.js (Study.tsx) - LARGEST MODULE**
**Location**: `src/pages/Study.tsx` (Lines 1-1064)

**JavaScript Data Structures**:
```javascript
// Lines 5-8: Type definitions (compile to JavaScript objects)
const Question = { text: String, choices: Array, answer: Number }
const Quiz = { id: String, title: String, questions: Array, completedBy: Array }
const Flashcard = { id: String, front: String, back: String }
const FlashcardSet = { id: String, title: String, cards: Array, completedBy: Array }
```

**JavaScript Sample Data**:
```javascript
// Lines 10-31: Sample quizzes
const SAMPLE_QUIZZES = [
  {
    id: 'algorithms',
    title: 'Algorithms Basics',
    questions: [
      { text: 'What is the Big-O complexity of binary search?', 
        choices: ['O(1)', 'O(log n)', 'O(n)', 'O(n log n)'], 
        answer: 1 },
      // ... more questions
    ],
    completedBy: []
  },
  // ... more quizzes
]

// Lines 33-54: Sample flashcards
const SAMPLE_FLASHCARDS = [
  {
    id: 'cs-terminology',
    title: 'CS Terminology',
    cards: [
      { id: '1', front: 'What is Big O notation?', 
        back: 'A way to describe the time complexity of an algorithm' },
      // ... more cards
    ],
    completedBy: []
  },
  // ... more flashcard sets
]
```

**JavaScript Functions** (Main component - Lines 56-1064):

```javascript
export default function Study() {
  const { user, updateProfile } = useAuth()
  const { addNotification } = useNotifications()
  
  // State variables (Lines 61-110)
  const [studyMode, setStudyMode] = useState('quiz')
  const [quizzes, setQuizzes] = useState(SAMPLE_QUIZZES)
  const [selectedQuizId, setSelectedQuizId] = useState(null)
  const [questionIndex, setQuestionIndex] = useState(0)
  const [selectedChoice, setSelectedChoice] = useState(null)
  const [score, setScore] = useState(0)
  // ... many more state variables
  
  // Reset quiz runner (Lines 112-120)
  const resetQuizRunner = (quiz) => {
    setQuestionIndex(0)
    setSelectedChoice(null)
    setScore(0)
    setQuizCompleted(false)
    setQuizResult(null)
    setAnswerSubmitted(false)
  }
  
  // Reset flashcard runner (Lines 122-127)
  const resetFlashcardRunner = (flashcardSet) => {
    setCardIndex(0)
    setShowAnswer(false)
    setFlashcardCompleted(false)
  }
  
  // Start quiz (Lines 129-134)
  const startQuiz = (id) => {
    setSelectedQuizId(id)
    setStudyMode('quiz')
    const quiz = quizzes.find((q) => q.id === id) ?? null
    resetQuizRunner(quiz)
  }
  
  // Start flashcards (Lines 136-141)
  const startFlashcards = (id) => {
    setSelectedFlashcardId(id)
    setStudyMode('flashcards')
    const flashcardSet = flashcardSets.find((f) => f.id === id) ?? null
    resetFlashcardRunner(flashcardSet)
  }
  
  // Submit answer (Lines 156-160)
  const submitAnswer = () => {
    if (!selectedQuiz || selectedChoice == null || !currentQuestion) return
    setAnswerSubmitted(true)
  }
  
  // Next question (Lines 162-203)
  const nextQuestion = () => {
    if (!selectedQuiz || !currentQuestion) return
    
    const newScore = selectedChoice === currentQuestion.answer ? score + 1 : score
    setScore(newScore)
    
    if (questionIndex < selectedQuiz.questions.length - 1) {
      setQuestionIndex(questionIndex + 1)
      setSelectedChoice(null)
      setAnswerSubmitted(false)
    } else {
      // Quiz completed
      const finalScore = newScore
      const passThreshold = Math.ceil(selectedQuiz.questions.length * 0.7)
      const passed = finalScore >= passThreshold
      
      setQuizCompleted(true)
      setQuizResult(passed ? 'pass' : 'fail')
      
      if (passed && !hasUserCompletedQuiz(selectedQuiz.id)) {
        const earned = selectedQuiz.questions.length * 25 // 25 XP per question
        updateProfile({ xp: (user?.xp ?? 0) + earned })
        
        setQuizzes(prev => prev.map(q => 
          q.id === selectedQuiz.id 
            ? { ...q, completedBy: [...(q.completedBy || []), user?.email || ''] }
            : q
        ))
        
        markDailyTaskComplete('quiz')
        
        addNotification({
          type: 'Quiz',
          text: `Completed quiz "${selectedQuiz.title}" (+${selectedQuiz.questions.length * 25} XP)`
        })
      }
    }
  }
  
  // Next card (Lines 209-241)
  const nextCard = () => {
    if (!selectedFlashcardSet) return
    
    if (cardIndex < selectedFlashcardSet.cards.length - 1) {
      setCardIndex(cardIndex + 1)
      setShowAnswer(false)
    } else {
      setFlashcardCompleted(true)
      
      if (!hasUserCompletedFlashcards(selectedFlashcardSet.id)) {
        const earned = selectedFlashcardSet.cards.length * 15 // 15 XP per card
        updateProfile({ xp: (user?.xp ?? 0) + earned })
        
        setFlashcardSets(prev => prev.map(f => 
          f.id === selectedFlashcardSet.id 
            ? { ...f, completedBy: [...(f.completedBy || []), user?.email || ''] }
            : f
        ))
        
        markDailyTaskComplete('flashcards')
        
        addNotification({
          type: 'Flashcards',
          text: `Completed flashcard set "${selectedFlashcardSet.title}" (+${selectedFlashcardSet.cards.length * 15} XP)`
        })
      }
    }
  }
  
  // Create quiz (Lines 263-280)
  const createQuiz = () => {
    if (!builderTitle || draftQuestions.length === 0) return
    const id = builderTitle.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '') || `quiz-${Date.now()}`
    const newQuiz = { id, title: builderTitle, questions: draftQuestions, completedBy: [] }
    setQuizzes((prev) => [newQuiz, ...prev])
    setSelectedQuizId(id)
    setDraftQuestions([])
    setBuilderTitle('')
    // ... reset all builder fields
    markDailyTaskComplete('create')
  }
  
  // Create flashcard set (Lines 290-303)
  const createFlashcardSet = () => {
    if (!flashcardTitle || draftCards.length === 0) return
    const id = flashcardTitle.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '') || `flashcards-${Date.now()}`
    const newFlashcardSet = { id, title: flashcardTitle, cards: draftCards, completedBy: [] }
    setFlashcardSets((prev) => [newFlashcardSet, ...prev])
    setSelectedFlashcardId(id)
    setDraftCards([])
    setFlashcardTitle('')
    // ... reset all builder fields
    markDailyTaskComplete('create')
  }
  
  // Daily bonus (Lines 311-320)
  const claimDailyBonus = () => {
    const completedTasks = dailyTasks.filter(task => task.completed)
    const totalBonus = completedTasks.reduce((sum, task) => sum + task.xp, 0)
    
    if (totalBonus > 0 && !dailyBonusClaimed) {
      updateProfile({ xp: (user?.xp ?? 0) + totalBonus })
      setDailyBonusClaimed(true)
      alert(`Daily bonus claimed! +${totalBonus} XP`)
    }
  }
  
  // Render UI (Lines 324-1064)
  return (
    <div className="page">
      {/* Study mode selector, quiz/flashcard library, creation forms, runners, daily checklist */}
    </div>
  )
}
```

---

### **10. Challenges.js (Challenges.tsx)**
**Location**: `src/pages/Challenges.tsx` (Lines 1-332)

**JavaScript Data Structure**:
```javascript
// Lines 4-15: Challenge object structure
const Challenge = {
  id: Number,
  title: String,
  description: String,
  progress: Number,
  maxProgress: Number,
  xpReward: Number,
  createdBy: String,
  participants: Number,
  endDate: String,
  category: String
}
```

**JavaScript Function**:
```javascript
export default function Challenges() {
  const { user } = useAuth()
  const [challenges, setChallenges] = useState([...]) // Lines 19-56
  const [title, setTitle] = useState('')
  const [desc, setDesc] = useState('')
  const [category, setCategory] = useState('Study')
  const [xpReward, setXpReward] = useState(100)
  const [maxProgress, setMaxProgress] = useState(10)
  const [activeTab, setActiveTab] = useState('create')
  
  // Create challenge function (Lines 65-85)
  const create = () => {
    if (!title.trim()) return
    const newChallenge = {
      id: Date.now(),
      title,
      description: desc || 'No description',
      progress: 0,
      maxProgress,
      xpReward,
      createdBy: user?.displayName || 'Anonymous',
      participants: 1,
      endDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      category
    }
    setChallenges((prev) => [newChallenge, ...prev])
    setTitle('')
    setDesc('')
    // ... reset all fields
  }
  
  // Join challenge function (Lines 87-94)
  const joinChallenge = (challengeId) => {
    setChallenges(prev => prev.map(c => 
      c.id === challengeId 
        ? { ...c, participants: c.participants + 1 }
        : c
    ))
    alert('Successfully joined the challenge!')
  }
  
  // Render UI (Lines 96-332)
  return (
    <div className="page">
      {/* Challenge creation and listing UI */}
    </div>
  )
}
```

---

### **11. Leaderboards.js (Leaderboards.tsx)**
**Location**: `src/pages/Leaderboards.tsx` (Lines 1-58)

**JavaScript Data Structure**:
```javascript
// Line 3: Row object structure
const Row = {
  name: String,
  xp: Number,
  major: String,
  college: String,
  classYear: String
}
```

**JavaScript Sample Data**:
```javascript
// Lines 4-9: Sample leaderboard data
const DATA = [
  { name: 'You', xp: 520, major: 'IS', college: 'Engineering', classYear: '2025' },
  { name: 'Sam', xp: 600, major: 'IS', college: 'Engineering', classYear: '2025' },
  // ... more rows
]
```

**JavaScript Function**:
```javascript
export default function Leaderboards() {
  const [tab, setTab] = useState('overall')
  const [filter, setFilter] = useState({ college: '', major: '', classYear: '', friends: false })
  
  // Filtered and sorted rows (Lines 15-21)
  const rows = useMemo(() => {
    let r = [...DATA]
    if (tab === 'college' && filter.college) r = r.filter((x) => x.college === filter.college)
    if (tab === 'major' && filter.major) r = r.filter((x) => x.major === filter.major)
    if (tab === 'class' && filter.classYear) r = r.filter((x) => x.classYear === filter.classYear)
    return r.sort((a, b) => b.xp - a.xp)
  }, [tab, filter])
  
  // Render UI (Lines 23-58)
  return (
    <div className="card">
      {/* Tab buttons, filters, table */}
    </div>
  )
}
```

---

### **12. Notifications.js (Notifications.tsx)**
**Location**: `src/pages/Notifications.tsx` (Lines 1-40)

**JavaScript Function**:
```javascript
export default function Notifications() {
  const { notifications, unreadCount, markAllRead } = useNotifications()
  
  // Auto-mark as read (Lines 7-9)
  useEffect(() => {
    if (unreadCount > 0) markAllRead()
  }, [unreadCount])
  
  // Render UI (Lines 11-28)
  return (
    <div className="card">
      <div>
        <h2>Notifications</h2>
        <span>{notifications.length} total</span>
      </div>
      {notifications.length === 0 ? (
        <div>No notifications yet.</div>
      ) : (
        <ul>
          {notifications.map((n) => (
            <li key={n.id}>[{n.type}] {n.text}</li>
          ))}
        </ul>
      )}
    </div>
  )
}
```

---

## 📊 **SUMMARY**

All files are **JavaScript modules** that compile from TypeScript:
- **12 JavaScript modules** (components/functions)
- **Multiple JavaScript functions** within each module
- **JavaScript objects** for data structures
- **JavaScript arrays** for collections
- **JavaScript localStorage** for persistence

The TypeScript annotations are just for development-time type checking - they compile away to pure JavaScript!

---

## 🎯 **KEY JAVASCRIPT CONCEPTS USED**

1. **ES6 Modules**: `import`/`export`
2. **Arrow Functions**: `() => {}`
3. **Destructuring**: `const { user } = useAuth()`
4. **Template Literals**: `` `text ${variable}` ``
5. **Array Methods**: `.map()`, `.filter()`, `.reduce()`, `.find()`
6. **Object Spread**: `{ ...prev, ...updates }`
7. **Optional Chaining**: `user?.email`
8. **Nullish Coalescing**: `user?.xp ?? 0`
9. **React Hooks**: `useState()`, `useEffect()`, `useContext()`, `useMemo()`
10. **Async/Await**: `async (email, password) => { await login(...) }`

---

**This is 100% JavaScript code!** The TypeScript is just type annotations that help during development but compile to standard JavaScript.

