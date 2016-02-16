package com.example.zem.patientcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zem.patientcareapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DownlinesAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> hashOfDownlines;

    public DownlinesAdapter(Context context, ArrayList<HashMap<String, String>> hashOfDownlines) {
        this.context = context;
        this.hashOfDownlines = hashOfDownlines;
    }

    @Override
    public int getCount() {
        return hashOfDownlines.size();
    }

    @Override
    public Object getItem(int position) {
        return hashOfDownlines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.downlines_item, parent, false);

        TextView full_name = (TextView) convertView.findViewById(R.id.full_name);
        TextView date_referred = (TextView) convertView.findViewById(R.id.date_referred);
        TextView level = (TextView) convertView.findViewById(R.id.level);

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = formatter.parse(hashOfDownlines.get(position).get("created_at"));

//        format to readable ones
            SimpleDateFormat fd = new SimpleDateFormat("MMM d, yyyy - h:mm a");
            String formatted_date = fd.format(date1);
            date_referred.setText(formatted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        full_name.setText(hashOfDownlines.get(position).get("fname")+" "+hashOfDownlines.get(position).get("lname"));
        level.setText(hashOfDownlines.get(position).get("level"));

        return convertView;
    }
}
