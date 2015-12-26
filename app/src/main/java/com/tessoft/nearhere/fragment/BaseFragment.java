package com.tessoft.nearhere.fragment;

import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;

import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.Util;
import com.tessoft.nearhere.NearhereApplication;

import com.tessoft.nearhere.R.id;
import com.tessoft.nearhere.R.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BaseFragment extends Fragment implements AdapterDelegate, TransactionDelegate{

	ObjectMapper mapper = new ObjectMapper();
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
	
	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		if ("logException".equals( actionName ) )
			Log.e("이근처", "exception", (Exception) param);
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
}
