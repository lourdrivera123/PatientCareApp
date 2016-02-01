package com.example.zem.patientcareapp.CheckoutModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.zem.patientcareapp.CheckoutModule.AddressForDelivery;
import com.example.zem.patientcareapp.CheckoutModule.PromosDiscounts;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

/**
 * Created by Zem on 11/18/2015.
 */
public class DeliverPickupOption extends AppCompatActivity implements View.OnClickListener {

    Toolbar myToolBar;
    LinearLayout delivery_btn, pickup_btn;
    SeekBar blood_seeker;
    TextView stepping_stone;
    OrderModel order_model;
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_and_pickup_option_layout);

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        delivery_btn = (LinearLayout) findViewById(R.id.delivery_btn);
        pickup_btn = (LinearLayout) findViewById(R.id.pickup_btn);
        blood_seeker = (SeekBar) findViewById(R.id.blood_seeker);
        stepping_stone = (TextView) findViewById(R.id.stepping_stone);

            order_model = (OrderModel) getIntent().getSerializableExtra("order_model");
            order_model.setAction("update");

//        if (getIntent().getSerializableExtra("order_model") != null) {
//            order_model = (OrderModel) getIntent().getSerializableExtra("order_model");
//            order_model.setAction("update");
//        } else {
//            order_model = new OrderModel();
//            order_model.setAction("insert");
//        }
        order_model.setPatient_id(SidebarActivity.getUserID());


        stepping_stone.setText("Step 1");
        blood_seeker.setProgress(0);
        blood_seeker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pickup and Delivery Option");
        myToolBar.setNavigationIcon(R.drawable.ic_back);

        delivery_btn.setOnClickListener(this);
        pickup_btn.setOnClickListener(this);
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
            case R.id.delivery_btn:
                order_model.setMode_of_delivery("delivery");
                intent = new Intent(this, AddressForDelivery.class);
                go_on();
                break;

            case R.id.pickup_btn:
                intent = new Intent(this, RecipientForDelivery.class);
                order_model.setMode_of_delivery("pickup");
                go_on();
                break;

            default:
                break;
        }
    }

    public void go_on() {
        intent.putExtra("order_model", order_model);
        startActivity(intent);
        this.finish();
    }
}
