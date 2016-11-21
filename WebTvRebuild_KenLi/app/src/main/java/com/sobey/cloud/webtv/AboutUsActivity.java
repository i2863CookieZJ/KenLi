package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AboutUsActivity extends BaseActivity {

	@GinInjectView(id = R.id.mAboutusBack)
	ImageButton mAboutusBack;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_aboutus;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		setupReviewActivity();
	}

	public void setupReviewActivity() {
		mAboutusBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutUsActivity.this.finish();
			}
		});
	}

}
