package com.example.loginencryptionapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DoctorManagementActivity extends AppCompatActivity {

    private EditText etDoctorName, etDoctorSpecialization, etDoctorPhone, etDoctorEmail;
    private Button btnAddDoctor, btnUpdateDoctor;
    private ListView lvDoctors;
    private DatabaseHelper dbHelper;
    private ArrayList<Doctor> doctorList;
    private DoctorAdapter doctorAdapter;
    private int selectedDoctorPosition = -1; // Track the selected doctor's position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_management);

        // Initialize views
        etDoctorName = findViewById(R.id.etDoctorName);
        etDoctorSpecialization = findViewById(R.id.etDoctorSpecialization);
        etDoctorPhone = findViewById(R.id.etDoctorPhone);
        etDoctorEmail = findViewById(R.id.etDoctorEmail);
        btnAddDoctor = findViewById(R.id.btnAddDoctor);
        btnUpdateDoctor = findViewById(R.id.btnUpdateDoctor);
        lvDoctors = findViewById(R.id.lvDoctors);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Load doctors into the list
        loadDoctors();

        // Add Doctor Button
        btnAddDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDoctor();
            }
        });

        // Update Doctor Button
        btnUpdateDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDoctor();
            }
        });

        // ListView Item Click Listener
        lvDoctors.setOnItemClickListener((parent, view, position, id) -> {
            // Track the selected doctor's position
            selectedDoctorPosition = position;

            // Populate the input fields with the selected doctor's details
            Doctor selectedDoctor = doctorList.get(position);
            etDoctorName.setText(selectedDoctor.getName());
            etDoctorSpecialization.setText(selectedDoctor.getSpecialization());
            etDoctorPhone.setText(selectedDoctor.getPhone());
            etDoctorEmail.setText(selectedDoctor.getEmail());
        });

        // ListView Long-Press Listener for Delete
        lvDoctors.setOnItemLongClickListener((parent, view, position, id) -> {
            // Show a confirmation dialog before deleting
            showDeleteConfirmationDialog(position);
            return true; // Consume the long-press event
        });
    }

    // Load doctors from the database
    private void loadDoctors() {
        doctorList = dbHelper.getAllDoctors();
        doctorAdapter = new DoctorAdapter(this, doctorList);
        lvDoctors.setAdapter(doctorAdapter);
    }

    // Add a new doctor
    private void addDoctor() {
        String name = etDoctorName.getText().toString().trim();
        String specialization = etDoctorSpecialization.getText().toString().trim();
        String phone = etDoctorPhone.getText().toString().trim();
        String email = etDoctorEmail.getText().toString().trim();

        if (name.isEmpty() || specialization.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAdded = dbHelper.addDoctor(name, specialization, phone, email);
        if (isAdded) {
            Toast.makeText(this, "Doctor added", Toast.LENGTH_SHORT).show();
            loadDoctors(); // Refresh the list
            clearFields();
        } else {
            Toast.makeText(this, "Failed to add doctor", Toast.LENGTH_SHORT).show();
        }
    }

    // Update a doctor
    private void updateDoctor() {
        if (selectedDoctorPosition == -1) {
            Toast.makeText(this, "Please select a doctor to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etDoctorName.getText().toString().trim();
        String specialization = etDoctorSpecialization.getText().toString().trim();
        String phone = etDoctorPhone.getText().toString().trim();
        String email = etDoctorEmail.getText().toString().trim();

        if (name.isEmpty() || specialization.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Doctor selectedDoctor = doctorList.get(selectedDoctorPosition);
        boolean isUpdated = dbHelper.updateDoctor(selectedDoctor.getId(), name, specialization, phone, email);

        if (isUpdated) {
            Toast.makeText(this, "Doctor updated", Toast.LENGTH_SHORT).show();
            loadDoctors(); // Refresh the list
            clearFields();
            selectedDoctorPosition = -1; // Reset selected position
        } else {
            Toast.makeText(this, "Failed to update doctor", Toast.LENGTH_SHORT).show();
        }
    }

    // Show a confirmation dialog for deleting a doctor
    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Doctor")
                .setMessage("Are you sure you want to delete this doctor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDoctor(position);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Delete a doctor
    private void deleteDoctor(int position) {
        Doctor selectedDoctor = doctorList.get(position);
        boolean isDeleted = dbHelper.deleteDoctor(selectedDoctor.getId());

        if (isDeleted) {
            Toast.makeText(this, "Doctor deleted", Toast.LENGTH_SHORT).show();
            loadDoctors(); // Refresh the list
            clearFields();
        } else {
            Toast.makeText(this, "Failed to delete doctor", Toast.LENGTH_SHORT).show();
        }
    }

    // Clear input fields
    private void clearFields() {
        etDoctorName.setText("");
        etDoctorSpecialization.setText("");
        etDoctorPhone.setText("");
        etDoctorEmail.setText("");
    }
}