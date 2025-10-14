# MediWay Frontend - Visual Sitemap & Navigation Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                         MEDIWAY APPLICATION                          │
│                    Smart Health Management System                    │
└─────────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════
                         🌐 PUBLIC ROUTES
═══════════════════════════════════════════════════════════════════════

┌─────────────────────────────────────────────────────────────────────┐
│  1. HOME PAGE (/)                                                    │
├─────────────────────────────────────────────────────────────────────┤
│  📍 Path: /                                                          │
│  🎨 Components:                                                      │
│     • HeroSection - "Your Health, Simplified with MediWay"          │
│     • FeaturesSection - 4 feature cards                             │
│     • AboutSection - Platform benefits                              │
│  🔘 Actions:                                                         │
│     • "Book Appointment" → /book-appointment (protected)            │
│     • "Register Now" → /register                                    │
└─────────────────────────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────────────────────┐
│  2. REGISTER PAGE (/register)                                        │
├─────────────────────────────────────────────────────────────────────┤
│  📍 Path: /register                                                  │
│  📝 Form Fields:                                                     │
│     • Full Name (required)                                          │
│     • Email Address (validated)                                     │
│     • Phone Number (10 digits)                                      │
│     • Date of Birth (date picker)                                   │
│     • Gender (dropdown)                                             │
│     • Password (min 6 chars)                                        │
│     • Confirm Password (match validation)                           │
│  ✨ Features:                                                        │
│     • Real-time validation                                          │
│     • QR code generation on success                                 │
│     • Health card preview                                           │
│     • Print health card option                                      │
│  🔘 Actions:                                                         │
│     • "Register Now" → Show QR Code → "Continue to Login"           │
│     • "Login here" link → /login                                    │
└─────────────────────────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────────────────────┐
│  3. LOGIN PAGE (/login)                                              │
├─────────────────────────────────────────────────────────────────────┤
│  📍 Path: /login                                                     │
│  📝 Form Fields:                                                     │
│     • Email Address                                                 │
│     • Password                                                      │
│     • Remember me checkbox                                          │
│  ⚡ Demo Mode:                                                       │
│     • Enter any email/password to login                             │
│     • Mock authentication enabled                                   │
│  🔘 Actions:                                                         │
│     • "Login" → /appointments (on success)                          │
│     • "Register now" link → /register                               │
│     • "Forgot password?" → /forgot-password                         │
└─────────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════
                       🔒 PROTECTED ROUTES
                    (Requires Authentication)
═══════════════════════════════════════════════════════════════════════

