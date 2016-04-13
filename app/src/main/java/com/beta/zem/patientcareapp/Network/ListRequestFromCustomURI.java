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

public class ListRequestFromCustomURI {
    public static void getJSONobj(final String q, final RespondListener<JSONObject> listener, final ErrorListener<VolleyError> errorlistener) {
        RequestQueue queue;
        Helpers helpers;

        queue = VolleySingleton.getInstance().getRequestQueue();
        helpers = new Helpers();

        JsonObjectRequest doctor_request = new JsonObjectRequest(Request.Method.GET, helpers.get_api_url(q), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.getResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorlistener.getError(error);
                Log.e("listOfPatients", error + "");
            }
        });
        queue.add(doctor_request);
    }
}
