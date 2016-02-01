package com.example.zem.patientcareapp.Fragment;

import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.SwipeTabsModule.EditTabsActivity;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.ConfigurationModule.Helpers;
import com.example.zem.patientcareapp.R;
import com.example.zem.patientcareapp.SidebarModule.SidebarActivity;

public class AccountFragment extends Fragment implements View.OnClickListener {
    ImageView image_holder;
    EditText username, current_pass, new_pass, retype_new_pass, password;
    TextView changePassword, show_and_hide_pass, confirm_password;
    Button btn_save, save, cancel;
    LinearLayout change_image;

    DbHelper dbhelper;
    PatientController pc;
    static Helpers helpers;

    Dialog dialog;

    public static String NEW_PASS = "";
    public static int checkIfChangedPass = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_fragment, container, false);

        image_holder = (ImageView) rootView.findViewById(R.id.image_holder);
        username = (EditText) rootView.findViewById(R.id.username);
        btn_save = (Button) rootView.findViewById(R.id.btn_save);
        changePassword = (TextView) rootView.findViewById(R.id.changePassword);
        password = (EditText) rootView.findViewById(R.id.password);
        change_image = (LinearLayout) rootView.findViewById(R.id.change_image);
        confirm_password = (TextView) rootView.findViewById(R.id.confirm_password);

        int edit = EditTabsActivity.edit_int;
        dbhelper = new DbHelper(getActivity());
        pc = new PatientController(getActivity());
        helpers = new Helpers();

        if (edit > 0) {
            changePassword.setVisibility(View.VISIBLE);
            password.setVisibility(View.GONE);
            change_image.setVisibility(View.VISIBLE);

            String edit_uname = SidebarActivity.getUname();
            Patient patient = pc.getloginPatient(edit_uname);

            username.setText(patient.getUsername());
            String imgFile = patient.getPhoto();

            ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress);

            if (imgFile != null || !imgFile.equals("")) {
                helpers.setImage(imgFile, progressBar, image_holder);
            }

        } else {
            changePassword.setVisibility(View.GONE);
            password.setVisibility(View.VISIBLE);
            confirm_password.setVisibility(View.VISIBLE);
        }

        changePassword.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changePassword:
                dialog = new Dialog(getActivity());
                dialog.setTitle("Change password");
                dialog.setContentView(R.layout.change_password_layout);
                dialog.show();

                current_pass = (EditText) dialog.findViewById(R.id.current_pass);
                new_pass = (EditText) dialog.findViewById(R.id.new_pass);
                retype_new_pass = (EditText) dialog.findViewById(R.id.retype_new_pass);
                save = (Button) dialog.findViewById(R.id.save);
                cancel = (Button) dialog.findViewById(R.id.cancel);
                show_and_hide_pass = (TextView) dialog.findViewById(R.id.show_and_hide_pass);

                current_pass.setTransformationMethod(new PasswordTransformationMethod());
                new_pass.setTransformationMethod(new PasswordTransformationMethod());
                retype_new_pass.setTransformationMethod(new PasswordTransformationMethod());

                show_and_hide_pass.setOnClickListener(this);
                save.setOnClickListener(this);
                cancel.setOnClickListener(this);

                break;

            case R.id.save:
                String current = helpers.md5(current_pass.getText().toString());
                String newPass = helpers.md5(new_pass.getText().toString());
                String retype = helpers.md5(retype_new_pass.getText().toString());
                int check = 0;

                if (current.equals("")) {
                    current_pass.setError("Field required");
                    check += 1;
                } else {
                    if (!current.equals(SidebarActivity.getPass())) {
                        current_pass.setError("Incorrect password");
                        check += 1;
                    }
                }
                if (newPass.equals("")) {
                    new_pass.setError("Field required");
                    check += 1;
                }
                if (retype.equals("")) {
                    retype_new_pass.setError("Field required");
                    check += 1;
                }
                if (!newPass.equals(retype)) {
                    check += 1;
                    retype_new_pass.setError("Passwords do not match");
                    new_pass.setError("Passwords do not match");
                } else {
                    retype_new_pass.setError(null);
                    new_pass.setError(null);
                }

                if (check == 0) {
                    checkIfChangedPass = 20;
                    NEW_PASS = newPass;
                    dialog.dismiss();
                }

                break;

            case R.id.cancel:
                dialog.dismiss();
                break;

            case R.id.show_and_hide_pass:
                if (show_and_hide_pass.getText().equals(getResources().getString(R.string.hide_password))) {
                    current_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    new_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    retype_new_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    show_and_hide_pass.setText(getResources().getString(R.string.show_password));
                } else {
                    current_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    new_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    retype_new_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    show_and_hide_pass.setText(getResources().getString(R.string.hide_password));
                }
                show_and_hide_pass.setPaintFlags(show_and_hide_pass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                break;
        }
    }
}