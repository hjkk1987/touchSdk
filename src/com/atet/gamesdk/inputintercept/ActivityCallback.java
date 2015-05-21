package com.atet.gamesdk.inputintercept;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.atet.gamesdk.utils.Glog;
import com.atet.gamesdk.utils.ReflectUtils;

import java.lang.reflect.Field;

/**
 * Created by zhouwei on 2015/4/28.
 */
public class ActivityCallback implements Application.ActivityLifecycleCallbacks {

    public final static String TAG = ActivityCallback.class.getSimpleName();

    private Context context;
    private InputIntercept inputIntercept;

    public ActivityCallback(Context context) {

        this.context = context;
        inputIntercept = new InputIntercept(context, null);
    }

    public void onCreated() {
    }

    public void onDestroy() {
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        System.out.println(">>> onActivityCreated " + activity);

        // 添加拦截器
        addIntercept(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        System.out.println(">>> onActivityDestroyed " + activity);

        // 移除拦截器
        removeIntercept(activity);
    }

    private void addIntercept(Activity activity) {

        if (activity == null) return ;

        Glog.d(TAG, "添加拦截器");

        try {
            Window window = activity.getWindow();
            Field callbackField = ReflectUtils.findField(window.getClass().getSuperclass(), "mCallback");
            Object object = callbackField.get(window);

            if (object instanceof Activity) {

                WindowCallback windowCallback = new WindowCallback(activity, inputIntercept);
                callbackField.set(window, windowCallback);
            }
        } catch (Exception e) {
            Glog.e(TAG, "添加拦截器失败", e);
        }
    }

    private void removeIntercept(Activity activity) {

        if (activity == null) return ;

        Glog.d(TAG, "移除拦截器");

        try {
            Window window = activity.getWindow();
            Field callbackField = ReflectUtils.findField(window.getClass().getSuperclass(), "mCallback");
            Object object = callbackField.get(window);

            if (object instanceof WindowCallback) {

                callbackField.set(window, activity);
            }
        } catch (Exception e) {
            Glog.e(TAG, "移除拦截器失败", e);
        }
    }
}
