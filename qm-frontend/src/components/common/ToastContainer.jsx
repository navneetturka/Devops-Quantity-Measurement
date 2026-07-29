import { FiCheckCircle, FiAlertCircle, FiInfo, FiX } from "react-icons/fi";
import { useToast } from "../../hooks/useToast";
import "./ToastContainer.css";

const ICONS = {
  success: <FiCheckCircle />,
  error: <FiAlertCircle />,
  info: <FiInfo />,
};

export default function ToastContainer() {
  const { toasts, dismiss } = useToast();

  if (toasts.length === 0) return null;

  return (
    <div className="toast-container" role="region" aria-live="polite" aria-label="Notifications">
      {toasts.map((t) => (
        <div key={t.id} className={`toast toast-${t.type}`} role="status">
          <span className="toast-icon">{ICONS[t.type] || ICONS.info}</span>
          <span className="toast-message">{t.message}</span>
          <button
            className="toast-close"
            onClick={() => dismiss(t.id)}
            aria-label="Dismiss notification"
          >
            <FiX />
          </button>
        </div>
      ))}
    </div>
  );
}
