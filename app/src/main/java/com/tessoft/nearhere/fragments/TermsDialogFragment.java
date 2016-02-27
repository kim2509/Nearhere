package com.tessoft.nearhere.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.Constants;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.domain.APIResponse;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.R.id;
import com.tessoft.nearhere.R.layout;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;

public class TermsDialogFragment extends DialogFragment implements OnClickListener, TransactionDelegate
{
	private static final int REQUEST_SET_DEPARTURE = 1;
	private static final int REQUEST_SET_DESTINATION = 2;
	View rootView = null;
	AdapterDelegate delegate;
	HashMap initData = null;
	NearhereApplication application = null;
	ObjectMapper mapper = new ObjectMapper();

	public TermsDialogFragment()
	{

	}

	public void setApplication( NearhereApplication app )
	{
		this.application = app;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) 
    {
    	View v = super.onCreateView(inflater, container, savedInstanceState);
    	
    	return v;
    }

    AlertDialog searchDialog = null;

	WebViewClient webViewClient = new WebViewClient() {
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		}
	};
	WebView webView1 = null;
	WebView webView2 = null;
	WebView webView3 = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
    	
    	try
    	{
    		LayoutInflater li = LayoutInflater.from( getActivity() );
    		rootView = li.inflate(layout.fragment_terms, null);
    		
    		initializeComponent();
    		
            // Set a theme on the dialog builder constructor!
    		AlertDialog.Builder builder = 
                new AlertDialog.Builder( getActivity() );
            builder.setView(rootView);
            
            searchDialog = builder.create();
            
            searchDialog.setOnShowListener(new OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					// TODO Auto-generated method stub

					webView1 = (WebView) rootView.findViewById(R.id.webView1);
					webView1.setWebViewClient( webViewClient );
					webView2 = (WebView) rootView.findViewById(R.id.webView2);
					webView2.setWebViewClient( webViewClient );
					webView3 = (WebView) rootView.findViewById(R.id.webView3);
					webView3.setWebViewClient( webViewClient );

					webView1.loadUrl(Constants.getServerURL() + "/taxi/getUserTerms.do?type=nearhere&version=1.0");
					webView2.loadUrl(Constants.getServerURL() + "/taxi/getUserTerms.do?type=personal&version=1.0");
					webView3.loadUrl( Constants.getServerURL() + "/taxi/getUserTerms.do?type=location&version=1.0");

					rootView.findViewById(R.id.btnAgree).setOnClickListener(TermsDialogFragment.this);
				}
			});
    	}
    	catch( Exception ex )
    	{
    	}
        
        return searchDialog;
    }

    public void initializeComponent()
    {
		if ( this.initData != null )
		{

		}
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == id.btnAgree )
			{
				HashMap hash = new HashMap();
				hash.put("userID", application.getLoginUser().getUserID());
				hash.put("nearhere_ver", "1.0");
				hash.put("personal_ver", "1.0");
				hash.put("location_ver", "1.0");
				sendHttp("/taxi/insertTermsAgreement.do",
						mapper.writeValueAsString(hash), Constants.HTTP_INSERT_USER_TERMS_AGREEMENT);
			}
		}
		catch( Exception ex )
		{
			
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(android.content.DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

				if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
					return true; // pretend we've processed it
				} else
					return false; // pass on to be processed as normal
			}
		});
	}

	public void sendHttp( String url, Object request, int requestCode )
	{
		new HttpTransactionReturningString( this, url, requestCode ).execute(request);
	}

	public void doPostTransaction( int requestCode, Object result )
	{
		try
		{
			if ( Constants.FAIL.equals(result) )
			{
				//showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}

			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				application.bUserTermsAgreed = true;
				application.sendBroadcast(new Intent(Constants.BROADCAST_START_LOCATION_UPDATE));
				searchDialog.dismiss();
			}
			else
			{
				return;
			}
		}
		catch( Exception ex )
		{

		}
	}
}