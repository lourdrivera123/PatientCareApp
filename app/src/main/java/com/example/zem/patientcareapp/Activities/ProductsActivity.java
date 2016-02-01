package com.example.zem.patientcareapp.Activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.zem.patientcareapp.Controllers.BasketController;
import com.example.zem.patientcareapp.Controllers.OrderPreferenceController;
import com.example.zem.patientcareapp.Controllers.OverlayController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Interface.StringRespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.Network.StringRequests;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.example.zem.patientcareapp.adapter.ProductsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User PC on 11/20/2015.
 */

public class ProductsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {
    ListView listOfProducts;
    Toolbar myToolBar;
    LinearLayout results_layout, root;
    TextView noOfResults, number_of_notif, no_products, branch_selected;
    ImageButton go_to_cart;
    Spinner spinner_categories;

    ProductsAdapter adapter;
    ArrayAdapter spinner_adapter;

    Helpers helpers;
    RequestQueue queue;
    static DbHelper db;
    BasketController bc;
    static OverlayController oc;
    OrderPreferenceController opc;

    Intent get_intent;

    public static ArrayList<Map<String, String>> temp_products_items, products_items, basket_items;
    public static ArrayList<HashMap<String, String>> specific_no_code;
    public static HashMap<String, String> map;
    ArrayList<HashMap<Integer, HashMap<String, String>>> searchProducts = new ArrayList();
    ArrayList<String> categories = new ArrayList();

