package com.example.zem.patientcareapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zem.patientcareapp.CheckoutModule.SummaryAdapter;
import com.example.zem.patientcareapp.Controllers.OrderController;
import com.example.zem.patientcareapp.Controllers.OrderDetailController;
import com.example.zem.patientcareapp.Customizations.NonScrollListView;
import com.example.zem.patientcareapp.Fragment.OrdersFragment;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.util.Log.d;

/**
 * Created by zemskie on 12/11/2015.
 */
public class OrderDetailsActivity extends AppCompatActivity {

    Toolbar myToolBar;
    NonScrollListView order_summary;
    ArrayList<HashMap<String, String>> items;
    ArrayList<HashMap<String, String>> order_information;
    OrderDetailController odc;
    OrderController oc;
    int order_id;
    TextView order_id_txtview, date_and_time, order_receiving_option, address_option, recipient_option, recipient_contact_number, payment_option, order_status, subtotal_value, coupon_discounts_value, points_discount_value, total_value, delivery_charge_value;
    LinearLayout subtotal_block, coupon_discounts_block, points_discount_block, delivery_charge_block;
    String promo_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        odc = new OrderDetailController(this);
        oc = new OrderController(this);

        order_id = getIntent().getIntExtra("order_id", 0);

        if (order_id == 0)
            exitActivity();

        setContentView(R.layout.order_details_layout);

        subtotal_block = (LinearLayout) findViewById(R.id.subtotal_block);
        coupon_discounts_block = (LinearLayout) findViewById(R.id.coupon_discounts_block);
        points_discount_block = (LinearLayout) findViewById(R.id.points_discount_block);
        date_and_time = (TextView) findViewById(R.id.date_and_time);
        order_id_txtview = (TextView) findViewById(R.id.order_id_txtview);
        order_receiving_option = (TextView) findViewById(R.id.order_receiving_option);
        address_option = (TextView) findViewById(R.id.address_option);
        recipient_option = (TextView) findViewById(R.id.recipient_option);
        recipient_contact_number = (TextView) findViewById(R.id.recipient_contact_number);
        payment_option = (TextView) findViewById(R.id.payment_option);
        order_status = (TextView) findViewById(R.id.order_status);
        subtotal_value = (TextView) findViewById(R.id.subtotal_value);
        coupon_discounts_value = (TextView) findViewById(R.id.coupon_discounts_value);
        points_discount_value = (TextView) findViewById(R.id.points_discount_value);
        total_value = (TextView) findViewById(R.id.total_value);
        delivery_charge_block = (LinearLayout) findViewById(R.id.delivery_charge_block);
        delivery_charge_value = (TextView) findViewById(R.id.delivery_charge_value);

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        order_summary = (NonScrollListView) findViewById(R.id.order_summary);

        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        myToolBar.setNavigationIcon(R.drawable.ic_back);

        d("order_od_id", order_id + "");

        order_information = oc.getOrder(order_id);
        items = odc.getOrderDetailsFromOrder(order_id);

        double pdc = Double.parseDouble(order_information.get(0).get("points_discount"));
        double cdc = Double.parseDouble(order_information.get(0).get("coupon_discount"));
        double dc = Double.parseDouble(order_information.get(0).get("delivery_charge"));
        promo_type = order_information.get(0).get("promo_type");

        points_discount_value.setText(""+pdc);
        coupon_discounts_value.setText(""+cdc);
        subtotal_value.setText(order_information.get(0).get("subtotal"));
        total_value.setText(order_information.get(0).get("total"));
        order_id_txtview.setText(order_information.get(0).get("order_id"));
        order_receiving_option.setText(order_information.get(0).get("order_receiving_option"));
        address_option.setText(order_information.get(0).get("recipient_address"));
        recipient_option.setText(order_information.get(0).get("recipient_name"));
        recipient_contact_number.setText(order_information.get(0).get("recipient_contact_number"));
        payment_option.setText(order_information.get(0).get("payment_method"));
        order_status.setText(order_information.get(0).get("order_status"));


        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = formatter.parse(order_information.get(0).get("orderred_on"));

            SimpleDateFormat fd = new SimpleDateFormat("MMM d, yyyy - h:mm a");
            String formatted_date = fd.format(date1);
            date_and_time.setText(formatted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (pdc > 0) {
            subtotal_block.setVisibility(View.VISIBLE);
            points_discount_block.setVisibility(View.VISIBLE);
        }

        if (cdc > 0) {
            subtotal_block.setVisibility(View.VISIBLE);
            coupon_discounts_block.setVisibility(View.VISIBLE);
        }

        if(dc > 0){
            subtotal_block.setVisibility(View.VISIBLE);
            delivery_charge_block.setVisibility(View.VISIBLE);
            delivery_charge_value.setText(order_information.get(0).get("delivery_charge"));
        }

        if(promo_type.equals("free_delivery")){
            delivery_charge_block.setVisibility(View.VISIBLE);
            delivery_charge_value.setText("Free");
        }

        SummaryAdapter adapter = new SummaryAdapter(OrderDetailsActivity.this, items);
        order_summary.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void exitActivity() {
        Intent order_intent = new Intent(getBaseContext(), SidebarActivity.class);
        order_intent.putExtra("select", 5);
        startActivity(order_intent);
        this.finish();
    }
}
