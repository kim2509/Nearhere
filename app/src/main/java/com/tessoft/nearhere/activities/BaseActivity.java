package com.tessoft.nearhere.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
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
import com.tessoft.nearhere.PhotoViewer;
import com.tessoft.nearhere.R;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

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
			String loginHash = "";

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
				else if ( "devHost".equals( key ) )
					Constants.devHostName = value.trim();
				else if ( "hash".equals( key ) ) {
					loginHash = URLDecoder.decode(value.trim(), "UTF-8");
					String cookieString = "userToken=" + URLEncoder.encode( loginHash, "utf-8");
					CookieManager.getInstance().setCookie(Constants.getServerHost(), cookieString);
				}
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

			if ( !Util.isEmptyString( userNo ) )
				user.setUserNo(userNo);

			if ( !Util.isEmptyString( userID ) )
				user.setUserID(userID);

			if ( !Util.isEmptyString( loginHash ) ) {
				application.setMetaInfo("hash", loginHash);
			}

			if ( !Util.isEmptyString( userNo ) && !Util.isEmptyString( userID ) )
				application.setLoginUser(user);

			application.setMetaInfo("registerUserFinished", "true");
			application.setMetaInfo("logout", "false");
		}
		catch( Exception ex )
		{
			application.showToastMessage(ex.getMessage());
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
		Log.e("NearHereHelp", log);
	}
	
	public void sendHttp( String url, Object request, int requestCode )
	{
		new HttpTransactionReturningString( this, url, requestCode ).execute(request);
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
		txtTitle.setText(title);
	}

	public String getMetaInfoString( String key )
	{
		return application.getMetaInfoString( key );
	}

	public void setMetaInfoString( String key, String value )
	{
		application.setMetaInfo(key, value);
	}

	@Override
	public String getStringValueForKey( String keyName )
	{
		return application.getMetaInfoString( keyName );
	}

	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		if ("logException".equals( actionName ) )
			Log.e("이근처", "exception", (Exception) param);
		else if ( "viewPost".equals( actionName ) )
		{
			Intent intent = new Intent( this, TaxiPostDetailActivity.class);
			intent.putExtra("postID", param.toString() );
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		}
		else if ( "openUserProfile".equals( actionName ) )
		{
			Intent intent = new Intent( this, UserProfileActivity.class);
			intent.putExtra("userID", param.toString() );
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
		}
		else if ( "snsLogin".equals( actionName ) )
		{
			showYesNoDialog("확인", "SNS 계정으로 로그인하시겠습니까?", "snsLogin");
		}
		else if ( ("showDialog".equals( actionName ) || "showOKDialog".equals( actionName )) && param instanceof HashMap)
		{
			HashMap<String, String> hash = (HashMap<String, String>) param;
			showOKDialog(hash.get("title"), hash.get("message"), hash.get("param"));
		}
		else if ( "openPhotoViewer".equals( actionName ) )
		{
			Intent intent = new Intent( this, PhotoViewer.class);
			intent.putExtra("imageURL", param.toString() );
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.stay);
		}
		else if ( "openExternalURL".equals( actionName ) )
		{
			String value = URLDecoder.decode(param.toString());
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(value));
			startActivity(browserIntent);
		}
		else if ( "openURL".equals( actionName ) )
		{
			Intent intent = new Intent( this , PopupWebViewActivity.class);
			String params = param.toString().substring(param.toString().indexOf("?") + 1);

			String[] paramAr = params.split("&");
			for (int i = 0; i < paramAr.length; i++) {
				if (paramAr[i].indexOf("=") >= 0 && paramAr[i].split("=").length > 1) {
					String key = paramAr[i].split("=")[0];
					String value = paramAr[i].split("=")[1];

					if (key != null && key.equals("title")) {
						intent.putExtra("title", java.net.URLDecoder.decode(value));
					} else if (key != null && key.equals("fullURL")) {
						intent.putExtra("fullURL", java.net.URLDecoder.decode(value));
					} else if (key != null && key.equals("url")) {
						intent.putExtra("url", java.net.URLDecoder.decode(value));
					} else if (key != null && key.equals("showNewButton")) {
						intent.putExtra("showNewButton", value);
					}
					else
					{
						intent.putExtra( key , value);
					}
				}
			}

			startActivity(intent);
		}
		else if ( "goUserMessageActivity".equals( actionName ) )
		{
			Intent intent = new Intent( this , UserMessageActivity.class);
			String params = param.toString().substring(param.toString().indexOf("?") + 1);

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

			hash.put("userID",  application.getLoginUser().getUserID() );
			intent.putExtra("messageInfo", hash );
			startActivity(intent);
		}
		else if ( "setMetaInfoString".equals( actionName ) && param instanceof HashMap )
		{
			HashMap<String, String> hash = (HashMap<String, String>) param;
			setMetaInfoString(hash.get("name"), hash.get("value"));
		}
		else if ( "sendBroadCast".equals( actionName ) && param instanceof Intent )
		{
			Intent intent = (Intent) param;
			sendBroadcast(intent);
		}
	}
}
