package com.example.zem.patientcareapp.Network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Controllers.UpdateController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;

import org.json.JSONObject;

public class GetRequest {

    public static void getJSONobj(final Context c, final String q, final String table_name, final String table_id, final RespondListener<JSONObject> listener, final ErrorListener<VolleyError> errorlistener) {
        RequestQueue queue;
        final Helpers helpers;
        final UpdateController upc;

        queue = VolleySingleton.getInstance().getRequestQueue();
        helpers = new Helpers();
        upc = new UpdateController(c);

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.GET, helpers.get_url(q, table_name), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Sync sync = new Sync();
                sync.init(c, q, table_name, table_id, response);
                try {
                    upc.updateLastUpdatedTable(table_name, response.getString("latest_updated_at"));
                } catch (Exception e) {
                    System.out.print("<GetRequest> something wrong with json: " + e);
                }
                listener.getResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorlistener.getError(error);
                Log.d("error <GetRequest> ", error + "");
            }
        });
        queue.add(jsonrequest);
    }
}
