package com.minlu.fosterpig.haikang;

import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.rtsp.RtspClientCallback;
import com.hikvision.sdk.consts.ConstantLiveSDK;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.nio.ByteBuffer;

/**
 * control liveing logic Created by hanshuangwu on 2016/2/1.
 */
public class LiveControl implements RtspClientCallback, PlayerCallBack.PlayerDisplayCB {

    private static final String TAG = "LiveControl";
    /**
     * 初始化阶段
     */
    public static final int LIVE_INIT = 0;
    /**
     * 取流阶段
     */
    public static final int LIVE_STREAM = 1;
    /**
     * 播放阶段
     */
    public static final int LIVE_PLAY = 2;
    /**
     * 预览状态
     */
    private int mLiveState = LIVE_INIT;

    /**
     * 播放地址
     */
    private String liveUrl = null;

    private RtspClient mRtspHandler = null;
    private Player mPlayerHandler = null;
    /**
     * 设备账号
     */
    private String username = "";
    /**
     * 账号密码
     */
    private String password = "";

    /**
     * surfaceView on which show videos
     */
    private SurfaceView mSurfaceView = null;

    /**
     * create engine id of RTSP
     */
    private int mRtspEngineIndex = RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID;
    private LiveCallBack mLiveCallBack = null;
    /**
     * 播放流量
     */
    private long mStreamRate = 0;
    /**
     * 播放库播放端口
     */
    private int mPlayerPort = -1;
    private int connectNum = 0;

    /**
     * 构造函数
     */
    public LiveControl() {
        mLiveState = LIVE_INIT;
        mPlayerHandler = Player.getInstance();
        mRtspHandler = RtspClient.getInstance();
    }

    /**
     * 设置预览参数
     *
     * @param url      播放地址
     * @param name     登陆设备的用户名
     * @param password 登陆设备的密码
     */
    public void setLiveParams(String url, String name, String password) {
        liveUrl = url;
        username = name;
        this.password = password;
    }

    /**
     * 获取当前播放状态
     *
     * @return LIVE_INIT初始化、LIVE_STREAM取流、LIVE_PLAY播放、LIVE_RELEASE释放资源
     */
    public int getLiveState() {
        return mLiveState;
    }

    /**
     * start live
     */
    public void startLive(SurfaceView sf) {
        if (null == sf) {
            Log.e(TAG, "startLive():: surfaceView is null");
        }
        mSurfaceView = sf;

        if (LIVE_STREAM == mLiveState) {
            Log.e(TAG, "startLive():: is palying");
        }
        startRtsp();
    }

