# DevReady - Career Readiness Planner

DevReady is a decision-support web system that tells students what to practice today using a **Skill Urgency Engine**.

This repository contains:

- Backend: Spring Boot API (`/backend/devready`)
- Frontend: React app (`/frontend/devready`)

## System Goal

Calculate which skills need attention most today based on:

`urgency = (days_since_practice * importance_weight * deadline_factor) / confidence`

Then generate a daily plan within the user time limit.

## Active Project Folders

Use these folders for DevReady:

- `/home/r-shrestha/Desktop/team_project/backend/devready`
- `/home/r-shrestha/Desktop/team_project/frontend/devready`

There are other older folders in the repo (for example `Backend/`) that are not part of the current DevReady app.

## Architecture

```text
React (Vite) frontend
        |
        | HTTP + JWT
        v
Spring Boot REST API
        |
        v
PostgreSQL (devready DB)
```

## Core Features

- JWT auth (register/login)
- Role starter packs (backend/frontend/data/general SWE)
- User skill tracking with confidence and last-practiced date
- Urgency engine with deadline booster
- Daily plan generator endpoint
- Practice log capture
- Event/deadline management
- Dashboard readiness score and plan visualization

## Database Tables

- `users`
- `roles`
- `skills`
- `role_skills`
- `user_skills`
- `practice_logs`
- `events`

## API Surface

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/roles`
- `POST /api/users/select-role`
- `GET /api/skills`
- `GET /api/userskills`
- `POST /api/practice`
- `GET /api/plan/today`
- `GET /api/events`
- `POST /api/events`

## Quick Start

### 1. Start PostgreSQL and create DB

```sql
CREATE DATABASE devready;
```

### 2. Run backend

```bash
cd /home/r-shrestha/Desktop/team_project/backend/devready
./mvnw spring-boot:run
```

Default backend URL: `http://localhost:8080`

### 3. Run frontend

```bash
cd /home/r-shrestha/Desktop/team_project/frontend/devready
npm install
npm run dev
```

Default frontend URL: `http://localhost:5173`

### 4. Seed demo data (optional but recommended)

```bash
PGPASSWORD=password psql -h localhost -U postgres -d devready -f /home/r-shrestha/Desktop/team_project/backend/devready/sql/seed_devready.sql
```

Demo users:

- `alice.backend@devready.local`
- `ben.frontend@devready.local`
- `dina.data@devready.local`

Password for all demo users: `Password@123`

## Environment Variables

### Backend

- `DEVREADY_DB_URL`
- `DEVREADY_DB_USER`
- `DEVREADY_DB_PASS`
- `DEVREADY_DDL_AUTO`
- `DEVREADY_SHOW_SQL`
- `DEVREADY_JWT_SECRET`
- `DEVREADY_JWT_EXPIRATION_MS`
- `PORT`

### Frontend

- `VITE_API_BASE_URL` (defaults to `http://localhost:8080`)

## Module Documentation

- Backend doc: [`backend/devready/README.md`](backend/devready/README.md)
- Frontend doc: [`frontend/devready/README.md`](frontend/devready/README.md)
