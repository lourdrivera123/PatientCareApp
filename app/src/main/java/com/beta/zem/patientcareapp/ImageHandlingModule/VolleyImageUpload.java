package com.beta.zem.patientcareapp.ImageHandlingModule;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.beta.zem.patientcareapp.ConfigurationModule.Constants;
import com.beta.zem.patientcareapp.Network.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

import static android.util.Log.d;

/**
 * Created by lourdrivera on 1/24/2016.
 */
public class VolleyImageUpload {

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void uploadImage(final Bitmap bitmap, final String patient_id, final String purpose, Context context){
        RequestQueue queue;
        d("upload_log", "I went here");

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(context,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FILE_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
//                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                        d("response_upload", s);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        d("error_upload", volleyError + "");
                        //Showing toast
//                        Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Getting Image Name
//                String name = editTextName.getText().toString().trim();

                String image = getStringImage(bitmap);
                //Converting Bitmap to String
//                String image = getStringImage(bitmap);
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put("image", image);
                params.put("patient_id", patient_id);
                params.put("purpose", purpose);

                //returning parameters
                return params;
            }
        };

        queue = VolleySingleton.getInstance().getRequestQueue();
        queue.add(stringRequest);
    }
}
