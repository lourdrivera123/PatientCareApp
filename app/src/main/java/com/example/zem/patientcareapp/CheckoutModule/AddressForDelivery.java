package com.example.zem.patientcareapp.CheckoutModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.R;

/**
 * Created by Zem on 11/18/2015.
 */
public class AddressForDelivery extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    Toolbar myToolBar;
    Button next_btn;
    RadioButton to_me, to_others;
    LinearLayout hideLayout;
    SeekBar blood_seeker;
    TextView stepping_stone;
    Intent get_intent;
    OrderModel order_model;
    DbHelper db;
    PatientController pc;
    Patient patient;
    EditText recipientAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_for_delivery);

        get_intent = getIntent();
        Bundle bundle = get_intent.getExtras();

        db = new DbHelper(this);
        pc = new PatientController(this);
        patient = pc.getCurrentLoggedInPatient();

        order_model = (OrderModel) get_intent.getSerializableExtra("order_model");

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        next_btn = (Button) findViewById(R.id.next_btn);
        to_me = (RadioButton) findViewById(R.id.to_me);
        to_others = (RadioButton) findViewById(R.id.to_others);
        hideLayout = (LinearLayout) findViewById(R.id.hideLayout);
        recipientAddress = (EditText) findViewById(R.id.recipientAddress);

        blood_seeker = (SeekBar) findViewById(R.id.blood_seeker);
        stepping_stone = (TextView) findViewById(R.id.stepping_stone);

        stepping_stone.setText("Step 2/4");
        blood_seeker.setProgress(40);

        to_me.setText("Use my own address \n (" + patient.getComplete_address() + ")");

        blood_seeker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Address for delivery");
        myToolBar.setNavigationIcon(R.drawable.ic_back);

        to_me.setOnCheckedChangeListener(this);
        to_others.setOnCheckedChangeListener(this);
        next_btn.setOnClickListener(this);

        hideLayout.setVisibility(View.GONE);

        if (!order_model.getRecipient_address().equals("") && !order_model.getRecipient_address().equals(patient.getComplete_address())) {
            to_others.setChecked(true);
            recipientAddress.setText(order_model.getRecipient_address());
        }
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (to_me.isChecked()) {
            hideLayout.setVisibility(View.GONE);
        } else if (to_others.isChecked()) {
            hideLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (to_me.isChecked()) {
            order_model.setRecipient_address(patient.getComplete_address());
        } else if (to_others.isChecked()) {
            order_model.setRecipient_address(recipientAddress.getText().toString());
        }

        Intent recipientfordelivery_intent = new Intent(this, RecipientForDelivery.class);
        recipientfordelivery_intent.putExtra("order_model", order_model);
        startActivity(recipientfordelivery_intent);
        this.finish();
    }
}
