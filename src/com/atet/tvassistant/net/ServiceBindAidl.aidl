package com.atet.tvassistant.net;

import com.atet.tvassistant.net.MessageRow;
import com.atet.tvassistant.net.IServiceCallback;

interface ServiceBindAidl {

	boolean isInited();
	
	void registerCallback(IServiceCallback cb);
    void unregisterCallback(IServiceCallback cb);
    
	MessageRow getMessageRow();
	void setMessageRow(in MessageRow messageRow);
}  