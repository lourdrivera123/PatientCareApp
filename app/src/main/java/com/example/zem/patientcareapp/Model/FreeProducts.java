package com.example.zem.patientcareapp.Model;

/**
 * Created by User PC on 6/5/2015.
 */
public class FreeProducts {
    int id, freeProductsId, dfpId, quantityFree, productId;
    String createdAt, updatedAt, deletedAt;

    public FreeProducts(){

    }

    /* SETTERS */

    public void setId(int id) {
        this.id = id;
    }

    public void setFreeProductsId(int freeProductsId) {
        this.freeProductsId = freeProductsId;
    }

    public void setDfpId(int dfpId) {
        this.dfpId = dfpId;
    }

    public void setQuantityFree(int quantityFree) {
        this.quantityFree = quantityFree;
    }

    public void setProductId(int productId){
        this.productId = productId;
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

    public int getFreeProductsId() {
        return freeProductsId;
    }

    public int getDfpId() {
        return dfpId;
    }

    public int getQuantityFree() {
        return quantityFree;
    }

    public int getProductId(){
        return productId;
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
