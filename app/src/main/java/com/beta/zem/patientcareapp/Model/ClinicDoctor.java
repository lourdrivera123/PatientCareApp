package com.beta.zem.patientcareapp.Model;

/**
 * Created by User PC on 7/10/2015.
 */
public class ClinicDoctor {
    int serverID, doctorID, clinicID, isActive;
    String schedule;

    public ClinicDoctor() {

    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }

    public void setClinicID(int clinicID) {
        this.clinicID = clinicID;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public int getServerID() {
        return serverID;
    }

    public int getDoctorID() {
        return doctorID;
    }

    public int getClinicID() {
        return clinicID;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getSchedule() {
        return schedule;
    }
}
