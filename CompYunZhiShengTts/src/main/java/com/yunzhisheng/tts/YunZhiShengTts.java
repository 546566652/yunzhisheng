package com.yunzhisheng.tts;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;
import com.yunzhisheng.tts.util.AssetUtils;

import java.io.File;

public class YunZhiShengTts {

    private static YunZhiShengTts instance;
    private static Context mContext;
    private SpeechSynthesizer mTTSPlayer;
    private final String mFrontendModel = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "viroyal/frontend_model";
    private final String mBackendModel = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "viroyal/backend_lzl";

    public static YunZhiShengTts getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            synchronized (YunZhiShengTts.class) {
                if (instance == null) {
                    instance = new YunZhiShengTts();
                }
            }
        }
        return instance;
    }

    public void copyFile() {
        AssetUtils.getInstance(mContext).copyAssetsToSD("OfflineTTSModels", "viroyal").setFileOperateCallback(new AssetUtils.FileOperateCallback() {
            @Override
            public void onSuccess() {
                Log.e("TTSOfflineActivity", "onSuccess: ------------------");
                // 初始化本地TTS播报
                initTts();
            }

            @Override
            public void onFailed(String error) {
                Log.e("TTSOfflineActivity", "onerror: ------------------" + error);
            }
        });
    }

    /**
     * 初始化本地离线TTS
     */
    private void initTts() {

        // 初始化语音合成对象
        mTTSPlayer = new SpeechSynthesizer(mContext, Config.appKey, Config.secret);
        // 设置本地合成
        mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
        File _FrontendModelFile = new File(mFrontendModel);
        if (!_FrontendModelFile.exists()) {
            Toast.makeText(mContext, "文件：" + mFrontendModel + "不存在，请将assets下相关文件拷贝到SD卡指定目录！", Toast.LENGTH_SHORT).show();
        }
        File _BackendModelFile = new File(mBackendModel);
        if (!_BackendModelFile.exists()) {
            Toast.makeText(mContext, "文件：" + mBackendModel + "不存在，请将assets下相关文件拷贝到SD卡指定目录！", Toast.LENGTH_SHORT).show();
        }
        // 设置前端模型
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, mFrontendModel);
        // 设置后端模型
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, mBackendModel);
        // 设置回调监听
        mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {

            @Override
            public void onEvent(int type) {
                switch (type) {
                    case SpeechConstants.TTS_EVENT_INIT:
                        // 初始化成功回调
                        log_i("onInitFinish");
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                        // 开始合成回调
                        log_i("beginSynthesizer");
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                        // 合成结束回调
                        log_i("endSynthesizer");
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                        // 开始缓存回调
                        log_i("beginBuffer");
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_READY:
                        // 缓存完毕回调
                        log_i("bufferReady");
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        // 开始播放回调
                        log_i("onPlayBegin");
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        // 播放完成回调
                        log_i("onPlayEnd");
                        break;
                    case SpeechConstants.TTS_EVENT_PAUSE:
                        // 暂停回调
                        log_i("pause");
                        break;
                    case SpeechConstants.TTS_EVENT_RESUME:
                        // 恢复回调
                        log_i("resume");
                        break;
                    case SpeechConstants.TTS_EVENT_STOP:
                        // 停止回调
                        log_i("stop");
                        break;
                    case SpeechConstants.TTS_EVENT_RELEASE:
                        // 释放资源回调
                        log_i("release");
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onError(int type, String errorMSG) {
                // 语音合成错误回调
                log_i("onError");
                Toast.makeText(mContext, errorMSG, Toast.LENGTH_SHORT).show();
            }
        });
        // 初始化合成引擎
        mTTSPlayer.init("");
    }

    private void log_i(String log) {
        Log.i("yunzhisheng", log);
    }

    public void onStop() {
        // 主动停止识别
        if (mTTSPlayer != null) {
            mTTSPlayer.stop();
        }
    }

    public void onDestroy() {
        // 主动释放离线引擎
        if (mTTSPlayer != null) {
            mTTSPlayer.release(SpeechConstants.TTS_RELEASE_ENGINE, null);
        }
    }

    public void TTSPlay(String content) {
        if (mTTSPlayer != null) {
            mTTSPlayer.stop();
            mTTSPlayer.playText(content);
        }
    }
}
