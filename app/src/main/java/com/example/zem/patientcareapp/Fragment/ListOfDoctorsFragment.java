package com.example.zem.patientcareapp.Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.zem.patientcareapp.Controllers.DoctorController;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Activities.DoctorActivity;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.adapter.ListOfDoctorsAdapter;
import com.example.zem.patientcareapp.Network.VolleySingleton;
import com.example.zem.patientcareapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOfDoctorsFragment extends Fragment implements TextWatcher, AdapterView.OnItemClickListener {
    ListView list_of_doctors;
    EditText search_doctor;
    TextView noUserFound;

    ArrayList<HashMap<Integer, ArrayList<String>>> searchDoctors;
    ArrayList<String> listOfSearchDoctors;
    ArrayList<HashMap<String, String>> arrayOfSearchDoctors;
    public static ArrayList<HashMap<String, String>> doctor_items;

    String s_doctor;

    DbHelper dbHelper;
    DoctorController doctor_controller;
    RequestQueue queue;
    ListOfDoctorsAdapter adapter;
    Helpers helpers;

    View root_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list_of_doctors_fragment, container, false);
        root_view = rootView;

        helpers = new Helpers();
        dbHelper = new DbHelper(getActivity());
        doctor_controller = new DoctorController(getActivity());
        queue = VolleySingleton.getInstance().getRequestQueue();

        arrayOfSearchDoctors = new ArrayList<>();
        listOfSearchDoctors = new ArrayList<>();

        search_doctor = (EditText) rootView.findViewById(R.id.search_doctor);
        noUserFound = (TextView) rootView.findViewById(R.id.noUserFound);

        doctor_items = doctor_controller.getAllDoctors();
        searchDoctors = doctor_controller.getSearchDoctors();
        populateDoctorListView(rootView, doctor_items);

        return rootView;
    }

    public void populateDoctorListView(View rootView, ArrayList<HashMap<String, String>> doctor_items) {
        for (int i = 0; i < doctor_items.size(); i++) {
            listOfSearchDoctors.add(doctor_items.get(i).get("fullname"));
        }

        arrayOfSearchDoctors.addAll(doctor_items);

        adapter = new ListOfDoctorsAdapter(getActivity(), doctor_items);
        list_of_doctors = (ListView) rootView.findViewById(R.id.list_of_doctors);
        list_of_doctors.setAdapter(adapter);

        list_of_doctors.setOnItemClickListener(this);
        search_doctor.addTextChangedListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int ID = Integer.parseInt(doctor_items.get(position).get("doc_id"));
        Intent intent = new Intent(getActivity(), DoctorActivity.class);
        intent.putExtra("doc_id", ID);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        s_doctor = search_doctor.getText().toString();
        doctor_items.clear();

        if (!s_doctor.equals("")) {
            for (int x = 0; x < searchDoctors.size(); x++) {
                for (Map.Entry<Integer, ArrayList<String>> ee : searchDoctors.get(x).entrySet()) {
                    Integer key = ee.getKey();
                    ArrayList<String> values = ee.getValue();
                    int check = 0;

                    for (int y = 0; y < values.size(); y++) {
                        if (values.get(y).toLowerCase().contains(s_doctor.toLowerCase()))
                            check += 1;
                    }

                    if (check > 0) {
                        noUserFound.setVisibility(View.GONE);
                        list_of_doctors.setVisibility(View.VISIBLE);

                        HashMap<String, String> hash = new HashMap<>();

                        hash.put("doc_id", String.valueOf(key));
                        hash.put("fullname", values.get(0));
                        hash.put("name", values.get(2));
                        doctor_items.add(hash);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            if (doctor_items.size() == 0) {
                noUserFound.setVisibility(View.VISIBLE);
                list_of_doctors.setVisibility(View.GONE);
            }
        } else {
            doctor_items = doctor_controller.getAllDoctors();
            populateDoctorListView(root_view, doctor_items);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}

