import React, { useState, useEffect, useRef } from 'react';
import { ArrowUpRight, Activity, Syringe, ShieldAlert, HeartHandshake } from 'lucide-react';
import { Link } from 'react-router-dom';

const HemoFeaturesGrid = () => {
  const [hoveredIdx, setHoveredIdx] = useState(null);
  const [scrollProgress, setScrollProgress] = useState(0);
  const sectionRef = useRef(null);

  useEffect(() => {
    const handleScroll = () => {
      if (!sectionRef.current) return;
      const rect = sectionRef.current.getBoundingClientRect();
      const windowHeight = window.innerHeight;
      
      // Calculate how far through the section the viewport is
      const progress = (windowHeight - rect.top) / (windowHeight + rect.height);
      setScrollProgress(Math.min(Math.max(progress, 0), 1));
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const features = [
    {
      id: 1,
      title: 'Donor Bags & Reserves',
      subtitle: 'Safe Sterile Storage',
      tag: '01 / RESERVE',
      icon: <Activity size={22} color="#e11d48" />,
      speedOffset: -20, // Parallax velocity delta
      illustration: (
        <div className="bag-illustration">
          <div className="bag-tube" />
          <div className="bag-body">
            <div className="bag-label-card">
              <span className="blood-type-marker">O- NEGATIVE</span>
              <div className="bag-barcode" />
            </div>
            <div className="bag-liquid-fill" />
            <div className="bag-glass-sheen" />
          </div>
        </div>
      ),
      description: 'Hermetically preserved units tracked with temperature sensors and RFID for zero spoilage.'
    },
    {
      id: 2,
      title: 'Precision Screening',
      subtitle: 'Laboratory Verified',
      tag: '02 / TESTING',
      icon: <Syringe size={22} color="#38bdf8" />,
      speedOffset: 15,
      illustration: (
        <div className="vial-illustration">
          <div className="glove-silhouette" />
          <div className="test-vial">
            <div className="vial-cap" />
            <div className="vial-fluid" />
            <div className="vial-reflection" />
            <div className="vial-graduation-marks">
              <span /><span /><span /><span />
            </div>
          </div>
        </div>
      ),
      description: 'Comprehensive 8-point infectious screening with algorithmic blood cross-matching.'
    },
    {
      id: 3,
      title: 'Blood Request',
      subtitle: 'Emergency Support',
      tag: '03 / DISPATCH',
      icon: <ShieldAlert size={22} color="#f59e0b" />,
      speedOffset: -30,
      illustration: (
        <div className="kit-illustration">
          <div className="medical-chest">
            <div className="chest-handle" />
            <div className="chest-cross">+</div>
            <div className="chest-latch latch-left" />
            <div className="chest-latch latch-right" />
            <div className="chest-accent-strip" />
          </div>
          {/* Frosted Glass Overlay Tag matching video 00:03 */}
          <div className="kit-floating-tag">
            <span className="tag-headline">Blood Request</span>
            <span className="tag-subline">Emergency Support</span>
          </div>
        </div>
      ),
      description: 'Instant hospital triage broadcast dispatching emergency units in under 15 minutes.'
    },
    {
      id: 4,
      title: 'Life Delivered',
      subtitle: 'Patient Recovery',
      tag: '04 / IMPACT',
      icon: <HeartHandshake size={22} color="#10b981" />,
      speedOffset: 25,
      illustration: (
        <div className="balloon-illustration">
          <div className="glossy-drop">
            <div className="drop-shine" />
            <div className="drop-core" />
          </div>
          <div className="drop-string" />
          <div className="hand-silhouette-balloon" />
        </div>
      ),
      description: 'Direct verification loops confirming patient stabilization and donor impact reporting.'
    }
  ];

  return (
    <section className="hemo-features-section" ref={sectionRef}>
      <div className="container">
        <div className="section-head-minimal">
          <div className="head-pill-tag">
            <span className="pill-dot" />
            <span>CRITICAL INFRASTRUCTURE</span>
          </div>
          <h2 className="section-title-clean">PARALLEL SUPPLY CHAIN</h2>
        </div>

        {/* 4 Parallel Scrolling Columns */}
        <div className="features-grid-4">
          {features.map((item, idx) => {
            // Calculate parallel scroll translation based on progress
            const parallaxY = (scrollProgress - 0.5) * item.speedOffset;

            return (
              <div 
                key={item.id} 
                className={`feature-col-card ${hoveredIdx === idx ? 'is-active' : ''}`}
                onMouseEnter={() => setHoveredIdx(idx)}
                onMouseLeave={() => setHoveredIdx(null)}
                style={{
                  transform: hoveredIdx === idx 
                    ? `translateY(${parallaxY - 8}px) scale(1.02)` 
                    : `translateY(${parallaxY}px)`
                }}
              >
                <div className="card-top-tag">{item.tag}</div>

                {/* Visual Stage */}
                <div className="feature-stage-view">
                  <div className="stage-ambient-glow" />
                  {item.illustration}
                </div>

                {/* Content */}
                <div className="feature-body-content">
                  <div className="feature-icon-bubble">{item.icon}</div>
                  <div className="feature-title-block">
                    <h3 className="feature-main-title">{item.title}</h3>
                    <p className="feature-subtitle">{item.subtitle}</p>
                  </div>
                  <p className="feature-desc-text">{item.description}</p>
                  
                  <Link to="/blood-requests" className="feature-link-pill">
                    <span>Explore Spec</span>
                    <ArrowUpRight size={14} />
                  </Link>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <style>{`
        .hemo-features-section {
          padding: 5rem 0 7rem;
          background: #08080a;
          position: relative;
          width: 100%;
          max-width: 100vw;
          overflow: hidden;
          box-sizing: border-box;
        }

        .section-head-minimal {
          margin-bottom: 3.5rem;
        }

        .head-pill-tag {
          display: inline-flex;
          align-items: center;
          gap: 0.5rem;
          padding: 0.35rem 0.85rem;
          background: rgba(225, 29, 72, 0.12);
          border: 1px solid rgba(225, 29, 72, 0.25);
          border-radius: 9999px;
          font-size: 0.72rem;
          font-weight: 700;
          color: #ff4d6d;
          margin-bottom: 0.85rem;
          letter-spacing: 0.08em;
        }

        .section-title-clean {
          font-family: var(--font-heading);
          font-size: clamp(2rem, 4vw, 3.2rem);
          font-weight: 900;
          letter-spacing: -0.03em;
          color: #ffffff;
          line-height: 1.1;
        }

        /* 4 Columns with Parallel Scrolling Motion */
        .features-grid-4 {
          display: grid;
          grid-template-columns: repeat(4, 1fr);
          gap: 1.25rem;
          width: 100%;
          box-sizing: border-box;
        }

        .feature-col-card {
          background: #101217;
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: 20px;
          overflow: hidden;
          display: flex;
          flex-direction: column;
          position: relative;
          transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), border-color 0.25s, box-shadow 0.25s;
          will-change: transform;
        }

        .feature-col-card:hover, .feature-col-card.is-active {
          border-color: rgba(225, 29, 72, 0.4);
          box-shadow: 0 25px 50px rgba(0, 0, 0, 0.85), 0 0 30px rgba(225, 29, 72, 0.18);
          z-index: 2;
        }

        .card-top-tag {
          position: absolute;
          top: 1rem;
          left: 1.25rem;
          z-index: 3;
          font-size: 0.68rem;
          font-weight: 800;
          letter-spacing: 0.08em;
          color: rgba(255, 255, 255, 0.45);
        }

        .feature-stage-view {
          height: 250px;
          display: flex;
          align-items: center;
          justify-content: center;
          position: relative;
          background: #14171f;
          border-bottom: 1px solid rgba(255, 255, 255, 0.06);
          overflow: hidden;
        }

        .stage-ambient-glow {
          position: absolute;
          inset: 0;
          background: radial-gradient(circle at 50% 40%, rgba(225, 29, 72, 0.12) 0%, transparent 70%);
          pointer-events: none;
        }

        /* 1. Blood Bag Graphic */
        .bag-illustration {
          position: relative;
          width: 105px;
          height: 140px;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        .bag-body {
          width: 92px;
          height: 115px;
          background: rgba(255, 255, 255, 0.08);
          border: 2px solid rgba(255, 255, 255, 0.35);
          border-radius: 12px 12px 22px 22px;
          position: relative;
          overflow: hidden;
          backdrop-filter: blur(8px);
          box-shadow: 0 15px 30px rgba(0, 0, 0, 0.5);
        }
        .bag-tube {
          width: 5px;
          height: 22px;
          background: rgba(225, 29, 72, 0.7);
          position: absolute;
          top: 0px;
          left: 50%;
          transform: translateX(-50%);
          border-radius: 3px;
        }
        .bag-liquid-fill {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 68%;
          background: linear-gradient(180deg, #e11d48 0%, #7f0d2c 100%);
          box-shadow: inset 0 2px 10px rgba(255, 255, 255, 0.35);
        }
        .bag-label-card {
          position: absolute;
          top: 14px;
          left: 8px;
          right: 8px;
          background: #ffffff;
          padding: 4px 6px;
          border-radius: 4px;
          z-index: 2;
          box-shadow: 0 2px 6px rgba(0,0,0,0.3);
        }
        .blood-type-marker {
          font-size: 0.58rem;
          font-weight: 800;
          color: #e11d48;
          display: block;
          text-align: center;
          letter-spacing: 0.05em;
        }
        .bag-barcode {
          height: 4px;
          background: repeating-linear-gradient(90deg, #000 0, #000 2px, transparent 2px, transparent 4px);
          margin-top: 3px;
        }
        .bag-glass-sheen {
          position: absolute;
          top: 0;
          left: 0;
          width: 50%;
          height: 100%;
          background: linear-gradient(90deg, rgba(255, 255, 255, 0.15) 0%, transparent 100%);
          pointer-events: none;
        }

        /* 2. Test Vial Graphic */
        .vial-illustration {
          position: relative;
          display: flex;
          align-items: center;
          justify-content: center;
          height: 160px;
        }
        .glove-silhouette {
          position: absolute;
          width: 80px;
          height: 60px;
          background: rgba(255, 255, 255, 0.12);
          border-radius: 30px;
          left: -20px;
          top: 50px;
          filter: blur(1px);
        }
        .test-vial {
          width: 32px;
          height: 125px;
          background: rgba(255, 255, 255, 0.08);
          border: 2px solid rgba(255, 255, 255, 0.45);
          border-radius: 6px 6px 16px 16px;
          position: relative;
          overflow: hidden;
          box-shadow: 0 12px 30px rgba(0, 0, 0, 0.6);
          transform: rotate(14deg);
        }
        .vial-cap {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 22px;
          background: #38bdf8;
          border-bottom: 2px solid #0284c7;
        }
        .vial-fluid {
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 68%;
          background: linear-gradient(180deg, #e11d48 0%, #4c0519 100%);
        }
        .vial-reflection {
          position: absolute;
          top: 25px;
          left: 4px;
          width: 3px;
          height: 75px;
          background: rgba(255, 255, 255, 0.5);
          border-radius: 2px;
        }
        .vial-graduation-marks {
          position: absolute;
          right: 4px;
          top: 45px;
          display: flex;
          flex-direction: column;
          gap: 8px;
        }
        .vial-graduation-marks span {
          width: 5px;
          height: 1px;
          background: rgba(255, 255, 255, 0.5);
        }

        /* 3. Emergency Kit Graphic */
        .kit-illustration {
          position: relative;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
        }
        .medical-chest {
          width: 110px;
          height: 85px;
          background: linear-gradient(135deg, #e11d48 0%, #9f1239 100%);
          border-radius: 12px;
          position: relative;
          box-shadow: 0 18px 35px rgba(225, 29, 72, 0.35);
          display: flex;
          align-items: center;
          justify-content: center;
        }
        .chest-cross {
          color: #ffffff;
          font-size: 2.2rem;
          font-weight: 900;
          line-height: 1;
        }
        .chest-handle {
          position: absolute;
          top: -14px;
          width: 44px;
          height: 14px;
          border: 3px solid #ffffff;
          border-bottom: none;
          border-radius: 6px 6px 0 0;
        }
        .chest-latch {
          position: absolute;
          top: 38%;
          width: 6px;
          height: 16px;
          background: #f8fafc;
          border-radius: 2px;
          box-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }
        .latch-left { left: -3px; }
        .latch-right { right: -3px; }
        .chest-accent-strip {
          position: absolute;
          bottom: 12px;
          left: 10px;
          right: 10px;
          height: 2px;
          background: rgba(255, 255, 255, 0.3);
        }

        /* Frosted Glass Overlay Tag */
        .kit-floating-tag {
          margin-top: 15px;
          background: rgba(18, 20, 26, 0.85);
          backdrop-filter: blur(12px);
          border: 1px solid rgba(255, 255, 255, 0.12);
          border-radius: 8px;
          padding: 6px 12px;
          text-align: center;
          box-shadow: 0 8px 18px rgba(0, 0, 0, 0.5);
        }
        .tag-headline {
          display: block;
          font-size: 0.8rem;
          font-weight: 800;
          color: #ffffff;
        }
        .tag-subline {
          display: block;
          font-size: 0.68rem;
          color: #ff4d6d;
          font-weight: 600;
        }

        /* 4. Glossy Balloon Graphic */
        .balloon-illustration {
          position: relative;
          display: flex;
          flex-direction: column;
          align-items: center;
        }
        .glossy-drop {
          width: 75px;
          height: 100px;
          border-radius: 50% 50% 50% 50% / 60% 60% 40% 40%;
          background: radial-gradient(circle at 35% 30%, #ff4d6d 0%, #be123c 60%, #4c0519 100%);
          position: relative;
          box-shadow: 0 16px 35px rgba(225, 29, 72, 0.45);
          animation: float-balloon 4s ease-in-out infinite alternate;
        }
        .drop-shine {
          position: absolute;
          top: 16px;
          left: 16px;
          width: 18px;
          height: 26px;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.65);
          filter: blur(2px);
          transform: rotate(-30deg);
        }
        .drop-core {
          position: absolute;
          bottom: 15px;
          left: 20px;
          right: 20px;
          height: 30px;
          border-radius: 50%;
          background: radial-gradient(circle, rgba(76, 5, 25, 0.8) 0%, transparent 80%);
        }
        .drop-string {
          width: 2px;
          height: 40px;
          background: rgba(255, 255, 255, 0.35);
          margin-top: 2px;
        }
        .hand-silhouette-balloon {
          width: 30px;
          height: 20px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 10px;
          margin-top: -5px;
          filter: blur(1px);
        }

        @keyframes float-balloon {
          0% { transform: translateY(0) rotate(0deg); }
          100% { transform: translateY(-10px) rotate(2deg); }
        }

        /* Card Content */
        .feature-body-content {
          padding: 1.5rem;
          display: flex;
          flex-direction: column;
          flex-grow: 1;
        }

        .feature-icon-bubble {
          margin-bottom: 0.85rem;
        }

        .feature-main-title {
          font-size: 1.15rem;
          font-weight: 800;
          color: #ffffff;
          margin-bottom: 0.2rem;
        }

        .feature-subtitle {
          font-size: 0.78rem;
          color: #8b92a4;
          font-weight: 500;
          margin-bottom: 0.75rem;
        }

        .feature-desc-text {
          font-size: 0.82rem;
          color: #9499a6;
          line-height: 1.5;
          margin-bottom: 1.25rem;
          flex-grow: 1;
        }

        .feature-link-pill {
          display: inline-flex;
          align-items: center;
          justify-content: space-between;
          padding: 0.45rem 0.9rem;
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.1);
          border-radius: 9999px;
          font-size: 0.76rem;
          font-weight: 600;
          color: #ffffff;
          transition: all 0.2s ease;
        }

        .feature-link-pill:hover {
          background: #ffffff;
          color: #08080a;
          border-color: #ffffff;
        }

        @media (max-width: 1024px) {
          .features-grid-4 { grid-template-columns: repeat(2, 1fr); }
        }
        @media (max-width: 600px) {
          .features-grid-4 { grid-template-columns: 1fr; }
        }
      `}</style>
    </section>
  );
};

export default HemoFeaturesGrid;
