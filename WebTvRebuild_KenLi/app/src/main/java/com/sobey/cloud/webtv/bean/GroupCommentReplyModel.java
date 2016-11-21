package com.sobey.cloud.webtv.bean;


import android.os.Parcel;
import android.os.Parcelable;

public class GroupCommentReplyModel implements Parcelable ,SobeyType{

	public String replyTime;
	public String replyUserId;
	public String replyUserName;
	public String replyContent;
	//回复给谁的
	public String replyTo;
	
	public GroupCommentReplyModel(){
		
	}
	
	public GroupCommentReplyModel(Parcel in){
		replyTime = in.readString();
		replyUserId = in.readString();
		replyUserName = in.readString();
		replyContent = in.readString();
		replyTo = in.readString();
	}
	
	public static final Parcelable.Creator<GroupCommentReplyModel> CREATOR = new Parcelable.Creator<GroupCommentReplyModel>() {
		public GroupCommentReplyModel createFromParcel(Parcel in) {
			return new GroupCommentReplyModel(in);
		}

		@Override
		public GroupCommentReplyModel[] newArray(int size) {
			return new GroupCommentReplyModel[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(replyTime);
		dest.writeString(replyUserId);
		dest.writeString(replyUserName);
		dest.writeString(replyContent);
		dest.writeString(replyTo);
	}

}
