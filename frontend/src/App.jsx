import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ScrollToTop from './components/ScrollToTop';
import ProtectedRoute from './components/ProtectedRoute';

// Pages
import Home from './pages/Home';
import Register from './pages/Register';
import Login from './pages/Login';
import Appointments from './pages/Appointments';
import BookAppointment from './pages/BookAppointment';
import Payments from './pages/Payments';
import Reports from './pages/Reports';
import Profile from './pages/Profile';
import AdminDashboard from './pages/AdminDashboard';
import DoctorDashboard from './pages/DoctorDashboard';
import PaymentSuccess from './pages/PaymentSuccess';
import PaymentCancel from './pages/PaymentCancel';
import PayPalCheckout from './pages/PayPalCheckout';
import DoctorProfile from './pages/DoctorProfile';
import ServiceDetail from './pages/ServiceDetail';
import About from './pages/About';

import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-background w-full overflow-x-hidden">
          <Navbar />
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<Home />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/about" element={<About />} />
            <Route path="/doctor/:doctorId" element={<DoctorProfile />} />
            <Route path="/service/:serviceId" element={<ServiceDetail />} />

            {/* Protected Routes */}
            <Route
              path="/admin/dashboard"
              element={
                <ProtectedRoute>
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/doctor/dashboard"
              element={
                <ProtectedRoute>
                  <DoctorDashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/appointments"
              element={
                <ProtectedRoute>
                  <Appointments />
                </ProtectedRoute>
              }
            />
            <Route
              path="/book-appointment"
              element={
                <ProtectedRoute>
                  <BookAppointment />
                </ProtectedRoute>
              }
            />
            <Route
              path="/payments"
              element={
                <ProtectedRoute>
                  <Payments />
                </ProtectedRoute>
              }
            />
            <Route
              path="/paypal-checkout"
              element={<PayPalCheckout />}
            />
            <Route
              path="/payment-success"
              element={<PaymentSuccess />}
            />
            <Route
              path="/payment-cancel"
              element={<PaymentCancel />}
            />
            {/* Reports route only for admin */}
            <Route
              path="/reports"
              element={
                <ProtectedRoute>
                  <Reports />
                </ProtectedRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <Profile />
                </ProtectedRoute>
              }
            />

            {/* Catch all - redirect to home */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
          <Footer />
          <ScrollToTop />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
