import React, { useState } from 'react';
import { ChevronRight, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

const HemoFAQ = () => {
  const [openIndex, setOpenIndex] = useState(0);

  const faqs = [
    {
      question: 'Who can donate blood?',
      answer: 'Most healthy individuals over 17 years old (16 with parental consent in some areas), weighing at least 110 pounds (50 kg), can donate. Eligibility depends on health, travel history, medications, and other factors.'
    },
    {
      question: 'How often can I donate blood?',
      answer: 'You can donate whole blood every 56 days (8 weeks). For platelet apheresis donations, you can donate every 7 days up to 24 times per year, and for double red cells, every 112 days.'
    },
    {
      question: 'Is it safe to donate blood?',
      answer: 'Absolutely. Donating blood is 100% safe. All equipment is sterile, single-use, and disposed of immediately after collection. You cannot contract any infectious disease through the donation process.'
    },
    {
      question: 'What happens to my blood after donation?',
      answer: 'Your donated unit is transported in temperature-controlled units to our testing labs, where it undergoes extensive infectious screening and blood typing. It is separated into Red Cells, Platelets, and Plasma, and matched instantly to emergency hospital requisitions.'
    }
  ];

  const toggleAccordion = (idx) => {
    setOpenIndex(openIndex === idx ? null : idx);
  };

  return (
    <section className="hemo-faq-section">
      {/* Infinite Horizontal Marquee Backdrop "FAQ FAQ FAQ FAQ" matching video 00:29 */}
      <div className="faq-marquee-viewport">
        <div className="faq-marquee-track">
          <span>FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;</span>
          <span>FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;FAQ&nbsp;&nbsp;</span>
        </div>
      </div>

      <div className="container faq-container">
        <div className="faq-content-wrapper">
          <div className="faq-accordion-list">
            {faqs.map((faq, idx) => {
              const isOpen = openIndex === idx;
              return (
                <div 
                  key={idx} 
                  className={`faq-item-card ${isOpen ? 'expanded' : ''}`}
                >
                  <button 
                    className="faq-question-btn" 
                    onClick={() => toggleAccordion(idx)}
                    aria-expanded={isOpen}
                  >
                    <div className="faq-q-left">
                      <ChevronRight 
                        size={18} 
                        className={`faq-chevron-icon ${isOpen ? 'rotate' : ''}`} 
                      />
                      <span className="faq-q-text">{faq.question}</span>
                    </div>
                  </button>

                  <div className={`faq-answer-drawer ${isOpen ? 'open' : ''}`}>
                    <div className="faq-answer-inner">
                      <p>{faq.answer}</p>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Bottom Banner "Let's become a donor" matching video 00:33 - 00:36 */}
          <div className="become-donor-banner">
            <h3 className="banner-heading">Let's become a donor</h3>
            <Link to="/register" className="btn btn-primary btn-donor-pill">
              <span>Start Saving Lives</span>
              <ArrowRight size={18} />
            </Link>
          </div>
        </div>
      </div>

      <style>{`
        .hemo-faq-section {
          padding: 6rem 0 8rem;
          background: #08080a;
          position: relative;
          overflow: hidden;
          width: 100%;
          max-width: 100vw;
          box-sizing: border-box;
        }

        /* Marquee Backdrop - Strictly Clipped, No Horizontal Overflow */
        .faq-marquee-viewport {
          position: absolute;
          top: 15%;
          left: 0;
          right: 0;
          width: 100%;
          overflow: hidden;
          pointer-events: none;
          z-index: 0;
        }

        .faq-marquee-track {
          display: flex;
          white-space: nowrap;
          width: max-content;
          animation: marquee-slide 35s linear infinite;
        }

        .faq-marquee-track span {
          font-family: var(--font-heading);
          font-size: clamp(6rem, 18vw, 15rem);
          font-weight: 900;
          letter-spacing: -0.04em;
          color: rgba(255, 255, 255, 0.035);
          user-select: none;
          line-height: 0.8;
          display: inline-block;
        }

        @keyframes marquee-slide {
          from { transform: translateX(0); }
          to { transform: translateX(-50%); }
        }

        .faq-container {
          position: relative;
          z-index: 1;
        }

        .faq-content-wrapper {
          max-width: 760px;
          margin: 0 auto;
          width: 100%;
          box-sizing: border-box;
        }

        .faq-accordion-list {
          display: flex;
          flex-direction: column;
          gap: 0.85rem;
          margin-bottom: 4.5rem;
        }

        .faq-item-card {
          background: #111319;
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: 14px;
          overflow: hidden;
          transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .faq-item-card:hover {
          border-color: rgba(255, 255, 255, 0.18);
        }

        .faq-item-card.expanded {
          background: #141720;
          border-color: rgba(225, 29, 72, 0.4);
          box-shadow: 0 15px 35px rgba(0, 0, 0, 0.7);
        }

        .faq-question-btn {
          width: 100%;
          padding: 1.4rem 1.6rem;
          background: transparent;
          border: none;
          color: #ffffff;
          font-size: 1.05rem;
          font-weight: 700;
          text-align: left;
          display: flex;
          align-items: center;
          justify-content: space-between;
          cursor: pointer;
        }

        .faq-q-left {
          display: flex;
          align-items: center;
          gap: 0.85rem;
        }

        .faq-chevron-icon {
          color: #e11d48;
          transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
          flex-shrink: 0;
        }

        .faq-chevron-icon.rotate {
          transform: rotate(90deg);
        }

        .faq-q-text {
          font-family: var(--font-body);
          font-size: 1.05rem;
          letter-spacing: -0.01em;
        }

        .faq-answer-drawer {
          max-height: 0;
          overflow: hidden;
          transition: max-height 0.4s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .faq-answer-drawer.open {
          max-height: 300px;
        }

        .faq-answer-inner {
          padding: 0 1.6rem 1.4rem 2.8rem;
          color: #9499a6;
          font-size: 0.92rem;
          line-height: 1.65;
        }

        /* Banner matching video 00:33 - 00:36 */
        .become-donor-banner {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 2.25rem 2.75rem;
          background: #111319;
          border: 1px solid rgba(255, 255, 255, 0.1);
          border-radius: 24px;
          box-shadow: 0 20px 45px rgba(0, 0, 0, 0.8);
          flex-wrap: wrap;
          gap: 1.5rem;
        }

        .banner-heading {
          font-family: var(--font-heading);
          font-size: clamp(1.8rem, 3.5vw, 2.5rem);
          font-weight: 800;
          color: #ffffff;
          letter-spacing: -0.03em;
        }

        .btn-donor-pill {
          padding: 0.85rem 2.2rem;
          font-size: 0.95rem;
          border-radius: 9999px;
        }

        @media (max-width: 650px) {
          .become-donor-banner {
            padding: 1.75rem 1.5rem;
          }
          .faq-answer-inner {
            padding-left: 1.6rem;
          }
        }
      `}</style>
    </section>
  );
};

export default HemoFAQ;
