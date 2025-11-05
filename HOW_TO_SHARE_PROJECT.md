# How to Share This Project with Friends

## Option 1: GitHub (Recommended for Developers) ⭐

This is the best option if your friends are developers and you want version control, collaboration, and easy updates.

### Steps:

1. **Create a GitHub account** (if you don't have one): https://github.com

2. **Create a new repository on GitHub:**
   - Go to https://github.com/new
   - Name it something like `uta-gamify` or `mav-gamify`
   - Choose **Private** or **Public** (private means only you and invited people can see it)
   - **Don't** initialize with README, .gitignore, or license (you already have these)

3. **Push your code to GitHub:**
   ```bash
   # Make sure you're in the project directory
   cd "C:\Users\Torre\OneDrive\Desktop\coding\Mav Gamify\uta-gamify"
   
   # Add all your files
   git add .
   
   # Commit your changes
   git commit -m "Initial commit - ready to share"
   
   # Add GitHub as remote (replace YOUR_USERNAME with your GitHub username)
   git remote add origin https://github.com/YOUR_USERNAME/uta-gamify.git
   
   # Push to GitHub
   git push -u origin master
   ```

4. **Share with friends:**
   - If **Public**: Just send them the repository URL: `https://github.com/YOUR_USERNAME/uta-gamify`
   - If **Private**: 
     - Go to Settings → Collaborators → Add people
     - Enter their GitHub usernames or email addresses
     - They'll receive an invitation

5. **Your friends can clone it:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/uta-gamify.git
   cd uta-gamify
   npm install
   ```

---

## Option 2: OneDrive Sharing (Easiest) 📁

Since your project is already in OneDrive, this is the simplest option for non-developers.

### Steps:

1. **Right-click** on the `uta-gamify` folder in File Explorer

2. Select **"Share"** or **"OneDrive" → "Share"**

3. Choose sharing options:
   - **Anyone with the link** - Anyone who has the link can access
   - **People in your organization** - Only people in your organization
   - **Specific people** - Only people you invite

4. Set permissions:
   - **Can view** - They can only read files
   - **Can edit** - They can modify files (be careful with this!)

5. Click **"Copy link"** or enter email addresses and click **"Send"**

6. **Important Notes:**
   - Your friends will need to sync the OneDrive folder
   - Large files (like `node_modules`) might take a while to sync
   - This is better for viewing, but not ideal for active development

---

## Option 3: Create a ZIP File 📦

Good for one-time sharing, but not ideal for ongoing collaboration.

### Steps:

1. **Before zipping, exclude unnecessary files:**
   - `node_modules` folder (friends can run `npm install` to get dependencies)
   - `dist` folder (build output, can be regenerated)
   - Database files (`.db` files) - these contain your local data
   - Any backup folders

2. **Create ZIP:**
   - Right-click the `uta-gamify` folder
   - Select **"Send to" → "Compressed (zipped) folder"**
   - This creates `uta-gamify.zip`

3. **Share the ZIP:**
   - Upload to Google Drive, Dropbox, or email (if small enough)
   - Or share via OneDrive

4. **Your friends:**
   - Extract the ZIP
   - Run `npm install` in both `uta-gamify` and `uta-gamify/server` directories
   - Follow setup instructions in `COMPLETE_SETUP_GUIDE.md`

---

## Option 4: GitLab / Bitbucket (Alternative to GitHub)

Similar to GitHub, but hosted on different platforms:
- **GitLab**: https://gitlab.com
- **Bitbucket**: https://bitbucket.org

Follow similar steps to GitHub option above.

---

## What to Share:

✅ **DO Share:**
- All source code files (`.ts`, `.tsx`, `.js`, `.css`, etc.)
- Configuration files (`package.json`, `vite.config.ts`, etc.)
- Documentation files (`.md` files)
- Database schema files (`schema.sql`)
- `.gitignore` file

❌ **DON'T Share:**
- `node_modules` folder (too large, can be regenerated)
- `dist` folder (build output, can be regenerated)
- Database files (`.db` files) - contains your personal data
- Environment files with secrets (`.env` files if you have any)
- Backup folders

---

## After Sharing - What Your Friends Need to Do:

1. **Install dependencies:**
   ```bash
   # In the main directory
   npm install
   
   # In the server directory
   cd server
   npm install
   ```

2. **Set up the database:**
   - Follow instructions in `COMPLETE_SETUP_GUIDE.md`
   - Or check `database/README.md`

3. **Run the application:**
   ```bash
   # Terminal 1: Start the frontend
   npm run dev
   
   # Terminal 2: Start the backend
   cd server
   node index.js
   ```

---

## Recommended Approach:

**For developers:** Use **GitHub** (Option 1) - it's the standard way to share code projects.

**For non-developers:** Use **OneDrive sharing** (Option 2) - it's the easiest and already set up.

**For quick one-time sharing:** Use **ZIP file** (Option 3) - simple but less convenient for updates.

