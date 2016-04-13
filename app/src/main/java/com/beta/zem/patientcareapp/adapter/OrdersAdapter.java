package com.beta.zem.patientcareapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Controllers.OrderController;
import com.beta.zem.patientcareapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by zemskie on 12/9/2015.
 */
public class OrdersAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> hashOfOrders;

    public OrdersAdapter(Context context, ArrayList<HashMap<String, String>> hashOfOrders){
        this.context = context;
        this.hashOfOrders = hashOfOrders;
    }

    @Override
    public int getCount() {
        return hashOfOrders.size();
    }

    @Override
    public Object getItem(int position) {
        return hashOfOrders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_row_order_item, null);
        }

        TextView num_of_items_and_to_whom = (TextView) convertView.findViewById(R.id.num_of_items_and_to_whom);
        TextView order_id = (TextView) convertView.findViewById(R.id.order_id);
        TextView product_qty_price = (TextView) convertView.findViewById(R.id.product_qty_price);
        TextView ordered_on = (TextView) convertView.findViewById(R.id.ordered_on);
        TextView status = (TextView) convertView.findViewById(R.id.status);

        num_of_items_and_to_whom.setText(hashOfOrders.get(position).get("num_of_items") + " Item(s) for "+hashOfOrders.get(position).get("recipient_name"));
        order_id.setText("Order #"+hashOfOrders.get(position).get("order_id"));
        product_qty_price.setText("Total: \u20B1"+ hashOfOrders.get(position).get("total"));
        status.setText(hashOfOrders.get(position).get("order_status"));

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = null;
            date1 = formatter.parse(hashOfOrders.get(position).get("date_ordered"));


//        format to readable ones
            SimpleDateFormat fd = new SimpleDateFormat("MMM d, yyyy - h:mm a");
            String formatted_date = fd.format(date1);
            ordered_on.setText("Orderred on " + formatted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
