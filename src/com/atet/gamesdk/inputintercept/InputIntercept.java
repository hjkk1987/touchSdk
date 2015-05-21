package com.atet.gamesdk.inputintercept;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import com.atet.gamesdk.utils.Glog;
import com.atet.gamesdk.utils.ReflectUtils;
import com.atet.gamesdk.virtualmouse.MouseInterface;
import com.atet.gamesdk.virtualmouse.MouseModel;
import com.atet.gamesdk.virtualmouse.VirtualMouse;

import java.lang.reflect.Field;

/**
 * Created by zhouwei on 2015/4/28.
 */
public class InputIntercept {

	public final static String TAG = InputIntercept.class.getSimpleName();

	private Context context;
	private MouseModel mouseModel;

	public InputIntercept(Context context, MouseModel mouseModel) {
		this.context = context;
		this.mouseModel = mouseModel;
	}

	public boolean dispatchKeyEvent(KeyEvent event) {

		System.out.println(">> filter keyEvent >> " + event);

		// if (KeyEvent.KEYCODE_ENTER == event.getKeyCode()) {
		// setKeyCode(event, KeyEvent.KEYCODE_BACK);
		// } else if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
		// setKeyCode(event, KeyEvent.KEYCODE_ENTER);
		// }

		if (mouseModel.onKeyEvent(event)) {
			// 模拟触屏处理
			return true;
		}

		return false;
	}

	private void setKeyCode(KeyEvent event, int keyCode) {

		try {
			Field field = ReflectUtils.findField(KeyEvent.class, "mKeyCode");
			field.set(event, keyCode);
		} catch (Exception e) {
			Glog.e(TAG, "设置KeyCode异常", e);
		}
	}

	public boolean dispatchGenericMotionEvent(MotionEvent event) {

		System.out.println(">> filter genericMotionEvent >> " + event);

		// int action = event.getAction();

		if (mouseModel.onMotionEvent(event)) {
			// 鼠标事件处理
			return true;
		}

		return false;
	}

	public boolean dispatchKeyShortcutEvent(KeyEvent event) {

		System.out.println(">> filter keyShortcutEvent >> " + event);

		return false;
	}

	public boolean dispatchTouchEvent(MotionEvent event) {

		System.out.println(">> filter touchEvent >> " + event);

		return false;
	}

	public boolean dispatchTrackballEvent(MotionEvent event) {

		System.out.println(">> filter trackballEvent >> " + event);

		return false;
	}

	public Context getContext() {
		return context;
	}
}
