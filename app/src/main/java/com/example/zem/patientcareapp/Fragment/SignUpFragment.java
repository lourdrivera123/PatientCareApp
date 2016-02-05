package com.example.zem.patientcareapp.Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.SwipeTabsModule.EditTabsActivity;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import java.util.Calendar;

public class SignUpFragment extends Fragment {
    static EditText fname, lname, mname, height, weight, occupation, birthday;
    public static Spinner civil_status_spinner;
    public static RadioButton male_rb, female_rb;
    static RadioGroup sex;
    public static View rootView;

    public static ArrayAdapter<String> civil_status_adapter;
    public static String[] civil_status_array = {
            "Single", "Married", "Widowed", "Separated", "Divorced"
    };
    String get_birthdate;

    DbHelper dbhelper;
    PatientController pc;

    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_signup_fragment, container, false);

        dbhelper = new DbHelper(getActivity());
        pc = new PatientController(getActivity());
        int edit = EditTabsActivity.edit_int;
        int signup = EditTabsActivity.signup_int;
        intent = EditTabsActivity.intent;

        fname = (EditText) rootView.findViewById(R.id.fname);
        lname = (EditText) rootView.findViewById(R.id.lname);
        mname = (EditText) rootView.findViewById(R.id.mname);
        height = (EditText) rootView.findViewById(R.id.height);
        weight = (EditText) rootView.findViewById(R.id.weight);
        birthday = (EditText) rootView.findViewById(R.id.birthday);
        occupation = (EditText) rootView.findViewById(R.id.occupation);
        male_rb = (RadioButton) rootView.findViewById(R.id.male_rb);
        female_rb = (RadioButton) rootView.findViewById(R.id.female_rb);
        sex = (RadioGroup) rootView.findViewById(R.id.sex);

        civil_status_spinner = (Spinner) rootView.findViewById(R.id.civil_status);
        civil_status_adapter = new ArrayAdapter<>(getActivity(), R.layout.address_spinner_list_item, civil_status_array);
        civil_status_spinner.setAdapter(civil_status_adapter);

        birthday.addTextChangedListener(tw);

        if (edit > 0) {
            String edit_uname = SidebarActivity.getUname();
            Patient patient = pc.getloginPatient(edit_uname);
            get_birthdate = patient.getBirthdate();

            fname.setText(patient.getFname());
            lname.setText(patient.getLname());
            mname.setText(patient.getMname());
            birthday.setText(patient.getBirthdate());
            occupation.setText(patient.getOccupation());
            height.setText(patient.getHeight());
            weight.setText(patient.getWeight());
            civil_status_spinner.setSelection(civil_status_adapter.getPosition(patient.getCivil_status()));

            if (male_rb.getText().equals(patient.getSex())) {
                male_rb.setChecked(true);
                female_rb.setChecked(false);
            } else if (female_rb.getText().equals(patient.getSex())) {
                female_rb.setChecked(true);
                male_rb.setChecked(false);
            }
        } else if (signup > 0) {
            if (intent.getExtras().getString("fname") != null) {
                get_birthdate = intent.getExtras().getString("birthdate");

                fname.setText(intent.getExtras().getString("fname"));
                mname.setText(intent.getExtras().getString("mname"));
                lname.setText(intent.getExtras().getString("lname"));
                birthday.setText(intent.getExtras().getString("birthdate"));
                occupation.setText(intent.getExtras().getString("occupation"));
                height.setText(intent.getExtras().getString("height"));
                weight.setText(intent.getExtras().getString("weight"));
                civil_status_spinner.setSelection(civil_status_adapter.getPosition(intent.getExtras().getString("civil_status")));

                if (male_rb.getText().equals(intent.getExtras().getString("sex")))
                    male_rb.setChecked(true);
                else if (female_rb.getText().equals(intent.getExtras().getString("sex")))
                    female_rb.setChecked(true);
            }
        }

        return rootView;
    }

    TextWatcher tw = new TextWatcher() {
        private String current = "";
        private String yyyymmdd = "yyyymmdd";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]", "");
                String cleanC = current.replaceAll("[^\\d.]", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8) {
                    clean = clean + yyyymmdd.substring(clean.length());
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day = Integer.parseInt(clean.substring(6, 8));
                    int mon = Integer.parseInt(clean.substring(4, 6));
                    int year = Integer.parseInt(clean.substring(0, 4));

                    if (mon > 12) mon = 12;
                    cal.set(Calendar.MONTH, mon - 1);
                    year = (year < 1800) ? 1800 : (year > 2100) ? 2100 : year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                    clean = String.format("%02d%02d%02d", year, mon, day);
                }

                clean = String.format("%s-%s-%s", clean.substring(0, 4), clean.substring(4, 6), clean.substring(6, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                birthday.setText(current);
                birthday.setSelection(sel < current.length() ? sel : current.length());

//                if ((current_year - year) < 18)
//                    birthdate.setError("Must be 18 years old and above");
//                else
//                    birthdate.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}