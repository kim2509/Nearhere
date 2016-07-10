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

        fragments = new Fragment[6];

        fragments[0] = CarPoolTaxiFragment.newInstance();
        fragments[1] = NewsFragment.newInstance();
        fragments[2] = FriendFragment.newInstance();
        fragments[3] = MessageBoxFragment.newInstance();
        fragments[4] = LocationFragment.newInstance();
        fragments[5] = NotificationFragment.newInstance();
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

        if ( application.friendRequestCount == 0 )
            titles[2] = "친구";
        else
            titles[2] = "친구(" + application.friendRequestCount + ")";

        if ( application.messageCount == 0 )
            titles[3] = "메시지";
        else
            titles[3] = "메시지(" + application.messageCount + ")";

        titles[4] = "위치";

        if ( application.NotificationCount == 0 )
            titles[5] = "알림";
        else
            titles[5] = "알림(" + application.NotificationCount + ")";

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