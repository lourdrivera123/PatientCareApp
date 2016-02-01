package com.example.zem.patientcareapp.Model;

import java.io.Serializable;

/**
 * Created by Dexter B. on 7/10/2015.
 */
public class Promo implements Serializable {
    String name, startDate, endDate, createdAt, updatedAt, deletedAt;
    int id, serverPromoId;

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setServerPromoId(int serverPromoId) {
        this.serverPromoId = serverPromoId;
    }


    // Getters

    public String getName() {
        return name;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
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

    public int getId() {
        return id;
    }

    public int getServerPromoId() {
        return serverPromoId;
    }
}
