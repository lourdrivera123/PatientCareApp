package com.example.zem.patientcareapp.CheckoutModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.R;

/**
 * Created by zemskie on 12/1/2015.
 */
public class ContactForDelivery extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    Button next_btn;
    Toolbar myToolBar;
    OrderModel order_model;
    Intent get_intent;
    SeekBar blood_seeker;
    TextView stepping_stone;
    RadioButton to_me, to_others;
    LinearLayout hideLayout;
    Patient patient;
    PatientController pc;
    EditText recipientContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_for_delivery);

        next_btn = (Button) findViewById(R.id.next_btn);
        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        blood_seeker = (SeekBar) findViewById(R.id.blood_seeker);
        stepping_stone = (TextView) findViewById(R.id.stepping_stone);
        to_me = (RadioButton) findViewById(R.id.to_me);
        to_others = (RadioButton) findViewById(R.id.to_others);
        hideLayout = (LinearLayout) findViewById(R.id.hideLayout);
        recipientContactNumber = (EditText) findViewById(R.id.recipientContactNumber);

        pc = new PatientController(this);
        patient = pc.getCurrentLoggedInPatient();

        get_intent = getIntent();
        Bundle bundle= get_intent.getExtras();
        order_model = (OrderModel) bundle.getSerializable("order_model");

        if(order_model.getMode_of_delivery().equals("delivery")){
            blood_seeker.setProgress(80);
            stepping_stone.setText("Step 4/5");
        } else {
            blood_seeker.setProgress(75);
            stepping_stone.setText("Step 3/4");
        }



        blood_seeker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        next_btn.setOnClickListener(this);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contact Number");
        myToolBar.setNavigationIcon(R.drawable.ic_back);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PaymentMethod.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order_model", order_model);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(to_me.isChecked()) {
            hideLayout.setVisibility(View.GONE);
            order_model.setRecipient_contactNumber(patient.getMobile_no() + "/"+patient.getTel_no());
        } else if( to_others.isChecked() ){
            order_model.setRecipient_contactNumber(recipientContactNumber.getText().toString());
            hideLayout.setVisibility(View.VISIBLE);
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
}