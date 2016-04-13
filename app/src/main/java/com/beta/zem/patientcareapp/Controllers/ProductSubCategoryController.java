package com.beta.zem.patientcareapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.beta.zem.patientcareapp.Model.ProductSubCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zem on 11/24/2015.
 */
public class ProductSubCategoryController extends DbHelper {

    DbHelper dbHelper;
    SQLiteDatabase sql_db;

    // PRODUCT_SUBCATEGORIES TABLE
    public static final String TBL_PRODUCT_SUBCATEGORIES = "product_subcategories",
            PROD_SUBCAT_NAME = "name",
            PROD_SUBCAT_CATEGORY_ID = "category_id",
            SERVER_PRODUCT_SUBCATEGORY_ID = "product_subcategory_id";

    // SQL to create table "product_subcategories"
    public static final String CREATE_TABLE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER UNIQUE, %s TEXT, %s INTEGER, %s  TEXT , %s  TEXT , %s  TEXT  )",
            TBL_PRODUCT_SUBCATEGORIES, AI_ID, SERVER_PRODUCT_SUBCATEGORY_ID, PROD_SUBCAT_NAME, PROD_SUBCAT_CATEGORY_ID, CREATED_AT, UPDATED_AT, DELETED_AT);

    public ProductSubCategoryController(Context context) {
        super(context);
        dbHelper = new DbHelper(context);
        sql_db = dbHelper.getWritableDatabase();
    }

    public boolean insertProductSubCategory(ProductSubCategory subCategory) {
        SQLiteDatabase sql_db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PROD_SUBCAT_NAME, subCategory.getName());
        values.put(PROD_SUBCAT_CATEGORY_ID, subCategory.getCategoryId());
        values.put(SERVER_PRODUCT_SUBCATEGORY_ID, subCategory.getId());
        values.put(CREATED_AT, subCategory.getCreatedAt());
        values.put(UPDATED_AT, subCategory.getUpdatedAt());
        values.put(DELETED_AT, subCategory.getDeletedAt());

        long rowID = sql_db.insert(TBL_PRODUCT_SUBCATEGORIES, null, values);
        sql_db.close();
        return rowID > 0;
    }

    public String[] getAllProductSubCategoriesArray(int categoryId) {
        List<String> list = new ArrayList();
        SQLiteDatabase sql_db = dbHelper.getWritableDatabase();
        String sql = "SELECT * FROM " + TBL_PRODUCT_SUBCATEGORIES + " WHERE category_id='" + categoryId + "' ORDER BY name";
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
        String[] arr = new String[list.size()];
        return list.toArray(arr);
    }

    //for subcategory
    public ProductSubCategory getSubCategoryByName(String name, int categoryId) {
        ProductSubCategory subCategory = new ProductSubCategory();
        SQLiteDatabase sql_db = dbHelper.getWritableDatabase();
        name = name.replace("'", "''");
        String sql = "SELECT * FROM " + TBL_PRODUCT_SUBCATEGORIES + " where name='" + name + "' and category_id='" + categoryId + "'";
        Cursor cur = sql_db.rawQuery(sql, null);

        while (cur.moveToNext()) {
            subCategory.setId(cur.getInt(cur.getColumnIndex(AI_ID)));
            subCategory.setName(cur.getString(cur.getColumnIndex(PROD_SUBCAT_NAME)));
            subCategory.setCategoryId(Integer.parseInt(cur.getString(cur.getColumnIndex(PROD_SUBCAT_CATEGORY_ID))));
            subCategory.setCreatedAt(cur.getString(cur.getColumnIndex(CREATED_AT)));
            subCategory.setUpdatedAt(cur.getString(cur.getColumnIndex(UPDATED_AT)));
            subCategory.setDeletedAt(cur.getString(cur.getColumnIndex(DELETED_AT)));
        }
        cur.close();
        sql_db.close();
        return subCategory;
    }

}
