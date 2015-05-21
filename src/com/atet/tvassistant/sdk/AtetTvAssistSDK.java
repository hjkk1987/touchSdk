package com.atet.tvassistant.sdk;

import com.atet.tvassistant.net.IServiceCallback;
import com.atet.tvassistant.net.ServiceBindAidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class AtetTvAssistSDK {
	private static AtetTvAssistSDK instance;
	private Context context;
	private ServiceBindAidl mService;
	private ServiceConnection mConnection;
	protected OnExecCommandListener onExecCommandListener;
	private OnRecCommandListener onRecCommandListener;

	private AtetTvAssistSDK() {
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle b = new Bundle();
			String str, user;
			b = msg.getData();
			str = b.getString("MSGTIP");
			user = b.getString("userInfo");
			onExecCommandListener.onExecCommand(user, str);
		}
	};

	public static AtetTvAssistSDK getInstance() {

		if (instance == null) {
			instance = new AtetTvAssistSDK();
		}

		return instance;
	}

	public void setOnExecCommandListener(
			OnExecCommandListener onExecCommandListener) {
		this.onExecCommandListener = onExecCommandListener;
	}

	public OnRecCommandListener getOnRecCommandListener() {
		return onRecCommandListener;
	}

	public void setOnRecCommandListener(
			OnRecCommandListener onRecCommandListener) {
		this.onRecCommandListener = onRecCommandListener;
	}

	/**
	 * 
	 * 
	 * @param context
	 * 
	 */

	public void init(Context context) {
		this.context = context;
		initConnection();
		startService();
		setOnRecCommandListener(new OnRecCommandListener() {

			@Override
			public void onRecCommand(String user, String cmd) {
				onExecCommandListener.onExecCommand(user, cmd);
			}

			@Override
			public void onRecCommand(String user, int type, float[] values) {
				onExecCommandListener.onExecCommand(user, type + ","
						+ values[0]);
			}
		});

	}

	/**
	 * ֱ�����
	 */
	public void destroy() {
		exitService();
	}

	/**
	 * �󶨷���
	 */
	private void startService() {
		Intent intent = new Intent("com.atet.tvassistant.net.ConnectionService");
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * ����
	 */
	private void exitService() {
		try {
			if (mService != null) {
				mService.unregisterCallback(mCallback);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mConnection != null) {
			context.unbindService(mConnection);
			mConnection = null;
			context = null;
		}
	}

	// ����service
	private void initConnection() {
		mConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				mService = ServiceBindAidl.Stub.asInterface(service);
				try {
					if (mService.isInited()) {
						mService.registerCallback(mCallback); // ע����Ϣ�ص�
					} else {
						exitService(); // ��ʼ��ʧ�� �˳�
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				mService = null;
			}

		};
	}

	private IServiceCallback mCallback = new IServiceCallback.Stub() {

		@Override
		public void handlerCommEvent(String user, int msgID, int param)
				throws RemoteException {
			Log.i("life", "client-msgID:" + msgID + ",param:" + param);

			Message msg = new Message();

			msg.what = msgID;
			msg.arg1 = param;

			if (mHandler != null)
				mHandler.sendMessage(msg);
		}

		// 由这边接收消息
		@Override
		public void handlerSearchEvent(String user, String msg)
				throws RemoteException {
			Message mesg = new Message();
			Bundle b = new Bundle();
			b.putString("MSGTIP", msg);
			b.putString("userInfo", user);
			mesg.setData(b);
			if (mHandler != null)
				mHandler.sendMessage(mesg);
		}

	};
}
