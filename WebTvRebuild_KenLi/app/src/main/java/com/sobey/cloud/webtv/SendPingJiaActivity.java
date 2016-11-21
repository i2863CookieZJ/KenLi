package com.sobey.cloud.webtv;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.PreferencesUtil;
import com.sobey.cloud.webtv.volley.VolleyListener;
import com.sobey.cloud.webtv.volley.VolleyRequset;
import com.sobey.cloud.webtv.widgets.CustomTitleView;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class SendPingJiaActivity extends BaseActivity {

	@GinInjectView(id = R.id.ac_sendpj_header_ctv)
	private CustomTitleView mHeaderCtv;
	@GinInjectView(id = R.id.ac_sendpj_shopicon)
	private ImageView shopIcon;
	@GinInjectView(id = R.id.ac_sendpj_pingfen)
	private TextView pingFenTv;
	@GinInjectView(id = R.id.ac_sendpj_pingfenstar)
	private RatingBar pingfenStar;
	@GinInjectView(id = R.id.ac_sendpj_comment)
	private EditText commentEt;
	@GinInjectView(id = R.id.ac_sendpj_numberlimit)
	private TextView numberLimitTv;
	@GinInjectView(id = R.id.ac_sendpj_send)
	private Button sendBtn;

	@Override
	public int getContentView() {
		return R.layout.activity_send_pingjia;
	}

	@Override
	public void onDataFinish(Bundle savedInstanceState) {
		super.onDataFinish(savedInstanceState);
		mHeaderCtv.setTitle("商品评价");
		String goodInfo = getIntent().getStringExtra("goodList");
		try {
			JSONArray jsonArray = new JSONArray(goodInfo);
			final JSONObject jObject = jsonArray.getJSONObject(0);
			Picasso.with(this).load(MConfig.ORDER_PIC_HEAD + jObject.getString("goods_img"))
					.error(R.drawable.default_thumbnail_banner).placeholder(R.drawable.default_thumbnail_banner)
					.into(shopIcon);
			getCommentInfo(jObject.getString("comment_id"));
			commentEt.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					numberLimitTv.setText(s.length() + "/500");
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
			sendBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						sendComment(jObject.getString("comment_id"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getCommentInfo(String commentId) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("action", "getOrderComment");
		param.put("comment_id", commentId);
		VolleyRequset.doPost(this, MConfig.myTieziApiUrl, "getCommentInfo", param, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				try {
					JSONObject result = new JSONObject(arg0);
					if ("2000".equals(result.get("code"))) {
						JSONObject resultJson = result.getJSONObject("data");
						// ping.setText("评分:" + resultJson.getString("point"));
						if ("1".equals(resultJson.getString("status"))) {// 已经评价了
							sendBtn.setVisibility(View.GONE);
							pingfenStar.setRating(Float.valueOf(resultJson.getString("point")));
							pingfenStar.setEnabled(false);
							commentEt.setText(resultJson.getString("contents"));
							numberLimitTv.setText(resultJson.getString("contents").length() + "/500");
							commentEt.setEnabled(false);
						} else {
							sendBtn.setVisibility(View.VISIBLE);
							commentEt.setEnabled(true);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(SendPingJiaActivity.this, "获取评论失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {

			}
		});
	}

	private void sendComment(String commentId) throws JSONException {
		String content = commentEt.getText().toString();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(this, "评论内容为空", Toast.LENGTH_SHORT).show();
			return;
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("action", "orderComment");
		param.put("comment_id", commentId);
		param.put("uid", PreferencesUtil.getLoggedUserId());
		param.put("point", "" + (int) pingfenStar.getRating());
		param.put("content", content);
		VolleyRequset.doPost(this, MConfig.myTieziApiUrl, "sendComment", param, new VolleyListener() {

			@Override
			public void onSuccess(String arg0) {
				try {
					JSONObject result = new JSONObject(arg0);
					if ("2000".equals(result.get("code"))) {
						Toast.makeText(SendPingJiaActivity.this, "感谢您的评价", Toast.LENGTH_SHORT).show();
						sendBtn.setVisibility(View.GONE);
						commentEt.setEnabled(false);
						pingfenStar.setEnabled(false);
						finishActivity();
					} else {
						Toast.makeText(SendPingJiaActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Toast.makeText(SendPingJiaActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFail(VolleyError arg0) {
				Toast.makeText(SendPingJiaActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub

			}
		});
	}

}
