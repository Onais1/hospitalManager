package com.example.loginencryptionapp;

public class Appointment {
    private int id;
    private String patientName;
    private String doctorName;
    private long dateTime;
    private String notes;

    public Appointment(int id, String patientName, String doctorName, long dateTime, String notes) {
        this.id = id;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.dateTime = dateTime;
        this.notes = notes;
    }

    // Getter methods for each property
    public int getId() { return id; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public long getDateTime() { return dateTime; }
    public String getNotes() { return notes; }
}
