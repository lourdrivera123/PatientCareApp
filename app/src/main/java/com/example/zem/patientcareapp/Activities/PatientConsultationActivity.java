package com.example.zem.patientcareapp.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.example.zem.patientcareapp.AlarmModule.AlarmService;
import com.example.zem.patientcareapp.Controllers.ClinicController;
import com.example.zem.patientcareapp.Controllers.DoctorController;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.PatientConsultationController;
import com.example.zem.patientcareapp.Controllers.SpecialtyController;
import com.example.zem.patientcareapp.Model.Consultation;
import com.example.zem.patientcareapp.Interface.ErrorListener;
import com.example.zem.patientcareapp.Interface.RespondListener;
import com.example.zem.patientcareapp.Network.GetRequest;
import com.example.zem.patientcareapp.Network.PostRequest;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.support.design.widget.Snackbar.make;
import static android.util.Log.d;
import static com.example.zem.patientcareapp.Network.GetRequest.getJSONobj;

public class PatientConsultationActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener, CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener {
    DbHelper dbhelper;
    PatientConsultationController pcc;
    Consultation consult;
    AlarmService alarmService;
    DoctorController dc;
    ClinicController cc;
    SpecialtyController sc;

    LinearLayout setDate, setAlarmedTime, root;
    TextView txtDate, txtAlarmedTime;
    CheckBox checkAlarm;
    AutoCompleteTextView search_doctor;
    Toolbar myToolBar;

    ListView doctors_list;
    AlertDialog dialog;

    Calendar cal;
    ArrayAdapter<String> doctorAdapter, clinicAdapter, specialtyAdapter, citiesMunicipalitiesAdapter, doctorListAdapter, specialty_names_adapter, clinic_names_adapter;

    ArrayList<HashMap<String, String>> doctorsHashmap, doctorClinicHashmap, specialtiesHashmap, citiesMunicipalitiesHashmap;
    ArrayList<String> listOfClinic, listOfDoctors, listOfSpecialties, listOfCitiesMunicipalities;

    String request;
    int hour, minute, new_hour, new_min, isAlarm;
    static int doctor_id = 0;
    static String time_alarm = "";
    int the_chosen_doctor = 0;

