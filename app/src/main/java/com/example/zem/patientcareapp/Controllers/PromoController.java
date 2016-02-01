package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.zem.patientcareapp.Model.Promo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/23/2015.
 */
public class PromoController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    // PROMOS TABLE
    public static final String TBL_PROMO = "promo",
            SERVER_PROMO_ID = "promo_id",
            PROMO_NAME = "name",
            PROMO_START_DATE = "start_date",
            PROMO_END_DATE = "end_date",
            PROMO_CREATED_AT = "created_at",
            PROMO_UPDATED_AT = "updated_at",
            PROMO_DELETED_AT = "deleted_at";

    // SQL to create table "promo"SQL_CREATE_PROMO_TABLE
    public static final String CREATE_TABLE = String.format("CREATE TABLE %s( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT )",
            TBL_PROMO, AI_ID, SERVER_PROMO_ID, PROMO_NAME, PROMO_START_DATE, PROMO_END_DATE, PROMO_CREATED_AT, PROMO_UPDATED_AT, PROMO_DELETED_AT);

    public PromoController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    /* PROMO TABLE */
    public boolean savePromo(Promo promo, String action) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SERVER_PROMO_ID, promo.getServerPromoId());
        values.put(PROMO_NAME, promo.getName());
        values.put(PROMO_START_DATE, promo.getStartDate());
        values.put(PROMO_END_DATE, promo.getEndDate());
        values.put(PROMO_CREATED_AT, promo.getCreatedAt());
        values.put(PROMO_UPDATED_AT, promo.getUpdatedAt());
        values.put(PROMO_DELETED_AT, promo.getDeletedAt());

        long row;

        if (action.equals("insert")) {
            row = sql_db.insert(TBL_PROMO, null, values);
        } else {
            row = sql_db.update(TBL_PROMO, values, SERVER_PROMO_ID + "=" + promo.getServerPromoId(), null);
        }
        sql_db.close();
        return row > 0;
    }

    //for promo
    public ArrayList<HashMap<String, String>> getPromo() {
        String sql = "Select pr.name as promo_name, pr.*, (Select min(dfp.less) from discounts_free_products as dfp " +
                "where dfp.promo_id = pr.promo_id and dfp.type=0) as min_discount, " +
                " (Select max(dfp.less) from discounts_free_products as dfp where dfp.promo_id = pr.promo_id and dfp.type=0) as max_discount from promo as pr " +
                "where  datetime('now') <= datetime(pr.end_date) and datetime('now') >= datetime(pr.start_date)";
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        Cursor cur = sql_db.rawQuery(sql, null);

        ArrayList<HashMap<String, String>> promo = new ArrayList<>();

        cur.moveToFirst();
        while (!cur.isAfterLast()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("promo_name", cur.getString(cur.getColumnIndex("promo_name")));
            map.put("min_discount", cur.getString(cur.getColumnIndex("min_discount")));
            map.put("max_discount", cur.getString(cur.getColumnIndex("max_discount")));
            map.put("start_date", cur.getString(cur.getColumnIndex("start_date")));
            map.put("end_date", cur.getString(cur.getColumnIndex("end_date")));

            promo.add(map);
            cur.moveToNext();
        }
        cur.close();
        sql_db.close();
        return promo;
    }

    public ArrayList<HashMap<String, String>> getPromoFreeProducts(int promoId) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "Select pfp.*, p.name as product_name, pd.name as promo_name, pd.quantity_required from promo_free_products " +
                "as pfp inner join promo_discounts as pd inner join products as p on p.product_id=pd.product_id where pfp.promo_id=" + promoId +
                " and datetime(pd.end_date) >= datetime('now')";
        Cursor cur = sql_db.rawQuery(sql, null);
        ArrayList<HashMap<String, String>> products = new ArrayList<>();

        while (!cur.isAfterLast()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("promo_name", cur.getString(cur.getColumnIndex("promo_name")));
            map.put("product_name", cur.getString(cur.getColumnIndex("product_name")));
            map.put("no_of_units_free", cur.getString(cur.getColumnIndex("no_of_units_free")));
            map.put("quantity_required", cur.getString(cur.getColumnIndex("quantity_required")));

            products.add(map);
            cur.moveToNext();
        }
        cur.close();
        sql_db.close();
        return products;
    }
}
