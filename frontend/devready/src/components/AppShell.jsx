import { NavLink, Outlet, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";

function AppShell() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <main className="app-shell">
      <header className="topbar">
        <div className="brand-block">
          <div className="brand-dot">DR</div>
          <div>
            <h1 className="brand-title">DevReady</h1>
            <p className="brand-subtitle">Decision-support planner for career readiness</p>
          </div>
        </div>

        <nav className="navbar">
          <NavLink to="/dashboard" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
            Dashboard
          </NavLink>
          <NavLink to="/plan" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
            Today's Plan
          </NavLink>
          <NavLink to="/skills" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
            Skills
          </NavLink>
          <NavLink to="/deadlines" className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}>
            Deadlines
          </NavLink>
          <button type="button" className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </nav>
      </header>

      <section style={{ marginBottom: 14 }}>
        <span className="tag brand">Signed in as {user?.email}</span>
      </section>

      <Outlet />
    </main>
  );
}

export default AppShell;
