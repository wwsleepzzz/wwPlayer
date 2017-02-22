package com.zzz.shiro.wwplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by wc on 2016/8/22.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private String[] titleArray;
    private List<Fragment> fragmentList;

    public PagerAdapter(FragmentManager ff , String[] titles, List<Fragment> fragments) {
        super(ff);
        this.titleArray = titles;
        this.fragmentList = fragments;
    }

    @Override public CharSequence getPageTitle(int position) {
        return titleArray[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
