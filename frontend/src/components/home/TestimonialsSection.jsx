import { useRef, useState } from 'react';
import { motion, useInView, AnimatePresence } from 'framer-motion';
import { Star, Quote, ChevronLeft, ChevronRight } from 'lucide-react';

const TestimonialsSection = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: "-100px" });
  const [currentIndex, setCurrentIndex] = useState(0);

  const testimonials = [
    {
      name: 'Sarah Mitchell',
      role: 'Patient',
      image: '/src/assets/img/testimonials/testimonials-1.jpg',
      rating: 5,
      text: 'Outstanding medical care! The doctors are extremely professional and caring. I felt completely at ease throughout my treatment. Highly recommend MediWay to anyone seeking quality healthcare.'
    },
    {
      name: 'John Davis',
      role: 'Patient',
      image: '/src/assets/img/testimonials/testimonials-2.jpg',
      rating: 5,
      text: 'The online booking system is incredibly convenient, and the staff is always friendly and helpful. My family and I have been patients here for years and we couldn\'t be happier.'
    },
    {
      name: 'Emily Chen',
      role: 'Patient',
      image: '/src/assets/img/testimonials/testimonials-3.jpg',
      rating: 5,
      text: 'Exceptional service from start to finish. The facility is modern and clean, and the medical team is top-notch. I appreciate the personalized care and attention to detail.'
    },
    {
      name: 'Michael Brown',
      role: 'Patient',
      image: '/src/assets/img/testimonials/testimonials-4.jpg',
      rating: 5,
      text: 'MediWay has been a game-changer for my health. The doctors take time to listen and provide thorough explanations. The technology they use is impressive and makes everything seamless.'
    },
    {
      name: 'Lisa Anderson',
      role: 'Patient',
      image: '/src/assets/img/testimonials/testimonials-5.jpg',
      rating: 5,
      text: 'I was nervous about my first visit, but the team made me feel so comfortable. The follow-up care has been excellent. Grateful to have found such a reliable healthcare partner.'
    }
  ];

  const nextTestimonial = () => {
    setCurrentIndex((prev) => (prev + 1) % testimonials.length);
  };

  const prevTestimonial = () => {
    setCurrentIndex((prev) => (prev - 1 + testimonials.length) % testimonials.length);
  };

  return (
    <section ref={ref} className="py-20 bg-gradient-to-br from-green-50 via-white to-emerald-50 overflow-hidden">
      <div className="container mx-auto px-4">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Left Content */}
          <motion.div
            initial={{ opacity: 0, x: -50 }}
            animate={isInView ? { opacity: 1, x: 0 } : {}}
            transition={{ duration: 0.6 }}
          >
            <motion.div
              className="inline-block mb-4"
              whileHover={{ scale: 1.1 }}
            >
              <Quote className="w-16 h-16 text-green-600" />
            </motion.div>
            
            <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
              Patient <span className="text-green-600">Testimonials</span>
            </h2>
            
            <p className="text-xl text-gray-600 mb-8 leading-relaxed">
              Don't just take our word for it. Hear what our patients have to say about their
              experience with MediWay. Your health and satisfaction are our top priorities.
            </p>

            {/* Statistics */}
            <div className="grid grid-cols-3 gap-6">
              {[
                { number: '5000+', label: 'Happy Patients' },
                { number: '4.9', label: 'Average Rating' },
                { number: '98%', label: 'Satisfaction' }
              ].map((stat, index) => (
                <motion.div
                  key={index}
                  className="text-center"
                  initial={{ opacity: 0, y: 20 }}
                  animate={isInView ? { opacity: 1, y: 0 } : {}}
                  transition={{ delay: 0.3 + index * 0.1 }}
                >
                  <div className="text-3xl font-bold text-green-600 mb-1">{stat.number}</div>
                  <div className="text-sm text-gray-600">{stat.label}</div>
                </motion.div>
              ))}
            </div>
          </motion.div>

          {/* Right Content - Testimonial Slider */}
          <motion.div
            className="relative"
            initial={{ opacity: 0, x: 50 }}
            animate={isInView ? { opacity: 1, x: 0 } : {}}
            transition={{ duration: 0.6 }}
          >
            <div className="relative bg-white rounded-3xl shadow-2xl p-8 overflow-hidden">
              {/* Background Decoration */}
              <div className="absolute top-0 right-0 w-40 h-40 bg-green-100 rounded-full -mr-20 -mt-20 opacity-50" />
              <div className="absolute bottom-0 left-0 w-32 h-32 bg-emerald-100 rounded-full -ml-16 -mb-16 opacity-50" />

              <AnimatePresence mode="wait">
                <motion.div
                  key={currentIndex}
                  initial={{ opacity: 0, x: 50 }}
                  animate={{ opacity: 1, x: 0 }}
                  exit={{ opacity: 0, x: -50 }}
                  transition={{ duration: 0.5 }}
                  className="relative z-10"
                >
                  {/* Author Info */}
                  <div className="flex items-center mb-6">
                    <motion.div
                      className="relative"
                      whileHover={{ scale: 1.1 }}
                    >
                      <img
                        src={testimonials[currentIndex].image}
                        alt={testimonials[currentIndex].name}
                        className="w-20 h-20 rounded-full object-cover border-4 border-green-100"
                        onError={(e) => {
                          e.target.src = 'https://via.placeholder.com/100?text=' + testimonials[currentIndex].name.charAt(0);
                        }}
                      />
                      <div className="absolute -bottom-1 -right-1 w-8 h-8 bg-green-600 rounded-full flex items-center justify-center">
                        <Star className="w-4 h-4 text-white fill-white" />
                      </div>
                    </motion.div>

                    <div className="ml-4">
                      <h4 className="text-xl font-bold text-gray-900">
                        {testimonials[currentIndex].name}
                      </h4>
                      <p className="text-green-600 font-semibold">
                        {testimonials[currentIndex].role}
                      </p>
                    </div>
                  </div>

                  {/* Rating */}
                  <div className="flex gap-1 mb-4">
                    {[...Array(testimonials[currentIndex].rating)].map((_, i) => (
                      <motion.div
                        key={i}
                        initial={{ opacity: 0, scale: 0 }}
                        animate={{ opacity: 1, scale: 1 }}
                        transition={{ delay: i * 0.1 }}
                      >
                        <Star className="w-5 h-5 text-yellow-400 fill-yellow-400" />
                      </motion.div>
                    ))}
                  </div>

                  {/* Testimonial Text */}
                  <p className="text-gray-700 text-lg leading-relaxed italic">
                    "{testimonials[currentIndex].text}"
                  </p>
                </motion.div>
              </AnimatePresence>

              {/* Navigation Buttons */}
              <div className="flex justify-between items-center mt-8 relative z-10">
                <motion.button
                  onClick={prevTestimonial}
                  className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center text-green-600 hover:bg-green-600 hover:text-white transition-all duration-300"
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                >
                  <ChevronLeft className="w-6 h-6" />
                </motion.button>

                {/* Dots Indicator */}
                <div className="flex gap-2">
                  {testimonials.map((_, index) => (
                    <motion.button
                      key={index}
                      onClick={() => setCurrentIndex(index)}
                      className={`h-2 rounded-full transition-all duration-300 ${
                        index === currentIndex 
                          ? 'w-8 bg-green-600' 
                          : 'w-2 bg-gray-300 hover:bg-green-400'
                      }`}
                      whileHover={{ scale: 1.2 }}
                    />
                  ))}
                </div>

                <motion.button
                  onClick={nextTestimonial}
                  className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center text-green-600 hover:bg-green-600 hover:text-white transition-all duration-300"
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                >
                  <ChevronRight className="w-6 h-6" />
                </motion.button>
              </div>
            </div>
          </motion.div>
        </div>
      </div>
    </section>
  );
};

export default TestimonialsSection;
