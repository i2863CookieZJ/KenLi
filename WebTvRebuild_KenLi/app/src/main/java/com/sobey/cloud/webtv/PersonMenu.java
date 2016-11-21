package com.sobey.cloud.webtv;

import java.util.ArrayList;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.obj.ModuleMenuItem;
import com.sobey.cloud.webtv.senum.LoginMode;
import com.sobey.cloud.webtv.senum.UserGender;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PersonMenu extends ListFragment {

	private BaseAdapter mAdapter = null;
	private ArrayList<ModuleMenuItem> mMenuItems = new ArrayList<ModuleMenuItem>();
	private View mContainer = null;
	private LinearLayout mLoginLayout = null;
	private LinearLayout mLogoutLayout = null;
	private AdvancedImageView mHeaderIcon = null;
	private TextView mNickname = null;
	private ImageView mLoginStateIcon = null;
	private ImageView mGenderIcon = null;

	public interface PersonChoiceListener {
		void onAction(String module);

		void onUserCenterClick();

		void onSettingsClick();
	}

	PersonChoiceListener mListener;

	public void setListener(PersonChoiceListener listener) {
		mListener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContainer = inflater.inflate(R.layout.sliding_personmenu, null);
		mLoginLayout = (LinearLayout) mContainer.findViewById(R.id.personmenu_login_layout);
		mLogoutLayout = (LinearLayout) mContainer.findViewById(R.id.personmenu_logout_layout);
		mHeaderIcon = (AdvancedImageView) mContainer.findViewById(R.id.header_icon);
		mNickname = (TextView) mContainer.findViewById(R.id.user_nickname);
		mLoginStateIcon = (ImageView) mContainer.findViewById(R.id.user_login_state);
		mGenderIcon = (ImageView) mContainer.findViewById(R.id.user_gender);
		return mContainer;
		
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mMenuItems.add(new ModuleMenuItem("推荐给朋友", null, getActivity().getResources().getDrawable(R.drawable.personmenu_share_icon)));
		mMenuItems.add(new ModuleMenuItem("我的评论", null, getActivity().getResources().getDrawable(R.drawable.personmenu_comments_icon)));
		mMenuItems.add(new ModuleMenuItem("我的收藏", null, getActivity().getResources().getDrawable(R.drawable.personmenu_collection_icon)));
		mMenuItems.add(new ModuleMenuItem("搜索资讯", null, getActivity().getResources().getDrawable(R.drawable.personmenu_search_icon)));
		// mMenuItems.add(new ModuleMenuItem("成都 晴天",
		// getActivity().getResources().getDrawable(R.drawable.personmenu_weather_icon)));
//		mMenuItems.add(new ModuleMenuItem("意见反馈", getActivity().getResources().getDrawable(R.drawable.personmenu_suggestion_icon)));
		mAdapter = new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_personmenu, null);
				}
				((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(mMenuItems.get(position).getIcon());
				((TextView) convertView.findViewById(R.id.title)).setText(mMenuItems.get(position).getTitle());
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return mMenuItems.size();
			}
		};
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mListener != null)
					mListener.onAction(mMenuItems.get(position).getTitle());
			}
		});

		View settingsView = mContainer.findViewById(R.id.dosetting);
		settingsView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null)
					mListener.onSettingsClick();
			}
		});

		View userCenterView = mContainer.findViewById(R.id.user_center);
		userCenterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null)
					mListener.onUserCenterClick();
			}
		});
	}

	public void setUserInfo(String headerIcon, String nickname, String loginState, String gender) {
		if (mLoginLayout != null) {
			mLoginLayout.setVisibility(View.VISIBLE);
			mLogoutLayout.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(headerIcon)) {
				mHeaderIcon.setNetImage(headerIcon);
			}
			if (!TextUtils.isEmpty(nickname)) {
				mNickname.setText(nickname);
			}
			if (!TextUtils.isEmpty(loginState)) {
				switch (LoginMode.valueOf(loginState)) {
				case Login_SinaWB:
					mLoginStateIcon.setImageResource(R.drawable.login_state_sinawb_icon);
					break;
				case Login_TencentWB:
					mLoginStateIcon.setImageResource(R.drawable.login_state_tencentwb_icon);
					break;
				case Login_QQ:
					mLoginStateIcon.setImageResource(R.drawable.login_state_qq_icon);
					break;
				default:
					mLoginStateIcon.setImageResource(R.drawable.trans);
					break;
				}
			}
			if (!TextUtils.isEmpty(gender)) {
				switch (UserGender.valueOf(gender)) {
				case Male:
					mGenderIcon.setImageResource(R.drawable.gender_male_icon);
					break;
				case Female:
					mGenderIcon.setImageResource(R.drawable.gender_female_icon);
					break;
				default:
					mGenderIcon.setImageResource(R.drawable.trans);
					break;
				}
			}
		}
	}

	public void clearUserInfo() {
		if (mLoginLayout != null) {
			mLoginLayout.setVisibility(View.GONE);
			mLogoutLayout.setVisibility(View.VISIBLE);
			mHeaderIcon.clear();
			mNickname.setText("");
			mLoginStateIcon.setImageResource(R.drawable.trans);
			mGenderIcon.setImageResource(R.drawable.trans);
		}
	}
}
