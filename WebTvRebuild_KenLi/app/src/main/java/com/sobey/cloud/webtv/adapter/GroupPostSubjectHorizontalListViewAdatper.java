package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.PhotoModel;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.Display;
import com.sobey.cloud.webtv.views.group.GroupChoosePhotoActivity;
import com.sobey.cloud.webtv.views.group.GroupPostSubjectActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class GroupPostSubjectHorizontalListViewAdatper extends BaseAdapter {
	private int mImgHeight = 0;
	private int mImgWidth = 0;
	private Context mContext;
	private List<PhotoModel> mPhotoModels = new ArrayList<PhotoModel>();
	private ArrayList<String> upLoadfiles = new ArrayList<String>();
	private Map<String,Bitmap> cacheBitmaps = new HashMap<String,Bitmap>();
	private OnItemDeleteListener mDeleteListener;
	public GroupPostSubjectHorizontalListViewAdatper(Context context){
		this.mContext = context;
		mImgHeight = Display.scaleHxxhdpi(365);
		mImgWidth = Display.scaleHxxhdpi(245);
	}
	public void setData(List<PhotoModel> photoModels){
		this.mPhotoModels = photoModels;
	}
	public void setDatas(ArrayList<String> paths){
		
		this.upLoadfiles = paths;
		
		mPhotoModels.clear();
		for(String path:paths){
			PhotoModel model = new PhotoModel();
			model.visiable = true;
			model.showDel = true;
			model.path = path;
			mPhotoModels.add(model);
		}
		//添加add图片
		PhotoModel model = new PhotoModel();
		model.resId = R.drawable.selector_btn_add_picture;
		model.showDel = false;
		model.visiable = true;
		mPhotoModels.add(model);
	}
	public void setOnItemDeletedListener(OnItemDeleteListener deleteListener){
		this.mDeleteListener = deleteListener;
	}
	@Override
	public int getCount() {
		return mPhotoModels.size();
	}

	@Override
	public PhotoModel getItem(int position) {
		return mPhotoModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhotoModel model = getItem(position);
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_group_post_subject_horizontallistview, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.item_imamge);
		Button delbtn = (Button) view.findViewById(R.id.item_delete_btn);
		Display.setLayoutParams(imageView, mImgWidth, mImgHeight);
		if(!TextUtils.isEmpty(model.path)){
			Bitmap bmp = null;
			if(cacheBitmaps.containsKey(model.path)){
				bmp = cacheBitmaps.get(model.path);
			}else{
				ArrayList<Object> objects = BaseUtil.getResizedBitmapByPath(model.path);
				bmp = (Bitmap) objects.get(0);
				cacheBitmaps.put(model.path, bmp);
			}
			imageView.setImageBitmap(bmp);
		}else{
			imageView.setBackgroundResource(model.resId);
		}
		if(model.visiable){
			imageView.setVisibility(View.VISIBLE);
		}else{
			imageView.setVisibility(View.INVISIBLE);
		}
		if(model.showDel){
			delbtn.setVisibility(View.VISIBLE);
		}else{
			delbtn.setVisibility(View.GONE);
		}
		imageView.setOnClickListener(new MyClickListener(model.path));
		delbtn.setOnClickListener(new MyClickListener(model.path));
		return view;
	}

	public List<PhotoModel> generateBaseData(){
		List<PhotoModel> list = new ArrayList<PhotoModel>();
		for(int i=0;i<5;i++){
			PhotoModel model = new PhotoModel();
			model.resId = R.drawable.btn_add_photo_n;
			model.showDel = false;
			if(i == 1){
				model.visiable = true;
			}
			list.add(model);
		}
		return list;
	}
	
	private class MyClickListener implements OnClickListener{
		private String mPath;
		
		public MyClickListener(String path){
			this.mPath = path;
		}
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.item_imamge:
				
				jump2GroupPostSubjectActivity();
				
				break;
			case R.id.item_delete_btn:
				
				doDeleteAction();
				
				break;

			default:
				break;
			}
			
		}
		void jump2GroupPostSubjectActivity(){
			//为空表示为添加图片
			if(mPath == null){
				Intent intent = new Intent(mContext,GroupChoosePhotoActivity.class);
				intent.putStringArrayListExtra("choosed_photo_path", upLoadfiles);
				((Activity)mContext).startActivityForResult(intent, 
						GroupPostSubjectActivity.REQ_CODE_FOR_GET_PICS);
			}
		}
		void doDeleteAction(){
			if(mPath.contains("file://")){
				mPath = mPath.replace("file://", "");
			}
			mDeleteListener.itemDeleted(mPath);
		}
		
	}
	public interface OnItemDeleteListener{
		void itemDeleted(String path);
	}
}
