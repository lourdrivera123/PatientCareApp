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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.example.zem.patientcareapp.Network.GetRequest;
import com.example.zem.patientcareapp.Customizations.NonScrollListView;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.Network.ListRequestFromCustomURI;
import com.example.zem.patientcareapp.Network.StringRequests;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.ShowPrescriptionDialog;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.example.zem.patientcareapp.adapter.ShoppingCartAdapter;

import org.json.JSONException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.support.design.widget.Snackbar.make;
import static android.util.Log.d;
import static com.example.zem.patientcareapp.Network.CustomPostRequest.send;
import static com.example.zem.patientcareapp.Network.ListRequestFromCustomURI.getJSONobj;
import static com.example.zem.patientcareapp.SidebarModule.SidebarActivity.getUserID;

public class SummaryActivity extends AppCompatActivity implements View.OnClickListener {

    public static AppCompatDialog pDialog;
    public static HashMap<String, String> promos_map;
    Toolbar myToolBar;
    Button change_id, order_now_btn, promo_code_btn, use_points_btn;
    OrderModel order_model;
    Intent get_intent;
    DbHelper dbHelper;
    Helpers helper;
    PatientController pc;
    BasketController bc;
    SettingController sc;
    ArrayList<HashMap<String, String>> items;
    double totalAmount = 0.0;
    double undiscounted_total = 0;
    double senior_discount = 0;
    NonScrollListView order_summary;
    TextView amount_subtotal, amount_of_coupon_discount, amount_of_points_discount, total_amount, delivery_charge;
    LinearLayout points_layout, coupon_layout, subtotal_layout, total_layout, delivery_charge_layout;
    TextView order_receiving_option, address_option, recipient_option, payment_option, recipient_contact_number, address_or_branch;
    LinearLayout root;
    LinearLayout promo_code_btn_layout, use_points_btn_layout;
    Settings settings;
    double delivery_charge_val = 0;
    double discounted_total = 0;
    AlertDialog.Builder builder;
    TextView label_expected_points, label_total_savings, label_senior_discount;
    String msg;
    double final_peso_discount, final_percentage_discount, final_min_purchase = 0;
    String final_free_gift, final_free_delivery, final_qty_required = "";
    EditText coupon;
    ProgressBar promo_progress;
    TextView message_after_promo_input;
    String modeOfDelivery = "";
    boolean final_isDelivery;
    double final_expected_points_value;
    EditText points_txtfield;
    TextView points_text;
    Patient patient;
    TextView how_to_senior_discount;
    EditText senior_id_number;
    Button upload_senior_id;
    ImageView senior_picture_id;
    ProgressBar progress;
    View view_senior;
    LinearLayout total_savings_layout;
//    boolean age_valid_for_senior_discount = false;
    String senior_validity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summarylayout);

        get_intent = getIntent();

        order_model = (OrderModel) get_intent.getSerializableExtra("order_model");
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
        label_senior_discount = (TextView) findViewById(R.id.label_senior_discount);

        promo_code_btn = (Button) findViewById(R.id.promo_code_btn);
        use_points_btn = (Button) findViewById(R.id.use_points_btn);
        promo_code_btn_layout = (LinearLayout) findViewById(R.id.promo_code_btn_layout);
        use_points_btn_layout = (LinearLayout) findViewById(R.id.use_points_btn_layout);
        how_to_senior_discount = (TextView) findViewById(R.id.how_to_senior_discount);
        total_savings_layout = (LinearLayout) findViewById(R.id.total_savings_layout);

        how_to_senior_discount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showSeniorValidationDialog();
            }
        });

        promo_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPromoDialog();
            }
        });
        use_points_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPointsDialog();
            }
        });

        dbHelper = new DbHelper(this);
        pc = new PatientController(this);
        bc = new BasketController();
        helper = new Helpers();
        sc = new SettingController(this);
        patient = pc.getloginPatient(SidebarActivity.getUname());

        showBeautifulDialog();
        getBasketDetails();
        setOrderDetails();
        _setActionBar();

        change_id.setOnClickListener(this);
        order_now_btn.setOnClickListener(this);
    }

    void _setActionBar(){
        setSupportActionBar(myToolBar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Summary");
        myToolBar.setNavigationIcon(R.drawable.ic_back);
    }

    void setOrderDetails(){
        order_receiving_option.setText(order_model.getMode_of_delivery());
        recipient_option.setText(order_model.getRecipient_name());
        payment_option.setText(helper.decodePaymentCode(order_model.getPayment_method(), order_model.getMode_of_delivery()));
        recipient_contact_number.setText(order_model.getRecipient_contactNumber());
    }

    void getBasketDetails(){
        String url_raw = "check_basket?patient_id=" + getUserID() + "&branch_id=" + order_model.getBranch_id();
        getJSONobj(url_raw, new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    d("response_fs", response + "");

                    final double expected_points_value = response.getDouble("expected_points");

                    int success = response.getInt("success");
                    final JSONArray json_mysql = response.getJSONArray("baskets");

                    if (success == 1) {
                        if (response.getBoolean("basket_quantity_changed")) {
                            letDialogSleep();
                            orderCancelled();
                        }

                        StringRequests.getString(SummaryActivity.this, "db/get.php?q=get_patient_points&patient_id=" + getUserID(), new StringRespondListener<String>() {
                            @Override
                            public void getResult(String response) {
                                patient.setPoints(Double.parseDouble(response));
                                pc.updatePoints(Double.parseDouble(response));

                                if (patient.getPoints() > 0)
                                    use_points_btn_layout.setVisibility(View.VISIBLE);
                                else
                                    use_points_btn_layout.setVisibility(View.GONE);

                                modeOfDelivery = order_model.getMode_of_delivery();

                                if (modeOfDelivery.equals("pickup")) {
                                    address_or_branch.setText("Branch to pickup order");
                                    setBranchNameFromServer();
                                    final_isDelivery = false;
                                    final_expected_points_value = expected_points_value;
                                    updateSettings(json_mysql);
                                } else {
                                    address_or_branch.setText("Address for delivery");
                                    address_option.setText(order_model.getRecipient_address());
                                    final_isDelivery = true;
                                    final_expected_points_value = expected_points_value;
                                    updateSettings(json_mysql);
                                }
                            }
                        }, new ErrorListener<VolleyError>() {
                            public void getError(VolleyError error) {
                                d("error for sumthing", error + "");
                            }
                        });
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
    }

    void updateSettings(final JSONArray json_mysql) {
        GetRequest.getJSONobj(getBaseContext(), "get_settings", "settings", "serverID", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                checkSeniorValidity(json_mysql);
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                make(root, "Please check network connection.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    void checkSeniorValidity(final JSONArray json_mysql) {
        getJSONobj("getSeniorValidity?patient_id=" + getUserID(), new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    int age = response.getInt("age");
                    int isSenior = Integer.parseInt(response.getString("isSenior"));
                    String senior_citizen_id_number = response.getString("senior_citizen_id_number");
                    String senior_id_picture = response.getString("senior_id_picture");

                    if (isSenior > 0 && !senior_citizen_id_number.equals("") && !senior_id_picture.equals(""))
                        senior_validity = "senior_valid";
                    else {
                        if (age > 59)
                            senior_validity = "senior_invalid";
                        else
                            senior_validity = "not_senior";
                    }

                    populateView(json_mysql);
                } catch (Exception e) {
                    d("snce", e + "");
                }
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                make(root, "Network error", LENGTH_SHORT).show();
            }
        });
    }

    void populateView(JSONArray json_mysql){
        items = bc.convertFromJson(SummaryActivity.this, json_mysql);

        for (HashMap<String, String> item : items) {
            String promo_type = item.get("promo_type");
            double item_subtotal_value = Double.parseDouble(item.get("item_subtotal"));
            double peso_discount = Double.parseDouble(item.get("peso_discount"));
            double percentage_discount = Double.parseDouble(item.get("percentage_discount"));
            double computed_discount;

            switch (promo_type) {
                case "peso_discount":
                    computed_discount = item_subtotal_value - peso_discount;
                    break;
                case "percentage_discount":
                    computed_discount = item_subtotal_value - percentage_discount;
                    break;
                default:
                    computed_discount = item_subtotal_value;
                    break;
            }

            undiscounted_total += item_subtotal_value;
            totalAmount = totalAmount + computed_discount;
        }

        SummaryAdapter adapter = new SummaryAdapter(SummaryActivity.this, items);
        order_summary.setAdapter(adapter);

        showTotalDetails();
    }

    void showSeniorValidationDialog(){
        view_senior = LayoutInflater.from(getBaseContext()).inflate(R.layout.senior_dialog_layout, null);
        senior_id_number = (EditText) view_senior.findViewById(R.id.senior_id_number);
        upload_senior_id = (Button) view_senior.findViewById(R.id.upload_senior_id);
        senior_picture_id = (ImageView) findViewById(R.id.senior_picture_id);
        progress = (ProgressBar) findViewById(R.id.progress);

        upload_senior_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senior_id_number_s  = senior_id_number.getText().toString();
                if(!senior_id_number_s.equals("")) {
                    Intent intent = new Intent(getBaseContext(), ShowPrescriptionDialog.class);
                    intent.putExtra("isForSeniorUpload", true);
                    intent.putExtra("senior_citizen_id_number", senior_id_number.getText().toString());
                    startActivityForResult(intent, 2);
                } else
                    senior_id_number.setError("Enter ID number first");
            }
        });

        builder = new AlertDialog.Builder(SummaryActivity.this);
        builder.setView(view_senior);
        builder.setCancelable(false);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2 && data != null)
        {
            String imgFile = data.getStringExtra("imgFile");
            senior_picture_id = (ImageView) view_senior.findViewById(R.id.senior_picture_id);
            progress = (ProgressBar) view_senior.findViewById(R.id.progress);
            upload_senior_id = (Button) view_senior.findViewById(R.id.upload_senior_id);

            if (imgFile != null || !imgFile.equals("")) {
                d("imgFile", imgFile + "");
                upload_senior_id.setVisibility(View.GONE);
                senior_picture_id.setVisibility(View.VISIBLE);
                helper.setImage(imgFile, progress, senior_picture_id);
                how_to_senior_discount.setVisibility(View.GONE);
//                label_senior_discount.setText("You will save " + helper.money_format(senior_discount));
//                total_savings_layout.setVisibility(View.GONE);
                flushBasketPromos();
            }
        }
    }

    void flushBasketPromos(){
        ShoppingCartAdapter.total_savings_value = 0;
        totalAmount = 0;
        undiscounted_total = 0;
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("patient_id", String.valueOf(getUserID()));

        d("flush", "did I even get here ?");
        send("flush_user_basket_promos", hashMap, new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                d("flush", response+"");
                try {
                    if(response.getBoolean("success")){
                        showBeautifulDialog();
                        getBasketDetails();
                    }
                } catch(Exception e)  {
                    System.out.println("<flush_user_basket_promos> request error" + e);
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError error) {
                System.out.println("src: <flush_user_basket_promos>: " + error.toString());
                d("flush", "wtf ?");
            }
        });
    }

    void showTotalDetails(){
        double coupon_discount = order_model.getCoupon_discount();
        d("coupon_discount_", coupon_discount + "");

        if (order_model.getCoupon_discount_type().equals("percentage_discount")) {
            d("converted_percentage", totalAmount * (coupon_discount / 100) + "");
            coupon_discount = totalAmount * (coupon_discount / 100);
        }

        double points_discount = order_model.getPoints_discount();

        if (coupon_discount == 0.0)
            coupon_layout.setVisibility(View.GONE);
        else
            coupon_layout.setVisibility(View.VISIBLE);

        if (points_discount == 0.0)
            points_layout.setVisibility(View.GONE);
        else
            points_layout.setVisibility(View.VISIBLE);

        amount_subtotal.setText(helper.money_format(totalAmount));
        amount_of_coupon_discount.setText(helper.money_format(coupon_discount));
        amount_of_points_discount.setText(helper.money_format(points_discount));

        senior_discount  = undiscounted_total * .20;

        label_expected_points.setText("You will receive " + final_expected_points_value + " points upon order.");
        label_total_savings.setText("You will save " + helper.money_format(ShoppingCartAdapter.total_savings_value));
        discounted_total = totalAmount - points_discount - coupon_discount;

        if(senior_discount > ShoppingCartAdapter.total_savings_value){
            if(senior_validity.equals("senior_valid")){
                if(ShoppingCartAdapter.total_savings_value == 0) {
                    label_total_savings.setVisibility(View.GONE);
                    label_senior_discount.setVisibility(View.VISIBLE);
                    how_to_senior_discount.setVisibility(View.GONE);
                    label_senior_discount.setText("You saved " + helper.money_format(senior_discount + points_discount + coupon_discount) + " from Senior Citizen Discount");
                    order_model.setSenior_discount(senior_discount);
                    discounted_total -= senior_discount;
                } else
                    flushBasketPromos();
            } else if(senior_validity.equals("senior_invalid")) {
                label_senior_discount.setVisibility(View.VISIBLE);
                how_to_senior_discount.setVisibility(View.VISIBLE);
                label_senior_discount.setText("You will save more ( " + helper.money_format(senior_discount) + " ) if you validate your Senior Citizen ID");
            } else {
                label_senior_discount.setVisibility(View.GONE);
                how_to_senior_discount.setVisibility(View.GONE);
                label_total_savings.setVisibility(View.VISIBLE);
                label_total_savings.setText("You will save " + helper.money_format(ShoppingCartAdapter.total_savings_value + coupon_discount + points_discount));
            }
        } else {
            label_total_savings.setVisibility(View.VISIBLE);
            label_total_savings.setText("You will save " + helper.money_format(ShoppingCartAdapter.total_savings_value + coupon_discount + points_discount));
        }
        if(ShoppingCartAdapter.total_savings_value == 0 && senior_discount == 0){
            total_savings_layout.setVisibility(View.GONE);
            subtotal_layout.setVisibility(View.GONE);
        }

        if (final_isDelivery) {
            settings = sc.getAllSettings();
            delivery_charge_layout.setVisibility(View.VISIBLE);
            if (order_model.getCoupon_discount_type().equals("free_delivery")) {
                delivery_charge.setTextColor(getResources().getColor(R.color.ColorPrimary));
                delivery_charge.setText("Free");
            } else {
                delivery_charge.setText(helper.money_format(settings.getDelivery_charge()));
                delivery_charge_val = settings.getDelivery_charge();
                discounted_total += delivery_charge_val;
                total_amount.setText(helper.money_format(discounted_total));
            }
        }

        total_amount.setText(helper.money_format(discounted_total));
    }

    void showPointsDialog() {
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.points_dialog_layout, null);
        points_txtfield = (EditText) view.findViewById(R.id.points_txtfield);
        points_text = (TextView) view.findViewById(R.id.points_text);

        points_txtfield.setText(String.valueOf(patient.getPoints()));
        points_text.setText(" out of "+ patient.getPoints() + " points");

        builder = new AlertDialog.Builder(SummaryActivity.this);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("Use", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double points_input = Double.parseDouble(points_txtfield.getText().toString());

                if (points_input > patient.getPoints() || points_input == 0) {
                    make(root, "Please input between 1 and " + patient.getPoints(), Snackbar.LENGTH_LONG).show();
                } else {
                    order_model.setPoints_discount(points_input);
                    dialog.dismiss();
                    showTotalDetails();
                }
            }
        });

    }

    void showPromoDialog() {
        View view1 = LayoutInflater.from(getBaseContext()).inflate(R.layout.coupon_dialog_layout, null);
        coupon = (EditText) view1.findViewById(R.id.coupon);
        promo_progress = (ProgressBar) view1.findViewById(R.id.promo_progress);
        message_after_promo_input = (TextView) view1.findViewById(R.id.message_after_promo_input);

        builder = new AlertDialog.Builder(SummaryActivity.this);
        builder.setView(view1);
        builder.setCancelable(false);
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message_after_promo_input.setVisibility(View.GONE);
                final String promo_code = coupon.getText().toString();
                if (!promo_code.equals("")) {
                    promo_progress.setVisibility(View.VISIBLE);
                    searchPromoCode(promo_code, dialog);
                }
            }
        });
    }

    void searchPromoCode(String promo_code, final AlertDialog dialog) {
        promos_map = new HashMap<>();
        ListOfPatientsRequest.getJSONobj(SummaryActivity.this, "check_promo_code&promo_code=" + promo_code, "promos", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                d("response_promo", response + "");
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray json_mysql = response.getJSONArray("promos");
                        JSONObject obj = json_mysql.getJSONObject(0);

                        d("obj_asd", obj + "");

                        //free gifts still needed to be discussed
                        promos_map.put("is_free_delivery", obj.getString("is_free_delivery"));
                        promos_map.put("percentage_discount", obj.getString("percentage_discount"));
                        promos_map.put("peso_discount", obj.getString("peso_discount"));

                        promos_map.put("product_applicability", obj.getString("product_applicability"));
                        promos_map.put("minimum_purchase_amount", obj.getString("minimum_purchase_amount"));

                        //additional common data
                        promos_map.put("promo_id", obj.getString("id"));
                        promos_map.put("offer_type", obj.getString("offer_type"));
                        promos_map.put("generic_redemption_code", obj.getString("generic_redemption_code"));
                        promos_map.put("start_date", obj.getString("start_date"));
                        promos_map.put("end_date", obj.getString("end_date"));

                        //setting msg for what the user have received
                        final_min_purchase = Double.parseDouble(promos_map.get("minimum_purchase_amount"));
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

//                        if (final_free_gift.equals("1")) {
//                            msg = "You got free gift, upon purchase.";
//                            order_model.setCoupon_discount_type("free_gift");
//                        }

                        if (final_free_delivery.equals("1")) {
                            msg = "You got free delivery.";
                            order_model.setCoupon_discount_type("free_delivery");
                        }

                        dialog.dismiss();
                        showTotalDetails();

                        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(SummaryActivity.this);
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
                        promo_code_btn_layout.setVisibility(View.GONE);
                    } else {
                        promo_progress.setVisibility(View.GONE);
                        message_after_promo_input.setTextColor(getResources().getColor(R.color.list_background_pressed));
                        message_after_promo_input.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
//                                Snackbar.make(root, e + "", Snackbar.LENGTH_INDEFINITE).show();
                    d("err", e + "");
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                make(root, "Network error", Snackbar.LENGTH_INDEFINITE).show();
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
                d("error for sumthing", error + "");
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
                            HashMap<String, String> map = new HashMap<>();
                            map.put("user_id", String.valueOf(getUserID()));
                            map.put("recipient_name", order_model.getRecipient_name());
                            map.put("recipient_address", order_model.getRecipient_address());
                            map.put("recipient_contactNumber", order_model.getRecipient_contactNumber());
                            map.put("branch_server_id", String.valueOf(order_model.getBranch_id())); //needs to be the id of the selected combobox
                            map.put("modeOfDelivery", order_model.getMode_of_delivery());
                            map.put("payment_method", order_model.getPayment_method());
                            map.put("status", "Pending");
                            map.put("coupon_discount", String.valueOf(order_model.getCoupon_discount()));
                            map.put("points_discount", String.valueOf(order_model.getPoints_discount()));
                            map.put("senior_discount", String.valueOf(order_model.getSenior_discount()));
                            map.put("delivery_charge", String.valueOf(delivery_charge_val));
                            map.put("promo_id", String.valueOf(order_model.getPromo_id()));
                            map.put("promo_type", String.valueOf(order_model.getCoupon_discount_type()));
                            map.put("email", patient.getEmail());

                            d("mappings", map.toString());

                            String url = "verify_cash_payment";
                            send(url, map, new RespondListener<JSONObject>() {
                                        @Override
                                        public void getResult(JSONObject response) {
                                            d("b_payresponse", response + "");
                                            try {
                                                if (response.getBoolean("basket_quantity_changed")) {
                                                    letDialogSleep();
                                                    orderCancelled();
                                                } else {
                                                    d("b_check", "true im here");
                                                    //request for orders request
                                                    GetRequest.getJSONobj(getBaseContext(), "get_orders&patient_id=" + getUserID(), "orders", "orders_id", new RespondListener<JSONObject>() {
                                                        @Override
                                                        public void getResult(JSONObject response) {

                                                            d("b_get_orders", response + "");

                                                            GetRequest.getJSONobj(getBaseContext(), "get_order_details&patient_id=" + getUserID(), "order_details", "order_details_id", new RespondListener<JSONObject>() {
                                                                @Override
                                                                public void getResult(JSONObject response) {

                                                                    d("b_get_od", response + "");

                                                                    GetRequest.getJSONobj(getBaseContext(), "get_order_billings&patient_id=" + getUserID(), "billings", "billings_id", new RespondListener<JSONObject>() {
                                                                        @Override
                                                                        public void getResult(JSONObject response) {
                                                                            d("get_ob", response + "");
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
                                                                            d("get_ob_error", error + "");
                                                                        }
                                                                    });
                                                                }
                                                            }, new ErrorListener<VolleyError>() {
                                                                public void getError(VolleyError error) {
                                                                    letDialogSleep();
                                                                    d("b_get_od_error", error + "");
                                                                }
                                                            });

                                                        }
                                                    }, new ErrorListener<VolleyError>() {
                                                        public void getError(VolleyError error) {
                                                            d("b_get_orders_error", error + "");
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
