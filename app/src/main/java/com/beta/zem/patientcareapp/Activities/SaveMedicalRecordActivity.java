package com.beta.zem.patientcareapp.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.beta.zem.patientcareapp.Controllers.ClinicController;
import com.beta.zem.patientcareapp.Controllers.DoctorController;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Controllers.PatientRecordController;
import com.beta.zem.patientcareapp.Controllers.PatientTreatmentsController;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;
import com.beta.zem.patientcareapp.Model.PatientRecord;
import com.beta.zem.patientcareapp.Network.PostRequest;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SaveMedicalRecordActivity extends AppCompatActivity implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener {
    Toolbar medRecord_toolbar;
    EditText date, complaint, diagnosis;
    ListView list_of_treatments;
    AutoCompleteTextView search_doctor, search_clinic;
    TextView add_treatment;
    LinearLayout root;

    String request;
    int doctor_id = 0, clinic_id = 0;

    ArrayList<HashMap<String, String>> arrayOfDoctors, treatments_items, arrayOfClinics;
    ArrayList<String> listOfDoctors, listOfClinics, listOfTreatments;

    Menu menu;
    ProgressDialog pDialog;
    ArrayAdapter search_doctor_adapter, search_clinics_adapter, treatmentsAdapter;

    DbHelper db;
    PatientRecord record;
    DoctorController doctor_controller;
    ClinicController cc;
    PatientRecordController prc;
    PatientTreatmentsController ptc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_new_medical_record);

        date = (EditText) findViewById(R.id.date);
        list_of_treatments = (ListView) findViewById(R.id.list_of_treatments);
        search_doctor = (AutoCompleteTextView) findViewById(R.id.search_doctor);
        search_clinic = (AutoCompleteTextView) findViewById(R.id.search_clinic);
        complaint = (EditText) findViewById(R.id.complaint);
        diagnosis = (EditText) findViewById(R.id.diagnosis);
        add_treatment = (TextView) findViewById(R.id.add_treatment);
        root = (LinearLayout) findViewById(R.id.root);

        medRecord_toolbar = (Toolbar) findViewById(R.id.medRecord_toolbar);
        setSupportActionBar(medRecord_toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("New Record");

        db = new DbHelper(this);
        cc = new ClinicController(this);
        doctor_controller = new DoctorController(this);
        prc = new PatientRecordController(this);
        ptc = new PatientTreatmentsController(this);

        arrayOfDoctors = doctor_controller.getDoctorName();
        arrayOfClinics = cc.getAllClinics();
        listOfDoctors = new ArrayList<>();
        listOfClinics = new ArrayList<>();
        listOfTreatments = new ArrayList<>();
        treatments_items = new ArrayList<>();

        for (int x = 0; x < arrayOfDoctors.size(); x++)
            listOfDoctors.add(arrayOfDoctors.get(x).get("fullname"));

        for (int x = 0; x < arrayOfClinics.size(); x++)
            listOfClinics.add(arrayOfClinics.get(x).get("clinic_name"));

        search_clinics_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfClinics);
        search_clinic.setAdapter(search_clinics_adapter);
        search_clinic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item_clicked = parent.getItemAtPosition(position).toString();
                int item_id = listOfClinics.indexOf(item_clicked);
                clinic_id = Integer.parseInt(arrayOfClinics.get(item_id).get("clinic_id"));
            }
        });

        search_doctor_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfDoctors);
        search_doctor.setAdapter(search_doctor_adapter);
        search_doctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String doctor_clicked = parent.getItemAtPosition(position).toString();
                int itemID = listOfDoctors.indexOf(doctor_clicked);
                doctor_id = Integer.parseInt(arrayOfDoctors.get(itemID).get("doctor_id"));
            }
        });

        treatmentsAdapter = new ArrayAdapter<>(this, R.layout.treatments_list_layout, listOfTreatments);
        list_of_treatments.setAdapter(treatmentsAdapter);

        list_of_treatments.setOnCreateContextMenuListener(this);
        date.setOnClickListener(this);
        add_treatment.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.save_and_cancel, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                String s_date = date.getText().toString();
                String s_doctor = search_doctor.getText().toString();
                String s_complaint = complaint.getText().toString();
                String s_diagnosis = diagnosis.getText().toString();
                String s_clinic = search_clinic.getText().toString();

                if (s_date.equals("") || s_doctor.equals("") || s_complaint.equals("") || s_diagnosis.equals("") || s_clinic.equals("") || treatments_items.size() == 0) {
                    if (date.getText().toString().equals(""))
                        date.setError("Field required");
                    if (search_doctor.getText().toString().equals(""))
                        search_doctor.setError("Field required");
                    if (complaint.getText().toString().equals(""))
                        complaint.setError("Field required");
                    if (treatments_items.size() == 0)
                        add_treatment.setError("Field required");
                    if (diagnosis.getText().toString().equals(""))
                        diagnosis.setError("Field required");
                    if (search_clinic.getText().toString().equals(""))
                        search_clinic.setError("Field required");
                } else {
                    record = new PatientRecord();
                    record.setComplaints(s_complaint);
                    record.setFindings(s_diagnosis);
                    record.setDate(s_date);
                    record.setDoctorID(doctor_id);
                    record.setClinicID(clinic_id);
                    record.setDoctorName(s_doctor);
                    record.setClinicName(s_clinic);
                    request = "insert";

                    pDialog = new ProgressDialog(this);
                    pDialog.setMessage("Please wait...");
                    pDialog.show();

                    HashMap<String, String> map = new HashMap<>();
                    map.put("request", "crud");
                    map.put("action", "insert");
                    map.put("table", "patient_records");
                    map.put("patient_id", String.valueOf(SidebarActivity.getUserID()));
                    map.put("doctor_id", String.valueOf(doctor_id));
                    map.put("clinic_id", String.valueOf(clinic_id));
                    map.put("doctor_name", s_doctor);
                    map.put("clinic_name", s_clinic);
                    map.put("complaints", s_complaint);
                    map.put("findings", s_diagnosis);
                    map.put("record_date", s_date);

                    PostRequest.send(SaveMedicalRecordActivity.this, map, new RespondListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject response) {
                            try {
                                int success = response.getInt("success");

                                if (success == 1) {
                                    int last_inserted_id = response.getInt("last_inserted_id");
                                    record.setRecordID(last_inserted_id);
                                    insertTreatments(last_inserted_id, record);
                                    Intent intent = new Intent(SaveMedicalRecordActivity.this, SidebarActivity.class);
                                    intent.putExtra("select", 4);
                                    startActivity(intent);
                                    SaveMedicalRecordActivity.this.finish();
                                }
                            } catch (JSONException e) {
                                Log.d("saveMedRecord1", e + "");
                                Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                            }
                            pDialog.dismiss();
                        }
                    }, new ErrorListener<VolleyError>() {
                        public void getError(VolleyError error) {
                            pDialog.dismiss();
                            Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                break;

            case R.id.cancel:
                this.finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date:
                FragmentManager fm = getSupportFragmentManager();
                CalendarDatePickerDialogFragment datepicker;
                String birth = date.getText().toString();
                int month, year, day;

                if (birth.equals("")) {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DATE);
                } else {
                    int indexOfYear = birth.indexOf("-");
                    int indexOfMonthandDay = birth.lastIndexOf("-");
                    year = Integer.parseInt(birth.substring(0, indexOfYear));
                    month = Integer.parseInt(birth.substring(indexOfYear + 1, indexOfMonthandDay)) - 1;
                    day = Integer.parseInt(birth.substring(indexOfMonthandDay + 1, birth.length()));
                }

                datepicker = CalendarDatePickerDialogFragment.newInstance(this, year, month, day);

                datepicker.show(fm, "fragment_date_picker_name");
                break;

            case R.id.add_treatment:
                readDialog();
                break;
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        String dateStr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        date.setText(dateStr);
        date.setError(null);
    }

    public void readDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_treatment);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        final EditText dosage = (EditText) dialog.findViewById(R.id.dosage);
        final AutoCompleteTextView search_medicine = (AutoCompleteTextView) dialog.findViewById(R.id.search_medicine);
        Button save_treatment = (Button) dialog.findViewById(R.id.save_treatment);
        Button cancel_treatment = (Button) dialog.findViewById(R.id.cancel_treatment);

        search_medicine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        add_treatment.setError(null);

        cancel_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_dosage = dosage.getText().toString();
                String s_medicine = search_medicine.getText().toString();

                if (s_dosage.equals("") || s_medicine.equals("")) {
                    if (s_medicine.equals(""))
                        search_medicine.setError("Field required");
                    if (s_dosage.equals(""))
                        dosage.setError("Field required");
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("medicine_name", s_medicine);
                    map.put("dosage", s_dosage);
                    treatments_items.add(map);
                    listOfTreatments.add(s_medicine + " - " + s_dosage);

                    treatmentsAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
    }

    public void insertTreatments(final int last_inserted_id, final PatientRecord record) {

        try {
            JSONArray master_arr = new JSONArray();

            for (int x = 0; x < treatments_items.size(); x++) {
                HashMap<String, String> hash = new HashMap<>();

                hash.put("patient_records_id", String.valueOf(last_inserted_id));
                hash.put("medicine_id", "0");
                hash.put("medicine_name", treatments_items.get(x).get("medicine_name"));
                hash.put("frequency", treatments_items.get(x).get("dosage"));

                JSONObject obj_for_server = new JSONObject(hash);
                master_arr.put(obj_for_server);
            }

            final JSONObject json_to_be_passed = new JSONObject();
            json_to_be_passed.put("json_treatments", master_arr);

            HashMap<String, String> hash = new HashMap<>();
            hash.put("table", "patient_treatments");
            hash.put("request", "crud");
            hash.put("action", "multiple_insert");
            hash.put("jsobj", json_to_be_passed.toString());

            PostRequest.send(SaveMedicalRecordActivity.this, hash, new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {
                    try {
                        if (prc.savePatientRecord(record, "insert")) {
                            int start_id = response.getInt("last_inserted_id");
                            ArrayList<HashMap<String, String>> newTreatments = new ArrayList<>();

                            for (int x = 0; x < treatments_items.size(); x++) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("patient_records_id", String.valueOf(last_inserted_id));
                                map.put("treatments_id", String.valueOf(start_id));
                                map.put("medicine_name", treatments_items.get(x).get("medicine_name"));
                                map.put("frequency", treatments_items.get(x).get("dosage"));
                                newTreatments.add(x, map);
                                start_id = start_id + 1;
                            }

                            if (ptc.savePatientTreatments(newTreatments, "insert")) {
                                Intent intent = new Intent(SaveMedicalRecordActivity.this, SidebarActivity.class);
                                intent.putExtra("select", 4);
                                startActivity(intent);
                                SaveMedicalRecordActivity.this.finish();
                                Snackbar.make(root, "Saved successfully", Snackbar.LENGTH_SHORT).show();
                            } else
                                Snackbar.make(root, "Unable to save treatments", Snackbar.LENGTH_SHORT).show();
                        } else
                            Snackbar.make(root, "Unable to save record", Snackbar.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d("saveMedRecord2", e + "");
                        Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    pDialog.dismiss();
                    Log.d("saveMedRecord4", error + "");
                    Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("saveMedRecord3", e + "");
            Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
        }
    }
}
