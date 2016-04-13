package com.beta.zem.patientcareapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.beta.zem.patientcareapp.Controllers.DbHelper;
import com.beta.zem.patientcareapp.Controllers.PatientController;
import com.beta.zem.patientcareapp.SwipeTabsModule.EditTabsActivity;
import com.beta.zem.patientcareapp.Model.Patient;
import com.beta.zem.patientcareapp.Interface.ErrorListener;
import com.beta.zem.patientcareapp.Interface.RespondListener;
import com.beta.zem.patientcareapp.Network.ListOfPatientsRequest;
import com.beta.zem.patientcareapp.R;
import com.beta.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static Spinner address_region, address_province, address_city_municipality, address_barangay;
    EditText optional_address_line, address_street, email, tel_no, cell_no;
    LinearLayout root;

    DbHelper dbhelper;
    PatientController pc;
    Patient patient;
    Intent intent;

    public static ArrayList<HashMap<String, String>> hashOfBarangays, hashOfProvinces, hashOfMunicipalities, hashOfRegions;
    ArrayList<String> listOfRegions, listOfProvinces, listOfMunicipalities, listOfBarangays;
    ArrayAdapter<String> regions_adapter, provinces_adapter, municipalities_adapter, barangays_adapter;

    public static String barangay_id;
    public static AppCompatDialog pDialog;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts_fragment, container, false);

        address_street = (EditText) rootView.findViewById(R.id.address_street);
        optional_address_line = (EditText) rootView.findViewById(R.id.optional_address_line);
        email = (EditText) rootView.findViewById(R.id.email);
        tel_no = (EditText) rootView.findViewById(R.id.tel_no);
        cell_no = (EditText) rootView.findViewById(R.id.cell_no);
        address_region = (Spinner) rootView.findViewById(R.id.address_region);
        address_barangay = (Spinner) rootView.findViewById(R.id.address_barangay);
        address_city_municipality = (Spinner) rootView.findViewById(R.id.address_city_municipality);
        address_province = (Spinner) rootView.findViewById(R.id.address_province);
        root = (LinearLayout) rootView.findViewById(R.id.root);

        hashOfRegions = new ArrayList<>();
        listOfRegions = new ArrayList<>();

        intent = EditTabsActivity.intent;
        dbhelper = new DbHelper(getActivity());
        pc = new PatientController(getActivity());

        if (intent.getIntExtra("edit", 0) > 0) {
            String edit_uname = SidebarActivity.getUname();
            patient = pc.getloginPatient(edit_uname);

            address_street.setText(patient.getAddress_street());
            email.setText(patient.getEmail());
            tel_no.setText(patient.getTel_no());
            cell_no.setText(patient.getMobile_no());
        } else if (intent.getIntExtra("signup", 0) > 0) {
            if (intent.getExtras().getString("fname") != null) {
                optional_address_line.setText(intent.getExtras().getString("optional_address"));
                address_street.setText(intent.getExtras().getString("address_street"));
                tel_no.setText(intent.getExtras().getString("tel_no"));
                cell_no.setText(intent.getExtras().getString("mobile_no"));
            }
        }

        showprogress();
        ListOfPatientsRequest.getJSONobj("get_regions", "regions", new RespondListener<JSONObject>() {
            @Override
            public void getResult(JSONObject response) {
                try {

                    JSONArray json_array_mysql = response.getJSONArray("regions");

                    HashMap<String, String> map_placeholder = new HashMap<>();
                    map_placeholder.put("name", "Select Region");
                    map_placeholder.put("code", "Select Region");
                    map_placeholder.put("region_server_id", "0");
                    hashOfRegions.add(map_placeholder);

                    for (int x = 0; x < json_array_mysql.length(); x++) {
                        HashMap<String, String> map_region = new HashMap<>();
                        JSONObject json_obj = json_array_mysql.getJSONObject(x);
                        map_region.put("name", json_obj.getString("name"));
                        map_region.put("code", json_obj.getString("code"));
                        map_region.put("region_server_id", json_obj.getString("id"));
                        hashOfRegions.add(map_region);
                    }


                    ArrayList<String> listOfRegions = new ArrayList<>();
                    for (int y = 0; y < hashOfRegions.size(); y++)
                        listOfRegions.add(hashOfRegions.get(y).get("name"));

                    regions_adapter = new ArrayAdapter<>(getActivity(), R.layout.address_spinner_list_item, listOfRegions);
                    address_region.setAdapter(regions_adapter);

                    if (intent.getIntExtra("signup", 0) > 0) {
                        if (intent.getExtras().getString("fname") != null) {
                            String regionSelected = "";

                            for (int x = 0; x < hashOfRegions.size(); x++) {
                                if (hashOfRegions.get(x).get("region_server_id").equals(intent.getExtras().getString("region_id")))
                                    regionSelected = hashOfRegions.get(x).get("name");
                            }
                            address_region.setSelection(regions_adapter.getPosition(regionSelected));
                        }
                    } else if (intent.getIntExtra("edit", 0) > 0) {
                        String regionSelected = "";

                        for (int x = 0; x < hashOfRegions.size(); x++) {
                            if (hashOfRegions.get(x).get("name").equals(patient.getRegion()))
                                regionSelected = hashOfRegions.get(x).get("name");
                        }
                        address_region.setSelection(regions_adapter.getPosition(regionSelected));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                closeprogress();
            }
        }, new ErrorListener<VolleyError>() {
            public void getError(VolleyError error) {
                closeprogress();
                Snackbar.make(container, "Network error", Snackbar.LENGTH_SHORT).show();
            }
        });
        address_region.setOnItemSelectedListener(this);
        address_province.setOnItemSelectedListener(this);
        address_city_municipality.setOnItemSelectedListener(this);
        address_barangay.setOnItemSelectedListener(this);
        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

        switch (parent.getId()) {
            case R.id.address_barangay:
                barangay_id = hashOfBarangays.get(position).get("barangay_server_id");
                EditTabsActivity.pDialog.dismiss();
                break;

            case R.id.address_region:
                hashOfProvinces = new ArrayList<>();
                listOfProvinces = new ArrayList<>();
                final int region_server_id = Integer.parseInt(hashOfRegions.get(position).get("region_server_id"));

                if (region_server_id == 0) {
                    HashMap<String, String> map_placeholder = new HashMap<>();
                    map_placeholder.put("name", "Select Province");
                    map_placeholder.put("province_server_id", "0");
                    map_placeholder.put("region_server_id", "0");
                    hashOfProvinces.add(map_placeholder);
                    listOfProvinces.add(hashOfProvinces.get(0).get("name"));

                    provinces_adapter = new ArrayAdapter<>(getActivity(), R.layout.address_spinner_list_item, listOfProvinces);
                    address_province.setAdapter(provinces_adapter);
                } else {
                    showprogress();
                    ListOfPatientsRequest.getJSONobj("get_provinces&region_id=" + region_server_id, "provinces", new RespondListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject response) {
                            try {
                                JSONArray json_array_mysql = response.getJSONArray("provinces");
                                for (int x = 0; x < json_array_mysql.length(); x++) {
                                    HashMap<String, String> map = new HashMap<>();
                                    JSONObject json_obj = json_array_mysql.getJSONObject(x);
                                    map.put("name", json_obj.getString("name"));
                                    map.put("province_server_id", json_obj.getString("id"));
                                    map.put("region_server_id", json_obj.getString("region_id"));
                                    hashOfProvinces.add(map);
                                }

                                for (int y = 0; y < hashOfProvinces.size(); y++)
                                    listOfProvinces.add(hashOfProvinces.get(y).get("name"));

                                provinces_adapter = new ArrayAdapter<>(getActivity(), R.layout.address_spinner_list_item, listOfProvinces);
                                address_province.setAdapter(provinces_adapter);

                                if (intent.getIntExtra("signup", 0) > 0) {
                                    if (intent.getExtras().getString("fname") != null) {
                                        String provinceSelected = "";

                                        for (int x = 0; x < hashOfProvinces.size(); x++) {
                                            if (hashOfProvinces.get(x).get("province_server_id").equals(intent.getExtras().getString("province_id")))
                                                provinceSelected = hashOfProvinces.get(x).get("name");
                                        }
                                        address_province.setSelection(provinces_adapter.getPosition(provinceSelected));
                                    }
                                } else if (intent.getIntExtra("edit", 0) > 0) {
                                    String provinceSelected = "";

                                    for (int x = 0; x < hashOfProvinces.size(); x++) {
                                        if (hashOfProvinces.get(x).get("name").equals(patient.getProvince()))
                                            provinceSelected = hashOfProvinces.get(x).get("name");
                                    }
                                    address_province.setSelection(provinces_adapter.getPosition(provinceSelected));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            closeprogress();
                        }
                    }, new ErrorListener<VolleyError>() {
                        public void getError(VolleyError error) {
                            closeprogress();
                            Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                break;

            case R.id.address_province:
                hashOfMunicipalities = new ArrayList<>();
                listOfMunicipalities = new ArrayList<>();
                final int province_server_id = Integer.parseInt(hashOfProvinces.get(position).get("province_server_id"));
                if (province_server_id == 0) {
                    HashMap<String, String> map_placeholder = new HashMap<>();
                    map_placeholder.put("name", "Select Municipality");
                    map_placeholder.put("municipality_server_id", "0");
                    map_placeholder.put("province_server_id", "0");
                    hashOfMunicipalities.add(map_placeholder);

                    listOfMunicipalities.add(hashOfMunicipalities.get(0).get("name"));

                    municipalities_adapter = new ArrayAdapter<>(getActivity(), R.layout.address_spinner_list_item, listOfMunicipalities);
                    address_city_municipality.setAdapter(municipalities_adapter);
                } else {
                    showprogress();
                    ListOfPatientsRequest.getJSONobj("get_municipalities&province_id=" + province_server_id, "municipalities", new RespondListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject response) {
                            try {
                                JSONArray json_array_mysql = response.getJSONArray("municipalities");
                                for (int x = 0; x < json_array_mysql.length(); x++) {
                                    HashMap<String, String> map = new HashMap<>();
                                    JSONObject json_obj = json_array_mysql.getJSONObject(x);
                                    map.put("name", json_obj.getString("name"));
                                    map.put("municipality_server_id", json_obj.getString("id"));
                                    map.put("province_server_id", json_obj.getString("province_id"));
                                    hashOfMunicipalities.add(map);
                                }

                                for (int y = 0; y < hashOfMunicipalities.size(); y++)
                                    listOfMunicipalities.add(hashOfMunicipalities.get(y).get("name"));

                                municipalities_adapter = new ArrayAdapter<>(getActivity(), R.layout.address_spinner_list_item, listOfMunicipalities);
                                address_city_municipality.setAdapter(municipalities_adapter);

                                if (intent.getIntExtra("signup", 0) > 0) {
                                    if (intent.getExtras().getString("fname") != null) {
                                        String municipalitySelected = "";

                                        for (int x = 0; x < hashOfMunicipalities.size(); x++) {
                                            if (hashOfMunicipalities.get(x).get("municipality_server_id").equals(intent.getExtras().getString("municipality_id")))
                                                municipalitySelected = hashOfMunicipalities.get(x).get("name");
                                        }
                                        address_city_municipality.setSelection(municipalities_adapter.getPosition(municipalitySelected));
                                    }
                                } else if (intent.getIntExtra("edit", 0) > 0) {
                                    String municipalitySelected = "";

                                    for (int x = 0; x < hashOfMunicipalities.size(); x++) {
                                        if (hashOfMunicipalities.get(x).get("name").equals(patient.getMunicipality()))
                                            municipalitySelected = hashOfMunicipalities.get(x).get("name");
                                    }
                                    address_city_municipality.setSelection(municipalities_adapter.getPosition(municipalitySelected));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            closeprogress();
                        }
                    }, new ErrorListener<VolleyError>() {
                        public void getError(VolleyError error) {
                            closeprogress();
                            Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                break;

            case R.id.address_city_municipality:
                hashOfBarangays = new ArrayList<>();
                listOfBarangays = new ArrayList<>();
                final int municipality_server_id = Integer.parseInt(hashOfMunicipalities.get(position).get("municipality_server_id"));
                if (municipality_server_id == 0) {
                    HashMap<String, String> map_placeholder = new HashMap<>();
                    map_placeholder.put("name", "Select Barangay");
                    map_placeholder.put("barangay_server_id", "0");
                    map_placeholder.put("municipality_server_id", "0");
                    hashOfBarangays.add(map_placeholder);

                    listOfBarangays.add(hashOfBarangays.get(0).get("name"));

                    barangays_adapter = new ArrayAdapter<>(getActivity(), R.layout.address_spinner_list_item, listOfBarangays);
                    address_barangay.setAdapter(barangays_adapter);
                } else {
                    showprogress();
                    ListOfPatientsRequest.getJSONobj("get_barangays&municipality_id=" + municipality_server_id, "barangays", new RespondListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject response) {
                            try {
                                JSONArray json_array_mysql = response.getJSONArray("barangays");
                                for (int x = 0; x < json_array_mysql.length(); x++) {
                                    HashMap<String, String> map = new HashMap<>();
                                    JSONObject json_obj = json_array_mysql.getJSONObject(x);
                                    map.put("name", json_obj.getString("name"));
                                    map.put("barangay_server_id", json_obj.getString("id"));
                                    map.put("municipality_server_id", json_obj.getString("municipality_id"));
                                    hashOfBarangays.add(map);
                                }

                                for (int y = 0; y < hashOfBarangays.size(); y++)
                                    listOfBarangays.add(hashOfBarangays.get(y).get("name"));

                                barangays_adapter = new ArrayAdapter<>(getActivity(), R.layout.address_spinner_list_item, listOfBarangays);
                                address_barangay.setAdapter(barangays_adapter);

                                if (intent.getIntExtra("signup", 0) > 0) {
                                    if (intent.getExtras().getString("fname") != null) {
                                        String barangaySelected = "";

                                        for (int x = 0; x < hashOfBarangays.size(); x++) {
                                            if (hashOfBarangays.get(x).get("barangay_server_id").equals(intent.getExtras().getString("barangay_id")))
                                                barangaySelected = hashOfBarangays.get(x).get("name");
                                        }
                                        address_barangay.setSelection(barangays_adapter.getPosition(barangaySelected));
                                    }
                                } else if (intent.getIntExtra("edit", 0) > 0) {
                                    String barangaySelected = "";

                                    for (int x = 0; x < hashOfBarangays.size(); x++) {
                                        if (hashOfBarangays.get(x).get("name").equals(patient.getBarangay()))
                                            barangaySelected = hashOfBarangays.get(x).get("name");
                                    }
                                    address_barangay.setSelection(barangays_adapter.getPosition(barangaySelected));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            closeprogress();
                        }
                    }, new ErrorListener<VolleyError>() {
                        public void getError(VolleyError error) {
                            closeprogress();
                            Snackbar.make(root, "Network error", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    void showprogress() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.progress_stuffing);
        builder.setCancelable(false);
        pDialog = builder.create();
        pDialog.show();
    }

    void closeprogress() {
        pDialog.dismiss();
    }
}
