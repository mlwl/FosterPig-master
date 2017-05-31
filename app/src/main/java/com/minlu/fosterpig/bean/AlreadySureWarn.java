package com.minlu.fosterpig.bean;

/**
 * Created by user on 2016/12/5.
 */
public class AlreadySureWarn {

    private String alarmTime;
    private String handleTime;
    private String areaName;
    private String stationName;
    private int type;
    private double value;

    public AlreadySureWarn(String alarmTime, String handleTime, String stationName, String areaName, int type, double value) {
        this.alarmTime = alarmTime;
        this.handleTime = handleTime;
        this.stationName = stationName;
        this.areaName = areaName;
        this.type = type;
        this.value = value;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
