package com.example.zem.patientcareapp.Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.SwipeTabsModule.EditTabsActivity;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

public class PatientProfileFragment extends Fragment {
    ImageView image_holder;
    DbHelper dbhelper;
    PatientController pc;
    Helpers helpers;

    TextView patient_name, username, birthdate, civil_status, height_weight, occupation, address_first_line, address_second_line, email, cp_no;
    String ptnt_name;
    String patient_uname;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_profile_fragment, container, false);

        helpers = new Helpers();
        dbhelper = new DbHelper(getActivity());
        pc = new PatientController(getActivity());
        image_holder = (ImageView) rootView.findViewById(R.id.image_holder);
        patient_name = (TextView) rootView.findViewById(R.id.patient_name);
        patient_name = (TextView) rootView.findViewById(R.id.patient_name);
        username = (TextView) rootView.findViewById(R.id.username);
        birthdate = (TextView) rootView.findViewById(R.id.birthdate);
        civil_status = (TextView) rootView.findViewById(R.id.civil_status);
        height_weight = (TextView) rootView.findViewById(R.id.height_weight);
        occupation = (TextView) rootView.findViewById(R.id.occupation);
        address_first_line = (TextView) rootView.findViewById(R.id.address_first_line);
        address_second_line = (TextView) rootView.findViewById(R.id.address_second_line);
        email = (TextView) rootView.findViewById(R.id.email);
        cp_no = (TextView) rootView.findViewById(R.id.cp_no);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);

        setHasOptionsMenu(true);
        loadData();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.update_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            int edit = 7;
            Intent intent = new Intent(getActivity(), EditTabsActivity.class);
            intent.putExtra(EditTabsActivity.EDIT_REQUEST, edit);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        loadData();
        super.onResume();
    }

    private void loadData() {
        patient_uname = SidebarActivity.getUname();
        Patient loginUser = pc.getloginPatient(patient_uname);

        ptnt_name = loginUser.getFname() + " " + loginUser.getLname();
        username.setText("username: " + patient_uname);

        patient_name.setText(ptnt_name);
        birthdate.setText("Birthdate: " + loginUser.getBirthdate());
        civil_status.setText("Civil Status: " + loginUser.getCivil_status());
        height_weight.setText("Height: " + loginUser.getHeight() + " ft. / Weight: " + loginUser.getWeight() + " kg.");

        if (loginUser.getOccupation() == null || loginUser.getOccupation().equals(""))
            occupation.setVisibility(View.GONE);
        else
            occupation.setText("Occupation: " + loginUser.getOccupation());

        if (!loginUser.getOptional_address().equals(""))
            address_first_line.setText(loginUser.getOptional_address() + ", " + loginUser.getAddress_street() + ", " + loginUser.getBarangay());
        else
            address_first_line.setText(loginUser.getAddress_street() + ", " + loginUser.getBarangay());

        address_second_line.setText((loginUser.getMunicipality().substring(0, 1).toUpperCase() + loginUser.getMunicipality().substring(1).toLowerCase()) + ", "
                + (loginUser.getProvince().substring(0, 1).toUpperCase() + loginUser.getProvince().substring(1).toLowerCase()) + ", Philippines");

        if (loginUser.getEmail() == null || loginUser.getEmail().equals(""))
            email.setVisibility(View.GONE);
        else
            email.setText(loginUser.getEmail());

        if (loginUser.getTel_no() == null || loginUser.getTel_no().equals(""))
            cp_no.setText(loginUser.getMobile_no());
        else
            cp_no.setText(loginUser.getTel_no() + " / " + loginUser.getMobile_no());

        String imgFile = loginUser.getPhoto();

        if (imgFile != null || !imgFile.equals(""))
            helpers.setImage(imgFile, progressBar, image_holder);
    }
}
