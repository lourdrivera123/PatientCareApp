package com.example.zem.patientcareapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.CheckoutModule.DeliverPickupOption;
import com.example.zem.patientcareapp.CheckoutModule.PromosDiscounts;
import com.example.zem.patientcareapp.Controllers.BasketController;
import com.example.zem.patientcareapp.Controllers.OrderPreferenceController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.Network.ListRequestFromCustomURI;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.example.zem.patientcareapp.adapter.ShoppingCartAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.util.Log.d;

public class ShoppingCartActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar myToolBar;
    ListView lisOfItems;
    LinearLayout root;
    TextView proceed_to_checkout;
    public static TextView total_amount;
    public static TextView total_savings;
    OrderPreferenceController opc;

    ShoppingCartAdapter adapter;
    BasketController bc;
    OrderModel order_model;
    public static AppCompatDialog pDialog;
    AlertDialog.Builder builder;
    TextView no_items_label;

    public ArrayList<HashMap<String, String>> items = new ArrayList();
    public ArrayList<HashMap<String, String>> items1 = new ArrayList();
    public static ArrayList<HashMap<String, String>> no_code_promos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        lisOfItems = (ListView) findViewById(R.id.lisOfItems);
        no_items_label = (TextView) findViewById(R.id.no_items_label);
        root = (LinearLayout) findViewById(R.id.root);
        total_amount = (TextView) findViewById(R.id.total_amount);
        proceed_to_checkout = (TextView) findViewById(R.id.proceed_to_checkout);
        total_savings = (TextView) findViewById(R.id.total_savings);

        bc = new BasketController();
        opc = new OrderPreferenceController(getBaseContext());
        no_code_promos = new ArrayList();
        order_model = opc.getOrderPreference();

        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shopping Cart");
        myToolBar.setNavigationIcon(R.drawable.ic_back);

        showBeautifulDialog();

        proceed_to_checkout.setOnClickListener(this);

        ListOfPatientsRequest.getJSONobj(ShoppingCartActivity.this, "get_nocode_promos", "promos", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray json_mysql = response.getJSONArray("promos");

                        for (int x = 0; x < json_mysql.length(); x++) {
                            JSONObject obj = json_mysql.getJSONObject(x);
                            HashMap<String, String> map = new HashMap();

                            if (obj.getString("product_applicability").equals("SPECIFIC_PRODUCTS")) {
                                map.put("promo_id", obj.getString("pr_promo_id"));
                                map.put("minimum_purchase", obj.getString("minimum_purchase"));
                                map.put("quantity_required", obj.getString("quantity_required"));
                                map.put("is_every", obj.getString("is_every"));
                                map.put("product_id", obj.getString("product_id"));
                                map.put("has_free_gifts", obj.getString("has_free_gifts"));
                                map.put("percentage_discount", obj.getString("percentage_discount"));
                                map.put("peso_discount", obj.getString("peso_discount"));
                                map.put("free_product_id", obj.getString("free_product_id"));
                                map.put("name", obj.getString("name"));
                                map.put("quantity_free", obj.getString("quantity_free"));
                                map.put("free_product_packing", obj.getString("free_product_packing"));
                                no_code_promos.add(map);
                            }
                        }
                    }
                } catch (Exception e) {
                    Snackbar.make(root, e + "", Snackbar.LENGTH_SHORT).show();
                }

                String url_raw = "check_basket?patient_id=" + SidebarActivity.getUserID() + "&branch_id=" + order_model.getBranch_id();
                ListRequestFromCustomURI.getJSONobj(url_raw, new RespondListener<JSONObject>() {
                    @Override
                    public void getResult(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                if (response.getBoolean("basket_quantity_changed")) {
                                    letDialogSleep();
                                    cartQuantityUpdated();
                                }
                                JSONArray json_mysql = response.getJSONArray("baskets");
                                d("baskets_json", json_mysql + "");

                                if (json_mysql.length() == 0) {
                                    lisOfItems.setVisibility(View.GONE);
                                    no_items_label.setVisibility(View.VISIBLE);
                                }
                                items = bc.convertFromJson(ShoppingCartActivity.this, json_mysql);
                                items1.addAll(items);
                                adapter = new ShoppingCartAdapter(ShoppingCartActivity.this, R.layout.item_shopping_cart, items);
                                lisOfItems.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            Toast.makeText(ShoppingCartActivity.this, e + "", Toast.LENGTH_SHORT).show();
                        }
                        letDialogSleep();

                    }
                }, new ErrorListener<VolleyError>() {
                    @Override
                    public void getError(VolleyError e) {
                        letDialogSleep();
                        Snackbar.make(root, "Network Error", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                letDialogSleep();
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    void cartQuantityUpdated() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ShoppingCartActivity.this);
//        dialog.setTitle("Order Cancelled!");
        dialog.setMessage("Our records show that one or more products in your cart exceeds the number of our stocks. \n" +
                "We updated your basket items.");
        dialog.setCancelable(false);
        dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    void showBeautifulDialog() {
        builder = new AlertDialog.Builder(ShoppingCartActivity.this);
        builder.setView(R.layout.progress_stuffing);
        builder.setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    void letDialogSleep() {
        pDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.proceed_to_checkout:
                updateBasket(CartWithPromos(items, no_code_promos), 1);
                break;
        }
    }


    protected void onPause() {
        updateBasket(CartWithPromos(items1, no_code_promos), 0);
        super.onPause();
    }

    ArrayList<HashMap<String, String>> CartWithPromos(ArrayList<HashMap<String, String>> items, ArrayList<HashMap<String, String>> promos) {
        ArrayList<HashMap<String, String>> new_items = new ArrayList();

        for (int x = 0; x < items.size(); x++) {
            for (int y = 0; y < promos.size(); y++) {
                if (items.get(x).get("product_id").equals(promos.get(y).get("product_id"))) {
                    HashMap<String, String> map = new HashMap();
                    map.putAll(items.get(x));
                    map.put("promo_id", "0");
                    map.put("promo_type", "");
                    map.put("promo_value", "0");
                    map.put("promo_free_product_qty", "0");

                    double cart_total = Double.parseDouble(items.get(x).get("price")) * Double.parseDouble(items.get(x).get("quantity"));

                    if (promos.get(y).get("has_free_gifts").equals("1")) {
                        if (Integer.parseInt(items.get(x).get("quantity")) >= Integer.parseInt(promos.get(y).get("quantity_required"))) {
                            map.put("promo_id", promos.get(y).get("promo_id"));
                            map.put("promo_type", "free_gift");
                            map.put("promo_value", promos.get(y).get("free_product_id"));

                            if (promos.get(y).get("is_every").equals("1")) {
                                int discount_times = (int) (Integer.parseInt(items.get(x).get("quantity")) / Integer.parseInt(promos.get(y).get("quantity_required")));
                                int qty_free = discount_times * Integer.parseInt(promos.get(y).get("quantity_free"));
                                map.put("promo_free_product_qty", String.valueOf(qty_free));
                            }
                        }
                    } else if (Double.parseDouble(promos.get(y).get("percentage_discount")) > 0) {
                        if (cart_total >= Double.parseDouble(promos.get(y).get("minimum_purchase"))) {
                            double percent = Double.parseDouble(String.valueOf(Double.parseDouble(promos.get(y).get("percentage_discount")) / 100.0f));
                            double percent_off = cart_total * percent;
                            map.put("promo_id", promos.get(y).get("promo_id"));
                            map.put("promo_type", "percentage_discount");
                            map.put("promo_value", String.valueOf(percent_off));
                        }
                    } else if (Double.parseDouble(promos.get(y).get("peso_discount")) > 0) {
                        if (cart_total >= Double.parseDouble(promos.get(y).get("minimum_purchase"))) {
                            map.put("promo_id", promos.get(y).get("promo_id"));
                            map.put("promo_type", "peso_discount");

                            if (promos.get(y).get("is_every").equals("1")) {
                                int discount_times = (int) ((int) cart_total / Double.parseDouble(promos.get(y).get("minimum_purchase")));
                                double peso_off = discount_times * Double.parseDouble(promos.get(y).get("peso_discount"));
                                map.put("promo_value", String.valueOf(peso_off));
                            } else
                                map.put("promo_value", String.valueOf(promos.get(y).get("peso_discount")));
                        }
                    } else {
                        map.put("promo_value", String.valueOf(0));
                    }

                    new_items.add(map);
                }
            }
        }


        for (int x = 0; x < new_items.size(); x++) {
            for (int y = 0; y < items.size(); y++) {
                if (new_items.get(x).get("product_id").equals(items.get(y).get("product_id"))) {
                    items.remove(y);
                }
            }
        }

        for (int x = 0; x < items.size(); x++) {
            HashMap<String, String> map = items.get(x);
            map.put("promo_id", "0");
            map.put("promo_type", "");
            map.put("promo_value", "0");
            map.put("promo_free_product_qty", "0");
            items.set(x, map);
        }

        new_items.addAll(items);

        return new_items;
    }

    void updateBasket(ArrayList<HashMap<String, String>> objects, final int check) {
        try {
            JSONArray master_arr = new JSONArray();
            HashMap<String, String> hash = new HashMap();
            JSONObject obj_for_server;

            for (int x = 0; x < objects.size(); x++) {
                hash.put("quantity", objects.get(x).get("quantity"));
                hash.put("id", objects.get(x).get("basket_id"));
                hash.put("promo_id", objects.get(x).get("promo_id"));
                hash.put("promo_type", objects.get(x).get("promo_type"));
                hash.put("promo_value", objects.get(x).get("promo_value"));
                hash.put("promo_free_product_qty", objects.get(x).get("promo_free_product_qty"));
                obj_for_server = new JSONObject(hash);
                master_arr.put(obj_for_server);
            }

            final JSONObject json_to_be_passed = new JSONObject();
            json_to_be_passed.put("jsobj", master_arr);

            HashMap<String, String> params = new HashMap();
            params.put("table", "baskets");
            params.put("request", "crud");
            params.put("action", "multiple_update_for_basket");
            params.put("jsobj", json_to_be_passed.toString());

            d("a_jsobj", json_to_be_passed.toString());

            PostRequest.send(ShoppingCartActivity.this, params, new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {
                    d("response_sc", response + "");
                    try {
                        int success = response.getInt("success");
                        if (success == 1) {
                            if (check == 1) {
                                if (order_model.isValid()) {
                                    Intent intent = new Intent(ShoppingCartActivity.this, PromosDiscounts.class);
                                    intent.putExtra("order_model", order_model);
                                    startActivity(intent);
                                    ShoppingCartActivity.this.finish();
                                } else {
                                    Intent intent = new Intent(ShoppingCartActivity.this, DeliverPickupOption.class);
                                    intent.putExtra("order_model", order_model);
                                    startActivity(intent);
                                    ShoppingCartActivity.this.finish();
                                }
                            }
                        }
                    } catch (Exception e) {
                        Snackbar.make(root, "cart_error: " + e, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    Snackbar.make(root, "Network Error", Snackbar.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            Snackbar.make(root, "cart_json_error: " + e, Snackbar.LENGTH_SHORT).show();
        }
    }
}