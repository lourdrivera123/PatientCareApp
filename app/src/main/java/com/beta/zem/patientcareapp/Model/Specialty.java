package com.beta.zem.patientcareapp.Model;

import java.io.Serializable;

public class Specialty implements Serializable {
    private String name = "", created_at = "", updated_at = "", deleted_at = "";
    private int id = 0, specialty_id = 0;

    public Specialty(){

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

    public void setId(int id) {
        this.id = id;
    }

    public void setSpecialty_id(int specialty_id) {
        this.specialty_id = specialty_id;
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

    public String getDeleted_at() {
        return deleted_at;
    }

    public int getId() {
        return id;
    }

    public int getSpecialty_id() {
        return specialty_id;
    }
}
