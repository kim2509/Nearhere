package com.tessoft.nearhere.activities;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;

import com.tessoft.common.Constants;
import com.tessoft.common.TaxiPostReplyListAdapter;
import com.tessoft.common.Util;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.Post;
import com.tessoft.domain.User;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import com.tessoft.common.UploadTask;
import com.tessoft.domain.PostReply;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TaxiPostDetailActivity extends BaseListActivity 
implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, OnClickListener{

	private static final int DELETE_POST = 3;
	private static final int POST_DETAIL = 1;
	private static final int INSERT_POST_REPLY = 2;
	private static final int REQUEST_MODIFY_POST = 0;
	private static final int DELETE_POST_REPLY = 4;
	private static final int HTTP_UPDATE_POST_AS_FINISHED = 10;
	TaxiPostReplyListAdapter adapter = null;
	Post post = null;
	View header2 = null;
	View fbHeader = null;
	View headerPost = null;
	View headerButtons = null;
	GoogleMap map = null;
	int ZoomLevel = 13;
	ImageView imgProfile = null;
	TextView txtUserName = null;
	View footer2 = null;
	View footerPadding = null;
	Button btnFinish = null;
	
	CallbackManager callbackManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);

			header = getLayoutInflater().inflate(R.layout.taxi_post_detail_list_header_person, null);

			if ( !FacebookSdk.isInitialized() )
				FacebookSdk.sdkInitialize(getApplicationContext());
			
			fbHeader = getLayoutInflater().inflate(R.layout.taxi_main_list_header_fb, null);
			
			headerPost = getLayoutInflater().inflate(R.layout.taxi_post_detail_list_header_post, null);
			headerButtons = getLayoutInflater().inflate(R.layout.taxi_post_detail_list_buttons, null);
