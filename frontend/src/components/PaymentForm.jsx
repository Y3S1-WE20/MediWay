import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api';
import { endpoints } from '../api/endpoints';
import { Card } from './ui/card';
import { Button } from './ui/button';
import { Input } from './ui/input';

/**
 * Payment component for processing PayPal payments
 * Supports appointment payments and general payments
 */
function PaymentForm({ appointmentId, amount: initialAmount, description: initialDescription, onSuccess, onCancel }) {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    amount: initialAmount || '',
    currency: 'USD',
    description: initialDescription || '',
    appointmentId: appointmentId || null,
    paymentMethod: 'PAYPAL'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Validate amount
      if (!formData.amount || parseFloat(formData.amount) <= 0) {
        setError('Please enter a valid amount');
        setLoading(false);
        return;
      }

      // Prepare payment request
      const paymentRequest = {
        amount: parseFloat(formData.amount),
        currency: formData.currency,
        description: formData.description || 'MediWay Payment',
        appointmentId: formData.appointmentId,
        returnUrl: `${window.location.origin}/payment-success`,
        cancelUrl: `${window.location.origin}/payment-cancel`,
        paymentMethod: formData.paymentMethod
      };

      console.log('Creating payment:', paymentRequest);

      // Create payment via backend
      const response = await api.post(endpoints.createPayment, paymentRequest);

      console.log('Payment created:', response.data);

      // Store payment ID in session storage for later execution
      sessionStorage.setItem('pendingPaymentId', response.data.paymentId);
      sessionStorage.setItem('pendingPaymentData', JSON.stringify(response.data));

      // Redirect to PayPal
      if (response.data.approvalUrl) {
        window.location.href = response.data.approvalUrl;
      } else {
        setError('Failed to get PayPal approval URL');
      }
    } catch (err) {
      console.error('Payment creation error:', err);
      setError(err.response?.data?.error || err.message || 'Failed to create payment. Please try again.');
      setLoading(false);
    }
  };

  const handleCancel = () => {
    if (onCancel) {
      onCancel();
    } else {
      navigate(-1);
    }
  };

  return (
    <Card className="p-6 max-w-md mx-auto">
      <h2 className="text-2xl font-bold mb-6">Make Payment</h2>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <form onSubmit={handlePayment} className="space-y-4">
        {/* Amount */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Amount (USD) <span className="text-red-500">*</span>
          </label>
          <Input
            type="number"
            name="amount"
            value={formData.amount}
            onChange={handleInputChange}
            placeholder="Enter amount"
            min="0.01"
            step="0.01"
            required
            disabled={!!initialAmount || loading}
          />
        </div>

        {/* Description */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Description <span className="text-red-500">*</span>
          </label>
          <Input
            type="text"
            name="description"
            value={formData.description}
            onChange={handleInputChange}
            placeholder="Enter payment description"
            required
            disabled={!!initialDescription || loading}
          />
        </div>

        {/* Payment Method */}
        <div>
          <label className="block text-sm font-medium mb-2">
            Payment Method
          </label>
          <select
            name="paymentMethod"
            value={formData.paymentMethod}
            onChange={handleInputChange}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            disabled={loading}
          >
            <option value="PAYPAL">PayPal</option>
            <option value="CREDIT_CARD">Credit Card (via PayPal)</option>
            <option value="DEBIT_CARD">Debit Card (via PayPal)</option>
          </select>
        </div>

        {/* Payment Summary */}
        <div className="bg-gray-50 p-4 rounded-md">
          <h3 className="font-semibold mb-2">Payment Summary</h3>
          <div className="space-y-1 text-sm">
            <div className="flex justify-between">
              <span>Amount:</span>
              <span className="font-semibold">${formData.amount || '0.00'}</span>
            </div>
            <div className="flex justify-between">
              <span>Currency:</span>
              <span className="font-semibold">{formData.currency}</span>
            </div>
            <div className="flex justify-between">
              <span>Method:</span>
              <span className="font-semibold">{formData.paymentMethod}</span>
            </div>
          </div>
        </div>

        {/* Buttons */}
        <div className="flex gap-3 pt-4">
          <Button
            type="button"
            onClick={handleCancel}
            variant="outline"
            className="flex-1"
            disabled={loading}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            className="flex-1 bg-blue-600 hover:bg-blue-700"
            disabled={loading}
          >
            {loading ? (
              <span className="flex items-center gap-2">
                <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
                Processing...
              </span>
            ) : (
              <span>Pay with PayPal</span>
            )}
          </Button>
        </div>
      </form>

      <div className="mt-6 text-xs text-gray-500 text-center">
        <p>🔒 Secure payment powered by PayPal Sandbox</p>
        <p className="mt-1">You will be redirected to PayPal to complete the payment</p>
      </div>
    </Card>
  );
}

export default PaymentForm;
