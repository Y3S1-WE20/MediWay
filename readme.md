Appointment Scheduling - Doctor scheduling, booking logic, concurrency control 
Statistical Reports -Generate PDF/CSV hospital insights and analytics
Manage Patient Medical Records -CRUD operations for diagnoses, treatments, and prescriptions
Payment Handling -Integrate payment gateway sandbox, handle transactions, generate receipts

cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run

--for ngrok portal run ngrok http 5173 on terminal after running the frontend
## Public ngrok Link

After starting the frontend, expose it using ngrok:

```sh
ngrok http 5173
```

Access your app via the generated ngrok URL (e.g., `https://your-ngrok-id.ngrok.io`).

**Update this section with your current ngrok link:**

**Frontend ngrok URL:** [https://your-ngrok-id.ngrok.io](https://your-ngrok-id.ngrok.io)