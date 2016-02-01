package com.example.zem.patientcareapp.SidebarModule;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zem.patientcareapp.Activities.ShoppingCartActivity;
import com.example.zem.patientcareapp.AlarmModule.AlarmService;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.OverlayController;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Controllers.PatientRecordController;
import com.example.zem.patientcareapp.Controllers.PatientTreatmentsController;
import com.example.zem.patientcareapp.Fragment.HomeTileFragment;
import com.example.zem.patientcareapp.Fragment.ListOfDoctorsFragment;
import com.example.zem.patientcareapp.Fragment.MessagesFragment;
import com.example.zem.patientcareapp.Fragment.OrdersFragment;
import com.example.zem.patientcareapp.Fragment.PatientHistoryFragment;
import com.example.zem.patientcareapp.Fragment.PatientProfileFragment;
import com.example.zem.patientcareapp.Fragment.PromoFragment;
import com.example.zem.patientcareapp.ImageGallery.ImageHelper;
import com.example.zem.patientcareapp.Activities.MainActivity;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.adapter.NavDrawerListAdapter;
import com.example.zem.patientcareapp.gcm.gcmquickstart.QuickstartPreferences;
import com.example.zem.patientcareapp.gcm.gcmquickstart.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static android.util.Log.d;

/**
 * Created by Zem on 7/16/2015.
 */

public class SidebarActivity extends AppCompatActivity {
    private String[] navMenuTitles; // slide menu items
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public static String uname, pass;
    public static int userID;

    AlarmService alarmService;
    SharedPreferences.Editor editor;
    private NavDrawerListAdapter adapter;
    FragmentTransaction fragmentTransaction;
    private ActionBarDrawerToggle mDrawerToggle;
    public static SharedPreferences sharedpreferences;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    LinearLayout root;
    ImageView img_first, sideBar_overlay;
    Toolbar myToolBar;

    static com.example.zem.patientcareapp.Model.Patient patient;
    static DbHelper dbHelper;
    static PatientController pc;
    PatientRecordController prc;
    PatientTreatmentsController ptc;
    OverlayController oc;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SidebarActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidebar_layout);

        prc = new PatientRecordController(this);
        ptc = new PatientTreatmentsController(this);

        root = (LinearLayout) findViewById(R.id.root);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                d("mainactivity",  "oneceive");
                SharedPreferences sp =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sp
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    d("mainactivity",  "senttoken");
//                    showdl("senttoken");
                } else {
                    d("mainactivity",  "errortoken");

//                    showdl("error");
                }
            }
        };

        if (checkPlayServices()) {
            d("gcm_reg", "reg ba ?");
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        dbHelper = new DbHelper(this);
        oc = new OverlayController(this);
        pc = new PatientController(this);

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        myToolBar.setNavigationIcon(R.drawable.ic_navigator);

        showOverLay();

        //////////////FOR THE SIDEBAR///////////////////////////////
        //Header of the listview
        View header = getLayoutInflater().inflate(R.layout.header_sidebar, null);
        img_first = (ImageView) header.findViewById(R.id.img_first);
        TextView username = (TextView) header.findViewById(R.id.username);
        username.setText(getUname());

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.temp_user);
        img_first.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 300));

        navDrawerItems = new ArrayList();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load slide menu items
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons); // nav drawer icons from resources

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));

        navMenuIcons.recycle(); // Recycle the typed array

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.addHeaderView(header);
        mDrawerList.setAdapter(adapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigator, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            displayView(0); // on first time display view for first nav item
        }

        displayView(getIntent().getIntExtra("select", 0));

        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if (pc.checkUserIfRegistered(getUname()) > 0) {
            // start consultation schedules reminder
//            alarmService = new AlarmService(this);
//            alarmService.patientConsultationReminder();
        } else {
            editor.clear();
            editor.commit();
            moveTaskToBack(true);
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    void showdl(String msg){
        AlertDialog.Builder order_completed_dialog = new AlertDialog.Builder(SidebarActivity.this);
        order_completed_dialog.setTitle("Connection status");
        order_completed_dialog.setMessage(msg);
        order_completed_dialog.setCancelable(false);
        order_completed_dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        order_completed_dialog.show();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static String getUname() {
        uname = sharedpreferences.getString(MainActivity.name, "DEFAULT");
        return uname;
    }

    public static String getPass() {
        pass = sharedpreferences.getString(MainActivity.pass, "DEFAULT");
        return pass;
    }

    public static int getUserID() {
        patient = pc.getloginPatient(getUname());
        userID = patient.getServerID();

        return userID;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position); // display view for selected nav drawer item
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.main.finish();
        this.finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.go_to_cart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.go_to_cart)
            startActivity(new Intent(this, ShoppingCartActivity.class));

        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        String title = "Home";
        switch (position) {
            case 0:
                fragment = new HomeTileFragment();
                break;
            case 1:
                fragment = new HomeTileFragment();
                break;
            case 2:
                title = "Profile";
                fragment = new PatientProfileFragment();
                break;
            case 3:
                title = "Messages";
                fragment = new MessagesFragment();
                break;
            case 4:
                title = "Medical Records";
                fragment = new PatientHistoryFragment();
                break;
            case 5:
                title = "Recent Orders";
                fragment = new OrdersFragment();
                break;
            case 6:
                title = "Doctors";
                fragment = new ListOfDoctorsFragment();
                break;
            case 7:
                title = "Promos";
                fragment = new PromoFragment();
                break;
//            case 8:
//                title = "News";
//                fragment = new HomeTileFragment();
//                break;
            case 8:
                if(prc.deleteAllRecords()) {
                    if(ptc.deleteTreatments()) {
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(this, MainActivity.class));
                        SidebarActivity.this.finish();
                    } else
                        Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                } else
                    Snackbar.make(root, "Error occurred", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
            getSupportActionBar().setTitle(title);
        } else
            Log.e("SidebarAct", "Error in creating fragment");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void showOverLay() {
        if (!oc.checkOverlay("HomeTile", "check")) {
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.overlay_hometile);

            sideBar_overlay = (ImageView) dialog.findViewById(R.id.sideBar_overlay);
            LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overLayHometile);
            layout.setAlpha((float) 0.8);

            sideBar_overlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (oc.checkOverlay("HomeTile", "insert"))
                        dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
