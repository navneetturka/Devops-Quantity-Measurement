import { FiInbox, FiTrash2 } from "react-icons/fi";
import { formatNumber } from "../../utils/units";
import { SkeletonTableRows } from "../common/Skeleton.jsx";
import EmptyState from "../common/EmptyState.jsx";
import "./HistoryTable.css";

const OP_BADGE = {
  add: "badge-add",
  subtract: "badge-subtract",
  divide: "badge-divide",
  convert: "badge-convert",
  compare: "badge-compare",
};

function formatDate(value) {
  if (!value) return "—";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "—";
  return d.toLocaleString(undefined, {
    month: "short",
    day: "numeric",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function HistoryTable({ rows, loading, onDeleteRow }) {
  if (!loading && rows.length === 0) {
    return (
      <EmptyState
        icon={<FiInbox />}
        title="No history found"
        description="Once you run a measurement, it will show up here — searchable and filterable."
      />
    );
  }

  return (
    <div className="history-table-wrap">
      <table className="history-table">
        <thead>
          <tr>
            <th>Operation</th>
            <th>Type</th>
            <th>First Quantity</th>
            <th>Second Quantity</th>
            <th>Result</th>
            <th>Date</th>
            <th>Status</th>
            <th aria-label="Actions"></th>
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <SkeletonTableRows rows={6} columns={8} />
          ) : (
            rows.map((row) => (
              <tr key={row.id}>
                <td>
                  <span className={`badge ${OP_BADGE[row.operation] || "badge-compare"}`}>{row.operation}</span>
                </td>
                <td className="mono">{row.thisMeasurementType?.replace("Unit", "")}</td>
                <td className="mono">
                  {formatNumber(row.thisValue)} {row.thisUnit}
                </td>
                <td className="mono">
                  {row.thatValue !== undefined && row.thatValue !== null
                    ? `${formatNumber(row.thatValue)} ${row.thatUnit || ""}`
                    : "—"}
                </td>
                <td className="mono">
                  {row.resultString || `${formatNumber(row.resultValue)} ${row.resultUnit || ""}`.trim()}
                </td>
                <td className="mono history-date">{formatDate(row.createdAt)}</td>
                <td>
                  {row.error ? (
                    <span className="badge badge-error">Error</span>
                  ) : (
                    <span className="badge badge-success">Success</span>
                  )}
                </td>
                <td className="history-row-actions">
                  <button
                    type="button"
                    className="btn btn-ghost btn-icon history-delete-btn"
                    aria-label="Delete this record"
                    title="Delete this record"
                    onClick={() => onDeleteRow?.(row)}
                  >
                    <FiTrash2 size={15} />
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
