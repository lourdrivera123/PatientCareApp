package com.beta.zem.patientcareapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.beta.zem.patientcareapp.Controllers.ClinicController;
import com.beta.zem.patientcareapp.Controllers.DoctorController;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Model.Doctor;
import com.beta.zem.patientcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorActivity extends AppCompatActivity implements View.OnClickListener {
    TextView doctor_name, specialty, clinic_name, clinic_schedule, clinic_address_first_line, clinic_address_second_line, contact_number;
    ListView listOfDoctors;
    Toolbar doctorsToolbar;
    Button scheduleConsultation;

    ArrayList<HashMap<String, String>> hashClinicsByDoctorID;

    DbHelper dbHelper;
    Doctor doctor;

    int id = 0;
    int doctorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile);

        doctorsToolbar = (Toolbar) findViewById(R.id.doctorsToolbar);
        doctor_name = (TextView) findViewById(R.id.doctor_name);
        specialty = (TextView) findViewById(R.id.specialty);
        listOfDoctors = (ListView) findViewById(R.id.listOfDoctors);
        scheduleConsultation = (Button) findViewById(R.id.scheduleConsultation);

        dbHelper = new DbHelper(this);
        Intent intent = getIntent();
        DoctorController doctor_controller = new DoctorController(this);

        doctorID = intent.getIntExtra("doc_id", 0);
        ClinicController cc = new ClinicController(this);
        hashClinicsByDoctorID = cc.getClinicByDoctorID(doctorID);

        CustomAdapterForDoctor customAdapter = new CustomAdapterForDoctor(this, R.layout.list_item_of_doctors_layout, R.id.clinic_name, hashClinicsByDoctorID);
        listOfDoctors.setAdapter(customAdapter);

        if (doctorID > 0) {
            doctor = doctor_controller.getDoctorByID(doctorID);

            doctor_name.setText(doctor.getLname() + ", " + doctor.getFname() + " " + doctor.getMname().charAt(0) + ".");
            specialty.setText("(" + doctor.getSpecialty() + ", " + doctor.getSub_specialty() + ")");
        }

        setSupportActionBar(doctorsToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Doctor Information");
        doctorsToolbar.setNavigationIcon(R.drawable.ic_back);

        scheduleConsultation.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PatientConsultationActivity.class);
        intent.putExtra("request", "add");
        intent.putExtra("doctorID", doctorID);
        startActivity(intent);
    }

    public class CustomAdapterForDoctor extends ArrayAdapter {

        public CustomAdapterForDoctor(Context context, int resource, int textViewResourceId, ArrayList<HashMap<String, String>> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);//let the adapter handle setting up the row views

            clinic_name = (TextView) v.findViewById(R.id.clinic_name);
            clinic_address_first_line = (TextView) v.findViewById(R.id.clinic_address_first_line);
            clinic_address_second_line = (TextView) v.findViewById(R.id.clinic_address_second_line);
            contact_number = (TextView) v.findViewById(R.id.contact_number);
            clinic_schedule = (TextView) v.findViewById(R.id.clinic_schedule);
            String firstLine, secondLine, building = "", barangay = "";

            Log.d("hashClinics", hashClinicsByDoctorID + "");

//            if (!hashClinicsByDoctorID.get(position).get(ClinicController.CLINIC_BUILDING).equals(""))
//                building = "#" + hashClinicsByDoctorID.get(position).get(ClinicController.CLINIC_BUILDING);
//            if (!hashClinicsByDoctorID.get(position).get(ClinicController.CLINIC_BARANGAY).equals(""))
//                barangay = ", " + hashClinicsByDoctorID.get(position).get(ClinicController.CLINIC_BARANGAY);
//
//            firstLine = (building + hashClinicsByDoctorID.get(position).get(ClinicController.CLINIC_STREET) + barangay).trim();
//            secondLine = hashClinicsByDoctorID.get(position).get(ClinicController.CLINIC_CITY) +
//                    ", " + hashClinicsByDoctorID.get(position).get(ClinicController.CLINIC_REGION) +
//                    ", Philippines, " + hashClinicsByDoctorID.get(position).get(ClinicController.CLINIC_ZIP);

            clinic_name.setText(hashClinicsByDoctorID.get(position).get("name"));
            contact_number.setText("Contact #: " + hashClinicsByDoctorID.get(position).get("contact_no"));
            clinic_schedule.setText(hashClinicsByDoctorID.get(position).get("clinic_sched"));
//            clinic_address_first_line.setText(firstLine);
//            clinic_address_second_line.setText(secondLine);

            return v;
        }
    }
}
