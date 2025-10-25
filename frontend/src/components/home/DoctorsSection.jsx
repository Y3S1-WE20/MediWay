import { useRef, useState } from 'react';
import { motion, useInView } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Twitter, Facebook, Instagram, Linkedin } from 'lucide-react';

const DoctorsSection = () => {
  const ref = useRef(null);
  const navigate = useNavigate();
  const isInView = useInView(ref, { once: true, margin: "-100px" });
  const [hoveredIndex, setHoveredIndex] = useState(null);

  const doctors = [
    {
      id: 'sarah-johnson',
      name: 'Dr. Sarah Johnson',
      role: 'Chief Medical Officer',
      image: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400',
      specialty: 'Cardiology',
      description: 'Expert in cardiovascular diseases with over 15 years of experience.',
      social: {
        twitter: '#',
        facebook: '#',
        instagram: '#',
        linkedin: '#'
      }
    },
    {
      id: 'michael-chen',
      name: 'Dr. Michael Chen',
      role: 'Senior Anesthesiologist',
      image: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400',
      specialty: 'Anesthesiology',
      description: 'Specialized in pain management and perioperative care.',
      social: {
        twitter: '#',
        facebook: '#',
        instagram: '#',
        linkedin: '#'
      }
    },
    {
      id: 'emily-williams',
      name: 'Dr. Emily Williams',
      role: 'Lead Neurologist',
      image: 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=400',
      specialty: 'Neurology',
      description: 'Leading expert in neurological disorders and brain health.',
      social: {
        twitter: '#',
        facebook: '#',
        instagram: '#',
        linkedin: '#'
      }
    },
    {
      id: 'james-anderson',
      name: 'Dr. James Anderson',
      role: 'Head of Pediatrics',
      image: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400',
      specialty: 'Pediatrics',
      description: 'Compassionate care for children of all ages.',
      social: {
        twitter: '#',
        facebook: '#',
        instagram: '#',
        linkedin: '#'
      }
    }
  ];

  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: {
        staggerChildren: 0.15
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
    <section ref={ref} className="py-20 bg-gradient-to-b from-gray-50 to-white">
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
            Our Expert <span className="text-green-600">Doctors</span>
          </motion.h2>
          <motion.p 
            className="text-xl text-gray-600 max-w-2xl mx-auto"
            initial={{ opacity: 0, y: 20 }}
            animate={isInView ? { opacity: 1, y: 0 } : {}}
            transition={{ duration: 0.6, delay: 0.3 }}
          >
            Meet our team of dedicated healthcare professionals
          </motion.p>
        </motion.div>

        {/* Doctors Grid */}
        <motion.div
          className="grid md:grid-cols-2 lg:grid-cols-4 gap-8"
          variants={container}
          initial="hidden"
          animate={isInView ? "show" : "hidden"}
        >
          {doctors.map((doctor, index) => (
            <motion.div
              key={index}
              variants={item}
              onHoverStart={() => setHoveredIndex(index)}
              onHoverEnd={() => setHoveredIndex(null)}
              className="group relative"
            >
              <div className="bg-white rounded-2xl overflow-hidden shadow-lg hover:shadow-2xl transition-all duration-500">
                {/* Image Container */}
                <div className="relative h-80 overflow-hidden">
                  <motion.img
                    src={doctor.image}
                    alt={doctor.name}
                    className="w-full h-full object-cover"
                    whileHover={{ scale: 1.1 }}
                    transition={{ duration: 0.6 }}
                    onError={(e) => {
                      e.target.src = 'https://via.placeholder.com/400x500?text=' + doctor.name;
                    }}
                  />
                  
                  {/* Gradient Overlay */}
                  <motion.div
                    className="absolute inset-0 bg-gradient-to-t from-gray-900 via-gray-900/50 to-transparent"
                    initial={{ opacity: 0.6 }}
                    whileHover={{ opacity: 0.8 }}
                    transition={{ duration: 0.3 }}
                  />

                  {/* Social Links - Appear on Hover */}
                  <motion.div
                    className="absolute bottom-4 left-1/2 transform -translate-x-1/2 flex gap-3"
                    initial={{ y: 20, opacity: 0 }}
                    animate={hoveredIndex === index ? { y: 0, opacity: 1 } : { y: 20, opacity: 0 }}
                    transition={{ duration: 0.3 }}
                  >
                    {[
                      { icon: <Twitter className="w-4 h-4" />, link: doctor.social.twitter },
                      { icon: <Facebook className="w-4 h-4" />, link: doctor.social.facebook },
                      { icon: <Instagram className="w-4 h-4" />, link: doctor.social.instagram },
                      { icon: <Linkedin className="w-4 h-4" />, link: doctor.social.linkedin }
                    ].map((social, idx) => (
                      <motion.a
                        key={idx}
                        href={social.link}
                        className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center text-white hover:bg-green-600 transition-all duration-300"
                        whileHover={{ scale: 1.2, rotate: 360 }}
                        whileTap={{ scale: 0.9 }}
                      >
                        {social.icon}
                      </motion.a>
                    ))}
                  </motion.div>

                  {/* Specialty Badge */}
                  <motion.div
                    className="absolute top-4 right-4 bg-green-600 text-white px-4 py-2 rounded-full text-sm font-semibold shadow-lg"
                    initial={{ x: 20, opacity: 0 }}
                    animate={isInView ? { x: 0, opacity: 1 } : {}}
                    transition={{ delay: 0.5 + index * 0.1 }}
                  >
                    {doctor.specialty}
                  </motion.div>
                </div>

                {/* Content */}
                <div className="p-6">
                  <motion.h3
                    className="text-xl font-bold text-gray-900 mb-1"
                    whileHover={{ color: '#16a34a' }}
                  >
                    {doctor.name}
                  </motion.h3>
                  <p className="text-green-600 font-semibold mb-3">{doctor.role}</p>
                  <p className="text-gray-600 text-sm leading-relaxed">
                    {doctor.description}
                  </p>

                  {/* View Profile Button */}
                  <motion.button
                    onClick={() => navigate(`/doctor/${doctor.id}`)}
                    className="mt-4 w-full py-2 bg-green-50 text-green-600 rounded-lg font-semibold hover:bg-green-600 hover:text-white transition-all duration-300"
                    whileHover={{ scale: 1.02 }}
                    whileTap={{ scale: 0.98 }}
                  >
                    View Profile
                  </motion.button>
                </div>
              </div>
            </motion.div>
          ))}
        </motion.div>
      </div>
    </section>
  );
};

export default DoctorsSection;
