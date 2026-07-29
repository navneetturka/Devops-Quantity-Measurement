import { useEffect } from "react";
import { FiAlertTriangle } from "react-icons/fi";
import "./ConfirmDialog.css";

// Minimal, on-brand confirmation modal — used instead of window.confirm()
// for destructive actions (delete one record / clear all history).
export default function ConfirmDialog({
  open,
  title,
  description,
  confirmLabel = "Delete",
  cancelLabel = "Cancel",
  danger = true,
  busy = false,
  onConfirm,
  onCancel,
}) {
  useEffect(() => {
    if (!open) return;
    function onKeyDown(e) {
      if (e.key === "Escape") onCancel?.();
    }
    document.addEventListener("keydown", onKeyDown);
    return () => document.removeEventListener("keydown", onKeyDown);
  }, [open, onCancel]);

  if (!open) return null;

  return (
    <div className="confirm-overlay" role="presentation" onMouseDown={onCancel}>
      <div
        className="confirm-dialog card"
        role="alertdialog"
        aria-modal="true"
        aria-labelledby="confirm-dialog-title"
        onMouseDown={(e) => e.stopPropagation()}
      >
        <div className="confirm-dialog-icon">
          <FiAlertTriangle />
        </div>
        <h3 id="confirm-dialog-title">{title}</h3>
        {description && <p>{description}</p>}
        <div className="confirm-dialog-actions">
          <button type="button" className="btn btn-secondary" onClick={onCancel} disabled={busy}>
            {cancelLabel}
          </button>
          <button
            type="button"
            className={"btn " + (danger ? "btn-danger" : "btn-primary")}
            onClick={onConfirm}
            disabled={busy}
          >
            {busy ? "Please wait…" : confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}
