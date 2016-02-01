package com.example.zem.patientcareapp.Model;

/**
 * Created by Zem on 5/21/2015.
 */
public class Dosage {
    int id, dosage_id, product_id;
    String name, created_at, updated_at, deleted_at;

    public Dosage(){

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDosage_id(int dosage_id) {
        this.dosage_id = dosage_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public int getId() {
        return id;
    }

    public int getDosage_id() {
        return dosage_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getName() {
        return name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
