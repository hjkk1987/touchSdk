package com.atet.gamesdk.utils;

import android.os.SystemClock;
import android.view.InputDevice;
import android.view.MotionEvent;

/**
 * Created by zhouwei on 2015/5/6.
 */
public class EventUtils {

    public static MotionEvent obtainTDownMotionEvent(float x, float y) {

        MotionEvent event = MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN,
                x,
                y,
                0
        );
        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);

        return event;
    }

    public static MotionEvent obtainTUpMotionEvent(long downTime, float x, float y) {

        MotionEvent event = MotionEvent.obtain(
                downTime,
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP,
                x,
                y,
                0
        );
        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);

        return event;
    }

    public static MotionEvent obtainTMoveMotionEvent(long downTime, float x, float y) {

        MotionEvent event = MotionEvent.obtain(
                downTime,
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_MOVE,
                x,
                y,
                0
        );
        event.setSource(InputDevice.SOURCE_TOUCHSCREEN);

        return event;
    }
}
