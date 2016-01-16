package com.tessoft.nearhere.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tessoft.nearhere.R;

public class PopupWebViewActivity extends BaseActivity {

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_popup_web_view);

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient( new WebChromeClient(){

            });
            webView.setWebViewClient( new WebViewClient(){

            });

            if ( getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("url"))
            {
                webView.loadUrl( getIntent().getExtras().getString("url"));
            }
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }
}
