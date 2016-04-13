package com.beta.zem.patientcareapp.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.adapter.CircleFragmentAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by User PC on 12/2/2015.
 */

public class CircleFragment extends Fragment {
    private static final String KEY_CONTENT = "TestFragment:Content";
    static ArrayList<String> get_content = new ArrayList();
    ImageView product_image;
    ProgressBar progress;
    static int x = 0;

    private static DisplayImageOptions options;

    public static CircleFragment newInstance(String content) {
        CircleFragment fragment = new CircleFragment();

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append(content).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        fragment.mContent = builder.toString();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_stub)
                .showImageForEmptyUri(R.mipmap.ic_empty)
                .showImageOnFail(R.mipmap.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        get_content = CircleFragmentAdapter.CONTENT;
        x = 0;

        return fragment;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_viewpager, container, false);

        product_image = (ImageView) v.findViewById(R.id.product_image);
        progress = (ProgressBar) v.findViewById(R.id.progress);

        if (get_content.get(0).equals("default")) {
            product_image.setImageDrawable(getResources().getDrawable(R.drawable.no_image_availabe));
        } else {
            try {
                String filepath = "http://159.203.111.108/images/240x240/" + get_content.get(x);

                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(filepath, product_image, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progress.setProgress(0);
                        progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progress.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        progress.setProgress(Math.round(100.0f * current / total));
                    }
                });
            } catch (Exception e) {
                Log.d("CircleFraq1", e + "");
            }
        }

        x++;
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
