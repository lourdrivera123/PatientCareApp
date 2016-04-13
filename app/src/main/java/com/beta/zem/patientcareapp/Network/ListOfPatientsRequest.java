package com.beta.zem.patientcareapp.Network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beta.zem.patientcareapp.ConfigurationModule.Helpers;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;

import org.json.JSONObject;

public class ListOfPatientsRequest {
    public static void getJSONobj(final String q, String table_name, final RespondListener<JSONObject> listener, final ErrorListener<VolleyError> errorlistener) {
        RequestQueue queue;
        final Helpers helpers;

        queue = VolleySingleton.getInstance().getRequestQueue();
        helpers = new Helpers();
        final String url = helpers.get_url(q, table_name);

        JsonObjectRequest doctor_request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.getResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorlistener.getError(error);
                Log.e("listOfPatients", url+" "+error);
            }
        });
        queue.add(doctor_request);
    }

}
