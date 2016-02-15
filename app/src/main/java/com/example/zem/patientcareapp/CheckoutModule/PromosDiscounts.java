package com.example.zem.patientcareapp.CheckoutModule;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.Activities.ProductsActivity;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Customizations.GlowingText;
import com.example.zem.patientcareapp.Customizations.NonScrollListView;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Interface.StringRespondListener;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.Network.StringRequests;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/19/2015.
 */

public class PromosDiscounts extends AppCompatActivity implements View.OnClickListener {
    Toolbar myToolBar;
    GlowingText glowButton;
    Button redeem_points, next_btn;
    float startGlowRadius = 25f,
            minGlowRadius = 2f,
            maxGlowRadius = 16f;
    OrderModel order_model;
    TextView points_text;
    Patient patient;
    PatientController pc;
    LinearLayout redeem_points_card, root;
    EditText coupon, points_txtfield;
    ProgressBar promo_progress;
    TextView message_after_promo_input;
    public static HashMap<String, String> promos_map;
    String msg;
    double final_peso_discount, final_percentage_discount, final_min_purchase = 0;
    String final_free_gift, final_free_delivery, final_qty_required = "";
    ArrayList<String> all_promos;
    Button promo_code_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promos_and_discounts_layout);

        promos_map = new HashMap<>();
        msg = "";
        order_model = (OrderModel) getIntent().getSerializableExtra("order_model");


        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        redeem_points = (Button) findViewById(R.id.redeem_points);
        points_text = (TextView) findViewById(R.id.points_text);
        next_btn = (Button) findViewById(R.id.next_btn);
        redeem_points_card = (LinearLayout) findViewById(R.id.redeem_points_card);
        coupon = (EditText) findViewById(R.id.coupon);
        root = (LinearLayout) findViewById(R.id.root);
        promo_progress = (ProgressBar) findViewById(R.id.promo_progress);
        message_after_promo_input = (TextView) findViewById(R.id.message_after_promo_input);
        points_txtfield = (EditText) findViewById(R.id.points_txtfield);
        promo_code_btn = (Button) findViewById(R.id.promo_code_btn);

        all_promos = new ArrayList<>();

        promo_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message_after_promo_input.setVisibility(View.GONE);
                if (!coupon.getText().toString().equals("")) {
                    promo_progress.setVisibility(View.VISIBLE);
                    searchPromoCode(coupon.getText().toString());
                }
            }
        });

        coupon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    message_after_promo_input.setVisibility(View.GONE);
                    promo_progress.setVisibility(View.VISIBLE);

                    if (!coupon.getText().toString().equals(""))
                        searchPromoCode(coupon.getText().toString());

                }
                return false;
            }
        });


        pc = new PatientController(this);
        patient = pc.getloginPatient(SidebarActivity.getUname());

        StringRequests.getString(PromosDiscounts.this, "db/get.php?q=get_patient_points&patient_id=" + SidebarActivity.getUserID(), new StringRespondListener<String>() {
            @Override
            public void getResult(String response) {
                patient.setPoints(Double.parseDouble(response));
                pc.updatePoints(Double.parseDouble(response));

                if (patient.getPoints() > 0) {
                    redeem_points_card.setVisibility(View.VISIBLE);
                    points_txtfield.setText(String.valueOf(patient.getPoints()));
                    points_text.setText(" out of " + patient.getPoints() + " points");
                    glowButton = new GlowingText(
                            PromosDiscounts.this,               // Pass activity Object
                            getBaseContext(),       // Context
                            redeem_points,                 // Button View
                            minGlowRadius,          // Minimum Glow Radius
                            maxGlowRadius,          // Maximum Glow Radius
                            startGlowRadius,        // Start Glow Radius - Increases to MaxGlowRadius then decreases to MinGlowRadius.
                            Color.WHITE,              // Glow Color (int)
                            2);                     // Glowing Transition Speed (Range of 1 to 10)
                }
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Log.d("error for sumthing", error + "");
            }
        });

        next_btn.setOnClickListener(this);
        redeem_points.setOnClickListener(this);

        setSupportActionBar(myToolBar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Promos & Discounts");
        myToolBar.setNavigationIcon(R.drawable.ic_back);
    }

    void searchPromoCode(String promo_code) {
        ListOfPatientsRequest.getJSONobj("check_promo_code&promo_code=" + promo_code, "promos", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                Log.d("response_promo", response + "");
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray json_mysql = response.getJSONArray("promos");

//                                    for (int x = 0; x < json_mysql.length(); x++) {
                        JSONObject obj = json_mysql.getJSONObject(0);

                        //free gifts still needed to be discussed
                        promos_map.put("is_free_delivery", obj.getString("is_free_delivery"));
                        promos_map.put("percentage_discount", obj.getString("percentage_discount"));
                        promos_map.put("peso_discount", obj.getString("peso_discount"));

                        promos_map.put("product_applicability", obj.getString("product_applicability"));
                        promos_map.put("minimum_purchase", obj.getString("minimum_purchase_amount"));

                        //additional common data
                        promos_map.put("promo_id", obj.getString("id"));
                        promos_map.put("offer_type", obj.getString("offer_type"));
                        promos_map.put("coupon_code", obj.getString("generic_redemption_code"));
                        promos_map.put("start_date", obj.getString("start_date"));
                        promos_map.put("end_date", obj.getString("end_date"));

                        //setting msg for what the user have received
                        final_min_purchase = Double.parseDouble(promos_map.get("minimum_purchase"));
                        final_peso_discount = Double.parseDouble(promos_map.get("peso_discount"));
                        final_percentage_discount = Double.parseDouble(promos_map.get("percentage_discount"));
                        final_free_gift = "";
                        final_free_delivery = promos_map.get("is_free_delivery");
                        final_qty_required = promos_map.get("quantity_required");


                        if (final_peso_discount > 0) {
                            msg = "You got ₱" + promos_map.get("peso_discount") + " discount on your total order.";
                            order_model.setCoupon_discount(Double.parseDouble(promos_map.get("peso_discount")));
                            order_model.setCoupon_discount_type("peso_discount");
                        }

                        if (final_percentage_discount > 0) {
                            msg = "You got " + promos_map.get("percentage_discount") + "% discount on your total order.";
                            order_model.setCoupon_discount(Double.parseDouble(promos_map.get("percentage_discount")));
                            order_model.setCoupon_discount_type("percentage_discount");
                        }

                        if (final_free_gift.equals("1")) {
                            msg = "You got free gift, upon purchase.";
                            order_model.setCoupon_discount_type("free_gift");
                        }

                        if (final_free_delivery.equals("1")) {
                            msg = "You got free delivery.";
                            order_model.setCoupon_discount_type("free_delivery");
                        }


                        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(PromosDiscounts.this);
                        confirmationDialog.setTitle("Congratulations!");
                        confirmationDialog.setMessage(msg);
                        confirmationDialog.setCancelable(false);
                        confirmationDialog.setPositiveButton("Ok, Thanks", null);
                        confirmationDialog.show();

                        message_after_promo_input.setTextColor(getResources().getColor(R.color.ColorPrimary));
                        message_after_promo_input.setText(msg);
                        message_after_promo_input.setVisibility(View.VISIBLE);
                        order_model.setPromo_id(Integer.parseInt(promos_map.get("promo_id")));

                        promo_progress.setVisibility(View.GONE);
                        coupon.setVisibility(View.GONE);
                        promo_code_btn.setVisibility(View.GONE);

//                                    }
                    } else {
                        promo_progress.setVisibility(View.GONE);
                        message_after_promo_input.setTextColor(getResources().getColor(R.color.list_background_pressed));
                        message_after_promo_input.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
//                                Snackbar.make(root, e + "", Snackbar.LENGTH_INDEFINITE).show();
                    Log.d("err", e + "");
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                Snackbar.make(root, "Network error", Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                glowButton.stopGlowing();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.redeem_points:
                double points_input = Double.parseDouble(points_txtfield.getText().toString());
                if (points_input > patient.getPoints() || points_input == 0) {
                    Snackbar.make(root, "Please input between 1 and " + patient.getPoints(), Snackbar.LENGTH_LONG).show();
                } else {
                    order_model.setPoints_discount(points_input);
                    redeem_points.setVisibility(View.GONE);
                    points_txtfield.setVisibility(View.GONE);
                    points_text.setTextColor(getResources().getColor(R.color.ColorPrimary));
                    points_text.setText("Your order total will be discounted ₱ " + order_model.getPoints_discount() + " upon checkout");
                }
                break;
            case R.id.next_btn:
                Intent intent = new Intent(this, SummaryActivity.class);
                intent.putExtra("order_model", order_model);
                startActivity(intent);
                this.finish();
                break;
        }
    }
}
