import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Droplets, ShieldCheck, Heart } from 'lucide-react';

const HemoHero = () => {
  const [mouseOffset, setMouseOffset] = useState({ x: 0, y: 0 });
  const [scrollY, setScrollY] = useState(0);
  const heroRef = useRef(null);

  useEffect(() => {
    const handleMouseMove = (e) => {
      const { innerWidth, innerHeight } = window;
      const x = (e.clientX - innerWidth / 2) / (innerWidth / 2);
      const y = (e.clientY - innerHeight / 2) / (innerHeight / 2);
      setMouseOffset({ x, y });
    };

    const handleScroll = () => {
      setScrollY(window.scrollY);
    };

    window.addEventListener('mousemove', handleMouseMove, { passive: true });
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => {
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);

  return (
    <section className="hemo-hero" ref={heroRef}>
      {/* Background ambient lighting */}
      <div 
        className="hero-glow-sphere top-glow" 
        style={{
          transform: `translate3d(${mouseOffset.x * 25}px, ${mouseOffset.y * 25 - scrollY * 0.15}px, 0)`
        }}
      />
      <div 
        className="hero-glow-sphere center-glow" 
        style={{
          transform: `translate3d(${-mouseOffset.x * 20}px, ${-mouseOffset.y * 20 - scrollY * 0.1}px, 0)`
        }}
      />

      <div className="container hero-container">
        {/* Top subtle badge */}
        <div className="hero-top-pill">
          <span className="pill-dot" />
          <span>Next-Gen Smart Blood Donation Network</span>
          <span className="pill-separator">•</span>
          <span className="pill-accent">Live 24/7 Matching</span>
        </div>

        {/* Editorial Statement - as seen in 00:00 & 00:37 */}
        <div className="hero-editorial-statement">
          By connecting <strong className="text-highlight">donors</strong> with those in need, we ensure a steady, <strong className="text-highlight">safe blood supply</strong> for hospitals and patients.
        </div>

        {/* Giant Editorial Headline + 3D Erythrocyte */}
        <div className="hero-giant-row">
          <div className="hero-typography-block">
            <h1 className="hero-title">
              <span className="title-row-1">BLOOD</span>
              <span className="title-row-2">DONATION</span>
            </h1>
          </div>

          {/* 3D Realistic Erythrocyte (Red Blood Cell) */}
          <div 
            className="hero-cell-stage"
            style={{
              transform: `perspective(1000px) rotateY(${mouseOffset.x * 15}deg) rotateX(${-mouseOffset.y * 15}deg) translateY(${scrollY * 0.08}px)`
            }}
          >
            <div className="erythrocyte-3d">
              <div className="cell-outer-rim" />
              <div className="cell-concave-core" />
              <div className="cell-specular-sheen" />
              <div className="cell-glow-rim" />
            </div>
            <div 
              className="cell-drop-shadow"
              style={{
                transform: `translateX(${mouseOffset.x * 12}px) scale(${1 - Math.abs(mouseOffset.y) * 0.1})`
              }}
            />
          </div>
        </div>

        {/* Bottom Bar: Action Buttons & Mission Caption */}
        <div className="hero-bottom-bar">
          <div className="hero-action-buttons">
            <Link to="/register" className="btn btn-primary btn-hero-cta">
              <span>Join The Mission</span>
              <ArrowRight size={18} />
            </Link>

            <Link to="/login" className="btn btn-outline btn-hero-secondary">
              <span>Access Portal</span>
            </Link>
          </div>

          <div className="hero-mission-caption">
            <p className="caption-bold">Making every donation count</p>
            <p className="caption-dim">toward a healthier, connected future</p>
          </div>
        </div>

        {/* Real-time Ticker Metrics */}
        <div className="hero-metrics-strip">
          <div className="metric-pill">
            <div className="metric-icon-box"><Droplets size={16} /></div>
            <div>
              <span className="metric-value">100%</span>
              <span className="metric-name">Verified Compatibility</span>
            </div>
          </div>

          <div className="metric-pill">
            <div className="metric-icon-box"><ShieldCheck size={16} /></div>
            <div>
              <span className="metric-value">&lt; 15 mins</span>
              <span className="metric-name">Average Emergency Match</span>
            </div>
          </div>

          <div className="metric-pill">
            <div className="metric-icon-box"><Heart size={16} /></div>
            <div>
              <span className="metric-value">Zero Waste</span>
              <span className="metric-name">Automated Expiry Routing</span>
            </div>
          </div>
        </div>
      </div>

      <style>{`
        .hemo-hero {
          position: relative;
          min-height: calc(100vh - 75px);
          display: flex;
          align-items: center;
          padding: 2.5rem 0 4rem;
          background: #08080a;
          overflow: hidden;
          width: 100%;
          max-width: 100vw;
          box-sizing: border-box;
        }

        .hero-glow-sphere {
          position: absolute;
          border-radius: 50%;
          filter: blur(100px);
          pointer-events: none;
          z-index: 0;
          will-change: transform;
        }
        .top-glow {
          top: -5%;
          right: 10%;
          width: clamp(280px, 40vw, 550px);
          height: clamp(280px, 40vw, 550px);
          background: radial-gradient(circle, rgba(225, 29, 72, 0.22) 0%, rgba(225, 29, 72, 0) 70%);
        }
        .center-glow {
          bottom: 5%;
          left: 5%;
          width: clamp(240px, 35vw, 450px);
          height: clamp(240px, 35vw, 450px);
          background: radial-gradient(circle, rgba(159, 18, 57, 0.18) 0%, rgba(8, 8, 10, 0) 70%);
        }

        .hero-container {
          position: relative;
          z-index: 1;
          width: 100%;
          max-width: 1240px;
          margin: 0 auto;
          padding: 0 1.5rem;
          box-sizing: border-box;
        }

        .hero-top-pill {
          display: inline-flex;
          align-items: center;
          gap: 0.65rem;
          padding: 0.4rem 0.95rem;
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.1);
          border-radius: 9999px;
          font-size: 0.76rem;
          color: #9499a6;
          margin-bottom: 2rem;
          backdrop-filter: blur(10px);
          max-width: 100%;
          flex-wrap: wrap;
        }
        .pill-dot {
          width: 7px;
          height: 7px;
          background: #e11d48;
          border-radius: 50%;
          box-shadow: 0 0 8px #e11d48;
          animation: pulse-badge 2s infinite;
        }
        .pill-separator { color: rgba(255, 255, 255, 0.2); }
        .pill-accent { color: #ffffff; font-weight: 600; }

        .hero-editorial-statement {
          max-width: 680px;
          font-size: clamp(1.15rem, 2.2vw, 1.45rem);
          line-height: 1.55;
          color: #8b92a4;
          margin-bottom: 2rem;
          font-weight: 400;
          letter-spacing: -0.01em;
        }
        .text-highlight {
          color: #ffffff;
          font-weight: 700;
        }

        .hero-giant-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          gap: 2rem;
          position: relative;
          margin-bottom: 2.5rem;
          width: 100%;
        }

        .hero-typography-block {
          flex: 1;
          min-width: 0;
        }

        .hero-title {
          font-family: var(--font-heading);
          font-size: clamp(3rem, 7.8vw, 7.5rem);
          font-weight: 900;
          line-height: 0.92;
          letter-spacing: -0.04em;
          text-transform: uppercase;
          color: #ffffff;
          margin: 0;
          word-break: break-word;
        }

        .title-row-1 {
          display: block;
          color: #ffffff;
        }

        .title-row-2 {
          display: block;
          color: transparent;
          -webkit-text-stroke: 1.5px rgba(255, 255, 255, 0.9);
          letter-spacing: -0.03em;
        }

        /* 3D Erythrocyte (Red Blood Cell) */
        .hero-cell-stage {
          position: relative;
          width: clamp(180px, 22vw, 290px);
          height: clamp(180px, 22vw, 290px);
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          transition: transform 0.2s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .erythrocyte-3d {
          position: relative;
          width: 82%;
          height: 82%;
          border-radius: 46% 54% 50% 50% / 50% 48% 52% 50%;
          background: radial-gradient(circle at 35% 30%, #ff4d6d 0%, #e11d48 35%, #881337 75%, #30030e 100%);
          box-shadow: 
            inset -18px -18px 40px rgba(0, 0, 0, 0.85),
            inset 12px 12px 30px rgba(255, 120, 150, 0.7),
            0 25px 60px rgba(225, 29, 72, 0.45);
          animation: erythrocyte-float 6s ease-in-out infinite alternate;
        }

        .cell-concave-core {
          position: absolute;
          top: 50%;
          left: 50%;
          width: 44%;
          height: 44%;
          transform: translate(-50%, -50%);
          border-radius: 50%;
          background: radial-gradient(circle, #250209 15%, #60081d 70%, transparent 100%);
          box-shadow: inset 0 0 25px rgba(0, 0, 0, 0.95);
          opacity: 0.9;
        }

        .cell-specular-sheen {
          position: absolute;
          top: 15%;
          left: 20%;
          width: 32%;
          height: 18%;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.55);
          filter: blur(5px);
          transform: rotate(-35deg);
        }

        .cell-glow-rim {
          position: absolute;
          inset: -10px;
          border-radius: 50%;
          border: 1px dashed rgba(225, 29, 72, 0.25);
          animation: spin-slow 30s linear infinite;
        }

        .cell-drop-shadow {
          position: absolute;
          bottom: -15px;
          width: 75%;
          height: 25px;
          background: radial-gradient(ellipse, rgba(0, 0, 0, 0.75) 0%, transparent 70%);
          filter: blur(8px);
          border-radius: 50%;
        }

        @keyframes erythrocyte-float {
          0% { transform: translateY(0) rotate(0deg) scale(1); }
          50% { transform: translateY(-14px) rotate(3deg) scale(1.02); }
          100% { transform: translateY(5px) rotate(-2deg) scale(0.99); }
        }

        @keyframes spin-slow {
          from { transform: rotate(0deg); }
          to { transform: rotate(360deg); }
        }

        .hero-bottom-bar {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding-top: 1.75rem;
          border-top: 1px solid rgba(255, 255, 255, 0.08);
          margin-bottom: 2rem;
          flex-wrap: wrap;
          gap: 1.25rem;
          width: 100%;
        }

        .hero-action-buttons {
          display: flex;
          align-items: center;
          gap: 1rem;
          flex-wrap: wrap;
        }

        .btn-hero-cta {
          padding: 0.85rem 2rem;
          font-size: 0.95rem;
        }

        .btn-hero-secondary {
          padding: 0.85rem 1.6rem;
          font-size: 0.95rem;
        }

        .hero-mission-caption {
          text-align: right;
        }
        .caption-bold {
          font-weight: 700;
          color: #ffffff;
          font-size: 0.95rem;
        }
        .caption-dim {
          color: #8b92a4;
          font-size: 0.82rem;
        }

        .hero-metrics-strip {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 1rem;
          width: 100%;
        }

        .metric-pill {
          display: flex;
          align-items: center;
          gap: 0.9rem;
          padding: 0.85rem 1.15rem;
          background: rgba(18, 20, 26, 0.65);
          border: 1px solid rgba(255, 255, 255, 0.07);
          border-radius: 14px;
          backdrop-filter: blur(12px);
          min-width: 0;
        }

        .metric-icon-box {
          width: 38px;
          height: 38px;
          border-radius: 10px;
          background: rgba(225, 29, 72, 0.15);
          color: #e11d48;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }

        .metric-value {
          display: block;
          font-size: 1.1rem;
          font-weight: 800;
          color: #ffffff;
          line-height: 1.2;
          white-space: nowrap;
        }
        .metric-name {
          display: block;
          font-size: 0.72rem;
          color: #9499a6;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        @media (max-width: 860px) {
          .hero-giant-row {
            flex-direction: column-reverse;
            align-items: flex-start;
            gap: 1.5rem;
          }
          .hero-cell-stage {
            align-self: center;
            width: 170px;
            height: 170px;
          }
          .hero-mission-caption {
            text-align: left;
          }
          .hero-metrics-strip {
            grid-template-columns: 1fr;
          }
        }
      `}</style>
    </section>
  );
};

export default HemoHero;
