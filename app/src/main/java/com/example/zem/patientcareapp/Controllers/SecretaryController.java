package com.example.zem.patientcareapp.Controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Zem on 11/23/2015.
 */
public class SecretaryController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    // SECRETARIES TABLE
    public static final String SEC_FNAME = "fname",
            SEC_MNAME = "mname",
            SEC_LNAME = "lname",
            SEC_ADDRESS_HOUSE_NO = "address_house_no",
            SEC_ADDRESS_STREET = "address_street",
            SEC_ADDRESS_BARANGAY = "address_barangay",
            SEC_ADDRESS_CITY_MUNICIPALITY = "address_city_municipality",
            SEC_ADDRESS_PROVINCE = "address_province",
            SEC_ADDRESS_REGION = "address_region",
            SEC_ADDRESS_ZIP = "address_zip",
            SEC_CELL_NO = "cell_no",
            SEC_TEL_NO = "tel_no",
            SEC_EMAIL = "email",
            SEC_PHOTO = "photo",
            TBL_SECRETARIES = "secretaries",
            SERVER_SECRETARIES_ID = "secretaries_id";

     // SQL to create table "secretaries"
        public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s  TEXT , %s  TEXT , %s  TEXT  )",
                TBL_SECRETARIES, AI_ID, SERVER_SECRETARIES_ID, SEC_FNAME, SEC_MNAME, SEC_LNAME, SEC_ADDRESS_HOUSE_NO, SEC_ADDRESS_STREET, SEC_ADDRESS_BARANGAY, SEC_ADDRESS_CITY_MUNICIPALITY, SEC_ADDRESS_PROVINCE, SEC_ADDRESS_REGION, SEC_ADDRESS_ZIP, SEC_CELL_NO, SEC_TEL_NO, SEC_EMAIL, SEC_PHOTO, CREATED_AT, UPDATED_AT, DELETED_AT);

    public SecretaryController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    
}