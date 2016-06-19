package com.tessoft.nearhere.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.fragments.CarPoolTaxiFragment;
import com.tessoft.nearhere.fragments.FriendFragment;
import com.tessoft.nearhere.fragments.LocationHistoryFragment;
import com.tessoft.nearhere.fragments.MessageBoxFragment;
import com.tessoft.nearhere.fragments.NotificationFragment;

/**
 * Created by Daeyong on 2015-12-14.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    Fragment[] fragments = null;
    public NearhereApplication application = null;

    public MainPagerAdapter(FragmentManager fm, NearhereApplication application) {
        super(fm);

        this.application = application;

        fragments = new Fragment[5];

        fragments[0] = CarPoolTaxiFragment.newInstance();
        fragments[1] = FriendFragment.newInstance();
        fragments[2] = MessageBoxFragment.newInstance();
        fragments[3] = LocationHistoryFragment.newInstance();
        fragments[4] = NotificationFragment.newInstance();
    }

    public void setApplication( NearhereApplication application )
    {
        this.application = application;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String[] titles = null;

        titles = new String[5];
        titles[0] = "카풀";

        if ( application.friendRequestCount == 0 )
            titles[1] = "친구";
        else
            titles[1] = "친구(" + application.friendRequestCount + ")";

        if ( application.messageCount == 0 )
            titles[2] = "채팅";
        else
            titles[2] = "채팅(" + application.messageCount + ")";

        titles[3] = "위치";
        titles[4] = "알림";

        if ( application.NotificationCount == 0 )
            titles[4] = "알림";
        else
            titles[4] = "알림(" + application.NotificationCount + ")";

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