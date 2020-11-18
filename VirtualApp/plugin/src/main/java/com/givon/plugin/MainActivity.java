package com.givon.plugin;

import android.app.Activity;
import android.os.Bundle;

import de.robv.android.xposed.XposedBridge;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        XposedBridge.log("XXXXX onCreate");
    }
}
