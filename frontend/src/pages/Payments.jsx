import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { DollarSign, CheckCircle, Receipt } from 'lucide-react';
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
  const [error, setError] = useState(null);

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

  const generateReceipt = async (paymentId) => {
    try {
      setLoading(true);
      const response = await api.post(`/payments/${paymentId}/generate-receipt`);
      
      if (response.data.success) {
        alert('Receipt generated successfully!');
        fetchReceipts(); // Refresh receipts list
      } else {
        alert(response.data.message || 'Failed to generate receipt');
      }
    } catch (error) {
      console.error('Error generating receipt:', error);
      alert('Error generating receipt. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const downloadReceipt = async (paymentId) => {
    if (!paymentId) {
      alert('Payment ID is required to download receipt.');
      return;
    }

    try {
      setLoading(true);
      console.log('Downloading receipt for payment ID:', paymentId);
      
      // First check if receipt exists, if not generate it
      let receiptResponse;
      try {
        receiptResponse = await api.get(`/payments/receipt/payment/${paymentId}`, {
          headers: { 'X-User-Id': user?.id }
        });
      } catch (error) {
        console.log('Receipt not found, attempting to generate...');
        if (error.response && (error.response.status === 404 || error.response.status === 500)) {
          // Receipt doesn't exist, generate it first
          try {
            const generateResponse = await api.post(`/payments/${paymentId}/generate-receipt`, {}, {
              headers: { 'X-User-Id': user?.id }
            });
            
            if (generateResponse.data.success) {
              receiptResponse = { data: generateResponse.data.receipt };
              console.log('Receipt generated successfully:', receiptResponse.data);
            } else {
              alert('Failed to generate receipt: ' + (generateResponse.data.message || 'Unknown error'));
              return;
            }
          } catch (genError) {
            console.error('Failed to generate receipt:', genError);
            alert('Failed to generate receipt. The backend server may not be running. Error: ' + 
                  (genError.response?.data?.message || genError.message || 'Unknown error'));
            return;
          }
        } else {
          throw error;
        }
      }

      const receiptNumber = receiptResponse.data?.receiptNumber;
      if (!receiptNumber) {
        alert('Receipt number not found. Please contact support.');
        return;
      }
      
      console.log('Downloading PDF for receipt number:', receiptNumber);
      
      // Download the PDF
      const response = await api.get(`/payments/receipt/${receiptNumber}/pdf`, {
        responseType: 'blob',
        headers: { 'X-User-Id': user?.id }
      });
      
      if (response.data.size === 0) {
        alert('PDF file is empty. Please try regenerating the receipt.');
        return;
      }
      
      // Create download link
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `receipt-${receiptNumber}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      console.log('Receipt downloaded successfully');
    } catch (error) {
      console.error('Error downloading receipt:', error);
      
      if (error.code === 'NETWORK_ERROR' || !error.response) {
        alert('Network error: Cannot connect to server. Please ensure the backend is running and try again.');
      } else if (error.response?.status === 403) {
        alert('Access denied. You may not have permission to download this receipt.');
      } else if (error.response?.status === 404) {
        alert('Receipt not found. Please contact support.');
      } else {
        alert('Error downloading receipt: ' + (error.response?.data?.message || error.message || 'Please try again'));
      }
    } finally {
      setLoading(false);
    }
  };

  const downloadReceiptByNumber = async (receiptNumber) => {
    if (!receiptNumber) {
      alert('Receipt number is required to download PDF.');
      return;
    }

    try {
      setLoading(true);
      console.log('Downloading PDF for receipt number:', receiptNumber);
      
      const response = await api.get(`/payments/receipt/${receiptNumber}/pdf`, {
        responseType: 'blob',
        headers: { 'X-User-Id': user?.id }
      });
      
      if (response.data.size === 0) {
        alert('PDF file is empty. Please try regenerating the receipt.');
        return;
      }
      
      // Create download link
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `receipt-${receiptNumber}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      console.log('Receipt PDF downloaded successfully');
    } catch (error) {
      console.error('Error downloading receipt PDF:', error);
      
      if (error.code === 'NETWORK_ERROR' || !error.response) {
        alert('Network error: Cannot connect to server. Please ensure the backend is running and try again.');
      } else if (error.response?.status === 403) {
        alert('Access denied. You may not have permission to download this receipt.');
      } else if (error.response?.status === 404) {
        alert('Receipt not found. Please contact support.');
      } else {
        alert('Error downloading receipt PDF: ' + (error.response?.data?.message || error.message || 'Please try again'));
      }
    } finally {
      setLoading(false);
    }
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
                  <p className="text-3xl font-bold text-green-600">${totalPaid.toFixed(2)}</p>
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
                  <p className="text-3xl font-bold text-yellow-600">${totalPending.toFixed(2)}</p>
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

        {receipts.length > 0 && (
          <div className="space-y-4 mb-8">
            <h2 className="text-2xl font-bold text-gray-800 mb-4">My Receipts</h2>
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
              {receipts.map((receipt) => (
                <Card key={receipt.id} className="hover:shadow-md transition-shadow">
                  <CardContent className="p-4">
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <h4 className="font-semibold text-sm">{receipt.receiptNumber}</h4>
                        <p className="text-xs text-gray-500">
                          {new Date(receipt.issueDate).toLocaleDateString()}
                        </p>
                      </div>
                      <Badge className="bg-green-100 text-green-800">Receipt</Badge>
                    </div>
                    <div className="space-y-1 mb-3">
                      <p className="text-sm"><strong>Service:</strong> {receipt.serviceDescription}</p>
                      <p className="text-sm"><strong>Doctor:</strong> {receipt.doctorName}</p>
                      <p className="text-lg font-bold text-green-600">${receipt.amount.toFixed(2)}</p>
                    </div>
                    <Button
                      size="sm"
                      onClick={() => downloadReceiptByNumber(receipt.receiptNumber)}
                      className="w-full flex items-center justify-center gap-1"
                    >
                      <Receipt className="w-4 h-4" />
                      Download PDF
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        )}

        {payments.length > 0 && (
          <div className="space-y-4">
            <h2 className="text-2xl font-bold text-gray-800 mb-4">Payment History</h2>
            {payments.map((payment) => (
              <Card key={payment.id}>
                <CardContent className="p-6">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h3 className="text-lg font-semibold">{payment.description || 'Medical Service Payment'}</h3>
                      <div className="flex items-center gap-2 mt-2 mb-2">
                        {getStatusBadge(payment.status)}
                      </div>
                      <p className="text-sm text-gray-500">
                        {payment.paymentDate ? new Date(payment.paymentDate).toLocaleString() : 'Date not available'}
                      </p>
                      {payment.transactionId && (
                        <p className="text-xs text-gray-400 mt-1">
                          Transaction ID: {payment.transactionId}
                        </p>
                      )}
                    </div>
                    <div className="flex flex-col items-end">
                      <p className="text-2xl font-bold text-green-600">
                        ${payment.amount.toFixed(2)}
                      </p>
                      {payment.status === 'COMPLETED' && (
                        <div className="flex gap-2 mt-3">
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => generateReceipt(payment.id)}
                            className="flex items-center gap-1"
                          >
                            <Receipt className="w-4 h-4" />
                            Receipt
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => downloadReceipt(payment.id)}
                            className="flex items-center gap-1"
                          >
                            <DollarSign className="w-4 h-4" />
                            PDF
                          </Button>
                        </div>
                      )}
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Payments;
