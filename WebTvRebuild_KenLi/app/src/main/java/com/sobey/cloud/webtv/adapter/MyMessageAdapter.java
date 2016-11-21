package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.MessageModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class MyMessageAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<MessageModel> mMessageModels;
	private boolean mShowArrow;

	public MyMessageAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<MessageModel> messageModels) {
		this.mMessageModels = messageModels;
	}

	public void setShowArrow(boolean showArrow) {
		this.mShowArrow = showArrow;
	}

	@Override
	public int getCount() {
		return mMessageModels.size();
	}

	@Override
	public MessageModel getItem(int position) {
		return mMessageModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			GinInjector.manualInjectView(holder, convertView);
			convertView = inflater.inflate(R.layout.listitem_my_message2, null);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.logo = (AdvancedImageView) convertView.findViewById(R.id.my_message_head_iv);
		holder.title = (TextView) convertView.findViewById(R.id.my_message_title);
		holder.addtime = (TextView) convertView.findViewById(R.id.my_message_addtime);
		holder.summary = (TextView) convertView.findViewById(R.id.my_message_summary);
		// holder.action_btn = (Button)
		// convertView.findViewById(R.id.my_message_action_btn);
		holder.linn1 = (RelativeLayout) convertView.findViewById(R.id.linn1);
		holder.arrorIv = (ImageView) convertView.findViewById(R.id.my_message_arror_iv);

		if (mShowArrow) {
			holder.arrorIv.setVisibility(View.VISIBLE);
		} else {
			holder.arrorIv.setVisibility(View.INVISIBLE);
		}

		MessageModel message = mMessageModels.get(position);
		StringBuilder sb = new StringBuilder();
		sb.append(message.msgPublishUserName).append(" ").append("回复了你：");
		holder.title.setText(sb.toString());
		holder.summary.setText(message.msgContent);
		holder.addtime.setText(message.msgPublishTime);
		ImageLoader.getInstance().displayImage(message.msgPublishUserHeadUrl, holder.logo);

		// LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)
		// holder.logo
		// .getLayoutParams(); // 取控件textView当前的布局参数
		// linearParams.width = Display.ScreenWidth / 5;
		// linearParams.height = Display.ScreenWidth / 5;
		// logo.getLayoutParams();
		// LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams)
		// linn1
		// .getLayoutParams(); // 取控件textView当前的布局参数
		// linearParams1.width = Display.ScreenWidth;
		// linn1.getLayoutParams();
		// ImageLoader.getInstance().displayImage(message.msgPublishUserHeadUrl,
		// logo);
		// action_btn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// }
		// });
		// linn1.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// }
		// });
		return convertView;
	}

	class Holder {
		@GinInjectView(id = R.id.my_message_head_iv)
		private AdvancedImageView logo;
		@GinInjectView(id = R.id.my_message_title)
		private TextView title;
		@GinInjectView(id = R.id.my_message_addtime)
		private TextView addtime;
		@GinInjectView(id = R.id.my_message_summary)
		private TextView summary;
		@GinInjectView(id = R.id.my_message_action_btn)
		private Button action_btn;
		@GinInjectView(id = R.id.linn1)
		private RelativeLayout linn1;
		@GinInjectView(id = R.id.my_message_arror_iv)
		private ImageView arrorIv;
	}

	public void generateData() {
		mMessageModels = new ArrayList<MessageModel>();
		for (int i = 0; i < 20; i++) {
			MessageModel messageModel = new MessageModel();
			messageModel.msgId = i + "";
			messageModel.msgContent = "据《纽约时报》报道，如今女性更钟爱全包臀的“大妈裤”，丁字裤已经不流行了。那你喜欢以下哪种类型的女式短裤呢？";
			messageModel.msgPublishUserHeadUrl = "";
			messageModel.msgType = 1;
			messageModel.msgPublishUserName = "时尚时尚最时尚";
			messageModel.msgPublishTime = "15-6-9 18:00";
			mMessageModels.add(messageModel);
		}
	}
}