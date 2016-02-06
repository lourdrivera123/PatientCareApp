package com.example.zem.patientcareapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zem.patientcareapp.Activities.ProductsActivity;
import com.example.zem.patientcareapp.Activities.SelectedProductActivity;
import com.example.zem.patientcareapp.ConfigurationModule.Constants;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.Controllers.PatientPrescriptionController;
import com.example.zem.patientcareapp.ImageHandlingModule.AndroidMultipartEntity;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowPrescriptionDialog extends AppCompatActivity implements View.OnClickListener {
    public static ArrayList<HashMap<String, String>> uploadsByUser;
    LinearLayout pick_camera_layout, pick_gallery_layout;
    ProgressBar progressBar;
    Dialog upload_dialog;
    ArrayList<String> arrayOfPrescriptions;
    Helpers helper;
    PatientPrescriptionController ppc;
    String imageFileUri;
    String filePath = null;
    long totalSize = 0;
    int patientID;
    private TextView txtPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gallery_camera);

        pick_camera_layout = (LinearLayout) findViewById(R.id.pick_camera_layout);
        pick_gallery_layout = (LinearLayout) findViewById(R.id.pick_gallery_layout);

        pick_camera_layout.setOnClickListener(this);
        pick_gallery_layout.setOnClickListener(this);

        helper = new Helpers();
        ppc = new PatientPrescriptionController(this);
        patientID = SidebarActivity.getUserID();
        arrayOfPrescriptions = refreshPrescriptionList();

        upload_dialog = new Dialog(this);
        upload_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        upload_dialog.setContentView(R.layout.activity_upload);

        txtPercentage = (TextView) upload_dialog.findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) upload_dialog.findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pick_camera_layout:
                Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(this)));
                startActivityForResult(intent_camera, 1337);
                break;

            case R.id.pick_gallery_layout:
                Intent intent_gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent_gallery.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                startActivityForResult(intent_gallery, 111);
                break;
        }
    }

    private File getTempFile(Context context) {
        final File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (!path.exists()) {
            path.mkdir();
        }
        return new File(path, "image.tmp");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == this.RESULT_OK) { //GALLERY
            if (data.getData() != null && !data.getData().equals(Uri.EMPTY)) {
                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};

                Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String path = cursor.getString(columnIndex);

                filePath = path;
                showProgressbar();
                new UploadFileToServer().execute();

                cursor.close();
            }
        } else if (requestCode == 1337 && resultCode == this.RESULT_OK) { //CAMERA
            final File file = getTempFile(this);
            try {
                Bitmap captureBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(file));

                Uri tempUri = getImageUri(this, captureBmp);
                File finalFile = new File(getRealPathFromURI(tempUri));
                String path = String.valueOf(finalFile);

                filePath = path;
                showProgressbar();

                new UploadFileToServer().execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void showProgressbar() {
        upload_dialog.show();
    }

    public ArrayList<String> refreshPrescriptionList() {
        uploadsByUser = ppc.getPrescriptionByUserID(patientID);
        ArrayList<String> prescriptionArray = new ArrayList();

        for (int x = 0; x < uploadsByUser.size(); x++) {
            prescriptionArray.add(uploadsByUser.get(x).get(PatientPrescriptionController.PRESCRIPTIONS_FILENAME));
        }
        return prescriptionArray;
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString;

            int patientID = SidebarActivity.getUserID();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost;

            boolean isForSeniorUpload = getIntent().getBooleanExtra("isForSeniorUpload", false);
                httppost = new HttpPost(Constants.FILE_UPLOAD_URL);
            try {
                AndroidMultipartEntity entity = new AndroidMultipartEntity(
                        new AndroidMultipartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("patient_id", new StringBody("" + patientID));
                entity.addPart("image", new FileBody(sourceFile));

                if(isForSeniorUpload){
                    entity.addPart("purpose", new StringBody("senior_citizen_upload"));
                    entity.addPart("senior_citizen_id_number", new StringBody(getIntent().getStringExtra("senior_citizen_id_number")));
                } else {
                    entity.addPart("purpose", new StringBody("prescription_upload"));
                }

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("response_showprescd", result + "");

            JSONObject jObject;
            String image_url = "";
            int serverID = 0;
            try {
                jObject = new JSONObject(result);
                if (jObject.getBoolean("error")) {
                    uploadfaileddialog(jObject.getString("message"), "Upload Failed");
                } else {
                    image_url = jObject.getString("file_name");
                    serverID = jObject.getInt("server_id");

                    //put the refresh grid here or the display newly added image here
                    if (ppc.insertUploadOnPrescription(patientID, image_url, serverID)) {
                        arrayOfPrescriptions = refreshPrescriptionList();
                        ShowPrescriptionDialog.this.finish();
                        ProductsActivity.is_finish = serverID;
                        SelectedProductActivity.is_resumed = serverID;
                    } else
                        Toast.makeText(ShowPrescriptionDialog.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                upload_dialog.dismiss();
                uploadfaileddialog("Sorry, we cannot upload your file", "Upload Failed");
            }
            upload_dialog.dismiss();

            super.onPostExecute(result);
        }

        void uploadfaileddialog(String msg, String title) {
            AlertDialog.Builder uploadfaildialog = new AlertDialog.Builder(new ContextThemeWrapper(ShowPrescriptionDialog.this, R.style.myDialog));

            uploadfaildialog.setTitle(title);
            uploadfaildialog.setMessage(msg);
            uploadfaildialog.setCancelable(false);
            uploadfaildialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            uploadfaildialog.show();
        }
    }
}
