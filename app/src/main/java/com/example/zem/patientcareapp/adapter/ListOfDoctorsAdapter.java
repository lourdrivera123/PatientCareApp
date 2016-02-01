package com.example.zem.patientcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.zem.patientcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListOfDoctorsAdapter extends ArrayAdapter {
    private ArrayList<HashMap<String, String>> objects;
    private static LayoutInflater inflater = null;

    public ListOfDoctorsAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    public View getView(int position, View vi, ViewGroup parent) {
            vi = inflater.inflate(R.layout.list_item_doctors_fragment, null);

            TextView doctor_name = (TextView) vi.findViewById(R.id.doctor_name);
            TextView specialty = (TextView) vi.findViewById(R.id.specialty);

            HashMap<String, String> doctor;
            doctor = objects.get(position);

            doctor_name.setText(doctor.get("fullname"));
            specialty.setText(doctor.get("name"));

        return vi;
    }
}
