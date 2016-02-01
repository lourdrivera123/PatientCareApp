package com.example.zem.patientcareapp.Model;

/**
 * Created by Dexter B. on 6/5/2015.
 */
public class PromoDiscount {
    String name, startDate, endDate, createdAt, updatedAt, deletedAt;
    int type, quantityRequired, productId, id, promoDiscountId;
    double less;
    public PromoDiscount(){

    }

    /* SETTERS */

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

    public void setPromoDiscountId(int promoDiscountId) {
        this.promoDiscountId = promoDiscountId;
    }

    public void setLess(double less) {
        this.less = less;
    }

    /* GETTERS */

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

    public int getPromoDiscountId() {
        return promoDiscountId;
    }

    public double getLess() {
        return less;
    }
}

