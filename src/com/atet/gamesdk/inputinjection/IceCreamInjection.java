package com.atet.gamesdk.inputinjection;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import android.view.IWindowManager;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.atet.gamesdk.utils.Glog;
import com.atet.gamesdk.utils.ReflectUtils;

import java.lang.reflect.Method;

/**
 * Created by zhouwei on 2015/4/28.
 *
 * 4.0系统的事件注入类(只能处理4.0版本系统的事件注入)
 */
public class IceCreamInjection extends Injection {

    private Object inputManager;
    private Method injectInputEventNoWaitMethod;
    private Method injectKeyEventMethod;
    private Method injectPointerEventMethod;
    private Method injectTrackballEventMethod;

    public IceCreamInjection(Context context) {
        super(context);
    }

    @Override
    void initInjection(Context context) {

        IBinder iBinder = ServiceManager.getService(Context.WINDOW_SERVICE);
        inputManager = IWindowManager.Stub.asInterface(iBinder);

        if (inputManager != null) {

            Class classz = inputManager.getClass();

            injectInputEventNoWaitMethod = ReflectUtils.findMethod(
                    classz,"injectInputEventNoWait", InputEvent.class);

            injectKeyEventMethod = ReflectUtils.findMethod(
                    classz, "injectKeyEvent", new Class[]{KeyEvent.class, boolean.class});

            injectPointerEventMethod = ReflectUtils.findMethod(
                    classz, "injectPointerEvent", new Class[]{MotionEvent.class, boolean.class});

            injectTrackballEventMethod = ReflectUtils.findMethod(
                    classz, "injectTrackballEvent", new Class[]{MotionEvent.class, boolean.class});
        }
    }

    @Override
    public boolean injectEvent(InputEvent ie, int mode) {
        // 不支持mode设置
        return injectInputEventNoWait(ie);
    }

    @Override
    public boolean injectInputEventNoWait(InputEvent ie) {
        return invokeInjectEvent(  injectInputEventNoWaitMethod, inputManager, new Object[]{ie});
    }

    @Override
    public boolean injectKeyEvent(KeyEvent event, boolean sync) {
        return invokeInjectEvent(injectKeyEventMethod, inputManager, new Object[]{event, sync});
    }

    @Override
    public boolean injectPointerEvent(MotionEvent event, boolean sync) {
        return invokeInjectEvent(injectPointerEventMethod, inputManager, new Object[]{event, sync});
    }

    @Override
    public boolean injectTrackballEvent(MotionEvent event, boolean sync) {
        return invokeInjectEvent(injectTrackballEventMethod, inputManager, new Object[]{event, sync});
    }
}
