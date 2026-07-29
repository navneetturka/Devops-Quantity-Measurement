import { useEffect, useMemo, useState } from "react";
import { FiSearch, FiTrash2 } from "react-icons/fi";
import { useToast } from "../hooks/useToast";
import { useHistory } from "../hooks/useHistory";
import { MEASUREMENT_TYPES } from "../utils/units";
import HistoryTable from "../components/history/HistoryTable.jsx";
import ConfirmDialog from "../components/common/ConfirmDialog.jsx";
import "./History.css";

const PAGE_SIZE = 8;

export default function History() {
  const toast = useToast();
  const { rows, loading, loaded, refresh, removeEntry, clearAll } = useHistory();

  const [search, setSearch] = useState("");
  const [operationFilter, setOperationFilter] = useState("ALL");
  const [typeFilter, setTypeFilter] = useState("ALL");
  const [page, setPage] = useState(1);

  const [rowPendingDelete, setRowPendingDelete] = useState(null);
  const [clearAllOpen, setClearAllOpen] = useState(false);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    if (!loaded) {
      refresh().catch(() => toast.error("Could not load history from the server."));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function confirmDeleteRow() {
    if (!rowPendingDelete) return;
    setBusy(true);
    try {
      await removeEntry(rowPendingDelete.id);
      toast.success("History record deleted.");
      setRowPendingDelete(null);
    } catch {
      toast.error("Could not delete that record. Please try again.");
    } finally {
      setBusy(false);
    }
  }

  async function confirmClearAll() {
    setBusy(true);
    try {
      await clearAll();
      toast.success("All history has been cleared.");
      setClearAllOpen(false);
      setPage(1);
    } catch {
      toast.error("Could not clear history. Please try again.");
    } finally {
      setBusy(false);
    }
  }

  const filtered = useMemo(() => {
    return rows.filter((r) => {
      if (operationFilter !== "ALL" && r.operation !== operationFilter.toLowerCase()) return false;
      if (typeFilter !== "ALL" && r.thisMeasurementType !== typeFilter) return false;
      if (search.trim()) {
        const needle = search.trim().toLowerCase();
        const haystack = `${r.thisUnit} ${r.thatUnit} ${r.resultUnit} ${r.operation}`.toLowerCase();
        if (!haystack.includes(needle)) return false;
      }
      return true;
    });
  }, [rows, search, operationFilter, typeFilter]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const pageRows = filtered.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

  useEffect(() => {
    setPage(1);
  }, [search, operationFilter, typeFilter]);

  return (
    <div className="page history-page">
      <div className="history-header">
        <div>
          <h1>Operation History</h1>
          <p className="history-sub">Every compare, convert, add, subtract and divide you've run.</p>
        </div>
        <button
          type="button"
          className="btn btn-secondary history-clear-btn"
          onClick={() => setClearAllOpen(true)}
          disabled={rows.length === 0}
        >
          <FiTrash2 /> Clear history
        </button>
      </div>

      <div className="history-controls card">
        <div className="history-search">
          <FiSearch />
          <input
            type="text"
            placeholder="Search by unit or operation…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            aria-label="Search history"
          />
        </div>

        <select
          value={operationFilter}
          onChange={(e) => setOperationFilter(e.target.value)}
          aria-label="Filter by operation"
        >
          <option value="ALL">All operations</option>
          <option value="ADD">Add</option>
          <option value="SUBTRACT">Subtract</option>
          <option value="DIVIDE">Divide</option>
          <option value="CONVERT">Convert</option>
          <option value="COMPARE">Compare</option>
        </select>

        <select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)} aria-label="Filter by type">
          <option value="ALL">All types</option>
          {MEASUREMENT_TYPES.map((t) => (
            <option key={t.key} value={t.key}>
              {t.label}
            </option>
          ))}
        </select>
      </div>

      <HistoryTable rows={pageRows} loading={loading && !loaded} onDeleteRow={setRowPendingDelete} />

      {!loading && totalPages > 1 && (
        <div className="history-pagination">
          <button
            className="btn btn-ghost"
            disabled={page === 1}
            onClick={() => setPage((p) => Math.max(1, p - 1))}
          >
            Previous
          </button>
          <span className="mono history-page-label">
            Page {page} of {totalPages}
          </span>
          <button
            className="btn btn-ghost"
            disabled={page === totalPages}
            onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
          >
            Next
          </button>
        </div>
      )}

      <ConfirmDialog
        open={Boolean(rowPendingDelete)}
        title="Delete this history record?"
        description="This will permanently remove this operation from your history. This action cannot be undone."
        confirmLabel="Delete"
        busy={busy}
        onConfirm={confirmDeleteRow}
        onCancel={() => setRowPendingDelete(null)}
      />

      <ConfirmDialog
        open={clearAllOpen}
        title="Clear all history?"
        description="This will permanently remove every operation record. This action cannot be undone."
        confirmLabel="Clear all"
        busy={busy}
        onConfirm={confirmClearAll}
        onCancel={() => setClearAllOpen(false)}
      />
    </div>
  );
}
