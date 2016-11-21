package com.sobey.cloud.webtv.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.appsdk.advancedimageview.AdvancedImageView;
import com.higgses.griffin.annotation.app.GinInjectView;
import com.higgses.griffin.annotation.app.event.GinOnClick;
import com.higgses.griffin.database.GinSqliteDB;
import com.sobey.cloud.webtv.kenli.R;
import com.sobey.cloud.webtv.ConvenienceFragment;
import com.sobey.cloud.webtv.VolunteerFragment;
import com.sobey.cloud.webtv.broadcast.ECShopBroadReciver;
import com.sobey.cloud.webtv.core.BaseActivity;
import com.sobey.cloud.webtv.obj.Government;
import com.sobey.cloud.webtv.utils.MConfig;
import com.sobey.cloud.webtv.utils.VersionCheck;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WebPageFragment extends BaseActivity implements ECShopBroadReciver.ReciveHandle {

    @SuppressLint("UseSparseArrays")
    private Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();
    private FragmentTransaction ftd;
    @GinInjectView(id = R.id.home_page)
    private TextView home_page;
    @GinInjectView(id = R.id.home_group)
    private TextView home_group;
    @GinInjectView(id = R.id.ebusiness)
    private TextView ebusiness;
    @GinInjectView(id = R.id.ebusiness_icon)
    private ImageView ebusinessIcon;
    @GinInjectView(id = R.id.f_posting)
    private TextView f_posting;
    @GinInjectView(id = R.id.convenience)
    private TextView convenience;
    @GinInjectView(id = R.id.my_profile)
    private TextView my_profile;
    @GinInjectView(id = R.id.zhiyuanzhe)
    private TextView zhiyuanzhe;
    @GinInjectView(id = R.id.quanzi)
    private TextView quanzi;
    @GinInjectView(id = R.id.bottomGuideBar)
    private LinearLayout bottomGuideBar;

    @GinInjectView(id = R.id.home_page_iv)
    private ImageView home_page_iv;

    @GinInjectView(id = R.id.quanzi_iv)
    private ImageView quanzi_iv;

    @GinInjectView(id = R.id.convenience_iv)
    private ImageView convenience_iv;

    @GinInjectView(id = R.id.my_profile_iv)
    private ImageView my_profile_iv;

    private HomePageFragment homePageFragment;
    private ActivitiesFragment groupFrament;
    private NewPostingFragment newPostingFragment;
    private QuanziFrament quanziFrament;
    private ConvenienceFragment convenienceFragment;
    private EBusinessFragment eBusinessFragment;
    private VolunteerFragment zhiyuanzheFragment;
    private MyProfile myProfileFragment;
    private boolean isExit = false;
    // private Handler handler;
    private GinSqliteDB dbUtils;

    private ECShopBroadReciver broadcastReciver;

    private int currentFragmentIndex;

    @Override
    public int getContentView() {
        // TODO Auto-generated method stub
        return R.layout.new_main_frane;
    }

    @Override
    public void onDataFinish(Bundle savedInstanceState) {
        super.onDataFinish(savedInstanceState);
        broadcastReciver = new ECShopBroadReciver();
        broadcastReciver.handle = this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ECShopBroadReciver.ECSHOP_BROAD);
        registerReceiver(broadcastReciver, intentFilter);

        dbUtils = GinSqliteDB.create(this);
        ftd = getSupportFragmentManager().beginTransaction();
        homePageFragment = new HomePageFragment();
        map.put(0, homePageFragment);
        ftd.add(R.id.content_fragment, homePageFragment);
        ftd.commit();

        setCurrentFragment(0);
        VersionCheck versionCheck = new VersionCheck();
        versionCheck.check(this);
        changeColorAndDrawable(0);

        if (Build.VERSION.SDK_INT >= 23) {
            checkSDCardPermission();
        }
    }

    /**
     * 切换视图
     */
    public void setCurrentFragment(int index) {
        currentFragmentIndex = index;
        bottomGuideBar.setVisibility(View.VISIBLE);
        ftd = getSupportFragmentManager().beginTransaction();
        switch (index) {
            case 0:
                if (homePageFragment == null) {
                    homePageFragment = new HomePageFragment();
                    ftd.add(R.id.content_fragment, homePageFragment);
                    map.put(0, homePageFragment);
                }
                break;

            case 1:
                if (groupFrament == null) {
                    groupFrament = new ActivitiesFragment();
                    ftd.add(R.id.content_fragment, groupFrament);
                    map.put(1, groupFrament);
                }
                break;
            case 2:
                if (newPostingFragment == null) {
                    newPostingFragment = new NewPostingFragment();
                    ftd.add(R.id.content_fragment, newPostingFragment);
                    map.put(2, newPostingFragment);
                }
                break;
            case 3:
                if (convenienceFragment == null) {
                    convenienceFragment = new ConvenienceFragment();
                    ftd.add(R.id.content_fragment, convenienceFragment);
                    map.put(3, convenienceFragment);
                }
                break;
            case 4:
                if (eBusinessFragment == null) {
                    eBusinessFragment = new EBusinessFragment();
                    ftd.add(R.id.content_fragment, eBusinessFragment);
                    map.put(4, eBusinessFragment);
                }
                break;
            case 5:
                if (myProfileFragment == null) {
                    myProfileFragment = new MyProfile();
                    ftd.add(R.id.content_fragment, myProfileFragment);
                    map.put(5, myProfileFragment);
                }
                // Intent intent=new Intent(this, PersonalCenterActivity.class);
                // startActivity(intent);
                break;
            case 6:
                if (zhiyuanzheFragment == null) {
                    zhiyuanzheFragment = new VolunteerFragment();
                    ftd.add(R.id.content_fragment, zhiyuanzheFragment);
                    map.put(6, zhiyuanzheFragment);
                }
                break;
            case 7:
                if (quanziFrament == null) {
                    quanziFrament = new QuanziFrament();
                    ftd.add(R.id.content_fragment, quanziFrament);
                    map.put(7, quanziFrament);
                }
                break;
        }

        for (Integer key : map.keySet()) {
            ftd.hide(map.get(key));
        }
        ftd.show(map.get(index));
        ftd.commit();
    }

    /**
     * 对底部按钮进行监听
     */
    @Override
    @GinOnClick(id = {R.id.home_page_ll, R.id.quanzi_ll, R.id.home_group, R.id.f_posting, R.id.convenience_ll,
            R.id.ebusiness_fr, R.id.my_profile_ll, R.id.zhiyuanzhe})
    public void onClick(View view) {
        int num = 0;
        switch (view.getId()) {
            case R.id.home_page_ll:
                num = 0;
                changeColorAndDrawable(0);
                break;
            case R.id.convenience_ll:
                num = 3;

                changeColorAndDrawable(3);
                break;
            case R.id.ebusiness_fr:
                num = 4;
                changeColorAndDrawable(4);
                break;
            case R.id.my_profile_ll:// 我的
                num = 5;

                changeColorAndDrawable(5);
                break;
            case R.id.quanzi_ll:// 圈子
                num = 7;

                changeColorAndDrawable(7);
                break;
        }
        setCurrentFragment(num);
    }

    /**
     * 改变图标以及字体颜色
     */
    private void changeColorAndDrawable(int index) {
        home_page_iv.setImageResource(R.drawable.shouye_normal);
        home_page.setTextColor(getResources().getColor(R.color.common_light_gray_text_color));

        quanzi_iv.setImageResource(R.drawable.quanzi_normal);
        quanzi.setTextColor(getResources().getColor(R.color.common_light_gray_text_color));

        ebusinessIcon.setImageResource(R.drawable.new_temai_normal);

        my_profile_iv.setImageResource(R.drawable.wo_normal);
        my_profile.setTextColor(getResources().getColor(R.color.common_light_gray_text_color));

        convenience_iv.setImageResource(R.drawable.faxian_normal);
        convenience.setTextColor(getResources().getColor(R.color.common_light_gray_text_color));

        if (index == 0) {
            home_page_iv.setImageResource(R.drawable.shouye_checked);
            home_page.setTextColor(getResources().getColor(R.color.common_red_color));
        } else if (index == 7) {
            quanzi_iv.setImageResource(R.drawable.quanzi_checked);
            quanzi.setTextColor(getResources().getColor(R.color.common_red_color));
        } else if (index == 4) {
            ebusinessIcon.setImageResource(R.drawable.new_temai_pressed);
        } else if (index == 3) {
            convenience_iv.setImageResource(R.drawable.faxian_checked);
            convenience.setTextColor(getResources().getColor(R.color.common_red_color));
        } else if (index == 5) {
            my_profile_iv.setImageResource(R.drawable.wo_checked);
            my_profile.setTextColor(getResources().getColor(R.color.common_red_color));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitBy2Click();
        }
        return false;
    }

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            AdvancedImageView.destory();
            finishActivity();
            System.exit(0);
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        List<Government> governments = dbUtils.findAll(Government.class);
        if (governments != null && governments.size() > 0) {

        } else {
            zhengwu("3685", "通知中心");
            zhengwu("3687", "办事指南");
            zhengwu("3688", "机关黄页");
        }

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
                }
            }
        }
    }

    @Override
    public void handle(Intent intent) {
        String msg = intent.getStringExtra("msg");
        if (map.get(currentFragmentIndex) == eBusinessFragment && ECShopBroadReciver.HIDE_ACTION_BAR.equals(msg)) {
            bottomGuideBar.setVisibility(View.GONE);
        } else {
            bottomGuideBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReciver != null) {
            unregisterReceiver(broadcastReciver);
        }
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private void checkSDCardPermission() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(WebPageFragment.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(WebPageFragment.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            }

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(WebPageFragment.this).setMessage(message).setPositiveButton("授权", okListener)
                .setNegativeButton("拒绝", null).create().show();
    }
}
