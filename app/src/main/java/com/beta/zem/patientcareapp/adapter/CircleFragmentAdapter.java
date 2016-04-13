package com.beta.zem.patientcareapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.beta.zem.patientcareapp.Fragment.CircleFragment;

import java.util.ArrayList;

/**
 * Created by User PC on 12/2/2015.
 */
public class CircleFragmentAdapter extends FragmentPagerAdapter {

    public static final ArrayList<String> CONTENT = new ArrayList();
    private int mCount = CONTENT.size();

    public CircleFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return CircleFragment.newInstance(CONTENT.get(position % CONTENT.size()));
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CircleFragmentAdapter.CONTENT.get(position % CONTENT.size());
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}
