package com.beta.zem.patientcareapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Controllers.MessageController;
import com.beta.zem.patientcareapp.Controllers.UpdateController;
import com.beta.zem.patientcareapp.Model.Messages;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;
import com.beta.zem.patientcareapp.Network.PostRequest;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by User PC on 10/13/2015.
 */

public class MessageActivity extends AppCompatActivity {
    TextView date, subject, message;
    Toolbar messages_toolbar;

    DbHelper db;
    Messages msg;
    public static AppCompatDialog pDialog;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity_layout);

        date = (TextView) findViewById(R.id.date);
        subject = (TextView) findViewById(R.id.subject);
        message = (TextView) findViewById(R.id.message);

        messages_toolbar = (Toolbar) findViewById(R.id.messages_toolbar);
        setSupportActionBar(messages_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Messages");
        messages_toolbar.setNavigationIcon(R.drawable.ic_back);

        db = new DbHelper(this);
        MessageController mc = new MessageController(this);
        Intent intent = getIntent();
        int server_id = intent.getIntExtra("serverID", 0);

        msg = mc.getSpecificMessage(server_id);

        if (msg.getIsRead() == 0) {
            HashMap<String, String> hashMap = new HashMap();
            hashMap.put("request", "crud");
            hashMap.put("table", "messages");
            hashMap.put("action", "update");
            hashMap.put("id", String.valueOf(msg.getServerID()));
            hashMap.put("patient_id", String.valueOf(SidebarActivity.getUserID()));
            hashMap.put("isRead", String.valueOf(1));

//            final ProgressDialog pdialog = new ProgressDialog(this);
//            pdialog.setCancelable(false);
//            pdialog.setMessage("Loading...");
//            pdialog.show();
            showBeautifulDialog();

            PostRequest.send(this, hashMap, new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {
                    try {
                        int success = response.getInt("success");

                        if (success == 1) {
                            UpdateController uc = new UpdateController(getBaseContext());
                            if (uc.updateIsRead_table(msg.getServerID(), "messages", "serverID") == false)
                                Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getBaseContext(), "Server error occurred", Toast.LENGTH_SHORT).show();
                        Log.d("MsgActvity", e + "");
                    }
                    letDialogSleep();
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    letDialogSleep();
                    Log.d("MsgActvity", error + "");
                    Toast.makeText(getBaseContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        }

        date.setText(msg.getDate());
        subject.setText("Subject: " + msg.getSubject());
        message.setText(msg.getContent());
    }

    void showBeautifulDialog() {
        builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setView(R.layout.progress_stuffing);
        builder.setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    void letDialogSleep() {
        pDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
