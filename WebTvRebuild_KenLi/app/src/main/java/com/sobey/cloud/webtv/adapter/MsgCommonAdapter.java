package com.sobey.cloud.webtv.adapter;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.sobey.cloud.webtv.bean.MsgCommonBean;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.ui.RoundImageView;
import com.sobey.cloud.webtv.utils.BaseUtil;
import com.sobey.cloud.webtv.utils.FaceUtil;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MsgCommonAdapter extends UniversalAdapter<MsgCommonBean> {

	public MsgCommonAdapter(Context context, List<MsgCommonBean> mDatas) {
		super(context, mDatas, R.layout.item_msg_common);
	}

	@Override
	public void convert(UniversalViewHolder holder, MsgCommonBean mcb) {
		RoundImageView headIcon = holder.findViewById(R.id.item_msgcommon_headicon);
		TextView time = holder.findViewById(R.id.item_msgcommon_time);
		TextView userName = holder.findViewById(R.id.item_msgcommon_who);
		TextView content = holder.findViewById(R.id.item_msgcommon_content);
		TextView msgCount = holder.findViewById(R.id.item_msgcommon_msgcount);
		LinearLayout replyLayout = holder.findViewById(R.id.item_msgcommon_reply);
		TextView replyUser = holder.findViewById(R.id.item_msgcommon_replyuser);
		TextView replyContent = holder.findViewById(R.id.item_msgcommon_replycontent);
		if (TextUtils.isEmpty(mcb.getHeadUrl())) {
			headIcon.setImageResource(R.drawable.mymsg_sys);
		} else {
			Picasso.with(mContext).load(mcb.getHeadUrl()).placeholder(R.drawable.default_head)
					.error(R.drawable.default_head).into(headIcon);
		}
		time.setText(mcb.getTime());
		userName.setText(mcb.getUserName());
		try {
			content.setText(URLDecoder.decode(mcb.getContent(), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			content.setText(mcb.getContent());
		}
		if (TextUtils.isEmpty(mcb.getMsgCount())) {
			msgCount.setVisibility(View.INVISIBLE);
		} else {
			msgCount.setVisibility(View.VISIBLE);
			msgCount.setText(mcb.getMsgCount());
		}

		// 如果是圈子评论
		if (!TextUtils.isEmpty(mcb.getReplyContent())) {
			replyLayout.setVisibility(View.VISIBLE);
			replyUser.setText(mcb.getReplyUser() + ":");
			replyUser.setTextColor(Color.rgb(234, 79, 0));
			String text = mcb.getReplyContent();
			if (!TextUtils.isEmpty(text) && text.contains("\n")) {
				text = text.replace("\n", "<br/>");
			}
			try {
				replyContent.setText(Html.fromHtml(text, new MyImgGetter(), null));
			} catch (Exception e) {
				replyContent.setText(text);
			}
		} else {
			replyLayout.setVisibility(View.GONE);
		}
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 下载图片完成后刷新界面
			if (msg.what == 0) {
				notifyDataSetChanged();
			}
		};
	};

	private void toSavePhoto(final String path, final String fileName) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					InputStream inputStream = BaseUtil.getImageStream(path);
					File file = BaseUtil.createSavePhotoFile(fileName);
					Message msg = new Message();
					msg.what = 0;
					msg.obj = BaseUtil.downLoadFile(inputStream, file);
					myHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private class MyImgGetter implements Html.ImageGetter {

		@Override
		public Drawable getDrawable(String source) {
			// static/image/smiley/default/call.gif
			Drawable drawable = null;
			String face = source.substring(source.lastIndexOf("/") + 1, source.lastIndexOf("."));
			if (source.startsWith("http")) {
				String fileName = BaseUtil.getFileName(source);
				String fullPath = BaseUtil.getFilePath() + fileName;
				File file = new File(fullPath);
				if (BaseUtil.isFileExist(fullPath)) {
					// Toast.makeText(this, "该图片已下载!",
					// Toast.LENGTH_SHORT).show();
					drawable = Drawable.createFromPath(file.getAbsolutePath());
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
					return drawable;
				} else {
					toSavePhoto(source, fileName);
					return mContext.getResources().getDrawable(R.drawable.default_thumbnail_banner);
				}
			}
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
