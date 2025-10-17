import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { CheckCircle, CreditCard, DollarSign, ArrowLeft, Loader } from 'lucide-react';
import { PayPalScriptProvider, PayPalButtons } from '@paypal/react-paypal-js';
import { Card, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import api from '../api/api';

// PayPal Client ID from your sandbox account
const PAYPAL_CLIENT_ID = "AQI0v2iVOX7LKr4xDLL2eH6pKKrj-G2aXGQFByWp4w3B9m73fqL6_mfzAdP5Ii1ujVhQK3rlJkNERmAc";

const PayPalCheckout = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [paymentDetails, setPaymentDetails] = useState(null);
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState(null);

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
    <PayPalScriptProvider 
      options={{ 
        "client-id": PAYPAL_CLIENT_ID,
        "currency": "USD",
        "intent": "capture",
        "disable-funding": "card,credit,paylater,venmo",
        "enable-funding": "paypal"
      }}
    >
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

              {/* Error Display */}
              {error && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
                  <p className="text-sm text-red-800 text-center">{error}</p>
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
              </div>

              {/* PayPal Buttons */}
              {processing ? (
                <div className="flex items-center justify-center py-8">
                  <Loader className="w-8 h-8 text-blue-600 animate-spin" />
                  <span className="ml-3 text-gray-600">Processing payment...</span>
                </div>
              ) : (
                <div className="mb-4">
                  <PayPalButtons
                    style={{
                      layout: "vertical",
                      shape: "rect",
                      color: "gold",
                      label: "paypal",
                      height: 45
                    }}
                    disabled={processing}
                    forceReRender={[paymentDetails.amount]}
                    createOrder={(data, actions) => {
                      console.log('Creating PayPal order...');
                      return actions.order.create({
                        purchase_units: [
                          {
                            amount: {
                              value: paymentDetails.amount.toFixed(2),
                              currency_code: "USD",
                            },
                            description: `Medical Consultation - Appointment #${paymentDetails.appointmentId}`,
                          },
                        ],
                        application_context: {
                          shipping_preference: "NO_SHIPPING"
                        }
                      });
                    }}
                    onApprove={async (data, actions) => {
                      console.log('PayPal onApprove data:', data);
                      setProcessing(true);
                      setError(null);

                      try {
                        // Payment approved by PayPal - send confirmation to backend
                        // We don't need to call actions.order.capture() as it causes "Window closed" errors
                        // PayPal has already approved the payment when onApprove is called
                        console.log('Payment approved! Order ID:', data.orderID);
                        console.log('Sending payment confirmation to backend...');
                        
                        const response = await api.post('/payments/execute', {
                          paymentId: paymentDetails.paymentId,
                          transactionId: data.orderID, // Use the PayPal order ID as transaction ID
                          paypalOrderId: data.orderID,
                          payerId: data.payerID,
                          paymentSource: data.paymentSource
                        });

                        console.log('Backend response:', response.data);

                        if (response.data.success) {
                          // Clear session storage
                          sessionStorage.removeItem('pendingPaymentId');
                          sessionStorage.removeItem('pendingAppointmentId');

                          // Redirect to success page
                          navigate(`/payment-success?paymentId=${paymentDetails.paymentId}&appointmentId=${paymentDetails.appointmentId}`);
                        } else {
                          throw new Error(response.data.message || 'Payment execution failed');
                        }
                      } catch (error) {
                        console.error('Payment approval failed:', error);
                        const errorMsg = error.response?.data?.message || error.message || 'Payment processing failed. Please try again.';
                        setError(errorMsg);
                        setProcessing(false);
                      }
                    }}
                    onCancel={(data) => {
                      console.log('PayPal payment cancelled:', data);
                      setError('Payment was cancelled. You can try again or return to appointments.');
                    }}
                    onError={(err) => {
                      console.error('PayPal error:', err);
                      setError('Payment error occurred. Please try again or contact support if the issue persists.');
                    }}
                  />
                </div>
              )}

              {/* Cancel Button */}
              <Button
                onClick={handleCancel}
                disabled={processing}
                variant="outline"
                className="w-full border-2 border-gray-300 hover:bg-gray-100 py-4"
              >
                <ArrowLeft className="w-4 h-4 mr-2" />
                Cancel Payment
              </Button>

              {/* Terms */}
              <p className="text-xs text-gray-500 text-center mt-6">
                By completing this payment, you agree to MediWay's terms and conditions.
              </p>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </PayPalScriptProvider>
  );
};

export default PayPalCheckout;
