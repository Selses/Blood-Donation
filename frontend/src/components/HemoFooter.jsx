import React from 'react';
import { Link } from 'react-router-dom';
import { Heart, Globe, Shield, ArrowUpRight } from 'lucide-react';

const HemoFooter = () => {
  return (
    <footer className="hemo-footer">
      <div className="container">
        {/* Top Grid */}
        <div className="footer-top-grid">
          <div className="footer-brand-col">
            <div className="footer-logo">
              <span className="logo-hemo">HEMO</span>
              <span className="logo-sym">&copy;</span>
            </div>
            <p className="footer-tagline">
              Autonomous Smart Blood Allocation & Emergency Match Infrastructure.
            </p>
            <div className="footer-social-icons">
              <span className="social-pill">X / Twitter</span>
              <span className="social-pill">Discord</span>
              <span className="social-pill">GitHub</span>
            </div>
          </div>

          <div className="footer-links-col">
            <h4 className="footer-col-title">Platform Links</h4>
            <ul className="footer-nav-list">
              <li><Link to="/blood-requests">Blood Requests</Link></li>
              <li><Link to="/inventory">Live Inventory</Link></li>
              <li><Link to="/notifications">Emergency Ticker</Link></li>
              <li><Link to="/login">Portal Access</Link></li>
            </ul>
          </div>

          <div className="footer-links-col">
            <h4 className="footer-col-title">Network Utilities</h4>
            <ul className="footer-nav-list">
              <li><a href="#hero">Style Guide</a></li>
              <li><a href="#events">Events Camp</a></li>
              <li><a href="#how-it-works">Donation Protocol</a></li>
              <li><a href="#faq">Clinical FAQ</a></li>
            </ul>
          </div>

          <div className="footer-credits-col">
            <div className="footer-badge-box">
              <Shield size={18} color="#e11d48" />
              <span>HIPAA Compliant &amp; 100% Encrypted</span>
            </div>
            <p className="credit-text">Engineered for Rapid Response</p>
          </div>
        </div>

        {/* Giant HEMO© Outline Brand Footprint as seen in video bottom */}
        <div className="footer-giant-wordmark">
          <span>HEMO</span>
          <span className="giant-copy">&copy;</span>
        </div>

        {/* Bottom copyright line */}
        <div className="footer-bottom-bar">
          <p>&copy; {new Date().getFullYear()} HEMO Blood Donation Network. All rights reserved.</p>
          <div className="bottom-meta-links">
            <span>Privacy Policy</span>
            <span>•</span>
            <span>Terms of Service</span>
            <span>•</span>
            <span>Clinical Verification</span>
          </div>
        </div>
      </div>

      <style>{`
        .hemo-footer {
          background: #050507;
          border-top: 1px solid rgba(255, 255, 255, 0.08);
          padding: 5rem 0 3rem;
          position: relative;
          overflow: hidden;
          width: 100%;
          max-width: 100vw;
          box-sizing: border-box;
        }

        .footer-top-grid {
          display: grid;
          grid-template-columns: 2fr 1fr 1fr 1.5fr;
          gap: 3rem;
          margin-bottom: 5rem;
        }

        .footer-logo {
          font-family: var(--font-heading);
          font-size: 2rem;
          font-weight: 900;
          letter-spacing: -0.04em;
          color: #ffffff;
          display: flex;
          align-items: baseline;
          gap: 0.25rem;
          margin-bottom: 1rem;
        }
        .logo-hemo { color: #ffffff; }
        .logo-sym { font-size: 1rem; color: var(--primary); }

        .footer-tagline {
          font-size: 0.88rem;
          color: var(--text-muted);
          line-height: 1.6;
          max-width: 320px;
          margin-bottom: 1.5rem;
        }

        .footer-social-icons {
          display: flex;
          gap: 0.5rem;
        }

        .social-pill {
          padding: 0.35rem 0.75rem;
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-full);
          font-size: 0.72rem;
          font-weight: 600;
          color: var(--text-muted);
          cursor: pointer;
          transition: all 0.2s ease;
        }
        .social-pill:hover {
          color: #ffffff;
          border-color: rgba(255, 255, 255, 0.3);
        }

        .footer-col-title {
          font-size: 0.85rem;
          font-weight: 700;
          letter-spacing: 0.05em;
          color: #ffffff;
          text-transform: uppercase;
          margin-bottom: 1.25rem;
        }

        .footer-nav-list {
          list-style: none;
          display: flex;
          flex-direction: column;
          gap: 0.75rem;
        }

        .footer-nav-list a {
          color: var(--text-muted);
          font-size: 0.88rem;
          transition: color 0.2s ease;
        }

        .footer-nav-list a:hover {
          color: #ffffff;
        }

        .footer-badge-box {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          padding: 0.85rem 1.15rem;
          background: rgba(255, 255, 255, 0.03);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-md);
          font-size: 0.8rem;
          font-weight: 600;
          color: #ffffff;
          margin-bottom: 1rem;
        }

        .credit-text {
          font-size: 0.8rem;
          color: var(--text-dim);
        }

        .footer-giant-wordmark {
          text-align: center;
          font-family: var(--font-heading);
          font-size: clamp(4rem, 14vw, 13rem);
          font-weight: 900;
          letter-spacing: -0.05em;
          color: transparent;
          -webkit-text-stroke: 1.5px rgba(255, 255, 255, 0.12);
          user-select: none;
          line-height: 0.85;
          margin-bottom: 3rem;
          display: flex;
          align-items: baseline;
          justify-content: center;
          gap: 1vw;
          max-width: 100%;
          overflow: hidden;
        }
        .giant-copy {
          font-size: 0.3em;
        }

        .footer-bottom-bar {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding-top: 2rem;
          border-top: 1px solid rgba(255, 255, 255, 0.06);
          font-size: 0.8rem;
          color: var(--text-dim);
          flex-wrap: wrap;
          gap: 1rem;
        }

        .bottom-meta-links {
          display: flex;
          align-items: center;
          gap: 0.75rem;
        }

        @media (max-width: 900px) {
          .footer-top-grid {
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
          }
        }
        @media (max-width: 600px) {
          .footer-top-grid {
            grid-template-columns: 1fr;
          }
        }
      `}</style>
    </footer>
  );
};

export default HemoFooter;
