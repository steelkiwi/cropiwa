package com.steelkiwi.cropiwa.sample;

import android.app.Application;

/**
 * @author yarolegovich https://github.com/yarolegovich
 * 25.02.2017.
 */

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
