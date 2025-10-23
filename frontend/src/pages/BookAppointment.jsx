import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Calendar, Clock, CheckCircle, ArrowLeft, DollarSign, User } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Select } from '../components/ui/select';
import { useAuth } from '../hooks/useAuth';
import api from '../api/api';
import { endpoints } from '../api/endpoints';

const BookAppointment = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [loading, setLoading] = useState(false);
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState(null);
  
  const [formData, setFormData] = useState({
    doctorId: '',
    appointmentDate: '',
    appointmentTime: '',
    reason: '',
  });

  useEffect(() => {
    fetchDoctors();
  }, []);

  useEffect(() => {
    // Redirect to login if user is not authenticated
    if (!isAuthenticated) {
      navigate('/login');
    }
  }, [isAuthenticated, navigate]);

  const fetchDoctors = async () => {
    try {
      const response = await api.get(endpoints.getDoctors);
      setDoctors(response.data);
    } catch (error) {
      console.error('Error fetching doctors:', error);
      setError('Failed to load doctors. Please refresh the page.');
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    if (name === 'doctorId' && value) {
      const doctor = doctors.find(d => d.doctorId === value);
      setSelectedDoctor(doctor);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const requestData = {
        doctorId: formData.doctorId,
        appointmentDate: formData.appointmentDate,
        appointmentTime: formData.appointmentTime,
        reason: formData.reason,
        consultationFee: selectedDoctor?.consultationFee
      };

      const resp = await api.post(endpoints.createAppointment, requestData);
      console.debug('Create appointment response:', resp.status, resp.data);
      
      setSuccess(true);
      setTimeout(() => {
        navigate('/appointments');
      }, 2000);
    } catch (error) {
      console.error('Error booking appointment:', error);
      // show detailed message when available
      const serverMessage = error.response?.data?.message || error.message;
      setError(serverMessage || 'Failed to book appointment. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // Specialization groups for better UI
  const groupedDoctors = doctors.reduce((acc, doctor) => {
    if (!acc[doctor.specialization]) {
      acc[doctor.specialization] = [];
    }
    acc[doctor.specialization].push(doctor);
    return acc;
  }, {});

  if (success) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center pt-16">
        <motion.div
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          className="text-center"
        >
          <div className="w-24 h-24 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <CheckCircle className="w-16 h-16 text-green-600" />
          </div>
          <h2 className="text-3xl font-bold text-gray-800 mb-2">Appointment Booked!</h2>
          <p className="text-gray-600">Redirecting to appointments...</p>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 pt-24 pb-12 px-4">
      <div className="container mx-auto max-w-4xl">
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
          <Button
            onClick={() => navigate('/appointments')}
            variant="ghost"
            className="mb-6"
          >
            <ArrowLeft className="w-5 h-5 mr-2" />
            Back to Appointments
          </Button>

          <h1 className="text-4xl font-bold text-gray-800 mb-2">Book an Appointment</h1>
          <p className="text-gray-600 mb-8">Schedule your consultation with our expert doctors</p>

          {error && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700"
            >
              {error}
            </motion.div>
          )}

          <Card>
            <CardHeader>
              <CardTitle>Appointment Details</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Doctor Selection */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    <User className="w-4 h-4 inline mr-2" />
                    Select Doctor
                  </label>
                  <select
                    name="doctorId"
                    value={formData.doctorId}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#4CAF50] focus:border-transparent"
                  >
                    <option value="">Choose a doctor...</option>
                    {Object.entries(groupedDoctors).map(([specialization, specDoctors]) => (
                      <optgroup key={specialization} label={specialization}>
                        {specDoctors.map(doctor => (
                          <option key={doctor.doctorId} value={doctor.doctorId}>
                            {doctor.name} - ${doctor.consultationFee?.toFixed(2)} ({doctor.experienceYears} years exp)
                          </option>
                        ))}
                      </optgroup>
                    ))}
                  </select>
                </div>

                {/* Selected Doctor Info */}
                {selectedDoctor && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    className="p-4 bg-blue-50 rounded-lg"
                  >
                    <h3 className="font-semibold text-gray-800 mb-2">{selectedDoctor.name}</h3>
                    <p className="text-sm text-gray-600">Specialization: {selectedDoctor.specialization}</p>
                    <p className="text-sm text-gray-600">Qualification: {selectedDoctor.qualification}</p>
                    <p className="text-sm text-gray-600">Experience: {selectedDoctor.experienceYears} years</p>
                    <p className="text-sm font-semibold text-green-600 mt-2">
                      <DollarSign className="w-4 h-4 inline" />
                      Consultation Fee: ${selectedDoctor.consultationFee?.toFixed(2)}
                    </p>
                  </motion.div>
                )}

                {/* Date Selection */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    <Calendar className="w-4 h-4 inline mr-2" />
                    Appointment Date
                  </label>
                  <Input
                    type="date"
                    name="appointmentDate"
                    value={formData.appointmentDate}
                    onChange={handleChange}
                    min={new Date().toISOString().split('T')[0]}
                    required
                  />
                </div>

                {/* Time Selection */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    <Clock className="w-4 h-4 inline mr-2" />
                    Appointment Time
                  </label>
                  <Input
                    type="time"
                    name="appointmentTime"
                    value={formData.appointmentTime}
                    onChange={handleChange}
                    required
                  />
                </div>

                {/* Reason */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Reason for Visit
                  </label>
                  <textarea
                    name="reason"
                    value={formData.reason}
                    onChange={handleChange}
                    rows="4"
                    placeholder="Describe your symptoms or reason for consultation..."
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#4CAF50] focus:border-transparent"
                  />
                </div>

                <Button
                  type="submit"
                  disabled={loading || !formData.doctorId || !formData.appointmentDate || !formData.appointmentTime}
                  className="w-full bg-[#4CAF50] hover:bg-[#45a049] text-white py-3"
                >
                  {loading ? 'Booking...' : 'Book Appointment'}
                </Button>
              </form>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};

export default BookAppointment;
