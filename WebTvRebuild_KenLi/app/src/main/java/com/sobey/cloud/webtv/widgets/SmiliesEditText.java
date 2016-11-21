package com.sobey.cloud.webtv.widgets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sobey.cloud.webtv.utils.FaceUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class SmiliesEditText extends EditText {
	private final String TAG = this.getClass().getName();
	public SmiliesEditText(Context context) {
		super(context);
	}

	public SmiliesEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void insertIcon(String face) {
		// SpannableString连续的字符串，长度不可变，同时可以附加一些object;可变的话使用SpannableStringBuilder
		int index = getSelectionStart();
		Editable editable = getText().insert(index, "[" + face + "]");
		String content = editable.toString();
//		String content = getText().toString() + "[" + face + "]";
		SpannableString spannableString = new SpannableString(content);
		Pattern pattern = null;
		pattern = Pattern.compile("\\[(\\S+?)\\]");
		Matcher matcher = pattern.matcher(content);
		int id = 0;
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			String str = matcher.group(1);
			Log.i(TAG, "str:"+str);
			if(str.contains("default")){
				id = FaceUtil.defaultFaces.get(str.replace("default_", ""));
			}else if(str.contains("coolmonkey")){
				id = FaceUtil.coolmonkeyFaces.get(str.replace("coolmonkey_", ""));
			}else if(str.contains("grapeman")){
				id = FaceUtil.grapemanFaces.get(str.replace("grapeman_", ""));
			}else{
				continue;
			}
			spannableString.setSpan(new ImageSpan(getContext(), id), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		}
		setText(spannableString);
		setSelection(spannableString.length());
	}
}