package com.example.zem.patientcareapp.SwipeTabsModule;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.example.zem.patientcareapp.ConfigurationModule.Constants;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.OverlayController;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Fragment.AccountFragment;
import com.example.zem.patientcareapp.Fragment.ContactsFragment;
import com.example.zem.patientcareapp.Fragment.SignUpFragment;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.ImageHandlingModule.AndroidMultipartEntity;
import com.example.zem.patientcareapp.Activities.MainActivity;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Interface.StringRespondListener;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.Network.StringRequests;
import com.example.zem.patientcareapp.Network.VolleySingleton;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.Activities.ReferralActivity;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.example.zem.patientcareapp.adapter.TabsPagerAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditTabsActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {
    public static Patient patient;
    RequestQueue queue;
    Patient editUser;
    DbHelper dbHelper;
    PatientController pc;
    OverlayController oc;

    Helpers helpers;
    SignUpFragment fragment;

    private ViewPager viewPager;
    TabLayout tab_layout;
    Toolbar toolbar;
    LinearLayout root;
    public boolean hasError = true, hasError2 = true, hasError3 = true;

    // SIGN UP FRAGMENT
    EditText birthday, fname, lname, mname, height, weight, occupation;
    RadioGroup sex;
    Spinner civil_status_spinner;
    String s_fname, s_lname, s_mname, s_birthdate, s_sex, s_civil_status, s_height, s_weight, s_occupation;

    // CONTACTS FRAGMENT
    EditText address_street, optional_address_line, tel_no, cell_no, email;
    Spinner address_region, address_barangay, address_city_municipality, address_province;
    int i_region_id;
    String s_street, s_optional_address, s_email, s_tel_no, s_cell_no;

    // ACCOUNT INFO FRAGMENT
    String username = "", pass = "", s_filepath = "";
    ImageView image_holder;

    public static final String SIGNUP_REQUEST = "signup", EDIT_REQUEST = "edit";
    String purpose = "", image_url = "", url;
    public static int signup_int = 0, edit_int = 0;
    int check = 0, limit = 4, count = 0, unselected;
    long totalSize = 0;

    JSONObject patient_json_object_mysql = null;
    JSONArray patient_json_array_mysql = null;
    public static SharedPreferences sharedpreferences;

    private TextView txtPercentage;

    ProgressBar progressBar;
    Dialog upload_dialog;
    public static Intent intent;
    public static AppCompatDialog pDialog;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_tabs_layout);

        dbHelper = new DbHelper(this);
        oc = new OverlayController(this);
        helpers = new Helpers();
        patient = new Patient();
        fragment = new SignUpFragment();
        pc = new PatientController(this);

        showOverLay();
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        intent = getIntent();
        signup_int = intent.getIntExtra(SIGNUP_REQUEST, 0);
        edit_int = intent.getIntExtra(EDIT_REQUEST, 0);

        queue = VolleySingleton.getInstance().getRequestQueue();
        url = Constants.POST_URL;

        showBeautifulDialog();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        root = (LinearLayout) findViewById(R.id.root);
        setupViewPager(viewPager);
        tab_layout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        tab_layout.setOnTabSelectedListener(this);

        if (signup_int > 0) {
            patient.setReferred_byUser(intent.getStringExtra("referred_by_User"));
            patient.setReferred_byDoctor(intent.getStringExtra("referred_by_Doctor"));
        }
    }

    void showBeautifulDialog() {
        builder = new AlertDialog.Builder(EditTabsActivity.this);
        builder.setView(R.layout.progress_stuffing);
        builder.setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    void uploadfaileddialog(String msg, String title) {
        AlertDialog.Builder uploadfaildialog = new AlertDialog.Builder(EditTabsActivity.this);

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

    void letDialogSleep() {
        pDialog.dismiss();
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

    private void setupViewPager(ViewPager viewPager) {
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignUpFragment(), "Gen. Info");
        adapter.addFragment(new ContactsFragment(), "Contact & Addr.");
        adapter.addFragment(new AccountFragment(), "Acct. Info");
        viewPager.setAdapter(adapter);
    }

    public HashMap<String, String> setParams(String request) {
        HashMap<String, String> params = new HashMap<>();
        if (request.equals("update")) {
            if (AccountFragment.checkIfChangedPass > 0) {
                params.put("password", AccountFragment.NEW_PASS);
                patient.setPassword(AccountFragment.NEW_PASS);
            }
            params.put("id", String.valueOf(patient.getServerID()));
            params.put("request", "crud");
            params.put("action", "update");
            params.put("table", "patients");
        } else {
            patient.setPhoto("");

            params.put("request", "register");
            params.put("password", patient.getPassword());
            params.put("action", "insert");
            params.put("table", "patients");
            params.put("photo", patient.getPhoto());
            params.put("referral_id", patient.getReferral_id());
            params.put("referred_byUser", patient.getReferred_byUser());
            params.put("referred_byDoctor", patient.getReferred_byDoctor());
        }
        params.put("fname", patient.getFname());
        params.put("mname", patient.getMname());
        params.put("lname", patient.getLname());
        params.put("username", patient.getUsername());
        params.put("occupation", patient.getOccupation());
        params.put("birthdate", patient.getBirthdate());
        params.put("sex", patient.getSex());
        params.put("civil_status", patient.getCivil_status());
        params.put("height", patient.getHeight());
        params.put("weight", patient.getWeight());
        params.put("optional_address", patient.getOptional_address());
        params.put("address_street", patient.getAddress_street());
        params.put("tel_no", patient.getTel_no());
        params.put("mobile_no", patient.getMobile_no());
        params.put("email_address", patient.getEmail());
        params.put("address_barangay_id", ContactsFragment.barangay_id);

        return params;
    }

    public void readFromSignUp() {
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        mname = (EditText) findViewById(R.id.mname);
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        occupation = (EditText) findViewById(R.id.occupation);
        civil_status_spinner = (Spinner) findViewById(R.id.civil_status);
        sex = (RadioGroup) findViewById(R.id.sex);
        int selectedId = sex.getCheckedRadioButtonId();

        s_fname = fname.getText().toString();
        s_lname = lname.getText().toString();
        s_mname = mname.getText().toString();
        s_height = height.getText().toString();
        s_weight = weight.getText().toString();
        s_occupation = occupation.getText().toString();
        s_civil_status = civil_status_spinner.getSelectedItem().toString();
        s_sex = selectedId == R.id.male_rb ? "Male" : "Female";

        birthday = (EditText) findViewById(R.id.birthday);
        s_birthdate = birthday.getText().toString();

        limit = 5;
        count = 0;

        if (s_fname.equals("")) {
            fname.setError("Field Required");
        } else {
            patient.setFname(s_fname);
            count++;
        }

        if (s_lname.equals("")) {
            lname.setError("Field Required");
        } else {
            patient.setLname(s_lname);
            count++;
        }

        if (s_birthdate.equals("")) {
            birthday.setError("Field Required");
        } else if (s_birthdate.contains("m") || s_birthdate.contains("d") || s_birthdate.contains("y")) {
            birthday.setError("Invalid date");
        } else {
            patient.setBirthdate(s_birthdate);
            count++;
        }

        if (s_height.equals("")) {
            height.setError("Field Required");
        } else {
            patient.setHeight(s_height);
            count++;
        }

        if (s_weight.equals("")) {
            weight.setError("Field Required");
        } else {
            patient.setWeight(s_weight);
            count++;
        }

        this.hasError = count != limit;

        patient.setSex(s_sex);

        //NOT REQUIRED VARIABLES
        if (s_mname.equals(""))
            s_mname = "";

        if (s_occupation.equals(""))
            s_occupation = "";

        patient.setMname(s_mname);
        patient.setOccupation(s_occupation);
        patient.setCivil_status(s_civil_status);
    }

    public void validateAtPosition2() {
        address_street = (EditText) findViewById(R.id.address_street);
        optional_address_line = (EditText) findViewById(R.id.optional_address_line);
        email = (EditText) findViewById(R.id.email);
        tel_no = (EditText) findViewById(R.id.tel_no);
        cell_no = (EditText) findViewById(R.id.cell_no);
        address_region = (Spinner) findViewById(R.id.address_region);
        address_barangay = (Spinner) findViewById(R.id.address_barangay);
        address_city_municipality = (Spinner) findViewById(R.id.address_city_municipality);
        address_province = (Spinner) findViewById(R.id.address_province);
        i_region_id = Integer.parseInt(ContactsFragment.hashOfRegions.get(address_region.getSelectedItemPosition()).get("region_server_id"));

        s_street = address_street.getText().toString();

        s_email = email.getText().toString();
        s_tel_no = tel_no.getText().toString();
        s_cell_no = cell_no.getText().toString();
        s_optional_address = optional_address_line.getText().toString();

        count = 0;
        limit = 4;

        if (s_street.equals("")) {
            address_street.setError("Field Required");
        } else {
            patient.setAddress_street(s_street);
            count++;
        }

        if (i_region_id == 0) {
            TextView errorText = (TextView) address_region.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
        } else {
            count++;
        }

        if (s_cell_no.equals("")) {
            cell_no.setError("Field Required");
        } else {
            patient.setMobile_no(s_cell_no);
            count++;
        }

        if (s_email.equals(""))
            email.setError("Field required");
        else if (!isEmailValid(s_email))
            email.setError("Please input a valid email");
        else {
            patient.setEmail(s_email);
            count++;
        }

        this.hasError2 = count != limit;

        patient.setTel_no(s_tel_no);
        patient.setOptional_address(s_optional_address);
    }

    public void validateUserAccountInfo() {
        EditText et_username = (EditText) findViewById(R.id.username);
        image_holder = (ImageView) findViewById(R.id.image_holder);

        username = et_username.getText().toString();

        count = 0;
        limit = 3;

        if (signup_int > 0) {
            EditText password = (EditText) findViewById(R.id.password);
            EditText confirm_password = (EditText) findViewById(R.id.confirm_password);

            if (password.getText().toString().equals("")) {
                password.setError("Field is required");
            } else if (!validatechars(password.getText().toString())) {
                password.setError("Minimum of 6 characters");
            } else {
                count++;
            }

            if (confirm_password.getText().toString().equals("")) {
                confirm_password.setError("Field is required");
            } else if (!validatechars(confirm_password.getText().toString())) {
                confirm_password.setError("Minimum of 6 characters");
            } else if (!confirm_password.getText().toString().equals(password.getText().toString())) {
                Log.d("sad", confirm_password.getText().toString() + " , " + password.getText().toString());
                confirm_password.setError("Password does not match");
            } else {
                pass = helpers.md5(confirm_password.getText().toString());
                patient.setPassword(pass);
                count++;
            }

        } else
            count += 2;

        if (username.equals("")) {
            et_username.setError("Field is required");
        } else if (!validatechars(username)) {
            et_username.setError("Minimum of 6 characters");
        } else {
            patient.setUsername(username);
            count++;
        }

        if (count == limit)
            this.hasError3 = false;
    }

    public boolean validatechars(String str) {
        Log.d("str_nums", String.valueOf(str.length()));
        return str.length() >= 6;
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            try {
                if (data.getData() != null && !data.getData().equals(Uri.EMPTY)) {
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    s_filepath = cursor.getString(columnIndex);
                    cursor.close();

                    if (edit_int > 0)
                        purpose = "profile_upload_update";
                    else
                        purpose = "profile_upload_insert";

                    showProgressbar();
                    new UploadFileToServer().execute();
                    check = 23;
                } else
                    patient.setPhoto("");
            } catch (Exception e) {
                Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void showProgressbar() {
        upload_dialog = new Dialog(this);
        upload_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        upload_dialog.setContentView(R.layout.activity_upload);

        txtPercentage = (TextView) upload_dialog.findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) upload_dialog.findViewById(R.id.progressBar);
        upload_dialog.show();
    }

    //////////////////////ViewPager Listener/////////////////////////////////////
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            readFromSignUp();

        } else if (position == 1) {
            if (unselected == 0)
                readFromSignUp();
            validateAtPosition2();

        } else if (position == 2) {
            final Button choose_image_btn = (Button) findViewById(R.id.choose_image_btn);
            image_holder = (ImageView) findViewById(R.id.image_holder);
            TextView changePassword = (TextView) findViewById(R.id.changePassword);

            if (signup_int > 0)
                changePassword.setVisibility(View.GONE);

            choose_image_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 111);
                }
            });

            if (unselected == 1)
                validateAtPosition2();
            else if (unselected == 0)
                readFromSignUp();

            Button btn_submit = (Button) findViewById(R.id.btn_save);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    validateUserAccountInfo();
                    if (hasError) {
                        viewPager.setCurrentItem(0);
                    } else if (hasError2) {
                        viewPager.setCurrentItem(1);
                    } else {
                        validateUserAccountInfo();

                        if (!hasError3) {
                            patient.setBarangay_id(Integer.parseInt(ContactsFragment.barangay_id));
                            patient.setBarangay(ContactsFragment.address_barangay.getSelectedItem().toString());
                            patient.setMunicipality(ContactsFragment.address_city_municipality.getSelectedItem().toString());
                            patient.setProvince(ContactsFragment.address_province.getSelectedItem().toString());
                            patient.setRegion(ContactsFragment.address_region.getSelectedItem().toString());

                            if (edit_int > 0) {
                                showBeautifulDialog();
                                editUser = pc.getloginPatient(SidebarActivity.getUname());

                                patient.setServerID(editUser.getServerID());
                                HashMap<String, String> params = setParams("update");

                                PostRequest.send(getBaseContext(), params, new RespondListener<JSONObject>() {
                                    @Override
                                    public void getResult(JSONObject response) {
                                        int success = 0;

                                        try {
                                            success = response.getInt("success");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        if (success == 1) {
                                            if (pc.savePatient(patient_json_object_mysql, patient, "update")) {
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putString(MainActivity.name, patient.getUsername());
                                                editor.putString(MainActivity.pass, patient.getPassword());
                                                editor.apply();

                                                Snackbar.make(root, "Updated successfully", Snackbar.LENGTH_SHORT).show();
                                                EditTabsActivity.this.finish();
                                            } else
                                                Snackbar.make(root, "Something went wrong", Snackbar.LENGTH_SHORT).show();
                                        } else
                                            Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                                        letDialogSleep();
                                    }
                                }, new ErrorListener<VolleyError>() {
                                    public void getError(VolleyError error) {
                                        letDialogSleep();
                                        Snackbar.make(root, "Networ error", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                showBeautifulDialog();

                                final HashMap<String, String> params = setParams("register");

                                StringRequests.getString(EditTabsActivity.this, "api/generate/referral_id", new StringRespondListener<String>() {

                                    @Override
                                    public void getResult(String response) {
                                        patient.setReferral_id(response);
                                        params.put("referral_id", patient.getReferral_id());

                                        PostRequest.send(getBaseContext(), params, new RespondListener<JSONObject>() {
                                            @Override
                                            public void getResult(JSONObject response) {
                                                try {
                                                    int success = response.getInt("success");

                                                    if (success == 2) {
                                                        Snackbar.make(root, "Username is already registered", Snackbar.LENGTH_SHORT).show();
                                                    } else if (success == 1) {
                                                        patient_json_array_mysql = response.getJSONArray("patient");
                                                        patient_json_object_mysql = patient_json_array_mysql.getJSONObject(0);

                                                        if (ReferralActivity.cpd_id > 0) {
                                                            HashMap<String, String> map = new HashMap<>();
                                                            map.put("request", "crud");
                                                            map.put("action", "update");
                                                            map.put("table", "clinic_patient_doctor");
                                                            map.put("id", String.valueOf(ReferralActivity.cpd_id));
                                                            map.put("patient_id", String.valueOf(patient_json_object_mysql.getInt("id")));

                                                            PostRequest.send(EditTabsActivity.this, map, new RespondListener<JSONObject>() {
                                                                @Override
                                                                public void getResult(JSONObject response) {
                                                                    try {
                                                                        int success = response.getInt("success");

                                                                        if (success != 1)
                                                                            Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();

                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }, new ErrorListener<VolleyError>() {
                                                                public void getError(VolleyError error) {
                                                                    Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }

                                                        if (pc.savePatient(patient_json_object_mysql, patient, "insert")) {
                                                            SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                                            editor.putString(MainActivity.name, patient.getUsername());
                                                            editor.putString(MainActivity.pass, patient.getPassword());
                                                            editor.apply();

                                                            startActivity(new Intent(getBaseContext(), SidebarActivity.class));
                                                            EditTabsActivity.this.finish();
                                                        } else
                                                            Snackbar.make(root, "Something went wrong", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                                                    Log.d("EditTabs2", e + "");
                                                }
                                                letDialogSleep();
                                            }
                                        }, new ErrorListener<VolleyError>() {
                                            public void getError(VolleyError error) {
                                                letDialogSleep();
                                                Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }, new ErrorListener<VolleyError>() {
                                    public void getError(VolleyError error) {
                                        letDialogSleep();
                                        Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    ///////////////////////////////////////////TabLayout Listener///////////////////////////
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        unselected = tab.getPosition();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /////////////////////////////////////////////////////
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

            int patientID = pc.getCurrentLoggedInPatient().getServerID();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.FILE_UPLOAD_URL);

            try {
                AndroidMultipartEntity entity = new AndroidMultipartEntity(
                        new AndroidMultipartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(s_filepath);

                // Adding file data to http body
                entity.addPart("patient_id", new StringBody("" + patientID));
                entity.addPart("image", new FileBody(sourceFile));
                entity.addPart("purpose", new StringBody(purpose));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity); // Server response
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
            try {
                JSONObject jObject;
                jObject = new JSONObject(result);
                if (jObject.getBoolean("error")) {
                    upload_dialog.dismiss();
                    uploadfaileddialog(jObject.getString("message"), "Upload Failed");
                } else {
                    image_url = jObject.getString("file_name");
                    patient.setPhoto(image_url);

                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

                    if (pc.updatePatientImage(image_url, SidebarActivity.getUserID()))
                        Log.d("updated photo", "true");
                    else
                        Log.d("updated photo", "false");

                    helpers.setImage(image_url, progressBar, image_holder);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                uploadfaileddialog("Sorry, we cannot upload your file", "Upload Failed");
            }
            upload_dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private void showOverLay() {
        if (!oc.checkOverlay("EditTabs", "check")) {
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.edittabs_overlay);

            LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.editTabsLayout);
            layout.setAlpha((float) 0.8);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (oc.checkOverlay("EditTabs", "insert"))
                        dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}