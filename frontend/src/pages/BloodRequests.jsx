import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { bloodRequestService } from '../services/bloodRequestService';
import { useAuth } from '../context/AuthContext';
import { Activity, PlusCircle, Search, Filter, AlertCircle } from 'lucide-react';

const BloodRequests = () => {
  const { user } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Filters
  const [bloodGroupFilter, setBloodGroupFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [citySearch, setCitySearch] = useState('');

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await bloodRequestService.getAllRequests();
      setRequests(data);
    } catch (err) {
      setError(err.message || 'Unable to load blood requests.');
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

  const filteredRequests = requests.filter(r => {
    if (bloodGroupFilter && r.bloodGroup !== bloodGroupFilter) return false;
    if (statusFilter && r.status !== statusFilter) return false;
    if (citySearch && !r.city?.toLowerCase().includes(citySearch.toLowerCase())) return false;
    return true;
  });

  return (
    <div className="container requests-page">
      <div className="page-header">
        <div>
          <h1>Emergency Blood Requests</h1>
          <p className="text-muted">Real-time live requests matching donors, hospitals, and blood banks</p>
        </div>

        {(user?.role === 'RECIPIENT' || user?.role === 'HOSPITAL' || user?.role === 'ADMIN') && (
          <Link to="/blood-requests/new" className="btn btn-primary">
            <PlusCircle size={18} />
            <span>Create Blood Request</span>
          </Link>
        )}
      </div>

      {error && (
        <div className="alert alert-danger">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Filter Bar */}
      <div className="card filter-bar">
        <div className="filter-group">
          <Search size={16} className="filter-icon" />
          <input 
            type="text"
            className="form-input search-input"
            placeholder="Search by city..."
            value={citySearch}
            onChange={(e) => setCitySearch(e.target.value)}
          />
        </div>

        <div className="filter-dropdowns">
          <select 
            className="form-select filter-select"
            value={bloodGroupFilter}
            onChange={(e) => setBloodGroupFilter(e.target.value)}
          >
            <option value="">All Blood Groups</option>
            <option value="A+">A+</option>
            <option value="A-">A-</option>
            <option value="B+">B+</option>
            <option value="B-">B-</option>
            <option value="AB+">AB+</option>
            <option value="AB-">AB-</option>
            <option value="O+">O+</option>
            <option value="O-">O-</option>
          </select>

          <select 
            className="form-select filter-select"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="">All Statuses</option>
            <option value="PENDING">PENDING</option>
            <option value="MATCHED">MATCHED</option>
            <option value="ACCEPTED">ACCEPTED</option>
            <option value="FULFILLED">FULFILLED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        </div>
      </div>

      {/* Requests Table */}
      <div className="card requests-card">
        {loading ? (
          <div className="loading-state">
            <Activity className="animate-spin text-primary mx-auto mb-2" size={28} />
            <p>Loading emergency requests...</p>
          </div>
        ) : filteredRequests.length === 0 ? (
          <div className="empty-state">
            <Activity size={32} className="text-dim mx-auto mb-2" />
            <p>No blood requests match the selected criteria.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Patient</th>
                  <th>Blood Group</th>
                  <th>Units</th>
                  <th>Hospital</th>
                  <th>City</th>
                  <th>Urgency</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredRequests.map((req) => (
                  <tr key={req.id}>
                    <td className="text-dim font-mono">#{req.id}</td>
                    <td className="font-bold">{req.recipientName}</td>
                    <td><span className="blood-group-tag">{req.bloodGroup}</span></td>
                    <td className="font-semibold">{req.requiredUnits} unit(s)</td>
                    <td className="text-muted">{req.hospitalName}</td>
                    <td>{req.city}</td>
                    <td>{getUrgencyBadge(req.urgency)}</td>
                    <td>{getStatusBadge(req.status)}</td>
                    <td>
                      <Link to={`/blood-requests/${req.id}`} className="btn-table">
                        View Details
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <style>{`
        .requests-page {
          padding-top: 1.5rem;
          padding-bottom: 3rem;
        }
        .page-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 1.5rem;
        }
        .page-header h1 {
          font-size: 1.5rem;
          font-weight: 800;
        }
        .filter-bar {
          display: flex;
          align-items: center;
          justify-content: space-between;
          gap: 1rem;
          margin-bottom: 1.5rem;
          padding: 1rem 1.25rem;
        }
        .filter-group {
          position: relative;
          flex: 1;
          max-width: 400px;
        }
        .filter-icon {
          position: absolute;
          left: 0.875rem;
          top: 50%;
          transform: translateY(-50%);
          color: var(--text-dim);
        }
        .search-input {
          padding-left: 2.5rem;
        }
        .filter-dropdowns {
          display: flex;
          gap: 0.75rem;
        }
        .filter-select {
          min-width: 150px;
        }
        .requests-card {
          padding: 0;
          overflow: hidden;
        }
        .custom-table th {
          padding: 0.875rem 1rem;
          background: rgba(15, 23, 42, 0.6);
        }
        .custom-table td {
          padding: 1rem;
        }
        .loading-state, .empty-state {
          padding: 3rem 1rem;
          text-align: center;
          color: var(--text-muted);
        }
        .font-mono { font-family: monospace; }
        .font-bold { font-weight: 700; }
        .font-semibold { font-weight: 600; }
        @media (max-width: 768px) {
          .page-header, .filter-bar { flex-direction: column; align-items: stretch; }
          .filter-group { max-width: 100%; }
        }
      `}</style>
    </div>
  );
};

export default BloodRequests;
