package com.atet.tvassistant.net;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageRow implements Parcelable {
	private final static String TAG = "PTP_MSG";

	public String mSender;
	public String mMsg;
	public String mTime;
	public int keyType;
	public int keyValue1;
	public int keyValue2;

	public static final String mDel = "^&^";

	private MessageRow() {
		this.mSender = null;
		this.mTime = null;
		this.mMsg = null;
	}

	public MessageRow(String sender, String msg, String time) {
		mTime = time;
		if (time == null) {
			Date now = new Date();
			// SimpleDateFormat timingFormat = new
			// SimpleDateFormat("mm/dd hh:mm");
			// mTime = new SimpleDateFormat("dd/MM HH:mm").format(now);
			mTime = new SimpleDateFormat("h:mm a").format(now);
		}
		mSender = sender;
		mMsg = msg;
	}

	public MessageRow(Parcel in) {
		readFromParcel(in);
	}

	public String toString() {
		return mSender + mDel + mMsg + mDel + mTime;
	}

	public static final Parcelable.Creator<MessageRow> CREATOR = new Parcelable.Creator<MessageRow>() {
		public MessageRow createFromParcel(Parcel in) {
			return new MessageRow(in);
		}

		public MessageRow[] newArray(int size) {
			return new MessageRow[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mSender);
		dest.writeString(mMsg);
		dest.writeString(mTime);
	}

	public void readFromParcel(Parcel in) {
		mSender = in.readString();
		mMsg = in.readString();
		mTime = in.readString();
	}
}
