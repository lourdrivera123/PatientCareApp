package com.beta.zem.patientcareapp.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.beta.zem.patientcareapp.Activities.ProductsActivity;
import com.beta.zem.patientcareapp.Activities.SelectedProductActivity;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Controllers.PatientController;
import com.beta.zem.patientcareapp.Controllers.PatientPrescriptionController;
import com.beta.zem.patientcareapp.ConfigurationModule.Config;
import com.beta.zem.patientcareapp.ConfigurationModule.Constants;
import com.beta.zem.patientcareapp.ConfigurationModule.Helpers;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;
import com.beta.zem.patientcareapp.Network.PostRequest;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.ShowPrescriptionDialog;
import com.beta.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.beta.zem.patientcareapp.SwipeTabsModule.ViewPagerActivity;
import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TrialPrescriptionFragment extends Fragment implements View.OnClickListener {
    GridView gridView;
    RelativeLayout root;
    FloatingActionButton add_pres;

    public static ArrayList<HashMap<String, String>> uploadsByUser;
    ArrayList<String> arrayOfPrescriptions;

    Helpers helper;
    ImageAdapter imgAdapter;
    DbHelper dbhelper;
    PatientController pc;
    PatientPrescriptionController ppc;
    View rootView;

    int patientID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_trial_prescription_fragment, container, false);

        dbhelper = new DbHelper(getActivity());
        pc = new PatientController(getActivity());
        ppc = new PatientPrescriptionController(getActivity());
        helper = new Helpers();

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        add_pres = (FloatingActionButton) rootView.findViewById(R.id.add_pres);
        root = (RelativeLayout) rootView.findViewById(R.id.root);

        patientID = SidebarActivity.getUserID();
        arrayOfPrescriptions = refreshPrescriptionList();

        add_pres.setOnClickListener(this);

        arrayOfPrescriptions = refreshPrescriptionList();
        imgAdapter = new ImageAdapter(getActivity(), arrayOfPrescriptions);
        gridView.setAdapter(imgAdapter);
        gridView.setOnCreateContextMenuListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        ProductsActivity.is_finish = 0;
        SelectedProductActivity.is_resumed = 0;
        arrayOfPrescriptions = refreshPrescriptionList();
        gridView.setAdapter(new ImageAdapter(getActivity(), arrayOfPrescriptions));
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.delete_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == R.id.delete_context) {
            AlertDialog.Builder delete = new AlertDialog.Builder(getActivity());
            delete.setTitle("Delete?");
            delete.setNegativeButton("No", null);
            delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final int serverID = Integer.parseInt(uploadsByUser.get(menuInfo.position).get(PatientPrescriptionController.PRESCRIPTIONS_SERVER_ID));
                    final String filename = uploadsByUser.get(menuInfo.position).get(PatientPrescriptionController.PRESCRIPTIONS_FILENAME);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("table", "patient_prescriptions");
                    hashMap.put("request", "crud");
                    hashMap.put("action", "delete_prescription");
                    hashMap.put("id", String.valueOf(serverID));
                    hashMap.put("url", "uploads/user_" + SidebarActivity.getUserID() + "/" + filename);

                    final ProgressDialog pdialog = new ProgressDialog(getActivity());
                    pdialog.setCancelable(false);
                    pdialog.setMessage("Deleting...");
                    pdialog.show();

                    PostRequest.send(getActivity(), hashMap, new RespondListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject response) {
                            try {
                                int success = response.getInt("success");

                                Log.d("success is", success + "");

                                if (success == 1) {
                                    if (ppc.deletePrescriptionByServerID(serverID)) {
                                        arrayOfPrescriptions = refreshPrescriptionList();
                                        gridView.setAdapter(new ImageAdapter(getActivity(), arrayOfPrescriptions));
                                    } else
                                        Snackbar.make(root, "Sorry, we can't delete your item right now", Snackbar.LENGTH_SHORT).show();
                                } else if (success == 2) {
                                    pdialog.dismiss();
                                    Snackbar.make(root, "You are not allowed to delete this prescription", Snackbar.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.d("trialpres1", e + "");
                                Snackbar.make(root, "Server error occurred", Snackbar.LENGTH_SHORT).show();
                            }
                            pdialog.dismiss();
                        }
                    }, new ErrorListener<VolleyError>() {
                        public void getError(VolleyError error) {
                            pdialog.dismiss();
                            Log.d("trialpres2", error + "");
                            Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            delete.show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_pres:
                startActivity(new Intent(getActivity(), ShowPrescriptionDialog.class));
                break;
        }
    }

    protected void startImagePagerActivity(int position) {
        Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
        intent.putExtra(Config.IMAGE_POSITION, position);
        startActivity(intent);
    }

    private class ImageAdapter extends BaseAdapter {
        String[] image_urls;
        private LayoutInflater inflater;
        private DisplayImageOptions options;

        public ImageAdapter(Context context, ArrayList<String> uploadsByUser) {
            inflater = LayoutInflater.from(context);
            image_urls = uploadsByUser.toArray(new String[uploadsByUser.size()]);

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
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.item_grid_image, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            imageView.setTag(position);
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
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
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });

            return convertView;
        }
    }

    public ArrayList<String> refreshPrescriptionList() {
        uploadsByUser = ppc.getPrescriptionByUserID(patientID);
        ArrayList<String> prescriptionArray = new ArrayList<>();

        for (int x = 0; x < uploadsByUser.size(); x++) {
            prescriptionArray.add(uploadsByUser.get(x).get(PatientPrescriptionController.PRESCRIPTIONS_FILENAME));
        }
        return prescriptionArray;
    }
}
