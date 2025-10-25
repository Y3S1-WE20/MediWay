import { useRef, useEffect } from 'react';
import { motion, useInView } from 'framer-motion';
import { gsap } from 'gsap';
import { Users, Building2, Award, FlaskConical } from 'lucide-react';

const StatsSection = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: "-100px" });
  const numbersRef = useRef([]);

  const stats = [
    { 
      icon: <Users className="w-12 h-12" />, 
      number: 85, 
      label: 'Expert Doctors',
      color: 'from-blue-500 to-cyan-500'
    },
    { 
      icon: <Building2 className="w-12 h-12" />, 
      number: 18, 
      label: 'Departments',
      color: 'from-green-500 to-emerald-500'
    },
    { 
      icon: <FlaskConical className="w-12 h-12" />, 
      number: 12, 
      label: 'Research Labs',
      color: 'from-purple-500 to-pink-500'
    },
    { 
      icon: <Award className="w-12 h-12" />, 
      number: 150, 
      label: 'Awards Won',
      color: 'from-orange-500 to-red-500'
    }
  ];

  useEffect(() => {
    if (isInView) {
      numbersRef.current.forEach((el, index) => {
        if (el) {
          const target = stats[index].number;
          gsap.to(el, {
            innerText: target,
            duration: 2,
            ease: 'power2.out',
            snap: { innerText: 1 },
            delay: index * 0.1
          });
        }
      });
    }
  }, [isInView]);

  return (
    <section ref={ref} className="relative py-20 overflow-hidden">
      {/* Animated Background */}
      <div className="absolute inset-0 bg-gradient-to-br from-green-600 via-emerald-600 to-green-700">
        <motion.div
          className="absolute inset-0"
          animate={{
            backgroundPosition: ['0% 0%', '100% 100%'],
          }}
          transition={{
            duration: 20,
            repeat: Infinity,
            repeatType: 'reverse'
          }}
          style={{
            backgroundImage: 'radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 0%, transparent 50%), radial-gradient(circle at 80% 80%, rgba(255,255,255,0.1) 0%, transparent 50%)',
            backgroundSize: '100% 100%'
          }}
        />
      </div>

      {/* Floating Particles */}
      <div className="absolute inset-0 overflow-hidden">
        {[...Array(20)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute w-2 h-2 bg-white/20 rounded-full"
            style={{
              left: `${Math.random() * 100}%`,
              top: `${Math.random() * 100}%`,
            }}
            animate={{
              y: [0, -30, 0],
              opacity: [0.2, 0.5, 0.2],
            }}
            transition={{
              duration: 3 + Math.random() * 2,
              repeat: Infinity,
              delay: Math.random() * 2,
            }}
          />
        ))}
      </div>

      <div className="container mx-auto px-4 relative z-10">
        <motion.div
          className="grid grid-cols-2 lg:grid-cols-4 gap-8"
          initial={{ opacity: 0 }}
          animate={isInView ? { opacity: 1 } : {}}
          transition={{ duration: 0.8 }}
        >
          {stats.map((stat, index) => (
            <motion.div
              key={index}
              className="text-center group"
              initial={{ opacity: 0, y: 50 }}
              animate={isInView ? { opacity: 1, y: 0 } : {}}
              transition={{ 
                duration: 0.6, 
                delay: index * 0.1,
                ease: "easeOut"
              }}
            >
              <motion.div
                className="flex justify-center mb-6"
                whileHover={{ scale: 1.1, rotate: 5 }}
                transition={{ duration: 0.3 }}
              >
                <div className={`bg-gradient-to-br ${stat.color} p-4 rounded-2xl text-white shadow-2xl group-hover:shadow-3xl transition-all duration-300`}>
                  {stat.icon}
                </div>
              </motion.div>

              <motion.div
                className="text-6xl font-bold text-white mb-2"
                whileHover={{ scale: 1.1 }}
              >
                <span ref={(el) => (numbersRef.current[index] = el)}>0</span>
                {stat.number >= 100 && '+'}
              </motion.div>

              <motion.p
                className="text-xl text-white/90 font-semibold"
                initial={{ opacity: 0 }}
                animate={isInView ? { opacity: 1 } : {}}
                transition={{ delay: 0.5 + index * 0.1 }}
              >
                {stat.label}
              </motion.p>

              {/* Decorative Line */}
              <motion.div
                className="mt-4 h-1 bg-white/30 mx-auto"
                initial={{ width: 0 }}
                animate={isInView ? { width: '60%' } : {}}
                transition={{ delay: 0.7 + index * 0.1, duration: 0.8 }}
              />
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
};

export default StatsSection;
