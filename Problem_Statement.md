

## 1. Title
Blood Donation Network and Emergency Matching Platform

## 2. Domain
Healthcare

## 3. Who is the User? (User Types & Roles)

1. Donor
   - Registers blood group and availability.
   - Receives emergency donation requests.
   - Accepts or declines donation requests.

2. Recipient / Patient
   - Searches for blood donors.
   - Sends emergency blood requests.
   - Tracks request status.

3. Hospital / Blood Bank
   - Verifies donor information.
   - Updates blood stock availability.
   - Manages emergency requests.

4. Admin
   - Manages users and hospitals.
   - Verifies registrations.
   - Monitors the overall system.

---

## 4. What Problem are We Solving?

During medical emergencies, finding compatible blood donors quickly is a major challenge. Patients often depend on social media or personal contacts, which can delay treatment. Blood banks may not always have sufficient stock, and there is no efficient way to instantly connect nearby eligible donors with recipients. This platform provides real-time donor matching based on blood group and location, enabling faster communication and reducing delays in emergency situations.

---

## 5. Proposed Solution

The Blood Donation Network and Emergency Matching Platform will:

- Allow donors to register with blood group and location.
- Enable recipients to search for compatible donors.
- Match donors based on blood group and nearby location.
- Send emergency blood request notifications.
- Allow donors to accept or reject requests.
- Maintain blood bank inventory.
- Provide hospital verification.
- Store donation history.
- Display request status in real time.
- Generate reports for administrators.

---

## 6. Core Entities / Database Tables

1. User
2. Donor
3. Recipient
4. Hospital
5. Blood Bank
6. Blood Request
7. Donation History
8. Blood Inventory
9. Notification
10. Admin

---

## 7. User Roles & Permissions

### Admin
- Manage users
- Verify hospitals
- Manage blood banks
- View reports
- Remove fake accounts

### Donor
- Register profile
- Update availability
- Accept or reject requests
- View donation history

### Recipient
- Search donors
- Send emergency requests
- Track request status

### Hospital
- Verify patients
- Update blood stock
- Manage emergency requests

---

## 8. Success Criteria

- Users can register successfully.
- Donors can update availability.
- Patients can find compatible donors within seconds.
- Emergency requests reach eligible nearby donors.
- Hospitals can manage blood inventory efficiently.
- Admin can monitor the entire platform.
- Blood request status is updated in real time.

---

## 9. Out of Scope

- Online payment integration.
- Ambulance booking.
- AI-based disease prediction.
- Medical consultation.
- Organ donation management.
- International donor matching.

---

## 10. Chosen Track

Java (Spring Boot)