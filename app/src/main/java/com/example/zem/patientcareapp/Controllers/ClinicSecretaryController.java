package com.example.zem.patientcareapp.Controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Zem on 11/23/2015.
 */
public class ClinicSecretaryController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    // CLINIC_SECRETARY TABLE
    public static final String CS_SECRETARY_ID = "secretary_id",
            CS_IS_ACTIVE = "is_active",
            TBL_CLINIC_SECRETARY = "clinic_secretary",
            CS_CLINIC_ID = "clinic_id";

    // SQL to create table "clinic_secretary"
	public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER, %s INTEGER, %s INT )",
                TBL_CLINIC_SECRETARY, CS_CLINIC_ID, CS_SECRETARY_ID, CS_IS_ACTIVE);

    public ClinicSecretaryController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    
}
