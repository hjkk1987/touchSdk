package com.atet.tvassistant.sdk;

public interface OnRecCommandListener {
	public void onRecCommand(String user, String cmd);

	public void onRecCommand(String user, int type, float[] values);
}
