package com.production.wunner.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Location {
    private String Name,Description,Mentor;
    private LatLng latLng;
    private int TimeDest, TimeJob;
    private int Radius=100;
    private Marker myMarker;
    private boolean CheckIn;

    public boolean isCheckIn() {
        return CheckIn;
    }

    public void setCheckIn(boolean checkIn) {
        CheckIn = checkIn;
    }

    public int getRadius() {
        return Radius;
    }
    public void setRadius(int radius) {
        Radius = radius;
    }

    public Marker getMyMarker() {
        return myMarker;
    }

    public void setMyMarker(Marker myMarker) {
        this.myMarker = myMarker;
    }

    public int getTimeDest() {
        return TimeDest;
    }

    public void setTimeDest(int timeDest) {
        TimeDest = timeDest;
    }

    public int getTimeJob() {
        return TimeJob;
    }

    public void setTimeJob(int timeJob) {
        TimeJob = timeJob;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMentor() {
        return Mentor;
    }

    public void setMentor(String mentor) {
        Mentor = mentor;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Location(String name,LatLng latLng) {
        Name = name;
        this.latLng = latLng;
        this.CheckIn=false;
    }
}
