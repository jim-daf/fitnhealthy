package com.example.fitnhealthy.models;

public class ProfileImageUpload {
    String filename,fileUrl;

    public ProfileImageUpload(){

    }
    public ProfileImageUpload(String filename, String fileUrl) {
        this.filename = filename;
        this.fileUrl = fileUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
