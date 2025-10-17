import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import api from '../api/api';
import { Card } from '../components/ui/card';
import { Button } from '../components/ui/button';

function PaymentSuccess() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState('processing'); // processing, success, error
  const [paymentData, setPaymentData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const executePayment = async () => {
      try {
        // Get payment ID and token from URL params or session storage
        const paymentId = searchParams.get('paymentId') || sessionStorage.getItem('pendingPaymentId');
        const appointmentId = searchParams.get('appointmentId') || sessionStorage.getItem('pendingAppointmentId');
        const token = searchParams.get('token') || sessionStorage.getItem('paypalToken');
        const payerId = searchParams.get('PayerID') || sessionStorage.getItem('paypalPayerId');

        if (!paymentId && !token) {
          setError('Payment ID or token not found. Please log in and try again.');
          setStatus('error');
          return;
        }

        // Prefer token-based execution if available
        let response;
        if (token && payerId) {
          response = await api.post('/paypal/execute-token', {
            token,
            payerId
          });
        } else if (paymentId) {
          response = await api.post('/paypal/execute', {
            paymentId,
            payerId: payerId || 'MOCK-PAYER'
          });
        } else {
          setError('Missing payment information.');
          setStatus('error');
          return;
        }

        if (response.data.success) {
          setPaymentData({
            paymentId: response.data.paymentId,
            appointmentId: response.data.appointmentId,
            message: response.data.message
          });
          setStatus('success');
        } else {
          setError(response.data.message || 'Payment execution failed');
          setStatus('error');
        }

        // Clear pending payment from session
        sessionStorage.removeItem('pendingPaymentId');
        sessionStorage.removeItem('pendingAppointmentId');
        sessionStorage.removeItem('paypalToken');
        sessionStorage.removeItem('paypalPayerId');
      } catch (err) {
        console.error('Payment execution error:', err);
        setError(err.response?.data?.error || err.message || 'Failed to complete payment');
        setStatus('error');
      }
    };

    executePayment();
  }, [searchParams]);

  if (status === 'processing') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <Card className="p-8 max-w-md w-full text-center">
          <div className="flex justify-center mb-4">
            <svg className="animate-spin h-12 w-12 text-blue-600" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
            </svg>
          </div>
          <h2 className="text-xl font-semibold mb-2">Processing Payment...</h2>
          <p className="text-gray-600">Please wait while we confirm your payment</p>
        </Card>
      </div>
    );
  }

  if (status === 'error') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <Card className="p-8 max-w-md w-full">
          <div className="text-center mb-6">
            <div className="flex justify-center mb-4">
              <div className="bg-red-100 rounded-full p-3">
                <svg className="h-12 w-12 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </div>
            </div>
            <h2 className="text-2xl font-bold text-red-600 mb-2">Payment Failed</h2>
            <p className="text-gray-600 mb-4">{error}</p>
          </div>
          <div className="flex gap-3">
            <Button onClick={() => navigate('/payments')} variant="outline" className="flex-1">
              View Payments
            </Button>
            <Button onClick={() => navigate('/')} className="flex-1 bg-green-600 hover:bg-green-700">
              Go Home
            </Button>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <Card className="p-8 max-w-lg w-full">
        {/* Success Icon */}
        <div className="text-center mb-6">
          <div className="flex justify-center mb-4">
            <div className="bg-green-100 rounded-full p-3">
              <svg className="h-12 w-12 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
          </div>
          <h2 className="text-2xl font-bold text-green-600 mb-2">Payment Successful!</h2>
          <p className="text-gray-600">Your payment has been processed successfully</p>
        </div>

        {/* Payment Details */}
        {paymentData && (
          <div className="bg-gray-50 rounded-lg p-4 mb-6">
            <h3 className="font-semibold mb-3">Payment Details</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-600">Amount:</span>
                <span className="font-semibold">${paymentData.amount} {paymentData.currency}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Payment ID:</span>
                <span className="font-mono text-xs">{paymentData.paymentId}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Status:</span>
                <span className="bg-green-100 text-green-800 px-2 py-1 rounded text-xs font-semibold">
                  {paymentData.status}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Method:</span>
                <span className="font-semibold">{paymentData.paymentMethod}</span>
              </div>
            </div>
          </div>
        )}

        {/* Action Buttons */}
        <div className="flex gap-3">
          <Button onClick={() => navigate('/payments')} variant="outline" className="flex-1">
            View All Payments
          </Button>
          <Button onClick={() => navigate('/')} className="flex-1 bg-green-600 hover:bg-green-700">
            Go to Dashboard
          </Button>
        </div>
      </Card>
    </div>
  );
}

export default PaymentSuccess;
