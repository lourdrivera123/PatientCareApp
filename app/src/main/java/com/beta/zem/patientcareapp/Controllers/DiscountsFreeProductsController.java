package com.beta.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.beta.zem.patientcareapp.Model.DiscountsFreeProducts;

/**
 * Created by Zem on 11/23/2015.
 */
public class DiscountsFreeProductsController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    // DISCOUNTS or FREE PRODUCTS PROMOTION TABLE
    public static final String TBL_DISCOUNTS_FREE_PRODUCTS = "discounts_free_products",
            SERVER_DFP_ID = "dfp_id",
            DFP_PROMO_ID = "promo_id",
            DFP_PRODUCT_ID = "product_id",
            DFP_TYPE = "type",                          // type = 1 or 0 , 1 for Free Products and 0 for Discount
            DFP_QUANTITY_REQUIRED = "quantity_required",
            DFP_LESS = "less";

            // SQL to create table discounts_free_products
        public static final String CREATE_TABLE = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s DOUBLE, %s TEXT, %s TEXT, %s TEXT)",
                TBL_DISCOUNTS_FREE_PRODUCTS, AI_ID, SERVER_DFP_ID, DFP_PROMO_ID, DFP_PRODUCT_ID, DFP_TYPE, DFP_QUANTITY_REQUIRED, DFP_LESS, CREATED_AT, UPDATED_AT, DELETED_AT);

    public DiscountsFreeProductsController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    /* DISCOUNTS & FREE PRODUCTS TABLE TABLE */
    public boolean saveDiscountsFreeProducts(DiscountsFreeProducts discountsFreeProducts, String action) {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SERVER_DFP_ID, discountsFreeProducts.getDfpId());
        values.put(DFP_LESS, discountsFreeProducts.getLess());
        values.put(DFP_PROMO_ID, discountsFreeProducts.getPromoId());
        values.put(DFP_PRODUCT_ID, discountsFreeProducts.getProductId());
        values.put(DFP_QUANTITY_REQUIRED, discountsFreeProducts.getQuantityRequired());
        values.put(DFP_TYPE, discountsFreeProducts.getType());
        values.put(CREATED_AT, discountsFreeProducts.getCreatedAt());
        values.put(UPDATED_AT, discountsFreeProducts.getUpdatedAt());
        values.put(DELETED_AT, discountsFreeProducts.getDeletedAt());

        long row;

        if (action.equals("insert")) {
            row = sql_db.insert(TBL_DISCOUNTS_FREE_PRODUCTS, null, values);
        } else {
            row = sql_db.update(TBL_DISCOUNTS_FREE_PRODUCTS, values, SERVER_DFP_ID + "=" + discountsFreeProducts.getDfpId(), null);
        }
        sql_db.close();
        return row > 0;
    }
}
