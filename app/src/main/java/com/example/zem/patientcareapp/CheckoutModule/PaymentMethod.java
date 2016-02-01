package com.example.zem.patientcareapp.CheckoutModule;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Controllers.OrderPreferenceController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONObject;

import java.util.HashMap;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.make;
import static android.util.Log.d;
import static com.example.zem.patientcareapp.Network.CustomPostRequest.send;
import static java.lang.System.out;

/**
 * Created by Zem on 11/18/2015.
 */
public class PaymentMethod extends AppCompatActivity implements View.OnClickListener {

    Toolbar myToolBar;
    SeekBar blood_seeker;
    TextView stepping_stone;
    Intent get_intent;
    OrderModel order_model;
    LinearLayout cash, visa_or_mastercard, paypal, root;
    Intent intent;
    String payment_method;
    OrderPreferenceController opc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_method_layout);

        get_intent = getIntent();
//        Bundle bundle= get_intent.getExtras();

        order_model = (OrderModel) get_intent.getSerializableExtra("order_model");

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        blood_seeker = (SeekBar) findViewById(R.id.blood_seeker);
        stepping_stone = (TextView) findViewById(R.id.stepping_stone);
        cash = (LinearLayout) findViewById(R.id.cash);
        visa_or_mastercard = (LinearLayout) findViewById(R.id.visa_or_mastercard);
        paypal = (LinearLayout) findViewById(R.id.paypal);
        root = (LinearLayout) findViewById(R.id.root);

        opc = new OrderPreferenceController(this);


        if (order_model.getMode_of_delivery().equals("delivery")) {
            stepping_stone.setText("Step 4/4");
            blood_seeker.setProgress(100);
        } else {
            stepping_stone.setText("Step 3/3");
            blood_seeker.setProgress(100);
        }

        blood_seeker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        cash.setOnClickListener(this);
        visa_or_mastercard.setOnClickListener(this);
        paypal.setOnClickListener(this);

        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment Method");
        myToolBar.setNavigationIcon(R.drawable.ic_back);
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
            case R.id.cash:
//                order_model.setPayment_method("cash");
                payment_method = "cash_on_delivery";
                saveSelectedBranchOnline();
                break;
            case R.id.visa_or_mastercard:
                payment_method = "visa_or_mastercard";
//                order_model.setPayment_method("visa_or_mastercard");
                saveSelectedBranchOnline();
                break;
            case R.id.paypal:
                payment_method = "paypal";
//                order_model.setPayment_method("order_model");
                saveSelectedBranchOnline();
                break;
            default:
                break;
        }
    }

//    public void ok_lets_go() {
//        order_model.setPayment_method(payment_method);
//        if (opc.savePreference(order_model)) {
//            intent = new Intent(this, PromosDiscounts.class);
//            intent.putExtra("order_model", order_model);
//            startActivity(intent);
//            this.finish();
//        } else {
//            Log.d("ot", "what  the fuck is wrong ?");
//        }
//    }

    void saveSelectedBranchOnline(){
        order_model.setPayment_method(payment_method);
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_BRANCH_ID, String.valueOf(order_model.getBranch_id()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_PATIENT_ID, String.valueOf(SidebarActivity.getUserID()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_RECIPIENT_NAME, String.valueOf(order_model.getRecipient_name()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_RECIPIENT_ADDRESS, String.valueOf(order_model.getRecipient_address()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_RECIPIENT_NUMBER, String.valueOf(order_model.getRecipient_contactNumber()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_MODE_OF_DELIVERY, String.valueOf(order_model.getMode_of_delivery()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_PAYMENT_METHOD, String.valueOf(order_model.getPayment_method()));
        hashMap.put("action", "update");

        send("saveBranchPreference", hashMap, new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    d("orderprefresponse", response + "");
                    if(response.getBoolean("success")){
                        order_model.setServer_id(response.getInt("server_id"));
                        if (opc.savePreference(order_model)) {
                            intent = new Intent(PaymentMethod.this, PromosDiscounts.class);
                            intent.putExtra("order_model", order_model);
                            startActivity(intent);
                            PaymentMethod.this.finish();
                        } else {
                            Log.d("ot", "what  the fuck is wrong ?");
                        }
                    } else {
                        make(root, "Unable to save branch", LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    out.println("<saveBranchPreference> request error" + e);
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError error) {
                out.println("src: <saveBranchPreference>: " + error.toString());
            }
        });
    }
}
