package com.example.loginencryptionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class DoctorAdapter extends ArrayAdapter<Doctor> {
    private Context context;
    private ArrayList<Doctor> doctors;

    public DoctorAdapter(Context context, ArrayList<Doctor> doctors) {
        super(context, 0, doctors);
        this.context = context;
        this.doctors = doctors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        Doctor doctor = doctors.get(position);
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(doctor.toString());

        return convertView;
    }
}