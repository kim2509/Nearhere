package com.tessoft.nearhere;

import java.util.HashMap;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.Util;
import com.tessoft.domain.User;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class NearhereApplication extends Application{

	ObjectMapper mapper = new ObjectMapper();
	
	public int getMetaInfoInt( String key )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
		if ( settings.contains(key) )
			return Util.getInt(settings.getString(key, ""));
		return 0;
	}
	
	public String getMetaInfoString( String key )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
		if ( settings.contains(key) )
			return settings.getString(key, "");
		else return "";
	}
	
	public void setMetaInfo( String key, String value )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
		SharedPreferences.Editor editor = settings.edit();
		editor.putString( key, value );
		editor.commit();
	}
	
	public String getUniqueDeviceID()
	{
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid.toString();
	}
	
	public void setLoginUser( User user ) throws Exception
	{
		String loginUserInfo = mapper.writeValueAsString(user);
		loginUserInfo = Util.encodeBase64(loginUserInfo);
		setMetaInfo("loginUserInfo", loginUserInfo );
	}
	
	public User getLoginUser()
	{
		try
		{
			try
			{
				String result = getMetaInfoString("loginUserInfo");
				
				User user = new User();
				
				if ( Util.isEmptyString( result ) )
				{
					// 전환
					user.setUserNo( getMetaInfoString("userNo") );
					user.setUserID( getMetaInfoString("userID") );
					user.setUserName( getMetaInfoString("userName") );
					user.setProfileImageURL( getMetaInfoString("profileImageURL"));
					user.setUuid( getUniqueDeviceID() );
					
					setLoginUser( user );
					
					setMetaInfo("userNo", "");
					setMetaInfo("userID", "");
					setMetaInfo("userName", "");
					setMetaInfo("profileImageURL", "");
					
					return user;
				}
				else
				{
					result = Util.decodeBase64( result );
					
//					debug( this, "loginUser[decoded]:" + result );
					
					user = mapper.readValue( result , new TypeReference<User>(){});	
				}
				
				if ( user == null )
					user = new User();
				
				return user;
			}
			catch( Exception ex )
			{
				catchException(this, ex);
				return new User();
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
			return new User();
		}
	}
	
	public void catchException ( Object target, Exception ex )
	{
		if ( ex == null )
			writeLog( "[" + target.getClass().getName() + "] NullPointerException!!!" );
		else
			Log.e("error", "exception", ex);
	}
	
	public void writeLog( String log )
	{
		Log.e("debug", log );
	}
	
	public void debug( Object obj, String log )
	{
		Log.e("debug", "[" + obj.getClass().getName() + "] " + log );
	}
	
	public void debug( String log )
	{
		Log.e("debug", log );
	}
	
	public void sendHttp( String url, Object request, int requestCode, TransactionDelegate listener )
	{
		new HttpTransactionReturningString( listener, url, requestCode ).execute( request );
	}
	
	public boolean checkIfGPSEnabled()
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		return manager.isProviderEnabled( LocationManager.GPS_PROVIDER );
	}
	
	public boolean isGooglePlayServicesAvailable() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			return false;
		}
		return true;
	}
	
	public void showToastMessage( String message )
	{
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	public HashMap getDefaultRequest()
	{
		HashMap request = new HashMap();
		request.put("OSVersion", getOSVersion());
		request.put("AppVersion", getPackageVersion());
		request.put("UUID", getUniqueDeviceID());
		return request;
	}
	
	public String getOSVersion()
	{
		return Build.VERSION.RELEASE;
	}
	
	public String getPackageVersion()
	{
		PackageInfo pInfo;
		try {
			
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
}
