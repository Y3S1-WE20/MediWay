import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useAuth } from '../context/AuthContext';
import { 
  Shield, Users, Calendar, FileText, Plus, Edit, Trash2, 
  Search, Eye, UserCheck, X, Save, Stethoscope 
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import api from '../api/api';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');
  const [doctors, setDoctors] = useState([]);
  const [patients, setPatients] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showCreateDoctorModal, setShowCreateDoctorModal] = useState(false);
  const [showEditDoctorModal, setShowEditDoctorModal] = useState(false);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  
  const [doctorFormData, setDoctorFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    specialization: '',
    licenseNumber: '',
    experience: ''
  });

  const [stats, setStats] = useState([
    { title: 'Total Patients', value: '0', icon: Users, color: 'bg-blue-500' },
    { title: 'Total Doctors', value: '0', icon: Stethoscope, color: 'bg-green-500' },
    { title: 'Total Appointments', value: '0', icon: Calendar, color: 'bg-purple-500' },
    { title: 'Active Users', value: '0', icon: Shield, color: 'bg-orange-500' },
  ]);

  // Fetch dashboard data
  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // For now, use mock data since backend might not be running
      const mockDoctors = [
        { id: 2, fullName: 'Dr. John Smith', email: 'doctor@mediway.com', role: 'DOCTOR', phone: '123-456-7890', specialization: 'Cardiology' },
        { id: 3, fullName: 'Dr. Sarah Johnson', email: 'sarah@mediway.com', role: 'DOCTOR', phone: '123-456-7891', specialization: 'Pediatrics' }
      ];
      
      const mockPatients = [
        { id: 4, fullName: 'Patient One', email: 'patient1@test.com', role: 'PATIENT' },
        { id: 5, fullName: 'Patient Two', email: 'patient2@test.com', role: 'PATIENT' }
      ];

      setDoctors(mockDoctors);
      setPatients(mockPatients);
      setAppointments([]);

      // Update stats
      setStats([
        { title: 'Total Patients', value: mockPatients.length.toString(), icon: Users, color: 'bg-blue-500' },
        { title: 'Total Doctors', value: mockDoctors.length.toString(), icon: Stethoscope, color: 'bg-green-500' },
        { title: 'Total Appointments', value: '0', icon: Calendar, color: 'bg-purple-500' },
        { title: 'Active Users', value: (mockDoctors.length + mockPatients.length + 1).toString(), icon: Shield, color: 'bg-orange-500' },
      ]);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateDoctor = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // Mock creation for now
      const newDoctor = {
        id: Date.now(),
        ...doctorFormData,
        role: 'DOCTOR'
      };
      
      setDoctors([...doctors, newDoctor]);
      alert('Doctor created successfully! (Note: This is a demo - backend integration needed)');
      setShowCreateDoctorModal(false);
      resetDoctorForm();
    } catch (error) {
      console.error('Error creating doctor:', error);
      alert('Error creating doctor: ' + (error.response?.data?.message || 'Please try again'));
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteDoctor = async (doctorId) => {
    if (window.confirm('Are you sure you want to delete this doctor?')) {
      try {
        setDoctors(doctors.filter(d => d.id !== doctorId));
        alert('Doctor deleted successfully! (Note: This is a demo - backend integration needed)');
      } catch (error) {
        console.error('Error deleting doctor:', error);
        alert('Error deleting doctor: Please try again');
      }
    }
  };

  const resetDoctorForm = () => {
    setDoctorFormData({
      fullName: '',
      email: '',
      phone: '',
      password: '',
      specialization: '',
      licenseNumber: '',
      experience: ''
    });
    setSelectedDoctor(null);
  };

  const openEditModal = (doctor) => {
    setSelectedDoctor(doctor);
    setDoctorFormData({
      fullName: doctor.fullName || '',
      email: doctor.email || '',
      phone: doctor.phone || '',
      password: '',
      specialization: doctor.specialization || '',
      licenseNumber: doctor.licenseNumber || '',
      experience: doctor.experience || ''
    });
    setShowEditDoctorModal(true);
  };

  const filteredDoctors = doctors.filter(doctor =>
    doctor.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doctor.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doctor.specialization?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="text-gray-600 mt-2">
            Welcome back, {user?.name} ({user?.role})
          </p>
        </motion.div>

        {/* Tabs */}
        <div className="mb-6">
          <nav className="flex space-x-1 bg-white rounded-lg p-1 shadow-sm">
            {[
              { id: 'overview', name: 'Overview', icon: Shield },
              { id: 'doctors', name: 'Manage Doctors', icon: Stethoscope },
              { id: 'patients', name: 'Patients', icon: Users },
              { id: 'appointments', name: 'Appointments', icon: Calendar }
            ].map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                  activeTab === tab.id
                    ? 'bg-[#4CAF50] text-white'
                    : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                <tab.icon className="w-4 h-4" />
                {tab.name}
              </button>
            ))}
          </nav>
        </div>

        {/* Overview Tab */}
        {activeTab === 'overview' && (
          <div className="space-y-6">
            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {stats.map((stat, index) => (
                <motion.div
                  key={stat.title}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                >
                  <Card>
                    <CardContent className="p-6">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm text-gray-600">{stat.title}</p>
                          <p className="text-2xl font-bold mt-1">{stat.value}</p>
                        </div>
                        <div className={`${stat.color} p-3 rounded-lg`}>
                          <stat.icon className="w-6 h-6 text-white" />
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </motion.div>
              ))}
            </div>

            {/* Quick Actions */}
            <Card>
              <CardHeader>
                <CardTitle>Quick Actions</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <Button 
                    onClick={() => setActiveTab('doctors')}
                    className="bg-[#4CAF50] hover:bg-[#45a049] flex items-center gap-2"
                  >
                    <Plus className="w-4 h-4" />
                    Add New Doctor
                  </Button>
                  <Button 
                    variant="outline"
                    onClick={() => fetchDashboardData()}
                    className="flex items-center gap-2"
                  >
                    <FileText className="w-4 h-4" />
                    Refresh Data
                  </Button>
                  <Button 
                    variant="outline"
                    onClick={() => setActiveTab('appointments')}
                    className="flex items-center gap-2"
                  >
                    <Calendar className="w-4 h-4" />
                    View Appointments
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Doctors Management Tab */}
        {activeTab === 'doctors' && (
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <CardTitle>Doctor Management</CardTitle>
                  <Button 
                    onClick={() => setShowCreateDoctorModal(true)}
                    className="bg-[#4CAF50] hover:bg-[#45a049] flex items-center gap-2"
                  >
                    <Plus className="w-4 h-4" />
                    Add New Doctor
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                {/* Search */}
                <div className="mb-6">
                  <div className="relative">
                    <Search className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                    <Input
                      placeholder="Search doctors by name, email, or specialization..."
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>

                {/* Doctors List */}
                <div className="space-y-4">
                  {filteredDoctors.length > 0 ? (
                    filteredDoctors.map((doctor) => (
                      <div key={doctor.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                        <div className="flex justify-between items-start mb-3">
                          <div>
                            <h3 className="font-semibold text-lg">{doctor.fullName}</h3>
                            <p className="text-sm text-gray-500">{doctor.email}</p>
                            {doctor.specialization && (
                              <p className="text-sm text-gray-600 mt-1">Specialization: {doctor.specialization}</p>
                            )}
                          </div>
                          <div className="flex gap-2">
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => openEditModal(doctor)}
                              className="flex items-center gap-1"
                            >
                              <Edit className="w-4 h-4" />
                              Edit
                            </Button>
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => handleDeleteDoctor(doctor.id)}
                              className="flex items-center gap-1 text-red-600 hover:text-red-700"
                            >
                              <Trash2 className="w-4 h-4" />
                              Delete
                            </Button>
                          </div>
                        </div>
                        
                        <div className="grid md:grid-cols-2 gap-4 text-sm text-gray-600">
                          <div>
                            <p><span className="font-medium">Phone:</span> {doctor.phone || 'Not provided'}</p>
                            <p><span className="font-medium">User ID:</span> {doctor.id}</p>
                          </div>
                          <div>
                            <p><span className="font-medium">Role:</span> {doctor.role}</p>
                            <p><span className="font-medium">Status:</span> Active</p>
                          </div>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      <Stethoscope className="w-12 h-12 mx-auto mb-4 opacity-50" />
                      <p>No doctors found</p>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Create Doctor Modal */}
        {showCreateDoctorModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Create New Doctor</h2>
                <Button
                  variant="outline"
                  onClick={() => { setShowCreateDoctorModal(false); resetDoctorForm(); }}
                  className="p-2"
                >
                  <X className="w-4 h-4" />
                </Button>
              </div>
              
              <form onSubmit={handleCreateDoctor} className="space-y-4">
                <div className="grid md:grid-cols-2 gap-4">
                  <Input
                    label="Full Name"
                    value={doctorFormData.fullName}
                    onChange={(e) => setDoctorFormData({...doctorFormData, fullName: e.target.value})}
                    required
                  />
                  <Input
                    label="Email"
                    type="email"
                    value={doctorFormData.email}
                    onChange={(e) => setDoctorFormData({...doctorFormData, email: e.target.value})}
                    required
                  />
                </div>
                
                <div className="grid md:grid-cols-2 gap-4">
                  <Input
                    label="Phone"
                    value={doctorFormData.phone}
                    onChange={(e) => setDoctorFormData({...doctorFormData, phone: e.target.value})}
                    required
                  />
                  <Input
                    label="Password"
                    type="password"
                    value={doctorFormData.password}
                    onChange={(e) => setDoctorFormData({...doctorFormData, password: e.target.value})}
                    required
                  />
                </div>
                
                <div className="grid md:grid-cols-2 gap-4">
                  <Input
                    label="Specialization"
                    value={doctorFormData.specialization}
                    onChange={(e) => setDoctorFormData({...doctorFormData, specialization: e.target.value})}
                  />
                  <Input
                    label="License Number"
                    value={doctorFormData.licenseNumber}
                    onChange={(e) => setDoctorFormData({...doctorFormData, licenseNumber: e.target.value})}
                  />
                </div>
                
                <Input
                  label="Experience (years)"
                  type="number"
                  value={doctorFormData.experience}
                  onChange={(e) => setDoctorFormData({...doctorFormData, experience: e.target.value})}
                />
                
                <div className="flex gap-2 pt-4">
                  <Button
                    type="submit"
                    disabled={loading}
                    className="bg-[#4CAF50] hover:bg-[#45a049] flex items-center gap-2"
                  >
                    <Save className="w-4 h-4" />
                    Create Doctor
                  </Button>
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => { setShowCreateDoctorModal(false); resetDoctorForm(); }}
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

export default AdminDashboard;
