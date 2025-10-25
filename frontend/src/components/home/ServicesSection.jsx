import { useRef } from 'react';
import { motion, useInView } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Heart, Stethoscope, Brain, Pill, AlertCircle, Baby } from 'lucide-react';

const ServicesSection = () => {
  const ref = useRef(null);
  const navigate = useNavigate();
  const isInView = useInView(ref, { once: true, margin: "-100px" });

  const services = [
    {
      id: 'cardiology',
      icon: <Heart className="w-8 h-8" />,
      title: 'Cardiology',
      description: 'Comprehensive heart care with state-of-the-art technology and experienced cardiologists.',
      color: 'from-red-500 to-pink-500'
    },
    {
      id: 'general-medicine',
      icon: <Stethoscope className="w-8 h-8" />,
      title: 'General Medicine',
      description: 'Expert primary care services for all your health needs and concerns.',
      color: 'from-blue-500 to-cyan-500'
    },
    {
      id: 'neurology',
      icon: <Brain className="w-8 h-8" />,
      title: 'Neurology',
      description: 'Advanced neurological care and treatment for brain and nervous system disorders.',
      color: 'from-purple-500 to-pink-500'
    },
    {
      id: 'pharmacy',
      icon: <Pill className="w-8 h-8" />,
      title: 'Pharmacy',
      description: 'Complete pharmaceutical services with verified medications and expert consultation.',
      color: 'from-green-500 to-teal-500'
    },
    {
      id: 'emergency',
      icon: <AlertCircle className="w-8 h-8" />,
      title: 'Emergency Care',
      description: '24/7 emergency medical services with rapid response and expert trauma care.',
      color: 'from-orange-500 to-red-500'
    },
    {
      id: 'pediatrics',
      icon: <Baby className="w-8 h-8" />,
      title: 'Pediatrics',
      description: 'Specialized care for infants, children, and adolescents with gentle approach.',
      color: 'from-pink-500 to-purple-500'
    }
  ];

  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  };

  const item = {
    hidden: { y: 50, opacity: 0 },
    show: { 
      y: 0, 
      opacity: 1,
      transition: {
        duration: 0.6,
        ease: "easeOut"
      }
    }
  };

  return (
    <section ref={ref} className="py-20 bg-gradient-to-b from-white to-gray-50">
      <div className="container mx-auto px-4">
        {/* Section Header */}
        <motion.div
          className="text-center mb-16"
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.6 }}
        >
          <motion.h2 
            className="text-4xl md:text-5xl font-bold text-gray-900 mb-4"
            initial={{ opacity: 0, y: 20 }}
            animate={isInView ? { opacity: 1, y: 0 } : {}}
            transition={{ duration: 0.6, delay: 0.2 }}
          >
            Our <span className="text-green-600">Services</span>
          </motion.h2>
          <motion.p 
            className="text-xl text-gray-600 max-w-2xl mx-auto"
            initial={{ opacity: 0, y: 20 }}
            animate={isInView ? { opacity: 1, y: 0 } : {}}
            transition={{ duration: 0.6, delay: 0.3 }}
          >
            Comprehensive healthcare solutions tailored to your needs
          </motion.p>
        </motion.div>

        {/* Services Grid */}
        <motion.div
          className="grid md:grid-cols-2 lg:grid-cols-3 gap-8"
          variants={container}
          initial="hidden"
          animate={isInView ? "show" : "hidden"}
        >
          {services.map((service, index) => (
            <motion.div
              key={index}
              variants={item}
              whileHover={{ 
                y: -10,
                transition: { duration: 0.3 }
              }}
              className="group relative"
            >
              <div className="relative bg-white rounded-2xl p-8 shadow-lg hover:shadow-2xl transition-all duration-500 overflow-hidden">
                {/* Gradient Background on Hover */}
                <motion.div
                  className={`absolute inset-0 bg-gradient-to-br ${service.color} opacity-0 group-hover:opacity-5 transition-opacity duration-500`}
                />

                {/* Icon Container */}
                <motion.div
                  className={`relative w-16 h-16 bg-gradient-to-br ${service.color} rounded-2xl flex items-center justify-center text-white mb-6 shadow-lg`}
                  whileHover={{ 
                    rotate: [0, -10, 10, -10, 0],
                    scale: 1.1
                  }}
                  transition={{ duration: 0.5 }}
                >
                  {service.icon}
                </motion.div>

                {/* Content */}
                <h3 className="text-2xl font-bold text-gray-900 mb-3 group-hover:text-green-600 transition-colors duration-300">
                  {service.title}
                </h3>
                <p className="text-gray-600 leading-relaxed mb-4">
                  {service.description}
                </p>

                {/* Learn More Link */}
                <motion.button
                  onClick={() => navigate(`/service/${service.id}`)}
                  className="flex items-center text-green-600 font-semibold group-hover:gap-3 gap-2 transition-all duration-300"
                  whileHover={{ x: 5 }}
                >
                  <span>Learn More</span>
                  <motion.span
                    animate={{ x: [0, 5, 0] }}
                    transition={{ duration: 1.5, repeat: Infinity }}
                  >
                    →
                  </motion.span>
                </motion.button>

                {/* Decorative Element */}
                <motion.div
                  className="absolute top-0 right-0 w-20 h-20 bg-gradient-to-br from-green-400/10 to-emerald-400/10 rounded-bl-full"
                  initial={{ scale: 0 }}
                  whileHover={{ scale: 1 }}
                  transition={{ duration: 0.4 }}
                />
              </div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
};

export default ServicesSection;
