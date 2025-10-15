# Authentication Fix for Appointment Endpoints

## Issue Identified
The appointment endpoints were failing with a 500 error when trying to fetch user appointments. The root cause was in the `AppointmentController` where it attempted to parse the username (email) from the JWT token as a UUID:

```java
// INCORRECT - This was causing the error
UUID patientId = UUID.fromString(authentication.getName());
```

The JWT token stores the **email address** as the subject (username), NOT the user ID. This caused an `IllegalArgumentException` when trying to parse an email like "tester1@gmail.com" as a UUID.

## Solution Applied

### 1. Added UserRepository Dependency
Added `UserRepository` to the controller to look up users by email:

```java
private final UserRepository userRepository;
```

### 2. Created Helper Method
Added a helper method to extract the user ID from authentication:

```java
private UUID getUserIdFromAuthentication(Authentication authentication) {
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return user.getUserId();
}
```

### 3. Updated All Affected Endpoints
Replaced direct UUID parsing with the helper method in:
- `createAppointment()` - POST /api/appointments
- `getMyAppointments()` - GET /api/appointments/my
- `cancelAppointment()` - DELETE /api/appointments/{id}

## How JWT Authentication Works

1. **User Login**: When a user logs in, the backend generates a JWT token with:
   - Subject: User's email address
   - Claims: User's role (PATIENT, ADMIN, DOCTOR)

2. **Frontend Storage**: The token is stored in `localStorage` as `mediway_token`

3. **API Requests**: Frontend sends token in Authorization header:
   ```
   Authorization: Bearer <token>
   ```

4. **Backend Verification**: Spring Security extracts the username (email) from the token and creates an `Authentication` object

5. **User ID Lookup**: Controller now looks up the user by email to get their UUID for database queries

## Testing the Fix

### Step 1: Restart Backend
Stop and restart the Spring Boot application to load the updated controller.

### Step 2: Test Appointments Endpoint
```powershell
# Run the diagnostic script
.\backend\scripts\test-appointments-api.ps1

# When prompted, paste your JWT token from browser console:
# localStorage.getItem('mediway_token')
```

### Step 3: Expected Response
You should now see your appointments in JSON format:
```json
[
  {
    "appointmentId": "...",
    "patientId": "...",
    "doctorId": "...",
    "doctorName": "Dr. Sarah Johnson",
    "doctorSpecialization": "Cardiology",
    "appointmentDate": "2025-10-25",
    "appointmentTime": "10:00:00",
    "status": "PENDING",
    "reason": "Regular checkup and consultation",
    "notes": null,
    "consultationFee": 150.00,
    "createdAt": "2025-10-15T15:45:30"
  }
]
```

## Frontend Improvements

Also updated `Appointments.jsx` to show more detailed error messages:
```javascript
const errorMessage = error.response?.data?.message || error.message || 'Failed to load appointments. Please try again.';
setError(`Failed to load appointments: ${errorMessage}`);
```

This helps with debugging by showing the actual backend error message instead of a generic error.

## Related Files Modified
- `backend/src/main/java/com/mediway/backend/controller/AppointmentController.java`
- `frontend/src/pages/Appointments.jsx`

## Next Steps
1. Restart backend application
2. Refresh frontend (hard refresh: Ctrl+Shift+R)
3. Navigate to Appointments page
4. Verify your appointment appears
5. Test "Pay Now" button to create payment
6. Complete PayPal flow
7. Verify payment and receipt are created
