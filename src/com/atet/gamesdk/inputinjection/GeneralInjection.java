package com.atet.gamesdk.inputinjection;

import android.content.Context;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.atet.gamesdk.utils.Glog;
import com.atet.gamesdk.utils.ReflectUtils;

import java.lang.reflect.Method;

/**
 * Created by zhouwei on 2015/4/28.<br/>
 *
 * 注入事件方法
 * {@link #injectEvent(android.view.InputEvent, int)}<br/>
 *
 * 系统事件注入类,支持4.0以上的系统<br/>
 * 注: 事件注入需要添加 android.permission.INJECT_EVENTS 权限<br/>
 */
public class GeneralInjection extends Injection {

    private Object inputManager;
    private Method injectInputEventMethod;

    public GeneralInjection(Context context) {
        super(context);
    }

    @Override
    void initInjection(Context context) {

        // 获取系统输出管理类
        inputManager = context.getSystemService(Context.INPUT_SERVICE);

        if (inputManager != null) {

            injectInputEventMethod = ReflectUtils.findMethod(inputManager.getClass(),
                    "injectInputEvent", new Class[]{InputEvent.class, int.class});
        }
    }

    @Override
    public boolean injectEvent(InputEvent ie, int mode) {
        return invokeInjectEvent(injectInputEventMethod, inputManager, new Object[]{ie, mode});
    }

    @Override
    public boolean injectInputEventNoWait(InputEvent ie) {
        return injectEvent(ie, InjectionManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    @Override
    public boolean injectKeyEvent(KeyEvent event, boolean sync) {
        return injectEvent(event, sync ?
                InjectionManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH :
                InjectionManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT);
    }

    @Override
    public boolean injectPointerEvent(MotionEvent event, boolean sync) {
        return injectEvent(event, sync ?
                InjectionManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH :
                InjectionManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT);
    }

    @Override
    public boolean injectTrackballEvent(MotionEvent event, boolean sync) {
        throw new UnsupportedOperationException();
    }
}
