package com.example.zem.patientcareapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zem.patientcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zem on 11/5/2015.
 */
public class BranchesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> hashOfBranches;

    public BranchesAdapter(Context context, ArrayList<HashMap<String, String>> hashOfBranches){
        this.context = context;
        this.hashOfBranches = hashOfBranches;
    }

    @Override
    public int getCount() {
        return hashOfBranches.size();
    }

    @Override
    public Object getItem(int position) {
        return hashOfBranches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_branches_info, null);
        }

        TextView branch_name = (TextView) convertView.findViewById(R.id.branch_name);
        TextView branch_address = (TextView) convertView.findViewById(R.id.branch_address);

        branch_name.setText(hashOfBranches.get(position).get("name"));
        branch_address.setText(hashOfBranches.get(position).get("full_address"));

        return convertView;
    }
}
