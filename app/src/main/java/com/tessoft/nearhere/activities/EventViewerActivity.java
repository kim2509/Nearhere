package com.tessoft.nearhere.activities;

import com.tessoft.common.Constants;
import com.tessoft.nearhere.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class EventViewerActivity extends BaseActivity {

	WebView webView = null;
	
	WebViewClient webViewClient = new WebViewClient() {
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Toast.makeText( getApplicationContext(), description, Toast.LENGTH_SHORT).show();
		}
		
		public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
			handler.proceed();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_event_viewer);
			
			String eventSeq = getIntent().getExtras().getString("eventSeq");
			String pushNo = getIntent().getExtras().getString("pushNo");
			
			webView = (WebView) findViewById(R.id.webView);
			webView.getSettings().setJavaScriptEnabled(true);
			
			webView.setWebViewClient(webViewClient);
			webView.setWebChromeClient(new WebChromeClient());
			
			if ( getIntent().getExtras().containsKey("ssl") && "true".equals( getIntent().getExtras().getString("ssl") ) )
				webView.loadUrl( Constants.getServerSSLURL() + "/taxi/eventDetail.do?eventSeq=" + eventSeq + "&pushNo=" + pushNo );
			else
				webView.loadUrl( Constants.getServerURL() + "/taxi/eventDetail.do?eventSeq=" + eventSeq + "&pushNo=" + pushNo );
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
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
	
	@Override
	public void onBackPressed() {

		finish();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {

			webView.reload();
			
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
