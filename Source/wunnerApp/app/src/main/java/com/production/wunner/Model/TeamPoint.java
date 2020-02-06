package com.production.wunner.Model;

import com.google.gson.annotations.SerializedName;

public class TeamPoint {
    @SerializedName("StationID")
    private String StationID;
    @SerializedName("Point")
    private float Point;
    @SerializedName("TimeStart")
    private float TimeStart;
    @SerializedName("TimeEnd")
    private float TimeEnd;

    public TeamPoint(String stationID, float point, float timeStart, float timeEnd) {
        StationID = stationID;
        Point = point;
        TimeStart = timeStart;
        TimeEnd = timeEnd;
    }
// Getter Methods

    public String getStationID() {
        return StationID;
    }

    public float getPoint() {
        return Point;
    }

    public float getTimeStart() {
        return TimeStart;
    }

    public float getTimeEnd() {
        return TimeEnd;
    }

    // Setter Methods

    public void setStationID(String StationID) {
        this.StationID = StationID;
    }

    public void setPoint(float Point) {
        this.Point = Point;
    }

    public void setTimeStart(float TimeStart) {
        this.TimeStart = TimeStart;
    }

    public void setTimeEnd(float TimeEnd) {
        this.TimeEnd = TimeEnd;
    }
}
