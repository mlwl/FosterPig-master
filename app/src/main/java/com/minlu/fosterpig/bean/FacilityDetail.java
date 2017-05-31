package com.minlu.fosterpig.bean;

/**
 * Created by user on 2016/11/30.
 */
public class FacilityDetail {

    private double dataValue;
    private int facilityType;
    private int isWarn;
    private String siteName;
    private String areaName;
    private int isHandle;

    public FacilityDetail(int isHandle, double dataValue, int facilityType, int isWarn, String siteName, String areaName) {
        this.isHandle = isHandle;
        this.dataValue = dataValue;
        this.facilityType = facilityType;
        this.isWarn = isWarn;
        this.siteName = siteName;
        this.areaName = areaName;
    }

    public int getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(int isHandle) {
        this.isHandle = isHandle;
    }

    public double getDataValue() {
        return dataValue;
    }

    public void setDataValue(double dataValue) {
        this.dataValue = dataValue;
    }

    public int getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(int facilityType) {
        this.facilityType = facilityType;
    }

    public int getIsWarn() {
        return isWarn;
    }

    public void setIsWarn(int isWarn) {
        this.isWarn = isWarn;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
