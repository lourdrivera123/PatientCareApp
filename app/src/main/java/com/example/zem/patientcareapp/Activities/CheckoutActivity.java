package com.example.zem.patientcareapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.zem.patientcareapp.R;

/**
 * Created by User PC on 9/21/2015.
 */

public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener {
//    LinearLayout hideLayout;
//    TextView customerName, customerContactNumber, customerAddress;
//    EditText recipientName, recipientAddress, recipientContactNumber;
//    RadioButton pickup, deliver, paypal, visa_or_mastercard, cod;
//    Spinner listOfECEBranches;
//    CheckBox check;
//    Button proceed;

//    String senderName, senderAddress, senderContactNumber, receiverName, receiverAddress, receiverContactNumber,
//            modeOfDelivery, eceBranch, payment_option;
//    String ptnt_completeAddress, ptnt_fullname, ptnt_contactNumber;
//    ArrayAdapter adapter;
//
//    ArrayList<String> arrayOfECEBranches;
//    ArrayList<HashMap<String, String>> getBranchesFromDB;
//
//    DbHelper db;
//    Patient patient;
//    Context context;
//
//    int billing_id = 0, branch_server_id = 0;
//    int p_year, p_month, p_day;

    Toolbar myToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_and_pickup_option_layout);

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pickup and Delivery Option");
        myToolBar.setNavigationIcon(R.drawable.ic_back);

        LinearLayout delivery_btn = (LinearLayout) findViewById(R.id.delivery_btn);
        LinearLayout pickup_btn = (LinearLayout) findViewById(R.id.pickup_btn);

        delivery_btn.setOnClickListener(this);
        pickup_btn.setOnClickListener(this);

//        p_year = 0;
//        p_month = 0;
//        p_day = 0;

//        context = getBaseContext();
//        patient = db.getCurrentLoggedInPatient();
//        arrayOfECEBranches = new ArrayList();
//        getBranchesFromDB = db.getECEBranches();

//        for (int x = 0; x < getBranchesFromDB.size(); x++) {
//            arrayOfECEBranches.add(getBranchesFromDB.get(x).get(db.BRANCHES_NAME));
//        }

//        customerName = (TextView) findViewById(R.id.customerName);
//        customerAddress = (TextView) findViewById(R.id.customerAddress);
//        customerContactNumber = (TextView) findViewById(R.id.customerContactNumber);
//        recipientName = (EditText) findViewById(R.id.recipientName);
//        recipientAddress = (EditText) findViewById(R.id.recipientAddress);
//        recipientContactNumber = (EditText) findViewById(R.id.recipientContactNumber);
//        pickup = (RadioButton) findViewById(R.id.pickup);
//        deliver = (RadioButton) findViewById(R.id.deliver);
//        paypal = (RadioButton) findViewById(R.id.paypal);
//        visa_or_mastercard = (RadioButton) findViewById(R.id.visa_or_mastercard);
//        cod = (RadioButton) findViewById(R.id.cod);
//        listOfECEBranches = (Spinner) findViewById(R.id.listOfECEBranches);
//        proceed = (Button) findViewById(R.id.proceed);
//        check = (CheckBox) findViewById(R.id.check);
//        hideLayout = (LinearLayout) findViewById(R.id.hideLayout);
//
//        adapter = new ArrayAdapter(this, R.layout.spinner_item, arrayOfECEBranches);
//        listOfECEBranches.setAdapter(adapter);

