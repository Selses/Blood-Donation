import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ArrowRight, AlertCircle, ShieldCheck } from 'lucide-react';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Please enter both email and password.');
      return;
    }
    setError('');
    setLoading(true);
    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  const handleQuickDemo = (demoEmail, demoPassword) => {
    setEmail(demoEmail);
    setPassword(demoPassword);
  };

  return (
    <div className="hemo-auth-wrapper">
      <div className="auth-ambient-glow" />

      <div className="hemo-auth-card">
        {/* Brand Header */}
        <div className="auth-header">
          <Link to="/" className="auth-brand-logo">
            <span>HEMO</span>
            <span className="brand-reg">&copy;</span>
          </Link>
          <h1 className="auth-title">Welcome Back</h1>
          <p className="auth-subtitle">Sign in to your verified donation workspace</p>
        </div>

        {error && (
          <div className="alert alert-danger">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label className="form-label">EMAIL ADDRESS</label>
            <input 
              type="email"
              className="form-input"
              placeholder="e.g. physician@hospital.org"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <div className="form-label-split">
              <label className="form-label">PASSWORD</label>
              <span className="forgot-hint">Encrypted SSL</span>
            </div>
            <input 
              type="password"
              className="form-input"
              placeholder="••••••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button 
            type="submit" 
            className="btn btn-primary auth-submit-btn"
            disabled={loading}
          >
            <span>{loading ? 'Authenticating...' : 'Sign In To Network'}</span>
            <ArrowRight size={18} />
          </button>
        </form>

        {/* Quick Demo Login Preset Buttons */}
        <div className="auth-quick-roles">
          <span className="quick-roles-title">QUICK DEMO PROFILES</span>
          <div className="quick-roles-grid">
            <button type="button" onClick={() => handleQuickDemo('ravi.wf@test.com', 'Password@123')} className="role-chip">
              Donor (Ravi)
            </button>
            <button type="button" onClick={() => handleQuickDemo('arun.wf@test.com', 'Password@123')} className="role-chip">
              Recipient (Arun)
            </button>
            <button type="button" onClick={() => handleQuickDemo('tgh.wf@test.com', 'Password@123')} className="role-chip">
              Hospital (Trichy GH)
            </button>
            <button type="button" onClick={() => handleQuickDemo('admin@test.com', 'Password@123')} className="role-chip">
              Admin Portal
            </button>
          </div>
        </div>

        <div className="auth-footer-link">
          <span>New to the HEMO Network? </span>
          <Link to="/register" className="highlight-cta">Create Account</Link>
        </div>
      </div>

      <style>{`
        .hemo-auth-wrapper {
          min-height: calc(100vh - 80px);
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 3rem 1.5rem;
          background: #08080a;
          position: relative;
          overflow: hidden;
        }

        .auth-ambient-glow {
          position: absolute;
          width: 500px;
          height: 500px;
          background: radial-gradient(circle, rgba(225, 29, 72, 0.18) 0%, transparent 70%);
          border-radius: 50%;
          filter: blur(100px);
          pointer-events: none;
        }

        .hemo-auth-card {
          position: relative;
          z-index: 1;
          width: 100%;
          max-width: 460px;
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
          letter-spacing: -0.02em;
        }

        .auth-subtitle {
          font-size: 0.85rem;
          color: var(--text-muted);
        }

        .form-label-split {
          display: flex;
          align-items: center;
          justify-content: space-between;
        }
        .forgot-hint {
          font-size: 0.72rem;
          color: var(--text-dim);
          font-weight: 600;
        }

        .auth-submit-btn {
          width: 100%;
          padding: 0.9rem;
          margin-top: 0.75rem;
          font-size: 0.95rem;
        }

        .auth-quick-roles {
          margin-top: 2rem;
          padding-top: 1.75rem;
          border-top: 1px solid rgba(255, 255, 255, 0.08);
        }

        .quick-roles-title {
          display: block;
          font-size: 0.7rem;
          font-weight: 800;
          letter-spacing: 0.1em;
          color: var(--text-dim);
          margin-bottom: 0.75rem;
          text-align: center;
        }

        .quick-roles-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 0.5rem;
        }

        .role-chip {
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-sm);
          color: var(--text-muted);
          padding: 0.45rem 0.6rem;
          font-size: 0.75rem;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s ease;
          text-align: center;
        }
        .role-chip:hover {
          background: rgba(255, 255, 255, 0.1);
          color: #ffffff;
          border-color: rgba(255, 255, 255, 0.25);
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
      `}</style>
    </div>
  );
};

export default Login;
