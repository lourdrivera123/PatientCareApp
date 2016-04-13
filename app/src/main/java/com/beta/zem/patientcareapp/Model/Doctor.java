package com.beta.zem.patientcareapp.Model;

import java.io.Serializable;

/**
 * Created by Dexter B. on 5/4/2015.
 */

public class Doctor implements Serializable {
    private String fname = "", mname = "", lname = "",
            affiliation = "", specialty = "", sub_specialty = "",
            email = "", referral_id = "", created_at = "", updated_at = "", deleted_at = "";

    private int prc_no = 0, server_doc_id = 0, sub_specialty_id = 0;

    public Doctor() {

    }

    public void setFullname(String first_name, String middle_name, String last_name) {
        this.setFname(first_name);
        this.setMname(middle_name);
        this.setLname(last_name);
    }

    public void setServer_doc_id(int server_doc_id) {
        this.server_doc_id = server_doc_id;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setPrc_no(int prc_no) {
        this.prc_no = prc_no;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setSub_specialty(String sub_specialty) {
        this.sub_specialty = sub_specialty;
    }

    public void setSub_specialty_id(int sub_specialty_id) { this.sub_specialty_id = sub_specialty_id; }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setEmail(String email) { this.email = email; }

    public void setReferral_id(String referral_id) { this.referral_id = referral_id; }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public int getServer_doc_id() {
        return server_doc_id;
    }

    public String getFname() {
        return fname;
    }

    public String getMname() {
        return mname;
    }

    public String getLname() {
        return lname;
    }

    public int getPrc_no() {
        return prc_no;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getSub_specialty() {
        return sub_specialty;
    }

    public int getSub_specialty_id() {
        return sub_specialty_id;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getEmail() { return email; }

    public String getReferral_id() { return referral_id; }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }
}
