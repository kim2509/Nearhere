package com.tessoft.nearhere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.tessoft.nearhere.R;

public class PopupWebViewActivity extends BaseActivity implements View.OnClickListener{

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_popup_web_view);

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient() {

            });
            webView.setWebViewClient( new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // Here put your code

                    if ( url.startsWith("nearhere://viewPost?postID=") )
                    {
                        String params = url.substring( url.indexOf("?") + 1);
                        String[] paramAr = params.split("&");
                        for ( int i = 0; i < paramAr.length; i++ )
                        {
                            if ( paramAr[i].indexOf("=") >= 0 )
                            {
                                String key = paramAr[i].split("=")[0];
                                String value = paramAr[i].split("=")[1];

                                if ( key != null && key.equals("postID"))
                                {
                                    Intent intent = new Intent( getApplicationContext(), TaxiPostDetailActivity.class);
                                    intent.putExtra("postID", value );
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                                }
                            }
                        }

                        return true;
                    }

                    return true; //Allow WebView to load url
                }
            });

            if ( getIntent() != null && getIntent().getExtras() != null )
            {
                if ( getIntent().getExtras().containsKey("title") )
                    setTitle( getIntent().getExtras().get("title").toString() );

                if ( getIntent().getExtras().containsKey("url") )
                    webView.loadUrl( getIntent().getExtras().getString("url"));
            }

            Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(this);
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }

    @Override
    public void onBackPressed() {
        try
        {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }
        catch( Exception ex )
        {

        }
    }

    @Override
    public void onClick(View v) {
        try {
            if ( v.getId() == R.id.btnRefresh )
                webView.reload();
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }
}
