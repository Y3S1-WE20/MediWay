import React, { useEffect, useRef, useState } from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import { FaChevronLeft, FaChevronRight, FaQuoteLeft, FaStar } from 'react-icons/fa';
import FeaturesSection from '../components/FeaturesSection';
import AboutSection from '../components/AboutSection';

// Logo import
import logo from '../assets/logo.png';

function useInterval(callback, delay) {
  const savedRef = useRef();
  useEffect(() => { savedRef.current = callback; });
  useEffect(() => {
    if (delay == null) return;
    const id = setInterval(() => savedRef.current && savedRef.current(), delay);
    return () => clearInterval(id);
  }, [delay]);
}

function Dot({ active, onClick }) {
  return (
    <button
      onClick={onClick}
      style={{
        width: 12,
        height: 12,
        borderRadius: '50%',
        border: 'none',
        background: active ? '#0ea5e9' : '#cbd5e1',
        cursor: 'pointer',
        transition: 'all 0.3s'
      }}
      aria-label={active ? 'current slide' : 'go to slide'}
    />
  );
}

function Hero() {
  const slides = [
    {
      img: 'https://images.pexels.com/photos/3938022/pexels-photo-3938022.jpeg',
      title: 'World-Class Healthcare',
      subtitle: 'Experience exceptional medical care with our team of expert doctors and state-of-the-art facilities.'
    },
    {
      img: 'https://images.pexels.com/photos/6129676/pexels-photo-6129676.jpeg',
      title: 'Book Appointments Instantly',
      subtitle: 'Schedule your consultation with ease. Get the care you need, when you need it.'
    },
    {
      img: 'https://images.pexels.com/photos/30797638/pexels-photo-30797638.jpeg',
      title: 'Comprehensive Health Solutions',
      subtitle: 'From preventive care to specialized treatments, we are here for your complete wellness.'
    },
  ];

  const [i, setI] = useState(0);
  useInterval(() => setI(prev => (prev + 1) % slides.length), 5000);

  const cur = slides[i];

  return (
    <section style={{
      position: 'relative',
      borderRadius: 24,
      overflow: 'hidden',
      height: '85vh',
      minHeight: 600,
      background: '#000',
      boxShadow: '0 20px 60px rgba(0,0,0,0.3)'
    }}>
      <img
        src={cur.img}
        alt={cur.title}
        style={{
          width: '100%',
          height: '100%',
          objectFit: 'cover',
          opacity: 0.85,
          transition: 'opacity 0.5s'
        }}
      />
      <div style={{
        position: 'absolute',
        inset: 0,
        background: 'linear-gradient(135deg, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0.3) 50%, rgba(0,0,0,0) 100%)'
      }} />
      
      <div style={{
        position: 'absolute',
        inset: 0,
        display: 'flex',
        alignItems: 'center',
        padding: '0 80px'
      }}>
        <div style={{ maxWidth: 720 }}>
          <div style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 8,
            background: 'rgba(14, 165, 233, 0.2)',
            backdropFilter: 'blur(10px)',
            padding: '8px 16px',
            borderRadius: 50,
            color: '#93c5fd',
            fontSize: 14,
            fontWeight: 600,
            marginBottom: 20
          }}>
            <img src={logo} alt="MediWay" style={{ height: 24 }} />
            Trusted Healthcare Partner
          </div>
          
          <h1 style={{
            margin: 0,
            color: '#fff',
            fontSize: 56,
            fontWeight: 800,
            lineHeight: 1.1,
            marginBottom: 16,
            textShadow: '0 2px 20px rgba(0,0,0,0.5)'
          }}>
            {cur.title}
          </h1>
          
          <p style={{
            color: '#e2e8f0',
            fontSize: 18,
            lineHeight: 1.6,
            marginBottom: 32,
            maxWidth: 600
          }}>
            {cur.subtitle}
          </p>
          
          <div style={{ display: 'flex', gap: 16 }}>
            <Link
              to="/appointments"
              style={{
                padding: '16px 32px',
                background: '#0ea5e9',
                color: '#fff',
                textDecoration: 'none',
                borderRadius: 12,
                fontWeight: 700,
                fontSize: 16,
                transition: 'all 0.3s',
                boxShadow: '0 4px 14px rgba(14, 165, 233, 0.4)'
              }}
              onMouseEnter={e => {
                e.target.style.background = '#0284c7';
                e.target.style.transform = 'translateY(-2px)';
                e.target.style.boxShadow = '0 6px 20px rgba(14, 165, 233, 0.5)';
              }}
              onMouseLeave={e => {
                e.target.style.background = '#0ea5e9';
                e.target.style.transform = 'translateY(0)';
                e.target.style.boxShadow = '0 4px 14px rgba(14, 165, 233, 0.4)';
              }}
            >
              Book Appointment
            </Link>
            
            <Link
              to="/register"
              style={{
                padding: '16px 32px',
                background: 'rgba(255, 255, 255, 0.15)',
                backdropFilter: 'blur(10px)',
                color: '#fff',
                textDecoration: 'none',
                borderRadius: 12,
                fontWeight: 700,
                fontSize: 16,
                border: '2px solid rgba(255, 255, 255, 0.3)',
                transition: 'all 0.3s'
              }}
              onMouseEnter={e => {
                e.target.style.background = 'rgba(255, 255, 255, 0.25)';
                e.target.style.transform = 'translateY(-2px)';
              }}
              onMouseLeave={e => {
                e.target.style.background = 'rgba(255, 255, 255, 0.15)';
                e.target.style.transform = 'translateY(0)';
              }}
            >
              Register Now
            </Link>
          </div>
        </div>
      </div>

      <button
        onClick={() => setI((i - 1 + slides.length) % slides.length)}
        aria-label="previous"
        style={{
          position: 'absolute',
          left: 24,
          top: '50%',
          transform: 'translateY(-50%)',
          background: 'rgba(255, 255, 255, 0.9)',
          backdropFilter: 'blur(10px)',
          border: 'none',
          borderRadius: '50%',
          width: 50,
          height: 50,
          display: 'grid',
          placeItems: 'center',
          cursor: 'pointer',
          fontSize: 20,
          color: '#0f172a',
          transition: 'all 0.3s',
          boxShadow: '0 4px 12px rgba(0,0,0,0.15)'
        }}
        onMouseEnter={e => {
          e.target.style.background = '#fff';
          e.target.style.transform = 'translateY(-50%) scale(1.1)';
        }}
        onMouseLeave={e => {
          e.target.style.background = 'rgba(255, 255, 255, 0.9)';
          e.target.style.transform = 'translateY(-50%) scale(1)';
        }}
      >
        <FaChevronLeft />
      </button>

      <button
        onClick={() => setI((i + 1) % slides.length)}
        aria-label="next"
        style={{
          position: 'absolute',
          right: 24,
          top: '50%',
          transform: 'translateY(-50%)',
          background: 'rgba(255, 255, 255, 0.9)',
          backdropFilter: 'blur(10px)',
          border: 'none',
          borderRadius: '50%',
          width: 50,
          height: 50,
          display: 'grid',
          placeItems: 'center',
          cursor: 'pointer',
          fontSize: 20,
          color: '#0f172a',
          transition: 'all 0.3s',
          boxShadow: '0 4px 12px rgba(0,0,0,0.15)'
        }}
        onMouseEnter={e => {
          e.target.style.background = '#fff';
          e.target.style.transform = 'translateY(-50%) scale(1.1)';
        }}
        onMouseLeave={e => {
          e.target.style.background = 'rgba(255, 255, 255, 0.9)';
          e.target.style.transform = 'translateY(-50%) scale(1)';
        }}
      >
        <FaChevronRight />
      </button>

      <div style={{
        position: 'absolute',
        bottom: 32,
        left: 0,
        right: 0,
        display: 'flex',
        justifyContent: 'center',
        gap: 12
      }}>
        {slides.map((_, idx) => <Dot key={idx} active={i === idx} onClick={() => setI(idx)} />)}
      </div>
    </section>
  );
}

