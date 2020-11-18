package com.givon.plugin;

import java.io.File;
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
public class XposedInitDex implements IXposedHookLoadPackage {
    Class Dex;
    Method Dex_getBytes;
    Method getDex;

    static boolean isFirst = true;
    static int index = 0;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XC_LoadPackage.LoadPackageParam loadPackageParam2 = loadPackageParam;
        System.out.println("handleLoadPackage");
        XposedBridge.log("XXXXX handleLoadPackage");
        initRefect();
        if (loadPackageParam2.packageName.equals("com.qihoo.cloudisk")) {
            StringBuffer stringBuffer2 = new StringBuffer();
            ClassLoader classLoader = loadPackageParam2.classLoader;
            XposedHelpers.findAndHookMethod("java.lang.ClassLoader",
                    classLoader, "loadClass", String.class, boolean.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            MethodHookParam methodHookParam2 = param;
                            XposedBridge.log("XXXXX loadClass afterHooked");
                            Object obj = methodHookParam2.args[1];
                            Class cls = (Class) methodHookParam2.getResult();
                            if (cls != null) {
                                try {
                                    String name = cls.getName();
                                    XposedBridge.log("XXXXX loadClass:" + name);
                                    ClassLoader classLoader = Class.forName("com.givon.plugin.XposedInitDex").getClassLoader();
                                    Class cls2 = Class.forName(name, false, ClassLoader.getSystemClassLoader());
                                    if (isFirst) {
                                        isFirst = false;
                                        Class cls3 = Class.forName("com.qihoo360.crypt.entryRunApplication", false, ClassLoader.getSystemClassLoader());
                                    }
                                } catch (ClassNotFoundException e2) {
                                    XposedBridge.log("XXXXX ClassNotFoundException  " + e2.getMessage());
                                    obj = null;
                                }
                                if (obj == null) {
                                    try {
                                        byte[] bArr = (byte[]) Dex_getBytes.invoke(getDex.invoke(cls, new Object[0]), new Object[0]);
                                        if (bArr != null) {
                                            File file = new File("/sdcard/z/", bArr.length + ".dex");
                                            if (file.exists()) {
//                                                file = new File("/sdcard/z/", bArr.length + "_" + index + ".dex");
//                                                index++;
                                            }else {
                                                FIO.writeByte(bArr, file.getAbsolutePath());
                                            }

                                            XposedBridge.log("XXXXX file:" + file.getAbsolutePath());
                                        }
                                    } catch (Exception e3) {
                                        XposedBridge.log("XXXXX Exception  " + e3.toString());
                                    }

                                }else {
                                    writeDex("",obj);
                                }
                            }

                        }
                    });

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

    public void initRefect() {
        try {
            this.Dex = Class.forName("com.android.dex.Dex");
            Dex_getBytes = Dex.getDeclaredMethod("getBytes", new Class[0]);
            getDex = Class.forName("java.lang.Class").getDeclaredMethod("getDex", new Class[0]);
        } catch (Throwable e) {
            XposedBridge.log("XXXXX " + e.getMessage());
        }
    }

    void writeDex(String str, Object obj) {
        String str2 = str;
        try {
            byte[] bArr = (byte[]) Dex_getBytes.invoke(this.getDex.invoke(obj.getClass(), new Object[0]), new Object[0]);
            if (bArr != null) {
                File file = new File("/sdcard/z/", bArr.length + ".dex");
                if (!file.exists()) {
                    FIO.writeByte(bArr, file.getAbsolutePath());
                }
            }
        } catch (InvocationTargetException e) {
            XposedBridge.log("XXXXX " + e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}