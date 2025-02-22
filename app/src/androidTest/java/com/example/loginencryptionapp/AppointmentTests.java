package com.example.loginencryptionapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class AppointmentTests {

    private DatabaseHelper DatabaseHelper;
    private Context context;
    private String formattedDate;

    @Rule
    public ActivityScenarioRule<AppointmentsActivity> activityRule = new ActivityScenarioRule<>(AppointmentsActivity.class);

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        DatabaseHelper = new DatabaseHelper(context);

        // Clear existing data and add test entries
        DatabaseHelper.deleteAllPatients();
        DatabaseHelper.deleteAllDoctors();
        DatabaseHelper.deleteAllAppointments();

        DatabaseHelper.addPatient("John Doe", 30, "Male");
        DatabaseHelper.addDoctor("Dr. Smith", "Cardiology", "1234567890", "dr.smith@example.com");
    }

    @Test
    public void testAddAndDeleteAppointment() {
        // Launch the activity
        ActivityScenario.launch(AppointmentsActivity.class);

        // Select patient from spinner
        onView(withId(R.id.spinnerPatient)).perform(click());
        onData(Matchers.allOf(
                Matchers.is(Matchers.instanceOf(String.class)), // Match String objects
                Matchers.containsString("John Doe") // Match patient name
        )).perform(click());

        // Select doctor from spinner
        onView(withId(R.id.spinnerDoctor)).perform(click());
        onData(Matchers.allOf(
                Matchers.is(Matchers.instanceOf(Doctor.class)), // Match Doctor objects
                Matchers.hasToString(Matchers.containsString("Dr. Smith")) // Match toString() output
        )).perform(click());

        // Open Date and Time Picker
        onView(withId(R.id.btnDateTime)).perform(click());

        // Set Date (tomorrow)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(calendar.getTime());

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        onView(withText("OK")).perform(click());

        // Set Time (10:30 AM)
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(10, 30));
        onView(withText("OK")).perform(click());

        // Enter notes
        onView(withId(R.id.etNotes)).perform(typeText("Routine check-up"));

        // Submit appointment
        onView(withId(R.id.btnSaveAppointment)).perform(click());

        // Wait for UI update
        Espresso.onIdle();

        // Verify all input fields in the ListView item using a constraint string
        String expectedAppointmentPattern = "\\d+ - Patient: John Doe\nDoctor: Dr. Smith\nDate: 2025-02-23 10:30 AM\nNotes: Routine check-up";

        onData(Matchers.anything()).inAdapterView(withId(R.id.listViewAppointments))
                .atPosition(0).check(matches(withText(Matchers.matchesPattern(expectedAppointmentPattern))));

        // Long press the appointment to delete
        onData(Matchers.anything()).inAdapterView(withId(R.id.listViewAppointments))
                .atPosition(0).perform(longClick());

        // Confirm deletion
        onView(withText("Delete Appointment")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(click());



    }
}
