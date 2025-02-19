package com.example.loginencryptionapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private Spinner spinnerPatient, spinnerDoctor;
    private Button btnDateTime, btnSaveAppointment;
    private EditText etNotes;
    private ListView listViewAppointments;
    private DatabaseHelper dbHelper;
    private long selectedDateTime = 0;
    private ArrayAdapter<String> appointmentsAdapter;
    private List<String> appointmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Initialise views
        spinnerPatient = findViewById(R.id.spinnerPatient);
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        btnDateTime = findViewById(R.id.btnDateTime);
        btnSaveAppointment = findViewById(R.id.btnSaveAppointment);
        etNotes = findViewById(R.id.etNotes);
        listViewAppointments = findViewById(R.id.listViewAppointments);

        // Initialise database helper
        dbHelper = new DatabaseHelper(this);

        // Load patients and doctors into spinners
        loadPatients();
        loadDoctors();

        // Load all appointments into the ListView
        loadAppointments();

        // Date and Time Picker
        btnDateTime.setOnClickListener(v -> showDateTimePicker());

        // Save Appointment
        btnSaveAppointment.setOnClickListener(v -> saveAppointment());

        // Long-press to delete appointment
        listViewAppointments.setOnItemLongClickListener((parent, view, position, id) -> {
            // Get the appointment ID from the selected item
            String selectedAppointment = appointmentsList.get(position);
            int appointmentId = Integer.parseInt(selectedAppointment.split(" - ")[0]);

            // Show confirmation dialog
            showDeleteConfirmationDialog(appointmentId);
            return true; // Consume the long-press event
        });
    }

    // Load patients into the spinner
    private void loadPatients() {
        List<String> patients = dbHelper.getAllPatients();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, patients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPatient.setAdapter(adapter);
    }

    // Load doctors into the spinner
    private void loadDoctors() {
        List<Doctor> doctors = dbHelper.getAllDoctors();
        ArrayAdapter<Doctor> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setAdapter(adapter);
    }

    // Load all appointments into the ListView
    private void loadAppointments() {
        appointmentsList = dbHelper.getAllAppointments(); // Fetch all appointments
        appointmentsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentsList);
        listViewAppointments.setAdapter(appointmentsAdapter);
    }

    // Show Date and Time Picker
    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // Time Picker
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute1) -> {
                calendar.set(year1, month1, dayOfMonth, hourOfDay, minute1);
                selectedDateTime = calendar.getTimeInMillis();
                btnDateTime.setText("Selected: " + android.text.format.DateFormat.format("yyyy-MM-dd hh:mm a", calendar));
            }, hour, minute, false);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }

    // Save Appointment to Database
    private void saveAppointment() {
        // Get selected patient and doctor IDs
        String selectedPatient = spinnerPatient.getSelectedItem().toString();
        int patientId = Integer.parseInt(selectedPatient.split(" - ")[0]);

        Doctor selectedDoctor = (Doctor) spinnerDoctor.getSelectedItem();
        int doctorId = selectedDoctor.getId();

        // Get notes
        String notes = etNotes.getText().toString();

        // Validate date and time
        if (selectedDateTime == 0) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add appointment to database
        boolean isAdded = dbHelper.addAppointment(patientId, doctorId, selectedDateTime, notes);
        if (isAdded) {
            Toast.makeText(this, "Appointment added successfully", Toast.LENGTH_SHORT).show();
            loadAppointments(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to add appointment", Toast.LENGTH_SHORT).show();
        }
    }

    // Show confirmation dialog for deleting an appointment
    private void showDeleteConfirmationDialog(int appointmentId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the appointment
                        boolean isDeleted = dbHelper.deleteAppointment(appointmentId);
                        if (isDeleted) {
                            Toast.makeText(AppointmentsActivity.this, "Appointment deleted", Toast.LENGTH_SHORT).show();
                            loadAppointments(); // Refresh the list
                        } else {
                            Toast.makeText(AppointmentsActivity.this, "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}