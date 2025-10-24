import React, { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Users, 
  FileText, 
  Calendar, 
  Activity, 
  Plus,
  Edit,
  Trash2,
  Search,
  Filter,
  Save,
  X,
  TrendingUp,
  Clock,
  CheckCircle,
  AlertCircle,
  Download,
  User
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Select } from '../components/ui/select';
import { Badge } from '../components/ui/badge';
import { useAuth } from '../hooks/useAuth';
import api from '../api/api';

const DoctorDashboard = () => {
  const { user } = useAuth();
  const [medicalRecords, setMedicalRecords] = useState([]);
  const [myPatients, setMyPatients] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [doctorProfile, setDoctorProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterPatient, setFilterPatient] = useState('');
  
  // Form state for creating/editing medical records
  const [formData, setFormData] = useState({
    patientId: '',
    appointmentId: '',
    diagnosis: '',
    treatment: '',
    prescription: '',
    notes: ''
  });

  const fetchDashboardData = useCallback(async () => {
    setLoading(true);
    try {
      // Fetch doctor profile
      try {
        const doctorResponse = await api.get(`/doctors/${user.id}`);
        setDoctorProfile(doctorResponse.data);
      } catch (err) {
        console.error('Error fetching doctor profile:', err);
      }

      // Fetch appointments for the doctor
      const appointmentsResponse = await fetch(`http://localhost:8080/doctors/${user.id}/appointments`, { headers: { 'X-User-Id': user.id } });
      let appointmentsData = await appointmentsResponse.json();
      if (!Array.isArray(appointmentsData)) {
        console.error('Appointments API error:', appointmentsData);
        appointmentsData = [];
      }
      setAppointments(appointmentsData);

      // Fetch medical records for this doctor using api instance
      let recordsData = [];
      try {
        const recordsResponse = await api.get(`/api/medical-records/doctor/${user.id}`);
        if (Array.isArray(recordsResponse.data)) {
          recordsData = recordsResponse.data;
        } else {
          console.error('Medical Records API error:', recordsResponse.data);
        }
      } catch (err) {
        console.error('Medical Records API error:', err);
      }
      setMedicalRecords(recordsData);

      // Fetch real patient details for all unique patient IDs in appointments
      const patientIds = Array.from(new Set((appointmentsData || []).map(apt => apt.patientId)));
      let patientsMap = {};
      if (patientIds.length > 0) {
        try {
          // Try batch endpoint first
          const resp = await api.get(`/api/patients/batch?ids=${patientIds.join(',')}`);
          if (Array.isArray(resp.data) && resp.data.length === patientIds.length) {
            resp.data.forEach(p => { patientsMap[p.id] = p; });
          } else {
            // fallback: fetch each patient by ID
            await Promise.all(patientIds.map(async id => {
              try {
                const res = await api.get(`/api/patients/${id}`);
                if (res.data && res.data.id) {
                  patientsMap[id] = res.data;
                } else {
                  patientsMap[id] = { id, name: `Patient ${id}` };
                }
              } catch {
                patientsMap[id] = { id, name: `Patient ${id}` };
              }
            }));
          }
        } catch (err) {
          // fallback: fetch each patient by ID
          await Promise.all(patientIds.map(async id => {
            try {
              const res = await api.get(`/api/patients/${id}`);
              if (res.data && res.data.id) {
                patientsMap[id] = res.data;
              } else {
                patientsMap[id] = { id, name: `Patient ${id}` };
              }
            } catch {
              patientsMap[id] = { id, name: `Patient ${id}` };
            }
          }));
        }
      }
      setMyPatients(Object.values(patientsMap));

    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      setAppointments([]);
      setMedicalRecords([]);
      setMyPatients([]);
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  const handleCreateRecord = async (e) => {
    e.preventDefault();
    try {
      // Prepare payload: remove doctorId, and remove appointmentId if empty
      const payload = { ...formData };
      delete payload.doctorId; // never send doctorId from frontend
      if (!payload.appointmentId) delete payload.appointmentId;
      if (!payload.patientId) {
        alert('Please select a patient.');
        return;
      }
      const response = await api.post('/medical-records', payload);
      // Handle both response types: {success, message, record} or direct record object
      if (response.data && (response.data.success || response.data.record)) {
        alert(response.data.message || 'Medical record created successfully!');
        setShowCreateModal(false);
        resetForm();
        fetchDashboardData();
      } else if (response.data && response.data.id) {
        alert('Medical record created successfully!');
        setShowCreateModal(false);
        resetForm();
        fetchDashboardData();
      } else {
        alert(response.data && response.data.message ? response.data.message : 'Unknown error creating medical record');
      }
    } catch (error) {
      console.error('Error creating medical record:', error);
      alert('Error creating medical record');
    }
  };

  const handleEditRecord = async (e) => {
    e.preventDefault();
    try {
      const response = await api.put(`/medical-records/${selectedRecord.id}`, formData);
      if (response.data.success) {
        alert('Medical record updated successfully!');
        setShowEditModal(false);
        resetForm();
        fetchDashboardData();
      } else {
        alert(response.data.message);
      }
    } catch (error) {
      console.error('Error updating medical record:', error);
      alert('Error updating medical record');
    }
  };

  const handleDeleteRecord = async (recordId) => {
    if (window.confirm('Are you sure you want to delete this medical record?')) {
      try {
        const response = await api.delete(`/medical-records/${recordId}`);
        if (response.data.success) {
          alert('Medical record deleted successfully!');
          fetchDashboardData();
        } else {
          alert(response.data.message);
        }
      } catch (error) {
        console.error('Error deleting medical record:', error);
        alert('Error deleting medical record');
      }
    }
  };

  const openEditModal = (record) => {
    setSelectedRecord(record);
    setFormData({
      patientId: record.patientId,
      appointmentId: record.appointmentId || '',
      diagnosis: record.diagnosis || '',
      treatment: record.treatment || '',
      prescription: record.prescription || '',
      notes: record.notes || ''
    });
    setShowEditModal(true);
  };

  const resetForm = () => {
    setFormData({
      patientId: '',
      appointmentId: '',
      diagnosis: '',
      treatment: '',
      prescription: '',
      notes: ''
    });
    setSelectedRecord(null);
  };

  const getPatientName = (patientId) => {
    const patient = myPatients.find(p => p.id === patientId);
    return patient ? patient.name : `Patient ${patientId}`;
  };
  const getPatientPhoto = (patientId) => {
    const patient = myPatients.find(p => p.id === patientId);
    return patient && patient.profilePicture ? patient.profilePicture : null;
  };

  const filteredRecords = medicalRecords.filter(record => {
    const matchesSearch = searchTerm === '' || 
      record.diagnosis?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.treatment?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      record.prescription?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      getPatientName(record.patientId).toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesFilter = filterPatient === '' || record.patientId.toString() === filterPatient;
    
    return matchesSearch && matchesFilter;
  });

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50 flex items-center justify-center pt-16">
        <motion.div
          className="text-center"
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.5 }}
        >
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
            className="w-20 h-20 border-4 border-[#4CAF50] border-t-transparent rounded-full mx-auto mb-4"
          />
          <p className="text-gray-600 font-medium">Loading dashboard...</p>
        </motion.div>
      </div>
    );
  }

  // Calculate statistics
  const totalPatients = myPatients.length;
  const totalRecords = medicalRecords.length;
  const todayAppointments = appointments.filter(apt => {
    const aptDate = new Date(apt.appointmentDate);
    const today = new Date();
    return aptDate.toDateString() === today.toDateString();
  }).length;
  const completedAppointments = appointments.filter(apt => apt.status === 'COMPLETED').length;

  // Stats card data (all green theme)
  const statsData = [
    {
      title: 'Total Patients',
      value: totalPatients,
      icon: Users,
      gradient: 'from-green-500 to-green-600',
      bgGradient: 'from-green-50 to-green-100',
      change: '+12%',
      changeType: 'increase'
    },
    {
      title: 'Medical Records',
      value: totalRecords,
      icon: FileText,
      gradient: 'from-green-400 to-green-600',
      bgGradient: 'from-green-50 to-green-100',
      change: '+8%',
      changeType: 'increase'
    },
    {
      title: "Today's Appointments",
      value: todayAppointments,
      icon: Calendar,
      gradient: 'from-green-500 to-green-700',
      bgGradient: 'from-green-50 to-green-100',
      change: `${todayAppointments} scheduled`,
      changeType: 'neutral'
    },
    {
      title: 'Completed',
      value: completedAppointments,
      icon: CheckCircle,
      gradient: 'from-green-400 to-green-700',
      bgGradient: 'from-green-50 to-green-100',
      change: `${appointments.length} total`,
      changeType: 'neutral'
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50 pt-24 pb-12 px-4">
      <div className="container mx-auto max-w-7xl">
        {/* Header Section */}
        <motion.div 
          initial={{ opacity: 0, y: -20 }} 
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="mb-8"
        >
          <div className="flex flex-col md:flex-row md:items-center md:justify-between">
            <div>
              <h1 className="text-4xl md:text-5xl font-bold bg-gradient-to-r from-blue-600 to-green-600 bg-clip-text text-transparent mb-2">
                Welcome Back, Dr. {user.name}
              </h1>
              <p className="text-gray-600 text-lg flex items-center gap-2">
                <Activity className="w-5 h-5" />
                Dashboard Overview
              </p>
            </div>
            <motion.div
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.2 }}
              className="mt-4 md:mt-0"
            >
              <Button
                className="bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white shadow-lg hover:shadow-xl transition-all duration-300 flex items-center gap-2"
                onClick={() => {
                  // Export medical records as CSV
                  if (!medicalRecords.length) {
                    alert('No medical records to export.');
                    return;
                  }
                  const csvHeaders = ['Patient Name','Diagnosis','Treatment','Prescription','Notes'];
                  const csvRows = medicalRecords.map(r => [
                    JSON.stringify(getPatientName(r.patientId)),
                    JSON.stringify(r.diagnosis || ''),
                    JSON.stringify(r.treatment || ''),
                    JSON.stringify(r.prescription || ''),
                    JSON.stringify(r.notes || '')
                  ].join(','));
                  const csvContent = [csvHeaders.join(','), ...csvRows].join('\r\n');
                  const blob = new Blob([csvContent], { type: 'text/csv' });
                  const url = URL.createObjectURL(blob);
                  const a = document.createElement('a');
                  a.href = url;
                  a.download = 'medical_records_report.csv';
                  document.body.appendChild(a);
                  a.click();
                  document.body.removeChild(a);
                  URL.revokeObjectURL(url);
                }}
              >
                <Download className="w-4 h-4" />
                Export Report
              </Button>
            </motion.div>
          </div>
        </motion.div>

        {/* Doctor Profile Section */}
        {doctorProfile && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="mb-8"
          >
            <Card className="shadow-xl border-0 bg-white/80 backdrop-blur-sm overflow-hidden">
              <div className="bg-gradient-to-r from-green-500 to-blue-500 h-32"></div>
              <div className="px-6 pb-6 relative">
                <div className="flex flex-col md:flex-row items-center md:items-start gap-6 -mt-16">
                  <div className="w-32 h-32 rounded-full border-4 border-white shadow-xl bg-gradient-to-r from-green-400 to-green-600 flex items-center justify-center text-white text-4xl font-bold">
                    {doctorProfile.name?.charAt(0) || 'D'}
                  </div>
                  <div className="flex-1 text-center md:text-left mt-4 md:mt-8">
                    <h2 className="text-3xl font-bold text-gray-800">{doctorProfile.name}</h2>
                    <p className="text-xl text-green-600 font-semibold mt-1">{doctorProfile.specialization}</p>
                    <div className="flex flex-wrap justify-center md:justify-start gap-4 mt-4 text-gray-600">
                      <div className="flex items-center gap-2">
                        <Users className="w-5 h-5 text-green-600" />
                        <span>{medicalRecords.length} Total Records</span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Calendar className="w-5 h-5 text-green-600" />
                        <span>{appointments.length} Appointments</span>
                      </div>
                      {doctorProfile.email && (
                        <div className="flex items-center gap-2">
                          <span className="text-gray-500">📧</span>
                          <span>{doctorProfile.email}</span>
                        </div>
                      )}
                      {doctorProfile.phone && (
                        <div className="flex items-center gap-2">
                          <span className="text-gray-500">📱</span>
                          <span>{doctorProfile.phone}</span>
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="flex items-center gap-2 mt-4 md:mt-8">
                    <Badge className={`${doctorProfile.available ? 'bg-green-500' : 'bg-gray-500'} text-white px-4 py-2 text-sm font-semibold`}>
                      {doctorProfile.available ? '● Available' : '● Unavailable'}
                    </Badge>
                  </div>
                </div>
              </div>
            </Card>
          </motion.div>
        )}

        {/* Stats Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {statsData.map((stat, index) => {
            const Icon = stat.icon;
            return (
              <motion.div
                key={stat.title}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1, duration: 0.5 }}
                whileHover={{ y: -5, transition: { duration: 0.2 } }}
              >
                <Card className={`relative overflow-hidden border-0 shadow-lg hover:shadow-2xl transition-all duration-300 bg-gradient-to-br ${stat.bgGradient}`}>
                  {/* Decorative background pattern */}
                  <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
                    <svg viewBox="0 0 100 100" className="w-full h-full">
                      <circle cx="50" cy="50" r="40" fill="currentColor" />
                    </svg>
                  </div>
                  <CardContent className="p-6 relative z-10">
                    <div className="flex items-start justify-between mb-4">
                      <div className={`p-3 rounded-xl bg-gradient-to-r ${stat.gradient} shadow-lg`}>
                        <Icon className="w-6 h-6 text-white" />
                      </div>
                      <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        transition={{ delay: 0.5 + index * 0.1 }}
                      >
                        {stat.changeType === 'increase' ? (
                          <div className="flex items-center gap-1 text-green-600 text-sm font-medium">
                            <TrendingUp className="w-4 h-4" />
                            {stat.change}
                          </div>
                        ) : (
                          <div className="flex items-center gap-1 text-green-700 text-sm font-medium">
                            <Clock className="w-4 h-4" />
                            {stat.change}
                          </div>
                        )}
                      </motion.div>
                    </div>
                    <h3 className="text-sm font-medium text-green-700 mb-1">{stat.title}</h3>
                    <p className="text-3xl font-bold text-green-900">{stat.value}</p>
                  </CardContent>
                </Card>
              </motion.div>
            );
          })}
        </div>

        {/* Manage Medical Records Section */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <Card className="shadow-xl border-0 bg-white/80 backdrop-blur-sm">
            <CardHeader className="border-b border-gray-100 bg-gradient-to-r from-blue-50 to-green-50">
              <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                <div>
                  <CardTitle className="text-2xl font-bold text-gray-800 flex items-center gap-2">
                    <FileText className="w-6 h-6 text-blue-600" />
                    Medical Records Management
                  </CardTitle>
                  <p className="text-sm text-gray-600 mt-1">Manage and track all patient medical records</p>
                </div>
                <Button 
                  className="bg-gradient-to-r from-blue-500 to-green-500 hover:from-blue-600 hover:to-green-600 text-white shadow-lg hover:shadow-xl transition-all duration-300 flex items-center gap-2"
                  onClick={() => { setShowCreateModal(true); resetForm(); }}
                >
                  <Plus className="w-4 h-4" />
                  Add New Record
                </Button>
              </div>
            </CardHeader>

            {/* Search and Filter Bar */}
            <div className="p-6 bg-gray-50 border-b border-gray-100">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <Input
                    type="text"
                    placeholder="Search by diagnosis, treatment, or patient..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-10 border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 rounded-lg shadow-sm"
                  />
                </div>
                <div className="relative">
                  <Filter className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <Select
                    value={filterPatient}
                    onChange={(e) => setFilterPatient(e.target.value)}
                    className="pl-10 border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 rounded-lg shadow-sm"
                  >
                    <option value="">All Patients</option>
                    {myPatients.map(patient => (
                      <option key={patient.id} value={patient.id.toString()}>
                        {patient.name}
                      </option>
                    ))}
                  </Select>
                </div>
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <AlertCircle className="w-4 h-4" />
                  {filteredRecords.length} record{filteredRecords.length !== 1 ? 's' : ''} found
                </div>
              </div>
            </div>

            <CardContent className="p-6">
              <AnimatePresence mode="wait">
                {filteredRecords.length > 0 ? (
                  <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    className="overflow-x-auto rounded-lg border border-gray-200"
                  >
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gradient-to-r from-gray-50 to-gray-100">
                        <tr>
                          {['Patient', 'Diagnosis', 'Treatment', 'Prescription', 'Notes', 'Actions'].map((header) => (
                            <th 
                              key={header}
                              className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider"
                            >
                              {header}
                            </th>
                          ))}
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        <AnimatePresence>
                          {filteredRecords.map((record, index) => (
                            <motion.tr 
                              key={record.id}
                              initial={{ opacity: 0, x: -20 }}
                              animate={{ opacity: 1, x: 0 }}
                              exit={{ opacity: 0, x: 20 }}
                              transition={{ delay: index * 0.05 }}
                              className="hover:bg-blue-50 transition-colors duration-200"
                            >
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex items-center">
                                  <div className="w-10 h-10 rounded-full bg-gradient-to-r from-blue-400 to-green-400 flex items-center justify-center text-white font-semibold mr-3">
                                    {getPatientName(record.patientId).charAt(0)}
                                  </div>
                                  <div>
                                    <div className="font-medium text-gray-900">{getPatientName(record.patientId)}</div>
                                    <div className="text-sm text-gray-500">ID: {record.patientId}</div>
                                  </div>
                                </div>
                              </td>
                              <td className="px-6 py-4">
                                <div className="text-sm font-medium text-gray-900 max-w-xs truncate">{record.diagnosis}</div>
                              </td>
                              <td className="px-6 py-4">
                                <div className="text-sm text-gray-700 max-w-xs truncate">{record.treatment || 'N/A'}</div>
                              </td>
                              <td className="px-6 py-4">
                                <div className="text-sm text-gray-700 max-w-xs truncate">{record.prescription || 'N/A'}</div>
                              </td>
                              <td className="px-6 py-4">
                                <div className="text-sm text-gray-500 max-w-xs truncate">{record.notes || 'N/A'}</div>
                              </td>
                              <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex gap-2">
                                  <motion.button
                                    whileHover={{ scale: 1.05 }}
                                    whileTap={{ scale: 0.95 }}
                                    onClick={() => openEditModal(record)}
                                    className="p-2 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors duration-200"
                                    title="Edit"
                                  >
                                    <Edit className="w-4 h-4" />
                                  </motion.button>
                                  <motion.button
                                    whileHover={{ scale: 1.05 }}
                                    whileTap={{ scale: 0.95 }}
                                    onClick={() => handleDeleteRecord(record.id)}
                                    className="p-2 text-red-600 hover:bg-red-100 rounded-lg transition-colors duration-200"
                                    title="Delete"
                                  >
                                    <Trash2 className="w-4 h-4" />
                                  </motion.button>
                                </div>
                              </td>
                            </motion.tr>
                          ))}
                        </AnimatePresence>
                      </tbody>
                    </table>
                  </motion.div>
                ) : (
                  <motion.div
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.9 }}
                    className="text-center py-16"
                  >
                    <div className="w-24 h-24 mx-auto mb-4 bg-gradient-to-br from-gray-100 to-gray-200 rounded-full flex items-center justify-center">
                      <FileText className="w-12 h-12 text-gray-400" />
                    </div>
                    <h3 className="text-lg font-semibold text-gray-700 mb-2">No medical records found</h3>
                    <p className="text-gray-500 mb-4">
                      {searchTerm || filterPatient ? 'Try adjusting your search or filter' : 'Create your first medical record to get started'}
                    </p>
                    {!searchTerm && !filterPatient && (
                      <Button 
                        onClick={() => { setShowCreateModal(true); resetForm(); }}
                        className="bg-gradient-to-r from-blue-500 to-green-500 hover:from-blue-600 hover:to-green-600 text-white"
                      >
                        <Plus className="w-4 h-4 mr-2" />
                        Add First Record
                      </Button>
                    )}
                  </motion.div>
                )}
              </AnimatePresence>
            </CardContent>
          </Card>
        </motion.div>

        {/* My Appointments Section - Table View */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
          className="mt-8"
        >
          <Card className="shadow-xl border-0 bg-white/80 backdrop-blur-sm">
            <CardHeader className="border-b border-gray-100 bg-gradient-to-r from-green-50 to-blue-50">
              <CardTitle className="text-2xl font-bold text-gray-800 flex items-center gap-2">
                <Calendar className="w-6 h-6 text-green-600" />
                My Appointments
              </CardTitle>
            </CardHeader>
            <CardContent className="p-6">
              <AnimatePresence mode="wait">
                {appointments.length > 0 ? (
                  <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    className="overflow-x-auto"
                  >
                    <table className="w-full">
                      <thead className="bg-gray-50">
                        <tr>
                          <th className="text-left p-4 font-semibold text-gray-700 border-b">Patient ID</th>
                          <th className="text-left p-4 font-semibold text-gray-700 border-b">Patient Name</th>
                          <th className="text-left p-4 font-semibold text-gray-700 border-b">Date</th>
                          <th className="text-left p-4 font-semibold text-gray-700 border-b">Time</th>
                          <th className="text-left p-4 font-semibold text-gray-700 border-b">Status</th>
                        </tr>
                      </thead>
                      <tbody>
                        <AnimatePresence>
                          {appointments.map((appointment, index) => {
                            const patientName = getPatientName(appointment.patientId);
                            const appointmentDate = appointment.appointmentDate 
                              ? new Date(appointment.appointmentDate).toISOString().split('T')[0]
                              : 'Not scheduled';
                            const appointmentTime = appointment.appointmentDate
                              ? new Date(appointment.appointmentDate).toLocaleTimeString('en-US', { 
                                  hour: '2-digit', 
                                  minute: '2-digit',
                                  hour12: false 
                                })
                              : '--:--';
                            
                            return (
                              <motion.tr
                                key={appointment.id}
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                exit={{ opacity: 0, x: 20 }}
                                transition={{ delay: index * 0.05 }}
                                className="border-b hover:bg-gray-50 transition-colors"
                              >
                                <td className="p-4 text-gray-700">{appointment.patientId}</td>
                                <td className="p-4 text-gray-700 font-medium">{patientName}</td>
                                <td className="p-4 text-gray-700">{appointmentDate}</td>
                                <td className="p-4 text-gray-700">{appointmentTime}</td>
                                <td className="p-4">
                                  <Badge 
                                    className={`
                                      ${appointment.status === 'COMPLETED' 
                                        ? 'bg-green-500 hover:bg-green-600 text-white' 
                                        : appointment.status === 'CONFIRMED' || appointment.status === 'SCHEDULED'
                                        ? 'bg-green-500 hover:bg-green-600 text-white'
                                        : appointment.status === 'CANCELLED'
                                        ? 'bg-gray-500 hover:bg-gray-600 text-white'
                                        : appointment.status === 'PENDING'
                                        ? 'bg-red-500 hover:bg-red-600 text-white'
                                        : 'bg-gray-400 hover:bg-gray-500 text-white'
                                      } font-semibold px-4 py-1 rounded-full`}
                                  >
                                    {appointment.status}
                                  </Badge>
                                </td>
                              </motion.tr>
                            );
                          })}
                        </AnimatePresence>
                      </tbody>
                    </table>
                  </motion.div>
                ) : (
                  <motion.div
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.9 }}
                    className="text-center py-16"
                  >
                    <div className="w-24 h-24 mx-auto mb-4 bg-gradient-to-br from-gray-100 to-gray-200 rounded-full flex items-center justify-center">
                      <Calendar className="w-12 h-12 text-gray-400" />
                    </div>
                    <h3 className="text-lg font-semibold text-gray-700 mb-2">No appointments scheduled</h3>
                    <p className="text-gray-500">Your appointment schedule is clear</p>
                  </motion.div>
                )}
              </AnimatePresence>
            </CardContent>
          </Card>
        </motion.div>

        {/* Create Medical Record Modal */}
        <AnimatePresence>
          {showCreateModal && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4"
              onClick={() => { setShowCreateModal(false); resetForm(); }}
            >
              <motion.div
                initial={{ scale: 0.9, y: 20 }}
                animate={{ scale: 1, y: 0 }}
                exit={{ scale: 0.9, y: 20 }}
                transition={{ type: "spring", duration: 0.5 }}
                className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] overflow-hidden"
                onClick={(e) => e.stopPropagation()}
              >
                {/* Modal Header */}
                <div className="bg-gradient-to-r from-blue-500 to-green-500 p-6 text-white">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center">
                        <Plus className="w-6 h-6" />
                      </div>
                      <div>
                        <h2 className="text-2xl font-bold">Create Medical Record</h2>
                        <p className="text-blue-100 text-sm">Add a new patient medical record</p>
                      </div>
                    </div>
                    <motion.button
                      whileHover={{ scale: 1.1, rotate: 90 }}
                      whileTap={{ scale: 0.9 }}
                      onClick={() => { setShowCreateModal(false); resetForm(); }}
                      className="p-2 hover:bg-white/20 rounded-lg transition-colors"
                    >
                      <X className="w-6 h-6" />
                    </motion.button>
                  </div>
                </div>

                {/* Modal Body */}
                <div className="p-6 overflow-y-auto max-h-[calc(90vh-180px)]">
                  <form onSubmit={handleCreateRecord} className="space-y-5">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                      {/* Patient Selection */}
                      <div className="md:col-span-2">
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          Patient <span className="text-red-500">*</span>
                        </label>
                        <div className="relative">
                          <Users className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                          <Select
                            value={formData.patientId}
                            onChange={(e) => setFormData({...formData, patientId: e.target.value})}
                            required
                            className="pl-10 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all"
                          >
                            <option value="">Select Patient</option>
                            {myPatients.map(patient => (
                              <option key={patient.id} value={patient.id}>
                                {patient.name}
                              </option>
                            ))}
                          </Select>
                        </div>
                      </div>

                      {/* Appointment Selection */}
                      <div className="md:col-span-2">
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          Appointment <span className="text-gray-400">(Optional)</span>
                        </label>
                        <div className="relative">
                          <Calendar className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
                          <Select
                            value={formData.appointmentId}
                            onChange={(e) => setFormData({...formData, appointmentId: e.target.value})}
                            className="pl-10 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all"
                          >
                            <option value="">No specific appointment</option>
                            {appointments.map(apt => (
                              <option key={apt.id} value={apt.id}>
                                {getPatientName(apt.patientId)} - {apt.appointmentDate ? new Date(apt.appointmentDate).toLocaleString() : ''}
                              </option>
                            ))}
                          </Select>
                        </div>
                      </div>
                    </div>

                    {/* Diagnosis */}
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">
                        Diagnosis <span className="text-red-500">*</span>
                      </label>
                      <textarea
                        value={formData.diagnosis}
                        onChange={(e) => setFormData({...formData, diagnosis: e.target.value})}
                        className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all resize-none"
                        rows="3"
                        required
                        placeholder="Enter patient diagnosis..."
                      />
                    </div>

                    {/* Treatment */}
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-2">
                        Treatment
                      </label>
                      <textarea
                        value={formData.treatment}
                        onChange={(e) => setFormData({...formData, treatment: e.target.value})}
                        className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all resize-none"
                        rows="3"
                        placeholder="Enter treatment plan..."
                      />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                      {/* Prescription */}
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          Prescription
                        </label>
                        <textarea
                          value={formData.prescription}
                          onChange={(e) => setFormData({...formData, prescription: e.target.value})}
                          className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all resize-none"
                          rows="2"
                          placeholder="Enter prescription..."
                        />
                      </div>

                      {/* Notes */}
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          Notes
                        </label>
                        <textarea
                          value={formData.notes}
                          onChange={(e) => setFormData({...formData, notes: e.target.value})}
                          className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all resize-none"
                          rows="2"
                          placeholder="Additional notes..."
                        />
                      </div>
                    </div>
                  </form>
                </div>

                {/* Modal Footer */}
                <div className="border-t border-gray-200 p-6 bg-gray-50 flex gap-3 justify-end">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => { setShowCreateModal(false); resetForm(); }}
                    className="px-6 border-2 border-gray-300 hover:bg-gray-100"
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    onClick={handleCreateRecord}
                    className="px-6 bg-gradient-to-r from-blue-500 to-green-500 hover:from-blue-600 hover:to-green-600 text-white shadow-lg hover:shadow-xl flex items-center gap-2"
                  >
                    <Save className="w-4 h-4" />
                    Create Record
                  </Button>
                </div>
              </motion.div>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Edit Medical Record Modal */}
        <AnimatePresence>
          {showEditModal && (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4"
              onClick={() => { setShowEditModal(false); resetForm(); }}
            >
              <motion.div
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
                onClick={(e) => e.stopPropagation()}
                className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
              >
                {/* Modal Header */}
                <div className="sticky top-0 bg-gradient-to-r from-blue-500 to-green-500 text-white p-6 rounded-t-2xl flex items-center justify-between shadow-lg">
                  <div className="flex items-center gap-3">
                    <Edit className="w-6 h-6" />
                    <h2 className="text-2xl font-bold">Edit Medical Record</h2>
                  </div>
                  <button
                    onClick={() => { setShowEditModal(false); resetForm(); }}
                    className="hover:bg-white/20 p-2 rounded-full transition-colors"
                  >
                    <X className="w-6 h-6" />
                  </button>
                </div>

                {/* Modal Body */}
                <div className="p-6">
                  <form className="space-y-6">
                    <div className="grid grid-cols-1 gap-6">
                      {/* Patient Selection */}
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          <User className="w-4 h-4 inline mr-2" />
                          Patient
                        </label>
                        <select
                          value={formData.patientId}
                          onChange={(e) => setFormData({...formData, patientId: e.target.value})}
                          className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all"
                        >
                          <option value="">Select Patient</option>
                          {myPatients.map(patient => (
                            <option key={patient.id} value={patient.id}>
                              {patient.name}
                            </option>
                          ))}
                        </select>
                      </div>

                      {/* Appointment Selection */}
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          <Calendar className="w-4 h-4 inline mr-2" />
                          Appointment
                        </label>
                        <select
                          value={formData.appointmentId}
                          onChange={(e) => setFormData({...formData, appointmentId: e.target.value})}
                          className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all"
                        >
                          <option value="">Select Appointment</option>
                          {appointments.map(apt => (
                            <option key={apt.id} value={apt.id}>
                              {apt.patientName} - {apt.appointmentDate} {apt.appointmentTime}
                            </option>
                          ))}
                        </select>
                      </div>

                      {/* Diagnosis */}
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          <FileText className="w-4 h-4 inline mr-2" />
                          Diagnosis
                        </label>
                        <textarea
                          value={formData.diagnosis}
                          onChange={(e) => setFormData({...formData, diagnosis: e.target.value})}
                          className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all resize-none"
                          rows="3"
                          placeholder="Enter diagnosis..."
                        />
                      </div>

                      {/* Treatment */}
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          Treatment Plan
                        </label>
                        <input
                          type="text"
                          value={formData.treatment}
                          onChange={(e) => setFormData({...formData, treatment: e.target.value})}
                          className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all"
                          placeholder="Treatment plan..."
                        />
                      </div>

                      {/* Prescription */}
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          Prescription
                        </label>
                        <input
                          type="text"
                          value={formData.prescription}
                          onChange={(e) => setFormData({...formData, prescription: e.target.value})}
                          className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all"
                          placeholder="Prescription details..."
                        />
                      </div>

                      {/* Notes */}
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-2">
                          Notes
                        </label>
                        <textarea
                          value={formData.notes}
                          onChange={(e) => setFormData({...formData, notes: e.target.value})}
                          className="w-full p-3 border-2 border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200 rounded-lg transition-all resize-none"
                          rows="2"
                          placeholder="Additional notes..."
                        />
                      </div>
                    </div>
                  </form>
                </div>

                {/* Modal Footer */}
                <div className="border-t border-gray-200 p-6 bg-gray-50 flex gap-3 justify-end">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => { setShowEditModal(false); resetForm(); }}
                    className="px-6 border-2 border-gray-300 hover:bg-gray-100"
                  >
                    Cancel
                  </Button>
                  <Button
                    type="submit"
                    onClick={handleEditRecord}
                    className="px-6 bg-gradient-to-r from-blue-500 to-green-500 hover:from-blue-600 hover:to-green-600 text-white shadow-lg hover:shadow-xl flex items-center gap-2"
                  >
                    <Save className="w-4 h-4" />
                    Update Record
                  </Button>
                </div>
              </motion.div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
};

export default DoctorDashboard;
