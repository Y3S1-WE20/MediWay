import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Calendar, ArrowRight, Phone, Mail } from 'lucide-react';

const CTASection = () => {
  const navigate = useNavigate();

  return (
    <section className="relative py-20 overflow-hidden">
      {/* Animated Gradient Background */}
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
            backgroundSize: '200% 200%'
          }}
        />
      </div>

      {/* Floating Elements */}
      <div className="absolute inset-0 overflow-hidden">
        {[...Array(15)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute w-2 h-2 bg-white/20 rounded-full"
            style={{
              left: `${Math.random() * 100}%`,
              top: `${Math.random() * 100}%`,
            }}
            animate={{
              y: [0, -30, 0],
              opacity: [0.2, 0.8, 0.2],
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
        <div className="max-w-4xl mx-auto">
          {/* Main Content */}
          <motion.div
            className="text-center mb-12"
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
          >
            <motion.div
              className="inline-block mb-6"
              animate={{
                scale: [1, 1.1, 1],
              }}
              transition={{
                duration: 2,
                repeat: Infinity,
              }}
            >
              <Calendar className="w-16 h-16 text-white mx-auto" />
            </motion.div>

            <motion.h2
              className="text-4xl md:text-5xl font-bold text-white mb-6"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.2 }}
            >
              Ready to Take Care of Your Health?
            </motion.h2>

            <motion.p
              className="text-xl text-white/90 mb-8 max-w-2xl mx-auto"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.3 }}
            >
              Book your appointment today and experience world-class healthcare
              with our expert medical team.
            </motion.p>

            {/* CTA Buttons */}
            <motion.div
              className="flex flex-col sm:flex-row gap-4 justify-center mb-12"
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.4 }}
            >
              <motion.button
                onClick={() => navigate('/book-appointment')}
                className="px-8 py-4 bg-white text-green-600 rounded-lg font-semibold shadow-lg hover:bg-gray-100 transition-all duration-300 flex items-center justify-center gap-2"
                whileHover={{ scale: 1.05, boxShadow: '0 20px 40px rgba(255, 255, 255, 0.3)' }}
                whileTap={{ scale: 0.95 }}
              >
                <Calendar className="w-5 h-5" />
                Book Appointment Now
                <motion.span
                  animate={{ x: [0, 5, 0] }}
                  transition={{ duration: 1.5, repeat: Infinity }}
                >
                  <ArrowRight className="w-5 h-5" />
                </motion.span>
              </motion.button>

              <motion.button
                onClick={() => navigate('/register')}
                className="px-8 py-4 bg-transparent border-2 border-white text-white rounded-lg font-semibold hover:bg-white hover:text-green-600 transition-all duration-300"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Create Account
              </motion.button>
            </motion.div>
          </motion.div>

          {/* Contact Info Cards */}
          <motion.div
            className="grid md:grid-cols-2 gap-6"
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ delay: 0.5 }}
          >
            <motion.div
              className="bg-white/10 backdrop-blur-md rounded-2xl p-6 border border-white/20"
              whileHover={{ 
                scale: 1.05,
                backgroundColor: 'rgba(255, 255, 255, 0.15)'
              }}
              transition={{ duration: 0.3 }}
            >
              <div className="flex items-center gap-4">
                <motion.div
                  className="w-14 h-14 bg-white/20 rounded-full flex items-center justify-center"
                  whileHover={{ rotate: 360 }}
                  transition={{ duration: 0.6 }}
                >
                  <Phone className="w-7 h-7 text-white" />
                </motion.div>
                <div>
                  <h4 className="text-white/80 text-sm font-semibold mb-1">Call Us</h4>
                  <p className="text-white text-xl font-bold">+1 (555) 123-4567</p>
                </div>
              </div>
            </motion.div>

            <motion.div
              className="bg-white/10 backdrop-blur-md rounded-2xl p-6 border border-white/20"
              whileHover={{ 
                scale: 1.05,
                backgroundColor: 'rgba(255, 255, 255, 0.15)'
              }}
              transition={{ duration: 0.3 }}
            >
              <div className="flex items-center gap-4">
                <motion.div
                  className="w-14 h-14 bg-white/20 rounded-full flex items-center justify-center"
                  whileHover={{ rotate: 360 }}
                  transition={{ duration: 0.6 }}
                >
                  <Mail className="w-7 h-7 text-white" />
                </motion.div>
                <div>
                  <h4 className="text-white/80 text-sm font-semibold mb-1">Email Us</h4>
                  <p className="text-white text-xl font-bold">info@mediway.com</p>
                </div>
              </div>
            </motion.div>
          </motion.div>

          {/* Trust Indicators */}
          <motion.div
            className="mt-12 pt-8 border-t border-white/20 text-center"
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            transition={{ delay: 0.6 }}
          >
            <p className="text-white/80 mb-4">Trusted by over 50,000+ patients</p>
            <div className="flex justify-center gap-8 flex-wrap">
              {['24/7 Support', 'Verified Doctors', 'Secure Payments', 'Easy Booking'].map((item, index) => (
                <motion.div
                  key={index}
                  className="flex items-center gap-2 text-white"
                  initial={{ opacity: 0, x: -20 }}
                  whileInView={{ opacity: 1, x: 0 }}
                  viewport={{ once: true }}
                  transition={{ delay: 0.7 + index * 0.1 }}
                >
                  <div className="w-2 h-2 bg-white rounded-full" />
                  <span className="text-sm font-medium">{item}</span>
                </motion.div>
              ))}
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default CTASection;
