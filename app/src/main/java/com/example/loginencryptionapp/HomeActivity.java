package com.example.loginencryptionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnLogout, btnPatient, btnDoctorManagement; // Added btnDoctorManagement

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        tvWelcome = findViewById(R.id.tv_welcome);
        btnLogout = findViewById(R.id.btn_logout);
        btnPatient = findViewById(R.id.btn_patient);
        btnDoctorManagement = findViewById(R.id.btn_doctor_management); // Initialize the new button

        // Get the username from Intent
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            tvWelcome.setText("Welcome, " + username + "!");
        }

        // Navigate to PatientActivity
        btnPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PatientManagement.class);
                startActivity(intent);
            }
        });

        // Navigate to DoctorManagementActivity
        btnDoctorManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DoctorManagementActivity.class);
                startActivity(intent);
            }
        });

        // Logout button listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}