package com.tessoft.common;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tessoft.nearhere.R;
import com.tessoft.nearhere.activities.BaseActivity;
import com.tessoft.nearhere.activities.TaxiPostDetailActivity;
import com.tessoft.nearhere.activities.UserProfileActivity;

import org.codehaus.jackson.map.ObjectMapper;

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
}
