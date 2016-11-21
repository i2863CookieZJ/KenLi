package com.sobey.cloud.webtv.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.GroupSubjectModel;
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
public class GroupPersonalInfoAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private List<GroupSubjectModel> groupSubjectModels;
	private String mKeyWord;
	private final String TAG = this.getClass().getName();

	public GroupPersonalInfoAdapter(Context ctx) {
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
			convertView = mInflater.inflate(R.layout.item_group_detail_listview_subject, null);
			holder.nameTv = (TextView) convertView.findViewById(R.id.item_subject_name_tv);
			holder.headIv = (AdvancedImageView) convertView.findViewById(R.id.item_subject_iv);
			holder.desTv = (TextView) convertView.findViewById(R.id.item_subject_des_tv);
			holder.replyNumTv = (TextView) convertView.findViewById(R.id.item_subject_reply);
			holder.likeNumTv = (TextView) convertView.findViewById(R.id.item_subject_like);
			holder.titleTv = (TextView) convertView.findViewById(R.id.item_subject_title_tv);
			holder.contentTv = (TextView) convertView.findViewById(R.id.item_subject_content_tv);
			holder.img1 = (ImageView) convertView.findViewById(R.id.item_subject_content_img1);
			holder.img2 = (ImageView) convertView.findViewById(R.id.item_subject_content_img2);
			holder.img3 = (ImageView) convertView.findViewById(R.id.item_subject_content_img3);
			holder.imgContentLayout = convertView.findViewById(R.id.item_subject_content_img_layout);
			holder.jblayout = (LinearLayout) convertView.findViewById(R.id.item_subject_jblayout);
			convertView.setTag(holder);
		} else {
			// 由于type=0的时候也会产生convertView 此时并未设置holder
			holder = (ViewHolder) convertView.getTag();
		}

		holder.headIv.setRondRadius(90);

		int replyCount = parseString2Integer(subjectModel.subjectReplyCount);
		int likeCount = parseString2Integer(subjectModel.subjectLikeCount);

		String[] urls = subjectModel.subjectPicUrls;

		buildContentPicViews(urls, holder);

		String content = subjectModel.subjectContent;
		String title = subjectModel.subjectTitle;

		if ("qz_ccsobey_placeholder".equals(content)) {
			holder.contentTv.setText("");
		} else {
			// <font color=\"#ff0000\">红色</font>
			String fontEx = "<font color=\"#ff0000\">";
			String fontSufx = "</font>";
			String newChar = new StringBuilder().append(fontEx).append(mKeyWord).append(fontSufx).toString();
			if (!TextUtils.isEmpty(mKeyWord) && !TextUtils.isEmpty(title) && title.contains(mKeyWord)) {
				holder.titleTv.setText(Html.fromHtml(title.replace(mKeyWord, newChar)));
			} else {
				holder.titleTv.setText(subjectModel.subjectTitle);
			}
			if (!TextUtils.isEmpty(mKeyWord) && !TextUtils.isEmpty(content) && content.contains(mKeyWord)) {
				content = content.replace(mKeyWord, newChar);
			}
			if (!TextUtils.isEmpty(content) && content.contains("\n")) {
				content = content.replace("\n", "<br/>");
			}
			try {
				holder.contentTv.setText(Html.fromHtml(content, new MyImgGetter(), null));
			} catch (Exception e) {
				holder.contentTv.setText(content);
			}
		}

		holder.desTv.setText(getDes(subjectModel.groupName, subjectModel.publishTime));
		// if(subjectModel.publishUserId.equals(PreferencesUtil.getLoggedUserId())){
		// holder.nameTv.setText(PreferencesUtil.getLoggedUserName());
		// }else{
		// }
		holder.nameTv.setText(subjectModel.publishUserName);
		// holder.contentTv.setText(subjectModel.subjectContent);

		// ImageLoader.getInstance().displayImage(subjectModel.publishUserHeadUrl,
		// holder.headIv);
		Picasso.with(mContext).load(subjectModel.publishUserHeadUrl.replace("http://qz.sobeycache.com", ""))
				.placeholder(R.drawable.default_head).error(R.drawable.default_head).into(holder.headIv);
		if (replyCount > 0) {
			holder.replyNumTv.setText("" + replyCount);
		} else {
			holder.replyNumTv.setText("0");
		}

		if (likeCount > 0) {
			holder.likeNumTv.setText("" + likeCount);
		} else {
			holder.likeNumTv.setText("0");
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jump2SubjectActivity(subjectModel);
			}
		});
		holder.jblayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "已举报", Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;

	}

	// 04-18 14:00 同城圈
	private String getDes(String groupName, String date) {
		String pattern1 = "yyyy-MM-dd HH:mm:ss";
		String pattern2 = "MM-dd HH:mm";
		String convertDate = "";
		SimpleDateFormat dateFormat1 = new SimpleDateFormat(pattern1);
		SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern2);
		Date d = null;
		try {
			d = dateFormat1.parse(date);
		} catch (ParseException e) {
			convertDate = date;
		}
		if (null != d)
			convertDate = dateFormat2.format(d);
		StringBuilder sb = new StringBuilder();
		sb.append(convertDate).append(" ").append(groupName);
		return sb.toString();
	}

	private void buildContentPicViews(String[] urls, ViewHolder holder) {
		if (null != urls && urls.length > 0) {
			holder.imgContentLayout.setVisibility(View.VISIBLE);
			if (urls.length < 2) {
				ImageLoader.getInstance().displayImage(urls[0], holder.img1);
				holder.img1.setVisibility(View.VISIBLE);
				holder.img2.setVisibility(View.INVISIBLE);
				holder.img3.setVisibility(View.INVISIBLE);
			} else if (urls.length < 3) {
				ImageLoader.getInstance().displayImage(urls[0], holder.img1);
				ImageLoader.getInstance().displayImage(urls[1], holder.img2);
				holder.img1.setVisibility(View.VISIBLE);
				holder.img2.setVisibility(View.VISIBLE);
				holder.img3.setVisibility(View.INVISIBLE);
			} else {
				ImageLoader.getInstance().displayImage(urls[0], holder.img1);
				ImageLoader.getInstance().displayImage(urls[1], holder.img2);
				ImageLoader.getInstance().displayImage(urls[2], holder.img3);
				holder.img1.setVisibility(View.VISIBLE);
				holder.img2.setVisibility(View.VISIBLE);
				holder.img3.setVisibility(View.VISIBLE);
			}

		} else {
			holder.imgContentLayout.setVisibility(View.GONE);
		}
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
		AdvancedImageView headIv;
		TextView nameTv;
		// TextView nameTv2;
		// TextView followTv;
		TextView desTv;
		// TextView countTv;
		TextView likeNumTv;
		TextView replyNumTv;
		TextView titleTv;
		TextView contentTv;
		ImageView img1;
		ImageView img2;
		ImageView img3;
		LinearLayout jblayout;
		View imgContentLayout;
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
