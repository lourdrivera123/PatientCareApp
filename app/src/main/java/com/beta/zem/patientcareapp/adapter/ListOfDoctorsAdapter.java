package com.beta.zem.patientcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beta.zem.patientcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListOfDoctorsAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> objects;
    private static LayoutInflater inflater = null;

    public ListOfDoctorsAdapter(Context context, ArrayList<HashMap<String, String>> objects) {
        inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View vi, ViewGroup parent) {
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item_doctors_fragment, parent, false);

        TextView doctor_name = (TextView) vi.findViewById(R.id.doctor_name);
        TextView specialty = (TextView) vi.findViewById(R.id.specialty);

        HashMap<String, String> doctor;
        doctor = objects.get(position);

        doctor_name.setText(doctor.get("fullname"));
        specialty.setText(doctor.get("name"));

        return vi;
    }
}