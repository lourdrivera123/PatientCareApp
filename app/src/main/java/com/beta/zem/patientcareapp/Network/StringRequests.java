package com.beta.zem.patientcareapp.Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beta.zem.patientcareapp.ConfigurationModule.Helpers;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.StringRespondListener;

/**
 * Created by User PC on 10/28/2015.
 */
public class StringRequests {

    public static void getString(final Context c, final String q, final StringRespondListener<String> listener, final ErrorListener<VolleyError> errorlistener) {
        RequestQueue queue;
        Helpers helpers;

        queue = VolleySingleton.getInstance().getRequestQueue();
        helpers = new Helpers();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, helpers.get_api_url(q), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.
                listener.getResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorlistener.getError(error);
            }
        });
        queue.add(stringRequest);
    }
}