//        proceed.setOnClickListener(this);
//        check.setOnCheckedChangeListener(this);
//        pickup.setOnCheckedChangeListener(this);
//
//        ptnt_completeAddress = patient.getAddress_street() + " ";
//        ptnt_fullname = patient.getFname() + " " + patient.getLname();
//        ptnt_contactNumber = patient.getMobile_no();
//
//        customerName.setText("Name: " + ptnt_fullname);
//        customerAddress.setText("Address: " + ptnt_completeAddress);
//        customerContactNumber.setText("Contact No.: " + ptnt_contactNumber);
    }

    public CheckoutActivity() {

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
                setContentView(R.layout.address_for_delivery);
                myToolBar = (Toolbar) findViewById(R.id.myToolBar);
                Button next_btn = (Button) findViewById(R.id.next_btn);
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(R.layout.payment_method_layout);
                        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
                        setSupportActionBar(myToolBar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setTitle("Payment Method");
                        myToolBar.setNavigationIcon(R.drawable.ic_back);
                    }
                });

                setSupportActionBar(myToolBar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Recipient for delivery");
                myToolBar.setNavigationIcon(R.drawable.ic_back);
                break;
            case R.id.pickup_btn:
                setContentView(R.layout.payment_method_layout);
                myToolBar = (Toolbar) findViewById(R.id.myToolBar);
                setSupportActionBar(myToolBar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Payment Method");
                myToolBar.setNavigationIcon(R.drawable.ic_back);
                break;
            default:
                break;

        }
//            case R.id.proceed:
//                senderName = ptnt_fullname;
//                senderAddress = ptnt_completeAddress;
//                senderContactNumber = ptnt_contactNumber;
//                eceBranch = listOfECEBranches.getSelectedItem().toString();
//                int pos = listOfECEBranches.getSelectedItemPosition();
//
//                branch_server_id = Integer.parseInt(getBranchesFromDB.get(pos).get("branches_id"));
//
//                if (check.isChecked()) {
//                    receiverName = senderName;
//                    receiverAddress = senderAddress;
//                    receiverContactNumber = senderContactNumber;
//                } else {
//                    receiverName = recipientName.getText().toString();
//                    receiverAddress = recipientAddress.getText().toString();
//                    receiverContactNumber = recipientContactNumber.getText().toString();
//                }
//
//                if (pickup.isChecked())
//                    modeOfDelivery = "pick-up";
//                else if (deliver.isChecked())
//                    modeOfDelivery = "delivery";
//
//                if (cod.isChecked()) {
//                    HashMap<String, String> map = new HashMap();
//                    map.put("request", "save_orders");
//                    map.put("user_id", String.valueOf(SidebarActivity.getUserID()));
//                    map.put("recipient_name", receiverName);
//                    map.put("recipient_address", receiverAddress);
//                    map.put("recipient_contactNumber", receiverContactNumber);
//                    map.put("branch_id", String.valueOf(1)); //needs to be the id of the selected combobox
//                    map.put("modeOfDelivery", modeOfDelivery);
//                    map.put("payment_method", "cod");
//                    map.put("status", "pending");
//
//                    PostRequest.send(getBaseContext(), map, serverRequest, new RespondListener<JSONObject>() {
//                        @Override
//                        public void getResult(JSONObject response) {
//                            try {
//                                if (db.emptyBasket(SidebarActivity.getUserID())) {
//                                    //request for orders request
//                                    GetRequest.getJSONobj(getBaseContext(), "get_orders&patient_id=" + SidebarActivity.getUserID(), "orders", "orders_id", new RespondListener<JSONObject>() {
//                                        @Override
//                                        public void getResult(JSONObject response) {
//
//                                            GetRequest.getJSONobj(getBaseContext(), "get_order_details&patient_id=" + SidebarActivity.getUserID(), "order_details", "order_details_id", new RespondListener<JSONObject>() {
//                                                @Override
//                                                public void getResult(JSONObject response) {
//
//                                                    try {
//                                                String timestamp_ordered = response.getString("server_timestamp");
//
//                                                Intent order_intent = new Intent(getBaseContext(), OrdersActivity.class);
//                                                order_intent.putExtra("payment_from", "cod");
//                                                order_intent.putExtra("timestamp_ordered", timestamp_ordered);
//                                                startActivity(order_intent);
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                                }
//                                            }, new ErrorListener<VolleyError>() {
//                                                public void getError(VolleyError error) {
//                                                    Log.d("Error", error + "");
//                                                    Toast.makeText(getBaseContext(), "Couldn't refresh list. Please check your Internet connection", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                        }
//                                    }, new ErrorListener<VolleyError>() {
//                                        public void getError(VolleyError error) {
//                                            Log.d("Error", error + "");
//                                            Toast.makeText(getBaseContext(), "Couldn't refresh list. Please check your Internet connection", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            } catch (Exception e) {
//                                System.out.print("src: <ShoppingCartActivity > " + e.toString());
//                            }
//                        }
//                    }, new ErrorListener<VolleyError>() {
//                        @Override
//                        public void getError(VolleyError error) {
//                            System.out.print("src: <HomeTileActivityClone>: " + error.toString());
//                            Toast.makeText(getBaseContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                } else if (paypal.isChecked()) {
//
//                    Intent intent = new Intent(getBaseContext(), PayPalCheckout.class); //CheckoutActivity.class
//                    intent.putExtra("request", "save_orders");
//                    intent.putExtra("user_id", String.valueOf(SidebarActivity.getUserID()));
//                    intent.putExtra("recipient_name", receiverName);
//                    intent.putExtra("recipient_address", receiverAddress);
//                    intent.putExtra("recipient_contactNumber", receiverContactNumber);
//                    intent.putExtra("branch_server_id", branch_server_id);
//                    intent.putExtra("modeOfDelivery", modeOfDelivery);
//                    intent.putExtra("payment_method", "paypal");
//                    intent.putExtra("status", "pending");
//                    startActivity(intent);
//
//                } else if (visa_or_mastercard.isChecked()) {
//                    Intent intent = new Intent(getBaseContext(), PayPalCheckout.class); //CheckoutActivity.class
//                    intent.putExtra("request", "save_orders");
//                    intent.putExtra("user_id", String.valueOf(SidebarActivity.getUserID()));
//                    intent.putExtra("recipient_name", receiverName);
//                    intent.putExtra("recipient_address", receiverAddress);
//                    intent.putExtra("recipient_contactNumber", receiverContactNumber);
//                    intent.putExtra("branch_server_id", branch_server_id);
//                    intent.putExtra("modeOfDelivery", modeOfDelivery);
//                    intent.putExtra("payment_method", "visa_or_mastercard");
//                    intent.putExtra("status", "pending");
//                    startActivity(intent);
//                }
//
//                break;
//        }
    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (check.isChecked())
//            hideLayout.setVisibility(View.GONE);
//        else
//            hideLayout.setVisibility(View.VISIBLE);
//    }
}
