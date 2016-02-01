package com.example.zem.patientcareapp.CheckoutModule;


import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Activities.ShoppingCartActivity;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Controllers.BasketController;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Controllers.SettingController;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Interface.StringRespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.Model.Settings;
import com.example.zem.patientcareapp.Network.CustomPostRequest;
import com.example.zem.patientcareapp.Network.GetRequest;
import com.example.zem.patientcareapp.Customizations.NonScrollListView;
import com.example.zem.patientcareapp.Network.ListRequestFromCustomURI;
import com.example.zem.patientcareapp.Network.StringRequests;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.example.zem.patientcareapp.adapter.ShoppingCartAdapter;

import org.json.JSONException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/20/2015.
 */
public class SummaryActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar myToolBar;
    Button change_id, order_now_btn;
    OrderModel order_model;
    Intent get_intent;
    DbHelper dbHelper;
    Helpers helper;
    PatientController pc;
    BasketController bc;
    SettingController sc;

    ArrayList<HashMap<String, String>> items;
    Double totalAmount = 0.0;
    NonScrollListView order_summary;
    TextView amount_subtotal, amount_of_coupon_discount, amount_of_points_discount, total_amount, delivery_charge;
    LinearLayout points_layout, coupon_layout, subtotal_layout, total_layout, delivery_charge_layout;
    TextView order_receiving_option, address_option, recipient_option, payment_option, recipient_contact_number, address_or_branch;
    LinearLayout root;
    Settings settings;
    double delivery_charge_val = 0;
    double discounted_total = 0;
    public static AppCompatDialog pDialog;
    AlertDialog.Builder builder;
    TextView label_expected_points, label_total_savings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summarylayout);

        get_intent = getIntent();

        order_model = (OrderModel) get_intent.getSerializableExtra("order_model");
        Log.d("summary_activity_om", order_model.getMode_of_delivery());
        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        change_id = (Button) findViewById(R.id.change_id);
        order_summary = (NonScrollListView) findViewById(R.id.order_summary);

        amount_subtotal = (TextView) findViewById(R.id.amount_subtotal);
        amount_of_coupon_discount = (TextView) findViewById(R.id.amount_of_coupon_discount);
        amount_of_points_discount = (TextView) findViewById(R.id.amount_of_points_discount);
        total_amount = (TextView) findViewById(R.id.total_amount);

        order_receiving_option = (TextView) findViewById(R.id.order_receiving_option);
        address_option = (TextView) findViewById(R.id.address_option);
        recipient_option = (TextView) findViewById(R.id.recipient_option);
        payment_option = (TextView) findViewById(R.id.payment_option);
        recipient_contact_number = (TextView) findViewById(R.id.recipient_contact_number);
        address_or_branch = (TextView) findViewById(R.id.address_or_branch);
        delivery_charge = (TextView) findViewById(R.id.delivery_charge);

        delivery_charge_layout = (LinearLayout) findViewById(R.id.delivery_charge_layout);
        points_layout = (LinearLayout) findViewById(R.id.points_layout);
        coupon_layout = (LinearLayout) findViewById(R.id.coupon_layout);
        subtotal_layout = (LinearLayout) findViewById(R.id.subtotal_layout);
        total_layout = (LinearLayout) findViewById(R.id.total_layout);
        order_now_btn = (Button) findViewById(R.id.order_now_btn);
        root = (LinearLayout) findViewById(R.id.root);
        label_expected_points = (TextView) findViewById(R.id.label_expected_points);
        label_total_savings = (TextView) findViewById(R.id.label_total_savings);

        dbHelper = new DbHelper(this);
        pc = new PatientController(this);
        bc = new BasketController();
        helper = new Helpers();
        sc = new SettingController(this);

