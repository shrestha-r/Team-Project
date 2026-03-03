# DevReady Backend

Spring Boot backend for **DevReady - Career Readiness Planner**.

## Overview

This service powers the DevReady decision engine:

- JWT authentication
- Role-based skill starter packs
- Skill urgency calculation
- Daily plan generation
- Practice logging
- Deadline/event tracking

Base package: `com.playback.devready`

## Tech Stack

- Java 17
- Spring Boot 3.5.11
- Spring Web, Data JPA, Security, Validation
- PostgreSQL
- JWT (`io.jsonwebtoken`)
- Maven Wrapper (`./mvnw`)

## Project Structure

`src/main/java/com/playback/devready`:

- `config` - security config, startup seed config
- `controller` - REST endpoints
- `service` - business logic and urgency engine
- `repository` - JPA repositories
- `model` - entities (`users`, `skills`, `events`, etc.)
- `dto` - request/response contracts
- `security` - JWT filter + user details
- `util` - token provider

## Database Schema

Tables used:

- `users`
- `roles`
- `skills`
- `role_skills`
- `user_skills`
- `practice_logs`
- `events`

`DataInitializer` auto-seeds:

- starter roles (`Backend Developer`, `Frontend Developer`, `Data Scientist`, `General SWE`)
- master skills
- role-skill mappings

## Core Engine

Urgency formula:

`urgency = (days_since_last_practice * importance_weight * deadline_factor) / confidence`

Implemented in `UrgencyEngineService`.  
`DailyPlanService` ranks skills by urgency and allocates minutes until the user daily limit is reached.

## API Endpoints

All endpoints require Bearer JWT except `/api/auth/**`.

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

### Roles

- `GET /api/roles`
- `POST /api/users/select-role`

### Skills

- `GET /api/skills`
- `GET /api/userskills`
- `POST /api/practice`

### Plan

- `GET /api/plan/today`

### Events

- `GET /api/events`
- `POST /api/events`

## Configuration

From `src/main/resources/application.properties`:

- `DEVREADY_DB_URL` (default `jdbc:postgresql://localhost:5432/devready`)
- `DEVREADY_DB_USER` (default `postgres`)
- `DEVREADY_DB_PASS` (default `password`)
- `DEVREADY_DDL_AUTO` (default `update`)
- `DEVREADY_SHOW_SQL` (default `false`)
- `PORT` (default `8080`)
- `DEVREADY_JWT_SECRET` (set a secure 32+ char key in non-local environments)
- `DEVREADY_JWT_EXPIRATION_MS` (default `86400000`)

## Local Setup

1. Start PostgreSQL and create DB:

```sql
CREATE DATABASE devready;
```

2. Export env vars (optional if using defaults):

```bash
export DEVREADY_DB_URL=jdbc:postgresql://localhost:5432/devready
export DEVREADY_DB_USER=postgres
export DEVREADY_DB_PASS=password
export DEVREADY_JWT_SECRET='replace-with-a-strong-32-plus-char-secret'
```

3. Run the service:

```bash
cd /home/r-shrestha/Desktop/team_project/backend/devready
./mvnw spring-boot:run
```

4. Run tests:

```bash
./mvnw test
```

## Seed Data

Role/skill master data is automatic at startup.

To insert full demo data for all tables, run:

```bash
PGPASSWORD=password psql -h localhost -U postgres -d devready -f /home/r-shrestha/Desktop/team_project/backend/devready/sql/seed_devready.sql
```

Seeded demo users:

- `alice.backend@devready.local`
- `ben.frontend@devready.local`
- `dina.data@devready.local`

Password for all: `Password@123`

## Troubleshooting

- Java classpath errors in VS Code usually mean wrong JDK version. Use JDK 17 for this project.
- If auth fails after changing JWT secret, clear browser local storage and login again.
- If DB tables are missing, ensure backend started at least once with `DEVREADY_DDL_AUTO=update`.
