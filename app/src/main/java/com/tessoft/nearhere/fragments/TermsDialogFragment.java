package com.tessoft.nearhere.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.TextView;

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

	TextView txtTerm1 = null;
	TextView txtTerm2 = null;
	TextView txtTerm3 = null;
	CheckBox chkTerm1 = null;
	CheckBox chkTerm2 = null;
	CheckBox chkTerm3 = null;
	CheckBox chkTermAll = null;

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

					txtTerm1 = (TextView) rootView.findViewById(id.txtTerm1);
					txtTerm1.setOnClickListener(TermsDialogFragment.this);
					txtTerm2 = (TextView) rootView.findViewById(id.txtTerm2);
					txtTerm2.setOnClickListener(TermsDialogFragment.this);
					txtTerm3 = (TextView) rootView.findViewById(id.txtTerm3);
					txtTerm3.setOnClickListener(TermsDialogFragment.this);

					chkTerm1 = (CheckBox) rootView.findViewById(R.id.chkTerm1);
					chkTerm1.setOnClickListener(TermsDialogFragment.this);
					chkTerm2 = (CheckBox) rootView.findViewById(R.id.chkTerm2);
					chkTerm2.setOnClickListener(TermsDialogFragment.this);
					chkTerm3 = (CheckBox) rootView.findViewById(R.id.chkTerm3);
					chkTerm3.setOnClickListener(TermsDialogFragment.this);
					chkTermAll = (CheckBox) rootView.findViewById(R.id.chkTermAll);
					chkTermAll.setOnClickListener(TermsDialogFragment.this);

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
			if ( v.getId() == id.txtTerm1 )
			{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(Constants.getServerURL() + "/taxi/getUserTerms.do?type=nearhere&version=1.0"));
				startActivity(browserIntent);
			}
			else if ( v.getId() == id.txtTerm2 )
			{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(Constants.getServerURL() + "/taxi/getUserTerms.do?type=personal&version=1.0"));
				startActivity(browserIntent);
			}
			else if ( v.getId() == id.txtTerm3 )
			{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(Constants.getServerURL() + "/taxi/getUserTerms.do?type=location&version=1.0"));
				startActivity(browserIntent);
			}
			else if ( v.getId() == id.chkTerm1 || v.getId() == id.chkTerm2 || v.getId() == id.chkTerm3 )
			{
				if ( chkTerm1.isChecked() && chkTerm2.isChecked() && chkTerm3.isChecked() )
					chkTermAll.setChecked(true);
				else
					chkTermAll.setChecked(false);
			}
			else if ( v.getId() == id.chkTermAll )
			{
				if ( chkTermAll.isChecked() )
				{
					chkTerm1.setChecked(true);
					chkTerm2.setChecked(true);
					chkTerm3.setChecked(true);
				}
				else
				{
					chkTerm1.setChecked(false);
					chkTerm2.setChecked(false);
					chkTerm3.setChecked(false);
				}
			}
			else if ( v.getId() == id.btnAgree )
			{
				if ( chkTerm1.isChecked() && chkTerm2.isChecked() && chkTerm3.isChecked() )
				{
					HashMap hash = new HashMap();
					hash.put("userID", application.getLoginUser().getUserID());
					hash.put("nearhere_ver", "1.0");
					hash.put("personal_ver", "1.0");
					hash.put("location_ver", "1.0");
					sendHttp("/taxi/insertTermsAgreement.do",
							mapper.writeValueAsString(hash), Constants.HTTP_INSERT_USER_TERMS_AGREEMENT);
				}
				else
				{
					application.showToastMessage("약관에 동의해 주시기 바랍니다.");
				}
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