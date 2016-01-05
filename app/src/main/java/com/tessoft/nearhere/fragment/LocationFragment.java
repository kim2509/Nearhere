package com.tessoft.nearhere.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.tessoft.nearhere.LocationService;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends BaseFragment implements OnMapReadyCallback , View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View rootView = null;
    int ZoomLevel = 14;
    private GoogleMap map = null;
    Button btnShare = null;

    private OnFragmentInteractionListener mListener;

    public LocationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapper = new ObjectMapper();

        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_LOCATION_UPDATED));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_STOP_LOCATION_SERVICE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try
        {
            // Inflate the layout for this fragment
            if ( rootView == null )
            {
                rootView =  inflater.inflate(R.layout.fragment_location, container, false);

                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                Button btnMapToggle = (Button) rootView.findViewById(R.id.btnMapToggle);
                if ( "GONE".equals( application.getMetaInfoString(Constants.METAINFO_LOCATION_MAP_VISIBILITY) ) )
                {
                    rootView.findViewById(R.id.layoutMap).setVisibility(ViewGroup.GONE);
                    btnMapToggle.setText("지도 펼치기");
                }
                else
                {
                    rootView.findViewById(R.id.layoutMap).setVisibility(ViewGroup.VISIBLE);
                    btnMapToggle.setText("지도 접기");
                }

                btnMapToggle.setOnClickListener(this);

                btnShare = (Button) rootView.findViewById(R.id.btnShare);
                btnShare.setOnClickListener(this);

                TextView txtURL = (TextView) rootView.findViewById(R.id.txtURL);
                txtURL.setOnClickListener(this);

                if ( !Util.isEmptyString(NearhereApplication.address) )
                {
                    TextView txtCurrentAddress = (TextView) rootView.findViewById(R.id.txtCurrentAddress);
                    txtCurrentAddress.setText(NearhereApplication.address);
                }
            }
        }
        catch( Exception ex )
        {
            showToastMessage( ex.getMessage() );
        }

        return rootView;
    }

    @Override
    public void onResume() {

        try
        {
            super.onResume();

            if ( application.checkIfGPSEnabled() == false )
            {
                rootView.findViewById(R.id.layoutLocationBody).setVisibility(ViewGroup.GONE);
                rootView.findViewById(R.id.layoutGPS).setVisibility(ViewGroup.VISIBLE);
            }
            else
            {
                rootView.findViewById(R.id.layoutLocationBody).setVisibility(ViewGroup.VISIBLE);
                rootView.findViewById(R.id.layoutGPS).setVisibility(ViewGroup.GONE);
            }

            if ( NearhereApplication.bLocationServiceExecuting && !Util.isEmptyString( NearhereApplication.strRealtimeLocationID ) )
            {
                btnShare.setText("종료하기");
                TextView txtURL = (TextView) rootView.findViewById(R.id.txtURL);
                txtURL.setVisibility(ViewGroup.VISIBLE);
                txtURL.setText(Constants.getServerURL() + "/ul.do?ID=" +
                        java.net.URLEncoder.encode(NearhereApplication.strRealtimeLocationID, "utf-8"));
            }
        }
        catch( Exception ex )
        {

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                moveMap(Double.parseDouble( NearhereApplication.latitude), Double.parseDouble( NearhereApplication.longitude ));
            }
            else
                moveMap(latitude, longitude);

            LinearLayout layoutMap = (LinearLayout) rootView.findViewById(R.id.layoutMap);
            int Visibility = layoutMap.getVisibility();

            if ( Visibility == ViewGroup.VISIBLE )
                map.setMyLocationEnabled(true);
        }
        catch( Exception ex )
        {
            application.catchException(this, ex);
        }
    }

    private void moveMap(double latitude, double longitude) {
        LatLng location = new LatLng( latitude , longitude);
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(location);
        map.moveCamera(center);
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(ZoomLevel);
        map.animateCamera(zoom);
    }

    @Override
    public void onClick(View v) {

        try
        {
            if ( v.getId() == R.id.btnMapToggle )
            {
                mapClicked();
            }
            else if ( v.getId() == R.id.btnShare )
            {
                if ( application.checkIfGPSEnabled() == false )
                {
                    showOKDialog("경고", "GPS 를 켜 주시기 바랍니다.");
                    return;
                }

                shareButtonClicked((Button) v);
            }
            else if ( v.getId() == R.id.txtURL )
            {
                shareURL(v);
            }
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

    private void shareButtonClicked(Button v) throws IOException {
        Button btn = v;

        if ( "공유하기".equals(btn.getText()) )
        {
            startLocationService();
        }
        else if ( "종료하기".equals( btn.getText()) )
        {
            stopLocationService();
        }
    }

    private void startLocationService() throws IOException {
        HashMap hash = new HashMap();
        hash.put("userID", application.getLoginUser().getUserID());
        sendHttp("/location/getNewLocation.do", mapper.writeValueAsString(hash), Constants.HTTP_GET_NEW_LOCATION);
        Button btnShare = (Button) rootView.findViewById(R.id.btnShare);
        btnShare.setText("종료하기");

        rootView.findViewById(R.id.txtShareGuide).setVisibility(ViewGroup.GONE);
        rootView.findViewById(R.id.txtExitGuide).setVisibility(ViewGroup.VISIBLE);
    }

    private void stopLocationService() throws IOException {
        Intent intent = new Intent( getActivity().getApplicationContext(), LocationService.class );
        getActivity().stopService(intent);
        Button btnShare = (Button) rootView.findViewById(R.id.btnShare);
        btnShare.setText("공유하기");

        if ( !Util.isEmptyString(NearhereApplication.strRealtimeLocationID) )
        {
            HashMap hash = new HashMap();
            hash.put("locationID", NearhereApplication.strRealtimeLocationID);
            sendHttp("/location/finishLocationTracking.do", mapper.writeValueAsString(hash), Constants.HTTP_FINISH_LOCATION_TRACKING);
        }

        rootView.findViewById(R.id.txtShareGuide).setVisibility(ViewGroup.VISIBLE);
        rootView.findViewById(R.id.txtExitGuide).setVisibility(ViewGroup.GONE);
    }

    private void mapClicked() {
        Button btnMapToggle = (Button) rootView.findViewById(R.id.btnMapToggle);
        LinearLayout layoutMap = (LinearLayout) rootView.findViewById(R.id.layoutMap);
        int Visibility = layoutMap.getVisibility();

        layoutMap.setVisibility( Visibility == ViewGroup.VISIBLE ? ViewGroup.GONE : ViewGroup.VISIBLE );

        if ( Visibility == ViewGroup.VISIBLE )
        {
            map.setMyLocationEnabled(false);
            application.setMetaInfo(Constants.METAINFO_LOCATION_MAP_VISIBILITY, "GONE");
            btnMapToggle.setText("지도 펼치기");
        }
        else
        {
            CameraUpdate zoom= CameraUpdateFactory.zoomTo(ZoomLevel);
            map.animateCamera(zoom);
            map.setMyLocationEnabled(true);
            application.setMetaInfo(Constants.METAINFO_LOCATION_MAP_VISIBILITY, "VISIBLE");
            btnMapToggle.setText("지도 접기");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if ( intent == null ) return;

                if ( Constants.BROADCAST_LOCATION_UPDATED.equals(intent.getAction()) )
                {
                    if ( intent.getExtras() != null )
                    {
                        String latitude = intent.getExtras().getString("latitude");
                        String longitude = intent.getExtras().getString("longitude");
                        String address = intent.getExtras().getString("address");

                        moveMap(Double.parseDouble(latitude), Double.parseDouble(longitude));

                        TextView txtCurrentAddress = (TextView) rootView.findViewById(R.id.txtCurrentAddress);
                        txtCurrentAddress.setText(address);
                    }
                }
                else if ( Constants.BROADCAST_STOP_LOCATION_SERVICE.equals( intent.getAction() ) )
                {
                    stopLocationService();
                }
            }
            catch( Exception ex )
            {
                application.catchException(this, ex);
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(mMessageReceiver);
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

            if ( "0000".equals(response.getResCode()) )
            {
                if ( requestCode == Constants.HTTP_GET_NEW_LOCATION ) {

                    String userString = mapper.writeValueAsString(response.getData());
                    HashMap data = mapper.readValue(userString, new TypeReference<HashMap>() {});

                    TextView txtURL = (TextView) rootView.findViewById(R.id.txtURL);
                    txtURL.setVisibility(ViewGroup.VISIBLE);

                    NearhereApplication.strRealtimeLocationID = data.get("locationID").toString();

                    txtURL.setText(Constants.getServerURL() + "/ul.do?ID=" +
                            java.net.URLEncoder.encode(NearhereApplication.strRealtimeLocationID, "utf-8"));

                    Intent intent = new Intent( getActivity().getApplicationContext(), LocationService.class );
                    intent.putExtra("locationID", NearhereApplication.strRealtimeLocationID);
                    getActivity().startService(intent);
                }
                else if ( requestCode == Constants.HTTP_FINISH_LOCATION_TRACKING ) {
                    rootView.findViewById(R.id.txtURL).setVisibility(ViewGroup.GONE);
                    NearhereApplication.strRealtimeLocationID = "";
                }

            }
        }
        catch( Exception ex )
        {
            application.catchException(this, ex);
        }
    }
}
