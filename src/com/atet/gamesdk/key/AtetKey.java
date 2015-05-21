package com.atet.gamesdk.key;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.atet.gamesdk.inputinjection.InjectionManager;
import com.atet.gamesdk.inputintercept.InterceptController;
import com.atet.gamesdk.utils.Glog;
import com.atet.gamesdk.virtualmouse.VirtualMouse;
import com.atet.tvassistant.sdk.AtetTvAssistSDK;
import com.atet.tvassistant.sdk.OnExecCommandListener;

/*
 * File：AtetKey.java
 *
 * Copyright (C) 2015 AtetGameSDK Project
 * Date：2015年5月15日 下午1:51:34
 * All Rights SXHL(New Space) Corporation Reserved.
 * http://www.at-et.cn
 *
 */

/**
 * @description: 声明ATET对象，用于获取模拟器传过来的键值信息
 * 
 * @author: HuJun
 * @date: 2015年5月15日 下午1:51:34
 */

public class AtetKey {
	private static AtetKey mInstance = null;
	private Context context = null;
	private OnKeyEventListener onKeyEventListener = null;
	private OnMotionEventListener onMotionEventListener = null;
	private OnGestureEventListener onGestureEventListener = null;
	private OnKeyBoardInputListener onKeyBoardInputListener = null;
	protected String Tag = AtetKey.class.getName();
	protected static boolean isVirtualShow = false;
	private VirtualMouse virtualMouse = null;
	private InterceptController interceptController = null;

	public AtetKey(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		// AtetTvAssistSDK.getInstance();
		AtetTvAssistSDK.getInstance().init(context);
		InjectionManager.getInstance().initialize(context);

	}

	/**
	 * @description: 鼠标对象是否显示
	 * 
	 * @param show
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月18日 下午6:17:53
	 */
	public void showMouse(boolean show) {
		this.isVirtualShow = show;
	}

