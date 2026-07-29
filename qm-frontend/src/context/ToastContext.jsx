import { createContext, useCallback, useRef, useState } from "react";

export const ToastContext = createContext(null);

let idCounter = 0;

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const timers = useRef({});

  const dismiss = useCallback((id) => {
    setToasts((list) => list.filter((t) => t.id !== id));
    clearTimeout(timers.current[id]);
    delete timers.current[id];
  }, []);

  const showToast = useCallback(
    (message, { type = "info", duration = 3200 } = {}) => {
      const id = ++idCounter;
      setToasts((list) => [...list, { id, message, type }]);
      timers.current[id] = setTimeout(() => dismiss(id), duration);
      return id;
    },
    [dismiss]
  );

  const value = {
    toasts,
    showToast,
    success: (msg, opts) => showToast(msg, { ...opts, type: "success" }),
    error: (msg, opts) => showToast(msg, { ...opts, type: "error" }),
    info: (msg, opts) => showToast(msg, { ...opts, type: "info" }),
    dismiss,
  };

  return <ToastContext.Provider value={value}>{children}</ToastContext.Provider>;
}
