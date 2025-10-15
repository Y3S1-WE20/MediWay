// API Endpoints
export const PATIENTS = '/patients';
export const APPOINTMENTS = '/appointments';
export const PAYMENTS = '/payments';
export const REPORTS = '/reports';
export const DOCTORS = '/doctors';
export const AUTH = '/auth';

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
  createAppointment: `${APPOINTMENTS}`,
  cancelAppointment: (id) => `${APPOINTMENTS}/${id}`,
  getAvailableSlots: `${APPOINTMENTS}/slots`,
  
  // Doctors
  getDoctors: `${DOCTORS}`,
  getDoctorsBySpecialization: (specialization) => `${DOCTORS}/specialization/${specialization}`,
  
  // Payments
  getPayments: `${PAYMENTS}`,
  createPayment: `${PAYMENTS}`,
  getUnpaidBills: `${PAYMENTS}/unpaid`,
  
  // Reports
  getReports: `${REPORTS}`,
  getPatientStats: `${REPORTS}/patient-stats`,
  getAppointmentStats: `${REPORTS}/appointment-stats`,
};
