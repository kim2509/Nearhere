package com.tessoft.nearhere;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tessoft.common.AddressTaskDelegate;
import com.tessoft.common.Constants;
import com.tessoft.common.GetAddressTask;
import com.tessoft.common.Util;
import com.tessoft.domain.User;
import com.tessoft.domain.UserLocation;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import java.util.HashMap;

public class LocationUpdateService extends Service 
	implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, AddressTaskDelegate{

	GoogleApiClient mGoogleApiClient = null;
	ObjectMapper mapper = null;
	NearhereApplication application = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		try
		{
			super.onCreate();
			
			application = (NearhereApplication) getApplication();
			
			if ( application.isGooglePlayServicesAvailable() == false || application.checkIfGPSEnabled() == false )
			{
				stopSelf();
				return;
			}
			
	        buildGoogleApiClient();
	        
	        createLocationRequest();
	        
	    	mapper = new ObjectMapper();			
		}
		catch( Exception ex )
		{
			
		}
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
		}
		catch( Exception ex )
		{
		}
		
        return START_STICKY;
	}

	@Override
	public void onDestroy() { 

		try
		{
			if ( mGoogleApiClient != null && mGoogleApiClient.isConnected() )
			{
				stopLocationUpdates();
				mGoogleApiClient.disconnect();
			}
		}
		catch( Exception ex )
		{
			
		}
		
		super.onDestroy();
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
			NearhereApplication.latitude = String.valueOf( location.getLatitude() );
			NearhereApplication.longitude = String.valueOf( location.getLongitude() );
			
			new GetAddressTask( this, this, 1 ).execute(location);
		}
		catch( Exception ex )
		{
		}
	}
	
	@Override
	public void onAddressTaskPostExecute(int requestCode, Object result) {
		// TODO Auto-generated method stub
		try
		{
			if ( result != null && result instanceof HashMap)
			{
				HashMap resultMap = (HashMap) result;
				if ( resultMap.containsKey("address") && resultMap.get("address") != null )
				{
					NearhereApplication.address = Util.getDongAddressString( resultMap.get("address").toString() );
					User user = application.getLoginUser();
					UserLocation userLocation = new UserLocation();
					userLocation.setUser(user);
					userLocation.setLocationName("현재위치");
					userLocation.setLatitude(NearhereApplication.latitude);
					userLocation.setLongitude(NearhereApplication.longitude);
					userLocation.setAddress(NearhereApplication.address);

					Intent broadcastIntent = new Intent( Constants.BROADCAST_LOCATION_UPDATED );
					broadcastIntent.putExtra("latitude", NearhereApplication.latitude );
					broadcastIntent.putExtra("longitude", NearhereApplication.longitude );
					broadcastIntent.putExtra("address", NearhereApplication.address );
					sendBroadcast(broadcastIntent);

					application.sendHttp("/taxi/updateUserLocation.do", mapper.writeValueAsString( userLocation ),
							Constants.HTTP_UPDATE_LOCATION, null );
				}
			}
		}
		catch( Exception ex )
		{
			application.catchException(null, ex);
		}
		finally
		{
			stopSelf();
		}
	}
}