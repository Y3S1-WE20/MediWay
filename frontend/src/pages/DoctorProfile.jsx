import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { 
  ArrowLeft, 
  Calendar, 
  Award, 
  GraduationCap, 
  Clock, 
  MapPin, 
  Phone, 
  Mail,
  Star,
  CheckCircle
} from 'lucide-react';

// Mock doctor data
const doctorsData = {
  'sarah-johnson': {
    id: 1,
    name: 'Dr. Sarah Johnson',
    role: 'Chief Medical Officer',
    specialty: 'Cardiology',
    image: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400',
    rating: 4.9,
    reviews: 250,
    experience: '15+ years',
    education: [
      'MD, Harvard Medical School',
      'Fellowship in Cardiology, Johns Hopkins',
      'Board Certified in Internal Medicine'
    ],
    languages: ['English', 'Spanish', 'French'],
    specializations: [
      'Interventional Cardiology',
      'Heart Failure Management',
      'Cardiac Imaging',
      'Preventive Cardiology'
    ],
    about: 'Dr. Sarah Johnson is a highly experienced cardiologist with over 15 years of practice. She specializes in interventional cardiology and has performed over 5,000 successful procedures. Her patient-centered approach and commitment to excellence have earned her numerous accolades.',
    achievements: [
      'Top Cardiologist Award 2023',
      'Published 50+ research papers',
      'Speaker at International Cardiology Conference',
      'Director of Cardiac Care Unit'
    ],
    availability: {
      days: ['Monday', 'Tuesday', 'Wednesday', 'Friday'],
      hours: '9:00 AM - 5:00 PM'
    },
    contact: {
      phone: '+1 (555) 123-4567',
      email: 'dr.sarah@mediway.com',
      location: 'MediWay Medical Center, Floor 3, Room 301'
    }
  },
  'michael-chen': {
    id: 2,
    name: 'Dr. Michael Chen',
    role: 'Senior Anesthesiologist',
    specialty: 'Anesthesiology',
    image: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400',
    rating: 4.8,
    reviews: 180,
    experience: '12+ years',
    education: [
      'MD, Stanford University',
      'Residency in Anesthesiology, Mayo Clinic',
      'Board Certified in Anesthesiology'
    ],
    languages: ['English', 'Mandarin'],
    specializations: [
      'Cardiac Anesthesia',
      'Pediatric Anesthesia',
      'Pain Management',
      'Critical Care'
    ],
    about: 'Dr. Michael Chen is a dedicated anesthesiologist known for his expertise in complex surgical procedures. He has extensive experience in cardiac and pediatric anesthesia, ensuring patient comfort and safety throughout surgical interventions.',
    achievements: [
      'Excellence in Patient Care Award 2022',
      'Research in Advanced Pain Management',
      'Teaching Excellence Award',
      'Member, American Society of Anesthesiologists'
    ],
    availability: {
      days: ['Monday', 'Wednesday', 'Thursday', 'Friday'],
      hours: '8:00 AM - 6:00 PM'
    },
    contact: {
      phone: '+1 (555) 234-5678',
      email: 'dr.michael@mediway.com',
      location: 'MediWay Medical Center, Floor 2, Room 205'
    }
  },
  'emily-williams': {
    id: 3,
    name: 'Dr. Emily Williams',
    role: 'Lead Neurologist',
    specialty: 'Neurology',
    image: 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=400',
    rating: 4.9,
    reviews: 220,
    experience: '18+ years',
    education: [
      'MD, Yale School of Medicine',
      'Fellowship in Neurology, Cleveland Clinic',
      'Board Certified in Neurology'
    ],
    languages: ['English', 'German'],
    specializations: [
      'Stroke Management',
      'Epilepsy Treatment',
      'Neurodegenerative Disorders',
      'Movement Disorders'
    ],
    about: 'Dr. Emily Williams is a renowned neurologist with a focus on stroke management and neurodegenerative disorders. Her innovative treatment approaches and compassionate care have helped thousands of patients regain their quality of life.',
    achievements: [
      'Neurologist of the Year 2023',
      'Pioneer in Stroke Research',
      '100+ Publications in Medical Journals',
      'Director of Neurology Department'
    ],
    availability: {
      days: ['Tuesday', 'Wednesday', 'Thursday', 'Friday'],
      hours: '10:00 AM - 6:00 PM'
    },
    contact: {
      phone: '+1 (555) 345-6789',
      email: 'dr.emily@mediway.com',
      location: 'MediWay Medical Center, Floor 4, Room 401'
    }
  },
  'james-anderson': {
    id: 4,
    name: 'Dr. James Anderson',
    role: 'Head of Pediatrics',
    specialty: 'Pediatrics',
    image: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400',
    rating: 5.0,
    reviews: 300,
    experience: '20+ years',
    education: [
      'MD, Columbia University',
      'Residency in Pediatrics, Children\'s Hospital Boston',
      'Board Certified in Pediatrics'
    ],
    languages: ['English', 'Spanish', 'Portuguese'],
    specializations: [
      'Pediatric Emergency Care',
      'Childhood Development',
      'Pediatric Infectious Diseases',
      'Adolescent Medicine'
    ],
    about: 'Dr. James Anderson is a highly respected pediatrician with two decades of experience caring for children. His warm bedside manner and expertise in pediatric emergency care make him a trusted choice for families seeking comprehensive pediatric healthcare.',
    achievements: [
      'Pediatrician of the Decade Award',
      'Founded Community Child Health Program',
      'Author of "Modern Pediatric Care"',
      'International Pediatric Association Member'
    ],
    availability: {
      days: ['Monday', 'Tuesday', 'Thursday', 'Saturday'],
      hours: '8:00 AM - 4:00 PM'
    },
    contact: {
      phone: '+1 (555) 456-7890',
      email: 'dr.james@mediway.com',
      location: 'MediWay Medical Center, Floor 1, Room 105'
    }
  }
};

