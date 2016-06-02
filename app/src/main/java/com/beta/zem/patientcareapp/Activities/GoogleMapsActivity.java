package com.beta.zem.patientcareapp.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.beta.zem.patientcareapp.CheckoutModule.DeliverPickupOption;
import com.beta.zem.patientcareapp.CheckoutModule.PromosDiscounts;
import com.beta.zem.patientcareapp.Controllers.BranchController;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Controllers.OrderPreferenceController;
import com.beta.zem.patientcareapp.Customizations.GlowingText;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;
import com.beta.zem.patientcareapp.Interface.StringRespondListener;
import com.beta.zem.patientcareapp.Model.OrderModel;
import com.beta.zem.patientcareapp.Network.GetRequest;
import com.beta.zem.patientcareapp.Network.StringRequests;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.SidebarModule.SidebarActivity;
import com.beta.zem.patientcareapp.adapter.BranchesAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.make;
import static android.util.Log.d;
import static com.beta.zem.patientcareapp.Network.CustomPostRequest.send;
import static java.lang.System.out;

public class GoogleMapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener {

    //Ravi Tamada starting
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    //End of Ravi

    //from yancy paredes declarations
    private static final LatLng DAVAO = new LatLng(7.0722, 125.6131);
    private static LatLng MY_GEOCODE, ECE_DAVAO;
    private GoogleMap map;
    //end of yancyparedes

    //basics
    DbHelper dbHelper;
    ArrayList<HashMap<String, String>> ece_branches, ece_branches_in_the_same_region;
    ArrayList<String> listOfBranches;
    BranchesAdapter branches_adapter;

    Toolbar mytoolbar;
    ListView list_view_of_branches;
    BranchController bc;
    OrderModel order_model;
    OrderPreferenceController opc;
    LinearLayout root;

    //basics

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_layout);

        dbHelper = new DbHelper(getBaseContext());
        bc = new BranchController(getBaseContext());
        opc = new OrderPreferenceController(getBaseContext());

        list_view_of_branches = (ListView) findViewById(R.id.list_view_of_branches);
        mytoolbar = (Toolbar) findViewById(R.id.mytoolbar);
        root = (LinearLayout) findViewById(R.id.root);

        setSupportActionBar(mytoolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Branch");
        mytoolbar.setNavigationIcon(R.drawable.ic_back);

        list_view_of_branches.setOnItemClickListener(this);

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();

        return super.onOptionsItemSelected(item);
    }

    private void setMapMarker() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            MY_GEOCODE = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            ECE_DAVAO = new LatLng(7.051969, 125.5947593);
        ECE_DAVAO = new LatLng(7.163199, 125.577526);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        // before loop:
        final List<Marker> markers = new ArrayList<>();
        final List<Marker> same_region_markers = new ArrayList<>();


        GetRequest.getJSONobj(getBaseContext(), "google_distance_matrix&mylocation_lat=" + mLastLocation.getLatitude() + "&mylocation_long=" + mLastLocation.getLongitude(), "branches", "branches_id", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                Log.d("googlemapactivity", response + "");
//                    Log.d("mlastlocation", "bday "+mLastLocation+"");
//                    ece_branches = dbHelper.getECEBranches();
                ece_branches = bc.getECEBranchesfromjson(response, "sorted_nearest_branches");
                ece_branches_in_the_same_region = bc.getECEBranchesfromjson(response, "branches_in_the_same_region");

                Bitmap marker_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.map_marker_ece);
                Bitmap my_marker_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.my_map_marker);

                //change ECE_DAVAO constant to MY_GEOCODE if going production
                Marker my_location_marker = map.addMarker(new MarkerOptions().position(MY_GEOCODE).title("You are here !").icon(BitmapDescriptorFactory.fromBitmap(my_marker_icon)));
                same_region_markers.add(my_location_marker);

                Log.d("srm", same_region_markers.size() + "");

                for (int i = 0; i < ece_branches.size(); i++) {
                    double lat_from_row = Double.parseDouble(ece_branches.get(i).get("latitude"));
                    double long_from_row = Double.parseDouble(ece_branches.get(i).get("longitude"));
                    int same_region = Integer.parseInt(ece_branches.get(i).get("same_region"));
                    LatLng latlong = new LatLng(lat_from_row, long_from_row);
                    Marker marker = map.addMarker(new MarkerOptions().position(latlong).title(ece_branches.get(i).get("name")).snippet(ece_branches.get(i).get("full_address")).icon(BitmapDescriptorFactory.fromBitmap(marker_icon)));

                    if (same_region == 1)
                        same_region_markers.add(marker);
                    else
                        markers.add(marker);
                }

                if (same_region_markers.size() == 0) {
                    same_region_markers.add(markers.get(0));
                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : same_region_markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels

                CameraUpdate cu1 = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                map.moveCamera(cu1);
                map.animateCamera(cu1);

                branches_adapter = new BranchesAdapter(getBaseContext(), ece_branches);
                list_view_of_branches.setAdapter(branches_adapter);
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Log.d("googlemapsAct0", error + "");
                ece_branches = bc.getECEBranches();
                branches_adapter = new BranchesAdapter(getBaseContext(), ece_branches);
                list_view_of_branches.setAdapter(branches_adapter);
            }
        });
        } else {
//            make(root, "Please turn on location services", Snackbar.LENGTH_LONG);
            Toast.makeText(GoogleMapsActivity.this, "Please turn on location services", Toast.LENGTH_LONG).show();
//            root.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if(mLastLocation == null)
//                        turn_on_location_dialog();
//                }
//            }, 3000);
        }
    }

    public void turn_on_location_dialog() {
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(GoogleMapsActivity.this);
        confirmationDialog.setTitle("Warning!");
        confirmationDialog.setMessage("Try to turn on Location Services, or set it to High Accuracy.");
        confirmationDialog.setCancelable(false);

        confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GoogleMapsActivity.this.finish();
            }
        });

        confirmationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        confirmationDialog.show();
    }


    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
        setMapMarker();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Once connected with google api, get the location
