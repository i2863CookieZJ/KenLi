package com.sobey.cloud.webtv.widgets;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 倒计时Button
 * 
 * @author Shen Gongfei
 *
 */
public class CountDownButton extends Button {

	private int allTimeLenght;
	private String oldTxt;
	private Timer timer;

	public CountDownButton(Context context) {
		this(context, null);
	}

	public CountDownButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void startCountDown() {
		startCountDown(60);
	}

	public void startCountDown(int timeLenght) {
		allTimeLenght = timeLenght;
		oldTxt = getText().toString();
		final DecimalFormat df = new DecimalFormat("#00");
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				post(new Runnable() {
					public void run() {
						setText("重发(" + df.format(--allTimeLenght) + ")秒");
						if (allTimeLenght < 0) {
							stopCountDown();
						}
					}
				});
			}
		}, 0, 1000);

	}

	public void stopCountDown() {
		if (timer != null) {
			timer.cancel();
			setText(oldTxt);
		}
	}
}
