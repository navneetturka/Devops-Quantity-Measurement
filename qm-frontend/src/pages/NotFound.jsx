import { Link } from "react-router-dom";
import "./NotFound.css";

export default function NotFound() {
  return (
    <div className="not-found">
      <span className="not-found-code mono">404</span>
      <h1>Out of range.</h1>
      <p>That page isn't a value this app knows how to measure.</p>
      <Link to="/" className="btn btn-primary">
        Back to Home
      </Link>
    </div>
  );
}
