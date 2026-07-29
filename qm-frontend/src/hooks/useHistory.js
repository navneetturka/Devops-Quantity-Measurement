import { useContext } from "react";
import { HistoryContext } from "../context/HistoryContext.jsx";

export function useHistory() {
  const ctx = useContext(HistoryContext);
  if (!ctx) {
    throw new Error("useHistory must be used within a HistoryProvider");
  }
  return ctx;
}
