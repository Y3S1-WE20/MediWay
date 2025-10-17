import React, { useState, useEffect } from 'react';
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
import { useAuth } from '../context/AuthContext';
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

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // Fetch doctor's medical records
      const recordsResponse = await api.get(`/medical-records/doctor/${user.id}`);
      setMedicalRecords(recordsResponse.data);

      // Fetch appointments for the doctor
      const appointmentsResponse = await api.get('/appointments/my');
      setAppointments(appointmentsResponse.data);

      // Extract unique patients from appointments and medical records
      const patientIds = new Set([
        ...appointmentsResponse.data.map(apt => apt.patientId),
        ...recordsResponse.data.map(record => record.patientId)
      ]);
      
      // Fetch patient details
      const patientsPromises = Array.from(patientIds).map(id => 
        api.get(`/patients/${id}`).catch(() => null)
      );
      const patientsResponses = await Promise.all(patientsPromises);
      const patients = patientsResponses.filter(resp => resp !== null).map(resp => resp.data);
      setMyPatients(patients);

    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRecord = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/medical-records', formData);
      if (response.data.success) {
        alert('Medical record created successfully!');
        setShowCreateModal(false);
        resetForm();
        fetchDashboardData();
      } else {
        alert(response.data.message);
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
          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Total Patients</p>
                  <p className="text-3xl font-bold text-blue-600">{myPatients.length}</p>
                </div>
                <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                  <Users className="w-6 h-6 text-blue-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Medical Records</p>
                  <p className="text-3xl font-bold text-green-600">{medicalRecords.length}</p>
                </div>
                <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
                  <FileText className="w-6 h-6 text-green-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Appointments</p>
                  <p className="text-3xl font-bold text-purple-600">{appointments.length}</p>
                </div>
                <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
                  <Calendar className="w-6 h-6 text-purple-600" />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600 mb-1">Active Cases</p>
                  <p className="text-3xl font-bold text-orange-600">
                    {medicalRecords.filter(r => r.treatment && !r.treatment.includes('completed')).length}
                  </p>
                </div>
                <div className="w-12 h-12 bg-orange-100 rounded-full flex items-center justify-center">
                  <Activity className="w-6 h-6 text-orange-600" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Medical Records Management */}
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle>Medical Records Management</CardTitle>
              <Button 
                onClick={() => setShowCreateModal(true)}
                className="bg-[#4CAF50] hover:bg-[#45a049] flex items-center gap-2"
              >
                <Plus className="w-4 h-4" />
                New Record
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            {/* Search and Filter */}
            <div className="flex flex-col sm:flex-row gap-4 mb-6">
              <div className="flex-1">
                <div className="relative">
                  <Search className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                  <Input
                    placeholder="Search records by diagnosis, treatment, or patient..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-10"
                  />
                </div>
              </div>
              <div className="sm:w-48">
                <Select
                  value={filterPatient}
                  onChange={(e) => setFilterPatient(e.target.value)}
                >
                  <option value="">All Patients</option>
                  {myPatients.map(patient => (
                    <option key={patient.id} value={patient.id}>
                      {patient.name}
                    </option>
                  ))}
                </Select>
              </div>
            </div>

            {/* Records List */}
            <div className="space-y-4">
              {filteredRecords.length > 0 ? (
                filteredRecords.map((record) => (
                  <div key={record.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <h3 className="font-semibold text-lg">{getPatientName(record.patientId)}</h3>
                        <p className="text-sm text-gray-500">
                          {new Date(record.recordDate).toLocaleDateString()} at {new Date(record.recordDate).toLocaleTimeString()}
                        </p>
                      </div>
                      <div className="flex gap-2">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => openEditModal(record)}
                          className="flex items-center gap-1"
                        >
                          <Edit className="w-4 h-4" />
                          Edit
                        </Button>
                        {user.role === 'ADMIN' && (
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleDeleteRecord(record.id)}
                            className="flex items-center gap-1 text-red-600 hover:text-red-700"
                          >
                            <Trash2 className="w-4 h-4" />
                            Delete
                          </Button>
                        )}
                      </div>
                    </div>
                    
                    <div className="grid md:grid-cols-2 gap-4">
                      <div>
                        <p className="text-sm font-medium text-gray-700">Diagnosis:</p>
                        <p className="text-sm text-gray-600 mb-2">{record.diagnosis || 'Not specified'}</p>
                        
                        <p className="text-sm font-medium text-gray-700">Treatment:</p>
                        <p className="text-sm text-gray-600">{record.treatment || 'Not specified'}</p>
                      </div>
                      <div>
                        <p className="text-sm font-medium text-gray-700">Prescription:</p>
                        <p className="text-sm text-gray-600 mb-2">{record.prescription || 'None prescribed'}</p>
                        
                        <p className="text-sm font-medium text-gray-700">Notes:</p>
                        <p className="text-sm text-gray-600">{record.notes || 'No additional notes'}</p>
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <FileText className="w-12 h-12 mx-auto mb-4 opacity-50" />
                  <p>No medical records found</p>
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
                        {getPatientName(apt.patientId)} - {new Date(apt.appointmentDate).toLocaleDateString()}
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

        {/* Edit Medical Record Modal */}
        {showEditModal && selectedRecord && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Edit Medical Record</h2>
                <Button
                  variant="outline"
                  onClick={() => { setShowEditModal(false); resetForm(); }}
                  className="p-2"
                >
                  <X className="w-4 h-4" />
                </Button>
              </div>
              
              <form onSubmit={handleEditRecord} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Patient</label>
                  <Input
                    value={getPatientName(selectedRecord.patientId)}
                    disabled
                    className="bg-gray-100"
                  />
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
                    Update Record
                  </Button>
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => { setShowEditModal(false); resetForm(); }}
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
