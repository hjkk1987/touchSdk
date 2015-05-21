package com.atet.gamesdk.inputintercept;

import android.content.Context;
import android.os.Build;
import android.view.WindowManager;
import com.atet.gamesdk.utils.Glog;
import com.atet.gamesdk.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by zhouwei on 2015/5/7.
 */
public class InterceptMonitor extends TimerTask {

    private final static String TAG = InterceptMonitor.class.getSimpleName();

    private Context context;
    private MonitorCallback monitorCallback;

    private Map<Object, String>history = new HashMap<Object, String>();
    private WindownView windownView;

    public InterceptMonitor(Context context) {
        this(context, null);
    }

    public InterceptMonitor(Context context, MonitorCallback monitorCallback) {
        this.context = context;
        this.monitorCallback = monitorCallback;

        initInterceptMonitor();
    }

    private void initInterceptMonitor() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // 小于4.0的不处理
            return ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // 小于4.2的系统
            windownView = new WindownViewV4_0(context);
            return ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // 小于4.4的系统
            windownView = new WindownViewV4_2(context);
            return ;
        }

        windownView = new WindownViewV4_4(context);
    }

    public MonitorCallback getMonitorCallback() {
        return monitorCallback;
    }

    public void setMonitorCallback(MonitorCallback monitorCallback) {
        this.monitorCallback = monitorCallback;
    }

    @Override
    public void run() {

        if (windownView == null) {
            System.out.println(">>>>>>> 系统版本不支持...");
            return ;
        }

        // 重新加载
        windownView.reloadViews();

        if (windownView.compareHistory(history)) {
//            System.out.println(">>>  跟上次的一样,没有进行修改");
            return ;
        }

        // 进行修改了
        history.clear();
        windownView.reloadViews();

        while (windownView.hasNext()) {

            // 处理拦截事件
            handlerIntercept(windownView.next());
        }
    }

    /**
     * 处理事件拦截的方法
     * @param viewRootImpl
     */
    private void handlerIntercept(Object viewRootImpl) {

        if (viewRootImpl == null) return ;

        try {
            Field mViewField = ReflectUtils.findField(viewRootImpl.getClass(), "mView");
            Object mView = ReflectUtils.getValueQuietly(mViewField, viewRootImpl);

            if (monitorCallback != null) {
                // 处理拦截..........
                monitorCallback.handlerIntercept(viewRootImpl, mView);
            }

            // 添加到历史记录中
            history.put(viewRootImpl, viewRootImpl.toString());
        } catch (Exception e) {
            Glog.e(TAG, "处理拦截异常!", e);
        }
    }

    /**
     * 4.4以上版本的窗口处理类
     */
    private class WindownViewV4_4 extends WindownView {

        Object mGlobal;
        Class mGlobalClass;

        private int index = 0;
        private ArrayList<Object> mRoots;

        public WindownViewV4_4(Context context) {
            super(context);

            Field mGlobalField = ReflectUtils.findField(windowManagerClass, "mGlobal");
            mGlobal = ReflectUtils.getValueQuietly(mGlobalField, windowManager);
            mGlobalClass = mGlobal.getClass();
        }

        @Override
        public void reloadViews() {

            index = 0;
            Field mRootsField = ReflectUtils.findField(mGlobalClass, "mRoots");
            mRoots = (ArrayList<Object>) ReflectUtils.getValueQuietly(mRootsField, mGlobal);
        }

        @Override
        public int size() {
            return mRoots == null ? 0 : mRoots.size();
        }

        @Override
        public boolean hasNext() {
            return index < size() ? true : false;
        }

        @Override
        public Object next() {
            return mRoots.get(index ++);
        }
    }

    /**
     * 4.2到4.4的窗口处理类
     */
    private class WindownViewV4_2 extends WindownView {

        Object mGlobal;
        Class mGlobalClass;

        private int index = 0;
        private Object[] mRoots;

        public WindownViewV4_2(Context context) {
            super(context);

            Field mGlobalField = ReflectUtils.findField(windowManagerClass, "mGlobal");
            mGlobal = ReflectUtils.getValueQuietly(mGlobalField, windowManager);
            mGlobalClass = mGlobal.getClass();
        }

        @Override
        public void reloadViews() {

            index = 0;
            Field mRootsField = ReflectUtils.findField(mGlobalClass, "mRoots");
            mRoots = (Object[]) ReflectUtils.getValueQuietly(mRootsField, mGlobal);
        }

        @Override
        public int size() {
            return mRoots == null ? 0 : mRoots.length;
        }

        @Override
        public boolean hasNext() {
            return index < size() ? true : false;
        }

        @Override
        public Object next() {
            return mRoots[index ++];
        }
    }

    /**
     * 4.0到4.2版本的窗口处理类
     */
    private class WindownViewV4_0 extends WindownView {

        private int index = 0;
        private Object[] mRoots;

        public WindownViewV4_0(Context context) {
            super(context);
        }

        @Override
        public void reloadViews() {

            index = 0;
            Field mRootsField = ReflectUtils.findField(windowManagerClass, "mRoots");
            mRoots = (Object[]) ReflectUtils.getValueQuietly(mRootsField, windowManager);
        }

        @Override
        public int size() {
            return mRoots == null ? 0 : mRoots.length;
        }

        @Override
        public boolean hasNext() {
            return index < size() ? true : false;
        }

        @Override
        public Object next() {
            return mRoots[index ++];
        }
    }


    /**
     * 窗口类(来用加载当前应用的显示窗口)
     */
    private abstract class WindownView implements Iterator<Object> {

        Context context;

        WindowManager windowManager;
        Class windowManagerClass;

        public WindownView(Context context) {
            this.context = context;

            // 获取窗口服务
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManagerClass = windowManager.getClass();
        }

        public abstract void reloadViews();

        public abstract int size();

        public boolean compareHistory(Map<Object, String> history) {

            if (history == null || size() != history.size()) {
                return false;
            }

            while (hasNext()) {

                if (!history.containsKey(next())) {

                    return false;
                }
            }

            return true;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * 监控回调的接口(用来处理注入拦截代码)
     */
    public interface MonitorCallback {

        void handlerIntercept(Object viewRootImpl, Object mView);
    }
}
