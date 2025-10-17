import React, { createContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  // Load user from localStorage on mount
  useEffect(() => {
    const storedToken = localStorage.getItem('mediway_token');
    const storedUser = localStorage.getItem('mediway_user');
    
    if (storedUser) {
      setUser(JSON.parse(storedUser));
      setToken(storedToken || 'dummy-token');
    }
    setLoading(false);
  }, []);

  const login = (userData, authToken) => {
    setUser(userData);
    setToken(authToken || 'dummy-token'); // Use dummy token if none provided
    localStorage.setItem('mediway_token', authToken || 'dummy-token');
    localStorage.setItem('mediway_user', JSON.stringify(userData));
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('mediway_token');
    localStorage.removeItem('mediway_user');
  };

  const updateUser = (updatedData) => {
    const updatedUser = { ...user, ...updatedData };
    setUser(updatedUser);
    localStorage.setItem('mediway_user', JSON.stringify(updatedUser));
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
    isAuthenticated: !!user,
    isAdmin,
    isDoctor,
    isPatient,
    loading
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthContext;
