package com.tessoft.common;

import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.PhotoViewer;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.activities.BaseActivity;
import com.tessoft.nearhere.activities.PopupWebViewActivity;
import com.tessoft.nearhere.activities.TaxiPostDetailActivity;
import com.tessoft.nearhere.activities.UserMessageActivity;
import com.tessoft.nearhere.activities.UserProfileActivity;

import org.codehaus.jackson.map.ObjectMapper;

import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by Daeyong on 2016-01-30.
 */
public class CommonWebViewClient extends WebViewClient {

    private BaseActivity activity = null;

    public CommonWebViewClient( BaseActivity activity )
    {
        super();
        this.activity = activity;
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
                        Intent intent = new Intent( activity, TaxiPostDetailActivity.class);
                        intent.putExtra("postID", value );
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
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
                        Intent intent = new Intent( activity, UserProfileActivity.class);
                        intent.putExtra("userID", value );
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
                    }
                }
            }
            return true;
        }
        else if ( url.startsWith("nearhere://snsLogin") )
        {
            activity.showYesNoDialog("확인", "SNS 계정으로 로그인하시겠습니까?", "snsLogin");

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

            activity.showOKDialog( title, message, param );

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
                            Intent intent = new Intent( activity, PhotoViewer.class);
                            intent.putExtra("imageURL", value );
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.fade_in, R.anim.stay);
                        }
                        else if ( url.startsWith("nearhere://openExternalURL?url=") ) {
                            value = URLDecoder.decode(value);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(value));
                            activity.startActivity(browserIntent);
                        }
                    }
                }
            }

            return true;
        }
        else if (url.startsWith("nearhere://openURL?")) {
            Intent intent = new Intent( activity , PopupWebViewActivity.class);
            String params = url.substring(url.indexOf("?") + 1);

            String[] paramAr = params.split("&");
            for (int i = 0; i < paramAr.length; i++) {
                if (paramAr[i].indexOf("=") >= 0 && paramAr[i].split("=").length > 1) {
                    String key = paramAr[i].split("=")[0];
                    String value = paramAr[i].split("=")[1];

                    if (key != null && key.equals("title")) {
                        intent.putExtra("title", java.net.URLDecoder.decode(value));
                    } else if (key != null && key.equals("url")) {
                        intent.putExtra("url", java.net.URLDecoder.decode(value));
                    } else if (key != null && key.equals("showNewButton")) {
                        intent.putExtra("showNewButton", value);
                    }
                }
            }

            activity.startActivity(intent);

            return true;
        }
        else if ( url.startsWith("nearhere://goUserMessageActivity") )
        {
            Intent intent = new Intent( activity , UserMessageActivity.class);
            String params = url.substring(url.indexOf("?") + 1);

            HashMap hash = new HashMap();

            String[] paramAr = params.split("&");

            for (int i = 0; i < paramAr.length; i++) {
                if (paramAr[i].indexOf("=") >= 0 && paramAr[i].split("=").length > 1) {
                    String key = paramAr[i].split("=")[0];
                    String value = paramAr[i].split("=")[1];

                    if (key != null && key.equals("userID")) {
                        hash.put("fromUserID", value );
                    }
                }
            }

            hash.put("userID",  activity.application.getLoginUser().getUserID() );
            intent.putExtra("messageInfo", hash );
            activity.startActivity(intent);

            return true;
        }

        return false;
    }

    @JavascriptInterface
    public String getMetaInfoString(String name )
    {
        return activity.getMetaInfoString(name);
    }

    @JavascriptInterface
    public String getUserID()
    {
        return activity.application.getLoginUser().getUserID();
    }

    @JavascriptInterface
    public String getUserType()
    {
        return activity.application.getLoginUser().getType();
    }

    @JavascriptInterface
    public void setMetaInfoString( String name, String value )
    {
        activity.setMetaInfoString(name, value);
    }

    @JavascriptInterface
    public void openMap( String param )
    {
        activity.doAction(Constants.ACTION_SEARCH_MAP, param);
    }

    @JavascriptInterface
    public void openDatePicker()
    {
        activity.doAction(Constants.ACTION_OPEN_DATE_PICKER, null);
    }

    @JavascriptInterface
    public void openTimePicker()
    {
        activity.doAction(Constants.ACTION_OPEN_TIME_PICKER, null);
    }

    @JavascriptInterface
    public void showOKDialog( String title, String message, String param )
    {
        activity.showOKDialog(title, message, param);
    }

    @JavascriptInterface
    public void finishActivity( String param )
    {
        activity.doAction(Constants.ACTION_FINISH_ACTIVITY, param);
    }

    @JavascriptInterface
    public String getDefaultInfo()
    {
        try
        {
            HashMap hash = activity.application.getDefaultRequest();
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
        activity.doAction(Constants.ACTION_SET_NEW_POST_URL, url);
    }

    @JavascriptInterface
    public void refreshFriendTabCount()
    {
        Intent intent = new Intent("updateUnreadCount");
        intent.putExtra("type", "friendRequest" );
        activity.sendBroadcast(intent);
    }

    @JavascriptInterface
    public void showNewButton( String visibleYN )
    {
        activity.doAction("showNewButton", visibleYN);
    }

    @JavascriptInterface
    public void respondLocationRequest( String visibleYN )
    {
        activity.doAction("respondLocationRequest", visibleYN );
    }
}
