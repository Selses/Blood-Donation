import React, { useState, useEffect, useRef } from 'react';
import { Calendar, MapPin, ChevronLeft, ChevronRight, Award, Users, ArrowUpRight } from 'lucide-react';
import { Link } from 'react-router-dom';

const HemoEventsDeck = () => {
  const [activeCardIndex, setActiveCardIndex] = useState(0);
  const [scrollY, setScrollY] = useState(0);
  const sectionRef = useRef(null);

  const eventCards = [
    {
      id: 'event-1',
      title: 'EVERYONE COULD BE A HERO',
      tagline: '# DONATED TODAY! SAVE A LIFE',
      heroQuote: 'GIVE BLOOD',
      badge: 'Everyone is Hero',
      date: '14 June • 9AM - 4PM',
      location: 'Central Medical Center & City Blood Drive',
      themeColor: '#e11d48',
      accentBg: 'linear-gradient(145deg, #1f1418 0%, #0d0a0b 100%)',
      stats: '1,200+ Donors Registered',
      description: 'Annual global campaign honoring voluntary blood donors and providing critical whole-blood reserves for emergency centers.'
    },
    {
      id: 'event-2',
      title: 'WORLD HEMOPHILIA DAY',
      tagline: 'ACCESS FOR ALL: PREVENTION OF BLEEDS',
      heroQuote: '17th APRIL',
      badge: 'World Donor Day',
      date: '17th April • Starts 10:00 AM',
      location: 'Regional General Hospital Auditorium',
      themeColor: '#f43f5e',
      accentBg: 'linear-gradient(145deg, #241118 0%, #0d0a0d 100%)',
      stats: 'Platelet & Factor VIII Focus',
      description: 'Specialized collection camp focusing on platelet apheresis and rare plasma proteins for pediatric clotting disorders.'
    },
    {
      id: 'event-3',
      title: 'WORLD BLOOD DONOR DAY',
      tagline: 'DONATE BLOOD',
      heroQuote: 'IT ONLY TAKES 10 MINUTES',
      badge: 'Weekly Donor',
      date: 'Weekly Friday • 10:00 AM',
      location: 'University Medical Quad & Mobile Unit',
      themeColor: '#0ea5e9',
      accentBg: 'linear-gradient(145deg, #0e1a24 0%, #080c10 100%)',
      stats: 'Youth Hero Certificates',
      description: 'Ongoing community partnership driving continuous fresh O-negative and rare phenotype stockpiles.'
    },
    {
      id: 'event-4',
      title: 'World Blood Donor Day',
      tagline: 'Friday, 14th April • Starts from 10:00 AM',
      heroQuote: 'YOUR ADDRESS HERE',
      badge: 'The Donation Day',
      date: '14th April • 10AM - 5PM',
      location: 'Metro Community Center & City Blood Bank',
      themeColor: '#14b8a6',
      accentBg: 'linear-gradient(145deg, #0c1a1a 0%, #070d0d 100%)',
      stats: 'Emergency Reserve Fill',
      description: 'Citywide blood donation campaign connecting corporate, university, and independent donors with regional emergency care networks.'
    }
  ];

  useEffect(() => {
    const handleScroll = () => {
      if (!sectionRef.current) return;
      const rect = sectionRef.current.getBoundingClientRect();
      // Parallax scroll on section
      setScrollY(rect.top);
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const handleNext = () => {
    setActiveCardIndex((prev) => (prev + 1) % eventCards.length);
  };

  const handlePrev = () => {
    setActiveCardIndex((prev) => (prev - 1 + eventCards.length) % eventCards.length);
  };

  return (
    <section className="hemo-events-section" ref={sectionRef}>
      {/* Giant Backdrop Text: "EVENTS" spanning viewport with parallax - strictly contained */}
      <div 
        className="events-giant-bg-text"
        style={{
          transform: `translate(-50%, calc(-50% + ${scrollY * 0.05}px))`
        }}
      >
        <span>EVENTS</span>
      </div>

      <div className="container events-container">
        <div className="events-stage-wrapper">
          {/* Controls Bar */}
          <div className="events-top-nav">
            <span className="events-counter-pill">
              0{activeCardIndex + 1} / 0{eventCards.length} FEATURED CAMPAIGNS
            </span>
            <div className="deck-control-arrows">
              <button onClick={handlePrev} className="arrow-btn" aria-label="Previous campaign">
                <ChevronLeft size={18} />
              </button>
              <button onClick={handleNext} className="arrow-btn" aria-label="Next campaign">
                <ChevronRight size={18} />
              </button>
            </div>
          </div>

          {/* Stacked Interactive Card Deck matching video peeling/flipping */}
          <div className="cards-deck-cluster">
            {eventCards.map((event, index) => {
              // Calculate offset relative to active card
              const offset = (index - activeCardIndex + eventCards.length) % eventCards.length;
              const isCurrent = offset === 0;

              return (
                <div
                  key={event.id}
                  className={`deck-card-layer offset-${offset} ${isCurrent ? 'active' : ''}`}
                  onClick={() => {
                    // Clicking the active card advances to the next, clicking background card brings it to front
                    if (isCurrent) {
                      handleNext();
                    } else {
                      setActiveCardIndex(index);
                    }
                  }}
                  style={{
                    '--theme-color': event.themeColor,
                    background: event.accentBg,
                    zIndex: 10 - offset
                  }}
                >
                  <div className="deck-card-inner">
                    {/* Top Poster Header */}
                    <div className="deck-card-header">
                      <div className="deck-card-pill">
                        <Award size={14} color={event.themeColor} />
                        <span>{event.badge}</span>
                      </div>
                      <span className="deck-tagline">{event.tagline}</span>
                    </div>

                    {/* Bold Campaign Title & Hero Quote */}
                    <div className="deck-title-block">
                      <h3 className="deck-event-title">{event.title}</h3>
                      <div className="deck-hero-quote">{event.heroQuote}</div>
                    </div>

                    <p className="deck-event-desc">{event.description}</p>

                    {/* Metadata strip */}
                    <div className="deck-meta-details">
                      <div className="deck-meta-row">
                        <Calendar size={14} color={event.themeColor} />
                        <span>{event.date}</span>
                      </div>
                      <div className="deck-meta-row">
                        <MapPin size={14} color={event.themeColor} />
                        <span>{event.location}</span>
                      </div>
                      <div className="deck-meta-row">
                        <Users size={14} color={event.themeColor} />
                        <span>{event.stats}</span>
                      </div>
                    </div>

                    {/* Footer Actions */}
                    <div className="deck-card-footer" onClick={(e) => e.stopPropagation()}>
                      <Link to="/register" className="btn btn-primary btn-deck-rsvp">
                        <span>Register For Camp</span>
                        <ArrowUpRight size={14} />
                      </Link>
                      <span className="deck-footer-hint">Click poster to peel next</span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      <style>{`
        .hemo-events-section {
          padding: 6rem 0 8rem;
          background: #08080a;
          position: relative;
          overflow: hidden;
          width: 100%;
          max-width: 100vw;
          box-sizing: border-box;
        }

        /* Giant Background Text - No Overflow */
        .events-giant-bg-text {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          font-family: var(--font-heading);
          font-size: clamp(5rem, 16vw, 15rem);
          font-weight: 900;
          letter-spacing: -0.04em;
          color: rgba(255, 255, 255, 0.04);
          user-select: none;
          pointer-events: none;
          white-space: nowrap;
          z-index: 0;
          line-height: 0.8;
          width: 100%;
          text-align: center;
          will-change: transform;
        }

        .events-container {
          position: relative;
          z-index: 1;
        }

        .events-stage-wrapper {
          position: relative;
          max-width: 680px;
          margin: 0 auto;
          width: 100%;
          box-sizing: border-box;
        }

        .events-top-nav {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 2rem;
          width: 100%;
        }

        .events-counter-pill {
          font-size: 0.76rem;
          font-weight: 700;
          letter-spacing: 0.08em;
          color: #9499a6;
          background: rgba(255, 255, 255, 0.04);
          padding: 0.35rem 0.85rem;
          border-radius: 9999px;
          border: 1px solid rgba(255, 255, 255, 0.08);
        }

        .deck-control-arrows {
          display: flex;
          gap: 0.5rem;
        }

        .arrow-btn {
          width: 38px;
          height: 38px;
          border-radius: 50%;
          background: #14161b;
          border: 1px solid rgba(255, 255, 255, 0.12);
          color: #ffffff;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.2s ease;
        }

        .arrow-btn:hover {
          background: #ffffff;
          color: #08080a;
          transform: scale(1.05);
        }

        /* 3D Stacked Card Deck */
        .cards-deck-cluster {
          position: relative;
          height: 520px;
          perspective: 1200px;
          width: 100%;
          box-sizing: border-box;
        }

        .deck-card-layer {
          position: absolute;
          inset: 0;
          border-radius: 24px;
          border: 1px solid rgba(255, 255, 255, 0.12);
          box-shadow: 0 25px 50px rgba(0, 0, 0, 0.9);
          cursor: pointer;
          transition: all 0.55s cubic-bezier(0.16, 1, 0.3, 1);
          transform-origin: center bottom;
          width: 100%;
          box-sizing: border-box;
        }

        /* Layer 0 (Active Top Card) */
        .deck-card-layer.offset-0 {
          transform: translate3d(0, 0, 0) scale(1) rotate(0deg);
          opacity: 1;
          border-color: rgba(255, 255, 255, 0.25);
          box-shadow: 0 30px 60px rgba(0, 0, 0, 0.95), 0 0 35px rgba(225, 29, 72, 0.2);
        }

        /* Layer 1 (Card Behind) */
        .deck-card-layer.offset-1 {
          transform: translate3d(18px, -18px, -40px) scale(0.96) rotate(3deg);
          opacity: 0.88;
          filter: brightness(0.85);
        }

        /* Layer 2 (Third Card) */
        .deck-card-layer.offset-2 {
          transform: translate3d(-18px, -34px, -80px) scale(0.92) rotate(-3deg);
          opacity: 0.65;
          filter: brightness(0.65);
        }

        /* Layer 3 (Fourth Card) */
        .deck-card-layer.offset-3 {
          transform: translate3d(0, -48px, -120px) scale(0.88) rotate(0deg);
          opacity: 0.45;
          filter: brightness(0.45);
        }

        .deck-card-inner {
          padding: 2.25rem;
          height: 100%;
          display: flex;
          flex-direction: column;
          box-sizing: border-box;
          backdrop-filter: blur(20px);
          border-radius: inherit;
        }

        .deck-card-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 1.5rem;
          flex-wrap: wrap;
          gap: 0.75rem;
        }

        .deck-card-pill {
          display: inline-flex;
          align-items: center;
          gap: 0.4rem;
          background: rgba(255, 255, 255, 0.08);
          padding: 0.35rem 0.85rem;
          border-radius: 9999px;
          font-size: 0.75rem;
          font-weight: 700;
          color: #ffffff;
        }

        .deck-tagline {
          font-size: 0.72rem;
          font-weight: 800;
          letter-spacing: 0.08em;
          color: var(--theme-color);
          text-transform: uppercase;
        }

        .deck-title-block {
          margin-bottom: 1rem;
        }

        .deck-event-title {
          font-family: var(--font-heading);
          font-size: clamp(1.4rem, 3.2vw, 2.1rem);
          font-weight: 800;
          line-height: 1.15;
          color: #ffffff;
          letter-spacing: -0.02em;
          margin-bottom: 0.3rem;
        }

        .deck-hero-quote {
          font-size: 0.95rem;
          font-weight: 800;
          color: var(--theme-color);
          letter-spacing: 0.05em;
        }

        .deck-event-desc {
          font-size: 0.88rem;
          color: #9499a6;
          line-height: 1.55;
          margin-bottom: 1.5rem;
        }

        .deck-meta-details {
          display: flex;
          flex-direction: column;
          gap: 0.55rem;
          margin-bottom: 1.5rem;
          background: rgba(0, 0, 0, 0.4);
          padding: 0.9rem 1.15rem;
          border-radius: 12px;
          border: 1px solid rgba(255, 255, 255, 0.06);
        }

        .deck-meta-row {
          display: flex;
          align-items: center;
          gap: 0.65rem;
          font-size: 0.82rem;
          color: #d1d5db;
        }

        .deck-card-footer {
          margin-top: auto;
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding-top: 1rem;
          border-top: 1px solid rgba(255, 255, 255, 0.08);
          flex-wrap: wrap;
          gap: 0.75rem;
        }

        .btn-deck-rsvp {
          padding: 0.6rem 1.4rem;
          font-size: 0.84rem;
        }

        .deck-footer-hint {
          font-size: 0.72rem;
          color: #64748b;
        }

        @media (max-width: 650px) {
          .cards-deck-cluster { height: 560px; }
          .deck-card-inner { padding: 1.5rem; }
          .deck-card-layer.offset-1 { transform: translate3d(8px, -12px, -30px) scale(0.96) rotate(2deg); }
          .deck-card-layer.offset-2 { transform: translate3d(-8px, -24px, -60px) scale(0.92) rotate(-2deg); }
          .deck-card-layer.offset-3 { transform: translate3d(0, -36px, -90px) scale(0.88) rotate(0deg); }
        }
      `}</style>
    </section>
  );
};

export default HemoEventsDeck;
