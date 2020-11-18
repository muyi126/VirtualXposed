package com.givon.plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author guzhu muyi126@163.com
 * @ProjectName: VirtualXposed
 * @Package: com.givon.plugin
 * @ClassName: XposedInit
 * @Date 2019/1/29
 * @Version: 1.0
 */
public class XposedInit implements IXposedHookLoadPackage {
    Class Dex;
    Method Dex_getBytes;
    Method getDex;

    static boolean isFirst = true;
    static int index = 0;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XC_LoadPackage.LoadPackageParam loadPackageParam2 = loadPackageParam;
        System.out.println("handleLoadPackage");
        XposedBridge.log("XXXXX handleLoadPackage:" + loadPackageParam2.packageName);
        if (loadPackageParam2.packageName.equals("com.tencent.mm")) {
            Class<?> classIfExists = XposedHelpers.findClassIfExists("com.tencent.mm.R", loadPackageParam2.classLoader);
            XposedBridge.log("XXXXX :" + classIfExists);
            if (classIfExists != null) {
                Field[] declaredFields = classIfExists.getDeclaredFields();
                XposedBridge.log("XXXXX :" + declaredFields.length);
                if (declaredFields != null) {
                    for (Field f : declaredFields) {
                        XposedBridge.log("REE :" + f.getName());
                    }
                }
            }

        }
    }


    public static void printfStack() {
        XposedBridge.log("printfStack");
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            StringBuilder builder = new StringBuilder("\n\n--------------------------");
            for (int i = 0; i < stackElements.length; i++) {
                builder.append("\nClass Name:" + stackElements[i].getClassName());
                builder.append("\nFile Name:" + stackElements[i].getFileName() + "(" + stackElements[i].getLineNumber() + ")");
                builder.append("\nMethod Name:" + stackElements[i].getMethodName());
                builder.append("\n>>>>");
            }
            builder.append("\n\n--------------------------");
            XposedBridge.log(builder.toString());
        }
    }


}