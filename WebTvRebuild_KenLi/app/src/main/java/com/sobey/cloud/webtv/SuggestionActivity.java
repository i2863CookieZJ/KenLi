package com.sobey.cloud.webtv;

import com.dylan.common.animation.AnimationController;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class SuggestionActivity extends BaseActivity {

	@GinInjectView(id = R.id.mSuggestionLoadingImage)
	ImageView mSuggestionLoadingImage;
	@GinInjectView(id = R.id.mSuggestionBack)
	ImageButton mSuggestionBack;
	@GinInjectView(id = R.id.mSuggestionSubmit)
	ImageButton mSuggestionSubmit;
	
	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_suggestion;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				AnimationController animationController = new AnimationController();
				animationController.fadeOut(mSuggestionLoadingImage, 3000, 0);
			}
		}, 1000);
		
		mSuggestionBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
		
		mSuggestionSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
