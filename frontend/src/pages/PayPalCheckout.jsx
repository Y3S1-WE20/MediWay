import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { CheckCircle, CreditCard, DollarSign, ArrowLeft, Loader } from 'lucide-react';
import { PayPalScriptProvider, PayPalButtons } from '@paypal/react-paypal-js';
import { Card, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import api from '../api/api';
import { useAuth } from '../context/AuthContext';

// PayPal configuration
const PAYPAL_CLIENT_ID = "AQI0v2iVOX7LKr4xDLL2eH6pKKrj-G2aXGQFByWp4w3B9m73fqL6_mfzAdP5Ii1ujVhQK3rlJkNERmAc";

const initialOptions = {
  "client-id": PAYPAL_CLIENT_ID,
  currency: "USD",
  intent: "capture",
  "data-page-type": "product-details",
  components: "buttons",
  "disable-funding": "credit,card"
};

const PayPalCheckout = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { token, user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [appointmentDetails, setAppointmentDetails] = useState(null);
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState(null);
  const [paymentComplete, setPaymentComplete] = useState(false);
  const [retryCount, setRetryCount] = useState(0);

  // Set auth header
  const setAuthHeader = () => {
    if (token) {
      api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      return true;
    }
    return false;
  };

  useEffect(() => {
    const loadAppointmentDetails = async () => {
      try {
        const appointmentId = searchParams.get('appointmentId');
        const amount = searchParams.get('amount') || '50.00';

        if (!appointmentId) {
          setError('Appointment ID not found');
          setTimeout(() => navigate('/appointments'), 3000);
          return;
        }

        if (!setAuthHeader()) {
          setError('You must be logged in to make a payment');
          setTimeout(() => navigate('/login'), 3000);
          return;
        }

        setAppointmentDetails({
          appointmentId,
          amount: parseFloat(amount)
        });
        setLoading(false);
      } catch (error) {
        console.error('Error loading appointment details:', error);
        setError('Failed to load appointment details');
        setTimeout(() => navigate('/appointments'), 3000);
      }
    };

    loadAppointmentDetails();
  }, [searchParams, navigate, token]);

  // PayPal payment handlers
  const createOrder = (data, actions) => {
    console.log('Creating PayPal order for appointment:', appointmentDetails.appointmentId);
    
    return actions.order.create({
      purchase_units: [{
        amount: {
          value: appointmentDetails.amount.toString(),
          currency_code: "USD"
        },
        description: `MediWay Appointment Payment - ID: ${appointmentDetails.appointmentId}`
      }],
      application_context: {
        shipping_preference: "NO_SHIPPING",
        user_action: "PAY_NOW",
        return_url: `${window.location.origin}/paypal-checkout?success=true&appointmentId=${appointmentDetails.appointmentId}`,
        cancel_url: `${window.location.origin}/paypal-checkout?cancelled=true&appointmentId=${appointmentDetails.appointmentId}`
      }
    });
  };

  const onApprove = async (data, actions) => {
    console.log('PayPal onApprove triggered with data:', data);
    
    if (processing) {
      console.log('Already processing, ignoring duplicate onApprove call');
      return;
    }
    
    try {
      setProcessing(true);
      setError(null); // Clear any previous errors
      
      console.log('Payment approved, order ID:', data.orderID);
      
      // Instead of capturing through actions.order.capture() which requires the popup window,
      // we'll use the orderID to capture on the backend or handle it differently
      
      // Send payment completion to backend with the order ID
      const payload = {
        appointmentId: appointmentDetails.appointmentId,
        paypalOrderId: data.orderID,
        payerId: data.payerID,
        amount: appointmentDetails.amount,
        userId: user?.id || 1,
        facilitated: data.facilitatorAccessToken ? true : false
      };
      console.log('Sending completion to backend with payload:', payload);
      const response = await api.post('/paypal/complete', payload);
      console.log('Backend response:', response.data);

      if (response.data.success) {
        console.log('Payment completed successfully!');
        setPaymentComplete(true);
        
        // Show success for a bit longer before redirecting
        setTimeout(() => {
          navigate('/appointments?payment=success');
        }, 2000);
      } else {
        throw new Error(response.data.message || 'Payment processing failed');
      }
    } catch (error) {
      console.error('Payment approval error:', error);
      setError('Payment processing failed: ' + (error.message || 'Unknown error'));
      setProcessing(false);
    }
  };

  const retryPayment = () => {
    setError(null);
    setProcessing(false);
    setRetryCount(prev => prev + 1);
  };

  const onError = (err) => {
    console.error('PayPal error:', err);
    setProcessing(false);
    
    // Handle different types of errors
    if (err.message && err.message.includes('Window closed')) {
      if (retryCount < 2) {
        setError('Payment window was closed. Please try again and keep the PayPal window open until payment completes.');
      } else {
        setError('Payment window keeps closing. Please disable popup blockers and try again.');
      }
    } else if (err.message && err.message.includes('popup')) {
      setError('Payment popup was blocked. Please allow popups for this site and try again.');
    } else {
      setError(`PayPal payment failed: ${err.message || 'Unknown error'}. Please try again.`);
    }
  };

  const onCancel = (data) => {
    console.log('PayPal payment cancelled:', data);
    setProcessing(false);
    setError('Payment was cancelled. You can try again or return to appointments.');
  };

  const handleCancel = () => {
    navigate('/appointments');
  };

  if (loading || !appointmentDetails) {
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

  if (paymentComplete) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 flex items-center justify-center p-4">
        <motion.div
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          className="text-center"
        >
          <CheckCircle className="w-24 h-24 text-green-500 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-gray-800 mb-2">Payment Successful!</h1>
          <p className="text-gray-600 mb-4">Your appointment has been confirmed.</p>
          <p className="text-sm text-gray-500">Redirecting to appointments...</p>
        </motion.div>
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
            {/* PayPal Logo */}
            <div className="text-center mb-6">
              <div className="inline-flex items-center gap-2 bg-blue-600 text-white px-6 py-3 rounded-lg text-2xl font-bold">
                <CreditCard className="w-8 h-8" />
                <span>PayPal</span>
              </div>
              <p className="text-sm text-gray-500 mt-2">
                Secure Payment Gateway
              </p>
            </div>

            {/* Payment Details */}
            <div className="bg-gray-50 rounded-lg p-6 mb-6">
              <h3 className="text-lg font-semibold text-gray-800 mb-4">
                Payment Details
              </h3>
              <div className="space-y-3">
                <div className="flex justify-between">
                  <span className="text-gray-600">Appointment ID:</span>
                  <span className="font-mono text-sm">#{appointmentDetails.appointmentId}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Description:</span>
                  <span className="text-sm">Medical Consultation</span>
                </div>
                <div className="border-t pt-3 mt-3 flex justify-between items-center">
                  <span className="text-lg font-semibold text-gray-800">Total Amount:</span>
                  <span className="text-2xl font-bold text-blue-600">
                    ${appointmentDetails.amount.toFixed(2)}
                  </span>
                </div>
              </div>
            </div>

            {/* Error Display */}
            {error && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
                <p className="text-sm text-red-800 text-center mb-3">{error}</p>
                <div className="flex justify-center">
                  <Button
                    onClick={retryPayment}
                    className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 text-sm"
                  >
                    Try Again
                  </Button>
                </div>
              </div>
            )}

            {/* Security Notice */}
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
              <p className="text-xs text-blue-800 text-center">
                🔒 Secure payment powered by PayPal Sandbox (Development Mode)
              </p>
              <p className="text-xs text-gray-600 text-center mt-2">
                Use PayPal sandbox test account to complete payment
              </p>
              <div className="mt-3 text-xs text-gray-600 text-center">
                <p><strong>Important:</strong> Keep the PayPal window open until payment completes</p>
                <p>If popup is blocked, please allow popups for this site</p>
              </div>
            </div>

            {/* PayPal Buttons */}
            {processing ? (
              <div className="flex items-center justify-center py-8">
                <Loader className="w-8 h-8 text-blue-600 animate-spin" />
                <span className="ml-3 text-gray-600">Processing payment...</span>
              </div>
            ) : (
              <PayPalScriptProvider options={initialOptions}>
                <PayPalButtons
                  key={`paypal-buttons-${retryCount}`}
                  style={{
                    layout: "vertical",
                    shape: "rect",
                    color: "gold",
                    label: "paypal",
                    height: 45
                  }}
                  createOrder={createOrder}
                  onApprove={onApprove}
                  onError={onError}
                  onCancel={onCancel}
                  disabled={processing}
                  forceReRender={[appointmentDetails?.amount, retryCount]}
                />
              </PayPalScriptProvider>
            )}

            {/* Action Buttons */}
            <div className="space-y-3">
              <Button
                onClick={handleCancel}
                disabled={processing}
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
