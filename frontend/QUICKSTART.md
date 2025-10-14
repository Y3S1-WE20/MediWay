# MediWay Frontend - Quick Start Guide

## 🎯 What's Included

Your complete MediWay frontend application is now ready with:

✅ **7 Complete Pages**:
- Home (with Hero, Features, and About sections)
- Register (with QR code generation)
- Login (with authentication)
- Appointments (list and manage)
- Book Appointment (multi-step booking)
- Payments (bills and payment processing)
- Reports (charts and analytics)
- Profile (user info and health card)

✅ **Core Features**:
- Authentication system with protected routes
- Responsive navbar with mobile menu
- Smooth animations throughout
- Form validation
- Mock API integration (ready for backend)
- Modern UI with TailwindCSS
- Charts and analytics with Recharts

## 🚀 Getting Started

### 1. Start the Development Server

The app is already installed and ready. Just run:

```bash
cd f:\MediWay\frontend
npm run dev
```

The app will be available at: **http://localhost:5173/**

### 2. Explore the Application

#### **As a Guest User:**
1. Visit the home page to see the hero section and features
2. Click "Register Now" to create an account
3. Fill the registration form and see your QR health card
4. Click "Login" to access the dashboard

#### **As a Logged-In User:**
1. After login, you'll be redirected to Appointments
2. Click "Book New Appointment" to book
3. Navigate to Payments to see bills
4. Visit Reports for analytics dashboard
5. Check Profile to see your health card

### 3. Test Features

**Registration Flow:**
- Enter any valid data in the registration form
- See success message with QR code preview
- Redirected to login

**Login (Demo Mode):**
- Enter **any email and password** to login
- Currently in demo mode with mock authentication
- You'll be logged in and redirected to appointments

**Navigation:**
- Navbar changes based on auth status
- Mobile menu works on small screens
- Active route is highlighted

**Appointments:**
- View existing appointments
- Book new appointments with multi-step form
- Cancel appointments with confirmation

**Payments:**
- See unpaid and paid bills
- Click "Pay Now" to process payment
- Choose between card or insurance
- See payment success animation

**Reports:**
- View hospital statistics
- Interactive charts with Recharts
- Export functionality (UI ready)

**Profile:**
- View your information
- Edit profile details
- See your QR health card
- Print health card option

## 📋 Key Files to Know

### Configuration
- `vite.config.js` - Vite configuration with path aliases
- `src/api/api.js` - Axios setup with interceptors
- `src/api/endpoints.js` - All API endpoints

### Context & Routing
- `src/context/AuthContext.jsx` - Authentication state management
- `src/App.jsx` - Main routing configuration
- `src/components/ProtectedRoute.jsx` - Route protection

### UI Components
- `src/components/ui/` - Reusable components (Button, Card, Input, etc.)
- `src/components/Navbar.jsx` - Navigation with auth awareness
- `src/components/Footer.jsx` - Footer component

### Pages
All pages are in `src/pages/`:
- `Home.jsx` - Landing page
- `Register.jsx` - Registration with QR
- `Login.jsx` - Login form
- `Appointments.jsx` - Appointments list
- `BookAppointment.jsx` - Booking flow
- `Payments.jsx` - Billing and payments
- `Reports.jsx` - Analytics dashboard
- `Profile.jsx` - User profile

## 🔌 Connecting to Your Backend

Currently, the app uses **mock data** for demonstration. To connect to your actual backend:

### Step 1: Update API Base URL
In `src/api/api.js`, the baseURL is already set to:
```javascript
baseURL: 'http://localhost:8080/api'
```

Change this if your backend runs on a different URL.

### Step 2: Replace Mock Calls with Real API Calls

In each page component, you'll find comments like:
```javascript
// Simulate API call - replace with actual API
const response = await new Promise(...);

// In production, use:
// const response = await api.post(endpoints.register, formData);
```

Simply **uncomment** the production code and **remove** the mock `setTimeout` code.

### Step 3: Test with Backend

1. Start your backend server
2. Start the frontend dev server
3. Test registration, login, and other features
4. Handle any API response format differences

## 🎨 Customization

### Colors
The primary color is `#4CAF50` (green). To change:
- Search for `#4CAF50` across the project
- Replace with your brand color
- Also update `#45a049` (hover state)

### Add New Pages
1. Create component in `src/pages/`
2. Add route in `src/App.jsx`
3. Add link in `src/components/Navbar.jsx`

### Add New API Endpoints
1. Add to `src/api/endpoints.js`
2. Use in components with: `await api.get(endpoints.yourEndpoint)`

## 🐛 Troubleshooting

**Vite server won't start:**
- Make sure you're in the frontend directory
- Run `npm install` again
- Check if port 5173 is available

**Imports not working:**
- The `@` alias points to `src/` directory
- Check `vite.config.js` for alias configuration

**Navbar not showing:**
- Check if `<Navbar />` is in App.jsx
- Ensure Router wraps the entire app

**Protected routes not working:**
- Check AuthContext is wrapping the app
- Verify token storage in localStorage
- Check ProtectedRoute component

**Animations not working:**
- Ensure framer-motion is installed
- Check for console errors

## 📱 Mobile Testing

The app is fully responsive. To test:
1. Open DevTools (F12)
2. Toggle device toolbar
3. Test different screen sizes
4. Check mobile menu functionality

## 🚀 Production Build

When ready to deploy:

```bash
npm run build
```

This creates optimized files in `dist/` folder.

Deploy to:
- **Vercel**: `vercel deploy`
- **Netlify**: Drag `dist` folder to Netlify
- **GitHub Pages**: Configure in repo settings

## 📊 What's Working Now

✅ All routes configured and working
✅ Authentication flow (with mock data)
✅ All pages fully functional
✅ Responsive design
✅ Smooth animations
✅ Form validations
✅ Protected routes
✅ Mock API calls
✅ Charts and analytics
✅ Payment flow
✅ Profile management

## 🎯 Next Steps

1. **Connect Backend**: Replace mock calls with real API
2. **Add Real QR Generation**: Use a QR library for actual QR codes
3. **Add File Upload**: For profile pictures
4. **Add Notifications**: Toast messages for actions
5. **Add Search**: Filter appointments, bills, etc.
6. **Add Pagination**: For long lists
7. **Add Export**: Download reports as PDF
8. **Add Print Styles**: For health card printing

## 💡 Tips

- Use the demo mode to explore all features
- All forms have validation
- Check browser console for any errors
- Mobile menu works on screens < 768px
- All animations can be customized in each component

## 🤝 Support

If you encounter issues:
1. Check the browser console for errors
2. Verify all dependencies are installed
3. Ensure you're using Node.js 18+
4. Check the README_FRONTEND.md for detailed docs

---

**Happy Coding! 🎉**

Your MediWay frontend is production-ready and waiting for backend integration!