    AlertDialog.Builder builder;
    View view_dialog_picker;
    Button choose_doctor_btn, search_specialty_btn, search_place_btn, search_clinic_btn;
    EditText filter_doctor_search;
    TextView the_chosen_one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_layout);

        myToolBar = (Toolbar) findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Set Consultation");

        dbhelper = new DbHelper(this);
        dc = new DoctorController(this);
        pcc = new PatientConsultationController(this);
        cc = new ClinicController(this);
        sc = new SpecialtyController(this);

        alarmService = new AlarmService(this);
        Intent getIntent = getIntent();
        listOfDoctors = new ArrayList();
        listOfClinic = new ArrayList();
        listOfSpecialties = new ArrayList<>();
        listOfCitiesMunicipalities = new ArrayList<>();

        doctorClinicHashmap = dc.getDoctorsInnerJoinClinics();
        doctorsHashmap = dc.getAllDoctors();
        specialtiesHashmap = dc.getSpecialties();
        citiesMunicipalitiesHashmap = cc.getClinicCitiesList();

        setDate = (LinearLayout) findViewById(R.id.setDate);
        setAlarmedTime = (LinearLayout) findViewById(R.id.setAlarmedTime);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtAlarmedTime = (TextView) findViewById(R.id.txtAlarmedTime);
        checkAlarm = (CheckBox) findViewById(R.id.checkAlarm);
        search_doctor = (AutoCompleteTextView) findViewById(R.id.search_doctor);
        root = (LinearLayout) findViewById(R.id.root);
        doctors_list = (ListView) findViewById(R.id.doctors_list);
        search_specialty_btn = (Button) findViewById(R.id.search_specialty_btn);
        search_place_btn = (Button) findViewById(R.id.search_place_btn);
        search_clinic_btn = (Button) findViewById(R.id.search_clinic_btn);
        filter_doctor_search = (EditText) findViewById(R.id.filter_doctor_search);
        the_chosen_one = (TextView) findViewById(R.id.the_chosen_one);

        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);


        for (int i = 0; i < specialtiesHashmap.size(); i++)
            listOfSpecialties.add(specialtiesHashmap.get(i).get("name"));

        for (int i = 0; i < citiesMunicipalitiesHashmap.size(); i++)
            listOfCitiesMunicipalities.add(citiesMunicipalitiesHashmap.get(i).get("address_city_municipality"));

        /*
        * Call Populate Doctor Listview Method
        * */
        populateDoctorListView();

        filter_doctor_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                PatientConsultationActivity.this.doctorListAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        if (getIntent.getStringExtra("request").equals("add")) {
            request = "add";
            consult = new Consultation();

            if (hour > 12)
                txtAlarmedTime.setText((hour - 12) + " : " + minute + " PM");
            else
                txtAlarmedTime.setText(hour + " : " + minute + " AM");

            new_hour = hour;
            new_min = minute;

            String check_date;

            if (cal.get(Calendar.DATE) < 10)
                check_date = "0" + (cal.get(Calendar.DATE));
            else
                check_date = String.valueOf(cal.get(Calendar.DATE));
            String month = ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" : "") + (cal.get(Calendar.MONTH) + 1);

            txtDate.setText(cal.get(Calendar.YEAR) + "-" + month + "-" + check_date);
        }

        setDate.setOnClickListener(this);
        setAlarmedTime.setOnClickListener(this);
        checkAlarm.setOnCheckedChangeListener(this);
        search_specialty_btn.setOnClickListener(this);
        search_place_btn.setOnClickListener(this);
        search_clinic_btn.setOnClickListener(this);
    }

    public void populateDoctorListView() {
        listOfDoctors = new ArrayList();
        for (int i = 0; i < doctorsHashmap.size(); i++)
            listOfDoctors.add(doctorsHashmap.get(i).get("fullname"));

        d("doctor_log", listOfDoctors + "");

        doctorListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listOfDoctors);
        doctors_list.setAdapter(doctorListAdapter);

        doctors_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                the_chosen_doctor = Integer.parseInt(doctorsHashmap.get(i).get("doc_id"));
                showsearchDialog("clinic_chooser");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_and_cancel, menu);
        return true;
    }

    public void showsearchDialog(String filter) {
        if (filter.equals("specialty")) {
            view_dialog_picker = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_search_specialty_dialog, null);
            final ListView lv = (ListView) view_dialog_picker.findViewById(R.id.list_of_something_to_choose);
            final EditText search_text = (EditText) view_dialog_picker.findViewById(R.id.search_bar_for_dialog);

            /*
            * Populating Doctor Specialty List View
            */
            getJSONobj(getBaseContext(), "get_doctor_specialties", "specialties", "specialty_id", new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {
                    getJSONobj(getBaseContext(), "get_doctor_sub_specialties", "sub_specialties", "sub_specialty_id", new RespondListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject response) {
                            d("pc", "specialty and sub specialty updated");
                            ArrayList<HashMap<String, String>> specialty_hashmap = sc.getAllSpecialties();
                            d("sc", specialty_hashmap + "");
                            ArrayList<String> specialty_names_list = new ArrayList();

                            for (int i = 0; i < specialty_hashmap.size(); i++)
                                specialty_names_list.add(specialty_hashmap.get(i).get("specialty_name"));

                            specialty_names_adapter = new ArrayAdapter<String>(PatientConsultationActivity.this, android.R.layout.simple_expandable_list_item_1, specialty_names_list);
                            lv.setAdapter(specialty_names_adapter);
                        }
                    }, new ErrorListener<VolleyError>() {
                        public void getError(VolleyError error) {
                            make(root, "Network error", LENGTH_SHORT).show();
                        }
                    });
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    make(root, "Network error", LENGTH_SHORT).show();
                }
            });

            /*
            *  Watching for search_text changes
            *  Then filter the specialyadapter using the search_text value
            */
            search_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    PatientConsultationActivity.this.specialty_names_adapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            /*
            * Item click listener for specialty listview
            * In real world terms this is when the user selects a specialty
            * */
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /*
                    * Insert the reload doctors list here with a specialty filter
                    * */
                    String val = specialtiesHashmap.get(i).get("name");
                    doctorsHashmap = dc.getAllDoctorsWithFilter("specialty", val);
                    d("dh", doctorsHashmap + "");
                    populateDoctorListView();
                    letDialogSleep();
                }
            });
        } else if (filter.equals("place")) {
            view_dialog_picker = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_place_dialog, null);
            final ListView lv = (ListView) view_dialog_picker.findViewById(R.id.list_of_something_to_choose);
            final EditText search_text = (EditText) view_dialog_picker.findViewById(R.id.search_bar_for_dialog);

            getJSONobj(getBaseContext(), "get_clinics", "clinics", "clinics_id", new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {
                    d("cc_place", "clinic controller updated");
                    ArrayList<HashMap<String, String>> clinic_hashmap = cc.getClinicCitiesList();
                    d("cc", clinic_hashmap + "");
                    ArrayList<String> clinic_names_list = new ArrayList();
//
                    for (int i = 0; i < clinic_hashmap.size(); i++)
                        clinic_names_list.add(clinic_hashmap.get(i).get("address_city_municipality"));

                    clinic_names_adapter = new ArrayAdapter<String>(PatientConsultationActivity.this, android.R.layout.simple_expandable_list_item_1, clinic_names_list);
                    lv.setAdapter(clinic_names_adapter);
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    make(root, "Network error", LENGTH_SHORT).show();
                }
            });

              /*
            *  Watching for search_text changes
            *  Then filter the clinic_names_adapter using the search_text value
            *  But note that clinic_names_adapter here is used to display Cities of clinics
            */
            search_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    PatientConsultationActivity.this.clinic_names_adapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

             /*
            * Item click listener for place listview
            * In real world terms this is when the user selects a place
            * */
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /*
                    * Insert the reload doctors list here with a place from clinics filter
                    * */
                    String val = citiesMunicipalitiesHashmap.get(i).get("address_city_municipality");
                    doctorsHashmap = dc.getAllDoctorsWithFilter("places", val);
                    d("dh", doctorsHashmap + "");
                    populateDoctorListView();
                    letDialogSleep();
                }
            });
        } else if (filter.equals("clinic")) {
            view_dialog_picker = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_clinic_dialog, null);
            final ListView lv = (ListView) view_dialog_picker.findViewById(R.id.list_of_something_to_choose);
            final EditText search_text = (EditText) view_dialog_picker.findViewById(R.id.search_bar_for_dialog);

            getJSONobj(getBaseContext(), "get_clinics", "clinics", "clinics_id", new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {
                    d("cc", "clinic controller updated");
                    ArrayList<HashMap<String, String>> clinic_hashmap = cc.getAllClinics();
                    d("cc", clinic_hashmap + "");
                    ArrayList<String> clinic_names_list = new ArrayList();
//
                    for (int i = 0; i < clinic_hashmap.size(); i++)
                        clinic_names_list.add(clinic_hashmap.get(i).get("clinic_name"));

                    clinic_names_adapter = new ArrayAdapter<String>(PatientConsultationActivity.this, android.R.layout.simple_expandable_list_item_1, clinic_names_list);
                    lv.setAdapter(clinic_names_adapter);
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    make(root, "Network error", LENGTH_SHORT).show();
                }
            });

               /*
            *  Watching for search_text changes
            *  Then filter the clinic_names_adapter using the search_text value
            *  Note that this is the original Clinic Listing so clinic_names_adapter
            *  is used to display list of clinic names
            */
            search_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    PatientConsultationActivity.this.clinic_names_adapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

              /*
            * Item click listener for clinic listview
            * In real world terms this is when the user selects a clinic
            * */
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    /*
                    * Insert the reload doctors list here with a place from clinics filter
                    * */
                    String val = doctorClinicHashmap.get(i).get("clinics_id");
                    doctorsHashmap = dc.getAllDoctorsWithFilter("clinic", val);
                    d("dh", doctorsHashmap + "");
                    populateDoctorListView();
                    letDialogSleep();
                }
            });
        } else if (filter.equals("clinic_chooser")) {
            view_dialog_picker = LayoutInflater.from(getBaseContext()).inflate(R.layout.show_clinic_dialog, null);
            final ListView lv = (ListView) view_dialog_picker.findViewById(R.id.list_of_something_to_choose);
            final EditText search_text = (EditText) view_dialog_picker.findViewById(R.id.search_bar_for_dialog);

            getJSONobj(getBaseContext(), "get_clinics", "clinics", "clinics_id", new RespondListener<JSONObject>() {
                @Override
                public void getResult(JSONObject response) {

                    getJSONobj(getBaseContext(), "get_clinic_doctor", "clinic_doctor", "clinic_doctor_id", new RespondListener<JSONObject>() {
                        @Override
                        public void getResult(JSONObject response) {
                            final ArrayList<HashMap<String, String>> clinic_hashmap = cc.getDoctorClinics(the_chosen_doctor);
                            d("chosen_doctor_clinics", clinic_hashmap + "");
                            ArrayList<String> clinic_names_list = new ArrayList();
//
                            for (int i = 0; i < clinic_hashmap.size(); i++)
                                clinic_names_list.add(clinic_hashmap.get(i).get("clinic_name"));

                            ArrayAdapter clinic_names_adapter = new ArrayAdapter<String>(PatientConsultationActivity.this, android.R.layout.simple_expandable_list_item_1, clinic_names_list);
                            lv.setAdapter(clinic_names_adapter);

                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    d("clinic_id =", clinic_hashmap.get(i).get("clinics_id") + "");
                                    consult.setClinicID(Integer.parseInt(clinic_hashmap.get(i).get("clinics_id")));
                                    consult.setDoctorID(the_chosen_doctor);
                                    String c_name = clinic_hashmap.get(i).get("clinic_name");
                                    String d_name = clinic_hashmap.get(i).get("doctor_name");
                                    the_chosen_one.setText("Doctor: "+d_name+" \nClinic: "+c_name);
                                    letDialogSleep();
                                }
                            });
                        }
                    }, new ErrorListener<VolleyError>() {
                        public void getError(VolleyError error) {
                            make(root, "Network error", LENGTH_SHORT).show();
                        }
                    });
                }
            }, new ErrorListener<VolleyError>() {
                public void getError(VolleyError error) {
                    make(root, "Network error", LENGTH_SHORT).show();
                }
            });

               /*
            *  Watching for search_text changes
            *  Then filter the clinic_names_adapter using the search_text value
            *  Note that clinic_names_adapter here is used to
            *  display clinics of Selected Doctor only
            */
            search_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    PatientConsultationActivity.this.clinic_names_adapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        builder = new AlertDialog.Builder(PatientConsultationActivity.this);
        builder.setView(view_dialog_picker);
        builder.setCancelable(false);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    void letDialogSleep() {
        dialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int check = 0;

        if (item.getItemId() == R.id.save) {
            if (check == 0) {
                consult.setPatientID(SidebarActivity.getUserID());
                consult.setDate(txtDate.getText().toString());
                consult.setIsAlarmed(isAlarm);
                consult.setAlarmedTime(time_alarm);
                consult.setIs_approved(0);
                consult.setIs_read(0);
                consult.setPtnt_is_approved(0);

                final HashMap<String, String> hashMap = new HashMap();
                hashMap.put("table", "consultations");
                hashMap.put("request", "crud");
                hashMap.put("action", "insert");
                hashMap.put("patient_id", String.valueOf(consult.getPatientID()));
                hashMap.put("doctor_id", String.valueOf(consult.getDoctorID()));
                hashMap.put("clinic_id", String.valueOf(consult.getClinicID()));
                hashMap.put("date", consult.getDate());
                hashMap.put("time", "");
                hashMap.put("is_alarm", String.valueOf(consult.getIsAlarmed()));
                hashMap.put("alarm_time", consult.getAlarmedTime());
                hashMap.put("comment_doctor", "");
                hashMap.put("comment_patient", "");

                final ProgressDialog pdialog = new ProgressDialog(this);
                pdialog.setMessage("Please wait...");
                pdialog.show();

                PostRequest.send(this, hashMap, new RespondListener<JSONObject>() {
                    @Override
                    public void getResult(JSONObject response) {
                        try {
                            d("response", response + "");
                            consult.setServerID(response.getInt("last_inserted_id"));
                            consult.setCreated_at(response.getString("created_at"));

                            if (pcc.savePatientConsultation(consult, request)) {
                                alarmService.patientConsultationReminder();
                                PatientConsultationActivity.this.finish();
                                make(root, "Your request has been submitted. Please wait for your confirmation.", LENGTH_SHORT).show();
                            } else
                                make(root, "Error occurred", LENGTH_SHORT).show();
                        } catch (Exception e) {
                            d("patient_consultation", e + "");
                            make(root, "Server error occurred", LENGTH_SHORT).show();
                        }
                        pdialog.dismiss();
                    }
                }, new ErrorListener<VolleyError>() {
                    public void getError(VolleyError error) {
                        pdialog.dismiss();
                        make(root, "Network Error", LENGTH_SHORT).show();
                    }
                });
            }
        } else
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setDate:
                FragmentManager fm = getSupportFragmentManager();
                CalendarDatePickerDialogFragment datepicker;
                String dateInView = txtDate.getText().toString();
                int month, year, day;

                int indexOfYear = dateInView.indexOf("-");
                int indexOfMonthandDay = dateInView.lastIndexOf("-");
                year = Integer.parseInt(dateInView.substring(0, indexOfYear));
                month = Integer.parseInt(dateInView.substring(indexOfYear + 1, indexOfMonthandDay)) - 1;
                day = Integer.parseInt(dateInView.substring(indexOfMonthandDay + 1, dateInView.length()));

                datepicker = CalendarDatePickerDialogFragment.newInstance(this, year, month, day);
                datepicker.setDateRange(new MonthAdapter.CalendarDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)), null);
                datepicker.show(fm, "fragment_date_picker_name");

                break;

            case R.id.setAlarmedTime:
                RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment.newInstance(this, new_hour, minute, DateFormat.is24HourFormat(PatientConsultationActivity.this));

                timePickerDialog.show(getSupportFragmentManager(), "timePickerDialogFragment");
                break;

            case R.id.search_specialty_btn:
                showsearchDialog("specialty");
                break;

            case R.id.search_place_btn:
                showsearchDialog("place");
                break;

            case R.id.search_clinic_btn:
                showsearchDialog("clinic");
                break;
        }
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        String meridiem = "AM";
        new_hour = hourOfDay;
        new_min = minute;

        if (hourOfDay == 0)
            hourOfDay = 12;
        else if (hourOfDay == 12)
            meridiem = "PM";
        else if (hourOfDay > 12) {
            hourOfDay -= 12;
            meridiem = "PM";
        }
        if (minute < 10)
            txtAlarmedTime.setText(hourOfDay + " : 0" + minute + " " + meridiem);
        else
            txtAlarmedTime.setText(hourOfDay + " : " + minute + " " + meridiem);
    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        String day;
        String month = ((monthOfYear + 1) < 10 ? "0" : "") + (monthOfYear + 1);

        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;
        else
            day = String.valueOf(dayOfMonth);

        String dateStr = year + "-" + month + "-" + day;
        txtDate.setText(dateStr);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item_clicked = parent.getItemAtPosition(position).toString();
        prepareSpinner(item_clicked);

        for (int i = 0; i < doctorsHashmap.size(); i++) {
            if (doctorsHashmap.get(i).get("fullname").equals(item_clicked))
                doctor_id = Integer.parseInt(doctorsHashmap.get(i).get("doc_id"));
        }
    }

    public void prepareSpinner(String doctor_name) {
        listOfClinic.clear();

        for (int x = 0; x < doctorClinicHashmap.size(); x++) {
            if (doctor_name.equals(doctorClinicHashmap.get(x).get("fullname"))) {
                listOfClinic.add(doctorClinicHashmap.get(x).get("clinic_name"));
                clinicAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (checkAlarm.isChecked()) {
            setAlarmedTime.setVisibility(View.VISIBLE);
            isAlarm = 1;
            time_alarm = txtAlarmedTime.getText().toString();
        } else {
            setAlarmedTime.setVisibility(View.GONE);
            isAlarm = 0;
            time_alarm = "";
        }
    }

   /* @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String s_doctor = search_doctor.getText().toString();

        for (String doctor : listOfDoctors) {
            if (!doctor.toLowerCase().contains(s_doctor.toLowerCase())) {
                listOfClinic.clear();
                listOfClinic.add("Choose a Clinic");
                clinicAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }*/
}
