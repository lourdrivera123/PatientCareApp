package com.example.zem.patientcareapp.Model;

import java.io.Serializable;

/**
 * Created by User PC on 5/4/2015.
 */
public class Patient implements Serializable {

    private String fname = "", mname = "", lname = "", username = "", password = "",
            occupation = "", birthdate = "", sex = "", civil_status = "", height = "",
            weight = "", cell_no = "", tel_no = "", email = "", photo = "",
            referral_id = "", referred_byUser = "", referred_byDoctor = "",
            optional_address = "", address_street = "", barangay = "", municipality = "", province = "", region = "";

    int id = 0, serverID = 0, barangay_id = 0;

    double points = 0;

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public Patient() {

    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public int getServerID() {
        return serverID;
    }

    public void setId(int id) {
        this.id = id;

    }

    public void setReferral_id(String referral_id) {
        this.referral_id = referral_id;
    }

    public void setReferred_byUser(String referred_byUser) {
        this.referred_byUser = referred_byUser;
    }

    public void setReferred_byDoctor(String referred_byDoctor) {
        this.referred_byDoctor = referred_byDoctor;
    }

    public int getId() {
        return id;
    }


    public void setFullname(String first_name, String middle_name, String last_name) {
        this.setFname(first_name);
        this.setMname(middle_name);
        this.setLname(last_name);
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCivil_status() {
        return civil_status;
    }

    public void setCivil_status(String civil_status) {
        this.civil_status = civil_status;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAddress_street() {
        return address_street;
    }

    public void setAddress_street(String address_street) {
        this.address_street = address_street;
    }

    public void setMobile_no(String cell_no) {
        this.cell_no = cell_no;
    }

    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setBarangay_id(int brgy_id) {
        this.barangay_id = brgy_id;
    }

    public void setOptional_address(String optional_address) {
        this.optional_address = optional_address;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    //GETTER
    public String getComplete_address(){ return this.getAddress_street() + ", "+this.getBarangay()+", "+this.getMunicipality(); }

    public String getOccupation() {
        return occupation;
    }

    public String getTel_no() {
        return tel_no;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getSex() {
        return sex;
    }

    public String getWeight() {
        return weight;
    }

    public String getMobile_no() {
        return cell_no;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public String getReferral_id() {
        return referral_id;
    }

    public String getReferred_byUser() {
        return referred_byUser;
    }

    public String getReferred_byDoctor() {
        return referred_byDoctor;
    }

    public String getOptional_address() {
        return optional_address;
    }

    public int getBarangay_id() {
        return barangay_id;
    }

    public String getBarangay() {
        return barangay;
    }

    public String getMunicipality() {
        return municipality;
    }

    public String getProvince() {
        return province;
    }

    public String getRegion() {
        return region;
    }
}
