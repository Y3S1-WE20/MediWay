import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  FileText, 
  Plus, 
  Search, 
  Edit, 
  Trash2, 
  Calendar, 
  User, 
  Stethoscope,
  Filter,
  Download
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Badge } from '../components/ui/badge';
import { useAuth } from '../context/AuthContext';
import api from '../api/api';
import { endpoints } from '../api/endpoints';
import MedicalRecordForm from '../components/MedicalRecordForm';
import ClinicalTabs from '../components/clinical/ClinicalTabs';

const MedicalRecords = () => {
  const { user } = useAuth();
  const [medicalRecords, setMedicalRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [filteredRecords, setFilteredRecords] = useState([]);
  const [patientSearch, setPatientSearch] = useState('');
  const [selectedPatientId, setSelectedPatientId] = useState('');
  const [confirmation, setConfirmation] = useState('');

  useEffect(() => {
    fetchMedicalRecords();
  }, []);

  useEffect(() => {
    filterRecords();
  }, [medicalRecords, searchQuery]);

  const fetchMedicalRecords = async () => {
    setLoading(true);
    setError(null);
    try {
      let response;
      if (user?.role === 'DOCTOR') {
        response = await api.get(endpoints.getMedicalRecordsByDoctor(user.userId));
      } else {
        response = await api.get(endpoints.getMedicalRecordsByPatient(user.userId));
      }
      setMedicalRecords(response.data);
    } catch (error) {
      console.error('Error fetching medical records:', error);
      setError('Failed to load medical records. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchPatientById = async () => {
    if (!patientSearch.trim()) {
      setSelectedPatientId('');
      await fetchMedicalRecords();
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const response = await api.get(endpoints.getMedicalRecordsByPatient(patientSearch.trim()));
      setSelectedPatientId(patientSearch.trim());
      setMedicalRecords(response.data);
    } catch (error) {
      console.error('Error searching by patient ID:', error);
      setError('Patient not found or no records yet.');
      setSelectedPatientId(patientSearch.trim());
      setMedicalRecords([]);
    } finally {
      setLoading(false);
    }
  };

  const filterRecords = () => {
    if (!searchQuery.trim()) {
      setFilteredRecords(medicalRecords);
      return;
    }

    const filtered = medicalRecords.filter(record =>
      record.diagnosis?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      record.medications?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      record.notes?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      record.patientName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      record.doctorName?.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredRecords(filtered);
  };

  const handleCreateRecord = async (recordData) => {
    try {
      const response = await api.post(endpoints.createMedicalRecord, recordData);
      setMedicalRecords(prev => [response.data, ...prev]);
      setShowForm(false);
      setError(null);
      setConfirmation('Medical record created successfully.');
      try {
        const audits = JSON.parse(localStorage.getItem('mediway_audit') || '[]');
        audits.push({
          action: 'CREATE_MEDICAL_RECORD',
          timestamp: new Date().toISOString(),
          doctorId: user?.userId,
          patientId: recordData.patientId,
          recordId: response.data?.recordId,
        });
        localStorage.setItem('mediway_audit', JSON.stringify(audits));
      } catch (_) {
        // ignore audit errors silently
      }
    } catch (error) {
      console.error('Error creating medical record:', error);
      setError('Failed to create medical record. Please try again.');
    }
  };

  const handleUpdateRecord = async (id, recordData) => {
    try {
      const response = await api.put(endpoints.updateMedicalRecord(id), recordData);
      setMedicalRecords(prev => 
        prev.map(record => record.recordId === id ? response.data : record)
      );
      setEditingRecord(null);
      setError(null);
    } catch (error) {
      console.error('Error updating medical record:', error);
      setError('Failed to update medical record. Please try again.');
    }
  };

  const handleDeleteRecord = async (id) => {
    if (!window.confirm('Are you sure you want to delete this medical record?')) {
      return;
    }

    try {
      await api.delete(endpoints.deleteMedicalRecord(id));
      setMedicalRecords(prev => prev.filter(record => record.recordId !== id));
      setError(null);
    } catch (error) {
      console.error('Error deleting medical record:', error);
      setError('Failed to delete medical record. Please try again.');
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'bg-green-100 text-green-800';
      case 'ARCHIVED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-blue-100 text-blue-800';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-background p-6">
        <div className="container mx-auto max-w-7xl">
          <div className="flex items-center justify-center h-64">
            <div className="text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
              <p className="text-muted-foreground">Loading medical records...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-6">
      <div className="container mx-auto max-w-7xl">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="mb-8"
        >
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="p-2 bg-primary/10 rounded-lg">
                <FileText className="h-6 w-6 text-primary" />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-foreground">Medical Records</h1>
                <p className="text-muted-foreground">
                  {user?.role === 'DOCTOR' ? 'Manage patient medical records' : 'View your medical history'}
                </p>
              </div>
            </div>
            
            {user?.role === 'DOCTOR' && (
              <Button onClick={() => setShowForm(true)} className="flex items-center space-x-2">
                <Plus className="h-4 w-4" />
                <span>Add Record</span>
              </Button>
            )}
          </div>
        </motion.div>

        {/* Error Message */}
        {error && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg"
          >
            <p className="text-red-800">{error}</p>
          </motion.div>
        )}
        {confirmation && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            className="mb-6 p-4 bg-green-50 border border-green-200 rounded-lg"
          >
            <p className="text-green-800">{confirmation}</p>
          </motion.div>
        )}

        {/* Search and Filters */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.1 }}
          className="mb-6"
        >
          <Card>
            <CardContent className="p-6">
              <div className="flex items-center space-x-4">
                <div className="flex-1 relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="Search records by diagnosis, medications, or notes..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10"
                  />
                </div>
                <div className="flex items-center space-x-2">
                  <Input
                    placeholder="Patient ID (UUID)"
                    value={patientSearch}
                    onChange={(e) => setPatientSearch(e.target.value)}
                    className="w-[260px]"
                  />
                  <Button variant="outline" size="sm" onClick={handleSearchPatientById}>
                    Search Patient
                  </Button>
                </div>
                <Button variant="outline" size="sm">
                  <Filter className="h-4 w-4 mr-2" />
                  Filter
                </Button>
                <Button variant="outline" size="sm">
                  <Download className="h-4 w-4 mr-2" />
                  Export
                </Button>
              </div>
            </CardContent>
          </Card>
        </motion.div>

        {/* Medical Records List */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.2 }}
          className="grid gap-6"
        >
          {filteredRecords.length === 0 ? (
            <Card>
              <CardContent className="p-12 text-center">
                <FileText className="h-16 w-16 text-muted-foreground mx-auto mb-4" />
                <h3 className="text-lg font-semibold text-foreground mb-2">
                  {searchQuery ? 'No records found' : 'No medical records yet'}
                </h3>
                <p className="text-muted-foreground mb-4">
                  {searchQuery 
                    ? 'Try adjusting your search criteria'
                    : user?.role === 'DOCTOR' 
                      ? 'Start by adding a new medical record'
                      : 'Your medical records will appear here'
                  }
                </p>
                {user?.role === 'DOCTOR' && !searchQuery && (
                  <Button onClick={() => setShowForm(true)}>
                    <Plus className="h-4 w-4 mr-2" />
                    Add First Record
                  </Button>
                )}
              </CardContent>
            </Card>
          ) : (
            filteredRecords.map((record, index) => (
              <motion.div
                key={record.recordId}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: index * 0.1 }}
              >
                <Card className="hover:shadow-lg transition-shadow">
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <CardTitle className="text-xl mb-2">{record.diagnosis}</CardTitle>
                        <div className="flex items-center space-x-4 text-sm text-muted-foreground">
                          <div className="flex items-center space-x-1">
                            <User className="h-4 w-4" />
                            <span>
                              {user?.role === 'DOCTOR' ? record.patientName : record.doctorName}
                            </span>
                          </div>
                          <div className="flex items-center space-x-1">
                            <Calendar className="h-4 w-4" />
                            <span>{formatDate(record.createdAt)}</span>
                          </div>
                        </div>
                      </div>
                      
                      {user?.role === 'DOCTOR' && (
                        <div className="flex items-center space-x-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => setEditingRecord(record)}
                          >
                            <Edit className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleDeleteRecord(record.recordId)}
                            className="text-red-600 hover:text-red-700"
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      )}
                    </div>
                  </CardHeader>
                  
                  <CardContent>
                    {record.medications && (
                      <div className="mb-4">
                        <h4 className="font-semibold text-foreground mb-2 flex items-center">
                          <Stethoscope className="h-4 w-4 mr-2" />
                          Medications
                        </h4>
                        <p className="text-muted-foreground">{record.medications}</p>
                      </div>
                    )}
                    
                    {record.notes && (
                      <div>
                        <h4 className="font-semibold text-foreground mb-2">Notes</h4>
                        <p className="text-muted-foreground">{record.notes}</p>
                      </div>
                    )}
                </CardContent>
                </Card>
              </motion.div>
            ))
          )}
        </motion.div>

        {/* Medical Record Form Modal */}
        {showForm && (
          <MedicalRecordForm
            fixedPatientId={selectedPatientId || undefined}
            onSubmit={handleCreateRecord}
            onCancel={() => setShowForm(false)}
          />
        )}

        {/* Edit Medical Record Form Modal */}
        {editingRecord && (
          <MedicalRecordForm
            record={editingRecord}
            onSubmit={(data) => handleUpdateRecord(editingRecord.recordId, data)}
            onCancel={() => setEditingRecord(null)}
          />
        )}

        {/* Clinical Details Drawer-like section for the first selected record (simple inline) */}
        {filteredRecords.length > 0 && (
          <div className="mt-8">
            <h2 className="text-xl font-semibold mb-2">Clinical Details</h2>
            <ClinicalTabs recordId={filteredRecords[0].recordId} />
          </div>
        )}
      </div>
    </div>
  );
};

export default MedicalRecords;
