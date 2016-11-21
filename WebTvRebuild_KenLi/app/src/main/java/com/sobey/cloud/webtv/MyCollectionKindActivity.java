package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.ebusiness.MyCollectActivity;

import android.content.Intent;
import android.view.View;

/**
 * 我的收藏分类界面
 * 
 * @author Administrator
 *
 */
public class MyCollectionKindActivity extends BaseActivity {

	@Override
	public int getContentView() {
		return R.layout.activity_mycollectionkind;
	}

	@GinOnClick(id = { R.id.ac_myck_zx, R.id.ac_myck_tm, R.id.ac_myck_tz })
	private void itemClick(View view) {
		Intent intent = null;
		switch (view.getId()) {
		case R.id.ac_myck_zx:
			intent = new Intent(this, CollectionCommonActivity.class);
			intent.putExtra("fromWho", 0);
			intent.putExtra("title", "资讯收藏");
			startActivity(intent);
			break;
		case R.id.ac_myck_tz:
			intent = new Intent(this, CollectionCommonActivity.class);
			intent.putExtra("fromWho", 1);
			intent.putExtra("title", "帖子收藏");
			startActivity(intent);
			break;
		case R.id.ac_myck_tm:
			startActivity(new Intent(this, MyCollectActivity.class));
			break;
		}
	}
}