┌─────────────────────────────────────────────────────────────────────┐
│  4. APPOINTMENTS PAGE (/appointments)                                │
├─────────────────────────────────────────────────────────────────────┤
│  📍 Path: /appointments                                              │
│  🔒 Protected: Yes                                                   │
│  📊 Content:                                                         │
│     • List of all appointments                                      │
│     • Status badges (confirmed, pending, completed)                 │
│     • Doctor name, specialization, hospital                         │
│     • Date and time information                                     │
│  🎯 Empty State:                                                     │
│     • "No Appointments Yet" message                                 │
│     • "Book Appointment" CTA button                                 │
│  🔘 Actions:                                                         │
│     • "Book New Appointment" → /book-appointment                    │
│     • "Reschedule" button (UI ready)                                │
│     • "Cancel" button → Confirmation dialog → Remove                │
└─────────────────────────────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────────────────────────────┐
│  5. BOOK APPOINTMENT PAGE (/book-appointment)                        │
├─────────────────────────────────────────────────────────────────────┤
│  📍 Path: /book-appointment                                          │
│  🔒 Protected: Yes                                                   │
│  📋 Multi-Step Process:                                              │
│                                                                      │
│  STEP 1: Select Doctor                                              │
│     • Dropdown: Specialization                                      │
│       - Cardiologist                                                │
│       - Dermatologist                                               │
│       - General Physician                                           │
│       - Orthopedic                                                  │
│       - Pediatrician                                                │
│       - Neurologist                                                 │
│     • Doctor Cards (selectable)                                     │
│       - Doctor name                                                 │
│       - Experience years                                            │
│       - Hospital name                                               │
│                                                                      │
│  STEP 2: Choose Date & Time                                         │
│     • Selected doctor summary card                                  │
│     • Date picker (future dates only)                               │
│     • Time slot grid (8 slots per day)                              │
│       - 09:00 AM, 10:00 AM, 11:00 AM, 12:00 PM                     │
│       - 02:00 PM, 03:00 PM, 04:00 PM, 05:00 PM                     │
│     • Symptoms input (optional)                                     │
│                                                                      │
│  SUCCESS SCREEN:                                                     │
│     • Checkmark animation                                           │
│     • "Appointment Booked!" message                                 │
│     • Auto-redirect → /appointments                                 │
│                                                                      │
│  🔘 Actions:                                                         │
│     • "Next" (Step 1 → Step 2)                                      │
│     • "Back" (Step 2 → Step 1)                                      │
│     • "Confirm Booking" → Success → /appointments                   │
│     • "Back to Appointments" → /appointments                        │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  6. PAYMENTS PAGE (/payments)                                        │
├─────────────────────────────────────────────────────────────────────┤
│  📍 Path: /payments                                                  │
│  🔒 Protected: Yes                                                   │
│  📊 Summary Cards (Top Row):                                         │
│     • Total Unpaid (red)                                            │
│     • Unpaid Bills Count (yellow)                                   │
│     • Paid Bills Count (green)                                      │
│                                                                      │
│  📋 Unpaid Bills Section:                                            │
│     • Bill number                                                   │
│     • Description                                                   │
│     • Date & Due date                                               │
│     • Amount                                                        │
│     • "Pay Now" button → Payment Modal                              │
│                                                                      │
│  📜 Payment History Section:                                         │
│     • Paid bills list                                               │
│     • Payment date                                                  │
│     • Amount (green)                                                │
│     • Status badge                                                  │
│                                                                      │
│  💳 PAYMENT MODAL:                                                   │
│     • Amount to pay (highlighted)                                   │
│     • Payment method selector:                                      │
│       ○ Credit/Debit Card                                           │
│         - Card number                                               │
│         - Cardholder name                                           │
│         - Expiry date (MM/YY)                                       │
│         - CVV (3 digits)                                            │
│       ○ Insurance                                                   │
│         - Insurance ID                                              │
│     • Security indicator                                            │
│     • Processing animation                                          │
│     • Success screen with checkmark                                 │
│                                                                      │
│  🔘 Actions:                                                         │
│     • "Pay Now" → Open payment modal                                │
│     • "Pay $X.XX" → Process → Success → Close modal                │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  7. REPORTS PAGE (/reports)                                          │
├─────────────────────────────────────────────────────────────────────┤
│  📍 Path: /reports                                                   │
│  🔒 Protected: Yes                                                   │
│  📊 Statistics Cards (Top Row):                                      │
│     • Total Patients (blue)                                         │
│     • Total Appointments (green)                                    │
│     • Total Revenue (purple)                                        │
│     • Growth Rate (orange)                                          │
│                                                                      │
│  📈 Charts Section:                                                  │
│                                                                      │
│  Row 1:                                                              │
│     • Appointments Trend (Bar Chart)                                │
│       - Monthly appointments for 6 months                           │
│       - Green bars with tooltips                                    │
│                                                                      │
│     • Revenue Trend (Line Chart)                                    │
│       - Monthly revenue for 6 months                                │
│       - Green line with data points                                 │
│                                                                      │
│  Row 2:                                                              │
│     • Appointments by Specialization (Pie Chart)                    │
│       - 5 specializations                                           │
│       - Color-coded segments                                        │
│       - Percentage labels                                           │
│                                                                      │
│     • Top Performing Departments                                    │
│       - Ranked list (1-5)                                           │
│       - Progress bars                                               │
│       - Patient counts                                              │
│                                                                      │
│  🔘 Actions:                                                         │
│     • "Export Report" button (UI ready)                             │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  8. PROFILE PAGE (/profile)                                          │
├─────────────────────────────────────────────────────────────────────┤
│  📍 Path: /profile                                                   │
│  🔒 Protected: Yes                                                   │
│  📐 Layout: Two-column grid                                          │
│                                                                      │
│  LEFT COLUMN: Health Card                                            │
│     • Profile avatar (initial)                                      │
│     • Patient name                                                  │
│     • Patient ID                                                    │
│     • QR Code display                                               │
│     • "Print Card" button                                           │
│     • "Download QR" button                                          │
│     • "Active Member" badge                                         │
│                                                                      │
│  RIGHT COLUMN: Personal Information                                  │
│     View Mode:                                                       │
│       • Full Name (with icon)                                       │
│       • Email (with icon)                                           │
│       • Phone (with icon)                                           │
│       • Date of Birth (with icon)                                   │
│       • Gender (with icon)                                          │
│       • "Edit" button                                               │
│                                                                      │
│     Edit Mode:                                                       │
│       • Editable form fields                                        │
│       • "Save" button (with loading)                                │
│       • "Cancel" button                                             │
│                                                                      │
│  BOTTOM: Medical Summary                                             │
│     • Total Appointments                                            │
│     • Completed Appointments                                        │
│     • Total Payments                                                │
│                                                                      │
│  🔘 Actions:                                                         │
│     • "Edit" → Enable edit mode                                     │
│     • "Save" → Update profile → Disable edit mode                   │
│     • "Cancel" → Revert changes → Disable edit mode                 │
│     • "Print Card" → Open print dialog                              │
│     • "Download QR" → Download QR code                              │
└─────────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════
                      🧭 NAVIGATION STRUCTURE
