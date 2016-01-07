package com.tessoft.nearhere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.tessoft.common.AddressTaskDelegate;
import com.tessoft.common.GetAddressTask;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.Util;
import com.tessoft.domain.Contact;
import com.tessoft.nearhere.activities.SafetyKeeperActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SafetyKeeperService extends Service 
	implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, AddressTaskDelegate, TransactionDelegate {

	protected static final int HTTP_SAFETY_KEEPER_STARTED = 10;
	protected static final int HTTP_SAFETY_KEEPER_FINISHED = 20;
	private NotificationManager mNM;
	GoogleApiClient mGoogleApiClient = null;
	TimerTask mTask = null;
	Timer mTimer = null;
	TimerTask exitTask = null;
	Timer exitTimer = null;
	String latitude = "";
	String longitude = "";
	public static String address = "";
	SafetyKeeperHandler mainHandler = null;
	public static ArrayList<Contact> arContacts = null;
	Intent intent = null;
	public static String message = "";
	public static int totalCount = 0;
	public static int minutes = 0;
	public static int sentCount = 0;
	public static int exitHour = 0;
	ObjectMapper mapper = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		// Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
        
        buildGoogleApiClient();
        
        createLocationRequest();
        
        mainHandler = new SafetyKeeperHandler();
        
        mTask = new TimerTask() {
            @Override
            public void run() {
                try
                {
                	Message msg = mainHandler.obtainMessage();
                	msg.what = 1;
                	mainHandler.sendMessage(msg);
                }
                catch( Exception ex )
                {
                }
            }
        };
        
        mTimer = new Timer();
        
        exitTask = new TimerTask() {
            @Override
            public void run() {
                try
                {
                	stopSelf();
                }
                catch( Exception ex )
                {
                }
            }
        };
        
        exitTimer = new Timer();
        
        Intent intent = new Intent("safetyKeeperStarted");
    	sendBroadcast(intent);
    	
    	mapper = new ObjectMapper();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		try
		{
			// We want this service to continue running until it is explicitly
	        // stopped, so return sticky.
			if ( mGoogleApiClient != null && mGoogleApiClient.isConnected() == false )
				mGoogleApiClient.connect();
			
			this.intent = intent;
			
			totalCount = sentCount = 0;
			bStarted = false;
			
			if ( intent != null && intent.getExtras() != null )
			{
				if ( intent.getExtras().containsKey("totalCount") && intent.getExtras().get("totalCount") != null &&
						intent.getExtras().containsKey("minutes") && intent.getExtras().get("minutes") != null)
				{
					totalCount = Integer.parseInt(intent.getExtras().getString("totalCount"));
					minutes = Integer.parseInt(intent.getExtras().getString("minutes"));
				}
				
				if ( intent.getExtras().containsKey("contacts") && intent.getExtras().get("contacts") != null )
				{
					arContacts = (ArrayList<Contact>) intent.getExtras().get("contacts");
				}
				
				if ( intent.getExtras().containsKey("exitHour") && intent.getExtras().get("exitHour") != null )
				{
					exitHour = Integer.parseInt(intent.getExtras().getString("exitHour"));
				}
			}
			
			//자동종료처리
			exitTimer.schedule( exitTask , 1000 * 60 * 60 * exitHour );
			
			HashMap hash = new HashMap();
			hash.put("userID", intent.getExtras().get("userID"));
			hash.put("totalCount", totalCount);
			hash.put("minutes", minutes);
			hash.put("contacts", arContacts.size());
			hash.put("exitHour", exitHour);
			hash.put("sentCount", sentCount);
			sendHttp("/taxi/statistics.do?name=safetyKeeperStarted", mapper.writeValueAsString( hash ), HTTP_SAFETY_KEEPER_STARTED );
		}
		catch( Exception ex )
		{
			showToastMessage("시작하는 도중에 오류가 발생했습니다.");
		}
		
        return START_STICKY;
	}

	@Override
	public void onDestroy() { 

		try
		{
			Toast.makeText(this, "서비스를 종료합니다.", Toast.LENGTH_LONG).show();

			if ( mGoogleApiClient != null && mGoogleApiClient.isConnected() )
			{
				stopLocationUpdates();
				mGoogleApiClient.disconnect();
			}
			
			if ( mTimer != null )
			{
				mTimer.cancel();
			}
			
			mNM.cancel(1);
			
			HashMap hash = new HashMap();
			hash.put("userID", intent.getExtras().get("userID"));
			hash.put("totalCount", totalCount);
			hash.put("minutes", minutes);
			hash.put("sentCount", sentCount);
			hash.put("contacts", arContacts.size());
			hash.put("exitHour", exitHour);
			sendHttp("/taxi/statistics.do?name=safetyKeeperFinished", mapper.writeValueAsString( hash ), HTTP_SAFETY_KEEPER_STARTED );
			
			Intent intent = new Intent("safetyKeeperFinished");
        	sendBroadcast(intent);
		}
		catch( Exception ex )
		{
			
		}
		
		super.onDestroy();
	}

	// Handler 클래스
    class SafetyKeeperHandler extends Handler {
         
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             
            try
            {
            	switch (msg.what) {
                case 1:
                	
                	sendSMS();
                	
                    break;
                     
                case 2:
                    break;
     
                default:
                    break;
                }            	
            }
            catch( Exception ex )
            {
            	
            }
        }
         
    };
    
    @SuppressWarnings("unchecked")
	public void sendSMS() throws Exception
    {
    	if ( intent == null || intent.getExtras() == null ||
    			!intent.getExtras().containsKey("contacts") ||
    			!intent.getExtras().containsKey("message") ) return;
    	
    	message = intent.getExtras().get("message").toString();
    	String userName = intent.getExtras().get("userName").toString();
    	
    	String resultMessage = "";
    	
    	// 전송횟수 도달
    	if ( totalCount <= sentCount ) 
    	{
    		stopSelf();
    		return;
    	}
    	
    	for ( int i = 0; i < arContacts.size(); i++ )
    	{
    		Contact contact = arContacts.get(i);
    		SmsManager sms = SmsManager.getDefault();
    		if ( Util.isEmptyString(contact.getNumber()) ) continue;
    	
    		String number = contact.getNumber();
    		number = number.replaceAll("\\-", "");
    		
    		if ( Util.isEmptyString(address) ) address = "[GPS 꺼져있음]";
    		
    		String sendMessage = "[이근처 합승-" + userName + "님] "+ message + " [" + address.replaceAll("\\|", " ") + "]";
			sms.sendTextMessage( number , null, sendMessage , null, null);
			
			resultMessage += contact.getName() + ",";
    	}

    	if ( arContacts.size() > 0 )
    	{
    		sentCount++;
    		Intent sendIntent = new Intent("sentSMS");
			sendBroadcast(sendIntent);
    	}
    	
    	resultMessage = resultMessage.substring(0, resultMessage.length() - 1 );
    	showToastMessage(resultMessage + "님께 메시지를 전송했습니다.");
    }
    
	/**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "안심귀가알리미가 실행중입니다.";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        notification.flags = Notification.FLAG_ONGOING_EVENT;
        
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SafetyKeeperActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "안심귀가알리미",
                       text, contentIntent);

        // Send the notification.
        mNM.notify(1, notification);
    }
    
    protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();
	}
    
    LocationRequest mLocationRequest = null;
	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(90000);
		mLocationRequest.setFastestInterval(90000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		try
		{
			startLocationUpdates();
		}
		catch( Exception ex )
		{

		}
	}
	
	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}
    
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		try
		{
			latitude = String.valueOf( location.getLatitude() );
			longitude = String.valueOf( location.getLongitude() );
			
			new GetAddressTask( this, this, 1 ).execute(location);
		}
		catch( Exception ex )
		{
		}
	}
	
	boolean bStarted = false;
	
	@Override
	public void onAddressTaskPostExecute(int requestCode, Object result) {
		// TODO Auto-generated method stub
		if ( result != null && result instanceof HashMap)
		{
			HashMap resultMap = (HashMap) result;
			if ( resultMap.containsKey("address") && resultMap.get("address") != null )
			{
				address = resultMap.get("address").toString();
			}
			
			if ( bStarted == false )
			{
				mTimer.schedule(mTask, 2000, 1000 * 60 * minutes );
				bStarted = true;
			}
		}
	}
	
	public void showToastMessage( String msg )
	{
		Toast.makeText(this, msg , Toast.LENGTH_LONG).show();
	}
	
	public void sendHttp( String url, Object request, int requestCode )
	{
		new HttpTransactionReturningString( this, url, requestCode ).execute( request );
	}

	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		
	}
}