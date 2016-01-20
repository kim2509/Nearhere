package com.tessoft.nearhere.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.Constants;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.Util;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.Post;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R.anim;
import com.tessoft.nearhere.R.array;
import com.tessoft.nearhere.R.id;
import com.tessoft.nearhere.R.layout;
import com.tessoft.nearhere.activities.MainActivity;
import com.tessoft.nearhere.activities.NewTaxiPostActivity;
import com.tessoft.nearhere.activities.TaxiPostDetailActivity;
import com.tessoft.nearhere.activities.UserListActivity;
import com.tessoft.nearhere.activities.UserProfileActivity;
import com.tessoft.nearhere.adapters.TaxiArrayAdapter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.util.HashMap;
import java.util.List;

public class TaxiFragment extends BaseFragment 
	implements AdapterDelegate, TransactionDelegate, OnItemSelectedListener, OnClickListener{

	protected static final int REQUEST_POST_DETAIL = 2001;
	private static final int GET_POSTS = 1;
	protected static final int REQUEST_SET_DEPARTURE= 10;
	protected static final int REQUEST_SET_DESTINATION = 20;
	protected static final int REQUEST_NEW_POST = 2;
	private static final int HTTP_SAFETY_KEEPER_CLICKED = 30;
	
	View rootView = null;
	ListView listMain = null;
	View fbHeader = null;
	View snsLoginHeader = null;
	View footer = null;
	ObjectMapper mapper = new ObjectMapper();
	
	TaxiArrayAdapter adapter = null;
	View nearUsersCountHeader = null;
	View searchConditionHeader = null;

	int pageNo = 1;
	
	Post moreFlag = new Post();

	public static TaxiFragment newInstance() {
		TaxiFragment fragment = new TaxiFragment();
		Bundle args = new Bundle();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_TAXI_REFRESH));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try
		{
			rootView = inflater.inflate(layout.fragment_taxi_main, container, false);

			snsLoginHeader = getActivity().getLayoutInflater().inflate(layout.taxi_main_list_header1, null);
			searchConditionHeader = getActivity().getLayoutInflater().inflate(layout.taxi_main_list_header3, null);
			nearUsersCountHeader = getActivity().getLayoutInflater().inflate(layout.taxi_main_list_header2, null);
			footer = getActivity().getLayoutInflater().inflate(layout.list_footer_taxi_main, null);

			listMain = (ListView) rootView.findViewById(id.listMain);

			listMain.addHeaderView(searchConditionHeader, null, false);
			listMain.addHeaderView(nearUsersCountHeader, null, false);
			
			if ( "Guest".equals(application.getLoginUser().getType() ) )
			{
				listMain.addHeaderView(snsLoginHeader, null, false );

				snsLoginHeader.findViewById(id.btnKakaoLogin).setOnClickListener(this);
				snsLoginHeader.findViewById(id.btnFBLogin).setOnClickListener(this);
			}
			else
			{
				listMain.removeHeaderView(snsLoginHeader);
			}
			
			listMain.addFooterView(footer, null, false);
			listMain.setHeaderDividersEnabled(true);
			listMain.setFooterDividersEnabled(false);

			adapter = new TaxiArrayAdapter( getActivity().getApplicationContext(), this, 0 );
			listMain.setAdapter(adapter);

			initializeComponents();

			listMain.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
										int arg2, long arg3) {
					// TODO Auto-generated method stub
					Post post = (Post) arg1.getTag();

					Intent intent = new Intent(getActivity(), TaxiPostDetailActivity.class);
					intent.putExtra("postID", post.getPostID());

					if (departure != null) {
						intent.putExtra("fromLatitude", String.valueOf(departure.latitude));
						intent.putExtra("fromLongitude", String.valueOf(departure.longitude));
					}

					if (destination != null) {
						intent.putExtra("toLatitude", String.valueOf(destination.latitude));
						intent.putExtra("toLongitude", String.valueOf(destination.longitude));
					}

					startActivity(intent);
					getActivity().overridePendingTransition(anim.slide_in_from_right, anim.slide_out_to_left);
				}
			});

			moreFlag.setMoreFlag(true);
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
		
		return rootView;
	}

	Spinner spSearchOrder = null;
	Spinner spStatus = null;
	ArrayAdapter<CharSequence> adapterSearchOrder = null;
	
	public void initializeComponents()
	{
		spSearchOrder = (Spinner) rootView.findViewById(id.spSearchOrder);
		adapterSearchOrder = ArrayAdapter.createFromResource( getActivity(),
				array.search_order, android.R.layout.simple_spinner_item);
		adapterSearchOrder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spSearchOrder.setAdapter(adapterSearchOrder);
//		spSearchOrder.setSelection(0, false);
		spSearchOrder.setOnItemSelectedListener( this );
		
		spStatus = (Spinner) rootView.findViewById(id.spStatus);
		ArrayAdapter<CharSequence> adapterStatus = ArrayAdapter.createFromResource( getActivity(),
				array.status_list, android.R.layout.simple_spinner_item);
		adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spStatus.setAdapter(adapterStatus);
		spStatus.setSelection(0, false);
		spStatus.setOnItemSelectedListener( this );
		
		Button btnAddPost = (Button) rootView.findViewById(id.btnAddPost);
		btnAddPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try
				{
					if ( "Guest".equals( application.getLoginUser().getType()))
					{
						showYesNoDialog("확인", "SNS 계정연동 후에 등록하실 수 있습니다.\r\n\r\nSNS계정연동하시겠습니까?\r\n", "kakaoLoginCheck" );
						return;
					}
					
					Intent intent = new Intent( getActivity(), NewTaxiPostActivity.class);
					if ( departure != null )
					{
						intent.putExtra("address", departureAddress );
						intent.putExtra("departure", departure);
					}

					startActivityForResult(intent, REQUEST_NEW_POST );
					getActivity().overridePendingTransition(anim.slide_in_from_bottom, anim.stay);
				}
				catch( Exception ex )
				{
					application.catchException(TaxiFragment.this, ex);
				}
			}
		});
		
		Button btnSearchDetail = (Button) searchConditionHeader.findViewById(id.btnSearchDetail);
		btnSearchDetail.setOnClickListener(this);
		
		TextView txtNumOfUsers = (TextView) rootView.findViewById(id.txtNumOfUsers);
		txtNumOfUsers.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try
		{
			super.onActivityResult(requestCode, resultCode, data);

			if ( resultCode == getActivity().RESULT_OK )
			{
				if ( requestCode == REQUEST_NEW_POST || requestCode == REQUEST_POST_DETAIL )
				{
					if ( data.getExtras().getBoolean("reload") )
					{
						reloadContents();
						MainActivity mainActivity = (MainActivity) getActivity();
						mainActivity.loadMenuItems();
						mainActivity.reloadProfile();
					}
				}
			}
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
			if ( Constants.FAIL.equals(result) )
			{
				rootView.findViewById(id.marker_progress).setVisibility(ViewGroup.GONE);
				showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}

			super.doPostTransaction(requestCode, result);

			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});
			
			if ( "0000".equals( response.getResCode() ) )
			{
				if ( requestCode == GET_POSTS )
				{
					rootView.findViewById(id.marker_progress).setVisibility(ViewGroup.GONE);
					listMain.setVisibility(ViewGroup.VISIBLE);
					
					String postData = mapper.writeValueAsString( response.getData() );
					List<Post> postList = mapper.readValue( postData, new TypeReference<List<Post>>(){});
					
					String moreFlagString = response.getData2().toString().split("\\|")[0];
					String totalCount = response.getData2().toString().split("\\|")[1];

					adapter.remove(moreFlag);
					
					if ( "Guest".equals(application.getLoginUser().getType() ) )
					{
						listMain.removeHeaderView(snsLoginHeader);
						listMain.addHeaderView(snsLoginHeader, null, false );
						snsLoginHeader.findViewById(id.btnKakaoLogin).setOnClickListener(this);
						snsLoginHeader.findViewById(id.btnFBLogin).setOnClickListener(this);
					}
					else
					{
						listMain.removeHeaderView(snsLoginHeader);
					}
					
					if ( "true".equals( moreFlagString ) )
					{
						postList.add( moreFlag );
					}
					
					adapter.addAll(postList);
					adapter.notifyDataSetChanged();

					int userCount = Integer.parseInt( response.getData3().toString() );
					
					if ( postList.size() == 0 )
					{
						listMain.removeFooterView(footer);
						listMain.addFooterView(footer, null, false );
						footer.findViewById(id.txtGuide1).setVisibility(ViewGroup.GONE);
						TextView txtView = (TextView) footer.findViewById(id.txtGuide);
						txtView.setVisibility(ViewGroup.VISIBLE);
						
						if ( userCount > 0 )
							txtView.setText("근처에 " + userCount + " 명의 회원이 있습니다\r\n"
									+ "새 글을 등록해서 합승을 제안해보세요.\r\n근처의 회원들에게는 푸쉬메시지가 전송됩니다.");
						else
							txtView.setText("현재 등록된 내역이 없습니다.\r\n새 글을 등록해서 합승을 제안해보세요.\r\n근처의 회원들에게는 푸쉬메시지가 전송됩니다.");
					}
					else
					{
						listMain.removeFooterView(footer);
					}
					
					listMain.removeHeaderView(nearUsersCountHeader);
					
					if ( departure != null )
					{
						listMain.addHeaderView(nearUsersCountHeader, null, false);
						rootView.findViewById(id.layoutUsers).setVisibility(ViewGroup.VISIBLE);
						
						TextView txtNumOfUsers = (TextView) rootView.findViewById(id.txtNumOfUsers);
						if ( userCount > 0 )
						{
							txtNumOfUsers.setText("근처에 " + userCount + " 명의 사용자가 있습니다.\n합승등록을 하면 이들에게 푸시메시지가 전송됩니다.");
						}
						else
						{
							txtNumOfUsers.setText("근처에 사용자를 찾을 수 없습니다.\nGPS 가 활성화 되어 있는 지 확인해 주시기 바랍니다.");
						}
					}
					else
						rootView.findViewById(id.layoutUsers).setVisibility(ViewGroup.GONE);
				}				
			}
			else
			{
				showOKDialog("경고", response.getResMsg(), null);
				return;
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	public void inquiryPosts() throws Exception
	{
		HashMap hash = new HashMap();

		String fromLatitude = "";
		String fromLongitude = "";
		
		if ( departure != null )
		{
			fromLatitude = String.valueOf( departure.latitude ); 
			fromLongitude = String.valueOf( departure.longitude );
		}

		if ( !Util.isEmptyString( fromLatitude ) && !Util.isEmptyString( fromLongitude ) )
		{
			hash.put("fromLatitude", fromLatitude);
			hash.put("fromLongitude", fromLongitude);			
		}
		
		if ( destination != null && destination.latitude != 0 && destination.longitude != 0 )
		{
			hash.put("toLatitude", String.valueOf( destination.latitude ) );
			hash.put("toLongitude", String.valueOf( destination.longitude ) );
		}
		
		if ( !"전체".equals(departureDistance) && !Util.isEmptyString( departureDistance ) )
			hash.put("fromDistance", Util.getDistanceDouble( departureDistance ));
		
		if ( !"전체".equals(destinationDistance) && !Util.isEmptyString( destinationDistance ) )
			hash.put("toDistance", Util.getDistanceDouble( destinationDistance ) );
		
		hash.put("userID", application.getLoginUser().getUserID() );
		
		Spinner spStatus = (Spinner) rootView.findViewById(id.spStatus);
		String status = spStatus.getSelectedItem().toString();
		if ( !"전체".equals( status ) )
			hash.put("status", status );

		hash.put("pageNo", pageNo );
		
		if ( pageNo == 1 )
			adapter.clear();
		
		sendHttp("/taxi/getPostsNearHereV2.do", mapper.writeValueAsString(hash), GET_POSTS );
	}

	LatLng departure = null;
	LatLng destination = null;
	String currentAddress = "";
	String departureDistance = "";
	String departureAddress = "";
	String destinationDistance = "";
	String destinationAddress= "";
	
	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		super.doAction(actionName, param);
		
		try
		{
			if ( "userProfile".equals( actionName ) )
			{
				goUserProfileActivity(param.toString());
			}
			else if ( "searchResult".equals( actionName ) )
			{
				if ( param != null && param instanceof HashMap )
				{
					HashMap hash = (HashMap) param;
					
					if ( hash.containsKey("departure") && hash.get("departure") != null )
						departure = (LatLng) hash.get("departure");
					else
						departure = null;
					
					if ( hash.containsKey("destination") && hash.get("destination") != null )
						destination = (LatLng) hash.get("destination");
					else
						destination = null;
					
					if ( hash.containsKey("departureAddress") && hash.get("departureAddress") != null )
						departureAddress = hash.get("departureAddress").toString();
					else
						departureAddress = null;
					
					if ( hash.containsKey("destinationAddress") && hash.get("destinationAddress") != null )
						destinationAddress = hash.get("destinationAddress").toString();
					else
						destinationAddress = null;
					
					if ( hash.containsKey("departureDistance") && hash.get("departureDistance") != null )
						departureDistance = hash.get("departureDistance").toString();
					else
						departureDistance = null;
					
					if ( hash.containsKey("destinationDistance") && hash.get("destinationDistance") != null )
						destinationDistance = hash.get("destinationDistance").toString();
					else
						destinationDistance = null;
				}
				
				if ( departure != null || destination != null )
				{
					if ( "최근순".equals( spSearchOrder.getSelectedItem() ) )
						spSearchOrder.setSelection(1);//거리순
					else
					{
						reloadContents();
					}
				}
				else if ( departure == null && destination == null )
					spSearchOrder.setSelection(0);
				
			}
			else if ( "loadMore".equals( actionName ) )
			{
				pageNo++;
				inquiryPosts();
			}
				
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	public void goUserProfileActivity( String userID )
	{
		Intent intent = new Intent( getActivity(), UserProfileActivity.class);
		intent.putExtra("userID", userID );
		startActivity(intent);
		getActivity().overridePendingTransition(anim.slide_in_from_bottom, anim.stay);
	}
	
	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try
			{
				reloadContents();
			}
			catch( Exception ex )
			{
				catchException(this, ex);
			}
		}
	};

	public void reloadContents() throws Exception {
		listMain.setVisibility(ViewGroup.GONE);
		rootView.findViewById(id.marker_progress).setVisibility(ViewGroup.VISIBLE);

		adapter.clear();
		pageNo = 1;
		inquiryPosts();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mMessageReceiver);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void onItemSelected(AdapterView<?> spinner , View selectedView, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		try
		{
			TextView txtView = (TextView) selectedView;
			
			if ( spinner.getId() == id.spSearchOrder )
			{
				if ("최근순".equals( txtView.getText().toString() ))
				{
					departure = null;
					departureAddress = null;
				}
				else if ( "거리순".equals( txtView.getText().toString() ))
				{
					if ( departure == null )
					{
						if ( Util.isEmptyString(NearhereApplication.latitude ) || Util.isEmptyString(NearhereApplication.longitude ) )
						{
							showOKDialog("알림", "출발지를 설정해 주십시오.", null);
							spSearchOrder.setSelection(0);
							return;
						}
						
						departure = new LatLng( Double.parseDouble( NearhereApplication.latitude ), Double.parseDouble( NearhereApplication.longitude ));
						departureAddress = NearhereApplication.address;
					}
				}
			}

			reloadContents();
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v.getId() == id.btnSearchDetail )
				showDialog();
			else if ( v.getId() == id.txtNumOfUsers )
			{
				Intent intent = new Intent( getActivity(), UserListActivity.class);
				intent.putExtra("latitude", String.valueOf( departure.latitude ) );
				intent.putExtra("longitude", String.valueOf( departure.longitude ) );
				startActivity(intent);
				getActivity().overridePendingTransition(anim.slide_in_from_right, anim.slide_out_to_left);
			}
			else if ( v.getId() == id.btnLeftMenu )
			{
				getActivity().sendBroadcast(new Intent("openDrawer"));
			}
			else if ( v.getId() == id.btnRefresh )
			{
				adapter.clear();
				pageNo = 1;
				listMain.setVisibility(ViewGroup.GONE);
				rootView.findViewById(id.marker_progress).setVisibility(ViewGroup.VISIBLE);
				inquiryPosts();
			}
			else if ( v.getId() == id.btnKakaoLogin )
			{
				showYesNoDialog("확인", "카카오 계정으로 로그인하시겠습니까?", "kakaoLogin" );
			}
			else if ( v.getId() == id.btnFBLogin )
			{
				showYesNoDialog("확인", "Facebook 계정으로 로그인하시겠습니까?", "facebookLogin" );
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	public void showDialog() {

	    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    HashMap hash = new HashMap();
	    hash.put("departure", departure);
	    hash.put("departureAddress", departureAddress );
	    hash.put("departureDistance", departureDistance);
	    hash.put("destination", destination);
	    hash.put("destinationAddress", destinationAddress);
	    hash.put("destinationDistance", destinationDistance);
	    
	    DialogFragment newFragment = new SearchDialogFragment( this, hash );
	    newFragment.show(ft, "dialog");
	}
	
	@Override
	public void yesClicked(Object param) {
		// TODO Auto-generated method stub
		super.yesClicked(param);
		
		if ( "kakaoLogin".equals( param ) || "facebookLogin".equals( param ) )
		{
			MainActivity mainActivity = (MainActivity) getActivity();
			mainActivity.yesClicked("logout");
		}
		else if ("kakaoLoginCheck".equals( param ) )
		{
			MainActivity mainActivity = (MainActivity) getActivity();
			mainActivity.kakaoLogout();
		}
	}
}
