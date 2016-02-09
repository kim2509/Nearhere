package com.tessoft.nearhere.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LimitedAgeMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.Constants;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.Util;
import com.tessoft.domain.User;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class BaseActivity extends FragmentActivity implements TransactionDelegate, AdapterDelegate {

	ObjectMapper mapper = new ObjectMapper();
	public NearhereApplication application = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (NearhereApplication) getApplication();

		Constants.bReal = true;

		checkIfAdminUser();

		initImageLoader();
	}

	public void checkIfAdminUser()
	{
		try
		{
			File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

			//Get the text file
			File file = new File(sdcard,"nearhere.txt");

			if ( !file.exists() ) return;

			//Read text from file
			StringBuilder text = new StringBuilder();

			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
			br.close();

			String loginInfo = text.toString();

			if ( Util.isEmptyString(loginInfo) ) return;

			String[] tokens = loginInfo.split("\\;");

			String userNo = "";
			String userID = "";
			String pw = "";
			String pushOffOnNewPost = "";
			String server = "";

			for ( int i = 0; i < tokens.length; i++ )
			{
				String key = tokens[i].split("\\=")[0];
				String value = tokens[i].split("\\=")[1];
				if ( "userNo".equals( key ) )
					userNo = value;
				else if ( "userID".equals( key ) )
					userID = value;
				else if ( "pw".equals( key ) )
					pw = value;
				else if ( "pushOffOnNewPost".equals( key ) )
					pushOffOnNewPost = value;
				else if ( "server".equals( key ) )
					server = value.trim();
			}

			if (!"이근처합승".equals(pw.trim()))
			{
				Constants.bAdminMode = false;
				return;
			}

			if ( "Y".equals( pushOffOnNewPost.trim() ) ) Constants.bPushOffOnNewPost = true;
			else Constants.bPushOffOnNewPost = false;

			if ( "REAL".equals( server ) )
				Constants.bReal = true;
			else if ( "DEV".equals( server ) )
				Constants.bReal = false;
			else
				Constants.bReal = true;

			Constants.bAdminMode = true;

			User user = application.getLoginUser();
			user.setUserNo(userNo);
			user.setUserID(userID);
			application.setLoginUser(user);

			application.setMetaInfo("registerUserFinished", "true");
			application.setMetaInfo("logout", "false");
		}
		catch( Exception ex )
		{

		}
	}

	public void showOKDialog( String message, final Object param )
	{
		showOKDialog("확인", message, param);
	}
	
	public void showOKDialog( String title, String message, final Object param )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle( title )
			   .setMessage( message )
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                okClicked( param );
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void okClicked( Object param )
	{
		
	}
	
	public void showYesNoDialog( String title, String message, final Object param )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title)
			   .setMessage( message )
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   yesClicked( param );
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   noClicked( param );
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void yesClicked( Object param )
	{
		
	}
	
	public void noClicked( Object param )
	{
		
	}
	
	public double getMetaInfoDouble( String key )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
		if ( settings.contains(key) )
			return Double.parseDouble( settings.getString(key, "0") );
		else
			return 0;
	}
	
	public void writeLog( String log )
	{
		Log.e("NearHereHelp", log );
	}
	
	public void sendHttp( String url, Object request, int requestCode )
	{
		new HttpTransactionReturningString( this, url, requestCode ).execute( request );
	}
	
	public void doPostTransaction( int requestCode, Object result )
	{
		
	}
	
	public void catchException ( Object target, Exception ex )
	{
		if ( ex == null )
			writeLog( "[" + target.getClass().getName() + "] NullPointerException!!!" );
		else
			Log.e("이근처", "exception", ex);
	}
	
	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		if ("logException".equals( actionName ) )
			Log.e("이근처", "exception", (Exception) param);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	public static boolean bInitImageLoader = false;
	public void initImageLoader()
	{
		if ( BaseActivity.bInitImageLoader == false )
		{
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
			.cacheInMemory(true)
			.delayBeforeLoading(100)
			.build();
			
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.memoryCacheExtraOptions(100, 100) // default = device screen dimensions
			.diskCacheExtraOptions(100, 100, null)
			.threadPoolSize(3) // default
			.threadPriority(Thread.NORM_PRIORITY - 2) // default
			.tasksProcessingOrder(QueueProcessingType.FIFO) // default
			.denyCacheImageMultipleSizesInMemory()
//			.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
			.memoryCache(new LimitedAgeMemoryCache(new LruMemoryCache(2 * 1024 * 1024), 60 ))
			.memoryCacheSize(2 * 1024 * 1024)
			.memoryCacheSizePercentage(13) // default
			.diskCacheSize(50 * 1024 * 1024)
			.diskCacheFileCount(100)
			.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
			.defaultDisplayImageOptions(options) // default
			.writeDebugLogs()
			.build();
			ImageLoader.getInstance().init(config);
			BaseActivity.bInitImageLoader = true;
		}
	}
	
	public void goMainActivity()
	{
		try
		{
			Intent intent = new Intent( this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, 
					android.R.anim.fade_out);
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	public void goKaKaoLoginActivity() {
		Intent intent = new Intent( getApplicationContext(), KakaoLoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, 
				android.R.anim.fade_out);
	}
	
	protected void setTitle( String title ) {
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		findViewById(R.id.txtTitle).setVisibility(ViewGroup.VISIBLE);
		txtTitle.setText( title );
	}

	public String getMetaInfoString( String key )
	{
		return application.getMetaInfoString( key );
	}

	public void setMetaInfoString( String key, String value )
	{
		application.setMetaInfo(key, value);
	}
}
