import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { 
  Heart, 
  Users, 
  Award, 
  Shield, 
  TrendingUp, 
  Globe,
  CheckCircle,
  ArrowRight,
  Star,
  Target,
  Eye,
  Lightbulb
} from 'lucide-react';

const About = () => {
  const navigate = useNavigate();

  const stats = [
    { number: '25+', label: 'Years of Excellence', icon: <Award className="w-6 h-6" /> },
    { number: '85+', label: 'Expert Doctors', icon: <Users className="w-6 h-6" /> },
    { number: '50K+', label: 'Happy Patients', icon: <Heart className="w-6 h-6" /> },
    { number: '150+', label: 'Awards Won', icon: <Star className="w-6 h-6" /> }
  ];

  const values = [
    {
      icon: <Heart className="w-8 h-8" />,
      title: 'Patient-Centered Care',
      description: 'We put our patients at the heart of everything we do, ensuring personalized and compassionate healthcare.'
    },
    {
      icon: <Award className="w-8 h-8" />,
      title: 'Excellence',
      description: 'We strive for excellence in medical care, continuous improvement, and professional development.'
    },
    {
      icon: <Shield className="w-8 h-8" />,
      title: 'Integrity',
      description: 'We maintain the highest standards of ethics, honesty, and transparency in all our interactions.'
    },
    {
      icon: <Lightbulb className="w-8 h-8" />,
      title: 'Innovation',
      description: 'We embrace cutting-edge technology and innovative approaches to deliver the best healthcare solutions.'
    }
  ];

  const milestones = [
    {
      year: '2000',
      title: 'MediWay Founded',
      description: 'Started with a vision to provide world-class healthcare to the community.'
    },
    {
      year: '2005',
      title: 'Expansion',
      description: 'Opened three new specialized departments including Cardiology and Neurology.'
    },
    {
      year: '2010',
      title: 'Technology Integration',
      description: 'Introduced electronic health records and telemedicine services.'
    },
    {
      year: '2015',
      title: 'Research Center',
      description: 'Established our medical research center for clinical trials and innovations.'
    },
    {
      year: '2020',
      title: 'Digital Transformation',
      description: 'Launched comprehensive digital healthcare platform and mobile app.'
    },
    {
      year: '2025',
      title: 'Global Recognition',
      description: 'Recognized as one of the top healthcare providers internationally.'
    }
  ];

  const team = [
    {
      name: 'Dr. Robert Chen',
      role: 'CEO & Medical Director',
      image: 'https://images.unsplash.com/photo-1560250097-0b93528c311a?w=400',
      description: 'Leading MediWay with 30+ years of healthcare expertise'
    },
    {
      name: 'Dr. Lisa Martinez',
      role: 'Chief of Surgery',
      image: 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400',
      description: 'Renowned surgeon with expertise in minimally invasive procedures'
    },
    {
      name: 'Dr. David Thompson',
      role: 'Head of Research',
      image: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400',
      description: 'Pioneer in medical research and clinical innovations'
    },
    {
      name: 'Sarah Williams',
      role: 'Director of Nursing',
      image: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=400',
      description: 'Passionate about patient care and nursing excellence'
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-green-600 to-emerald-600 text-white py-24">
        <div className="absolute inset-0 overflow-hidden">
          <div className="absolute -top-40 -right-40 w-96 h-96 bg-white/10 rounded-full blur-3xl" />
          <div className="absolute -bottom-40 -left-40 w-96 h-96 bg-white/10 rounded-full blur-3xl" />
        </div>
        
        <div className="max-w-7xl mx-auto px-4 relative z-10">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="text-center max-w-3xl mx-auto"
          >
            <h1 className="text-5xl md:text-6xl font-bold mb-6">About MediWay</h1>
            <p className="text-xl text-white/90">
              Dedicated to providing exceptional healthcare services with compassion, 
              innovation, and excellence since 2000.
            </p>
          </motion.div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {stats.map((stat, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: index * 0.1 }}
                className="text-center"
              >
                <div className="w-16 h-16 bg-green-100 rounded-2xl flex items-center justify-center text-green-600 mx-auto mb-4">
                  {stat.icon}
                </div>
                <h3 className="text-4xl font-bold text-gray-900 mb-2">{stat.number}</h3>
                <p className="text-gray-600">{stat.label}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Mission & Vision */}
      <section className="py-20">
        <div className="max-w-7xl mx-auto px-4">
          <div className="grid md:grid-cols-2 gap-12">
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              className="bg-white rounded-2xl shadow-lg p-8"
            >
              <div className="w-16 h-16 bg-green-100 rounded-2xl flex items-center justify-center text-green-600 mb-6">
                <Target className="w-8 h-8" />
              </div>
              <h2 className="text-3xl font-bold text-gray-900 mb-4">Our Mission</h2>
              <p className="text-gray-600 leading-relaxed">
                To provide accessible, high-quality healthcare services that improve the well-being 
                of our community. We are committed to delivering patient-centered care through 
                innovation, compassion, and clinical excellence while maintaining the highest 
                standards of safety and ethics.
              </p>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, x: 20 }}
              whileInView={{ opacity: 1, x: 0 }}
              viewport={{ once: true }}
              className="bg-white rounded-2xl shadow-lg p-8"
            >
              <div className="w-16 h-16 bg-green-100 rounded-2xl flex items-center justify-center text-green-600 mb-6">
                <Eye className="w-8 h-8" />
              </div>
              <h2 className="text-3xl font-bold text-gray-900 mb-4">Our Vision</h2>
              <p className="text-gray-600 leading-relaxed">
                To be the leading healthcare provider recognized for transforming lives through 
                exceptional care, pioneering medical research, and innovative health solutions. 
                We envision a healthier future where every individual has access to world-class 
                medical services and personalized treatment.
              </p>
            </motion.div>
          </div>
        </div>
      </section>

      {/* Core Values */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-center mb-16"
          >
            <h2 className="text-4xl font-bold text-gray-900 mb-4">Our Core Values</h2>
            <p className="text-xl text-gray-600">The principles that guide everything we do</p>
          </motion.div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {values.map((value, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: index * 0.1 }}
                className="bg-gradient-to-br from-green-50 to-emerald-50 rounded-2xl p-6 hover:shadow-xl transition-shadow"
              >
                <div className="w-16 h-16 bg-green-600 text-white rounded-2xl flex items-center justify-center mb-4">
                  {value.icon}
                </div>
                <h3 className="text-xl font-bold text-gray-900 mb-3">{value.title}</h3>
                <p className="text-gray-600">{value.description}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Timeline */}
      <section className="py-20">
        <div className="max-w-7xl mx-auto px-4">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-center mb-16"
          >
            <h2 className="text-4xl font-bold text-gray-900 mb-4">Our Journey</h2>
            <p className="text-xl text-gray-600">Key milestones in our history</p>
          </motion.div>

          <div className="relative">
            {/* Timeline line */}
            <div className="absolute left-1/2 transform -translate-x-1/2 h-full w-1 bg-green-200 hidden md:block" />

            <div className="space-y-12">
              {milestones.map((milestone, index) => (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, y: 20 }}
                  whileInView={{ opacity: 1, y: 0 }}
                  viewport={{ once: true }}
                  transition={{ delay: index * 0.1 }}
                  className={`flex flex-col md:flex-row gap-8 items-center ${
                    index % 2 === 0 ? 'md:flex-row-reverse' : ''
                  }`}
                >
                  <div className="flex-1" />
                  <div className="relative">
                    <div className="w-16 h-16 bg-green-600 text-white rounded-full flex items-center justify-center font-bold text-lg shadow-lg z-10 relative">
                      {milestone.year}
                    </div>
                  </div>
                  <div className="flex-1">
                    <div className="bg-white rounded-2xl shadow-lg p-6">
                      <h3 className="text-xl font-bold text-gray-900 mb-2">{milestone.title}</h3>
                      <p className="text-gray-600">{milestone.description}</p>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Leadership Team */}
      <section className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="text-center mb-16"
          >
            <h2 className="text-4xl font-bold text-gray-900 mb-4">Leadership Team</h2>
            <p className="text-xl text-gray-600">Meet the experts leading our mission</p>
          </motion.div>

          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {team.map((member, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: index * 0.1 }}
                className="group bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-2xl transition-shadow"
              >
                <div className="relative h-64 overflow-hidden">
                  <img
                    src={member.image}
                    alt={member.name}
                    className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
                  <div className="absolute bottom-4 left-4 right-4 text-white">
                    <h3 className="text-xl font-bold mb-1">{member.name}</h3>
                    <p className="text-sm text-white/90">{member.role}</p>
                  </div>
                </div>
                <div className="p-6">
                  <p className="text-gray-600 text-sm">{member.description}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-gradient-to-br from-green-600 to-emerald-600 text-white">
        <div className="max-w-4xl mx-auto px-4 text-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
          >
            <h2 className="text-4xl font-bold mb-6">Join Us in Our Mission</h2>
            <p className="text-xl text-white/90 mb-8">
              Experience healthcare excellence with MediWay. Book your appointment today 
              and let us take care of your health.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <motion.button
                onClick={() => navigate('/book-appointment')}
                className="px-8 py-4 bg-white text-green-600 rounded-lg font-semibold hover:bg-gray-100 transition-colors inline-flex items-center justify-center gap-2"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Book Appointment
                <ArrowRight className="w-5 h-5" />
              </motion.button>
              <motion.button
                onClick={() => navigate('/login')}
                className="px-8 py-4 bg-transparent border-2 border-white text-white rounded-lg font-semibold hover:bg-white/10 transition-colors"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Contact Us
              </motion.button>
            </div>
          </motion.div>
        </div>
      </section>
    </div>
  );
};

export default About;
