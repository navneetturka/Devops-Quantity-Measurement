import "./Skeleton.css";

export function SkeletonLine({ width = "100%", height = 14 }) {
  return <span className="skeleton skeleton-line" style={{ width, height }} />;
}

export function SkeletonBlock({ width = "100%", height = 60 }) {
  return <span className="skeleton skeleton-block" style={{ width, height }} />;
}

export function SkeletonTableRows({ rows = 5, columns = 6 }) {
  return (
    <>
      {Array.from({ length: rows }).map((_, r) => (
        <tr key={r} className="skeleton-row">
          {Array.from({ length: columns }).map((__, c) => (
            <td key={c}>
              <SkeletonLine width={c === 0 ? "70%" : "85%"} />
            </td>
          ))}
        </tr>
      ))}
    </>
  );
}
