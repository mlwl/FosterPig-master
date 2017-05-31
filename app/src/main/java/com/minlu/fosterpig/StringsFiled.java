package com.minlu.fosterpig;

import android.graphics.Color;

/**
 * Created by user on 2016/7/27.
 */
public class StringsFiled {

    public final static String IP_ADDRESS_PREFIX = "address";

    public final static String ACTIVITY_TITLE = "activity_title";

    public final static String OPEN_FRAGMENT_BUNDLE_KEY = "open_fragment_bundle_key";

    public final static String MAIN_TO_WARN_AMMONIA = "main_to_warn_ammonia";

    public final static String MAIN_TO_WARN_AMMONIA_JSON = "main_to_warn_ammonia_json";

    public final static String MAIN_TO_ALREADY_WARN_AMMONIA_JSON = "main_to_already_warn_ammonia_json";

    public final static String MAIN_TO_WARN_TEMPERATURE = "main_to_warn_temperature";

    public final static String MAIN_TO_WARN_TEMPERATURE_JSON = "main_to_warn_temperature_json";

    public final static String MAIN_TO_ALREADY_WARN_TEMPERATURE_JSON = "main_to_already_warn_temperature_json";

    public final static String MAIN_TO_WARN_HUMIDITY = "main_to_warn_humidity";

    public final static String MAIN_TO_WARN_HUMIDITY_JSON = "main_to_warn_humidity_json";

    public final static String MAIN_TO_ALREADY_WARN_HUMIDITY_JSON = "main_to_already_warn_humidity_json";

    public final static String MAIN_TO_WARN_POWER_SUPPLY = "main_to_warn_power_supply";

    public final static String MAIN_TO_WARN_POWER_SUPPLY_JSON = "main_to_warn_power_supply_json";

    public final static String MAIN_TO_ALREADY_WARN_POWER_SUPPLY_JSON = "main_to_already_warn_power_supply_json";

    public final static String TAG_OPEN_ALL_SITE_FRAGMENT = "all_site";

    public final static String TAG_OPEN_WARN_INFORMATION_FRAGMENT = "warn_information";

    public final static String TAG_OPEN_SURE_WARN_FRAGMENT = "sure_warn";

    public final static String TAG_OPEN_VIDEO_FRAGMENT = "video";

    public final static String TAG_OPEN_NO_SURE_FRAGMENT = "no_sure";

    public final static String TAG_OPEN_ALREADY_SURE_FRAGMENT = "already_sure";

    public final static String INFORM_WARN = "inform_warn";

    public final static String LOGIN_USER = "login_user";

    public static final String IS_ALLOW_SOUND_PLAY = "is_allow_sound_play";

    public static final String IS_ALLOW_SOUND_PLAY_TIME = "is_allow_sound_play_time";

    public static final String MEDIA_IS_PLAYING = "media_is_playing";

    public final static String IS_AUTO_LOGIN = "is_auto_login";

    public final static String IS_SURE_WARN = "is_sure_warn";

    public final static String VIDEO_IP = "video_ip";

    public final static String VIDEO_USER = "video_user";

    public final static String VIDEO_PASSWORD = "video_password";

    public final static String VIDEO_PORT = "video_port";

    public final static String VIDEO_CHANNEL_NUMBER = "video_channel_number";

    public final static String VIDEO_ID = "video_id";

    public final static String TRUE_TIME_DATA_ACTIVITY_ALREADY_PRESS = "true_time_data_activity_already_press";

    /**
     * 获取二级列表
     */
    public final static String GET_TWO_LIST_VIEW = "get_two_list_view";
    /**
     * 父节点类型
     */
    public final static String PARENT_NODE_TYPE = "parentNodeType";
    /**
     * 父节点ID
     */
    public final static String PARENT_ID = "parentId";
    /**
     * 摄像头信息
     */
    public final static String CAMERA_INFORMATION = "camera_information";


