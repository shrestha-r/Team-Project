import { useEffect, useMemo, useState } from "react";
import dayjs from "dayjs";
import { Bar, BarChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import StatCard from "../components/StatCard";
import api from "../services/api";

const REMINDER_THRESHOLD = 35;
const CEMETERY_THRESHOLD = 15;

function DashboardPage() {
  const [plan, setPlan] = useState(null);
  const [skills, setSkills] = useState([]);
  const [events, setEvents] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const load = async () => {
      try {
        const [planRes, skillsRes, eventsRes] = await Promise.all([
          api.get("/api/plan/today"),
          api.get("/api/userskills"),
          api.get("/api/events")
        ]);

        setPlan(planRes.data);
        setSkills(skillsRes.data);
        setEvents(eventsRes.data);
      } catch (err) {
        setError(err.response?.data?.message || "Failed to load dashboard data");
      }
    };

    load();
  }, []);

  const chartData = useMemo(() => {
    if (!plan?.items) {
      return [];
    }

    return plan.items.slice(0, 6).map((item) => ({
      skill: item.skill,
      minutes: item.minutes,
      urgency: Number(item.urgency.toFixed(2))
    }));
  }, [plan]);

  const healthInsights = useMemo(() => {
    if (!skills.length) {
      return { average: 0, soon: [], critical: [], soonest: null };
    }

    const soonCandidates = skills
      .filter((skill) => (skill.healthScore ?? 0) > CEMETERY_THRESHOLD)
      .sort(
        (a, b) =>
          (a.daysToReminder ?? Number.POSITIVE_INFINITY) - (b.daysToReminder ?? Number.POSITIVE_INFINITY)
      )
      .slice(0, 4);

    const critical = skills.filter((skill) => (skill.healthScore ?? 0) <= CEMETERY_THRESHOLD);
    const average =
      skills.reduce((sum, skill) => sum + (skill.healthScore ?? 0), 0) / skills.length;

    return {
      average,
      soon: soonCandidates,
      critical,
      soonest: soonCandidates[0] ?? null
    };
  }, [skills]);

  const averageHealth = Math.round(healthInsights.average);
  const nextReminderText = healthInsights.soonest
    ? `${Math.max(0, Math.round(healthInsights.soonest.daysToReminder))} days`
    : "None yet";
  const reminderQueue = Math.max(0, healthInsights.soon.length);
  const cemeteryQueue = Math.max(0, healthInsights.critical.length);

  return (
    <section className="grid" style={{ gap: 16 }}>
      <article className="page-card">
        <h2 className="page-title">Readiness dashboard</h2>
        <p className="page-subtitle">Live summary from your urgency engine and daily planner.</p>

        <div className="grid cards">
          <StatCard label="Readiness score" value={`${plan?.readinessScore ?? 0}%`} hint="Confidence + recency blend" />
          <StatCard label="Daily limit" value={`${plan?.dailyLimit ?? 0} min`} hint="Configured study capacity" />
          <StatCard label="Allocated today" value={`${plan?.totalAllocated ?? 0} min`} hint="Planner output" />
          <StatCard label="Tracked skills" value={skills.length} hint="Role + practice data" />
          <StatCard label="Upcoming deadlines" value={events.length} hint="Interview / exam / personal" />
        </div>
      </article>

      <div className="row">
        <article className="page-card col">
          <h3 className="section-title">Today plan distribution</h3>
          {chartData.length ? (
            <div style={{ width: "100%", height: 260 }}>
              <ResponsiveContainer>
                <BarChart data={chartData}>
                  <XAxis dataKey="skill" tick={{ fontSize: 12 }} />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="minutes" fill="#0f766e" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          ) : (
            <p className="empty">No plan items yet. Pick a role and log practice to generate output.</p>
          )}
        </article>

        <article className="page-card col">
          <h3 className="section-title">Highest urgency skills</h3>
          {skills.length ? (
            <ul className="list">
              {skills.slice(0, 6).map((skill) => (
                <li className="list-item" key={skill.userSkillId}>
                  <div>
                    <strong>{skill.skillName}</strong>
                    <p className="page-subtitle" style={{ margin: 0 }}>
                      Confidence {skill.confidence}/10
                    </p>
                  </div>
                  <span className="tag accent">Urgency {skill.urgency}</span>
                </li>
              ))}
            </ul>
          ) : (
            <p className="empty">No user skills yet. Go to role selection first.</p>
          )}
        </article>
      </div>

<article className="page-card">
  <h3 className="section-title">Upcoming deadlines</h3>
  {events.length ? (
    <ul className="list">
      {events.slice(0, 8).map((event) => (
        <li key={event.id} className="list-item">
          <div>
            <strong>{event.title}</strong>
            <p className="page-subtitle" style={{ margin: 0 }}>
              {dayjs(event.eventDate).format("DD MMM YYYY")}
            </p>
          </div>
          <span className="tag brand">{event.type}</span>
        </li>
      ))}
    </ul>
  ) : (
    <p className="empty">No deadlines yet. Add one from the Deadlines page.</p>
  )}

  {error ? <p className="error-text">{error}</p> : null}
</article>

<article className="page-card">
  <h3 className="section-title">Skill maintenance health</h3>
  <p className="page-subtitle">
    Health scores follow the exponential decay engine outlined in the proposal (
    <span className="mono">H(t) = 100 × e⁻λt</span>) so we only remind you when a skill nears{" "}
    {REMINDER_THRESHOLD}% and surface a cemetery warning before the score hits{" "}
    {CEMETERY_THRESHOLD}%.
  </p>

  <div className="grid cards">
    <StatCard
      label="Average skill health"
      value={`${averageHealth}%`}
      hint="Category-aware decay model"
    />
    <StatCard
      label="Reminder queue"
      value={reminderQueue}
      hint={`Approaching ${REMINDER_THRESHOLD}% health`}
    />
    <StatCard
      label="Next smart reminder"
      value={nextReminderText}
      hint="Threshold set to 35% health"
    />
    <StatCard
      label="Cemetery warnings"
      value={cemeteryQueue}
      hint="Health ≤ 15% moves skills to the Skill Cemetery"
    />
  </div>
</article>

<div className="row">
  <article className="page-card col">
    <h3 className="section-title">Health watchlist</h3>
    <p className="page-subtitle">
      Skills nearing {REMINDER_THRESHOLD}% health so smart reminders stay context-aware.
    </p>

    {healthInsights.soon.length ? (
      <ul className="list">
        {healthInsights.soon.map((skill) => (
          <li className="list-item" key={`${skill.userSkillId}-reminder`}>
            <div>
              <strong>{skill.skillName}</strong>
              <p className="page-subtitle" style={{ margin: 0 }}>
                {skill.healthScore}% health · {skill.daysSince} days since practice
              </p>
            </div>

            <span className="tag accent">
              {skill.daysToReminder <= 0
                ? "Remind today"
                : `${Math.round(skill.daysToReminder)}d`}
            </span>
          </li>
        ))}
      </ul>
    ) : (
      <p className="empty">
        No skills are close enough to the reminder threshold yet.
      </p>
    )}
  </article>
        <article className="page-card col">
          <h3 className="section-title">Skill Cemetery preview</h3>
          <p className="page-subtitle">
            Skills at {CEMETERY_THRESHOLD}% or below are in danger of landing in the cemetery; we show the estimated relearn time so you can choose maintenance over recovery.
          </p>
          {healthInsights.critical.length ? (
            <ul className="list">
              {healthInsights.critical.slice(0, 4).map((skill) => (
                <li className="list-item" key={`${skill.userSkillId}-cemetery`}>
                  <div>
                    <strong>{skill.skillName}</strong>
                    <p className="page-subtitle" style={{ margin: 0 }}>
                      Health {skill.healthScore}% · {skill.daysSince} days since practice
                    </p>
                  </div>
                  <span className="tag danger">Relearn approx {skill.relearnWeeks}w</span>
                </li>
              ))}
            </ul>
          ) : (
            <p className="empty">No skills are in the cemetery danger zone yet.</p>
          )}
        </article>
      </div>
    </section>
  );
}

export default DashboardPage;
