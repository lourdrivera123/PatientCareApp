package com.example.zem.patientcareapp.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Activities.ProductsActivity;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PromoFragment extends Fragment implements TextWatcher {
    EditText search;
    ListView list_of_promos;
    LinearLayout root;
    TextView no_promos;

    ArrayList<HashMap<String, String>> map_promos = new ArrayList();

    ListViewAdapter adapter;

    String[] months = {
            "Jan.", "Feb.", "Mar.", "Apr.", "May.", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_promos, container, false);

        search = (EditText) rootView.findViewById(R.id.search);
        list_of_promos = (ListView) rootView.findViewById(R.id.list_of_promos);
        root = (LinearLayout) rootView.findViewById(R.id.root);
        no_promos = (TextView) rootView.findViewById(R.id.no_promos);

        search.addTextChangedListener(this);

        final ProgressDialog pdialog = new ProgressDialog(getActivity());
        pdialog.setMessage("Please wait...");
        pdialog.show();

        ListOfPatientsRequest.getJSONobj(getActivity(), "get_nocode_promos", "promos", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        no_promos.setVisibility(View.GONE);
                        list_of_promos.setVisibility(View.VISIBLE);
                        JSONArray json_mysql = response.getJSONArray("promos");

                        for (int x = 0; x < json_mysql.length(); x++) {
                            JSONObject obj = json_mysql.getJSONObject(x);
                            HashMap<String, String> map = new HashMap();

                            map.put("promo_id", obj.getString("pr_promo_id"));
                            map.put("promo_title", obj.getString("long_title"));
                            map.put("applicability", obj.getString("product_applicability"));
                            map.put("pt_minimum_purchase", obj.getString("minimum_purchase_amount"));
                            map.put("pt_free_delivery", obj.getString("pr_free_delivery"));
                            map.put("pt_percentage", obj.getString("pr_percentage"));
                            map.put("pt_peso", obj.getString("pr_peso"));
                            map.put("start_date", obj.getString("start_date"));
                            map.put("end_date", obj.getString("end_date"));
                            map_promos.add(map);
                        }
                    } else {
                        no_promos.setVisibility(View.VISIBLE);
                        list_of_promos.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Snackbar.make(root, e + "", Snackbar.LENGTH_SHORT).show();
                }
                adapter = new ListViewAdapter(getActivity(), R.layout.item_promo_products, map_promos);
                list_of_promos.setAdapter(adapter);
                pdialog.dismiss();
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                pdialog.dismiss();
                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class ListViewAdapter extends ArrayAdapter {
        LayoutInflater inflater;
        TextView title, applicability, duration, view_products, promos_per_transaction;

        public ListViewAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects) {
            super(context, resource, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item_promo_products, parent, false);

            title = (TextView) view.findViewById(R.id.title);
            applicability = (TextView) view.findViewById(R.id.applicability);
            duration = (TextView) view.findViewById(R.id.duration);
            view_products = (TextView) view.findViewById(R.id.view_products);
            promos_per_transaction = (TextView) view.findViewById(R.id.promos_per_transaction);

            if (map_promos.get(position).get("applicability").equals("SPECIFIC_PRODUCTS"))
                applicability.setText("on Selected products");
            else {
                applicability.setText("during Checkout");
                double pt_min_purchase = Double.parseDouble(map_promos.get(position).get("pt_minimum_purchase"));

                if (pt_min_purchase > 0) {
                    promos_per_transaction.setVisibility(View.VISIBLE);
                    String pt_free_delivery = map_promos.get(position).get("pt_free_delivery");
                    int pt_percentage = Integer.parseInt(map_promos.get(position).get("pt_percentage"));
                    double pt_peso = Double.parseDouble(map_promos.get(position).get("pt_peso"));
                    String final_promo = "";

                    if (pt_percentage > 0)
                        final_promo = "*" + pt_percentage + "% off on your entire purchase for a minimum order amount of Php " + pt_min_purchase;
                    else if (pt_peso > 0)
                        final_promo = "*Php " + pt_peso + " off on your entire purchase for a minimum order amount of Php " + pt_min_purchase;

                    if (final_promo.equals("")) {
                        if (pt_free_delivery.equals("1"))
                            final_promo = "*FREE DELIVERY for a minimum order amount of Php " + pt_min_purchase;
                    } else {
                        if (pt_free_delivery.equals("1"))
                            final_promo = new StringBuilder(final_promo).insert(1, "FREE DELIVERY, ").toString();
                    }
                    promos_per_transaction.setText(final_promo);
                }
            }

            int start_date_month = Integer.parseInt(map_promos.get(position).get("start_date").substring(5, 7)) - 1;
            String start_date = months[start_date_month] + " " + map_promos.get(position).get("start_date").substring(8);

            int end_date_month = Integer.parseInt(map_promos.get(position).get("end_date").substring(5, 7)) - 1;
            String end_date = months[end_date_month] + " " + map_promos.get(position).get("end_date").substring(8);

            title.setText(map_promos.get(position).get("promo_title"));
            duration.setText(start_date + " - " + end_date);

            view_products.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int promo_id = Integer.parseInt(map_promos.get(position).get("promo_id"));
                    Intent intent = new Intent(getActivity(), ProductsActivity.class);
                    intent.putExtra("promo_id", promo_id);
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}
