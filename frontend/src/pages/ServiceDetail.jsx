import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { 
  ArrowLeft, 
  Heart, 
  Stethoscope, 
  Brain, 
  Pill, 
  AlertCircle, 
  Baby,
  CheckCircle,
  Clock,
  Award,
  Users
} from 'lucide-react';

// Mock services data
const servicesData = {
  cardiology: {
    id: 1,
    name: 'Cardiology',
    icon: <Heart className="w-8 h-8" />,
    gradient: 'from-red-500 to-pink-500',
    description: 'Expert heart care for all cardiac conditions',
    fullDescription: 'Our Cardiology department offers comprehensive cardiovascular care with state-of-the-art diagnostic and treatment facilities. We specialize in both preventive cardiology and advanced interventional procedures.',
    image: 'https://images.unsplash.com/photo-1628348068343-c6a848d2b6dd?w=800',
    services: [
      'Cardiac Catheterization',
      'Echocardiography',
      'Stress Testing',
      'Holter Monitoring',
      'Pacemaker Implantation',
      'Coronary Angioplasty',
      'Heart Failure Management',
      'Arrhythmia Treatment'
    ],
    features: [
      {
        title: '24/7 Emergency Care',
        description: 'Round-the-clock cardiac emergency services'
      },
      {
        title: 'Advanced Technology',
        description: 'Latest cardiac imaging and intervention tools'
      },
      {
        title: 'Expert Team',
        description: 'Board-certified cardiologists and cardiac surgeons'
      },
      {
        title: 'Personalized Treatment',
        description: 'Customized care plans for each patient'
      }
    ],
    stats: {
      patients: '10,000+',
      procedures: '5,000+',
      successRate: '98%',
      experience: '20+ years'
    }
  },
  'general-medicine': {
    id: 2,
    name: 'General Medicine',
    icon: <Stethoscope className="w-8 h-8" />,
    gradient: 'from-blue-500 to-cyan-500',
    description: 'Comprehensive primary care services',
    fullDescription: 'Our General Medicine department provides comprehensive primary healthcare services for patients of all ages. We focus on disease prevention, health maintenance, and management of chronic conditions.',
    image: 'https://images.unsplash.com/photo-1666214280557-f1b5022eb634?w=800',
    services: [
      'Annual Health Checkups',
      'Chronic Disease Management',
      'Preventive Care',
      'Vaccination Services',
      'Health Screenings',
      'Lifestyle Counseling',
      'Minor Procedures',
      'Prescription Management'
    ],
    features: [
      {
        title: 'Holistic Approach',
        description: 'Complete patient-centered care'
      },
      {
        title: 'Preventive Focus',
        description: 'Emphasis on disease prevention'
      },
      {
        title: 'Chronic Care',
        description: 'Expert management of long-term conditions'
      },
      {
        title: 'Family Medicine',
        description: 'Care for the entire family'
      }
    ],
    stats: {
      patients: '25,000+',
      procedures: '15,000+',
      successRate: '96%',
      experience: '25+ years'
    }
  },
  neurology: {
    id: 3,
    name: 'Neurology',
    icon: <Brain className="w-8 h-8" />,
    gradient: 'from-purple-500 to-pink-500',
    description: 'Advanced neurological care and treatment',
    fullDescription: 'Our Neurology department specializes in the diagnosis and treatment of disorders affecting the brain, spinal cord, and nervous system. We employ cutting-edge diagnostic tools and evidence-based treatment protocols.',
    image: 'https://images.unsplash.com/photo-1559757175-5700dde675bc?w=800',
    services: [
      'Stroke Management',
      'Epilepsy Treatment',
      'Movement Disorders',
      'Headache Management',
      'Memory Disorders',
      'Neuropathy Care',
      'Multiple Sclerosis Treatment',
      'Neurological Rehabilitation'
    ],
    features: [
      {
        title: 'Advanced Imaging',
        description: 'State-of-the-art neuroimaging facilities'
      },
      {
        title: 'Stroke Center',
        description: 'Certified comprehensive stroke center'
      },
      {
        title: 'Research-Driven',
        description: 'Latest treatment protocols and research'
      },
      {
        title: 'Multidisciplinary Care',
        description: 'Coordinated team approach'
      }
    ],
    stats: {
      patients: '8,000+',
      procedures: '4,500+',
      successRate: '95%',
      experience: '18+ years'
    }
  },
  pharmacy: {
    id: 4,
    name: 'Pharmacy',
    icon: <Pill className="w-8 h-8" />,
    gradient: 'from-green-500 to-teal-500',
    description: 'Full-service pharmacy with expert guidance',
    fullDescription: 'Our in-house pharmacy offers a comprehensive range of medications and pharmaceutical services. Our expert pharmacists provide medication counseling and ensure safe, effective medication use.',
    image: 'https://images.unsplash.com/photo-1576602976047-174e57a47881?w=800',
    services: [
      'Prescription Dispensing',
      'Medication Counseling',
      'Drug Interaction Screening',
      'Compounding Services',
      'Immunizations',
      'Health Monitoring',
      'Medication Therapy Management',
      'Home Delivery'
    ],
    features: [
      {
        title: 'Expert Pharmacists',
        description: 'Licensed and experienced pharmacy team'
      },
      {
        title: 'Quality Assurance',
        description: 'Rigorous quality control measures'
      },
      {
        title: 'Insurance Support',
        description: 'Assistance with insurance claims'
      },
      {
        title: 'Digital Services',
        description: 'Online prescription refills'
      }
    ],
    stats: {
      patients: '30,000+',
      procedures: '100,000+',
      successRate: '99%',
      experience: '15+ years'
    }
  },
  emergency: {
    id: 5,
    name: 'Emergency Care',
    icon: <AlertCircle className="w-8 h-8" />,
    gradient: 'from-orange-500 to-red-500',
    description: '24/7 emergency medical services',
    fullDescription: 'Our Emergency Department provides immediate, life-saving care 24 hours a day, 7 days a week. Equipped with advanced trauma facilities and staffed by experienced emergency medicine specialists.',
    image: 'https://images.unsplash.com/photo-1516549655169-df83a0774514?w=800',
    services: [
      'Trauma Care',
      'Cardiac Emergencies',
      'Stroke Response',
      'Respiratory Emergencies',
      'Pediatric Emergencies',
      'Toxicology Services',
      'Emergency Surgery',
      'Critical Care'
    ],
    features: [
      {
        title: '24/7 Availability',
        description: 'Always open, always ready'
      },
      {
        title: 'Rapid Response',
        description: 'Fast triage and treatment'
      },
      {
        title: 'Advanced Equipment',
        description: 'Latest emergency medical technology'
      },
      {
        title: 'Expert Team',
        description: 'Board-certified emergency physicians'
      }
    ],
    stats: {
      patients: '50,000+',
      procedures: '20,000+',
      successRate: '97%',
      experience: '30+ years'
    }
  },
  pediatrics: {
    id: 6,
    name: 'Pediatrics',
    icon: <Baby className="w-8 h-8" />,
    gradient: 'from-pink-500 to-purple-500',
    description: 'Specialized care for children',
    fullDescription: 'Our Pediatrics department is dedicated to providing comprehensive healthcare for infants, children, and adolescents. We create a child-friendly environment while delivering expert medical care.',
    image: 'https://images.unsplash.com/photo-1631217868264-e5b90bb7e133?w=800',
    services: [
      'Well-Child Visits',
      'Vaccinations',
      'Growth Monitoring',
      'Developmental Assessments',
      'Pediatric Emergencies',
      'Chronic Disease Management',
      'Adolescent Medicine',
      'Newborn Care'
    ],
    features: [
      {
        title: 'Child-Friendly',
        description: 'Comfortable environment for kids'
      },
      {
        title: 'Family-Centered',
        description: 'Involving parents in care decisions'
      },
      {
        title: 'Preventive Focus',
        description: 'Emphasis on healthy development'
      },
      {
        title: 'Specialized Care',
        description: 'Pediatric subspecialties available'
      }
    ],
    stats: {
      patients: '15,000+',
      procedures: '8,000+',
      successRate: '99%',
      experience: '22+ years'
    }
  }
};

