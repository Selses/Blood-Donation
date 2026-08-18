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
  TrendingUp
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
      // 1. Fetch Blood Requests
      const requests = await bloodRequestService.getAllRequests().catch(() => []);
      
      // 2. Fetch Notifications
      const notifs = await notificationService.getMyNotifications().catch(() => []);
      const unreadCountData = await notificationService.getUnreadCount().catch(() => ({ count: 0 }));

      // 3. Fetch Inventory (if organization or admin)
      let inventory = [];
      if (['HOSPITAL', 'BLOOD_BANK', 'ADMIN'].includes(user?.role)) {
        inventory = await inventoryService.getMyInventory().catch(() => []);
      }

      // Calculate aggregated metrics from real API data
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

      setRecentRequests(requests.slice(0, 5));
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
      <div className="container py-5 text-center">
        <div className="card loading-card">
          <Activity className="animate-spin text-primary mx-auto mb-3" size={32} />
          <h2>Loading your dashboard...</h2>
          <p className="text-muted">Fetching real-time blood network data</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container dashboard-page">
      <div className="welcome-banner card">
        <div className="welcome-text">
          <h1>Welcome back, <span className="text-primary">{user?.name || user?.email}</span></h1>
          <p className="role-description">
            Role: <strong>{user?.role}</strong> &bull; Connected to Supabase PostgreSQL Network
          </p>
        </div>
        {(user?.role === 'RECIPIENT' || user?.role === 'HOSPITAL' || user?.role === 'ADMIN') && (
          <Link to="/blood-requests/new" className="btn btn-primary">
            <PlusCircle size={18} />
            <span>Create Blood Request</span>
          </Link>
        )}
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {/* Stats Grid */}
      <div className="stats-grid">
        <div className="card stat-card">
          <div className="stat-icon stat-icon-red">
            <Activity size={24} />
          </div>
          <div className="stat-content">
            <div className="stat-value">{stats.totalRequests}</div>
            <div className="stat-label">Blood Requests</div>
          </div>
        </div>

        <div className="card stat-card">
          <div className="stat-icon stat-icon-amber">
            <AlertTriangle size={24} />
          </div>
          <div className="stat-content">
            <div className="stat-value">{stats.pendingRequests + stats.matchedRequests}</div>
            <div className="stat-label">Active / Pending</div>
          </div>
        </div>

        <div className="card stat-card">
          <div className="stat-icon stat-icon-emerald">
            <CheckCircle2 size={24} />
          </div>
          <div className="stat-content">
            <div className="stat-value">{stats.acceptedRequests}</div>
            <div className="stat-label">Accepted Matches</div>
          </div>
        </div>

        <div className="card stat-card">
          <div className="stat-icon stat-icon-blue">
            <Bell size={24} />
          </div>
          <div className="stat-content">
            <div className="stat-value">{stats.unreadNotifications}</div>
            <div className="stat-label">Unread Notifications</div>
          </div>
        </div>

        {['HOSPITAL', 'BLOOD_BANK', 'ADMIN'].includes(user?.role) && (
          <div className="card stat-card">
            <div className="stat-icon stat-icon-purple">
              <Package size={24} />
            </div>
            <div className="stat-content">
              <div className="stat-value">{stats.totalStockUnits}</div>
              <div className="stat-label">Inventory Units Available</div>
            </div>
          </div>
        )}
      </div>

      {/* Dashboard Main Grid */}
      <div className="dashboard-content-grid">
        {/* Recent Requests Section */}
        <div className="card content-card">
          <div className="card-header">
            <h3>Recent Blood Requests</h3>
            <Link to="/blood-requests" className="header-link">View All <ArrowRight size={14} /></Link>
          </div>

          {recentRequests.length === 0 ? (
            <div className="empty-state">No blood requests found.</div>
          ) : (
            <div className="table-responsive">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Patient</th>
                    <th>Group</th>
                    <th>Hospital</th>
                    <th>Urgency</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {recentRequests.map((req) => (
                    <tr key={req.id}>
                      <td className="font-bold">{req.recipientName}</td>
                      <td><span className="blood-group-tag">{req.bloodGroup}</span></td>
                      <td className="text-muted">{req.hospitalName}</td>
                      <td>{getUrgencyBadge(req.urgency)}</td>
                      <td>{getStatusBadge(req.status)}</td>
                      <td>
                        <Link to={`/blood-requests/${req.id}`} className="btn-table">Details</Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Notifications Sidebar */}
        <div className="card content-card">
          <div className="card-header">
            <h3>Recent Notifications</h3>
            <Link to="/notifications" className="header-link">View All <ArrowRight size={14} /></Link>
          </div>

          {recentNotifications.length === 0 ? (
            <div className="empty-state">No notifications.</div>
          ) : (
            <div className="recent-notif-list">
              {recentNotifications.map((n) => (
                <div key={n.id} className={`recent-notif-item ${!n.read ? 'unread' : ''}`}>
                  <div className="notif-title-row">
                    <span className="notif-title">{n.title}</span>
                    <span className="notif-type-tag">{n.type}</span>
                  </div>
                  <p className="notif-message">{n.message}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <style>{`
        .dashboard-page {
          padding-top: 1.5rem;
          padding-bottom: 3rem;
        }
        .welcome-banner {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 1.5rem;
          background: linear-gradient(135deg, rgba(30, 41, 59, 0.9), rgba(15, 23, 42, 0.9));
          border-left: 4px solid var(--primary);
        }
        .welcome-text h1 {
          font-size: 1.5rem;
          font-weight: 800;
          margin-bottom: 0.25rem;
        }
        .text-primary { color: var(--primary); }
        .role-description {
          color: var(--text-muted);
          font-size: 0.875rem;
        }
        .stats-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
          gap: 1rem;
          margin-bottom: 1.5rem;
        }
        .stat-card {
          display: flex;
          align-items: center;
          gap: 1rem;
          padding: 1.25rem;
        }
        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: var(--radius-md);
          display: flex;
          align-items: center;
          justify-content: center;
        }
        .stat-icon-red { background: rgba(225, 29, 72, 0.15); color: #f43f5e; }
        .stat-icon-amber { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
        .stat-icon-emerald { background: rgba(16, 185, 129, 0.15); color: #10b981; }
        .stat-icon-blue { background: rgba(59, 130, 246, 0.15); color: #3b82f6; }
        .stat-icon-purple { background: rgba(168, 85, 247, 0.15); color: #c084fc; }
        .stat-value {
          font-size: 1.5rem;
          font-weight: 800;
        }
        .stat-label {
          font-size: 0.8125rem;
          color: var(--text-muted);
          font-weight: 500;
        }
        .dashboard-content-grid {
          display: grid;
          grid-template-columns: 2fr 1fr;
          gap: 1.5rem;
        }
        .card-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 1rem;
          padding-bottom: 0.75rem;
          border-bottom: 1px solid var(--border);
        }
        .card-header h3 {
          font-size: 1.125rem;
          font-weight: 700;
        }
        .header-link {
          font-size: 0.8125rem;
          color: var(--primary);
          font-weight: 600;
          display: flex;
          align-items: center;
          gap: 0.25rem;
        }
        .header-link:hover { text-decoration: underline; }
        .custom-table {
          width: 100%;
          border-collapse: collapse;
          text-align: left;
          font-size: 0.875rem;
        }
        .custom-table th {
          padding: 0.625rem 0.75rem;
          color: var(--text-dim);
          font-size: 0.75rem;
          text-transform: uppercase;
          border-bottom: 1px solid var(--border);
        }
        .custom-table td {
          padding: 0.75rem;
          border-bottom: 1px solid rgba(51, 65, 85, 0.5);
        }
        .blood-group-tag {
          display: inline-block;
          padding: 0.15rem 0.5rem;
          border-radius: var(--radius-sm);
          background: rgba(225, 29, 72, 0.15);
          color: #fb7185;
          font-weight: 800;
          font-size: 0.8125rem;
        }
        .btn-table {
          padding: 0.25rem 0.625rem;
          font-size: 0.75rem;
          background: #334155;
          color: white;
          border-radius: var(--radius-sm);
          font-weight: 600;
        }
        .btn-table:hover { background: var(--primary); }
        .recent-notif-item {
          padding: 0.75rem;
          border-radius: var(--radius-md);
          background: rgba(15, 23, 42, 0.6);
          margin-bottom: 0.5rem;
          border-left: 3px solid transparent;
        }
        .recent-notif-item.unread {
          border-left-color: var(--primary);
          background: rgba(225, 29, 72, 0.08);
        }
        .notif-title-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 0.25rem;
        }
        .notif-title {
          font-size: 0.8125rem;
          font-weight: 700;
        }
        .notif-type-tag {
          font-size: 0.6875rem;
          color: var(--text-dim);
        }
        .notif-message {
          font-size: 0.75rem;
          color: var(--text-muted);
          line-height: 1.4;
        }
        .empty-state {
          padding: 2rem 1rem;
          text-align: center;
          color: var(--text-dim);
          font-size: 0.875rem;
        }
        @media (max-width: 900px) {
          .dashboard-content-grid { grid-template-columns: 1fr; }
          .welcome-banner { flex-direction: column; align-items: flex-start; gap: 1rem; }
        }
      `}</style>
    </div>
  );
};

export default Dashboard;
