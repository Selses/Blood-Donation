import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ArrowRight, AlertCircle, Shield } from 'lucide-react';

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    bloodGroup: 'O+',
    role: 'DONOR',
    phone: '',
    address: '',
    city: '',
    state: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(formData);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Registration failed. Please check your data.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="hemo-register-wrapper">
      <div className="auth-ambient-glow" />

      <div className="hemo-register-card">
        <div className="auth-header">
          <Link to="/" className="auth-brand-logo">
            <span>HEMO</span>
            <span className="brand-reg">&copy;</span>
          </Link>
          <h1 className="auth-title">Join The Mission</h1>
          <p className="auth-subtitle">Register to donate or manage life-saving blood supplies</p>
        </div>

        {error && (
          <div className="alert alert-danger">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="register-form-grid">
          <div className="form-group col-span-2">
            <label className="form-label">FULL NAME / ENTITY NAME</label>
            <input
              type="text"
              name="name"
              className="form-input"
              placeholder="e.g. John Doe / City Blood Bank"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">ACCOUNT ROLE</label>
            <select
              name="role"
              className="form-select"
              value={formData.role}
              onChange={handleChange}
            >
              <option value="DONOR">Donor</option>
              <option value="RECIPIENT">Recipient</option>
              <option value="HOSPITAL">Hospital</option>
              <option value="BLOOD_BANK">Blood Bank</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">BLOOD GROUP</label>
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
            <label className="form-label">EMAIL ADDRESS</label>
            <input
              type="email"
              name="email"
              className="form-input"
              placeholder="name@example.com"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">PHONE NUMBER</label>
            <input
              type="tel"
              name="phone"
              className="form-input"
              placeholder="+1 555-0199"
              value={formData.phone}
              onChange={handleChange}
            />
          </div>

          <div className="form-group col-span-2">
            <label className="form-label">PASSWORD</label>
            <input
              type="password"
              name="password"
              className="form-input"
              placeholder="••••••••••••"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group col-span-2">
            <label className="form-label">CITY & REGION</label>
            <input
              type="text"
              name="city"
              className="form-input"
              placeholder="e.g. Trichy, Tamil Nadu"
              value={formData.city}
              onChange={handleChange}
            />
          </div>

          <div className="col-span-2">
            <button
              type="submit"
              className="btn btn-primary register-submit-btn"
              disabled={loading}
            >
              <span>{loading ? 'Creating Profile...' : 'Complete Registration'}</span>
              <ArrowRight size={18} />
            </button>
          </div>
        </form>

        <div className="auth-footer-link">
          <span>Already registered? </span>
          <Link to="/login" className="highlight-cta">Sign In</Link>
        </div>
      </div>

      <style>{`
        .hemo-register-wrapper {
          min-height: calc(100vh - 80px);
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 3.5rem 1.5rem;
          background: #08080a;
          position: relative;
          overflow: hidden;
        }

        .auth-ambient-glow {
          position: absolute;
          width: 550px;
          height: 550px;
          background: radial-gradient(circle, rgba(225, 29, 72, 0.16) 0%, transparent 70%);
          border-radius: 50%;
          filter: blur(120px);
          pointer-events: none;
        }

        .hemo-register-card {
          position: relative;
          z-index: 1;
          width: 100%;
          max-width: 600px;
          background: #111318;
          border: 1px solid rgba(255, 255, 255, 0.1);
          border-radius: var(--radius-xl);
          padding: 2.75rem 2.5rem;
          box-shadow: 0 25px 60px rgba(0, 0, 0, 0.85);
          backdrop-filter: blur(20px);
        }

        .auth-header {
          text-align: center;
          margin-bottom: 2rem;
        }

        .auth-brand-logo {
          font-family: var(--font-heading);
          font-size: 1.8rem;
          font-weight: 900;
          color: #ffffff;
          display: inline-flex;
          align-items: baseline;
          gap: 0.2rem;
          margin-bottom: 0.75rem;
        }
        .brand-reg {
          font-size: 0.8rem;
          color: var(--primary);
        }

        .auth-title {
          font-family: var(--font-heading);
          font-size: 1.6rem;
          font-weight: 800;
          color: #ffffff;
          margin-bottom: 0.35rem;
        }

        .auth-subtitle {
          font-size: 0.85rem;
          color: var(--text-muted);
        }

        .register-form-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 1.15rem;
        }

        .col-span-2 {
          grid-column: span 2;
        }

        .register-submit-btn {
          width: 100%;
          padding: 0.9rem;
          margin-top: 0.5rem;
          font-size: 0.95rem;
        }

        .auth-footer-link {
          margin-top: 1.75rem;
          text-align: center;
          font-size: 0.85rem;
          color: var(--text-muted);
        }

        .highlight-cta {
          color: var(--primary);
          font-weight: 700;
        }
        .highlight-cta:hover {
          text-decoration: underline;
        }

        @media (max-width: 600px) {
          .register-form-grid { grid-template-columns: 1fr; }
          .col-span-2 { grid-column: span 1; }
          .hemo-register-card { padding: 2rem 1.5rem; }
        }
      `}</style>
    </div>
  );
};

export default Register;
