package com.tessoft.nearhere.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tessoft.nearhere.fragments.CarPoolTaxiFragment;
import com.tessoft.nearhere.fragments.LocationFragment;
import com.tessoft.nearhere.fragments.TaxiFragment;

/**
 * Created by Daeyong on 2015-12-14.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    Fragment[] fragments = null;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new Fragment[3];
        fragments[0] = LocationFragment.newInstance();
        fragments[1] = CarPoolTaxiFragment.newInstance();
        fragments[2] = TaxiFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if ( position == 0 ) return "실시간 위치공유";
        else  if( position == 1 ) return "카풀/합승";
        else  if( position == 2 ) return "카풀/합승";
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