//        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("Please wait...");
//        dialog.setCancelable(false);
//        dialog.show();

        showBeautifulDialog();

        String url_raw = "check_basket?patient_id=" + SidebarActivity.getUserID() + "&branch_id=" + order_model.getBranch_id();
        ListRequestFromCustomURI.getJSONobj(this, url_raw, "baskets", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    Log.d("response_fs", response + "");

                    double expected_points_value = response.getDouble("expected_points");

                    int success = response.getInt("success");
                    final JSONArray json_mysql = response.getJSONArray("baskets");

                    if (success == 1) {
                        if (response.getBoolean("basket_quantity_changed")) {
                            letDialogSleep();
                            orderCancelled();
                        }

                        if (order_model.getMode_of_delivery().equals("pickup")) {
                            address_or_branch.setText("Branch to pickup order");
                            setBranchNameFromServer();
                            populateView(json_mysql, false, expected_points_value);
                        } else {
                            address_or_branch.setText("Address for delivery");
                            address_option.setText(order_model.getRecipient_address());
                            populateView(json_mysql, true, expected_points_value);
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(SummaryActivity.this, e + "", Toast.LENGTH_SHORT).show();
                }
                letDialogSleep();
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                letDialogSleep();
                Toast.makeText(getBaseContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        order_receiving_option.setText(order_model.getMode_of_delivery());
        recipient_option.setText(order_model.getRecipient_name());
        payment_option.setText(helper.decodePaymentCode(order_model.getPayment_method(), order_model.getMode_of_delivery()));
        recipient_contact_number.setText(order_model.getRecipient_contactNumber());

        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Summary");
        myToolBar.setNavigationIcon(R.drawable.ic_back);

        change_id.setOnClickListener(this);
        order_now_btn.setOnClickListener(this);
    }

    void populateView(final JSONArray json_mysql, final boolean isDelivery, final double expected_points_value) {
        GetRequest.getJSONobj(getBaseContext(), "get_settings", "settings", "serverID", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                items = bc.convertFromJson(SummaryActivity.this, json_mysql);

                for (HashMap<String, String> item : items) {
                    String promo_type = item.get("promo_type");
                    double item_subtotal_value = Double.parseDouble(item.get("item_subtotal"));
                    double peso_discount = Double.parseDouble(item.get("peso_discount"));
                    double percentage_discount = Double.parseDouble(item.get("percentage_discount"));
                    double computed_discount = 0;

                    if (promo_type.equals("peso_discount"))
                        computed_discount = item_subtotal_value - peso_discount;
                    else if (promo_type.equals("percentage_discount"))
                        computed_discount = item_subtotal_value - percentage_discount;
                    else
                        computed_discount = item_subtotal_value;

                    totalAmount = totalAmount + computed_discount;
                }

                double coupon_discount = order_model.getCoupon_discount();

                if (order_model.getCoupon_discount_type().equals("percentage_discount")) {
                    Log.d("converted_percentage", totalAmount * (coupon_discount / 100) + "");
                    coupon_discount = totalAmount * (coupon_discount / 100);
                }

                double points_discount = order_model.getPoints_discount();
                discounted_total = totalAmount - points_discount - coupon_discount;

                        /* discounts and total block*/
                if (coupon_discount == 0.0)
                    coupon_layout.setVisibility(View.GONE);

                if (points_discount == 0.0)
                    points_layout.setVisibility(View.GONE);

                if (coupon_discount == 0.0 && points_discount == 0.0)
                    subtotal_layout.setVisibility(View.GONE);

                amount_subtotal.setText("\u20B1 " + String.valueOf(totalAmount));
                amount_of_coupon_discount.setText("\u20B1 " + String.format("%.2f", coupon_discount));
                amount_of_points_discount.setText("\u20B1 " + String.format("%.2f", points_discount));
                total_amount.setText("\u20B1 " + String.format("%.2f", discounted_total));
                label_expected_points.setText("You will receive "+ expected_points_value + " points upon order." );
                label_total_savings.setText("You will save \u20b1 "+ ShoppingCartAdapter.total_savings_value);

                if (isDelivery) {
                    settings = sc.getAllSettings();
                    delivery_charge_layout.setVisibility(View.VISIBLE);
                    if (order_model.getCoupon_discount_type().equals("free_delivery")) {
                        delivery_charge.setTextColor(getResources().getColor(R.color.ColorPrimary));
                        delivery_charge.setText("Free");
                    } else {
                        delivery_charge.setText("₱ " + settings.getDelivery_charge());
                        delivery_charge_val = settings.getDelivery_charge();
                        discounted_total += delivery_charge_val;
                        total_amount.setText("\u20B1 " + String.format("%.2f", discounted_total));
                    }
                }
                /* discounts and total block*/
                SummaryAdapter adapter = new SummaryAdapter(SummaryActivity.this, items);
                order_summary.setAdapter(adapter);
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Snackbar.make(root, "Please check network connection.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    void setBranchNameFromServer() {
        StringRequests.getString(SummaryActivity.this, "db/get.php?q=get_branch_name_from_id&branch_id=" + order_model.getBranch_id(), new StringRespondListener<String>() {
            @Override
            public void getResult(String response) {
                address_option.setText(response);
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Log.d("error for sumthing", error + "");
            }
        });
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

//    public void checkForSettingsUpdate() {
//        GetRequest.getJSONobj(getBaseContext(), "get_settings", "settings", "serverID", new RespondListener<JSONObject>() {
//            @Override
//            public void getResult(JSONObject response) {
//                settings = sc.getAllSettings();
//                delivery_charge_layout.setVisibility(View.VISIBLE);
//                if (order_model.getCoupon_discount_type().equals("free_delivery")) {
//                    delivery_charge.setTextColor(getResources().getColor(R.color.ColorPrimary));
//                    delivery_charge.setText("Free");
//                } else {
//                    delivery_charge.setText("₱ " + settings.getDelivery_charge());
//                    delivery_charge_val = settings.getDelivery_charge();
//                    discounted_total += delivery_charge_val;
//                    total_amount.setText("\u20B1 " + String.format("%.2f", discounted_total));
//                }
//            }
//        }, new ErrorListener<VolleyError>() {
//            public void getError(VolleyError error) {
//                Snackbar.make(root, "Please check network connection.", Snackbar.LENGTH_LONG).show();
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_id:
                Intent intent = new Intent(this, DeliverPickupOption.class);
                intent.putExtra("order_model", order_model);
                startActivity(intent);
                break;
            case R.id.order_now_btn:
                if (order_model.getPayment_method().equals("paypal")) {
                    Intent paypal_intent = new Intent(this, PayPalCheckout.class);
                    paypal_intent.putExtra("order_model", order_model);
                    paypal_intent.putExtra("delivery_charge", String.valueOf(delivery_charge_val));
                    startActivity(paypal_intent);
                    SummaryActivity.this.finish();
                } else if (order_model.getPayment_method().equals("cash_on_delivery")) {
                    AlertDialog.Builder order_confirmation_dialog = new AlertDialog.Builder(SummaryActivity.this);
                    order_confirmation_dialog.setTitle("Confirmation");
                    order_confirmation_dialog.setMessage("Have you carefully reviewed your order and ready to checkout ?");
                    order_confirmation_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Patient patient = pc.getloginPatient(SidebarActivity.getUname());
                            showBeautifulDialog();
                            HashMap<String, String> map = new HashMap();
                            map.put("user_id", String.valueOf(SidebarActivity.getUserID()));
                            map.put("recipient_name", order_model.getRecipient_name());
                            map.put("recipient_address", order_model.getRecipient_address());
                            map.put("recipient_contactNumber", order_model.getRecipient_contactNumber());
                            map.put("branch_server_id", String.valueOf(order_model.getBranch_id())); //needs to be the id of the selected combobox
                            map.put("modeOfDelivery", order_model.getMode_of_delivery());
                            map.put("payment_method", order_model.getPayment_method());
                            map.put("status", "Pending");
                            map.put("coupon_discount", String.valueOf(order_model.getCoupon_discount()));
                            map.put("points_discount", String.valueOf(order_model.getPoints_discount()));
                            map.put("delivery_charge", String.valueOf(delivery_charge_val));
                            map.put("promo_id", String.valueOf(order_model.getPromo_id()));
                            map.put("promo_type", String.valueOf(order_model.getCoupon_discount_type()));
                            map.put("email", patient.getEmail());

                            Log.d("mappings", map.toString());

                            String url = "verify_cash_payment";
                            CustomPostRequest.send(url, map, new RespondListener<JSONObject>() {
                                        @Override
                                        public void getResult(JSONObject response) {
                                            Log.d("b_payresponse", response + "");
                                            try {
                                                if (response.getBoolean("basket_quantity_changed")) {
                                                    letDialogSleep();
                                                    orderCancelled();
                                                } else {
                                                    Log.d("b_check", "true im here");
                                                    //request for orders request
                                                    GetRequest.getJSONobj(getBaseContext(), "get_orders&patient_id=" + SidebarActivity.getUserID(), "orders", "orders_id", new RespondListener<JSONObject>() {
                                                        @Override
                                                        public void getResult(JSONObject response) {

                                                            Log.d("b_get_orders", response + "");

                                                            GetRequest.getJSONobj(getBaseContext(), "get_order_details&patient_id=" + SidebarActivity.getUserID(), "order_details", "order_details_id", new RespondListener<JSONObject>() {
                                                                @Override
                                                                public void getResult(JSONObject response) {

                                                                    Log.d("b_get_od", response + "");

                                                                    GetRequest.getJSONobj(getBaseContext(), "get_order_billings&patient_id=" + SidebarActivity.getUserID(), "billings", "billings_id", new RespondListener<JSONObject>() {
                                                                        @Override
                                                                        public void getResult(JSONObject response) {
                                                                            Log.d("get_ob", response + "");
                                                                            try {
                                                                                String timestamp_ordered = response.getString("server_timestamp");

                                                                                orderCompletedDialog(timestamp_ordered);

                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            letDialogSleep();
                                                                        }
                                                                    }, new ErrorListener<VolleyError>() {
                                                                        public void getError(VolleyError error) {
                                                                            letDialogSleep();
                                                                            Log.d("get_ob_error", error + "");
                                                                        }
                                                                    });
                                                                }
                                                            }, new ErrorListener<VolleyError>() {
                                                                public void getError(VolleyError error) {
                                                                    letDialogSleep();
                                                                    Log.d("b_get_od_error", error + "");
                                                                }
                                                            });

                                                        }
                                                    }, new ErrorListener<VolleyError>() {
                                                        public void getError(VolleyError error) {
                                                            Log.d("b_get_orders_error", error + "");
                                                            letDialogSleep();
                                                        }
                                                    });

                                                }

                                            } catch (Exception e) {
                                                System.out.print("src: <SummaryAct> " + e.toString());
                                                SummaryActivity.this.finish();
                                            }
                                        }
                                    }

                                    , new ErrorListener<VolleyError>()

                                    {
                                        @Override
                                        public void getError(VolleyError error) {
                                            letDialogSleep();
                                            System.out.print("src: <HomeTileActivityClone>: " + error.toString());
                                            Toast.makeText(getBaseContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                        }
                    });
                    order_confirmation_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            SummaryActivity.this.finish();
                            dialog.dismiss();
                        }
                    });
                    order_confirmation_dialog.show();
                }
                break;
        }
    }

    void orderCancelled() {
        AlertDialog.Builder cancelled_order_dialog = new AlertDialog.Builder(SummaryActivity.this);
        cancelled_order_dialog.setTitle("Order Cancelled!");
        cancelled_order_dialog.setMessage("Sorry to inform you that your order have been cancelled. \n" +
                "Our records show that one or more products that you want to order exceeds the number of our stocks. \n" +
                "We updated your basket items. \n" +
                "Please try again.");
        cancelled_order_dialog.setCancelable(false);
        cancelled_order_dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(SummaryActivity.this, ShoppingCartActivity.class));
                SummaryActivity.this.finish();
            }
        });
        cancelled_order_dialog.show();
    }

    void orderCompletedDialog(final String timestamp_ordered) {
        AlertDialog.Builder order_completed_dialog = new AlertDialog.Builder(SummaryActivity.this);
        order_completed_dialog.setTitle("Order Completed");
        order_completed_dialog.setMessage("Thank you for ordering through Patient Care App! \n " +
                "We will inform you once we have taken action on your order. \n");
        order_completed_dialog.setCancelable(false);
        order_completed_dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent order_intent = new Intent(SummaryActivity.this, SidebarActivity.class);
                order_intent.putExtra("payment_from", "cod");
                order_intent.putExtra("timestamp_ordered", timestamp_ordered);
                order_intent.putExtra("select", 5);
                startActivity(order_intent);
                SummaryActivity.this.finish();
            }
        });
        order_completed_dialog.show();
    }

    void showBeautifulDialog() {
        builder = new AlertDialog.Builder(SummaryActivity.this);
        builder.setView(R.layout.progress_stuffing);
        builder.setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    void letDialogSleep() {
        pDialog.dismiss();
    }
}
