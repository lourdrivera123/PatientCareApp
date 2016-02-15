package com.example.zem.patientcareapp.Model;

import java.io.Serializable;

public class ProductCategory implements Serializable {
    String name = "";
    String createdAt = "";
    String updatedAt = "";
    String deletedAt = "";
    int categoryId;


    public ProductCategory(){

    }
    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryId(int id){
        this.categoryId = id;
    }

    public String getName() {
        return name;
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

    public int getCategoryId() {
        return categoryId;
    }
}
