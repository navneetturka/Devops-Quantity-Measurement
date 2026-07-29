import { Link } from "react-router-dom";
import { useHistory } from "../../hooks/useHistory";
import { formatNumber } from "../../utils/units";
import { formatDate, formatTime } from "../../utils/datetime";
import EmptyState from "../common/EmptyState.jsx";
import { SkeletonBlock } from "../common/Skeleton.jsx";
import { FiClock } from "react-icons/fi";
import "./RecentActivity.css";

const OP_BADGE = {
  add: "badge-add",
  subtract: "badge-subtract",
  divide: "badge-divide",
  convert: "badge-convert",
  compare: "badge-compare",
};

// Reads from the same backend-persisted history as the History page, so
// the two can never show a different number of operations.
export default function RecentActivity({ limit = 6 }) {
  const { rows: allRows, loading, loaded } = useHistory();
  const rows = allRows.slice(0, limit);

  return (
    <div className="card recent-activity">
      <div className="recent-activity-header">
        <p className="recent-activity-title">Recent Activity</p>
        <Link to="/history" className="recent-activity-link">
          View all
        </Link>
      </div>

      {loading && !loaded && (
        <div className="recent-activity-list">
          {Array.from({ length: 4 }).map((_, i) => (
            <SkeletonBlock key={i} height={38} />
          ))}
        </div>
      )}

      {loaded && rows.length === 0 && (
        <EmptyState
          icon={<FiClock />}
          title="No activity yet"
          description="Run your first measurement above to see it here."
        />
      )}

      {rows.length > 0 && (
        <div className="recent-activity-list">
          {rows.map((row) => (
            <div key={row.id} className="recent-activity-row">
              <span className={`badge ${OP_BADGE[row.operation] || "badge-compare"}`}>{row.operation}</span>
              <span className="recent-activity-detail mono">
                {formatNumber(row.thisValue)} {row.thisUnit}
                {row.thatValue !== undefined && row.thatValue !== null && (
                  <>
                    {" "}
                    &amp; {formatNumber(row.thatValue)} {row.thatUnit}
                  </>
                )}
                {" → "}
                {row.resultString || `${formatNumber(row.resultValue)} ${row.resultUnit || ""}`.trim()}
              </span>
              <span className="recent-activity-time">
                <span>{formatDate(row.createdAt)}</span>
                <span>{formatTime(row.createdAt)}</span>
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
