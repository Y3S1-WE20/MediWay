import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, Link } from 'react-router-dom';
import { LogIn, Mail, Lock, User } from 'lucide-react';
import { Input } from '../components/ui/input';
import { Button } from '../components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { useAuth } from '../hooks/useAuth';
import api from '../api/api';
import { endpoints } from '../api/endpoints';

const Login = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [loading, setLoading] = useState(false);
  const [loginMode, setLoginMode] = useState('patient'); // 'patient', 'doctor', or 'admin'
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validate = () => {
    const newErrors = {};
    
    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email is invalid';
    }
    if (!formData.password) {
      newErrors.password = 'Password is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validate()) return;

    setLoading(true);

    try {
      let loginEndpoint = '/patients/login'; // Default for patient
      if (loginMode === 'doctor') loginEndpoint = '/doctors/login';
      if (loginMode === 'admin') loginEndpoint = '/admin/login';
      // Call backend API (no /api prefix)
      const response = await api.post(loginEndpoint, {
        email: formData.email,
        password: formData.password,
      });

      const data = response.data;

      // Create user object
      const userData = {
        id: data.userId,
        name: data.name,
        email: formData.email, // Since response doesn't include email
        role: data.role,
      };

      // Store user data (no token for now)
      login(userData);

      // Navigate based on role
      console.log('User role from login:', data.role);
      console.log('User data:', userData);

      if (data.role === 'ADMIN') {
        console.log('Redirecting to admin dashboard');
        navigate('/admin/dashboard');
      } else if (data.role === 'DOCTOR') {
        console.log('Redirecting to doctor dashboard');
        navigate('/doctor/dashboard');
      } else {
        console.log('Redirecting to appointments page');
        navigate('/appointments');
      }
    } catch (error) {
      console.error('Login error:', error);
      setErrors({ 
        submit: error.response?.data?.message || 'Invalid credentials. Please try again.' 
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#4CAF50]/5 via-white to-[#4CAF50]/10 flex items-center justify-center px-4 py-20">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
        className="w-full max-w-md"
      >
        <Card>
          <CardHeader>
            <div className="text-center">
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
                className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4"
              >
                <User className="w-8 h-8 text-[#4CAF50]" />
              </motion.div>
              <CardTitle className="text-3xl">Welcome Back</CardTitle>
              <p className="text-gray-600 mt-2">
                Login to access your health dashboard
              </p>
              <div className="flex justify-center gap-2 mt-4">
                <Button variant={loginMode === 'patient' ? 'default' : 'outline'} onClick={() => setLoginMode('patient')}>Patient Login</Button>
                <Button variant={loginMode === 'doctor' ? 'default' : 'outline'} onClick={() => setLoginMode('doctor')}>Doctor Login</Button>
                <Button variant={loginMode === 'admin' ? 'default' : 'outline'} onClick={() => setLoginMode('admin')}>Admin Login</Button>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6 px-6 pb-6">
              <Input
                label="Email Address"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleChange}
                error={errors.email}
                placeholder="john@example.com"
                required
              />

              <Input
                label="Password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                error={errors.password}
                placeholder="••••••••"
                required
              />

              <div className="flex items-center justify-between">
                <label className="flex items-center">
                  <input
                    type="checkbox"
                    className="w-4 h-4 text-[#4CAF50] border-gray-300 rounded focus:ring-[#4CAF50]"
                  />
                  <span className="ml-2 text-sm text-gray-600">Remember me</span>
                </label>
                <Link to="/forgot-password" className="text-sm text-[#4CAF50] hover:underline">
                  Forgot password?
                </Link>
              </div>

              {errors.submit && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="bg-red-50 text-red-600 p-3 rounded-lg text-sm"
                >
                  {errors.submit}
                </motion.div>
              )}

              <Button
                type="submit"
                disabled={loading}
                className="w-full bg-[#4CAF50] hover:bg-[#45a049] text-white py-3"
              >
                {loading ? (
                  <motion.div
                    animate={{ rotate: 360 }}
                    transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                    className="w-5 h-5 border-2 border-white border-t-transparent rounded-full mx-auto"
                  />
                ) : (
                  <span className="flex items-center justify-center">
                    <LogIn className="w-5 h-5 mr-2" />
                    Login
                  </span>
                )}
              </Button>

              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-gray-500">or</span>
                </div>
              </div>

              <p className="text-center text-sm text-gray-600">
                Don't have an account?{' '}
                <Link to="/register" className="text-[#4CAF50] hover:underline font-medium">
                  Register now
                </Link>
              </p>
            </form>
          </CardContent>
        </Card>

        {/* Demo credentials hint */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.5 }}
          className="mt-6 text-center"
        >
          <div className={`border rounded-lg p-4 ${
            loginMode === 'admin' 
              ? 'bg-blue-50 border-blue-200' 
              : 'bg-green-50 border-green-200'
          }`}>
            <p className={`text-sm font-medium ${
              loginMode === 'admin' ? 'text-blue-800' : 'text-green-800'
            }`}>
              {loginMode === 'admin' ? 'Admin Login' : 'Patient/Doctor Login'}
            </p>
            <p className={`text-xs mt-1 ${
              loginMode === 'admin' ? 'text-blue-700' : 'text-green-700'
            }`}>
              {loginMode === 'admin' 
                ? 'Use your admin credentials to access the admin dashboard'
                : 'Register first if you don\'t have an account'
              }
            </p>
          </div>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default Login;
