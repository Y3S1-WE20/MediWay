import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { X, FileText, User, Stethoscope, MessageSquare } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from './ui/card';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { useAuth } from '../context/AuthContext';
import api from '../api/api';
import { endpoints } from '../api/endpoints';

const MedicalRecordForm = ({ record, onSubmit, onCancel, fixedPatientId }) => {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    diagnosis: '',
    medications: '',
    notes: '',
    patientId: fixedPatientId || '',
    doctorId: user?.userId || ''
  });
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [useManualPatientId, setUseManualPatientId] = useState(false);
  const [useManualDoctorId, setUseManualDoctorId] = useState(false); // retained but input shown always

  useEffect(() => {
    if (record) {
      setFormData({
        diagnosis: record.diagnosis || '',
        medications: record.medications || '',
        notes: record.notes || '',
        patientId: record.patientId || fixedPatientId || '',
        doctorId: record.doctorId || user?.userId || ''
      });
    }
    
    // Fetch patients if user is a doctor
    if (user?.role === 'DOCTOR') {
      fetchPatients();
    }
  }, [record, user, fixedPatientId]);

  const fetchPatients = async () => {
    // Try multiple likely endpoints to fetch all patients; fall back gracefully
    const candidatePaths = [
      // Common patterns in this codebase or typical APIs
      '/patients',
      '/patients/all',
      '/users',
      '/auth/users',
    ];

    // Normalizer to a consistent shape { patientId, patientName }
    const normalizeUsers = (items) => {
      if (!Array.isArray(items)) return [];
      return items
        .map((u) => ({
          patientId: u.userId || u.patientId || u.id,
          patientName: u.fullName || u.patientName || u.name || u.email,
        }))
        .filter((u) => u.patientId && u.patientName);
    };

    // 1) Prefer real patients endpoint if available via appointments (existing fallback)
    try {
      const apptRes = await api.get(endpoints.getAppointments);
      const fromAppointments = apptRes.data.reduce((acc, appointment) => {
        if (
          appointment?.patientId &&
          !acc.find((p) => p.patientId === appointment.patientId)
        ) {
          acc.push({
            patientId: appointment.patientId,
            patientName: appointment.patientName || appointment.patientFullName || 'Patient',
          });
        }
        return acc;
      }, []);
      if (fromAppointments.length > 0) {
        setPatients(fromAppointments);
        return;
      }
    } catch (_) {
      // ignore and try the generic endpoints below
    }

    // 2) Try generic endpoints
    for (const path of candidatePaths) {
      try {
        const res = await api.get(path);
        const normalized = normalizeUsers(res.data);
        if (normalized.length > 0) {
          // Dedupe by patientId
          const deduped = normalized.filter(
            (p, idx, arr) => arr.findIndex((x) => x.patientId === p.patientId) === idx
          );
          setPatients(deduped);
          return;
        }
      } catch (_) {
        // try next path
      }
    }

    // If nothing found, keep empty list; UI will offer manual entry
    setPatients([]);
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
    
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.diagnosis.trim()) {
      newErrors.diagnosis = 'Diagnosis is required';
    }
    
    if (user?.role === 'DOCTOR' && !formData.patientId) {
      newErrors.patientId = 'Please select a patient';
    }
    if (!formData.doctorId) {
      newErrors.doctorId = 'Doctor ID is required';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setLoading(true);
    
    try {
      await onSubmit(formData);
    } catch (error) {
      console.error('Error submitting form:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onCancel();
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50"
      onClick={handleBackdropClick}
    >
      <motion.div
        initial={{ scale: 0.95, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.95, opacity: 0 }}
        className="w-full max-w-2xl max-h-[90vh] overflow-y-auto"
      >
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-primary/10 rounded-lg">
                  <FileText className="h-5 w-5 text-primary" />
                </div>
                <div>
                  <CardTitle>
                    {record ? 'Edit Medical Record' : 'Add Medical Record'}
                  </CardTitle>
                  <p className="text-sm text-muted-foreground">
                    {record ? 'Update the medical record details' : 'Create a new medical record'}
                  </p>
                </div>
              </div>
              <Button variant="ghost" size="sm" onClick={onCancel}>
                <X className="h-4 w-4" />
              </Button>
            </div>
          </CardHeader>
          
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Patient Selection (Doctor only) */}
              {user?.role === 'DOCTOR' && (
                <div>
                  <label className="block text-sm font-medium text-foreground mb-2">
                    <User className="h-4 w-4 inline mr-2" />
                    Patient
                  </label>
                  {!useManualPatientId && !fixedPatientId && (
                    <select
                      value={formData.patientId}
                      onChange={(e) => handleInputChange('patientId', e.target.value)}
                      className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary ${
                        errors.patientId ? 'border-red-500' : 'border-gray-300'
                      }`}
                      disabled={!!record} // Don't allow changing patient when editing
                    >
                      <option value="">Select a patient</option>
                      {patients.map((patient) => (
                        <option key={patient.patientId} value={patient.patientId}>
                          {patient.patientName}
                        </option>
                      ))}
                    </select>
                  )}

                  {(useManualPatientId || fixedPatientId) && (
                    <Input
                      type="text"
                      placeholder="Enter patient ID"
                      value={formData.patientId}
                      onChange={(e) => handleInputChange('patientId', e.target.value)}
                      className={errors.patientId ? 'border-red-500' : ''}
                      disabled={!!record || !!fixedPatientId}
                    />
                  )}

                  {/* Helper to switch modes */}
                  {!record && !fixedPatientId && (
                    <div className="mt-2 text-xs text-muted-foreground">
                      {patients.length === 0 ? (
                        <button
                          type="button"
                          className="text-primary underline"
                          onClick={() => setUseManualPatientId((v) => !v)}
                        >
                          {useManualPatientId ? 'Select from list instead' : "Can't find patient? Enter ID manually"}
                        </button>
                      ) : (
                        <button
                          type="button"
                          className="text-primary underline"
                          onClick={() => setUseManualPatientId((v) => !v)}
                        >
                          {useManualPatientId ? 'Select from list instead' : 'Enter patient ID manually'}
                        </button>
                      )}
                    </div>
                  )}
                  {errors.patientId && (
                    <p className="text-red-500 text-sm mt-1">{errors.patientId}</p>
                  )}
                </div>
              )}

              {/* Doctor (ID) */}
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  <Stethoscope className="h-4 w-4 inline mr-2" />
                  Doctor ID
                </label>
                <Input
                  type="text"
                  placeholder="Enter doctor ID (UUID)"
                  value={formData.doctorId}
                  onChange={(e) => handleInputChange('doctorId', e.target.value)}
                  className={errors.doctorId ? 'border-red-500' : ''}
                />
                <div className="text-xs text-muted-foreground mt-1">
                  Pre-filled with your user ID. If your backend uses a separate Doctors table, paste the Doctor UUID here.
                </div>
                {errors.doctorId && (
                  <p className="text-red-500 text-sm mt-1">{errors.doctorId}</p>
                )}
              </div>

              {/* Diagnosis */}
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  <Stethoscope className="h-4 w-4 inline mr-2" />
                  Diagnosis *
                </label>
                <Input
                  type="text"
                  value={formData.diagnosis}
                  onChange={(e) => handleInputChange('diagnosis', e.target.value)}
                  placeholder="Enter the medical diagnosis"
                  className={errors.diagnosis ? 'border-red-500' : ''}
                />
                {errors.diagnosis && (
                  <p className="text-red-500 text-sm mt-1">{errors.diagnosis}</p>
                )}
              </div>

              {/* Medications */}
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  <Stethoscope className="h-4 w-4 inline mr-2" />
                  Medications
                </label>
                <textarea
                  value={formData.medications}
                  onChange={(e) => handleInputChange('medications', e.target.value)}
                  placeholder="Enter prescribed medications and dosages"
                  rows={3}
                  className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary resize-none ${
                    errors.medications ? 'border-red-500' : 'border-gray-300'
                  }`}
                />
                {errors.medications && (
                  <p className="text-red-500 text-sm mt-1">{errors.medications}</p>
                )}
              </div>

              {/* Notes */}
              <div>
                <label className="block text-sm font-medium text-foreground mb-2">
                  <MessageSquare className="h-4 w-4 inline mr-2" />
                  Notes
                </label>
                <textarea
                  value={formData.notes}
                  onChange={(e) => handleInputChange('notes', e.target.value)}
                  placeholder="Additional notes, observations, or recommendations"
                  rows={4}
                  className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary resize-none ${
                    errors.notes ? 'border-red-500' : 'border-gray-300'
                  }`}
                />
                {errors.notes && (
                  <p className="text-red-500 text-sm mt-1">{errors.notes}</p>
                )}
              </div>

              {/* Form Actions */}
              <div className="flex items-center justify-end space-x-3 pt-4 border-t">
                <Button
                  type="button"
                  variant="outline"
                  onClick={onCancel}
                  disabled={loading}
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  disabled={loading}
                  className="flex items-center space-x-2"
                >
                  {loading && (
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  )}
                  <span>{record ? 'Update Record' : 'Create Record'}</span>
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </motion.div>
    </motion.div>
  );
};

export default MedicalRecordForm;
