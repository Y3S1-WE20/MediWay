import { useEffect } from 'react';
import HeroSection from "../components/home/HeroSection";
import AboutSection from "../components/home/AboutSection";
import ServicesSection from "../components/home/ServicesSection";
import StatsSection from "../components/home/StatsSection";
import DoctorsSection from "../components/home/DoctorsSection";
import TestimonialsSection from "../components/home/TestimonialsSection";
import FAQSection from "../components/home/FAQSection";
import CTASection from "../components/home/CTASection";

const Home = () => {
  useEffect(() => {
    // Smooth scroll behavior
    document.documentElement.style.scrollBehavior = 'smooth';
    
    return () => {
      document.documentElement.style.scrollBehavior = 'auto';
    };
  }, []);

  return (
    <div className="w-full min-h-screen">
      <HeroSection />
      <AboutSection />
      <ServicesSection />
      <StatsSection />
      <DoctorsSection />
      <TestimonialsSection />
      <FAQSection />
      <CTASection />
    </div>
  );
};

export default Home;
