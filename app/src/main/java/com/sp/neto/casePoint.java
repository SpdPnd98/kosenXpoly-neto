package com.sp.neto;

public class casePoint {
    private String lat;
    private String lon;
    private String details;
    private String time;
    private String date;

    public casePoint(){

    }

    public casePoint(String lat, String lon, String details, String time, String date){
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.details = details;
        this.date = date;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
