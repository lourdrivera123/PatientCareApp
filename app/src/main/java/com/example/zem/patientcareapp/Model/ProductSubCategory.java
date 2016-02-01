package com.example.zem.patientcareapp.Model;

import java.io.Serializable;

/**
 * Created by User PC on 5/18/2015.
 */
public class ProductSubCategory implements Serializable {
    String name = "";
    int categoryId;
    String createdAt = "";
    String updatedAt = "";
    String deletedAt = "";
    int id;

    public ProductSubCategory(){

    }

    /* SETTERS */

    public void setId(int id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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


    /* GETTERS */

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCategoryId() {
        return categoryId;
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
