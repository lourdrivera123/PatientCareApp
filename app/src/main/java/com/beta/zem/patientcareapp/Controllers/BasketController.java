package com.beta.zem.patientcareapp.Controllers;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/23/2015.
 */

public class BasketController {

    public BasketController() {

    }

    public ArrayList<HashMap<String, String>> convertFromJson(Context context, JSONArray json_array) {
        ArrayList<HashMap<String, String>> basketItems = new ArrayList();

        try {
            for (int x = 0; x < json_array.length(); x++) {
                JSONObject obj = json_array.getJSONObject(x);

                double item_subtotal = Double.parseDouble(String.valueOf(obj.getDouble("price"))) * Double.parseDouble(String.valueOf(obj.getDouble("quantity")));

                HashMap<String, String> map = new HashMap();
                map.put("item_subtotal", String.valueOf(item_subtotal));
                map.put("product_id", String.valueOf(obj.getInt("id")));
                map.put("basket_id", String.valueOf(obj.getInt("basket_id")));
                map.put("name", obj.getString("name"));
                map.put("price", String.valueOf(obj.getDouble("price")));
                map.put("quantity", String.valueOf(obj.getInt("quantity")));
                map.put("unit", obj.getString("unit"));
                map.put("sku", obj.getString("sku"));
                map.put("packing", obj.getString("packing"));
                map.put("qty_per_packing", String.valueOf(obj.getInt("qty_per_packing")));
                map.put("prescription_required", String.valueOf(obj.getInt("prescription_required")));
                map.put("prescription_id", String.valueOf(obj.getInt("prescription_id")));
                map.put("is_approved", String.valueOf(obj.getInt("is_approved")));
                map.put("available_quantity", obj.getString("available_quantity"));
                map.put("promo_type", obj.getString("promo_type"));
                map.put("peso_discount", String.valueOf(obj.getDouble("peso_discount")));
                map.put("percentage_discount", String.valueOf(obj.getDouble("percentage_discount")));
                map.put("promo_free_product_qty", String.valueOf(obj.getInt("promo_free_product_qty")));


                basketItems.add(map);
            }
        } catch (Exception e) {
            Toast.makeText(context, e + "", Toast.LENGTH_SHORT).show();
        }

        return basketItems;
    }
}
