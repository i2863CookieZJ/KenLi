package com.sobey.cloud.webtv.bean;



import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class FileModel implements Parcelable ,SobeyType,Comparable<FileModel>{

	public String dirFirstPhotoPath;
	public int choosed;
	public int filesCount;
	public String dir;
	public Integer order;
	public ArrayList<String> filesPaths;
	public FileModel(){
		
	}
	
	public FileModel(Parcel in){
		dirFirstPhotoPath = in.readString();
		choosed = in.readInt();
		filesCount = in.readInt();
		dir = in.readString();
		order = in.readInt();
		
		filesPaths = new ArrayList<String>();
		in.readList( filesPaths, FileModel.class.getClassLoader() );
	}
	
	public static final Parcelable.Creator<FileModel> CREATOR = new Parcelable.Creator<FileModel>() {
		public FileModel createFromParcel(Parcel in) {
			return new FileModel(in);
		}

		@Override
		public FileModel[] newArray(int size) {
			return new FileModel[size];
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(dirFirstPhotoPath);
		dest.writeInt(choosed);
		dest.writeInt(filesCount);
		dest.writeString(dir);
		dest.writeInt(order);
		dest.writeList(filesPaths);
	}

	@Override
	public int compareTo(FileModel another) {
		return this.order.compareTo(another.order);
	}

}
