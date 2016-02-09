package com.example.zem.patientcareapp.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Activities.ViewPatientRecordActivity;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Controllers.PatientRecordController;
import com.example.zem.patientcareapp.Controllers.PatientTreatmentsController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Model.PatientRecord;
import com.example.zem.patientcareapp.Network.ListRequestFromCustomURI;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.Activities.SaveMedicalRecordActivity;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientHistoryFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView list_of_history;
    ImageButton add_record;
    TextView noResults;
    LinearLayout root;

    ArrayList<HashMap<String, String>> hashHistory;
    ArrayList<String> arrayOfRecords;
    ArrayList<Integer> selectedList;

    private SelectionAdapter mAdapter;

    DbHelper dbHelper;
    PatientRecordController prc;
    PatientTreatmentsController ptc;
    Helpers helpers;
    Dialog dialog;
    ProgressDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_records, container, false);

        dbHelper = new DbHelper(getActivity());
        ptc = new PatientTreatmentsController(getActivity());
        helpers = new Helpers();
        arrayOfRecords = new ArrayList<>();
        selectedList = new ArrayList<>();

        add_record = (ImageButton) rootView.findViewById(R.id.add_record);
        noResults = (TextView) rootView.findViewById(R.id.noResults);
        list_of_history = (ListView) rootView.findViewById(R.id.list_of_history);
        root = (LinearLayout) rootView.findViewById(R.id.root);

        list_of_history.setOnItemClickListener(this);
        list_of_history.setOnCreateContextMenuListener(this);
        add_record.setOnClickListener(this);

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Please wait...");

        SyncClinicsRecord();

        return rootView;
    }

    @Override
    public void onResume() {
        prc = new PatientRecordController(getActivity());
        hashHistory = prc.getAllPatientRecords();

        mAdapter = new SelectionAdapter(getActivity(), R.layout.listview_history_views, hashHistory);
        list_of_history.setAdapter(mAdapter);

        if (hashHistory.size() == 0) {
            noResults.setVisibility(View.VISIBLE);
            list_of_history.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.delete_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = menuInfo.position;

        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(getActivity());
        confirmationDialog.setTitle("Are you sure you want to delete this record?");
        confirmationDialog.setNegativeButton("No", null);
        confirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int record_id = Integer.parseInt(hashHistory.get(pos).get("record_id"));

                Log.d("record_id", record_id + "");

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("table", "patient_records");
                hashMap.put("request", "crud");
                hashMap.put("action", "delete");
                hashMap.put("id", String.valueOf(record_id));

                final ProgressDialog pdialog = new ProgressDialog(getActivity());
                pdialog.setCancelable(false);
                pdialog.setMessage("Loading...");
                pdialog.show();

                PostRequest.send(getActivity(), hashMap, new RespondListener<JSONObject>() {
                    @Override
                    public void getResult(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                if (prc.deleteRecord(record_id) && ptc.deleteTreatmentsByRecordID(record_id)) {
                                    hashHistory.remove(pos);
                                    mAdapter.notifyDataSetChanged();
                                    Snackbar.make(root, "Record has been deleted", Snackbar.LENGTH_SHORT).show();

                                    if (hashHistory.size() == 0) {
                                        list_of_history.setVisibility(View.GONE);
                                        noResults.setVisibility(View.VISIBLE);
                                    }
                                } else
                                    Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                            } else
                                Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Snackbar.make(root, e + "", Snackbar.LENGTH_SHORT).show();
                        }
                        pdialog.dismiss();
                    }
                }, new ErrorListener<VolleyError>() {
                    public void getError(VolleyError error) {
                        pdialog.dismiss();
                        Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
        confirmationDialog.show();

        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int view_record_id = Integer.parseInt(hashHistory.get(position).get("record_id"));

        Intent view_record = new Intent(getActivity(), ViewPatientRecordActivity.class);
        view_record.putExtra("record_id", view_record_id);
        startActivity(view_record);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_record:
                dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.add_new_med_records);

                ImageButton clinicRecord = (ImageButton) dialog.findViewById(R.id.clinicRecord);
                clinicRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        final Dialog dialog2 = new Dialog(getActivity());
                        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog2.setContentView(R.layout.get_clinic_record);
                        dialog2.show();

                        final EditText username = (EditText) dialog2.findViewById(R.id.username);
                        final EditText password = (EditText) dialog2.findViewById(R.id.password);
                        Button submitBtn = (Button) dialog2.findViewById(R.id.submitBtn);

                        password.setTransformationMethod(new PasswordTransformationMethod());

                        submitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (username.getText().toString().equals(""))
                                    username.setError("Field required");
                                else if (password.getText().toString().equals(""))
                                    password.setError("Field required");
                                else {
                                    String uname = username.getText().toString();
                                    String pword = password.getText().toString();
                                    dialog2.dismiss();

                                    getClinicRecords(uname, pword);
                                }
                            }
                        });
                    }
                });

                final ImageButton personalRecord = (ImageButton) dialog.findViewById(R.id.personalRecord);
                personalRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), SaveMedicalRecordActivity.class));
                        getActivity().finish();
                        dialog.dismiss();
                    }
                });
                dialog.show();

                break;
        }
    }

    void updateList() {
        noResults.setVisibility(View.GONE);
        list_of_history.setVisibility(View.VISIBLE);

        hashHistory.clear();
        hashHistory = prc.getAllPatientRecords();
        mAdapter = new SelectionAdapter(getActivity(), R.layout.listview_history_views, hashHistory);
        list_of_history.setAdapter(mAdapter);
    }

    void SyncClinicsRecord() {
        final ProgressDialog progress1 = new ProgressDialog(getActivity());
        progress1.setMessage("Please wait...");
        progress1.show();

        String url = "db/get.php?q=get_uname_pword_from_clinic&patient_id=" + SidebarActivity.getUserID();

        ListRequestFromCustomURI.getJSONobj(url, new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray json_mysql = response.getJSONArray("clinic_patient_doctor");
                        JSONArray array = new JSONArray();
                        int cpr_id;

                        for (int x = 0; x < json_mysql.length() - 1; x++) {
                            JSONObject obj = json_mysql.getJSONObject(x);

                            cpr_id = obj.getInt("cpr_id");

                            for (int y = (x + 1); y < json_mysql.length(); y++) {
                                JSONObject obj_future = json_mysql.getJSONObject(y);

                                if (obj.getString("pr_record_id").equals("")) {
                                    if (cpr_id != obj_future.getInt("cpr_id")) {
                                        insertHistory(array);
                                        array = new JSONArray();
                                        array.put(obj_future);
                                    } else {
                                        array.put(obj);
                                        array.put(obj_future);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("patientHistoryFrag5", e + "");
                    Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                }
                progress1.dismiss();
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                progress1.dismiss();
                Log.d("patientHistoryFrag6", e + "");
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    void getClinicRecords(String uname, String pword) {
        progress.show();
        String url = "db/get.php?q=get_clinic_records&username=" + uname + "&password=" + pword + "&patient_id=" + SidebarActivity.getUserID();

        ListRequestFromCustomURI.getJSONobj(url, new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        int hasRecord = response.getInt("has_record");
                        JSONArray json_mysql = response.getJSONArray("records");

                        if (hasRecord == 1) {
                            Snackbar.make(root, "Record already exists", Snackbar.LENGTH_SHORT).show();
                        } else
                            insertHistory(json_mysql);
                    } else
                        Snackbar.make(root, "Your Medical record has not been uploaded. Please try again later", Snackbar.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("patientHistoryFrag0", e + "");
                    Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                progress.dismiss();
                Log.d("patientHistoryFrag1", e + "");
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void insertHistory(final JSONArray array) {
        progress.show();

        try {
            JSONObject object = array.getJSONObject(0);

            final HashMap<String, String> map = new HashMap<>();
            map.put("table", "patient_records");
            map.put("request", "crud");
            map.put("action", "insert");
            map.put("clinic_patient_record_id", object.getString("cpr_id"));
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
                                hash.put("medicine_name", obj.getString("med_name"));
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
                                                updateList();
                                                progress.dismiss();
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.d("patientHistoryFrag4", e + "");
                                        Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }, new ErrorListener<VolleyError>() {
                                public void getError(VolleyError error) {
                                    progress.dismiss();
                                    Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.d("patientHistoryFrag3", e + "");
                        Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                    }
                    progress.dismiss();
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    progress.dismiss();
                    Log.d("patientHistoryFrag2", error + "");
                    Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class SelectionAdapter extends ArrayAdapter {
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> objects;

        public SelectionAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects) {
            super(context, resource, objects);
            inflater = LayoutInflater.from(context);
            this.objects = objects;
        }

        class ViewHolder {
            TextView record_date, doctor, clinic;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder mViewHolder;

            if (v == null) {
                mViewHolder = new ViewHolder();
                v = inflater.inflate(R.layout.listview_history_views, parent, false);

                mViewHolder.record_date = (TextView) v.findViewById(R.id.record_date);
                mViewHolder.doctor = (TextView) v.findViewById(R.id.doctor_name);
                mViewHolder.clinic = (TextView) v.findViewById(R.id.clinic);

                v.setTag(mViewHolder);
            } else
                mViewHolder = (ViewHolder) v.getTag();

            mViewHolder.doctor.setText(objects.get(position).get("doctor_name"));
            mViewHolder.record_date.setText(objects.get(position).get("record_date"));
            mViewHolder.clinic.setText(objects.get(position).get("clinic_name"));

            return v;
        }
    }
}
