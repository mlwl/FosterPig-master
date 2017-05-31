package com.minlu.fosterpig.bean;

import java.util.List;

/**
 * Created by user on 2016/11/30.
 */
public class AllSiteBean {

    private String areaName;
    private int facilitySum;
    private int facilityWarnNumber;
    private List<FacilityDetail>  facilityDetails;

    public AllSiteBean(String areaName, int facilitySum, int facilityWarnNumber, List<FacilityDetail> facilityDetails) {
        this.areaName = areaName;
        this.facilitySum = facilitySum;
        this.facilityWarnNumber = facilityWarnNumber;
        this.facilityDetails = facilityDetails;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getFacilitySum() {
        return facilitySum;
    }

    public void setFacilitySum(int facilitySum) {
        this.facilitySum = facilitySum;
    }

    public int getFacilityWarnNumber() {
        return facilityWarnNumber;
    }

    public void setFacilityWarnNumber(int facilityWarnNumber) {
        this.facilityWarnNumber = facilityWarnNumber;
    }

    public List<FacilityDetail> getFacilityDetails() {
        return facilityDetails;
    }

    public void setFacilityDetails(List<FacilityDetail> facilityDetails) {
        this.facilityDetails = facilityDetails;
    }
}
