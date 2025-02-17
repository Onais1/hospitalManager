package com.example.loginencryptionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout for this activity

        // Initialise UI elements by linking them to the XML layout
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);

        // Initialise DatabaseHelper to interact with the database
        databaseHelper = new DatabaseHelper(this);

        // Set click listener for the login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(); // Call method to handle login
            }
        });

        // Set click listener for the register link
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the RegisterActivity when clicked
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to handle user login
    private void loginUser() {
        String username = etUsername.getText().toString().trim(); // Get input username
        String password = etPassword.getText().toString().trim(); // Get input password

        // Check if username or password fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return; // Stop execution if fields are empty
        }

        // Authenticate user using DatabaseHelper
        boolean isAuthenticated = databaseHelper.authenticateUser(username, password);
        if (isAuthenticated) {
            // If authentication is successful, show success message
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Navigate to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close this activity to prevent going back to login
        } else {
            // If authentication fails, show error message
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
}
