package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.zem.patientcareapp.Model.FreeProducts;

/**
 * Created by Zem on 11/23/2015.
 */
public class FreeProductsController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    // FREE PRODUCTS TABLE
    public static final String TBL_FREE_PRODUCTS = "free_products",
            SERVER_FP_ID = "free_products_id",
            FP_DFP_ID = "dfp_id",               // foreign key ID from discounts_free_products table
            FP_PRODUCT_ID = "product_id",       // the ID of the free item
            FP_QTY_FREE = "quantity_free",      // how many items are for free
            FP_CREATED_AT = "created_at",
            FP_UPDATED_AT = "updated_at",
            FP_DELETED_AT = "deleted_at";

     // SQL to create tale "free_products"
        public static final String CREATE_TABLE = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s TEXT)",
                TBL_FREE_PRODUCTS, AI_ID, SERVER_FP_ID, FP_DFP_ID, FP_PRODUCT_ID, FP_QTY_FREE, FP_CREATED_AT, FP_UPDATED_AT, FP_DELETED_AT);

    public FreeProductsController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    /* PROMO_FREE_PRODUCTS */
    public boolean saveFreeProducts(FreeProducts freeProducts, String action) {
        long row;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SERVER_FP_ID, freeProducts.getFreeProductsId());
        values.put(FP_DFP_ID, freeProducts.getDfpId());
        values.put(FP_PRODUCT_ID, freeProducts.getFreeProductsId());
        values.put(FP_QTY_FREE, freeProducts.getQuantityFree());
        values.put(FP_CREATED_AT, freeProducts.getCreatedAt());
        values.put(FP_UPDATED_AT, freeProducts.getUpdatedAt());
        values.put(FP_DELETED_AT, freeProducts.getDeletedAt());

        if (action.equals("insert")) {
            row = sql_db.insert(TBL_FREE_PRODUCTS, null, values);
        } else {
            row = sql_db.update(TBL_FREE_PRODUCTS, values, SERVER_FP_ID + "=" + freeProducts.getFreeProductsId(), null);
        }
        sql_db.close();
        return row > 0;
    }
}
