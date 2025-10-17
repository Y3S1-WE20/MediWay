import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { CheckCircle, CreditCard, DollarSign, ArrowLeft } from 'lucide-react';
import { Card, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import api from '../api/api';

const PayPalCheckout = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [paymentDetails, setPaymentDetails] = useState(null);

  useEffect(() => {
    const loadPaymentDetails = () => {
      try {
        const paymentId = searchParams.get('paymentId') || sessionStorage.getItem('pendingPaymentId');
        const appointmentId = searchParams.get('appointmentId') || sessionStorage.getItem('pendingAppointmentId');
        const amount = searchParams.get('amount') || '50.00';

        if (!paymentId) {
          alert('Payment ID not found');
          navigate('/appointments');
          return;
        }

        setPaymentDetails({
          paymentId,
          appointmentId,
          amount: parseFloat(amount)
        });
        setLoading(false);
      } catch (error) {
        console.error('Error loading payment:', error);
        alert('Failed to load payment details');
        navigate('/appointments');
      }
    };

    loadPaymentDetails();
  }, [searchParams, navigate]);

  const handlePayNow = async () => {
    setLoading(true);
    try {
      // Simulate PayPal payment processing
      await new Promise(resolve => setTimeout(resolve, 2000)); // 2 second delay for realism

      // Execute payment
      const response = await api.post('/payments/execute', {
        paymentId: paymentDetails.paymentId,
        transactionId: 'PAYPAL-MOCK-' + Date.now()
      });

      if (response.data.success) {
        // Redirect to success page
        navigate(`/payment-success?paymentId=${paymentDetails.paymentId}&appointmentId=${paymentDetails.appointmentId}`);
      } else {
        alert(response.data.message || 'Payment failed');
        setLoading(false);
      }
    } catch (error) {
      console.error('Payment error:', error);
      alert('Payment processing failed. Please try again.');
      setLoading(false);
    }
  };

  const handleCancel = async () => {
    try {
      await api.post('/payments/cancel', {
        paymentId: paymentDetails.paymentId
      });
      navigate(`/payment-cancel?paymentId=${paymentDetails.paymentId}`);
    } catch (error) {
      console.error('Cancel error:', error);
      navigate('/appointments');
    }
  };

  if (loading || !paymentDetails) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
          className="w-16 h-16 border-4 border-[#4CAF50] border-t-transparent rounded-full"
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-md"
      >
        <Card className="shadow-2xl border-2 border-blue-100">
          <CardContent className="p-8">
            {/* PayPal Logo Simulation */}
            <div className="text-center mb-6">
              <div className="inline-flex items-center gap-2 bg-blue-600 text-white px-6 py-3 rounded-lg text-2xl font-bold">
                <CreditCard className="w-8 h-8" />
                <span>PayPal</span>
              </div>
              <p className="text-sm text-gray-500 mt-2">
                Simulated Payment Gateway (Prototype)
              </p>
            </div>

            {/* Payment Details */}
            <div className="bg-gray-50 rounded-lg p-6 mb-6">
              <h3 className="text-lg font-semibold text-gray-800 mb-4">
                Payment Details
              </h3>
              <div className="space-y-3">
                <div className="flex justify-between">
                  <span className="text-gray-600">Payment ID:</span>
                  <span className="font-mono text-sm">#{paymentDetails.paymentId}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Appointment ID:</span>
                  <span className="font-mono text-sm">#{paymentDetails.appointmentId}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Description:</span>
                  <span className="text-sm">Medical Consultation</span>
                </div>
                <div className="border-t pt-3 mt-3 flex justify-between items-center">
                  <span className="text-lg font-semibold text-gray-800">Total Amount:</span>
                  <span className="text-2xl font-bold text-blue-600">
                    ${paymentDetails.amount.toFixed(2)}
                  </span>
                </div>
              </div>
            </div>

            {/* Payment Method (Simulated) */}
            <div className="bg-blue-50 border-2 border-blue-200 rounded-lg p-4 mb-6">
              <div className="flex items-center gap-3">
                <CheckCircle className="w-6 h-6 text-blue-600" />
                <div>
                  <p className="font-semibold text-gray-800">PayPal Balance</p>
                  <p className="text-sm text-gray-600">Simulated Payment Method</p>
                </div>
              </div>
            </div>

            {/* Security Notice */}
            <div className="bg-green-50 border border-green-200 rounded-lg p-4 mb-6">
              <p className="text-xs text-green-800 text-center">
                🔒 This is a simulated payment for prototype demonstration.
                No actual payment will be processed.
              </p>
            </div>

            {/* Action Buttons */}
            <div className="space-y-3">
              <Button
                onClick={handlePayNow}
                disabled={loading}
                className="w-full bg-blue-600 hover:bg-blue-700 text-white py-6 text-lg font-semibold"
              >
                {loading ? (
                  <span className="flex items-center justify-center gap-2">
                    <motion.div
                      animate={{ rotate: 360 }}
                      transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                      className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
                    />
                    Processing Payment...
                  </span>
                ) : (
                  <span className="flex items-center justify-center gap-2">
                    <DollarSign className="w-6 h-6" />
                    Pay ${paymentDetails.amount.toFixed(2)}
                  </span>
                )}
              </Button>

              <Button
                onClick={handleCancel}
                disabled={loading}
                variant="outline"
                className="w-full border-2 border-gray-300 hover:bg-gray-100 py-4"
              >
                <ArrowLeft className="w-4 h-4 mr-2" />
                Cancel Payment
              </Button>
            </div>

            {/* Terms */}
            <p className="text-xs text-gray-500 text-center mt-6">
              By completing this payment, you agree to MediWay's terms and conditions.
            </p>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  );
};

export default PayPalCheckout;
