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
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.tessoft.common.CommonWebViewClient;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.fragments.DatePickerFragment;
import com.tessoft.nearhere.fragments.TimePickerFragment;

import org.codehaus.jackson.type.TypeReference;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PopupWebViewActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener{

    private WebView webView = null;
    private String pageID = "";
    private int RESULT_LOAD_IMAGE = 3;

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if ( Constants.BROADCAST_REFRESH.equals( intent.getAction() ) )
                {
                    webView.reload();
                }
                else if ( Constants.BROADCAST_REFRESH_PAGE.equals( intent.getAction() ))
                {
                    if ( intent.getExtras() != null && pageID.equals(intent.getExtras().get("broadcastParam")) )
                        webView.reload();
                }
                else if ( Constants.BROADCAST_FINISH_ACTIVITY.equals( intent.getAction() ))
                {
                    if ( intent.getExtras() != null && pageID.equals(intent.getExtras().get("broadcastParam")) )
                        finish();
                }
            }
            catch( Exception ex )
            {
                catchException(this, ex);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_popup_web_view);

            CommonWebViewClient commonWebViewClient = new CommonWebViewClient(this, application );

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.setInitialScale(1);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);

            webView.setWebChromeClient(new WebChromeClient() {

            });

            webView.setBackgroundColor(0);

            if (getIntent() != null && getIntent().getExtras() != null )
            {
                if ( getIntent().getExtras().containsKey("title") )
                    setTitle( getIntent().getExtras().get("title").toString() );

                if ( getIntent().getExtras().containsKey("fullURL") ) {
                    String url = getIntent().getExtras().getString("fullURL");
                    webView.loadUrl( url );
                }
                else if ( getIntent().getExtras().containsKey("url") ) {
                    String url = getIntent().getExtras().getString("url");

                    if ( url.indexOf("?") >= 0 )
                        webView.loadUrl( url + "&isApp=Y");
                    else
                        webView.loadUrl( getIntent().getExtras().getString("url") + "?isApp=Y");
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

                if ( !getIntent().getExtras().containsKey("disableWebViewClient")) {
                    webView.setWebViewClient(commonWebViewClient);
                    webView.addJavascriptInterface(commonWebViewClient, "Android");
                }

                if ( "Y".equals( getIntent().getExtras().getString("titleBarHidden") ) )
                    findViewById(R.id.titleBar).setVisibility(ViewGroup.GONE);
                else
                    findViewById(R.id.titleBar).setVisibility(ViewGroup.VISIBLE);

                if ( getIntent().getExtras().containsKey("pageID") )
                    pageID =  getIntent().getExtras().get("pageID").toString();
            }

            Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(this);

            registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_REFRESH));
            registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_REFRESH_PAGE));
            registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_FINISH_ACTIVITY));
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
                webView.loadUrl("javascript:getNewPostURL();");
            }
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }

    @Override
    public void doAction(String actionName, Object param) {
        super.doAction(actionName, param);

        if ( Constants.ACTION_SET_NEW_POST_URL.equals( actionName ) )
        {
            Intent intent = new Intent(this, NewTaxiPostActivity2.class);
            intent.putExtra("url", param.toString() );
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
        }
        else if ( "showNewButton".equals( actionName ) )
        {
            if ( "Y".equals( param ) )
                findViewById(R.id.btnAddPost).setVisibility(ViewGroup.VISIBLE);
            else
                findViewById(R.id.btnAddPost).setVisibility(ViewGroup.GONE);
        }
        else if ("finishActivity".equals( actionName ))
        {
            if ( !Util.isEmptyString( param.toString() ) )
            {
                sendBroadcast( new Intent( param.toString() ) );
            }

            finish();
        }
        else if ("finishActivity2".equals(actionName))
        {
            if ( !Util.isEmptyString( param.toString() ) )
            {
                handleJSONParam( param.toString() );
            }

            finish();
        }
        else if ("sendBroadcasts".equals(actionName))
        {
            if ( !Util.isEmptyString( param.toString() ) )
            {
                handleJSONParam( param.toString() );
            }
        }
        else if ("selectPhotoUpload".equals(actionName))
        {
            if ( !Util.isEmptyString( param.toString() ) )
            {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
            }
        }
        else if ( Constants.SHOW_PROGRESS_BAR.equals( actionName ) )
            findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
        else if ( Constants.HIDE_PROGRESS_BAR.equals( actionName ) )
            findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
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
    }

    public void handleJSONParam( String jsonString )
    {
        try
        {
            HashMap param = mapper.readValue(jsonString, new TypeReference<HashMap>(){});

            if ( !Util.isEmptyForKey(param, "broadcastList") )
            {
                List<HashMap> broadcastList = (List<HashMap>) param.get("broadcastList");

                for ( int i = 0; i < broadcastList.size(); i++ )
                {
                    Intent intent = new Intent( Util.getStringFromHash( broadcastList.get(i), "broadcastName") );
                    intent.putExtra("broadcastParam", Util.getStringFromHash( broadcastList.get(i), "broadcastParam") );
                    sendBroadcast(intent);
                }
            }
        }
        catch( Exception ex )
        {
            catchException(null, ex);
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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        // TODO Auto-generated method stub
        String dateString = Util.getDateStringFromDate(new Date(year - 1900, monthOfYear, dayOfMonth), "yyyy-MM-dd");
        webView.loadUrl("javascript:onDateSet('" + dateString + "');");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        Date date = new Date();
        date.setHours( hourOfDay );
        date.setMinutes( minute );

        String timeString = Util.getDateStringFromDate( date, "HH:mm" );
        webView.loadUrl("javascript:onTimeSet('" + timeString + "');");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mMessageReceiver);
    }
}
