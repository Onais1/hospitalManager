package com.example.loginencryptionapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PatientManagementTest {

    private Context context;
    private DatabaseHelper databaseHelper;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        databaseHelper = new DatabaseHelper(context);
        databaseHelper.deleteAllPatients(); // Clear existing patients before tests
    }

    @Test
    public void testAddPatient() {
        ActivityScenario.launch(PatientManagement.class);

        // Add a new patient
        onView(withId(R.id.et_patient_name)).perform(replaceText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.et_patient_age)).perform(replaceText("30"), closeSoftKeyboard());
        onView(withId(R.id.et_patient_gender)).perform(replaceText("Male"), closeSoftKeyboard());
        onView(withId(R.id.btn_add_patient)).perform(click());

        // Verify patient appears in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.lv_patients))
                .atPosition(0)
                .check(matches(withText(containsString("John Doe - 30 - Male")))); // Use containsString
    }

    @Test
    public void testUpdatePatient() {
        ActivityScenario.launch(PatientManagement.class);

        // Add a new patient
        onView(withId(R.id.et_patient_name)).perform(replaceText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.et_patient_age)).perform(replaceText("30"), closeSoftKeyboard());
        onView(withId(R.id.et_patient_gender)).perform(replaceText("Male"), closeSoftKeyboard());
        onView(withId(R.id.btn_add_patient)).perform(click());

        // Verify the patient appears in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.lv_patients))
                .atPosition(0)
                .check(matches(withText(containsString("John Doe - 30 - Male"))));

        // Click on the patient to open the edit screen
        onData(anything())
                .inAdapterView(withId(R.id.lv_patients))
                .atPosition(0)
                .perform(click());

        // Verify the edit screen is displayed
        onView(withId(R.id.et_patient_name)).check(matches(isDisplayed())); // Ensures edit screen is open

        // Update the patient's age from 30 to 40
        onView(withId(R.id.et_patient_age)).perform(replaceText("40"), closeSoftKeyboard());

        // Click the update button to save the changes
        onView(withId(R.id.btn_update_patient)).perform(click());

        // Wait for the UI to update
        Espresso.onIdle();

        // Verify the updated patient appears in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.lv_patients))
                .atPosition(0)
                .check(matches(withText(containsString("John Doe - 40 - Male")))); // Updated age
    }

    @Test
    public void testDeletePatient() {
        ActivityScenario.launch(PatientManagement.class);

        // Add a new patient
        onView(withId(R.id.et_patient_name)).perform(replaceText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.et_patient_age)).perform(replaceText("40"), closeSoftKeyboard());
        onView(withId(R.id.et_patient_gender)).perform(replaceText("Male"), closeSoftKeyboard());
        onView(withId(R.id.btn_add_patient)).perform(click());

        // Verify patient appears in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.lv_patients))
                .atPosition(0)
                .check(matches(withText(containsString("John Doe - 40 - Male"))));

        // Long press to delete patient
        onData(anything())
                .inAdapterView(withId(R.id.lv_patients))
                .atPosition(0)
                .perform(ViewActions.longClick());

        // Handle the confirmation dialog by clicking "Yes"
        onView(withText("Yes"))
                .perform(click());

        // Wait for the UI to update
        Espresso.onIdle();

        // Verify patient no longer appears in the ListView
        onView(withText(containsString("John Doe - 40 - Male"))).check(doesNotExist());
    }
}