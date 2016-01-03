package com.tessoft.nearhere;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tessoft.common.AddressTaskDelegate;
import com.tessoft.common.Constants;
import com.tessoft.common.GetAddressTask;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.Util;
import com.tessoft.domain.User;
import com.tessoft.domain.UserLocation;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service
		implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, TransactionDelegate , AddressTaskDelegate{

	private NotificationManager mNM;
	ObjectMapper mapper = null;
	GoogleApiClient mGoogleApiClient = null;
	String locationID = "";
	NearhereApplication application = null;

	Timer exitTimer = null;
	TimerTask exitTask = null;

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

		mapper = new ObjectMapper();

		application = (NearhereApplication) getApplication();
		NearhereApplication.bLocationServiceExecuting = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		try
		{
			if ( intent != null && intent.getExtras().containsKey("locationID") )
			{
				locationID = intent.getExtras().getString("locationID");
			}
			// We want this service to continue running until it is explicitly
			// stopped, so return sticky.
			if ( mGoogleApiClient != null && mGoogleApiClient.isConnected() == false )
				mGoogleApiClient.connect();

			//자동종료처리
			exitTimer = new Timer();
			exitTask = new TimerTask() {
				@Override
				public void run() {
					try
					{
						Intent intent = new Intent( Constants.BROADCAST_STOP_LOCATION_SERVICE );
						sendBroadcast(intent);
					}
					catch( Exception ex )
					{
					}
				}
			};
			exitTimer.schedule( exitTask , 1000 * 60 * 60 );
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

			mNM.cancel(1);

			NearhereApplication.bLocationServiceExecuting = false;
		}
		catch( Exception ex )
		{

		}

		super.onDestroy();
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = "위치서비스가 실행중입니다.";

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher, text,
				System.currentTimeMillis());

		notification.flags = Notification.FLAG_ONGOING_EVENT;

		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, "위치서비스",
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
		mLocationRequest.setInterval(5000);
		mLocationRequest.setFastestInterval(5000);
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
			/*
			latitude = String.valueOf( location.getLatitude() );
			longitude = String.valueOf( location.getLongitude() );

			showToastMessage(latitude + " lng:" + longitude);
			HashMap request = new HashMap();
			request.put("locationID", locationID);
			request.put("latitude", latitude);
			request.put("longitude", longitude);
			sendHttp("/location/insertLocationDetail.do", mapper.writeValueAsString(request), Constants.HTTP_ADD_LOCATION);
			*/

			new GetAddressTask( getApplicationContext(), this, 1 ).execute(location);
		}
		catch( Exception ex )
		{
		}
	}

	@Override
	public void onAddressTaskPostExecute(int requestCode, Object result) {
		try {
			if ( result != null && result instanceof HashMap)
			{
				HashMap resultMap = (HashMap) result;
				if ( resultMap.containsKey("address") && resultMap.get("address") != null )
				{
					User user = application.getLoginUser();
					UserLocation userLocation = new UserLocation();
					userLocation.setUser(user);
					userLocation.setLocationName("현재위치");
					userLocation.setLatitude(resultMap.get("latitude").toString());
					userLocation.setLongitude(resultMap.get("longitude").toString());
					userLocation.setAddress( Util.getDongAddressString(resultMap.get("address").toString()));

					NearhereApplication.latitude = resultMap.get("latitude").toString();
					NearhereApplication.longitude = resultMap.get("longitude").toString();
					NearhereApplication.address = Util.getDongAddressString(resultMap.get("address").toString());

					sendHttp("/taxi/updateUserLocation.do", mapper.writeValueAsString(userLocation), Constants.HTTP_UPDATE_LOCATION);
				}
			}
		}
		catch( Exception ex )
		{

		}
	}

	public void showToastMessage( String msg )
	{
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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