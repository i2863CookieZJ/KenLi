package com.sobey.cloud.webtv.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import com.appsdk.advancedimageview.AdvancedImageCarousel;
import com.dylan.uiparts.listview.DragListView;
import com.sobey.cloud.webtv.LiveNewsDetailActivity;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.CommonWebActivity2;
import com.sobey.cloud.webtv.adapter.LiveNewsAdapter;
import com.sobey.cloud.webtv.api.HttpInvoke.OnJsonObjectResultListener;
import com.sobey.cloud.webtv.api.News;
import com.sobey.cloud.webtv.fragment.HuiZhouSarft;
import com.sobey.cloud.webtv.obj.CacheData;
import com.sobey.cloud.webtv.obj.ViewHolderGeneralNews;
import com.sobey.cloud.webtv.utils.JsonCache;
import com.sobey.cloud.webtv.utils.MConfig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class LiveNewsUtil extends GeneralNewsHome {


    /**
     * 直播栏目id
     */
    public static ArrayList<String> VideoLiveCatalogIDS = new ArrayList<String>() {
        {
            add(MConfig.VIDEO_LIVE_ID);
        }
    };

    private RelativeLayout loadingMask;
    private Activity mActivity;

    public LiveNewsUtil(int state) {
        super(state);
    }

    @Override
    public void init(Context context, String catalogId, String catalogName, DragListView listView, BaseAdapter adapter,
                     AdvancedImageCarousel imageCarousel, ImageView imageCarouselBottomViewIcon,
                     TextView imageCarouselBottomViewTitle) {
        mContext = context;
        mCatalogId = catalogId;
        mCatalogName = catalogName;
        mListView = listView;
        mAdapter = adapter;
        mImageCarousel = imageCarousel;
        mImageCarouselBottomViewIcon = imageCarouselBottomViewIcon;
        mImageCarouselBottomViewTitle = imageCarouselBottomViewTitle;
        if (mImageCarouselBottomViewTitle != null) {
            mImageCarouselBottomViewTitle.invalidate();
        }

        inflater = LayoutInflater.from(mContext);

        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(false);
        mListView.setListener(this);
        mListView.setHeaderColor(0xffffffff);
        mListView.setHeaderDividersEnabled(false);
        mListView.setFooterBackgroundColor(0xffffffff);
        mListView.setBackgroundColor(0xffffffff);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        mAdapter = new LiveNewsAdapter(mArticles, mContext, catalogId, catalogName, width);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position - mListView.getHeaderViewsCount();
                Log.v("pos", pos + "");
                JSONObject obj = mArticles.get(pos);
                try {
                    obj.put("parentid", mCatalogId);
                    openDetailActivity(obj, position, mCatalogId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mListView.setAdapter(mAdapter);
        mListView.setAsOuter();

        mPageIndex = -1;
        mArticles.clear();
        mAdapter.notifyDataSetChanged();
        if (mImageCarousel != null) {
            mImageCarousel.removeAllCarouselView();
            mImageCarousel.setVisibility(View.GONE);
            mImageCarousel.setIntervalTime(mImageCarouselIntervalTime);
        }
        loadingMask.setVisibility(View.VISIBLE);
        loadMore(mCatalogId, mPageIndex);
    }

    protected void loadViewHolder(int position, View convertView, ViewHolderGeneralNews viewHolderGeneralNews) {
        viewHolderGeneralNews = (ViewHolderGeneralNews) convertView.getTag();
        viewHolderGeneralNews.getPicture_image1().clear();
        viewHolderGeneralNews.getPicture_image2().clear();
        viewHolderGeneralNews.getPicture_image3().clear();
        viewHolderGeneralNews.getThumbnail().clear();
        try {
            int pos = position;// -mListView.getHeaderViewsCount();
            JSONObject obj = mArticles.get(pos);
            switch (Integer.valueOf(obj.getString("type"))) {
                case MConfig.TypePicture:
                    viewHolderGeneralNews.getPictureLayout().setVisibility(View.VISIBLE);
                    viewHolderGeneralNews.getNormalLayout().setVisibility(View.GONE);
                    viewHolderGeneralNews.getPicture_title().setText(obj.getString("title"));
                    viewHolderGeneralNews.getPicture_comments().setText(obj.getString("commcount"));
                    viewHolderGeneralNews.getPicture_refername().setText(obj.getString("refername"));
                    JSONArray pictureArray = obj.getJSONArray("content");
                    int pictureSum = pictureArray.length();
                    viewHolderGeneralNews.getPicture_image1().setNetImage(obj.getString("logo"));
                    if (pictureSum > 1) {
                        viewHolderGeneralNews.getPicture_image2()
                                .setNetImage(((JSONObject) pictureArray.get(0)).getString("filepath"));
                        viewHolderGeneralNews.getPicture_image3()
                                .setNetImage(((JSONObject) pictureArray.get(1)).getString("filepath"));
                        viewHolderGeneralNews.getPicture_comments().setVisibility(View.GONE);
                        convertView.findViewById(R.id.commentContainer).setVisibility(View.GONE);
                    }
                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    int width = (int) ((metrics.widthPixels - 50.0) / 3.0);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 3.0 / 4.0));
                    params.setMargins(3, 3, 3, 3);
                    break;
                default:
                    viewHolderGeneralNews.getPictureLayout().setVisibility(View.GONE);
                    viewHolderGeneralNews.getNormalLayout().setVisibility(View.VISIBLE);
                    viewHolderGeneralNews.getTitle().setText(obj.getString("title"));
                    viewHolderGeneralNews.getSummary().setText(obj.getString("summary"));
                    viewHolderGeneralNews.getComments().setText(obj.getString("commcount"));
                    viewHolderGeneralNews.getThumbnail().setNetImage(obj.getString("logo"));
                    if (obj.getString("attribute").contains("video")
                            || Integer.valueOf(obj.getString("type")) == MConfig.TypeVideo) {
                        viewHolderGeneralNews.getType().setImageResource(R.drawable.type_video_icon);
                        viewHolderGeneralNews.getType().setVisibility(View.VISIBLE);
                    } else {
                        viewHolderGeneralNews.getType().setVisibility(View.GONE);
                    }
                    break;
            }
        } catch (Exception e) {
            convertView = null;
            e.printStackTrace();
        }
    }

    public LiveNewsUtil(RelativeLayout mask, Activity activity) {
        loadingMask = mask;
        mActivity = activity;
    }

    public void openDetailActivity(JSONObject information, int index, String parentID) {
        HuiZhouSarft.disposeVideoComponent(mContext);
        if (MConfig.LIVE_360.contains(mCatalogId) || MConfig.LIVE_ACTIVITY.contains(mCatalogId)) {
            // Intent intent1 = new Intent(mContext, CommonWebActivity.class);
            // Bundle bundle = new Bundle();
            // try {
            // bundle.putString("title", "Title");
            // bundle.putString("url", obj.getString("url"));
            // intent1.putExtras(bundle);
            // mActivity.startActivity(intent1);
            // } catch (JSONException e) {
            // }

            Intent intent1 = new Intent(mContext, CommonWebActivity2.class);
            intent1.putExtra("title", mCatalogName);
            intent1.putExtra("information", information.toString());
            mActivity.startActivity(intent1);
        } else {
            HuiZhouSarft.disposeVideoComponent(mContext);

            String string = "{\"id\":\"" + information.optString("id", "") + "\",\"parentid\":\""
                    + parentID + "\"}";
            Intent intent = new Intent();
            intent.putExtra("information", string);
            intent.putExtra("liveMark", "0");
            intent.setClass(mContext, LiveNewsDetailActivity.class);
            mContext.startActivity(intent);

//            if (VideoLiveCatalogIDS.indexOf(parentID) != -1) {// 视频直播
//                String string = "{\"id\":\"" + information.optString("id", "") + "\",\"parentid\":\""
//                        + parentID + "\"}";
//                Intent intent = new Intent();
//                intent.putExtra("information", string);
//                intent.putExtra("liveMark", "0");
//                intent.setClass(mContext, LiveNewsDetailActivity.class);
//                mContext.startActivity(intent);
//            } else {// 音频直播
//                String string = "{\"id\":\"" + information.optString("id", "") + "\",\"parentid\":\"" + parentID
//                        + "\",\"title\":\"" + information.optString("title") + "\"}";
//                Intent intent = new Intent();
//                intent.putExtra("information", string);
//                intent.putExtra("index", index);
//                intent.putExtra("liveMark", "-1");
//                intent.setClass(mContext, LiveNewsDetailActivity2.class);
//                mContext.startActivity(intent);
//            }
        }
    }

    @Override
    protected void loadMore(final String catalogId, final int pageIndex) {
        isLoading = true;
        News.getArticleList(0, catalogId, mPageSize, (pageIndex == -1 ? 1 : pageIndex), mContext,
                new OnJsonObjectResultListener() {
                    @Override
                    public void onOK(JSONObject result) {
                        try {
                            ((LiveNewsAdapter) mAdapter).filterLen = 0;
                            int total = result.getInt("total");
                            JSONArray array = result.getJSONArray("articles");
                            if (pageIndex <= 1) {
                                mArticles.clear();
                            }
                            for (int i = 0; i < array.length(); i++) {
                                mArticles.add(array.getJSONObject(i));
                            }
                            ((LiveNewsAdapter) mAdapter).filterImageListNews();
                            mAdapter.notifyDataSetChanged();
                            if (state == 1) {

                            } else {
                                // setNetImageBanner();
                            }
                            if (mArticles.size() >= total - ((LiveNewsAdapter) mAdapter).filterLen) {
                                mListView.setPullLoadEnable(false);
                            } else {
                                mListView.setPullLoadEnable(true);
                            }
                            mListView.setPullRefreshEnable(true);
                            mListView.stopRefresh();
                            mListView.stopLoadMore();
                            mCacheDatas
                                    .add(new CacheData((pageIndex == -1 ? 1 : pageIndex), catalogId, mArticles, total));
                            if (pageIndex == -1) {
                                JsonCache.getInstance().set(catalogId, "list", result);
                            }
                        } catch (Exception e) {
                            mListView.setPullRefreshEnable(true);
                            mListView.stopRefresh();
                            mListView.stopLoadMore();
                        } finally {
                            if (mCatalogId == catalogId) {
                                loadingMask.setVisibility(View.GONE);
                            }
                            isLoading = false;
                        }
                    }

                    @Override
                    public void onNG(String reason) {
                        mListView.setPullRefreshEnable(true);
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                        isLoading = false;
                        if (mCatalogId == catalogId) {
                            loadingMask.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancel() {
                        mListView.setPullRefreshEnable(true);
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                        isLoading = false;
                        if (mCatalogId == catalogId) {
                            loadingMask.setVisibility(View.GONE);
                        }
                    }
                });
    }

}
