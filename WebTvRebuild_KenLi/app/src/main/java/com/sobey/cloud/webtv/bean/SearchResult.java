package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResult implements Parcelable ,SobeyType{

	public List<GroupUserModel> userModels;
	public List<GroupModel> groupModels;
	public List<GroupSubjectModel> subjectModels;
	
	public SearchResult() {
	}

	public SearchResult(Parcel in) {
		userModels = new ArrayList<GroupUserModel>();
		in.readList( userModels, GroupUserModel.class.getClassLoader() );
		groupModels = new ArrayList<GroupModel>();
		in.readList( groupModels, GroupModel.class.getClassLoader() );
		subjectModels = new ArrayList<GroupSubjectModel>();
		in.readList( subjectModels, GroupSubjectModel.class.getClassLoader() );
	}

	public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
		public SearchResult createFromParcel(Parcel in) {
			return new SearchResult(in);
		}

		@Override
		public SearchResult[] newArray(int size) {
			return new SearchResult[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(userModels);
		dest.writeList(groupModels);
		dest.writeList(subjectModels);
	}

}