    private void startRtsp() {
        if (null == mRtspHandler) {
            Log.e(TAG, "startRtsp():: mRtspHandler is null");
            return;
        }
        mRtspEngineIndex = mRtspHandler.createRtspClientEngine(this, RtspClient.RTPRTSP_TRANSMODE);
        if (mRtspEngineIndex < 0) {
            Log.e(TAG, "startRtsp():: errorCode is R:" + mRtspHandler.getLastError());
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLiveSDK.RTSP_FAIL);
            }
            return;
        }

        boolean ret = mRtspHandler.startRtspProc(mRtspEngineIndex, liveUrl, username, password);
        if (!ret) {
            Log.e(TAG, "startRtsp():: errorCode is R" + mRtspHandler.getLastError());
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLiveSDK.RTSP_FAIL);
            }
            return;
        }
        mLiveState = LIVE_STREAM;
        if (null != mLiveCallBack) {
            mLiveCallBack.onMessageCallback(ConstantLiveSDK.RTSP_SUCCESS);
        }
    }

    /**
     * set callback at play control level
     */
    public void setLiveCallBack(LiveCallBack liveCallBack) {
        this.mLiveCallBack = liveCallBack;
    }

    /**
     * @param handle    引擎ID
     * @param dataType  数据类型，决定data的数据类型，包括DATATYPE_HEADER和DATATYPE_STREAM两种类型
     * @param data      回调数据，分为：header数据和stream数据，由datatype作区分，header用于初始化播放库
     * @param length    data的数据长度
     * @param timeStamp 时间戳（保留）
     * @param packetNo  rtp包号（保留）
     * @param useId     用户数据，默认就是引擎ID，与handle相同
     */
    @Override
    public void onDataCallBack(int handle, int dataType, byte[] data, int length, int timeStamp, int packetNo, int useId) {
        if (mStreamRate + length >= Long.MAX_VALUE) {
            mStreamRate = 0;
        }
        mStreamRate += length;

        switch (dataType) {
            case RtspClient.DATATYPE_HEADER:
                boolean ret = processStreamHeader(data, length);
                if (!ret) {
                    if (null != mLiveCallBack) {
                        mLiveCallBack.onMessageCallback(ConstantLiveSDK.START_OPEN_FAILED);
                        return;
                    } else {
                        Log.e(TAG, "onDataCallBack():: liveCallBack is null");
                    }
                } else {
                    Log.e(TAG, "MediaPlayer Header success!");
                }
                break;
            default:
                processStreamData(data, length);
                break;
        }
        processRecordData(dataType, data, length);

    }

    /**
     * 录像数据处理
     *
     * @param dataType   数据流
     * @param dataBuffer 数据缓存
     * @param dataLength 数据长度
     */
    private void processRecordData(int dataType, byte[] dataBuffer, int dataLength) {
        if (null == dataBuffer || dataLength == 0) {
            return;
        }

        if (RtspClient.DATATYPE_HEADER == dataType) {
            // 数据流
            ByteBuffer mStreamHeadDataBuffer = ByteBuffer.allocate(dataLength);
            for (int i = 0; i < dataLength; i++) {
                mStreamHeadDataBuffer.put(dataBuffer[i]);
            }
        }
        dataBuffer = null;
    }

    /**
     * 向播放库塞数据
     */
    private void processStreamData(byte[] data, int length) {
        if (null == data || 0 == length) {
            Log.e(TAG, "processStreamData():: Stream data is null or length is zero");
            return;
        }
        if (null != mPlayerHandler) {
            boolean ret = mPlayerHandler.inputData(mPlayerPort, data, length);
            if (!ret) {
                SystemClock.sleep(10);
            }
        }
    }

    /**
     * 处理数据流头
     */
    private boolean processStreamHeader(byte[] data, int length) {
        if (-1 != mPlayerPort) {
            closePlayer();
        }

        return startPlayer(data, length);
    }

    /**
     * 开启播放库
     */
    private boolean startPlayer(byte[] data, int length) {
        if (null == data || 0 == length) {
            Log.e(TAG, "startPlayer(): Stream data error data is null or len is 0");
            return false;
        }

        if (null == mPlayerHandler) {
            Log.e(TAG, "startPlayer(): mPlayerHandler is null");
            return false;
        }

        mPlayerPort = mPlayerHandler.getPort();
        if (-1 == mPlayerPort) {
            Log.e(TAG, "startPlayer(): mPlayerPort is -1");
            return false;
        }

        boolean ret = mPlayerHandler.setStreamOpenMode(mPlayerPort, Player.STREAM_REALTIME);
        if (!ret) {
            int tempErrorCode = mPlayerHandler.getLastError(mPlayerPort);
            mPlayerHandler.freePort(mPlayerPort);
            mPlayerPort = -1;
            Log.e(TAG, "startPlayer():: Player setStreamOpenMode failed! errorCode is P" + tempErrorCode);
            return false;
        }

        ret = mPlayerHandler.openStream(mPlayerPort, data, length, 2 * 1024 * 1024);
        if (!ret) {
            Log.e(TAG, "startPlayer():: mPlayerHandle.openStream failed!" + "Port: " + mPlayerPort + "ErrorCode is P "
                    + mPlayerHandler.getLastError(mPlayerPort));
            return false;
        }

        ret = mPlayerHandler.setDisplayCB(mPlayerPort, this);
        if (!ret) {
            Log.e(TAG,
                    "startPlayer():: mPlayerHandle.setDisplayCB() failed errorCode is P"
                            + mPlayerHandler.getLastError(mPlayerPort));
            return false;
        }

        if (null == mSurfaceView) {
            Log.e(TAG, "startPlayer():: surfaceView is null");
            return false;
        }

        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (null == surfaceHolder) {
            Log.e(TAG, "startPlayer():: mPlayer mainSurface is null !");
            return false;
        }

        ret = mPlayerHandler.play(mPlayerPort, surfaceHolder);
        if (!ret) {
            Log.e(TAG, "startPlayer():: mPlayerHnadle.paly failed!" + "Port: " + mPlayerPort + "PlayView Surface: "
                    + surfaceHolder + "errorCode is P" + mPlayerHandler.getLastError(mPlayerPort));
            return false;
        }

        return true;
    }

    /**
     * 关闭播放库
     */
    private void closePlayer() {
        if (null != mPlayerHandler && -1 != mPlayerPort) {
            boolean ret = mPlayerHandler.stop(mPlayerPort);
            if (!ret) {
                Log.e(TAG, "closePlayer(): Player stop  failed!  errorCode is P" + mPlayerHandler.getLastError(mPlayerPort));
            }

            ret = mPlayerHandler.closeStream(mPlayerPort);
            if (!ret) {
                Log.e(TAG, "closePlayer(): Player closeStream failed!");
            }

            ret = mPlayerHandler.freePort(mPlayerPort);
            if (!ret) {
                Log.e(TAG, "closePlayer(): Player freePort  failed!");
            }

            mPlayerPort = -1;
        }
    }

    /**
     * @param handle 引擎id
     * @param opt    回调消息，包括：RTSPCLIENT_MSG_PLAYBACK_FINISH,RTSPCLIENT_MSG_BUFFER_OVERFLOW ,RTSPCLIENT_MSG_CONNECTION_EXCEPTION 三种
     * @param param1 保留参数
     * @param param2 保留参数
     * @param useId  用户数据，默认就是引擎id,与handle相同
     */
    @Override
    public void onMessageCallBack(int handle, int opt, int param1, int param2, int useId) {
        if (opt == RtspClient.RTSPCLIENT_MSG_CONNECTION_EXCEPTION) {
            stop();
            Log.e(TAG, "onMessageCallBack():: rtsp connection exception");
            if (connectNum > 3) {
                Log.e(TAG, "onMessageCallBack():: rtsp connection more than three times");
                connectNum = 0;
            } else {
                startLive(mSurfaceView);
                connectNum++;
            }
        }
    }

    /**
     * 停止预览
     */
    public void stop() {
        if (LIVE_INIT == mLiveState) {
            return;
        }

        stopRtsp();
        closePlayer();
        if (null != mLiveCallBack) {
            mLiveCallBack.onMessageCallback(ConstantLiveSDK.STOP_SUCCESS);
        }

        mLiveState = LIVE_INIT;
    }

    /**
     * 停止RTSP
     */
    private void stopRtsp() {
        if (null != mRtspHandler) {
            if (RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID != mRtspEngineIndex) {
                mRtspHandler.stopRtspProc(mRtspEngineIndex);
                mRtspHandler.releaseRtspClientEngineer(mRtspEngineIndex);
                mRtspEngineIndex = RtspClient.RTSPCLIENT_INVALIDATE_ENGINEID;
            }
        }
    }

    @Override
    public void onDisplay(int i, byte[] bytes, int i1, int i2, int i3, int i4, int i5, int i6) {
        if (LIVE_PLAY != mLiveState) {
            mLiveState = LIVE_PLAY;
            if (null != mLiveCallBack) {
                mLiveCallBack.onMessageCallback(ConstantLiveSDK.PLAY_DISPLAY_SUCCESS);
            } else {
                Log.e(TAG, "onDisplay():: liveCallBack is null");
            }
        }
    }


    public interface LiveCallBack {

        /**
         * message callback of play engine
         */
        void onMessageCallback(int message);
    }
}
