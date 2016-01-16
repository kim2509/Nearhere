package com.tessoft.nearhere.activities;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.APIResponse;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;

import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import net.daum.mf.map.api.MapView;

public class SearchMapActivity extends BaseActivity implements OnMapReadyCallback {

    View header = null;
    int ZoomLevel = 14;
    private GoogleMap map = null;
    ListView listMain = null;
    EditText edtSearchDestination = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_search_map);

            setTitle("목적지 선택");

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            listMain = (ListView) findViewById(R.id.listMain);

            /*
            LinearLayout layoutMap = (LinearLayout) findViewById(R.id.layoutMap);
            ViewGroup.LayoutParams params = layoutMap.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutMap.setVisibility(ViewGroup.VISIBLE);

            findViewById(R.id.layoutList).setVisibility(ViewGroup.GONE);
            */

            HashMap hash = application.getDefaultRequest();
            hash.put("userID", application.getLoginUser().getUserID());
            sendHttp("/taxi/getUserDestinations.do", mapper.writeValueAsString(hash), Constants.HTTP_SEARCH_USER_DESTINATIONS);


            initializeComponent();

            MapView mapView = new MapView(this);
            mapView.setDaumMapApiKey("1ce23b1035fde7488e6be71df90904d7");
        }
        catch( Exception ex )
        {
            catchException( this, ex );
        }
    }

    private void initializeComponent() {
        edtSearchDestination = (EditText) findViewById(R.id.edtSearchDestination);
        edtSearchDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDestinations( s.toString() );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void setTitle( String title ) {
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText(title);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try
        {
            this.map = googleMap;

            // 초기위치 서울시청
            double latitude = 37.5627667;
            double longitude = 126.9821314;

            if ( !Util.isEmptyString( NearhereApplication.latitude ) && !Util.isEmptyString( NearhereApplication.longitude ) )
            {
                latitude = Double.parseDouble( NearhereApplication.latitude );
                longitude = Double.parseDouble( NearhereApplication.longitude );
            }

            moveMap(latitude, longitude);

            map.setMyLocationEnabled(true);
        }
        catch( Exception ex )
        {
            application.catchException(this, ex);
        }
    }

    private void moveMap(double latitude, double longitude) {
        LatLng location = new LatLng( latitude , longitude);
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(location);
        map.moveCamera(center);
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(ZoomLevel);
        map.animateCamera(zoom);
    }

    @Override
    public void doPostTransaction(int requestCode, Object result) {
        try {
            if (Constants.FAIL.equals(result)) {
                showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
                return;
            }

            super.doPostTransaction(requestCode, result);

            APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

            if ( "0000".equals( response.getResCode() ) )
            {
                if ( requestCode == Constants.HTTP_SEARCH_USER_DESTINATIONS )
                {
                    HashMap data = (HashMap) response.getData();
                    List<String> destinationList = (List<String>) data.get("destinationList");
                    String[] destinationAr = destinationList.toArray(new String[destinationList.size()]);

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, destinationAr);
                    listMain.setAdapter(adapter);
                }
            }
        }
        catch( Exception ex )
        {
            catchException(this, ex);
        }
    }

    boolean bSearchingDestination = false;

    String searchingDestination = "";

    private void searchDestinations( String destination )
    {
        searchingDestination = destination;

        if ( bSearchingDestination ) return;

        bSearchingDestination = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                bSearchingDestination = false;

                try
                {
                    Geocoder geocoder = new Geocoder( getApplicationContext(), Locale.getDefault());
                    List <Address> addresses = geocoder.getFromLocationName( searchingDestination.trim(), 10 );

                    String[] destinationAr = new String[addresses.size()];
                    for ( int i = 0; i < addresses.size(); i++ )
                        destinationAr[i] = addresses.get(i).getFeatureName();

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, destinationAr);
                    listMain.setAdapter(adapter);

//                    application.showToastMessage( mapper.writeValueAsString(addresses) );
                }
                catch( Exception ex )
                {

                }
            }
        }, 1000);
    }
}
