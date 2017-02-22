package com.zzz.shiro.wwplayer;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.animation.AnimatorProxy;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        {

    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private TestFragment testFragment1;
    private TestFragment testFragment2;
    private TestFragment testFragment3;
    private TestFragment testFragment4;
    private TestFragment testFragment5;
    private TestFragment testFragment6;
    private boolean fragmentsUpdateFlag[] = {false, false, false, false, false};





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        initViewPager();
    }



    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.id_vp);

        testFragment1 = TestFragment.getFragment("1");
        testFragment2 = TestFragment.getFragment("2");
        testFragment3 = TestFragment.getFragment("3");
        testFragment4 = TestFragment.getFragment("4");
        testFragment5 = TestFragment.getFragment("5");

        testFragment6 = TestFragment.getFragment("6");

        fragmentList = new ArrayList<>();
        fragmentList.add(testFragment1);
        fragmentList.add(testFragment2);
        fragmentList.add(testFragment3);
        fragmentList.add(testFragment4);
        fragmentList.add(testFragment5);

        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);

        testFragment2.setTransListener(new TestFragment.onTranslateClick() {
            @Override
            public void onClick(String msg) {
                //点击切换按钮
                //设置fragment2的tag为true
                fragmentsUpdateFlag[1] = true;
                //替换fragment
                fragmentList.set(1, testFragment6);
                adapter.notifyDataSetChanged();
            }
        });
        testFragment6.setTransListener(new TestFragment.onTranslateClick() {
            @Override
            public void onClick(String msg) {
                //点击切换按钮
                //设置fragment2的tag为true
                fragmentsUpdateFlag[1] = true;
                //替换fragment
                fragmentList.set(1, testFragment6);
                //刷新
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
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
