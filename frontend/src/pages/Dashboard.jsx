import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { bloodRequestService } from '../services/bloodRequestService';
import { notificationService } from '../services/notificationService';
import { inventoryService } from '../services/inventoryService';
import { 
  Activity, 
  Heart, 
  Package, 
  Bell, 
  PlusCircle, 
  AlertTriangle, 
  CheckCircle2, 
  ArrowRight,
  TrendingUp,
  ShieldCheck,
  Droplets,
  Clock
} from 'lucide-react';

const Dashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalRequests: 0,
    pendingRequests: 0,
    matchedRequests: 0,
    acceptedRequests: 0,
    fulfilledRequests: 0,
    unreadNotifications: 0,
    inventoryCount: 0,
    totalStockUnits: 0
  });
  const [recentRequests, setRecentRequests] = useState([]);
  const [recentNotifications, setRecentNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const requests = await bloodRequestService.getAllRequests().catch(() => []);
      const notifs = await notificationService.getMyNotifications().catch(() => []);
      const unreadCountData = await notificationService.getUnreadCount().catch(() => ({ count: 0 }));

      let inventory = [];
      if (['HOSPITAL', 'BLOOD_BANK', 'ADMIN'].includes(user?.role)) {
        inventory = await inventoryService.getMyInventory().catch(() => []);
      }

      const pending = requests.filter(r => r.status === 'PENDING').length;
      const matched = requests.filter(r => r.status === 'MATCHED').length;
      const accepted = requests.filter(r => r.status === 'ACCEPTED').length;
      const fulfilled = requests.filter(r => r.status === 'FULFILLED').length;
      const totalUnits = inventory.reduce((sum, item) => sum + (item.availableUnits || 0), 0);

      setStats({
        totalRequests: requests.length,
        pendingRequests: pending,
        matchedRequests: matched,
        acceptedRequests: accepted,
        fulfilledRequests: fulfilled,
        unreadNotifications: unreadCountData.count || 0,
        inventoryCount: inventory.length,
        totalStockUnits: totalUnits
      });

      setRecentRequests(requests.slice(0, 6));
      setRecentNotifications(notifs.slice(0, 4));
    } catch (err) {
      setError(err.message || 'Failed to load dashboard data.');
    } finally {
      setLoading(false);
    }
  };

  const getUrgencyBadge = (urgency) => {
    switch (urgency?.toUpperCase()) {
      case 'CRITICAL': return <span className="badge badge-critical">CRITICAL</span>;
      case 'HIGH': return <span className="badge badge-high">HIGH</span>;
      case 'MEDIUM': return <span className="badge badge-medium">MEDIUM</span>;
      default: return <span className="badge badge-low">LOW</span>;
    }
  };

  const getStatusBadge = (status) => {
    switch (status?.toUpperCase()) {
      case 'PENDING': return <span className="badge badge-pending">PENDING</span>;
      case 'MATCHED': return <span className="badge badge-matched">MATCHED</span>;
      case 'ACCEPTED': return <span className="badge badge-accepted">ACCEPTED</span>;
      case 'FULFILLED': return <span className="badge badge-fulfilled">FULFILLED</span>;
      default: return <span className="badge badge-cancelled">{status}</span>;
    }
  };

  if (loading) {
    return (
      <div className="container py-5 text-center loading-wrapper">
        <div className="hemo-loader-sphere">
          <Activity className="animate-spin text-primary" size={36} />
        </div>
        <h2 className="loading-title">Syncing HEMO Network...</h2>
        <p className="loading-subtitle">Retrieving real-time blood allocation stream</p>
      </div>
    );
  }

  return (
    <div className="container dashboard-page">
      {/* Top Welcome Banner */}
      <div className="hemo-banner-card">
        <div className="banner-details">
          <div className="banner-badge">
            <span className="live-dot" />
            <span>HEMO OPERATIONAL CONSOLE</span>
          </div>
          <h1 className="banner-title">
            Welcome back, <span className="highlight-text">{user?.name || user?.email}</span>
          </h1>
          <p className="banner-meta">
            Role: <strong>{user?.role}</strong> &bull; Supabase PostgreSQL Live Sync &bull; Secure Protocol
          </p>
        </div>

        {(user?.role === 'RECIPIENT' || user?.role === 'HOSPITAL' || user?.role === 'ADMIN') && (
          <Link to="/blood-requests/new" className="btn btn-primary btn-banner-cta">
            <PlusCircle size={18} />
            <span>Create Blood Request</span>
          </Link>
        )}
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Metrics Grid */}
      <div className="hemo-metrics-grid">
        <div className="metric-box-card">
          <div className="metric-box-top">
            <span className="metric-box-label">TOTAL REQUESTS</span>
            <div className="metric-box-icon red"><Activity size={18} /></div>
          </div>
          <div className="metric-box-val">{stats.totalRequests}</div>
          <div className="metric-box-sub">Registered across network</div>
        </div>

        <div className="metric-box-card">
          <div className="metric-box-top">
            <span className="metric-box-label">ACTIVE / PENDING</span>
            <div className="metric-box-icon amber"><Clock size={18} /></div>
          </div>
          <div className="metric-box-val">{stats.pendingRequests + stats.matchedRequests}</div>
          <div className="metric-box-sub">Seeking immediate match</div>
        </div>

        <div className="metric-box-card">
          <div className="metric-box-top">
            <span className="metric-box-label">ACCEPTED MATCHES</span>
            <div className="metric-box-icon emerald"><CheckCircle2 size={18} /></div>
          </div>
          <div className="metric-box-val">{stats.acceptedRequests}</div>
          <div className="metric-box-sub">Donors actively dispatched</div>
        </div>

        <div className="metric-box-card">
          <div className="metric-box-top">
            <span className="metric-box-label">NOTIFICATIONS</span>
            <div className="metric-box-icon blue"><Bell size={18} /></div>
          </div>
          <div className="metric-box-val">{stats.unreadNotifications}</div>
          <div className="metric-box-sub">Unread urgent alerts</div>
        </div>

        {['HOSPITAL', 'BLOOD_BANK', 'ADMIN'].includes(user?.role) && (
          <div className="metric-box-card">
            <div className="metric-box-top">
              <span className="metric-box-label">INVENTORY UNITS</span>
              <div className="metric-box-icon purple"><Package size={18} /></div>
            </div>
            <div className="metric-box-val">{stats.totalStockUnits}</div>
            <div className="metric-box-sub">Units available in reserve</div>
          </div>
        )}
      </div>

      {/* Main Content Grid */}
      <div className="dashboard-content-split">
        {/* Left Column: Recent Requests Table */}
        <div className="hemo-panel-card requests-panel">
          <div className="panel-header-row">
            <div>
              <h3 className="panel-title">Active Blood Requests</h3>
              <p className="panel-subtitle">Live emergency &amp; scheduled patient requisitions</p>
            </div>
            <Link to="/blood-requests" className="panel-action-link">
              <span>View All</span>
              <ArrowRight size={14} />
            </Link>
          </div>

          {recentRequests.length === 0 ? (
            <div className="panel-empty-state">No blood requests currently registered in system.</div>
          ) : (
            <div className="hemo-table-wrap">
              <table className="hemo-table">
                <thead>
                  <tr>
                    <th>PATIENT</th>
                    <th>BLOOD</th>
                    <th>HOSPITAL</th>
                    <th>URGENCY</th>
                    <th>STATUS</th>
                    <th>ACTION</th>
                  </tr>
                </thead>
                <tbody>
                  {recentRequests.map((req) => (
                    <tr key={req.id}>
                      <td className="patient-cell">{req.recipientName}</td>
                      <td>
                        <span className="hemo-blood-pill">{req.bloodGroup}</span>
                      </td>
                      <td className="hospital-cell">{req.hospitalName}</td>
                      <td>{getUrgencyBadge(req.urgency)}</td>
                      <td>{getStatusBadge(req.status)}</td>
                      <td>
                        <Link to={`/blood-requests/${req.id}`} className="btn-table-action">
                          Details
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Right Column: Notifications Feed */}
        <div className="hemo-panel-card notifs-panel">
          <div className="panel-header-row">
            <div>
              <h3 className="panel-title">Emergency Feed</h3>
              <p className="panel-subtitle">Real-time alerts &amp; match requests</p>
            </div>
            <Link to="/notifications" className="panel-action-link">
              <span>Feed</span>
              <ArrowRight size={14} />
            </Link>
          </div>

          {recentNotifications.length === 0 ? (
            <div className="panel-empty-state">No notifications to display.</div>
          ) : (
            <div className="notifs-feed-list">
              {recentNotifications.map((n) => (
                <div key={n.id} className={`notif-feed-item ${!n.read ? 'is-unread' : ''}`}>
                  <div className="notif-top">
                    <span className="notif-heading">{n.title}</span>
                    <span className="notif-tag">{n.type}</span>
                  </div>
                  <p className="notif-body-txt">{n.message}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <style>{`
        .dashboard-page {
          padding: 2.5rem 1.5rem 5rem;
        }

        .loading-wrapper {
          min-height: 60vh;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
        }

        .hemo-loader-sphere {
          width: 70px;
          height: 70px;
          border-radius: 50%;
          background: rgba(225, 29, 72, 0.15);
          display: flex;
          align-items: center;
          justify-content: center;
          margin-bottom: 1.5rem;
          box-shadow: 0 0 30px rgba(225, 29, 72, 0.3);
        }

        .loading-title {
          font-family: var(--font-heading);
          font-size: 1.8rem;
          color: #ffffff;
          margin-bottom: 0.5rem;
        }
        .loading-subtitle {
          color: var(--text-muted);
          font-size: 0.9rem;
        }

        .hemo-banner-card {
          background: linear-gradient(135deg, #14161c 0%, #0d0e12 100%);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-xl);
          padding: 2.25rem 2.5rem;
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 2rem;
          box-shadow: 0 20px 40px rgba(0, 0, 0, 0.6);
          flex-wrap: wrap;
          gap: 1.5rem;
        }

        .banner-badge {
          display: inline-flex;
          align-items: center;
          gap: 0.45rem;
          font-size: 0.72rem;
          font-weight: 800;
          letter-spacing: 0.1em;
          color: var(--primary);
          margin-bottom: 0.5rem;
        }
        .live-dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: var(--primary);
          box-shadow: 0 0 8px var(--primary);
        }

        .banner-title {
          font-family: var(--font-heading);
          font-size: clamp(1.6rem, 3vw, 2.2rem);
          font-weight: 800;
          color: #ffffff;
          margin-bottom: 0.35rem;
          letter-spacing: -0.02em;
        }
        .highlight-text { color: var(--primary); }

        .banner-meta {
          font-size: 0.85rem;
          color: var(--text-muted);
        }

        .btn-banner-cta {
          padding: 0.85rem 1.8rem;
          font-size: 0.92rem;
        }

        .hemo-metrics-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
          gap: 1.25rem;
          margin-bottom: 2rem;
        }

        .metric-box-card {
          background: #111318;
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-lg);
          padding: 1.5rem;
          transition: all 0.25s ease;
        }
        .metric-box-card:hover {
          border-color: rgba(255, 255, 255, 0.2);
          transform: translateY(-3px);
        }

        .metric-box-top {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 0.75rem;
        }

        .metric-box-label {
          font-size: 0.7rem;
          font-weight: 800;
          letter-spacing: 0.08em;
          color: var(--text-dim);
        }

        .metric-box-icon {
          width: 32px;
          height: 32px;
          border-radius: var(--radius-sm);
          display: flex;
          align-items: center;
          justify-content: center;
        }
        .metric-box-icon.red { background: rgba(225, 29, 72, 0.15); color: #ff4d6d; }
        .metric-box-icon.amber { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
        .metric-box-icon.emerald { background: rgba(16, 185, 129, 0.15); color: #10b981; }
        .metric-box-icon.blue { background: rgba(56, 189, 248, 0.15); color: #38bdf8; }
        .metric-box-icon.purple { background: rgba(192, 132, 252, 0.15); color: #c084fc; }

        .metric-box-val {
          font-family: var(--font-heading);
          font-size: 2.2rem;
          font-weight: 800;
          color: #ffffff;
          line-height: 1.1;
          margin-bottom: 0.35rem;
        }

        .metric-box-sub {
          font-size: 0.78rem;
          color: var(--text-muted);
        }

        .dashboard-content-split {
          display: grid;
          grid-template-columns: 2fr 1fr;
          gap: 1.75rem;
        }

        .hemo-panel-card {
          background: #111318;
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-xl);
          padding: 2rem;
        }

        .panel-header-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 1.5rem;
          padding-bottom: 1rem;
          border-bottom: 1px solid rgba(255, 255, 255, 0.06);
        }

        .panel-title {
          font-family: var(--font-heading);
          font-size: 1.25rem;
          font-weight: 800;
          color: #ffffff;
        }

        .panel-subtitle {
          font-size: 0.8rem;
          color: var(--text-dim);
        }

        .panel-action-link {
          display: inline-flex;
          align-items: center;
          gap: 0.35rem;
          font-size: 0.82rem;
          font-weight: 700;
          color: var(--primary);
        }
        .panel-action-link:hover { text-decoration: underline; }

        .hemo-table-wrap {
          overflow-x: auto;
        }

        .hemo-table {
          width: 100%;
          border-collapse: collapse;
          text-align: left;
        }

        .hemo-table th {
          padding: 0.75rem 0.85rem;
          font-size: 0.7rem;
          font-weight: 800;
          letter-spacing: 0.06em;
          color: var(--text-dim);
          border-bottom: 1px solid rgba(255, 255, 255, 0.08);
        }

        .hemo-table td {
          padding: 0.95rem 0.85rem;
          font-size: 0.88rem;
          border-bottom: 1px solid rgba(255, 255, 255, 0.04);
        }

        .patient-cell {
          font-weight: 700;
          color: #ffffff;
        }

        .hospital-cell {
          color: var(--text-muted);
          font-size: 0.84rem;
        }

        .hemo-blood-pill {
          display: inline-block;
          padding: 0.2rem 0.6rem;
          background: rgba(225, 29, 72, 0.15);
          color: #ff4d6d;
          border: 1px solid rgba(225, 29, 72, 0.3);
          border-radius: var(--radius-sm);
          font-size: 0.8rem;
          font-weight: 800;
        }

        .btn-table-action {
          padding: 0.35rem 0.75rem;
          background: rgba(255, 255, 255, 0.06);
          border: 1px solid rgba(255, 255, 255, 0.1);
          border-radius: var(--radius-full);
          font-size: 0.78rem;
          font-weight: 600;
          color: #ffffff;
          transition: all 0.2s;
        }
        .btn-table-action:hover {
          background: var(--primary);
          border-color: var(--primary);
        }

        .notifs-feed-list {
          display: flex;
          flex-direction: column;
          gap: 0.85rem;
        }

        .notif-feed-item {
          padding: 1rem;
          background: rgba(255, 255, 255, 0.02);
          border: 1px solid rgba(255, 255, 255, 0.06);
          border-radius: var(--radius-md);
          transition: all 0.2s;
        }

        .notif-feed-item.is-unread {
          border-left: 3px solid var(--primary);
          background: rgba(225, 29, 72, 0.05);
        }

        .notif-top {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 0.35rem;
        }

        .notif-heading {
          font-size: 0.85rem;
          font-weight: 700;
          color: #ffffff;
        }

        .notif-tag {
          font-size: 0.68rem;
          font-weight: 700;
          color: var(--text-dim);
          text-transform: uppercase;
        }

        .notif-body-txt {
          font-size: 0.8rem;
          color: var(--text-muted);
          line-height: 1.45;
        }

        .panel-empty-state {
          padding: 2.5rem 1rem;
          text-align: center;
          color: var(--text-dim);
          font-size: 0.88rem;
        }

        @media (max-width: 950px) {
          .dashboard-content-split { grid-template-columns: 1fr; }
          .hemo-banner-card { flex-direction: column; align-items: flex-start; }
        }
      `}</style>
    </div>
  );
};

export default Dashboard;
