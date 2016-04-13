package com.beta.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.beta.zem.patientcareapp.Model.SubSpecialty;

/**
 * Created by Zem on 11/24/2015.
 */
public class SubSpecialtyController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //SPECIALTIES_TABLE
    public static final String TBL_SUB_SPECIALTIES = "sub_specialties",
            SERVER_SUB_SPECIALTY_ID = "sub_specialty_id",
            SUB_SPECIALTY_FOREIGN_ID = "specialty_id",
            SUB_SPECIALTY_NAME = "name";

    public static String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER UNIQUE, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            TBL_SUB_SPECIALTIES, AI_ID, SERVER_SUB_SPECIALTY_ID, SUB_SPECIALTY_FOREIGN_ID, SUB_SPECIALTY_NAME, CREATED_AT, UPDATED_AT, DELETED_AT);

    public SubSpecialtyController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean saveSubSpecialty(SubSpecialty sub_specialty, String request) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        long rowID = 0;
        ContentValues values = new ContentValues();

        values.put(SERVER_SUB_SPECIALTY_ID, sub_specialty.getSub_specialty_id());
        values.put(SUB_SPECIALTY_FOREIGN_ID, sub_specialty.getSpecialty_id());
        values.put(SUB_SPECIALTY_NAME, sub_specialty.getName());
        values.put(CREATED_AT, sub_specialty.getCreated_at());
        values.put(UPDATED_AT, sub_specialty.getUpdated_at());
        values.put(DELETED_AT, sub_specialty.getDeleted_at());

        if (request.equals("insert")) {
            rowID = sql_db.insert(TBL_SUB_SPECIALTIES, null, values);
        } else if (request.equals("update")) {
            rowID = sql_db.update(TBL_SUB_SPECIALTIES, values, AI_ID + "=" + sub_specialty.getSpecialty_id(), null);
        }

        sql_db.close();
        return rowID > 0;
    }
}