function PatientStories() {
  const testimonials = [
    {
      name: 'Tharindu Perera',
      text: 'The care I received at MediWay was exceptional. The doctors were attentive, the staff was friendly, and the facilities were top-notch. I felt truly cared for throughout my treatment.',
      rating: 5,
      role: 'Patient',
      img: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop'
    },
    {
      name: 'Ishara Fernando',
      text: 'Booking an appointment was so easy! The entire process from consultation to getting my test results was seamless. The medical team was professional and compassionate.',
      rating: 5,
      role: 'Patient',
      img: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop'
    },
    {
      name: 'Nadeesha Silva',
      text: 'I highly recommend MediWay for their health packages. Great value and comprehensive diagnostics. The doctors took time to explain everything clearly.',
      rating: 5,
      role: 'Patient',
      img: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop'
    },
  ];

  const [currentIndex, setCurrentIndex] = useState(0);
  useInterval(() => setCurrentIndex(prev => (prev + 1) % testimonials.length), 6000);

  const current = testimonials[currentIndex];

  return (
    <section style={{
      padding: '80px 0',
      background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
      borderRadius: 24,
      position: 'relative',
      overflow: 'hidden'
    }}>
      <div style={{
        position: 'absolute',
        top: -100,
        right: -100,
        width: 300,
        height: 300,
        background: 'radial-gradient(circle, rgba(14, 165, 233, 0.1) 0%, transparent 70%)',
        borderRadius: '50%'
      }} />

      <div style={{ textAlign: 'center', marginBottom: 60, position: 'relative', zIndex: 1 }}>
        <h2 style={{
          margin: 0,
          color: '#fff',
          fontSize: 42,
          fontWeight: 800,
          marginBottom: 12
        }}>
          Patient Stories
        </h2>
        <p style={{
          color: '#94a3b8',
          fontSize: 18,
          maxWidth: 600,
          margin: '0 auto'
        }}>
          Real experiences from our patients—because your trust matters
        </p>
      </div>

      <div style={{
        maxWidth: 900,
        margin: '0 auto',
        position: 'relative',
        zIndex: 1
      }}>
        <div style={{
          background: 'rgba(255, 255, 255, 0.05)',
          backdropFilter: 'blur(10px)',
          borderRadius: 20,
          padding: 48,
          border: '1px solid rgba(255, 255, 255, 0.1)',
          boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)'
        }}>
          <FaQuoteLeft style={{
            fontSize: 40,
            color: '#0ea5e9',
            opacity: 0.5,
            marginBottom: 20
          }} />

          <p style={{
            color: '#e2e8f0',
            fontSize: 20,
            lineHeight: 1.7,
            marginBottom: 32,
            fontStyle: 'italic'
          }}>
            "{current.text}"
          </p>

          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: 20
          }}>
            <img
              src={current.img}
              alt={current.name}
              style={{
                width: 70,
                height: 70,
                borderRadius: '50%',
                objectFit: 'cover',
                border: '3px solid #0ea5e9'
              }}
            />
            <div>
              <div style={{
                color: '#fff',
                fontSize: 20,
                fontWeight: 700,
                marginBottom: 4
              }}>
                {current.name}
              </div>
              <div style={{
                color: '#94a3b8',
                fontSize: 14,
                marginBottom: 8
              }}>
                {current.role}
              </div>
              <div style={{ display: 'flex', gap: 4 }}>
                {[...Array(current.rating)].map((_, i) => (
                  <FaStar key={i} style={{ color: '#fbbf24', fontSize: 16 }} />
                ))}
              </div>
            </div>
          </div>
        </div>

        <div style={{
          display: 'flex',
          justifyContent: 'center',
          gap: 12,
          marginTop: 32
        }}>
          {testimonials.map((_, idx) => (
            <Dot key={idx} active={currentIndex === idx} onClick={() => setCurrentIndex(idx)} />
          ))}
        </div>
      </div>
    </section>
  );
}

const Home = () => {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.5 }}
      className="min-h-screen bg-background"
    >
      <Hero />
      <PatientStories />
      <FeaturesSection />
      <AboutSection />
    </motion.div>
  );
};

export default Home;
