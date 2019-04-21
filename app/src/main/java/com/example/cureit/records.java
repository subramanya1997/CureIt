package com.example.cureit;

public class records {

    private String description,date,doctorsName;

    public records() {

    }

    public records(String description, String date, String doctorsName) {
        this.description = description;
        this.date = date;
        this.doctorsName = doctorsName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctorsName() {
        return doctorsName;
    }

    public void setDoctorsName(String doctorsName) {
        this.doctorsName = doctorsName;
    }
}
