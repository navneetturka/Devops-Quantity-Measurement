import { Link } from "react-router-dom";
import { FiArrowRight } from "react-icons/fi";
import { TbRulerMeasure, TbTemperature, TbDroplet, TbWeight } from "react-icons/tb";
import { useAuth } from "../hooks/useAuth";
import "./Home.css";

const UNIT_TYPES = [
  { icon: <TbRulerMeasure />, label: "Length" },
  { icon: <TbWeight />, label: "Weight" },
  { icon: <TbTemperature />, label: "Temperature" },
  { icon: <TbDroplet />, label: "Volume" },
];

// Kept intentionally short — this is a welcome screen, not a spec sheet.
// The full breakdown (unit families, stack, feature list) lives on /about.
export default function Home() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="home">
      <section className="hero">
        <div className="page hero-inner">
          <h1>Measure anything. Trust every digit.</h1>
          <p className="hero-sub">
            Compare, convert, add, subtract and divide across length, weight, temperature and
            volume — precise, unit-aware results without doing the arithmetic yourself.
          </p>
          <div className="hero-actions">
            <Link to={isAuthenticated ? "/dashboard" : "/login"} className="btn btn-primary">
              Get Started <FiArrowRight />
            </Link>
            <Link to="/about" className="btn btn-secondary">
              Learn how it works
            </Link>
          </div>

          <ul className="hero-units">
            {UNIT_TYPES.map((u) => (
              <li key={u.label}>
                <span className="hero-unit-icon">{u.icon}</span>
                {u.label}
              </li>
            ))}
          </ul>
        </div>
      </section>
    </div>
  );
}
