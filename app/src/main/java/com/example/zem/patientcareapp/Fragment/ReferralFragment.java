package com.example.zem.patientcareapp.Fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Controllers.PointsController;
import com.example.zem.patientcareapp.Customizations.NonScrollListView;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Interface.StringRespondListener;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.Network.StringRequests;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.example.zem.patientcareapp.adapter.DownlinesAdapter;
import com.example.zem.patientcareapp.adapter.PointsLogAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User PC on 1/4/2016.
 */

public class ReferralFragment extends Fragment {
    TextView referralsLvlLimit;
    LinearLayout parent;
    LayoutInflater inflater;

    Patient ptnt;
    PatientController pc;
    NonScrollListView earned_points_log, used_points_log, downlines;
    PointsLogAdapter points_log_adapter;
    ArrayList<HashMap<String, String>> items;
    PointsController ptc;
    TextView patient_points;
    Patient patient;
    DownlinesAdapter downlinesAdapter;

    View container;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_referrals, container, false);
        this.inflater = inflater;
        this.container = container;

        referralsLvlLimit = (TextView) root.findViewById(R.id.referralsLvlLimit);
        parent = (LinearLayout) root.findViewById(R.id.parent);
        earned_points_log = (NonScrollListView) root.findViewById(R.id.earned_points_log);
        patient_points = (TextView) root.findViewById(R.id.patient_points);
        used_points_log = (NonScrollListView) root.findViewById(R.id.used_points_log);
        downlines = (NonScrollListView) root.findViewById(R.id.downlines);

        pc = new PatientController(getActivity());
        ptc = new PointsController();

        ptnt = pc.getCurrentLoggedInPatient();
        patient = pc.getloginPatient(SidebarActivity.getUname());

        checkForSettingsUpdate();

        ListOfPatientsRequest.getJSONobj(getActivity(), "get_settings", "settings", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    JSONArray json_mysql = response.getJSONArray("settings");
                } catch (JSONException e) {
                    Log.d("referrals_exception", e + "");
                }
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Snackbar.make(container, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        });

        StringRequests.getString(getActivity(), "db/get.php?q=get_patient_points&patient_id=" + SidebarActivity.getUserID(), new StringRespondListener<String>() {
            @Override
            public void getResult(String response) {
                patient.setPoints(Double.parseDouble(response));
                pc.updatePoints(Double.parseDouble(response));
                patient_points.setText("Your Points - " + patient.getPoints());
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Log.d("error for sumthing", error + "");
            }
        });

        ListOfPatientsRequest.getJSONobj(getActivity(), "get_patient_referral_commissions&patient_id=" + SidebarActivity.getUserID(), "referral_commission", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    JSONArray json_mysql = response.getJSONArray("referral_commission");
                    Log.d("response", response + "");
                    Log.d("referral", json_mysql + "");

                    items = ptc.convertFromJson(json_mysql);
                    points_log_adapter = new PointsLogAdapter(getActivity(), items);
                    earned_points_log.setAdapter(points_log_adapter);

                } catch (JSONException e) {
                    Log.d("referrals_exception", e + "");
                }
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Snackbar.make(container, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        });

        ListOfPatientsRequest.getJSONobj(getActivity(), "get_used_points&patient_id=" + SidebarActivity.getUserID(), "used_points", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    JSONArray json_mysql = response.getJSONArray("used_points");
                    Log.d("response", response + "");
                    Log.d("referral", json_mysql + "");

                    items = ptc.convertFromJson(json_mysql);
                    points_log_adapter = new PointsLogAdapter(getActivity(), items);
                    used_points_log.setAdapter(points_log_adapter);

                } catch (JSONException e) {
                    Log.d("used_points_exception", e + "");
                }
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Snackbar.make(container, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        });


        ListOfPatientsRequest.getJSONobj(getActivity(), "get_patients_downlines&referral_id=" + ptnt.getReferral_id(), "patients", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    JSONArray json_mysql = response.getJSONArray("downlines");
                    items = pc.convertFromJson(json_mysql);
                    downlinesAdapter = new DownlinesAdapter(getActivity(), items);
                    downlines.setAdapter(downlinesAdapter);
                } catch (Exception e) {
                    Log.d("exception2", e + "");
                    Snackbar.make(container, "Error occurred", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Snackbar.make(container, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    public void checkForSettingsUpdate() {
        ListOfPatientsRequest.getJSONobj(getActivity(), "get_settings", "settings", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    JSONArray json_mysql = response.getJSONArray("settings");
                    JSONObject object = json_mysql.getJSONObject(0);
                    int limit = object.getInt("level_limit");

                } catch (JSONException e) {
                    Log.d("exception1", e + "");
                    Snackbar.make(container, "Error occurred", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Snackbar.make(container, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

//    public void addTableRow() {
//        final ProgressDialog progress = new ProgressDialog(getActivity());
//        progress.setMessage("Please wait...");
//        progress.show();
//
//        Log.d("referral", ptnt.getReferral_id() + "");
//
//        ListOfPatientsRequest.getJSONobj(getActivity(), "get_patients", "patients", new RespondListener<JSONObject>() {
//            @Override
//            public void getResult(JSONObject response) {
//                String referral_ID = null;
//
//                try {
//                    JSONArray json_mysql = response.getJSONArray("patients");
//
//                    for (int x = 0; x < json_mysql.length(); x++) {
//                        JSONObject obj = json_mysql.getJSONObject(x);
//
//                    }
//                } catch (Exception e) {
//                    Log.d("exception2", e + "");
//                    Snackbar.make(parent, "Error occurred", Snackbar.LENGTH_SHORT).show();
//                }
//
//                progress.dismiss();
//            }
//        }, new ErrorListener<VolleyError>() {
//            public void getError(VolleyError error) {
//                progress.dismiss();
//                Snackbar.make(parent, "Network Error", Snackbar.LENGTH_SHORT).show();
//            }
//        });

//        TableRow tr = new TableRow(getActivity());
//        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//
//        for (int x = 0; x < 5; x++) {
//            TextView txt = new TextView(getActivity());
//            txt.setGravity(Gravity.CENTER);
//            txt.setBackgroundDrawable(getResources().getDrawable(R.drawable.cell_border));
//            txt.setText(x + "");
//            tr.addView(txt);
//        }
//
//        table_parent.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
//    }
}
