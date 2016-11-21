package com.lib.mediachooser.activity;

import com.lib.mediachooser.MediaChooserConstants;
import com.lib.mediachooser.fragment.BucketImageFragment;
import com.lib.mediachooser.fragment.BucketVideoFragment;
import com.third.library.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class BucketHomeFragmentActivity extends FragmentActivity {
	private FragmentTabHost mTabHost;
	private RelativeLayout mHeaderBar;
	private TextView mFooterBarDone;
	private TextView mFooterBarCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_media_chooser);

		mHeaderBar = (RelativeLayout) findViewById(R.id.headerbar);
		mFooterBarDone = (TextView) findViewById(R.id.footerbar_add_text);
		mFooterBarCancel = (TextView) findViewById(R.id.footerbar_cancel_text);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

		mHeaderBar.setVisibility(View.GONE);
		mFooterBarDone.setVisibility(View.GONE);
		mFooterBarCancel.setText(MediaChooserConstants.footerBarCancelString);
		mFooterBarCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
		if (MediaChooserConstants.fileType.equalsIgnoreCase("image")) {
			mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.image)), BucketImageFragment.class, null);
		} else if (MediaChooserConstants.fileType.equalsIgnoreCase("video")) {
			mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.video)), BucketVideoFragment.class, null);
		} else {
			mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.image)), BucketImageFragment.class, null);
			mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.video)), BucketVideoFragment.class, null);
		}

		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			TextView textView = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			if (textView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) textView.getLayoutParams();
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
				params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
				mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title).setLayoutParams(params);
			} else if (textView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) textView.getLayoutParams();
				params.gravity = Gravity.CENTER;
				mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title).setLayoutParams(params);
			}
			mTabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 100;
		}

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				onTabChange(tabId);
			}
		});
	}

	private void onTabChange(String tabId) {
		android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
		BucketImageFragment imageFragment = (BucketImageFragment) fragmentManager.findFragmentByTag("tab1");
		BucketVideoFragment videoFragment = (BucketVideoFragment) fragmentManager.findFragmentByTag("tab2");
		android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		if (tabId.equalsIgnoreCase("tab1")) {
			if (imageFragment == null) {
				BucketImageFragment newImageFragment = new BucketImageFragment();
				fragmentTransaction.add(R.id.tabcontent, newImageFragment, "tab1");
			} else {
				if (videoFragment != null) {
					fragmentTransaction.hide(videoFragment);
				}
				fragmentTransaction.show(imageFragment);
			}
		} else {
			if (videoFragment == null) {
				final BucketVideoFragment newVideoFragment = new BucketVideoFragment();
				fragmentTransaction.add(R.id.tabcontent, newVideoFragment, "tab2");
			} else {
				if (imageFragment != null) {
					fragmentTransaction.hide(imageFragment);
				}
				fragmentTransaction.show(videoFragment);
			}
		}
		fragmentTransaction.commit();
	}
}
