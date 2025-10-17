import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Heart, LogOut, User, LayoutDashboard, Users, UserCog } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { Button } from './ui/button';

const AdminNavbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <motion.nav
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      className="fixed top-0 left-0 right-0 bg-white shadow-md z-50"
    >
      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          {/* Logo */}
          <Link to="/admin/dashboard" className="flex items-center space-x-2">
            <Heart className="h-8 w-8 text-purple-600" />
            <span className="text-2xl font-bold text-gray-800">
              MediWay <span className="text-purple-600">Admin</span>
            </span>
          </Link>

          {/* Navigation Links */}
          <div className="hidden md:flex items-center space-x-6">
            <Link
              to="/admin/dashboard"
              className="flex items-center space-x-2 text-gray-700 hover:text-purple-600 transition-colors"
            >
              <LayoutDashboard className="h-5 w-5" />
              <span>Dashboard</span>
            </Link>
            
            <Link
              to="/admin/users"
              className="flex items-center space-x-2 text-gray-700 hover:text-purple-600 transition-colors"
            >
              <Users className="h-5 w-5" />
              <span>Users</span>
            </Link>

            <Link
              to="/admin/doctors"
              className="flex items-center space-x-2 text-gray-700 hover:text-purple-600 transition-colors"
            >
              <UserCog className="h-5 w-5" />
              <span>Doctors</span>
            </Link>
          </div>

          {/* User Menu */}
          <div className="flex items-center space-x-4">
            <div className="hidden md:flex items-center space-x-2 text-gray-700">
              <User className="h-5 w-5" />
              <span className="font-medium">{user?.name}</span>
            </div>

            <Button
              onClick={handleLogout}
              variant="outline"
              className="flex items-center space-x-2"
            >
              <LogOut className="h-4 w-4" />
              <span>Logout</span>
            </Button>
          </div>
        </div>
      </div>
    </motion.nav>
  );
};

export default AdminNavbar;
