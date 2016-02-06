package com.example.zem.patientcareapp.Network;

import android.content.Context;
import android.widget.Toast;

import com.example.zem.patientcareapp.Controllers.BillingController;
import com.example.zem.patientcareapp.Controllers.BranchController;
import com.example.zem.patientcareapp.Controllers.ClinicController;
import com.example.zem.patientcareapp.Controllers.ClinicDoctorController;
import com.example.zem.patientcareapp.Controllers.DiscountsFreeProductsController;
import com.example.zem.patientcareapp.Controllers.DoctorController;
import com.example.zem.patientcareapp.Controllers.DbHelper;
import com.example.zem.patientcareapp.Controllers.DosageController;
import com.example.zem.patientcareapp.Controllers.FreeProductsController;
import com.example.zem.patientcareapp.Controllers.MessageController;
import com.example.zem.patientcareapp.Controllers.OrderController;
import com.example.zem.patientcareapp.Controllers.OrderDetailController;
import com.example.zem.patientcareapp.Controllers.OrderPreferenceController;
import com.example.zem.patientcareapp.Controllers.PatientConsultationController;
import com.example.zem.patientcareapp.Controllers.PatientController;
import com.example.zem.patientcareapp.Controllers.PatientPrescriptionController;
import com.example.zem.patientcareapp.Controllers.PatientRecordController;
import com.example.zem.patientcareapp.Controllers.PatientTreatmentsController;
import com.example.zem.patientcareapp.Controllers.ProductCategoryController;
import com.example.zem.patientcareapp.Controllers.ProductSubCategoryController;
import com.example.zem.patientcareapp.Controllers.SettingController;
import com.example.zem.patientcareapp.Controllers.SpecialtyController;
import com.example.zem.patientcareapp.Controllers.SubSpecialtyController;
import com.example.zem.patientcareapp.Controllers.UpdateController;
import com.example.zem.patientcareapp.Model.DiscountsFreeProducts;
import com.example.zem.patientcareapp.Model.Clinic;
import com.example.zem.patientcareapp.Model.ClinicDoctor;
import com.example.zem.patientcareapp.Model.Consultation;
import com.example.zem.patientcareapp.Model.Doctor;
import com.example.zem.patientcareapp.Model.Dosage;
import com.example.zem.patientcareapp.Model.FreeProducts;
import com.example.zem.patientcareapp.Model.Patient;
import com.example.zem.patientcareapp.Model.PatientRecord;
import com.example.zem.patientcareapp.Model.ProductCategory;
import com.example.zem.patientcareapp.Model.ProductSubCategory;
import com.example.zem.patientcareapp.Model.Specialty;
import com.example.zem.patientcareapp.Model.SubSpecialty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.util.Log.d;

/**
 * Created by Dexter B. on 5/11/2015.
 */

public class Sync {

    JSONArray json_array_mysql = null;
    JSONArray json_array_sqlite = null;
    JSONArray json_array_final = null;
    JSONArray json_array_final_update = null;

    String tableName, tableId;
    DbHelper dbHelper;
    DoctorController doctor_controller;
    SpecialtyController sp;
    SubSpecialtyController ssp;
    ProductCategoryController pcc;
    ProductSubCategoryController pscc;
    DosageController dc;
    PatientRecordController prc;
    PatientTreatmentsController ptc;
    DiscountsFreeProductsController dfpc;
    FreeProductsController fpc;
    ClinicController clinic_controller;
    ClinicDoctorController cdc;
    PatientPrescriptionController ppc;
    SettingController sc;
    BranchController brc;
    OrderController oc;
    OrderDetailController odc;
    MessageController mc;
    PatientConsultationController ptcc;
    UpdateController uc;
    BillingController blc;
    OrderPreferenceController opc;
    Context context;

