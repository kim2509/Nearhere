package com.tessoft.nearhere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

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
                    else if ( url.startsWith("nearhere://openUserProfile?userID=") )
                    {
                        String params = url.substring( url.indexOf("?") + 1);
                        String[] paramAr = params.split("&");
                        for ( int i = 0; i < paramAr.length; i++ )
                        {
                            if ( paramAr[i].indexOf("=") >= 0 )
                            {
                                String key = paramAr[i].split("=")[0];
                                String value = paramAr[i].split("=")[1];

                                if ( key != null && key.equals("userID"))
                                {
                                    Intent intent = new Intent( getApplicationContext(), UserProfileActivity.class);
                                    intent.putExtra("userID", value );
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
                                }
                            }
                        }
                        return true;
                    }
                    else if ( url.startsWith("nearhere://snsLogin") )
                    {
                        showYesNoDialog("확인", "SNS 계정으로 로그인하시겠습니까?", "snsLogin" );
                    }
                    else if ( url.startsWith("nearhere://showOKDialog?") )
                    {
                        String title = "";
                        String message = "";
                        String param = "";

                        String params = url.substring( url.indexOf("?") + 1);
                        String[] paramAr = params.split("&");
                        for ( int i = 0; i < paramAr.length; i++ )
                        {
                            if ( paramAr[i].indexOf("=") >= 0 && paramAr[i].split("=").length > 1)
                            {
                                String key = paramAr[i].split("=")[0];
                                String value = paramAr[i].split("=")[1];

                                if ( key != null && key.equals("title"))
                                    title = java.net.URLDecoder.decode(value);
                                if ( key != null && key.equals("message"))
                                    message = java.net.URLDecoder.decode(value);
                                if ( key != null && key.equals("param"))
                                    param = java.net.URLDecoder.decode(value);
                            }
                        }

                        showOKDialog( title, message, param );
                    }

                    return true; //Allow WebView to load url
                }
            });

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
                    if ( "Y".equals( getIntent().getExtras().getString("showNewButton") ) )
                        findViewById(R.id.btnAddPost).setVisibility(ViewGroup.VISIBLE);
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
