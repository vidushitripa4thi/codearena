# CodeArena — Screenshot Capture Guide

This guide describes how to capture and organize genuine visual previews of the CodeArena web platform for the repository documentation.

---

## Recommended Dimensions & Display Settings

- **Recommended Resolution:** `1440 x 900` or `1280 x 800` (16:10 or 16:9 aspect ratio)
- **Browser:** Chrome, Edge, or Firefox in fullscreen / clean view (no browser extension icons or personal bookmarks bar visible)
- **Theme:** Default platform dark theme (`slate-950` / `slate-900`)
- **Format:** Optimized PNG images placed in this directory (`docs/screenshots/`)

---

## Required Screenshots & Naming Conventions

Save the captured screenshots using the following exact file names so the root `README.md` renders them automatically:

| File Name | Route | What to Showcase |
| :--- | :--- | :--- |
| `landing.png` | `http://localhost:5173/` | Hero section, feature cards, "Explore Problems" and "Get Started" call-to-actions, and top navigation bar. |
| `auth.png` | `http://localhost:5173/login` or `/register` | Sign In / Registration card with clean inputs, validation hints, and link to toggle between sign-in and sign-up. |
| `dashboard.png` | `http://localhost:5173/dashboard` | Logged-in user overview, role badge, quick actions, statistics cards, and recent problem recommendations. |
| `problems.png` | `http://localhost:5173/problems` | Problems catalogue, search input, difficulty filter pills (All / Easy / Medium / Hard), problem cards, and pagination controls. |
| `problem-detail.png` | `http://localhost:5173/problems/1` | Split view: Left pane with problem description, examples, constraints; Right pane with Monaco Code Editor, language selector, and "Submit Solution" button. |
| `submissions.png` | `http://localhost:5173/submissions` | Submissions history table showcasing problem title, language badge, submitted timestamp, and `PENDING` intake status badge. |

---

## Step-by-Step Instructions to Capture

### 1. Start the Backend with Seeder
In terminal 1:
```powershell
$env:DB_PASSWORD="your_postgres_password"
$env:JWT_SECRET="codearena-jwt-secret-key-that-is-at-least-256-bits-long-32-bytes"
$env:SEED_DATA="true"
mvn spring-boot:run
```
*Wait until Spring Boot logs `Started CodeArenaApplication in ... seconds`.*

### 2. Start the Frontend Dev Server
In terminal 2:
```powershell
cd frontend
npm run dev
```
*Open `http://localhost:5173` in your browser.*

### 3. Log In with a Demo Account
- Register a fresh user (e.g., `demo_coder` / `demo@codearena.com` / `Password123!`).
- Navigate through each page to take clean screenshots.
- Ensure no personal credentials, real email addresses, or browser bookmark bars appear in the captured images.

### 4. Save and Commit
Place the files into `docs/screenshots/`:
```text
docs/screenshots/
├── README.md
├── landing.png
├── auth.png
├── dashboard.png
├── problems.png
├── problem-detail.png
└── submissions.png
```
Once added, git commit and push to refresh the README visuals on GitHub!
