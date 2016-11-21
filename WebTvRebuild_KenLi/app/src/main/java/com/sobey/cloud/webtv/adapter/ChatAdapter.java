package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.LetterModel;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.widgets.MyAlertDialog;
import com.sobey.cloud.webtv.widgets.MyAlertDialog.MyOnClickListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {
	List<LetterModel> letters = new ArrayList<LetterModel>();
	private Context mContext;
	public ChatAdapter(Context context){
		this.mContext = context;
	}
	@Override
	public int getCount() {
		return letters.size();
	}

	@Override
	public LetterModel getItem(int position) {
		return letters.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LetterModel letterModel = getItem(position);
		String uid = PreferencesUtil.getLoggedUserId();
		ViewHolder holder = new ViewHolder();
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_layout, null);
			holder.leftLayout = convertView.findViewById(R.id.chat_left_layout);
			holder.rightLayout = convertView.findViewById(R.id.chat_right_layout);
			
			holder.leftTimeTv = (TextView) convertView.findViewById(R.id.chat_left_time_tv);
			holder.leftContentTv = (TextView) convertView.findViewById(R.id.chat_left_content_tv);
			holder.leftDesTv = (TextView) convertView.findViewById(R.id.chat_left_content_des);
			holder.leftHeadIv = (AdvancedImageView) convertView.findViewById(R.id.chat_left_head_iv);
			
			holder.rightTimeTv = (TextView) convertView.findViewById(R.id.chat_right_time_tv);
			holder.rightContentTv = (TextView) convertView.findViewById(R.id.chat_right_content_tv);
			holder.rightDesTv = (TextView) convertView.findViewById(R.id.chat_right_content_des);
			
			holder.pb = (ProgressBar) convertView.findViewById(R.id.chat_pb);
			holder.sendErrorBtn = (Button) convertView.findViewById(R.id.chat_send_error_btn);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(uid.equals(letterModel.letterPublishUserId)){
			holder.leftLayout.setVisibility(View.GONE);
			holder.rightLayout.setVisibility(View.VISIBLE);
			
			holder.rightTimeTv.setText(letterModel.letterPublishTime);
			holder.rightContentTv.setText(letterModel.letterContent);
			holder.rightDesTv.setText(letterModel.letterDes);
		}else{
			holder.leftLayout.setVisibility(View.VISIBLE);
			holder.rightLayout.setVisibility(View.GONE);
			
			holder.leftTimeTv.setText(letterModel.letterPublishTime);
			holder.leftContentTv.setText(letterModel.letterContent);
			holder.leftDesTv.setText(letterModel.letterDes);
			ImageLoader.getInstance().displayImage(letterModel.letterPublishUserHeadUrl, 
					holder.leftHeadIv);
		}
		
		if(letterModel.sendSuccess == 1){
			holder.pb.setVisibility(View.INVISIBLE);
			holder.sendErrorBtn.setVisibility(View.INVISIBLE);
		}else if(letterModel.sendSuccess == 0){
			holder.pb.setVisibility(View.VISIBLE);
			holder.sendErrorBtn.setVisibility(View.INVISIBLE);
		}else if(letterModel.sendSuccess == -1){
			holder.pb.setVisibility(View.INVISIBLE);
			holder.sendErrorBtn.setVisibility(View.VISIBLE);
		}
		holder.sendErrorBtn.setOnClickListener(new MyClickListener(letterModel));
		return convertView;
	}
	public void addData(LetterModel letterModel){
		letters.add(letters.size(), letterModel);
		notifyDataSetChanged();
	}
	public List<LetterModel> getData(){
		return letters;
	}
	public void generateDatas(String publishUserId,String publishUrl){
		for(int i=0;i<40;i++){
			LetterModel letterModel = new LetterModel();
			letterModel.sendSuccess = 1;
			letterModel.letterPublishTime = "2015-6-5 19:00";
			letterModel.letterId = i+"";
			if(i % 2 == 0){
				letterModel.letterPublishUserId = PreferencesUtil.getLoggedUserId();
			}else{
				letterModel.letterPublishUserId = publishUserId;
				letterModel.letterPublishUserHeadUrl = publishUrl;
			}
			letterModel.letterContent = "Tomcat注册成windows系统服务之后，如何增加 security 安全参数在dos命令行启动tomcat为："
					+ "startup.bat -security，传递了security安全参数，防止用jsp列表服务器上的文件、目录对象，但是注册成系统服务之后，"
					+ "无法为服务增加参数。在网上搜索了很多资料，问类似问题的也不少，但是没有一个解决的。经过自己测试，终于找到了解决办法。"
					+ "为了不让其他网友走自己同样的弯路，"
					+ "特将方法总结一下。在 tomcat的bin目录下，有一tomcat5w.exe文件，此工具是监控tomcat服务状态及配置服务的。";
			if(i % 3 == 0){
				letterModel.letterDes = "Tomcat5.exe可以把Tomcat加入服务；Tomcat5w.exe可以辅助配置已经添加的服务。";
			}
			if(i == 38)
				letterModel.letterContent = "阿西吧哟";
			letters.add(letterModel);
		}
	}
	private class ViewHolder {
		TextView leftTimeTv;
		AdvancedImageView leftHeadIv;
		TextView leftContentTv;
		TextView leftDesTv;
		TextView rightTimeTv;
//		AdvancedImageView rightHeadIv;
		TextView rightContentTv;
		TextView rightDesTv;
		View leftLayout;
		View rightLayout;
		ProgressBar pb;
		Button sendErrorBtn;
	}
	private class MyClickListener implements View.OnClickListener{
		private LetterModel letterModel;
		public MyClickListener(LetterModel letterModel){
			this.letterModel = letterModel;
		}
		@Override
		public void onClick(View v) {
			showTurnPageDialog();
		}
		
		
	}
	private void showTurnPageDialog() {
		final MyAlertDialog alertDialog = new MyAlertDialog(mContext,
				R.layout.dialog_layout_for_base, 0);

		alertDialog.setMyOnClickListener(new MyOnClickListener() {

			@Override
			public void onConfirm() {
				doSendAction();
				alertDialog.dismiss();
			}

			@Override
			public void onClick(View view) {

			}

			@Override
			public void onCancel() {
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
		alertDialog.setTitle(mContext.getResources().getString(R.string.sure_to_resend));
	}
	private void doSendAction(){
		
	}
}
