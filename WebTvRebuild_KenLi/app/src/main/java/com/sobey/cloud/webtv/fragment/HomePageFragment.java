package com.sobey.cloud.webtv.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.NewPersonalCenterActivity;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.fragment.utils.HomeFragmentAdapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

/**
 * 首页Fragment
 */
public class HomePageFragment extends BaseFragment implements View.OnClickListener {
	@GinInjectView(id = R.id.home_attention_ll)
	private LinearLayout home_attention_ll;
	@GinInjectView(id = R.id.home_message_ll)
	private LinearLayout home_message_ll;
	@GinInjectView(id = R.id.home_live_ll)
	private LinearLayout home_live_ll;
	@GinInjectView(id = R.id.home_activity_ll)
	private LinearLayout home_activity_ll;
	@GinInjectView(id = R.id.home_attention)
	private RadioButton attenRb;
	@GinInjectView(id = R.id.home_message)
	private RadioButton msgRb;
	@GinInjectView(id = R.id.home_live)
	private RadioButton liveRb;
	@GinInjectView(id = R.id.home_activity)
	private RadioButton homeRb;
	@GinInjectView(id = R.id.main_pager)
	public ViewPager main_pager;
	@GinInjectView(id = R.id.user_login)
	private ImageView user_login;
	private FragmentManager supportManager;
	private List<Fragment> list;
	private Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();
	private HomeFragmentAdapter adapter;
	private int index;
	private Handler handler;
	private Handler handler2;

	public void setHandler(Handler handler) {
		this.handler2 = handler;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = LayoutInflater.from(getActivity());
		View v = getCacheView(mInflater, R.layout.new_page_frame);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isUseCache()) {
			return;
		}
		setupActivity();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		super.onClick(view);
		switch (view.getId()) {
		case R.id.home_attention_ll:
			changeViewPager(0);
			break;
		case R.id.home_message_ll:
			changeViewPager(1);
			break;
		case R.id.home_live_ll:
			changeViewPager(2);
			break;
		case R.id.home_activity_ll:
			changeViewPager(3);
			break;

		default:
			break;
		}
	}

	private void setupActivity() {
		user_login.setVisibility(View.GONE);
		user_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), NewPersonalCenterActivity.class);
				startActivity(intent);
			}
		});
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					attenRb.setTextColor(Color.parseColor("#2A979A"));
					msgRb.setTextColor(getResources().getColor(R.color.home_topradio_nochoose));
					homeRb.setTextColor(getResources().getColor(R.color.home_topradio_nochoose));
					liveRb.setTextColor(getResources().getColor(R.color.home_topradio_nochoose));
					main_pager.setCurrentItem(0);
					break;
				case 2:
					Message msg1 = new Message();
					handler2.sendMessage(msg1);
					break;
				}

			}
		};
		fragementOnResume();

		home_attention_ll.setOnClickListener(this);
		home_message_ll.setOnClickListener(this);
		home_live_ll.setOnClickListener(this);
		home_activity_ll.setOnClickListener(this);
	}

	public void fragementOnResume() {
		// TODO Auto-generated method stub
		if (adapter == null) {
			setViewPager();
		}

	}

	private void buidData() {
		list = new ArrayList<Fragment>();
		list.add(new HomeHeadLines());
		list.add(new HomeNewsFragment());
		list.add(new LiveFragment());
		list.add(new ActivitiesFragment());

	}

	private void setViewPager() {
		supportManager = getChildFragmentManager();
		buidData();
		adapter = new HomeFragmentAdapter(supportManager, list);
		main_pager.setOffscreenPageLimit(3);
		main_pager.setAdapter(adapter);
		main_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int paramInt) {
				index = paramInt;
				if (index == 0) {
					attenRb.setChecked(true);
					msgRb.setChecked(false);
					homeRb.setChecked(false);
					liveRb.setChecked(false);
				} else if (index == 1) {
					attenRb.setChecked(false);
					msgRb.setChecked(true);
					homeRb.setChecked(false);
					liveRb.setChecked(false);
				} else if (index == 2) {
					attenRb.setChecked(false);
					msgRb.setChecked(false);
					homeRb.setChecked(false);
					liveRb.setChecked(true);
				}else if (index == 3) {
					attenRb.setChecked(false);
					msgRb.setChecked(false);
					homeRb.setChecked(true);
					liveRb.setChecked(false);
				}
			}

			@Override
			public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int paramInt) {
				// TODO Auto-generated method stub

			}
		});

		main_pager.setCurrentItem(0);

	}

	private void changeViewPager(int index) {
		if (index == 0) {
			attenRb.setChecked(true);
			msgRb.setChecked(false);
			homeRb.setChecked(false);
			liveRb.setChecked(false);
		} else if (index == 1) {
			attenRb.setChecked(false);
			msgRb.setChecked(true);
			homeRb.setChecked(false);
			liveRb.setChecked(false);
		} else if (index == 2) {
			attenRb.setChecked(false);
			msgRb.setChecked(false);
			homeRb.setChecked(false);
			liveRb.setChecked(true);
		} else if (index == 3) {
			attenRb.setChecked(false);
			msgRb.setChecked(false);
			homeRb.setChecked(true);
			liveRb.setChecked(false);
		}
		main_pager.setCurrentItem(index);
	}
}
