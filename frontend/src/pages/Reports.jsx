import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { FileText, Activity, Pill, FlaskConical, Calendar, Download, AlertCircle } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { useAuth } from '../context/AuthContext';
import api from '../api/api';

const Reports = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [summary, setSummary] = useState(null);
  const [medicalRecords, setMedicalRecords] = useState([]);
  const [prescriptions, setPrescriptions] = useState([]);
  const [labResults, setLabResults] = useState([]);
  const [activeTab, setActiveTab] = useState('summary');

  useEffect(() => {
    fetchReports();
  }, []);

  const fetchReports = async () => {
    setLoading(true);
    try {
      // Fetch summary
      const summaryResponse = await api.get('/reports/summary');
      setSummary(summaryResponse.data);

      // Fetch medical records
      const recordsResponse = await api.get('/reports/medical-records');
      setMedicalRecords(recordsResponse.data);

      // Fetch prescriptions
      const prescriptionsResponse = await api.get('/reports/prescriptions');
      setPrescriptions(prescriptionsResponse.data);

      // Fetch lab results
      const labResponse = await api.get('/reports/lab-results');
      setLabResults(labResponse.data);
    } catch (error) {
      console.error('Error fetching reports:', error);
    } finally {
      setLoading(false);
    }
  };

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
      <div className="container mx-auto max-w-7xl">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div>
              <h1 className="text-4xl font-bold text-gray-800 mb-2">
                Hospital Reports & Analytics
              </h1>
              <p className="text-gray-600">
                Overview of hospital performance and statistics
              </p>
            </div>
            <Button className="bg-[#4CAF50] hover:bg-[#45a049]">
              <Download className="w-5 h-5 mr-2" />
              Export Report
            </Button>
          </div>
        </motion.div>

        {/* Stats Cards */}
        <div className="grid md:grid-cols-4 gap-6 mb-8">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
          >
            <Card hover>
              <CardContent className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                    <Users className="w-6 h-6 text-blue-600" />
                  </div>
                  <TrendingUp className="w-5 h-5 text-green-600" />
                </div>
                <p className="text-sm text-gray-600 mb-1">Total Patients</p>
                <p className="text-3xl font-bold text-gray-800">
                  {stats.totalPatients.toLocaleString()}
                </p>
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
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                    <Calendar className="w-6 h-6 text-green-600" />
                  </div>
                  <TrendingUp className="w-5 h-5 text-green-600" />
                </div>
                <p className="text-sm text-gray-600 mb-1">Total Appointments</p>
                <p className="text-3xl font-bold text-gray-800">
                  {stats.totalAppointments.toLocaleString()}
                </p>
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
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
                    <BarChart3 className="w-6 h-6 text-purple-600" />
                  </div>
                  <TrendingUp className="w-5 h-5 text-green-600" />
                </div>
                <p className="text-sm text-gray-600 mb-1">Total Revenue</p>
                <p className="text-3xl font-bold text-gray-800">
                  ${(stats.totalRevenue / 1000).toFixed(0)}K
                </p>
              </CardContent>
            </Card>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
          >
            <Card hover>
              <CardContent className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div className="w-12 h-12 bg-orange-100 rounded-full flex items-center justify-center">
                    <TrendingUp className="w-6 h-6 text-orange-600" />
                  </div>
                  <TrendingUp className="w-5 h-5 text-green-600" />
                </div>
                <p className="text-sm text-gray-600 mb-1">Growth Rate</p>
                <p className="text-3xl font-bold text-gray-800">
                  +{stats.growthRate}%
                </p>
              </CardContent>
            </Card>
          </motion.div>
        </div>

        {/* Charts Row 1 */}
        <div className="grid lg:grid-cols-2 gap-6 mb-6">
          {/* Appointments Trend */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
          >
            <Card>
              <CardHeader>
                <CardTitle>Appointments Trend</CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={appointmentData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                    <XAxis dataKey="month" stroke="#666" />
                    <YAxis stroke="#666" />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="appointments" fill="#4CAF50" radius={[8, 8, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </motion.div>

          {/* Revenue Trend */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.6 }}
          >
            <Card>
              <CardHeader>
                <CardTitle>Revenue Trend</CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={revenueData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                    <XAxis dataKey="month" stroke="#666" />
                    <YAxis stroke="#666" />
                    <Tooltip />
                    <Legend />
                    <Line
                      type="monotone"
                      dataKey="revenue"
                      stroke="#4CAF50"
                      strokeWidth={3}
                      dot={{ fill: '#4CAF50', r: 6 }}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </motion.div>
        </div>

        {/* Charts Row 2 */}
        <div className="grid lg:grid-cols-2 gap-6">
          {/* Specialization Distribution */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7 }}
          >
            <Card>
              <CardHeader>
                <CardTitle>Appointments by Specialization</CardTitle>
              </CardHeader>
              <CardContent>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={specializationData}
                      cx="50%"
                      cy="50%"
                      labelLine={false}
                      label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                      outerRadius={100}
                      fill="#8884d8"
                      dataKey="value"
                    >
                      {specializationData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </motion.div>

          {/* Top Performing Departments */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.8 }}
          >
            <Card>
              <CardHeader>
                <CardTitle>Top Performing Departments</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {specializationData
                    .sort((a, b) => b.value - a.value)
                    .map((dept, index) => (
                      <motion.div
                        key={dept.name}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: 0.8 + index * 0.1 }}
                        className="flex items-center gap-4"
                      >
                        <div className="flex-shrink-0 w-8 h-8 bg-[#4CAF50] text-white rounded-full flex items-center justify-center font-bold">
                          {index + 1}
                        </div>
                        <div className="flex-1">
                          <div className="flex items-center justify-between mb-1">
                            <span className="font-medium text-gray-800">{dept.name}</span>
                            <span className="text-sm text-gray-600">{dept.value} patients</span>
                          </div>
                          <div className="w-full bg-gray-200 rounded-full h-2">
                            <motion.div
                              initial={{ width: 0 }}
                              animate={{ width: `${(dept.value / specializationData[0].value) * 100}%` }}
                              transition={{ delay: 0.8 + index * 0.1, duration: 0.5 }}
                              className="bg-[#4CAF50] h-2 rounded-full"
                            />
                          </div>
                        </div>
                      </motion.div>
                    ))}
                </div>
              </CardContent>
            </Card>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default Reports;
