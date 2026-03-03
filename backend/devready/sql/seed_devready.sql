BEGIN;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1) Users
INSERT INTO users (email, password_hash, daily_time_limit, created_at)
VALUES
    ('alice.backend@devready.local', crypt('Password@123', gen_salt('bf', 10)), 100, NOW()),
    ('ben.frontend@devready.local', crypt('Password@123', gen_salt('bf', 10)), 90, NOW()),
    ('dina.data@devready.local', crypt('Password@123', gen_salt('bf', 10)), 120, NOW())
ON CONFLICT (email) DO UPDATE
SET daily_time_limit = EXCLUDED.daily_time_limit;

-- 2) User skills (auto-map each seeded user to a starter role)
WITH user_role_map AS (
    SELECT u.id AS user_id, r.id AS role_id
    FROM users u
    JOIN roles r ON (u.email = 'alice.backend@devready.local' AND r.name = 'Backend Developer')
                OR (u.email = 'ben.frontend@devready.local' AND r.name = 'Frontend Developer')
                OR (u.email = 'dina.data@devready.local' AND r.name = 'Data Scientist')
    WHERE u.email IN (
        'alice.backend@devready.local',
        'ben.frontend@devready.local',
        'dina.data@devready.local'
    )
),
ranked_role_skills AS (
    SELECT
        urm.user_id,
        rs.skill_id,
        rs.importance_weight,
        ROW_NUMBER() OVER (PARTITION BY urm.user_id ORDER BY rs.importance_weight DESC, rs.skill_id) AS rn
    FROM user_role_map urm
    JOIN role_skills rs ON rs.role_id = urm.role_id
)
INSERT INTO user_skills (user_id, skill_id, confidence, last_practiced, custom_importance)
SELECT
    rrs.user_id,
    rrs.skill_id,
    GREATEST(3, 8 - (rrs.rn % 4)),
    (CURRENT_DATE - ((rrs.rn * 2) + (rrs.user_id % 3)) * INTERVAL '1 day')::date,
    rrs.importance_weight
FROM ranked_role_skills rrs
ON CONFLICT (user_id, skill_id) DO UPDATE
SET confidence = EXCLUDED.confidence,
    last_practiced = EXCLUDED.last_practiced,
    custom_importance = EXCLUDED.custom_importance;

-- 3) Practice logs (one initial log per user_skill)
INSERT INTO practice_logs (user_skill_id, minutes, notes, practice_date)
SELECT
    us.id,
    (20 + ((us.id % 4) * 5))::int,
    'Seeded practice session',
    us.last_practiced
FROM user_skills us
WHERE NOT EXISTS (
    SELECT 1
    FROM practice_logs pl
    WHERE pl.user_skill_id = us.id
);

-- 4) Events / deadlines
INSERT INTO events (user_id, title, event_date, type)
SELECT
    u.id,
    'Backend Internship Interview',
    CURRENT_DATE + INTERVAL '20 day',
    'INTERVIEW'
FROM users u
WHERE u.email = 'alice.backend@devready.local'
  AND NOT EXISTS (
      SELECT 1 FROM events e
      WHERE e.user_id = u.id
        AND e.title = 'Backend Internship Interview'
  );

INSERT INTO events (user_id, title, event_date, type)
SELECT
    u.id,
    'Frontend UI Assessment',
    CURRENT_DATE + INTERVAL '15 day',
    'EXAM'
FROM users u
WHERE u.email = 'ben.frontend@devready.local'
  AND NOT EXISTS (
      SELECT 1 FROM events e
      WHERE e.user_id = u.id
        AND e.title = 'Frontend UI Assessment'
  );

INSERT INTO events (user_id, title, event_date, type)
SELECT
    u.id,
    'Data Science Take-home',
    CURRENT_DATE + INTERVAL '28 day',
    'PERSONAL'
FROM users u
WHERE u.email = 'dina.data@devready.local'
  AND NOT EXISTS (
      SELECT 1 FROM events e
      WHERE e.user_id = u.id
        AND e.title = 'Data Science Take-home'
  );

COMMIT;
