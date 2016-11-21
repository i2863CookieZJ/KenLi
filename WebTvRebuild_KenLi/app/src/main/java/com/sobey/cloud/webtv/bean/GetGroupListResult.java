package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GetGroupListResult implements Parcelable ,SobeyType{

	public List<GroupModel> followedGroupList;
	public List<GroupModel> otherGroupList;
	
	
	public GetGroupListResult() {
	}

	public GetGroupListResult(Parcel in) {
		followedGroupList = new ArrayList<GroupModel>();
		otherGroupList = new ArrayList<GroupModel>();
		in.readList( followedGroupList, GroupModel.class.getClassLoader() );
		in.readList( otherGroupList, GroupModel.class.getClassLoader() );
	}

	public static final Parcelable.Creator<GetGroupListResult> CREATOR = new Parcelable.Creator<GetGroupListResult>() {
		public GetGroupListResult createFromParcel(Parcel in) {
			return new GetGroupListResult(in);
		}

		@Override
		public GetGroupListResult[] newArray(int size) {
			return new GetGroupListResult[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(followedGroupList);
		dest.writeList(otherGroupList);
	}

}
