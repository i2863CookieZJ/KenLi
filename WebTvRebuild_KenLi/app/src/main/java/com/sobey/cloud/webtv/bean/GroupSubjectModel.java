package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupSubjectModel implements Parcelable, SobeyType {

	public String subjectTitle;
	public String subjectId;
	public String subjectContent;
	public String[] subjectPicUrls;
	public String publishTime;
	public String publishUserId;
	public String publishUserHeadUrl;
	public String publishUserName;
	public String subjectLikeCount;
	public String subjectReplyCount;
	public String groupId;
	public String groupName;
	public String groupHeadUrl;
	public String groupInfo;
//	public String likedUserCount;
	public int digest;

	public int isLiked;
	public int isCollected;
	public String subjectUrl;
	// 用于标志是否是置顶的帖子
	public int type = 1;
	public int commentCount;
//	public int is_follow;
	public List<GroupUserModel> likedUserList;
	public List<GroupCommentModel> commentList;

	public GroupSubjectModel() {

	}

	public GroupSubjectModel(Parcel in) {
		subjectTitle = in.readString();
		subjectId = in.readString();
		subjectContent = in.readString();

		int length = in.readInt();
		if (length > 0) {
			subjectPicUrls = new String[length];
			in.readStringArray(subjectPicUrls);
		}

		publishTime = in.readString();
		publishUserId = in.readString();
		publishUserHeadUrl = in.readString();
		publishUserName = in.readString();
		subjectLikeCount = in.readString();
		subjectReplyCount = in.readString();
		type = in.readInt();

		likedUserList = new ArrayList<GroupUserModel>();
		in.readList(likedUserList, GroupUserModel.class.getClassLoader());

		commentList = new ArrayList<GroupCommentModel>();
		in.readList(commentList, GroupCommentModel.class.getClassLoader());

		groupId = in.readString();
		groupName = in.readString();
		groupHeadUrl = in.readString();
		groupInfo = in.readString();

		isLiked = in.readInt();
		isCollected = in.readInt();
		subjectUrl = in.readString();
		commentCount = in.readInt();
//		is_follow = in.readInt();
		digest= in.readInt();
	}

	public static final Parcelable.Creator<GroupSubjectModel> CREATOR = new Parcelable.Creator<GroupSubjectModel>() {
		public GroupSubjectModel createFromParcel(Parcel in) {
			return new GroupSubjectModel(in);
		}

		@Override
		public GroupSubjectModel[] newArray(int size) {
			return new GroupSubjectModel[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(subjectTitle);
		dest.writeString(subjectId);
		dest.writeString(subjectContent);
//		dest.writeString(likedUserCount);
		if (subjectPicUrls == null) {
			dest.writeInt(0);
		} else {
			dest.writeInt(subjectPicUrls.length);
		}
		if (subjectPicUrls != null) {
			dest.writeStringArray(subjectPicUrls);
		}

		dest.writeString(publishTime);
		dest.writeString(publishUserId);
		dest.writeString(publishUserHeadUrl);
		dest.writeString(publishUserName);
		dest.writeString(subjectLikeCount);
		dest.writeString(subjectReplyCount);
		dest.writeInt(type);

		dest.writeList(likedUserList);
		dest.writeList(commentList);
		dest.writeString(groupId);
		dest.writeString(groupName);
		dest.writeString(groupHeadUrl);
		dest.writeString(groupInfo);

		dest.writeInt(isLiked);
		dest.writeInt(isCollected);
		dest.writeString(subjectUrl);
		dest.writeInt(commentCount);
//		dest.writeInt(is_follow);
		dest.writeInt(digest);
	}

}
