package com.example.zem.patientcareapp.CheckoutModule;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.zem.patientcareapp.ConfigurationModule.Config;
import com.example.zem.patientcareapp.ConfigurationModule.Constants;
import com.example.zem.patientcareapp.Controllers.BasketController;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Activities.MainActivity;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Model.OrderModel;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Network.GetRequest;
import com.example.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayPalCheckout extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;

    RequestQueue queue;

    OrderModel order_model;

    private static final int REQUEST_CODE_PAYMENT = 1;

    private List<PayPalItem> productsInCart = new ArrayList<>();

    DbHelper dbHelper;
    BasketController bc;
    PatientController pc;

    PayPalItem[] items_paypal;
    PayPalPaymentDetails paymentDetails;
    PayPalPayment payment;
    BigDecimal subtotal;
    BigDecimal shipping = new BigDecimal("0.0");

    // If you have tax, add it here
    BigDecimal tax = new BigDecimal("0.0");
    BigDecimal amount;
    String recipient_name = "", recipient_address = "", recipient_contactNumber = "", modeOfDelivery = "", payment_method = "";
    String delivery_charge;

    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(this);
        bc = new BasketController();
        pc = new PatientController(this);
        pDialog = new ProgressDialog(this);
        queue = Volley.newRequestQueue(this);
        order_model = (OrderModel) getIntent().getSerializableExtra("order_model");
        delivery_charge = getIntent().getStringExtra("delivery_charge");

        Intent ckintent = getIntent();
        OrderModel om = (OrderModel) ckintent.getSerializableExtra("order_model");

        recipient_name = om.getRecipient_name();
        recipient_address = om.getRecipient_address();
        recipient_contactNumber = om.getRecipient_contactNumber();
        modeOfDelivery = om.getMode_of_delivery();
        payment_method = om.getPayment_method();

