import { Link } from "react-router-dom";
import "./Footer.css";

export default function Footer() {
  return (
    <footer className="footer">
      <div className="page footer-inner">
        <div className="footer-brand">
          <span className="brand-mark small">QM</span>
          <div>
            <p className="footer-title">Quantity Measurement</p>
            <p className="footer-tagline">Precision unit measurement, done right.</p>
          </div>
        </div>

        <nav className="footer-links">
          <Link to="/">Home</Link>
          <Link to="/about">About</Link>
          <Link to="/login">Login</Link>
        </nav>

        <p className="footer-meta">
          Built with Spring Boot &amp; React · UC20 · &copy; {new Date().getFullYear()}
        </p>
      </div>
    </footer>
  );
}