    public static int is_finish;
    int promo_id = 0;
    OrderModel order_model;
    public static AppCompatDialog pDialog;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_products_layout);

        results_layout = (LinearLayout) findViewById(R.id.results_layout);
        root = (LinearLayout) findViewById(R.id.root);
        noOfResults = (TextView) findViewById(R.id.noOfResults);
        listOfProducts = (ListView) findViewById(R.id.listOfProducts);
        spinner_categories = (Spinner) findViewById(R.id.spinner_categories);
        no_products = (TextView) findViewById(R.id.no_products);
        branch_selected = (TextView) findViewById(R.id.branch_selected);

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        db = new DbHelper(this);
        bc = new BasketController();
        oc = new OverlayController(this);
        queue = Volley.newRequestQueue(this);
        helpers = new Helpers();
        opc = new OrderPreferenceController(this);

        basket_items = new ArrayList();
        map = new HashMap();
        products_items = new ArrayList();
        temp_products_items = new ArrayList();
        specific_no_code = new ArrayList();
        order_model = opc.getOrderPreference();

        showOverLay(this);
        getProductCategories();
        getAllBasketItems();

        get_intent = getIntent();
        promo_id = get_intent.getIntExtra("promo_id", 0);

        setBranchNameFromServer();

        ListOfPatientsRequest.getJSONobj(this, "get_nocode_promos", "promos", new RespondListener<JSONObject>() {
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
                                specific_no_code.add(map);
                            }
                        }
                    }
                } catch (Exception e) {
                    Snackbar.make(root, e + "", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });

        listOfProducts.setOnItemClickListener(this);
        spinner_categories.setOnItemSelectedListener(this);
    }

    void setBranchNameFromServer() {
        StringRequests.getString(ProductsActivity.this, "db/get.php?q=get_branch_name_from_id&branch_id=" + order_model.getBranch_id(), new StringRespondListener<String>() {
            @Override
            public void getResult(String response) {
                branch_selected.setText(response+" Branch");
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Log.d("fetch_name_err", error + "");
            }
        });
    }

    void showBeautifulDialog() {
        builder = new AlertDialog.Builder(ProductsActivity.this);
        builder.setView(R.layout.progress_stuffing);
        builder.setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    void letDialogSleep() {
        pDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SelectedProductActivity.is_resumed = 0;

        if (is_finish != 0) {
//            final ProgressDialog pdialog = new ProgressDialog(this);
//            pdialog.setCancelable(false);
//            pdialog.setMessage("Please wait...");
//            pdialog.show();
            showBeautifulDialog();

            int prescriptionId = is_finish;
            final HashMap<String, String> hashMap = map;
            hashMap.put("prescription_id", prescriptionId + "");
            hashMap.put("is_approved", "0");

            PostRequest.send(ProductsActivity.this, hashMap, new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {
                    try {
                        int success = response.getInt("success");

                        if (success == 1) {
                            hashMap.put("server_id", String.valueOf(response.getInt("last_inserted_id")));
                            transferHashMap(hashMap);
                        }
                    } catch (Exception e) {
                        Log.d("exception3", e + "");
                        Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                    }
                    letDialogSleep();
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    letDialogSleep();
                    Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        is_finish = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.shoppingcart);
        MenuItemCompat.setActionView(item, R.layout.count_badge_layout);
        RelativeLayout badgeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);

        number_of_notif = (TextView) badgeLayout.findViewById(R.id.number_of_notif);
        go_to_cart = (ImageButton) badgeLayout.findViewById(R.id.go_to_cart);
        go_to_cart.setOnClickListener(this);

        MenuItem change_branch = menu.findItem(R.id.change_branch);
        change_branch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(getBaseContext(), GoogleMapsActivity.class));
//        ProductsActivity.this.finish();
                return false;
            }
        });

        if (number_of_notif.getVisibility() == View.VISIBLE)
            number_of_notif.setText("12");

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                results_layout.setVisibility(View.GONE);
                spinner_categories.setSelection(0);
                adapter = new ProductsAdapter(ProductsActivity.this, R.layout.product_item, temp_products_items);
                listOfProducts.setAdapter(adapter);
                return true;
            }
        });

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, String> ss = (Map<String, String>) adapter.getItem(position);
        int product_id = Integer.parseInt(ss.get("product_id"));

        Intent intent = new Intent(this, SelectedProductActivity.class);
        intent.putExtra(SelectedProductActivity.PRODUCT_ID, product_id);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            results_layout.setVisibility(View.VISIBLE);
            products_items.clear();
            int ctr = 0;
            String query = intent.getStringExtra(SearchManager.QUERY);

            for (int x = 0; x < searchProducts.size(); x++) {
                for (Map.Entry<Integer, HashMap<String, String>> ee : searchProducts.get(x).entrySet()) {
                    HashMap<String, String> values = ee.getValue();

                    if (values.get("product_name").toLowerCase().contains(query.toLowerCase()) || values.get("generic_name").toLowerCase().contains(query.toLowerCase())) {
                        Map<String, String> details = temp_products_items.get(x);
                        products_items.add(details);
                        ctr += 1;
                    }
                }
            }
            adapter = new ProductsAdapter(ProductsActivity.this, R.layout.product_item, products_items);
            listOfProducts.setAdapter(adapter);
            noOfResults.setText(ctr + "");

            if (ctr == 0)
                Snackbar.make(root, "No results were found", Snackbar.LENGTH_SHORT).show();
        }
    }

    public static void showOverLay(Context context) {
        if (!oc.checkOverlay("Products", "check")) {
            final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.products_overlay);

            LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.productsLayout);
            layout.setAlpha((float) 0.8);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (oc.checkOverlay("Products", "insert"))
                        dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void toggleOnClick(View v) {
        int pos = Integer.parseInt(String.valueOf(v.getTag()));
        int product_id = Integer.parseInt(products_items.get(pos).get("product_id"));
        String prod_name = products_items.get(pos).get("name");

        if (((ToggleButton) v).isChecked()) {
            if (db.insertFaveProduct(SidebarActivity.getUserID(), product_id))
                Snackbar.make(root, prod_name + " is added to your favorites", Snackbar.LENGTH_SHORT).show();
            else
                Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
        } else {
            db.removeFavorite(SidebarActivity.getUserID(), product_id);
        }
    }

    void getAllBasketItems() {
        String url_raw = "get_basket_items&patient_id=" + SidebarActivity.getUserID() + "&table=baskets";

        ListOfPatientsRequest.getJSONobj(ProductsActivity.this, url_raw, "baskets", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray json_mysql = response.getJSONArray("baskets");

                        for (int x = 0; x < json_mysql.length(); x++) {
                            JSONObject obj = json_mysql.getJSONObject(x);

                            HashMap<String, String> map = new HashMap();
                            map.put("server_id", String.valueOf(obj.getInt("id")));
                            map.put("product_id", String.valueOf(obj.getInt("product_id")));
                            map.put("quantity", String.valueOf(obj.getInt("quantity")));
                            map.put("prescription_id", String.valueOf(obj.getInt("prescription_id")));
                            basket_items.add(map);
                        }
                    }
                } catch (Exception e) {
                    Log.d("exception4", e + "");
                    Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public static void transferHashMap(HashMap<String, String> map) {
        HashMap<String, String> hash = new HashMap();

        if (map.get("action").equals("update")) {
            for (int x = 0; x < basket_items.size(); x++) {
                if (map.get("id").equals(basket_items.get(x).get("server_id"))) {
                    hash.put("server_id", map.get("id"));
                    hash.put("product_id", basket_items.get(x).get("product_id"));
                    hash.put("quantity", map.get("quantity"));
                    hash.put("prescription_id", basket_items.get(x).get("prescription_id"));
                }
            }
        } else {
            hash.put("server_id", map.get("server_id"));
            hash.put("product_id", map.get("product_id"));
            hash.put("quantity", map.get("quantity"));
            hash.put("prescription_id", map.get("prescription_id"));
        }

        basket_items.add(hash);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_to_cart:
                startActivity(new Intent(this, ShoppingCartActivity.class));
                ProductsActivity.this.finish();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        no_products.setVisibility(View.GONE);
        listOfProducts.setVisibility(View.VISIBLE);
        final String category = categories.get(position);
        products_items.clear();
        searchProducts.clear();
        temp_products_items.clear();

//        final ProgressDialog progress1 = new ProgressDialog(this);
//        progress1.setMessage("Please wait...");
//        progress1.setCancelable(false);
//        progress1.show();

        showBeautifulDialog();

        ListOfPatientsRequest.getJSONobj(getBaseContext(), "get_products&branch_id=" + order_model.getBranch_id()+"&patient_id="+SidebarActivity.getUserID(), "products", new RespondListener<JSONObject>() {
                    @Override
                    public void getResult(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray json_array = response.getJSONArray("products");

                                for (int x = 0; x < json_array.length(); x++) {
                                    JSONObject obj = json_array.getJSONObject(x);

                                    HashMap<String, String> map = new HashMap();
                                    map.put("product_id", obj.getString("id"));
                                    map.put("subcategory_id", String.valueOf(obj.getInt("subcategory_id")));
                                    map.put("name", obj.getString("name"));
                                    map.put("generic_name", obj.getString("generic_name"));
                                    map.put("description", obj.getString("description"));
                                    map.put("prescription_required", String.valueOf(obj.getInt("prescription_required")));
                                    map.put("price", String.valueOf(obj.getInt("price")));
                                    map.put("unit", obj.getString("unit"));
                                    map.put("packing", obj.getString("packing"));
                                    map.put("qty_per_packing", String.valueOf(obj.getInt("qty_per_packing")));
                                    map.put("temp_basket_qty", "0");
                                    map.put("category_name", obj.getString("cat_name"));
                                    map.put("category_id", String.valueOf(obj.getInt("cat_id")));
                                    map.put("available_quantity", String.valueOf(obj.getInt("available_quantity")));
                                    map.put("in_cart", String.valueOf(obj.getInt("in_cart")));
                                    products_items.add(map);

                                    HashMap<Integer, HashMap<String, String>> hash = new HashMap();
                                    HashMap<String, String> temp = new HashMap();
                                    temp.put("product_name", map.get("name"));
                                    temp.put("generic_name", map.get("generic_name"));
                                    hash.put(obj.getInt("id"), temp);
                                    searchProducts.add(hash);
                                }
                                temp_products_items.addAll(products_items);

                                if (promo_id > 0) { //IF GIKAN SA PROMOFRAGMENT

                                } else { //IF GIKAN SA HOMETILEFRAGMENT
                                    ArrayList<Map<String, String>> newMap = new ArrayList();

                                    if (category.equals("Favorites")) {
                                        ArrayList<Integer> fave_IDs = db.getFavoritesByUserID(SidebarActivity.getUserID());

                                        if (fave_IDs.size() > 0) {
                                            for (int x = 0; x < fave_IDs.size(); x++) {
                                                String product_id = String.valueOf(fave_IDs.get(x));

                                                for (Map<String, String> map : products_items) {
                                                    if (map.get("product_id").equals(product_id))
                                                        newMap.add(map);
                                                }
                                            }
                                        } else {
                                            no_products.setVisibility(View.VISIBLE);
                                            listOfProducts.setVisibility(View.GONE);
                                        }
                                    } else if (category.equals("All")) {
                                        newMap.addAll(products_items);
                                    } else {
                                        for (Map<String, String> map : products_items) {
                                            if (map.containsValue(category))
                                                newMap.add(map);
                                        }

                                        if (newMap.size() == 0) {
                                            no_products.setVisibility(View.VISIBLE);
                                            listOfProducts.setVisibility(View.GONE);
                                        }
                                    }

                                    adapter = new ProductsAdapter(ProductsActivity.this, R.layout.product_item, newMap);
                                    listOfProducts.setAdapter(adapter);
                                    letDialogSleep();
                                }
                            }
                        } catch (Exception e) {
                            Log.d("exception1", e + "");
                            Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                        }
                        letDialogSleep();
                    }
                }, new ErrorListener<VolleyError>() {
                    public void getError(VolleyError error) {
                        letDialogSleep();
                        Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getProductCategories() {
        categories.add("All");
        categories.add("Favorites");

        ListOfPatientsRequest.getJSONobj(getBaseContext(), "get_product_categories", "product_categories", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray json_array = response.getJSONArray("product_categories");

                        for (int x = 0; x < json_array.length(); x++) {
                            JSONObject obj = json_array.getJSONObject(x);
                            categories.add(obj.getString("name"));
                        }
                    }
                } catch (Exception e) {
                    Log.d("exception2", e + "");
                    Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                }
                spinner_adapter = new ArrayAdapter(ProductsActivity.this, R.layout.spinner_toolbar_item, categories);
                spinner_categories.setAdapter(spinner_adapter);
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
