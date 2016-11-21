package com.sobey.cloud.webtv.bean;



import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class EbusinessSellerModel implements Parcelable ,SobeyType{

	public String sellerName;
	public String sellerID;
	public int status;
	public String postNo;
	public List<EbusinessGoodsModel> goods;
	
	public EbusinessSellerModel(){
		
	}
	
	public EbusinessSellerModel(Parcel in){
		sellerName = in.readString();
		sellerID = in.readString();
		status = in.readInt();
		postNo = in.readString();
		
		goods = new ArrayList<EbusinessGoodsModel>();
		in.readList( goods, EbusinessGoodsModel.class.getClassLoader() );
	}
	
	public static final Parcelable.Creator<EbusinessSellerModel> CREATOR = new Parcelable.Creator<EbusinessSellerModel>() {
		public EbusinessSellerModel createFromParcel(Parcel in) {
			return new EbusinessSellerModel(in);
		}

		@Override
		public EbusinessSellerModel[] newArray(int size) {
			return new EbusinessSellerModel[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sellerName);
		dest.writeString(sellerID);
		dest.writeInt(status);
		dest.writeString(postNo);
		
		dest.writeList(goods);
	}

}
