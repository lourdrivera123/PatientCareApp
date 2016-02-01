package com.example.zem.patientcareapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.AlarmModule.AlarmService;
import com.example.zem.patientcareapp.ConfigurationModule.Constants;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Network.CustomRequest;
import com.example.zem.patientcareapp.Network.GetRequest;
import com.example.zem.patientcareapp.Network.Sync;
import com.example.zem.patientcareapp.Network.VolleySingleton;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.example.zem.patientcareapp.gcm.gcmquickstart.QuickstartPreferences;
import com.example.zem.patientcareapp.gcm.gcmquickstart.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.support.design.widget.Snackbar.make;
import static android.util.Log.d;
import static com.example.zem.patientcareapp.Network.GetRequest.getJSONobj;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SidebarActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    EditText username_txtfield, password_txtfield;
    TextView signup, forgotpw;
    LinearLayout login, root;
    Toolbar login_toolbar;

    public static final String MyPREFERENCES = "MyPrefs", name = "nameKey", pass = "passwordKey";
    public static SharedPreferences sharedpreferences;
    String uname, password;
    String url = Constants.POST_URL;

    ProgressDialog pDialog;
    RequestQueue queue;

    static Patient patient;
    DbHelper dbHelper;
    PatientController pc;
    static Helpers helpers;
    public static Activity main;
    Sync sync;

    JSONArray patient_json_array_mysql = null;
    JSONObject patient_json_object_mysql = null;
    AlarmService alarmService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_login);

        login_toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(login_toolbar);
        getSupportActionBar().setTitle("PatientCare");

        dbHelper = new DbHelper(this);
        pc = new PatientController(this);
        helpers = new Helpers();
        alarmService = new AlarmService(this);
        main = this;
        queue = VolleySingleton.getInstance().getRequestQueue();
        sync = new Sync();

        signup = (TextView) findViewById(R.id.signup);
        forgotpw = (TextView) findViewById(R.id.forgot_password);
        login = (LinearLayout) findViewById(R.id.login);
        root = (LinearLayout) findViewById(R.id.root);
        username_txtfield = (EditText) findViewById(R.id.username_txtfield);
        password_txtfield = (EditText) findViewById(R.id.password_txtfield);

        password_txtfield.setTransformationMethod(new PasswordTransformationMethod());

        signup.setOnClickListener(this);
        forgotpw.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    void showdl(String msg){
        AlertDialog.Builder order_completed_dialog = new AlertDialog.Builder(MainActivity.this);
        order_completed_dialog.setTitle("Connection status");
        order_completed_dialog.setMessage(msg);
        order_completed_dialog.setCancelable(false);
        order_completed_dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        order_completed_dialog.show();
    }

//    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
//                        .show();
//            } else {
//                Log.i(TAG, "This device is not supported.");
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }

    @Override
    protected void onResume() {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(name)) {
            if (sharedpreferences.contains(pass)) {
                Intent i = new Intent(this, SidebarActivity.class);
                startActivity(i);
            }
        } else {
            username_txtfield.setText("");
            password_txtfield.setText("");
        }

