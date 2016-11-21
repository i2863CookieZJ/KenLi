package com.sobey.cloud.webtv.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.dylan.common.utils.CheckNetwork;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.GinInjector;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobey.cloud.webtv.CommonWebActivity2;
import com.sobey.cloud.webtv.LiveNewsDetailActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo.State;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 直播
 *
 * @author lazy
 */
public class LiveNewsAdapter extends RegularNewsAdapter {


    /**
     * 直播栏目id
     */
    public static ArrayList<String> VideoLiveCatalogIDS = new ArrayList<String>() {
        {
            add(MConfig.VIDEO_LIVE_ID);
        }
    };

    public int filterLen = 0;
    protected List<JSONObject> dataList;

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public LiveNewsAdapter(List<JSONObject> list, Context context, String parentid, String parentName, int width) {
        super();
        this.dataList = list;
        this.parentid = parentid;
        this.parentName = parentName;
        this.width = width;
        this.context = context;
        inflater = LayoutInflater.from(context);
        filterImageListNews();
    }

    public void filterImageListNews() {
        for (JSONObject item : dataList) {
            if ((MConfig.TypePicture + "").equals(item.optString("type"))) {
                JSONArray jsonArray = item.optJSONArray("content");
                if (jsonArray.length() == 0) {
                    dataList.remove(item);
                    filterLen++;
                    filterImageListNews();
                    break;
                }
            }

        }
    }

    public void setAdaptorData(List<JSONObject> list, String parentid, int width) {
        this.dataList = list;
        this.parentid = parentid;
        this.width = width;
        filterImageListNews();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_news_live, null);
            GinInjector.manualInjectView(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        setViewData(holder, position, convertView);
        return convertView;
    }

    public void setViewData(Holder holder, final int position, View convertView) {
        SharedPreferences settings = context.getSharedPreferences("settings", 0);
        CheckNetwork network = new CheckNetwork(context);
        boolean isShowPicture = (settings.getInt("show_picture", 1) == 1 ? true : false)
                || network.getWifiState(false) == State.CONNECTED;
        holder.title.setText(dataList.get(position).optString("title").trim());
        holder.timeTv.setText(dataList.get(position).optString("publishdate"));

        if (MConfig.LIVE_360.contains(parentid) || MConfig.LIVE_ACTIVITY.contains(parentid)) {
            holder.liveTypeIv.setImageResource(R.drawable.ic_home_live_360);
        } else {
            holder.liveTypeIv.setImageResource(R.drawable.ic_home_live_sobey);
        }

        if (isShowPicture) {
            ImageLoader.getInstance().displayImage(dataList.get(position).optString("logo"), holder.liveBannerIv);
        }

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject(dataList.get(position).toString());
                    jsonObject.put("parentid", parentid);
                    openDetailActivity(jsonObject,position,parentid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class Holder {
        @GinInjectView(id = R.id.title)
        public TextView title;
        @GinInjectView(id = R.id.live_type_iv)
        public ImageView liveTypeIv;
        @GinInjectView(id = R.id.time_tv)
        public TextView timeTv;
        @GinInjectView(id = R.id.live_banner_iv)
        public AdvancedImageView liveBannerIv;
        @GinInjectView(id = R.id.itemBottomLine)
        public View itemBottomLine;
    }

    public void openDetailActivity(JSONObject information, int index, String parentID) {
        HuiZhouSarft.disposeVideoComponent(context);
        if (MConfig.LIVE_360.contains(parentid) || MConfig.LIVE_ACTIVITY.contains(parentid)) {
            // Intent intent1 = new Intent(mContext, CommonWebActivity.class);
            // Bundle bundle = new Bundle();
            // try {
            // bundle.putString("title", "Title");
            // bundle.putString("url", obj.getString("url"));
            // intent1.putExtras(bundle);
            // mActivity.startActivity(intent1);
            // } catch (JSONException e) {
            // }

            Intent intent1 = new Intent(context, CommonWebActivity2.class);
            intent1.putExtra("title", parentName);
            intent1.putExtra("information", information.toString());
            context.startActivity(intent1);
        } else {
            HuiZhouSarft.disposeVideoComponent(context);

            String string = "{\"id\":\"" + information.optString("id", "") + "\",\"parentid\":\""
                    + parentID + "\"}";
            Intent intent = new Intent();
            intent.putExtra("information", string);
            intent.putExtra("liveMark", "0");
            intent.setClass(context, LiveNewsDetailActivity.class);
            context.startActivity(intent);

//            if (VideoLiveCatalogIDS.indexOf(parentID) != -1) {// 视频直播
//                String string = "{\"id\":\"" + information.optString("id", "") + "\",\"parentid\":\""
//                        + MConfig.VIDEO_LIVE_ID + "\"}";
//                Intent intent = new Intent();
//                intent.putExtra("information", string);
//                intent.putExtra("liveMark", "0");
//                intent.setClass(context, LiveNewsDetailActivity.class);
//                context.startActivity(intent);
//            } else {// 音频直播
//                String string = "{\"id\":\"" + information.optString("id", "") + "\",\"parentid\":\"" + parentID
//                        + "\",\"title\":\"" + information.optString("title") + "\"}";
//                Intent intent = new Intent();
//                intent.putExtra("information", string);
//                intent.putExtra("index", index);
//                intent.putExtra("liveMark", "-1");
//                intent.setClass(context, LiveNewsDetailActivity2.class);
//                context.startActivity(intent);
//            }
        }
    }
}