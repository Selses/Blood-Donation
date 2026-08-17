import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { notificationService } from '../services/notificationService';
import { 
  HeartHandshake, 
  LayoutDashboard, 
  Activity, 
  PlusCircle, 
  Package, 
  Bell, 
  LogOut, 
  User as UserIcon,
  Menu,
  X
} from 'lucide-react';

const Navbar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [unreadCount, setUnreadCount] = useState(0);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    if (isAuthenticated) {
      fetchUnreadCount();
      const interval = setInterval(fetchUnreadCount, 15000); // refresh every 15s
      return () => clearInterval(interval);
    }
  }, [isAuthenticated, location.pathname]);

  const fetchUnreadCount = async () => {
    try {
      const data = await notificationService.getUnreadCount();
      setUnreadCount(data.count || 0);
    } catch (err) {
      // Ignore count fetch errors gracefully
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!isAuthenticated) return null;

  const role = user?.role;

  const navLinkClass = (path) => {
    const isActive = location.pathname === path;
    return `nav-link ${isActive ? 'active' : ''}`;
  };

  return (
    <header className="main-navbar">
      <div className="nav-container">
        <Link to="/dashboard" className="nav-brand">
          <div className="brand-icon">
            <HeartHandshake size={24} color="#e11d48" />
          </div>
          <div className="brand-text">
            <span>Blood</span>Connect
          </div>
        </Link>

        <nav className={`nav-menu ${mobileMenuOpen ? 'open' : ''}`}>
          <Link to="/dashboard" className={navLinkClass('/dashboard')} onClick={() => setMobileMenuOpen(false)}>
            <LayoutDashboard size={18} />
            <span>Dashboard</span>
          </Link>

          <Link to="/blood-requests" className={navLinkClass('/blood-requests')} onClick={() => setMobileMenuOpen(false)}>
            <Activity size={18} />
            <span>Blood Requests</span>
          </Link>

          {(role === 'RECIPIENT' || role === 'HOSPITAL' || role === 'ADMIN') && (
            <Link to="/blood-requests/new" className={navLinkClass('/blood-requests/new')} onClick={() => setMobileMenuOpen(false)}>
              <PlusCircle size={18} />
              <span>Create Request</span>
            </Link>
          )}

          {(role === 'HOSPITAL' || role === 'BLOOD_BANK' || role === 'ADMIN') && (
            <Link to="/inventory" className={navLinkClass('/inventory')} onClick={() => setMobileMenuOpen(false)}>
              <Package size={18} />
              <span>Inventory</span>
            </Link>
          )}

          <Link to="/notifications" className={navLinkClass('/notifications')} onClick={() => setMobileMenuOpen(false)}>
            <div className="notif-badge-wrapper">
              <Bell size={18} />
              {unreadCount > 0 && <span className="notif-bubble">{unreadCount}</span>}
            </div>
            <span>Notifications</span>
          </Link>
        </nav>

        <div className="nav-user">
          <div className="user-info">
            <div className="user-avatar">
              <UserIcon size={16} />
            </div>
            <div className="user-details">
              <div className="user-name">{user?.name || user?.email}</div>
              <div className="user-role-badge">{role}</div>
            </div>
          </div>

          <button onClick={handleLogout} className="btn-logout" title="Logout">
            <LogOut size={18} />
          </button>

          <button className="mobile-toggle" onClick={() => setMobileMenuOpen(!mobileMenuOpen)}>
            {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      <style>{`
        .main-navbar {
          background: rgba(15, 23, 42, 0.85);
          backdrop-filter: blur(12px);
          border-bottom: 1px solid var(--border);
          position: sticky;
          top: 0;
          z-index: 50;
        }
        .nav-container {
          max-width: 1200px;
          margin: 0 auto;
          padding: 0.75rem 1.5rem;
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
        .nav-brand {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          font-weight: 800;
          font-size: 1.25rem;
          color: white;
        }
        .brand-icon {
          background: rgba(225, 29, 72, 0.15);
          border: 1px solid rgba(225, 29, 72, 0.3);
          border-radius: var(--radius-md);
          padding: 0.375rem;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        .brand-text span {
          color: var(--primary);
        }
        .nav-menu {
          display: flex;
          align-items: center;
          gap: 0.5rem;
        }
        .nav-link {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          padding: 0.5rem 0.875rem;
          border-radius: var(--radius-md);
          color: var(--text-muted);
          font-weight: 600;
          font-size: 0.875rem;
          transition: all 0.2s ease;
        }
        .nav-link:hover {
          color: var(--text-main);
          background: var(--bg-card);
        }
        .nav-link.active {
          color: white;
          background: var(--bg-card-hover);
          border: 1px solid var(--border);
        }
        .notif-badge-wrapper {
          position: relative;
          display: flex;
          align-items: center;
        }
        .notif-bubble {
          position: absolute;
          top: -6px;
          right: -8px;
          background: var(--primary);
          color: white;
          font-size: 0.6875rem;
          font-weight: 700;
          border-radius: var(--radius-full);
          padding: 0.1rem 0.35rem;
          min-width: 16px;
          text-align: center;
        }
        .nav-user {
          display: flex;
          align-items: center;
          gap: 1rem;
        }
        .user-info {
          display: flex;
          align-items: center;
          gap: 0.625rem;
        }
        .user-avatar {
          width: 32px;
          height: 32px;
          border-radius: var(--radius-full);
          background: #334155;
          display: flex;
          align-items: center;
          justify-content: center;
          color: var(--text-muted);
        }
        .user-details {
          display: flex;
          flex-direction: column;
        }
        .user-name {
          font-size: 0.8125rem;
          font-weight: 700;
          color: var(--text-main);
          max-width: 140px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .user-role-badge {
          font-size: 0.6875rem;
          font-weight: 800;
          color: var(--primary);
          letter-spacing: 0.05em;
        }
        .btn-logout {
          background: transparent;
          border: none;
          color: var(--text-dim);
          cursor: pointer;
          padding: 0.5rem;
          border-radius: var(--radius-md);
          display: flex;
          align-items: center;
          transition: all 0.2s ease;
        }
        .btn-logout:hover {
          color: var(--danger);
          background: var(--danger-bg);
        }
        .mobile-toggle {
          display: none;
          background: transparent;
          border: none;
          color: var(--text-main);
          cursor: pointer;
        }
        @media (max-width: 860px) {
          .mobile-toggle { display: block; }
          .user-details { display: none; }
          .nav-menu {
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            background: var(--bg-card);
            flex-direction: column;
            padding: 1rem;
            border-bottom: 1px solid var(--border);
            display: none;
          }
          .nav-menu.open { display: flex; }
        }
      `}</style>
    </header>
  );
};

export default Navbar;
