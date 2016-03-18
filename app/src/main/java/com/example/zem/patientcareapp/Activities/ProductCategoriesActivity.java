package com.example.zem.patientcareapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.example.zem.patientcareapp.adapter.ProductCategoriesAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductCategoriesActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    Toolbar main_toolbar;
    ImageButton search_product, go_to_cart;
    ListView list_of_categories;
    TextView number_of_notif;
    LinearLayout root;

    ArrayList<HashMap<String, String>> categories;

    ProductCategoriesAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_categories);

        search_product = (ImageButton) findViewById(R.id.search_product);
        list_of_categories = (ListView) findViewById(R.id.list_of_categories);
        main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        root = (LinearLayout) findViewById(R.id.root);

        setSupportActionBar(main_toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shop");

        categories = new ArrayList<>();

        getProductCategories();

        search_product.setOnClickListener(this);
        list_of_categories.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        getAllBasketItems();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.go_to_cart_menu, menu);

        MenuItem item = menu.findItem(R.id.go_to_cart);
        MenuItemCompat.setActionView(item, R.layout.count_badge_layout);
        RelativeLayout badgeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);

        number_of_notif = (TextView) badgeLayout.findViewById(R.id.number_of_notif);
        go_to_cart = (ImageButton) badgeLayout.findViewById(R.id.go_to_cart);
        go_to_cart.setOnClickListener(this);

        return super.onCreateOptionsMenu(menu);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_to_cart:
                startActivity(new Intent(this, ShoppingCartActivity.class));
                break;

            case R.id.search_product:
                startActivity(new Intent(this, SearchAllProductsActivity.class));
                break;
        }
    }

    void getAllBasketItems() {
        String url_raw = "get_basket_items&patient_id=" + SidebarActivity.getUserID() + "&table=baskets";

        ListOfPatientsRequest.getJSONobj(url_raw, "baskets", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");
                    int count = 0;

                    if (success == 1) {
                        if (response.getBoolean("has_contents")) {
                            JSONArray json_mysql = response.getJSONArray("baskets");

                            for (int x = 0; x < json_mysql.length(); x++)
                                count += 1;

                            if (count > 0) {
                                number_of_notif.setVisibility(View.VISIBLE);
                                number_of_notif.setText(String.valueOf(count));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("prod_categories1", e + "");
                    Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                Log.d("prod_categories2", e + "");
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int category_id = Integer.parseInt(categories.get(position).get("category_id"));

        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra("category_id", category_id);
        intent.putExtra("promo_id", 0);
        startActivity(intent);
    }

    public void getProductCategories() {
        HashMap<String, String> map = new HashMap<>();
        map.put("category_id", "0");
        map.put("category_name", "Favorites");
        map.put("background_color", "#BCC6CC");
        map.put("background_icon", "");
        categories.add(map);

        final AppCompatDialog pDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress_stuffing);
        builder.setCancelable(false);
        pDialog = builder.create();
        pDialog.show();

        ListOfPatientsRequest.getJSONobj("get_product_categories", "product_categories", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        if(response.getBoolean("has_contents")){
                            JSONArray json_array = response.getJSONArray("product_categories");

                            for (int x = 0; x < json_array.length(); x++) {
                                JSONObject obj = json_array.getJSONObject(x);
                                HashMap<String, String> new_map = new HashMap<>();
                                new_map.put("category_id", obj.getString("id"));
                                new_map.put("category_name", obj.getString("name"));
                                new_map.put("background_color", obj.getString("background_color"));
                                new_map.put("background_icon", "");
                                categories.add(new_map);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("prod_categories3", e + "");
                    Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                }
                adapter = new ProductCategoriesAdapter(ProductCategoriesActivity.this, R.layout.item_product_categories, categories);
                list_of_categories.setAdapter(adapter);
                pDialog.dismiss();
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                pDialog.dismiss();
                Log.d("prod_categories4", error + "");
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
