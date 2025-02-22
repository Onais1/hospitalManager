package com.example.loginencryptionapp;

import static org.junit.Assert.*;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
    private DatabaseHelper databaseHelper;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        databaseHelper = new DatabaseHelper(context);
        databaseHelper.clearDatabase();
    }

    @After
    public void tearDown() {
        databaseHelper.close();
    }

    @Test
    public void testAddPatient() {
        boolean isAdded = databaseHelper.addPatient("John Doe", 30, "Male");
        assertTrue("Failed to add patient", isAdded);

        ArrayList<String> patients = databaseHelper.getAllPatients();
        assertEquals("Patient count incorrect", 1, patients.size());
    }

    @Test
    public void testReadPatients() {
        databaseHelper.addPatient("Alice", 25, "Female");
        databaseHelper.addPatient("Bob", 40, "Male");

        ArrayList<String> patients = databaseHelper.getAllPatients();
        assertEquals("Incorrect patient count", 2, patients.size());
    }

    @Test
    public void testUpdatePatient() {
        databaseHelper.addPatient("Charlie", 35, "Male");
        int patientId = Integer.parseInt(databaseHelper.getAllPatients().get(0).split(" - ")[0]);

        boolean isUpdated = databaseHelper.updatePatient(patientId, "Charlie Brown", 36, "Male");
        assertTrue("Failed to update patient", isUpdated);

        ArrayList<String> patients = databaseHelper.getAllPatients();
        assertTrue("Updated patient not found", patients.get(0).contains("Charlie Brown"));
    }

    @Test
    public void testDeletePatient() {
        databaseHelper.addPatient("David", 50, "Male");
        int patientId = Integer.parseInt(databaseHelper.getAllPatients().get(0).split(" - ")[0]);

        boolean isDeleted = databaseHelper.deletePatient(patientId);
        assertTrue("Failed to delete patient", isDeleted);

        ArrayList<String> patients = databaseHelper.getAllPatients();
        assertEquals("Patient still exists after deletion", 0, patients.size());
    }
}
