package com.tessoft.nearhere;

import java.util.ArrayList;

import com.tessoft.common.ReadContactArrayAdapter;
import com.tessoft.domain.Contact;

import com.tessoft.nearhere.fragment.ReadContactsFragment;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SafetyKeeperActivity extends BaseActivity implements OnClickListener{

	ListView listMain = null;
	View header = null;
	View footer = null;
	ReadContactArrayAdapter adapter = null;
	TextView txtPreview = null;
	TextView txtPreviewLabel = null;
	Spinner spTotalCount = null;
	Spinner spMinutes = null;
	ArrayAdapter<CharSequence> adapterTotalCount = null;
	ArrayAdapter<CharSequence> adapterMinutes = null;
	ArrayAdapter<CharSequence> adapterExitHour = null;
	EditText edtMessage = null;
	TextView txtStatus = null;
	String address = MainActivity.fullAddress;
	Spinner spExitHour = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_safety_keeper);

			listMain = (ListView) findViewById(R.id.listMain);

			header = getLayoutInflater().inflate(R.layout.list_header_safety_keeper, null);
			footer = getLayoutInflater().inflate(R.layout.list_footer_safety_keeper, null);

			listMain = (ListView) findViewById(R.id.listMain);
			listMain.setHeaderDividersEnabled(false);
			listMain.addHeaderView(header, null, false );
			listMain.addFooterView(footer, null, false );

			adapter = new ReadContactArrayAdapter(this, 0, this );
			listMain.setAdapter(adapter);

			Button btnReadContacts = (Button) header.findViewById(R.id.btnReadContacts);
			btnReadContacts.setOnClickListener(this);

			listMain.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					
					if ( isMyServiceRunning( SafetyKeeperService.class )) return;
					
					Contact item = (Contact) arg1.getTag();
					adapter.remove(item);

					if ( adapter.getCount() <= 0 )
					{
						TextView txtEmpty = (TextView) findViewById(R.id.txtEmpty);
						txtEmpty.setVisibility(ViewGroup.VISIBLE);
					}
				}
			});

			txtPreview = (TextView) findViewById(R.id.txtPreview);
			txtPreviewLabel = (TextView) findViewById(R.id.txtPreviewLabel);

			edtMessage = (EditText) findViewById(R.id.edtMessage );
			edtMessage.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
					String msg = "[이근처 합승-" + application.getLoginUser().getUserName() + "님] " + s + 
							" [" + address.replaceAll("\\|", " ") + "]";

					txtPreview.setText( msg );
					txtPreviewLabel.setText("미리보기(" + msg.length() + "글자)");
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});

			txtPreview.setText( "[이근처 합승-" + application.getLoginUser().getUserName() + "님] [" + 
					MainActivity.fullAddress.replaceAll("\\|", " ") + "]" );

			spTotalCount = (Spinner) findViewById(R.id.spTotalCount);
			adapterTotalCount = ArrayAdapter.createFromResource( this,
					R.array.sms_total_count, android.R.layout.simple_spinner_item);
			adapterTotalCount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spTotalCount.setAdapter(adapterTotalCount);
			spTotalCount.setSelection(0, false);

			spMinutes = (Spinner) findViewById(R.id.spMinutes);
			adapterMinutes = ArrayAdapter.createFromResource( this,
					R.array.sms_minutes, android.R.layout.simple_spinner_item);
			adapterMinutes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spMinutes.setAdapter(adapterMinutes);
			spMinutes.setSelection(0, false);

			Button btnStart = (Button) findViewById(R.id.btnStart);
			btnStart.setOnClickListener(this);
			Button btnStop = (Button) findViewById(R.id.btnStop);
			btnStop.setOnClickListener(this);

			txtStatus = (TextView) findViewById(R.id.txtStatus);
			
			spExitHour = (Spinner) findViewById(R.id.spExitHour);
			adapterExitHour = ArrayAdapter.createFromResource( this,
					R.array.exit_hour, android.R.layout.simple_spinner_item);
			adapterExitHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spExitHour.setAdapter(adapterExitHour);
			spExitHour.setSelection(1, false);
			
			checkIfGPSEnabled();
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

	public void showDialog() {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		DialogFragment newFragment = new ReadContactsFragment( this );
		newFragment.show(ft, "dialog");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == R.id.btnReadContacts)
			{
				if ( isMyServiceRunning(SafetyKeeperService.class) )
				{
					showOKDialog("경고", "서비스 실행도중에는 연락처를 추가할 수 없습니다.", null );
					return;
				}
				
				showDialog();
			}
			else if ( v.getId() == R.id.btnStart )
			{
				if ( isMyServiceRunning(SafetyKeeperService.class) )
				{
					showOKDialog("경고", "이미 서비스가 실행중입니다.", null );
					return;
				}
				
				Intent intent = new Intent( getApplicationContext(), SafetyKeeperService.class );

				intent.putExtra("message", edtMessage.getText().toString() );

				ArrayList<Contact> items = new ArrayList<Contact>();
				for ( int i = 0; i < adapter.getCount(); i++ )
					items.add( adapter.getItem(i));

				if ( items.size() <= 0 )
				{
					showOKDialog("확인", "보낼 사람을 지정해 주십시오.", null);
					return;
				}

				intent.putExtra("contacts", items );

				intent.putExtra("totalCount", spTotalCount.getSelectedItem().toString() );
				intent.putExtra("minutes", spMinutes.getSelectedItem().toString());
				intent.putExtra("exitHour", spExitHour.getSelectedItem().toString() );
				intent.putExtra("userName", application.getLoginUser().getUserName() );
				intent.putExtra("userID", application.getLoginUser().getUserID() );

				startService(intent);
			}
			else if ( v.getId() == R.id.btnStop )
			{
				if ( isMyServiceRunning(SafetyKeeperService.class) == false )
				{
					showOKDialog("경고", "서비스가 실행중이 아닙니다.", null );
					return;
				}
				
				Intent intent = new Intent("com.tessoft.nearhere.safetyKeeperService");
				stopService(intent);
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		super.doAction(actionName, param);
		if ( "selectContact".equals( actionName ) )
		{
			adapter.add((Contact) param );
			if ( adapter.getCount() > 0 )
			{
				TextView txtEmpty = (TextView) findViewById(R.id.txtEmpty);
				txtEmpty.setVisibility(ViewGroup.GONE);
			}
		}
	}

	public void checkIfGPSEnabled()
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

		if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			buildAlertMessageNoGps();
		}
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("경고")
		.setMessage("GPS 가 꺼져 있으면 원활한 서비스를 제공받을 수 없습니다.\n활성화시키겠습니까?")
		.setCancelable(false)
		.setPositiveButton("예", new DialogInterface.OnClickListener() {
			public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		})
		.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onBackPressed() {

		finish();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		overridePendingTransition(R.anim.stay, R.anim.slide_out_to_bottom);
	}

	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try
			{
				if ( "currentLocationChanged".equals( intent.getAction() ) )
				{
					String msg = "[이근처 합승-" + application.getLoginUser().getUserName() + "] " + edtMessage.getText().toString() + 
							" [" + MainActivity.fullAddress.replaceAll("\\|", " ") + "]";
					txtPreview.setText( msg );
				}
				else if ( "safetyKeeperStarted".equals( intent.getAction() ) )
				{
					setControlStatusAsStarted();
				}
				else if ( "safetyKeeperFinished".equals( intent.getAction() ) )
				{
					setControlStatusAsFinished();
				}
				else if ( "sentSMS".equals( intent.getAction() ) )
				{
					txtStatus.setText("현재 서비스가 진행중입니다. [" + SafetyKeeperService.sentCount + "회 전송완료]");
				}
			}
			catch( Exception ex )
			{
				catchException(this, ex);
			}
		}

		private void setControlStatusAsStarted() {
			spTotalCount.setEnabled(false);
			spMinutes.setEnabled(false);
			findViewById(R.id.edtMessage).setEnabled(false);
			
			txtStatus.setVisibility(ViewGroup.VISIBLE);
			txtStatus.setText("현재 서비스가 진행중입니다. [" + SafetyKeeperService.sentCount + "회 전송완료]");
		}
	};
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		try
		{
			registerReceiver(mMessageReceiver, new IntentFilter("currentLocationChanged"));
			registerReceiver(mMessageReceiver, new IntentFilter("safetyKeeperStarted"));
			registerReceiver(mMessageReceiver, new IntentFilter("safetyKeeperFinished"));
			registerReceiver(mMessageReceiver, new IntentFilter("sentSMS"));
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if ( isMyServiceRunning(SafetyKeeperService.class) )
		{
			spTotalCount.setEnabled(false);
			spMinutes.setEnabled(false);
			findViewById(R.id.edtMessage).setEnabled(false);
			
			txtStatus.setVisibility(ViewGroup.VISIBLE);
			txtStatus.setText("현재 서비스가 진행중입니다. [" + SafetyKeeperService.sentCount + "회 전송완료]");
		
			for ( int i = 0; i < adapterTotalCount.getCount(); i++ )
			{
				String cnt = adapterTotalCount.getItem(i).toString();
				if ( SafetyKeeperService.totalCount == Integer.parseInt( cnt ) )
				{
					spTotalCount.setSelection(i);
					break;
				}
			}
			
			for ( int i = 0; i < adapterMinutes.getCount(); i++ )
			{
				String cnt = adapterMinutes.getItem(i).toString();
				if ( SafetyKeeperService.minutes == Integer.parseInt( cnt ) )
				{
					spMinutes.setSelection(i);
					break;
				}
			}
			
			adapter.clear();
			adapter.addAll(SafetyKeeperService.arContacts);
			
			if ( SafetyKeeperService.arContacts.size() > 0 ) findViewById(R.id.txtEmpty).setVisibility(ViewGroup.GONE);
			else findViewById(R.id.txtEmpty).setVisibility(ViewGroup.VISIBLE);
				
			adapter.notifyDataSetChanged();
			edtMessage.setText(SafetyKeeperService.message);
			address = SafetyKeeperService.address;
		}
		else
		{
			setControlStatusAsFinished();
		}
	}

	private void setControlStatusAsFinished() {
		spTotalCount.setEnabled(true);
		spMinutes.setEnabled(true);
		findViewById(R.id.edtMessage).setEnabled(true);
		
		txtStatus.setVisibility(ViewGroup.GONE);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(mMessageReceiver);
	}
	
	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
