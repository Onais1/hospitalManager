package com.example.loginencryptionapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etNINumber;
    private Button btnHashPassword, btnEncryptNI, btnDecryptNI, btnRegister;
    private TextView tvHashResult, tvEncryptResult, tvDecryptResult, tvLoginLink;
    private DatabaseHelper databaseHelper;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etNINumber = findViewById(R.id.et_ninumber);
        btnHashPassword = findViewById(R.id.btn_hash_password);
        btnEncryptNI = findViewById(R.id.btn_encrypt_ni);
        btnDecryptNI = findViewById(R.id.btn_decrypt_ni);
        btnRegister = findViewById(R.id.btn_register);
        tvHashResult = findViewById(R.id.tv_hash_result);
        tvEncryptResult = findViewById(R.id.tv_encrypt_result);
        tvDecryptResult = findViewById(R.id.tv_decrypt_result);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Generate RSA key pair for encryption
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating encryption keys", Toast.LENGTH_SHORT).show();
        }

        // Register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Login link click listener
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        // Password hashing
        btnHashPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String hashedPassword = hashPassword(password);
                tvHashResult.setText("Hashed Password: " + hashedPassword);
                tvHashResult.setVisibility(View.VISIBLE);
            }
        });

        // NI Encryption
        btnEncryptNI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String niNumber = etNINumber.getText().toString();
                String encryptedNI = encryptNI(niNumber);
                tvEncryptResult.setText("Encrypted NI: " + encryptedNI);
                tvEncryptResult.setVisibility(View.VISIBLE);
            }
        });

        // NI Decryption
        btnDecryptNI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String encryptedNI = tvEncryptResult.getText().toString().replace("Encrypted NI: ", "");
                String decryptedNI = decryptNI(encryptedNI);
                tvDecryptResult.setText("Decrypted NI: " + decryptedNI);
                tvDecryptResult.setVisibility(View.VISIBLE);
            }
        });
    }

    // Method to handle user registration
    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = databaseHelper.addUser(username, password);

        if (isInserted) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

            // Log all users to check if the user was stored correctly
            databaseHelper.logAllUsers();

            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    // Secure password hashing using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Encrypt NI number using RSA
    private String encryptNI(String niNumber) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(niNumber.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Decrypt NI number using RSA
    private String decryptNI(String encryptedNI) {
        try {
            byte[] encryptedBytes = Base64.decode(encryptedNI, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Decryption Error!";
        }
    }


}


