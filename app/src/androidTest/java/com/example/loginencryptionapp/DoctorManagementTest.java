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
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;

import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DoctorManagementTest {

    private Context context;
    private DatabaseHelper databaseHelper;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        databaseHelper = new DatabaseHelper(context);
        databaseHelper.deleteAllDoctors(); // Clear existing doctors before tests
    }

    @Test
    public void testAddDoctor() {
        ActivityScenario.launch(DoctorManagementActivity.class);

        // Add a new doctor
        onView(withId(R.id.etDoctorName)).perform(replaceText("Dr. Smith"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorSpecialization)).perform(replaceText("Neurology"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorPhone)).perform(replaceText("0987654321"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorEmail)).perform(replaceText("dr.smith@neurology.com"), closeSoftKeyboard());
        onView(withId(R.id.btnAddDoctor)).perform(click());

        // Verify doctor appears in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.lvDoctors))
                .atPosition(0)
                .check(matches(withText(containsString("Dr. Smith - Neurology - 0987654321 - dr.smith@neurology.com"))));
    }

    @Test
    public void testUpdateDoctor() {
        ActivityScenario.launch(DoctorManagementActivity.class);

        // Add a new doctor
        onView(withId(R.id.etDoctorName)).perform(replaceText("Dr. Smith"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorSpecialization)).perform(replaceText("Cardiology"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorPhone)).perform(replaceText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorEmail)).perform(replaceText("dr.smith@example.com"), closeSoftKeyboard());
        onView(withId(R.id.btnAddDoctor)).perform(click());

        // Click on the doctor to open the edit screen
        onData(anything())
                .inAdapterView(withId(R.id.lvDoctors))
                .atPosition(0)
                .perform(click());

        // Update the doctor's details
        onView(withId(R.id.etDoctorSpecialization)).perform(replaceText("Neurology"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorPhone)).perform(replaceText("0987654321"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorEmail)).perform(replaceText("dr.smith@neurology.com"), closeSoftKeyboard());
        onView(withId(R.id.btnUpdateDoctor)).perform(click());

        // Verify the updated doctor appears in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.lvDoctors))
                .atPosition(0)
                .check(matches(withText(containsString("Dr. Smith - Neurology - 0987654321 - dr.smith@neurology.com"))));
    }

    @Test
    public void testDeleteDoctor() {
        ActivityScenario.launch(DoctorManagementActivity.class);

        // Add a new doctor
        onView(withId(R.id.etDoctorName)).perform(replaceText("Dr. Smith"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorSpecialization)).perform(replaceText("Neurology"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorPhone)).perform(replaceText("0987654321"), closeSoftKeyboard());
        onView(withId(R.id.etDoctorEmail)).perform(replaceText("dr.smith@neurology.com"), closeSoftKeyboard());
        onView(withId(R.id.btnAddDoctor)).perform(click());

        // Verify doctor appears in the ListView
        onData(anything())
                .inAdapterView(withId(R.id.lvDoctors))
                .atPosition(0)
                .check(matches(withText(containsString("Dr. Smith - Neurology - 0987654321 - dr.smith@neurology.com"))));

        // Long press to delete doctor
        onData(anything())
                .inAdapterView(withId(R.id.lvDoctors))
                .atPosition(0)
                .perform(ViewActions.longClick());

        // Handle the confirmation dialog by clicking "Yes"
        onView(withText("Yes")).perform(click());

        // Verify doctor no longer appears in the ListView
        onView(withText(containsString("Dr. Smith - Neurology - 0987654321 - dr.smith@neurology.com"))).check(doesNotExist());
    }
}
