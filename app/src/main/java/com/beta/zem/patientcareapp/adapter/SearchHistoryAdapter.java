package com.beta.zem.patientcareapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beta.zem.patientcareapp.R;

import java.util.ArrayList;

public class SearchHistoryAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> objects;

    TextView history_keyword;

    public SearchHistoryAdapter(Context context, ArrayList<String> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_history, parent, false);

        history_keyword = (TextView) convertView.findViewById(R.id.history_keyword);
        history_keyword.setText(objects.get(position));

        return convertView;
    }
}
