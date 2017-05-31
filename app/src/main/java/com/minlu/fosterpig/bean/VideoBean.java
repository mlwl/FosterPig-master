package com.minlu.fosterpig.bean;

import java.util.List;

/**
 * Created by user on 2016/11/30.
 */
public class VideoBean {

    private String areaName;
    private List<SiteVideo> siteVideos;

    public VideoBean(String areaName, List<SiteVideo> siteVideos) {
        this.areaName = areaName;
        this.siteVideos = siteVideos;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public List<SiteVideo> getSiteVideos() {
        return siteVideos;
    }

    public void setSiteVideos(List<SiteVideo> siteVideos) {
        this.siteVideos = siteVideos;
    }
}
