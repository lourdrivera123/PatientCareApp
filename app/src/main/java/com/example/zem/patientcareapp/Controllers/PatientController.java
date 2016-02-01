package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.zem.patientcareapp.Fragment.AccountFragment;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/23/2015.
 */
public class PatientController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;
    Helpers helper = new Helpers();

    //PATIENTS_TABLE
    public static final String TBL_PATIENTS = "patients",
            PTNT_FNAME = "fname",
            PTNT_MNAME = "mname",
            PTNT_LNAME = "lname",
            PTNT_USERNAME = "username",
            PTNT_PASSWORD = "password",
            PTNT_OCCUPATION = "occupation",
            PTNT_BIRTHDATE = "birthdate",
            PTNT_SEX = "sex",
            PTNT_CIVIL_STATUS = "civil_status",
            PTNT_HEIGHT = "height",
            PTNT_WEIGHT = "weight",
            PTNT_OPTIONAL_ADDRESS = "optional_address",
            PTNT_STREET = "address_street",
            PTNT_BRGY_ID = "address_barangay_id",
            PTNT_BARANGAY = "address_barangay",
            PTNT_CITY = "address_city_municipality",
            PTNT_PROVINCE = "address_province",
            PTNT_REGION = "address_region",
            PTNT_TEL_NO = "tel_no",
            PTNT_MOBILE_NO = "mobile_no",
            PTNT_EMAIL = "email_address",
            PTNT_PHOTO = "photo",
            PTNT_POINTS = "points",
            PTNT_REFERRAL_ID = "referral_id",
            PTNT_REFERRED_BY_USER = "referred_byUser",
            PTNT_REFERRED_BY_DOCTOR = "referred_byDoctor";

    // SQL to create table "patients"
    public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            TBL_PATIENTS, AI_ID, PATIENT_ID, PTNT_FNAME, PTNT_MNAME, PTNT_LNAME, PTNT_USERNAME, PTNT_PASSWORD, PTNT_OCCUPATION, PTNT_BIRTHDATE, PTNT_SEX, PTNT_CIVIL_STATUS, PTNT_HEIGHT, PTNT_WEIGHT, PTNT_OPTIONAL_ADDRESS, PTNT_STREET, PTNT_BRGY_ID, PTNT_BARANGAY, PTNT_CITY, PTNT_PROVINCE, PTNT_REGION, PTNT_TEL_NO, PTNT_MOBILE_NO, PTNT_EMAIL, PTNT_PHOTO, PTNT_POINTS, PTNT_REFERRAL_ID, PTNT_REFERRED_BY_USER, PTNT_REFERRED_BY_DOCTOR, CREATED_AT, UPDATED_AT, DELETED_AT);

    public PatientController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean savePatient(JSONObject patient_json_object_mysql, Patient patient, String request) {
        int patient_id = 0;
        String created_at = "";

        if (request.equals("update")) {
            patient_id = patient.getServerID();
        } else {
            try {
                patient_id = patient_json_object_mysql.getInt("id");
                created_at = patient_json_object_mysql.getString("created_at");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PATIENT_ID, patient_id);
        values.put(PTNT_FNAME, patient.getFname());
        values.put(PTNT_MNAME, patient.getMname());
        values.put(PTNT_LNAME, patient.getLname());
        values.put(PTNT_USERNAME, patient.getUsername());
        values.put(PTNT_OCCUPATION, patient.getOccupation());
        values.put(PTNT_BIRTHDATE, patient.getBirthdate());
        values.put(PTNT_SEX, patient.getSex());
        values.put(PTNT_CIVIL_STATUS, patient.getCivil_status());
        values.put(PTNT_HEIGHT, patient.getHeight());
        values.put(PTNT_WEIGHT, patient.getWeight());
        values.put(PTNT_OPTIONAL_ADDRESS, patient.getOptional_address());
        values.put(PTNT_STREET, patient.getAddress_street());
        values.put(PTNT_BRGY_ID, patient.getBarangay_id());
        values.put(PTNT_BARANGAY, patient.getBarangay());
        values.put(PTNT_CITY, patient.getMunicipality());
        values.put(PTNT_PROVINCE, patient.getProvince());
        values.put(PTNT_REGION, patient.getRegion());
        values.put(PTNT_TEL_NO, patient.getTel_no());
        values.put(PTNT_MOBILE_NO, patient.getMobile_no());
        values.put(PTNT_EMAIL, patient.getEmail());
        values.put(PTNT_PHOTO, patient.getPhoto());
        values.put(PTNT_POINTS, patient.getPoints());
        values.put(PTNT_REFERRAL_ID, patient.getReferral_id());
        values.put(PTNT_REFERRED_BY_USER, patient.getReferred_byUser());
        values.put(PTNT_REFERRED_BY_DOCTOR, patient.getReferred_byDoctor());
        values.put(CREATED_AT, created_at);

        long rowID = 0;

        if (request.equals("insert")) {
            values.put(PTNT_PASSWORD, patient.getPassword());
            rowID = sql_db.insert(TBL_PATIENTS, null, values);
        } else if (request.equals("update")) {
            if (AccountFragment.checkIfChangedPass > 0)
                values.put(PTNT_PASSWORD, patient.getPassword());

            rowID = sql_db.update(TBL_PATIENTS, values, PATIENT_ID + "=" + patient_id, null);
        }

        sql_db.close();

        return rowID > 0;
    }

    public boolean LoginUser(String uname, String password) {
        int login = 0;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql1 = "SELECT * FROM " + TBL_PATIENTS + " WHERE " + PTNT_USERNAME + " = '" + uname + "' and " + PTNT_PASSWORD + " = '" + helper.md5(password) + "'";
        Cursor cur = sql_db.rawQuery(sql1, null);
        cur.moveToFirst();

        if (cur.getCount() > 0) {
            login = 1;
        }

        cur.close();
        sql_db.close();
        return login > 0;
    }

    public int checkUserIfRegistered(String username) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_PATIENTS + " WHERE " + PTNT_USERNAME + " = '" + username + "'";
        Cursor cur = sql_db.rawQuery(sql, null);
        cur.moveToFirst();
        int check = 0;

        if (cur.getCount() > 0) {
            check = 1;
        }

        cur.close();
        sql_db.close();
        return check;
    }

    public Patient getCurrentLoggedInPatient() {
        Patient patient = this.getloginPatient(SidebarActivity.getUname());
        return patient;
    }

    public Patient getloginPatient(String username) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        Patient patient = new Patient();

        String sql = "SELECT * FROM " + TBL_PATIENTS + " WHERE " + PTNT_USERNAME + " = '" + username + "'";
        Cursor cur = sql_db.rawQuery(sql, null);
        cur.moveToFirst();

        if (cur.getCount() > 0) {
            patient.setId(cur.getInt(cur.getColumnIndex(AI_ID)));
            patient.setServerID(cur.getInt(cur.getColumnIndex(PATIENT_ID)));
            patient.setFname(cur.getString(cur.getColumnIndex(PTNT_FNAME)));
            patient.setMname(cur.getString(cur.getColumnIndex(PTNT_MNAME)));
            patient.setLname(cur.getString(cur.getColumnIndex(PTNT_LNAME)));
            patient.setUsername(cur.getString(cur.getColumnIndex(PTNT_USERNAME)));
            patient.setPassword(cur.getString(cur.getColumnIndex(PTNT_PASSWORD)));
            patient.setOccupation(cur.getString(cur.getColumnIndex(PTNT_OCCUPATION)));
            patient.setBirthdate(cur.getString(cur.getColumnIndex(PTNT_BIRTHDATE)));
            patient.setSex(cur.getString(cur.getColumnIndex(PTNT_SEX)));
            patient.setCivil_status(cur.getString(cur.getColumnIndex(PTNT_CIVIL_STATUS)));
            patient.setHeight(cur.getString(cur.getColumnIndex(PTNT_HEIGHT)));
            patient.setWeight(cur.getString(cur.getColumnIndex(PTNT_WEIGHT)));
            patient.setOptional_address(cur.getString(cur.getColumnIndex(PTNT_OPTIONAL_ADDRESS)));
            patient.setAddress_street(cur.getString(cur.getColumnIndex(PTNT_STREET)));
            patient.setBarangay(cur.getString(cur.getColumnIndex(PTNT_BARANGAY)));
            patient.setMunicipality(cur.getString(cur.getColumnIndex(PTNT_CITY)));
            patient.setProvince(cur.getString(cur.getColumnIndex(PTNT_PROVINCE)));
            patient.setRegion(cur.getString(cur.getColumnIndex(PTNT_REGION)));
            patient.setTel_no(cur.getString(cur.getColumnIndex(PTNT_TEL_NO)));
            patient.setMobile_no(cur.getString(cur.getColumnIndex(PTNT_MOBILE_NO)));
            patient.setEmail(cur.getString(cur.getColumnIndex(PTNT_EMAIL)));
            patient.setPhoto(cur.getString(cur.getColumnIndex(PTNT_PHOTO)));
            patient.setPoints(cur.getDouble(cur.getColumnIndex(PTNT_POINTS)));
            patient.setReferral_id(cur.getString(cur.getColumnIndex(PTNT_REFERRAL_ID)));
            patient.setReferred_byUser(cur.getString(cur.getColumnIndex(PTNT_REFERRED_BY_USER)));
            patient.setReferred_byDoctor(cur.getString(cur.getColumnIndex(PTNT_REFERRED_BY_DOCTOR)));
        }
        cur.close();
        sql_db.close();

        return patient;
    }

    public boolean updatePatientImage(String patient_image, int id) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PTNT_PHOTO, patient_image);

        long row = sql_db.update(TBL_PATIENTS, values, PATIENT_ID + "=" + id, null);

        sql_db.close();
        return row > 0;
    }

    public boolean updatePoints(double points) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PTNT_POINTS, points);

        long row = sql_db.update(TBL_PATIENTS, values, PATIENT_ID + "=" + SidebarActivity.getUserID(), null);

        sql_db.close();
        return row > 0;
    }

    public ArrayList<HashMap<String, String>> convertFromJson(JSONArray json_array) {
        ArrayList<HashMap<String, String>> points_list = new ArrayList();

        try {
            for (int x = 0; x < json_array.length(); x++) {
                JSONObject obj = json_array.getJSONObject(x);

                HashMap<String, String> map = new HashMap();
                map.put("fname", obj.getString("fname"));
                map.put("lname", obj.getString("lname"));
                map.put("created_at", obj.getString("created_at"));

                points_list.add(map);
            }
        } catch (Exception e) {
            Log.d("error_converting", e + "");
        }

        return points_list;
    }

}
