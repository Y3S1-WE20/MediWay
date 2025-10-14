# MediWay Frontend - Smart Health Management System

A modern, responsive React application for healthcare management built with React, TailwindCSS, React Router DOM, and Axios.

## 🚀 Features

- **User Authentication**: Secure registration and login system
- **Smart QR Health Card**: Digital health card with QR code for quick access
- **Appointment Management**: Book, view, and cancel appointments with ease
- **Online Payments**: Secure payment processing for medical bills
- **Hospital Analytics**: Comprehensive reports and charts for hospital insights
- **User Profile**: Manage personal information and view medical history
- **Responsive Design**: Optimized for mobile and desktop devices
- **Smooth Animations**: Framer Motion animations for enhanced UX

## 🛠️ Tech Stack

- **React 19**: Frontend framework
- **TailwindCSS 4**: Utility-first CSS framework
- **React Router DOM 6**: Client-side routing
- **Axios**: HTTP client for API calls
- **Recharts**: Charting library for analytics
- **Framer Motion**: Animation library
- **Lucide React**: Modern icon library
- **Vite**: Build tool and dev server

## 📁 Project Structure

```
frontend/
├── src/
│   ├── api/                  # API configuration and endpoints
│   │   ├── api.js           # Axios instance with interceptors
│   │   └── endpoints.js     # API endpoint constants
│   ├── components/          # Reusable components
│   │   ├── ui/              # UI components (Button, Card, Input, etc.)
│   │   ├── Navbar.jsx       # Navigation bar with auth awareness
│   │   ├── Footer.jsx       # Footer component
│   │   ├── HeroSection.jsx  # Home page hero section
│   │   ├── FeaturesSection.jsx
│   │   ├── AboutSection.jsx
│   │   └── ProtectedRoute.jsx
│   ├── context/             # React Context for state management
│   │   └── AuthContext.jsx  # Authentication context
│   ├── pages/               # Page components
│   │   ├── Home.jsx
│   │   ├── Register.jsx
│   │   ├── Login.jsx
│   │   ├── Appointments.jsx
│   │   ├── BookAppointment.jsx
│   │   ├── Payments.jsx
│   │   ├── Reports.jsx
│   │   └── Profile.jsx
│   ├── App.jsx              # Main app component with routing
│   ├── main.jsx             # Entry point
│   └── index.css            # Global styles
├── public/                  # Static assets
├── package.json
└── vite.config.js           # Vite configuration
```

## 🎨 Design System

- **Primary Color**: `#4CAF50` (Soft Green)
- **Background**: White with subtle gradients
- **Text**: Gray tones for readability
- **Components**: Rounded corners, clean spacing, subtle shadows
- **Animations**: Fade, slide, and scale effects on interactions

## 🚦 Routes

### Public Routes
- `/` - Home page with hero and features
- `/register` - User registration with QR generation
- `/login` - User login

### Protected Routes (Require Authentication)
- `/appointments` - View and manage appointments
- `/book-appointment` - Book new appointment
- `/payments` - View bills and make payments
- `/reports` - Hospital analytics dashboard
- `/profile` - User profile and health card

## 🔧 Installation & Setup

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Start development server**:
   ```bash
   npm run dev
   ```

3. **Build for production**:
   ```bash
   npm run build
   ```

4. **Preview production build**:
   ```bash
   npm run preview
   ```

## 🔌 Backend Integration

The app is configured to connect to a backend API at `http://localhost:8080/api`. 

### API Endpoints Used:
- `POST /api/patients/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/appointments` - Get appointments
- `POST /api/appointments` - Create appointment
- `DELETE /api/appointments/:id` - Cancel appointment
- `GET /api/payments` - Get payment history
- `POST /api/payments` - Process payment
- `GET /api/reports` - Get hospital analytics
- `GET /api/patients/:id` - Get patient profile
- `PUT /api/patients/:id` - Update patient profile

### Currently Running in Demo Mode
All pages currently use mock data for demonstration. To connect to your backend:

1. Update the `baseURL` in `src/api/api.js` if your backend runs on a different URL
2. Uncomment the actual API calls in each page component
3. Remove the simulated `setTimeout` calls

## 🎯 Key Features Implementation

### Authentication Context
- Manages user state across the app
- Stores JWT token and user data in localStorage
- Provides login, logout, and updateUser functions
- Protects routes with ProtectedRoute component

### Responsive Navigation
- Mobile-friendly hamburger menu
- Auth-aware navigation (different links for logged in/out users)
- Smooth scroll effect on navbar
- Active route highlighting

### Smooth Animations
- Page transitions with Framer Motion
- Hover effects on cards and buttons
- Loading states with animated spinners
- Staggered list animations

### Form Validation
- Real-time validation feedback
- Error messages for invalid inputs
- Disabled submit buttons during processing

## 🎨 UI Components

All UI components are built with TailwindCSS and support:
- Multiple variants (default, success, warning, danger, etc.)
- Different sizes (sm, default, lg)
- Hover and focus states
- Accessibility features

## 📱 Responsive Design

- Mobile-first approach
- Breakpoints: `sm`, `md`, `lg`, `xl`
- Touch-friendly interactive elements
- Optimized layouts for all screen sizes

## 🔐 Security Features

- JWT token-based authentication
- Axios interceptors for automatic token injection
- Protected routes with authentication checks
- Secure logout with token cleanup
- 401 response handling with auto-redirect

## 🚀 Production Deployment

1. Build the app:
   ```bash
   npm run build
   ```

2. The build output will be in the `dist` folder

3. Deploy to your preferred hosting service (Vercel, Netlify, etc.)

4. Update environment variables for production API URL

## 📝 Notes

- This is a frontend-only application currently using mock data
- Backend integration points are marked with comments in the code
- All placeholder API calls can be replaced with actual API calls
- Design follows the MediWay technical architecture document

## 🤝 Contributing

Follow the existing code structure and design patterns when adding new features.

## 📄 License

All rights reserved © 2025 MediWay
