package com.example.zem.patientcareapp.Model;

import java.io.Serializable;

/**
 * Created by Dexter B. on 5/4/2015.
 */
public class Secretary implements Serializable {
    private String fname = "", mname = "", lname = "",
            address_house_no = "", address_street = "", address_barangay = "",
            address_city_municipality = "", address_province = "", address_region = "", address_zip = "",
            cell_no = "", tel_no = "", photo = "", email = "", country = "";

    public Secretary(){

    }


    /**    SETTERS ------------------------------------------------------------------ **/
    public void setFullAddress(String house_no, String street, String barangay, String city_municipality,
                               String province, String region, String country, String zip){
        this.setAddress_house_no(house_no);
        this.setAddress_street(street);
        this.setAddress_barangay(barangay);
        this.setAddress_city_municipality(city_municipality);
        this.setAddress_province(province);
        this.setAddress_zip(zip);
        this.setAddress_region(region);
        this.setCountry(country);
    }

    public void setFullname(String first_name, String middle_name, String last_name){
        this.setFname(first_name);
        this.setMname(middle_name);
        this.setLname(last_name);
    }

    public void setContactInfo(String email, String cell_no, String tel_no){
        this.setEmail(email);
        this.setCell_no(cell_no);
        this.setTel_no(tel_no);
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

    public void setAddress_house_no(String address_house_no) {
        this.address_house_no = address_house_no;
    }

    public void setAddress_street(String address_street) {
        this.address_street = address_street;
    }

    public void setAddress_barangay(String address_barangay) {
        this.address_barangay = address_barangay;
    }

    public void setAddress_city_municipality(String address_city_municipality) {
        this.address_city_municipality = address_city_municipality;
    }

    public void setAddress_province(String address_province) {
        this.address_province = address_province;
    }

    public void setAddress_region(String address_region) {
        this.address_region = address_region;
    }

    public void setAddress_zip(String address_zip) {
        this.address_zip = address_zip;
    }

    public void setCell_no(String cell_no) {
        this.cell_no = cell_no;
    }

    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCountry(String country){
        this.country = country;
    }

    /**   GETTERS ------------------------------------------------------------------- **/
    public String getFullname(boolean middlename_is_full){
        if(middlename_is_full == true) {
            return fname + " " + mname + " " + lname;
        }
        return fname+" "+mname.indexOf(0)+". "+lname;

    }

    public String getFullAddress(){
        return address_house_no+" "+address_street+" "+address_barangay+"\n"
                +address_city_municipality+" "+address_province+" "+address_region+" "+country+", "+address_zip;
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

    public String getAddress_house_no() {
        return address_house_no;
    }

    public String getAddress_street() {
        return address_street;
    }

    public String getAddress_barangay() {
        return address_barangay;
    }

    public String getAddress_city_municipality() {
        return address_city_municipality;
    }

    public String getAddress_province() {
        return address_province;
    }

    public String getAddress_region() {
        return address_region;
    }

    public String getAddress_zip() {
        return address_zip;
    }

    public String getCell_no() {
        return cell_no;
    }

    public String getTel_no() {
        return tel_no;
    }

    public String getPhoto() {
        return photo;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry(){
        return country;
    }
}
