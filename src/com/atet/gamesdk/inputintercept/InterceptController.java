package com.atet.gamesdk.inputintercept;

import android.content.Context;
import android.view.Window;
import com.android.internal.view.RootViewSurfaceTaker;
import com.atet.gamesdk.utils.ReflectUtils;
import com.atet.gamesdk.virtualmouse.MouseInterface;
import com.atet.gamesdk.virtualmouse.MouseModel;
import com.atet.gamesdk.virtualmouse.VirtualMouse;

import java.lang.reflect.Field;
import java.util.Timer;

/**
 * Created by zhouwei on 2015/5/7.
 *
 * 事件拦截的控制类
 */
public class InterceptController implements InterceptMonitor.MonitorCallback {


    private Context context;
    private MouseModel mouseModel;

    private Timer timer;
    private InterceptMonitor interceptMonitor;

    private InputIntercept inputIntercept;

    public InterceptController(Context context, MouseInterface mouseInterface) {
        this.context = context;
        this.mouseModel = new MouseModel(mouseInterface);
    }

    public void onCreated() {

        timer = new Timer("game sdk timer");

        inputIntercept = new InputIntercept(getContext(), mouseModel);

        interceptMonitor = new InterceptMonitor(getContext(), this);
        timer.schedule(interceptMonitor, 0, 1000);
    }

    public void onDestroy() {

        if (timer != null) timer.cancel();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void handlerIntercept(Object viewRootImpl, Object mView) {

        if (viewRootImpl == null || mView == null) return ;

//        System.out.println(">>>>>>>>>> " + mView);

        if (mView instanceof RootViewSurfaceTaker) {

            Field phoneWindowField = ReflectUtils.findField(mView.getClass(), "this$0");
            Object phoneWindow = ReflectUtils.getValueQuietly(phoneWindowField, mView);
            Field mCallbackField = ReflectUtils.findField(phoneWindow.getClass().getSuperclass(), "mCallback");
            Object mCallback = ReflectUtils.getValueQuietly(mCallbackField, phoneWindow);

            if (mCallback instanceof WindowCallback) {
                // 已经加了拦截,不需要处理了
                return ;
            }

            WindowCallback windowCallback = new WindowCallback((Window.Callback) mCallback, inputIntercept);
            ReflectUtils.setValueQuietly(mCallbackField, phoneWindow, windowCallback);
        } else {

            // 通过 cglib看能解决当前的问题不
            System.out.println("############### 未处理到: " + mView);
        }
    }
}
