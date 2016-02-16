package com.example.zem.patientcareapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.Network.GetRequest;
import com.example.zem.patientcareapp.Network.VolleySingleton;
import com.example.zem.patientcareapp.R;

import org.json.JSONObject;

/**
 * Created by Zem on 6/1/2015.
 */
public class SplashActivity extends Activity {

    Helpers helpers;
    RequestQueue queue;
    DbHelper dbHelper;
    LinearLayout splash_layout;
    public static final String PREFS_NAME = "firstTimeUsePref";
    int identifier = 0;
    //identifier in future runs must be 13 since we made 13 network requests, if you want to add a network request in splash activity please add the max identifier too
    int max_identifier = 9;
    String[] requestsArray, tableNamesArray, serverIdsArray;
//    boolean onPauseisFired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        dbHelper = new DbHelper(this);
        helpers = new Helpers();
        requestsArray = getResources().getStringArray(R.array.requests);
        tableNamesArray = getResources().getStringArray(R.array.table_names);
        serverIdsArray = getResources().getStringArray(R.array.server_ids);
        splash_layout  = (LinearLayout) findViewById(R.id.splash_layout);

        queue = VolleySingleton.getInstance().getRequestQueue();

        if(identifier < max_identifier)
            for_loop_splash();
         else
            checker();
    }

    public void for_loop_splash(){
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        identifier = settings.getInt("identifier", 0);
        Log.d("max_identifier_forls", max_identifier+"");
        Log.d("identifier_forls", identifier+"");
        if(identifier < max_identifier) {
            for(int x = identifier; x < max_identifier; x++ ){
                GetRequest.getJSONobj(SplashActivity.this, requestsArray[x], tableNamesArray[x], serverIdsArray[x], new RespondListener<JSONObject>() {
                    @Override
                    public void getResult(JSONObject response) {
                        int pref_identifier = identifier + 1;
                        settings.edit().putInt("identifier", pref_identifier).commit();
                        checker();
                    }
                }, new ErrorListener<VolleyError>() {
                    public void getError(VolleyError error) {

                        queue.cancelAll(new RequestQueue.RequestFilter() {
                            @Override
                            public boolean apply(Request<?> request) {
                                return true;
                            }
                        });
                        Log.d("Error", error + "");
                        AlertDialog.Builder no_connection_dialog = new AlertDialog.Builder(SplashActivity.this);
                        no_connection_dialog.setTitle("Warning");
                        no_connection_dialog.setMessage("Looks like you don't have internet, please check your connection.");
                        no_connection_dialog.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for_loop_splash();
                            }
                        });
                        no_connection_dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        no_connection_dialog.show();
                    }
                });
                Log.d("x_occurences", x+"");
            }
        } else {
           checker();
        }
    }

    public void checker(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        identifier = settings.getInt("identifier", 0);
        if(identifier == max_identifier){
            Intent mainactivity = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(mainactivity);
                    finish();
        }
    }
}