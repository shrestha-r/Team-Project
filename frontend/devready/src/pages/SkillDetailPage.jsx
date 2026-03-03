import dayjs from "dayjs";
import { useEffect, useState } from "react";
import api from "../services/api";

function SkillDetailPage() {
  const [skills, setSkills] = useState([]);
  const [selectedUserSkillId, setSelectedUserSkillId] = useState("");
  const [minutes, setMinutes] = useState(20);
  const [confidence, setConfidence] = useState(5);
  const [practiceDate, setPracticeDate] = useState(dayjs().format("YYYY-MM-DD"));
  const [notes, setNotes] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadSkills = async () => {
    try {
      const { data } = await api.get("/api/userskills");
      setSkills(data);
      if (!selectedUserSkillId && data.length) {
        setSelectedUserSkillId(String(data[0].userSkillId));
      }
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load skills");
    }
  };

  useEffect(() => {
    loadSkills();
  }, []);

  const submitPractice = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    try {
      await api.post("/api/practice", {
        userSkillId: Number(selectedUserSkillId),
        minutes: Number(minutes),
        confidence: Number(confidence),
        notes,
        practiceDate
      });

      setSuccess("Practice log submitted and skill updated.");
      setNotes("");
      await loadSkills();
    } catch (err) {
      setError(err.response?.data?.message || "Could not save practice log");
    }
  };

  return (
    <section className="grid" style={{ gap: 16 }}>
      <article className="page-card">
        <h2 className="page-title">Skill history and confidence</h2>
        <p className="page-subtitle">Review urgency values and update practice records.</p>

        {skills.length ? (
          <ul className="list" style={{ marginTop: 16 }}>
            {skills.map((skill) => (
              <li className="list-item" key={skill.userSkillId}>
                <div>
                  <strong>{skill.skillName}</strong>
                  <p className="page-subtitle" style={{ margin: 0 }}>
                    Last practiced: {dayjs(skill.lastPracticed).format("DD MMM YYYY")}
                  </p>
                </div>
                <div style={{ textAlign: "right" }}>
                  <div className="tag brand">Conf {skill.confidence}/10</div>
                  <div className="tag accent" style={{ marginTop: 6 }}>
                    Urgency {skill.urgency}
                  </div>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p className="empty">No skills found. Choose a role first.</p>
        )}
      </article>

      <article className="page-card">
        <h3 className="section-title">Log practice</h3>

        <form className="grid" onSubmit={submitPractice}>
          <div className="form-grid">
            <label>
              Skill
              <select
                value={selectedUserSkillId}
                onChange={(e) => setSelectedUserSkillId(e.target.value)}
                required
              >
                {skills.map((skill) => (
                  <option key={skill.userSkillId} value={skill.userSkillId}>
                    {skill.skillName}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Minutes practiced
              <input
                type="number"
                min={1}
                max={300}
                value={minutes}
                onChange={(e) => setMinutes(e.target.value)}
                required
              />
            </label>

            <label>
              Confidence (1-10)
              <input
                type="number"
                min={1}
                max={10}
                value={confidence}
                onChange={(e) => setConfidence(e.target.value)}
                required
              />
            </label>

            <label>
              Practice date
              <input
                type="date"
                value={practiceDate}
                onChange={(e) => setPracticeDate(e.target.value)}
                required
              />
            </label>
          </div>

          <label>
            Notes
            <textarea
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="What did you revise today?"
            />
          </label>

          <button className="primary" type="submit" disabled={!skills.length}>
            Save practice log
          </button>

          {error ? <p className="error-text">{error}</p> : null}
          {success ? <p className="success-text">{success}</p> : null}
        </form>
      </article>
    </section>
  );
}

export default SkillDetailPage;
