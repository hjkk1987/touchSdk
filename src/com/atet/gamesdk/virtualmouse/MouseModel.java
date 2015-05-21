package com.atet.gamesdk.virtualmouse;

import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhouwei on 2015/5/5.
 *
 * 处理模拟鼠标类
 */
public class MouseModel {

    private MouseInterface mouseInterface;

    private Timer timer;
    private MouseMoveTask timerTask;

    public MouseModel(MouseInterface mouseInterface) {
        this.mouseInterface = mouseInterface;
        timer = new Timer("mouse timer");
    }

    public boolean onKeyEvent(KeyEvent event) {

        if (KeyEvent.KEYCODE_BUTTON_L1 != event.getKeyCode()) {
            return false;
        }
        return mouseInterface.onKeyEvent(event);
    }

    public boolean onMotionEvent(MotionEvent event) {

        if (MotionEvent.ACTION_MOVE != event.getAction()) {
            return false;
        }

        float x = event.getAxisValue(MotionEvent.AXIS_Z) * 10;
        float y = event.getAxisValue(MotionEvent.AXIS_RZ) * 10;

        if (Math.abs(x) >= 3 || Math.abs(y) >= 3) {

            if (timerTask == null || !timerTask.isRun()) {

                // 插入执行的任务
                timerTask = new MouseMoveTask();
                timer.schedule(timerTask, 0, 25);
//                System.out.println(">>>>>>>> add task ..");
            }

            // 移动鼠标
            timerTask.move(x, y);
        } else {

            if (timerTask != null && timerTask.isRun()) {
                // 停止移动
                timerTask.stopMove();
            }
        }

        return true;
    }

    /**
     * 鼠标移动的定时任务
     */
    private class MouseMoveTask extends TimerTask {

        private boolean run = true;
        private boolean move = true;
        private float trimx, trimy;

        @Override
        public void run() {

//            System.out.println(">>>>>> run ");

            if (move) {
                // 修改鼠标的位置
                mouseInterface.onMove((int)trimx, (int)trimy);
            }
        }

        @Override
        public boolean cancel() {
            run = false;
            return super.cancel();
        }

        public boolean isRun() {
            return run;
        }

        public void move(float x, float y) {
            move = true;
            trimx = x;
            trimy = y;
        }

        public void stopMove() {
            move = false;
            cancel();
//            System.out.println("***********结束移动**********");
        }
    }
}
