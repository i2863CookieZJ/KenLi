package com.sobey.cloud.webtv;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.digest.Base64Parse;
import com.dylan.common.media.scanner.MediaScanner;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.api.SignUtil;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.senum.UserGender;
import com.sobey.cloud.webtv.utils.BufferUtil;
import com.sobey.cloud.webtv.utils.CommonMethod;
import com.sobey.cloud.webtv.utils.PhotoPopWindow;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.widgets.CustomProgressDialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UploadUserInfoActivity extends BaseActivity {

	private static final int MODIFY_PHOTO = 0;

	private String[] mSex = { "男", "女" };
	private int mSexIndex = 0;
	private PhotoPopWindow mPhotoPopWindow;
	private MediaScanner mMediaScanner;
	private Bitmap mHeadIconBitmap;
	private String mUserName;

	@GinInjectView(id = R.id.mUploadUserInfoGetHeadIcon)
	LinearLayout mUploadUserInfoGetHeadIcon;
	@GinInjectView(id = R.id.mUploadUserInfoHeadIcon)
	AdvancedImageView mUploadUserInfoHeadIcon;
	@GinInjectView(id = R.id.mUploadUserInfoNick)
	EditText mUploadUserInfoNick;
	@GinInjectView(id = R.id.mUploadUserInfoSex)
	TextView mUploadUserInfoSex;
	@GinInjectView(id = R.id.mUploadUserInfoEmail)
	EditText mUploadUserInfoEmail;
	@GinInjectView(id = R.id.mUploadUserInfoSubmitBtn)
	TextView mUploadUserInfoSubmitBtn;
	@GinInjectView(id = R.id.back_rl)
	RelativeLayout mBackRl;
	@GinInjectView(id = R.id.userNameText)
	TextView userNameText;

	@GinInjectView(id = R.id.operator_rl)
	private RelativeLayout mOperatorRl;

	@GinInjectView(id = R.id.operator_tv)
	private TextView mOperatorTv;

	protected boolean isRegistAutoUpload;

	protected boolean loadSocialICONEnd = false;

	private EditText phoneEt;

	/**
	 * 进度框
	 */
	private CustomProgressDialog mCustomProgressDialog;

	@Override
	public int getContentView() {
		return R.layout.activity_uploaduserinfo;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);

		this.mOperatorTv.setVisibility(View.VISIBLE);
		this.mOperatorTv.setTextColor(getResources().getColor(R.color.common_red_color));
		this.mOperatorTv.setText("保存");

		phoneEt = (EditText) findViewById(R.id.mUploadUserInfoPhone);
		isRegistAutoUpload = getIntent().getBooleanExtra("autolupload", false);
		boolean issocialRegist = getIntent().getBooleanExtra("issocialRegist", false);
		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			startActivity(new Intent(UploadUserInfoActivity.this, LoginActivity.class));
			finishActivity();
			return;
		}
		// if (LoginMode.valueOf(userInfo.getString("state", "")) !=
		// LoginMode.Login_Customer) {
		// mUploadUserInfoSubmitBtn.setVisibility(View.GONE);
		// mUploadUserInfoGetHeadIcon.setEnabled(false);
		// mUploadUserInfoNick.setEnabled(false);
		// mUploadUserInfoSex.setEnabled(false);
		// mUploadUserInfoEmail.setEnabled(false);
		// } else {
		mUploadUserInfoSubmitBtn.setVisibility(View.INVISIBLE);
		// }
		mUserName = userInfo.getString("id", "");
		userNameText.setText(mUserName);
		mUploadUserInfoNick.setText(userInfo.getString("nickname", ""));
		mUploadUserInfoEmail.setText(userInfo.getString("email", ""));
		phoneEt.setText(userInfo.getString("telphone", "").replace("null", ""));
		// mUploadUserInfoHeadIcon.setNetImage(userInfo.getString("headicon",
		// ""));
		String ulr = userInfo.getString("headicon", "");
		// 如果是注册 过来的
		if (isRegistAutoUpload) {
			if (!issocialRegist) // 如果是非社交 即手机 邮箱注册的 直接先上传个人信息
			{
				uploadUserInfo();
			} else// 否则就先把社交头像显示加载完成了后 再上传个人信息到服务器端
			{
				ImageLoader.getInstance().displayImage(ulr, mUploadUserInfoHeadIcon, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						loadSocialICONEnd = true;
						if (isRegistAutoUpload) {
							uploadUserInfo();
						}
						mUploadUserInfoHeadIcon.setImageResource(R.drawable.default_thumbnail_userlogo);
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						loadSocialICONEnd = true;
						mHeadIconBitmap = arg2;
						mUploadUserInfoHeadIcon.setImageBitmap(arg2);
						if (isRegistAutoUpload) {
							// ToastUtil.showToast(getApplicationContext(),
							// "加载第三方图片完成");
							uploadUserInfo();
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});
			}
		} else// 不是注册 过来的就直接显示地址
		{
			mUploadUserInfoHeadIcon.setNetImage(ulr);
		}
		if (!TextUtils.isEmpty(userInfo.getString("gender", ""))
				&& UserGender.valueOf(userInfo.getString("gender", "")) == UserGender.Female) {
			mSexIndex = 1;
			mUploadUserInfoSex.setText(mSex[mSexIndex]);
		}

		View mActivityLayoutView = (RelativeLayout) findViewById(R.id.activity_uploaduserinfo_layout);
		mPhotoPopWindow = new PhotoPopWindow(this, mActivityLayoutView);
		mMediaScanner = new MediaScanner(this, MODIFY_PHOTO);

		mUploadUserInfoGetHeadIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) > 20) {
					// 使用系统图库 5.0以上的系统图库自带拍照按钮
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					UploadUserInfoActivity.this.startActivityForResult(intent,
							PhotoPopWindow.PHOTO_POPWINDOW_SELECT_PICTURE);
				} else {
					mPhotoPopWindow.showPhotoWindow();
				}
				// String info = "Product Model: " + android.os.Build.MODEL +
				// "," + android.os.Build.VERSION.SDK_INT + ","
				// + android.os.Build.VERSION.RELEASE;
				// Toast.makeText(UploadUserInfoActivity.this, info,
				// Toast.LENGTH_SHORT).show();

			}
		});

		mUploadUserInfoSex.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String sex = mUploadUserInfoSex.getText().toString().trim();
				int index = 0;
				for (int i = 0; i < mSex.length; i++) {
					if (sex.equalsIgnoreCase(mSex[i])) {
						index = i;
						break;
					}
				}
				Builder builder = new AlertDialog.Builder(UploadUserInfoActivity.this);
				builder.setTitle("请选择性别");
				builder.setSingleChoiceItems(mSex, index, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mSexIndex = which;
					}
				});
				builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mUploadUserInfoSex.setText(mSex[mSexIndex]);
						dialog.dismiss();
					}
				}).show();
			}
		});

		mOperatorRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadUserInfo();
			}
		});

		mBackRl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishActivity();
			}
		});
	}

	private boolean isMobileNum(String number) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(number);
		return m.matches();
	}

	private void uploadUserInfo() {

		String nickname = mUploadUserInfoNick.getText().toString().trim();
		String sex = mSexIndex == 0 ? "男" : "女";
		String email = mUploadUserInfoEmail.getText().toString().trim();
		String logo = Base64Parse.bitmapToBase64(mHeadIconBitmap);
		String tel = phoneEt.getText().toString().trim();
		if (!TextUtils.isEmpty(tel) && !isMobileNum(tel)) {
			Toast.makeText(UploadUserInfoActivity.this, "输入的手机号错误,请修改", Toast.LENGTH_SHORT).show();
			return;
		}
		if (logo == null) {
			logo = "";
		}
		if (!isRegistAutoUpload) {
			// Toast.makeText(UploadUserInfoActivity.this, "开始上传信息...",
			// Toast.LENGTH_SHORT).show();
		}
		showProgressDialog("正在上传信息...");
		News.editUserInfo(mUserName, nickname, sex, email, logo, tel, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				BufferUtil.saveTextData("uploadUserInfo", result.toString());
				try {
					// ToastUtil.showToast(getApplicationContext(),
					// "编辑用户信息返回数据"+result.toString());
					JSONObject object = result.getJSONObject(0);
					String returnCode = object.getString("returncode");
					if (returnCode.equalsIgnoreCase("SUCCESS")) {
						getUserInfo(mUserName);
					} else {
						if (!isRegistAutoUpload)
							Toast.makeText(UploadUserInfoActivity.this, object.getString("returnmsg"),
									Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					if (!isRegistAutoUpload)
						Toast.makeText(UploadUserInfoActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onNG(String reason) {
				if (!isRegistAutoUpload)
					Toast.makeText(UploadUserInfoActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				if (!isRegistAutoUpload)
					Toast.makeText(UploadUserInfoActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
			}
		});
		if (!CommonMethod.checkNetwork(this)) {// 此处检查网络，不让他往下走，不然会出现两次弹窗提示无网络
			dismissProgressDialog();
			return;
		}
		String uid = PreferencesUtil.getLoggedUserId();
		News.modifyUserInfo(uid, nickname, sex, email, tel, this, new OnJsonObjectResultListener() {

			@Override
			public void onOK(JSONObject result) {
				dismissProgressDialog();
				if (null != result) {
					int code = result.optInt("returnCode");
					if (200 == code) {
						Log.i("UploadUserInfoActivity", "更新圈子昵称成功！");
					} else {
						Log.i("UploadUserInfoActivity", "更新圈子昵称失败！");
					}
				}
			}

			@Override
			public void onNG(String reason) {
				dismissProgressDialog();
				Log.i("UploadUserInfoActivity", "更新圈子昵称失败！");
			}

			@Override
			public void onCancel() {
				dismissProgressDialog();
				Log.i("UploadUserInfoActivity", "更新圈子昵称失败！");
			}
		});

	}

	private void getUserInfo(String username) {
		News.getUserInfo(username, this, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				BufferUtil.saveTextData("getUserInfo", result.toString());
				try {
					JSONObject object = result.getJSONObject(0);
					if (object != null) {
						String username = object.getString("username");
						String nickname = object.getString("nickname");
						String email = object.getString("email");
						String logo = object.getString("logo");
						String sex = object.getString("sex");
						String telphone = object.getString("telphone");
						SharedPreferences settings = UploadUserInfoActivity.this.getSharedPreferences("user_info", 0);
						Editor editor = settings.edit();
						editor.putString("id", username);
						editor.putString("headicon", logo);
						editor.putString("nickname", nickname);
						editor.putString("telphone", telphone);
						// editor.putString("state",
						// LoginMode.Login_Customer.toString());
						if (sex.equalsIgnoreCase("男")) {
							editor.putString("gender", UserGender.Male.toString());
						} else if (sex.equalsIgnoreCase("女")) {
							editor.putString("gender", UserGender.Female.toString());
						} else {
							editor.putString("gender", UserGender.Undefined.toString());
						}
						editor.putString("email", email);
						editor.commit();
						// TODO 同步cms用户信息
						// syncUserInfoInCms(username, nickname, sex, email,
						// Base64Parse.bitmapToBase64(mHeadIconBitmap));
						if (!isRegistAutoUpload)
							Toast.makeText(UploadUserInfoActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
						if (!isRegistAutoUpload)
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									finishActivity();
								}
							}, 1000);
					} else {
						if (!isRegistAutoUpload)
							Toast.makeText(UploadUserInfoActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					if (!isRegistAutoUpload)
						Toast.makeText(UploadUserInfoActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				} finally {
					isRegistAutoUpload = false;
				}
			}

			@Override
			public void onNG(String reason) {
				if (!isRegistAutoUpload)
					Toast.makeText(UploadUserInfoActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				isRegistAutoUpload = false;
			}

			@Override
			public void onCancel() {
				if (!isRegistAutoUpload)
					Toast.makeText(UploadUserInfoActivity.this, "服务器繁忙,请稍后重试", Toast.LENGTH_SHORT).show();
				isRegistAutoUpload = false;
			}
		});
	}

	private void syncUserInfoInCms(String userName, String nickName, String sex, String email, String logo) {
		SignUtil.modifyUserInfoFromUCenter(userName, nickName, sex, email, logo, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// Toast.makeText(UploadUserInfoActivity.this, "同步成功",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				Toast.makeText(UploadUserInfoActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case MODIFY_PHOTO: {
				mHeadIconBitmap = data.getParcelableExtra("data");
				mUploadUserInfoHeadIcon.setImageBitmap(mHeadIconBitmap);
				break;
			}
			case PhotoPopWindow.PHOTO_POPWINDOW_TAKE_PHOTO: {
				try {
					File photoFile = mPhotoPopWindow.getPhotoFile();
					mMediaScanner.start(photoFile);
				} catch (Exception e) {
					Toast.makeText(this, "拍摄头像出错,请重新尝试", Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case PhotoPopWindow.PHOTO_POPWINDOW_SELECT_PICTURE:
				Uri uri = data.getData();
				boolean isSDCard = true;
				File currentPhotoFile = null;
				ContentResolver cr = getContentResolver();
				Cursor cursor = cr.query(uri, null, null, null, null);
				if (cursor != null) {
					cursor.moveToFirst();
					isSDCard = false;
					currentPhotoFile = new File(cursor.getString(1));
				}
				if (isSDCard) {
					currentPhotoFile = new File(uri.getEncodedPath());
				}
				if (currentPhotoFile.exists()) {
					mMediaScanner.start(currentPhotoFile);
				} else {
					Toast.makeText(this, "该文件不存在", Toast.LENGTH_SHORT).show();
				}
				break;
			case PhotoPopWindow.CHOOSED_IMAGE_FILE:
				String path = data.getStringExtra("path");
				if (!TextUtils.isEmpty(path)) {
					File pathFile = new File(path);
					if (pathFile.exists()) {
						mMediaScanner.start(pathFile);
					} else {
						Toast.makeText(this, "图片文件不存在", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPhotoPopWindow.isShowing()) {
				mPhotoPopWindow.hidePhotoWindow();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (mMediaScanner != null) {
			mMediaScanner.destory();
		}
		if (isRegistAutoUpload) {
			if (!loadSocialICONEnd) {
				uploadUserInfo();
			}
		}
		super.onDestroy();
	}

	/**
	 * 显示进度框
	 * 
	 * @param message
	 */
	private void showProgressDialog(String message) {
		mCustomProgressDialog = new CustomProgressDialog(this, message);
		mCustomProgressDialog.show();
	}

	/**
	 * 关闭进度框
	 * 
	 * @param message
	 */
	private void dismissProgressDialog() {
		if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
			mCustomProgressDialog.dismiss();
		}
	}
}
