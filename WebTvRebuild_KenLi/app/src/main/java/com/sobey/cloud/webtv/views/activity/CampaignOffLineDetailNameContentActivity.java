package com.sobey.cloud.webtv.views.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class CampaignOffLineDetailNameContentActivity extends BaseActivity {

	private ArrayList<NameObj> mNameObjs = new ArrayList<NameObj>();

	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mCampaigndetailBack;
	@GinInjectView(id = R.id.mCampaigndetailNameListLayout)
	LinearLayout mCampaigndetailNameListLayout;

	@Override
	public int getContentView() {
		return R.layout.activity_campaign_offline_detail_name_content;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setupNewsDetailActivity();
	}

	private void setupNewsDetailActivity() {
		try {
			JSONArray information = new JSONArray(getIntent().getStringExtra("information"));
			if (information == null || information.length() <= 0) {
				finishActivity();
			}
			mNameObjs.clear();
			for (int i = 0; i < information.length(); i++) {
				JSONObject obj = information.optJSONObject(i);
				mNameObjs.add(new NameObj(obj.optString("WinnerImgUrl"), obj.optString("WinnerName")));
			}

			mCampaigndetailBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finishActivity();
				}
			});

			mCampaigndetailNameListLayout.removeAllViews();
			for (int i = 0; i < mNameObjs.size();) {
				LinearLayout layout = new LinearLayout(this);
				layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				for (int j = 0; j < 5; j++) {
					NameObj obj = null;
					if (i < mNameObjs.size()) {
						obj = mNameObjs.get(i);
					}
					View view = LayoutInflater.from(this).inflate(R.layout.layout_campaign_offline_detail_name_item,
							null);
					if (obj == null) {
						((AdvancedImageView) view.findViewById(R.id.imageview)).setImageResource(R.drawable.trans);
						((TextView) view.findViewById(R.id.textview)).setText("");
					} else {
						SharedPreferences settings = this.getSharedPreferences("settings", 0);
						CheckNetwork network = new CheckNetwork(this);
						boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
								|| network.getWifiState(false) == State.CONNECTED;
						if (isShowPicture)
							((AdvancedImageView) view.findViewById(R.id.imageview)).setNetImage(obj.imageUrl);
						((TextView) view.findViewById(R.id.textview)).setText(obj.name);
					}
					LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
					params.weight = 1;
					view.setLayoutParams(params);
					layout.addView(view);
					i++;
				}
				mCampaigndetailNameListLayout.addView(layout);
			}
			mCampaigndetailNameListLayout.invalidate();
		} catch (Exception e) {
		}
	}

	private class NameObj {
		String imageUrl;
		String name;

		public NameObj(String imageUrl, String name) {
			this.imageUrl = imageUrl;
			this.name = name;
		}
	}
}
