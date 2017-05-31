package com.minlu.fosterpig.bean;

/**
 * Created by user on 2016/12/27.
 */

public class SiteVideo {

    private String siteName;
    private String videoIP;
    private String videoUser;
    private String videoPassWord;
    private int videoPort;
    private int videoChannelNumber;
    private int id;

    public SiteVideo(int id, String siteName, String videoIP, String videoUser, String videoPassWord, int videoPort, int videoChannelNumber) {
        this.id = id;
        this.siteName = siteName;
        this.videoIP = videoIP;
        this.videoUser = videoUser;
        this.videoPassWord = videoPassWord;
        this.videoPort = videoPort;
        this.videoChannelNumber = videoChannelNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getVideoIP() {
        return videoIP;
    }

    public void setVideoIP(String videoIP) {
        this.videoIP = videoIP;
    }

    public String getVideoUser() {
        return videoUser;
    }

    public void setVideoUser(String videoUser) {
        this.videoUser = videoUser;
    }

    public String getVideoPassWord() {
        return videoPassWord;
    }

    public void setVideoPassWord(String videoPassWord) {
        this.videoPassWord = videoPassWord;
    }

    public int getVideoPort() {
        return videoPort;
    }

    public void setVideoPort(int videoPort) {
        this.videoPort = videoPort;
    }

    public int getVideoChannelNumber() {
        return videoChannelNumber;
    }

    public void setVideoChannelNumber(int videoChannelNumber) {
        this.videoChannelNumber = videoChannelNumber;
    }
}
