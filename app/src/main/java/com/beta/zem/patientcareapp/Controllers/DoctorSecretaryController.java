package com.beta.zem.patientcareapp.Controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Zem on 11/23/2015.
 */
public class DoctorSecretaryController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

        // DOCTOR_SECRETARY TABLE
    public static final String TBL_DOCTOR_SECRETARY = "doctor_secretary",
            DS_SECRETARY_ID = "secretary_id",
            DS_DOCTOR_ID = "doctor_id",
            DS_IS_ACTIVE = "is_active";

// SQL to create table "doctor_secretary"
        public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER, %s INTEGER, %s INT )",
                TBL_DOCTOR_SECRETARY, DS_DOCTOR_ID, DS_SECRETARY_ID, DS_IS_ACTIVE);

    public DoctorSecretaryController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    
}
