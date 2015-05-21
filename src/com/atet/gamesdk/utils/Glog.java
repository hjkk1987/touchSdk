package com.atet.gamesdk.utils;

import android.util.Log;

/**
 * Created by zhouwei on 2015/4/28.
 *
 * 日志输出类
 */
public class Glog {

    private final static boolean DEBUG = true;
    private final static String TAG = Glog.class.getSimpleName();

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
//        if (DEBUG) Log.d(tag, msg);
        if (DEBUG) System.out.println(">> " + tag + " >> " + msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
//        if (DEBUG) Log.d(tag, msg, tr);
        if (DEBUG) System.out.println(">> " + tag + " >> " + msg);
        tr.printStackTrace();
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
//        if (DEBUG) Log.e(tag, msg);
        d(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
//        if (DEBUG) Log.e(tag, msg, tr);
        d(tag, msg, tr);
    }
}
