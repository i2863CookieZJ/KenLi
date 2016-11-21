package com.sobey.cloud.webtv.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
import com.sobey.cloud.webtv.ui.RoundImageView;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.sobey.cloud.webtv.views.group.GroupSubjectActivity;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class MyTieziAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private List<GroupSubjectModel> groupSubjectModels;
	private String mKeyWord;
	private final String TAG = this.getClass().getName();

	public MyTieziAdapter(Context ctx) {
		this.mInflater = LayoutInflater.from(ctx);
		this.mContext = ctx;
	}

	public void setKeyword(String keyWord) {
		this.mKeyWord = keyWord;
	}

	public void setData(List<GroupSubjectModel> groupSubjectModels) {
		this.groupSubjectModels = groupSubjectModels;
	}

	@Override
	public int getCount() {
		return groupSubjectModels.size();
	}

	@Override
	public GroupSubjectModel getItem(int position) {
		return groupSubjectModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("GroupDetailAdapter", "childcount:" + parent.getChildCount() + ",position:" + position);
		final GroupSubjectModel subjectModel = (GroupSubjectModel) getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.mytiezi_item_layout, null);
			holder.headIv = (RoundImageView) convertView.findViewById(R.id.mytiezi_item_headimg);
			holder.desTv = (TextView) convertView.findViewById(R.id.mytiezi_item_fromqz);
			holder.replyNumTv = (TextView) convertView.findViewById(R.id.mytiezi_item_pl);
			holder.likeNumTv = (TextView) convertView.findViewById(R.id.mytiezi_item_dz);
			holder.titleTv = (TextView) convertView.findViewById(R.id.mytiezi_item_title);
			holder.pushTimeTv = (TextView) convertView.findViewById(R.id.mytiezi_item_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int replyCount = parseString2Integer(subjectModel.subjectReplyCount);
		int likeCount = parseString2Integer(subjectModel.subjectLikeCount);

		// String content = subjectModel.subjectContent;
		String title = subjectModel.subjectTitle;

		// <font color=\"#ff0000\">红色</font>
		String fontEx = "<font color=\"#ff0000\">";
		String fontSufx = "</font>";
		String newChar = new StringBuilder().append(fontEx).append(mKeyWord).append(fontSufx).toString();
		if (!TextUtils.isEmpty(mKeyWord) && !TextUtils.isEmpty(title) && title.contains(mKeyWord)) {
			holder.titleTv.setText(Html.fromHtml(title.replace(mKeyWord, newChar)));
		} else {
			holder.titleTv.setText(subjectModel.subjectTitle);
		}
		holder.desTv.setText(subjectModel.groupName);
		holder.pushTimeTv.setText(getFormatTime(subjectModel.publishTime));
		Picasso.with(mContext).load(subjectModel.publishUserHeadUrl.replace("http://qz.sobeycache.com", ""))
				.placeholder(R.drawable.default_head).error(R.drawable.default_head).into(holder.headIv);
		if (replyCount > 0) {
			holder.replyNumTv.setText("评论数：" + replyCount);
		} else {
			holder.replyNumTv.setText("评论数：0");
		}

		if (likeCount > 0) {
			holder.likeNumTv.setText("点赞数：" + likeCount);
		} else {
			holder.likeNumTv.setText("点赞数：0");
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump2SubjectActivity(subjectModel);
			}
		});
		return convertView;

	}

	// 04-18 14:00 同城圈
	private String getFormatTime(String date) {
		String pattern = "yyyy-MM-dd";
		String convertDate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Date d = null;
		try {
			d = dateFormat.parse(date);
			convertDate = dateFormat.format(d);
		} catch (ParseException e) {
			convertDate = date;
		}
		return convertDate;

	}

	private int parseString2Integer(String count) {
		try {
			return Integer.parseInt(count);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private void jump2SubjectActivity(GroupSubjectModel groupSubjectModel) {

		Intent intent = new Intent(mContext, GroupSubjectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("mSubjectModel", groupSubjectModel);
		bundle.putString("title", groupSubjectModel.groupName);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
	}

	private class ViewHolder {
		RoundImageView headIv;
		TextView desTv;
		TextView likeNumTv;
		TextView replyNumTv;
		TextView titleTv;
		TextView pushTimeTv;
	}

	private class MyImgGetter implements Html.ImageGetter {

		@Override
		public Drawable getDrawable(String source) {
			// static/image/smiley/default/call.gif
			Drawable drawable = null;
			String face = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf("."));
			Log.i(TAG, "face:" + face);
			if (source.contains("default")) {
				drawable = mContext.getResources().getDrawable(FaceUtil.defaultFaces.get(face));
			} else if (source.contains("coolmonkey")) {
				drawable = mContext.getResources().getDrawable(FaceUtil.coolmonkeyFaces.get(face));
			} else if (source.contains("grapeman")) {
				drawable = mContext.getResources().getDrawable(FaceUtil.grapemanFaces.get(face));
			}
			if (null != drawable) {
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			}
			return drawable;
		}

	}

}
