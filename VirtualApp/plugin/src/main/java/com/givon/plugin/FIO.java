package com.givon.plugin;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author guzhu muyi126@163.com
 * @ProjectName: VirtualXposed
 * @Package: com.givon.plugin
 * @ClassName: FIO
 * @Date 2019/2/28
 * @Version: 1.0
 */
public class FIO {
    public static void writeByte(byte[] bArr, String str) {
        byte[] bArr2 = bArr;
        try {
            OutputStream outputStream2 = new FileOutputStream(str);;
            outputStream2.write(bArr2);
            outputStream2.close();
        } catch (Exception e) {
            Exception exception = e;
        }
    }
}
