package com.example.loginencryptionapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import android.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etNINumber;
    private Button btnHashPassword, btnEncryptNI, btnDecryptNI, btnRegister, btnLogin;
    private TextView tvHashResult, tvEncryptResult, tvDecryptResult, tvLoginRegisterResult;

    // In-memory storage for users (username -> {hashedPassword, encryptedNINumber})
    private Map<String, String[]> userDatabase = new HashMap<>();

    // RSA public and private keys
    private PublicKey publicKey;
    private PrivateKey privateKey;

    // To store the encrypted NI number for decryption
    private String encryptedNINumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etNINumber = findViewById(R.id.et_ninumber);
        btnHashPassword = findViewById(R.id.btn_hash_password);
        btnEncryptNI = findViewById(R.id.btn_encrypt_ni);
        btnDecryptNI = findViewById(R.id.btn_decrypt_ni);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);
        tvHashResult = findViewById(R.id.tv_hash_result);
        tvEncryptResult = findViewById(R.id.tv_encrypt_result);
        tvDecryptResult = findViewById(R.id.tv_decrypt_result);
        tvLoginRegisterResult = findViewById(R.id.tv_login_register_result);

        // Generate RSA key pair
        generateRSAKeys();

        // Button to Hash Password with input validation
        btnHashPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                if (validatePassword(password)) {
                    try {
                        String hashedPassword = hashPasswordWithSalt(password);
                        tvHashResult.setText("Hashed Password: " + hashedPassword);
                    } catch (Exception e) {
                        tvHashResult.setText("Error: " + e.getMessage());
                    }
                }
            }
        });

        // Button to Encrypt NI Number with input validation
        btnEncryptNI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String niNumber = etNINumber.getText().toString();
                if (validateNINumber(niNumber)) {
                    try {
                        encryptedNINumber = encryptNINumber(niNumber);
                        tvEncryptResult.setText("Encrypted NI Number: " + encryptedNINumber);
                    } catch (Exception e) {
                        tvEncryptResult.setText("Error: " + e.getMessage());
                    }
                }
            }
        });

        // Button to Decrypt NI Number with validation
        btnDecryptNI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (encryptedNINumber != null) {
                    try {
                        String decryptedNINumber = decryptNINumber(encryptedNINumber);
                        tvDecryptResult.setText("Decrypted NI Number: " + decryptedNINumber);
                    } catch (Exception e) {
                        tvDecryptResult.setText("Error: " + e.getMessage());
                    }
                } else {
                    tvDecryptResult.setText("No NI number to decrypt.");
                }
            }
        });

        // Button to Register User
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String niNumber = etNINumber.getText().toString();

                if (validateInputFields(username, password, niNumber)) {
                    try {
                        String hashedPassword = hashPasswordWithSalt(password);
                        String encryptedNINumber = encryptNINumber(niNumber);

                        // Save user to in-memory database
                        userDatabase.put(username, new String[]{hashedPassword, encryptedNINumber});
                        tvLoginRegisterResult.setText("User registered successfully!");

                    } catch (Exception e) {
                        tvLoginRegisterResult.setText("Error: Registration failed.");
                    }
                }
            }
        });

        // Button to Login User
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    if (userDatabase.containsKey(username)) {
                        String[] storedData = userDatabase.get(username);
                        String storedHashedPassword = storedData[0];

                        try {
                            String hashedPassword = hashPasswordWithSalt(password);

                            // Compare the hashed passwords
                            if (storedHashedPassword.equals(hashedPassword)) {
                                tvLoginRegisterResult.setText("Login successful!");
                            } else {
                                tvLoginRegisterResult.setText("Login failed: Incorrect password.");
                            }
                        } catch (Exception e) {
                            tvLoginRegisterResult.setText("Error: Login failed.");
                        }
                    } else {
                        tvLoginRegisterResult.setText("Login failed: User not found.");
                    }
                } else {
                    tvLoginRegisterResult.setText("Please enter username and password.");
                }
            }
        });
    }

    // Generate RSA Key Pair
    private void generateRSAKeys() {
        try {
            // Create a KeyPairGenerator for RSA encryption
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);  // Use a 2048-bit RSA key size
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            publicKey = keyPair.getPublic();  // Store public key
            privateKey = keyPair.getPrivate();  // Store private key
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Encrypt NI Number with RSA Public Key
    private String encryptNINumber(String niNumber) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  // Initialise cipher in encryption mode with public key
        byte[] encryptedData = cipher.doFinal(niNumber.getBytes());  // Encrypt the NI number

        // Return Base64-encoded string
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    // Decrypt NI Number with RSA Private Key
    private String decryptNINumber(String encryptedNINumber) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  // Initialise cipher in decryption mode with private key
        byte[] decryptedData = cipher.doFinal(Base64.decode(encryptedNINumber, Base64.DEFAULT));  // Decrypt the NI number

        // Return decrypted NI number as string
        return new String(decryptedData);
    }

    // Hash the password with salt using SHA-256
    private String hashPasswordWithSalt(String password) throws Exception {
        byte[] salt = generateSalt();
        String saltedPassword = password + Base64.encodeToString(salt, Base64.DEFAULT);  // Use Base64 to encode salt

        // Hash the salted password using SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(saltedPassword.getBytes());

        return bytesToHex(hashedBytes);  // Convert hashed bytes to hex string
    }

    // Generate random salt
    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);  // Use SecureRandom to generate salt
        return salt;
    }

    // Convert byte array to hexadecimal string
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Validate all input fields for registration
    private boolean validateInputFields(String username, String password, String niNumber) {
        if (!validateUsername(username)) {
            return false;
        }
        if (!validatePassword(password)) {
            return false;
        }
        if (!validateNINumber(niNumber)) {
            return false;
        }
        return true;
    }

    // Validate username (letters only)
    private boolean validateUsername(String username) {
        if (!username.matches("[a-zA-Z ]+")) {
            tvLoginRegisterResult.setText("Username should contain only letters.");
            return false;
        }
        return true;
    }

    // Validate password for hashing
    private boolean validatePassword(String password) {
        // Validate that password contains at least 1 uppercase letter, 1 symbol, and is at least 6 characters long
        if (!password.matches("^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,}$")) {
            tvLoginRegisterResult.setText("Password must be at least 6 characters long, contain one uppercase letter, and one symbol.");
            return false;
        }
        return true;
    }

    // Validate NI Number (2 letters, 6 digits, 1 letter)
    private boolean validateNINumber(String niNumber) {
        if (!niNumber.matches("^[A-Za-z]{2}\\d{6}[A-Za-z]$")) {
            tvLoginRegisterResult.setText("NI Number should follow the format: 2 letters, 6 digits, and 1 letter.");
            return false;
        }
        return true;
    }
}

