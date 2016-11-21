package com.sobey.cloud.webtv.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class GetEbCollectListResult implements Parcelable ,SobeyType{

	public int totalPage;
	public List<EbusinessGoodsModel> goods;
	
	
	public GetEbCollectListResult() {
	}

	public GetEbCollectListResult(Parcel in) {
		totalPage = in.readInt();
		goods = new ArrayList<EbusinessGoodsModel>();
		in.readList( goods, EbusinessGoodsModel.class.getClassLoader() );
	}

	public static final Parcelable.Creator<GetEbCollectListResult> CREATOR = new Parcelable.Creator<GetEbCollectListResult>() {
		public GetEbCollectListResult createFromParcel(Parcel in) {
			return new GetEbCollectListResult(in);
		}

		@Override
		public GetEbCollectListResult[] newArray(int size) {
			return new GetEbCollectListResult[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(totalPage);
		dest.writeList(goods);
	}

}
