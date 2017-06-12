package com.tessoft.nearhere.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.astuetz.PagerSlidingTabStrip;
import com.tessoft.common.Constants;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.activities.PopupWebViewActivity;
import com.tessoft.nearhere.adapters.MainPagerAdapter;

import java.net.URLEncoder;

public class MainFragment extends BaseFragment implements OnPageChangeListener, View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    View rootView = null;
    Button btnRefresh = null;
    Button btnNotification = null;
    MainPagerAdapter pagerAdapter = null;
    PagerSlidingTabStrip tabs = null;
    public int selectedTabIndex = 0;
    ViewPager pager = null;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialize the ViewPager and set an adapter
        pager = (ViewPager) rootView.findViewById(R.id.pagerMain);
        pagerAdapter = new MainPagerAdapter(getChildFragmentManager(), application);
        pager.setAdapter(pagerAdapter);

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);

        tabs.setViewPager(pager);

        // continued from above
        tabs.setOnPageChangeListener(this);

        btnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
        btnRefresh.setVisibility(ViewGroup.VISIBLE);
        btnRefresh.setOnClickListener(this);

        btnNotification = (Button) rootView.findViewById(R.id.btnNotification);
        btnNotification.setVisibility(ViewGroup.VISIBLE);
        btnNotification.setOnClickListener(this);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void updateTabCount() {
        tabs.notifyDataSetChanged();
    }

    public void selectTab(int position)
    {
        pager.setCurrentItem(position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedTabIndex = position;

        if ( position == 5 )
            btnRefresh.setVisibility(ViewGroup.GONE);
        else
            btnRefresh.setVisibility(ViewGroup.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

        try
        {
            if ( v.getId() == R.id.btnRefresh )
            {
                Fragment fragment = pagerAdapter.getItem( selectedTabIndex );
                if ( fragment instanceof TaxiFragment )
                {
                    TaxiFragment taxiFragment = (TaxiFragment) fragment;
                    taxiFragment.reloadContents();
                }
                else
                {
                    Intent intent = null;

                    if ( selectedTabIndex == 1 )
                        intent = new Intent(Constants.BROADCAST_REFRESH_CAFE);
                    else if ( selectedTabIndex == 2 )
                        intent = new Intent(Constants.BROADCAST_REFRESH_NEWS);
                    else if ( selectedTabIndex == 3 )
                        intent = new Intent(Constants.BROADCAST_REFRESH_TRAVEL_INFO);
                    else if ( selectedTabIndex == 4 )
                        intent = new Intent(Constants.BROADCAST_REFRESH_FRIEND_LIST);
                    else if ( selectedTabIndex == 6 )
                        intent = new Intent(Constants.BROADCAST_REFRESH_LOCATION_HISTORY);
                    else
                        intent = new Intent(Constants.BROADCAST_REFRESH);

                    if ( intent != null )
                        getActivity().sendBroadcast(intent);
                }
            }
            else if ( v.getId() == R.id.btnNotification )
            {
                Intent intent = new Intent(getActivity(), PopupWebViewActivity.class);
                String url = Constants.getServerSSLURL() +
                        "/notification/list.do?isApp=Y&userID=" + application.getLoginUser().getUserID() +
                        "&userHash=" + URLEncoder.encode(application.getMetaInfoString("hash")) +
                        "&appVersion=" + application.getPackageVersion();
                intent.putExtra("url", url);
                intent.putExtra("title", "알림");
                startActivity(intent);
            }
        }
        catch( Exception ex )
        {

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Fragment getChildFragment( int position )
    {
        return pagerAdapter.getItem(position);
    }
}
