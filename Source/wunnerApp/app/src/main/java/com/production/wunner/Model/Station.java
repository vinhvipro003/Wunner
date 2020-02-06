package com.production.wunner.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Station implements Serializable {
    @SerializedName("StationID")
    private String StationID;
    @SerializedName("StationName")
    private String StationName;
    @SerializedName("StationPoint")
    private float StationPoint;
    @SerializedName("StationTime")
    private float StationTime;
    @SerializedName("StationDate")
    private String StationDate;
    @SerializedName("StationDescription")
    private String StationDescription;
    @SerializedName("StationIndex")
    private float StationIndex;
    @SerializedName("StationLaw")
    private String StationLaw;
    @SerializedName("StationLatitude")
    private String StationLatitude;
    @SerializedName("StationLongtitude")
    private String StationLongtitude;
    private Marker  marker;
    private Double Latitude;
    private Double Longitude;
    private Boolean checkin;

    public String getStationLongtitude() {
        return StationLongtitude;
    }

    public void setStationLongtitude(String stationLongtitude) {
        StationLongtitude = stationLongtitude;
    }

    public Boolean getCheckin() {
        return checkin;
    }

    public void setCheckin(Boolean checkin) {
        this.checkin = checkin;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    private LatLng latLng;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
// Getter Methods

    public String getStationID() {
        return StationID;
    }

    public String getStationName() {
        return StationName;
    }

    public float getStationPoint() {
        return StationPoint;
    }

    public float getStationTime() {
        return StationTime;
    }

    public String getStationDate() {
        return StationDate;
    }

    public String getStationDescription() {
        return StationDescription;
    }

    public float getStationIndex() {
        return StationIndex;
    }

    public String getStationLaw() {
        return StationLaw;
    }

    public String getStationLatitude() {
        return StationLatitude;
    }

    // Setter Methods

    public void setStationID(String StationID) {
        this.StationID = StationID;
    }

    public void setStationName(String StationName) {
        this.StationName = StationName;
    }

    public void setStationPoint(float StationPoint) {
        this.StationPoint = StationPoint;
    }

    public void setStationTime(float StationTime) {
        this.StationTime = StationTime;
    }

    public void setStationDate(String StationDate) {
        this.StationDate = StationDate;
    }

    public void setStationDescription(String StationDescription) {
        this.StationDescription = StationDescription;
    }

    public void setStationIndex(float StationIndex) {
        this.StationIndex = StationIndex;
    }

    public void setStationLaw(String StationLaw) {
        this.StationLaw = StationLaw;
    }

    public void setStationLatitude(String StationLatitude) {
        this.StationLatitude = StationLatitude;
    }
}