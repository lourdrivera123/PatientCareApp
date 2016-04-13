package com.beta.zem.patientcareapp.Model;

/**
 * Created by Dexter B. on 6/5/2015.
 */
public class DiscountsFreeProducts {
    String createdAt, updatedAt, deletedAt;
    int type, quantityRequired, productId, id, dfpId, promoId;
    double less;

    public DiscountsFreeProducts(){

    }

    // SETTERS

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setQuantityRequired(int quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDfpId(int dfpId) {
        this.dfpId = dfpId;
    }

    public void setPromoId(int promoId){
        this.promoId = promoId;
    }

    public void setLess(double less) {
        this.less = less;
    }

    // GETTERS

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public int getType() {
        return type;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

    public int getProductId() {
        return productId;
    }

    public int getId() {
        return id;
    }

    public int getDfpId() {
        return dfpId;
    }

    public int getPromoId(){
        return promoId;
    }

    public double getLess() {
        return less;
    }
}

