package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.FileModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupChoosephotoFileDirAdapter extends BaseAdapter {
	List<FileModel> mFileModels;
	Context mContext;
	public GroupChoosephotoFileDirAdapter(Context ctx){
		this.mContext = ctx;
	}
	public void setData(List<FileModel> fileModels){
		this.mFileModels = fileModels;
	}
	public List<FileModel> getData(){
		return this.mFileModels;
	}
	@Override
	public int getCount() {
		return mFileModels.size();
	}

	@Override
	public FileModel getItem(int position) {
		return mFileModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		FileModel fileModel = getItem(position);
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_photo_file_dir, null);
			holder.imageView1 = (AdvancedImageView) convertView.findViewById(R.id.item_choose_photo_file_dir_iv);
			holder.imageView2 = (ImageView) convertView.findViewById(R.id.item_choose_photo_file_dir_iv2);
			holder.tv = (TextView) convertView.findViewById(R.id.item_choose_photo_file_dir_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader.getInstance().displayImage("file://"+fileModel.dirFirstPhotoPath, holder.imageView1);
		StringBuilder sb = new StringBuilder();
		sb.append(fileModel.dir).append("(").append(fileModel.filesCount).append(")");
		holder.tv.setText(sb.toString());
		if(fileModel.choosed == 1){
			holder.imageView2.setVisibility(View.VISIBLE);
		}else{
			holder.imageView2.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
	
	
	private class ViewHolder{
		AdvancedImageView imageView1;
		ImageView imageView2;
		TextView tv;
	}

}
