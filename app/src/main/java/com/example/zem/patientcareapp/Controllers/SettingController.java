package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.zem.patientcareapp.Model.Settings;

import org.json.JSONObject;

/**
 * Created by Zem on 11/23/2015.
 */
public class SettingController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //REFERRAL_SETTINGS TABLES
    public static final String TBL_SETTINGS = "settings",
            SETTINGS_SERVER_ID = "serverID",
            SETTINGS_POINTS = "points",
            SETTINGS_POINTS_TO_PESO = "points_to_peso",
            SETTINGS_LVL_LIMIT = "level_limit",
            SETTINGS_REFERRAL_COMM = "referral_commission",
            SETTINGS_COMM_VARIATION = "commission_variation",
            SETTINGS_DELIVERY_CHARGE = "delivery_charge",
            SETTINGS_DELIVERY_MINIMUM = "delivery_minimum";

            public static final String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s DOUBLE, %s DOUBLE, %s INTEGER, %s DOUBLE, %s DOUBLE, %s DOUBLE, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)",
                TBL_SETTINGS, AI_ID, SETTINGS_SERVER_ID, SETTINGS_POINTS, SETTINGS_POINTS_TO_PESO, SETTINGS_LVL_LIMIT, SETTINGS_REFERRAL_COMM, SETTINGS_COMM_VARIATION, SETTINGS_DELIVERY_CHARGE, SETTINGS_DELIVERY_MINIMUM, CREATED_AT, UPDATED_AT, DELETED_AT);

    public SettingController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    //REFERRAL SETTINGS
    public boolean saveSettings(JSONObject json, String request) {
//        SQLiteDatabase db = getWritableDatabase();
        ContentValues val = new ContentValues();
        long rowID = 0;

        try {
            val.put(SETTINGS_SERVER_ID, json.getInt("id"));
            val.put(SETTINGS_POINTS, json.getDouble(SETTINGS_POINTS));
            val.put(SETTINGS_POINTS_TO_PESO, json.getDouble(SETTINGS_POINTS_TO_PESO));
            val.put(SETTINGS_LVL_LIMIT, json.getInt(SETTINGS_LVL_LIMIT));
            val.put(SETTINGS_REFERRAL_COMM, json.getDouble(SETTINGS_REFERRAL_COMM));
            val.put(SETTINGS_COMM_VARIATION, json.getDouble(SETTINGS_COMM_VARIATION));
            val.put(SETTINGS_DELIVERY_CHARGE, json.getDouble(SETTINGS_DELIVERY_CHARGE));
            val.put(SETTINGS_DELIVERY_MINIMUM, json.getInt(SETTINGS_DELIVERY_MINIMUM));
            val.put(CREATED_AT, json.getString(CREATED_AT));
            val.put(UPDATED_AT, json.getString(UPDATED_AT));
            val.put(DELETED_AT, json.getString(DELETED_AT));

            if (request.equals("insert")) {
                rowID = sql_db.insert(TBL_SETTINGS, null, val);
            } else if (request.equals("update")) {
                rowID = sql_db.update(TBL_SETTINGS, val, SETTINGS_SERVER_ID + " = " + json.getInt("id"), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sql_db.close();
        return rowID > 0;
    }


    public Settings getAllSettings() {
        Settings settings = new Settings();

        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_SETTINGS;
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            settings.setServerID(cur.getInt(cur.getColumnIndex(SETTINGS_SERVER_ID)));
            settings.setPoints(cur.getDouble(cur.getColumnIndex(SETTINGS_POINTS)));
            settings.setPoints_to_peso(cur.getDouble(cur.getColumnIndex(SETTINGS_POINTS_TO_PESO)));
            settings.setLvl_limit(cur.getInt(cur.getColumnIndex(SETTINGS_LVL_LIMIT)));
            settings.setReferral_comm(cur.getDouble(cur.getColumnIndex(SETTINGS_REFERRAL_COMM)));
            settings.setComm_variation(cur.getDouble(cur.getColumnIndex(SETTINGS_COMM_VARIATION)));
            settings.setDelivery_charge(cur.getDouble(cur.getColumnIndex(SETTINGS_DELIVERY_CHARGE)));
            settings.setDelivery_minimum(cur.getInt(cur.getColumnIndex(SETTINGS_DELIVERY_MINIMUM)));
            settings.setCreated_at(cur.getString(cur.getColumnIndex(CREATED_AT)));
            settings.setUpdated_at(cur.getString(cur.getColumnIndex(UPDATED_AT)));
            settings.setDeleted_at(cur.getString(cur.getColumnIndex(DELETED_AT)));
        }

        cur.close();
        sql_db.close();
        return settings;
    }
}
