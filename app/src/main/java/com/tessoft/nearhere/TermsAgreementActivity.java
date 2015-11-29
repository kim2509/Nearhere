package com.tessoft.nearhere;

import java.util.HashMap;

import org.codehaus.jackson.type.TypeReference;

import com.tessoft.common.Constants;
import com.tessoft.domain.APIResponse;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TermsAgreementActivity extends BaseActivity {

	private static final int INSERT_USER_TERMS_AGREEMENT = 1;
	WebView webView1 = null;
	WebView webView2 = null;
	WebView webView3 = null;

	WebViewClient webViewClient = new WebViewClient() {
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Toast.makeText( getApplicationContext(), description, Toast.LENGTH_SHORT).show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try
		{
			super.onCreate(savedInstanceState);

			supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

			setContentView(R.layout.activity_terms_agreement);

			webView1 = (WebView) findViewById(R.id.webView1);
			webView1.setWebViewClient( webViewClient );
			webView2 = (WebView) findViewById(R.id.webView2);
			webView2.setWebViewClient( webViewClient );
			webView3 = (WebView) findViewById(R.id.webView3);
			webView3.setWebViewClient( webViewClient );
			
			webView1.loadUrl( Constants.getServerURL() + "/taxi/getUserTerms.do?type=nearhere&version=1.0");
			webView2.loadUrl( Constants.getServerURL() + "/taxi/getUserTerms.do?type=personal&version=1.0");
			webView3.loadUrl( Constants.getServerURL() + "/taxi/getUserTerms.do?type=location&version=1.0");
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.terms_agreement, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateTermsAgreement( View v )
	{
		try
		{
			setProgressBarIndeterminateVisibility(true);

			application.debug("profilePoint:" + application.getLoginUser().getProfilePoint());
			
			HashMap hash = new HashMap();
			hash.put("userID", application.getLoginUser().getUserID());
			hash.put("nearhere_ver", "1.0");
			hash.put("personal_ver", "1.0");
			hash.put("location_ver", "1.0");
			sendHttp("/taxi/insertTermsAgreement.do", mapper.writeValueAsString(hash), INSERT_USER_TERMS_AGREEMENT);	
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		try
		{
			if ( Constants.FAIL.equals(result) )
			{
				setProgressBarIndeterminateVisibility(false);
				showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}

			setProgressBarIndeterminateVisibility(false);

			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				if ( requestCode == INSERT_USER_TERMS_AGREEMENT )
				{
					goMainActivity();
					super.finish();
				}
			}
			else
			{
				showOKDialog("경고", response.getResMsg(), null);
				return;
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	boolean doubleBackToExitPressedOnce = false;
	@Override
	public void onBackPressed() {

		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "이전 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce=false;                       
			}
		}, 2000);
	}
}
