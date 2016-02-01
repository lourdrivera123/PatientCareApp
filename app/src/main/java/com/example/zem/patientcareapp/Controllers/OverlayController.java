package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Zem on 11/23/2015.
 */
public class OverlayController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //OVERLAY
    public static final String TBL_OVERLAYS = "overlays",
            OVERLAY_TITLE = "title";

	 public static final String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER)",
                TBL_OVERLAYS, AI_ID, OVERLAY_TITLE, IS_READ);            

    public OverlayController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean checkOverlay(String title, String request) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        long check = 0;

        if (request.equals("check")) {
            String sql = "SELECT * FROM " + TBL_OVERLAYS + " WHERE " + OVERLAY_TITLE + " = '" + title + "' AND " + IS_READ + " = 1";
            Cursor cur = sql_db.rawQuery(sql, null);

            while (cur.moveToNext()) {
                check += 1;
            }

            cur.close();
        } else {
            ContentValues val = new ContentValues();

            val.put(OVERLAY_TITLE, title);
            val.put(IS_READ, 1);

            check = sql_db.insert(TBL_OVERLAYS, null, val);
        }

        sql_db.close();

        return check > 0;
    }
}
