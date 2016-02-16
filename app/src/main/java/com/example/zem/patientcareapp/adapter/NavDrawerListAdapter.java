package com.example.zem.patientcareapp.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zem.patientcareapp.SidebarModule.NavDrawerItem;
import com.example.zem.patientcareapp.R;

public class NavDrawerListAdapter extends BaseAdapter {
    private ArrayList<NavDrawerItem> navDrawerItems;
    LayoutInflater inflater;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.navDrawerItems = navDrawerItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);

        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

        txtTitle.setText(navDrawerItems.get(position).getTitle());
        icon.setImageResource(navDrawerItems.get(position).getIcon());

        // displaying count. check whether it set visible or not
        if (navDrawerItems.get(position).getCounterVisibility()) {
            txtCount.setText(navDrawerItems.get(position).getCount());
        } else
            txtCount.setVisibility(View.GONE);

        return convertView;
    }
}
