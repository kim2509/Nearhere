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

import com.tessoft.common.Constants;
import com.tessoft.domain.APIResponse;
import com.tessoft.nearhere.R;

import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import net.daum.mf.map.api.MapView;

public class SearchMapActivity extends BaseActivity {

    View header = null;
    ListView listMain = null;
    EditText edtSearchDestination = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try
        {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_search_map);

            setTitle("목적지 선택");

            listMain = (ListView) findViewById(R.id.listMain);

            LinearLayout layoutMap = (LinearLayout) findViewById(R.id.layoutMap);
            ViewGroup.LayoutParams params = layoutMap.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutMap.setVisibility(ViewGroup.VISIBLE);
            findViewById(R.id.layoutList).setVisibility(ViewGroup.GONE);

            HashMap hash = application.getDefaultRequest();
            hash.put("userID", application.getLoginUser().getUserID());
            sendHttp("/taxi/getUserDestinations.do", mapper.writeValueAsString(hash), Constants.HTTP_SEARCH_USER_DESTINATIONS);


            initializeComponent();

            MapView mapView = (MapView) findViewById(R.id.map_view);
            mapView.setDaumMapApiKey(Constants.DAUM_MAP_API_KEY);
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
                searchDestinations(s.toString());
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
