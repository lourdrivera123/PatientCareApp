package com.beta.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beta.zem.patientcareapp.ConfigurationModule.Helpers;
import com.beta.zem.patientcareapp.Model.Doctor;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //DOCTORS_TABLE
    public static final String TBL_DOCTORS = "doctors",
            DOC_DOC_ID = "doc_id",
            DOC_LNAME = "lname",
            DOC_MNAME = "mname",
            DOC_FNAME = "fname",
            DOC_PRC_NO = "prc_no",
            DOC_SUB_SPECIALTY_ID = "sub_specialty_id",
            DOC_AFFILIATIONS = "affiliations",
            DOC_EMAIL = "email",
            DOC_REFERRAL_ID = "referral_id";

    public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER UNIQUE, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            TBL_DOCTORS, AI_ID, DOC_DOC_ID, DOC_LNAME, DOC_MNAME, DOC_FNAME, DOC_PRC_NO, DOC_SUB_SPECIALTY_ID, DOC_AFFILIATIONS, DOC_EMAIL, DOC_REFERRAL_ID, CREATED_AT, UPDATED_AT, DELETED_AT);

    public DoctorController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean saveDoctor(Doctor doctor, String request) {
        long rowID = 0;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DOC_DOC_ID, doctor.getServer_doc_id());
        values.put(DOC_LNAME, doctor.getLname());
        values.put(DOC_MNAME, doctor.getMname());
        values.put(DOC_FNAME, doctor.getFname());
        values.put(DOC_PRC_NO, doctor.getPrc_no());
        values.put(DOC_SUB_SPECIALTY_ID, doctor.getSub_specialty_id());
        values.put(DOC_AFFILIATIONS, doctor.getAffiliation());
        values.put(DOC_EMAIL, doctor.getEmail());
        values.put(DOC_REFERRAL_ID, doctor.getReferral_id());
        values.put(CREATED_AT, doctor.getCreated_at());
        values.put(UPDATED_AT, doctor.getUpdated_at());
        values.put(DELETED_AT, doctor.getDeleted_at());

        if (request.equals("insert")) {
            rowID = sql_db.insert(TBL_DOCTORS, null, values);

        } else if (request.equals("update")) {
            rowID = sql_db.update(TBL_DOCTORS, values, DOC_DOC_ID + "=" + doctor.getServer_doc_id(), null);
        }

        sql_db.close();
        return rowID > 0;
    }

    public ArrayList<HashMap<String, String>> getDoctorName() {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_DOCTORS;
        Cursor cur = sql_db.rawQuery(sql, null);
        HashMap<String, String> map;
        ArrayList<HashMap<String, String>> doctors = new ArrayList<>();

        String fullname, fname, lname;
        while (cur.moveToNext()) {
            lname = Helpers.curGetStr(cur, DOC_LNAME);
            fname = Helpers.curGetStr(cur, DOC_FNAME);
            fullname = fname + " " + lname;

            map = new HashMap<>();
            map.put("doctor_id", String.valueOf(cur.getInt(cur.getColumnIndex(DOC_DOC_ID))));
            map.put("fullname", fullname);
            doctors.add(map);
        }
        cur.close();
        sql_db.close();

        return doctors;
    }

    public ArrayList<HashMap<String, String>> getAllDoctors() {
        ArrayList<HashMap<String, String>> doctors = new ArrayList<>();

        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT d.*, s.name FROM " + TBL_DOCTORS + " as d inner join " + SubSpecialtyController.TBL_SUB_SPECIALTIES + " as ss on d.sub_specialty_id = ss.sub_specialty_id inner join " + SpecialtyController.TBL_SPECIALTIES + " as s on ss.specialty_id = s.specialty_id";

        Cursor cur = sql_db.rawQuery(sql, null);

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            HashMap<String, String> map = new HashMap<>();
            map.put(DOC_DOC_ID, cur.getString(cur.getColumnIndex(DOC_DOC_ID)));
            map.put(DOC_FNAME, cur.getString(cur.getColumnIndex(DOC_FNAME)));
            map.put(DOC_LNAME, cur.getString(cur.getColumnIndex(DOC_LNAME)));
            map.put("fullname", cur.getString(cur.getColumnIndex(DOC_LNAME)) + ", " + cur.getString(cur.getColumnIndex(DOC_FNAME)) + " " + cur.getString(cur.getColumnIndex(DOC_MNAME)).substring(0, 1));
            map.put(DOC_MNAME, cur.getString(cur.getColumnIndex(DOC_MNAME)));
            map.put(DOC_SUB_SPECIALTY_ID, cur.getString(cur.getColumnIndex(DOC_SUB_SPECIALTY_ID)));
            map.put("name", cur.getString(cur.getColumnIndex("name")));
            map.put(DOC_REFERRAL_ID, cur.getString(cur.getColumnIndex(DOC_REFERRAL_ID)));
            doctors.add(map);

            cur.moveToNext();
        }

        cur.close();
        sql_db.close();

        return doctors;
    }

    public ArrayList<HashMap<String, String>> getAllDoctorsWithFilter(String what_to_search, String value_to_search) {
        ArrayList<HashMap<String, String>> doctors = new ArrayList<>();
        String sql = "";

        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();

        if(what_to_search.equals("specialty")){
            sql = "SELECT d.*, s.name FROM doctors as d inner join sub_specialties as ss on d.sub_specialty_id = ss.sub_specialty_id inner join specialties as s on ss.specialty_id = s.specialty_id where s.name = '"+value_to_search+"'";
        } else if(what_to_search.equals("places")){
            sql = "SELECT d.*, s.name FROM " + TBL_DOCTORS + " as d inner join " +
                    SubSpecialtyController.TBL_SUB_SPECIALTIES + " as ss on d.sub_specialty_id = ss.sub_specialty_id inner join "
                    + SpecialtyController.TBL_SPECIALTIES +
                    " as s on ss.specialty_id = s.specialty_id inner join clinic_doctor as cd on cd.doctor_id = d.doc_id " +
                    "inner join clinics as c on c.clinics_id = cd.clinic_id where c.address_city_municipality = '"+value_to_search+"'";
        } else if(what_to_search.equals("clinic")) {
            sql = "SELECT d.*, s.name FROM " + TBL_DOCTORS + " as d inner join " +
                    SubSpecialtyController.TBL_SUB_SPECIALTIES + " as ss on d.sub_specialty_id = ss.sub_specialty_id inner join "
                    + SpecialtyController.TBL_SPECIALTIES +
                    " as s on ss.specialty_id = s.specialty_id inner join clinic_doctor as cd on cd.doctor_id = d.doc_id " +
                    "inner join clinics as c on c.clinics_id = cd.clinic_id where c.clinics_id = "+value_to_search;
        }

        Cursor cur = sql_db.rawQuery(sql, null);

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            HashMap<String, String> map = new HashMap<>();
            map.put(DOC_DOC_ID, cur.getString(cur.getColumnIndex(DOC_DOC_ID)));
            map.put(DOC_FNAME, cur.getString(cur.getColumnIndex(DOC_FNAME)));
            map.put(DOC_LNAME, cur.getString(cur.getColumnIndex(DOC_LNAME)));
            map.put("fullname", cur.getString(cur.getColumnIndex(DOC_LNAME)) + ", " + cur.getString(cur.getColumnIndex(DOC_FNAME)) + " " + cur.getString(cur.getColumnIndex(DOC_MNAME)).substring(0, 1));
            map.put(DOC_MNAME, cur.getString(cur.getColumnIndex(DOC_MNAME)));
            map.put(DOC_SUB_SPECIALTY_ID, cur.getString(cur.getColumnIndex(DOC_SUB_SPECIALTY_ID)));
            map.put("name", cur.getString(cur.getColumnIndex("name")));
            map.put(DOC_REFERRAL_ID, cur.getString(cur.getColumnIndex(DOC_REFERRAL_ID)));
            doctors.add(map);

            cur.moveToNext();
        }

        cur.close();
        sql_db.close();

        return doctors;
    }

    public ArrayList<HashMap<Integer, ArrayList<String>>> getSearchDoctors() {
        ArrayList<HashMap<Integer, ArrayList<String>>> doctors = new ArrayList<>();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "select d.*, s.name as specialty, ss.name as sub_specialty from " + SpecialtyController.TBL_SPECIALTIES + " as s" +
                " inner join " + SubSpecialtyController.TBL_SUB_SPECIALTIES + " as ss on s.specialty_id = ss.specialty_id inner join " + TBL_DOCTORS + " as d on ss.sub_specialty_id = d.sub_specialty_id";
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<Integer, ArrayList<String>> map = new HashMap<>();
            ArrayList<String> list = new ArrayList<>();

            list.add(cur.getString(cur.getColumnIndex(DOC_LNAME)) + ", " + cur.getString(cur.getColumnIndex(DOC_FNAME)) + " " + cur.getString(cur.getColumnIndex(DOC_MNAME)).substring(0, 1));
            list.add(cur.getString(cur.getColumnIndex(DOC_PRC_NO)));
            list.add(cur.getString(cur.getColumnIndex("specialty")));
            list.add(cur.getString(cur.getColumnIndex("sub_specialty")));

            map.put(cur.getInt(cur.getColumnIndex("doc_id")), list);
            doctors.add(map);
        }

        sql_db.close();
        cur.close();

        return doctors;
    }

    public Doctor getDoctorByID(int doctorID) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sqlgetDoctorByID = "SELECT d.*, s.name , ss.name as sub_name FROM " + TBL_DOCTORS + " as d inner join " +
                SubSpecialtyController.TBL_SUB_SPECIALTIES + " as ss on d.sub_specialty_id = ss.sub_specialty_id inner join " + SpecialtyController.TBL_SPECIALTIES +
                " as s on ss.specialty_id = s.specialty_id where d.doc_id = " + doctorID;
        Cursor cur = sql_db.rawQuery(sqlgetDoctorByID, null);
        cur.moveToFirst();
        Doctor doctor = new Doctor();

        if (cur.getCount() > 0) {
            doctor.setLname(cur.getString(cur.getColumnIndex(DOC_LNAME)));
            doctor.setMname(cur.getString(cur.getColumnIndex(DOC_MNAME)));
            doctor.setFname(cur.getString(cur.getColumnIndex(DOC_FNAME)));
            doctor.setPrc_no(cur.getInt(cur.getColumnIndex(DOC_PRC_NO)));
            doctor.setSpecialty(cur.getString(cur.getColumnIndex(SpecialtyController.SPECIALTY_NAME)));
            doctor.setSub_specialty(cur.getString(cur.getColumnIndex("sub_name")));
            doctor.setSub_specialty_id(cur.getInt(cur.getColumnIndex(DOC_SUB_SPECIALTY_ID)));
            doctor.setAffiliation(cur.getString(cur.getColumnIndex(DOC_AFFILIATIONS)));
            doctor.setEmail(cur.getString(cur.getColumnIndex(DOC_EMAIL)));
            doctor.setReferral_id(cur.getString(cur.getColumnIndex(DOC_REFERRAL_ID)));
            doctor.setCreated_at(cur.getString(cur.getColumnIndex(CREATED_AT)));
            doctor.setUpdated_at(cur.getString(cur.getColumnIndex(UPDATED_AT)));
            doctor.setDeleted_at(cur.getString(cur.getColumnIndex(DELETED_AT)));
        }
        cur.close();
        sql_db.close();

        return doctor;
    }

    public ArrayList<HashMap<String, String>> getDoctorsInnerJoinClinics() {
        ArrayList<HashMap<String, String>> listOfDoctorClinic = new ArrayList<>();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();

        String sql = "select d.lname, d.mname, d.fname, c.name, c.clinics_id, cd.clinic_sched from " + TBL_DOCTORS + " as d INNER JOIN " +
                ClinicDoctorController.TBL_CLINIC_DOCTOR + " as cd on " + "d.doc_id = cd.doctor_id INNER JOIN " + ClinicController.TBL_CLINICS + " as c on cd.clinic_id = " +
                "c.clinics_id WHERE cd.is_active = 1";
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();

            map.put("clinics_id", String.valueOf(cur.getInt(cur.getColumnIndex(ClinicController.SERVER_CLINICS_ID))));
            map.put("fullname", cur.getString(cur.getColumnIndex(DOC_LNAME)) + ", " + cur.getString(cur.getColumnIndex(DOC_FNAME)) + " " + cur.getString(cur.getColumnIndex(DOC_MNAME)).substring(0, 1));
            map.put("clinic_name", cur.getString(cur.getColumnIndex(ClinicController.CLINIC_NAME)));
            map.put("clinic_sched", cur.getString(cur.getColumnIndex(ClinicDoctorController.CD_CLINIC_SCHED)));
            listOfDoctorClinic.add(map);
        }
        cur.close();
        sql_db.close();

        return listOfDoctorClinic;
    }

    public ArrayList<HashMap<String, String>> getSpecialties() {
        ArrayList<HashMap<String, String>> listOfSpecialties = new ArrayList<>();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();

        String sql = "SELECT * from specialties ORDER BY name ASC";
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();

            map.put("specialty_id", String.valueOf(cur.getInt(cur.getColumnIndex("specialty_id"))));
            map.put("name", cur.getString(cur.getColumnIndex("name")));
            listOfSpecialties.add(map);
        }
        cur.close();
        sql_db.close();

        return listOfSpecialties;
    }
}
