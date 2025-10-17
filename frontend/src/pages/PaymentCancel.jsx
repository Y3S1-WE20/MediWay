import { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import api from '../api/api';
import { endpoints } from '../api/endpoints';
import { Card } from '../components/ui/card';
import { Button } from '../components/ui/button';

function PaymentCancel() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const cancelPayment = async () => {
      try {
        const paymentId = searchParams.get('paymentId') || sessionStorage.getItem('pendingPaymentId');
        
        if (paymentId) {
          console.log('Cancelling payment:', paymentId);
          await api.post('/payments/cancel', { paymentId });
          console.log('Payment cancelled successfully');
        }

        // Clear pending payment from session
        sessionStorage.removeItem('pendingPaymentId');
        sessionStorage.removeItem('pendingAppointmentId');
      } catch (error) {
        console.error('Error cancelling payment:', error);
      }
    };

    cancelPayment();
  }, [searchParams]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <Card className="p-8 max-w-md w-full">
        <div className="text-center mb-6">
          <div className="flex justify-center mb-4">
            <div className="bg-yellow-100 rounded-full p-3">
              <svg className="h-12 w-12 text-yellow-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
            </div>
          </div>
          <h2 className="text-2xl font-bold text-yellow-600 mb-2">Payment Cancelled</h2>
          <p className="text-gray-600 mb-6">
            You have cancelled the payment. No charges have been made to your account.
          </p>
        </div>

        <div className="flex gap-3">
          <Button onClick={() => navigate('/payments')} variant="outline" className="flex-1">
            Try Again
          </Button>
          <Button onClick={() => navigate('/')} className="flex-1 bg-green-600 hover:bg-green-700">
            Go Home
          </Button>
        </div>
      </Card>
    </div>
  );
}

export default PaymentCancel;
