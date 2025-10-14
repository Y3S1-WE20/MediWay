import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Heart, QrCode } from "lucide-react";

const HeroSection = () => {
  const navigate = useNavigate();

  return (
    <section className="relative min-h-[90vh] flex items-center justify-center px-4 py-20 overflow-hidden">
      {/* Gradient Background */}
      <div className="absolute inset-0 bg-gradient-to-br from-primary/5 via-accent/10 to-background" />
      
      <div className="container mx-auto max-w-6xl relative z-10">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Text Content */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="text-center lg:text-left"
          >
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.2, duration: 0.5 }}
              className="inline-flex items-center gap-2 px-4 py-2 bg-accent/50 rounded-full mb-6"
            >
              <Heart className="w-4 h-4 text-primary" />
              <span className="text-sm font-medium text-foreground">Trusted by 10,000+ patients</span>
            </motion.div>

            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-foreground mb-6 leading-tight">
              Your Health,{" "}
              <span className="text-primary">Simplified</span>{" "}
              with MediWay
            </h1>

            <p className="text-lg md:text-xl text-muted-foreground mb-8 max-w-xl">
              Experience seamless healthcare management with smart appointments, 
              digital health cards, and secure access to your medical records.
            </p>

            <div className="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
              <Button 
                size="lg" 
                className="text-base font-semibold shadow-lg hover:shadow-xl transition-shadow bg-[#4CAF50] hover:bg-[#45a049] text-white"
                onClick={() => navigate('/book-appointment')}
              >
                Book Appointment
              </Button>
              <Button 
                size="lg" 
                variant="outline" 
                className="text-base font-semibold border-[#4CAF50] text-[#4CAF50] hover:bg-[#4CAF50] hover:text-white"
                onClick={() => navigate('/register')}
              >
                Register Now
              </Button>
            </div>
          </motion.div>

          {/* Hero Illustration */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.3, duration: 0.6 }}
            className="relative flex items-center justify-center"
          >
            <div className="relative w-full max-w-md aspect-square">
              {/* Decorative circles */}
              <motion.div
                animate={{ 
                  scale: [1, 1.1, 1],
                  opacity: [0.3, 0.5, 0.3]
                }}
                transition={{ 
                  duration: 4,
                  repeat: Infinity,
                  ease: "easeInOut"
                }}
                className="absolute inset-0 rounded-full bg-primary/10 blur-3xl"
              />
              
              {/* Main illustration container */}
              <div className="relative bg-card rounded-3xl p-8 shadow-[0_8px_30px_rgb(0,0,0,0.08)]">
                {/* Doctor + Patient SVG */}
                <svg
                  viewBox="0 0 400 400"
                  className="w-full h-full"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  {/* Doctor figure */}
                  <circle cx="150" cy="120" r="40" fill="hsl(150 45% 48%)" opacity="0.2" />
                  <circle cx="150" cy="120" r="30" fill="hsl(150 45% 48%)" />
                  <rect x="120" y="160" width="60" height="80" rx="10" fill="hsl(150 45% 48%)" opacity="0.3" />
                  <rect x="125" y="165" width="50" height="70" rx="8" fill="hsl(150 45% 48%)" />
                  
                  {/* Patient figure */}
                  <circle cx="250" cy="140" r="35" fill="hsl(165 55% 75%)" opacity="0.2" />
                  <circle cx="250" cy="140" r="25" fill="hsl(165 55% 75%)" />
                  <rect x="225" y="170" width="50" height="70" rx="8" fill="hsl(165 55% 75%)" opacity="0.3" />
                  <rect x="230" y="175" width="40" height="60" rx="6" fill="hsl(165 55% 75%)" />
                  
                  {/* QR Code illustration */}
                  <g transform="translate(160, 260)">
                    <rect width="80" height="80" rx="8" fill="white" stroke="hsl(150 45% 48%)" strokeWidth="2" />
                    <rect x="10" y="10" width="25" height="25" fill="hsl(150 45% 48%)" />
                    <rect x="45" y="10" width="25" height="25" fill="hsl(150 45% 48%)" />
                    <rect x="10" y="45" width="25" height="25" fill="hsl(150 45% 48%)" />
                    <rect x="45" y="45" width="25" height="25" fill="hsl(150 45% 48%)" opacity="0.3" />
                  </g>
                  
                  {/* Floating health icons */}
                  <motion.g
                    animate={{ y: [0, -10, 0] }}
                    transition={{ duration: 3, repeat: Infinity, ease: "easeInOut" }}
                  >
                    <circle cx="80" cy="200" r="20" fill="hsl(165 55% 85%)" />
                    <text x="80" y="210" textAnchor="middle" fontSize="24">💊</text>
                  </motion.g>
                  
                  <motion.g
                    animate={{ y: [0, -8, 0] }}
                    transition={{ duration: 2.5, repeat: Infinity, ease: "easeInOut", delay: 0.5 }}
                  >
                    <circle cx="320" cy="180" r="20" fill="hsl(165 55% 85%)" />
                    <text x="320" y="190" textAnchor="middle" fontSize="24">🩺</text>
                  </motion.g>
                </svg>
                
                {/* QR Badge */}
                <motion.div
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                  transition={{ delay: 0.8, type: "spring", stiffness: 200 }}
                  className="absolute -bottom-4 -right-4 bg-primary text-primary-foreground rounded-2xl p-4 shadow-lg"
                >
                  <QrCode className="w-8 h-8" />
                </motion.div>
              </div>
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default HeroSection;
