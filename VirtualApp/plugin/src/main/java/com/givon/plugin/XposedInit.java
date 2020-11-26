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
    public static String TAG = "XXVW";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XC_LoadPackage.LoadPackageParam loadPackageParam2 = loadPackageParam;
        System.out.println("handleLoadPackage");
        XposedBridge.log("XXXXX handleLoadPackage:" + loadPackageParam2.packageName);
        if (loadPackageParam2.packageName.equals("com.tencent.mm")) {
//            checkClassAllArg("com.tencent.mm.R", loadPackageParam2.classLoader);
//            checkClassAllArg("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI", loadPackageParam2.classLoader);
            dumpDex(loadPackageParam2.classLoader);

        }
    }


    public void dumpDex(ClassLoader classLoader) {
        try {
            this.Dex = Class.forName("com.android.dex.Dex");
            Dex_getBytes = Dex.getDeclaredMethod("getBytes", new Class[0]);
            getDex = Class.forName("java.lang.Class").getDeclaredMethod("getDex", new Class[0]);
        } catch (Throwable e) {
            llog("DumpDex", e.getMessage());
            return;
        }
        XposedHelpers.findAndHookMethod("java.lang.ClassLoader",
                classLoader, "loadClass", String.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        MethodHookParam methodHookParam2 = param;
                        Object obj = methodHookParam2.args[1];
                        Class cls = (Class) methodHookParam2.getResult();
                        if (cls != null) {
                            try {
                                String name = cls.getName();
                                llog("DumpDex", "loadClass:" + name);
                                ClassLoader classLoader = Class.forName("com.givon.plugin.XposedInitDex").getClassLoader();
                                Class cls2 = Class.forName(name, false, ClassLoader.getSystemClassLoader());
                                if (isFirst) {
                                    Class cls3 = Class.forName("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI", false, ClassLoader.getSystemClassLoader());
                                    isFirst = false;
                                }
                            } catch (ClassNotFoundException e2) {
                                llog("DumpDex", "ClassNotFoundException  " + e2.getMessage());
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
                                        } else {
                                            FIO.writeByte(bArr, file.getAbsolutePath());
                                        }

                                        llog("DumpDex", "file:" + file.getAbsolutePath());
                                    }
                                } catch (Exception e3) {
                                    llog("DumpDex", "Exception  " + e3.toString());
                                }

                            } else {
                                writeDex("", obj);
                            }
                        }

                    }
                });
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
            llog("DumpDex", e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public void checkClassAllArg(String className, ClassLoader classLoader) {
        llog(TAG, "checkClassAllArg =====" + className + "=====");
        checkClassMethod(className, classLoader);
        checkClassFiled(className, classLoader);
        llog(TAG, "checkClassAllArg =====" + className + "===== END");
    }

    public void checkClassMethod(String className, ClassLoader classLoader) {
        Class<?> classIfExists = XposedHelpers.findClassIfExists(className, classLoader);
        if (classIfExists != null) {
            Method[] declaredMethods = classIfExists.getDeclaredMethods();

            llog(TAG, "declaredMethods len:" + declaredMethods.length);
            if (declaredMethods != null) {
                for (Method m : declaredMethods) {
                    m.setAccessible(true);
                    llog(TAG, "checkClassMethod Method:" + m.getName());
                }
            } else {
                llog(TAG, "checkClassMethod declaredMethods==null");
            }
        } else {
            llog(TAG, "checkClassMethod classIfExists==null");
        }
    }


    public void checkClassFiled(String className, ClassLoader classLoader) {
        Class<?> classIfExists = XposedHelpers.findClassIfExists(className, classLoader);
        if (classIfExists != null) {
            Field[] declaredFields = classIfExists.getDeclaredFields();

            llog(TAG, "declaredFields len:" + declaredFields.length);
            if (declaredFields != null) {
                for (Field f : declaredFields) {
                    f.setAccessible(true);
                    llog(TAG, "checkClassFiled Field:" + f.getName());
                }
            } else {
                llog(TAG, "checkClassFiled declaredFields==null");
            }
        } else {
            llog(TAG, "checkClassFiled classIfExists==null");
        }
    }

    public void llog(String TAG, String msg) {
        XposedBridge.log("[" + TAG + "] " + msg);
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