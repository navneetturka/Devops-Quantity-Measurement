import RecentActivity from "../components/dashboard/RecentActivity.jsx";
import MeasurementCard from "../components/dashboard/MeasurementCard.jsx";
import "./Dashboard.css";

export default function Dashboard() {
  return (
    <div className="page dashboard">
      <div className="dashboard-stack">
        <MeasurementCard />
        <RecentActivity limit={5} />
      </div>
    </div>
  );
}
