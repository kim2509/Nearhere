package com.tessoft.common;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tessoft.nearhere.fragment.TaxiFragment;

/**
 * Created by Daeyong on 2015-12-14.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    Fragment[] fragments = null;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new Fragment[2];
        fragments[0] = LocationFragment.newInstance();
        fragments[1] = TaxiFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if ( position == 0 ) return "실시간 위치공유";
        else  if( position == 1 ) return "카풀/합승";
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