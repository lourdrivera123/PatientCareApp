package com.example.zem.patientcareapp.Model;

/**
 * Created by Dexter B. on 6/2/2015.
 */
public class Clinic {
    String name, contactNumber, createdAt, updatedAt, deletedAt, address_street = "", address_barangay = "", address_city_municipality = "",
            address_province = "", address_region = "", country = "", address_zip = "", building = "";
    int id, clinicsId, unit_floor_room_no = 0, lot_no = 0, block_no = 0, phase_no = 0, address_house_no = 0;


    public Clinic() {

    }

    /* SETTERS */

    public void setId(int id) {
        this.id = id;
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

    public void setUnit_floor_room_no(String unit_floor_room_no) {
        if(!unit_floor_room_no.equals("")){
            this.unit_floor_room_no = Integer.parseInt(unit_floor_room_no);
        }
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setLot_no(String lot_no) {
        if(!lot_no.equals("")) {
            this.lot_no = Integer.parseInt(lot_no);
        }
    }

    public void setBlock_no(String block_no) {
        if(!block_no.equals("")){
            this.block_no = Integer.parseInt(block_no);
        }
    }

    public void setPhase_no(String phase_no) {
        if(!phase_no.equals("")){
            this.phase_no = Integer.parseInt(phase_no);
        }
    }

    public void setAddress_house_no(String address_house_no) {
        if(!address_house_no.equals("")){
            this.address_house_no = Integer.parseInt(address_house_no);
        }
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

    public void setCountry(String country) {
        this.country = country;
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

    public void setFullAddress(String unit_floor_room_no, String building, String lot_no, String block_no,
                               String phase_no, String address_house_no, String street, String barangay, String city_municipality,
                               String province, String region, String zip) {
        this.setUnit_floor_room_no(unit_floor_room_no);
        this.setBuilding(building);
        this.setLot_no(lot_no);
        this.setBlock_no(block_no);
        this.setPhase_no(phase_no);
        this.setAddress_house_no(address_house_no);
        this.setAddress_street(street);
        this.setAddress_barangay(barangay);
        this.setAddress_city_municipality(city_municipality);
        this.setAddress_province(province);
        this.setAddress_zip(zip);
        this.setAddress_region(region);
        this.setCountry(country);
    }

    /* GETTERS */

    public int getId() {
        return id;
    }

    public String getAddress_street() {
        return address_street;
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

    public int getUnit_floor_room_no() {
        return unit_floor_room_no;
    }

    public String getBuilding() {
        return building;
    }

    public int getLot_no() {
        return lot_no;
    }

    public int getBlock_no() {
        return block_no;
    }

    public int getPhase_no() {
        return phase_no;
    }

    public String getAddress_barangay() {
        return address_barangay;
    }

    public String getAddress_region() {
        return address_region;
    }

    public int getAddress_house_no() {
        return address_house_no;
    }

    public String getAddress_city_municipality() {
        return address_city_municipality;
    }

    public String getAddress_province() {
        return address_province;
    }

    public String getAddress_zip() {
        return address_zip;
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
