# Quick Run Commands

## Correct Path Structure
```
Mav Gamify/
  └── uta-gamify/          ← You need to be HERE first
      ├── server/          ← Then go into server
      ├── database/
      └── src/
```

## Commands to Run

### Step 1: Navigate to uta-gamify
```powershell
cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify"
```

### Step 2: Then go to server
```powershell
cd server
```

### Step 3: Install and setup
```powershell
npm install
npm run db:setup
npm run dev
```

## OR - One-liner for Backend Setup
```powershell
cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify\server"
npm install
npm run db:setup
npm run dev
```

## For Frontend (in a NEW terminal)
```powershell
cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify"
npm run dev
```

