package com.beta.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zemskie on 12/9/2015.
 */
public class BillingController extends DbHelper {

    //BILLINGS TABLE
    public static final String TBL_BILLINGS = "billings",
            SERVER_BILLINGS_ID = "billings_id",
            BILLINGS_ORDER_ID = "order_id",
            BILLINGS_GROSS_TOTAL = "gross_total",
            BILLINGS_TOTAL = "total",
            BILLINGS_PAYMENT_STATUS = "payment_status",
            BILLINGS_PAYMENT_METHOD = "payment_method",
            BILLINGS_COUPON_DISCOUNT = "coupon_discount",
            BILLINGS_POINTS_DISCOUNT = "points_discount";

    public static final String CREATE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s DOUBLE, %s DOUBLE, %s TEXT, %s TEXT, %s DOUBLE, %s DOUBLE, %s TEXT, %s TEXT, %s TEXT)",
            TBL_BILLINGS, AI_ID, SERVER_BILLINGS_ID, BILLINGS_ORDER_ID, BILLINGS_GROSS_TOTAL, BILLINGS_TOTAL, BILLINGS_PAYMENT_STATUS, BILLINGS_PAYMENT_METHOD, BILLINGS_COUPON_DISCOUNT, BILLINGS_POINTS_DISCOUNT, CREATED_AT, UPDATED_AT, DELETED_AT);

    DbHelper dbhelper;

    public BillingController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
    }

    public boolean saveBillings(JSONObject jobject){
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            values.put(SERVER_BILLINGS_ID, jobject.getInt(AI_ID));
            values.put(BILLINGS_ORDER_ID, jobject.getInt(BILLINGS_ORDER_ID));
            values.put(BILLINGS_GROSS_TOTAL, jobject.getDouble(BILLINGS_GROSS_TOTAL));
            values.put(BILLINGS_TOTAL, jobject.getDouble(BILLINGS_TOTAL));
            values.put(BILLINGS_PAYMENT_STATUS, jobject.getString(BILLINGS_PAYMENT_STATUS));
            values.put(BILLINGS_PAYMENT_METHOD, jobject.getString(BILLINGS_PAYMENT_METHOD));
            values.put(BILLINGS_COUPON_DISCOUNT, jobject.getDouble(BILLINGS_COUPON_DISCOUNT));
            values.put(BILLINGS_POINTS_DISCOUNT, jobject.getDouble(BILLINGS_POINTS_DISCOUNT));
            values.put(CREATED_AT, jobject.getString(CREATED_AT));
            values.put(UPDATED_AT, jobject.getString(UPDATED_AT));
            values.put(DELETED_AT, jobject.getString(DELETED_AT));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long row = sql_db.insert(TBL_BILLINGS, null, values);

        sql_db.close();
        return row > 0;
    }
}
