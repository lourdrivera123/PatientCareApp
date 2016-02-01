package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.zem.patientcareapp.Model.ClinicDoctor;

/**
 * Created by Zem on 11/23/2015.
 */
public class ClinicDoctorController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    // CLINIC_DOCTOR TABLE
    public static final String TBL_CLINIC_DOCTOR = "clinic_doctor",
            CD_SERVER_ID = "clinic_doctor_id",
            CD_CLINIC_ID = "clinic_id",
            CD_DOCTOR_ID = "doctor_id",
            CD_CLINIC_SCHED = "clinic_sched",
            CD_IS_ACTIVE = "is_active";

// SQL to create table "clinic_doctor"
     public static final String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INT )",
                TBL_CLINIC_DOCTOR, AI_ID, CD_SERVER_ID, CD_CLINIC_ID, CD_DOCTOR_ID, CD_CLINIC_SCHED, CD_IS_ACTIVE);

    public ClinicDoctorController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean saveClinicDoctor(ClinicDoctor cd, String request) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CD_SERVER_ID, cd.getServerID());
        values.put(CD_DOCTOR_ID, cd.getDoctorID());
        values.put(CD_CLINIC_ID, cd.getClinicID());
        values.put(CD_CLINIC_SCHED, cd.getSchedule());
        values.put(CD_IS_ACTIVE, cd.getIsActive());

        long rowID = 0;

        if (request.equals("insert"))
            rowID = sql_db.insert(TBL_CLINIC_DOCTOR, null, values);
        else if (request.equals("update"))
            rowID = sql_db.update(TBL_CLINIC_DOCTOR, values, CD_SERVER_ID + " = " + cd.getServerID(), null);

        sql_db.close();

        return rowID > 0;
    }
}
