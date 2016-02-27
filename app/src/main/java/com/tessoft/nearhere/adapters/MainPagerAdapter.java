package com.tessoft.nearhere.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tessoft.nearhere.fragments.CarPoolTaxiFragment;
import com.tessoft.nearhere.fragments.LocationFragment;

/**
 * Created by Daeyong on 2015-12-14.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    Fragment[] fragments = null;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new Fragment[2];
        fragments[0] = CarPoolTaxiFragment.newInstance();
//        fragments[1] = DriverFragment.newInstance();
        fragments[1] = LocationFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if( position == 0 ) return "카풀/합승";
//        else if ( position == 1 ) return "대리운전";
        else  if( position == 1 ) return "위치공유";
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