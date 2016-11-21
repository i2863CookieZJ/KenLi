package com.sobey.cloud.webtv.bean;



import android.os.Parcel;
import android.os.Parcelable;

public class EbusinessGoodsModel implements Parcelable ,SobeyType{

	public String sellerName           ;
	public String sellerID           ;
	public int status           ;
	public String goodsID           ;
	public String goodsName        ;
	public String goodsImageURL    ;
	public String goodsOrginalPrice   ;
	public String goodsFactPrice         ;
	public String carriage               ;
	public String goodsURL                      ;
	
	public EbusinessGoodsModel(){
		
	}
	
	public EbusinessGoodsModel(Parcel in){
		sellerName = in.readString();
		sellerID = in.readString();
		status = in.readInt();
		goodsID = in.readString();
		goodsName = in.readString();
		goodsImageURL = in.readString();
		goodsOrginalPrice = in.readString();
		goodsFactPrice = in.readString();
		goodsURL = in.readString();
	}
	
	public static final Parcelable.Creator<EbusinessGoodsModel> CREATOR = new Parcelable.Creator<EbusinessGoodsModel>() {
		public EbusinessGoodsModel createFromParcel(Parcel in) {
			return new EbusinessGoodsModel(in);
		}

		@Override
		public EbusinessGoodsModel[] newArray(int size) {
			return new EbusinessGoodsModel[size];
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
		dest.writeString(goodsID);
		dest.writeString(goodsName);
		dest.writeString(goodsImageURL);
		dest.writeString(goodsOrginalPrice);
		dest.writeString(goodsFactPrice);
		dest.writeString(goodsURL);
	}

}
