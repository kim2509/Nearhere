package com.tessoft.nearhere.fragments;

import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;
import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.nearhere.activities.SetDestinationActivity;
import com.tessoft.nearhere.R.anim;
import com.tessoft.nearhere.R.array;
import com.tessoft.nearhere.R.id;
import com.tessoft.nearhere.R.layout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchDialogFragment extends DialogFragment implements OnClickListener
{
	private static final int REQUEST_SET_DEPARTURE = 1;
	private static final int REQUEST_SET_DESTINATION = 2;
	View rootView = null;
	AdapterDelegate delegate;
	HashMap initData = null;

	public SearchDialogFragment()
	{

	}

	public SearchDialogFragment( AdapterDelegate delegate, HashMap data ) {
		// TODO Auto-generated constructor stub
		this.delegate = delegate;
		this.initData = data;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_SET_DESTINATION));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mMessageReceiver);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) 
    {
    	View v = super.onCreateView(inflater, container, savedInstanceState);
    	
    	return v;
    }

    AlertDialog searchDialog = null;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
    	
    	try
    	{
    		LayoutInflater li = LayoutInflater.from( getActivity() );
    		rootView = li.inflate(layout.fragment_search_detail, null);
    		
    		initializeComponent();
    		
            // Set a theme on the dialog builder constructor!
    		AlertDialog.Builder builder = 
                new AlertDialog.Builder( getActivity() );
            builder.setView( rootView );
            
            builder
    		.setCancelable(false)
    		.setPositiveButton("조회", null )
    		.setNeutralButton("초기화", null)
    		.setNegativeButton("취소",
    		  new DialogInterface.OnClickListener() {
    		    public void onClick(DialogInterface dialog,int id) {
    			dialog.cancel();
    		    }
    		  });
            
            searchDialog = builder.create();
            
            searchDialog.setOnShowListener(new OnShowListener() {
				
				@Override
				public void onShow(DialogInterface dialog) {
					// TODO Auto-generated method stub
					Button searchBtn = searchDialog.getButton(AlertDialog.BUTTON_POSITIVE);
					
					searchBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
							TextView txtWarning = (TextView) rootView.findViewById(id.txtWarning);
							
							if ( "출발지 설정".equals( btnSelectDeparture.getText()) &&
									!"전체".equals( spDepartureDistance.getSelectedItem() ))
							{
								txtWarning.setVisibility(ViewGroup.VISIBLE);
								txtWarning.setText("출발지를 설정해 주십시오.");
								return;
							}
							
							if ( "도착지 설정".equals( btnSelectDestination.getText()) &&
									!"전체".equals( spDestinationDistance.getSelectedItem() ))
							{
								txtWarning.setVisibility(ViewGroup.VISIBLE);
								txtWarning.setText("도착지를 설정해 주십시오.");
								return;
							}
							
							txtWarning.setVisibility(ViewGroup.GONE );
							
							departureDistance = spDepartureDistance.getSelectedItem().toString();
							destinationDistance = spDestinationDistance.getSelectedItem().toString();
							
							searchResult();
							
							searchDialog.dismiss();
						}
					});
					
					Button neutral = searchDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
					neutral.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							btnSelectDeparture.setText("출발지 설정");
							btnSelectDestination.setText("도착지 설정");
							spDepartureDistance.setSelection(0);
							spDestinationDistance.setSelection(0);
							departure = null;
							destination = null;
							departureAddress = null;
							destinationAddress = null;
							departureDistance = null;
							destinationDistance = null;
						}
					});
				}
			});
    	}
    	catch( Exception ex )
    	{
    	}
        
        return searchDialog;
    }
    
    Button btnSelectDeparture = null;
    Button btnSelectDestination = null;
    Spinner spDepartureDistance = null;
    Spinner spDestinationDistance = null;
    
    public void initializeComponent()
    {
    	spDepartureDistance = (Spinner) rootView.findViewById(id.spDepartureDistance);
		ArrayAdapter<CharSequence> adapterDepartureDistance = ArrayAdapter.createFromResource( getActivity(),
				array.distance_list, android.R.layout.simple_spinner_item);
		adapterDepartureDistance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDepartureDistance.setAdapter(adapterDepartureDistance);
		
		spDestinationDistance = (Spinner) rootView.findViewById(id.spDestinationDistance);
		ArrayAdapter<CharSequence> adapterDestinationDistance = ArrayAdapter.createFromResource( getActivity(),
				array.distance_list, android.R.layout.simple_spinner_item);
		adapterDestinationDistance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDestinationDistance.setAdapter(adapterDestinationDistance);
		
		btnSelectDeparture = (Button) rootView.findViewById(id.btnSelectDeparture);
		btnSelectDeparture.setOnClickListener(this);
		btnSelectDestination = (Button) rootView.findViewById(id.btnSelectDestination);
		btnSelectDestination.setOnClickListener(this);
		
		if ( this.initData != null )
		{
			if ( initData.containsKey("departure") && initData.get("departure") != null )
				departure = (LatLng) initData.get("departure");
			
			if ( initData.containsKey("destination") && initData.get("destination") != null )
				destination = (LatLng) initData.get("destination");
			
			if ( initData.containsKey("departureAddress") && initData.get("departureAddress") != null &&
					!Util.isEmptyString(initData.get("departureAddress").toString()) )
			{
				departureAddress = initData.get("departureAddress").toString();
				btnSelectDeparture.setText( departureAddress );
			}
			
			if ( initData.containsKey("destinationAddress") && initData.get("destinationAddress") != null &&
					!Util.isEmptyString( initData.get("destinationAddress").toString() ) )
			{
				destinationAddress = initData.get("destinationAddress").toString();
				btnSelectDestination.setText( initData.get("destinationAddress").toString() );
			}

			if ( initData.containsKey("departureDistance") && initData.get("departureDistance") != null &&
					!Util.isEmptyString( initData.get("departureDistance").toString() ) )
			{
				departureDistance = initData.get("departureDistance").toString();
				for ( int i = 0; i < adapterDepartureDistance.getCount(); i++ )
				{
					if ( adapterDepartureDistance.getItem(i).equals( departureDistance ) )
					{
						spDepartureDistance.setSelection(i);
						break;
					}
				}
			}
			
			if ( initData.containsKey("destinationDistance") && initData.get("destinationDistance") != null &&
					!Util.isEmptyString( initData.get("destinationDistance").toString() ) )
			{
				destinationDistance = initData.get("destinationDistance").toString();
				for ( int i = 0; i < adapterDestinationDistance.getCount(); i++ )
				{
					if ( adapterDestinationDistance.getItem(i).equals( destinationDistance ) )
					{
						spDestinationDistance.setSelection(i);
						break;
					}
				}
			}
		}
    }
    
    LatLng departure = null;
    String departureAddress = "";
    String departureDistance = "";
    LatLng destination = null;
    String destinationAddress = "";
    String destinationDistance = "";
    

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == id.btnSelectDeparture || v.getId() == id.btnSelectDestination )
			{
				Intent intent = new Intent( getActivity(), SetDestinationActivity.class);
				intent.putExtra("anim1", anim.stay );
				intent.putExtra("anim2", anim.slide_out_to_right);
				
				if ( v.getId() == id.btnSelectDeparture )
				{
					intent.putExtra("param", "DEPARTURE");

					if ( departure != null )
						intent.putExtra("initLocation", departure);
					startActivity(intent);
				}
				else
				{
					intent.putExtra("param", "DESTINATION");

					if ( destination != null )
						intent.putExtra("initLocation", destination);
					startActivity(intent);
				}
				
				getActivity().overridePendingTransition(anim.slide_in_from_right, anim.stay);
			}
		}
		catch( Exception ex )
		{
			
		}
	}

	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			if ( intent != null && !Util.isEmptyString( intent.getExtras().getString("param") ) )
			{
				if ( "DEPARTURE".equals( intent.getExtras().getString("param") ) )
				{
					if ( !intent.getExtras().containsKey("location") || !intent.getExtras().containsKey("address") ) return;

					departure = (LatLng) intent.getExtras().get("location");
					departureAddress = intent.getExtras().getString("address");
					btnSelectDeparture.setText(departureAddress);
				}
				else if ( "DESTINATION".equals( intent.getExtras().getString("param") ) )
				{
					if ( !intent.getExtras().containsKey("location") || !intent.getExtras().containsKey("address") ) return;

					destination= (LatLng) intent.getExtras().get("location");
					destinationAddress = intent.getExtras().getString("address");
					btnSelectDestination.setText(destinationAddress);
				}
			}
		}
	};

	public void searchResult()
	{
		HashMap data = new HashMap();
		data.put("departure", departure);
		data.put("destination", destination);
		data.put("departureDistance", departureDistance );
		data.put("destinationDistance", destinationDistance );
		data.put("departureAddress", departureAddress );
		data.put("destinationAddress", destinationAddress );
		
		this.delegate.doAction("searchResult", data);
	}
}