    public void init(Context c, String request, String table_name, String table_id, JSONObject response) {
        tableName = table_name;
        tableId = table_id;
        context = c;

        dbHelper = new DbHelper(context);
        sp = new SpecialtyController(context);
        ssp = new SubSpecialtyController(context);
        pcc = new ProductCategoryController(context);
        pscc = new ProductSubCategoryController(context);
        dc = new DosageController(context);
        prc = new PatientRecordController(context);
        ptc = new PatientTreatmentsController(context);
        doctor_controller = new DoctorController(context);
        dfpc = new DiscountsFreeProductsController(context);
        fpc = new FreeProductsController(context);
        clinic_controller = new ClinicController(context);
        cdc = new ClinicDoctorController(context);
        ppc = new PatientPrescriptionController(context);
        sc = new SettingController(context);
        brc = new BranchController(context);
        oc = new OrderController(context);
        odc = new OrderDetailController(context);
        mc = new MessageController(context);
        ptcc = new PatientConsultationController(context);
        uc = new UpdateController(context);
        blc = new BillingController(context);
        opc = new OrderPreferenceController(context);

        try {
            int success = response.getInt("success");
            if (success == 1) {
                json_array_mysql = response.getJSONArray(tableName);
                json_array_sqlite = dbHelper.getAllJSONArrayFrom(tableName);

                json_array_final = checkWhatToInsert(json_array_mysql, json_array_sqlite, tableId);
                json_array_final_update = checkWhatToUpdate(json_array_mysql, tableName, response.getString("latest_updated_at"));

                if (json_array_final != null) {
                    for (int i = 0; i < json_array_final.length(); i++) {
                        JSONObject json_object = json_array_final.getJSONObject(i);

                        if (json_object != null) {
                            if (tableName.equals("doctors")) {
                                if (!doctor_controller.saveDoctor(setDoctor(json_object), "insert"))
                                    d("sync_21", "wala na save");
                            } else if (tableName.equals("specialties")) {
                                if (!sp.saveSpecialty(setSpecialty(json_object), "insert"))
                                    d("sync_20", "wala na save");
                            } else if (tableName.equals("sub_specialties")) {
                                if (!ssp.saveSubSpecialty(setSubSpecialty(json_object), "insert"))
                                    d("sync_19", "wala na save");
                            } else if (tableName.equals("product_categories")) {
                                try {
                                    if (!pcc.insertProductCategory(setProductCategory(json_object)))
                                        d("sync_18", "wala na save");
                                } catch (Exception e) {
                                    Toast.makeText(context, "Something went wrong! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else if (tableName.equals("product_subcategories")) {
                                if (!pscc.insertProductSubCategory(setProductSubCategory(json_object)))
                                    d("sync_17", "wala na save");
                            } else if (tableName.equals("dosage_format_and_strength")) {
                                if (!dc.insertDosage(setDosage(json_object)))
                                    d("sync_16", "wala na save");
                            } else if (tableName.equals("patient_records")) {
                                if (prc.savePatientRecord(setPatientRecord(json_object), "insert"))
                                    d("sync_14", "wala na save");
                            } else if (tableName.equals("patient_treatments")) {
                                if (!ptc.savePatientTreatments(setTreatments(json_object), "insert"))
                                    d("sync_13", "wala na save");
                            } else if (tableName.equals("discounts_free_products")) {
                                if (!dfpc.saveDiscountsFreeProducts(setDiscountsFreeProducts(json_object), "insert"))
                                    d("sync_12", "wala na save");
                            } else if (tableName.equals("free_products")) {
                                if (!fpc.saveFreeProducts(setFreeProducts(json_object), "insert"))
                                    d("sync_11", "wala na save");
                            } else if (tableName.equals("clinics")) {
                                if (!clinic_controller.saveClinic(setClinic(json_object), "insert"))
                                    d("sync_9", "wala na save");
                            } else if (tableName.equals("clinic_doctor")) {
                                if (!cdc.saveClinicDoctor(setClinicDoctor(json_object), "insert"))
                                    d("sync_8", "wala na save");
                            } else if (tableName.equals("patient_prescriptions")) {
                                if (!ppc.savePrescription(json_object))
                                    d("sync_7", "wala na save");
                            } else if (tableName.equals("settings")) {
                                if (!sc.saveSettings(json_object, "insert"))
                                    d("sync_6", "wala na save");
                            } else if (tableName.equals("branches")) {
                                if (!brc.saveBranches(json_object))
                                    d("sync_5", "wala na save");
                            } else if (tableName.equals("orders")) {
                                if (!oc.saveOrders(json_object))
                                    d("sync_4", "wala na save");
                            } else if (tableName.equals("order_details")) {
                                if (!odc.saveOrderDetails(json_object))
                                    d("sync_3", "wala na save");
                            } else if (tableName.equals("messages")) {
                                if (!mc.saveMessages(json_object, "insert"))
                                    d("sync_2", "wala na save");
                            } else if (tableName.equals("consultations")) {
                                if (!ptcc.savePatientConsultation(setConsultation(json_object), "add"))
                                    d("sync_1", "wala na save");
                            } else if (tableName.equals("billings")) {
                                if (!blc.saveBillings(json_object))
                                    d("sync_0", "wala na save");
                            } else if (tableName.equals("order_preference")) {
                                if (!opc.savePreferenceFromJson(json_object))
                                    d("sync_0", "wala na save");
                            }
                        }
                    }
                    json_array_final = null;
                } else
                    Toast.makeText(context, "the final list is empty", Toast.LENGTH_SHORT).show();

                if (json_array_final_update != null) {
                    for (int i = 0; i < json_array_final_update.length(); i++) {
                        JSONObject json_object = json_array_final_update.getJSONObject(i);
                        if (!json_object.equals("null") && !json_object.equals(null)) {
                            if (tableName.equals("doctors")) {
                                if (!doctor_controller.saveDoctor(setDoctor(json_object), "update"))
                                    Toast.makeText(context, "failed to save ", Toast.LENGTH_SHORT).show();
                            } else if (tableName.equals("settings")) {
                                if (!sc.saveSettings(json_object, "update"))
                                    System.out.print("referral_settings FAILED TO SAVE <src: Sync.java>");
                            } else if (tableName.equals("messages")) {
                                if (!mc.saveMessages(json_object, "update"))
                                    System.out.print("messages FAILED TO SAVE <src: Sync.java>");
                            } else if (tableName.equals("consultations")) {
                                if (!ptcc.savePatientConsultation(setConsultation(json_object), "update"))
                                    System.out.print("consultations FAILED TO SAVE <src: Sync.java>");
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, "general error" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public Consultation setConsultation(JSONObject json) {
        Consultation consult = new Consultation();

        try {
            consult.setServerID(json.getInt("id"));
            consult.setPatientID(json.getInt("patient_id"));
            consult.setDoctorID(json.getInt("doctor_id"));
            consult.setClinicID(json.getInt("clinic_id"));
            consult.setDate(json.getString("date"));
            consult.setTime(json.getString("time"));
            consult.setIsAlarmed(json.getInt("is_alarm"));
            consult.setAlarmedTime(json.getString("alarm_time"));
            consult.setIs_approved(json.getInt("is_approved"));
            consult.setIs_read(json.getInt("isRead"));
            consult.setComment_doctor(json.getString("comment_doctor"));
            consult.setPtnt_is_approved(json.getInt("patient_is_approved"));
            consult.setComment_patient(json.getString("comment_patient"));
            consult.setCreated_at(json.getString("created_at"));
            consult.setUpdated_at(json.getString("updated_at"));
        } catch (Exception e) {
            d("sync1", e + "");
        }

        return consult;
    }

    public Dosage setDosage(JSONObject json_object) {
        Dosage dosage = new Dosage();
        try {
            dosage.setDosage_id(json_object.getInt("id"));
            dosage.setProduct_id(json_object.getInt("product_id"));
            dosage.setName(json_object.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dosage;
    }

    public PatientRecord setPatientRecord(JSONObject json_object) {
        PatientRecord patient_record = new PatientRecord();
        try {
            patient_record.setRecordID(json_object.getInt("id"));
            patient_record.setCpr_id(json_object.getInt("clinic_patient_record_id"));
            patient_record.setComplaints(json_object.getString("complaints"));
            patient_record.setFindings(json_object.getString("findings"));
            patient_record.setDate(json_object.getString("record_date"));
            patient_record.setDoctorID(json_object.getInt("doctor_id"));
            patient_record.setClinicID(json_object.getInt("clinic_id"));
            patient_record.setDoctorName(json_object.getString("doctor_name"));
            patient_record.setClinicName(json_object.getString("clinic_name"));
            patient_record.setCreated_at(json_object.getString("created_at"));
            patient_record.setUpdated_at(json_object.getString("updated_at"));
            patient_record.setDeleted_at(json_object.getString("deleted_at"));
        } catch (Exception e) {

        }
        return patient_record;
    }

    public ArrayList<HashMap<String, String>> setTreatments(JSONObject json) {
        ArrayList<HashMap<String, String>> listOfTreatments = new ArrayList();

        try {
            HashMap<String, String> map = new HashMap();
            map.put("treatments_id", String.valueOf(json.getInt("id")));
            map.put("patient_records_id", String.valueOf(json.getInt("patient_records_id")));
            map.put("medicine_id", json.getString("medicine_id"));
            map.put("medicine_name", json.getString("medicine_name"));
            map.put("frequency", json.getString("frequency"));
            map.put("duration", json.getString("duration"));
            map.put("duration_type", json.getString("duration_type"));
            listOfTreatments.add(map);
        } catch (Exception e) {

        }
        return listOfTreatments;
    }

    public JSONArray checkWhatToInsert(JSONArray json_array_mysql, JSONArray json_array_sqlite, String server_id) throws JSONException {
        JSONArray json_array_final_storage = new JSONArray();
        try {
            for (int i = 0; i < json_array_mysql.length(); i++) {
                JSONObject product_json_object_mysql = json_array_mysql.getJSONObject(i);
                Boolean flag = false;

                if (json_array_sqlite == null) {
                    json_array_final_storage.put(product_json_object_mysql);
                } else {
                    //checking each row in sqlite if the mysql id exists
                    for (int x = 0; x < json_array_sqlite.length(); x++) {
                        JSONObject json_object_sqlite = json_array_sqlite.getJSONObject(x);

                        if (product_json_object_mysql.getInt("id") == json_object_sqlite.getInt(server_id)) {
                            flag = true;
                        }
                    }

                    if (!flag) {
                        json_array_final_storage.put(product_json_object_mysql);
                    }

                }
            }

        } catch (JSONException e) {
            Toast.makeText(context, "error in check what to insert" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return json_array_final_storage;
    }

    public JSONArray checkWhatToUpdate(JSONArray json_array_mysql, String tblname, String latest_updated_at) {
        JSONArray doctors_json_array_final_storage = new JSONArray();
        try {
            if (!uc.getLastUpdate(tblname).equals(latest_updated_at)) {

                for (int i = 0; i < json_array_mysql.length(); i++) {

                    JSONObject json_object_mysql = json_array_mysql.getJSONObject(i);

                    if (!json_object_mysql.getString("updated_at").equals("null") && json_object_mysql.getString("updated_at") != null) {

                        if (checkDateTime(uc.getLastUpdate(tblname), json_object_mysql.getString("updated_at"))) { //to be repared
                            //the sqlite last update is lesser than from mysql
                            //put your json object into final array here.

                            doctors_json_array_final_storage.put(json_object_mysql);
//                        Log.d("Updated at Compare", "the updated_at column in mysql is greater than in sqlite");

                        } else {
//                        Log.d("Updated at Compare", "the updated_at column in sqlite is greater than in mysql");

                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return doctors_json_array_final_storage;
    }

    public boolean checkDateTime(String str1, String str2) {
        Boolean something = false;
        try {
            if (!str1.equals("") || str2.equals("")) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            /*String str1 = "12/10/2013";*/
                Date date1 = formatter.parse(str1);

            /*String str2 = "13/10/2013";*/
                Date date2 = formatter.parse(str2);

                if (date1.compareTo(date2) < 0) {
                    something = true;
                    System.out.println("date2 is Greater than my date1");
                }
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return something;
    }

    // SETTERS
    public Doctor setDoctor(JSONObject json) {
        Doctor doctor = new Doctor();
        try {

            doctor.setServer_doc_id(json.getInt("id"));
            doctor.setFullname(json.getString("fname"), json.getString("mname"), json.getString("lname"));
            doctor.setPrc_no(json.getInt("prc_no"));
            doctor.setSub_specialty_id(json.getInt("sub_specialty_id"));
            doctor.setAffiliation(json.getString("affiliation"));
            doctor.setEmail(json.getString("email"));
            doctor.setReferral_id(json.getString("referral_id"));
            doctor.setCreated_at(json.getString("created_at"));
            doctor.setUpdated_at(json.getString("updated_at"));
            doctor.setDeleted_at(json.getString("deleted_at"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return doctor;
    }

    public Clinic setClinic(JSONObject json) {
        Clinic clinic = new Clinic();
        try {
            clinic.setName(json.getString("name"));
            clinic.setClinicsId(json.getInt("id"));
            clinic.setContactNumber(json.getString("contact_no"));
            clinic.setFullAddress(json.getString(ClinicController.CLINIC_BARANGAY), json.getString(ClinicController.CLINIC_CITY),
                    json.getString(ClinicController.CLINIC_PROVINCE), json.getString(ClinicController.CLINIC_REGION));
            clinic.setCreatedAt(json.getString("created_at"));
            clinic.setUpdatedAt(json.getString("updated_at"));
            clinic.setDeletedAt(json.getString("deleted_at"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return clinic;
    }

    public ClinicDoctor setClinicDoctor(JSONObject json) {
        ClinicDoctor cd = new ClinicDoctor();

        try {
            cd.setServerID(json.getInt("id"));
            cd.setDoctorID(json.getInt("doctor_id"));
            cd.setClinicID(json.getInt("clinic_id"));
            cd.setSchedule(json.getString("clinic_sched"));
            cd.setIsActive(json.getInt("is_active"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cd;
    }

    public Specialty setSpecialty(JSONObject json) {
        Specialty specialty = new Specialty();
        try {

            specialty.setSpecialty_id(json.getInt("id"));
            specialty.setName(json.getString("name"));
            specialty.setCreated_at(json.getString("created_at"));
            specialty.setUpdated_at(json.getString("updated_at"));
            specialty.setDeleted_at(json.getString("deleted_at"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return specialty;
    }

    public SubSpecialty setSubSpecialty(JSONObject json) {
        SubSpecialty sub_specialty = new SubSpecialty();
        try {

            sub_specialty.setSub_specialty_id(json.getInt("id"));
            sub_specialty.setSpecialty_id(json.getInt("specialty_id"));
            sub_specialty.setName(json.getString("name"));
            sub_specialty.setCreated_at(json.getString("created_at"));
            sub_specialty.setUpdated_at(json.getString("updated_at"));
            sub_specialty.setDeleted_at(json.getString("deleted_at"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sub_specialty;
    }

    public ProductCategory setProductCategory(JSONObject json) throws JSONException {
        ProductCategory pc = new ProductCategory();
        try {
            pc.setName(json.getString("name"));
            pc.setCategoryId(Integer.parseInt(json.getString("id")));
            pc.setCreatedAt(json.getString("created_at"));
            pc.setUpdatedAt(json.getString("updated_at"));
            pc.setDeletedAt(json.getString("deleted_at"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pc;
    }

    public ProductSubCategory setProductSubCategory(JSONObject json) throws JSONException {
        ProductSubCategory sc = new ProductSubCategory();
        try {
            sc.setName(json.getString("name"));
            sc.setId(Integer.parseInt(json.getString("id")));
            sc.setCategoryId(Integer.parseInt(json.getString("category_id")));
            sc.setCreatedAt(json.getString("created_at"));
            sc.setUpdatedAt(json.getString("updated_at"));
            sc.setDeletedAt(json.getString("deleted_at"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sc;
    }

    public Patient setPatient(JSONObject json) {
        Patient patient = new Patient();
        try {
            patient.setServerID(json.getInt("id"));
            patient.setUsername(json.getString(PatientController.PTNT_USERNAME));
            patient.setPassword(json.getString(PatientController.PTNT_PASSWORD));
            patient.setOccupation(json.getString(PatientController.PTNT_OCCUPATION));
            patient.setBirthdate(json.getString(PatientController.PTNT_BIRTHDATE));
            patient.setSex(json.getString(PatientController.PTNT_SEX));
            patient.setCivil_status(json.getString(PatientController.PTNT_CIVIL_STATUS));
            patient.setHeight(json.getString(PatientController.PTNT_HEIGHT));
            patient.setWeight(json.getString(PatientController.PTNT_WEIGHT));
            patient.setFullname(json.getString(PatientController.PTNT_FNAME), json.getString(PatientController.PTNT_MNAME), json.getString(PatientController.PTNT_LNAME));
            patient.setAddress_street(json.getString(PatientController.PTNT_STREET));
            patient.setBarangay_id(json.getInt("address_barangay_id"));
            patient.setBarangay(json.getString("address_barangay"));
            patient.setMunicipality(json.getString("address_city_municipality"));
            patient.setProvince(json.getString("address_province"));
            patient.setRegion(json.getString("address_region"));
            patient.setTel_no(json.getString(PatientController.PTNT_TEL_NO));
            patient.setMobile_no(json.getString(PatientController.PTNT_MOBILE_NO));
            patient.setEmail(json.getString(PatientController.PTNT_EMAIL));
            patient.setPhoto(json.getString(PatientController.PTNT_PHOTO));
            patient.setPoints(json.getDouble(PatientController.PTNT_POINTS));
            patient.setReferral_id(json.getString(PatientController.PTNT_REFERRAL_ID));
            patient.setReferred_byUser(json.getString(PatientController.PTNT_REFERRED_BY_USER));
            patient.setReferred_byDoctor(json.getString(PatientController.PTNT_REFERRED_BY_DOCTOR));
            patient.setIsSenior(json.getInt(PatientController.PTNT_ISSENIOR));
            patient.setSenior_citizen_id_number(json.getString(PatientController.PTNT_SENIOR_CIN));
            patient.setSenior_id_picture(json.getString(PatientController.PTNT_SIP));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return patient;
    }

    public DiscountsFreeProducts setDiscountsFreeProducts(JSONObject json) throws JSONException {
        DiscountsFreeProducts discountsFreeProducts = new DiscountsFreeProducts();

        discountsFreeProducts.setDfpId(json.getInt("id"));
        discountsFreeProducts.setProductId(json.getInt(DiscountsFreeProductsController.DFP_PRODUCT_ID));
        discountsFreeProducts.setPromoId(json.getInt(DiscountsFreeProductsController.DFP_PROMO_ID));
        discountsFreeProducts.setLess(json.getDouble(DiscountsFreeProductsController.DFP_LESS));
        discountsFreeProducts.setQuantityRequired(json.getInt(DiscountsFreeProductsController.DFP_QUANTITY_REQUIRED));
        discountsFreeProducts.setType(json.getInt(DiscountsFreeProductsController.DFP_TYPE));
        discountsFreeProducts.setCreatedAt(json.getString(DbHelper.CREATED_AT));
        discountsFreeProducts.setUpdatedAt(json.getString(DbHelper.UPDATED_AT));
        discountsFreeProducts.setDeletedAt(json.getString(DbHelper.DELETED_AT));

        return discountsFreeProducts;
    }

    public FreeProducts setFreeProducts(JSONObject json) throws JSONException {
        FreeProducts freeProducts = new FreeProducts();

        freeProducts.setFreeProductsId(json.getInt("id"));
        freeProducts.setDfpId(json.getInt(FreeProductsController.FP_DFP_ID));
        freeProducts.setQuantityFree(json.getInt(FreeProductsController.FP_QTY_FREE));
        freeProducts.setCreatedAt(json.getString(FreeProductsController.FP_CREATED_AT));
        freeProducts.setUpdatedAt(json.getString(FreeProductsController.FP_UPDATED_AT));
        freeProducts.setDeletedAt(json.getString(FreeProductsController.FP_DELETED_AT));

        return freeProducts;
    }
}
