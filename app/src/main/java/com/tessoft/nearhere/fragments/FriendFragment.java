package com.tessoft.nearhere.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.tessoft.common.CommonWebViewClient;
import com.tessoft.common.Constants;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.activities.BaseActivity;

import java.net.URLEncoder;

/**
 * Created by Daeyong on 2016-04-18.
 */
public class FriendFragment extends BaseFragment {

    protected View rootView = null;
    private WebView webView = null;

    // TODO: Rename and change types and number of parameters
    public static FriendFragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_REFRESH_FRIEND_LIST));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if ( intent.getAction().equals(Constants.BROADCAST_REFRESH_FRIEND_LIST))
                    webView.reload();
            }
            catch( Exception ex )
            {
                catchException(this, ex);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try
        {
            // Inflate the layout for this fragment
            if ( rootView == null )
            {
                rootView = inflater.inflate(R.layout.fragment_car_pool_taxi, container, false);

                CommonWebViewClient commonWebViewClient = new CommonWebViewClient((BaseActivity) getActivity(), application );

                webView = (WebView) rootView.findViewById(R.id.webView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebChromeClient(new WebChromeClient() {

                });

                webView.addJavascriptInterface(commonWebViewClient, "Android");

                webView.setBackgroundColor(0);
                webView.loadUrl(Constants.getServerSSLURL() + "/friend/list.do?isApp=Y"
                        + "&userHash=" + URLEncoder.encode(application.getMetaInfoString("hash")) +
                        "&appVersion=" + application.getPackageVersion());

                webView.setWebViewClient(commonWebViewClient);
            }
        }
        catch( Exception ex )
        {
            application.catchException(this , ex );
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mMessageReceiver);
    }
}