//        displayLocation();
        setMapMarker();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int branches_selected_id = Integer.parseInt(ece_branches.get(position).get("branches_id"));

        order_model = opc.getOrderPreference();

        if (order_model.hasSelectedBranch()) {
            if (order_model.getBranch_id() != branches_selected_id) {
                order_model.setAction("update");
                order_model.setBranch_id(branches_selected_id);
                showconfirmation();
            } else {
                redirect();
            }
        } else {
            order_model.setAction("insert");
            order_model.setBranch_id(branches_selected_id);
            saveSelectedBranchOnline(order_model);
        }
    }

    void showconfirmation() {
        final AlertDialog.Builder confirmationDialog1 = new AlertDialog.Builder(GoogleMapsActivity.this);
        confirmationDialog1.setTitle("Warning!");
        confirmationDialog1.setMessage("If you select another branch, your current cart and order preference will reset.\nDo you wish to continue?");
        confirmationDialog1.setCancelable(false);

        confirmationDialog1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
//                GoogleMapsActivity.this.finish();
            }
        });

        confirmationDialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                flushBasketOnline();
            }
        });
        confirmationDialog1.show();
    }

    void redirect() {
        startActivity(new Intent(this, ProductCategoriesActivity.class));
        this.finish();
    }

    void saveSelectedBranchOnline(final OrderModel order_model) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_BRANCH_ID, String.valueOf(order_model.getBranch_id()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_PATIENT_ID, String.valueOf(SidebarActivity.getUserID()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_RECIPIENT_NAME, String.valueOf(order_model.getRecipient_name()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_RECIPIENT_ADDRESS, String.valueOf(order_model.getRecipient_address()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_RECIPIENT_NUMBER, String.valueOf(order_model.getRecipient_contactNumber()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_MODE_OF_DELIVERY, String.valueOf(order_model.getMode_of_delivery()));
        hashMap.put(OrderPreferenceController.ORDER_PREFERENCES_PAYMENT_METHOD, String.valueOf(order_model.getPayment_method()));

        if (order_model.getAction().equals("insert"))
            hashMap.put("action", "insert");
        else if (order_model.getAction().equals("update"))
            hashMap.put("action", "update");

        send("saveBranchPreference", hashMap, new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {
                    d("orderprefresponse", response + "");
                    if (response.getBoolean("success")) {
                        order_model.setServer_id(response.getInt("server_id"));
                        if (opc.saveSelectedBranch(order_model)) {
                            redirect();
                        } else {
                            make(root, "Cannot save branch on local database", LENGTH_LONG).show();
                        }
                    } else {
                        make(root, "Unable to save branch", LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    out.println("<saveBranchPreference> request error" + e);
                }
            }
        }, new ErrorListener<VolleyError>() {
            @Override
            public void getError(VolleyError error) {
                out.println("src: <saveBranchPreference>: " + error.toString());
            }
        });
    }

    void flushBasketOnline() {
        StringRequests.getString(GoogleMapsActivity.this, "db/get.php?q=empty_basket_to_change_branch&patient_id=" + SidebarActivity.getUserID(), new StringRespondListener<String>() {
            @Override
            public void getResult(String response) {
                if (response.equals("deleted")) {
                    order_model.setMode_of_delivery("");
                    order_model.setPayment_method("");
                    order_model.setRecipient_address("");
                    order_model.setRecipient_name("");
                    saveSelectedBranchOnline(order_model);
                } else
                    make(root, "Unable to proceed, please try again", Snackbar.LENGTH_SHORT);
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                Log.d("error flashing basket", error + "");
            }
        });
    }
}
