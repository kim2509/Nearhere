package com.tessoft.nearhere;

import com.tessoft.common.Constants;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class HybridActivity extends BaseActivity{

	WebView webView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try
		{
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.fragment_main);
			
			webView = (WebView) findViewById(R.id.webView);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.clearView();
			webView.setWebViewClient( webViewClient );
			webView.setWebChromeClient(new WebChromeClient());
			webView.loadUrl( Constants.getServerURL() + "/taxi/" + getIntent().getExtras().getString("url"));
			
			Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
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

			JSInterface mainJSInterface = new JSInterface();
			webView.addJavascriptInterface(mainJSInterface, "Android");
		}
		catch( Exception ex )
		{
			
		}
		
	}
	
	WebViewClient webViewClient = new WebViewClient() {
		
		public void onPageFinished(WebView view, String url) {
			
			
		};
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		}

		public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
			handler.proceed();
		};
	};
	
	class JSInterface
	{
		@JavascriptInterface
		public void init()
		{
			
		}
		
		@JavascriptInterface
		public void sendEvent( String cmd )
		{
		}
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
}
