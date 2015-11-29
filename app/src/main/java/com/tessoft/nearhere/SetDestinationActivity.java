package com.tessoft.nearhere;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.AddressTaskDelegate;
import com.tessoft.common.GetAddressTask;
import com.tessoft.common.GoogleMapkiUtil;
import com.tessoft.common.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SetDestinationActivity extends BaseActivity 
implements OnMapReadyCallback, AddressTaskDelegate {

	GoogleMap map = null;
	int ZoomLevel = 14;
	Marker marker = null;
	String command = "";
	LatLng initLocation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try
		{
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_set_destination);

			MapFragment mapFragment = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);

			if ( getIntent().getExtras().containsKey("initLocation"))
			{
				initLocation = (LatLng) getIntent().getExtras().get("initLocation");
			}
			else
			{
				initLocation = new LatLng( Util.getDouble(MainActivity.latitude),
						Util.getDouble( MainActivity.longitude ) );
			}
			
			EditText edtSearchLocation = (EditText) findViewById(R.id.edtSearchLocation);
			edtSearchLocation.setOnEditorActionListener( new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					// TODO Auto-generated method stub
					try
					{
						if ( actionId == EditorInfo.IME_NULL  
							      && event.getKeyCode() == KeyEvent.KEYCODE_ENTER )
							searchOnMap( null );
					}
					catch( Exception ex )
					{
						catchException(this, ex);
					}
					
					return false;
				}
			});
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
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

	@Override
	public void onMapReady(GoogleMap map ) {
		// TODO Auto-generated method stub
		try
		{
			this.map = map;

			moveMap(initLocation);
			addMarker( initLocation );
			
			selectedLocation = initLocation;
			getAddress(selectedLocation, 1);

			map.setMyLocationEnabled(true);

			map.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng location ) {
					// TODO Auto-generated method stub
					SetDestinationActivity.this.map.clear();
					
					selectedLocation = location;
					
					addMarker( location );
					
					findViewById(R.id.btnSetDestination).setEnabled(false);
					getAddress(location, 1);
				}
			});
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	public void getAddress(LatLng location, int requestCode ) {
		Location loc = new Location("");
		loc.setLatitude(location.latitude);
		loc.setLongitude(location.longitude);
		
		Location[] locs = new Location[1];
		locs[0] = loc;
		new GetAddressTask( getApplicationContext(), this, requestCode ).execute(locs);
	}
	
	public void addMarker( LatLng location )
	{
		map.clear();
		marker = map.addMarker(new MarkerOptions()
		.position( location ));
	}

	private void moveMap( LatLng location )
	{
		CameraUpdate center=
				CameraUpdateFactory.newLatLng( location );
		map.moveCamera(center);
		CameraUpdate zoom=CameraUpdateFactory.zoomTo(ZoomLevel);
		map.animateCamera(zoom);
	}

	@Override
	public void onAddressTaskPostExecute(int requestCode, Object result) {
		// TODO Auto-generated method stub
		
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		String title = "";
		
		selectedAddress = Util.getDongAddressString( result );
		
		title = "위치 : " + Util.getDongAddressString( result );
		txtTitle.setText( title );
		
		findViewById(R.id.btnSetDestination).setEnabled(true);
	}

	public void setDestination( View v )
	{
		try
		{
			if ( marker == null )
			{
				showOKDialog("지점을 선택해 주십시오.", null);
				return;
			}
			
			Intent data = new Intent();
			data.putExtra("address", selectedAddress);
			data.putExtra("location", selectedLocation);
			setResult( RESULT_OK, data);
			finish();
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	String selectedAddress = "";
	LatLng selectedLocation = null;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		try
		{
			getMenuInflater().inflate(R.menu.set_destination, menu);
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	    
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

		if ( getIntent().getExtras() != null && getIntent().getExtras().containsKey("anim1") )
		{
			overridePendingTransition( getIntent().getExtras().getInt("anim1"), getIntent().getExtras().getInt("anim2"));
		}
		else
			overridePendingTransition(R.anim.stay, R.anim.slide_out_to_right);
	}
	
	GoogleMapkiUtil httpUtil = new GoogleMapkiUtil();
	
	public ProgressDialog progressDialog = null;
	
	public void searchOnMap( View v )
	{
		try
		{
			EditText edtSearchLocation = (EditText) findViewById(R.id.edtSearchLocation);
			
			if ( TextUtils.isEmpty( edtSearchLocation.getText() ) )
			{
				edtSearchLocation.setError("지역을  입력해 주십시오.");
				return;
			}
			
			if (progressDialog != null && progressDialog.isShowing())
				return;
			
			InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtSearchLocation.getWindowToken(), 0);
			
//			progressDialog = ProgressDialog.show(
//					this, "확인", "검색 중입니다");
			
			//httpUtil.requestMapSearch( new ResultHandler( this ), edtSearchLocation.getText().toString().trim(), "");
			
			Geocoder geocoder = new Geocoder( getApplicationContext(), Locale.getDefault());
			List <Address> addresses = geocoder.getFromLocationName( edtSearchLocation.getText().toString().trim(), 1);
			
//			progressDialog.dismiss();
			
			if ( addresses == null || addresses.size() < 1 )
			{
				showOKDialog("경고", "검색 결과가 올바르지 않습니다.\r\n다른 키워드로 검색해 보십시오.", null);
				return;
			}
			
			LatLng location =  new LatLng( addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
			moveMap( location );
			map.clear();
			addMarker( location );
			getAddress(location, 1);
			selectedLocation = location;
		}
		catch( Exception ex )
		{
			progressDialog.dismiss();
			catchException(this, ex);
		}
	}
	
	static class ResultHandler extends Handler {
		
		AdapterDelegate delegate = null;
		
		public ResultHandler( AdapterDelegate delegate) {
			// TODO Auto-generated constructor stub
			this.delegate = delegate;
		}
		
		@Override
		public void handleMessage(Message msg) {
			
			String result = msg.getData().getString(GoogleMapkiUtil.RESULT);
		
			ArrayList<String> searchList = null;
			
			if (result.equals(GoogleMapkiUtil.SUCCESS_RESULT)) {
				searchList = msg.getData().getStringArrayList("searchList");
			}
			
			this.delegate.doAction("mapSearchResult", searchList );
		}
	}
	
	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		super.doAction(actionName, param);

		try
		{
			progressDialog.dismiss();
			
			ArrayList<String> searchList = null;
			if ( param == null )
			{
				showOKDialog("경고", "검색 결과가 올바르지 않습니다.\r\n인터넷 연결을 확인해 보시기 바랍니다.", null);
				return;
			}
			
			searchList = (ArrayList<String>) param;
			
			if ( searchList.size() > 2)
			{
				Double latitude = Double.parseDouble( searchList.get(1) );
				Double longitude = Double.parseDouble( searchList.get(2));
				LatLng location =  new LatLng(latitude, longitude);
				moveMap( location );
				map.clear();
				addMarker( location );
				getAddress(location, 1);
				selectedLocation = location;
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
		
	}
}
