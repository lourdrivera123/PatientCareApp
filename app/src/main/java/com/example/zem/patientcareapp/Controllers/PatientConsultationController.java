package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.zem.patientcareapp.Model.Consultation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.util.Log.d;

/**
 * Created by Zem on 11/23/2015.
 */
public class PatientConsultationController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //CONSULTATION
    public static final String TBL_PATIENT_CONSULTATIONS = "consultations",
            CONSULT_SERVER_ID = "consultation_id",
            CONSULT_DOCTOR_ID = "doctor_id",
            CONSULT_CLINIC_ID = "clinic_id",
            CONSULT_DATE = "date",
            CONSULT_TIME = "time",
            CONSULT_IS_ALARMED = "is_alarm",
            CONSULT_ALARMED_TIME = "alarm_time",
            CONSULT_IS_APPROVED = "is_approved",
            CONSULT_COMMENT_DOCTOR = "comment_doctor",
            CONSULT_PTNT_IS_APPROVED = "patient_is_approved",
            CONSULT_COMMENT_PTNT = "comment_patient";

    //SQL to create PATIENT CONSULTATIONS
    public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s INTEGER, %s INTEGER, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)",
            TBL_PATIENT_CONSULTATIONS, AI_ID, CONSULT_SERVER_ID, PATIENT_ID, CONSULT_DOCTOR_ID, CONSULT_CLINIC_ID, CONSULT_DATE, CONSULT_TIME, CONSULT_IS_ALARMED, CONSULT_ALARMED_TIME, CONSULT_IS_APPROVED, IS_READ, CONSULT_COMMENT_DOCTOR, CONSULT_PTNT_IS_APPROVED, CONSULT_COMMENT_PTNT, CREATED_AT, UPDATED_AT);

    public PatientConsultationController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean savePatientConsultation(Consultation consult, String request) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long rowID = 0;

        values.put(CONSULT_SERVER_ID, consult.getServerID());
        values.put(PATIENT_ID, consult.getPatientID());
        values.put(CONSULT_DOCTOR_ID, consult.getDoctorID());
        values.put(CONSULT_CLINIC_ID, consult.getClinicID());
        values.put(CONSULT_DATE, consult.getDate());
        values.put(CONSULT_TIME, consult.getTime());
        values.put(CONSULT_IS_ALARMED, consult.getIsAlarmed());
        values.put(CONSULT_ALARMED_TIME, consult.getAlarmedTime());
        values.put(CONSULT_IS_APPROVED, consult.getIs_approved());
        values.put(IS_READ, consult.getIs_read());
        values.put(CONSULT_COMMENT_DOCTOR, consult.getComment_doctor());
        values.put(CONSULT_PTNT_IS_APPROVED, consult.getPtnt_is_approved());
        values.put(CONSULT_COMMENT_PTNT, consult.getComment_patient());
        values.put(CREATED_AT, consult.getCreated_at());

        if (request.equals("add")) {
            rowID = sql_db.insert(TBL_PATIENT_CONSULTATIONS, null, values);
        } else if (request.equals("update")) {
            rowID = sql_db.update(TBL_PATIENT_CONSULTATIONS, values, CONSULT_SERVER_ID + "=" + consult.getServerID(), null);
        }
        sql_db.close();

        return rowID > 0;
    }

    public ArrayList<HashMap<String, String>> getAllConsultationsByUserId(int userID) {
        ArrayList<HashMap<String, String>> listOfAllConsultations = new ArrayList();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT c.*, d.fname, d.lname, d.mname, cn.name FROM consultations as c inner join doctors as d " +
                "on c.doctor_id = d.doc_id inner join clinics as cn on c.clinic_id = cn.id where c.patient_id = " + userID + " ORDER BY c.date ASC";
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap();
            map.put(AI_ID, String.valueOf(cur.getInt(cur.getColumnIndex(AI_ID))));
            map.put(CONSULT_SERVER_ID, String.valueOf(cur.getInt(cur.getColumnIndex(CONSULT_SERVER_ID))));
            map.put(CONSULT_DOCTOR_ID, String.valueOf(cur.getInt(cur.getColumnIndex(CONSULT_DOCTOR_ID))));
            map.put("doctor_name", cur.getString(cur.getColumnIndex("fname")) + " " + cur.getString(cur.getColumnIndex("mname")).substring(0, 1) + ". " + cur.getString(cur.getColumnIndex("lname")));
            map.put(CONSULT_CLINIC_ID, String.valueOf(cur.getInt(cur.getColumnIndex(CONSULT_CLINIC_ID))));
            map.put("clinic_name", cur.getString(cur.getColumnIndex("name")));
            map.put(CONSULT_DATE, cur.getString(cur.getColumnIndex(CONSULT_DATE)));
            map.put(CONSULT_TIME, cur.getString(cur.getColumnIndex(CONSULT_TIME)));
            map.put(CONSULT_IS_ALARMED, String.valueOf(cur.getInt(cur.getColumnIndex(CONSULT_IS_ALARMED))));
            map.put(CONSULT_ALARMED_TIME, cur.getString(cur.getColumnIndex(CONSULT_ALARMED_TIME)));
            map.put(CONSULT_IS_APPROVED, String.valueOf(cur.getInt(cur.getColumnIndex(CONSULT_IS_APPROVED))));
            map.put(IS_READ, String.valueOf(cur.getInt(cur.getColumnIndex(IS_READ))));
            map.put(CONSULT_COMMENT_DOCTOR, cur.getString(cur.getColumnIndex(CONSULT_COMMENT_DOCTOR)));
            map.put(CONSULT_PTNT_IS_APPROVED, String.valueOf(cur.getInt(cur.getColumnIndex(CONSULT_PTNT_IS_APPROVED))));
            map.put(CONSULT_COMMENT_PTNT, cur.getString(cur.getColumnIndex(CONSULT_COMMENT_PTNT)));

            listOfAllConsultations.add(map);
        }

        cur.close();
        sql_db.close();

        return listOfAllConsultations;
    }

    public boolean updateSomeConsultation(JSONObject json) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long updated_id = 0;

        try {
            values.put(CONSULT_TIME, json.getString("time"));
            values.put(IS_READ, 1);
            values.put(CONSULT_IS_APPROVED, json.getInt("is_approved"));
            values.put(CONSULT_COMMENT_DOCTOR, json.getString("comment_doctor"));

            updated_id = sql_db.update(TBL_PATIENT_CONSULTATIONS, values, CONSULT_SERVER_ID + " = " + json.getInt("id"), null);

        } catch (JSONException e) {
            d("j_err", e +"");
            e.printStackTrace();
        }
        sql_db.close();
        return updated_id > 0;
    }

    public boolean AcceptRejectConsultation(HashMap<String, String> map, String operation) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(CONSULT_SERVER_ID, Integer.parseInt(map.get("id")));
        val.put(CONSULT_IS_APPROVED, map.get("is_approved"));
        val.put(CONSULT_PTNT_IS_APPROVED, map.get("patient_is_approved"));
        val.put(CONSULT_COMMENT_PTNT, map.get("comment_patient"));

        if (operation.equals("accept"))
            val.put(IS_READ, 0);
        else
            val.put(IS_READ, 1);

        long id = sql_db.update(TBL_PATIENT_CONSULTATIONS, val, CONSULT_SERVER_ID + " = " + Integer.parseInt(map.get("id")), null);

        sql_db.close();
        return id > 0;
    }

    public boolean removeFromSQLite(int AI_id) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        long deletedID = sql_db.delete(TBL_PATIENT_CONSULTATIONS, AI_ID + " = " + AI_id, null);

        d("delete", deletedID + "");
        sql_db.close();
        return deletedID > 0;
    }

}
