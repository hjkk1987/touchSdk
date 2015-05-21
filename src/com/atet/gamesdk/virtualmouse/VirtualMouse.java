package com.atet.gamesdk.virtualmouse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.*;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.atet.gamesdk.inputinjection.InjectionManager;
import com.atet.gamesdk.utils.DensityUtil;
import com.atet.gamesdk.utils.EventUtils;
import com.atet.gamesdk.utils.Glog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhouwei on 2015/5/5.
 */
public class VirtualMouse extends RelativeLayout implements MouseInterface {

	/**
	 * 鼠标在没有移动的时间最大停留的时间
	 */
	private final static int STAY_TIME = 8000;

	/** 显示鼠标 */
	public static final int SHOW_MOUSE = 0x0001;
	/** 隐藏鼠标 */
	public static final int HIDE_MOUSE = 0x0002;
	/** 移动鼠标 */
	public static final int MOVE_MOUSE = 0x0003;

	private ImageView virtualMouseImage;

	Rect frame = new Rect();

	boolean show = false;
	WindowManager windowManager;
	WindowManager.LayoutParams toyLayoutParams;

	private boolean down = false;
	private long downTime;

	Timer timer;
	MouseTimerTask mouseTimerTask;

	public VirtualMouse(Context context, Bitmap bitmap) {
		super(context);

		// 初始化...
		initVirtualMouse(bitmap);
	}

	public void onCreated() {

		if (timer == null) {
			timer = new Timer();
		}
	}

	public void onDestroy() {

		if (isShow())
			hideMouse();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * 初始化浮窗控件相关的信息
	 */
	private void initVirtualMouse(Bitmap bitmap) {

		windowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);

		// 获取当前屏幕状态信息
		getWindowVisibleDisplayFrame(frame);

		LayoutParams layoutParams = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		setLayoutParams(layoutParams);

		// 设置WindowManager.LayoutParams
		toyLayoutParams = new WindowManager.LayoutParams();

		// 设置window type
		toyLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		toyLayoutParams.format = PixelFormat.RGBA_8888;

		// 设置Window flag (可以移动到屏幕之外，不需要焦点与触屏事件)
		toyLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

		// 调整悬浮窗口至左上角
		toyLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

		// 设置悬浮窗口长宽数据
		toyLayoutParams.width = DensityUtil.dip2px(getContext(), 40);
		toyLayoutParams.height = DensityUtil.dip2px(getContext(), 40);

		// 以屏幕左上角为原点，设置x、y初始值
		toyLayoutParams.x = 0;
		toyLayoutParams.y = getStatusBarHeight();

		// 获取显示的布局文件对象
		virtualMouseImage = new ImageView(getContext());
		virtualMouseImage.setLayoutParams(new RelativeLayout.LayoutParams(
				GridLayout.LayoutParams.MATCH_PARENT,
				GridLayout.LayoutParams.MATCH_PARENT));
		if (bitmap == null) {
			virtualMouseImage.setImageBitmap(loadMouseBitmap());
		} else {
			virtualMouseImage.setImageBitmap(bitmap);
		}
		// 添加到
		addView(virtualMouseImage);
	}

