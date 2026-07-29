import { createContext, useEffect, useState, useCallback } from "react";
import {
  getStoredToken,
  getStoredUser,
  saveSession,
  clearSession,
  startGoogleLogin,
} from "../services/authService";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getStoredToken());
  const [user, setUser] = useState(getStoredUser());
  const [initializing, setInitializing] = useState(true);

  useEffect(() => {
    // Session is read synchronously from localStorage on boot — no network
    // round trip needed before the first paint.
    setInitializing(false);
  }, []);

  const login = useCallback(() => {
    startGoogleLogin();
  }, []);

  // Called by the OAuth redirect handler once the backend hands back a
  // token (see pages/OAuthRedirect.jsx for why this step exists).
  const completeLogin = useCallback(({ token: newToken, name, email, picture }) => {
    saveSession({ token: newToken, name, email, picture });
    setToken(newToken);
    setUser({ name, email, picture });
  }, []);

  const logout = useCallback(() => {
    clearSession();
    setToken(null);
    setUser(null);
  }, []);

  const value = {
    token,
    user,
    isAuthenticated: Boolean(token),
    initializing,
    login,
    completeLogin,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
