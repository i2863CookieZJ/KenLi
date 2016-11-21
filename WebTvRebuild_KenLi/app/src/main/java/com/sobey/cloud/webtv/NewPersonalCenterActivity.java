package com.sobey.cloud.webtv;

import com.sobey.cloud.webtv.kenli.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
/**
 * 新的个人中心
 * @author zouxudong
 *
 */
public class NewPersonalCenterActivity extends FragmentActivity {

	private View top_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_personal_center);
		top_back=findViewById(R.id.top_back);
		if(top_back!=null)
		{
			top_back.setVisibility(View.VISIBLE);
			top_back.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					NewPersonalCenterActivity.this.finish();
				}
			});
		}
			
	}
}
