package com.beta.zem.patientcareapp.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.VolleyError;
import com.beta.zem.patientcareapp.ConfigurationModule.Helpers;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;
import com.beta.zem.patientcareapp.Network.PostRequest;
import com.beta.zem.patientcareapp.Activities.ProductsActivity;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.ShowPrescriptionDialog;
import com.beta.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.util.Log.d;

public class ProductsAdapter extends ArrayAdapter implements View.OnClickListener {
    LayoutInflater inflater;
    Context context;

    TextView product_name, rs_price, cart_text, out_of_stock, is_promo, in_stocks;
    ImageView product_image, cart_icon;
    LinearLayout add_to_cart, root;
    ToggleButton add_to_favorite;

    DbHelper db;
    Helpers helpers;

    ArrayList<Map<String, String>> products_items;
    ArrayList<Integer> list_favorites;

    int initial_count = 20;

    public ProductsAdapter(Context context, int resource, ArrayList<Map<String, String>> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        this.context = context;
        products_items = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.product_item, parent, false);

        product_image = (ImageView) convertView.findViewById(R.id.product_image);
        product_name = (TextView) convertView.findViewById(R.id.product_name);
        rs_price = (TextView) convertView.findViewById(R.id.rs_price);
        add_to_cart = (LinearLayout) convertView.findViewById(R.id.add_to_cart);
        add_to_favorite = (ToggleButton) convertView.findViewById(R.id.add_to_favorite);
        cart_icon = (ImageView) convertView.findViewById(R.id.cart_icon);
        cart_text = (TextView) convertView.findViewById(R.id.cart_text);
        out_of_stock = (TextView) convertView.findViewById(R.id.out_of_stock);
        root = (LinearLayout) convertView.findViewById(R.id.root);
        is_promo = (TextView) convertView.findViewById(R.id.is_promo);
        in_stocks = (TextView) convertView.findViewById(R.id.in_stocks);

        add_to_cart.setTag(position);
        add_to_favorite.setTag(position);

        if (Integer.parseInt(products_items.get(position).get("available_quantity")) == 0) {
            out_of_stock.setVisibility(View.VISIBLE);
            add_to_cart.setVisibility(View.GONE);
        }

        add_to_cart.setOnClickListener(this);

        db = new DbHelper(context);
        helpers = new Helpers();
        list_favorites = db.getFavoritesByUserID(SidebarActivity.getUserID());

        int product_id = Integer.parseInt(products_items.get(position).get("product_id"));

        for (int x = 0; x < list_favorites.size(); x++) {
            if (list_favorites.get(x) == product_id)
                add_to_favorite.setChecked(true);
        }

        if (ProductsActivity.specific_no_code.size() > 0) {
            for (int x = 0; x < ProductsActivity.specific_no_code.size(); x++) {
                if (ProductsActivity.specific_no_code.get(x).get("product_id").equals(products_items.get(position).get("product_id"))) {
                    double min_purchase = Double.parseDouble(ProductsActivity.specific_no_code.get(x).get("minimum_purchase"));
                    int qty_required = Integer.parseInt(ProductsActivity.specific_no_code.get(x).get("quantity_required"));
                    double peso_discount = Double.parseDouble(ProductsActivity.specific_no_code.get(x).get("peso_discount"));
                    String percentage_discount = ProductsActivity.specific_no_code.get(x).get("percentage_discount");
                    String free_gift = ProductsActivity.specific_no_code.get(x).get("has_free_gifts");
                    String is_every = ProductsActivity.specific_no_code.get(x).get("is_every");

                    String type_of_promo = "", type_of_minimum = "";

                    if (min_purchase > 0) {
                        if (!percentage_discount.equals("0"))
                            type_of_promo = percentage_discount + "% off";
                        else if (peso_discount > 0)
                            type_of_promo = peso_discount + " Php  off";

                        if (is_every.equals("1"))
                            type_of_minimum = " for every Php " + min_purchase + " worth of purchase";
                        else
                            type_of_minimum = " for a minimum purchase of Php " + min_purchase;
                    } else if (qty_required > 0 && free_gift.equals("1")) {
                        String purchases = helpers.getPluralForm(products_items.get(position).get("packing"), qty_required);
                        type_of_promo = "A free item";

                        if (is_every.equals("1"))
                            type_of_minimum = " for every " + qty_required + " " + purchases;
                        else
                            type_of_minimum = " for " + qty_required + " " + purchases + " or more";
                    }

                    if (!type_of_promo.equals("")) {
                        is_promo.setVisibility(View.VISIBLE);
                        is_promo.setText(type_of_promo + type_of_minimum);
                    }
                }
            }
        }

        product_name.setText(products_items.get(position).get("name"));
        rs_price.setText("Php " + products_items.get(position).get("price") + "/" + products_items.get(position).get("packing"));

        if (reachedEndOfList(position))
            loadMoreData();

