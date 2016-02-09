package com.example.zem.patientcareapp.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Activities.GoogleMapsActivity;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.OrderPreferenceController;
import com.example.zem.patientcareapp.Controllers.PatientConsultationController;
import com.example.zem.patientcareapp.Controllers.PatientRecordController;
import com.example.zem.patientcareapp.Controllers.PatientTreatmentsController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Model.PatientRecord;
import com.example.zem.patientcareapp.SwipeTabsModule.MasterTabActivity;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.Activities.ProductsActivity;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeTileFragment extends Fragment implements View.OnClickListener {
    LinearLayout orderLayout, refillLayout, pointsLayout, prescriptionLayout, consultationLayout;
    TextView notifConsultation;
    LinearLayout root;

    DbHelper db;
    PatientConsultationController pcc;
    PatientTreatmentsController ptc;
    PatientRecordController prc;
    OrderPreferenceController opc;
    static int patientID;
    Context context;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_home_layout, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
        db = new DbHelper(context);
        pcc = new PatientConsultationController(context);
        opc = new OrderPreferenceController(context);
        prc = new PatientRecordController(context);
        ptc = new PatientTreatmentsController(context);

        orderLayout = (LinearLayout) rootView.findViewById(R.id.orderLayout);
        refillLayout = (LinearLayout) rootView.findViewById(R.id.refillLayout);
        pointsLayout = (LinearLayout) rootView.findViewById(R.id.pointsLayout);
        prescriptionLayout = (LinearLayout) rootView.findViewById(R.id.prescriptionLayout);
        consultationLayout = (LinearLayout) rootView.findViewById(R.id.consultationLayout);
        notifConsultation = (TextView) rootView.findViewById(R.id.notifConsultation);
        root = (LinearLayout) rootView.findViewById(R.id.root);

        orderLayout.setOnClickListener(this);
        refillLayout.setOnClickListener(this);
        pointsLayout.setOnClickListener(this);
        prescriptionLayout.setOnClickListener(this);
        consultationLayout.setOnClickListener(this);

        patientID = SidebarActivity.getUserID();
        SyncToClinicPatientRecords();
    }

    @Override
    public void onResume() {
        ListOfPatientsRequest.getJSONobj(getActivity(), "get_consultations_notif&patient_ID=" + patientID, "consultations", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray json_mysql = response.getJSONArray("consultations");
                        notifConsultation.setVisibility(View.VISIBLE);

                        for (int x = 0; x < json_mysql.length(); x++) {
                            JSONObject obj = json_mysql.getJSONObject(x);

                            if (!pcc.updateSomeConsultation(obj))
                                Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                        }
                    } else
                        notifConsultation.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    Log.d("home1", e + "");
                    Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                Log.d("home2", e+"");
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent(getActivity(), MasterTabActivity.class);
        switch (v.getId()) {

            case R.id.orderLayout:
                 //this condition is to determine if the user's age is >= 60 senior and they have uploaded a valid senior id.
//                if(true){
//                    startActivity(new Intent(getActivity(), SeniorCitizenActivity.class));
//                }

                OrderModel order_model = opc.getOrderPreference();
                if (order_model.hasSelectedBranch())
                    startActivity(new Intent(getActivity(), ProductsActivity.class));
                else
                    startActivity(new Intent(getActivity(), GoogleMapsActivity.class));
                break;

//            case R.id.refillLayout:
//                intent.putExtra("selected", 0);
//                startActivity(intent);
//                break;

            case R.id.pointsLayout:
                intent.putExtra("selected", 0);
                startActivity(intent);
                break;

            case R.id.prescriptionLayout:
                intent.putExtra("selected", 1);
                startActivity(intent);
                break;

            case R.id.consultationLayout:
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("request", "crud");
                hashMap.put("table", "consultations");
                hashMap.put("action", "update_with_custom_where_clause");
                hashMap.put("isRead", String.valueOf(1));
                hashMap.put("custom_where_clause", "patient_id = " + String.valueOf(SidebarActivity.getUserID()) + " and isRead=0 and is_approved!=0");

                final ProgressDialog pdialog = new ProgressDialog(getActivity());
                pdialog.setCancelable(false);
                pdialog.setMessage("Loading...");
                pdialog.show();

                PostRequest.send(getActivity(), hashMap, new RespondListener<JSONObject>() {
                    @Override
                    public void getResult(JSONObject response) {
                        pdialog.dismiss();
                    }
                }, new ErrorListener<VolleyError>() {
                    public void getError(VolleyError error) {
                        pdialog.dismiss();
                        Snackbar.make(root, "Please check your Network connection", Snackbar.LENGTH_SHORT).show();
                    }
                });

                intent.putExtra("selected", 2);
                startActivity(intent);
                break;
        }
    }

    public void SyncToClinicPatientRecords() {
        ListOfPatientsRequest.getJSONobj(getActivity(), "sync_clinics_record&patient_id=" + patientID, "clinic_patients_records", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    Log.d("response", response+"");

                    if (success == 1) {
                        JSONArray json_mysql = response.getJSONArray("clinic_patients_records");
//                        insertHistory(json_mysql);
                    }
                } catch (Exception e) {
                    Log.d("home4", e + "");
                    Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                Log.d("home3", e+"");
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void insertHistory(final JSONArray array) {
        try {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setView(R.layout.progress_stuffing);
//            builder.setCancelable(false);
//            final AppCompatDialog pDialog = builder.create();
//            pDialog.show();

            JSONObject object = array.getJSONObject(0);

            final HashMap<String, String> map = new HashMap<>();
            map.put("table", "patient_records");
            map.put("request", "crud");
            map.put("action", "insert");
            map.put("clinic_patient_record_id", object.getString("clinic_patients_record_id"));
            map.put("patient_id", String.valueOf(SidebarActivity.getUserID()));
            map.put("doctor_id", String.valueOf(object.getInt("doctor_id")));
            map.put("clinic_id", String.valueOf(object.getInt("clinic_id")));
            map.put("complaints", object.getString("complaints"));
            map.put("findings", object.getString("findings"));
            map.put("record_date", object.getString("record_date"));

            PostRequest.send(getActivity(), map, new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {
                    try {
                        int success = response.getInt("success");
                        if (success == 1) {
                            int last_inserted_id = response.getInt("last_inserted_id");

                            final PatientRecord pr = new PatientRecord();
                            pr.setRecordID(last_inserted_id);
                            pr.setCpr_id(Integer.parseInt(map.get("clinic_patient_record_id")));
                            pr.setDoctorID(Integer.parseInt(map.get("doctor_id")));
                            pr.setClinicID(Integer.parseInt(map.get("clinic_id")));
                            pr.setComplaints(map.get("complaints"));
                            pr.setFindings(map.get("findings"));
                            pr.setDate(map.get("record_date"));

                            final JSONArray master_arr = new JSONArray();
                            final ArrayList<HashMap<String, String>> array_treatments = new ArrayList<>();

                            for (int x = 0; x < array.length(); x++) {
                                JSONObject obj = array.getJSONObject(x);
                                HashMap<String, String> hash = new HashMap<>();

                                hash.put("patient_records_id", String.valueOf(last_inserted_id));
                                hash.put("medicine_id", obj.getString("medicine_id"));
                                hash.put("medicine_name", "sample medicine_name");
                                hash.put("frequency", obj.getString("frequency"));
                                hash.put("duration", obj.getString("duration"));
                                hash.put("duration_type", obj.getString("duration_type"));

                                JSONObject obj_for_server = new JSONObject(hash);
                                master_arr.put(obj_for_server);
                                array_treatments.add(hash);
                            }

                            JSONObject json_to_be_passed = new JSONObject();
                            json_to_be_passed.put("json_treatments", master_arr);

                            HashMap<String, String> hash = new HashMap<>();
                            hash.put("table", "patient_treatments");
                            hash.put("request", "crud");
                            hash.put("action", "multiple_insert");
                            hash.put("jsobj", json_to_be_passed.toString());

                            PostRequest.send(getActivity(), hash, new RespondListener<JSONObject>() {
                                @Override
                                public void getResult(JSONObject response) {
                                    try {
                                        int start_server_id = response.getInt("last_inserted_id");

                                        for (int x = 0; x < array_treatments.size(); x++) {
                                            HashMap<String, String> map = array_treatments.get(x);
                                            map.put("treatments_id", String.valueOf(start_server_id));

                                            array_treatments.set(x, map);
                                            start_server_id += 1;
                                        }

                                        if (prc.savePatientRecord(pr, "insert")) {
                                            if (ptc.savePatientTreatments(array_treatments, "insert")) {
                                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                                dialog.setMessage("You have received your medical rejfkdlas;");
                                                dialog.setNegativeButton(null, null);
                                                dialog.setPositiveButton("Ok", null);
                                                dialog.show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.d("home7", e + "");
                                        Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                                    }
//                                    pDialog.dismiss();
                                }
                            }, new ErrorListener<VolleyError>() {
                                public void getError(VolleyError error) {
//                                    pDialog.dismiss();
                                    Log.d("home8", error+"");
                                    Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.d("home6", e + "");
                        Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                    }
//                    pDialog.dismiss();
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
//                    pDialog.dismiss();
                    Log.d("home5", error + "");
                    Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}