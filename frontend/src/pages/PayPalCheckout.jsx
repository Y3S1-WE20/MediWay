import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { CheckCircle, CreditCard, DollarSign, ArrowLeft } from 'lucide-react';
import { Card, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import api from '../api/api';
import { endpoints } from '../api/endpoints';

const PayPalCheckout = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const handlePayPalReturn = async () => {
      try {
        const paymentId = searchParams.get('paymentId');
        const payerId = searchParams.get('PayerID');
        const token = searchParams.get('token');

        console.log('=== PayPal Return Handler ===');
        console.log('URL Parameters:', { paymentId, payerId, token });
        console.log('All search params:', Object.fromEntries(searchParams.entries()));

        if (!payerId) {
          console.error('❌ Missing payerId parameter - PayPal return invalid');
          setError('Payment Failed - Invalid PayPal return parameters');
          setLoading(false);
          return;
        }

        // Handle Express Checkout flow (token-based)
        if (token && !paymentId) {
          console.log('🔄 Handling Express Checkout flow with token:', token);

          try {
            console.log('📡 Calling execute-token endpoint with:', { token, payerId });
            const response = await api.post(endpoints.executeTokenPayment, {
              token: token,
              payerId: payerId
            });

            console.log('✅ Execute-token response:', response);

            if (response.data.success) {
              console.log('🎉 Payment successful, navigating to appointments');
              navigate('/appointments');
            } else {
              console.error('❌ Payment execution failed:', response.data.message);
              setError(response.data.message || 'Payment execution failed');
            }
          } catch (apiError) {
            console.error('💥 API Error in execute-token:', apiError);
            console.error('API Error response:', apiError.response?.data);
            console.error('API Error status:', apiError.response?.status);
            setError(apiError.response?.data?.message || 'Payment processing failed');
          }
        }
        // Handle REST API flow (paymentId-based)
        else if (paymentId) {
          console.log('🔄 Handling REST API flow with paymentId:', paymentId);

          // For simulated checkout, use a default payerId if not provided
          const finalPayerId = payerId || `SIMULATED_PAYER_${paymentId}`;
          console.log('📡 Final payerId for execution:', finalPayerId);

          try {
            console.log('📡 Calling execute-payment endpoint with:', { paymentId, payerId: finalPayerId });
            const response = await api.post(endpoints.executePayment, {
              paymentId,
              payerId: finalPayerId
            });

            console.log('✅ Execute-payment response:', response);

            if (response.data.success) {
              console.log('🎉 Payment successful, navigating to appointments');
              navigate('/appointments');
            } else {
              console.error('❌ Payment execution failed:', response.data.message);
              setError(response.data.message || 'Payment execution failed');
            }
          } catch (apiError) {
            console.error('💥 API Error in execute-payment:', apiError);
            console.error('API Error response:', apiError.response?.data);
            console.error('API Error status:', apiError.response?.status);
            setError(apiError.response?.data?.message || 'Payment processing failed');
          }
        } else {
          console.error('❌ No valid payment parameters found');
          setError('Payment Failed - Missing required parameters');
        }
      } catch (error) {
        console.error('💥 Unexpected error in handlePayPalReturn:', error);
        setError('Payment processing failed due to unexpected error');
      } finally {
        console.log('🏁 PayPal return handler completed');
        setLoading(false);
      }
    };

    handlePayPalReturn();
  }, [searchParams, navigate]);

  const handleCancel = () => {
    navigate('/appointments');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="w-full max-w-md"
        >
          <Card className="shadow-2xl border-2 border-blue-100">
            <CardContent className="p-8 text-center">
              <div className="mb-4">
                <CreditCard className="w-16 h-16 mx-auto text-blue-500" />
              </div>
              <h2 className="text-xl font-semibold text-gray-800 mb-2">
                Processing Payment
              </h2>
              <p className="text-gray-600 mb-4">
                Completing your appointment payment...
              </p>
              <motion.div
                animate={{ rotate: 360 }}
                transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full mx-auto"
              />
              <p className="text-sm text-gray-500 mt-4">
                Development Mode: Simulated PayPal Checkout
              </p>
            </CardContent>
          </Card>
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
            <div className="text-center">
              {error ? (
                <div>
                  <div className="text-red-500 mb-4">
                    <CreditCard className="w-16 h-16 mx-auto mb-2" />
                    <h2 className="text-xl font-semibold">Payment Failed</h2>
                  </div>
                  <p className="text-gray-600 mb-6">{error}</p>
                  <Button onClick={handleCancel} className="w-full">
                    Return to Appointments
                  </Button>
                </div>
              ) : (
                <div>
                  <div className="text-green-500 mb-4">
                    <CheckCircle className="w-16 h-16 mx-auto mb-2" />
                    <h2 className="text-xl font-semibold">Payment Successful!</h2>
                  </div>
                  <p className="text-gray-600 mb-6">Your payment has been processed successfully.</p>
                  <Button onClick={handleCancel} className="w-full">
                    View Appointments
                  </Button>
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  );
};

export default PayPalCheckout;
