import { Navigate, Route, Routes } from "react-router-dom";
import AppShell from "./components/AppShell";
import ProtectedRoute from "./components/ProtectedRoute";
import DashboardPage from "./pages/DashboardPage";
import DeadlinePage from "./pages/DeadlinePage";
import LoginPage from "./pages/LoginPage";
import RoleSelectPage from "./pages/RoleSelectPage";
import SkillDetailPage from "./pages/SkillDetailPage";
import TodayPlanPage from "./pages/TodayPlanPage";

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route
        element={
          <ProtectedRoute>
            <AppShell />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="/role-select" element={<RoleSelectPage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/plan" element={<TodayPlanPage />} />
        <Route path="/skills" element={<SkillDetailPage />} />
        <Route path="/deadlines" element={<DeadlinePage />} />
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

export default App;
