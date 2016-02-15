package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.zem.patientcareapp.Model.Clinic;

import java.util.ArrayList;
import java.util.HashMap;

public class ClinicController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    // CLINICS TABLE
    public static final String TBL_CLINICS = "clinics",
            CLINIC_NAME = "name",
            CLINIC_CONTACT_NO = "contact_no",
            CLINIC_BARANGAY = "address_barangay",
            CLINIC_CITY = "address_city_municipality",
            CLINIC_PROVINCE = "address_province",
            CLINIC_REGION = "address_region",
            SERVER_CLINICS_ID = "clinics_id";

    // SQL to create table "clinics"
    public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT" +
                    ", %s TEXT, %s  TEXT , %s  TEXT , %s  TEXT" +
                    ", %s TEXT, %s TEXT, %s TEXT  )",
            TBL_CLINICS, AI_ID, SERVER_CLINICS_ID, CLINIC_NAME, CLINIC_CONTACT_NO, CLINIC_BARANGAY, CLINIC_CITY, CLINIC_PROVINCE, CLINIC_REGION, CREATED_AT, UPDATED_AT, DELETED_AT);

    public ClinicController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean saveClinic(Clinic clinic, String type) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SERVER_CLINICS_ID, clinic.getClinicsId());
        values.put(CLINIC_NAME, clinic.getName());
        values.put(CLINIC_CONTACT_NO, clinic.getContactNumber());
        values.put(CLINIC_BARANGAY, clinic.getAddress_barangay());
        values.put(CLINIC_CITY, clinic.getAddress_city_municipality());
        values.put(CLINIC_PROVINCE, clinic.getAddress_province());
        values.put(CLINIC_REGION, clinic.getAddress_region());
        values.put(CREATED_AT, clinic.getCreatedAt());
        values.put(UPDATED_AT, clinic.getUpdatedAt());
        values.put(DELETED_AT, clinic.getDeletedAt());

        long row = 0;

        if (type.equals("insert")) {
            row = sql_db.insert(TBL_CLINICS, null, values);
        } else if (type.equals("update")) {
            row = sql_db.update(TBL_CLINICS, values, SERVER_CLINICS_ID + " = " + clinic.getClinicsId(), null);
        }

        sql_db.close();
        return row > 0;
    }

    public ArrayList<HashMap<String, String>> getAllClinics() {
        ArrayList<HashMap<String, String>> listOfClinics = new ArrayList<>();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();

        String sql = "SELECT * FROM " + TBL_CLINICS;
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("clinic_id", String.valueOf(cur.getInt(cur.getColumnIndex(SERVER_CLINICS_ID))));
            map.put("clinic_name", cur.getString(cur.getColumnIndex(CLINIC_NAME)));
            listOfClinics.add(map);
        }

        cur.close();
        sql_db.close();

        return listOfClinics;
    }

    public ArrayList<HashMap<String, String>> getClinicByDoctorID(int doctorID) {
        ArrayList<HashMap<String, String>> listOfClinics = new ArrayList<>();

        String sql = "SELECT c.*, cd.clinic_sched  FROM " + TBL_CLINICS + " as c INNER JOIN " + ClinicDoctorController.TBL_CLINIC_DOCTOR + " as cd ON c." + SERVER_CLINICS_ID +
                " = cd." + ClinicDoctorController.CD_CLINIC_ID + " WHERE cd." + ClinicDoctorController.CD_DOCTOR_ID + " = " + doctorID;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();

            map.put(SERVER_CLINICS_ID, cur.getString(cur.getColumnIndex(SERVER_CLINICS_ID)));
            map.put(CLINIC_NAME, cur.getString(cur.getColumnIndex(CLINIC_NAME)));
            map.put(CLINIC_CONTACT_NO, cur.getString(cur.getColumnIndex(CLINIC_CONTACT_NO)));
            map.put(CLINIC_BARANGAY, cur.getString(cur.getColumnIndex(CLINIC_BARANGAY)));
            map.put(CLINIC_CITY, cur.getString(cur.getColumnIndex(CLINIC_CITY)));
            map.put(CLINIC_PROVINCE, cur.getString(cur.getColumnIndex(CLINIC_PROVINCE)));
            map.put(CLINIC_REGION, cur.getString(cur.getColumnIndex(CLINIC_REGION)));
            map.put(ClinicDoctorController.CD_CLINIC_SCHED, cur.getString(cur.getColumnIndex(ClinicDoctorController.CD_CLINIC_SCHED)));
            listOfClinics.add(map);
        }
        cur.close();
        sql_db.close();

        return listOfClinics;
    }

}
