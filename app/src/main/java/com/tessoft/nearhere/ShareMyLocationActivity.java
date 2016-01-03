package com.tessoft.nearhere;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.tessoft.common.Constants;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;

public class ShareMyLocationActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener{

	int ZoomLevel = 14;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_share_my_location);
			
			setTitle("내 위치 공유");
			findViewById(R.id.btnRefresh).setVisibility(ViewGroup.GONE);
			
			MapFragment mapFragment = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);

			Button btnShare = (Button) findViewById(R.id.btnShare);
			btnShare.setOnClickListener(this);
		}
		catch( Exception ex )
		{
			application.catchException(this, ex);
		}
	}

	@Override
	public void showOKDialog(String message, Object param) {
		super.showOKDialog(message, param);
	}

	@Override
	public void onMapReady(GoogleMap map ) {
		// TODO Auto-generated method stub
		try
		{
			double latitude = Double.parseDouble(NearhereApplication.latitude);
			double longitude = Double.parseDouble(NearhereApplication.longitude);
			
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

	@Override
	public void onClick(View v) {

		try
		{
			Button btn = (Button) v;

			if ( v.getId() == R.id.btnShare && "공유하기".equals(btn.getText()) )
			{
				HashMap hash = new HashMap();
				hash.put("userID", application.getLoginUser().getUserID());
				sendHttp("/location/getNewLocation.do", mapper.writeValueAsString(hash), Constants.HTTP_GET_NEW_LOCATION);
				btn.setText("종료하기");
			}
			else if ( v.getId() == R.id.btnShare && "종료하기".equals( btn.getText()) )
			{
				Intent intent = new Intent( getApplicationContext(), LocationService.class );
				stopService(intent);
				btn.setText("공유하기");
			}
		}
		catch( Exception ex )
		{

		}
	}

	@Override
	public void doPostTransaction(int requestCode, Object result) {

		try
		{
			if ( Constants.FAIL.equals(result) )
			{
				showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}

			super.doPostTransaction(requestCode, result);

			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				String userString = mapper.writeValueAsString(response.getData());
				HashMap data = mapper.readValue(userString, new TypeReference<HashMap>() {});

				findViewById(R.id.txtLocationGuide).setVisibility(ViewGroup.VISIBLE);

				TextView txtURL = (TextView) findViewById(R.id.txtURL);
				txtURL.setVisibility(ViewGroup.VISIBLE);

				txtURL.setText( Constants.getServerURL() + "/location/userLocation.do?locationID=" + data.get("locationID"));

				Intent intent = new Intent( getApplicationContext(), LocationService.class );
				intent.putExtra("locationID", data.get("locationID").toString());
				startService(intent);
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex );
		}
	}
}
