package com.beta.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/23/2015.
 */
public class BranchController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //BRANCHES TABLE
    public static final String TBL_BRANCHES = "branches",
            SERVER_BRANCHES_ID = "branches_id",
            BRANCHES_NAME = "name",
            BRANCHES_ADDITIONAL_ADDRESS = "additional_address",
            BRANCHES_BRGY_ID = "barangay_id",
            BRANCHES_BARANGAY = "address_barangay",
            BRANCHES_CITY = "address_city_municipality",
            BRANCHES_PROVINCE = "address_province",
            BRANCHES_REGION = "address_region",
            BRANCHES_STATUS = "status";

         public static final String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                TBL_BRANCHES, AI_ID, SERVER_BRANCHES_ID, BRANCHES_NAME, BRANCHES_ADDITIONAL_ADDRESS, BRANCHES_BRGY_ID, BRANCHES_BARANGAY, BRANCHES_CITY, BRANCHES_PROVINCE, BRANCHES_REGION, BRANCHES_STATUS, CREATED_AT,
                UPDATED_AT, DELETED_AT);  

    public BranchController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean saveBranches(JSONObject object) {
        long rowID = 0;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(SERVER_BRANCHES_ID, object.getInt("id"));
            values.put(BRANCHES_NAME, object.getString(BRANCHES_NAME));
            values.put(BRANCHES_ADDITIONAL_ADDRESS, object.getString(BRANCHES_ADDITIONAL_ADDRESS));
            values.put(BRANCHES_BRGY_ID, object.getInt(BRANCHES_BRGY_ID));
            values.put(BRANCHES_BARANGAY, object.getString(BRANCHES_BARANGAY));
            values.put(BRANCHES_CITY, object.getString(BRANCHES_CITY));
            values.put(BRANCHES_PROVINCE, object.getString(BRANCHES_PROVINCE));
            values.put(BRANCHES_REGION, object.getString(BRANCHES_REGION));
            values.put(BRANCHES_STATUS, object.getString(BRANCHES_STATUS));
            values.put(CREATED_AT, object.getString("created_at"));

            rowID = sql_db.insert(TBL_BRANCHES, null, values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sql_db.close();
        return rowID > 0;
    }

    public ArrayList<HashMap<String, String>> getECEBranches() {
        ArrayList<HashMap<String, String>> listOfBranches = new ArrayList();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_BRANCHES;
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap();
            map.put(SERVER_BRANCHES_ID, String.valueOf(cur.getInt(cur.getColumnIndex(SERVER_BRANCHES_ID))));
            map.put(BRANCHES_NAME, cur.getString(cur.getColumnIndex(BRANCHES_NAME)));
            map.put(BRANCHES_ADDITIONAL_ADDRESS, cur.getString(cur.getColumnIndex(BRANCHES_ADDITIONAL_ADDRESS)));
            map.put(BRANCHES_BRGY_ID, cur.getString(cur.getColumnIndex(BRANCHES_BRGY_ID)));
            map.put(BRANCHES_BARANGAY, cur.getString(cur.getColumnIndex(BRANCHES_BARANGAY)));
            map.put(BRANCHES_CITY, cur.getString(cur.getColumnIndex(BRANCHES_CITY)));
            map.put(BRANCHES_PROVINCE, cur.getString(cur.getColumnIndex(BRANCHES_PROVINCE)));
            map.put(BRANCHES_REGION, cur.getString(cur.getColumnIndex(BRANCHES_REGION)));
            map.put("full_address", cur.getString(cur.getColumnIndex(BRANCHES_ADDITIONAL_ADDRESS)) + ", " + cur.getString(cur.getColumnIndex(BRANCHES_BARANGAY)) + ", " + cur.getString(cur.getColumnIndex(BRANCHES_CITY)));
            listOfBranches.add(map);
        }

        cur.close();
        sql_db.close();
        return listOfBranches;
    }

    public ArrayList<HashMap<String, String>> getECEBranchesfromjson(JSONObject jobject, String tbl_name) {

        ArrayList<HashMap<String, String>> listOfBranches = new ArrayList();
        String table_name = tbl_name;

        try {
            JSONArray json_array_mysql = jobject.getJSONArray(tbl_name);
            for (int i = 0; i < json_array_mysql.length(); i++) {
                JSONObject row = json_array_mysql.getJSONObject(i);
                HashMap<String, String> map = new HashMap();
                map.put(SERVER_BRANCHES_ID, String.valueOf(row.getInt(AI_ID)));
                map.put(BRANCHES_NAME, row.getString(BRANCHES_NAME));
                map.put(BRANCHES_ADDITIONAL_ADDRESS, row.getString(BRANCHES_ADDITIONAL_ADDRESS));
                map.put(BRANCHES_BRGY_ID, row.getString(BRANCHES_BRGY_ID));
                map.put(BRANCHES_BARANGAY, row.getString(BRANCHES_BARANGAY));
                map.put(BRANCHES_CITY, row.getString(BRANCHES_CITY));
                map.put(BRANCHES_PROVINCE, row.getString(BRANCHES_PROVINCE));
                map.put(BRANCHES_REGION, row.getString(BRANCHES_REGION));
                map.put("latitude", String.valueOf(row.getDouble("latitude")));
                map.put("longitude", String.valueOf(row.getDouble("longitude")));
                map.put("full_address", row.getString(BRANCHES_ADDITIONAL_ADDRESS) + ", " + row.getString(BRANCHES_BARANGAY) + ", " + row.getString(BRANCHES_CITY));
                map.put("same_region", String.valueOf(row.getInt("same_region")));
                listOfBranches.add(map);
            }
        } catch (Exception e) {

        }

        return listOfBranches;
    }
    
}
