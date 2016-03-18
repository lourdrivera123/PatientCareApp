package com.example.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.example.zem.patientcareapp.Model.ProductCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zem on 11/24/2015.
 */
public class ProductCategoryController extends DbHelper {

    DbHelper dbhelper;
    SQLiteDatabase sql_db;

    // PRODUCT_CATEGORIES TABLE
    public static final String PROD_CAT_NAME = "name",
            TBL_PRODUCT_CATEGORIES = "product_categories",
            SERVER_PRODUCT_CATEGORY_ID = "product_category_id";

    // SQL to create table "product_categories"
    public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER UNIQUE, %s TEXT, %s  TEXT , %s  TEXT , %s TEXT  )",
            TBL_PRODUCT_CATEGORIES, AI_ID, SERVER_PRODUCT_CATEGORY_ID, PROD_CAT_NAME, CREATED_AT, UPDATED_AT, DELETED_AT);

    public ProductCategoryController(Context context) {
        super(context);
        dbhelper = new DbHelper(context);
        sql_db = dbhelper.getWritableDatabase();
    }

    public boolean insertProductCategory(ProductCategory category) throws SQLiteConstraintException {
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long rowID = 0;
        try {
            values.put(PROD_CAT_NAME, category.getName());
            values.put(SERVER_PRODUCT_CATEGORY_ID, category.getCategoryId());
            values.put(CREATED_AT, category.getCreatedAt());
            values.put(UPDATED_AT, category.getUpdatedAt());
            values.put(DELETED_AT, category.getDeletedAt());

            rowID = sql_db.insert(TBL_PRODUCT_CATEGORIES, null, values);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        sql_db.close();
        return rowID > 0;
    }

    //for category
    public int getCategoryIdByName(String name) {
        int id = 0;
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT id FROM " + TBL_PRODUCT_CATEGORIES + " WHERE name='" + name + "'";
        Cursor cur = sql_db.rawQuery(sql, null);

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            id = cur.getInt(0);
            cur.moveToNext();
        }
        cur.close();
        sql_db.close();
        return id;
    }

    public List<String> getAllProductCategoriesArray() {
        List<String> list = new ArrayList();
        SQLiteDatabase sql_db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_PRODUCT_CATEGORIES;
        Cursor cur = sql_db.rawQuery(sql, null);
        int x = 0;
        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            list.add(x, cur.getString(2));
            x++;
            cur.moveToNext();
        }
        cur.close();
        sql_db.close();
        return list;
    }
}
