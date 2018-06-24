package com.think.okhttp3;

import android.app.Application;

import com.example.okhttputils.OkHttpClientManager;

/**
 *应用程序的入口.
 *
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClientManager instance = OkHttpClientManager.getInstance(this);
    }
}
