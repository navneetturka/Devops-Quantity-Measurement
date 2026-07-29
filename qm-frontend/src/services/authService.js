import api, { API_BASE_URL, TOKEN_KEY, USER_KEY } from "./api";

// Kicks off the existing backend flow: GET /api/auth/login redirects to
// /oauth2/authorization/google. This is a full browser navigation (not an
// axios call) because OAuth2 login can't happen inside an XHR.
export function startGoogleLogin() {
  window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
}

export function saveSession({ token, name, email, picture }) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, JSON.stringify({ name, email, picture }));
}

export function getStoredUser() {
  const raw = localStorage.getItem(USER_KEY);
  return raw ? JSON.parse(raw) : null;
}

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

// GET /api/auth/me — confirms the token is still valid and refreshes the
// cached profile (name/email/picture come straight from the JWT claims).
export async function fetchCurrentUser() {
  const { data } = await api.get("/api/auth/me");
  return data;
}
