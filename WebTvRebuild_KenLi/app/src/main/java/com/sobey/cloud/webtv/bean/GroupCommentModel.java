package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupCommentModel implements Parcelable, SobeyType {

	public String commentId;
	public GroupUserModel commentUser;
	// public List<String> commentPicUrls;
	public String[] commentPicUrls;
	public String commentContent;
	public String commentUserId;
	public String commentUserName;
	public List<GroupCommentReplyModel> commentReplyList;
	public String commentPostedTime;
	public String CommentUserHeadUrl;
	public String commentReplyContent;
	public int type = 1;// 默认为1
	public int floor;
	public int commentLikeCount;
	public int commentIsLiked;
//	public int is_follow;

	public GroupCommentModel() {

	}

	public GroupCommentModel(Parcel in) {
		commentId = in.readString();
		commentUser = in.readParcelable(GroupUserModel.class.getClassLoader());

		in.readStringArray(commentPicUrls);

		commentContent = in.readString();

		commentReplyList = new ArrayList<GroupCommentReplyModel>();
		in.readList(commentReplyList, GroupCommentReplyModel.class.getClassLoader());

		commentPostedTime = in.readString();
		commentUserId = in.readString();
		commentUserName = in.readString();
		CommentUserHeadUrl = in.readString();
		commentReplyContent = in.readString();

		type = in.readInt();
		floor = in.readInt();
		commentLikeCount = in.readInt();
		commentIsLiked = in.readInt();
//		is_follow = in.readInt();
	}

	public static final Parcelable.Creator<GroupCommentModel> CREATOR = new Parcelable.Creator<GroupCommentModel>() {
		public GroupCommentModel createFromParcel(Parcel in) {
			return new GroupCommentModel(in);
		}

		@Override
		public GroupCommentModel[] newArray(int size) {
			return new GroupCommentModel[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(commentId);
		dest.writeParcelable(commentUser, 0);
		dest.writeStringArray(commentPicUrls);
		dest.writeString(commentContent);
		dest.writeList(commentReplyList);
		dest.writeString(commentPostedTime);
		dest.writeString(commentUserId);
		dest.writeString(commentUserName);
		dest.writeString(CommentUserHeadUrl);
		dest.writeString(commentReplyContent);
		dest.writeInt(type);
		dest.writeInt(floor);
		dest.writeInt(commentLikeCount);
		dest.writeInt(commentIsLiked);
//		dest.writeInt(is_follow);
	}

}
