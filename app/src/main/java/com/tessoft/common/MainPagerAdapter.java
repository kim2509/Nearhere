package com.tessoft.common;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tessoft.nearhere.fragment.TaxiFragment;

/**
 * Created by Daeyong on 2015-12-14.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if ( position == 0 ) return "위치";
        else if ( position == 1 ) return "카풀";
        else return "합승";
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        if ( position == 0 )
            return LocationFragment.newInstance();
        else
            return TaxiFragment.newInstance();
    }

}