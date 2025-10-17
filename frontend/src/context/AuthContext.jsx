import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Load user from localStorage on mount
  useEffect(() => {
    try {
      const storedToken = localStorage.getItem('mediway_token');
      const storedUser = localStorage.getItem('mediway_user');
      
      if (storedToken && storedUser) {
        setToken(storedToken);
        setUser(JSON.parse(storedUser));
      }
    } catch (error) {
      console.error('Error loading auth data from localStorage:', error);
      // Clear corrupted data
      localStorage.removeItem('mediway_token');
      localStorage.removeItem('mediway_user');
    } finally {
      setLoading(false);
    }
  }, []);

  const login = (userData, authToken) => {
    try {
      setUser(userData);
      setToken(authToken);
      localStorage.setItem('mediway_token', authToken);
      localStorage.setItem('mediway_user', JSON.stringify(userData));
    } catch (error) {
      console.error('Error saving auth data to localStorage:', error);
    }
  };

  const logout = () => {
    try {
      setUser(null);
      setToken(null);
      localStorage.removeItem('mediway_token');
      localStorage.removeItem('mediway_user');
    } catch (error) {
      console.error('Error during logout:', error);
    }
  };

  const updateUser = (updatedData) => {
    try {
      const updatedUser = { ...user, ...updatedData };
      setUser(updatedUser);
      localStorage.setItem('mediway_user', JSON.stringify(updatedUser));
    } catch (error) {
      console.error('Error updating user data:', error);
    }
  };

  const isAdmin = () => user?.role === 'ADMIN';
  const isDoctor = () => user?.role === 'DOCTOR';
  const isPatient = () => user?.role === 'PATIENT';

  const value = {
    user,
    token,
    login,
    logout,
    updateUser,
    isAuthenticated: !!token,
    isAdmin,
    isDoctor,
    isPatient,
    loading
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
