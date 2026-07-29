import { useEffect, useRef, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import Loader from "../components/common/Loader.jsx";

// See README "OAuth redirect" section for why this route exists:
// SecurityConfig's OAuth2AuthenticationSuccessHandler was extended (the
// ONE backend change this UC needed) to redirect here with the JWT + user
// claims as query params instead of writing raw JSON to the response body.
export default function OAuthRedirect() {
  const [params] = useSearchParams();
  const { completeLogin } = useAuth();
  const navigate = useNavigate();
  const [error, setError] = useState(null);
  const handled = useRef(false);

  useEffect(() => {
    if (handled.current) return;
    handled.current = true;

    const token = params.get("token");
    const name = params.get("name");
    const email = params.get("email");
    const picture = params.get("picture");

    if (!token || !email) {
      setError("Google sign-in did not return a valid session. Please try again.");
      return;
    }

    completeLogin({ token, name, email, picture });
    navigate("/dashboard", { replace: true });
  }, [params, completeLogin, navigate]);

  if (error) {
    return (
      <div className="page" style={{ paddingTop: 120, textAlign: "center" }}>
        <p style={{ color: "var(--danger)", marginBottom: 16 }}>{error}</p>
        <a className="btn btn-secondary" href="/login">
          Back to login
        </a>
      </div>
    );
  }

  return <Loader label="Finishing sign-in…" />;
}