	/**
	 * @description:
	 * 
	 * @return 获取ATET键值句柄
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午1:57:13
	 */
	public static AtetKey getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new AtetKey(context);
		}
		return mInstance;
	}

	/**
	 * @description: 注册按键监听，开始接收按键键值,并且注册虚拟鼠标
	 * 
	 * @throws:用户可以知道是什么按键，可以返回
	 * @author: HuJun
	 * @date: 2015年5月15日 下午2:11:24
	 */
	public void register() {
		Log.e(Tag, "注册按键信息，开始接收");
		virtualMouse = new VirtualMouse(context, null);
		virtualMouse.onCreated();

		interceptController = new InterceptController(context, virtualMouse);
		interceptController.onCreated();
		AtetTvAssistSDK.getInstance().setOnExecCommandListener(
				new OnExecCommandListener() {

					@Override
					public void onExecCommand(String user, String camMsg) {
						// TODO Auto-generated method stub
						Log.d(Tag, "接收到消息:" + camMsg);
						ParseKey(camMsg);
					}
				});

	}

	/**
	 * @description:
	 * 
	 * @param cmdMsg
	 *            解析消息数据
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午3:28:17
	 */
	private void ParseKey(String cmdMsg) {

		if (cmdMsg.startsWith("mouse,9,")) {
			String[] values = cmdMsg.split(",");
			float moveX = Float.parseFloat(values[2]);
			float moveY = Float.parseFloat(values[3]);
			Glog.d("x:" + moveX + "  y:" + moveY);
			if (onMotionEventListener != null) {
				onMotionEventListener.onTouchMove((int) moveX, (int) moveY);
			}
			if (virtualMouse != null && isVirtualShow) {
				// Glog.d("virtualMouse move x y~");
				virtualMouse.onMove((int) moveX, (int) moveY);
			}
			// touchMove((int) moveX, (int) moveY);
		} else if (cmdMsg.startsWith("mouse")) {
			if (cmdMsg.equals("mouse,left")) {
				Glog.d("Mouse Left Key Click");
				KeyEvent keyEvent = getKey(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_ENTER);
				if (onKeyEventListener != null) {

					onKeyEventListener.onKeyClick(keyEvent);
				}
				keyInject(keyEvent);
			} else if (cmdMsg.equals("mouse,right")) {
				Glog.d("Mouse Right Key Click");
				KeyEvent keyEvent = getKey(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_BACK);
				if (onKeyEventListener != null) {
					onKeyEventListener.onKeyClick(keyEvent);
				}
				keyInject(keyEvent);
			} else {
				Glog.d("Mouse Open");
			}

		} else if (cmdMsg.startsWith("keyboard,")) {// 键盘输入内容
			int length = "keyboard,".length();
			if (!cmdMsg.equals("keyboard,")) {
				String content = cmdMsg.substring(length, cmdMsg.length());
				Glog.d("keyboard input data:" + content);
				if (onKeyBoardInputListener != null) {
					onKeyBoardInputListener.onKeyBoardInput(content);
				}
			}

		} else if (cmdMsg.startsWith("open")) {// 进入和退出滑鼠模式
			if (cmdMsg.equals("open,mouse")) {

			} else if (cmdMsg.equals("open,other")) {

			}
		} else if (cmdMsg.startsWith("guesture,")) {
			String[] values = cmdMsg.split(",");
			int keyValue = Integer.parseInt(values[1]);
			KeyEvent keyEvent = getKey(KeyEvent.ACTION_DOWN, keyValue);
			if (onKeyEventListener != null) {
				onKeyEventListener.onKeyClick(keyEvent);
			}
			keyInject(keyEvent);
		} else {// 普通键值
			int keyValue = Integer.parseInt(cmdMsg);
			Glog.d("key:" + keyValue + " is pressed!");
			KeyEvent keyEvent = getKey(KeyEvent.ACTION_DOWN, keyValue);
			if (onKeyEventListener != null) {
				onKeyEventListener.onKeyClick(keyEvent);
			}
			keyInject(keyEvent);
		}

	}

	/**
	 * @description: 注册注销所有的虚拟鼠标，注入系统
	 * 
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午2:12:14
	 */
	public void unregister() {
		AtetTvAssistSDK.getInstance().destroy();
		if (virtualMouse != null)
			virtualMouse.onDestroy();
		if (interceptController != null)
			interceptController.onDestroy();
	}

	/**
	 * @description:
	 * 
	 * @param action
	 * @param keyValue
	 * @return 获取当前的按键信息
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午2:38:10
	 */
	private KeyEvent getKey(int action, int keyValue) {
		KeyEvent keyEvent = new KeyEvent(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), action, keyValue, 0, 0);
		return keyEvent;
	}

	/**
	 * @description:
	 * 
	 * @param key
	 * @return 处理按键信息
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午2:43:32
	 */
	private boolean keyInject(KeyEvent key) {
		boolean result = InjectionManager.getInstance().injectInputEventNoWait(
				key);
		return result;
	}

	/**
	 * @description:
	 * 
	 * @param x
	 * @param y
	 *            鼠标移动的时候处理移动信息
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午2:56:05
	 */
	private void touchMove(int x, int y) {
		MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);

		me.setSource(InputDevice.SOURCE_TOUCHSCREEN);

		InjectionManager.getInstance().injectEvent(me,
				InjectionManager.INJECT_INPUT_EVENT_MODE_ASYNC);

		me.recycle();
	}

	/**
	 * @description: 用于监听数据的按键信息。
	 * 
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午2:58:22
	 */
	public void setOnKeyEventListener(OnKeyEventListener onKeyEventListener) {
		this.onKeyEventListener = onKeyEventListener;
	}

	/**
	 * @description:
	 * 
	 * @param onMotionEventListener
	 *            监听手势信息
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午4:11:27
	 */
	public void setOnMotionEventListener(
			OnMotionEventListener onMotionEventListener) {
		this.onMotionEventListener = onMotionEventListener;
	}

	/**
	 * @description:
	 * 
	 * @param onGestureEventListener
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午4:13:42
	 */
	public void setOnGestureEventListener(
			OnGestureEventListener onGestureEventListener) {
		this.onGestureEventListener = onGestureEventListener;
	}

	/**
	 * @description:
	 * 
	 * @param onKeyBoardInputListener
	 *            键盘输入事件监听
	 * @throws:
	 * @author: HuJun
	 * @date: 2015年5月15日 下午4:15:46
	 */
	public void setOnKeyBoardInputListener(
			OnKeyBoardInputListener onKeyBoardInputListener) {
		this.onKeyBoardInputListener = onKeyBoardInputListener;
	}

	/**
	 * @description: 定义事件监听，包括按键信息，TOUCH信息和手势信息
	 * 
	 * @author: HuJun
	 * @date: 2015年5月15日 下午3:00:07
	 */
	public static abstract interface OnKeyEventListener {
		public void onKeyClick(KeyEvent keyEvent);//
	}

	/**
	 * @description: 鼠标模式监听
	 * 
	 * @author: HuJun
	 * @date: 2015年5月15日 下午3:56:13
	 */
	public static abstract interface OnMotionEventListener {
		public void onTouchMove(int x, int y);// 移动
	}

	/**
	 * @description: 手势模式监听
	 * 
	 * @author: HuJun
	 * @date: 2015年5月15日 下午3:57:19
	 */
	public static abstract interface OnGestureEventListener {
		public void onGustureAction(int action);
	}

	/**
	 * @description: 键盘输入监听
	 * 
	 * @author: HuJun
	 * @date: 2015年5月15日 下午4:01:50
	 */
	public static abstract interface OnKeyBoardInputListener {
		public void onKeyBoardInput(String strContent);

	}

}
