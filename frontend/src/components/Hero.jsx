import { Button } from "@/components/ui/button";
import { Calendar, UserPlus } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import heroImage1 from "@/assets/hero-doctor-patient.jpg";
import heroImage2 from "@/assets/hero-technology.jpg";
import heroImage3 from "@/assets/hero-family.jpg";
import floatingHealthCard from "@/assets/floating-healthcard.jpg";
import floatingMedical from "@/assets/floating-medical.png";

const heroImages = [
  { src: heroImage1, alt: "Doctor and patient in modern healthcare setting" },
  { src: heroImage2, alt: "Medical professionals using digital healthcare technology" },
  { src: heroImage3, alt: "Family with doctor reviewing health information" }
];

const Hero = () => {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentImageIndex((prev) => (prev + 1) % heroImages.length);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <section className="relative min-h-[90vh] flex items-center overflow-hidden bg-gradient-subtle">
  {/* Background Image Carousel */}
      <div className="absolute inset-0 z-0">
        {heroImages.map((image, index) => (
          <div
            key={index}
            className={`absolute inset-0 transition-opacity duration-1000 ${
              index === currentImageIndex ? "opacity-100" : "opacity-0"
            }`}
          >
            <img 
              src={image.src} 
              alt={image.alt}
              className="w-full h-full object-cover"
              style={{ filter: 'brightness(0.98) saturate(0.75)', opacity: 0.98 }}
            />
          </div>
        ))}
        <div className="absolute inset-0 bg-gradient-to-r from-background/95 via-background/70 to-background/30" />
        {/* very light dark overlay (kept subtle) to preserve readability */}
        <div className="absolute inset-0 bg-gradient-to-r from-black/10 to-transparent pointer-events-none" />
        {/* stronger white blend overlays to smoothly merge image with page header/sides */}
        <div
          className="absolute top-0 left-0 bottom-0 pointer-events-none"
          style={{ width: '20rem', background: 'linear-gradient(90deg, rgba(255,255,255,0.98) 0%, rgba(255,255,255,0) 70%)' }}
        />
        <div
          className="absolute top-0 right-0 bottom-0 pointer-events-none hidden lg:block"
          style={{ width: '12rem', background: 'linear-gradient(270deg, rgba(255,255,255,0.98) 0%, rgba(255,255,255,0) 70%)' }}
        />
        <div
          className="absolute left-0 right-0 top-0 pointer-events-none"
          style={{ height: '8rem', background: 'linear-gradient(to bottom, rgba(255,255,255,0.98), rgba(255,255,255,0))' }}
        />
      </div>

      {/* Floating Decorative Images */}
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
      
      <div className="w-full relative z-10">
        <div className="max-w-[1100px] pl-6 lg:pl-36 pr-8 animate-fade-in">
          <h1 className="font-poppins text-[48px] md:text-[64px] lg:text-[84px] leading-tight font-bold mb-6 text-foreground" style={{maxWidth: '920px'}}>
            Your Trusted Digital Healthcare Partner
          </h1>
          <p className="text-lg md:text-xl text-muted-foreground mb-8 leading-relaxed" style={{maxWidth: '760px'}}>
            Book appointments instantly, manage your health records securely, and connect with the best healthcare providers across Sri Lanka — all in one place.
          </p>
          <div className="flex flex-row gap-4">
              <Button
                className="mr-4 px-8 py-3 text-lg font-semibold shadow-lg bg-green-600 hover:bg-green-700 text-white border-none"
                variant="default"
                onClick={() => navigate('/appointments')}
              >
                <Calendar className="inline-block w-5 h-5 mr-2" />
                Book Appointment
              </Button>
              <Button
                className="px-8 py-3 text-lg font-semibold border shadow-lg bg-white text-green-700 border-green-600 hover:bg-green-50 hover:text-green-800"
                variant="outline"
                onClick={() => navigate('/register')}
              >
                <UserPlus className="inline-block w-5 h-5 mr-2" />
                Register Now
              </Button>
          </div>
        </div>
      </div>

      {/* Image Carousel Indicators */}
      <div className="absolute bottom-8 left-1/2 transform -translate-x-1/2 z-10 flex gap-2">
        {heroImages.map((_, index) => (
          <button
            key={index}
            onClick={() => setCurrentImageIndex(index)}
            className={`w-2 h-2 rounded-full transition-all duration-300 ${
              index === currentImageIndex 
                ? "bg-primary w-8" 
                : "bg-primary/30 hover:bg-primary/50"
            }`}
            aria-label={`View image ${index + 1}`}
          />
        ))}
      </div>
    </section>
  );
};

export default Hero;
