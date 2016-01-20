package com.tessoft.nearhere.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.APIResponse;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.adapters.DestinationAdapter;

import net.daum.android.map.openapi.search.Item;
import net.daum.android.map.openapi.search.OnFinishSearchListener;
import net.daum.android.map.openapi.search.Searcher;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchMapActivity extends BaseActivity implements OnFinishSearchListener, MapView.MapViewEventListener,
        AdapterView.OnItemClickListener{

    View header = null;
    ListView listMain = null;
    EditText edtSearchDestination = null;
    DestinationAdapter adapter = null;
    MapView mapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_search_map);

            setTitle("목적지 선택");

            listMain = (ListView) findViewById(R.id.listMain);
            adapter = new DestinationAdapter( this, new ArrayList<Item>());
            listMain.setAdapter(adapter);
            listMain.setOnItemClickListener( this );

            /*
            LinearLayout layoutMap = (LinearLayout) findViewById(R.id.layoutMap);
            ViewGroup.LayoutParams params = layoutMap.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutMap.setVisibility(ViewGroup.VISIBLE);
            findViewById(R.id.layoutList).setVisibility(ViewGroup.GONE);
            */

            initializeComponent();

            HashMap hash = application.getDefaultRequest();
            hash.put("userID", application.getLoginUser().getUserID());
            sendHttp("/taxi/getUserDestinations.do", mapper.writeValueAsString(hash), Constants.HTTP_SEARCH_USER_DESTINATIONS);

            registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_DESTINATION_REFRESH));
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
                edtDestinationChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setDaumMapApiKey(Constants.DAUM_MAP_API_KEY);
        mapView.setMapType(MapView.MapType.Standard);
        mapView.setMapViewEventListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if no view has focus:
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSearchDestination, InputMethodManager.SHOW_FORCED);
    }

    protected void setTitle( String title ) {
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText(title);
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
//                    listMain.setAdapter(adapter);
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

    private void edtDestinationChanged( String destination )
    {
        searchingDestination = destination;

        if ( bSearchingDestination ) return;

        bSearchingDestination = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bSearchingDestination = false;

                if (Util.isEmptyString( searchingDestination ) )
                {
                    Intent intent = new Intent( Constants.BROADCAST_DESTINATION_REFRESH );
                    intent.putExtra("itemList", "" );
                    sendBroadcast(intent);
                    return;
                }

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                searcher.searchKeyword(getApplicationContext(),
                        searchingDestination, 0, 0, 10000, 1, Constants.DAUM_MAP_API_KEY, SearchMapActivity.this );
            }

        }, 1000);
    }

    @Override
    public void onSuccess(List<Item> itemList) {
        try {
            Intent intent = new Intent( Constants.BROADCAST_DESTINATION_REFRESH );
            intent.putExtra("itemList", mapper.writeValueAsString( itemList ) );
            sendBroadcast(intent);

            Log.d("data", mapper.writeValueAsString(itemList));
        } catch (Exception ex) {
            catchException(this, ex);
        }
    }

    @Override
    public void onFail() {

    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                if ( intent != null && intent.getExtras() != null && intent.getExtras().containsKey("itemList") )
                {
                    String itemListString = intent.getExtras().getString("itemList");
                    if ( Util.isEmptyString( itemListString ) )
                    {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    List<Item> itemList = mapper.readValue( itemListString, new TypeReference<List<Item>>(){});
                    if ( itemList != null && itemList.size() > 0 )
                    {
                        findViewById(R.id.layoutList).setVisibility(ViewGroup.VISIBLE);
                        findViewById(R.id.map_view).setVisibility(ViewGroup.GONE);
                        adapter.clear();
                        adapter.addAll(itemList);
                        adapter.notifyDataSetChanged();
                        TextView txtDestinationCount = (TextView) findViewById(R.id.txtDestinationCount);
                        txtDestinationCount.setText(itemList.size() + "");

                        RelativeLayout layoutMap = (RelativeLayout) findViewById(R.id.layoutMap);
                        ViewGroup.LayoutParams params = layoutMap.getLayoutParams();
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        layoutMap.setLayoutParams(params);

                        findViewById(R.id.layoutSearchResultCount).setVisibility(ViewGroup.GONE);
                    }
                }

            }
            catch( Exception ex )
            {
                catchException(this, ex);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

        MapPoint mapPoint = null;

        if ( !Util.isEmptyString(NearhereApplication.latitude) && !Util.isEmptyString(NearhereApplication.longitude) )
        {
            mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(NearhereApplication.latitude),
                    Double.parseDouble(NearhereApplication.longitude));
        }
        else
        {
            // 초기위치 서울시청
            double latitude = 37.5627667;
            double longitude = 126.9821314;

            mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        }

        mapView.setMapCenterPointAndZoomLevel(mapPoint, 2, true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ( view != null && view.getTag() != null && view.getTag() instanceof DestinationAdapter.ViewHolder )
        {
            DestinationAdapter.ViewHolder viewHolder = (DestinationAdapter.ViewHolder) view.getTag();

            findViewById(R.id.map_view).setVisibility(ViewGroup.VISIBLE);
            RelativeLayout layoutMap = (RelativeLayout) findViewById(R.id.layoutMap);
            ViewGroup.LayoutParams params = layoutMap.getLayoutParams();
            params.height = application.getPixelsFromDP( 300 );
            layoutMap.setLayoutParams(params);

            mapView.removeAllPOIItems();
            addMarker(MapPoint.mapPointWithGeoCoord(viewHolder.item.latitude, viewHolder.item.longitude),
                    viewHolder.item.title);

            findViewById(R.id.layoutSearchResultCount).setVisibility(ViewGroup.VISIBLE);

            // Check if no view has focus:
            View focusview = this.getCurrentFocus();
            if (focusview != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusview.getWindowToken(), 0);
            }
        }
    }

    private void addMarker(MapPoint point, String title ) {
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(title);
        marker.setTag(0);
        marker.setMapPoint(point);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(marker);
        mapView.selectPOIItem(marker, true);
        mapView.setMapCenterPoint(point, false);
    }

    public void toggleList( View view )
    {
        FrameLayout layoutList = (FrameLayout) findViewById(R.id.layoutList);
        RelativeLayout layoutMap = (RelativeLayout) findViewById(R.id.layoutMap);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layoutMap.getLayoutParams();

        if ( layoutList.getVisibility() == ViewGroup.VISIBLE )
        {
            layoutList.setVisibility(ViewGroup.GONE);

            // map 을 MATCH_PARENT 로 만든다.
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutMap.setLayoutParams(params);
        }
        else
        {
            layoutList.setVisibility( ViewGroup.VISIBLE );

            params.height = application.getPixelsFromDP( 300 );
            layoutMap.setLayoutParams(params);
        }


    }
}
