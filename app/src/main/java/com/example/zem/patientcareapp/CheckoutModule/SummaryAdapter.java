package com.example.zem.patientcareapp.CheckoutModule;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class SummaryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> hashOfOrders;
    Helpers helper;

    public SummaryAdapter(Context context, ArrayList<HashMap<String, String>> hashOfOrders){
        this.context = context;
        this.hashOfOrders = hashOfOrders;
        helper = new Helpers();
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
            convertView = mInflater.inflate(R.layout.basket_summary_item, null);
        }

        TextView medicine_name = (TextView) convertView.findViewById(R.id.medicine_name);
        TextView qty_price = (TextView) convertView.findViewById(R.id.qty_price);
        TextView item_subtotal = (TextView) convertView.findViewById(R.id.item_subtotal);
        TextView item_subtotal_discounted = (TextView) convertView.findViewById(R.id.item_subtotal_discounted);

        medicine_name.setText(hashOfOrders.get(position).get("name"));
        qty_price.setText(helper.money_format(Double.parseDouble(hashOfOrders.get(position).get("price"))) + " x " +  hashOfOrders.get(position).get("quantity"));
        item_subtotal.setText(helper.money_format(Double.parseDouble(hashOfOrders.get(position).get("item_subtotal"))));
        String promo_type= hashOfOrders.get(position).get("promo_type");

        double item_subtotal_value = Double.parseDouble(hashOfOrders.get(position).get("item_subtotal"));
        double peso_discount = Double.parseDouble(hashOfOrders.get(position).get("peso_discount"));
        double percentage_discount = Double.parseDouble(hashOfOrders.get(position).get("percentage_discount"));
        int promo_free_product_qty = Integer.parseInt(hashOfOrders.get(position).get("promo_free_product_qty"));

        if(promo_type.equals("peso_discount") ){
            item_subtotal_discounted.setVisibility(View.VISIBLE);
            item_subtotal_discounted.setText(new StringBuilder().append(helper.money_format(item_subtotal_value - peso_discount)).toString());
            item_subtotal.setPaintFlags(item_subtotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else if(promo_type.equals("percentage_discount")) {
            item_subtotal_discounted.setVisibility(View.VISIBLE);
            item_subtotal_discounted.setText(new StringBuilder().append(helper.money_format(item_subtotal_value - percentage_discount)+""));
            item_subtotal.setPaintFlags(item_subtotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else if(promo_type.equals("free_gift")) {
            item_subtotal_discounted.setVisibility(View.VISIBLE);
            item_subtotal_discounted.setText(promo_free_product_qty+" free item/s");
        }

        return convertView;
    }
}