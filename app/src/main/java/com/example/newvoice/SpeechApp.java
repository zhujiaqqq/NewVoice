package com.example.newvoice;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * @author jiazhu
 */
public class SpeechApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        StringBuilder param = new StringBuilder();
        param.append("appid=" + getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(SpeechApp.this, param.toString());
        super.onCreate();
    }
}
