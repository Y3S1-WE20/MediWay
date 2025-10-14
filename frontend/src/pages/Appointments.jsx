import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Calendar, Clock, User, Plus, X, CheckCircle, AlertCircle } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { useAuth } from '../context/AuthContext';
import api from '../api/api';
import { endpoints } from '../api/endpoints';

const Appointments = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cancelingId, setCancelingId] = useState(null);

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    setLoading(true);
    try {
      // Simulate API call - replace with actual API
      const mockAppointments = [
        {
          id: 1,
          doctorName: 'Dr. Sarah Johnson',
          specialization: 'Cardiologist',
          date: '2025-10-20',
          time: '10:00 AM',
          status: 'confirmed',
          hospital: 'City Hospital',
        },
        {
          id: 2,
          doctorName: 'Dr. Michael Chen',
          specialization: 'Dermatologist',
          date: '2025-10-18',
          time: '2:30 PM',
          status: 'pending',
          hospital: 'Medical Center',
        },
        {
          id: 3,
          doctorName: 'Dr. Emily Davis',
          specialization: 'General Physician',
          date: '2025-10-10',
          time: '9:00 AM',
          status: 'completed',
          hospital: 'Community Clinic',
        },
      ];

      setTimeout(() => {
        setAppointments(mockAppointments);
        setLoading(false);
      }, 800);

      // In production, use:
      // const response = await api.get(endpoints.getAppointments);
      // setAppointments(response.data);
    } catch (error) {
      console.error('Error fetching appointments:', error);
      setLoading(false);
    }
  };

  const handleCancelAppointment = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) {
      return;
    }

    setCancelingId(id);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // In production, use:
      // await api.delete(endpoints.cancelAppointment(id));

      setAppointments(prev => prev.filter(apt => apt.id !== id));
    } catch (error) {
      alert('Failed to cancel appointment. Please try again.');
    } finally {
      setCancelingId(null);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'confirmed':
        return 'success';
      case 'pending':
        return 'warning';
      case 'completed':
        return 'info';
      case 'cancelled':
        return 'danger';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'confirmed':
        return <CheckCircle className="w-4 h-4" />;
      case 'pending':
        return <Clock className="w-4 h-4" />;
      case 'completed':
        return <CheckCircle className="w-4 h-4" />;
      default:
        return <AlertCircle className="w-4 h-4" />;
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center pt-16">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
          className="w-16 h-16 border-4 border-[#4CAF50] border-t-transparent rounded-full"
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 pt-24 pb-12 px-4">
      <div className="container mx-auto max-w-6xl">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <h1 className="text-4xl font-bold text-gray-800 mb-2">
                My Appointments
              </h1>
              <p className="text-gray-600">
                Manage your upcoming and past appointments
              </p>
            </div>
            <Button
              onClick={() => navigate('/book-appointment')}
              className="bg-[#4CAF50] hover:bg-[#45a049] text-white"
            >
              <Plus className="w-5 h-5 mr-2" />
              Book New Appointment
            </Button>
          </div>
        </motion.div>

        {/* Appointments List */}
        {appointments.length === 0 ? (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-center py-20"
          >
            <Calendar className="w-24 h-24 mx-auto text-gray-300 mb-4" />
            <h3 className="text-2xl font-semibold text-gray-700 mb-2">
              No Appointments Yet
            </h3>
            <p className="text-gray-500 mb-6">
              Book your first appointment to get started
            </p>
            <Button
              onClick={() => navigate('/book-appointment')}
              className="bg-[#4CAF50] hover:bg-[#45a049] text-white"
            >
              Book Appointment
            </Button>
          </motion.div>
        ) : (
          <div className="grid gap-6">
            {appointments.map((appointment, index) => (
              <motion.div
                key={appointment.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
              >
                <Card hover className="overflow-hidden">
                  <CardContent className="p-6">
                    <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                      {/* Appointment Details */}
                      <div className="flex-1 space-y-3">
                        <div className="flex items-start justify-between">
                          <div>
                            <h3 className="text-xl font-semibold text-gray-800 mb-1">
                              {appointment.doctorName}
                            </h3>
                            <p className="text-sm text-gray-600">
                              {appointment.specialization} • {appointment.hospital}
                            </p>
                          </div>
                          <Badge variant={getStatusColor(appointment.status)} className="flex items-center gap-1">
                            {getStatusIcon(appointment.status)}
                            {appointment.status.charAt(0).toUpperCase() + appointment.status.slice(1)}
                          </Badge>
                        </div>

                        <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                          <div className="flex items-center gap-2">
                            <Calendar className="w-4 h-4 text-[#4CAF50]" />
                            <span>{new Date(appointment.date).toLocaleDateString('en-US', { 
                              weekday: 'short', 
                              year: 'numeric', 
                              month: 'short', 
                              day: 'numeric' 
                            })}</span>
                          </div>
                          <div className="flex items-center gap-2">
                            <Clock className="w-4 h-4 text-[#4CAF50]" />
                            <span>{appointment.time}</span>
                          </div>
                        </div>
                      </div>

                      {/* Actions */}
                      {appointment.status !== 'completed' && appointment.status !== 'cancelled' && (
                        <div className="flex gap-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => navigate(`/appointments/${appointment.id}/reschedule`)}
                          >
                            Reschedule
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleCancelAppointment(appointment.id)}
                            disabled={cancelingId === appointment.id}
                            className="text-red-600 hover:bg-red-50"
                          >
                            {cancelingId === appointment.id ? (
                              <motion.div
                                animate={{ rotate: 360 }}
                                transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                                className="w-4 h-4 border-2 border-red-600 border-t-transparent rounded-full"
                              />
                            ) : (
                              <>
                                <X className="w-4 h-4 mr-1" />
                                Cancel
                              </>
                            )}
                          </Button>
                        </div>
                      )}
                    </div>
                  </CardContent>
                </Card>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Appointments;