        return convertView;
    }

    private boolean reachedEndOfList(int position) {
        return position == products_items.size() - 1;
    }

    private void loadMoreData() {
        this.initial_count += 20;

        if (ProductsActivity.products_items.size() >= initial_count) {
            for (int x = (initial_count - 20); x <= initial_count; x++)
                products_items.add(ProductsActivity.products_items.get(x));
        } else {
            for (int x = (initial_count - 20); x <= ProductsActivity.products_items.size(); x++)
                products_items.add(ProductsActivity.products_items.get(x));
        }

        this.notifyDataSetChanged();
    }

    void AddToCart(HashMap<String, String> map) {
        ProductsActivity.map = map;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.add_to_cart:
                final Helpers helpers = new Helpers();
                int pos = Integer.parseInt(String.valueOf(v.getTag()));
                int product_id = Integer.parseInt(products_items.get(pos).get("product_id"));
                int productQty = 1, check = 0, old_qty = 0;
                String server_id = null;

                int is_required = Integer.parseInt(products_items.get(pos).get("prescription_required"));

                final ProgressDialog pdialog = new ProgressDialog(context);
                pdialog.setCancelable(false);
                pdialog.setMessage("Please wait...");

                for (int x = 0; x < ProductsActivity.basket_items.size(); x++) {
                    if (ProductsActivity.basket_items.get(x).get("product_id").equals(String.valueOf(product_id))) {
                        check += 1;
                        old_qty = Integer.parseInt(ProductsActivity.basket_items.get(x).get("quantity"));
                        server_id = ProductsActivity.basket_items.get(x).get("server_id");
                    }
                }

                if (check > 0) { //EXISTING ITEM IN YOUR BASKET (UPDATE ONLY)
                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("patient_id", String.valueOf(SidebarActivity.getUserID()));
                    hashMap.put("table", "baskets");
                    hashMap.put("request", "crud");
                    hashMap.put("action", "update");
                    hashMap.put("id", server_id);
                    int new_qty = old_qty + productQty;
                    hashMap.put("quantity", String.valueOf(new_qty));

                    pdialog.show();
                    PostRequest.send(context, hashMap, new RespondListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject response) {
                            try {
                                int success = response.getInt("success");

                                if (success == 1) {
                                    if (response.getBoolean("has_contents")) {
                                        ProductsActivity.transferHashMap(hashMap);
                                        Snackbar.make(v, "Your cart has been updated", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                d("prod_adapter6", e + "");
                                Snackbar.make(v, "Networ error", Snackbar.LENGTH_SHORT).show();
                            }
                            pdialog.dismiss();
                        }
                    }, new ErrorListener<VolleyError>() {
                        public void getError(VolleyError error) {
                            pdialog.dismiss();
                            d("prod_adapter5", error + "");
                            Snackbar.make(v, "Network error", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else { //ADD NEW SA BASKET
                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("product_id", String.valueOf(product_id));
                    hashMap.put("quantity", String.valueOf(productQty));
                    hashMap.put("patient_id", String.valueOf(SidebarActivity.getUserID()));
                    hashMap.put("table", "baskets");
                    hashMap.put("request", "crud");
                    hashMap.put("action", "insert");

                    if (is_required == 1) { //IF PRESCRIPTION IS REQUIRED
                        GridView gridView;
                        final Dialog builder;
                        HashMap<GridView, Dialog> map;
                        map = helpers.showPrescriptionDialog(context);

                        if (map.size() > 0) { //IF NAA NAY UPLOADED NGA PRESCRIPTION
                            Map.Entry<GridView, Dialog> entry = map.entrySet().iterator().next();
                            gridView = entry.getKey();
                            builder = entry.getValue();

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    pdialog.show();
                                    int prescriptionId = (int) id;
                                    hashMap.put("prescription_id", prescriptionId + "");
                                    hashMap.put("is_approved", "0");

                                    PostRequest.send(context, hashMap, new RespondListener<JSONObject>() {
                                        @Override
                                        public void getResult(JSONObject response) {
                                            try {
                                                int success = response.getInt("success");

                                                if (success == 1) {
                                                    if (response.getBoolean("has_contents")) {
                                                        hashMap.put("server_id", String.valueOf(response.getInt("last_inserted_id")));
                                                        ProductsActivity.transferHashMap(hashMap);
                                                        Snackbar.make(v, "New item has been added to your cart", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                d("prod_adapter4", e + "");
                                                Snackbar.make(v, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                                            }
                                            pdialog.dismiss();
                                        }
                                    }, new ErrorListener<VolleyError>() {
                                        public void getError(VolleyError error) {
                                            pdialog.dismiss();
                                            d("prod_adapter3", error + "");
                                            Snackbar.make(v, "Network error", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.dismiss();
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    pdialog.dismiss();
                                }
                            });
                        } else { //IF EMPTY ANG PRESCRIPTIONS NGA TAB
                            AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(context);
                            confirmationDialog.setTitle("Attention!");
                            confirmationDialog.setMessage("This product requires you to upload a prescription, do you wish to continue ?");
                            confirmationDialog.setNegativeButton("No", null);
                            confirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddToCart(hashMap);
                                    context.startActivity(new Intent(context, ShowPrescriptionDialog.class));
                                }
                            });
                            confirmationDialog.show();
                        }
                    } else { //IF PRESCRIPTION IS NOT REQUIRED
                        pdialog.show();

                        hashMap.put("prescription_id", "0");
                        hashMap.put("is_approved", "1");

                        d("params_pa", hashMap + "");

                        PostRequest.send(context, hashMap, new RespondListener<JSONObject>() {
                            @Override
                            public void getResult(JSONObject response) {
                                try {
                                    int success = response.getInt("success");

                                    if (success == 1) {
                                        if (response.getBoolean("has_contents")) {
                                            hashMap.put("server_id", String.valueOf(response.getInt("last_inserted_id")));
                                            ProductsActivity.transferHashMap(hashMap);
                                            Snackbar.make(v, "New item has been added to your cart", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (Exception e) {
                                    d("prod_adapter2", e + "");
                                    Snackbar.make(v, "Server error occurred" + "", Snackbar.LENGTH_SHORT).show();
                                }
                                pdialog.dismiss();
                            }
                        }, new ErrorListener<VolleyError>() {
                            public void getError(VolleyError error) {
                                pdialog.dismiss();
                                d("prod_adapter1", error + "");
                                Snackbar.make(v, "Network error", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                break;
        }
    }
}
