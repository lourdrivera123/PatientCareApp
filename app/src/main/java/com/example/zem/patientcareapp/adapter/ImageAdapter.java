package com.example.zem.patientcareapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.zem.patientcareapp.ConfigurationModule.Constants;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dexter B. on 8/6/2015.
 */

public class ImageAdapter extends ArrayAdapter {
    String[] image_urls;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    ArrayList<HashMap<String, String>> uploadsByUser;

    public ImageAdapter(Context context, int resource, ArrayList<HashMap<String, String>> uploadsByUser) {

        super(context, R.layout.item_grid_image, uploadsByUser);

        inflater = LayoutInflater.from(context);
        this.uploadsByUser = uploadsByUser;
        image_urls = new String[uploadsByUser.size()];

        for (int x = 0; x < uploadsByUser.size(); x++) {
            image_urls[x] = uploadsByUser.get(x).get("filename");
            System.out.println("image_urls[" + x + "]: " + image_urls[x]);
        }

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_stub)
                .showImageForEmptyUri(R.mipmap.ic_empty)
                .showImageOnFail(R.mipmap.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return image_urls.length;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(uploadsByUser.get(position).get("id"));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_grid_image, parent, false);
        assert view != null;

        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setTag(position);

        com.nostra13.universalimageloader.core.ImageLoader.getInstance()
                .displayImage(Constants.UPLOAD_PATH_URL + "user_" + SidebarActivity.getUserID() + "/" + image_urls[position], imageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                        System.out.println("onLoadingComplete position: " + position + " id: " + uploadsByUser.get(position).get("id"));
                        view.setTag(uploadsByUser.get(position).get("id"));
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        progressBar.setProgress(Math.round(100.0f * current / total));
                    }
                });

        return view;
    }
}