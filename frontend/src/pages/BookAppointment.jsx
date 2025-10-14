import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Calendar, User, Clock, CheckCircle, ArrowLeft } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Select } from '../components/ui/select';
import { useAuth } from '../context/AuthContext';
import api from '../api/api';
import { endpoints } from '../api/endpoints';

const BookAppointment = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [step, setStep] = useState(1);
  const [specializations, setSpecializations] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [availableSlots, setAvailableSlots] = useState([]);
  
  const [formData, setFormData] = useState({
    specialization: '',
    doctorId: '',
    date: '',
    timeSlot: '',
    symptoms: '',
  });

  const mockSpecializations = [
    'Cardiologist',
    'Dermatologist',
    'General Physician',
    'Orthopedic',
    'Pediatrician',
    'Neurologist',
  ];

  const mockDoctors = {
    'Cardiologist': [
      { id: 1, name: 'Dr. Sarah Johnson', experience: '15 years', hospital: 'City Hospital' },
      { id: 2, name: 'Dr. Robert Smith', experience: '10 years', hospital: 'Medical Center' },
    ],
    'Dermatologist': [
      { id: 3, name: 'Dr. Michael Chen', experience: '12 years', hospital: 'Skin Care Clinic' },
      { id: 4, name: 'Dr. Lisa Anderson', experience: '8 years', hospital: 'Medical Center' },
    ],
    'General Physician': [
      { id: 5, name: 'Dr. Emily Davis', experience: '20 years', hospital: 'Community Clinic' },
      { id: 6, name: 'Dr. James Wilson', experience: '14 years', hospital: 'City Hospital' },
    ],
  };

  const mockTimeSlots = [
    '09:00 AM', '10:00 AM', '11:00 AM', '12:00 PM',
    '02:00 PM', '03:00 PM', '04:00 PM', '05:00 PM',
  ];

  useEffect(() => {
    setSpecializations(mockSpecializations);
  }, []);

  useEffect(() => {
    if (formData.specialization) {
      setDoctors(mockDoctors[formData.specialization] || []);
      setFormData(prev => ({ ...prev, doctorId: '', date: '', timeSlot: '' }));
    }
  }, [formData.specialization]);

  useEffect(() => {
    if (formData.date && formData.doctorId) {
      setAvailableSlots(mockTimeSlots);
    }
  }, [formData.date, formData.doctorId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // In production, use:
      // await api.post(endpoints.createAppointment, formData);

      setStep(3); // Success step
      setTimeout(() => {
        navigate('/appointments');
      }, 2000);
    } catch (error) {
      alert('Failed to book appointment. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getMinDate = () => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  };

  const selectedDoctor = doctors.find(d => d.id === parseInt(formData.doctorId));

  if (step === 3) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4 pt-16">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="text-center"
        >
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
            className="mx-auto w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mb-6"
          >
            <CheckCircle className="w-16 h-16 text-[#4CAF50]" />
          </motion.div>
          <h2 className="text-3xl font-bold text-gray-800 mb-2">
            Appointment Booked!
          </h2>
          <p className="text-gray-600 mb-4">
            Your appointment has been confirmed successfully
          </p>
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
            className="w-8 h-8 border-2 border-[#4CAF50] border-t-transparent rounded-full mx-auto"
          />
          <p className="text-sm text-gray-500 mt-4">
            Redirecting to appointments...
          </p>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 pt-24 pb-12 px-4">
      <div className="container mx-auto max-w-3xl">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <Button
            variant="ghost"
            onClick={() => navigate('/appointments')}
            className="mb-4"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Appointments
          </Button>
          <h1 className="text-4xl font-bold text-gray-800 mb-2">
            Book Appointment
          </h1>
          <p className="text-gray-600">
            Choose your preferred doctor and time slot
          </p>
        </motion.div>

        {/* Progress Steps */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="mb-8"
        >
          <div className="flex items-center justify-center gap-4">
            {[1, 2].map((s) => (
              <React.Fragment key={s}>
                <div className="flex items-center">
                  <div
                    className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold transition-all ${
                      step >= s
                        ? 'bg-[#4CAF50] text-white'
                        : 'bg-gray-200 text-gray-500'
                    }`}
                  >
                    {s}
                  </div>
                  <span className="ml-2 text-sm font-medium text-gray-700">
                    {s === 1 ? 'Select Doctor' : 'Choose Time'}
                  </span>
                </div>
                {s < 2 && (
                  <div
                    className={`h-1 w-16 transition-all ${
                      step > s ? 'bg-[#4CAF50]' : 'bg-gray-200'
                    }`}
                  />
                )}
              </React.Fragment>
            ))}
          </div>
        </motion.div>

        {/* Form Card */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <Card>
            <CardHeader>
              <CardTitle>
                {step === 1 ? 'Select Doctor' : 'Choose Date & Time'}
              </CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-6">
                {step === 1 && (
                  <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="space-y-4"
                  >
                    <Select
                      label="Specialization"
                      name="specialization"
                      value={formData.specialization}
                      onChange={handleChange}
                      required
                    >
                      <option value="">Select Specialization</option>
                      {specializations.map((spec) => (
                        <option key={spec} value={spec}>
                          {spec}
                        </option>
                      ))}
                    </Select>

                    {formData.specialization && (
                      <motion.div
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                      >
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Select Doctor
                        </label>
                        <div className="space-y-3">
                          {doctors.map((doctor) => (
                            <motion.div
                              key={doctor.id}
                              whileHover={{ scale: 1.02 }}
                              className={`p-4 border-2 rounded-lg cursor-pointer transition-all ${
                                parseInt(formData.doctorId) === doctor.id
                                  ? 'border-[#4CAF50] bg-green-50'
                                  : 'border-gray-200 hover:border-gray-300'
                              }`}
                              onClick={() =>
                                setFormData(prev => ({ ...prev, doctorId: doctor.id.toString() }))
                              }
                            >
                              <div className="flex items-center justify-between">
                                <div>
                                  <h4 className="font-semibold text-gray-800">
                                    {doctor.name}
                                  </h4>
                                  <p className="text-sm text-gray-600">
                                    {doctor.experience} • {doctor.hospital}
                                  </p>
                                </div>
                                <div
                                  className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${
                                    parseInt(formData.doctorId) === doctor.id
                                      ? 'border-[#4CAF50] bg-[#4CAF50]'
                                      : 'border-gray-300'
                                  }`}
                                >
                                  {parseInt(formData.doctorId) === doctor.id && (
                                    <CheckCircle className="w-4 h-4 text-white" />
                                  )}
                                </div>
                              </div>
                            </motion.div>
                          ))}
                        </div>
                      </motion.div>
                    )}

                    <div className="flex justify-end pt-4">
                      <Button
                        type="button"
                        onClick={() => setStep(2)}
                        disabled={!formData.doctorId}
                        className="bg-[#4CAF50] hover:bg-[#45a049]"
                      >
                        Next
                      </Button>
                    </div>
                  </motion.div>
                )}

                {step === 2 && (
                  <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="space-y-4"
                  >
                    {selectedDoctor && (
                      <div className="bg-green-50 p-4 rounded-lg border border-green-200">
                        <p className="text-sm text-gray-600">Selected Doctor</p>
                        <p className="font-semibold text-gray-800">
                          {selectedDoctor.name} - {formData.specialization}
                        </p>
                      </div>
                    )}

                    <Input
                      label="Preferred Date"
                      name="date"
                      type="date"
                      value={formData.date}
                      onChange={handleChange}
                      min={getMinDate()}
                      required
                    />

                    {availableSlots.length > 0 && (
                      <motion.div
                        initial={{ opacity: 0, y: 10 }}
                        animate={{ opacity: 1, y: 0 }}
                      >
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Available Time Slots
                        </label>
                        <div className="grid grid-cols-4 gap-3">
                          {availableSlots.map((slot) => (
                            <motion.button
                              key={slot}
                              type="button"
                              whileHover={{ scale: 1.05 }}
                              whileTap={{ scale: 0.95 }}
                              onClick={() =>
                                setFormData(prev => ({ ...prev, timeSlot: slot }))
                              }
                              className={`p-3 rounded-lg border-2 text-sm font-medium transition-all ${
                                formData.timeSlot === slot
                                  ? 'border-[#4CAF50] bg-green-50 text-[#4CAF50]'
                                  : 'border-gray-200 hover:border-gray-300'
                              }`}
                            >
                              {slot}
                            </motion.button>
                          ))}
                        </div>
                      </motion.div>
                    )}

                    <Input
                      label="Symptoms / Reason for Visit (Optional)"
                      name="symptoms"
                      value={formData.symptoms}
                      onChange={handleChange}
                      placeholder="Describe your symptoms..."
                      containerClassName="mb-0"
                    />

                    <div className="flex gap-3 pt-4">
                      <Button
                        type="button"
                        variant="outline"
                        onClick={() => setStep(1)}
                      >
                        Back
                      </Button>
                      <Button
                        type="submit"
                        disabled={!formData.timeSlot || loading}
                        className="flex-1 bg-[#4CAF50] hover:bg-[#45a049]"
                      >
                        {loading ? (
                          <motion.div
                            animate={{ rotate: 360 }}
                            transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                            className="w-5 h-5 border-2 border-white border-t-transparent rounded-full mx-auto"
                          />
                        ) : (
                          'Confirm Booking'
                        )}
                      </Button>
                    </div>
                  </motion.div>
                )}
              </form>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};

export default BookAppointment;
