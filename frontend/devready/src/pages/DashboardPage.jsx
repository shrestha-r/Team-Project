import { useEffect, useMemo, useState } from "react";
import dayjs from "dayjs";
import { Bar, BarChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import StatCard from "../components/StatCard";
import api from "../services/api";

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
    </section>
  );
}

export default DashboardPage;
