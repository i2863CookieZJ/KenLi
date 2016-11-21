package com.sobey.cloud.webtv.widgets;


import com.sobey.cloud.webtv.kenli.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyAlertDialog extends Dialog implements android.view.View.OnClickListener{

	private int layoutResID = -1;
	private int type;//0-默认对话框  1-地图页对话框
	private View mView;
	private String mTitle,mConfirmText,mCancelText;
	public MyAlertDialog(Context context,int layoutResID,int type) {
		super(context);
		this.layoutResID = layoutResID;
		this.type = type;
	}
	public MyAlertDialog(Context context,View view,int type) {
		super(context);
		this.mView = view;
		this.type = type;
	}
	public MyAlertDialog(Context context,String title,String confirmText,String cancelText) {
		super(context);
		this.mTitle = title;
		this.mConfirmText = confirmText;
		this.mCancelText = cancelText;
		type = 0;
	}
	public MyAlertDialog(Context context,String title,String confirmText,
				String cancelText,int layoutResId) {
		super(context);
		this.mTitle = title;
		this.mConfirmText = confirmText;
		this.mCancelText = cancelText;
		this.layoutResID = layoutResId;
		type = 0;
	}
	private MyOnClickListener clickListener;
	private View alertDialogView;
	private EditText editText;
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature( Window.FEATURE_NO_TITLE );
		this.getWindow().getDecorView().setBackgroundColor(android.R.color.transparent);
		if(null != mView){
			setContentView(mView);
		}else if(-1 != layoutResID){
			setContentView(layoutResID);
		}else{
			setContentView(R.layout.dialog_layout_for_turnpage);
		}
		alertDialogView = this.getWindow().getDecorView();
		initViews();
	}
	private void initViews(){
		if(type == 0){
			titleTv = (TextView) alertDialogView.findViewById(R.id.my_dialog_title_tv);
			Button doActionBtn = (Button) alertDialogView.findViewById(R.id.my_dialog_do_action_btn);
			Button cancelBtn = (Button) alertDialogView.findViewById(R.id.my_dialog_do_cancel_btn);
			editText = (EditText) alertDialogView.findViewById(R.id.my_dialog_edit_text);
			pageTips = (TextView) alertDialogView.findViewById(R.id.my_dialog_page_tips);
			doActionBtn.setOnClickListener(this);
			cancelBtn.setOnClickListener(this);
			
			if(!TextUtils.isEmpty(mTitle)){
				titleTv.setText(mTitle);
			}
			if(!TextUtils.isEmpty(mConfirmText)){
				doActionBtn.setText(mConfirmText);
			}
			if(!TextUtils.isEmpty(mCancelText)){
				cancelBtn.setText(mCancelText);
			}
		}
	}
	public String getInputText(){
		if(null != editText){
			return editText.getText().toString();
		}
		return "";
	}
	public EditText getEidtText(){
		return editText;
	}
	public void setPageTipsText(String text){
		if(null != pageTips){
			pageTips.setText(text);
		}
	}
	public void setTitle(String text){
		if(null != titleTv){
				titleTv.setText(text);
			}
	}
	private TextView pageTips;
	private TextView titleTv;
	public interface MyOnClickListener{
		void onConfirm();
		void onCancel();
		void onClick(View view);
	}
	public void setMyOnClickListener(MyOnClickListener clickListener){
		this.clickListener = clickListener;
	}
	@Override
	public void onClick(View v) {
		if(type == 0){
			switch (v.getId()) {
			case R.id.my_dialog_do_cancel_btn:
				clickListener.onCancel();
				break;
			case R.id.my_dialog_do_action_btn:
				clickListener.onConfirm();
				break;
			default:
				break;
			}
		}else if(type == 1){
			clickListener.onClick(v);
		}
	}
}
