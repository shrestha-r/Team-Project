import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function RoleSelectPage() {
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [savingRoleId, setSavingRoleId] = useState(null);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    const loadRoles = async () => {
      try {
        const { data } = await api.get("/api/roles");
        setRoles(data);
      } catch (err) {
        setError(err.response?.data?.message || "Failed to load roles");
      } finally {
        setLoading(false);
      }
    };

    loadRoles();
  }, []);

  const selectRole = async (roleId) => {
    setSavingRoleId(roleId);
    setError("");

    try {
      await api.post("/api/users/select-role", { roleId });
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || "Could not select this role");
    } finally {
      setSavingRoleId(null);
    }
  };

  return (
    <section className="page-card">
      <h2 className="page-title">Choose your role starter pack</h2>
      <p className="page-subtitle">Your role maps directly to default skills and importance weights.</p>

      {loading ? <p className="empty">Loading roles...</p> : null}

      <div className="grid cards">
        {roles.map((role) => (
          <article key={role.id} className="stat-card">
            <p className="section-title" style={{ marginBottom: 6 }}>{role.name}</p>
            <p className="page-subtitle">{role.description}</p>
            <button
              type="button"
              className="primary"
              style={{ marginTop: 12 }}
              disabled={savingRoleId === role.id}
              onClick={() => selectRole(role.id)}
            >
              {savingRoleId === role.id ? "Applying..." : "Select role"}
            </button>
          </article>
        ))}
      </div>

      {error ? <p className="error-text">{error}</p> : null}
    </section>
  );
}

export default RoleSelectPage;
