package com.sobey.cloud.webtv;

import java.util.ArrayList;
import java.util.List;

import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.higgses.griffin.database.GinSqliteDB;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.core.BaseFragment;
import com.sobey.cloud.webtv.obj.Government;
import com.sobey.cloud.webtv.utils.MConfig;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 发现
 * 
 * @author lgx
 *
 */
public class ConvenienceFragment extends BaseFragment {
	private Intent intent;
	private GinSqliteDB dbUtils;
	private List<Government> governments = new ArrayList<Government>();

	/**
	 * 初始化布局Inflater
	 */
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = LayoutInflater.from(getActivity());
		View v = getCacheView(mInflater, R.layout.new_convenience_frame);
		initIcon(v);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (isUseCache()) {
			return;
		}

		dbUtils = GinSqliteDB.create(getActivity());
		try {
			governments = dbUtils.findAll(Government.class);
			Log.v("便民长度", governments.size() + "");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void initIcon(View v) {
//		String colorValue = getResources().getString(R.color.new_green);
//		if ("#ffff0000".equals(colorValue)) {
//			changeColorAndDrawable(v, R.id.wz_chaxun, R.drawable.convenience_cha_red);
//			changeColorAndDrawable(v, R.id.gongjiao, R.drawable.convenience_car_red);
//			changeColorAndDrawable(v, R.id.dache, R.drawable.convenience_dache_red);
//			changeColorAndDrawable(v, R.id.ct_keyun, R.drawable.convenience_chtu_red);
//			changeColorAndDrawable(v, R.id.lc_shike, R.drawable.convenience_lieche_red);
//			changeColorAndDrawable(v, R.id.tz_conter, R.drawable.convenience_tongzhi_red);
//			changeColorAndDrawable(v, R.id.bs_zhinan, R.drawable.convenience_zhinan_red);
//			changeColorAndDrawable(v, R.id.jg_huangy, R.drawable.convenience_suan_red);
//			changeColorAndDrawable(v, R.id.map, R.drawable.convenience_map_red);
//		}
	}

	/**
	 * 改变图标以及字体颜色
	 */
	private void changeColorAndDrawable(View v, int viewId, int imageid) {
		TextView tv = (TextView) v.findViewById(viewId);
		Drawable dbChecked = getResources().getDrawable(imageid);
		dbChecked.setBounds(0, 0, dbChecked.getMinimumWidth(), dbChecked.getMinimumHeight());
		tv.setCompoundDrawables(null, dbChecked, null, null);
	}

	// R.id.user_login,R.id.two_jiaoyi,R.id.fw_zhongjie,R.id.zp_qiuzhi,
	@GinOnClick(id = { R.id.wz_chaxun, R.id.gongjiao, R.id.dache, R.id.ct_keyun, R.id.lc_shike, R.id.tz_zhongxin_lin,
			R.id.bs_zhinan_lin, R.id.jg_huangye_lin, R.id.map, R.id.policy_statute, R.id.certificates_handle })
	public void onclick(View view) {
		switch (view.getId()) {
		// 地图
		case R.id.map:
			// intent=new Intent(getActivity(), BaiDuMapActivity.class);
			intent = new Intent(getActivity(), LinkPageActivity.class);
			intent.putExtra("title", "地图");
			intent.putExtra("url", MConfig.DITU);
			startActivity(intent);
			break;
		// 跳转个人中心
		/*
		 * case R.id.user_login: intent=new Intent(getActivity(),
		 * NewPersonalCenterActivity.class); startActivity(intent); break;
		 */
		// 二手交易
		/*
		 * case R.id.two_jiaoyi: // intent =new Intent(getActivity(),
		 * PropertyManagementActivity.class); intent =new Intent(getActivity(),
		 * LinkPageActivity.class); intent.putExtra("title", "二手交易");
		 * intent.putExtra("url", MConfig.ERSHOU); startActivity(intent); break;
		 */
		// 房屋中介
		/*
		 * case R.id.fw_zhongjie: // intent =new Intent(getActivity(),
		 * HouseAgentActivity.class); intent =new Intent(getActivity(),
		 * LinkPageActivity.class); intent.putExtra("title", "房屋中介");
		 * intent.putExtra("url", MConfig.FANGWU); startActivity(intent); break;
		 */
		// 招聘求职
		/*
		 * case R.id.zp_qiuzhi: // intent =new Intent(getActivity(),
		 * JobRecruitmentActivity.class); intent =new Intent(getActivity(),
		 * LinkPageActivity.class); intent.putExtra("title", "招聘求职");
		 * intent.putExtra("url", MConfig.ZHAOPIN); startActivity(intent);
		 * break;
		 */
		// 违章查询
		case R.id.wz_chaxun:
			// intent =new Intent(getActivity(),
			// QueryTrafficViolationsActivity.class);
			intent = new Intent(getActivity(), LinkPageActivity.class);
			intent.putExtra("title", "违章查询");
			intent.putExtra("url", MConfig.WEIZHANG);
			startActivity(intent);
			break;
		// 公交
		case R.id.gongjiao:
			// intent =new Intent(getActivity(), TransitActivity.class);
			intent = new Intent(getActivity(), LinkPageActivity.class);
			intent.putExtra("title", "公交");
			intent.putExtra("url", MConfig.GONGJIAO);
			startActivity(intent);
			break;
		// 长途客运
		case R.id.ct_keyun:
			// intent =new Intent(getActivity(),
			// PublicTransportOnRoadActivity.class);
			intent = new Intent(getActivity(), LinkPageActivity.class);
			intent.putExtra("title", "长途客运");
			intent.putExtra("url", MConfig.CHANGTU);
			startActivity(intent);
			break;
		// 列车时刻
		case R.id.lc_shike:
			// intent =new Intent(getActivity(),
			// AcceleratedScheduleActivity.class);
			intent = new Intent(getActivity(), LinkPageActivity.class);
			intent.putExtra("title", "列车时刻");
			intent.putExtra("url", MConfig.SHIKE);
			startActivity(intent);
			break;
		// 打车
		case R.id.dache:
			// intent =new Intent(getActivity(), TakingTaxiActivity.class);
			intent = new Intent(getActivity(), LinkPageActivity.class);
			intent.putExtra("title", "打车");
			intent.putExtra("url", MConfig.DACHE);
			startActivity(intent);
			break;
		case R.id.tz_zhongxin_lin:
			// if (governments!=null&&governments.size()>0) {
			// for (Government government :governments) {
			// if (government.getState().equals("3685")) {
			// tiaozhuan(government);
			// }
			// }
			// }else {
			// zhengwu("3685","通知中心");
			// }
			openZhengWuGongGao(MConfig.ZhengWu_ItemID0, getResources().getString(R.string.zhengwu_item0));
			break;
		case R.id.bs_zhinan_lin:
			// if (governments!=null&&governments.size()>0) {
			// for (Government government :governments) {
			// if (government.getState().equals("3687")) {
			// tiaozhuan(government);
			// }
			// }
			// }else {
			// zhengwu("3687","办事指南");
			// }
			openZhengWuGongGao(MConfig.ZhengWu_ItemID1, getResources().getString(R.string.zhengwu_item1));
			break;
		case R.id.jg_huangye_lin:
			// if (governments!=null&&governments.size()>0) {
			// for (Government government :governments) {
			// if (government.getState().equals("3688")) {
			// tiaozhuan(government);
			// }
			// }
			// }else {
			// zhengwu("3688","机关黄页");
			// }

			openZhengWuGongGao(MConfig.ZhengWu_ItemID2, getResources().getString(R.string.zhengwu_item2));
			break;
		case R.id.policy_statute:
			openZhengWuGongGao(MConfig.ZhengWu_ItemID3, getResources().getString(R.string.zhengwu_item3));
			break;
		case R.id.certificates_handle:
			openZhengWuGongGao(MConfig.ZhengWu_ItemID4, getResources().getString(R.string.zhengwu_item4));
			break;
		}
	}

	protected void openZhengWuGongGao(String id, String title) {
		Intent intent = new Intent();
		intent.putExtra("title", title);
		intent.putExtra("ids", id);
		intent.setClass(getActivity(), VideoAndNormalNewsListActivity.class);
		startActivity(intent);
	}

	public void tiaozhuan(Government government) {
		Intent intent0 = new Intent(getActivity(), HomeActivity.class);
		intent0.putExtra("index", government.getIndexs());
		intent0.putExtra("ids", government.getIds());
		intent0.putExtra("state", government.getState());
		intent0.putExtra("title", government.getTitle());
		startActivity(intent0);
	}

	public void zhengwu(String id, String title) {
		for (int i = 0; i < MConfig.CatalogList.size(); i++) {
			if (MConfig.CatalogList.get(i).id.equals(MConfig.ZHENGWUID)) {
				Government government = new Government();
				government.setIds(MConfig.CatalogList.get(i).id + "");
				government.setIndexs(i);
				government.setState(id);
				government.setTitle(title);
				try {
					dbUtils.save(government);
				} catch (Exception e) {
					// TODO: handle exception
				}
				Intent intent0 = new Intent(getActivity(), HomeActivity.class);
				intent0.putExtra("index", i);
				intent0.putExtra("ids", MConfig.CatalogList.get(i).id + "");
				intent0.putExtra("state", id);
				intent0.putExtra("title", title);
				startActivity(intent0);
			}
		}
	}
}