	private Bitmap loadMouseBitmap() {

		InputStream in = null;

		try {
			in = getContext().getAssets().open("mouse.png");
			return BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Glog.e("加载鼠标图标异常!");
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	@Override
	public void onMove(int trimx, int trimy) {

		showMouse();

		// 移动浮窗
		Message.obtain(handler, MOVE_MOUSE, trimx, trimy).sendToTarget();

		// 如果按钮已经按下，需要注入move事件...
		// ************************ 这里还需要改进,因为坐标还存在偏差 *************************
		if (down) {
			// 注入移动的事件事件
			injectEvent(EventUtils.obtainTMoveMotionEvent(downTime,
					getPointX(), getPointY()));
		}

		activationMouseTimerTask();
	}

	@Override
	public boolean onKeyEvent(KeyEvent event) {

		if (!isShow())
			return false;

		activationMouseTimerTask();

		int action = event.getAction();
		int repeatCount = event.getRepeatCount();

		if (KeyEvent.ACTION_DOWN == action && repeatCount == 0) {

			down = true;

			// 模拟点击按下的事件
			MotionEvent motionEvent = EventUtils.obtainTDownMotionEvent(
					getPointX(), getPointY());
			downTime = motionEvent.getDownTime();

			// 注入事件
			injectEvent(motionEvent);
		} else if (KeyEvent.ACTION_UP == action && repeatCount == 0) {

			down = false;

			// 模拟点击释放的事件
			MotionEvent motionEvent = EventUtils.obtainTUpMotionEvent(downTime,
					getPointX(), getPointY());

			// 注入事件
			injectEvent(motionEvent);
		}

		return true;
	}

	private void showMouse() {

		if (!isShow()) {
			// 显示浮窗
			handler.sendEmptyMessage(SHOW_MOUSE);
		}
	}

	private void hideMouse() {

		if (isShow()) {
			// 隐藏鼠标
			handler.sendEmptyMessage(HIDE_MOUSE);
		}
	}

	/**
	 * 激动鼠标延时隐藏的定时任务
	 */
	private void activationMouseTimerTask() {

		if ((mouseTimerTask == null || !mouseTimerTask.isRun())
				&& timer != null) {

			mouseTimerTask = new MouseTimerTask();
			timer.schedule(mouseTimerTask, 0, 1000);
		}

		mouseTimerTask.resetLastTime();
	}

	private void injectEvent(MotionEvent event) {

		if (event == null)
			return;

		InjectionManager injectionManager = InjectionManager.getInstance();
		injectionManager.injectInputEventNoWait(event);

		event.recycle();
	}

	/**
	 * @return 返回鼠标是否显示
	 */
	public boolean isShow() {
		return show;
	}

	/**
	 * @return 获取状态栏高度
	 */
	public int getStatusBarHeight() {
		return frame.top;
	}

	/**
	 * @return 获取屏幕的宽
	 */
	public int getScreenWidth() {
		return frame.width();
	}

	/**
	 * @return 获取屏幕的高
	 */
	public int getScreenHeight() {
		return frame.height();
	}

	/**
	 * @return 获取鼠标的x坐标
	 */
	public int getPointX() {
		return toyLayoutParams.x;
	}

	/**
	 * @return 获取鼠标的y坐标
	 */
	public int getPointY() {
		return toyLayoutParams.y;
	}

	/**
	 * 处理消息的方法
	 * 
	 * @param msg
	 */
	private void onHandleMessage(Message msg) {

		int what = msg.what;

		if (what == SHOW_MOUSE) {

			if (!isShow()) {
				show = true;
				windowManager.addView(this, toyLayoutParams);
			}
		} else if (msg.what == HIDE_MOUSE) {

			if (isShow()) {
				show = false;
				windowManager.removeView(this);
			}
		} else if (msg.what == MOVE_MOUSE) {

			if (isShow()) {

				// 更新x，y坐标
				toyLayoutParams.x += msg.arg1;
				toyLayoutParams.y += msg.arg2;

				if (toyLayoutParams.x < 0) {
					toyLayoutParams.x = 0;
				}

				if (toyLayoutParams.x > getScreenWidth()) {
					toyLayoutParams.x = getScreenWidth();
				}

				if (toyLayoutParams.y < 0) {
					toyLayoutParams.y = 0;
				}

				if (toyLayoutParams.y > getScreenHeight()) {
					toyLayoutParams.y = getScreenHeight();
				}

				// 更新坐标位置
				windowManager.updateViewLayout(this, toyLayoutParams);
			}
		}
	}

	private final MouseHandler handler = new MouseHandler();

	private final class MouseHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// 处理消息
			onHandleMessage(msg);
		}
	}

	/**
	 * 鼠标延时隐藏的定时器
	 */
	private final class MouseTimerTask extends TimerTask {

		private boolean run = true;
		private long lastTime;

		public MouseTimerTask() {
			// 重置下时间
			resetLastTime();
		}

		@Override
		public void run() {

			if (System.currentTimeMillis() - lastTime > STAY_TIME) {

				// 鼠标的停留处理
				hideMouse();
				cancel();
			}
		}

		public boolean isRun() {
			return run;
		}

		@Override
		public boolean cancel() {
			run = false;
			return super.cancel();
		}

		public void resetLastTime() {
			lastTime = System.currentTimeMillis();
		}
	}
}
