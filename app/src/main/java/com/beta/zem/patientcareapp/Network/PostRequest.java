package com.beta.zem.patientcareapp.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.beta.zem.patientcareapp.ConfigurationModule.Constants;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Zem on 8/4/2015.
 */
public class PostRequest {

    public static void send(final Context c, final HashMap<String, String> parameters, final RespondListener<JSONObject> listener, final ErrorListener<VolleyError> errorlistener) {
        RequestQueue queue;

        queue = VolleySingleton.getInstance().getRequestQueue();
        String url = Constants.POST_URL;

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, parameters,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listener.getResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorlistener.getError(error);
                Log.d("<PostRequest>", error + "");
            }
        });
        queue.add(jsObjRequest);
    }
}