═══════════════════════════════════════════════════════════════════════

┌─────────────────────────────────────────────────────────────────────┐
│  NAVBAR (Responsive, Sticky, Auth-aware)                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  LOGGED OUT (Public):                                                │
│     🏠 Home                                                          │
│     📝 Register                                                      │
│     🔐 Login                                                         │
│                                                                      │
│  LOGGED IN (Private):                                                │
│     🏠 Home                                                          │
│     📅 Appointments                                                  │
│     💳 Payments                                                      │
│     📊 Reports                                                       │
│     👤 Profile                                                       │
│     ━━━━━━━━━━━━━━━━━━━━━━                                           │
│     👤 [User Name]                                                   │
│     🚪 Logout                                                        │
│                                                                      │
│  Mobile (<768px):                                                    │
│     • Hamburger menu                                                │
│     • Slide-in drawer                                               │
│     • Full-height menu                                              │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  FOOTER (Always visible)                                             │
├─────────────────────────────────────────────────────────────────────┤
│  © 2025 MediWay. All rights reserved.                                │
│  Centered, gray text                                                │
└─────────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════
                       🔄 USER FLOW DIAGRAMS
═══════════════════════════════════════════════════════════════════════

NEW USER JOURNEY:
─────────────────
    Home
      ↓
   Register (fill form)
      ↓
   QR Code Success
      ↓
   Login (enter credentials)
      ↓
   Appointments (dashboard)
      ↓
   Book Appointment (multi-step)
      ↓
   View Appointment (in list)

RETURNING USER JOURNEY:
───────────────────────
    Home
      ↓
   Login
      ↓
   Appointments
      ↓
   [View | Book | Cancel]

PAYMENT FLOW:
─────────────
    Appointments (view appointment)
      ↓
    Payments (see bills)
      ↓
    Pay Now (modal)
      ↓
    Enter Details
      ↓
    Success
      ↓
    Updated Bill Status

PROFILE UPDATE:
───────────────
    Profile
      ↓
    Click Edit
      ↓
    Modify Fields
      ↓
    Save
      ↓
    Updated Profile

═══════════════════════════════════════════════════════════════════════
                     🎨 COMPONENT HIERARCHY
═══════════════════════════════════════════════════════════════════════

App (Router + AuthProvider)
├── Navbar
├── Routes
│   ├── Home
│   │   ├── HeroSection
│   │   ├── FeaturesSection
│   │   └── AboutSection
│   ├── Register
│   ├── Login
│   ├── ProtectedRoute
│   │   ├── Appointments
│   │   ├── BookAppointment
│   │   ├── Payments
│   │   ├── Reports
│   │   └── Profile
└── Footer

═══════════════════════════════════════════════════════════════════════
                        🔑 KEY FEATURES
═══════════════════════════════════════════════════════════════════════

✅ Responsive Design       - Works on all devices
✅ Smooth Animations       - Framer Motion throughout
✅ Form Validation         - Real-time feedback
✅ Loading States          - Clear user feedback
✅ Error Handling          - User-friendly messages
✅ Empty States            - Helpful CTAs
✅ Protected Routes        - Secure access control
✅ Auth Context            - Global state management
✅ Mock Data               - Demo-ready
✅ API Ready               - Backend integration points
✅ Charts & Analytics      - Recharts library
✅ Payment Processing      - Card & Insurance
✅ QR Health Card          - Digital patient ID
✅ Multi-step Forms        - Progressive booking
✅ Status Tracking         - Real-time updates

═══════════════════════════════════════════════════════════════════════
