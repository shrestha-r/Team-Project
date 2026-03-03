# DevReady Frontend

React frontend for **DevReady - Career Readiness Planner**.

## Overview

This app provides the student-facing UI for:

- login/register
- role starter-pack selection
- readiness dashboard
- TODAY'S PLAN view
- skill history + practice logging
- deadline management

Backend expected: Spring Boot API running at `http://localhost:8080` by default.

## Tech Stack

- React 19 + Vite 7
- React Router
- Axios
- Recharts
- Day.js

## App Structure

`src/`:

- `pages` - route-level pages
- `components` - shared UI (`AppShell`, `ProtectedRoute`, `StatCard`)
- `services` - API client + auth service
- `context` - auth session context
- `hooks` - `useAuth`

## Routes

- `/login` - login/register page
- `/role-select` - select role starter pack
- `/dashboard` - readiness summary
- `/plan` - daily plan output
- `/skills` - skill list + practice log form
- `/deadlines` - add/list events

All routes except `/login` are protected.

## Auth Flow

- Login/register calls backend auth endpoints.
- JWT is stored in `localStorage` key `devready_token`.
- User info is stored in `localStorage` key `devready_user`.
- Axios interceptor adds `Authorization: Bearer <token>` on each request.

## API Integration Map

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/roles`
- `POST /api/users/select-role`
- `GET /api/plan/today`
- `GET /api/userskills`
- `POST /api/practice`
- `GET /api/events`
- `POST /api/events`

## Environment Variables

Create `.env.local` in this folder if needed:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

If not set, frontend defaults to `http://localhost:8080`.

## Local Setup

1. Install dependencies:

```bash
cd /home/r-shrestha/Desktop/team_project/frontend/devready
npm install
```

2. Start dev server:

```bash
npm run dev
```

3. Open:

- `http://localhost:5173`

## Build for Production

```bash
npm run build
npm run preview
```

## Lint

```bash
npm run lint
```

## Notes

- If API calls fail, verify backend is running and `VITE_API_BASE_URL` is correct.
- If you get `401`, logout/login again to refresh token in local storage.
