import { useCallback, useRef, useState } from "react";

// Encapsulates the "copy + show ✓ Copied for 2s" behavior so any component
// (MeasurementCard today, others later) can reuse it identically.
export function useClipboard(resetDelay = 2000) {
  const [copied, setCopied] = useState(false);
  const timerRef = useRef(null);

  const copy = useCallback(
    async (text) => {
      try {
        await navigator.clipboard.writeText(String(text));
        setCopied(true);
        clearTimeout(timerRef.current);
        timerRef.current = setTimeout(() => setCopied(false), resetDelay);
        return true;
      } catch {
        return false;
      }
    },
    [resetDelay]
  );

  return { copied, copy };
}
