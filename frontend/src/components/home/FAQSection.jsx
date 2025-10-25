import { useState, useRef } from 'react';
import { motion, useInView, AnimatePresence } from 'framer-motion';
import { Plus, Minus } from 'lucide-react';

const FAQSection = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: "-100px" });
  const [openIndex, setOpenIndex] = useState(0);

  const faqs = [
    {
      question: 'How do I book an appointment?',
      answer: 'Booking an appointment is easy! Simply click on the "Book Appointment" button on our homepage, select your preferred doctor and time slot, and fill in your details. You will receive a confirmation email once your appointment is confirmed.'
    },
    {
      question: 'What are your operating hours?',
      answer: 'We are open 24/7 for emergency services. Our outpatient department operates from 8:00 AM to 8:00 PM on weekdays and 9:00 AM to 5:00 PM on weekends. Specialized departments may have different timings.'
    },
    {
      question: 'Do you accept insurance?',
      answer: 'Yes, we accept most major insurance plans. Please contact our billing department or check our website for a complete list of accepted insurance providers. We also offer flexible payment options for self-pay patients.'
    },
    {
      question: 'Can I access my medical records online?',
      answer: 'Absolutely! Our patient portal allows you to securely access your medical records, test results, prescriptions, and appointment history anytime, anywhere. Simply create an account using your patient ID.'
    },
    {
      question: 'What should I bring to my first appointment?',
      answer: 'Please bring a valid ID, your insurance card (if applicable), a list of current medications, and any relevant medical records from previous healthcare providers. Arriving 15 minutes early is recommended.'
    },
    {
      question: 'How do I get my test results?',
      answer: 'Test results are typically available within 2-5 business days and will be shared through our patient portal. You will receive an email notification when results are ready. Urgent results will be communicated directly by your doctor.'
    }
  ];

  const toggleFAQ = (index) => {
    setOpenIndex(openIndex === index ? -1 : index);
  };

  return (
    <section ref={ref} className="py-20 bg-gradient-to-b from-white to-gray-50">
      <div className="container mx-auto px-4">
        {/* Section Header */}
        <motion.div
          className="text-center mb-16 max-w-3xl mx-auto"
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.6 }}
        >
          <motion.div
            className="inline-block mb-4 px-4 py-2 bg-green-100 rounded-full"
            initial={{ opacity: 0, scale: 0.8 }}
            animate={isInView ? { opacity: 1, scale: 1 } : {}}
            transition={{ delay: 0.2 }}
          >
            <span className="text-green-600 font-semibold">FAQ</span>
          </motion.div>

          <motion.h2
            className="text-4xl md:text-5xl font-bold text-gray-900 mb-4"
            initial={{ opacity: 0, y: 20 }}
            animate={isInView ? { opacity: 1, y: 0 } : {}}
            transition={{ delay: 0.3 }}
          >
            Frequently Asked <span className="text-green-600">Questions</span>
          </motion.h2>

          <motion.p
            className="text-xl text-gray-600"
            initial={{ opacity: 0, y: 20 }}
            animate={isInView ? { opacity: 1, y: 0 } : {}}
            transition={{ delay: 0.4 }}
          >
            Find answers to common questions about our services
          </motion.p>
        </motion.div>

        {/* FAQ List */}
        <motion.div
          className="max-w-3xl mx-auto space-y-4"
          initial={{ opacity: 0 }}
          animate={isInView ? { opacity: 1 } : {}}
          transition={{ delay: 0.5 }}
        >
          {faqs.map((faq, index) => (
            <motion.div
              key={index}
              className="bg-white rounded-2xl shadow-lg overflow-hidden"
              initial={{ opacity: 0, y: 20 }}
              animate={isInView ? { opacity: 1, y: 0 } : {}}
              transition={{ delay: 0.5 + index * 0.1 }}
            >
              <motion.button
                className="w-full px-6 py-5 flex items-center justify-between text-left hover:bg-gray-50 transition-colors duration-300"
                onClick={() => toggleFAQ(index)}
                whileHover={{ backgroundColor: '#f9fafb' }}
              >
                <span className="text-lg font-bold text-gray-900 pr-8">
                  {faq.question}
                </span>

                <motion.div
                  className="flex-shrink-0 w-10 h-10 bg-green-100 rounded-full flex items-center justify-center text-green-600"
                  animate={{ 
                    backgroundColor: openIndex === index ? '#16a34a' : '#dcfce7',
                    color: openIndex === index ? '#ffffff' : '#16a34a'
                  }}
                  whileHover={{ scale: 1.1 }}
                  transition={{ duration: 0.2 }}
                >
                  <AnimatePresence mode="wait">
                    {openIndex === index ? (
                      <motion.div
                        key="minus"
                        initial={{ rotate: -90, opacity: 0 }}
                        animate={{ rotate: 0, opacity: 1 }}
                        exit={{ rotate: 90, opacity: 0 }}
                        transition={{ duration: 0.2 }}
                      >
                        <Minus className="w-5 h-5" />
                      </motion.div>
                    ) : (
                      <motion.div
                        key="plus"
                        initial={{ rotate: 90, opacity: 0 }}
                        animate={{ rotate: 0, opacity: 1 }}
                        exit={{ rotate: -90, opacity: 0 }}
                        transition={{ duration: 0.2 }}
                      >
                        <Plus className="w-5 h-5" />
                      </motion.div>
                    )}
                  </AnimatePresence>
                </motion.div>
              </motion.button>

              <AnimatePresence>
                {openIndex === index && (
                  <motion.div
                    initial={{ height: 0, opacity: 0 }}
                    animate={{ height: 'auto', opacity: 1 }}
                    exit={{ height: 0, opacity: 0 }}
                    transition={{ duration: 0.3, ease: 'easeInOut' }}
                    className="overflow-hidden"
                  >
                    <motion.div
                      className="px-6 pb-5"
                      initial={{ y: -10 }}
                      animate={{ y: 0 }}
                      transition={{ duration: 0.3 }}
                    >
                      <div className="pt-2 border-t border-gray-100">
                        <p className="text-gray-600 leading-relaxed mt-3">
                          {faq.answer}
                        </p>
                      </div>
                    </motion.div>
                  </motion.div>
                )}
              </AnimatePresence>
            </motion.div>
          ))}
        </motion.div>

        {/* Bottom CTA */}
        <motion.div
          className="text-center mt-12"
          initial={{ opacity: 0, y: 20 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ delay: 1 }}
        >
          <p className="text-gray-600 mb-4">Still have questions?</p>
          <motion.button
            className="px-8 py-3 bg-green-600 text-white rounded-lg font-semibold shadow-lg hover:bg-green-700 transition-all duration-300"
            whileHover={{ scale: 1.05, boxShadow: '0 20px 40px rgba(34, 197, 94, 0.3)' }}
            whileTap={{ scale: 0.95 }}
          >
            Contact Support
          </motion.button>
        </motion.div>
      </div>
    </section>
  );
};

export default FAQSection;
