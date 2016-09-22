package com.tessoft.nearhere.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.fragments.CarPoolTaxiFragment;
import com.tessoft.nearhere.fragments.FriendFragment;
import com.tessoft.nearhere.fragments.LocationFragment;
import com.tessoft.nearhere.fragments.MessageBoxFragment;
import com.tessoft.nearhere.fragments.NewsFragment;
import com.tessoft.nearhere.fragments.TravelInfoFragment;

/**
 * Created by Daeyong on 2015-12-14.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    Fragment[] fragments = null;
    public NearhereApplication application = null;

    public MainPagerAdapter(FragmentManager fm, NearhereApplication application) {
        super(fm);

        this.application = application;

        fragments = new Fragment[6];

        fragments[0] = CarPoolTaxiFragment.newInstance();
        fragments[1] = NewsFragment.newInstance();
        fragments[2] = TravelInfoFragment.newInstance();
        fragments[3] = FriendFragment.newInstance();
        fragments[4] = MessageBoxFragment.newInstance();
        fragments[5] = LocationFragment.newInstance();
    }

    public void setApplication( NearhereApplication application )
    {
        this.application = application;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String[] titles = null;

        titles = new String[6];
        titles[0] = "카풀";
        titles[1] = "소식";
        titles[2] = "여행";

        if ( application.friendRequestCount == 0 )
            titles[3] = "친구";
        else
            titles[3] = "친구(" + application.friendRequestCount + ")";

        if ( application.messageCount == 0 )
            titles[4] = "메시지";
        else
            titles[4] = "메시지(" + application.messageCount + ")";

        titles[5] = "위치";

        if ( position < titles.length )
        {
            return titles[position];
        }
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