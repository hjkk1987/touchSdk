package com.atet.gamesdk.inputinjection;

import android.content.Context;
import android.os.Build;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.atet.gamesdk.utils.Glog;

/**
 * Created by zhouwei on 2015/4/28.
 *
 * 系统事件注入的管理类
 */
public class InjectionManager {

    private final static String TAG = InjectionManager.class.getSimpleName();

    private final static InjectionManager INJECTION_MANAGER = new InjectionManager();

    public static final int INJECT_INPUT_EVENT_MODE_ASYNC = 0;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT = 1;
    public static final int INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2;

    public static InjectionManager getInstance() {
        return INJECTION_MANAGER;
    }

    private Injection injection;

    /**
     * 事件注入初始化方法
     * @param context
     */
    public void initialize (Context context) {

        if (injection != null) return ;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Glog.d(TAG, "暂时不支持系统版本");
            return ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            /* 4.0系统的注入方式 */
            injection = new IceCreamInjection(context);
            return ;
        }

        /* 一般系统情况的注入方式 - 4.1版本以上的系统 */
        injection = new GeneralInjection(context);
    }

    /**
     * 事件注入的方法
     * @param ie 注入的事件
     * @param mode 注入的模式
     * <li>
     *             {@link #INJECT_INPUT_EVENT_MODE_ASYNC}<br/>
     *             {@link #INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT}<br/>
     *             {@link #INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH}<br/>
     * </li>
     */
    public boolean injectEvent(InputEvent ie, int mode) {
        return injection != null ? injection.injectEvent(ie, mode) : false;
    }

    /**
     * 事件注入方法(使用默认的状态,不进行等待)
     * @param ie 注入的事件{@link android.view.InputEvent}
     * @return 注入事件的返回结果
     */
    public boolean injectInputEventNoWait(InputEvent ie) {
        return injection != null ? injection.injectInputEventNoWait(ie) : false;
    }

    /**
     * @deprecated
     * 注入KeyEvent事件方法
     * @param event 注入的事件{@link android.view.KeyEvent}
     * @param sync 是否进行同步
     * @return 注入事件返回的结果
     */
    public boolean injectKeyEvent(KeyEvent event, boolean sync) {
        return injection != null ? injection.injectKeyEvent(event, sync) : false;
    }

    /**
     * @deprecated
     * 注入PointerEvent事件的方法{@link android.view.MotionEvent}
     * @param event 注入的事件
     * @param sync 是否进行同步
     * @return 注入事件返回的结果
     */
    public boolean injectPointerEvent(MotionEvent event, boolean sync) {
        return injection != null ? injection.injectPointerEvent(event, sync) : false;
    }

    /**
     * @deprecated
     * 注入TrackballEvent事件的方法{@link android.view.MotionEvent}
     * @param event 注入的事件
     * @param sync 是否进行同步
     * @return 注入事件返回的结果
     */
    public boolean injectTrackballEvent(MotionEvent event, boolean sync) {
        return injection != null ? injection.injectTrackballEvent(event, sync) : false;
    }
}
