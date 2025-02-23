Hospital Management Application

Overview:
I have developed an android application for managing a hospital, including a dedicated database, users, patients, doctors, and appointments. The project utilises SQLite for local data storage and implements encryption for sensitive data like appointment notes and user passwords.

Features:
- User authentication with password hashing using SHA-265 and salt.
- Patient management using CRUD operations.
- Doctor management using CRUD operations.
- Appointment management with encrypted notes using AES-128 encryption.
- Secure storage of user credentials and other sensitive information.

Tech tools:
- Android Studio
- Java
- SQLite
- AES-128 encryption
- SHA-256 hashing

DB Scheme:
The applications database consists of four main tables;
- Users (id, username, password, salt)
- Patients (id, name, age, gender)
- Doctors (id, name, specialization, phone, email)
- Appointments(id, patient_id, doctor_id, date_time, notes, notes_salt)

Installation Guide:
Below is the git clone link for the repo;
git clone https://github.com/Onais1/hospitalManager.git

Usage:
- Run the application in android studio
- Register as a user and log in
- Manage patients, doctors, and appointments through the UI

Future Improvements
- Enhance the UI for better usability
- Add Firebase authentication
- Improve encryption methods
 
