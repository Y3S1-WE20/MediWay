import { useEffect, useRef, useState } from 'react';
import { motion, useScroll, useTransform } from 'framer-motion';
import { gsap } from 'gsap';
import { useNavigate } from 'react-router-dom';
import { Heart, Calendar, Shield, Clock } from 'lucide-react';
import heroImage1 from "@/assets/hero-doctor-patient.jpg";
import heroImage2 from "@/assets/hero-technology.jpg";
import heroImage3 from "@/assets/hero-family.jpg";
import floatingHealthCard from "@/assets/floating-healthcard.png";
import floatingMedical from "@/assets/floating-medical.png";

const HeroSection = () => {
  const navigate = useNavigate();
  const heroRef = useRef(null);
  const { scrollYProgress } = useScroll({
    target: heroRef,
    offset: ["start start", "end start"]
  });

  const y = useTransform(scrollYProgress, [0, 1], ["0%", "50%"]);
  const opacity = useTransform(scrollYProgress, [0, 1], [1, 0]);

  useEffect(() => {
    const ctx = gsap.context(() => {
      // Animate title
      gsap.fromTo('.hero-title', 
        {
          y: 100,
          opacity: 0
        },
        {
          y: 0,
          opacity: 1,
          duration: 1,
          ease: 'power4.out',
          delay: 0.2
        }
      );

      // Animate subtitle
      gsap.fromTo('.hero-subtitle', 
        {
          y: 50,
          opacity: 0
        },
        {
          y: 0,
          opacity: 1,
          duration: 1,
          ease: 'power4.out',
          delay: 0.4
        }
      );

      // Animate CTA buttons
      gsap.fromTo('.hero-cta', 
        {
          y: 30,
          opacity: 0
        },
        {
          y: 0,
          opacity: 1,
          duration: 1,
          ease: 'power4.out',
          delay: 0.6
        }
      );

      // Animate feature cards - keep them visible after animation
      gsap.fromTo('.feature-card', 
        {
          y: 50,
          opacity: 0
        },
        {
          y: 0,
          opacity: 1,
          duration: 0.8,
          stagger: 0.1,
          ease: 'power3.out',
          delay: 0.8
        }
      );
    }, heroRef);

    return () => ctx.revert();
  }, []);

  // Carousel images and automatic rotation
  const heroImages = [
    { src: heroImage1, alt: 'Doctor and patient in modern healthcare setting' },
    { src: heroImage2, alt: 'Medical professionals using digital healthcare technology' },
    { src: heroImage3, alt: 'Family with doctor reviewing health information' }
  ];

  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentImageIndex((prev) => (prev + 1) % heroImages.length);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  const features = [
    {
      icon: <Heart className="w-6 h-6" />,
      title: 'Expert Care',
      description: 'Experienced medical professionals'
    },
    {
      icon: <Calendar className="w-6 h-6" />,
      title: 'Easy Booking',
      description: 'Schedule appointments instantly'
    },
    {
      icon: <Shield className="w-6 h-6" />,
      title: 'Secure Records',
      description: 'Your health data is protected'
    },
    {
      icon: <Clock className="w-6 h-6" />,
      title: '24/7 Support',
      description: 'Always here when you need us'
    }
  ];

  return (
    // Full-bleed: set section to viewport width and center it using margin trick so background reaches edges
    <section
      ref={heroRef}
      style={{ width: '100vw', marginLeft: 'calc(50% - 50vw)' }}
      className="relative min-h-screen flex items-center justify-center overflow-hidden bg-gradient-to-br from-green-50 via-white to-emerald-50 pt-20"
    >
      {/* Animated Background (carousel) */}
      <motion.div
        className="absolute inset-0 z-0"
        style={{ y, opacity }}
      >
        {/* Background Image Carousel */}
        {heroImages.map((image, index) => (
          <div
            key={index}
            className={`absolute inset-0 transition-opacity duration-1000 ${
              index === currentImageIndex ? 'opacity-100' : 'opacity-0'
            }`}
          >
            <img
              src={image.src}
              alt={image.alt}
              className="absolute inset-0 w-full h-full object-cover object-center"
              onError={(e) => {
                e.currentTarget.onerror = null;
                e.currentTarget.src = 'https://images.unsplash.com/photo-1526256262350-7da7584cf5eb?w=1600&q=80&auto=format&fit=crop';
              }}
            />
          </div>
        ))}

        {/* Blend with white overlay to improve readability (stronger on small screens) */}
        <div className="absolute inset-0 bg-white/70 md:bg-white/50 lg:bg-white/30 pointer-events-none" />
        {/* subtle green tint overlay for brand feel */}
        <div className="absolute inset-0 bg-gradient-to-r from-green-500/5 to-emerald-500/5 mix-blend-overlay pointer-events-none" />

        {/* Floating decorative images (hidden on small screens) */}
        <div className="absolute right-10 top-20 hidden lg:block z-10 animate-float">
          <img
            src={floatingHealthCard}
            alt="Digital health card"
            className="w-48 h-48 object-contain drop-shadow-2xl"
          />
        </div>
        <div className="absolute right-32 bottom-32 hidden lg:block z-10 animate-float-delayed">
          <img
            src={floatingMedical}
            alt="Medical stethoscope"
            className="w-40 h-40 object-contain drop-shadow-2xl"
          />
        </div>
      </motion.div>

  <div className="w-full max-w-7xl mx-auto px-4 py-20 relative z-10">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Left Content */}
          <div className="space-y-8">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8 }}
            >
              <h1 className="hero-title text-5xl lg:text-7xl font-bold text-gray-900 leading-tight">
                Welcome to
                <span className="block text-green-600 mt-2">MediWay</span>
              </h1>
            </motion.div>

            <motion.p
              className="hero-subtitle text-xl text-gray-600 max-w-xl"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8, delay: 0.2 }}
            >
              Your trusted partner in healthcare. Experience world-class medical services
              with cutting-edge technology and compassionate care.
            </motion.p>

            <motion.div
              className="hero-cta flex flex-col sm:flex-row gap-4"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8, delay: 0.4 }}
            >
              <motion.button
                onClick={() => navigate('/book-appointment')}
                className="px-8 py-4 bg-green-600 text-white rounded-lg font-semibold shadow-lg hover:bg-green-700 transition-all duration-300"
                whileHover={{ scale: 1.05, boxShadow: '0 20px 40px rgba(34, 197, 94, 0.3)' }}
                whileTap={{ scale: 0.95 }}
              >
                Book Appointment
              </motion.button>
              <motion.button
                onClick={() => navigate('/about')}
                className="px-8 py-4 bg-white text-green-600 border-2 border-green-600 rounded-lg font-semibold hover:bg-green-50 transition-all duration-300"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Learn More
              </motion.button>
            </motion.div>
          </div>

          {/* Right Content - Feature Cards */}
          <div className="grid grid-cols-2 gap-4">
            {features.map((feature, index) => (
              <motion.div
                key={index}
                className="feature-card group bg-white p-6 rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 cursor-pointer"
                whileHover={{ 
                  y: -10,
                  transition: { duration: 0.3 }
                }}
              >
                <motion.div
                  className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center text-green-600 mb-4 group-hover:bg-green-600 group-hover:text-white transition-all duration-300"
                  whileHover={{ rotate: 360 }}
                  transition={{ duration: 0.6 }}
                >
                  {feature.icon}
                </motion.div>
                <h3 className="text-lg font-bold text-gray-900 mb-2">{feature.title}</h3>
                <p className="text-gray-600 text-sm">{feature.description}</p>
              </motion.div>
            ))}
          </div>
        </div>

        {/* Scroll Indicator */}
        <motion.div
          className="absolute bottom-10 left-1/2 transform -translate-x-1/2"
          animate={{ y: [0, 10, 0] }}
          transition={{ duration: 1.5, repeat: Infinity }}
        >
          <div className="w-6 h-10 border-2 border-green-600 rounded-full flex justify-center">
            <motion.div
              className="w-1 h-3 bg-green-600 rounded-full mt-2"
              animate={{ y: [0, 12, 0] }}
              transition={{ duration: 1.5, repeat: Infinity }}
            />
          </div>
        </motion.div>
      </div>
    </section>
  );
};

export default HeroSection;
