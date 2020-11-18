package com.givon.plugin;

import android.app.Application;
import android.content.Context;

/**
 * @author guzhu muyi126@163.com
 * @ProjectName: VirtualXposed
 * @Package: com.givon.plugin
 * @ClassName: StubApp
 * @Date 2019/2/28
 * @Version: 1.0
 */
public class StubApp {
    public static Context getOrigApplicationContext(Context context) {
        return context;
    }

    public static void interface11(int aa) {

    }

}
