package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "PatientCare";
    public static final int DB_VERSION = 1;

    public static final String CREATED_AT = "created_at", DELETED_AT = "deleted_at", UPDATED_AT = "updated_at", AI_ID = "id",
            PATIENT_ID = "patient_id", IS_READ = "isRead";

    //FAVORITES
    public static final String TBL_FAVORITES = "favorites",
            FAVE_PRODUCT_ID = "product_id",
            FAVE_USER_ID = "user_id";

    String sql_favorites = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER)",
            TBL_FAVORITES, AI_ID, FAVE_PRODUCT_ID, FAVE_USER_ID);

    //SEARCH_HISTORY
    static final String TBL_SEARCH_HISTORY = "search_history",
            HISTORY_USER_ID = "user_id",
            HISTORY_KEYWORD = "keyword";

    String sql_search_history = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT)",
            TBL_SEARCH_HISTORY, AI_ID, HISTORY_USER_ID, HISTORY_KEYWORD);

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_favorites);
        db.execSQL(sql_search_history);
        db.execSQL(DoctorController.CREATE_TABLE);
        db.execSQL(SpecialtyController.CREATE_TABLE);
        db.execSQL(SubSpecialtyController.CREATE_TABLE);
        db.execSQL(UpdateController.CREATE_TABLE);
        db.execSQL(PatientController.CREATE_TABLE);
        db.execSQL(ProductCategoryController.CREATE_TABLE);
        db.execSQL(ProductSubCategoryController.CREATE_TABLE);
        db.execSQL(PatientRecordController.CREATE_TABLE);
        db.execSQL(ClinicController.CREATE_TABLE);
        db.execSQL(ClinicDoctorController.CREATE_TABLE);
        db.execSQL(ClinicSecretaryController.CREATE_TABLE);
        db.execSQL(DoctorSecretaryController.CREATE_TABLE);
        db.execSQL(DiscountsFreeProductsController.CREATE_TABLE);
        db.execSQL(FreeProductsController.CREATE_TABLE);
        db.execSQL(PatientPrescriptionController.CREATE_TABLE);
        db.execSQL(PatientConsultationController.CREATE_TABLE);
        db.execSQL(OverlayController.CREATE_TABLE);
        db.execSQL(OrderController.CREATE_TABLE);
        db.execSQL(SettingController.CREATE_TABLE);
        db.execSQL(BranchController.CREATE_TABLE);
        db.execSQL(OrderDetailController.CREATE_TABLE);
        db.execSQL(BillingController.CREATE_TABLE);
        db.execSQL(MessageController.CREATE_TABLE);
        db.execSQL(PatientTreatmentsController.CREATE_TABLE);
        db.execSQL(OrderPreferenceController.CREATE_TABLE);

        insertTableNamesToUpdates(DoctorController.TBL_DOCTORS, db);
        insertTableNamesToUpdates(SpecialtyController.TBL_SPECIALTIES, db);
        insertTableNamesToUpdates(SubSpecialtyController.TBL_SUB_SPECIALTIES, db);
        insertTableNamesToUpdates(ProductCategoryController.TBL_PRODUCT_CATEGORIES, db);
        insertTableNamesToUpdates(ProductSubCategoryController.TBL_PRODUCT_SUBCATEGORIES, db);
        insertTableNamesToUpdates(PatientRecordController.TBL_PATIENT_RECORDS, db);
        insertTableNamesToUpdates(ClinicController.TBL_CLINICS, db);
        insertTableNamesToUpdates(PatientPrescriptionController.TBL_PATIENT_PRESCRIPTIONS, db);
        insertTableNamesToUpdates(SettingController.TBL_SETTINGS, db);
        insertTableNamesToUpdates(BranchController.TBL_BRANCHES, db);
        insertTableNamesToUpdates(OrderController.TBL_ORDERS, db);
        insertTableNamesToUpdates(MessageController.TBl_MSGS, db);
        insertTableNamesToUpdates(PatientConsultationController.TBL_PATIENT_CONSULTATIONS, db);
        insertTableNamesToUpdates(PatientRecordController.TBL_PATIENT_RECORDS, db);
        insertTableNamesToUpdates(PatientTreatmentsController.TBL_PATIENT_TREATMENTS, db);
        insertTableNamesToUpdates(BillingController.TBL_BILLINGS, db);
        insertTableNamesToUpdates(OrderDetailController.TBL_ORDER_DETAILS, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + DB_NAME;
        db.execSQL(sql);
    }

    /////////////////////////////INSERT METHODS///////////////////////////////////////
    public boolean insertTableNamesToUpdates(String table_name, SQLiteDatabase sql_db) {
//        SQLiteDatabase sql_db = getWritableDatabase();
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ContentValues values = new ContentValues();
        values.put(UpdateController.UPDATE_TBL_NAME, table_name);
        values.put(UpdateController.UPDATE_TIMESTAMP, formatter.format(now));
        values.put(UpdateController.UPDATE_SEEN, 0);

        long rowID = sql_db.insert(UpdateController.TBL_UPDATES, null, values);
        return rowID > 0;
    }

    public boolean insertFaveProduct(int user_id, int product_id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(FAVE_PRODUCT_ID, product_id);
        val.put(FAVE_USER_ID, user_id);

        long row = db.insert(TBL_FAVORITES, null, val);

        db.close();
        return row > 0;
    }

    public boolean insertSearchHistory(int user_id, String keyword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues val = new ContentValues();
        String sql = "SELECT * FROM " + TBL_SEARCH_HISTORY + " WHERE " + HISTORY_KEYWORD + " = '" + keyword + "'";
        Cursor cur = db.rawQuery(sql, null);

        if (!cur.moveToNext()) {
            val.put(HISTORY_USER_ID, user_id);
            val.put(HISTORY_KEYWORD, keyword);

            db.insert(TBL_SEARCH_HISTORY, null, val);
        }

        cur.close();
        db.close();

        return true;
    }

    //////////////////////////DELETE METHODS//////////////////////////////////////
    public boolean deleteFromTable(int serverID, String tableName, String column_serverID) {
        SQLiteDatabase db = getWritableDatabase();
        long deletedID = db.delete(tableName, column_serverID + " = " + serverID, null);

        db.close();
        return deletedID > 0;
    }

    public boolean removeFavorite(int user_id, int product_id) {
        SQLiteDatabase db = getWritableDatabase();
        long deleted_id = db.delete(TBL_FAVORITES, "product_id = " + product_id + " AND user_id = " + user_id, null);

        db.close();
        return deleted_id > 0;
    }

    public boolean deleteHistoryByUserID(int user_id) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.delete(TBL_SEARCH_HISTORY, HISTORY_USER_ID + " = " + user_id, null);

        db.close();

        return id > 0;
    }

    /////////////////////////GET METHODS/////////////////////////////
    public ArrayList<Integer> getFavoritesByUserID(int user_id) {
        ArrayList<Integer> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_FAVORITES + " WHERE " + FAVE_USER_ID + " = " + user_id;
        Cursor cur = db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            list.add(cur.getInt(cur.getColumnIndex(FAVE_PRODUCT_ID)));
        }

        cur.close();
        db.close();

        return list;
    }

    public ArrayList<String> getAllHistoryByUser(int user_id) {
        ArrayList<String> history = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_SEARCH_HISTORY + " WHERE " + HISTORY_USER_ID + " = " + user_id;
        Cursor cur = db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            history.add(cur.getString(cur.getColumnIndex(HISTORY_KEYWORD)));
        }

        cur.close();
        db.close();

        return history;
    }

    public JSONArray getAllJSONArrayFrom(String tbl_name) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + tbl_name;
        Cursor cursor = db.rawQuery(sql, null);
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("dbhelper1", e + "");
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return resultSet;
    }
}