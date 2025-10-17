import { Button } from "@/components/ui/button";
import { ArrowRight } from "lucide-react";
import { useNavigate } from "react-router-dom";

const CTA = () => {
  const navigate = useNavigate();

  return (
    <section className="py-20 bg-gradient-hero">
      <div className="container mx-auto px-4">
        <div className="max-w-3xl mx-auto text-center animate-fade-in">
          <h2 className="font-poppins text-3xl md:text-4xl lg:text-5xl font-bold mb-6 text-white">
            Join Thousands of Sri Lankans Managing Health Smarter with MediWay
          </h2>
          <p className="text-lg md:text-xl text-white/90 mb-8 leading-relaxed">
            Start your journey to better healthcare today. No paperwork, no hassle — just seamless digital health management.
          </p>
          <Button 
            variant="hero" 
            size="lg" 
            className="bg-white text-primary hover:bg-white/90 text-lg px-8 py-6 h-auto shadow-2xl"
            onClick={() => navigate('/register')}
          >
            Get Started Now
            <ArrowRight className="ml-2 h-5 w-5" />
          </Button>
        </div>
      </div>
    </section>
  );
};

export default CTA;
