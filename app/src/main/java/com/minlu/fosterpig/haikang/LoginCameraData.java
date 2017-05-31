package com.minlu.fosterpig.haikang;

import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.LoginData;

public class LoginCameraData {

    private static LoginCameraData loginCameraData;

    /*
    * 获取本类的单例对象
    * */
    public static LoginCameraData getInstance() {
        if (loginCameraData == null) {
            synchronized (LoginCameraData.class) {
                if (loginCameraData == null) {
                    loginCameraData = new LoginCameraData();
                }
            }
        }
        return loginCameraData;
    }

    /**
     * 登录返回的LoginData对象数据
     */
    private LoginData mLoginData;

    /**
     * 登陆时的Ip地址
     */
    private String mLoginIpAddress;

    /**
     * 监控点信息，用作临时传递数据用
     */
    private Camera mCamera;


    /**
     * 保存登录成功返回的LoginData信息
     */
    public void setLoginData(LoginData mLoginData) {
        this.mLoginData = mLoginData;
    }

    /**
     * 获取登录成功返回的LoginData信息
     */
    public LoginData getLoginData() {
        return mLoginData;
    }

    /*
    * 保存登录成功后的Ip地址
    * */
    public String getLoginIpAddress() {
        return mLoginIpAddress;
    }

    /*
    * 获取登录成功后的Ip地址
    * */
    public void setLoginIpAddress(String mLoginIpAddress) {
        this.mLoginIpAddress = mLoginIpAddress;
    }

    /**
     * 保存监控点信息
     */
    public void setCamera(Camera mCamera) {
        this.mCamera = mCamera;
    }

    /**
     * 获取监控点信息
     */
    public Camera getCamera() {
        return mCamera;
    }
}
