import React, { useState, useEffect, useCallback } from 'react';
import { motion } from 'framer-motion';
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
  X
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
      // Always send X-User-Id header via api instance
      // Fetch appointments for the doctor (still using fetch, as endpoint is not under /api)
      const appointmentsResponse = await fetch(`http://localhost:8080/doctors/${user.id}/appointments`, { headers: { 'X-User-Id': user.id } });
      let appointmentsData = await appointmentsResponse.json();
      if (!Array.isArray(appointmentsData)) {
        // If backend returns error object, show error and fallback
        console.error('Appointments API error:', appointmentsData);
        appointmentsData = [];
      }
      setAppointments(appointmentsData);

      // Fetch medical records for this doctor using api instance
      let recordsData = [];
      try {
        // Debug: log headers for this request
        const debugHeaders = await api.getUri({ url: `/api/medical-records/doctor/${user.id}` });
        console.log('DEBUG: Medical records request URI:', debugHeaders);
        console.log('DEBUG: User ID for header:', user.id);
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

      // Extract unique patients from appointments
      const patientIds = new Set((appointmentsData || []).map(apt => apt.patientId));
      // Mock patients for now
      const mockPatients = Array.from(patientIds).map(id => ({
        id: id,
        name: `Patient ${id}`
      }));
      setMyPatients(mockPatients);

    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      // Fallback to mock data
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
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="mb-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">Doctor Dashboard</h1>
          <p className="text-gray-600">Welcome back, Dr. {user.name}</p>
        </motion.div>

        {/* Stats Cards */}
        <div className="grid md:grid-cols-4 gap-6 mb-8">
          {/* ...existing code for stats cards... */}
        </div>

        {/* Manage Medical Records Section */}
        <Card className="mt-8 mb-8">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Manage Medical Records</CardTitle>
            <Button className="bg-[#4CAF50] hover:bg-[#45a049] flex items-center gap-2" onClick={() => { setShowCreateModal(true); resetForm(); }}>
              <Plus className="w-4 h-4" /> Add Medical Record
            </Button>
          </CardHeader>
          <CardContent>
            {filteredRecords.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead>
                    <tr>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Patient</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Diagnosis</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Treatment</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Prescription</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Notes</th>
                      <th className="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {filteredRecords.map(record => (
                      <tr key={record.id}>
                        <td className="px-4 py-2 whitespace-nowrap">{getPatientName(record.patientId)}</td>
                        <td className="px-4 py-2 whitespace-nowrap">{record.diagnosis}</td>
                        <td className="px-4 py-2 whitespace-nowrap">{record.treatment}</td>
                        <td className="px-4 py-2 whitespace-nowrap">{record.prescription}</td>
                        <td className="px-4 py-2 whitespace-nowrap">{record.notes}</td>
                        <td className="px-4 py-2 whitespace-nowrap flex gap-2">
                          <Button size="sm" variant="outline" onClick={() => openEditModal(record)}><Edit className="w-4 h-4" /></Button>
                          <Button size="sm" variant="destructive" onClick={() => handleDeleteRecord(record.id)}><Trash2 className="w-4 h-4" /></Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="text-center py-8 text-gray-500">
                <FileText className="w-12 h-12 mx-auto mb-4 opacity-50" />
                <p>No medical records found</p>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Appointments Section */}
        <Card className="mt-8">
          <CardHeader>
            <CardTitle>Today's Appointments</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {appointments.length > 0 ? (
                appointments.map((appointment) => (
                  <div key={appointment.id} className="border border-gray-200 rounded-lg p-4">
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <h3 className="font-semibold text-lg">Patient {appointment.patientId}</h3>
                        <p className="text-sm text-gray-500">
                          {appointment.appointmentDate ? new Date(appointment.appointmentDate).toLocaleString() : ''}
                        </p>
                        <Badge variant={appointment.status === 'COMPLETED' ? 'default' : 'secondary'}>
                          {appointment.status}
                        </Badge>
                      </div>
                    </div>
                    {appointment.notes && (
                      <p className="text-sm text-gray-600">Notes: {appointment.notes}</p>
                    )}
                  </div>
                ))
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <Calendar className="w-12 h-12 mx-auto mb-4 opacity-50" />
                  <p>No appointments scheduled</p>
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        {/* Create Medical Record Modal */}
        {showCreateModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Create Medical Record</h2>
                <Button
                  variant="outline"
                  onClick={() => { setShowCreateModal(false); resetForm(); }}
                  className="p-2"
                >
                  <X className="w-4 h-4" />
                </Button>
              </div>
              
              <form onSubmit={handleCreateRecord} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Patient</label>
                  <Select
                    value={formData.patientId}
                    onChange={(e) => setFormData({...formData, patientId: e.target.value})}
                    required
                  >
                    <option value="">Select Patient</option>
                    {myPatients.map(patient => (
                      <option key={patient.id} value={patient.id}>
                        {patient.name}
                      </option>
                    ))}
                  </Select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium mb-1">Appointment (Optional)</label>
                  <Select
                    value={formData.appointmentId}
                    onChange={(e) => setFormData({...formData, appointmentId: e.target.value})}
                  >
                    <option value="">No specific appointment</option>
                    {appointments.map(apt => (
                      <option key={apt.id} value={apt.id}>
                        {getPatientName(apt.patientId)} - {apt.appointmentDate ? new Date(apt.appointmentDate).toLocaleString() : ''}
                      </option>
                    ))}
                  </Select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium mb-1">Diagnosis</label>
                  <textarea
                    value={formData.diagnosis}
                    onChange={(e) => setFormData({...formData, diagnosis: e.target.value})}
                    className="w-full p-2 border border-gray-300 rounded-md"
                    rows="3"
                    required
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium mb-1">Treatment</label>
                  <textarea
                    value={formData.treatment}
                    onChange={(e) => setFormData({...formData, treatment: e.target.value})}
                    className="w-full p-2 border border-gray-300 rounded-md"
                    rows="3"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium mb-1">Prescription</label>
                  <textarea
                    value={formData.prescription}
                    onChange={(e) => setFormData({...formData, prescription: e.target.value})}
                    className="w-full p-2 border border-gray-300 rounded-md"
                    rows="2"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium mb-1">Notes</label>
                  <textarea
                    value={formData.notes}
                    onChange={(e) => setFormData({...formData, notes: e.target.value})}
                    className="w-full p-2 border border-gray-300 rounded-md"
                    rows="2"
                  />
                </div>
                
                <div className="flex gap-2 pt-4">
                  <Button
                    type="submit"
                    className="bg-[#4CAF50] hover:bg-[#45a049] flex items-center gap-2"
                  >
                    <Save className="w-4 h-4" />
                    Create Record
                  </Button>
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => { setShowCreateModal(false); resetForm(); }}
                  >
                    Cancel
                  </Button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DoctorDashboard;
