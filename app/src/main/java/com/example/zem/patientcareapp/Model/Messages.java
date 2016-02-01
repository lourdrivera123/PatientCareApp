package com.example.zem.patientcareapp.Model;

/**
 * Created by User PC on 10/13/2015.
 */
public class Messages {
    int serverID = 0, isRead = 0;
    String date, subject, content;

    public Messages() {

    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getServerID() {
        return serverID;
    }

    public int getIsRead() {
        return isRead;
    }

    public String getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
