import React, { useState, useEffect } from 'react';
import { Download } from 'lucide-react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend,
  LineChart, Line, PieChart, Pie, Cell, AreaChart, Area
} from 'recharts';
import { useAuth } from '../hooks/useAuth';
import { 
  Shield, Users, Calendar, Plus, Trash2, 
  Search, X, Save, Stethoscope, FileText
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');
  const [doctors, setDoctors] = useState([]);
  const [patients, setPatients] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [users, setUsers] = useState([]);
  const [payments, setPayments] = useState([]);
  const [dashboardStats, setDashboardStats] = useState({});
  const [showCreateUserModal, setShowCreateUserModal] = useState(false);
  const [userFormData, setUserFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'PATIENT',
    phone: ''
  });
  const [searchUserTerm, setSearchUserTerm] = useState('');
  const [searchPatientTerm, setSearchPatientTerm] = useState('');
  const [searchAppointmentTerm, setSearchAppointmentTerm] = useState('');
  const [searchPaymentTerm, setSearchPaymentTerm] = useState('');
  const [appointmentStatusFilter, setAppointmentStatusFilter] = useState('ALL');
  const [paymentStatusFilter, setPaymentStatusFilter] = useState('ALL');
  const [loading, setLoading] = useState(false);
  const [showCreateDoctorModal, setShowCreateDoctorModal] = useState(false);
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

  // Fetch all dashboard data
  const fetchDashboardData = async () => {
    // Fetch dashboard stats for analytics
    try {
      const statsResponse = await fetch('http://localhost:8080/reports/dashboard');
      const statsData = await statsResponse.json();
      setDashboardStats(statsData);
    } catch (e) { setDashboardStats({}); }

    // Fetch all payments for analytics tab
    try {
      const paymentsResponse = await fetch('http://localhost:8080/reports/payments');
      const paymentsData = await paymentsResponse.json();
      setPayments(paymentsData);
    } catch (e) { setPayments([]); }
    setLoading(true);
    try {
      // Fetch users (for Manage Users tab)
      const usersResponse = await fetch('http://localhost:8080/admin/users');
      const usersData = await usersResponse.json();
      setUsers(usersData);

      // Fetch doctors
      const doctorsResponse = await fetch('http://localhost:8080/admin/doctors');
      const doctorsData = await doctorsResponse.json();
      setDoctors(doctorsData);

      // Fetch patients (assuming all users with role PATIENT)
      const patientsData = usersData.filter(u => u.role === 'PATIENT');
      setPatients(patientsData);

      // Fetch appointments
      const appointmentsResponse = await fetch('http://localhost:8080/admin/appointments');
      const appointmentsData = await appointmentsResponse.json();
      setAppointments(appointmentsData);

      // Update stats
      setStats([
        { title: 'Total Patients', value: patientsData.length.toString(), icon: Users, color: 'bg-blue-500' },
        { title: 'Total Doctors', value: doctorsData.length.toString(), icon: Stethoscope, color: 'bg-green-500' },
        { title: 'Total Appointments', value: appointmentsData.length.toString(), icon: Calendar, color: 'bg-purple-500' },
        { title: 'Active Users', value: (usersData.length).toString(), icon: Shield, color: 'bg-orange-500' },
      ]);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  // User CRUD handlers
  const handleCreateUser = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/admin/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userFormData),
      });
      if (response.ok) {
        const newUser = await response.json();
        setUsers([...users, newUser]);
        alert('User created successfully!');
        setShowCreateUserModal(false);
        setUserFormData({ name: '', email: '', password: '', role: 'PATIENT', phone: '' });
        fetchDashboardData();
      } else {
        alert('Error creating user');
      }
    } catch (error) {
      alert('Error creating user');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteUser = async (userId) => {
    if (window.confirm('Are you sure you want to delete this user?')) {
      try {
        const response = await fetch(`http://localhost:8080/admin/users/${userId}`, { method: 'DELETE' });
        if (response.ok) {
          setUsers(users.filter(u => u.id !== userId));
          alert('User deleted successfully!');
          fetchDashboardData();
        } else {
          alert('Error deleting user');
        }
      } catch (error) {
        alert('Error deleting user');
      }
    }
  };

  // Report download handlers
  // Report download handlers removed (now in Reports.jsx)

  const handleCreateDoctor = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/admin/doctors', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          name: doctorFormData.fullName,
          email: doctorFormData.email,
          password: doctorFormData.password,
          specialization: doctorFormData.specialization,
          phone: doctorFormData.phone,
          available: true
        }),
      });

      if (response.ok) {
        const newDoctor = await response.json();
        setDoctors([...doctors, newDoctor]);
        alert('Doctor created successfully!');
        setShowCreateDoctorModal(false);
        resetDoctorForm();
        fetchDashboardData(); // Refresh data
      } else {
        const errorData = await response.json();
        alert('Error creating doctor: ' + (errorData.message || 'Please try again'));
      }
    } catch (error) {
      console.error('Error creating doctor:', error);
      alert('Error creating doctor: ' + (error.message || 'Please try again'));
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteDoctor = async (doctorId) => {
    if (window.confirm('Are you sure you want to delete this doctor?')) {
      try {
        const response = await fetch(`http://localhost:8080/admin/doctors/${doctorId}`, {
          method: 'DELETE',
        });

        if (response.ok) {
          setDoctors(doctors.filter(d => d.id !== doctorId));
          alert('Doctor deleted successfully!');
          fetchDashboardData(); // Refresh data
        } else {
          alert('Error deleting doctor: Please try again');
        }
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
  };

  const filteredDoctors = doctors.filter(doctor =>
    doctor.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doctor.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    doctor.specialization?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4">
        <div
          className="mb-8"
        >
          <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="text-gray-600 mt-2">
            Welcome back, {user?.name} ({user?.role})
          </p>
        </div>

        {/* Tabs */}
        <div className="mb-6">
          <nav className="flex space-x-1 bg-white rounded-lg p-1 shadow-sm">
            {[
              { id: 'overview', name: 'Overview', icon: Shield },
              { id: 'users', name: 'Manage Users', icon: Users },
              { id: 'doctors', name: 'Manage Doctors', icon: Stethoscope },
              { id: 'patients', name: 'Patients', icon: Users },
              { id: 'appointments', name: 'Appointments', icon: Calendar },
              { id: 'payments', name: 'Payments', icon: FileText },
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
            <a href="/reports">
              <Button variant="outline">Reports</Button>
            </a>
          </nav>
        </div>
        {/* Manage Users Tab */}
        {activeTab === 'users' && (
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <div className="flex justify-between items-center">
                  <CardTitle>User Management</CardTitle>
                  <Button onClick={() => setShowCreateUserModal(true)} className="bg-[#4CAF50] hover:bg-[#45a049] flex items-center gap-2">
                    <Plus className="w-4 h-4" /> Add New User
                  </Button>
                </div>
              </CardHeader>
              <CardContent>
                <div className="mb-6">
                  <div className="relative">
                    <Search className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                    <Input
                      placeholder="Search users by name, email, or role..."
                      value={searchUserTerm}
                      onChange={(e) => setSearchUserTerm(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>
                <div className="space-y-4">
                  {users.filter(user =>
                    user.name?.toLowerCase().includes(searchUserTerm.toLowerCase()) ||
                    user.email?.toLowerCase().includes(searchUserTerm.toLowerCase()) ||
                    user.role?.toLowerCase().includes(searchUserTerm.toLowerCase())
                  ).map(user => (
                    <div key={user.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                      <div className="flex justify-between items-center">
                        <div>
                          <h3 className="font-semibold text-lg">{user.name}</h3>
                          <p className="text-sm text-gray-500">{user.email}</p>
                          <p className="text-sm text-gray-600 mt-1">Role: {user.role}</p>
                        </div>
                        <div className="flex gap-2">
                          <Button size="sm" variant="outline" onClick={() => handleDeleteUser(user.id)} className="flex items-center gap-1 text-red-600 hover:text-red-700">
                            <Trash2 className="w-4 h-4" /> Delete
                          </Button>
                        </div>
                      </div>
                    </div>
                  ))}
                  {users.length === 0 && (
                    <div className="text-center py-8 text-gray-500">
                      <Users className="w-12 h-12 mx-auto mb-4 opacity-50" />
                      <p>No users found</p>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Create User Modal */}
        {showCreateUserModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Create New User</h2>
                <Button variant="outline" onClick={() => setShowCreateUserModal(false)} className="p-2">
                  <X className="w-4 h-4" />
                </Button>
              </div>
              <form onSubmit={handleCreateUser} className="space-y-4">
                <div className="grid md:grid-cols-2 gap-4">
                  <Input label="Name" value={userFormData.name} onChange={e => setUserFormData({ ...userFormData, name: e.target.value })} required />
                  <Input label="Email" type="email" value={userFormData.email} onChange={e => setUserFormData({ ...userFormData, email: e.target.value })} required />
                </div>
                <div className="grid md:grid-cols-2 gap-4">
                  <Input label="Password" type="password" value={userFormData.password} onChange={e => setUserFormData({ ...userFormData, password: e.target.value })} required />
                  <Input label="Phone" value={userFormData.phone} onChange={e => setUserFormData({ ...userFormData, phone: e.target.value })} />
                </div>
                <div className="grid md:grid-cols-2 gap-4">
                  <label className="block text-sm font-medium text-gray-700">Role
                    <select value={userFormData.role} onChange={e => setUserFormData({ ...userFormData, role: e.target.value })} className="mt-1 block w-full border-gray-300 rounded-md">
                      <option value="PATIENT">Patient</option>
                      <option value="DOCTOR">Doctor</option>
                      <option value="ADMIN">Admin</option>
                    </select>
                  </label>
                </div>
                <div className="flex gap-2 pt-4">
                  <Button type="submit" disabled={loading} className="bg-[#4CAF50] hover:bg-[#45a049] flex items-center gap-2">
                    <Save className="w-4 h-4" /> Create User
                  </Button>
                  <Button type="button" variant="outline" onClick={() => setShowCreateUserModal(false)}>Cancel</Button>
                </div>
              </form>
            </div>
          </div>
        )}
            {/* Report Download Buttons */}
            {/* Report Download Buttons removed; see Reports page */}

        {/* Overview Tab */}
        {activeTab === 'overview' && (
          <div className="space-y-6">
            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              {stats.map((stat) => (
                <div key={stat.title}>
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
                </div>
              ))}
            </div>

            {/* 1. Patient–Doctor–Appointment Overview (Bar Chart) */}
            <div className="bg-white rounded-lg shadow p-6 mt-6">
              <h2 className="text-lg font-semibold mb-4">Patient–Doctor–Appointment Overview</h2>
              <ResponsiveContainer width="100%" height={220}>
                <BarChart
                  data={[{
                    name: 'Patients', count: patients.length
                  }, {
                    name: 'Doctors', count: doctors.length
                  }, {
                    name: 'Appointments', count: appointments.length
                  }]}
                  margin={{ top: 20, right: 30, left: 0, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="count" fill="#4CAF50" />
                </BarChart>
              </ResponsiveContainer>
            </div>

            {/* 3. Payment Status Distribution (Pie/Donut Chart) */}
            <div className="bg-white rounded-lg shadow p-6 mt-6">
              <h2 className="text-lg font-semibold mb-4">Payment Status Distribution</h2>
              <ResponsiveContainer width="100%" height={220}>
                <PieChart>
                  <Pie
                    data={Object.entries(dashboardStats.paymentsByStatus || {}).map(([status, value]) => ({ name: status, value }))}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={80}
                    fill="#8884d8"
                    label
                  >
                    {Object.entries(dashboardStats.paymentsByStatus || {}).map((entry, idx) => (
                      <Cell key={`cell-${idx}`} fill={["#4CAF50", "#FF9800", "#F44336"][idx % 3]} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>

            {/* Quick Actions */}
            <Card>
              <CardHeader>
                <CardTitle>Quick Actions</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
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
                  <a href="/reports">
                    <Button variant="outline">Reports</Button>
                  </a>
                </div>
              </CardContent>
            </Card>
          </div>
        )}
        {/* Payments Tab */}
        {activeTab === 'payments' && (
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Payments</CardTitle>
              </CardHeader>
              <CardContent>
                {/* Search and Filter Controls */}
                <div className="mb-6 space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {/* Search Bar */}
                    <div className="relative">
                      <Search className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                      <Input
                        placeholder="Search by payment ID, user ID, or amount..."
                        value={searchPaymentTerm}
                        onChange={(e) => setSearchPaymentTerm(e.target.value)}
                        className="pl-10"
                      />
                    </div>
                    
                    {/* Status Filter */}
                    <select
                      value={paymentStatusFilter}
                      onChange={(e) => setPaymentStatusFilter(e.target.value)}
                      className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#4CAF50]"
                    >
                      <option value="ALL">All Statuses</option>
                      <option value="COMPLETED">Completed</option>
                      <option value="PENDING">Pending</option>
                      <option value="FAILED">Failed</option>
                      <option value="REFUNDED">Refunded</option>
                    </select>
                  </div>
                </div>

                <div className="space-y-2">
                  {payments.filter(payment => {
                    const matchesSearch = 
                      payment.id?.toString().includes(searchPaymentTerm) ||
                      payment.userId?.toString().includes(searchPaymentTerm) ||
                      payment.amount?.toString().includes(searchPaymentTerm);
                    
                    const matchesStatus = 
                      paymentStatusFilter === 'ALL' || payment.status === paymentStatusFilter;
                    
                    return matchesSearch && matchesStatus;
                  }).length > 0 ? (
                    payments.filter(payment => {
                      const matchesSearch = 
                        payment.id?.toString().includes(searchPaymentTerm) ||
                        payment.userId?.toString().includes(searchPaymentTerm) ||
                        payment.amount?.toString().includes(searchPaymentTerm);
                      
                      const matchesStatus = 
                        paymentStatusFilter === 'ALL' || payment.status === paymentStatusFilter;
                      
                      return matchesSearch && matchesStatus;
                    }).map((payment) => (
                      <div key={payment.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                        <div className="flex justify-between items-start">
                          <div>
                            <h3 className="font-semibold text-lg">Payment ID: {payment.id}</h3>
                            <p className="text-sm text-gray-500">User ID: {payment.userId}</p>
                            <p className="text-sm text-gray-600 mt-1">Amount: ₹{payment.amount}</p>
                            <p className="text-sm text-gray-600">Date: {payment.paymentDate}</p>
                          </div>
                          <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                            payment.status === 'COMPLETED' ? 'bg-green-500 text-white' : 
                            payment.status === 'PENDING' ? 'bg-yellow-500 text-white' : 
                            payment.status === 'FAILED' ? 'bg-red-500 text-white' : 
                            payment.status === 'REFUNDED' ? 'bg-blue-500 text-white' : 
                            'bg-gray-400 text-white'
                          }`}>
                            {payment.status}
                          </span>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      <FileText className="w-12 h-12 mx-auto mb-4 opacity-50" />
                      <p>{searchPaymentTerm || paymentStatusFilter !== 'ALL' ? 'No payments match your search criteria' : 'No payments found'}</p>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        )}
        {/* Patients Tab */}
        {activeTab === 'patients' && (
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Patients</CardTitle>
              </CardHeader>
              <CardContent>
                {/* Search Bar */}
                <div className="mb-6">
                  <div className="relative">
                    <Search className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                    <Input
                      placeholder="Search patients by name, email, or ID..."
                      value={searchPatientTerm}
                      onChange={(e) => setSearchPatientTerm(e.target.value)}
                      className="pl-10"
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  {patients.filter(patient =>
                    patient.name?.toLowerCase().includes(searchPatientTerm.toLowerCase()) ||
                    patient.email?.toLowerCase().includes(searchPatientTerm.toLowerCase()) ||
                    patient.id?.toString().includes(searchPatientTerm)
                  ).length > 0 ? (
                    patients.filter(patient =>
                      patient.name?.toLowerCase().includes(searchPatientTerm.toLowerCase()) ||
                      patient.email?.toLowerCase().includes(searchPatientTerm.toLowerCase()) ||
                      patient.id?.toString().includes(searchPatientTerm)
                    ).map((patient) => (
                      <div key={patient.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                        <div className="flex justify-between items-center">
                          <div>
                            <h3 className="font-semibold text-lg">{patient.name}</h3>
                            <p className="text-sm text-gray-500">{patient.email}</p>
                            {patient.phone && <p className="text-sm text-gray-600 mt-1">Phone: {patient.phone}</p>}
                          </div>
                          <div className="text-sm text-gray-600 bg-gray-100 px-3 py-1 rounded-full">ID: {patient.id}</div>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="text-center py-8 text-gray-500">
                      <Users className="w-12 h-12 mx-auto mb-4 opacity-50" />
                      <p>{searchPatientTerm ? 'No patients match your search' : 'No patients found'}</p>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        {/* Appointments Tab */}
        {activeTab === 'appointments' && (
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Appointments</CardTitle>
              </CardHeader>
              <CardContent>
                {/* Search and Filter Controls */}
                <div className="mb-6 space-y-4">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {/* Search Bar */}
                    <div className="relative">
                      <Search className="w-5 h-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
                      <Input
                        placeholder="Search by patient, doctor name, or ID..."
                        value={searchAppointmentTerm}
                        onChange={(e) => setSearchAppointmentTerm(e.target.value)}
                        className="pl-10"
                      />
                    </div>
                    
                    {/* Status Filter */}
                    <select
                      value={appointmentStatusFilter}
                      onChange={(e) => setAppointmentStatusFilter(e.target.value)}
                      className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-[#4CAF50]"
                    >
                      <option value="ALL">All Statuses</option>
                      <option value="PENDING">Pending</option>
                      <option value="SCHEDULED">Scheduled</option>
                      <option value="CONFIRMED">Confirmed</option>
                      <option value="COMPLETED">Completed</option>
                      <option value="CANCELLED">Cancelled</option>
                    </select>
                  </div>
                </div>

                {appointments.filter(appt => {
                  const matchesSearch = 
                    appt.patientName?.toLowerCase().includes(searchAppointmentTerm.toLowerCase()) ||
                    appt.patient?.name?.toLowerCase().includes(searchAppointmentTerm.toLowerCase()) ||
                    appt.doctorName?.toLowerCase().includes(searchAppointmentTerm.toLowerCase()) ||
                    appt.doctor?.name?.toLowerCase().includes(searchAppointmentTerm.toLowerCase()) ||
                    appt.patientId?.toString().includes(searchAppointmentTerm) ||
                    appt.doctorId?.toString().includes(searchAppointmentTerm);
                  
                  const matchesStatus = 
                    appointmentStatusFilter === 'ALL' || appt.status === appointmentStatusFilter;
                  
                  return matchesSearch && matchesStatus;
                }).length > 0 ? (
                  <div className="overflow-x-auto rounded-lg border border-gray-200">
                    <table className="min-w-full divide-y divide-gray-200">
                      <thead className="bg-gradient-to-r from-gray-50 to-gray-100">
                        <tr>
                          <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Patient Name</th>
                          <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Patient ID</th>
                          <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Doctor Name</th>
                          <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Doctor ID</th>
                          <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Date</th>
                          <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Time</th>
                          <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Status</th>
                          <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">Actions</th>
                        </tr>
                      </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
                        {appointments.filter(appt => {
                          const matchesSearch = 
                            appt.patientName?.toLowerCase().includes(searchAppointmentTerm.toLowerCase()) ||
                            appt.patient?.name?.toLowerCase().includes(searchAppointmentTerm.toLowerCase()) ||
                            appt.doctorName?.toLowerCase().includes(searchAppointmentTerm.toLowerCase()) ||
                            appt.doctor?.name?.toLowerCase().includes(searchAppointmentTerm.toLowerCase()) ||
                            appt.patientId?.toString().includes(searchAppointmentTerm) ||
                            appt.doctorId?.toString().includes(searchAppointmentTerm);
                          
                          const matchesStatus = 
                            appointmentStatusFilter === 'ALL' || appt.status === appointmentStatusFilter;
                          
                          return matchesSearch && matchesStatus;
                        }).map((appt, idx) => {
                          const dateObj = appt.appointmentDate ? new Date(appt.appointmentDate) : null;
                          const date = dateObj ? dateObj.toISOString().split('T')[0] : (appt.date || '');
                          const time = dateObj ? dateObj.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false }) : (appt.time || '--:--');
                          const canAction = appt.status === 'SCHEDULED' || appt.status === 'PENDING';
                          return (
                            <tr key={appt.id} className="hover:bg-gray-50 transition-colors">
                              <td className="px-6 py-4 font-medium text-gray-900">{appt.patientName || appt.patient?.name || `Patient ${appt.patientId}`}</td>
                              <td className="px-6 py-4 text-gray-700">{appt.patientId}</td>
                              <td className="px-6 py-4 text-gray-700">{appt.doctorName || appt.doctor?.name || `Doctor ${appt.doctorId}`}</td>
                              <td className="px-6 py-4 text-gray-700">{appt.doctorId}</td>
                              <td className="px-6 py-4 text-gray-700">{date}</td>
                              <td className="px-6 py-4 text-gray-700">{time}</td>
                              <td className="px-6 py-4">
                                <span className={`px-3 py-1 rounded-full text-xs font-semibold ${appt.status === 'COMPLETED' ? 'bg-green-500 text-white' : appt.status === 'CONFIRMED' || appt.status === 'SCHEDULED' ? 'bg-blue-500 text-white' : appt.status === 'CANCELLED' ? 'bg-gray-500 text-white' : appt.status === 'PENDING' ? 'bg-red-500 text-white' : 'bg-gray-400 text-white'}`}>{appt.status}</span>
                              </td>
                              <td className="px-6 py-4">
                                {canAction ? (
                                  <div className="flex gap-2">
                                    <Button size="sm" className="bg-green-500 hover:bg-green-600 text-white" onClick={async () => {
                                      // Accept: set status to COMPLETED
                                      await fetch(`http://localhost:8080/admin/appointments/${appt.id}/status?status=COMPLETED`, { method: 'PUT' });
                                      fetchDashboardData();
                                    }}>Accept</Button>
                                    <Button size="sm" className="bg-red-500 hover:bg-red-600 text-white" onClick={async () => {
                                      const reason = prompt('Enter rejection reason:');
                                      if (!reason) return;
                                      await fetch(`http://localhost:8080/admin/appointments/${appt.id}/status?status=CANCELLED&reason=${encodeURIComponent(reason)}`, { method: 'PUT' });
                                      fetchDashboardData();
                                    }}>Reject</Button>
                                  </div>
                                ) : null}
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="text-center py-8 text-gray-500">
                    <Calendar className="w-12 h-12 mx-auto mb-4 opacity-50" />
                    <p>{searchAppointmentTerm || appointmentStatusFilter !== 'ALL' ? 'No appointments match your search criteria' : 'No appointments found'}</p>
                  </div>
                )}
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
                {activeTab === 'payments' && (
                  <div className="space-y-6">
                    <Card>
                      <CardHeader>
                        <CardTitle>Payments</CardTitle>
                      </CardHeader>
                      <CardContent>
                        <div className="space-y-2">
                          {payments.length > 0 ? (
                            payments.map((payment) => (
                              <div key={payment.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                                <div className="flex justify-between items-center">
                                  <div>
                                    <h3 className="font-semibold text-lg">Payment ID: {payment.id}</h3>
                                    <p className="text-sm text-gray-500">User ID: {payment.userId}</p>
                                    <p className="text-sm text-gray-600">Amount: ₹{payment.amount}</p>
                                    <p className="text-sm text-gray-600">Status: {payment.status}</p>
                                    <p className="text-sm text-gray-600">Date: {payment.paymentDate}</p>
                                  </div>
                                </div>
                              </div>
                            ))
                          ) : (
                            <div className="text-center py-8 text-gray-500">
                              <FileText className="w-12 h-12 mx-auto mb-4 opacity-50" />
                              <p>No payments found</p>
                            </div>
                          )}
                        </div>
                      </CardContent>
                    </Card>
                  </div>
                )}
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
                            <h3 className="font-semibold text-lg">{doctor.name}</h3>
                            <p className="text-sm text-gray-500">{doctor.email}</p>
                            {doctor.specialization && (
                              <p className="text-sm text-gray-600 mt-1">Specialization: {doctor.specialization}</p>
                            )}
                          </div>
                          <div className="flex gap-2">
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
