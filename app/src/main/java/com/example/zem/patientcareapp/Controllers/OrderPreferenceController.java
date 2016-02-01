package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Network.CustomPostRequest;
import com.example.zem.patientcareapp.Network.GetRequest;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.util.Log.d;
import static com.example.zem.patientcareapp.Network.CustomPostRequest.send;
import static java.lang.System.out;

/**
 * Created by Zem on 11/23/2015.
 */
public class OrderPreferenceController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    //ORDERS TABLEre
    public static final String TBL_ORDER_PREFERENCES = "order_preference",
            ORDER_PREFERENCES_PATIENT_ID = "patient_id",
            ORDER_PREFERENCES_RECIPIENT_NAME = "recipient_name",
            ORDER_PREFERENCES_RECIPIENT_ADDRESS = "recipient_address",
            ORDER_PREFERENCES_RECIPIENT_NUMBER = "recipient_contactNumber",
            ORDER_PREFERENCES_MODE_OF_DELIVERY = "mode_of_delivery",
            ORDER_PREFERENCES_PAYMENT_METHOD = "payment_method",
            ORDER_PREFERENCES_BRANCH_ID = "branch_id",
            ORDER_PREFERENCES_SERVER_ID = "server_id";

    public static final String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT," +
                    "%s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s INTEGER )",
            TBL_ORDER_PREFERENCES, AI_ID, ORDER_PREFERENCES_PATIENT_ID, ORDER_PREFERENCES_RECIPIENT_NAME, ORDER_PREFERENCES_RECIPIENT_ADDRESS, ORDER_PREFERENCES_RECIPIENT_NUMBER, ORDER_PREFERENCES_MODE_OF_DELIVERY,
            ORDER_PREFERENCES_PAYMENT_METHOD, ORDER_PREFERENCES_BRANCH_ID, CREATED_AT, UPDATED_AT, DELETED_AT, ORDER_PREFERENCES_SERVER_ID);

    public OrderPreferenceController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean saveSelectedBranch(OrderModel order_model) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ORDER_PREFERENCES_BRANCH_ID, order_model.getBranch_id());
        values.put(ORDER_PREFERENCES_PATIENT_ID, SidebarActivity.getUserID());
        values.put(ORDER_PREFERENCES_RECIPIENT_NAME, order_model.getRecipient_name());
        values.put(ORDER_PREFERENCES_RECIPIENT_ADDRESS, order_model.getRecipient_address());
        values.put(ORDER_PREFERENCES_RECIPIENT_NUMBER, order_model.getRecipient_contactNumber());
        values.put(ORDER_PREFERENCES_MODE_OF_DELIVERY, order_model.getMode_of_delivery());
        values.put(ORDER_PREFERENCES_PAYMENT_METHOD, order_model.getPayment_method());
        values.put(ORDER_PREFERENCES_SERVER_ID, order_model.getServer_id());

        long row = 0;

        if (order_model.getAction().equals("insert")) {
            row = sql_db.insert(TBL_ORDER_PREFERENCES, null, values);
        } else if (order_model.getAction().equals("update")) {
            row = sql_db.update(TBL_ORDER_PREFERENCES, values, ORDER_PREFERENCES_PATIENT_ID + " = " + SidebarActivity.getUserID(), null);
        }
        sql_db.close();
        return row > 0;
    }

    public boolean savePreference(OrderModel order_model) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ORDER_PREFERENCES_PATIENT_ID, SidebarActivity.getUserID());
        values.put(ORDER_PREFERENCES_RECIPIENT_NAME, order_model.getRecipient_name());
        values.put(ORDER_PREFERENCES_RECIPIENT_ADDRESS, order_model.getRecipient_address());
        values.put(ORDER_PREFERENCES_RECIPIENT_NUMBER, order_model.getRecipient_contactNumber());
        values.put(ORDER_PREFERENCES_MODE_OF_DELIVERY, order_model.getMode_of_delivery());
        values.put(ORDER_PREFERENCES_PAYMENT_METHOD, order_model.getPayment_method());
        values.put(ORDER_PREFERENCES_BRANCH_ID, order_model.getBranch_id());

        long row = 0;

        row = sql_db.update(TBL_ORDER_PREFERENCES, values, ORDER_PREFERENCES_PATIENT_ID + " = " + SidebarActivity.getUserID(), null);

        sql_db.close();
        return row > 0;
    }

    public boolean savePreferenceFromJson(JSONObject jobject) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(ORDER_PREFERENCES_PATIENT_ID, jobject.getInt("patient_id"));
        values.put(ORDER_PREFERENCES_RECIPIENT_NAME, jobject.getString(ORDER_PREFERENCES_RECIPIENT_NAME));
        values.put(ORDER_PREFERENCES_RECIPIENT_ADDRESS, jobject.getString(ORDER_PREFERENCES_RECIPIENT_ADDRESS));
        values.put(ORDER_PREFERENCES_RECIPIENT_NUMBER, jobject.getString(ORDER_PREFERENCES_RECIPIENT_NUMBER));
        values.put(ORDER_PREFERENCES_MODE_OF_DELIVERY, jobject.getString(ORDER_PREFERENCES_MODE_OF_DELIVERY));
        values.put(ORDER_PREFERENCES_PAYMENT_METHOD, jobject.getString(ORDER_PREFERENCES_PAYMENT_METHOD));
        values.put(ORDER_PREFERENCES_BRANCH_ID, jobject.getInt(ORDER_PREFERENCES_BRANCH_ID));
        values.put(ORDER_PREFERENCES_SERVER_ID, jobject.getInt(ORDER_PREFERENCES_SERVER_ID));
        values.put(ORDER_PREFERENCES_SERVER_ID, jobject.getString(CREATED_AT));
        values.put(ORDER_PREFERENCES_SERVER_ID, jobject.getString(UPDATED_AT));
        values.put(ORDER_PREFERENCES_SERVER_ID, jobject.getString(DELETED_AT));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        long row = 0;

        row = sql_db.insert(TBL_ORDER_PREFERENCES, null, values);

        sql_db.close();
        return row > 0;
    }

    public OrderModel getOrderPreference() {
        OrderModel order_model = new OrderModel();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT * from " + TBL_ORDER_PREFERENCES + " where patient_id = " + SidebarActivity.getUserID() + " order by created_at DESC";
        Cursor cur = sql_db.rawQuery(sql, null);
        cur.moveToFirst();

        if (cur.getCount() > 0) {
            order_model.setRecipient_name(cur.getString(cur.getColumnIndex(ORDER_PREFERENCES_RECIPIENT_NAME)));
            order_model.setRecipient_address(cur.getString(cur.getColumnIndex(ORDER_PREFERENCES_RECIPIENT_ADDRESS)));
            order_model.setRecipient_contactNumber(cur.getString(cur.getColumnIndex(ORDER_PREFERENCES_RECIPIENT_NUMBER)));
            order_model.setMode_of_delivery(cur.getString(cur.getColumnIndex(ORDER_PREFERENCES_MODE_OF_DELIVERY)));
            order_model.setPayment_method(cur.getString(cur.getColumnIndex(ORDER_PREFERENCES_PAYMENT_METHOD)));
            order_model.setBranch_id(cur.getInt(cur.getColumnIndex(ORDER_PREFERENCES_BRANCH_ID)));
            order_model.setServer_id(cur.getInt(cur.getColumnIndex(ORDER_PREFERENCES_SERVER_ID)));
        }
        cur.close();
        sql_db.close();

        return order_model;
    }
}