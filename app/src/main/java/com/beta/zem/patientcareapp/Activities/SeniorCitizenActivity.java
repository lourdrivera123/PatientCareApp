package com.beta.zem.patientcareapp.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.ShowPrescriptionDialog;

public class SeniorCitizenActivity extends AppCompatActivity implements View.OnClickListener {

    EditText senior_id_number;
    Button upload_senior_id, next_btn;
    LinearLayout root;
    Toolbar myToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_citizen);
        myToolBar = (Toolbar) findViewById(R.id.myToolBar);

        senior_id_number = (EditText) findViewById(R.id.senior_id_number);
        upload_senior_id = (Button) findViewById(R.id.upload_senior_id);
        root = (LinearLayout) findViewById(R.id.root);

        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Branch");
        myToolBar.setNavigationIcon(R.drawable.ic_back);

        upload_senior_id.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getBaseContext(), ShowPrescriptionDialog.class);
        intent.putExtra("isForSeniorUpload", true);
        intent.putExtra("senior_citizen_id_number", senior_id_number.getText().toString());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();

        return super.onOptionsItemSelected(item);
    }
}
