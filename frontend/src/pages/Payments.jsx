import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { CreditCard, DollarSign, CheckCircle, X, Receipt, Shield } from 'lucide-react';
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
  const [bills, setBills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [selectedBill, setSelectedBill] = useState(null);
  const [processingPayment, setProcessingPayment] = useState(false);
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  
  const [paymentData, setPaymentData] = useState({
    paymentMethod: 'card',
    cardNumber: '',
    cardName: '',
    expiryDate: '',
    cvv: '',
    insuranceId: '',
  });

  useEffect(() => {
    fetchBills();
  }, []);

  const fetchBills = async () => {
    setLoading(true);
    try {
      // Simulate API call
      const mockBills = [
        {
          id: 1,
          billNumber: 'INV-2025-001',
          description: 'Consultation with Dr. Sarah Johnson',
          date: '2025-10-15',
          amount: 150.00,
          status: 'unpaid',
          dueDate: '2025-10-30',
        },
        {
          id: 2,
          billNumber: 'INV-2025-002',
          description: 'Laboratory Tests',
          date: '2025-10-12',
          amount: 85.50,
          status: 'unpaid',
          dueDate: '2025-10-27',
        },
        {
          id: 3,
          billNumber: 'INV-2025-003',
          description: 'X-Ray Examination',
          date: '2025-09-28',
          amount: 120.00,
          status: 'paid',
          paidDate: '2025-10-01',
        },
      ];

      setTimeout(() => {
        setBills(mockBills);
        setLoading(false);
      }, 800);

      // In production, use:
      // const response = await api.get(endpoints.getPayments);
      // setBills(response.data);
    } catch (error) {
      console.error('Error fetching bills:', error);
      setLoading(false);
    }
  };

  const handlePayNow = (bill) => {
    setSelectedBill(bill);
    setShowPaymentModal(true);
    setPaymentSuccess(false);
  };

  const handlePaymentSubmit = async (e) => {
    e.preventDefault();
    setProcessingPayment(true);

    try {
      // Simulate payment processing
      await new Promise(resolve => setTimeout(resolve, 2000));

      // In production, use:
      // await api.post(endpoints.createPayment, {
      //   billId: selectedBill.id,
      //   amount: selectedBill.amount,
      //   paymentMethod: paymentData.paymentMethod,
      //   ...paymentData,
      // });

      setPaymentSuccess(true);
      
      // Update bills list
      setBills(prev =>
        prev.map(bill =>
          bill.id === selectedBill.id
            ? { ...bill, status: 'paid', paidDate: new Date().toISOString().split('T')[0] }
            : bill
        )
      );

      setTimeout(() => {
        setShowPaymentModal(false);
        setPaymentData({
          paymentMethod: 'card',
          cardNumber: '',
          cardName: '',
          expiryDate: '',
          cvv: '',
          insuranceId: '',
        });
      }, 2000);
    } catch (error) {
      alert('Payment failed. Please try again.');
    } finally {
      setProcessingPayment(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setPaymentData(prev => ({ ...prev, [name]: value }));
  };

  const unpaidBills = bills.filter(b => b.status === 'unpaid');
  const paidBills = bills.filter(b => b.status === 'paid');
  const totalUnpaid = unpaidBills.reduce((sum, bill) => sum + bill.amount, 0);

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
          <h1 className="text-4xl font-bold text-gray-800 mb-2">
            Payments & Billing
          </h1>
          <p className="text-gray-600">
            Manage your medical bills and payment history
          </p>
        </motion.div>

        {/* Summary Cards */}
        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
          >
            <Card hover>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-600 mb-1">Total Unpaid</p>
                    <p className="text-3xl font-bold text-red-600">
                      ${totalUnpaid.toFixed(2)}
                    </p>
                  </div>
                  <div className="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center">
                    <DollarSign className="w-6 h-6 text-red-600" />
                  </div>
                </div>
              </CardContent>
            </Card>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
          >
            <Card hover>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-600 mb-1">Unpaid Bills</p>
                    <p className="text-3xl font-bold text-gray-800">
                      {unpaidBills.length}
                    </p>
                  </div>
                  <div className="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center">
                    <Receipt className="w-6 h-6 text-yellow-600" />
                  </div>
                </div>
              </CardContent>
            </Card>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <Card hover>
              <CardContent className="p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-600 mb-1">Paid Bills</p>
                    <p className="text-3xl font-bold text-green-600">
                      {paidBills.length}
                    </p>
                  </div>
                  <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                    <CheckCircle className="w-6 h-6 text-green-600" />
                  </div>
                </div>
              </CardContent>
            </Card>
          </motion.div>
        </div>

        {/* Unpaid Bills */}
        {unpaidBills.length > 0 && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="mb-8"
          >
            <h2 className="text-2xl font-bold text-gray-800 mb-4">
              Unpaid Bills
            </h2>
            <div className="space-y-4">
              {unpaidBills.map((bill, index) => (
                <motion.div
                  key={bill.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                >
                  <Card hover>
                    <CardContent className="p-6">
                      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                        <div className="flex-1">
                          <div className="flex items-center gap-3 mb-2">
                            <h3 className="text-lg font-semibold text-gray-800">
                              {bill.description}
                            </h3>
                            <Badge variant="danger">Unpaid</Badge>
                          </div>
                          <p className="text-sm text-gray-600 mb-1">
                            Bill No: {bill.billNumber}
                          </p>
                          <p className="text-sm text-gray-500">
                            Date: {new Date(bill.date).toLocaleDateString()} • 
                            Due: {new Date(bill.dueDate).toLocaleDateString()}
                          </p>
                        </div>
                        <div className="flex items-center gap-4">
                          <div className="text-right">
                            <p className="text-sm text-gray-600">Amount</p>
                            <p className="text-2xl font-bold text-gray-800">
                              ${bill.amount.toFixed(2)}
                            </p>
                          </div>
                          <Button
                            onClick={() => handlePayNow(bill)}
                            className="bg-[#4CAF50] hover:bg-[#45a049]"
                          >
                            Pay Now
                          </Button>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}

        {/* Paid Bills */}
        {paidBills.length > 0 && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
          >
            <h2 className="text-2xl font-bold text-gray-800 mb-4">
              Payment History
            </h2>
            <div className="space-y-4">
              {paidBills.map((bill, index) => (
                <motion.div
                  key={bill.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                >
                  <Card>
                    <CardContent className="p-6">
                      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                        <div className="flex-1">
                          <div className="flex items-center gap-3 mb-2">
                            <h3 className="text-lg font-semibold text-gray-800">
                              {bill.description}
                            </h3>
                            <Badge variant="success">Paid</Badge>
                          </div>
                          <p className="text-sm text-gray-600 mb-1">
                            Bill No: {bill.billNumber}
                          </p>
                          <p className="text-sm text-gray-500">
                            Paid on: {new Date(bill.paidDate).toLocaleDateString()}
                          </p>
                        </div>
                        <div className="text-right">
                          <p className="text-sm text-gray-600">Amount</p>
                          <p className="text-2xl font-bold text-green-600">
                            ${bill.amount.toFixed(2)}
                          </p>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}

        {/* Payment Modal */}
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
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
                onClick={(e) => e.stopPropagation()}
                className="w-full max-w-md"
              >
                <Card>
                  <CardHeader>
                    <div className="flex items-center justify-between">
                      <CardTitle>
                        {paymentSuccess ? 'Payment Successful!' : 'Payment Details'}
                      </CardTitle>
                      {!processingPayment && !paymentSuccess && (
                        <button
                          onClick={() => setShowPaymentModal(false)}
                          className="text-gray-500 hover:text-gray-700"
                        >
                          <X className="w-5 h-5" />
                        </button>
                      )}
                    </div>
                  </CardHeader>
                  <CardContent>
                    {paymentSuccess ? (
                      <motion.div
                        initial={{ scale: 0 }}
                        animate={{ scale: 1 }}
                        className="text-center py-8"
                      >
                        <div className="mx-auto w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mb-4">
                          <CheckCircle className="w-12 h-12 text-[#4CAF50]" />
                        </div>
                        <h3 className="text-2xl font-bold text-gray-800 mb-2">
                          Payment Complete
                        </h3>
                        <p className="text-gray-600 mb-4">
                          Your payment of ${selectedBill?.amount.toFixed(2)} has been processed successfully
                        </p>
                        <motion.div
                          animate={{ rotate: 360 }}
                          transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                          className="w-8 h-8 border-2 border-[#4CAF50] border-t-transparent rounded-full mx-auto"
                        />
                      </motion.div>
                    ) : (
                      <form onSubmit={handlePaymentSubmit} className="space-y-4">
                        <div className="bg-gray-50 p-4 rounded-lg">
                          <p className="text-sm text-gray-600">Amount to Pay</p>
                          <p className="text-3xl font-bold text-gray-800">
                            ${selectedBill?.amount.toFixed(2)}
                          </p>
                        </div>

                        <Select
                          label="Payment Method"
                          name="paymentMethod"
                          value={paymentData.paymentMethod}
                          onChange={handleInputChange}
                          required
                        >
                          <option value="card">Credit/Debit Card</option>
                          <option value="insurance">Insurance</option>
                        </Select>

                        {paymentData.paymentMethod === 'card' ? (
                          <>
                            <Input
                              label="Card Number"
                              name="cardNumber"
                              value={paymentData.cardNumber}
                              onChange={handleInputChange}
                              placeholder="1234 5678 9012 3456"
                              required
                            />
                            <Input
                              label="Cardholder Name"
                              name="cardName"
                              value={paymentData.cardName}
                              onChange={handleInputChange}
                              placeholder="John Doe"
                              required
                            />
                            <div className="grid grid-cols-2 gap-4">
                              <Input
                                label="Expiry Date"
                                name="expiryDate"
                                value={paymentData.expiryDate}
                                onChange={handleInputChange}
                                placeholder="MM/YY"
                                required
                              />
                              <Input
                                label="CVV"
                                name="cvv"
                                value={paymentData.cvv}
                                onChange={handleInputChange}
                                placeholder="123"
                                maxLength="3"
                                required
                              />
                            </div>
                          </>
                        ) : (
                          <Input
                            label="Insurance ID"
                            name="insuranceId"
                            value={paymentData.insuranceId}
                            onChange={handleInputChange}
                            placeholder="INS-123456"
                            required
                          />
                        )}

                        <div className="bg-blue-50 p-3 rounded-lg flex items-start gap-2">
                          <Shield className="w-5 h-5 text-blue-600 mt-0.5" />
                          <p className="text-xs text-blue-800">
                            Your payment information is encrypted and secure
                          </p>
                        </div>

                        <Button
                          type="submit"
                          disabled={processingPayment}
                          className="w-full bg-[#4CAF50] hover:bg-[#45a049]"
                        >
                          {processingPayment ? (
                            <motion.div
                              animate={{ rotate: 360 }}
                              transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                              className="w-5 h-5 border-2 border-white border-t-transparent rounded-full mx-auto"
                            />
                          ) : (
                            `Pay $${selectedBill?.amount.toFixed(2)}`
                          )}
                        </Button>
                      </form>
                    )}
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
