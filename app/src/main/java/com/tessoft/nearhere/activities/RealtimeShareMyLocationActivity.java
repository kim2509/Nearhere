package com.tessoft.nearhere.activities;

import android.os.Bundle;

import com.tessoft.nearhere.R;
import com.tessoft.nearhere.activities.BaseActivity;

public class RealtimeShareMyLocationActivity extends BaseActivity {

	int ZoomLevel = 14;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_share_my_location);
			
//			setTitle("내 위치 공유");
//			findViewById(R.id.btnRefresh).setVisibility(ViewGroup.GONE);
			
//			MapFragment mapFragment = (MapFragment) getFragmentManager()
//					.findFragmentById(R.id.map);
//			mapFragment.getMapAsync(this);
		}
		catch( Exception ex )
		{
			application.catchException(this, ex);
		}
	}
	
	/*
	@Override
	public void onMapReady(GoogleMap map ) {
		// TODO Auto-generated method stub
		try
		{
			double latitude = Double.parseDouble(MainActivity.latitude);
			double longitude = Double.parseDouble(MainActivity.longitude);
			
			LatLng location = new LatLng( latitude , longitude); 
			CameraUpdate center=
					CameraUpdateFactory.newLatLng( location );
			map.moveCamera(center);
			CameraUpdate zoom=CameraUpdateFactory.zoomTo(ZoomLevel);
			map.animateCamera(zoom);
			
			map.setMyLocationEnabled(true);
		}
		catch( Exception ex )
		{
			application.catchException(this, ex);
		}
	}
	
	public void shareURL(View v )
	{
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, ((TextView)v).getText());
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}
	*/
}