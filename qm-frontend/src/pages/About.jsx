import { SiSpringboot, SiReact, SiJsonwebtokens, SiGoogle } from "react-icons/si";
import { HiOutlineServer, HiOutlineDeviceMobile, HiOutlineShieldCheck, HiOutlineLightningBolt, HiOutlineChartBar } from "react-icons/hi";
import { TbRulerMeasure, TbTemperature, TbDroplet, TbWeight } from "react-icons/tb";
import "./About.css";

const UNIT_TYPES = [
  { icon: <TbRulerMeasure />, label: "Length", detail: "Feet · Inches · Yards · Meters · Centimeters" },
  { icon: <TbWeight />, label: "Weight", detail: "Kilogram · Gram · Pound · Ounce · Tonne" },
  { icon: <TbTemperature />, label: "Temperature", detail: "Celsius · Fahrenheit · Kelvin" },
  { icon: <TbDroplet />, label: "Volume", detail: "Litre · Millilitre · Gallon" },
];

const FEATURES = [
  {
    icon: <HiOutlineLightningBolt />,
    title: "Five operations, one engine",
    body: "Compare, convert, add, subtract and divide across any compatible unit pair.",
  },
  {
    icon: <HiOutlineShieldCheck />,
    title: "Google-only authentication",
    body: "No passwords to manage or leak — every session is a short-lived JWT.",
  },
  {
    icon: <HiOutlineChartBar />,
    title: "Full operation history",
    body: "Every calculation is persisted server-side, so you can search and revisit it later.",
  },
];

const STACK = [
  {
    icon: <SiSpringboot />,
    title: "Spring Boot",
    body: "The unchanged UC1–UC19 backend: REST controllers, a service layer with the conversion engine, and an H2-backed history repository.",
  },
  {
    icon: <SiReact />,
    title: "React",
    body: "This UC20 frontend — functional components, hooks, Context API and React Router, replacing the old static HTML/CSS/JS entirely.",
  },
  {
    icon: <SiJsonwebtokens />,
    title: "JWT",
    body: "Every session is a signed, short-lived token. Axios attaches it to every request automatically; a 401 anywhere signs you out.",
  },
  {
    icon: <SiGoogle />,
    title: "Google OAuth",
    body: "The only way in. No passwords are ever created, stored, or transmitted by this application.",
  },
  {
    icon: <HiOutlineServer />,
    title: "REST APIs",
    body: "compare, convert, add, subtract, divide and history endpoints under /api/v1/quantities, reused exactly as UC19 built them.",
  },
  {
    icon: <HiOutlineDeviceMobile />,
    title: "Responsive UI",
    body: "A clean, minimal card system and fluid grid that hold up from a widescreen monitor down to a phone.",
  },
];

export default function About() {
  return (
    <div className="page about-page">
      <div className="about-hero">
        <h1>A measurement engine, given a proper front door.</h1>
        <p>
          Quantity Measurement started as a kata: comparing feet to inches, then grew — use case by
          use case — into arithmetic across four measurement families, persistent
          history, and real authentication. UC20's job was singular: replace the
          original static frontend with a modern React application, without touching a
          single line of the backend logic that makes the numbers correct.
        </p>
      </div>

      <h2 className="about-section-title">Four measurement families, one workflow</h2>
      <p className="about-section-sub">
        Every type follows the same operations, with sensible rules built in — temperature,
        for instance, can't be divided.
      </p>
      <div className="about-grid about-grid-4">
        {UNIT_TYPES.map((u) => (
          <div key={u.label} className="about-card card">
            <div className="about-icon">{u.icon}</div>
            <h3>{u.label}</h3>
            <p>{u.detail}</p>
          </div>
        ))}
      </div>

      <h2 className="about-section-title">Built for accuracy, not guesswork</h2>
      <div className="about-grid about-grid-3">
        {FEATURES.map((f) => (
          <div key={f.title} className="about-card card">
            <div className="about-icon">{f.icon}</div>
            <h3>{f.title}</h3>
            <p>{f.body}</p>
          </div>
        ))}
      </div>

      <h2 className="about-section-title">Under the hood</h2>
      <div className="about-grid">
        {STACK.map((s) => (
          <div key={s.title} className="about-card card">
            <div className="about-icon">{s.icon}</div>
            <h3>{s.title}</h3>
            <p>{s.body}</p>
          </div>
        ))}
      </div>

      <div className="about-note card">
        <h3>Why the rules matter</h3>
        <p>
          Temperature can't be added, subtracted or divided — 30°C + 30°C isn't 60°C —
          so those operations are hidden whenever Temperature is selected. Divide never
          shows a result unit because a ratio between two quantities of the same type
          is unit-less by definition. These aren't UI quirks; they mirror how the
          service layer actually validates each operation.
        </p>
      </div>
    </div>
  );
}
