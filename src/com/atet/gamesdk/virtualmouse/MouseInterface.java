package com.atet.gamesdk.virtualmouse;

import android.view.KeyEvent;

/**
 * Created by zhouwei on 2015/5/6.
 */
public interface MouseInterface {

    /**
     * 移动鼠标
     * @param trimx
     * @param trimy
     */
    void onMove(int trimx, int trimy);

    /**
     * 处理按钮事件
     * @param event
     * @return
     */
    boolean onKeyEvent(KeyEvent event);
}