const DoctorProfile = () => {
  const { doctorId } = useParams();
  const navigate = useNavigate();
  const doctor = doctorsData[doctorId];

  if (!doctor) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Doctor Not Found</h2>
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
    <div className="min-h-screen bg-gray-50 py-12 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Back Button */}
        <motion.button
          onClick={() => navigate('/')}
          className="flex items-center gap-2 text-green-600 hover:text-green-700 mb-8"
          whileHover={{ x: -5 }}
        >
          <ArrowLeft className="w-5 h-5" />
          <span className="font-medium">Back to Home</span>
        </motion.button>

        <div className="grid lg:grid-cols-3 gap-8">
          {/* Left Column - Doctor Info */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="lg:col-span-1"
          >
            <div className="bg-white rounded-2xl shadow-lg overflow-hidden sticky top-24">
              <div className="relative h-64 bg-gradient-to-br from-green-400 to-emerald-600">
                <img
                  src={doctor.image}
                  alt={doctor.name}
                  className="w-full h-full object-cover mix-blend-overlay"
                />
              </div>
              
              <div className="p-6">
                <h1 className="text-2xl font-bold text-gray-900 mb-1">{doctor.name}</h1>
                <p className="text-green-600 font-medium mb-3">{doctor.role}</p>
                
                <div className="flex items-center gap-2 mb-6">
                  <div className="flex items-center gap-1">
                    {[...Array(5)].map((_, i) => (
                      <Star
                        key={i}
                        className={`w-4 h-4 ${
                          i < Math.floor(doctor.rating)
                            ? 'fill-yellow-400 text-yellow-400'
                            : 'text-gray-300'
                        }`}
                      />
                    ))}
                  </div>
                  <span className="text-sm text-gray-600">
                    {doctor.rating} ({doctor.reviews} reviews)
                  </span>
                </div>

                <div className="space-y-4 mb-6">
                  <div className="flex items-start gap-3">
                    <Award className="w-5 h-5 text-green-600 mt-0.5" />
                    <div>
                      <p className="text-sm font-medium text-gray-900">Experience</p>
                      <p className="text-sm text-gray-600">{doctor.experience}</p>
                    </div>
                  </div>
                  
                  <div className="flex items-start gap-3">
                    <MapPin className="w-5 h-5 text-green-600 mt-0.5" />
                    <div>
                      <p className="text-sm font-medium text-gray-900">Location</p>
                      <p className="text-sm text-gray-600">{doctor.contact.location}</p>
                    </div>
                  </div>
                  
                  <div className="flex items-start gap-3">
                    <Clock className="w-5 h-5 text-green-600 mt-0.5" />
                    <div>
                      <p className="text-sm font-medium text-gray-900">Availability</p>
                      <p className="text-sm text-gray-600">{doctor.availability.hours}</p>
                      <p className="text-xs text-gray-500 mt-1">
                        {doctor.availability.days.join(', ')}
                      </p>
                    </div>
                  </div>
                </div>

                <motion.button
                  onClick={() => navigate('/book-appointment')}
                  className="w-full py-3 bg-green-600 text-white rounded-lg font-semibold hover:bg-green-700 transition-colors"
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                >
                  <Calendar className="w-5 h-5 inline mr-2" />
                  Book Appointment
                </motion.button>
              </div>
            </div>
          </motion.div>

          {/* Right Column - Detailed Info */}
          <div className="lg:col-span-2 space-y-6">
            {/* About */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="bg-white rounded-2xl shadow-lg p-8"
            >
              <h2 className="text-2xl font-bold text-gray-900 mb-4">About</h2>
              <p className="text-gray-600 leading-relaxed">{doctor.about}</p>
            </motion.div>

            {/* Education */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              className="bg-white rounded-2xl shadow-lg p-8"
            >
              <div className="flex items-center gap-3 mb-6">
                <div className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
                  <GraduationCap className="w-6 h-6 text-green-600" />
                </div>
                <h2 className="text-2xl font-bold text-gray-900">Education</h2>
              </div>
              <ul className="space-y-3">
                {doctor.education.map((edu, index) => (
                  <li key={index} className="flex items-start gap-3">
                    <CheckCircle className="w-5 h-5 text-green-600 mt-0.5 flex-shrink-0" />
                    <span className="text-gray-700">{edu}</span>
                  </li>
                ))}
              </ul>
            </motion.div>

            {/* Specializations */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className="bg-white rounded-2xl shadow-lg p-8"
            >
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Specializations</h2>
              <div className="grid md:grid-cols-2 gap-4">
                {doctor.specializations.map((spec, index) => (
                  <motion.div
                    key={index}
                    className="flex items-center gap-3 p-4 bg-green-50 rounded-xl"
                    whileHover={{ scale: 1.05 }}
                  >
                    <div className="w-2 h-2 bg-green-600 rounded-full" />
                    <span className="text-gray-700 font-medium">{spec}</span>
                  </motion.div>
                ))}
              </div>
            </motion.div>

            {/* Achievements */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.4 }}
              className="bg-white rounded-2xl shadow-lg p-8"
            >
              <div className="flex items-center gap-3 mb-6">
                <div className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
                  <Award className="w-6 h-6 text-green-600" />
                </div>
                <h2 className="text-2xl font-bold text-gray-900">Achievements</h2>
              </div>
              <div className="grid gap-4">
                {doctor.achievements.map((achievement, index) => (
                  <motion.div
                    key={index}
                    className="flex items-start gap-3 p-4 border border-green-200 rounded-xl hover:bg-green-50 transition-colors"
                    whileHover={{ x: 5 }}
                  >
                    <Award className="w-5 h-5 text-green-600 mt-0.5 flex-shrink-0" />
                    <span className="text-gray-700">{achievement}</span>
                  </motion.div>
                ))}
              </div>
            </motion.div>

            {/* Contact */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 }}
              className="bg-gradient-to-br from-green-600 to-emerald-600 rounded-2xl shadow-lg p-8 text-white"
            >
              <h2 className="text-2xl font-bold mb-6">Contact Information</h2>
              <div className="space-y-4">
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                    <Phone className="w-6 h-6" />
                  </div>
                  <div>
                    <p className="text-sm opacity-90">Phone</p>
                    <p className="font-semibold">{doctor.contact.phone}</p>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                    <Mail className="w-6 h-6" />
                  </div>
                  <div>
                    <p className="text-sm opacity-90">Email</p>
                    <p className="font-semibold">{doctor.contact.email}</p>
                  </div>
                </div>
              </div>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DoctorProfile;
