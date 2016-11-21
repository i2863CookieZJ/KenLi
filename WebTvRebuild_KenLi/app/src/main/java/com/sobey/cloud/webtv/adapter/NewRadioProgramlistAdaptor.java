package com.sobey.cloud.webtv.adapter;

import java.util.List;

import com.dylan.common.utils.DateParse;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.NewRadioLiveChannelListview;
import com.sobey.cloud.webtv.app.ExceptionApplication;
import com.sobey.cloud.webtv.senum.ProgramListItem;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 新的音频节目单列表适配器
 * 
 * @author zouxudong
 *
 */
public class NewRadioProgramlistAdaptor extends BaseAdapter implements OnItemClickListener {

	private List<ProgramListItem> currentProgramList;
	private LayoutInflater inflater;
	public static boolean seekFlag;
	/**
	 * 日期索引
	 */
	private static int dateChooseIndex;
	public Handler handler;
	protected boolean supportTimeShift;
	protected int today;
	/**
	 * 选中的列表所引
	 */
	public static int lastChooseIndex = -1;
	/**
	 * 点击时当前的日期索引
	 */
	public static int currentDateIndex = -1;

	public NewRadioProgramlistAdaptor(List<ProgramListItem> args, LayoutInflater inflater, int chooseIndex,
			boolean supportTimeShift, int today) {
		currentProgramList = args;
		this.inflater = inflater;
		dateChooseIndex = chooseIndex;
		this.supportTimeShift = supportTimeShift;
		this.today = today;
	}

	@Override
	public int getCount() {
		return currentProgramList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return currentProgramList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		Holder holder;
		ProgramListItem item = currentProgramList.get(index);
		if (view == null) {
			view = inflater.inflate(R.layout.item_newradio_programlist, null);
			holder = new Holder();
			GinInjector.manualInjectView(holder, view);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		updateItemData(holder, item, view, index);
		return view;
	}

	@SuppressWarnings("deprecation")
	private void updateItemData(Holder holder, ProgramListItem item, View view, int position) {
		String time = DateParse.getDate(0, 0, 0, 0, item.getStarttime(), null, "HH:mm");
		holder.timeInfo.setText(time);
		holder.programInfo.setText(item.getName());
		Drawable playIcon = ExceptionApplication.app.getResources().getDrawable(R.drawable.radio_play_gray);
		holder.playMark.setImageDrawable(playIcon);
		Drawable drawable = ExceptionApplication.app.getResources().getDrawable(R.drawable.radio_item_white_bg);
		view.setBackgroundDrawable(drawable);
		switch (ProgramListItem.getItemState(item)) {
		case REPLAY:
			holder.timeInfo.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.black));
			holder.programInfo.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.black));
			if (supportTimeShift) {
				holder.playMark.setVisibility(View.VISIBLE);
				if (dateChooseIndex == currentDateIndex && position == lastChooseIndex) {
					holder.timeInfo.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.white));
					holder.programInfo.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.white));
					holder.playMark.setVisibility(View.VISIBLE);
					drawable = ExceptionApplication.app.getResources().getDrawable(R.drawable.huizhousarft_rectshape);
					view.setBackgroundDrawable(drawable);
					playIcon = ExceptionApplication.app.getResources().getDrawable(R.drawable.radio_play_white);
					holder.playMark.setImageDrawable(playIcon);
				}
			} else {
				holder.playMark.setVisibility(View.GONE);
			}
			break;
		case COMING_SOON:
			holder.timeInfo.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.black));
			holder.programInfo.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.black));
			holder.playMark.setVisibility(View.GONE);
			break;
		case LIVE:
			holder.timeInfo.setText("正在播放");
			if (!seekFlag) {
				holder.timeInfo.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.white));
				holder.programInfo.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.white));
				holder.playMark.setVisibility(View.VISIBLE);
				drawable = ExceptionApplication.app.getResources().getDrawable(R.drawable.huizhousarft_rectshape);
				view.setBackgroundDrawable(drawable);
				playIcon = ExceptionApplication.app.getResources().getDrawable(R.drawable.radio_play_white);
				holder.playMark.setImageDrawable(playIcon);
			} else {
				holder.timeInfo
						.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.home_tab_text_focus));
				holder.programInfo
						.setTextColor(ExceptionApplication.app.getResources().getColor(R.color.home_tab_text_focus));
				holder.playMark.setVisibility(View.VISIBLE);
				playIcon = ExceptionApplication.app.getResources()
						.getDrawable(R.drawable.livenewsdetail_guide_play_icon);
				holder.playMark.setImageDrawable(playIcon);
			}

			break;
		}
	}

	class Holder {
		@GinInjectView(id = R.id.timeInfo)
		private TextView timeInfo;
		@GinInjectView(id = R.id.programInfo)
		private TextView programInfo;
		@GinInjectView(id = R.id.playMark)
		private ImageView playMark;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
		// Holder holder=(Holder)view.getTag();
		ProgramListItem.Msg data = new ProgramListItem.Msg();
		ProgramListItem item = currentProgramList.get(index);
		data.item = item;
		data.index = index;
		Message message = new Message();
		message.obj = data;
		switch (ProgramListItem.getItemState(item)) {
		case COMING_SOON:
			break;
		case LIVE:
			if (seekFlag) {
				message.what = NewRadioLiveChannelListview.CHOOSE_ITEM_LIVE;
				seekFlag = false;
				lastChooseIndex = -1;
				handler.sendMessage(message);
				notifyDataSetChanged();
			}
			break;
		case REPLAY:
			if (supportTimeShift && lastChooseIndex != index) {
				message.what = NewRadioLiveChannelListview.CHOOSE_ITEM_REPLAY;
				seekFlag = true;
				lastChooseIndex = index;
				handler.sendMessage(message);
				notifyDataSetChanged();
				currentDateIndex = dateChooseIndex;
			}
			break;
		}
	}

}
