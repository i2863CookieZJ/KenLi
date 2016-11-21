package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GetEbReciverListResult implements Parcelable ,SobeyType{

	public int totalPage;
	public List<EbusinessReciverModel> reciverModels;
	
	
	public GetEbReciverListResult() {
	}

	public GetEbReciverListResult(Parcel in) {
		totalPage = in.readInt();
		reciverModels = new ArrayList<EbusinessReciverModel>();
		in.readList( reciverModels, EbusinessReciverModel.class.getClassLoader() );
	}

	public static final Parcelable.Creator<GetEbReciverListResult> CREATOR = new Parcelable.Creator<GetEbReciverListResult>() {
		public GetEbReciverListResult createFromParcel(Parcel in) {
			return new GetEbReciverListResult(in);
		}

		@Override
		public GetEbReciverListResult[] newArray(int size) {
			return new GetEbReciverListResult[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(totalPage);
		dest.writeList(reciverModels);
	}

}
