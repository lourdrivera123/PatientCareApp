package com.example.zem.patientcareapp.Model;

import java.io.Serializable;

public class Product implements Serializable {
    String name, genericName, description, unit, packing, photo, sku, createdAt, updatedAt, deletedAt;
    int id, prescriptionRequired, productId, subCategoryId, qtyPerPacking, availableQuantity;
    double price;

    public Product() {

    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getGenericName() {
        return genericName;
    }

    public String getDescription() {
        return description;
    }

    public int getPrescriptionRequired() {
        return prescriptionRequired;
    }

    public String getUnit() {
        return unit;
    }

    public String getPacking() {
        return packing;
    }

    public int getQtyPerPacking() {
        return qtyPerPacking;
    }

    public double getPrice() {
        return price;
    }

    public String getSku() {
        return sku;
    }

    public String getPhoto() {
        return photo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrescriptionRequired(int prescriptionRequired) {
        this.prescriptionRequired = prescriptionRequired;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public void setQtyPerPacking(int qtyPerPacking) {
        this.qtyPerPacking = qtyPerPacking;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setId(int id) {
        this.id = id;
    }
}
