import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import api from "../services/api";
import { login, register } from "../services/authService";

function LoginPage() {
  const [mode, setMode] = useState("login");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [dailyTimeLimit, setDailyTimeLimit] = useState(60);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const { saveSession } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const payload = {
        email,
        password,
        ...(mode === "register" ? { dailyTimeLimit: Number(dailyTimeLimit) } : {})
      };

      const authData = mode === "register" ? await register(payload) : await login(payload);
      saveSession(authData);

      const { data: userSkills } = await api.get("/api/userskills");
      if (!userSkills.length) {
        navigate("/role-select", { replace: true });
        return;
      }

      const destination = location.state?.from || "/dashboard";
      navigate(destination, { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || "Unable to authenticate. Check your details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrap">
      <section className="auth-card">
        <p className="mono">DevReady v1</p>
        <h1 className="page-title">{mode === "login" ? "Welcome back" : "Create your account"}</h1>
        <p className="page-subtitle">Build your daily readiness plan using the Skill Urgency Engine.</p>

        <form onSubmit={submit} className="grid" style={{ marginTop: 18 }}>
          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="you@example.com"
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
              placeholder="At least 8 characters"
            />
          </label>

          {mode === "register" ? (
            <label>
              Daily time limit (minutes)
              <input
                type="number"
                min={15}
                max={480}
                value={dailyTimeLimit}
                onChange={(e) => setDailyTimeLimit(e.target.value)}
                required
              />
            </label>
          ) : null}

          <div className="row" style={{ justifyContent: "space-between", alignItems: "center" }}>
            <button type="submit" className="primary" disabled={loading}>
              {loading ? "Please wait..." : mode === "login" ? "Login" : "Register"}
            </button>

            <button
              type="button"
              className="secondary"
              onClick={() => setMode(mode === "login" ? "register" : "login")}
              disabled={loading}
            >
              {mode === "login" ? "Need an account? Register" : "Already have an account? Login"}
            </button>
          </div>

          {error ? <p className="error-text">{error}</p> : null}
        </form>
      </section>
    </div>
  );
}

export default LoginPage;
