package com.example.loginencryptionapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class PatientManagement extends AppCompatActivity {

    private EditText etPatientName, etPatientAge, etPatientGender;
    private Button btnAddPatient, btnUpdatePatient;
    private ListView lvPatients;
    private ArrayAdapter<String> patientAdapter;
    private ArrayList<String> patientList;
    private DatabaseHelper databaseHelper;

    private int selectedPatientId = -1; // To store the selected patient's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_management);

        // Initialize views
        etPatientName = findViewById(R.id.et_patient_name);
        etPatientAge = findViewById(R.id.et_patient_age);
        etPatientGender = findViewById(R.id.et_patient_gender);
        btnAddPatient = findViewById(R.id.btn_add_patient);
        btnUpdatePatient = findViewById(R.id.btn_update_patient);
        lvPatients = findViewById(R.id.lv_patients);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialize patient list and adapter
        patientList = databaseHelper.getAllPatients();
        patientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, patientList);
        lvPatients.setAdapter(patientAdapter);

        // Add Patient
        btnAddPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPatient();
            }
        });

        // Update Patient
        btnUpdatePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePatient();
            }
        });

        // Click to select a patient for updating
        lvPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected patient
                String selectedPatient = patientList.get(position);

                // Extract the patient ID from the selected patient string
                String[] parts = selectedPatient.split(" - ");
                selectedPatientId = Integer.parseInt(parts[0]); // Extract ID

                // Populate the input fields with the selected patient's details
                etPatientName.setText(parts[1]); // Name
                etPatientAge.setText(parts[2]); // Age
                etPatientGender.setText(parts[3]); // Gender
            }
        });

        // Long press to confirm delete patient
        lvPatients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDeletePatient(position);
                return true;
            }
        });
    }

    // Method to add a new patient
    private void addPatient() {
        String name = etPatientName.getText().toString().trim();
        String ageStr = etPatientAge.getText().toString().trim();
        String gender = etPatientGender.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        boolean isAdded = databaseHelper.addPatient(name, age, gender);
        if (isAdded) {
            Toast.makeText(this, "Patient added!", Toast.LENGTH_SHORT).show();
            refreshPatientList();
            clearFields();
        } else {
            Toast.makeText(this, "Failed to add patient", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update a patient
    private void updatePatient() {
        // Check if a patient is selected
        if (selectedPatientId == -1) {
            Toast.makeText(this, "Please select a patient to update", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get updated details from input fields
        String name = etPatientName.getText().toString().trim();
        String ageStr = etPatientAge.getText().toString().trim();
        String gender = etPatientGender.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        // Update the patient in the database
        boolean isUpdated = databaseHelper.updatePatient(selectedPatientId, name, age, gender);
        if (isUpdated) {
            Toast.makeText(this, "Patient updated!", Toast.LENGTH_SHORT).show();
            refreshPatientList();
            clearFields();
            selectedPatientId = -1; // Reset selected patient ID
        } else {
            Toast.makeText(this, "Failed to update patient", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to show confirmation dialog before deleting a patient
    private void confirmDeletePatient(int position) {
        String selectedPatient = patientList.get(position);
        String[] parts = selectedPatient.split(" - ");
        int patientId = Integer.parseInt(parts[0]); // Extract ID
        String patientName = parts[1]; // Extract Name

        new AlertDialog.Builder(this)
                .setTitle("Delete Patient")
                .setMessage("Are you sure you want to delete " + patientName + "?")
                .setPositiveButton("Yes", (dialog, which) -> deletePatient(patientId))
                .setNegativeButton("No", null)
                .show();
    }

    // Method to delete a patient after confirmation
    private void deletePatient(int patientId) {
        boolean deleted = databaseHelper.deletePatient(patientId);
        if (deleted) {
            Toast.makeText(this, "Patient deleted!", Toast.LENGTH_SHORT).show();
            refreshPatientList();
        } else {
            Toast.makeText(this, "Failed to delete patient", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to refresh patient list after adding, updating, or deleting
    private void refreshPatientList() {
        patientList.clear();
        patientList.addAll(databaseHelper.getAllPatients());
        patientAdapter.notifyDataSetChanged();
    }

    // Method to clear input fields
    private void clearFields() {
        etPatientName.setText("");
        etPatientAge.setText("");
        etPatientGender.setText("");
    }
}