//        PayPalConfiguration object = new PayPalConfiguration();
//        object = object.acceptCreditCards(false);

        // Starting PayPal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        startService(intent);

        populateProductsInCart();

    }

    /**
     * Launching PalPay payment activity to complete the payment
     */
    private void launchPayPalPayment() {

        PayPalPayment thingsToBuy = prepareFinalCart();

        Intent intent = new Intent(PayPalCheckout.this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Receiving the PalPay payment response
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e(TAG, confirm.toJSONObject().toString(4));
                        Log.e(TAG, confirm.getPayment().toJSONObject().toString(4));

                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e(TAG, "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);

                        // Now verify the payment on the server side
                        verifyPaymentOnServer(paymentId, payment_client);

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
    }

    public void populateProductsInCart() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();

        String url_raw = "get_basket_details&patient_id=" + SidebarActivity.getUserID();
        ListOfPatientsRequest.getJSONobj(url_raw, "baskets", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    String name, sku;
                    double price;
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray json_mysql = response.getJSONArray("baskets");
                        ArrayList<HashMap<String, String>> items = bc.convertFromJson(PayPalCheckout.this, json_mysql);

                        for (HashMap<String, String> item : items) {
                            price = Double.parseDouble(item.get("price"));
                            double quantity = Double.parseDouble(item.get("quantity"));
                            double total = price * quantity;
                            name = item.get("name");
                            sku = item.get("sku");

                            PayPalItem paypal_item = new PayPalItem(name, 1, new BigDecimal(String.format("%.2f", total)), Config.DEFAULT_CURRENCY, sku);
                            productsInCart.add(paypal_item);

                            Toast.makeText(getApplicationContext(), paypal_item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
                        }

                        if (productsInCart.size() > 0) {
                            items_paypal = new PayPalItem[productsInCart.size()];
                            items_paypal = productsInCart.toArray(items_paypal);

                            subtotal = PayPalItem.getItemTotal(items_paypal);

                            launchPayPalPayment();
                        } else {
                            Toast.makeText(getApplicationContext(), "Cart is empty! Please add few products to cart.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(PayPalCheckout.this, e + "", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError e) {
                dialog.dismiss();
                Toast.makeText(PayPalCheckout.this, "Please check your Internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Preparing final cart amount that needs to be sent to PayPal for payment
     */
    private PayPalPayment prepareFinalCart() {

        paymentDetails = new PayPalPaymentDetails(
                shipping, subtotal, tax);

        BigDecimal coupon_discount = new BigDecimal(order_model.getCoupon_discount());
        BigDecimal points_discount = new BigDecimal(order_model.getPoints_discount());
        amount = subtotal.add(shipping).add(tax).subtract(coupon_discount).subtract(points_discount);

        payment = new PayPalPayment(
                amount,
                Config.DEFAULT_CURRENCY,
                "You will be paying - ",
                Config.PAYMENT_INTENT);

//        payment.items(items).paymentDetails(paymentDetails);

        return payment;

    }

    /**
     * Verifying the mobile payment on the server to avoid fraudulent payment
     */
    private void verifyPaymentOnServer(final String paymentId,
                                       final String payment_client) {
        // Showing progress dialog before making request
        pDialog.setMessage("Verifying payment...");
        showpDialog();

        StringRequest verifyReq = new StringRequest(Request.Method.POST,
                Constants.URL_VERIFY_PAYMENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //request for orders request
                GetRequest.getJSONobj(getBaseContext(), "get_orders&patient_id=" + SidebarActivity.getUserID(), "orders", "orders_id", new RespondListener<JSONObject>() {
                    @Override
                    public void getResult(JSONObject response) {
                        //request for order_details request
                        GetRequest.getJSONobj(getBaseContext(), "get_order_details&patient_id=" + SidebarActivity.getUserID(), "order_details", "order_details_id", new RespondListener<JSONObject>() {
                            @Override
                            public void getResult(JSONObject response) {

                                //request for order_details request
                                GetRequest.getJSONobj(getBaseContext(), "get_order_billings&patient_id=" + SidebarActivity.getUserID(), "billings", "billings_id", new RespondListener<JSONObject>() {
                                    @Override
                                    public void getResult(JSONObject response) {
                                        try {
                                            productsInCart.clear();
                                            String timestamp_ordered = response.getString("server_timestamp");

                                            Intent order_intent = new Intent(getBaseContext(), SidebarActivity.class);
                                            order_intent.putExtra("payment_from", "paypal");
                                            order_intent.putExtra("timestamp_ordered", timestamp_ordered);
                                            order_intent.putExtra("select", 5);
                                            startActivity(order_intent);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new ErrorListener<VolleyError>() {
                                    public void getError(VolleyError error) {
                                        Log.d("Error", error + "");
                                        Toast.makeText(getBaseContext(), "Couldn't refresh list. Please check your Internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }, new ErrorListener<VolleyError>() {
                            public void getError(VolleyError error) {
                                Log.d("Error", error + "");
                                Toast.makeText(getBaseContext(), "Couldn't refresh list. Please check your Internet connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, new ErrorListener<VolleyError>() {
                    public void getError(VolleyError error) {
                        Log.d("Error", error + "");
                        Toast.makeText(getBaseContext(), "Couldn't refresh list. Please check your Internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();

                // hiding the progress dialog
                hidepDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Verify Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hiding the progress dialog
                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Patient patient = pc.getloginPatient(SidebarActivity.getUname());

                Map<String, String> map = new HashMap<>();
                map.put("paymentId", paymentId);
                map.put("paymentClientJson", payment_client);
                map.put("patient_id", String.valueOf(patient.getServerID()));
                map.put("user_id", String.valueOf(SidebarActivity.getUserID()));
                map.put("branch_server_id", String.valueOf(order_model.getBranch_id()));
                map.put("recipient_name", recipient_name);
                map.put("recipient_address", recipient_address);
                map.put("recipient_contactNumber", recipient_contactNumber);
                map.put("modeOfDelivery", modeOfDelivery);
                map.put("payment_method", payment_method);
                map.put("status", "open");
                map.put("coupon_discount", String.valueOf(order_model.getCoupon_discount()));
                map.put("points_discount", String.valueOf(order_model.getPoints_discount()));
                map.put("delivery_charge", String.valueOf(delivery_charge));
                map.put("promo_id", String.valueOf(order_model.getPromo_id()));
                map.put("promo_type", String.valueOf(order_model.getCoupon_discount_type()));
                map.put("email", patient.getEmail());

                Log.d("maps shit", map + "");

                return map;
            }
        };

        // Setting timeout to volley request as verification request takes sometime
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);

        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(verifyReq);
        queue.add(verifyReq);
    }
}