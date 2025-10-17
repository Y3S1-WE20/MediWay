import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { User, Mail, Phone, Calendar, QrCode, Edit2, Save, X, Download } from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Select } from '../components/ui/select';
import { Badge } from '../components/ui/badge';
import { useAuth } from '../context/AuthContext';
import api from '../api/api';

const Profile = () => {
  const { user, updateUser } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [qrCode, setQrCode] = useState(null);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    fullName: '',
    phone: '',
  });

  useEffect(() => {
    fetchProfile();
    if (user?.role === 'PATIENT') {
      fetchQRCode();
    }
  }, [user]);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/profile');
      console.log('Profile data:', response.data);
      setProfile(response.data);
      setFormData({
        fullName: response.data.name || '',  // Backend returns 'name', not 'fullName'
        phone: response.data.phone || '',
      });
    } catch (error) {
      console.error('Error fetching profile:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchQRCode = async () => {
    try {
      const response = await api.get('/profile/qrcode');
      setQrCode(response.data);
    } catch (error) {
      console.error('Error fetching QR code:', error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const response = await api.put('/profile', {
        name: formData.fullName,  // Send as 'name' to match backend
        phone: formData.phone
      });
      console.log('Profile update response:', response.data);
      setProfile(response.data);
      updateUser({ ...user, name: response.data.name, phone: response.data.phone });
      setIsEditing(false);
      alert('Profile updated successfully!');
    } catch (error) {
      console.error('Error updating profile:', error);
      alert('Failed to update profile. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      fullName: profile?.name || '',  // Backend returns 'name'
      phone: profile?.phone || '',
    });
    setIsEditing(false);
  };

  const downloadQRCode = () => {
    if (qrCode && qrCode.qrCodeImage) {
      const link = document.createElement('a');
      link.href = qrCode.qrCodeImage;
      link.download = `mediway-qr-${profile?.fullName?.replace(/\s+/g, '-')}.png`;
      link.click();
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 pt-24 pb-12 px-4 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#4CAF50] mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 pt-24 pb-12 px-4">
      <div className="container mx-auto max-w-4xl">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="text-4xl font-bold text-gray-800 mb-2">
            My Profile
          </h1>
          <p className="text-gray-600">
            Manage your personal information and health card
          </p>
        </motion.div>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* QR Code Card */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.2 }}
            className="lg:col-span-1"
          >
            <Card>
              <CardHeader>
                <CardTitle className="text-center">Health Card</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-center space-y-4">
                  {/* Profile Avatar */}
                  <motion.div
                    whileHover={{ scale: 1.05 }}
                    className="mx-auto w-24 h-24 bg-gradient-to-br from-[#4CAF50] to-[#45a049] rounded-full flex items-center justify-center"
                  >
                    <span className="text-4xl font-bold text-white">
                      {user?.name?.charAt(0).toUpperCase() || 'U'}
                    </span>
                  </motion.div>

                  <div>
                    <h3 className="text-xl font-bold text-gray-800">
                      {profile?.name || user?.name || 'User'}
                    </h3>
                    <p className="text-sm text-gray-600">
                      Patient ID: {profile?.id ? `PAT-${profile.id}` : 'N/A'}
                    </p>
                    <p className="text-xs text-gray-500 mt-1">
                      {profile?.role || 'PATIENT'}
                    </p>
                  </div>

                  {/* QR Code */}
                  {user?.role === 'PATIENT' && (
                    <div className="bg-white p-4 rounded-lg border-2 border-dashed border-gray-300">
                      {qrCode && qrCode.qrCodeImage ? (
                        <div className="w-48 h-48 mx-auto">
                          <img 
                            src={qrCode.qrCodeImage} 
                            alt="Patient QR Code" 
                            className="w-full h-full object-contain"
                          />
                          <p className="text-xs text-gray-500 mt-2 text-center">Scan for quick access</p>
                        </div>
                      ) : (
                        <div className="w-48 h-48 mx-auto bg-gradient-to-br from-gray-100 to-gray-200 rounded-lg flex items-center justify-center">
                          <div className="text-center">
                            <QrCode className="w-32 h-32 mx-auto text-gray-400 mb-2" />
                            <p className="text-xs text-gray-500">Loading QR Code...</p>
                          </div>
                        </div>
                      )}
                    </div>
                  )}

                  {user?.role === 'PATIENT' && qrCode && (
                    <div className="space-y-2">
                      <Button
                        variant="outline"
                        className="w-full"
                        onClick={() => window.print()}
                      >
                        <QrCode className="w-4 h-4 mr-2" />
                        Print Card
                      </Button>
                      <Button
                        variant="outline"
                        className="w-full"
                        onClick={downloadQRCode}
                      >
                        <Download className="w-4 h-4 mr-2" />
                        Download QR
                      </Button>
                    </div>
                  )}

                  <Badge variant="success" className="w-full justify-center">
                    Active Member
                  </Badge>
                </div>
              </CardContent>
            </Card>
          </motion.div>

          {/* Profile Information Card */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.3 }}
            className="lg:col-span-2"
          >
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <CardTitle>Personal Information</CardTitle>
                  {!isEditing ? (
                    <Button
                      onClick={() => setIsEditing(true)}
                      variant="outline"
                      size="sm"
                    >
                      <Edit2 className="w-4 h-4 mr-2" />
                      Edit
                    </Button>
                  ) : (
                    <div className="flex gap-2">
                      <Button
                        onClick={handleCancel}
                        variant="ghost"
                        size="sm"
                      >
                        <X className="w-4 h-4 mr-2" />
                        Cancel
                      </Button>
                      <Button
                        onClick={handleSave}
                        disabled={saving}
                        size="sm"
                        className="bg-[#4CAF50] hover:bg-[#45a049]"
                      >
                        {saving ? (
                          <motion.div
                            animate={{ rotate: 360 }}
                            transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                            className="w-4 h-4 border-2 border-white border-t-transparent rounded-full"
                          />
                        ) : (
                          <>
                            <Save className="w-4 h-4 mr-2" />
                            Save
                          </>
                        )}
                      </Button>
                    </div>
                  )}
                </div>
              </CardHeader>
              <CardContent>
                {isEditing ? (
                  <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="space-y-4"
                  >
                    <Input
                      label="Full Name"
                      name="name"
                      value={formData.name}
                      onChange={handleChange}
                      required
                    />

                    <div className="grid md:grid-cols-2 gap-4">
                      <Input
                        label="Email Address"
                        name="email"
                        type="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                      />
                      <Input
                        label="Phone Number"
                        name="phone"
                        type="tel"
                        value={formData.phone}
                        onChange={handleChange}
                        required
                      />
                    </div>

                    <div className="grid md:grid-cols-2 gap-4">
                      <Input
                        label="Date of Birth"
                        name="dateOfBirth"
                        type="date"
                        value={formData.dateOfBirth}
                        onChange={handleChange}
                        required
                      />
                      <Select
                        label="Gender"
                        name="gender"
                        value={formData.gender}
                        onChange={handleChange}
                        required
                      >
                        <option value="">Select Gender</option>
                        <option value="male">Male</option>
                        <option value="female">Female</option>
                        <option value="other">Other</option>
                      </Select>
                    </div>
                  </motion.div>
                ) : (
                  <div className="space-y-6">
                    <div className="grid md:grid-cols-2 gap-6">
                      <div className="flex items-start gap-3">
                        <div className="mt-1 w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0">
                          <User className="w-5 h-5 text-blue-600" />
                        </div>
                        <div>
                          <p className="text-sm text-gray-600">Full Name</p>
                          <p className="text-lg font-semibold text-gray-800">
                            {profile?.name || user?.name || 'Not set'}
                          </p>
                        </div>
                      </div>

                      <div className="flex items-start gap-3">
                        <div className="mt-1 w-10 h-10 bg-green-100 rounded-full flex items-center justify-center flex-shrink-0">
                          <Mail className="w-5 h-5 text-green-600" />
                        </div>
                        <div>
                          <p className="text-sm text-gray-600">Email</p>
                          <p className="text-lg font-semibold text-gray-800">
                            {profile?.email || user?.email || 'Not set'}
                          </p>
                        </div>
                      </div>

                      <div className="flex items-start gap-3">
                        <div className="mt-1 w-10 h-10 bg-purple-100 rounded-full flex items-center justify-center flex-shrink-0">
                          <Phone className="w-5 h-5 text-purple-600" />
                        </div>
                        <div>
                          <p className="text-sm text-gray-600">Phone</p>
                          <p className="text-lg font-semibold text-gray-800">
                            {profile?.phone || user?.phone || 'Not set'}
                          </p>
                        </div>
                      </div>

                      <div className="flex items-start gap-3">
                        <div className="mt-1 w-10 h-10 bg-orange-100 rounded-full flex items-center justify-center flex-shrink-0">
                          <Calendar className="w-5 h-5 text-orange-600" />
                        </div>
                        <div>
                          <p className="text-sm text-gray-600">Date of Birth</p>
                          <p className="text-lg font-semibold text-gray-800">
                            {profile?.dateOfBirth || user?.dateOfBirth ? new Date(profile?.dateOfBirth || user.dateOfBirth).toLocaleDateString('en-US', {
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric'
                            }) : 'Not set'}
                          </p>
                        </div>
                      </div>
                    </div>

                    <div className="pt-4 border-t border-gray-200">
                      <div className="flex items-start gap-3">
                        <div className="mt-1 w-10 h-10 bg-pink-100 rounded-full flex items-center justify-center flex-shrink-0">
                          <User className="w-5 h-5 text-pink-600" />
                        </div>
                        <div>
                          <p className="text-sm text-gray-600">Gender</p>
                          <p className="text-lg font-semibold text-gray-800 capitalize">
                            {profile?.gender || user?.gender || 'Not specified'}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Medical History Summary */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.4 }}
              className="mt-6"
            >
              <Card>
                <CardHeader>
                  <CardTitle>Medical Summary</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid md:grid-cols-3 gap-6">
                    <div className="text-center p-4 bg-blue-50 rounded-lg">
                      <p className="text-3xl font-bold text-blue-600">12</p>
                      <p className="text-sm text-gray-600 mt-1">Total Appointments</p>
                    </div>
                    <div className="text-center p-4 bg-green-50 rounded-lg">
                      <p className="text-3xl font-bold text-green-600">8</p>
                      <p className="text-sm text-gray-600 mt-1">Completed</p>
                    </div>
                    <div className="text-center p-4 bg-purple-50 rounded-lg">
                      <p className="text-3xl font-bold text-purple-600">$1,245</p>
                      <p className="text-sm text-gray-600 mt-1">Total Payments</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
