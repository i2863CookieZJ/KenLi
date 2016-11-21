package com.sobey.cloud.webtv.bean;



import android.os.Parcel;
import android.os.Parcelable;

public class EbusinessReciverModel implements Parcelable ,SobeyType{

	public String reciver;
	public String phoneNo;
	public String address;
	public String postNo;
	
	public EbusinessReciverModel(){
		
	}
	
	public EbusinessReciverModel(Parcel in){
		reciver = in.readString();
		phoneNo = in.readString();
		address = in.readString();
		postNo = in.readString();
	}
	
	public static final Parcelable.Creator<EbusinessReciverModel> CREATOR = new Parcelable.Creator<EbusinessReciverModel>() {
		public EbusinessReciverModel createFromParcel(Parcel in) {
			return new EbusinessReciverModel(in);
		}

		@Override
		public EbusinessReciverModel[] newArray(int size) {
			return new EbusinessReciverModel[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(reciver);
		dest.writeString(phoneNo);
		dest.writeString(address);
		dest.writeString(postNo);
	}

}
