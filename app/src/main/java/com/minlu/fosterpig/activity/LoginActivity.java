package com.minlu.fosterpig.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.fragment.NetworkConfigFragment;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.sqlite.MySQLiteOpenHelper;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by pxj on 2016/11/22.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private EditText mEtUser;
    private EditText mEtPassWord;

    private CheckBox mRbRemember;
    private boolean mIsAuto;

    private String mUser;
    private String mPassWord;

    private String mHistoryPassWord;
    private String mHistoryUser;

    private Button mBLogin;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase writableDatabase;
    private String loginResult;
    private String loginResultMessage;
    private Button mBNetworkConfiguration;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_button:
                login();
                break;
            case R.id.bt_network_configuration_button:
                NetworkConfigFragment networkConfigFragment = new NetworkConfigFragment();
                networkConfigFragment.show(getSupportFragmentManager(), "networkConfigFragment");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 判断是否第一次进入login页,是就存储初始化的Ip,不是就跳过
        if (-1 == SharedPreferencesUtil.getint(ViewsUitls.getContext(), StringsFiled.FIRST_IN_LOGIN, -1)) {
            SharedPreferencesUtil.saveint(ViewsUitls.getContext(), StringsFiled.FIRST_IN_LOGIN, 1);
            SharedPreferencesUtil.saveStirng(getApplicationContext(), StringsFiled.IP_ADDRESS_PREFIX, "http://www.jsmjzl.com");
            System.out.println("第一次进入登录页面");
        } else {
            System.out.println("不是第一次进入登录页面");
        }

        //创建数据库操作对象
        mySQLiteOpenHelper = new MySQLiteOpenHelper(ViewsUitls.getContext());
        writableDatabase = mySQLiteOpenHelper.getWritableDatabase();

        getData();// 获取上次登录成功后的历史数据

        initView();

        if (mIsAuto) {
            login();
        }

    }

    private void getData() {

        mIsAuto = SharedPreferencesUtil.getboolean(ViewsUitls.getContext(),
                StringsFiled.IS_AUTO_LOGIN, false);
        // mHistoryPassward = SharedPreferencesUtil.getString(
        // ViewsUitls.getContext(), "mPassWord", "");
        /*
         * 参数1:表名 参数2:要查询的字段 参数3:where表达式 参数4:替换?号的真实值 参数5:分组 null
		 * 参数6:having表达式null 参数7:排序规则 c_age desc
		 */
        Cursor cursor = writableDatabase.query("t_user",
                new String[]{"c_password"}, "c_pw>?", new String[]{"0"},
                null, null, null);
        while (cursor.moveToNext()) {
            mHistoryPassWord = cursor.getString(0);
        }
        cursor.close();

        mHistoryUser = SharedPreferencesUtil.getString(ViewsUitls.getContext(),
                StringsFiled.LOGIN_USER, "");

    }

    private void initView() {
        mEtPassWord = (EditText) findViewById(R.id.login_password);
        mEtUser = (EditText) findViewById(R.id.login_user);
        mRbRemember = (CheckBox) findViewById(R.id.cb_login_remember_password);
        mRbRemember.setChecked(mIsAuto);
        mBLogin = (Button) findViewById(R.id.bt_login_button);
        mBLogin.setOnClickListener(this);
        mBNetworkConfiguration = (Button) findViewById(R.id.bt_network_configuration_button);
        mBNetworkConfiguration.setOnClickListener(this);

        // 根据历史记录来设置显示
        if (!mHistoryUser.isEmpty() && !mHistoryPassWord.isEmpty()) {
            mEtUser.setText(mHistoryUser);
            mEtPassWord.setText(mHistoryPassWord);
        }

        if (mEtUser != null && mBLogin != null) {
            ViewTreeObserver viewTreeObserver = mEtUser.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mEtUser.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    ViewGroup.LayoutParams layoutParams = mBLogin.getLayoutParams();
                    layoutParams.height = mEtUser.getHeight();
                    mBLogin.setLayoutParams(layoutParams);
                }
            });
        }

        if (mEtUser != null && mBNetworkConfiguration != null) {
            ViewTreeObserver viewTreeObserver = mEtUser.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mEtUser.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    ViewGroup.LayoutParams layoutParams = mBNetworkConfiguration.getLayoutParams();
                    layoutParams.height = mEtUser.getHeight();
                    mBNetworkConfiguration.setLayoutParams(layoutParams);
                }
            });
        }
    }


    private void login() {
        mUser = mEtUser.getText().toString().trim();
        mPassWord = mEtPassWord.getText().toString().trim();
//        cutOffShow("第二百零八", "第二百零九");
        if (!StringUtils.isEmpty(mUser) && !StringUtils.isEmpty(mPassWord)) {
            System.out.println("username:" + mUser + "password:" + mPassWord);
            requestIsLoginSuccess(mUser, mPassWord);
        } else {
            ToastUtil.showToast(this, "帐户密码不可为空");
        }
    }


    /*请求网络是否登录成功*/
    private void requestIsLoginSuccess(String userName, String passWord) {
        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("username", userName)
                .add("password", passWord).build();

        String ipAddress = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        Request request = new Request.Builder()
                .url(ipAddress + IpFiled.LOGIN)
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("=========================onFailure=============================");
                ViewsUitls.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LoginActivity.this, "网络异常,请查看网络是否配置正确");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    loginResult = response.body().string().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(loginResult + "      +++++++++++++++++++++++++++++++++");
                ViewsUitls.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.interentIsNormal(loginResult)) {
                            try {
                                JSONObject jsonObject = new JSONObject(loginResult);
                                if (jsonObject.has("resStr")) {
                                    loginResultMessage = jsonObject.optString("resStr");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (loginResult.contains("true")) {
                                saveSuccessPassWardUserName();
                                Intent mainActivity = new Intent(ViewsUitls.getContext(), MainActivity.class);
                                mainActivity.putExtra(StringsFiled.ACTIVITY_TITLE, "康乐畜牧养猪场");
                                startActivity(mainActivity);
                                ToastUtil.showToast(LoginActivity.this, "登录成功");
                                finish();
                            } else {
                                ToastUtil.showToast(LoginActivity.this, loginResultMessage);
                            }
                        } else {
                            ToastUtil.showToast(LoginActivity.this, "服务器异常,请稍候");
                        }
                    }
                });
            }
        });


    }


    /*当登录成功后需要将帐号密码进行保存*/
    private void saveSuccessPassWardUserName() {
        if (StringUtils.isEmpty(mHistoryPassWord)) {// 当数据库中没有保存过密码时需要第一次插入密码数据
            ContentValues values = new ContentValues();
            values.put("c_password", mPassWord);
            values.put("c_pw", 1);
            writableDatabase.insert("t_user", null, values);
        } else {// 修改数据
            if (!mHistoryPassWord.equals(mPassWord)) {// EditText中的密码与历史密码不一样
                ContentValues values = new ContentValues();
                values.put("c_password", mPassWord);
                writableDatabase.update("t_user", values, "c_pw>?",
                        new String[]{"0"});
            }
        }
        writableDatabase.close();
        mySQLiteOpenHelper.close();

        if (!mHistoryUser.equals(mUser)) {// EditText中的帐号与历史帐号不一样
            SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(),
                    StringsFiled.LOGIN_USER, mUser);
        }
        SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(),
                StringsFiled.IS_AUTO_LOGIN, mRbRemember.isChecked());
    }


    private boolean isCutOffShow = false;
    private int anInt = 0;

    private void cutOffShow(String start, String end) {
        try {
            InputStream is = getAssets().open("android_http.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains(start)) {
                    isCutOffShow = true;
                } else if (str.contains(end)) {
                    isCutOffShow = false;
                }
                if (isCutOffShow) {
                    str = str.trim();
                    Log.d("cutOffShow", "GC_CONCURRENT freed 1841K, 10% free 18569K/20551K, paused 17ms+0ms, total " + anInt + "ms");
                    Log.v("cutOffShow", str);
                    anInt++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
