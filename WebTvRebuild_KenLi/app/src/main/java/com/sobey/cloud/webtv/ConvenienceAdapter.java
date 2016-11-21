package com.sobey.cloud.webtv;

import java.util.List;

import org.json.JSONObject;

import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 便民服务的adapter
 * 
 * @author lgx
 *
 */

public class ConvenienceAdapter extends BaseAdapter {
	private List<JSONObject> list;
	private LayoutInflater inflater;

	private Context context;

	public ConvenienceAdapter(List<JSONObject> list, Context context) {
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_convenience, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.content.setText(list.get(position).optString("summary"));
		holder.title.setText(list.get(position).optString("title"));
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openDetailActivity(Integer.valueOf(list.get(position).optString("type")),
						list.get(position).toString());
			}
		});
		return convertView;
	}

	class ViewHolder {
		@GinInjectView(id = R.id.title)
		private TextView title;
		@GinInjectView(id = R.id.content)
		private TextView content;
	}

	public void openDetailActivity(int type, String information) {
		switch (type) {
		case MConfig.TypePicture:
			Intent intent = new Intent(context, PhotoNewsDetailActivity.class);
			intent.putExtra("information", information);
			// if (!TextUtils.isEmpty(mVoteInformation)) {
			// intent.putExtra("vote", mVoteInformation);
			// }
			context.startActivity(intent);
			break;
		case MConfig.TypeVideo:
			HuiZhouSarft.disposeVideoComponent(context);
			Intent intent1 = new Intent(context, VideoNewsDetailActivity.class);
			intent1.putExtra("information", information);
			// if (!TextUtils.isEmpty(mVoteInformation)) {
			// intent1.putExtra("vote", mVoteInformation);
			// }
			context.startActivity(intent1);
			break;
		case MConfig.TypeNews:
			Intent intent2 = new Intent(context, GeneralNewsDetailActivity.class);
			intent2.putExtra("information", information);
			// if (!TextUtils.isEmpty(mVoteInformation)) {
			// intent2.putExtra("vote", mVoteInformation);
			// }
			context.startActivity(intent2);
			break;
			default:
			HuiZhouSarft.disposeVideoComponent(context);
			Intent intent4 = new Intent(context, VideoNewsDetailActivity.class);
			intent4.putExtra("information", information);
			// if (!TextUtils.isEmpty(mVoteInformation)) {
			// intent1.putExtra("vote", mVoteInformation);
			// }
			context.startActivity(intent4);
			break;
		}
	}

}
