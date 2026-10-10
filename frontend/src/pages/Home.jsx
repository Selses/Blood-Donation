import React, { useState, useEffect } from 'react';
import HemoHero from '../components/HemoHero';
import HemoFeaturesGrid from '../components/HemoFeaturesGrid';
import HemoEventsDeck from '../components/HemoEventsDeck';
import HemoHowItWorks from '../components/HemoHowItWorks';
import HemoFAQ from '../components/HemoFAQ';
import HemoFooter from '../components/HemoFooter';

const Home = () => {
  const [mousePos, setMousePos] = useState({ x: -200, y: -200 });

  useEffect(() => {
    const handleMouseMove = (e) => {
      setMousePos({ x: e.clientX, y: e.clientY });
    };
    window.addEventListener('mousemove', handleMouseMove);
    return () => window.removeEventListener('mousemove', handleMouseMove);
  }, []);

  return (
    <div className="hemo-landing-page">
      {/* Ambient mouse follower glow */}
      <div 
        className="ambient-glow-follower"
        style={{
          left: `${mousePos.x}px`,
          top: `${mousePos.y}px`,
          opacity: mousePos.x > 0 ? 0.75 : 0
        }}
      />

      <div id="hero">
        <HemoHero />
      </div>

      <div id="features">
        <HemoFeaturesGrid />
      </div>

      <div id="events">
        <HemoEventsDeck />
      </div>

      <div id="how-it-works">
        <HemoHowItWorks />
      </div>

      <div id="faq">
        <HemoFAQ />
      </div>

      <HemoFooter />

      <style>{`
        .hemo-landing-page {
          background-color: #08080a;
          color: #ffffff;
          min-height: 100vh;
          width: 100%;
          max-width: 100vw;
          overflow-x: hidden;
          position: relative;
        }
      `}</style>
    </div>
  );
};

export default Home;
