package com.example.zem.patientcareapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zem.patientcareapp.Controllers.PatientRecordController;
import com.example.zem.patientcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewPatientRecordActivity extends AppCompatActivity {
    Intent intent;

    Toolbar toolbar;
    LinearLayout root;
    TextView clinic, doctor, record_date, complaint, findings;
    ListView list_of_medications;

    PatientRecordController prc;
    TreatmentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_record);

        root = (LinearLayout) findViewById(R.id.root);
        clinic = (TextView) findViewById(R.id.clinic);
        doctor = (TextView) findViewById(R.id.doctor);
        record_date = (TextView) findViewById(R.id.record_date);
        complaint = (TextView) findViewById(R.id.complaint);
        findings = (TextView) findViewById(R.id.findings);
        list_of_medications = (ListView) findViewById(R.id.list_of_medications);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Medical Record");

        prc = new PatientRecordController(this);

        intent = getIntent();
        int record_id = intent.getIntExtra("record_id", 0);

        if (record_id > 0) {
            ArrayList<HashMap<String, String>> patient_record = prc.getSpecPatientRecord(record_id);

            clinic.setText(patient_record.get(0).get("clinic_name"));
            doctor.setText(patient_record.get(0).get("doctor_name"));
            complaint.setText(patient_record.get(0).get("complaints"));
            findings.setText(patient_record.get(0).get("findings"));
            record_date.setText(patient_record.get(0).get("record_date"));

            adapter = new TreatmentsAdapter(this, R.layout.list_item_treatments, patient_record);
            list_of_medications.setAdapter(adapter);
        } else
            Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class TreatmentsAdapter extends ArrayAdapter {
        TextView medicine, frequency;
        ArrayList<HashMap<String, String>> objects;
        LayoutInflater inflater;

        public TreatmentsAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects) {
            super(context, resource, objects);
            this.objects = objects;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = inflater.inflate(R.layout.list_item_treatments, parent, false);

            medicine = (TextView) v.findViewById(R.id.medicine);
            frequency = (TextView) v.findViewById(R.id.frequency);

            medicine.setText(objects.get(position).get("medicine_name"));
            frequency.setText("(" + objects.get(position).get("frequency") + ")");

            return v;
        }
    }
}
