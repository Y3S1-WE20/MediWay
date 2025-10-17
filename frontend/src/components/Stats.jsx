import { useEffect, useRef, useState } from "react";
import { Users, Building2, Stethoscope, Clock } from "lucide-react";

const stats = [
  {
    icon: Users,
    value: 10000,
    suffix: "+",
    label: "Active Patients"
  },
  {
    icon: Building2,
    value: 50,
    suffix: "+",
    label: "Partner Hospitals"
  },
  {
    icon: Stethoscope,
    value: 500,
    suffix: "+",
    label: "Healthcare Providers"
  },
  {
    icon: Clock,
    value: 24,
    suffix: "/7",
    label: "Support Available"
  }
];

const Counter = ({ target, suffix }) => {
  const [count, setCount] = useState(0);
  const [isVisible, setIsVisible] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true);
        }
      },
      { threshold: 0.1 }
    );

    if (ref.current) {
      observer.observe(ref.current);
    }

    return () => observer.disconnect();
  }, []);

  useEffect(() => {
    if (!isVisible) return;
    const duration = 2000;
    const steps = 60;
    const increment = target / steps;
    let current = 0;
    const timer = setInterval(() => {
      current += increment;
      if (current >= target) {
        setCount(target);
        clearInterval(timer);
      } else {
        setCount(Math.floor(current));
      }
    }, duration / steps);
    return () => clearInterval(timer);
  }, [isVisible, target]);

  return (
    <div ref={ref} className="text-4xl md:text-5xl font-bold text-primary font-poppins">
      {count.toLocaleString()}{suffix}
    </div>
  );
};

const Stats = () => {
  return (
    <section className="py-20 bg-primary text-primary-foreground">
      <div className="container mx-auto px-4">
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8 max-w-6xl mx-auto">
          {stats.map((stat, index) => {
            const Icon = stat.icon;
            return (
              <div 
                key={index} 
                className="text-center animate-scale-in"
                style={{ animationDelay: `${index * 0.1}s` }}
              >
                <Icon className="h-12 w-12 mx-auto mb-4 opacity-90" />
                <Counter target={stat.value} suffix={stat.suffix} />
                <p className="text-lg mt-2 opacity-90">{stat.label}</p>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default Stats;
