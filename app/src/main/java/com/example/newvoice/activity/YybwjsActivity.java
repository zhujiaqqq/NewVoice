package com.example.newvoice.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newvoice.DipperCom;
import com.example.newvoice.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

public class YybwjsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "YybwjsActivity";
    public static int first_in = 0;
    private SpeechSynthesizer mTts;
    /**
     * 默认本地发音人
     */
    public static String voicerLocal = "xiaoyan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yybwjs);

        String str = new String();

        //DipperCom.receive_message = str.toCharArray();


        TextView text_ReceiveIDnumber = (TextView) findViewById(R.id.text_ReceiveIDnumber);
        TextView text_ReceiveContent = (TextView) findViewById(R.id.text_ReceiveContent);

        Button button_Fhsyj = (Button) findViewById(R.id.button_ReceiveFhsyj);
        button_Fhsyj.setOnClickListener(this);

        //1.创建SpeechSynthesizer对象
        mTts = SpeechSynthesizer.createSynthesizer(YybwjsActivity.this, new InitListener() {
            @Override
            public void onInit(int i) {
                Log.i(TAG, "onInit: code: " + i);
                if (i != ErrorCode.SUCCESS) {
                    Log.i(TAG, "onInit: error: " + i);
                }
            }
        });
        //clear params
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //2.合成参数设置
        //设置引擎类型为本地
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置本地发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);

        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        //加载本地合成资源，resPath为本地合成资源路径
        //mTts.setParameter(ResourceUtil.TTS_RES_PATH, resPath);
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");

        if (first_in == 0) {
            first_in = 1;
            finish();
        }

        //将接收到的信息显示出来
        if (DipperCom.receive_id != 0) {
            str = "接收到语音信息,";
            str = str + "发信人:" + DipperCom.receive_id;
            text_ReceiveIDnumber.setText(String.format("%06d", DipperCom.receive_id));
        }
        if (DipperCom.receive_message_len != 0) {
            text_ReceiveContent.setText(DipperCom.receive_message, 0, DipperCom.receive_message_len);
            str = str + ",通信内容:" + text_ReceiveContent.getText().toString();

            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putString("No" + DipperCom.S_SAVE_NUM, String.format("%04d", (DipperCom.S_SAVE_NUM + 1)) + "     接收     " + DipperCom.receive_id + "      " + text_ReceiveContent.getText().toString());
            DipperCom.S_SAVE_NUM++;
            editor.putInt("S_SAVE_NUM", DipperCom.S_SAVE_NUM);
            editor.apply();
            //3.开始合成
            mTts.startSpeaking(str, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ReceiveFhsyj:
                DipperCom.receive_id = 0;
                DipperCom.receive_message_len = 0;
                finish();

//                mTts.startSpeaking("今天星期天", null);
                break;
            default:
                break;
        }
    }


    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voicerLocal + ".jet"));
        return tempBuffer.toString();
    }
}
