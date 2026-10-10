import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { notificationService } from '../services/notificationService';
import { 
  Heart, 
  LayoutDashboard, 
  Activity, 
  PlusCircle, 
  Package, 
  Bell, 
  LogOut, 
  User as UserIcon,
  Menu,
  X,
  ArrowRight
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

  const role = user?.role;

  const navLinkClass = (path) => {
    const isActive = location.pathname === path;
    return `hemo-nav-link ${isActive ? 'active' : ''}`;
  };

  return (
    <header className="hemo-header">
      <div className="container nav-container">
        {/* Brand Logo matching HEMO© */}
        <Link to="/" className="hemo-brand">
          <span className="brand-title">HEMO</span>
          <span className="brand-registered">&copy;</span>
        </Link>

        {/* Navigation Links */}
        <nav className={`hemo-nav-menu ${mobileMenuOpen ? 'open' : ''}`}>
          {isAuthenticated ? (
            <>
              <Link to="/dashboard" className={navLinkClass('/dashboard')} onClick={() => setMobileMenuOpen(false)}>
                <LayoutDashboard size={16} />
                <span>Dashboard</span>
              </Link>

              <Link to="/blood-requests" className={navLinkClass('/blood-requests')} onClick={() => setMobileMenuOpen(false)}>
                <Activity size={16} />
                <span>Blood Requests</span>
              </Link>

              {(role === 'RECIPIENT' || role === 'HOSPITAL' || role === 'ADMIN') && (
                <Link to="/blood-requests/new" className={navLinkClass('/blood-requests/new')} onClick={() => setMobileMenuOpen(false)}>
                  <PlusCircle size={16} />
                  <span>Create Request</span>
                </Link>
              )}

              {(role === 'HOSPITAL' || role === 'BLOOD_BANK' || role === 'ADMIN') && (
                <Link to="/inventory" className={navLinkClass('/inventory')} onClick={() => setMobileMenuOpen(false)}>
                  <Package size={16} />
                  <span>Inventory</span>
                </Link>
              )}

              <Link to="/notifications" className={navLinkClass('/notifications')} onClick={() => setMobileMenuOpen(false)}>
                <div className="notif-wrapper">
                  <Bell size={16} />
                  {unreadCount > 0 && <span className="notif-bubble">{unreadCount}</span>}
                </div>
                <span>Notifications</span>
              </Link>
            </>
          ) : (
            <>
              <a href="/#features" className="hemo-nav-link" onClick={() => setMobileMenuOpen(false)}>Services</a>
              <a href="/#events" className="hemo-nav-link" onClick={() => setMobileMenuOpen(false)}>Events</a>
              <a href="/#how-it-works" className="hemo-nav-link" onClick={() => setMobileMenuOpen(false)}>How It Works</a>
              <a href="/#faq" className="hemo-nav-link" onClick={() => setMobileMenuOpen(false)}>FAQ</a>
            </>
          )}
        </nav>

        {/* Right Action / Auth Area */}
        <div className="hemo-nav-right">
          {isAuthenticated ? (
            <div className="user-profile-cluster">
              <div className="user-text-meta">
                <span className="user-name-txt">{user?.name || user?.email}</span>
                <span className="user-role-txt">{role}</span>
              </div>
              <button onClick={handleLogout} className="btn-logout-icon" title="Sign Out">
                <LogOut size={16} />
              </button>
            </div>
          ) : (
            <div className="guest-action-cluster">
              <Link to="/login" className="btn-login-ghost">
                Sign In
              </Link>
              <Link to="/register" className="btn btn-pill-white btn-header-cta">
                <span>Start Saving Lives</span>
                <ArrowRight size={14} />
              </Link>
            </div>
          )}

          <button 
            className="mobile-hamburger" 
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-label="Toggle navigation menu"
          >
            {mobileMenuOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </div>

      <style>{`
        .hemo-header {
          position: sticky;
          top: 0;
          z-index: 100;
          background: rgba(8, 8, 10, 0.85);
          backdrop-filter: blur(20px);
          border-bottom: 1px solid rgba(255, 255, 255, 0.07);
        }

        .nav-container {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding-top: 1rem;
          padding-bottom: 1rem;
        }

        .hemo-brand {
          display: flex;
          align-items: baseline;
          gap: 0.2rem;
          font-family: var(--font-heading);
          font-size: 1.6rem;
          font-weight: 900;
          letter-spacing: -0.04em;
          color: #ffffff;
        }
        .brand-registered {
          font-size: 0.75rem;
          color: var(--primary);
        }

        .hemo-nav-menu {
          display: flex;
          align-items: center;
          gap: 0.6rem;
        }

        .hemo-nav-link {
          display: flex;
          align-items: center;
          gap: 0.45rem;
          padding: 0.5rem 1rem;
          border-radius: var(--radius-full);
          font-size: 0.84rem;
          font-weight: 600;
          color: var(--text-muted);
          transition: all 0.2s ease;
        }

        .hemo-nav-link:hover {
          color: #ffffff;
          background: rgba(255, 255, 255, 0.05);
        }

        .hemo-nav-link.active {
          color: #ffffff;
          background: rgba(255, 255, 255, 0.09);
          border: 1px solid rgba(255, 255, 255, 0.12);
        }

        .notif-wrapper {
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
          font-size: 0.65rem;
          font-weight: 800;
          border-radius: var(--radius-full);
          padding: 0.1rem 0.35rem;
          min-width: 15px;
          text-align: center;
          box-shadow: 0 0 10px var(--primary);
        }

        .hemo-nav-right {
          display: flex;
          align-items: center;
          gap: 1rem;
        }

        .guest-action-cluster {
          display: flex;
          align-items: center;
          gap: 1rem;
        }

        .btn-login-ghost {
          font-size: 0.85rem;
          font-weight: 600;
          color: #ffffff;
          padding: 0.5rem 0.75rem;
          transition: opacity 0.2s;
        }
        .btn-login-ghost:hover { opacity: 0.8; }

        .btn-header-cta {
          padding: 0.55rem 1.15rem;
          font-size: 0.82rem;
        }

        .user-profile-cluster {
          display: flex;
          align-items: center;
          gap: 0.85rem;
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.08);
          padding: 0.35rem 0.85rem;
          border-radius: var(--radius-full);
        }

        .user-text-meta {
          display: flex;
          flex-direction: column;
        }

        .user-name-txt {
          font-size: 0.8rem;
          font-weight: 700;
          color: #ffffff;
          max-width: 120px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .user-role-txt {
          font-size: 0.65rem;
          font-weight: 800;
          color: var(--primary);
          letter-spacing: 0.06em;
        }

        .btn-logout-icon {
          background: transparent;
          border: none;
          color: var(--text-dim);
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 0.3rem;
          border-radius: 50%;
          transition: all 0.2s ease;
        }

        .btn-logout-icon:hover {
          color: var(--danger);
          background: rgba(239, 68, 68, 0.15);
        }

        .mobile-hamburger {
          display: none;
          background: transparent;
          border: none;
          color: #ffffff;
          cursor: pointer;
        }

        @media (max-width: 900px) {
          .mobile-hamburger { display: block; }
          .hemo-nav-menu {
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            background: #0d0f14;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            flex-direction: column;
            padding: 1.5rem;
            display: none;
          }
          .hemo-nav-menu.open { display: flex; }
          .user-text-meta { display: none; }
        }
      `}</style>
    </header>
  );
};

export default Navbar;
