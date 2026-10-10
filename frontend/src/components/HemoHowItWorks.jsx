import React, { useState, useEffect, useRef } from 'react';
import { ArrowRight, Droplets, Heart, Sparkles, CheckCircle2 } from 'lucide-react';
import { Link } from 'react-router-dom';

const HemoHowItWorks = () => {
  const [scrollY, setScrollY] = useState(0);
  const [pillsExpanded, setPillsExpanded] = useState(false);
  const sectionRef = useRef(null);

  useEffect(() => {
    const handleScroll = () => {
      if (!sectionRef.current) return;
      const rect = sectionRef.current.getBoundingClientRect();
      const windowHeight = window.innerHeight;
      
      // Expand pills when section reaches 75% of viewport
      if (rect.top < windowHeight * 0.75) {
        setPillsExpanded(true);
      }

      // Parallax scroll on steps
      const progress = (windowHeight - rect.top) / (windowHeight + rect.height);
      setScrollY(progress * 100);
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const stepsLeft = [
    {
      number: '01',
      title: 'Registration',
      description: 'Create an account or walk in with a valid ID. Fill out a quick health questionnaire to confirm eligibility.'
    },
    {
      number: '03',
      title: 'Donate Blood',
      description: 'Relax in a comfortable chair while our professionals collect your blood. The process takes just 8-10 minutes.'
    }
  ];

  const stepsRight = [
    {
      number: '02',
      title: 'Health Check',
      description: "We'll check your pulse, blood pressure, temperature, and hemoglobin to ensure you're ready to donate safely."
    },
    {
      number: '04',
      title: 'Refresh & Recover',
      description: "Enjoy a free snack and drink while resting for 10-15 minutes. You're done and already saving lives."
    }
  ];

  return (
    <section className="hemo-how-section" ref={sectionRef}>
      <div className="container">
        {/* Editorial Transition Quote matching video 00:14 - 00:16 */}
        <div className="editorial-transition-box">
          <p className="transition-quote-text">
            Blood donation is a vital act of generosity that saves lives by providing essential blood components to patients in need. Regular donors play a crucial role in maintaining a stable blood supply for hospitals and emergency situations.
          </p>
          <div className="transition-action-row">
            <Link to="/blood-requests" className="btn btn-outline btn-learn-more">
              <span>Learn More</span>
              <ArrowRight size={16} />
            </Link>
          </div>
        </div>

        {/* Dynamic Expanding Headline matching video 00:17 - 00:20 */}
        <div className="dynamic-how-header">
          <div className="headline-row row-1">
            <span className="headline-text">HOW</span>
            <div className={`dynamic-pill pill-drop ${pillsExpanded ? 'expanded' : ''}`}>
              <span className="pill-icon"><Droplets size={18} color="#e11d48" /></span>
              <span className="pill-inner-txt">Safe &amp; Verified</span>
            </div>
          </div>

          <div className="headline-row row-2">
            <div className={`dynamic-pill pill-heart ${pillsExpanded ? 'expanded' : ''}`}>
              <span className="pill-icon"><Heart size={18} color="#ffffff" /></span>
              <span className="pill-inner-txt">Clinical Protocol</span>
            </div>
            <span className="headline-text">DONATION</span>
          </div>

          <div className="headline-row row-3">
            <span className="headline-text">WORKS</span>
            <div className={`dynamic-pill pill-process ${pillsExpanded ? 'expanded' : ''}`}>
              <span className="pill-dot-pulse" />
              <span className="pill-inner-txt">It's quick and simple process</span>
            </div>
          </div>
        </div>

        {/* 2 Staggered Parallel Columns matching video 00:21 - 00:28 */}
        <div className="staggered-parallel-container">
          {/* Column 1: Steps 01 & 03 */}
          <div 
            className="parallel-column col-left"
            style={{
              transform: `translateY(${Math.max((scrollY - 50) * -0.4, -40)}px)`
            }}
          >
            {stepsLeft.map((step) => (
              <div key={step.number} className="step-item-card">
                <div className="step-card-header">
                  <h3 className="step-item-title">{step.title}</h3>
                  <span className="step-giant-number">{step.number}</span>
                </div>
                <p className="step-item-desc">{step.description}</p>
              </div>
            ))}
          </div>

          {/* Column 2: Steps 02 & 04 (Staggered Downwards) */}
          <div 
            className="parallel-column col-right"
            style={{
              transform: `translateY(${Math.max((scrollY - 50) * 0.4, 0)}px)`
            }}
          >
            {stepsRight.map((step) => (
              <div key={step.number} className="step-item-card">
                <div className="step-card-header">
                  <h3 className="step-item-title">{step.title}</h3>
                  <span className="step-giant-number">{step.number}</span>
                </div>
                <p className="step-item-desc">{step.description}</p>
              </div>
            ))}
          </div>
        </div>
      </div>

      <style>{`
        .hemo-how-section {
          padding: 6rem 0 8rem;
          background: #08080a;
          position: relative;
          width: 100%;
          max-width: 100vw;
          overflow: hidden;
          box-sizing: border-box;
        }

        /* Transition Statement Box (00:14) */
        .editorial-transition-box {
          max-width: 780px;
          margin-bottom: 5rem;
          padding-bottom: 3.5rem;
          border-bottom: 1px solid rgba(255, 255, 255, 0.08);
        }

        .transition-quote-text {
          font-size: clamp(1.2rem, 2.3vw, 1.6rem);
          line-height: 1.6;
          color: #d1d5db;
          margin-bottom: 2rem;
          font-weight: 400;
          letter-spacing: -0.01em;
        }

        .transition-action-row {
          display: flex;
          align-items: center;
        }

        .btn-learn-more {
          padding: 0.65rem 1.6rem;
          font-size: 0.88rem;
          border-radius: 9999px;
          border-color: rgba(255, 255, 255, 0.2);
        }

        /* Dynamic Headline with Expanding Capsules (00:17 - 00:20) */
        .dynamic-how-header {
          margin-bottom: 5.5rem;
          display: flex;
          flex-direction: column;
          gap: 0.5rem;
        }

        .headline-row {
          display: flex;
          align-items: center;
          gap: 1.25rem;
          flex-wrap: wrap;
        }

        .headline-text {
          font-family: var(--font-heading);
          font-size: clamp(2.8rem, 7vw, 6.5rem);
          font-weight: 900;
          line-height: 0.95;
          letter-spacing: -0.04em;
          color: #ffffff;
          text-transform: uppercase;
        }

        /* Expanding Dynamic Pill Capsules */
        .dynamic-pill {
          display: inline-flex;
          align-items: center;
          gap: 0.75rem;
          height: clamp(42px, 5.5vw, 68px);
          padding: 0 1.4rem;
          border-radius: 9999px;
          transition: all 0.65s cubic-bezier(0.16, 1, 0.3, 1);
          overflow: hidden;
          white-space: nowrap;
          box-shadow: 0 10px 25px rgba(0, 0, 0, 0.6);
          max-width: 80px;
          opacity: 0.85;
        }

        .dynamic-pill.expanded {
          max-width: 450px;
          opacity: 1;
        }

        .pill-drop {
          background: #ffffff;
          color: #08080a;
        }

        .pill-heart {
          background: #e11d48;
          color: #ffffff;
        }

        .pill-process {
          background: rgba(255, 255, 255, 0.08);
          border: 1px solid rgba(255, 255, 255, 0.18);
          color: #ffffff;
          backdrop-filter: blur(10px);
        }

        .pill-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }

        .pill-inner-txt {
          font-size: clamp(0.78rem, 1.4vw, 1.05rem);
          font-weight: 700;
          letter-spacing: -0.01em;
        }

        .pill-dot-pulse {
          width: 9px;
          height: 9px;
          background: #e11d48;
          border-radius: 50%;
          box-shadow: 0 0 10px #e11d48;
          animation: pulse-badge 2s infinite;
          flex-shrink: 0;
        }

        /* 2 Staggered Parallel Columns (00:21 - 00:28) */
        .staggered-parallel-container {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 2rem;
          width: 100%;
          box-sizing: border-box;
        }

        .parallel-column {
          display: flex;
          flex-direction: column;
          gap: 2rem;
          will-change: transform;
          transition: transform 0.15s ease-out;
        }

        .col-right {
          margin-top: 5rem;
        }

        .step-item-card {
          background: #111319;
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: 20px;
          padding: 2.25rem 2rem;
          box-shadow: 0 15px 35px rgba(0, 0, 0, 0.6);
          transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .step-item-card:hover {
          background: #151821;
          border-color: rgba(225, 29, 72, 0.4);
          transform: translateY(-4px);
          box-shadow: 0 20px 45px rgba(0, 0, 0, 0.8), 0 0 25px rgba(225, 29, 72, 0.12);
        }

        .step-card-header {
          display: flex;
          align-items: baseline;
          justify-content: space-between;
          margin-bottom: 1.25rem;
        }

        .step-item-title {
          font-family: var(--font-heading);
          font-size: clamp(1.4rem, 2.5vw, 1.85rem);
          font-weight: 800;
          color: #ffffff;
          letter-spacing: -0.02em;
        }

        .step-giant-number {
          font-family: var(--font-heading);
          font-size: clamp(2.4rem, 4vw, 3.2rem);
          font-weight: 900;
          color: rgba(255, 255, 255, 0.9);
          line-height: 1;
          letter-spacing: -0.04em;
        }

        .step-item-desc {
          font-size: 0.95rem;
          color: #9499a6;
          line-height: 1.6;
        }

        @media (max-width: 800px) {
          .staggered-parallel-container {
            grid-template-columns: 1fr;
            gap: 1.5rem;
          }
          .col-right {
            margin-top: 0;
          }
          .parallel-column {
            transform: none !important;
          }
        }
      `}</style>
    </section>
  );
};

export default HemoHowItWorks;
