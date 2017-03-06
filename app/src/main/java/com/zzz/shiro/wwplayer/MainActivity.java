package com.zzz.shiro.wwplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zzz.shiro.wwplayer.fragment.LocalFragment;
import com.zzz.shiro.wwplayer.fragment.MainFragment;
import com.zzz.shiro.wwplayer.fragment.PlayListFragment;
import com.zzz.shiro.wwplayer.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        {
    private String className = "MainActivity";
    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private MainFragment mainFragment;

    private LocalFragment myFragment;
    private PlayListFragment myFragment2;
    private boolean fragmentsUpdateFlag[] = {false, false, false, false, false};

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//開啟返回鍵
        }

        initViewPager();

        if (checkPermission()) {
            Log.e("value", "Permission already Granted, Now you can save image.");
        } else {
            requestPermission();
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("","按到了");

                Fragment ff= fragmentList.get(0);
                if (ff instanceof LocalFragment || ff instanceof PlayListFragment) {
                    //设置fragment2的tag为true
                    fragmentsUpdateFlag[0] = true;
                    //替换fragment
                    fragmentList.set(0, mainFragment);
                    //刷新
                    adapter.notifyDataSetChanged();

                }




                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.id_vp);

        mainFragment = MainFragment.getFragment("1");
        myFragment = new LocalFragment();
        myFragment2 = new PlayListFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(mainFragment);


        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);


        mainFragment.setChangeFgmtListener(new MainFragment.onChangeFgmtClick() {
            @Override
            public void onClick(View view) {
                //點擊切換按鈕
                //設置fragment2的tag為true
                fragmentsUpdateFlag[0] = true;

                if(view.getId()==R.id.ib_local){//本地

                    //給參數
                    Bundle bundle2 = new Bundle();
                    bundle2.putString(Constants.BundleId.playList,null);
                    myFragment.setArguments(bundle2);

                    fragmentList.set(0, myFragment); //替換fragment
                }
                else if(view.getId()==R.id.ib_list){//列表
                    //替換fragment
                    fragmentList.set(0, myFragment2);
                }

                adapter.notifyDataSetChanged();
            }
        });

        myFragment2.setChangeFmtListener(new PlayListFragment.onChangeFmtClick() {
            @Override
            public void onClick(String listName) {
                Log.d(className,"listview click");

                //點擊切換按鈕
                fragmentsUpdateFlag[0] = true;

                //給參數
                Bundle bundle2 = new Bundle();
                bundle2.putString(Constants.BundleId.playList,listName);
                myFragment.setArguments(bundle2);

                fragmentList.set(0, myFragment); //替換fragment
                adapter.notifyDataSetChanged();
            }
        });

//        testFragment2.setTransListener(new MainFragment.onTranslateClick() {
//            @Override
//            public void onClick(String msg) {
//                //点击切换按钮
//                //设置fragment2的tag为true
//                fragmentsUpdateFlag[1] = true;
//                //替换fragment
//                fragmentList.set(1, myFragment);
//                adapter.notifyDataSetChanged();
//            }
//        });

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }







    public class MyAdapter extends FragmentPagerAdapter {

        private FragmentManager fm;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }


        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position % fragmentList.size());
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //得到缓存的fragment

            Fragment fragment = (Fragment) super.instantiateItem(container, position);

            //得到tag


            String fragmentTag = fragment.getTag();

            if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {

                //如果这个fragment需要更新

                FragmentTransaction ft = fm.beginTransaction();

                //移除旧的fragment

                ft.remove(fragment);

                //换成新的fragment

                fragment = fragmentList.get(position % fragmentList.size());

                //添加新fragment时必须用前面获得的tag ❶

                ft.add(container.getId(), fragment, fragmentTag);

                ft.attach(fragment);

                ft.commit();


                //复位更新标志

                fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;

            }


            return fragment;
        }


    }
}
