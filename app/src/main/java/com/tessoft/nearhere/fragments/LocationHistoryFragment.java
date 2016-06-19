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

import org.codehaus.jackson.map.ObjectMapper;

import java.net.URLEncoder;

/**
 * Created by Daeyong on 2016-06-17.
 */
public class LocationHistoryFragment extends BaseFragment {

    View rootView = null;
    WebView webView = null;


    public LocationHistoryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LocationHistoryFragment newInstance() {
        LocationHistoryFragment fragment = new LocationHistoryFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapper = new ObjectMapper();

        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_REFRESH_LOCATION_HISTORY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try
        {
            // Inflate the layout for this fragment
            if ( rootView == null )
            {
                rootView =  inflater.inflate(R.layout.fragment_location_history, container, false);
                webView = (WebView) rootView.findViewById(R.id.webView);

                CommonWebViewClient commonWebViewClient = new CommonWebViewClient((BaseActivity) getActivity());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebChromeClient(new WebChromeClient() {

                });

                webView.addJavascriptInterface(commonWebViewClient, "Android");

                webView.setBackgroundColor(0);
                webView.loadUrl(Constants.getServerSSLURL() + "/location/history.do?isApp=Y&userID="
                        + application.getLoginUser().getUserID()
                        + "&userHash=" + URLEncoder.encode(application.getMetaInfoString("hash")) +
                        "&appVersion=" + application.getPackageVersion());

                webView.setWebViewClient(commonWebViewClient);
            }
        }
        catch( Exception ex )
        {
            showToastMessage( ex.getMessage() );
        }

        return rootView;
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if ( intent == null ) return;

                if ( Constants.BROADCAST_REFRESH_LOCATION_HISTORY.equals(intent.getAction()) )
                {
                    webView.reload();
                }
            }
            catch( Exception ex )
            {
                application.catchException(this, ex);
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(mMessageReceiver);
    }
}
