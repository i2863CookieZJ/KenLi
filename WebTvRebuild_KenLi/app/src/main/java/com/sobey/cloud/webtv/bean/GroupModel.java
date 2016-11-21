package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupModel implements Parcelable, SobeyType {

	public String groupId;
	public String headUrl;
	public String groupName;
	public String groupInfo;
	public String totalSubjectCount;
	public int newSubjectCount;
	public int isFollowed;
	public String subjectCount;// 帖子数
	public List<GroupSubjectModel> topicSubjectList;
	public List<GroupSubjectModel> subjectList;

	public GroupModel() {

	}

	public GroupModel(Parcel in) {
		groupId = in.readString();
		headUrl = in.readString();
		groupName = in.readString();
		groupInfo = in.readString();
		newSubjectCount = in.readInt();
		isFollowed = in.readInt();
		subjectCount = in.readString();
		totalSubjectCount = in.readString();

		topicSubjectList = new ArrayList<GroupSubjectModel>();
		in.readList(topicSubjectList, GroupSubjectModel.class.getClassLoader());
		subjectList = new ArrayList<GroupSubjectModel>();
		in.readList(subjectList, GroupSubjectModel.class.getClassLoader());

	}

	public static final Parcelable.Creator<GroupModel> CREATOR = new Parcelable.Creator<GroupModel>() {
		public GroupModel createFromParcel(Parcel in) {
			return new GroupModel(in);
		}

		@Override
		public GroupModel[] newArray(int size) {
			return new GroupModel[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(groupId);
		dest.writeString(headUrl);
		dest.writeString(groupName);
		dest.writeString(groupInfo);
		dest.writeInt(newSubjectCount);
		dest.writeInt(isFollowed);
		dest.writeString(subjectCount);
		dest.writeString(totalSubjectCount);

		dest.writeList(topicSubjectList);
		dest.writeList(subjectList);
	}

}
