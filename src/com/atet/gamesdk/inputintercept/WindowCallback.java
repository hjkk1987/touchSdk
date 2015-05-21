package com.atet.gamesdk.inputintercept;

import android.app.Activity;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by zhouwei on 2015/4/28.
 */
public class WindowCallback implements Window.Callback {

    private Window.Callback callback;
    private InputIntercept inputIntercept;

    public WindowCallback(Window.Callback callback, InputIntercept inputIntercept) {
        this.callback = callback;
        this.inputIntercept = inputIntercept;
    }

    public Window.Callback getCallback() {
        return callback;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        // 需要进行事件拦截
        if (inputIntercept.dispatchKeyEvent(event)) {
            return true;
        }

        return callback.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {

        // 需要进行事件拦截
        if (inputIntercept.dispatchKeyShortcutEvent(event)) {
            return true;
        }

        return callback.dispatchKeyShortcutEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        // 需要进行事件拦截
        if (inputIntercept.dispatchTouchEvent(event)) {
            return true;
        }

        return callback.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {

        // 需要进行事件拦截
        if (inputIntercept.dispatchTrackballEvent(event)) {
            return true;
        }

        return callback.dispatchTrackballEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {

        // 需要进行事件拦截
        if (inputIntercept.dispatchGenericMotionEvent(event)) {
            return true;
        }

        return callback.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return callback.dispatchPopulateAccessibilityEvent(event);
    }

    @Override
    public View onCreatePanelView(int featureId) {
        return callback.onCreatePanelView(featureId);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return callback.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return callback.onPreparePanel(featureId, view, menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return callback.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return callback.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        callback.onWindowAttributesChanged(attrs);
    }

    @Override
    public void onContentChanged() {
        callback.onContentChanged();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        callback.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onAttachedToWindow() {
        callback.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        callback.onDetachedFromWindow();
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        callback.onPanelClosed(featureId, menu);
    }

    @Override
    public boolean onSearchRequested() {
        return callback.onSearchRequested();
    }

    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback actionModeCallback) {
        return callback.onWindowStartingActionMode(actionModeCallback);
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        callback.onActionModeStarted(mode);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        callback.onActionModeFinished(mode);
    }
}
