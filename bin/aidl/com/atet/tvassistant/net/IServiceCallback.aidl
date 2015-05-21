package com.atet.tvassistant.net;

/**
 * a callback interface used by dvbService to send
 * synchronous notifications back to its clients.  Note that this is a
 * one-way interface so the server does not block waiting for the client.
 */
oneway interface IServiceCallback {
	
	/*
	 * handler common message from service
	 */
    void handlerCommEvent(String user,int msgID, int param);
    
    /*
	 * handler search message from service
	 */
    void handlerSearchEvent(String user,String msg);
}