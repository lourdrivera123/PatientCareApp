package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/23/2015.
 */
public class PatientPrescriptionController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

     // UPLOADS ON PRESCRIPTIONS
    public static final String TBL_PATIENT_PRESCRIPTIONS = "patient_prescriptions",
            PRESCRIPTIONS_SERVER_ID = "prescriptions_id",
            PRESCRIPTIONS_FILENAME = "filename",
            PRESCRIPTIONS_APPROVED = "is_approved";

          public static final  String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s TEXT, %s INTEGER, %s TEXT, %s TEXT)",
                TBL_PATIENT_PRESCRIPTIONS, AI_ID, PRESCRIPTIONS_SERVER_ID, PATIENT_ID, PRESCRIPTIONS_FILENAME, PRESCRIPTIONS_APPROVED, CREATED_AT, DELETED_AT);

    public PatientPrescriptionController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean insertUploadOnPrescription(Integer patientID, String filename, int serverID) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PRESCRIPTIONS_SERVER_ID, serverID);
        values.put(PATIENT_ID, patientID);
        values.put(PRESCRIPTIONS_FILENAME, filename);
        values.put(PRESCRIPTIONS_APPROVED, 0);

        long rowID = sql_db.insert(TBL_PATIENT_PRESCRIPTIONS, null, values);
        sql_db.close();
        return rowID > 0;
    }

    public boolean savePrescription(JSONObject object) {
        long rowID = 0;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(PRESCRIPTIONS_SERVER_ID, object.getInt("id"));
            values.put(PRESCRIPTIONS_APPROVED, object.getInt("is_approved"));
            values.put(PATIENT_ID, object.getInt("patient_id"));
            values.put(CREATED_AT, object.getString("created_at"));
            values.put(PRESCRIPTIONS_FILENAME, object.getString("filename"));

            rowID = sql_db.insert(TBL_PATIENT_PRESCRIPTIONS, null, values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sql_db.close();
        return rowID > 0;
    }

    //for prescription
    public ArrayList<HashMap<String, String>> getPrescriptionByUserID(int patientID) {
        ArrayList<HashMap<String, String>> listOfFilename = new ArrayList();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_PATIENT_PRESCRIPTIONS + " WHERE " + PATIENT_ID + " = " + patientID;
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap();
            map.put(PRESCRIPTIONS_SERVER_ID, String.valueOf(cur.getInt(cur.getColumnIndex(PRESCRIPTIONS_SERVER_ID))));
            map.put(PRESCRIPTIONS_FILENAME, cur.getString(cur.getColumnIndex(PRESCRIPTIONS_FILENAME)));
            listOfFilename.add(map);
        }

        cur.close();
        sql_db.close();

        return listOfFilename;
    }

    public boolean deletePrescriptionByServerID(int serverID) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        long deletedPrescriptionID = sql_db.delete(TBL_PATIENT_PRESCRIPTIONS, PRESCRIPTIONS_SERVER_ID + " = " + serverID, null);
        sql_db.close();

        return deletedPrescriptionID > 0;
    }
    
}
