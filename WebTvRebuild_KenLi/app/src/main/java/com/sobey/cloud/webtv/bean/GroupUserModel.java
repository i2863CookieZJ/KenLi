package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupUserModel implements Parcelable, SobeyType {

	public String userName;
	public String uid;
	public String userHeadUrl;
	public List<GroupSubjectModel> postedSubjectList;
	public int isFriend;

	public GroupUserModel() {

	}

	public GroupUserModel(Parcel in) {
		userName = in.readString();
		uid = in.readString();
		userHeadUrl = in.readString();
		postedSubjectList = new ArrayList<GroupSubjectModel>();
		in.readList(postedSubjectList, GroupSubjectModel.class.getClassLoader());

		isFriend = in.readInt();
	}

	public static final Parcelable.Creator<GroupUserModel> CREATOR = new Parcelable.Creator<GroupUserModel>() {
		public GroupUserModel createFromParcel(Parcel in) {
			return new GroupUserModel(in);
		}

		@Override
		public GroupUserModel[] newArray(int size) {
			return new GroupUserModel[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userName);
		dest.writeString(uid);
		dest.writeString(userHeadUrl);
		dest.writeList(postedSubjectList);
		dest.writeInt(isFriend);
	}

}
