package com.beta.zem.patientcareapp.Controllers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lourdrivera on 1/18/2016.
 */
public class PointsController {

    public ArrayList<HashMap<String, String>> convertFromJson(JSONArray json_array) {
        ArrayList<HashMap<String, String>> points_list = new ArrayList();

        try {
            for (int x = 0; x < json_array.length(); x++) {
                JSONObject obj = json_array.getJSONObject(x);

                HashMap<String, String> map = new HashMap();
                map.put("notes", obj.getString("notes"));
                map.put("created_at", obj.getString("created_at"));

                points_list.add(map);
            }
        } catch (Exception e) {
            Log.d("error_converting", e+"");
        }

        return points_list;
    }
}
