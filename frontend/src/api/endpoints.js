// API Endpoints
export const PATIENTS = '/patients';
export const APPOINTMENTS = '/appointments';
export const PAYMENTS = '/payments';
export const REPORTS = '/reports';
export const DOCTORS = '/doctors';
export const AUTH = '/auth';
export const MEDICAL_RECORDS = '/medical-records';

// Specific endpoints
export const endpoints = {
  // Auth
  login: `${AUTH}/login`,
  register: `${AUTH}/register`,
  authHealth: `${AUTH}/health`,
  
  // Patients
  getProfile: (id) => `${PATIENTS}/${id}`,
  updateProfile: (id) => `${PATIENTS}/${id}`,
  
  // Appointments
  getAppointments: `${APPOINTMENTS}`,
  getMyAppointments: `${APPOINTMENTS}/my`,
  createAppointment: `${APPOINTMENTS}`,
  cancelAppointment: (id) => `${APPOINTMENTS}/${id}`,
  getAppointmentById: (id) => `${APPOINTMENTS}/${id}`,
  updateAppointmentStatus: (id) => `${APPOINTMENTS}/${id}/status`,
  getAvailableSlots: `${APPOINTMENTS}/slots`,
  
  // Doctors
  getDoctors: `${APPOINTMENTS}/doctors`,
  getDoctorById: (id) => `${APPOINTMENTS}/doctors/${id}`,
  getDoctorsBySpecialization: (specialization) => `${DOCTORS}/specialization/${specialization}`,
  
  // Payments
  paymentHealth: `${PAYMENTS}/health`,
  createPayment: `${PAYMENTS}/create`,
  executePayment: `${PAYMENTS}/execute`,
  cancelPayment: `${PAYMENTS}/cancel`,
  getPayment: (id) => `${PAYMENTS}/${id}`,
  getMyPayments: `${PAYMENTS}/my-payments`,
  getAppointmentPayments: (appointmentId) => `${PAYMENTS}/appointment/${appointmentId}`,
  getReceiptByPayment: (paymentId) => `${PAYMENTS}/receipt/payment/${paymentId}`,
  getReceiptByNumber: (receiptNumber) => `${PAYMENTS}/receipt/${receiptNumber}`,
  getMyReceipts: `${PAYMENTS}/receipts/my-receipts`,
  
  // Reports
  getReports: `${REPORTS}`,
  getPatientStats: `${REPORTS}/patient-stats`,
  getAppointmentStats: `${REPORTS}/appointment-stats`,
  
  // Medical Records
  getMedicalRecords: `${MEDICAL_RECORDS}`,
  createMedicalRecord: `${MEDICAL_RECORDS}`,
  updateMedicalRecord: (id) => `${MEDICAL_RECORDS}/${id}`,
  getMedicalRecordById: (id) => `${MEDICAL_RECORDS}/${id}`,
  getMedicalRecordsByPatient: (patientId) => `${MEDICAL_RECORDS}/patient/${patientId}`,
  getMedicalRecordsByDoctor: (doctorId) => `${MEDICAL_RECORDS}/doctor/${doctorId}`,
  deleteMedicalRecord: (id) => `${MEDICAL_RECORDS}/${id}`,
  searchMedicalRecords: `${MEDICAL_RECORDS}/search`,

  // Clinical (Diagnoses/Treatments/Prescriptions)
  addDiagnosis: (recordId) => `/clinical/records/${recordId}/diagnoses`,
  updateDiagnosis: (diagnosisId) => `/clinical/diagnoses/${diagnosisId}`,
  deleteDiagnosis: (diagnosisId) => `/clinical/diagnoses/${diagnosisId}`,
  listDiagnoses: (recordId) => `/clinical/records/${recordId}/diagnoses`,

  addTreatment: (recordId) => `/clinical/records/${recordId}/treatments`,
  updateTreatment: (treatmentId) => `/clinical/treatments/${treatmentId}`,
  deleteTreatment: (treatmentId) => `/clinical/treatments/${treatmentId}`,
  listTreatments: (recordId) => `/clinical/records/${recordId}/treatments`,

  addPrescription: (recordId) => `/clinical/records/${recordId}/prescriptions`,
  updatePrescription: (prescriptionId) => `/clinical/prescriptions/${prescriptionId}`,
  deletePrescription: (prescriptionId) => `/clinical/prescriptions/${prescriptionId}`,
  listPrescriptions: (recordId) => `/clinical/records/${recordId}/prescriptions`,
};
