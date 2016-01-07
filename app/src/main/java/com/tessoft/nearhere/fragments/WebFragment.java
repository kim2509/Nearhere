package com.tessoft.nearhere.fragments;

import java.util.HashMap;

import com.tessoft.common.Constants;
import com.tessoft.nearhere.activities.HybridActivity;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebFragment extends BaseFragment {

	public static final int READY = 0;
	public static final int REFRESH_LOCATION = 1;
	public static final int GO_URL = 2;
	View rootView = null;
	WebView webView = null;
	WebViewHandler webViewHandler = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		try
		{
			super.onCreateView(inflater, container, savedInstanceState);
			rootView = inflater.inflate(R.layout.fragment_web, container, false);

			webViewHandler = new WebViewHandler();
			
			webView = (WebView) rootView.findViewById(R.id.webView);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.clearView();
			webView.setWebViewClient( webViewClient );
			webView.setWebChromeClient(new WebChromeClient());
			webView.loadUrl( Constants.getServerURL() + "/taxi/index.do");

			Button btnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
			btnRefresh.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						webView.reload();
					} catch ( Exception ex ) {
						// TODO Auto-generated catch block
						catchException(this, ex);
					}
				}
			});

			MainJSInterface mainJSInterface = new MainJSInterface();
			webView.addJavascriptInterface(mainJSInterface, "Android");
		}
		catch( Exception ex )
		{

		}

		return rootView;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		getActivity().registerReceiver(mMessageReceiver, new IntentFilter("currentLocationChanged"));
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		getActivity().unregisterReceiver(mMessageReceiver);
	}

	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try
			{
				if ( "currentLocationChanged".equals( intent.getAction() ) )
				{
					updateCurrentLocation();
				}
			}
			catch( Exception ex )
			{
				catchException(this, ex);
			}
		}
	};

	WebViewClient webViewClient = new WebViewClient() {
		
		public void onPageFinished(WebView view, String url) {
			
			
		};
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		}

		public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
			handler.proceed();
		};
	};

	public void updateCurrentLocation()
	{
		webView.loadUrl("javascript:updateCurrentLocation('" +
				NearhereApplication.latitude + "','" + NearhereApplication.longitude + "','" + NearhereApplication.address + "');");
	}

	class MainJSInterface
	{
		@JavascriptInterface
		public void init()
		{
			
		}
		
		@JavascriptInterface
		public void sendEvent( String cmd )
		{
			Message msg = webViewHandler.obtainMessage();
			
			if ( "ready".equals( cmd ) )
				msg.what = READY;
			else if ( "refreshLocation".equals( cmd ) )
				msg.what = REFRESH_LOCATION;
			else
				return;
			
			webViewHandler.sendMessage(msg);
		}
		
		@JavascriptInterface
		public void goURL( String url, String animation )
		{
			Message msg = webViewHandler.obtainMessage();
			
			HashMap param = new HashMap();
			param.put("url", url);
			param.put("animation", animation);
			
			msg.what = GO_URL;
			msg.obj = param;
			
			webViewHandler.sendMessage(msg);
		}
	}
	
    class WebViewHandler extends Handler {
         
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             
            try
            {
            	switch (msg.what) {
                case READY:
                	String userID = application.getLoginUser().getUserID();
    				webView.loadUrl("javascript:getMainInfo('" + userID + "');");	
                    break;
                     
                case REFRESH_LOCATION:
                	updateCurrentLocation();
                    break;
                    
                case GO_URL:
                	goURL( msg.obj );
                    break;
     
                default:
                    break;
                }            	
            }
            catch( Exception ex )
            {
            	
            }
        }
         
    };
    
    public void goURL( Object param )
    {
    	if ( param == null ) return;
    	
    	HashMap hashParam = (HashMap) param;
    	
    	Intent intent = null;
		intent = new Intent( getActivity(), HybridActivity.class);
		intent.putExtra("url", hashParam.get("url").toString());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
		getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }
}
