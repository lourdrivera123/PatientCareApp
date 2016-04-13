package com.beta.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beta.zem.patientcareapp.Model.Messages;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/23/2015.
 */
public class MessageController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //MESSAGES
    public static final String TBl_MSGS = "messages",
            MSGS_SERVER_ID = "serverID",
            MSGS_SUBJECT = "subject",
            MSGS_CONTENT = "content";

            //sql to create messages table
        public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)",
                TBl_MSGS, AI_ID, MSGS_SERVER_ID, PATIENT_ID, MSGS_SUBJECT, MSGS_CONTENT, IS_READ, CREATED_AT, UPDATED_AT, DELETED_AT);

    public MessageController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean saveMessages(JSONObject json, String request) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues val = new ContentValues();
        long rowID = 0;

        try {
            val.put(MSGS_SERVER_ID, json.getInt("id"));
            val.put(PATIENT_ID, json.getInt(PATIENT_ID));
            val.put(MSGS_SUBJECT, json.getString(MSGS_SUBJECT));
            val.put(MSGS_CONTENT, json.getString(MSGS_CONTENT));
            val.put(IS_READ, json.getInt(IS_READ));
            val.put(CREATED_AT, json.getString(CREATED_AT));
            val.put(UPDATED_AT, json.getString(UPDATED_AT));

            if (request.equals("insert")) {
                rowID = sql_db.insert(TBl_MSGS, null, val);
            } else {
                rowID = sql_db.update(TBl_MSGS, val, MSGS_SERVER_ID + " = " + json.getInt("id"), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sql_db.close();

        return rowID > 0;
    }

    public ArrayList<HashMap<String, String>> getAllMessages(int patientID) {
        ArrayList<HashMap<String, String>> array = new ArrayList();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBl_MSGS + " WHERE " + PATIENT_ID + " = " + patientID;
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            HashMap<String, String> map = new HashMap();
            map.put("subject", cur.getString(cur.getColumnIndex(MSGS_SUBJECT)));
            map.put("content", cur.getString(cur.getColumnIndex(MSGS_CONTENT)));
            map.put("created_at", cur.getString(cur.getColumnIndex(CREATED_AT)));
            map.put("isRead", cur.getString(cur.getColumnIndex(IS_READ)));
            map.put("serverID", String.valueOf(cur.getInt(cur.getColumnIndex(MSGS_SERVER_ID))));
            array.add(map);
        }

        cur.close();
        sql_db.close();

        return array;
    }

    public Messages getSpecificMessage(int serverID) {
        Messages messages = new Messages();
        String sql = "SELECT * FROM " + TBl_MSGS + " WHERE " + MSGS_SERVER_ID + " = " + serverID;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        Cursor cur = sql_db.rawQuery(sql, null);
        cur.moveToFirst();

        if (cur.getCount() > 0) {
            messages.setServerID(serverID);
            messages.setSubject(cur.getString(cur.getColumnIndex(MSGS_SUBJECT)));
            messages.setContent(cur.getString(cur.getColumnIndex(MSGS_CONTENT)));
            messages.setDate(cur.getString(cur.getColumnIndex(CREATED_AT)));
            messages.setIsRead(cur.getInt(cur.getColumnIndex(IS_READ)));
        }

        cur.close();
        sql_db.close();

        return messages;
    }
}
