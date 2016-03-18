package com.example.zem.patientcareapp.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.zem.patientcareapp.Activities.GoogleMapsActivity;
import com.example.zem.patientcareapp.Activities.ProductCategoriesActivity;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.OrderPreferenceController;
import com.example.zem.patientcareapp.Controllers.PatientConsultationController;
import com.example.zem.patientcareapp.Controllers.PatientRecordController;
import com.example.zem.patientcareapp.Controllers.PatientTreatmentsController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.SwipeTabsModule.MasterTabActivity;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static android.util.Log.d;

public class HomeTileFragment extends Fragment implements View.OnClickListener {
    static int patientID;
    LinearLayout orderLayout, refillLayout, pointsLayout, prescriptionLayout, consultationLayout;
    TextView notifConsultation;
    LinearLayout root;
    DbHelper db;
    PatientConsultationController pcc;
    PatientTreatmentsController ptc;
    PatientRecordController prc;
    OrderPreferenceController opc;
    Context context;

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.new_home_layout, container, false);

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

        return rootView;
    }

    @Override
    public void onResume() {
        ListOfPatientsRequest.getJSONobj("get_consultations_notif&patient_ID=" + patientID, "consultations", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        if (response.getBoolean("has_contents")) {
                            JSONArray json_mysql = response.getJSONArray("consultations");
                            notifConsultation.setVisibility(View.VISIBLE);

                            for (int x = 0; x < json_mysql.length(); x++) {
                                JSONObject obj = json_mysql.getJSONObject(x);

                                d("pcc_json", obj + "");
                                if (!pcc.updateSomeConsultation(obj))
                                    Snackbar.make(root, "Cannot Update Consultation", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    d("home1", e + "");
                    Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                d("home2", e + "");
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
                OrderModel order_model = opc.getOrderPreference();
                if (order_model.hasSelectedBranch())
                    startActivity(new Intent(getActivity(), ProductCategoriesActivity.class));
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
                        Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                    }
                });

                intent.putExtra("selected", 2);
                startActivity(intent);
                break;
        }
    }
}