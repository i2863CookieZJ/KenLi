package com.sobey.cloud.webtv.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.bean.SpeakMsgBean;
import com.sobey.cloud.webtv.ui.RoundImageView;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TalkingAdapter extends UniversalAdapter<SpeakMsgBean>implements OnClickListener, OnLongClickListener {

	private List<TextView> delBtnList;
	private OnMessageDelListener onMessageDelListener;

	public TalkingAdapter(Context context, List<SpeakMsgBean> mDatas) {
		super(context, mDatas, R.layout.item_talking);
		delBtnList = new ArrayList<TextView>();
	}

	@Override
	public void convert(UniversalViewHolder holder, SpeakMsgBean smb) {
		LinearLayout fromLayout = holder.findViewById(R.id.item_talking_fromlayout);
		LinearLayout toLayout = holder.findViewById(R.id.item_talking_tolayout);
		TextView timeTv = holder.findViewById(R.id.item_talking_time);
		// his
		RoundImageView hisHead = holder.findViewById(R.id.item_talking_hisicon);
		TextView heSayTv = holder.findViewById(R.id.item_talking_hesay);
		heSayTv.setOnLongClickListener(this);
		TextView delHisBtn = holder.findViewById(R.id.item_talking_delhismsg);
		delHisBtn.setOnClickListener(this);
		delHisBtn.setTag(smb);
		delBtnList.add(delHisBtn);
		// my
		RoundImageView iHead = holder.findViewById(R.id.item_talking_iicon);
		TextView iSayTv = holder.findViewById(R.id.item_talking_isay);
		iSayTv.setOnLongClickListener(this);
		TextView delMyBtn = holder.findViewById(R.id.item_talking_delmymsg);
		delMyBtn.setOnClickListener(this);
		delMyBtn.setTag(smb);
		delBtnList.add(delMyBtn);

		// 时间
		timeTv.setText(smb.getTime());

		if ("0".equals(smb.getFrom_type())) {// 我说的
			fromLayout.setVisibility(View.GONE);
			toLayout.setVisibility(View.VISIBLE);
			if (TextUtils.isEmpty(smb.getHead())) {
				iHead.setImageResource(R.drawable.default_head);
			} else {
				Picasso.with(mContext).load(smb.getHead()).placeholder(R.drawable.default_head)
						.error(R.drawable.default_head).into(iHead);
			}
			try {
				iSayTv.setText(URLDecoder.decode(smb.getMessage(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				iSayTv.setText(smb.getMessage());
			}
		} else if ("1".equals(smb.getFrom_type())) {// 对方说的
			fromLayout.setVisibility(View.VISIBLE);
			toLayout.setVisibility(View.GONE);
			if (TextUtils.isEmpty(smb.getFriend_head())) {
				hisHead.setImageResource(R.drawable.default_head);
			} else {
				Picasso.with(mContext).load(smb.getFriend_head()).placeholder(R.drawable.default_head)
						.error(R.drawable.default_head).into(hisHead);
			}
			try {
				heSayTv.setText(URLDecoder.decode(smb.getMessage(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				heSayTv.setText(smb.getMessage());
			}
		}
		// 触摸后隐藏所有删除按钮
		holder.getConvertView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					for (TextView delbtn : delBtnList) {
						delbtn.setVisibility(View.INVISIBLE);
					}
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		onMessageDelListener.onMessageDel((SpeakMsgBean) v.getTag());
		v.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onLongClick(View v) {
		ViewGroup viewGroup = (ViewGroup) v.getParent().getParent();
		// TextView delBtn;
		for (TextView delbtn : delBtnList) {
			delbtn.setVisibility(View.INVISIBLE);
		}
		switch (v.getId()) {
		case R.id.item_talking_hesay:
			viewGroup.findViewById(R.id.item_talking_delhismsg).setVisibility(View.VISIBLE);
			break;
		case R.id.item_talking_isay:
			viewGroup.findViewById(R.id.item_talking_delmymsg).setVisibility(View.VISIBLE);
			break;
		}
		return false;
	}

	public interface OnMessageDelListener {
		public void onMessageDel(SpeakMsgBean smb);
	}

	public void setOnMessageDelListener(OnMessageDelListener onMessageDelListener) {
		this.onMessageDelListener = onMessageDelListener;
	}

}
