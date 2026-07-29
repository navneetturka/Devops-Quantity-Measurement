import { createContext, useCallback, useEffect, useState } from "react";
import { quantityService } from "../services/quantityService";
import { useAuth } from "../hooks/useAuth";

export const HistoryContext = createContext(null);

// Single source of truth for operation history, backed entirely by the
// server (H2, persisted to disk — survives logout/restart). Both the
// Dashboard's "Recent Activity" panel and the History page read from the
// same `rows` array, so the two can never disagree on count again.
export function HistoryProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loaded, setLoaded] = useState(false);

  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      const data = await quantityService.getAllHistory();
      setRows(data);
      setLoaded(true);
      return data;
    } finally {
      setLoading(false);
    }
  }, []);

  // Load once per authenticated session; clear local state on logout
  // (the data itself stays safe in the database).
  useEffect(() => {
    if (isAuthenticated) {
      refresh();
    } else {
      setRows([]);
      setLoaded(false);
    }
  }, [isAuthenticated, refresh]);

  const removeEntry = useCallback(async (id) => {
    await quantityService.deleteHistoryEntry(id);
    setRows((prev) => prev.filter((r) => r.id !== id));
  }, []);

  const clearAll = useCallback(async () => {
    await quantityService.clearAllHistory();
    setRows([]);
  }, []);

  const value = { rows, loading, loaded, refresh, removeEntry, clearAll };

  return <HistoryContext.Provider value={value}>{children}</HistoryContext.Provider>;
}
