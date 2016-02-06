package com.example.zem.patientcareapp.Model;

/**
 * Created by Dexter B. on 6/2/2015.
 */
public class Clinic {
    String name, contactNumber, createdAt, updatedAt, deletedAt, address_barangay = "", address_city_municipality = "",
            address_province = "", address_region = "", addition_address = "";
    int id, clinicsId, barangay_id;

    public Clinic() {

    }

    /* SETTERS */

    public void setId(int id) {
        this.id = id;
    }

    public String getAddition_address() {
        return addition_address;
    }

    public void setAddition_address(String addition_address) {
        this.addition_address = addition_address;
    }

    public int getBarangay_id() {
        return barangay_id;
    }

    public void setBarangay_id(int barangay_id) {
        this.barangay_id = barangay_id;
    }

    public void setClinicsId(int clinics_id) {
        this.clinicsId = clinics_id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setFullAddress(String barangay, String city_municipality,
                               String province, String region) {

        this.setAddress_barangay(barangay);
        this.setAddress_city_municipality(city_municipality);
        this.setAddress_province(province);
        this.setAddress_region(region);
//        this.setBarangay_id(brgy_id);
//        this.setAddition_address(additional_address);
    }

    /* GETTERS */

    public int getId() {
        return id;
    }

    public int getClinicsId() {
        return clinicsId;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }


    public String getAddress_barangay() {
        return address_barangay;
    }

    public String getAddress_region() {
        return address_region;
    }


    public String getAddress_city_municipality() {
        return address_city_municipality;
    }

    public String getAddress_province() {
        return address_province;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }
}
