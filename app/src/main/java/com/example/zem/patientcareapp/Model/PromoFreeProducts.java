package com.example.zem.patientcareapp.Model;

/**
 * Created by User PC on 6/5/2015.
 */
public class PromoFreeProducts {
    int id, promoFreeProductsId, promoId, numberOfUnitsFree;
    String createdAt, updatedAt, deletedAt;

    public PromoFreeProducts(){

    }

    /* SETTERS */

    public void setId(int id) {
        this.id = id;
    }

    public void setPromoFreeProductsId(int promoFreeProductsId) {
        this.promoFreeProductsId = promoFreeProductsId;
    }

    public void setPromoId(int promoId) {
        this.promoId = promoId;
    }

    public void setNumberOfUnitsFree(int numberOfUnitsFree) {
        this.numberOfUnitsFree = numberOfUnitsFree;
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

    public int getPromoFreeProductsId() {
        return promoFreeProductsId;
    }

    public int getPromoId() {
        return promoId;
    }

    public int getNumberOfUnitsFree() {
        return numberOfUnitsFree;
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
