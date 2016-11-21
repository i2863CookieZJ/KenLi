package com.sobey.cloud.webtv.personal;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.adapter.MyMessageAdapter;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.BaseUtil;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyMessageActivity extends BaseActivity implements OnClickListener {

	@GinInjectView(id = R.id.my_message_letter_tv)
	TextView letterTv;
	@GinInjectView(id = R.id.my_message_notice_tv)
	TextView noticeTv;
	@GinInjectView(id = R.id.my_message_letter_dot_iv)
	ImageView letterDotIv;
	@GinInjectView(id = R.id.my_message_notice_dot_iv)
	ImageView noticeDotIv;
	@GinInjectView(id = R.id.my_message_listView)
	SwipeMenuListView mListView;
	@GinInjectView(id = R.id.my_message_notice_layout)
	RelativeLayout noticeLayout;
	@GinInjectView(id = R.id.my_message_letter_layout)
	RelativeLayout letterLayout;

	private boolean noticeChecked = true;
	private MyMessageAdapter adapter;
	private int colorRed;
	private int colorWhite;

	@Override
	public int getContentView() {
		return R.layout.activity_my_message;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setUpDatas();
	}

	public void setUpDatas() {

		adapter = new MyMessageAdapter(this);
		adapter.generateData();
		adapter.setShowArrow(false);
		mListView.setAdapter(adapter);
		mListView.setMenuCreator(createSwipeMenu());

		setListener();

		colorRed = getResources().getColor(R.color.new_green);
		colorWhite = getResources().getColor(R.color.white);

		setCheckedState();
	}

	private SwipeMenuCreator createSwipeMenu() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0xFF, 0xFF)));
				deleteItem.setWidth(BaseUtil.Dp2Px(MyMessageActivity.this, 90));
				deleteItem.setTitle("删除");
				deleteItem.setTitleSize(18);
				deleteItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(deleteItem);
			}
		};
		return creator;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.my_message_letter_layout:

			if (!noticeChecked) {
				return;
			}
			noticeChecked = false;
			setCheckedState();
			break;
		case R.id.my_message_notice_layout:

			if (noticeChecked) {
				return;
			}

			noticeChecked = true;
			setCheckedState();

			break;
		case R.id.title_left:

			this.finish();

			break;
		default:
			break;
		}
	}

	private void setCheckedState() {
		if (noticeChecked) {
			noticeLayout.setBackgroundResource(R.drawable.my_message_notice_checked_bg);
			letterLayout.setBackgroundResource(R.drawable.my_message_letter_unchecked_bg);
			letterTv.setTextColor(colorWhite);
			noticeTv.setTextColor(colorRed);

			adapter.setShowArrow(false);
		} else {
			noticeLayout.setBackgroundResource(R.drawable.my_message_notice_unchecked_bg);
			letterLayout.setBackgroundResource(R.drawable.my_message_letter_checked_bg);

			noticeTv.setTextColor(colorWhite);
			letterTv.setTextColor(colorRed);

			adapter.setShowArrow(true);
		}
	}

	private void setListener() {
		noticeLayout.setOnClickListener(this);
		letterLayout.setOnClickListener(this);
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					Toast.makeText(MyMessageActivity.this, "do delete:" + position, Toast.LENGTH_SHORT).show();
					break;
				case 1:
					break;
				}
				return false;
			}
		});
	}
}
