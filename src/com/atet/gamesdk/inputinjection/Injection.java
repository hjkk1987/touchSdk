package com.atet.gamesdk.inputinjection;

import android.content.Context;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.atet.gamesdk.utils.ReflectUtils;

import java.lang.reflect.Method;

/**
 * Created by zhouwei on 2015/4/28.
 *
 * 事件注入的抽像类
 */
public abstract class Injection {

    final static String TAG = Injection.class.getSimpleName();

    private Context context;

    public Injection(Context context) {
        this.context = context;

        initInjection(context);
    }

    Context getContext() {
        return context;
    }

    /**
     * 初始化方法,会回调这个方法
     */
    abstract void initInjection(Context context);

    /**
     * 事件注入方法
     * @param ie 注入的事件{@link android.view.InputEvent}
     * @param mode 注入模式
     * @return 注入事件的返回结果
     */
    public abstract boolean injectEvent(InputEvent ie, int mode);

    /**
     * 事件注入方法(使用默认的状态,不进行等待)
     * @param ie 注入的事件{@link android.view.InputEvent}
     * @return 注入事件的返回结果
     */
    public abstract boolean injectInputEventNoWait(InputEvent ie);

    /**
     * @deprecated
     * 注入KeyEvent事件方法
     * @param event 注入的事件{@link android.view.KeyEvent}
     * @param sync 是否进行同步
     * @return 注入事件返回的结果
     */
    public abstract boolean injectKeyEvent(KeyEvent event, boolean sync);

    /**
     * @deprecated
     * 注入PointerEvent事件的方法{@link android.view.MotionEvent}
     * @param event 注入的事件
     * @param sync 是否进行同步
     * @return 注入事件返回的结果
     */
    public abstract boolean injectPointerEvent(MotionEvent event, boolean sync);

    /**
     * @deprecated
     * 注入TrackballEvent事件的方法{@link android.view.MotionEvent}
     * @param event 注入的事件
     * @param sync 是否进行同步
     * @return 注入事件返回的结果
     */
    public abstract boolean injectTrackballEvent(MotionEvent event, boolean sync);


    /**
     * 反射调用事件注入的方法,并返回处理后的结果
     * @param method 注入的方法
     * @param receiver 注入的对象
     * @param args 参数
     * @return 返回注入的结果
     */
    boolean invokeInjectEvent(Method method, Object receiver, Object[] args) {

        if (method == null
                || receiver == null
                || args == null) {
            return false;
        }

        Boolean result = (Boolean) ReflectUtils.invokeQuietly(method, receiver, args);

        return result != null ? result.booleanValue() : false;
    }
}
