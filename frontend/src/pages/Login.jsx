import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { HeartHandshake, LogIn, AlertCircle } from 'lucide-react';

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
    <div className="login-wrapper">
      <div className="login-card card">
        <div className="login-header">
          <div className="login-icon">
            <HeartHandshake size={32} color="#e11d48" />
          </div>
          <h1>Blood Donation Network</h1>
          <p>Emergency Matching & Inventory Management</p>
        </div>

        {error && (
          <div className="alert alert-danger">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input 
              type="email"
              className="form-input"
              placeholder="name@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input 
              type="password"
              className="form-input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button 
            type="submit" 
            className="btn btn-primary login-btn"
            disabled={loading}
          >
            <LogIn size={18} />
            <span>{loading ? 'Logging in...' : 'Sign In'}</span>
          </button>
        </form>

        <div className="quick-access">
          <p className="quick-title">QUICK DEMO LOGINS:</p>
          <div className="quick-buttons">
            <button type="button" onClick={() => handleQuickDemo('ravi.wf@test.com', 'Password@123')} className="btn btn-secondary btn-sm">Donor (Ravi)</button>
            <button type="button" onClick={() => handleQuickDemo('arun.wf@test.com', 'Password@123')} className="btn btn-secondary btn-sm">Recipient (Arun)</button>
            <button type="button" onClick={() => handleQuickDemo('tgh.wf@test.com', 'Password@123')} className="btn btn-secondary btn-sm">Hospital (Trichy GH)</button>
            <button type="button" onClick={() => handleQuickDemo('admin@test.com', 'Password@123')} className="btn btn-secondary btn-sm">Admin</button>
          </div>
        </div>

        <div className="footer-action">
          <span>Don't have an account? </span>
          <a href="/register" className="link-highlight">Register Now</a>
        </div>
      </div>

      <style>{`
        .login-wrapper {
          min-height: 100vh;
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 1.5rem;
          background: radial-gradient(circle at top, #1e1b4b 0%, #0f172a 70%);
        }
        .login-card {
          width: 100%;
          max-width: 440px;
          border-radius: var(--radius-lg);
          padding: 2.25rem;
          box-shadow: var(--shadow-glow), var(--shadow-lg);
        }
        .login-header {
          text-align: center;
          margin-bottom: 2rem;
        }
        .login-icon {
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
        .login-header h1 {
          font-size: 1.5rem;
          font-weight: 800;
          color: white;
          margin-bottom: 0.35rem;
        }
        .login-header p {
          color: var(--text-muted);
          font-size: 0.875rem;
        }
        .login-btn {
          width: 100%;
          padding: 0.8125rem;
          margin-top: 0.5rem;
        }
        .quick-access {
          margin-top: 2rem;
          padding-top: 1.5rem;
          border-top: 1px solid var(--border);
        }
        .quick-title {
          font-size: 0.75rem;
          color: var(--text-dim);
          text-transform: uppercase;
          letter-spacing: 0.05em;
          font-weight: 700;
          margin-bottom: 0.75rem;
          text-align: center;
        }
        .quick-buttons {
          display: flex;
          gap: 0.5rem;
          flex-wrap: wrap;
        }
        .btn-sm {
          padding: 0.375rem 0.625rem;
          font-size: 0.75rem;
          flex: 1;
        }
        .footer-action {
          margin-top: 1.5rem;
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
      `}</style>
    </div>
  );
};

export default Login;
