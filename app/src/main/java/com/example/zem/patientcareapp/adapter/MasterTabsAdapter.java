package com.example.zem.patientcareapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.zem.patientcareapp.Fragment.PatientConsultationFragment;
import com.example.zem.patientcareapp.Fragment.ReferralFragment;
import com.example.zem.patientcareapp.Fragment.TrialPrescriptionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Esel on 5/5/2015.
 */
public class MasterTabsAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList();
    private final List<String> mFragmentTitleList = new ArrayList();

    public MasterTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
}
