package com.tessoft.nearhere;

import java.util.Date;

import org.codehaus.jackson.type.TypeReference;

import com.google.android.gms.maps.model.LatLng;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.Post;

import com.tessoft.nearhere.fragment.DatePickerFragment;
import com.tessoft.nearhere.fragment.TimePickerFragment;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class NewTaxiPostActivity extends BaseActivity implements OnClickListener, OnTimeSetListener, OnDateSetListener{

	private static final int MODIFY_POST = 2;
	private int SELECT_DEPARTURE = 1001;
	private int SELECT_DESTINATION = 1002;
	TextView txtDeparture = null;
	TextView txtDestination = null;
	Post post = null;
	Spinner spSexInfo = null;
	Spinner spNumOfUsers = null;
	Spinner spVehicle = null;
	Spinner spFareCondition = null;
	Spinner spStatus = null;
	EditText edtMessage = null;
	String mode = "new";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try
		{
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_new_taxi_post);

			txtDeparture = (TextView) findViewById(R.id.txtDeparture);
			
			txtDestination = (TextView) findViewById(R.id.txtDestination);
			
			TextView txtChangeDeparture = (TextView) findViewById(R.id.txtChangeDeparture);
			txtChangeDeparture.setOnClickListener(this);
			TextView txtChangeDestination = (TextView) findViewById(R.id.txtChangeDestination);
			txtChangeDestination.setOnClickListener(this);
			TextView txtChangeDepartureTime = (TextView) findViewById(R.id.txtChangeDepartureTime);
			txtChangeDepartureTime.setOnClickListener(this);
			TextView txtChangeDepartureDate = (TextView) findViewById(R.id.txtChangeDepartureDate);
			txtChangeDepartureDate.setOnClickListener(this);
			
			spSexInfo = (Spinner) findViewById(R.id.spSex);
			ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, 
					getResources().getStringArray(R.array.sex_select_list) );
			sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spSexInfo.setAdapter(sexAdapter);

			spNumOfUsers = (Spinner) findViewById(R.id.spNumOfUsers);
			ArrayAdapter<String> nouAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, 
					getResources().getStringArray(R.array.nou_list) );
			nouAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spNumOfUsers.setAdapter(nouAdapter);
			
			spVehicle = (Spinner) findViewById(R.id.spVehicle);
			ArrayAdapter<String> vehicleAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, 
					getResources().getStringArray(R.array.vehicle_list) );
			vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spVehicle.setAdapter(vehicleAdapter);
			
			spFareCondition = (Spinner) findViewById(R.id.spFareCondition);
			ArrayAdapter<String> fareAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, 
					getResources().getStringArray(R.array.fare_condition) );
			fareAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spFareCondition.setAdapter(fareAdapter);
			
			spStatus = (Spinner) findViewById(R.id.spStatus);
			ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, 
					getResources().getStringArray(R.array.status_list2) );
			statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spStatus.setAdapter(statusAdapter);
			
			txtDepartureDate = (TextView) findViewById(R.id.txtDepartureDate);
			txtDepartureTime = (TextView) findViewById(R.id.txtDepartureTime);
			
			txtDepartureDate.setText( Util.getNow("yyyy-MM-dd") );
			txtDepartureTime.setText( Util.getNow("HH:mm") );
			
			edtMessage = (EditText) findViewById(R.id.edtMessage);
			
			Button btnSend = (Button) findViewById(R.id.btnSend);
			
			findViewById(R.id.btnRefresh).setVisibility(ViewGroup.GONE);
			
			if ( getIntent().getExtras() != null && getIntent().getExtras().containsKey("mode"))
			{
				// 수정
				if ( "modify".equals( getIntent().getExtras().getString("mode") ))
				{
					setTitle("합승정보수정");
					post = (Post) getIntent().getExtras().get("post");
					btnSend.setText("수정하기");
					displayModifyInfo();
					mode = "modify";
				}
				
				visibleAdditionslFields();
			}
			else
			{
				// 신규
				setTitle("합승정보등록");
				
				if ( getIntent().getExtras() != null && getIntent().getExtras().containsKey("departure") )
				{
					departure = (LatLng) getIntent().getExtras().get("departure");
					txtDeparture.setText( getIntent().getExtras().getString("address"));
				}
				else if ( Util.isEmptyString( NearhereApplication.address ) == false )
				{
					double latitude =  Util.getDouble( NearhereApplication.latitude );
					double longitude = Util.getDouble( NearhereApplication.longitude );
					departure = new LatLng(latitude, longitude);
					txtDeparture.setText( NearhereApplication.address );
				}	
				
				spStatus.setEnabled(false);
				
				btnSend.setText("등록하기");
				
				mode = "new";
			}
			
			setProgressBarIndeterminateVisibility(false);
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_taxi_post, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.stay, R.anim.slide_out_to_bottom);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if ( v.getId() == R.id.txtChangeDeparture )
			selectDeparture();
		else if ( v.getId() == R.id.txtChangeDestination )
			selectDestination();
		else if ( v.getId() == R.id.txtChangeDepartureDate )
			showDatePickerDialog(null);
		else if ( v.getId() == R.id.txtChangeDepartureTime )
			showTimePickerDialog(null);
	}
	
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment( this );
		newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment( this );
		newFragment.show(getFragmentManager(), "timePicker");
	}

	TextView txtDepartureTime  = null;
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		Date date = new Date();
		date.setHours( hourOfDay );
		date.setMinutes( minute );
		
		String timeString = Util.getDateStringFromDate( date, "HH:mm" );
		txtDepartureTime.setText( timeString );
	}

	TextView txtDepartureDate = null;
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		String dateString = Util.getDateStringFromDate(new Date( year - 1900, monthOfYear, dayOfMonth), "yyyy-MM-dd"); 
		txtDepartureDate.setText( dateString );
	}
	
	public void selectDeparture()
	{
		Intent intent = new Intent( this, SetDestinationActivity.class);
		intent.putExtra("title", "출발지 선택");
		intent.putExtra("subTitle", "출발지를 선택해 주십시오.");
		intent.putExtra("departure", departure);
		startActivityForResult(intent, SELECT_DEPARTURE);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.stay);
	}
	
	public void selectDestination()
	{
		Intent intent = new Intent( this, SetDestinationActivity.class);
		intent.putExtra("title", "도착지 선택");
		intent.putExtra("subTitle", "도착지를 선택해 주십시오.");
		startActivityForResult(intent, SELECT_DESTINATION);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.stay);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		try
		{
			if ( resultCode == RESULT_OK )
			{
				String address = data.getExtras().getString("address");
				LatLng location = (LatLng) data.getExtras().get("location");
			
				if ( requestCode == SELECT_DEPARTURE )
				{
					txtDeparture.setText(address);
					departure = location;
				}
				else if ( requestCode == SELECT_DESTINATION )
				{
					txtDestination.setText(address);
					destination = location;
				}
			}
			
			if ( departure != null && destination != null )
			{
				visibleAdditionslFields();
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	private void visibleAdditionslFields() {
		findViewById(R.id.layoutDepartureDate).setVisibility(ViewGroup.VISIBLE);
		findViewById(R.id.layoutDepartureTime).setVisibility(ViewGroup.VISIBLE);
		findViewById(R.id.layoutAddInfo).setVisibility(ViewGroup.VISIBLE);
		findViewById(R.id.layoutAddInfo2).setVisibility(ViewGroup.VISIBLE);
		findViewById(R.id.edtMessage).setVisibility(ViewGroup.VISIBLE);
		findViewById(R.id.btnSend).setVisibility(ViewGroup.VISIBLE);
	}
	
	LatLng departure = null;
	LatLng destination = null;
	
	public void addPost( View v )
	{
		try
		{
			// 신규등록일 경우
			if ( post == null )
				post = new Post();

			if ( TextUtils.isEmpty( edtMessage.getText() ) )
			{
				edtMessage.setError("내용을 입력해 주십시오.");
				return;
			}

			if ( departure == null )
			{
				showOKDialog("경고", "출발지를 지정해 주세요.", null);
				return;
			}
			
			if ( destination == null )
			{
				showOKDialog("경고", "도착지를 지정해 주세요.", null);
				return;
			}
			
			Spinner spSex = (Spinner)findViewById(R.id.spSex);
			
			if ( "여자만".equals( spSex.getSelectedItem() ) && "M".equals( application.getLoginUser().getSex() ) )
			{
				showOKDialog("경고", "남성회원은 여자만 옵션을 선택할 수 없습니다.", null);
				return;
			}
			
			post.setMessage( edtMessage.getText().toString() );
			post.setFromLatitude( String.valueOf( departure.latitude) );
			post.setFromLongitude( String.valueOf( departure.longitude) );
			post.setFromAddress( txtDeparture.getText().toString() );
			post.setLatitude( String.valueOf( destination.latitude) );
			post.setLongitude( String.valueOf( destination.longitude) );
			post.setToAddress( txtDestination.getText().toString()  );
			post.setDepartureDate( txtDepartureDate.getText().toString() );
			post.setDepartureTime( txtDepartureTime.getText().toString() );
			post.setSexInfo( spSex.getSelectedItem().toString() );
			Spinner spNumOfUsers = (Spinner)findViewById(R.id.spNumOfUsers);
			post.setNumOfUsers( spNumOfUsers.getSelectedItem().toString() );
			post.setUser( application.getLoginUser() );
			
			post.setVehicle( spVehicle.getSelectedItem().toString() );
			post.setFareOption( spFareCondition.getSelectedItem().toString() );
			post.setStatus( spStatus.getSelectedItem().toString() );
			
			boolean bRepetitive = ((CheckBox)findViewById(R.id.chkRepetitive) ).isChecked();
			post.setRepetitiveYN( bRepetitive ? "Y":"N" );

			setProgressBarIndeterminateVisibility(true);
			
			if ( Constants.bPushOffOnNewPost )
				post.setbPushOff(true);
			
			if ( "new".equals( mode ) )
				sendHttp("/taxi/insertPost.do", mapper.writeValueAsString(post), 1);
			else
				sendHttp("/taxi/modifyPost.do", mapper.writeValueAsString(post), MODIFY_POST );
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		try
		{
			setProgressBarIndeterminateVisibility(false);
		
			if ( Constants.FAIL.equals(result) )
			{
				showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}
			
			super.doPostTransaction(requestCode, result);

			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				Intent intent = new Intent( Constants.BROADCAST_TAXI_REFRESH );
				getApplicationContext().sendBroadcast(intent);
				setResult(RESULT_OK);
				finish();
			}
			else
			{
				if ( !Util.isEmptyString( response.getResMsg() ))
					showOKDialog( response.getResMsg(), null);
				else
					showOKDialog("합승내역 등록도중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
			}

		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	public void displayModifyInfo()
	{
		if ( post == null ) return;
		
		departure = new LatLng( Double.parseDouble( post.getFromLatitude() ), 
				Double.parseDouble( post.getFromLongitude() ));
		txtDeparture.setText( post.getFromAddress());
		
		destination = new LatLng( Double.parseDouble( post.getLatitude() ), 
				Double.parseDouble( post.getLongitude() ));
		txtDestination.setText( post.getToAddress() );
	
		ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spSexInfo.getAdapter();
		spSexInfo.setSelection( adapter.getPosition( post.getSexInfo() ) );
		adapter = (ArrayAdapter<CharSequence>) spNumOfUsers.getAdapter();
		spNumOfUsers.setSelection( adapter.getPosition( post.getNumOfUsers() ) );
		
		edtMessage.setText( post.getMessage() );
		
		if ( !Util.isEmptyString( post.getVehicle() ) )
		{
			ArrayAdapter<CharSequence> vehicleAdapter = (ArrayAdapter<CharSequence>) spVehicle.getAdapter();
			spVehicle.setSelection(vehicleAdapter.getPosition( post.getVehicle() ));	
		}
		
		if ( !Util.isEmptyString( post.getFareOption() ) )
		{
			ArrayAdapter<CharSequence> fareOptionAdapter = (ArrayAdapter<CharSequence>) spFareCondition.getAdapter();
			spFareCondition.setSelection(fareOptionAdapter.getPosition( post.getFareOption() ));	
		}
		
		ArrayAdapter<CharSequence> spStatusAdapter = (ArrayAdapter<CharSequence>) spStatus.getAdapter();
		spStatus.setSelection( spStatusAdapter.getPosition( post.getStatus() ) );
		
		CheckBox chkRepetitive = (CheckBox) findViewById(R.id.chkRepetitive);
		if ( "Y".equals( post.getRepetitiveYN() ) )
			chkRepetitive.setChecked( true );
		else
			chkRepetitive.setChecked( false );
	}
}
