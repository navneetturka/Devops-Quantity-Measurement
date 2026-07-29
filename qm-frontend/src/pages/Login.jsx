import { Navigate } from "react-router-dom";
import { FcGoogle } from "react-icons/fc";
import { useAuth } from "../hooks/useAuth";
import "./Login.css";

export default function Login() {
  const { isAuthenticated, login } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="login-screen">
      <div className="login-card card">
        <div className="login-logo">
          <span className="brand-mark login-mark">QM</span>
        </div>
        <h1>Welcome to Quantity Measurement</h1>
        <p className="login-copy">
          The Quantity Measurement System — compare, convert, add, subtract and
          divide across length, weight, temperature and volume, backed by a
          Spring Boot API and secured with Google.
        </p>

        <button className="btn btn-primary btn-block google-btn" onClick={login}>
          <FcGoogle size={20} /> Continue with Google
        </button>

        <p className="login-note">
          We only use your Google account to verify who you are — no separate
          password is ever created or stored.
        </p>
      </div>
    </div>
  );
}
