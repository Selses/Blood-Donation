import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { bloodRequestService } from '../services/bloodRequestService';
import { PlusCircle, AlertCircle, CheckCircle2, ArrowLeft } from 'lucide-react';

const BloodRequestForm = () => {
  const [formData, setFormData] = useState({
    recipientName: '',
    bloodGroup: 'O+',
    hospitalName: '',
    city: '',
    urgency: 'HIGH',
    requiredUnits: 1
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'requiredUnits' ? parseInt(value, 10) || 1 : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.recipientName.trim() || !formData.hospitalName.trim() || !formData.city.trim()) {
      setError('Please fill in all required fields.');
      return;
    }
    if (formData.requiredUnits < 1) {
      setError('Required units must be at least 1.');
      return;
    }

    setError('');
    setLoading(true);
    try {
      const created = await bloodRequestService.createRequest(formData);
      setSuccessMsg('Blood request submitted successfully!');
      setTimeout(() => {
        navigate(`/blood-requests/${created.id}`);
      }, 1000);
    } catch (err) {
      setError(err.message || 'Failed to create blood request.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container form-page">
      <div className="form-card-wrapper">
        <button onClick={() => navigate(-1)} className="btn-back">
          <ArrowLeft size={16} /> Back
        </button>

        <div className="card form-card">
          <div className="form-header">
            <div className="header-icon">
              <PlusCircle size={28} color="#e11d48" />
            </div>
            <h2>Create Emergency Blood Request</h2>
            <p>Broadcast your request across the compatible donor network and blood banks.</p>
          </div>

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

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Patient / Recipient Name *</label>
              <input 
                type="text"
                name="recipientName"
                className="form-input"
                placeholder="e.g. Arun Kumar"
                value={formData.recipientName}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Blood Group *</label>
                <select 
                  name="bloodGroup" 
                  className="form-select"
                  value={formData.bloodGroup}
                  onChange={handleChange}
                >
                  <option value="A+">A+</option>
                  <option value="A-">A-</option>
                  <option value="B+">B+</option>
                  <option value="B-">B-</option>
                  <option value="AB+">AB+</option>
                  <option value="AB-">AB-</option>
                  <option value="O+">O+</option>
                  <option value="O-">O-</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Required Units *</label>
                <input 
                  type="number"
                  name="requiredUnits"
                  className="form-input"
                  min="1"
                  max="50"
                  value={formData.requiredUnits}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Hospital Name *</label>
                <input 
                  type="text"
                  name="hospitalName"
                  className="form-input"
                  placeholder="e.g. Trichy Government Hospital"
                  value={formData.hospitalName}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">City *</label>
                <input 
                  type="text"
                  name="city"
                  className="form-input"
                  placeholder="e.g. Trichy"
                  value={formData.city}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Urgency Level *</label>
              <select 
                name="urgency" 
                className="form-select"
                value={formData.urgency}
                onChange={handleChange}
              >
                <option value="CRITICAL">CRITICAL (Immediate Attention)</option>
                <option value="HIGH">HIGH (Urgent Request)</option>
                <option value="MEDIUM">MEDIUM (Standard Hospital Need)</option>
                <option value="LOW">LOW (Advance Preparation)</option>
              </select>
            </div>

            <button 
              type="submit" 
              className="btn btn-primary submit-btn"
              disabled={loading}
            >
              <PlusCircle size={18} />
              <span>{loading ? 'Submitting Request...' : 'Submit Emergency Blood Request'}</span>
            </button>
          </form>
        </div>
      </div>

      <style>{`
        .form-page {
          padding-top: 1.5rem;
          padding-bottom: 3rem;
        }
        .form-card-wrapper {
          max-width: 600px;
          margin: 0 auto;
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
        .form-card {
          padding: 2rem;
        }
        .form-header {
          text-align: center;
          margin-bottom: 1.75rem;
        }
        .header-icon {
          width: 52px;
          height: 52px;
          background: rgba(225, 29, 72, 0.15);
          border-radius: var(--radius-full);
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto 0.75rem;
        }
        .form-header h2 {
          font-size: 1.375rem;
          font-weight: 800;
          margin-bottom: 0.25rem;
        }
        .form-header p {
          font-size: 0.8125rem;
          color: var(--text-muted);
        }
        .form-row {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 1rem;
        }
        .submit-btn {
          width: 100%;
          padding: 0.8125rem;
          margin-top: 0.75rem;
        }
      `}</style>
    </div>
  );
};

export default BloodRequestForm;
