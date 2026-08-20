import React, { useState, useEffect } from 'react';
import { notificationService } from '../services/notificationService';
import { Bell, CheckCheck, Check, Clock, AlertCircle } from 'lucide-react';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('ALL'); // 'ALL' or 'UNREAD'

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await notificationService.getMyNotifications();
      setNotifications(data);
    } catch (err) {
      setError(err.message || 'Unable to load notifications.');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id) => {
    try {
      await notificationService.markAsRead(id);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
    } catch (err) {
      setError(err.message || 'Failed to mark notification as read.');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, read: true })));
    } catch (err) {
      setError(err.message || 'Failed to mark all as read.');
    }
  };

  const filteredNotifs = notifications.filter(n => {
    if (filter === 'UNREAD') return !n.read;
    return true;
  });

  const unreadCount = notifications.filter(n => !n.read).length;

  return (
    <div className="container notif-page">
      <div className="notif-header">
        <div>
          <h1>In-App Notifications</h1>
          <p className="text-muted">Stay alerted with live donor matches, requests, and emergency statuses.</p>
        </div>

        {unreadCount > 0 && (
          <button onClick={handleMarkAllAsRead} className="btn btn-secondary">
            <CheckCheck size={18} />
            <span>Mark All as Read</span>
          </button>
        )}
      </div>

      {error && (
        <div className="alert alert-danger">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Tabs */}
      <div className="notif-tabs">
        <button 
          className={`tab-btn ${filter === 'ALL' ? 'active' : ''}`}
          onClick={() => setFilter('ALL')}
        >
          All ({notifications.length})
        </button>
        <button 
          className={`tab-btn ${filter === 'UNREAD' ? 'active' : ''}`}
          onClick={() => setFilter('UNREAD')}
        >
          Unread ({unreadCount})
        </button>
      </div>

      {/* Notification List */}
      <div className="card notif-card">
        {loading ? (
          <div className="loading-state">
            <Clock className="animate-spin text-primary mx-auto mb-2" size={28} />
            <p>Loading notifications...</p>
          </div>
        ) : filteredNotifs.length === 0 ? (
          <div className="empty-state">
            <Bell size={32} className="text-dim mx-auto mb-2" />
            <p>No notifications found.</p>
          </div>
        ) : (
          <div className="notif-list">
            {filteredNotifs.map((n) => (
              <div 
                key={n.id} 
                className={`notif-item ${!n.read ? 'unread' : ''}`}
                onClick={() => !n.read && handleMarkAsRead(n.id)}
              >
                <div className="notif-content">
                  <div className="notif-top">
                    <div className="notif-title-group">
                      <span className="notif-title">{n.title}</span>
                      <span className="notif-type">{n.type}</span>
                    </div>
                    <span className="notif-date">
                      {n.createdAt ? new Date(n.createdAt).toLocaleDateString() + ' ' + new Date(n.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''}
                    </span>
                  </div>

                  <p className="notif-desc">{n.message}</p>
                </div>

                {!n.read && (
                  <button 
                    onClick={(e) => { e.stopPropagation(); handleMarkAsRead(n.id); }}
                    className="btn-mark-read"
                    title="Mark as read"
                  >
                    <Check size={16} />
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      <style>{`
        .notif-page {
          padding-top: 1.5rem;
          padding-bottom: 3rem;
        }
        .notif-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 1.5rem;
        }
        .notif-header h1 {
          font-size: 1.5rem;
          font-weight: 800;
        }
        .notif-tabs {
          display: flex;
          gap: 0.5rem;
          margin-bottom: 1.25rem;
        }
        .tab-btn {
          padding: 0.5rem 1rem;
          border-radius: var(--radius-md);
          background: transparent;
          border: 1px solid var(--border);
          color: var(--text-muted);
          font-weight: 600;
          font-size: 0.875rem;
          cursor: pointer;
          transition: all 0.2s ease;
        }
        .tab-btn.active {
          background: var(--primary);
          border-color: var(--primary);
          color: white;
        }
        .notif-card {
          padding: 0;
          overflow: hidden;
        }
        .notif-list {
          divide-y: 1px solid var(--border);
        }
        .notif-item {
          padding: 1.25rem 1.5rem;
          display: flex;
          align-items: center;
          justify-content: space-between;
          border-bottom: 1px solid var(--border);
          transition: background 0.2s ease;
          cursor: pointer;
        }
        .notif-item:hover {
          background: rgba(30, 41, 59, 0.6);
        }
        .notif-item.unread {
          background: rgba(225, 29, 72, 0.06);
          border-left: 4px solid var(--primary);
        }
        .notif-content {
          flex: 1;
        }
        .notif-top {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 0.35rem;
        }
        .notif-title-group {
          display: flex;
          align-items: center;
          gap: 0.625rem;
        }
        .notif-title {
          font-weight: 700;
          font-size: 0.9375rem;
        }
        .notif-type {
          font-size: 0.6875rem;
          font-weight: 700;
          background: #334155;
          padding: 0.15rem 0.4rem;
          border-radius: var(--radius-sm);
          color: var(--text-muted);
        }
        .notif-date {
          font-size: 0.75rem;
          color: var(--text-dim);
        }
        .notif-desc {
          font-size: 0.8125rem;
          color: var(--text-muted);
          line-height: 1.4;
        }
        .btn-mark-read {
          background: #334155;
          border: none;
          color: var(--text-muted);
          width: 32px;
          height: 32px;
          border-radius: var(--radius-full);
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          margin-left: 1rem;
          transition: all 0.2s ease;
        }
        .btn-mark-read:hover {
          background: var(--success);
          color: white;
        }
        .loading-state, .empty-state {
          padding: 3rem 1rem;
          text-align: center;
          color: var(--text-muted);
        }
        @media (max-width: 640px) {
          .notif-header { flex-direction: column; align-items: flex-start; gap: 1rem; }
        }
      `}</style>
    </div>
  );
};

export default Notifications;
