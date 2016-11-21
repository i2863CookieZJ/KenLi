package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GetUserMessageListResult implements Parcelable ,SobeyType{

	public List<MessageModel> msgList;
	public int msgCount;
	
	
	public GetUserMessageListResult() {
	}

	public GetUserMessageListResult(Parcel in) {
		msgList = new ArrayList<MessageModel>();
		in.readList( msgList, MessageModel.class.getClassLoader() );
		
		msgCount = in.readInt();
	}

	public static final Parcelable.Creator<GetUserMessageListResult> CREATOR = new Parcelable.Creator<GetUserMessageListResult>() {
		public GetUserMessageListResult createFromParcel(Parcel in) {
			return new GetUserMessageListResult(in);
		}

		@Override
		public GetUserMessageListResult[] newArray(int size) {
			return new GetUserMessageListResult[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(msgList);
		dest.writeInt(msgCount);
	}

}
