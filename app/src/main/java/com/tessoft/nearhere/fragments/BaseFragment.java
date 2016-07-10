package com.tessoft.nearhere.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.Util;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.PhotoViewer;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.R.id;
import com.tessoft.nearhere.R.layout;
import com.tessoft.nearhere.activities.PopupWebViewActivity;
import com.tessoft.nearhere.activities.TaxiPostDetailActivity;
import com.tessoft.nearhere.activities.UserMessageActivity;
import com.tessoft.nearhere.activities.UserProfileActivity;

import org.codehaus.jackson.map.ObjectMapper;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.UUID;

public class BaseFragment extends Fragment implements AdapterDelegate, TransactionDelegate{

	protected ObjectMapper mapper = new ObjectMapper();
	protected NearhereApplication application = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		application = (NearhereApplication) getActivity().getApplication();
	}
	
	public void showToastMessage( String message )
	{
		Toast.makeText( getActivity() , message, Toast.LENGTH_LONG).show();
	}
	
	public void catchException ( Object target, Exception ex )
	{
		if ( ex == null )
			writeLog( "[" + target.getClass().getName() + "] NullPointerException!!!" );
		else
			writeLog( "[" + target.getClass().getName() + "]" + ex.getMessage() );
	}
	
	public void writeLog( String log )
	{
		Log.i("NearHereHelp", log );
	}
	
	public void showOKDialog( String message, final Object param )
	{
		showOKDialog("확인", message, param);
	}
	
	public void showOKDialog( String title, String message, final Object param )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
		
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
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
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
	
	public void sendHttp( String url, Object request, int requestCode )
	{
		new HttpTransactionReturningString( this, url, requestCode ).execute( request );
	}
	
	public String getUniqueDeviceID()
	{
		final TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid.toString();
	}
	
	public void setMetaInfo( String key, String value )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		SharedPreferences.Editor editor = settings.edit();
		editor.putString( key, value );
		editor.commit();
	}
	
	public String getMetaInfoString( String key )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		if ( settings.contains(key) )
			return settings.getString(key, "");
		else return "";
	}
	
	public int getMetaInfoInt( String key )
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		if ( settings.contains(key) )
			return Util.getInt( settings.getString(key, "") );
		return 0;
	}
	
	public void showSimpleInputDialog(String title, String subTitle, String defaultValue, 
			String desc, final OnClickListener onClickListener )
	{
		LayoutInflater li = LayoutInflater.from( getActivity() );
		final View promptsView = li.inflate(layout.fragment_simple_input, null);

		EditText edtSimpleInput = (EditText) promptsView.findViewById(id.edtSimpleInput);
		edtSimpleInput.setText( defaultValue );
		edtSimpleInput.setHint(subTitle);
		
		if ( Util.isEmptyString( desc ) )
			promptsView.findViewById(id.txtDesc).setVisibility(ViewGroup.INVISIBLE);
		else
		{
			promptsView.findViewById(id.txtDesc).setVisibility(ViewGroup.VISIBLE);
			TextView txtDesc = (TextView) promptsView.findViewById(id.txtDesc);
			txtDesc.setText( desc );
		}

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity() );
		alertDialogBuilder.setView(promptsView);
		
		alertDialogBuilder
		.setCancelable(false)
		.setTitle( title )
		.setPositiveButton("확인", null )
		.setNegativeButton("취소",
		  new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog,int id) {
			dialog.cancel();
		    }
		  });
		
		// create alert dialog
		final AlertDialog simpleInputDialog = alertDialogBuilder.create();
		
		simpleInputDialog.setOnShowListener( new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				
				Button b = simpleInputDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setTag( simpleInputDialog );
				b.setOnClickListener( onClickListener );
			}
		});
		// show it
		simpleInputDialog.show();
	}
	
	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		if ("logException".equals( actionName ) )
			Log.e("이근처", "exception", (Exception) param);
		else if ( "viewPost".equals( actionName ) )
		{
			Intent intent = new Intent( getActivity(), TaxiPostDetailActivity.class);
			intent.putExtra("postID", param.toString());
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		}
		else if ( "openUserProfile".equals( actionName ) )
		{
			Intent intent = new Intent( getActivity(), UserProfileActivity.class);
			intent.putExtra("userID", param.toString() );
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
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
			Intent intent = new Intent( getActivity(), PhotoViewer.class);
			intent.putExtra("imageURL", param.toString() );
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.stay);
		}
		else if ( "openExternalURL".equals( actionName ) )
		{
			String value = URLDecoder.decode(param.toString());
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(value));
			startActivity(browserIntent);
		}
		else if ( "openURL".equals( actionName ) )
		{
			Intent intent = new Intent( getActivity() , PopupWebViewActivity.class);
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
			Intent intent = new Intent( getActivity() , UserMessageActivity.class);
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
			application.setMetaInfo(hash.get("name"), hash.get("value"));
		}
		else if ( "sendBroadCast".equals( actionName ) && param instanceof Intent )
		{
			Intent intent = (Intent) param;
			getActivity().sendBroadcast(intent);
		}
	}

	@Override
	public String getStringValueForKey( String keyName )
	{
		return "";
	}
}
