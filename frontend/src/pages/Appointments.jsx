import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Calendar, Clock, User, Plus, X, CheckCircle, AlertCircle, DollarSign, CreditCard } from 'lucide-react';
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
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.get(endpoints.getMyAppointments);
      console.log('Appointments response:', response.data);
      setAppointments(response.data);
    } catch (error) {
      console.error('Error fetching appointments:', error);
      console.error('Error details:', error.response?.data);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to load appointments. Please try again.';
      setError(`Failed to load appointments: ${errorMessage}`);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelAppointment = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this appointment?')) {
      return;
    }

    setCancelingId(id);
    try {
      await api.delete(endpoints.cancelAppointment(id));
      await fetchAppointments(); // Refresh the list
    } catch (error) {
      console.error('Error canceling appointment:', error);
      alert('Failed to cancel appointment. Please try again.');
    } finally {
      setCancelingId(null);
    }
  };

  const handlePayNow = async (appointment) => {
    try {
      // Prepare payment request with appointmentId and amount
      const paymentRequest = {
        appointmentId: appointment.appointmentId,
        amount: 50.00 // Default consultation fee - can be made dynamic
      };

      console.log('Creating payment for appointment:', paymentRequest);
      
      const response = await api.post('/payments/create', paymentRequest);
      
      console.log('Payment creation response:', response.data);
      
      if (response.data.success) {
        // Store payment info in sessionStorage for success/cancel callbacks
        sessionStorage.setItem('pendingPaymentId', response.data.paymentId);
        sessionStorage.setItem('pendingAppointmentId', appointment.appointmentId);
        
        // Redirect to PayPal
        window.location.href = response.data.approvalUrl;
      } else {
        alert(response.data.message || 'Failed to create payment');
      }
    } catch (error) {
      console.error('Error creating payment:', error);
      console.error('Error response:', error.response?.data);
      const errorMsg = error.response?.data?.message || 'Failed to initiate payment. Please try again.';
      alert(errorMsg);
    }
  };

  const getStatusColor = (status) => {
    switch (status.toUpperCase()) {
      case 'SCHEDULED':
        return 'bg-blue-100 text-blue-800';
      case 'CONFIRMED':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'COMPLETED':
        return 'bg-purple-100 text-purple-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status) => {
    switch (status.toUpperCase()) {
      case 'SCHEDULED':
        return <Clock className="w-4 h-4" />;
      case 'CONFIRMED':
        return <CheckCircle className="w-4 h-4" />;
      case 'PENDING':
        return <Clock className="w-4 h-4" />;
      case 'COMPLETED':
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

        {error && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700"
          >
            {error}
          </motion.div>
        )}

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
                key={appointment.appointmentId}
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
                              {appointment.doctorSpecialization}
                            </p>
                            {appointment.reason && (
                              <p className="text-sm text-gray-500 mt-1">
                                Reason: {appointment.reason}
                              </p>
                            )}
                          </div>
                          <Badge className={getStatusColor(appointment.status)}>
                            <span className="flex items-center gap-1">
                              {getStatusIcon(appointment.status)}
                              {appointment.status}
                            </span>
                          </Badge>
                        </div>

                        <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                          <div className="flex items-center gap-2">
                            <Calendar className="w-4 h-4 text-[#4CAF50]" />
                            <span>{new Date(appointment.appointmentDate).toLocaleDateString('en-US', { 
                              weekday: 'short', 
                              year: 'numeric', 
                              month: 'short', 
                              day: 'numeric' 
                            })}</span>
                          </div>
                          <div className="flex items-center gap-2">
                            <Clock className="w-4 h-4 text-[#4CAF50]" />
                            <span>{appointment.appointmentTime}</span>
                          </div>
                          {appointment.consultationFee && (
                            <div className="flex items-center gap-2">
                              <DollarSign className="w-4 h-4 text-[#4CAF50]" />
                              <span className="font-semibold">${appointment.consultationFee.toFixed(2)}</span>
                            </div>
                          )}
                        </div>
                      </div>

                      {/* Actions */}
                      {appointment.status !== 'COMPLETED' && appointment.status !== 'CANCELLED' && (
                        <div className="flex flex-col gap-2">
                          {appointment.consultationFee && !appointment.isPaid && appointment.status === 'SCHEDULED' && (
                            <Button
                              size="sm"
                              onClick={() => handlePayNow(appointment)}
                              className="bg-yellow-500 hover:bg-yellow-600 text-white"
                            >
                              <DollarSign className="w-4 h-4 mr-2" />
                              Pay Now (${appointment.consultationFee.toFixed(2)})
                            </Button>
                          )}
                          {appointment.isPaid && (
                            <Badge className="bg-green-100 text-green-800">
                              <CheckCircle className="w-4 h-4 mr-1" />
                              Paid
                            </Badge>
                          )}
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleCancelAppointment(appointment.appointmentId)}
                            disabled={cancelingId === appointment.appointmentId}
                            className="text-red-600 hover:bg-red-50 border-red-200"
                          >
                            {cancelingId === appointment.appointmentId ? (
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
