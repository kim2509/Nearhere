package com.tessoft.nearhere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.tessoft.common.CommonWebViewClient;
import com.tessoft.common.Constants;
import com.tessoft.nearhere.R;

public class PopupWebViewActivity extends BaseActivity implements View.OnClickListener{

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_popup_web_view);

            CommonWebViewClient commonWebViewClient = new CommonWebViewClient(this);

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient() {

            });

            webView.addJavascriptInterface(commonWebViewClient, "Android");
            webView.setWebViewClient(  commonWebViewClient );

            if ( getIntent() != null && getIntent().getExtras() != null )
            {
                if ( getIntent().getExtras().containsKey("title") )
                    setTitle( getIntent().getExtras().get("title").toString() );

                if ( getIntent().getExtras().containsKey("url") ) {
                    String url = getIntent().getExtras().getString("url");
                    if ( url.indexOf("?") >= 0 )
                        webView.loadUrl(getIntent().getExtras().getString("url") + "&isApp=Y");
                    else
                        webView.loadUrl(getIntent().getExtras().getString("url") + "?isApp=Y");
                }

                if ( getIntent().getExtras().containsKey("showNewButton") )
                {
                    if ( "Y".equals( getIntent().getExtras().getString("showNewButton") ) ) {
                        findViewById(R.id.btnAddPost).setVisibility(ViewGroup.VISIBLE);
                        Button btnAddPost = (Button) findViewById(R.id.btnAddPost);
                        btnAddPost.setOnClickListener(this);
                    }
                    else
                        findViewById(R.id.btnAddPost).setVisibility(ViewGroup.GONE);
                }
                else
                    findViewById(R.id.btnAddPost).setVisibility(ViewGroup.GONE);


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
            else if ( v.getId() == R.id.btnAddPost )
            {
                Intent intent = new Intent(this, NewTaxiPostActivity2.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
            }
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }

    @Override
    public void yesClicked(Object param) {

        if ( "snsLogin".equals( param ))
        {
            sendBroadcast(new Intent(Constants.BROADCAST_LOGOUT));
            finish();
        }

        super.yesClicked(param);
    }
}
