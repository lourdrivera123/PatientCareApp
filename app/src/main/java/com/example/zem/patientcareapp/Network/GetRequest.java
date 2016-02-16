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

import org.json.JSONException;
import org.json.JSONObject;

public class GetRequest {

    public static void getJSONobj(final Context c, final String q, final String table_name, final String table_id, final RespondListener<JSONObject> listener, final ErrorListener<VolleyError> errorlistener) {
        RequestQueue queue;
        final Helpers helpers;
        final UpdateController upc;

        queue = VolleySingleton.getInstance().getRequestQueue();
        helpers = new Helpers();
        upc = new UpdateController(c);
        final String url = helpers.get_url(q, table_name);

        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //condition here must not be success cause it still return 1, it shuold be something like if the table name
                    //is not on response json array
                    if(response.getInt("success") > 0){
                        Sync sync = new Sync();
                        sync.init(c, table_name, table_id, response);
                            upc.updateLastUpdatedTable(table_name, response.getString("latest_updated_at"));

                    } else {
                        System.out.print("<"+url+">Response is good but there is no need to update sqlite columns since there are no records - dafuq im still missing delete sqlite records !");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
