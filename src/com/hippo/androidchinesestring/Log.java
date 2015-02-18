package com.hippo.androidchinesestring;

/**
 * Created by Hippo on 2015/2/18.
 */
public final class Log {

    public static void d(Object... arg) {
        for (Object obj : arg) {
            System.out.println(obj);
        }
    }

}
