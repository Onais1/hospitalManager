package com.example.loginencryptionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hospital.db";
    private static final int DATABASE_VERSION = 3; // Updated version

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_SALT = "salt";

    // Patients Table
    private static final String TABLE_PATIENTS = "patients";
    private static final String COL_PATIENT_ID = "id";
    private static final String COL_PATIENT_NAME = "name";
    private static final String COL_PATIENT_AGE = "age";
    private static final String COL_PATIENT_GENDER = "gender";

    // Doctors Table
    private static final String TABLE_DOCTORS = "doctors"; // New table
    private static final String COL_DOCTOR_ID = "id";
    private static final String COL_DOCTOR_NAME = "name";
    private static final String COL_DOCTOR_SPECIALIZATION = "specialization";
    private static final String COL_DOCTOR_PHONE = "phone";
    private static final String COL_DOCTOR_EMAIL = "email";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COL_PASSWORD + " TEXT NOT NULL, "
                + COL_SALT + " TEXT NOT NULL)";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Patients Table
        String CREATE_PATIENTS_TABLE = "CREATE TABLE " + TABLE_PATIENTS + " ("
                + COL_PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PATIENT_NAME + " TEXT NOT NULL, "
                + COL_PATIENT_AGE + " INTEGER NOT NULL, "
                + COL_PATIENT_GENDER + " TEXT NOT NULL)";
        db.execSQL(CREATE_PATIENTS_TABLE);

        // Create Doctors Table
        String CREATE_DOCTORS_TABLE = "CREATE TABLE " + TABLE_DOCTORS + " ("
                + COL_DOCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DOCTOR_NAME + " TEXT NOT NULL, "
                + COL_DOCTOR_SPECIALIZATION + " TEXT NOT NULL, "
                + COL_DOCTOR_PHONE + " TEXT, "
                + COL_DOCTOR_EMAIL + " TEXT)";
        db.execSQL(CREATE_DOCTORS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE " + TABLE_PATIENTS + " ("
                    + COL_PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PATIENT_NAME + " TEXT NOT NULL, "
                    + COL_PATIENT_AGE + " INTEGER NOT NULL, "
                    + COL_PATIENT_GENDER + " TEXT NOT NULL)");
        }
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE " + TABLE_DOCTORS + " ("
                    + COL_DOCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_DOCTOR_NAME + " TEXT NOT NULL, "
                    + COL_DOCTOR_SPECIALIZATION + " TEXT NOT NULL, "
                    + COL_DOCTOR_PHONE + " TEXT, "
                    + COL_DOCTOR_EMAIL + " TEXT)");
        }
    }

    /*** PATIENT MANAGEMENT METHODS ***/

    // Add a new patient
    public boolean addPatient(String name, int age, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PATIENT_NAME, name);
        values.put(COL_PATIENT_AGE, age);
        values.put(COL_PATIENT_GENDER, gender);

        long result = db.insert(TABLE_PATIENTS, null, values);
        db.close();
        return result != -1;
    }

    // Retrieve all patients as a list
    public ArrayList<String> getAllPatients() {
        ArrayList<String> patients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PATIENTS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PATIENT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_NAME));
                int age = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PATIENT_AGE));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(COL_PATIENT_GENDER));

                patients.add(id + " - " + name + " - " + age + " - " + gender);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return patients;
    }

    // Update patient details
    public boolean updatePatient(int id, String name, int age, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PATIENT_NAME, name);
        values.put(COL_PATIENT_AGE, age);
        values.put(COL_PATIENT_GENDER, gender);

        int rowsUpdated = db.update(TABLE_PATIENTS, values, COL_PATIENT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsUpdated > 0;
    }

    // Delete a patient
    public boolean deletePatient(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_PATIENTS, COL_PATIENT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    /*** DOCTOR MANAGEMENT METHODS ***/

    // Add a new doctor
    public boolean addDoctor(String name, String specialization, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DOCTOR_NAME, name);
        values.put(COL_DOCTOR_SPECIALIZATION, specialization);
        values.put(COL_DOCTOR_PHONE, phone);
        values.put(COL_DOCTOR_EMAIL, email);

        long result = db.insert(TABLE_DOCTORS, null, values);
        db.close();
        return result != -1;
    }

    // Retrieve all doctors as a list
    // DatabaseHelper.java
    public ArrayList<Doctor> getAllDoctors() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DOCTORS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_DOCTOR_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOCTOR_NAME));
                String specialization = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOCTOR_SPECIALIZATION));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOCTOR_PHONE));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_DOCTOR_EMAIL));

                // Create a Doctor object and add it to the list
                Doctor doctor = new Doctor(id, name, specialization, phone, email);
                doctors.add(doctor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return doctors;
    }

    // Update doctor details
    public boolean updateDoctor(int id, String name, String specialization, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DOCTOR_NAME, name);
        values.put(COL_DOCTOR_SPECIALIZATION, specialization);
        values.put(COL_DOCTOR_PHONE, phone);
        values.put(COL_DOCTOR_EMAIL, email);

        int rowsUpdated = db.update(TABLE_DOCTORS, values, COL_DOCTOR_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsUpdated > 0;
    }

    // Delete a doctor
    public boolean deleteDoctor(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_DOCTORS, COL_DOCTOR_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    /*** USER MANAGEMENT METHODS ***/

    public void logAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                String salt = cursor.getString(cursor.getColumnIndexOrThrow(COL_SALT));

                Log.d("DatabaseHelper", "User: " + username + ", Password: " + password + ", Salt: " + salt);
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "No users found in the database.");
        }

        cursor.close();
        db.close();
    }

    public boolean addUser(String username, String password) {
        if (userExists(username)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, hashedPassword);
        values.put(COL_SALT, salt);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean userExists(String username) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ?", new String[]{username})) {
            return cursor.moveToFirst();
        }
    }

    public boolean authenticateUser(String username, String password) {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT " + COL_PASSWORD + ", " + COL_SALT + " FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ?", new String[]{username})) {

            if (cursor.moveToFirst()) {
                String storedHashedPassword = cursor.getString(0);
                String storedSalt = cursor.getString(1);
                return storedHashedPassword.equals(hashPassword(password, storedSalt));
            }
        }
        return false;
    }

    private String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest((salt + password).getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("DatabaseHelper", "Error hashing password", e);
            return null;
        }
    }

    /*** TEST METHODS ***/

    public void deleteAllPatients() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM patients");
        db.close();
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM patients");
        db.close();
    }
}