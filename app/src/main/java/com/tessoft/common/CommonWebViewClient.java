package com.tessoft.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tessoft.nearhere.NearhereApplication;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;

/**
 * Created by Daeyong on 2016-01-30.
 */
public class CommonWebViewClient extends WebViewClient {

    private AdapterDelegate delegate = null;
    private NearhereApplication application = null;

    public CommonWebViewClient( AdapterDelegate delegate, NearhereApplication application )
    {
        super();
        this.delegate = delegate;
        this.application = application;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

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
                        delegate.doAction("viewPost", value);
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
                        delegate.doAction("openUserProfile", value);
                    }
                }
            }
            return true;
        }
        else if ( url.startsWith("nearhere://snsLogin") )
        {
            delegate.doAction("snsLogin", null );
            return true;
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

            HashMap<String, String> hash = new HashMap<String, String>();
            hash.put("title", title);
            hash.put("message", message );
            hash.put("param", param );
            delegate.doAction("showDialog", hash );

            return true;
        }
        else if ( url.startsWith("nearhere://clearHistory") )
        {
            view.clearHistory();
            return true;
        }
        else if ( url.startsWith("nearhere://openPhotoViewer?url=") ||
                url.startsWith("nearhere://openExternalURL?url="))
        {
            String params = url.substring( url.indexOf("?") + 1);
            String[] paramAr = params.split("&");
            for ( int i = 0; i < paramAr.length; i++ ) {
                if (paramAr[i].indexOf("=") >= 0) {
                    String key = paramAr[i].split("=")[0];
                    String value = paramAr[i].split("=")[1];

                    if (key != null && key.equals("url")) {
                        if (url.startsWith("nearhere://openPhotoViewer?url="))
                        {
                            delegate.doAction("openPhotoViewer", value );
                        }
                        else if ( url.startsWith("nearhere://openExternalURL?url=") ) {
                            delegate.doAction("openExternalURL", value );
                        }
                    }
                }
            }

            return true;
        }
        else if (url.startsWith("nearhere://openURL?")) {

            delegate.doAction("openURL", url );

            return true;
        }
        else if ( url.startsWith("nearhere://goUserMessageActivity") )
        {
            delegate.doAction("goUserMessageActivity", url);

            return true;
        }

        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        delegate.doAction(Constants.SHOW_PROGRESS_BAR, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        delegate.doAction(Constants.HIDE_PROGRESS_BAR, url );
    }

    @JavascriptInterface
    public String getMetaInfoString(String name )
    {
        return delegate.getStringValueForKey(name);
    }

    @JavascriptInterface
    public String getUserID()
    {
        return application.getLoginUser().getUserID();
    }

    @JavascriptInterface
    public String getUserType()
    {
        return application.getLoginUser().getType();
    }

    @JavascriptInterface
    public void setMetaInfoString( String name, String value )
    {
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("name", name );
        hash.put("value", value );
        delegate.doAction("setMetaInfoString", hash );
    }

    @JavascriptInterface
    public void openMap( String param )
    {
        delegate.doAction( Constants.ACTION_SEARCH_MAP, param);
    }

    @JavascriptInterface
    public void openDatePicker()
    {
        delegate.doAction( Constants.ACTION_OPEN_DATE_PICKER, null );
    }

    @JavascriptInterface
    public void openTimePicker()
    {
        delegate.doAction( Constants.ACTION_OPEN_TIME_PICKER, null );
    }

    @JavascriptInterface
    public void showOKDialog( String title, String message, String param )
    {
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("title", title );
        hash.put("message", message );
        hash.put("param", param );
        delegate.doAction("showOKDialog", hash );
    }

    @JavascriptInterface
    public void finishActivity( String param )
    {
        if ( "refresh".equals( param ) )
            delegate.doAction( Constants.ACTION_FINISH_ACTIVITY, param);
        else
            delegate.doAction( "finishActivity", param);
    }

    @JavascriptInterface
    public void finishActivity2( String param )
    {
        delegate.doAction( "finishActivity2", param);
    }

    @JavascriptInterface
    public void sendBroadcasts( String param )
    {
        delegate.doAction( "sendBroadcasts", param);
    }


    @JavascriptInterface
    public void selectPhotoUpload( String param )
    {
        delegate.doAction( "selectPhotoUpload", param);
    }


    @JavascriptInterface
    public String getDefaultInfo()
    {
        try
        {
            HashMap hash = application.getDefaultRequest();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(hash);
        }
        catch( Exception ex )
        {

        }
        return "";
    }

    @JavascriptInterface
    public String getCurrentLocationInfo()
    {
        try
        {
            HashMap hash = new HashMap();
            hash.put("latitude", NearhereApplication.latitude );
            hash.put("longitude", NearhereApplication.longitude );
            hash.put("address", NearhereApplication.address );

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(hash);
        }
        catch( Exception ex )
        {

        }
        return "";
    }

    @JavascriptInterface
    public void setNewPostURL( String url )
    {
        delegate.doAction( Constants.ACTION_SET_NEW_POST_URL , url );
    }

    @JavascriptInterface
    public void refreshFriendTabCount()
    {
        Intent intent = new Intent("updateUnreadCount");
        intent.putExtra("type", "friendRequest" );
        delegate.doAction("sendBroadCast", intent );
    }

    @JavascriptInterface
    public void showNewButton( String visibleYN )
    {
        delegate.doAction("showNewButton", visibleYN );
    }

    @JavascriptInterface
    public void respondLocationRequest()
    {
        delegate.doAction("respondLocationRequest", null );
    }
}
