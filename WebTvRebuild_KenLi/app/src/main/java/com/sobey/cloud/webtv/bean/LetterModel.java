package com.sobey.cloud.webtv.bean;



import android.os.Parcel;
import android.os.Parcelable;

public class LetterModel implements Parcelable ,SobeyType{

	public String letterId;
	public String letterPublishUserId;
	public String letterPublishUesrName;
	public String letterPublishUserHeadUrl;
	public String letterPublishTime;
	public String letterContent;
	public String letterDes;
	public String letterUrl;
	public int letterCount;
	public int sendSuccess;//0-sending 1-success -1-failed
	
	public LetterModel(){
		
	}
	
	public LetterModel(Parcel in){
		letterId = in.readString();
		letterPublishUserId = in.readString();
		letterPublishUesrName = in.readString();
		letterPublishUserHeadUrl = in.readString();
		letterPublishTime = in.readString();
		letterContent = in.readString();
		letterDes = in.readString();
		letterUrl = in.readString();
		letterCount = in.readInt();
		sendSuccess = in.readInt();
	}
	
	public static final Parcelable.Creator<LetterModel> CREATOR = new Parcelable.Creator<LetterModel>() {
		public LetterModel createFromParcel(Parcel in) {
			return new LetterModel(in);
		}

		@Override
		public LetterModel[] newArray(int size) {
			return new LetterModel[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(letterId);
		dest.writeString(letterPublishUserId);
		dest.writeString(letterPublishUesrName);
		dest.writeString(letterPublishUserHeadUrl);
		dest.writeString(letterPublishTime);
		dest.writeString(letterContent);
		dest.writeString(letterDes);
		dest.writeString(letterUrl);
		dest.writeInt(letterCount);
		dest.writeInt(sendSuccess);
	}

}
