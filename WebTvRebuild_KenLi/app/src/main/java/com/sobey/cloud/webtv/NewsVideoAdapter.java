package com.sobey.cloud.webtv;

import java.util.List;

import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.kenli.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewsVideoAdapter extends VideoAdapter {

	public NewsVideoAdapter(List<JSONObject> list, Context context) {
		super(list, context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.item_news_viedio, null);
			GinInjector.manualInjectView(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.content.setText(list.get(position).optString("commcount").trim());
		holder.title.setText(list.get(position).optString("title").trim());
		holder.summary.setText(formatString(list.get(position).optString("summary").trim(), 25));
		// 改成新的图片框架
		ImageLoader.getInstance().displayImage(list.get(position).optString("logo").trim(), holder.image);
		// holder.image.setNetImage(list.get(position).optString("logo"));
		holder.bf_count.setText(list.get(position).optString("hitcount"));
		holder.bf_time.setText(list.get(position).optString("duration"));
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

	private String formatString(String stringToFormat, int maxLenth) {
		if (stringToFormat.length() > maxLenth) {
			stringToFormat = stringToFormat.substring(0, maxLenth - 1) + "...";
		}
		return stringToFormat;
	}

	class Holder {
		@GinInjectView(id = R.id.summary)
		public TextView summary;
		@GinInjectView(id = R.id.title)
		protected TextView title;
		@GinInjectView(id = R.id.pl_count)
		protected TextView content;
		@GinInjectView(id = R.id.image)
		protected AdvancedImageView image;
		@GinInjectView(id = R.id.bf_time)
		protected TextView bf_time;
		@GinInjectView(id = R.id.bf_count)
		protected TextView bf_count;
	}

}
