package com.example.zem.patientcareapp.SwipeTabsModule;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.OverlayController;
import com.example.zem.patientcareapp.Fragment.PatientConsultationFragment;
import com.example.zem.patientcareapp.Fragment.ReferralFragment;
import com.example.zem.patientcareapp.Fragment.TrialPrescriptionFragment;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.adapter.MasterTabsAdapter;

public class MasterTabActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private ViewPager viewPager;

    ImageView swipeLeftRight;
    Toolbar toolbar;
    TabLayout tab_layout;

    DbHelper dbHelper;
    OverlayController oc;
    Intent intent;
    int unselected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_tab_layout);
        dbHelper = new DbHelper(this);
        oc = new OverlayController(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showOverLay();

        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        tab_layout.setupWithViewPager(viewPager);
        tab_layout.setOnTabSelectedListener(this);

        intent = getIntent();
        viewPager.setCurrentItem(intent.getIntExtra("selected", 0));
    }

    private void setupViewPager(ViewPager viewPager) {
        MasterTabsAdapter adapter = new MasterTabsAdapter(getSupportFragmentManager());
//        adapter.addFragment(new ReferralFragment(), "Refills & Renewals");
        adapter.addFragment(new ReferralFragment(), "Referral Points");
        adapter.addFragment(new TrialPrescriptionFragment(), "Prescription");
        adapter.addFragment(new PatientConsultationFragment(), "Consultation");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

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

    private void showOverLay() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.mastertabs_overlay);

        if (!oc.checkOverlay("MasterTabs", "check")) {
            swipeLeftRight = (ImageView) dialog.findViewById(R.id.swipeLeftRight);
            LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.masterTabsLayout);
            layout.setAlpha((float) 0.8);

            swipeLeftRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (oc.checkOverlay("MasterTabs", "insert"))
                        dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
