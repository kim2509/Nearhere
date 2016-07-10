package com.tessoft.nearhere.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.tessoft.common.CommonWebViewClient;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.Post;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.fragments.DatePickerFragment;
import com.tessoft.nearhere.fragments.TimePickerFragment;

import java.util.Date;
import java.util.HashMap;

public class NewTaxiPostActivity2 extends BaseActivity
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener {

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_taxi_post2);

            CommonWebViewClient commonWebViewClient = new CommonWebViewClient( this, application );

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient() {
            });
            webView.addJavascriptInterface(commonWebViewClient, "Android");
            webView.setWebViewClient(commonWebViewClient);

            setTitle("합승 등록");

            if ( getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("mode"))
            {
                if ( "modify".equals( getIntent().getExtras().getString("mode") ) ) {
                    String postID = "";

                    if ( getIntent().getExtras().containsKey("post") && getIntent().getExtras().get("post") != null )
                    {
                        postID = ((Post) getIntent().getExtras().get("post") ).getPostID();

                        setTitle("합승 수정");
                    }

                    webView.loadUrl(Constants.getServerURL() + "/taxi/editPost.do?isApp=Y&postID=" + postID );
                }
                else {
                    webView.loadUrl(Constants.getServerURL() + "/taxi/newPost.do?isApp=Y");
                }
            }
            else if ( getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("url") )
                webView.loadUrl( getIntent().getExtras().getString("url") );
            else
                webView.loadUrl(Constants.getServerURL() + "/taxi/newPost.do?isApp=Y");

            Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(this);

            registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_SET_DESTINATION));
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if ( intent.getAction().equals(Constants.BROADCAST_SET_DESTINATION))
                {
                    if ( intent.getExtras() != null )
                    {
                        String param = intent.getExtras().getString("param");

                        HashMap resultData = (HashMap) intent.getExtras().get("resultData");

                        String name = resultData.get("name").toString();
                        String address = resultData.get("address").toString();
                        String latitude = resultData.get("latitude").toString();
                        String longitude = resultData.get("longitude").toString();

                        webView.loadUrl("javascript:setLocation('" +
                                param + "','" + name + "','" + address + "','" + latitude + "','" + longitude + "');");
                    }
                }
            }
            catch( Exception ex )
            {
                catchException(this, ex);
            }
        }
    };

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
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_to_bottom);
    }

    @Override
    public void doAction(String actionName, Object param) {
        super.doAction(actionName, param);

        if ( Constants.ACTION_SEARCH_MAP.equals( actionName ) )
        {
            Intent intent = new Intent( this, SearchMapActivity.class );
            intent.putExtra("broadcastKey", Constants.BROADCAST_SET_DESTINATION);
            intent.putExtra("param", param.toString());
            startActivity(intent);
        }
        else if ( Constants.ACTION_OPEN_DATE_PICKER.equals( actionName ) )
        {
            DialogFragment newFragment = new DatePickerFragment( this );
            newFragment.show(getFragmentManager(), "datePicker");
        }
        else if ( Constants.ACTION_OPEN_TIME_PICKER.equals( actionName ) )
        {
            DialogFragment newFragment = new TimePickerFragment( this );
            newFragment.show(getFragmentManager(), "timePicker");
        }
        else if ( Constants.ACTION_FINISH_ACTIVITY.equals( actionName ) )
        {
            if ( "refresh".equals( param ) )
                sendBroadcast(new Intent(Constants.BROADCAST_REFRESH));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO Auto-generated method stub
        String dateString = Util.getDateStringFromDate(new Date(year - 1900, monthOfYear, dayOfMonth), "yyyy-MM-dd");
        webView.loadUrl("javascript:setDepartureDate('" + dateString + "');");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        Date date = new Date();
        date.setHours( hourOfDay );
        date.setMinutes( minute );

        String timeString = Util.getDateStringFromDate( date, "HH:mm" );
        webView.loadUrl("javascript:setDepartureTime('" + timeString + "');");
    }
}