//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                uname = username_txtfield.getText().toString();
                password = password_txtfield.getText().toString();

                if (uname.equals("")) {
                    username_txtfield.setError("Field Required");
                } else if (password.equals("")) {
                    password_txtfield.setError("Field Required");
                } else {
                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setMessage("Loading...");
                    pDialog.show();

                    patient = new Patient();
                    patient.setUsername(uname);
                    patient.setPassword(helpers.md5(password));

                    final Map<String, String> params = setParams();

                    CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int success = response.getInt("success");

                                if (success == 1) {
                                    patient_json_array_mysql = response.getJSONArray("patient");
                                    JSONArray checked_json_array = sync.checkWhatToInsert(patient_json_array_mysql, dbHelper.getAllJSONArrayFrom("patients"), "patient_id");
                                    Patient syncedPatient;

                                    if (checked_json_array.length() > 0) {
                                        patient_json_object_mysql = checked_json_array.getJSONObject(0);

                                        syncedPatient = sync.setPatient(patient_json_object_mysql); //sync.setPatient here.
                                        pc.savePatient(patient_json_object_mysql, syncedPatient, "insert"); //then save on db
                                    } else {
                                        JSONObject obj = patient_json_array_mysql.getJSONObject(0);
                                        syncedPatient = sync.setPatient(obj);
                                    }

                                    if (pc.LoginUser(uname, password)) {
                                        //request for patient_prescriptions
                                        getJSONobj(getBaseContext(), "get_prescriptions&patient_id=" + syncedPatient.getServerID(), "patient_prescriptions", "prescriptions_id", new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {
                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                make(root, "Network error", LENGTH_SHORT).show();
                                            }
                                        });

                                        //request for orders request
                                        getJSONobj(getBaseContext(), "get_orders&patient_id=" + syncedPatient.getServerID(), "orders", "orders_id", new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {
                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                d("orders error", error + "");
                                                make(root, "Network error", LENGTH_SHORT).show();
                                            }
                                        });

                                        //request for order_billings
                                        getJSONobj(getBaseContext(), "get_order_billings&patient_id=" + syncedPatient.getServerID(), "billings", "billings_id", new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {
                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                make(root, "Network error", LENGTH_SHORT).show();
                                            }
                                        });

                                        //request for order_details
                                        getJSONobj(getBaseContext(), "get_order_details&patient_id=" + syncedPatient.getServerID(), "order_details", "order_details_id", new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {
                                                d("fuck response", response + "");
                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                make(root, "Network error", LENGTH_SHORT).show();
                                            }
                                        });

                                        //request for consultations
                                        getJSONobj(getBaseContext(), "get_consultations&patient_id=" + syncedPatient.getServerID(), "consultations", "consultation_id", new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {
                                                d("mainactivity response", response + "");
                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                make(root, "Network error", LENGTH_SHORT).show();
                                            }
                                        });

                                        //request for patient_records
                                        getJSONobj(getBaseContext(), "get_patient_records&patient_id=" + syncedPatient.getServerID(), "patient_records", "record_id", new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {

                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                make(root, "Network error", LENGTH_SHORT).show();
                                            }
                                        });

                                        //request for patient_treatments
                                        getJSONobj(getBaseContext(), "get_treatments&patient_id=" + syncedPatient.getServerID(), "patient_treatments", "treatments_id", new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {

                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                make(root, "Network error", LENGTH_SHORT).show();
                                            }
                                        });


                                        getJSONobj(getBaseContext(), "get_order_preference&patient_id=" + syncedPatient.getServerID(), "order_preference", "server_id", new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {

                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                make(root, "Network error", LENGTH_SHORT).show();
                                            }
                                        });


                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString(name, uname);
                                        editor.putString(pass, helpers.md5(password));
                                        editor.commit();

                                        String patient_image_name = patient_json_array_mysql.getJSONObject(0).getString("photo");
                                        if (!patient_image_name.equals(""))
                                            helpers.cacheImageOnly(patient_image_name, patient_json_array_mysql.getJSONObject(0).getInt("id"));
                                        pDialog.dismiss();
                                        startActivity(new Intent(getBaseContext(), SidebarActivity.class));
                                    } else {
                                        pDialog.dismiss();
                                        make(root, "Error occurred", LENGTH_SHORT).show();
                                    }
                                } else {
                                    make(root, "Invalid username/password", LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }
                            } catch (JSONException e) {
                                d("try catch error", e + "");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            make(root, "Network error", LENGTH_SHORT).show();
                        }
                    });
                    queue.add(jsObjRequest);
                }
                break;

            case R.id.signup:
                startActivity(new Intent(this, ReferralActivity.class));
                break;
        }
    }

    @Override
    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public static Map<String, String> setParams() {
        Map<String, String> params = new HashMap();

        params.put("request", "login");
        params.put("username", patient.getUsername());
        params.put("password", patient.getPassword());
        return params;
    }
}
