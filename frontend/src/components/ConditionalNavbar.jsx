import React from 'react';
import { useLocation } from 'react-router-dom';
import Navbar from './Navbar';
import DoctorNavbar from './DoctorNavbar';
import AdminNavbar from './AdminNavbar';

const ConditionalNavbar = () => {
  const location = useLocation();
  
  // Don't show any navbar on login/register pages
  if (location.pathname === '/login' || location.pathname === '/register') {
    return null;
  }
  
  // Show AdminNavbar for admin routes
  if (location.pathname.startsWith('/admin')) {
    return <AdminNavbar />;
  }
  
  // Show DoctorNavbar for doctor routes
  if (location.pathname.startsWith('/doctor')) {
    return <DoctorNavbar />;
  }
  
  // Show regular Navbar for patient routes and public pages
  return <Navbar />;
};

export default ConditionalNavbar;
