package com.sobey.cloud.webtv;

import org.json.JSONArray;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterModifyNicknameActivity extends BaseActivity {

	private String mUsername;

	@GinInjectView(id = R.id.mRegisterModifyNicknameText)
	EditText mRegisterModifyNicknameText;

	@GinInjectView(id = R.id.mRegisterModifyNicknameSubmitBtn)
	TextView mRegisterModifyNicknameSubmitBtn;

	@GinInjectView(id = R.id.mRegisterModifyNicknameBack)
	ImageButton mRegisterModifyNicknameBack;

	@Override
	public int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_register_modify_nickname;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onDataFinish(savedInstanceState);
		mUsername = getIntent().getStringExtra("phone_number");
		if (TextUtils.isEmpty(mUsername)) {
			finishActivity();
		}

		mRegisterModifyNicknameBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finishActivity();
			}
		});

		mRegisterModifyNicknameSubmitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String nickname = mRegisterModifyNicknameText.getText().toString().trim();
				if (TextUtils.isEmpty(nickname)) {
					Toast.makeText(RegisterModifyNicknameActivity.this, "请输入您的昵称", Toast.LENGTH_SHORT).show();
					return;
				}
				String sex = "M";
				String email = "";
				String logo = "";

				News.editUserInfo(mUsername, nickname, sex, email, logo, null, RegisterModifyNicknameActivity.this,
						new OnJsonArrayResultListener() {
							@Override
							public void onOK(JSONArray result) {
								Toast.makeText(RegisterModifyNicknameActivity.this, "注册完成", Toast.LENGTH_SHORT).show();
								Intent intent = getIntent();
								setResult(RESULT_OK, intent);
								finishActivity();
							}

							@Override
							public void onNG(String reason) {
							}

							public void onCancel() {
							}
						});
			}
		});
	}
}
