package com.sobey.cloud.webtv.bean;



import android.os.Parcel;
import android.os.Parcelable;

public class MessageModel implements Parcelable ,SobeyType{

	public String msgId;
	public String msgPublishUserId;
	public String msgPublishUserHeadUrl;
	public String msgPublishUserName;
	public String msgPublishTime;
	public String msgContent;
	public int msgType;
	public String subjectId;
	public String subjectTitle;
//	public int msgCount;
	
	public MessageModel(){
		
	}
	
	public MessageModel(Parcel in){
		msgId = in.readString();
		msgPublishUserId = in.readString();
		msgPublishUserHeadUrl = in.readString();
		msgPublishUserName = in.readString();
		msgPublishTime = in.readString();
		msgContent = in.readString();
		msgType = in.readInt();
		subjectId = in.readString();
		subjectTitle = in.readString();
//		msgCount = in.readInt();
	}
	
	public static final Parcelable.Creator<MessageModel> CREATOR = new Parcelable.Creator<MessageModel>() {
		public MessageModel createFromParcel(Parcel in) {
			return new MessageModel(in);
		}

		@Override
		public MessageModel[] newArray(int size) {
			return new MessageModel[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(msgId);
		dest.writeString(msgPublishUserId);
		dest.writeString(msgPublishUserHeadUrl);
		dest.writeString(msgPublishUserName);
		dest.writeString(msgPublishTime);
		dest.writeString(msgContent);
		dest.writeInt(msgType);
		dest.writeString(subjectId);
		dest.writeString(subjectTitle);
//		dest.writeInt(msgCount);
	}

}
