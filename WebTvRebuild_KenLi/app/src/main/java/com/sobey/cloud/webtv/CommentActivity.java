package com.sobey.cloud.webtv;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends BaseActivity {

	private static final int MAX_COUNT = 150;

	private String mTitle;
	private String mCatalogId;
	private String mCatalogType;
	private String mArticleId;
	private String mCommentUser;
	private String mContent;
	private String mIp = "127.0.0.1";

	private boolean mPublishFlag = false;

	@GinInjectView(id = R.id.mCommentContent)
	EditText mCommentContent;
	@GinInjectView(id = R.id.mCommentCloseBtn)
	ImageButton mCommentCloseBtn;
	@GinInjectView(id = R.id.mCommentPublish)
	ImageButton mCommentPublish;
	@GinInjectView(id = R.id.mCommentTextNumInfo)
	TextView mCommentTextNumInfo;

	@Override
	public int getContentView() {
		return R.layout.activity_comment;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		setupCommentActivity();
	}

	public void setupCommentActivity() {
		try {
			JSONObject obj = new JSONObject(getIntent().getStringExtra("information"));
			mTitle = obj.optString("title");
			mCatalogId = obj.optString("catalogid");
			mCatalogType = obj.optString("type");
			mArticleId = obj.optString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		SharedPreferences draftComment = CommentActivity.this.getSharedPreferences("comment_preference", 0);
		String draft = draftComment.getString("draft", null);
		if (!TextUtils.isEmpty(draft)) {
			mCommentContent.setText(draft);
			mCommentContent.setSelection(draft.length());
		}

		SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
		if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
			mCommentUser = "匿名";
		} else {
			mCommentUser = userInfo.getString("id", "");
		}

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(mCommentContent, 0);
			}
		}, 500);

		mCommentCloseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});

		mCommentPublish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContent = mCommentContent.getText().toString().trim();
				if (mContent.length() < 1) {
					Toast.makeText(CommentActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
					return;
				}
				addComment();
			}
		});

		mCommentContent.addTextChangedListener(mTextWatcher);
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		private int editStart;
		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = mCommentContent.getSelectionStart();
			editEnd = mCommentContent.getSelectionEnd();
			// 先去掉监听器，否则会出现栈溢出
			mCommentContent.removeTextChangedListener(mTextWatcher);
			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			mCommentContent.setSelection(editStart);
			// 恢复监听器
			mCommentContent.addTextChangedListener(mTextWatcher);
			setLeftCount();
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	};

	/**
	 * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
	 * 
	 * @param c
	 * @return
	 */
	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	/**
	 * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
	 */
	private void setLeftCount() {
		if ((MAX_COUNT - getInputCount()) <= 10) {
			mCommentTextNumInfo.setText(String.valueOf((MAX_COUNT - getInputCount())));
		} else {
			mCommentTextNumInfo.setText("");
		}
	}

	/**
	 * 获取用户输入的分享内容字数
	 * 
	 * @return
	 */
	private long getInputCount() {
		return calculateLength(mCommentContent.getText().toString().trim());
	}

	private void addComment() {
		Toast.makeText(CommentActivity.this, "正在上传评论信息...", Toast.LENGTH_SHORT).show();
		News.addComment(mTitle, mCatalogId, mCatalogType, mArticleId, mCommentUser, mContent, mIp, this,
				new OnJsonArrayResultListener() {
					@Override
					public void onOK(JSONArray result) {
						try {
							if (((JSONObject) result.get(0)).optString("returncode").equalsIgnoreCase("SUCCESS")) {
								Toast.makeText(CommentActivity.this, "发布评论成功", Toast.LENGTH_SHORT).show();
								mPublishFlag = true;
								close();
							} else {
								Toast.makeText(CommentActivity.this, "发布评论失败，请稍后重试!", Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							Toast.makeText(CommentActivity.this, "发布评论失败，请稍后重试!", Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onNG(String reason) {
						Toast.makeText(CommentActivity.this, "发布评论失败，请稍后重试!", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onCancel() {
						Toast.makeText(CommentActivity.this, "发布评论失败，请稍后重试!", Toast.LENGTH_LONG).show();
					}
				});
	}

	private void close() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				finishActivity();
			}
		}, 200);
	}

	@Override
	protected void onDestroy() {
		SharedPreferences draftComment = CommentActivity.this.getSharedPreferences("comment_preference", 0);
		Editor editor = draftComment.edit();
		if (!TextUtils.isEmpty(mCommentContent.getText().toString().trim()) && !mPublishFlag) {
			editor.putString("draft", mCommentContent.getText().toString().trim());
			Toast.makeText(CommentActivity.this, "内容已保存至草稿箱", Toast.LENGTH_SHORT).show();
		} else {
			editor.putString("draft", null);
		}
		editor.commit();
		super.onDestroy();
	}
}