    // 视频
    public final static String VIDEO_LOGIN_IP_KEY = "video_login_ip";

    public final static String VIDEO_LOGIN_USER_NAME_KEY = "video_login_user_name";

    public final static String VIDEO_LOGIN_PASS_WORD_KEY = "video_login_pass_word";

    // 第一次进入app
    public final static String FIRST_IN_APP = "first_in_app";
    // 第一次进入login
    public final static String FIRST_IN_LOGIN = "first_in_login";


    // 在main界面中的hundle
    public final static int SERVER_OUTAGE = 0;// 服务器宕机

    public final static int SERVER_THROW = 1;// 服务器异常

    public final static int STOP_LOADING_BUT_NO_CLICK = 2;

    public final static int MAIN_ANALYSIS_FINISH_JSON = 3;

    public final static int MAIN_DISPOSE_DATA_TO_UI = 4;

    public static final int SERVER_NO_DATA = 5;
    // 在main界面中的hundle

    public final static int MAIN_TO_WARN_VALUE_AMMONIA = 0;// 氨气报警信息

    public final static int MAIN_TO_WARN_VALUE_TEMPERATURE = 1;// 温度报警信息

    public final static int MAIN_TO_WARN_VALUE_HUMIDITY = 2;// 湿度报警信息

    public final static int MAIN_TO_WARN_VALUE_POWER_SUPPLY = 3;

    //
    public final static int SELECT_WARN_INFORMATION_TAB = 3;// 所有的报警信息

    public final static int SELECT_ALL_SITE_TAB = 4;// 所有站点信息

    public final static int SELECT_SURE_WARN_INFORMATION_TAB = 5;// 已经确认的报警信息

    public final static int SELECT_VIDEO_TAB = -1;// 视频界面Fragment暂时不需要传递int值
    //

    //  刷新的动画颜色
    public final static int SWIPE_REFRESH_FIRST_ROUND_COLOR = Color.RED;

    public final static int SWIPE_REFRESH_SECOND_ROUND_COLOR = Color.GREEN;

    public final static int SWIPE_REFRESH_THIRD_ROUND_COLOR = Color.BLUE;

    public final static int SWIPE_REFRESH_BACKGROUND = Color.WHITE; // 0xFF000000

    //  报警服务的响铃间隔时间
    public final static int ALARM_INTERVAL_TIME = 3600000;
    // 视频界面中实时数据控件存在的时间单位s
    public final static int VIDEO_TRUE_TIME_DATA_KEEP_TIME = 10;
    // 视频界面中实时数据控件刷新数据的时间单位ms
    public static final long VIDEO_TRUE_TIME_SHOW_INTERVAL = 5000;

    // 观察者模式的区分
    public final static int OBSERVER_MEDIA_PLAYER_PAUSE = 0;

    public final static int OBSERVER_MEDIA_PLAYER_IS_PLAYING = 1;

    public final static int OBSERVER_AMMONIA_SURE = 2;

    public final static int OBSERVER_TEMPERATURE_SURE = 3;

    public final static int OBSERVER_HUMIDITY_SURE = 4;

    public final static int OBSERVER_POWER_SUPPLY_SURE = 5;

    public final static int OBSERVER_UPDATE_NOTIFICATION = 6;


    /**
     * 获取监控点信息
     */
    public final static int GET_CAMERA_INFO = 1;
    /**
     * 获取监控点信息成功
     */
    public final static int GET_CAMERA_INFO_SUCCESS = 2;
    /**
     * 获取监控点信息失败
     */
    public final static int GET_CAMERA_INFO_FAILURE = 3;
    /**
     * 获取设备信息
     */
    public final static int GET_DEVICE_INFO = 4;
    /**
     * 获取设备信息成功
     */
    public final static int GET_DEVICE_INFO_SUCCESS = 5;
    /**
     * 获取设备信息失败
     */
    public final static int GET_DEVICE_INFO_FAILURE = 6;
}
