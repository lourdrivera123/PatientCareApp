package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientTreatmentsController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //PATIENT_TREATMENTS TABLE
    public static final String TBL_PATIENT_TREATMENTS = "patient_treatments",
            SERVER_TREATMENTS_ID = "treatments_id",
            TREATMENTS_PATIENT_RECORDS_ID = "patient_records_id",
            TREATMENTS_MEDICINE_ID = "medicine_id",
            TREATMENTS_MEDICINE_NAME = "medicine_name",
            TREATMENTS_FREQUENCY = "frequency",
            TREATMENTS_DURATION = "duration",
            TREATMENTS_DURATION_TYPE = "duration_type";

    //SQL to create "patient_treatments"
    public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            TBL_PATIENT_TREATMENTS, AI_ID, SERVER_TREATMENTS_ID, TREATMENTS_PATIENT_RECORDS_ID, TREATMENTS_MEDICINE_ID, TREATMENTS_MEDICINE_NAME, TREATMENTS_FREQUENCY, TREATMENTS_DURATION, TREATMENTS_DURATION_TYPE, CREATED_AT, UPDATED_AT, DELETED_AT);


    public PatientTreatmentsController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean savePatientTreatments(ArrayList<HashMap<String, String>> listOfTreatments, String request) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        long rowID = 0;

        for (int x = 0; x < listOfTreatments.size(); x++) {
            val.put(SERVER_TREATMENTS_ID, listOfTreatments.get(x).get("treatments_id"));
            val.put(TREATMENTS_PATIENT_RECORDS_ID, listOfTreatments.get(x).get("patient_records_id"));
            val.put(TREATMENTS_MEDICINE_ID, listOfTreatments.get(x).get("medicine_id"));
            val.put(TREATMENTS_MEDICINE_NAME, listOfTreatments.get(x).get("medicine_name"));
            val.put(TREATMENTS_FREQUENCY, listOfTreatments.get(x).get("frequency"));
            val.put(TREATMENTS_DURATION, listOfTreatments.get(x).get("duration"));
            val.put(TREATMENTS_DURATION_TYPE, listOfTreatments.get(x).get("duration_type"));

            if (request.equals("insert"))
                rowID = sql_db.insert(TBL_PATIENT_TREATMENTS, null, val);
        }

        sql_db.close();
        return rowID > 0;
    }

    public boolean deleteTreatments() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        long id = db.delete(TBL_PATIENT_TREATMENTS, null, null);

        db.close();

        return id > 0;
    }

    public boolean deleteTreatmentsByRecordID(int record_id) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.delete(TBL_PATIENT_TREATMENTS, TREATMENTS_PATIENT_RECORDS_ID + " = " + record_id, null);

        db.close();

        return id > 0;
    }
}
