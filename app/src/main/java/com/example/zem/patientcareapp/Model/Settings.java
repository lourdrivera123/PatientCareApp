package com.example.zem.patientcareapp.Model;

/**
 * Created by User PC on 9/30/2015.
 */
public class Settings {
    private int serverID, lvl_limit, delivery_minimum = 0;
    private double points, points_to_peso, referral_comm, comm_variation, delivery_charge = 0.0;
    private String created_at, updated_at, deleted_at = "";

    public Settings() {

    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public void setLvl_limit(int lvl_limit) {
        this.lvl_limit = lvl_limit;
    }

    public void setDelivery_minimum(int delivery_minimum) {
        this.delivery_minimum = delivery_minimum;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public void setPoints_to_peso(double points_to_peso) {
        this.points_to_peso = points_to_peso;
    }

    public void setDelivery_charge(double delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public void setReferral_comm(double referral_comm) {
        this.referral_comm = referral_comm;
    }

    public void setComm_variation(double comm_variation) {
        this.comm_variation = comm_variation;
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

    public int getServerID() {
        return serverID;
    }

    public int getLvl_limit() {
        return lvl_limit;
    }

    public int getDelivery_minimum() {
        return delivery_minimum;
    }

    public double getPoints() {
        return points;
    }

    public double getPoints_to_peso() {
        return points_to_peso;
    }

    public double getDelivery_charge() {
        return delivery_charge;
    }

    public double getReferral_comm() {
        return referral_comm;
    }

    public double getComm_variation() {
        return comm_variation;
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
}
