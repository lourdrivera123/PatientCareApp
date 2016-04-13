package com.beta.zem.patientcareapp.Activities;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;
import com.beta.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.beta.zem.patientcareapp.adapter.SearchHistoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAllProductsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    Toolbar search_toolbar;
    ListView list_of_results, list_of_search_history;
    TextView no_results_header, clear_history;
    LinearLayout root, no_results, empty_result, history_header;

    ArrayList<HashMap<String, String>> search_results;
    ArrayList<String> list_of_history;
    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all_products);

        search_toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        list_of_results = (ListView) findViewById(R.id.list_of_results);
        list_of_search_history = (ListView) findViewById(R.id.list_of_search_history);
        root = (LinearLayout) findViewById(R.id.root);
        no_results_header = (TextView) findViewById(R.id.no_results_header);
        no_results = (LinearLayout) findViewById(R.id.no_results);
        empty_result = (LinearLayout) findViewById(R.id.empty_result);
        history_header = (LinearLayout) findViewById(R.id.history_header);
        clear_history = (TextView) findViewById(R.id.clear_history);

        db = new DbHelper(this);

        checkHistory();

        setSupportActionBar(search_toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        clear_history.setOnClickListener(this);
        list_of_results.setOnItemClickListener(this);
        list_of_search_history.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_only_options, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                list_of_results.setVisibility(View.VISIBLE);
                history_header.setVisibility(View.GONE);
                empty_result.setVisibility(View.GONE);
                list_of_search_history.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                list_of_results.setVisibility(View.GONE);
                history_header.setVisibility(View.VISIBLE);
                no_results.setVisibility(View.GONE);
                no_results_header.setVisibility(View.GONE);

                checkHistory();
                return true;
            }
        });
        return true;
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
            final String query = intent.getStringExtra(SearchManager.QUERY);
            searchForKeyword(query);
        }
    }

    void searchForKeyword(final String query) {
        search_results = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchAllProductsActivity.this);
        builder.setView(R.layout.progress_stuffing);
        builder.setCancelable(false);
        final AppCompatDialog pDialog = builder.create();
        pDialog.show();

        ListOfPatientsRequest.getJSONobj("get_searched_products&keyword=" + query, "products", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    if (response.getInt("success") == 1) {
                        no_results_header.setVisibility(View.GONE);
                        no_results.setVisibility(View.GONE);
                        list_of_results.setVisibility(View.VISIBLE);

                        JSONArray json_array_mysql = response.getJSONArray("products");

                        for (int x = 0; x < json_array_mysql.length(); x++) {
                            JSONObject obj = json_array_mysql.getJSONObject(x);
                            HashMap<String, String> map = new HashMap<>();

                            map.put("product_id", obj.getString("id"));
                            map.put("product_name", obj.getString("name"));
                            map.put("product_price", obj.getString("price"));
                            search_results.add(map);
                        }

                        list_of_results.setAdapter(new ListViewAdapter(SearchAllProductsActivity.this, search_results));
                        db.insertSearchHistory(SidebarActivity.getUserID(), query);
                    } else {
                        no_results.setVisibility(View.VISIBLE);
                        no_results_header.setVisibility(View.VISIBLE);
                        list_of_results.setVisibility(View.GONE);
                        no_results_header.setText("No results were found matching your search for \"" + query + "\" in our items.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.dismiss();
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                pDialog.dismiss();
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_history:
                if (db.deleteHistoryByUserID(SidebarActivity.getUserID()))
                    empty_result.setVisibility(View.VISIBLE);
                break;
        }
    }

    void checkHistory() {
        list_of_history = db.getAllHistoryByUser(SidebarActivity.getUserID());

        if (list_of_history.size() > 0) {
            list_of_search_history.setVisibility(View.VISIBLE);
            empty_result.setVisibility(View.GONE);

            list_of_search_history.setAdapter(new SearchHistoryAdapter(this, list_of_history));
        } else {
            empty_result.setVisibility(View.VISIBLE);
            list_of_search_history.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.list_of_search_history:
                list_of_search_history.setVisibility(View.GONE);
                String item = String.valueOf(list_of_history.get(position));
                searchForKeyword(item);
                break;

            case R.id.list_of_results:
                int product_id = Integer.parseInt(search_results.get(position).get("product_id"));

                Intent intent = new Intent(this, SelectedProductActivity.class);
                intent.putExtra("productID", product_id);
                startActivity(intent);
                break;
        }
    }

    private class ListViewAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> objects;
        Context context;

        TextView product_name, price;

        private ListViewAdapter(Context context, ArrayList<HashMap<String, String>> objects) {
            this.objects = objects;
            this.context = context;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);

            product_name = (TextView) convertView.findViewById(R.id.product_name);
            price = (TextView) convertView.findViewById(R.id.price);

            product_name.setText(objects.get(position).get("product_name"));
            price.setText("Php " + objects.get(position).get("product_price"));

            return convertView;
        }
    }
}