//			header = getLayoutInflater().inflate(R.layout.taxi_post_detail_list_header1, null);
			header2 = getLayoutInflater().inflate(R.layout.taxi_post_detail_list_header2, null);
			footer = getLayoutInflater().inflate(R.layout.taxi_post_detail_list_footer, null);
			footer2 = getLayoutInflater().inflate(R.layout.taxi_post_detail_list_footer_2, null);
			footerPadding = getLayoutInflater().inflate(R.layout.padding_bottom_50, null);

			listMain = (ListView) findViewById(R.id.listMain);
			listMain.addHeaderView(header, null, false );
			listMain.addHeaderView(fbHeader, null, false );
			listMain.addHeaderView(headerPost, null, false );
			listMain.addHeaderView(headerButtons, null, false );
			listMain.addHeaderView(header2 );
			listMain.setHeaderDividersEnabled( false );
			listMain.addFooterView(footer, null, false );
			listMain.addFooterView(footerPadding, null, false );
			listMain.setFooterDividersEnabled(false);

			listMain.setSelector(android.R.color.transparent);

			adapter = new TaxiPostReplyListAdapter( getApplicationContext(), application.getLoginUser(),0 );
			listMain.setAdapter(adapter);
			adapter.setDelegate(this);

			initializeComponent();

			inquiryPostDetail();
			
			setTitle("합승상세");
			
			setupFacebookLogin();
		}
		catch(Exception ex )
		{
			catchException(this, ex);
		}
	}

	private void inquiryPostDetail() throws IOException,
	JsonGenerationException, JsonMappingException {
		HashMap hash = new HashMap();
		
		findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
		listMain.setVisibility(ViewGroup.GONE);
		
		if ( getIntent().getExtras() != null )
		{
			hash.put("postID", getIntent().getExtras().getString("postID") );
			if ( getIntent().getExtras().containsKey("fromLatitude") )
				hash.put("fromLatitude", getIntent().getExtras().getString("fromLatitude") );
			if ( getIntent().getExtras().containsKey("fromLongitude") )
				hash.put("fromLongitude", getIntent().getExtras().getString("fromLongitude") );
			if ( getIntent().getExtras().containsKey("toLatitude") )
				hash.put("toLatitude", getIntent().getExtras().getString("toLatitude") );
			if ( getIntent().getExtras().containsKey("toLongitude") )
				hash.put("toLongitude", getIntent().getExtras().getString("toLongitude") );
		}
		
		hash.put("userID", application.getLoginUser().getUserID() );

		sendHttp("/taxi/getPostDetail.do", mapper.writeValueAsString(hash), POST_DETAIL );
	}

	public void initializeComponent() throws Exception
	{
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		makeMapScrollable();

		imgProfile = (ImageView) header.findViewById(R.id.imgProfile);
		imgProfile.setImageResource(R.drawable.no_image);
		imgProfile.setOnClickListener( this );
		
		txtUserName = (TextView) header.findViewById(R.id.txtUserName);
		
		btnFinish = (Button) footer2.findViewById(R.id.btnFinish);
		btnFinish.setOnClickListener(this);
		
		if ( "true".equals( application.getMetaInfoString("hideMapOnPostDetail") ) )
		{
			findViewById(R.id.map_layout).setVisibility(ViewGroup.GONE);
			Button btnHideMap = (Button)findViewById(R.id.btnHideMap);
			btnHideMap.setText("경로 보기");
		}
		else
		{
			findViewById(R.id.map_layout).setVisibility(ViewGroup.VISIBLE);
			Button btnHideMap = (Button)findViewById(R.id.btnHideMap);
			btnHideMap.setText("경로 숨기기");
		}
		
		Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					inquiryPostDetail();
				} catch ( Exception ex ) {
					// TODO Auto-generated catch block
					catchException(this, ex);
				}
			}
		});
		
		options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true)
		.cacheInMemory(true)
		.showImageOnLoading(R.drawable.no_image)
		.showImageForEmptyUri(R.drawable.no_image)
		.showImageOnFail(R.drawable.no_image)
		.displayer(new RoundedBitmapDisplayer(20))
		.delayBeforeLoading(100)
		.build();
		
		findViewById(R.id.btnViewProfile).setOnClickListener(this);
		findViewById(R.id.btnFbLogin).setOnClickListener(this);
	}

	DisplayImageOptions options = null;
	
	private void makeMapScrollable() {

		ImageView transparentImageView = (ImageView) header2.findViewById(R.id.transparent_image);

		transparentImageView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				try
				{
					int action = event.getAction();
					switch (action) {
					case MotionEvent.ACTION_DOWN:
						// Disallow ScrollView to intercept touch events.
						listMain.requestDisallowInterceptTouchEvent(true);
						// Disable touch on transparent view
						return false;

					case MotionEvent.ACTION_UP:
						// Allow ScrollView to intercept touch events.
						listMain.requestDisallowInterceptTouchEvent(false);
						return true;

					case MotionEvent.ACTION_MOVE:
						listMain.requestDisallowInterceptTouchEvent(true);
						return false;

					default: 
						return true;
					}   
				}
				catch( Exception ex )
				{
					Log.e("error", ex.getMessage());
				}

				return true;
			}
		});
	}

	private Menu menu = null;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.taxi_post_detail, menu);
		this.menu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		try
		{
			if (id == R.id.action_refresh) {
				inquiryPostDetail();
				return true;
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void yesClicked(Object param) {
		// TODO Auto-generated method stub
		super.yesClicked(param);


		try
		{
			if ( param instanceof String && "postDelete".equals( param ) )
			{
				sendHttp("/taxi/deletePost.do", mapper.writeValueAsString(post), DELETE_POST );				
			}
			else if ( param instanceof PostReply )
			{
				sendHttp("/taxi/deletePostReply.do", mapper.writeValueAsString(param), DELETE_POST_REPLY );	
			}
			else if ( param instanceof String && "DIALOG_FINISH_POST".equals(param) )
			{
				post.setStatus("종료됨");
				sendHttp("/taxi/modifyPost.do", mapper.writeValueAsString(post), HTTP_UPDATE_POST_AS_FINISHED );
				Intent data = new Intent();
				data.putExtra("reload", true);
				setResult(RESULT_OK, data);
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	public void goUserProfileActivity( String userID )
	{
		Intent intent = new Intent( this, UserProfileActivity.class);
		intent.putExtra("userID", userID );
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
	}

	public void goUserChatActivity()
	{
		Intent intent = new Intent( this, UserMessageActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
	}

	Marker departureMarker = null;
	Marker destinationMarker = null;

	@Override
	public void onMapReady(GoogleMap m ) {
		// TODO Auto-generated method stub
		try
		{
			this.map = m;
			
			//강남역
			moveMap(new LatLng(37.497916, 127.027546));
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	private void drawPostOnMap() {
		double fromLatitude = Double.parseDouble(post.getFromLatitude());
		double fromLongitude = Double.parseDouble(post.getFromLongitude());

		LatLng Fromlocation = new LatLng( fromLatitude, fromLongitude );

		double latitude = Double.parseDouble(post.getLatitude());
		double longitude = Double.parseDouble(post.getLongitude());

		LatLng location = new LatLng( latitude, longitude );

		departureMarker = map.addMarker(new MarkerOptions()
		.position( Fromlocation )
		.title("출발지"));

		destinationMarker = map.addMarker(new MarkerOptions()
		.position( location )
		.title("도착지"));

		PolylineOptions rectOptions = new PolylineOptions()
		.add( Fromlocation ) 
		.add( location );

		Polyline polyline = map.addPolyline(rectOptions);

		moveMap( location );

		destinationMarker.showInfoWindow();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	private void moveMap( LatLng location )
	{
		CameraUpdate center=
				CameraUpdateFactory.newLatLng( location );
		map.moveCamera(center);
		CameraUpdate zoom=CameraUpdateFactory.zoomTo(ZoomLevel);
		map.animateCamera(zoom);
	}

	public void addPostReply( View v )
	{
		try
		{
			EditText edtPostReply = (EditText) footer.findViewById(R.id.edtPostReply);

			if ( "Guest".equals( application.getLoginUser().getType()))
			{
				showOKDialog("확인", "SNS 계정연동 후에 등록하실 수 있습니다.\r\n\r\n메인 화면에서 SNS연동을 할 수 있습니다.", "kakaoLoginCheck" );
				return;
			}
			
			if ( TextUtils.isEmpty(edtPostReply.getText()) )
			{
				edtPostReply.setError("댓글을 입력해 주시기 바랍니다.");
				return;
			}

			PostReply postReply = new PostReply();
			postReply.setPostID( post.getPostID() );
			postReply.setUser( application.getLoginUser() );
			postReply.setLatitude( NearhereApplication.latitude );
			postReply.setLongitude( NearhereApplication.longitude );
			postReply.setMessage( edtPostReply.getText().toString() );
			edtPostReply.setText("");

			sendHttp("/taxi/insertPostReply.do", mapper.writeValueAsString(postReply), INSERT_POST_REPLY );

			findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
			listMain.setVisibility(ViewGroup.GONE);
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
			findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
			
			if ( Constants.FAIL.equals(result) )
			{
				showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}

			super.doPostTransaction(requestCode, result);

			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				listMain.setVisibility(ViewGroup.VISIBLE);
				
				if ( requestCode == POST_DETAIL )
				{
					String postData = mapper.writeValueAsString( response.getData() );
					post = mapper.readValue( postData, new TypeReference<Post>(){});

					drawPostOnMap();
					
					setUserData();

					setPostData();

					adapter.setItemList( post.getPostReplies() );

					if ( !Util.isEmptyString(application.getLoginUser().getFacebookID()) ||
							!post.getUser().getUserID().equals( application.getLoginUser().getUserID() ))
					{
						listMain.removeHeaderView(fbHeader);
						LinearLayout layoutFacebookLogin = (LinearLayout) findViewById(R.id.layoutFacebookLogin);
						if ( layoutFacebookLogin != null )
							layoutFacebookLogin.setVisibility(ViewGroup.GONE);
					}
					else
					{
						LinearLayout layoutFacebookLogin = (LinearLayout) findViewById(R.id.layoutFacebookLogin);
						if ( layoutFacebookLogin != null )
							layoutFacebookLogin.setVisibility(ViewGroup.VISIBLE);
					}
					
					boolean bAddedFooterButon = false;
					
					if ( post.getUser().getUserID().equals( application.getLoginUser().getUserID() ) )
					{
						if ( listMain.getFooterViewsCount() == 3 )
							listMain.removeFooterView(footer2);
						
						listMain.addFooterView(footer2, null, false );
						bAddedFooterButon = true;
						findViewById(R.id.btnSendMessage).setVisibility(ViewGroup.GONE);					
					}
					else
					{
						listMain.removeFooterView(footer2);
						findViewById(R.id.btnSendMessage).setVisibility(ViewGroup.VISIBLE);
					}
					
					if ( bAddedFooterButon == false && Constants.bAdminMode )
					{
						if ( listMain.getFooterViewsCount() == 3 )
							listMain.removeFooterView(footer2);
						
						listMain.addFooterView(footer2, null, false );
					}
				}
				else if ( requestCode == INSERT_POST_REPLY )
				{
					inquiryPostDetail();
				}
				else if ( requestCode == DELETE_POST )
				{
					Intent data = new Intent();
					data.putExtra("reload", true);
					setResult(RESULT_OK, data);
					finish();
				}
				else if ( requestCode == DELETE_POST_REPLY || requestCode == HTTP_UPDATE_POST_AS_FINISHED )
				{
					inquiryPostDetail();
					Intent intent = new Intent( Constants.BROADCAST_TAXI_REFRESH );
					getApplicationContext().sendBroadcast(intent);
					finish();
				}
				else if ( requestCode == Constants.HTTP_PROFILE_IMAGE_UPLOAD )
				{
					String userString = mapper.writeValueAsString( response.getData2() );
					User user = mapper.readValue(userString, new TypeReference<User>(){});
					application.setLoginUser(user);
					
					user.setFacebookID(id);
					user.setUserName(name);
					
					if ("male".equals( gender.toLowerCase() ) || "남성".equals( gender ))
						user.setSex("M");
					else
						user.setSex("F");
						
					user.setFacebookURL(facebookURL);
					user.setFacebookProfileImageURL(facebookProfileImageURL);
					
					listMain.setVisibility(ViewGroup.GONE);
					findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
					sendHttp("/taxi/updateFacebookInfo.do", mapper.writeValueAsString(user), Constants.HTTP_UPDATE_FACEBOOK_INFO );
				}
				else if ( requestCode == Constants.HTTP_UPDATE_FACEBOOK_INFO )
				{
					String addInfoString = mapper.writeValueAsString( response.getData() );
					HashMap addInfo = mapper.readValue( addInfoString, new TypeReference<HashMap>(){});
					String userString = mapper.writeValueAsString( addInfo.get("user") );
					User user = mapper.readValue(userString, new TypeReference<User>(){});
					application.setLoginUser(user);
					
					findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
					listMain.setVisibility(ViewGroup.VISIBLE);
					
					inquiryPostDetail();
					Intent resultIntent = new Intent();
					resultIntent.putExtra("reload", true);
					setResult(RESULT_OK, resultIntent);	
				}
			}
			else
			{
				listMain.setVisibility(ViewGroup.GONE);
				showOKDialog("경고", response.getResMsg(), "error" );
				return;
			}
		}
		catch( Exception ex )
		{
//			catchException(this, ex);
		}
	}

	private void setPostData() throws Exception {
		
		String title = post.getMessage();
		if ( title != null ) title = title.trim();
		
		TextView txtTitle = (TextView) headerPost.findViewById(R.id.txtTitle);
		txtTitle.setText( title );
		
		TextView txtStatus = (TextView) headerPost.findViewById(R.id.txtStatus);
		if ( "진행중".equals( post.getStatus() ) )
		{
			txtStatus.setText( post.getStatus() );
			txtStatus.setBackgroundColor(Color.parseColor("#fe6f2b"));
			if ( post.getUser().getUserID().equals( application.getLoginUser().getUserID() ))
				btnFinish.setVisibility(ViewGroup.VISIBLE);	
		}
		else
		{
			txtStatus.setBackgroundColor(Color.parseColor("#6f6f6f"));
			txtStatus.setText( post.getStatus() );
			btnFinish.setVisibility(ViewGroup.GONE);
		}

		TextView txtDeparture = (TextView) headerPost.findViewById(R.id.txtDeparture);
		txtDeparture.setText( post.getFromAddress() );

		TextView txtDestination = (TextView) headerPost.findViewById(R.id.txtDestination);
		txtDestination.setText( post.getToAddress() );

		if ( post.getDepartureDate() != null )
		{
			TextView txtDepartureDateTime = (TextView) headerPost.findViewById(R.id.txtDepartureDateTime);
			
			String departureDateTime = "";
			
			if ( post.getDepartureDate().indexOf("오늘") >= 0 )
			{
				if ( post.getDepartureTime().indexOf("지금") >= 0 )
					departureDateTime = Util.getFormattedDateString(post.getCreatedDate(), "MM-dd HH:mm") + " 출발";
				else
					departureDateTime = Util.getFormattedDateString(post.getCreatedDate(), "MM-dd") + " " + post.getDepartureTime() + " 출발";
			}
			else
			{
				if ( post.getDepartureTime().indexOf("지금") >= 0 )
					departureDateTime = Util.getFormattedDateString(post.getDepartureDate() , "MM-dd") + " " + Util.getFormattedDateString(post.getCreatedDate(), "HH:mm") + " 출발";
				else
					departureDateTime = Util.getFormattedDateString(post.getDepartureDate() + " " + post.getDepartureTime() + ":00", "MM-dd HH:mm") + " 출발";	
			}
				
			txtDepartureDateTime.setText( departureDateTime );
		}

		TextView txtCreatedDate = (TextView) headerPost.findViewById(R.id.txtCreatedDate);
		txtCreatedDate.setText( Util.getFormattedDateString(post.getCreatedDate(), "MM-dd HH:mm") + " 등록");
		
		setControlsVisibility( post );
	}

	private void setUserData() {
		if ( !Util.isEmptyString(post.getUser().getProfileImageURL()))
		{
			ImageLoader.getInstance().displayImage( Constants.getThumbnailImageURL() + 
					post.getUser().getProfileImageURL() , imgProfile, options );
		}

		txtUserName.setText( post.getUser().getUserName() );

		ImageView imgSex = (ImageView) header.findViewById(R.id.imgSex);
		imgSex.setVisibility(ViewGroup.VISIBLE);
		
		if ( "M".equals( post.getUser().getSex() ))
		{
			imgSex.setImageResource(R.drawable.ic_male);
		}
		else if ( "F".equals( post.getUser().getSex() ))
		{
			imgSex.setImageResource(R.drawable.ic_female);
		}
		else
			imgSex.setVisibility(ViewGroup.GONE);
		
		if ( !Util.isEmptyString( post.getUser().getKakaoID() ) )
			findViewById(R.id.imgKakaoIcon).setVisibility(ViewGroup.VISIBLE);
		else
			findViewById(R.id.imgKakaoIcon).setVisibility(ViewGroup.GONE);
		
		if ( !Util.isEmptyString( post.getUser().getFacebookURL() ) )
		{
			findViewById(R.id.imgFacebookIcon).setVisibility(ViewGroup.VISIBLE);
			findViewById(R.id.btnGoFacebook).setVisibility(ViewGroup.VISIBLE);
		}
		else
		{
			findViewById(R.id.imgFacebookIcon).setVisibility(ViewGroup.GONE);
			findViewById(R.id.btnGoFacebook).setVisibility(ViewGroup.GONE);
		}
		
		TextView txtCreditValue = (TextView) header.findViewById(R.id.txtCreditValue);
		txtCreditValue.setText( post.getUser().getProfilePoint() + "%" );
		ProgressBar progressCreditValue = (ProgressBar) header.findViewById(R.id.progressCreditValue);
		progressCreditValue.setProgress( Integer.parseInt( post.getUser().getProfilePoint() ));
	}
	
	private void setControlsVisibility( Post item ) {
		
		if ( !Util.isEmptyString( item.getVehicle() ) )
		{
			TextView txtVehicle = (TextView) findViewById(R.id.txtVehicle);
			txtVehicle.setVisibility(ViewGroup.VISIBLE);
			txtVehicle.setText( item.getVehicle() );
		}
		else
			findViewById(R.id.txtVehicle).setVisibility(ViewGroup.GONE);
		
		if ( !Util.isEmptyString( item.getFareOption() ) )
		{
			TextView txtFareOption = (TextView) findViewById(R.id.txtFareOption);
			txtFareOption.setVisibility(ViewGroup.VISIBLE);
			txtFareOption.setText( item.getFareOption() );
		}
		else
			findViewById(R.id.txtFareOption).setVisibility(ViewGroup.GONE);
		
		if ( "Y".equals( item.getRepetitiveYN() ) )
		{
			TextView txtRepeat = (TextView) findViewById(R.id.txtRepeat);
			txtRepeat.setVisibility(ViewGroup.VISIBLE);
		}
		else
			findViewById(R.id.txtRepeat).setVisibility(ViewGroup.GONE);
		
		if ( !"상관없음".equals( item.getSexInfo() ) && !Util.isEmptyString( item.getSexInfo() ) )
		{
			TextView txtSex = (TextView) findViewById(R.id.txtSex);
			txtSex.setVisibility(ViewGroup.VISIBLE);
			txtSex.setText( item.getSexInfo() );
		}
		else
			findViewById(R.id.txtSex).setVisibility(ViewGroup.GONE);
		
		if ( !"상관없음".equals( item.getNumOfUsers() ) && !Util.isEmptyString( item.getNumOfUsers() ) )
		{
			TextView txtNOP = (TextView) findViewById(R.id.txtNOP);
			txtNOP.setVisibility(ViewGroup.VISIBLE);
			txtNOP.setText( item.getNumOfUsers() );
		}
		else
			findViewById(R.id.txtNOP).setVisibility(ViewGroup.GONE);
		
		TextView readCount = (TextView) findViewById(R.id.txtReadCount);
		if ( item.getReadCount() > 0 )
		{
			readCount.setVisibility(ViewGroup.VISIBLE);
			readCount.setText( "조회 : " + item.getReadCount() );
		}
		else
			readCount.setVisibility(ViewGroup.GONE);
	}
	
	@Override
	public void okClicked(Object param) {
		// TODO Auto-generated method stub
		super.okClicked(param);
		
		if ( "error".equals( param ) )
			onBackPressed();
	}

	public void modifyPost( View v )
	{
		Intent intent = new Intent( this, NewTaxiPostActivity.class);
		intent.putExtra("mode", "modify");
		intent.putExtra("post", post);
		startActivityForResult(intent, REQUEST_MODIFY_POST );
		overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent data ) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, responseCode, data);

		try
		{
			callbackManager.onActivityResult(requestCode, responseCode, data);
			
			if ( responseCode == RESULT_OK )
			{
				inquiryPostDetail();

				Intent intent = new Intent( Constants.BROADCAST_TAXI_REFRESH );
				getApplicationContext().sendBroadcast(intent);
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			int id = v.getId();

			if ( id == R.id.txtUserName || id == R.id.imgProfile || id == R.id.btnViewProfile )
			{
				goUserProfileActivity( post.getUser().getUserID() );
			}
			else if ( id == R.id.btnFinish )
			{
				showYesNoDialog("확인", "정말 종료하시겠습니까?", "DIALOG_FINISH_POST");
				return;
			}
			else if ( id == R.id.btnFbLogin )
			{
				findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
				listMain.setVisibility(ViewGroup.GONE);
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		try
		{
			super.doAction(actionName, param);

			if ( "userProfile".equals( actionName ) )
				goUserProfileActivity( param.toString() );
			else if ( "deleteReply".equals( actionName ) )
			{
				showYesNoDialog("확인", "정말 삭제하시겠습니까?", param );
				return;
			}	
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	//This is the handler that will manager to process the broadcast intent
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try
			{
				if ( intent.getExtras() != null && intent.getExtras().containsKey("type"))
				{
					String type = intent.getExtras().getString("type");
					if ( "postReply".equals( type ) && intent.getExtras().containsKey("postID"))
					{
						String postID = intent.getExtras().getString("postID");
						if ( post.getPostID().equals( postID ) )
							inquiryPostDetail();
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
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		try
		{
			registerReceiver(mMessageReceiver, new IntentFilter("updateUnreadCount"));
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(mMessageReceiver);
	}
	
	@Override
	public void onBackPressed() {

		finish();
		
		if ( MainActivity.active == false )
		{
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);			
		}
	}
	
	public void toggleMapVisibility( View v )
	{
		Button btn = (Button) v;
		if ( findViewById(R.id.map_layout).getVisibility() == ViewGroup.GONE )
		{
			findViewById(R.id.map_layout).setVisibility(ViewGroup.VISIBLE);
			btn.setText("경로 숨기기");
			application.setMetaInfo("hideMapOnPostDetail", "false");
		}
		else
		{
			findViewById(R.id.map_layout).setVisibility(ViewGroup.GONE);
			btn.setText("경로 보기");
			application.setMetaInfo("hideMapOnPostDetail", "true");
		}
	}
	
	public void goFacebook( View v )
	{
		try
		{
			String facebookURL = post.getUser().getFacebookURL();
			if ( Util.isEmptyString( facebookURL ) ) return;
			
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( facebookURL ));
			startActivity(browserIntent);
		}
		catch( Exception ex )
		{
			
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public void goUserMessageActivity( View v ) {
		
		if ( "Guest".equals( application.getLoginUser().getType()))
		{
			showOKDialog("확인", "SNS 계정연동 후에 등록하실 수 있습니다.\r\n\r\n메인 화면에서 SNS연동을 할 수 있습니다.", "kakaoLoginCheck" );
			return;
		}
		
		HashMap hash = new HashMap();
		hash.put("fromUserID", post.getUser().getUserID() );
		hash.put("userID",  application.getLoginUser().getUserID() );
		Intent intent = new Intent( this, UserMessageActivity.class);
		intent.putExtra("messageInfo", hash );
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
	}
	
	private void setupFacebookLogin() {
		LoginButton loginButton = (LoginButton) findViewById(R.id.btnFbLogin);
		loginButton.setOnClickListener(this);
		loginButton.setReadPermissions("user_friends");

		callbackManager = CallbackManager.Factory.create();
		// Callback registration
		loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
		    @Override
		    public void onSuccess(LoginResult loginResult) {
		        // App code
		    	try
		    	{
		    		GraphRequest request = GraphRequest.newMeRequest(
		    		        loginResult.getAccessToken(),
		    		        new GraphRequest.GraphJSONObjectCallback() {
		    		            @Override
		    		            public void onCompleted(
		    		                   JSONObject object,
		    		                   GraphResponse response) {
		    		                // Application code
		    		            	
		    		            	listMain.setVisibility(ViewGroup.GONE);
		    						findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
		    		            	registerUserProceed( object );
		    		            }

		    		        });
		    		Bundle parameters = new Bundle();
		    		parameters.putString("fields", "id,name,link,gender, first_name, last_name, picture.width(9999) ");
		    		parameters.putString("locale","ko_KR");
		    		request.setParameters(parameters);
		    		request.executeAsync();
		    	}
		    	catch( Exception ex )
		    	{
		    	}
		    	
		    }

		    @Override
		    public void onCancel() {
		        // App code
		    	findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
				listMain.setVisibility(ViewGroup.VISIBLE);
		    }

		    @Override
		    public void onError(FacebookException exception) {
		        // App code
		    	findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
				listMain.setVisibility(ViewGroup.VISIBLE);
		    }
		});
	}
	
	String id = "";
	String name = "";
	String gender = "";
	String facebookURL = "";
	String facebookProfileImageURL = "";
	
	private void registerUserProceed( JSONObject object )
	{
		try
		{
			if ( object == null )
			{
				showOKDialog("오류", "로그인 데이터가 올바르지 않습니다.\r\n다시 시도해 주십시오.", null );
				return;
			}
			
			id = object.getString("id");
			name = object.getString("name");
			gender = object.getString("gender");
			facebookURL = object.getString("link");
			facebookProfileImageURL = "";
			
			if ( object.has("picture") && object.getJSONObject("picture") != null )
			{
				JSONObject pictureObj = object.getJSONObject("picture");
				if ( pictureObj.has("data") )
					facebookProfileImageURL = pictureObj.getJSONObject("data").getString("url");
			}
			
			if ( !Util.isEmptyString( facebookProfileImageURL ) )
			{
				ImageLoader imageLoader = ImageLoader.getInstance();
				
				ImageSize targetSize = new ImageSize(640, 640);
				imageLoader.loadImage( facebookProfileImageURL, targetSize, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

						if (loadedImage != null )
						{
							Log.d("imageResult", "image loaded. " + loadedImage.getWidth() + " " + loadedImage.getHeight() );
							new UploadTask( getApplicationContext(), application.getLoginUser().getUserID() , 
									Constants.HTTP_PROFILE_IMAGE_UPLOAD, TaxiPostDetailActivity.this ).execute( loadedImage );
						}
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						super.onLoadingFailed(imageUri, view, failReason);
						Log.d("debug", "onLoadingFailed:" + failReason );
					}
				});
			}
		}
		catch( Exception ex )
		{
			application.debug(ex.getMessage());
		}
	}
	
}
