package com.beta.zem.patientcareapp.Network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Zem on 8/13/2015.
 */
public class ConvertCurrencyRequest {

    public static void send(final HashMap<String, String> parameters, final RespondListener<JSONObject> listener, final ErrorListener<VolleyError> errorlistener){
        RequestQueue queue;


        queue = VolleySingleton.getInstance().getRequestQueue();
        String url = "http://192.168.177.1/db/libs/adaptivepayments-sdk-php/samples/convertcurrencyreceipt_mod.php";

        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response is <Convertcurrency.java>: " + response);
                        listener.getResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorlistener.getError(error);
                Log.d("error on interface <Convertcurrency.java>", error + "");
            }
        });
        queue.add(jsObjRequest);
    }
}
