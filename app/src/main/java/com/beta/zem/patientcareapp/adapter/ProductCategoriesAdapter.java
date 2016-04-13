package com.beta.zem.patientcareapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.beta.zem.patientcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductCategoriesAdapter extends ArrayAdapter {
    LayoutInflater inflater;

    TextView category_name, view_category;
    ImageView category_icon;

    ArrayList<HashMap<String, String>> objects;

    public ProductCategoriesAdapter(Context context, int resource, ArrayList<HashMap<String, String>> objects) {
        super(context, resource, objects);

        inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_product_categories, parent, false);

        category_icon = (ImageView) convertView.findViewById(R.id.category_icon);
        category_name = (TextView) convertView.findViewById(R.id.category_name);
        view_category = (TextView) convertView.findViewById(R.id.view_category);

        try {
            String background_color = objects.get(position).get("background_color");

            category_name.setText(objects.get(position).get("category_name").toUpperCase());
            category_icon.setBackgroundColor(Color.parseColor(background_color));
            view_category.setTextColor(darkenColor(background_color));
        } catch (Exception e) {
            category_icon.setBackgroundColor(Color.parseColor("#3EA055"));
            view_category.setTextColor(darkenColor("#3EA055"));
        }

        return convertView;
    }

    int darkenColor(String color_string) {
        int color = Color.parseColor(color_string);

        int r = Color.red(color);
        int b = Color.blue(color);
        int g = Color.green(color);

        return Color.rgb((int) (r * .9), (int) (g * .9), (int) (b * .9));
    }
}
