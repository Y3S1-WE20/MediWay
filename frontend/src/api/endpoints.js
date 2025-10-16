// API Endpoints
export const PATIENTS = '/patients';
export const APPOINTMENTS = '/appointments';
export const PAYMENTS = '/payments';
export const REPORTS = '/reports';
export const DOCTORS = '/doctors';
export const AUTH = '/auth';
export const PROFILE = '/profile';

// Specific endpoints
export const endpoints = {
  // Auth
  login: `${AUTH}/login`,
  register: `${AUTH}/register`,
  authHealth: `${AUTH}/health`,
  
  // Profile
  getProfile: `${PROFILE}`,
  updateProfile: `${PROFILE}`,
  getQRCode: `${PROFILE}/qrcode`,
  verifyQRCode: `${PROFILE}/verify-qr`,
  
  // Patients
  getPatientProfile: (id) => `${PATIENTS}/${id}`,
  updatePatientProfile: (id) => `${PATIENTS}/${id}`,
  
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
  getReportSummary: `${REPORTS}/summary`,
  getComprehensiveReport: `${REPORTS}/patient/comprehensive`,
  getMedicalRecords: `${REPORTS}/medical-records`,
  getMedicalRecord: (id) => `${REPORTS}/medical-records/${id}`,
  createMedicalRecord: `${REPORTS}/medical-records`,
  getPrescriptions: `${REPORTS}/prescriptions`,
  getPrescription: (id) => `${REPORTS}/prescriptions/${id}`,
  createPrescription: `${REPORTS}/prescriptions`,
  updatePrescriptionStatus: (id) => `${REPORTS}/prescriptions/${id}/status`,
  getLabResults: `${REPORTS}/lab-results`,
  getLabResult: (id) => `${REPORTS}/lab-results/${id}`,
  createLabResult: `${REPORTS}/lab-results`,
  updateLabResultStatus: (id) => `${REPORTS}/lab-results/${id}/status`,
};