const ServiceDetail = () => {
  const { serviceId } = useParams();
  const navigate = useNavigate();
  const service = servicesData[serviceId];

  if (!service) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Service Not Found</h2>
          <button
            onClick={() => navigate('/')}
            className="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700"
          >
            Back to Home
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Hero Section */}
      <div className={`relative bg-gradient-to-r ${service.gradient} text-white py-20`}>
        <div className="max-w-7xl mx-auto px-4">
          <motion.button
            onClick={() => navigate('/')}
            className="flex items-center gap-2 text-white/90 hover:text-white mb-8"
            whileHover={{ x: -5 }}
          >
            <ArrowLeft className="w-5 h-5" />
            <span className="font-medium">Back to Home</span>
          </motion.button>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex items-center gap-6"
          >
            <div className="w-20 h-20 bg-white/20 backdrop-blur-sm rounded-2xl flex items-center justify-center">
              {service.icon}
            </div>
            <div>
              <h1 className="text-5xl font-bold mb-4">{service.name}</h1>
              <p className="text-xl text-white/90">{service.description}</p>
            </div>
          </motion.div>
        </div>
      </div>

      {/* Stats Section */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 py-12">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {Object.entries(service.stats).map(([key, value], index) => (
              <motion.div
                key={key}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
                className="text-center"
              >
                <p className="text-3xl font-bold text-green-600 mb-2">{value}</p>
                <p className="text-gray-600 capitalize">{key.replace(/([A-Z])/g, ' $1').trim()}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-12">
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-8">
            {/* About */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-white rounded-2xl shadow-lg overflow-hidden"
            >
              <img
                src={service.image}
                alt={service.name}
                className="w-full h-64 object-cover"
              />
              <div className="p-8">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">About Our Service</h2>
                <p className="text-gray-600 leading-relaxed">{service.fullDescription}</p>
              </div>
            </motion.div>

            {/* Services Offered */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="bg-white rounded-2xl shadow-lg p-8"
            >
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Services Offered</h2>
              <div className="grid md:grid-cols-2 gap-4">
                {service.services.map((item, index) => (
                  <motion.div
                    key={index}
                    className="flex items-start gap-3"
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.2 + index * 0.05 }}
                  >
                    <CheckCircle className="w-5 h-5 text-green-600 mt-0.5 flex-shrink-0" />
                    <span className="text-gray-700">{item}</span>
                  </motion.div>
                ))}
              </div>
            </motion.div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Features */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              className="bg-white rounded-2xl shadow-lg p-6 sticky top-24"
            >
              <h3 className="text-xl font-bold text-gray-900 mb-6">Key Features</h3>
              <div className="space-y-4">
                {service.features.map((feature, index) => (
                  <div key={index} className="border-l-4 border-green-600 pl-4">
                    <h4 className="font-semibold text-gray-900 mb-1">{feature.title}</h4>
                    <p className="text-sm text-gray-600">{feature.description}</p>
                  </div>
                ))}
              </div>

              <motion.button
                onClick={() => navigate('/book-appointment')}
                className="w-full mt-8 py-3 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 transition-colors"
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                Book Appointment
              </motion.button>
            </motion.div>

            {/* Contact Info */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className={`bg-gradient-to-br ${service.gradient} rounded-2xl shadow-lg p-6 text-white`}
            >
              <h3 className="text-xl font-bold mb-4">Need Help?</h3>
              <div className="space-y-3">
                <div className="flex items-center gap-3">
                  <Clock className="w-5 h-5" />
                  <span className="text-sm">24/7 Support Available</span>
                </div>
                <div className="flex items-center gap-3">
                  <Award className="w-5 h-5" />
                  <span className="text-sm">Board Certified Specialists</span>
                </div>
                <div className="flex items-center gap-3">
                  <Users className="w-5 h-5" />
                  <span className="text-sm">Patient-Centered Care</span>
                </div>
              </div>
              <button
                onClick={() => navigate('/login')}
                className="w-full mt-6 py-3 bg-white text-green-600 rounded-lg font-semibold hover:bg-gray-100 transition-colors"
              >
                Contact Us
              </button>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ServiceDetail;
