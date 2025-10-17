import axios from 'axios';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token and user ID
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('mediway_token');
    const userStr = localStorage.getItem('mediway_user');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add user ID to headers for backend to identify current user
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        if (user && user.id) {
          config.headers['X-User-Id'] = user.id;
        }
      } catch (e) {
        console.error('Error parsing user from localStorage:', e);
      }
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Unauthorized - clear local storage and redirect to login
      localStorage.removeItem('mediway_token');
      localStorage.removeItem('mediway_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
