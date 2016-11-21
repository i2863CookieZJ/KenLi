package com.sobey.cloud.webtv;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dylan.common.animation.AnimationController;
import com.dylan.common.utils.ScaleConversion;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.views.user.LoginActivity;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonArrayResultListener;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.widgets.VoteProgressBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VoteDetailActivity extends BaseActivity {

	@GinInjectView(id = R.id.mVotedetailBack)
	ImageButton mVotedetailBack;

	@GinInjectView(id = R.id.mVotedetailQuestionLayout)
	LinearLayout mVotedetailQuestionLayout;

	@GinInjectView(id = R.id.mVotedetailResultLayout)
	LinearLayout mVotedetailResultLayout;

	@GinInjectView(id = R.id.mVotedetailSubmitBtn)
	TextView mVotedetailSubmitBtn;

	@GinInjectView(id = R.id.mVotedetailSubmitSuccessBtn)
	TextView mVotedetailSubmitSuccessBtn;

	@GinInjectView(id = R.id.mVotedetailControlLayout)
	LinearLayout mVotedetailControlLayout;

	@GinInjectView(id = R.id.mVotedetailPreBtn)
	TextView mVotedetailPreBtn;

	@GinInjectView(id = R.id.mVotedetailNextBtn)
	TextView mVotedetailNextBtn;

	@GinInjectView(id = R.id.mVotedetailLastBtn)
	TextView mVotedetailLastBtn;

	@GinInjectView(id = R.id.mLoadingIconLayout)
	RelativeLayout mLoadingIconLayout;

	private int[] mColorList = { 0xffffcc00, 0xff3fc3f4, 0xff5aaf4a, 0xffe92725, 0xfff17b21 };
	private LayoutInflater mInflater;
	private String mVoteId;
	private String mVoteTitle;
	private int mIndex = 0;
	private ArrayList<QuestionObj> mQuestionList = new ArrayList<QuestionObj>();
	private ArrayList<QuestionObj> mVotedList = new ArrayList<QuestionObj>();
	private boolean mSubmitFlag = false;
	private String mUserName;

	@Override
	public int getContentView() {
		return R.layout.activity_vote_detail;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		try {
			SharedPreferences userInfo = this.getSharedPreferences("user_info", 0);
			if (userInfo == null || TextUtils.isEmpty(userInfo.getString("id", null))) {
				startActivity(new Intent(VoteDetailActivity.this, LoginActivity.class));
				finishActivity();
			}
			mUserName = userInfo.getString("id", "");

			mOpenLoadingIcon();
			hideAllView();

			mInflater = LayoutInflater.from(this);
			mVoteId = getIntent().getStringExtra("vote_id");
			mVoteTitle = getIntent().getStringExtra("vote_title");
			if (TextUtils.isEmpty(mVoteId)) {
				errorClose();
			}
			getContent();
			initFooter();
		} catch (Exception e) {
			errorClose();
		}
	}

	@GinOnClick(id = R.id.mVotedetailBack)
	private void initFooter() {
		finishActivity();
	}

	private void getContent() throws Exception {
		News.getVoteItems(this, mVoteId, new OnJsonArrayResultListener() {
			@Override
			public void onOK(JSONArray result) {
				try {
					parseContent(result);
					mCloseLoadingIcon();
				} catch (Exception e) {
					errorClose();
				}
			}

			@Override
			public void onNG(String reason) {
				errorClose();
			}

			@Override
			public void onCancel() {
				errorClose();
			}
		});
	}

	private void parseContent(JSONArray result) throws Exception {
		String subId = null;
		QuestionObj questionObj = new QuestionObj();
		for (int i = 0; i < result.length(); i++) {
			JSONObject obj = result.optJSONObject(i);
			OptionObj optionObj = new OptionObj();
			optionObj.itemId = obj.optString("ItemID");
			optionObj.itemScore = obj.optString("ItemScore");
			optionObj.itemType = obj.optString("ItemType");
			optionObj.subId = obj.optString("SubID");
			optionObj.itemContent = obj.optString("ItemContent");
			optionObj.voteId = obj.optString("VoteID");
			optionObj.subSubject = obj.optString("SubSubject");
			optionObj.subType = obj.optString("SubType");
			if (subId == null || subId.equalsIgnoreCase(optionObj.subId)) {
				subId = optionObj.subId;
				questionObj.optionList.add(optionObj);
			} else {
				mQuestionList.add(questionObj);
				mVotedList.add(new QuestionObj());
				questionObj = new QuestionObj();
				subId = optionObj.subId;
				questionObj.optionList.add(optionObj);
			}
		}
		mQuestionList.add(questionObj);
		mVotedList.add(new QuestionObj());
		showUI(0);
	}

	private void showUI(int index) {
		try {
			mIndex = index;
			hideAllView();

			// QUESTION AREA
			mVotedetailQuestionLayout.removeAllViews();
			int size = mQuestionList.size();
			if (index < 0 || index >= size) {
				return;
			}
			QuestionObj question = mQuestionList.get(index);
			OptionObj optionFirst = question.optionList.get(0);
			int itemSize = question.optionList.size();

			// Title
			TextView title = (TextView) mInflater.inflate(R.layout.item_vote_title, null);
			title.setText(mVoteTitle);
			mVotedetailQuestionLayout.addView(title);

			// Label
			if (size > 1) {
				TextView label = (TextView) mInflater.inflate(R.layout.item_vote_title, null);
				label.setText((index + 1) + "/" + size);
				label.setGravity(Gravity.CENTER);
				mVotedetailQuestionLayout.addView(label);
			}

			// Question title
			TextView questionTitle = (TextView) mInflater.inflate(R.layout.item_vote_title, null);
			String questionTitlePre = "";
			if (size > 1) {
				questionTitlePre = (index + 1) + ".";
			}
			questionTitle.setText(questionTitlePre + optionFirst.subSubject);
			mVotedetailQuestionLayout.addView(questionTitle);

			// 单选
			if (optionFirst.subType.equalsIgnoreCase("S")) {
				RadioGroup radioGroup = new RadioGroup(this);
				for (int i = 0; i < itemSize; i++) {
					// 选项按钮
					RadioButton radioButton = (RadioButton) mInflater.inflate(R.layout.item_vote_radio, null);
					radioButton.setText(question.optionList.get(i).itemContent);
					radioButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
					radioGroup.addView(radioButton);
					// 文本详情框
					EditText editText = null;
					if (question.optionList.get(i).itemType.equalsIgnoreCase("1")
							|| question.optionList.get(i).itemType.equalsIgnoreCase("2")) {
						if (question.optionList.get(i).itemType.equalsIgnoreCase("1")) {
							editText = (EditText) mInflater.inflate(R.layout.item_vote_edittext, null);
						} else {
							editText = (EditText) mInflater.inflate(R.layout.item_vote_edittext_multi_line, null);
						}
						editText.setEnabled(false);
						editText.setText(getDetail(mIndex, question.optionList.get(i).itemId));
						editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));
						MyTextWatcher watcher = new MyTextWatcher(question.optionList.get(i));
						editText.addTextChangedListener(watcher);
						radioGroup.addView(editText);
					}
					// 点击按钮事件
					MyTag tag = new MyTag();
					tag.optionObj = question.optionList.get(i);
					tag.editText = editText;
					radioButton.setTag(tag);
					radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
							MyTag tag = (MyTag) arg0.getTag();
							if (tag.editText != null) {
								if (arg1) {
									tag.editText.setEnabled(true);
									tag.editText.setText(tag.optionObj.details);
									tag.editText.selectAll();
								} else {
									tag.editText.setEnabled(false);
									tag.editText.setSelection(0);
								}
							}
							setChecked(mIndex, tag.optionObj, arg1);
						}
					});
					if (isChecked(index, question.optionList.get(i).itemId)) {
						radioGroup.check(radioButton.getId());
						if (editText != null) {
							editText.setEnabled(true);
						}
					}
					// 分割线
					if (i < itemSize - 1) {
						LinearLayout divider = (LinearLayout) mInflater.inflate(R.layout.item_vote_divider, null);
						divider.setLayoutParams(
								new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
						radioGroup.addView(divider);
					}
				}
				mVotedetailQuestionLayout.addView(radioGroup);
			}
			// 多选
			else if (optionFirst.subType.equalsIgnoreCase("D")) {
				for (int i = 0; i < itemSize; i++) {
					// 多选按钮
					CheckBox checkBox = (CheckBox) mInflater.inflate(R.layout.item_vote_checkbox, null);
					checkBox.setText(question.optionList.get(i).itemContent);
					checkBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
					mVotedetailQuestionLayout.addView(checkBox);
					// 文本详情框
					EditText editText = null;
					if (question.optionList.get(i).itemType.equalsIgnoreCase("1")
							|| question.optionList.get(i).itemType.equalsIgnoreCase("2")) {
						if (question.optionList.get(i).itemType.equalsIgnoreCase("1")) {
							editText = (EditText) mInflater.inflate(R.layout.item_vote_edittext, null);
						} else {
							editText = (EditText) mInflater.inflate(R.layout.item_vote_edittext_multi_line, null);
						}
						editText.setEnabled(false);
						editText.setText(getDetail(mIndex, question.optionList.get(i).itemId));
						editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));
						MyTextWatcher watcher = new MyTextWatcher(question.optionList.get(i));
						editText.addTextChangedListener(watcher);
						mVotedetailQuestionLayout.addView(editText);
					}
					// 点击按钮事件
					MyTag tag = new MyTag();
					tag.optionObj = question.optionList.get(i);
					tag.editText = editText;
					checkBox.setTag(tag);
					checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
							MyTag tag = (MyTag) arg0.getTag();
							if (tag.editText != null) {
								if (arg1) {
									tag.editText.setEnabled(true);
									tag.editText.setText(tag.optionObj.details);
									tag.editText.selectAll();
								} else {
									tag.editText.setEnabled(false);
									tag.editText.setSelection(0);
								}
							}
							setChecked(mIndex, tag.optionObj, arg1);
						}
					});
					if (isChecked(index, question.optionList.get(i).itemId)) {
						checkBox.setChecked(true);
						if (editText != null) {
							editText.setEnabled(true);
						}
					}
					// 分割线
					if (i < itemSize - 1) {
						LinearLayout divider = (LinearLayout) mInflater.inflate(R.layout.item_vote_divider, null);
						divider.setLayoutParams(
								new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
						mVotedetailQuestionLayout.addView(divider);
					}
				}
			}
			// 录入
			else if (optionFirst.subType.equalsIgnoreCase("W")) {
				for (int i = 0; i < itemSize; i++) {
					if (question.optionList.get(i).itemType.equalsIgnoreCase("1")
							|| question.optionList.get(i).itemType.equalsIgnoreCase("2")) {
						EditText editText;
						if (question.optionList.get(i).itemType.equalsIgnoreCase("1")) {
							editText = (EditText) mInflater.inflate(R.layout.item_vote_edittext, null);
						} else {
							editText = (EditText) mInflater.inflate(R.layout.item_vote_edittext_multi_line, null);
						}
						editText.setText(getDetail(mIndex, question.optionList.get(i).itemId));
						editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));
						MyTextWatcher watcher = new MyTextWatcher(question.optionList.get(i));
						editText.addTextChangedListener(watcher);
						mVotedetailQuestionLayout.addView(editText);
					}
				}
			} else {
				return;
			}
			mVotedetailQuestionLayout.setVisibility(View.VISIBLE);

			// BUTTON AREA
			if (size <= 1) {
				mVotedetailSubmitBtn.setEnabled(true);
				mVotedetailSubmitBtn.setVisibility(View.VISIBLE);
				mVotedetailSubmitBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						submitVote();
					}
				});
			} else {
				if (index <= 0) {
					mVotedetailPreBtn.setEnabled(false);
				} else {
					mVotedetailPreBtn.setEnabled(true);
					mVotedetailPreBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							showUI(mIndex - 1);
						}
					});
				}
				mVotedetailPreBtn.setVisibility(View.VISIBLE);
				if (index < size - 1) {
					mVotedetailNextBtn.setEnabled(true);
					mVotedetailNextBtn.setVisibility(View.VISIBLE);
					mVotedetailNextBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							showUI(mIndex + 1);
						}
					});
				} else {
					mVotedetailLastBtn.setEnabled(true);
					mVotedetailLastBtn.setVisibility(View.VISIBLE);
					mVotedetailLastBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							submitVote();
						}
					});
				}
				mVotedetailControlLayout.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			errorClose();
		}
	}

	private void submitVote() {
		if (!mSubmitFlag) {
			try {
				JSONObject resultObj = new JSONObject();
				resultObj.put("VoteID", mVoteId);
				resultObj.put("IP", "127.0.0.1");
				resultObj.put("AddUser", mUserName);
				JSONArray itemArray = new JSONArray();
				for (int i = 0; i < mVotedList.size(); i++) {
					ArrayList<OptionObj> options = mVotedList.get(i).optionList;
					for (int j = 0; j < options.size(); j++) {
						JSONObject optionObj = new JSONObject();
						optionObj.put("SubID", options.get(j).subId);
						optionObj.put("SubType", options.get(j).subType);
						optionObj.put("ItemID", options.get(j).itemId);
						optionObj.put("ItemType", options.get(j).itemType);
						optionObj.put("Details", options.get(j).details);
						itemArray.put(optionObj);
					}
				}
				if (itemArray.length() < 1) {
					Toast.makeText(this, "请填写完成后再提交", Toast.LENGTH_SHORT).show();
					return;
				}
				resultObj.put("ItemList", itemArray);
				Toast.makeText(VoteDetailActivity.this, "正在上传...", Toast.LENGTH_SHORT).show();
				mSubmitFlag = true;
				News.submitVote(resultObj.toString(), this, new OnJsonObjectResultListener() {
					@Override
					public void onOK(JSONObject result) {
						try {
							hideAllView();
							mVotedetailSubmitSuccessBtn.setVisibility(View.VISIBLE);
							JSONArray resultArray = result.optJSONArray("SubList");
							if (resultArray.length() > 0) {
								showResult(resultArray);
							}
						} catch (Exception e) {
						}
						mSubmitFlag = false;
					}

					@Override
					public void onNG(String reason) {
						Toast.makeText(VoteDetailActivity.this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
						mSubmitFlag = false;
					}

					@Override
					public void onCancel() {
						Toast.makeText(VoteDetailActivity.this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
						mSubmitFlag = false;
					}
				});
			} catch (Exception e) {
				Toast.makeText(this, "提交失败，请稍后重试", Toast.LENGTH_SHORT).show();
				mSubmitFlag = false;
			}
		}
	}

	private void showResult(JSONArray resultArray) {
		try {
			// Hide Input View
			View inputView = getWindow().peekDecorView();
			if (inputView != null) {
				InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				inputmanger.hideSoftInputFromWindow(inputView.getWindowToken(), 0);
			}
			// Title
			TextView title = (TextView) mInflater.inflate(R.layout.item_vote_title, null);
			title.setText(mVoteTitle);
			mVotedetailResultLayout.addView(title);

			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject questionObj = resultArray.getJSONObject(i);
				if (!questionObj.optString("SubType").equalsIgnoreCase("S")
						&& !questionObj.optString("SubType").equalsIgnoreCase("D")) {
					continue;
				}
				int total = Integer.valueOf(questionObj.optString("SubTotal", "1"));
				// Question title
				TextView questionTitle = (TextView) mInflater.inflate(R.layout.item_vote_title, null);
				questionTitle.setText((i + 1) + "." + questionObj.optString("Subject"));
				questionTitle.setPadding(ScaleConversion.dip2px(this, 3), ScaleConversion.dip2px(this, 10),
						ScaleConversion.dip2px(this, 3), ScaleConversion.dip2px(this, 3));
				mVotedetailResultLayout.addView(questionTitle);

				JSONArray itemArray = questionObj.optJSONArray("SubItems");
				for (int j = 0; j < itemArray.length(); j++) {
					// Item
					JSONObject itemObj = itemArray.optJSONObject(j);
					boolean checkFlag = isChecked(i, itemObj.optString("ItemID"));
					int score = Integer.valueOf(itemObj.optString("ItemScore", "0"));
					float rate = (float) (score * 1.0 / total);
					View view = mInflater.inflate(R.layout.item_vote_result, null);
					RadioButton radioButton = (RadioButton) view.findViewById(R.id.item_radio);
					CheckBox checkBox = (CheckBox) view.findViewById(R.id.item_checkbox);
					if (questionObj.optString("SubType").equalsIgnoreCase("S")) {
						radioButton.setText(itemObj.optString("ItemContent"));
						if (checkFlag) {
							radioButton.setChecked(true);
						}
						checkBox.setVisibility(View.GONE);
					} else {
						checkBox.setText(itemObj.optString("ItemContent"));
						if (checkFlag) {
							checkBox.setChecked(true);
						}
						radioButton.setVisibility(View.GONE);
					}
					VoteProgressBar progressBar = (VoteProgressBar) view.findViewById(R.id.item_progressbar);
					TextView itemSum = (TextView) view.findViewById(R.id.item_sum);
					TextView itemRate = (TextView) view.findViewById(R.id.item_rate);
					progressBar.init(rate, mColorList[j % 5]);
					itemSum.setText(String.valueOf(score));
					BigDecimal percent = new BigDecimal(rate * 100);
					itemRate.setText("(" + percent.setScale(2, BigDecimal.ROUND_HALF_UP) + "%)");
					if (checkFlag) {
						itemSum.setTextColor(0xffff0000);
						itemRate.setTextColor(0xffff0000);
					}
					mVotedetailResultLayout.addView(view);
					LinearLayout divider = (LinearLayout) mInflater.inflate(R.layout.item_vote_divider, null);
					divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
					mVotedetailResultLayout.addView(divider);
				}
			}

			mVotedetailResultLayout.setVisibility(View.VISIBLE);
		} catch (Exception e) {
		}
	}

	private boolean isChecked(int index, String itemId) {
		try {
			if (mVotedList.size() > index && mVotedList.get(index).optionList.size() > 0) {
				for (int i = 0; i < mVotedList.get(index).optionList.size(); i++) {
					if (mVotedList.get(index).optionList.get(i).itemId.equalsIgnoreCase(itemId)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private void setChecked(int index, OptionObj obj, boolean isAdd) {
		try {
			if (mVotedList.get(index) == null) {
				mVotedList.add(index, new QuestionObj());
			}
			if (obj.subType.equalsIgnoreCase("S") && isAdd) {
				mVotedList.get(index).optionList.clear();
			}
			for (int i = 0; i < mVotedList.get(index).optionList.size(); i++) {
				if (mVotedList.get(index).optionList.get(i).itemId.equalsIgnoreCase(obj.itemId)) {
					if (isAdd) {
						mVotedList.get(index).optionList.remove(i);
						mVotedList.get(index).optionList.add(obj);
					} else {
						mVotedList.get(index).optionList.remove(i);
					}
					return;
				}
			}
			if (isAdd) {
				mVotedList.get(index).optionList.add(obj);
			}
		} catch (Exception e) {
		}
	}

	private String getDetail(int index, String itemId) {
		try {
			if (mVotedList.size() > index && mVotedList.get(index).optionList.size() > 0) {
				for (int i = 0; i < mVotedList.get(index).optionList.size(); i++) {
					if (mVotedList.get(index).optionList.get(i).itemId.equalsIgnoreCase(itemId)) {
						return mVotedList.get(index).optionList.get(i).details;
					}
				}
			}
			return "";
		} catch (Exception e) {
			return "";
		}
	}

	private void hideAllView() {
		mVotedetailQuestionLayout.setVisibility(View.GONE);
		mVotedetailResultLayout.setVisibility(View.GONE);
		mVotedetailSubmitBtn.setVisibility(View.GONE);
		mVotedetailSubmitSuccessBtn.setVisibility(View.GONE);
		mVotedetailControlLayout.setVisibility(View.GONE);
		mVotedetailPreBtn.setVisibility(View.GONE);
		mVotedetailNextBtn.setVisibility(View.GONE);
		mVotedetailLastBtn.setVisibility(View.GONE);
	}

	private void errorClose() {
		Toast.makeText(VoteDetailActivity.this, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
		finishActivity();
	}

	public void mOpenLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.GONE) {
			AnimationController animationController = new AnimationController();
			animationController.show(mLoadingIconLayout);
		}
	}

	public void mCloseLoadingIcon() {
		if (mLoadingIconLayout.getVisibility() == View.VISIBLE) {
			AnimationController animationController = new AnimationController();
			animationController.fadeOut(mLoadingIconLayout, 1000, 0);
		}
	}

	@SuppressWarnings("unused")
	private class OptionObj {
		public String itemId = "";
		public String itemScore = "";
		public String itemType = "";
		public String subId = "";
		public String itemContent = "";
		public String voteId = "";
		public String subSubject = "";
		public String subType = "";
		public String details = "";
	}

	private class QuestionObj {
		public ArrayList<OptionObj> optionList = new ArrayList<VoteDetailActivity.OptionObj>();
	}

	private class MyTag {
		public EditText editText;
		public OptionObj optionObj;
	}

	private class MyTextWatcher implements TextWatcher {
		private OptionObj mOptionObj;

		public MyTextWatcher(OptionObj obj) {
			mOptionObj = obj;
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			String content = arg0.toString();
			if (mOptionObj.subType.equalsIgnoreCase("W")) {
				mOptionObj.details = content;
				if (!TextUtils.isEmpty(content)) {
					setChecked(mIndex, mOptionObj, true);
				} else {
					setChecked(mIndex, mOptionObj, false);
				}
			} else {
				if (isChecked(mIndex, mOptionObj.itemId)) {
					mOptionObj.details = content;
					setChecked(mIndex, mOptionObj, true);
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
	}

}
