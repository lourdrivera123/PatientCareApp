package com.example.zem.patientcareapp.Interface;

import com.android.volley.VolleyError;
import com.paypal.android.sdk.payments.PayPalPayment;

/**
 * Created by Zem on 8/3/2015.
 */
public interface RespondListener<T> {

    void getResult(T object);

}
