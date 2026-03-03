import dayjs from "dayjs";
import { useEffect, useState } from "react";
import api from "../services/api";

function DeadlinePage() {
  const [events, setEvents] = useState([]);
  const [title, setTitle] = useState("");
  const [eventDate, setEventDate] = useState(dayjs().add(7, "day").format("YYYY-MM-DD"));
  const [type, setType] = useState("INTERVIEW");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const loadEvents = async () => {
    try {
      const { data } = await api.get("/api/events");
      setEvents(data);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load events");
    }
  };

  useEffect(() => {
    loadEvents();
  }, []);

  const addEvent = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    try {
      await api.post("/api/events", { title, eventDate, type });
      setTitle("");
      setSuccess("Deadline added. Urgency factor will update automatically.");
      await loadEvents();
    } catch (err) {
      setError(err.response?.data?.message || "Could not add deadline");
    }
  };

  return (
    <section className="grid" style={{ gap: 16 }}>
      <article className="page-card">
        <h2 className="page-title">Add deadline</h2>
        <p className="page-subtitle">Interview and exam dates boost urgency for relevant skills.</p>

        <form className="grid" onSubmit={addEvent} style={{ marginTop: 14 }}>
          <div className="form-grid">
            <label>
              Title
              <input value={title} onChange={(e) => setTitle(e.target.value)} required maxLength={200} />
            </label>

            <label>
              Event date
              <input
                type="date"
                value={eventDate}
                onChange={(e) => setEventDate(e.target.value)}
                required
              />
            </label>

            <label>
              Type
              <select value={type} onChange={(e) => setType(e.target.value)}>
                <option value="INTERVIEW">INTERVIEW</option>
                <option value="EXAM">EXAM</option>
                <option value="PERSONAL">PERSONAL</option>
              </select>
            </label>
          </div>

          <button className="primary" type="submit">
            Save deadline
          </button>

          {error ? <p className="error-text">{error}</p> : null}
          {success ? <p className="success-text">{success}</p> : null}
        </form>
      </article>

      <article className="page-card">
        <h3 className="section-title">All deadlines</h3>
        {events.length ? (
          <ul className="list">
            {events.map((item) => (
              <li key={item.id} className="list-item">
                <div>
                  <strong>{item.title}</strong>
                  <p className="page-subtitle" style={{ margin: 0 }}>
                    {dayjs(item.eventDate).format("DD MMM YYYY")}
                  </p>
                </div>
                <span className="tag brand">{item.type}</span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="empty">No deadlines created yet.</p>
        )}
      </article>
    </section>
  );
}

export default DeadlinePage;
