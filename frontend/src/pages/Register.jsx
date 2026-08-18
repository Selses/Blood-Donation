import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authService } from '../services/authService';
import { HeartHandshake, UserPlus, AlertCircle, CheckCircle2 } from 'lucide-react';

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    role: 'DONOR'
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    setLoading(true);
    try {
      // 1. Register with backend API
      await authService.register({
        name: formData.name,
        email: formData.email,
        password: formData.password,
        phone: formData.phone,
        role: formData.role
      });

      setSuccess('Account created successfully! Logging you in...');

      // 2. Automatically log in and navigate to dashboard
      setTimeout(async () => {
        try {
          await login(formData.email, formData.password);
          navigate('/dashboard');
        } catch (loginErr) {
          navigate('/login');
        }
      }, 1000);
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
      setLoading(false);
    }
  };

  return (
    <div className="register-wrapper">
      <div className="register-card card">
        <div className="register-header">
          <div className="register-icon">
            <HeartHandshake size={32} color="#e11d48" />
          </div>
          <h1>Create an Account</h1>
          <p>Join the Blood Donation Network to save lives</p>
        </div>

        {error && (
          <div className="alert alert-danger">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        {success && (
          <div className="alert alert-success">
            <CheckCircle2 size={18} />
            <span>{success}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input
              type="text"
              name="name"
              className="form-input"
              placeholder="e.g. John Doe"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
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

          <div className="form-row">
            <div className="form-group flex-1">
              <label className="form-label">Phone Number</label>
              <input
                type="tel"
                name="phone"
                className="form-input"
                placeholder="9876543210"
                value={formData.phone}
                onChange={handleChange}
              />
            </div>

            <div className="form-group flex-1">
              <label className="form-label">Account Role</label>
              <select
                name="role"
                className="form-input"
                value={formData.role}
                onChange={handleChange}
              >
                <option value="DONOR">Donor</option>
                <option value="RECIPIENT">Recipient</option>
                <option value="HOSPITAL">Hospital</option>
                <option value="BLOOD_BANK">Blood Bank</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group flex-1">
              <label className="form-label">Password</label>
              <input
                type="password"
                name="password"
                className="form-input"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group flex-1">
              <label className="form-label">Confirm Password</label>
              <input
                type="password"
                name="confirmPassword"
                className="form-input"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary register-btn"
            disabled={loading}
          >
            <UserPlus size={18} />
            <span>{loading ? 'Creating Account...' : 'Register'}</span>
          </button>
        </form>

        <div className="footer-action">
          <span>Already have an account? </span>
          <Link to="/login" className="link-highlight">Sign In</Link>
        </div>
      </div>

      <style>{`
        .register-wrapper {
          min-height: 100vh;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 1.5rem;
          background: radial-gradient(circle at top, #1e1b4b 0%, #0f172a 70%);
        }
        .register-card {
          width: 100%;
          max-width: 500px;
          border-radius: var(--radius-lg);
          padding: 2.25rem;
          box-shadow: var(--shadow-glow), var(--shadow-lg);
        }
        .register-header {
          text-align: center;
          margin-bottom: 1.75rem;
        }
        .register-icon {
          width: 60px;
          height: 60px;
          background: rgba(225, 29, 72, 0.15);
          border: 1px solid rgba(225, 29, 72, 0.3);
          border-radius: var(--radius-full);
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto 1rem;
        }
        .register-header h1 {
          font-size: 1.5rem;
          font-weight: 800;
          color: white;
          margin-bottom: 0.35rem;
        }
        .register-header p {
          color: var(--text-muted);
          font-size: 0.875rem;
        }
        .form-row {
          display: flex;
          gap: 1rem;
        }
        .flex-1 {
          flex: 1;
        }
        .register-btn {
          width: 100%;
          padding: 0.8125rem;
          margin-top: 0.75rem;
        }
        .footer-action {
          margin-top: 1.75rem;
          padding-top: 1.25rem;
          border-top: 1px solid var(--border);
          text-align: center;
          font-size: 0.875rem;
          color: var(--text-muted);
        }
        .link-highlight {
          color: var(--primary);
          font-weight: 700;
          text-decoration: none;
        }
        .link-highlight:hover {
          text-decoration: underline;
        }
        @media (max-width: 540px) {
          .form-row {
            flex-direction: column;
            gap: 0;
          }
        }
      `}</style>
    </div>
  );
};

export default Register;
