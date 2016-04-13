package com.beta.zem.patientcareapp.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beta.zem.patientcareapp.R;

/**
 * Created by Zem on 11/2/2015.
 */
public class BeforeProductDisplayFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.beforeproductdisplay, container, false);

        return rootView;
    }
}
