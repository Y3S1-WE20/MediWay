import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { CreditCard, DollarSign, CheckCircle, X, Receipt, Shield, ExternalLink } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Select } from '../components/ui/select';
import { Badge } from '../components/ui/badge';
import { useAuth } from '../context/AuthContext';
import api from '../api/api';
import { endpoints } from '../api/endpoints';

const Payments = () => {
  const { user } = useAuth();
  const [payments, setPayments] = useState([]);
  const [receipts, setReceipts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [processingPayment, setProcessingPayment] = useState(false);
  const [error, setError] = useState(null);
  
  const [paymentData, setPaymentData] = useState({
    amount: '',
    currency: 'USD',
    description: '',
    paymentMethod: 'PAYPAL'
  });

  useEffect(() => {
    fetchPayments();
    fetchReceipts();
  }, []);

  const fetchPayments = async () => {
    setLoading(true);
    try {
      const response = await api.get(endpoints.getMyPayments);
      setPayments(response.data);
    } catch (error) {
      console.error('Error fetching payments:', error);
      setError('Failed to load payments.');
    } finally {
      setLoading(false);
    }
  };

  const fetchReceipts = async () => {
    try {
      const response = await api.get(endpoints.getMyReceipts);
      setReceipts(response.data);
    } catch (error) {
      console.error('Error fetching receipts:', error);
    }
  };

  const handleCreatePayment = async (e) => {
    e.preventDefault();
    setProcessingPayment(true);
    setError(null);

    try {
      const response = await api.post(endpoints.createPayment, {
        amount: parseFloat(paymentData.amount),
        currency: paymentData.currency,
        description: paymentData.description,
        paymentMethod: paymentData.paymentMethod,
        returnUrl: `/payment-success`,
        cancelUrl: `/payment-cancel`
      });

      sessionStorage.setItem('pendingPaymentId', response.data.paymentId);
      window.location.href = response.data.approvalUrl;
    } catch (error) {
      console.error('Error creating payment:', error);
      setError(error.response?.data?.message || 'Failed to create payment.');
      setProcessingPayment(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setPaymentData(prev => ({ ...prev, [name]: value }));
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      COMPLETED: { color: 'bg-green-100 text-green-800', label: 'Completed' },
      APPROVED: { color: 'bg-blue-100 text-blue-800', label: 'Approved' },
      CREATED: { color: 'bg-yellow-100 text-yellow-800', label: 'Pending' },
      FAILED: { color: 'bg-red-100 text-red-800', label: 'Failed' },
      CANCELLED: { color: 'bg-gray-100 text-gray-800', label: 'Cancelled' }
    };

    const config = statusConfig[status] || statusConfig.CREATED;
    return <Badge className={config.color}>{config.label}</Badge>;
  };

  const completedPayments = payments.filter(p => p.status === 'COMPLETED');
  const pendingPayments = payments.filter(p => ['CREATED', 'APPROVED'].includes(p.status));
  const totalPaid = completedPayments.reduce((sum, payment) => sum + payment.amount, 0);
  const totalPending = pendingPayments.reduce((sum, payment) => sum + payment.amount, 0);

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
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="mb-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">Payments & Billing</h1>
          <p className="text-gray-600">Manage your medical payments using PayPal</p>
        </motion.div>

        {error && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg text-red-700">
            {error}
          </motion.div>
        )}

        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <Card hover>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Total Paid</p>
                  <p className="text-3xl font-bold text-green-600">``</p>
                </div>
                <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                  <CheckCircle className="w-6 h-6 text-green-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card hover>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Pending</p>
                  <p className="text-3xl font-bold text-yellow-600">``</p>
                </div>
                <div className="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center">
                  <DollarSign className="w-6 h-6 text-yellow-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card hover>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Total Payments</p>
                  <p className="text-3xl font-bold text-gray-800">{payments.length}</p>
                </div>
                <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                  <Receipt className="w-6 h-6 text-blue-600" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <Button onClick={() => setShowPaymentModal(true)} className="bg-[#4CAF50] hover:bg-[#45a049] text-white mb-8">
          <CreditCard className="w-5 h-5 mr-2" />
          Create New Payment
        </Button>

        {payments.length > 0 && (
          <div className="space-y-4">
            <h2 className="text-2xl font-bold text-gray-800 mb-4">Payment History</h2>
            {payments.map((payment) => (
              <Card key={payment.id}>
                <CardContent className="p-6">
                  <div className="flex justify-between">
                    <div>
                      <h3 className="text-lg font-semibold">{payment.description}</h3>
                      {getStatusBadge(payment.status)}
                      <p className="text-sm text-gray-500">{new Date(payment.createdAt).toLocaleString()}</p>
                    </div>
                    <p className="text-2xl font-bold">{payment.currency} ``</p>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}

        <AnimatePresence>
          {showPaymentModal && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
              onClick={() => !processingPayment && setShowPaymentModal(false)}
            >
              <motion.div
                initial={{ scale: 0.9 }}
                animate={{ scale: 1 }}
                exit={{ scale: 0.9 }}
                onClick={(e) => e.stopPropagation()}
                className="w-full max-w-md"
              >
                <Card>
                  <CardHeader>
                    <CardTitle>Create Payment</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <form onSubmit={handleCreatePayment} className="space-y-4">
                      <Input label="Amount" name="amount" type="number" step="0.01" min="0.01" value={paymentData.amount} onChange={handleInputChange} required />
                      <Input label="Description" name="description" value={paymentData.description} onChange={handleInputChange} required />
                      <Button type="submit" disabled={processingPayment} className="w-full bg-[#4CAF50] hover:bg-[#45a049]">
                        {processingPayment ? 'Processing...' : 'Continue to PayPal'}
                      </Button>
                    </form>
                  </CardContent>
                </Card>
              </motion.div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
};

export default Payments;
