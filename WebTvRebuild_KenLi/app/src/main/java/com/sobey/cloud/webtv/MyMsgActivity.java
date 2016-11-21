package com.sobey.cloud.webtv;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyMsgActivity extends BaseActivity {

	private boolean haveMsg[];
	@GinInjectView(id = R.id.ac_mymsg_lettertip)
	TextView letterTipTv;
	@GinInjectView(id = R.id.ac_mymsg_qzpltip)
	TextView qzTipTv;
	@GinInjectView(id = R.id.ac_mymsg_sysmsgtip)
	TextView sysTipTv;

	@Override
	public int getContentView() {
		return R.layout.acitivity_mymsg;

	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		haveMsg = getIntent().getBooleanArrayExtra("havemsg");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (this.haveMsg != null) {
			if (haveMsg[0]) {
				letterTipTv.setText("您有消息啦~");
			} else {
				letterTipTv.setText("");
			}
			if (haveMsg[1]) {
				qzTipTv.setText("您有评论啦~");
			} else {
				qzTipTv.setText("");
			}
			if (haveMsg[2]) {
				sysTipTv.setText("您有消息啦~");
			} else {
				sysTipTv.setText("");
			}
		}
	}

	@GinOnClick(id = { R.id.ac_mymsg_letter, R.id.ac_mymsg_qzpl, R.id.ac_mymsg_sysmsg })
	private void itemClick(View view) {
		Intent intent = null;
		switch (view.getId()) {
		case R.id.ac_mymsg_letter:
			intent = new Intent(this, MsgCommonActivity.class);
			intent.putExtra("fromWho", 0);
			intent.putExtra("title", "私信列表");
			startActivity(intent);
			haveMsg[0] = false;
			break;
		case R.id.ac_mymsg_qzpl:
			intent = new Intent(this, MsgCommonActivity.class);
			intent.putExtra("fromWho", 1);
			intent.putExtra("title", "圈子评论");
			startActivity(intent);
			haveMsg[1] = false;
			break;
		case R.id.ac_mymsg_sysmsg:
			intent = new Intent(this, MsgCommonActivity.class);
			intent.putExtra("fromWho", 2);
			intent.putExtra("title", "系统消息");
			startActivity(intent);
			haveMsg[2] = false;
			break;
		}
	}

}
