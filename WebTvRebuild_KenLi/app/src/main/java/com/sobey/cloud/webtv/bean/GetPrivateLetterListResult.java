package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GetPrivateLetterListResult implements Parcelable ,SobeyType{

	public List<LetterModel> letterList;
	public int letterCount;
	
	
	public GetPrivateLetterListResult() {
	}

	public GetPrivateLetterListResult(Parcel in) {
		letterList = new ArrayList<LetterModel>();
		in.readList( letterList, LetterModel.class.getClassLoader() );
		
		letterCount = in.readInt();
	}

	public static final Parcelable.Creator<GetPrivateLetterListResult> CREATOR = new Parcelable.Creator<GetPrivateLetterListResult>() {
		public GetPrivateLetterListResult createFromParcel(Parcel in) {
			return new GetPrivateLetterListResult(in);
		}

		@Override
		public GetPrivateLetterListResult[] newArray(int size) {
			return new GetPrivateLetterListResult[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(letterList);
		dest.writeInt(letterCount);
	}

}
