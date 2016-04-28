package com.tessoft.nearhere.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tessoft.nearhere.fragments.CarPoolTaxiFragment;
import com.tessoft.nearhere.fragments.FriendFragment;
import com.tessoft.nearhere.fragments.LocationFragment;
import com.tessoft.nearhere.fragments.NotificationFragment;

/**
 * Created by Daeyong on 2015-12-14.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    Fragment[] fragments = null;
    String[] titles = null;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new Fragment[4];
        fragments[0] = CarPoolTaxiFragment.newInstance();
        fragments[1] = FriendFragment.newInstance();
        fragments[2] = NotificationFragment.newInstance();
        fragments[3] = LocationFragment.newInstance();

        titles = new String[4];
        titles[0] = "카풀/합승";
        titles[1] = "친구";
        titles[2] = "알림";
        titles[3] = "위치";
    }

    public String[] getTitles()
    {
        return titles;
    }

    public void setTitles( String[] titles )
    {
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if ( position < titles.length ) return titles[position];
        else return "";
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }
}