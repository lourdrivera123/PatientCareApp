package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zemskie on 11/25/2015.
 */
public class UpdateController extends DbHelper {

    DbHelper dbHelper;
    SQLiteDatabase sql_db;

    //Updates Table
    public static final String TBL_UPDATES = "updates",
            UPDATE_TBL_NAME = "tbl_name",
            UPDATE_TIMESTAMP = "timestamp",
            UPDATE_SEEN = "seen";

    // SQL to create table "tbl_updates"
   public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER)",
            TBL_UPDATES, AI_ID, UPDATE_TBL_NAME, UPDATE_TIMESTAMP, UPDATE_SEEN);

    public UpdateController(Context context) {
        super(context);
        dbHelper = new DbHelper(context);
        sql_db = dbHelper.getWritableDatabase();
    }

    public boolean updateLastUpdatedTable(String table_name, String server_timestamp) {
        SQLiteDatabase sql_db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UPDATE_TIMESTAMP, server_timestamp);

        int rowID = sql_db.update(TBL_UPDATES, values, UPDATE_TBL_NAME + "= '" + table_name + "'", null);
        sql_db.close();
        return rowID > 0;
    }

    public String getLastUpdate(String table_name) {
        SQLiteDatabase sql_db = dbHelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_UPDATES + " WHERE " + UPDATE_TBL_NAME + "= '" + table_name + "'";
        String last_update_date = "";
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            last_update_date = cur.getString(2);
        }
        cur.close();
        sql_db.close();
        return last_update_date;
    }

    public boolean updateIsRead_table(int serverID, String table_name, String column_serverID) {
        SQLiteDatabase sql_db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(IS_READ, 1);

        long updated_id = sql_db.update(table_name, values, column_serverID + " = " + serverID, null);

        sql_db.close();
        return updated_id > 0;
    }
}
