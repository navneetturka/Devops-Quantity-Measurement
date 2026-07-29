import { useEffect, useRef, useState } from "react";
import { FiChevronDown } from "react-icons/fi";
import "./Select.css";

// Native <select> popups are rendered by the OS/browser and can overflow
// their container on narrow viewports (the width is driven by the widest
// option text, not by the trigger). This component renders our own
// listbox instead, so the dropdown panel always matches the trigger's
// width and stays inside the page on every screen size.
export default function Select({
  id,
  value,
  onChange,
  onBlur,
  options,
  ariaLabel,
  invalid = false,
  disabled = false,
}) {
  const [open, setOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);
  const wrapRef = useRef(null);
  const listRef = useRef(null);

  const normalized = options.map((o) => (typeof o === "string" ? { value: o, label: o } : o));
  const selected = normalized.find((o) => o.value === value);

  useEffect(() => {
    if (!open) return;
    function onPointerDown(e) {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) {
        setOpen(false);
        onBlur?.();
      }
    }
    document.addEventListener("mousedown", onPointerDown);
    return () => document.removeEventListener("mousedown", onPointerDown);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  useEffect(() => {
    if (open) {
      setActiveIndex(Math.max(0, normalized.findIndex((o) => o.value === value)));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  useEffect(() => {
    if (open && listRef.current) {
      const activeEl = listRef.current.children[activeIndex];
      activeEl?.scrollIntoView({ block: "nearest" });
    }
  }, [open, activeIndex]);

  function toggleOpen() {
    if (disabled) return;
    setOpen((v) => {
      const next = !v;
      if (!next) onBlur?.();
      return next;
    });
  }

  function selectValue(v) {
    onChange(v);
    setOpen(false);
    onBlur?.();
  }

  function onKeyDown(e) {
    if (disabled) return;
    if (!open) {
      if (e.key === "Enter" || e.key === " " || e.key === "ArrowDown" || e.key === "ArrowUp") {
        e.preventDefault();
        setOpen(true);
      }
      return;
    }
    if (e.key === "Escape") {
      e.preventDefault();
      setOpen(false);
      onBlur?.();
    } else if (e.key === "ArrowDown") {
      e.preventDefault();
      setActiveIndex((i) => Math.min(normalized.length - 1, i + 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActiveIndex((i) => Math.max(0, i - 1));
    } else if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      if (normalized[activeIndex]) selectValue(normalized[activeIndex].value);
    }
  }

  return (
    <div className="qm-select" ref={wrapRef}>
      <button
        type="button"
        id={id}
        className={"qm-select-trigger" + (invalid ? " field-invalid" : "") + (open ? " qm-select-open" : "")}
        onClick={toggleOpen}
        onKeyDown={onKeyDown}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-label={ariaLabel}
        disabled={disabled}
      >
        <span className="qm-select-value">{selected ? selected.label : "Select…"}</span>
        <FiChevronDown className={"qm-select-chevron" + (open ? " qm-select-chevron-open" : "")} size={16} />
      </button>

      {open && (
        <ul className="qm-select-panel" role="listbox" ref={listRef} aria-label={ariaLabel}>
          {normalized.map((o, i) => (
            <li
              key={o.value}
              role="option"
              aria-selected={o.value === value}
              className={
                "qm-select-option" +
                (o.value === value ? " qm-select-option-selected" : "") +
                (i === activeIndex ? " qm-select-option-active" : "")
              }
              onMouseEnter={() => setActiveIndex(i)}
              onClick={() => selectValue(o.value)}
            >
              {o.label}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
