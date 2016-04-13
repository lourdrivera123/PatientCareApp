package com.beta.zem.patientcareapp.ConfigurationModule;


import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

public class Config {

    // PayPal app configuration
    public static final String PAYPAL_CLIENT_ID = "AcCQ4PKc6AtjThCsPPkGSH01nPPe7yJKB1oRT39hpgpUGrFkORy9gmuY5_OF4loXc45RaNrUq4h94PP1";
    public static final String PAYPAL_CLIENT_SECRET = "EH7LOgghxkz-pebLoT1dXSuDo0GiyPI3s1kKaMkp7fKQ25ezZovq5PGQqwVfAAjUpPFcPrAZYA33DcTC";

    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "PHP";

//

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";

    public static final boolean DEVELOPER_MODE = false;

    public static final String IMAGE_POSITION = "Image Position";
}
