package com.beta.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beta.zem.patientcareapp.Model.Specialty;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/24/2015.
 */
public class SpecialtyController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    public static final String TBL_SPECIALTIES = "specialties",
            SERVER_SPECIALTY_ID = "specialty_id",
            SPECIALTY_NAME = "name";

    public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER UNIQUE, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            TBL_SPECIALTIES, AI_ID, SERVER_SPECIALTY_ID, SPECIALTY_NAME, CREATED_AT, UPDATED_AT, DELETED_AT);

    public SpecialtyController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean saveSpecialty(Specialty specialty, String request) {
        long rowID = 0;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SERVER_SPECIALTY_ID, specialty.getSpecialty_id());
        values.put(SPECIALTY_NAME, specialty.getName());
        values.put(CREATED_AT, specialty.getCreated_at());
        values.put(UPDATED_AT, specialty.getUpdated_at());
        values.put(DELETED_AT, specialty.getDeleted_at());

        if (request.equals("insert")) {
            rowID = sql_db.insert(TBL_SPECIALTIES, null, values);
        } else if (request.equals("update")) {
            rowID = sql_db.update(TBL_SPECIALTIES, values, AI_ID + "=" + specialty.getSpecialty_id(), null);
        }

        sql_db.close();
        return rowID > 0;
    }

    public ArrayList<HashMap<String, String>> getAllSpecialties() {
        ArrayList<HashMap<String, String>> listOfSpecialties = new ArrayList<>();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();

        String sql = "SELECT * FROM " + TBL_SPECIALTIES;
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("specialty_id", String.valueOf(cur.getInt(cur.getColumnIndex(SERVER_SPECIALTY_ID))));
            map.put("specialty_name", cur.getString(cur.getColumnIndex(SPECIALTY_NAME)));
            listOfSpecialties.add(map);
        }

        cur.close();
        sql_db.close();

        return listOfSpecialties;
    }

}
