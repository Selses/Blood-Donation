import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { bloodRequestService } from '../services/bloodRequestService';
import { useAuth } from '../context/AuthContext';
import { 
  Activity, 
  ArrowLeft, 
  CheckCircle2, 
  XCircle, 
  AlertTriangle, 
  Building2, 
  MapPin, 
  Calendar, 
  Heart,
  PackageCheck,
  AlertCircle
} from 'lucide-react';

const BloodRequestDetails = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [request, setRequest] = useState(null);
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [fulfillSource, setFulfillSource] = useState('DONOR');

  useEffect(() => {
    fetchRequestAndMatches();
  }, [id]);

  const fetchRequestAndMatches = async () => {
    setLoading(true);
    setError('');
    try {
      const reqData = await bloodRequestService.getRequestById(id);
      setRequest(reqData);

      // Fetch matches for eligible roles or request view
      const matchData = await bloodRequestService.getMatchesForRequest(id).catch(() => []);
      setMatches(matchData);
    } catch (err) {
      setError(err.message || 'Unable to load blood request details.');
    } finally {
      setLoading(false);
    }
  };

  const handleAcceptMatch = async (matchId) => {
    setActionLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      await bloodRequestService.acceptMatch(id, matchId);
      setSuccessMsg('You have accepted this blood request match!');
      fetchRequestAndMatches();
    } catch (err) {
      setError(err.message || 'Failed to accept match.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeclineMatch = async (matchId) => {
    setActionLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      await bloodRequestService.declineMatch(id, matchId);
      setSuccessMsg('You declined this match.');
      fetchRequestAndMatches();
    } catch (err) {
      setError(err.message || 'Failed to decline match.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCancelRequest = async () => {
    if (!window.confirm('Are you sure you want to cancel this blood request?')) {
      return;
    }
    setActionLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      await bloodRequestService.cancelRequest(id);
      setSuccessMsg('Blood request cancelled successfully.');
      fetchRequestAndMatches();
    } catch (err) {
      setError(err.message || 'Failed to cancel request.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleFulfillRequest = async () => {
    setActionLoading(true);
    setError('');
    setSuccessMsg('');
    try {
      await bloodRequestService.fulfillRequest(id, { source: fulfillSource });
      setSuccessMsg(`Blood request successfully fulfilled via ${fulfillSource}!`);
      fetchRequestAndMatches();
    } catch (err) {
      setError(err.message || 'Failed to fulfill blood request.');
    } finally {
      setActionLoading(false);
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
        <Activity className="animate-spin text-primary mx-auto mb-2" size={32} />
        <p>Loading blood request...</p>
      </div>
    );
  }

  if (!request) {
    return (
      <div className="container py-5 text-center">
        <div className="alert alert-danger">{error || 'Blood request not found.'}</div>
        <button onClick={() => navigate('/blood-requests')} className="btn btn-secondary mt-3">
          <ArrowLeft size={16} /> Back to Requests
        </button>
      </div>
    );
  }

  const role = user?.role;
  const isOrg = ['HOSPITAL', 'BLOOD_BANK', 'ADMIN'].includes(role);
  const isRecipient = role === 'RECIPIENT' || role === 'ADMIN';

  return (
    <div className="container details-page">
      <button onClick={() => navigate(-1)} className="btn-back">
        <ArrowLeft size={16} /> Back to Requests
      </button>

      {error && (
        <div className="alert alert-danger">
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {successMsg && (
        <div className="alert alert-success">
          <CheckCircle2 size={18} />
          <span>{successMsg}</span>
        </div>
      )}

      <div className="details-layout">
        {/* Main Request Card */}
        <div className="card request-overview-card">
          <div className="overview-header">
            <div>
              <div className="req-meta">
                <span className="req-id">Request #{request.id}</span>
                {getUrgencyBadge(request.urgency)}
                {getStatusBadge(request.status)}
              </div>
              <h1 className="patient-name">{request.recipientName}</h1>
            </div>
            <div className="blood-group-large">
              {request.bloodGroup}
            </div>
          </div>

          <div className="info-grid">
            <div className="info-item">
              <Building2 size={18} className="info-icon" />
              <div>
                <div className="info-label">Hospital</div>
                <div className="info-value">{request.hospitalName}</div>
              </div>
            </div>

            <div className="info-item">
              <MapPin size={18} className="info-icon" />
              <div>
                <div className="info-label">Location / City</div>
                <div className="info-value">{request.city}</div>
              </div>
            </div>

            <div className="info-item">
              <Heart size={18} className="info-icon text-primary" />
              <div>
                <div className="info-label">Required Units</div>
                <div className="info-value">{request.requiredUnits} Unit(s)</div>
              </div>
            </div>

            <div className="info-item">
              <Calendar size={18} className="info-icon" />
              <div>
                <div className="info-label">Request Date</div>
                <div className="info-value">
                  {request.requestDate ? new Date(request.requestDate).toLocaleString() : 'N/A'}
                </div>
              </div>
            </div>
          </div>

          {/* Org Fulfillment Actions */}
          {isOrg && request.status !== 'FULFILLED' && request.status !== 'CANCELLED' && (
            <div className="fulfill-section">
              <h4>Hospital / Blood Bank Fulfillment</h4>
              <div className="fulfill-controls">
                <select 
                  className="form-select fulfill-select"
                  value={fulfillSource}
                  onChange={(e) => setFulfillSource(e.target.value)}
                >
                  <option value="DONOR">Fulfill via Accepted Donor</option>
                  <option value="INVENTORY">Fulfill via Inventory Stock</option>
                </select>

                <button 
                  onClick={handleFulfillRequest}
                  className="btn btn-success"
                  disabled={actionLoading}
                >
                  <PackageCheck size={18} />
                  <span>{actionLoading ? 'Processing...' : 'Confirm Fulfillment'}</span>
                </button>
              </div>
            </div>
          )}

          {/* Recipient Cancellation */}
          {isRecipient && request.status !== 'FULFILLED' && request.status !== 'CANCELLED' && (
            <div className="cancel-section">
              <button 
                onClick={handleCancelRequest}
                className="btn btn-danger"
                disabled={actionLoading}
              >
                <XCircle size={18} />
                <span>Cancel Blood Request</span>
              </button>
            </div>
          )}
        </div>

        {/* Matched Donors Section */}
        <div className="card matches-card">
          <div className="matches-header">
            <h3>Compatible Matched Donors ({matches.length})</h3>
            <p className="text-muted">Ranked by compatibility, proximity & medical eligibility</p>
          </div>

          {matches.length === 0 ? (
            <div className="empty-state">
              <AlertTriangle size={24} className="text-warning mx-auto mb-2" />
              <p>No compatible donor matches recorded yet.</p>
            </div>
          ) : (
            <div className="matches-list">
              {matches.map((m) => {
                const isMyMatch = role === 'DONOR' && m.status === 'PENDING';
                return (
                  <div key={m.matchId} className="match-card">
                    <div className="match-top">
                      <div>
                        <div className="match-name">{m.donorName}</div>
                        <div className="match-city text-muted">{m.city}</div>
                      </div>
                      <div className="match-score">Score: <strong>{m.matchScore}</strong></div>
                    </div>

                    <div className="match-reason text-muted">{m.matchReason}</div>

                    <div className="match-footer">
                      <span className={`badge badge-${m.status?.toLowerCase()}`}>{m.status}</span>

                      {isMyMatch && request.status !== 'ACCEPTED' && request.status !== 'FULFILLED' && request.status !== 'CANCELLED' && (
                        <div className="donor-actions">
                          <button 
                            onClick={() => handleAcceptMatch(m.matchId)}
                            className="btn btn-success btn-sm"
                            disabled={actionLoading}
                          >
                            Accept
                          </button>
                          <button 
                            onClick={() => handleDeclineMatch(m.matchId)}
                            className="btn btn-secondary btn-sm"
                            disabled={actionLoading}
                          >
                            Decline
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>

      <style>{`
        .details-page {
          padding-top: 1.5rem;
          padding-bottom: 3rem;
        }
        .btn-back {
          background: transparent;
          border: none;
          color: var(--text-muted);
          display: flex;
          align-items: center;
          gap: 0.5rem;
          margin-bottom: 1rem;
          cursor: pointer;
          font-weight: 600;
        }
        .btn-back:hover { color: white; }
        .details-layout {
          display: grid;
          grid-template-columns: 3fr 2fr;
          gap: 1.5rem;
        }
        .request-overview-card {
          padding: 2rem;
        }
        .overview-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 1.5rem;
          padding-bottom: 1.25rem;
          border-bottom: 1px solid var(--border);
        }
        .req-meta {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          margin-bottom: 0.5rem;
        }
        .req-id {
          font-size: 0.8125rem;
          font-family: monospace;
          color: var(--text-dim);
          font-weight: 700;
        }
        .patient-name {
          font-size: 1.75rem;
          font-weight: 800;
        }
        .blood-group-large {
          font-size: 2.25rem;
          font-weight: 900;
          color: var(--primary);
          background: rgba(225, 29, 72, 0.15);
          padding: 0.5rem 1rem;
          border-radius: var(--radius-lg);
          border: 2px solid var(--primary);
        }
        .info-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 1.25rem;
          margin-bottom: 1.75rem;
        }
        .info-item {
          display: flex;
          align-items: center;
          gap: 0.875rem;
          background: rgba(15, 23, 42, 0.5);
          padding: 1rem;
          border-radius: var(--radius-md);
        }
        .info-icon { color: var(--text-muted); }
        .info-label {
          font-size: 0.75rem;
          color: var(--text-dim);
          text-transform: uppercase;
          font-weight: 700;
        }
        .info-value {
          font-size: 0.9375rem;
          font-weight: 700;
        }
        .fulfill-section {
          background: rgba(16, 185, 129, 0.08);
          border: 1px solid rgba(16, 185, 129, 0.3);
          border-radius: var(--radius-md);
          padding: 1.25rem;
          margin-top: 1.5rem;
        }
        .fulfill-section h4 {
          font-size: 0.9375rem;
          font-weight: 700;
          color: #34d399;
          margin-bottom: 0.75rem;
        }
        .fulfill-controls {
          display: flex;
          gap: 0.75rem;
        }
        .fulfill-select {
          flex: 1;
        }
        .cancel-section {
          margin-top: 1.5rem;
          padding-top: 1.25rem;
          border-top: 1px solid var(--border);
        }
        .matches-card {
          padding: 1.5rem;
        }
        .matches-header {
          margin-bottom: 1.25rem;
          padding-bottom: 0.75rem;
          border-bottom: 1px solid var(--border);
        }
        .matches-header h3 {
          font-size: 1.125rem;
          font-weight: 700;
        }
        .match-card {
          background: rgba(15, 23, 42, 0.6);
          border: 1px solid var(--border);
          border-radius: var(--radius-md);
          padding: 1rem;
          margin-bottom: 0.75rem;
        }
        .match-top {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 0.5rem;
        }
        .match-name { font-weight: 700; }
        .match-reason {
          font-size: 0.75rem;
          margin-bottom: 0.75rem;
        }
        .match-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
        }
        .donor-actions {
          display: flex;
          gap: 0.5rem;
        }
        .btn-sm {
          padding: 0.35rem 0.75rem;
          font-size: 0.75rem;
        }
        @media (max-width: 900px) {
          .details-layout { grid-template-columns: 1fr; }
          .info-grid { grid-template-columns: 1fr; }
          .fulfill-controls { flex-direction: column; }
        }
      `}</style>
    </div>
  );
};

export default BloodRequestDetails;
