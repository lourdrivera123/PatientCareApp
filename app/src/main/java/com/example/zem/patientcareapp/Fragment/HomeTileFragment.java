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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Activities.GoogleMapsActivity;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.OrderPreferenceController;
import com.example.zem.patientcareapp.Controllers.PatientConsultationController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.SwipeTabsModule.MasterTabActivity;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.Activities.ProductsActivity;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class HomeTileFragment extends Fragment implements View.OnClickListener {
    LinearLayout orderLayout, refillLayout, pointsLayout, prescriptionLayout, consultationLayout;
    TextView notifConsultation;
    ScrollView root;

    DbHelper db;
    PatientConsultationController pcc;
    OrderPreferenceController opc;
    static int patientID;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_home_layout, container, false);

        context = getActivity();
        db = new DbHelper(context);
        pcc = new PatientConsultationController(context);
        opc = new OrderPreferenceController(context);

        root = (ScrollView) rootView.findViewById(R.id.root);
        orderLayout = (LinearLayout) rootView.findViewById(R.id.orderLayout);
        refillLayout = (LinearLayout) rootView.findViewById(R.id.refillLayout);
        pointsLayout = (LinearLayout) rootView.findViewById(R.id.pointsLayout);
        prescriptionLayout = (LinearLayout) rootView.findViewById(R.id.prescriptionLayout);
        consultationLayout = (LinearLayout) rootView.findViewById(R.id.consultationLayout);
        notifConsultation = (TextView) rootView.findViewById(R.id.notifConsultation);

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
                    Toast.makeText(getActivity(), e + "", Toast.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                Snackbar.make(root, "Please check your Network connection", Snackbar.LENGTH_SHORT).show();
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
}