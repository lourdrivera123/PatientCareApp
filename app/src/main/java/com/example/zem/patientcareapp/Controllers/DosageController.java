package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.zem.patientcareapp.Model.Dosage;

/**
 * Created by Zem on 11/23/2015.
 */
public class DosageController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //DOSAGE_FORMAT_AND_STRENGTH TABLE
    public static final String TBL_DOSAGE = "dosage_format_and_strength",
            SERVER_DOSAGE_ID = "dosage_id",
            DOSAGE_PROD_ID = "product_id",
            DOSAGE_NAME = "name";

     // SQL TO CREATE TABLE "TBL_DOSAGE"
      public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER UNIQUE, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)",
                TBL_DOSAGE, AI_ID, SERVER_DOSAGE_ID, DOSAGE_PROD_ID, DOSAGE_NAME, CREATED_AT, UPDATED_AT);

    public DosageController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean insertDosage(Dosage dosage) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SERVER_DOSAGE_ID, dosage.getDosage_id());
        values.put(DOSAGE_PROD_ID, dosage.getProduct_id());
        values.put(DOSAGE_NAME, dosage.getName());

        long rowID = sql_db.insert(TBL_DOSAGE, null, values);

        sql_db.close();
        return rowID > 0;
    }

    
}

