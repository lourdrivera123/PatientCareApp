package com.beta.zem.patientcareapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.beta.zem.patientcareapp.Activities.OrderDetailsActivity;
import com.beta.zem.patientcareapp.Controllers.BillingController;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Controllers.OrderController;
import com.beta.zem.patientcareapp.Controllers.OrderDetailController;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.adapter.OrdersAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 10/2/2015.
 */
public class OrdersFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView lv_items;

    OrdersAdapter adapter;
    ArrayList<HashMap<String, String>> order_items;
    DbHelper dbHelper;
    OrderController oc;
    OrderDetailController odc;
    BillingController blc;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.orders_layout, container, false);

        lv_items = (ListView) rootView.findViewById(R.id.lv_items);

        dbHelper = new DbHelper(getActivity());
        oc = new OrderController(getActivity());
        odc = new OrderDetailController(getActivity());
        blc = new BillingController(getActivity());

        order_items = oc.getAllOrderItems();

        adapter = new OrdersAdapter(getActivity(), order_items);

        lv_items.setAdapter(adapter);
        lv_items.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int order_id = Integer.parseInt(order_items.get(position).get("order_id"));

        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra("order_id", order_id);
        startActivity(intent);
    }
}
