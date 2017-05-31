package com.minlu.fosterpig.bean;

/**
 * Created by user on 2016/11/30.
 */
public class MainAllInformation {

    private String areaName;
    private String siteName;
    private String facilityName;
    private String startWarnTime;
    private int siteId;
    private int facilityId;
    private int areaId;
    private int facilityType;
    private double facilityValue;
    private int isWarn;

    private int mainId;

    public MainAllInformation(int mainId, String areaName, String siteName, int siteId, String facilityName, int facilityId, int areaId, int facilityType, double facilityValue, int isWarn, String startWarnTime) {
        this.mainId = mainId;
        this.areaName = areaName;
        this.siteName = siteName;
        this.siteId = siteId;
        this.facilityName = facilityName;
        this.facilityId = facilityId;
        this.areaId = areaId;
        this.facilityType = facilityType;
        this.facilityValue = facilityValue;
        this.isWarn = isWarn;
        this.startWarnTime = startWarnTime;
    }

    public int getMainId() {
        return mainId;
    }

    public void setMainId(int mainId) {
        this.mainId = mainId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(int facilityType) {
        this.facilityType = facilityType;
    }

    public double getFacilityValue() {
        return facilityValue;
    }

    public void setFacilityValue(double facilityValue) {
        this.facilityValue = facilityValue;
    }

    public int getIsWarn() {
        return isWarn;
    }

    public void setIsWarn(int isWarn) {
        this.isWarn = isWarn;
    }

    public String getStartWarnTime() {
        return startWarnTime;
    }

    public void setStartWarnTime(String startWarnTime) {
        this.startWarnTime = startWarnTime;
    }
}
