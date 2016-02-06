package com.tessoft.nearhere.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.Post;
import com.tessoft.domain.User;
import com.tessoft.domain.UserLocation;
import com.tessoft.nearhere.PhotoViewer;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.adapters.TaxiArrayAdapter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class UserProfileActivity extends BaseActivity {

	ListView listMain = null;
	View header = null;
	View footer = null;
	TaxiArrayAdapter adapter = null;
	DisplayImageOptions options = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try
		{
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_user_profile);

			header = getLayoutInflater().inflate(R.layout.user_profile_list_header, null);
			footer = getLayoutInflater().inflate(R.layout.user_profile_list_footer, null);

			listMain = (ListView) findViewById(R.id.listMain);
			listMain.addHeaderView(header, null, false );

			adapter = new TaxiArrayAdapter( this , this, 0);
			listMain.setAdapter(adapter);

			listMain.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if ( arg1.getTag() != null && arg1.getTag() instanceof Post )
					{
						Post post = (Post) arg1.getTag();

						Intent intent = new Intent( getApplicationContext(), TaxiPostDetailActivity.class);
						intent.putExtra("postID", post.getPostID());
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);						
					}
				}
			});

			footer.findViewById(R.id.txtNone).setVisibility(ViewGroup.GONE);

			inquiryUserInfo();

			ImageView imgProfile = (ImageView) header.findViewById(R.id.imgProfile);
			imgProfile.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if ( v.getTag() != null )
					{
						Intent intent = new Intent( getApplicationContext(), PhotoViewer.class);
						intent.putExtra("imageURL", v.getTag().toString());
						startActivity(intent);
						overridePendingTransition(R.anim.fade_in, R.anim.stay);						
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
			
			setTitle("사용자정보");
			
			Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
			btnRefresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						inquiryUserInfo();
					} catch ( Exception ex ) {
						// TODO Auto-generated catch block
						catchException(this, ex);
					}
				}
			});
			
			if ( getIntent().getExtras().getString("userID").equals( application.getLoginUser().getUserID() ))
				header.findViewById(R.id.btnSendMessage).setVisibility(ViewGroup.GONE);
			else
				header.findViewById(R.id.btnSendMessage).setVisibility(ViewGroup.VISIBLE);
		}
		catch( Exception ex )
		{
			Log.e("error", ex.getMessage());
		}
	}
	
	protected void setTitle( String title ) {
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText( title );
	}

	private void inquiryUserInfo() throws IOException, JsonGenerationException,
			JsonMappingException {
		
		findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
		listMain.setVisibility(ViewGroup.GONE);
		
		HashMap hash = application.getDefaultRequest();
		hash.put("userID", application.getLoginUser().getUserID() );
		hash.put("userIDToInquiry", getIntent().getExtras().getString("userID"));
		sendHttp("/taxi/getUserInfoV2.do", mapper.writeValueAsString( hash ), 1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked"})
	public void goUserMessageActivity( View v ) {
		
		if ( "Guest".equals( application.getLoginUser().getType()))
		{
			showOKDialog("확인", "SNS 계정연동 후에 등록하실 수 있습니다.\r\n\r\n메인 화면에서 SNS연동을 할 수 있습니다.", "kakaoLoginCheck" );
			return;
		}
		
		HashMap hash = new HashMap();
		hash.put("fromUserID", getIntent().getExtras().getString("userID") );
		hash.put("userID",  application.getLoginUser().getUserID() );
		Intent intent = new Intent( this, UserMessageActivity.class);
		intent.putExtra("messageInfo", hash );
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
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
			this.overridePendingTransition(R.anim.stay, R.anim.slide_out_to_bottom);
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

			listMain.setVisibility(ViewGroup.VISIBLE);
			
			super.doPostTransaction(requestCode, result);		


			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				if ( requestCode == 1 )
				{
					HashMap hash = (HashMap) response.getData();

					String userString = mapper.writeValueAsString( hash.get("user") );
					String locationListString = mapper.writeValueAsString( hash.get("locationList") );
					String userPostString = mapper.writeValueAsString( hash.get("userPost") );
					String postsUserRepliedString = mapper.writeValueAsString( hash.get("postsUserReplied") );

					User user = mapper.readValue(userString, new TypeReference<User>(){});
					List<UserLocation> locationList = mapper.readValue(locationListString, new TypeReference<List<UserLocation>>(){});
					List<Post> postList = mapper.readValue(userPostString, new TypeReference<List<Post>>(){});
					List<Post> userPostsReplied = mapper.readValue(postsUserRepliedString, new TypeReference<List<Post>>(){});
					postList.addAll( userPostsReplied );

					ImageView imgProfile = (ImageView) header.findViewById(R.id.imgProfile);
					imgProfile.setImageResource(R.drawable.no_image);

					if ( user != null && user.getProfileImageURL() != null && user.getProfileImageURL().isEmpty() == false )
					{
						ImageLoader.getInstance().displayImage( Constants.getThumbnailImageURL() +
								user.getProfileImageURL() , imgProfile, options);
						imgProfile.setTag(user.getProfileImageURL());
					}

					TextView txtUserName = (TextView) header.findViewById(R.id.txtUserName);

					if ( !Util.isEmptyString( user.getUserName() ) )
						txtUserName.setText( user.getUserName() );

					TextView txtCreditValue = (TextView) header.findViewById(R.id.txtCreditValue);
					txtCreditValue.setText( user.getProfilePoint() + "%" );
					ProgressBar progressCreditValue = (ProgressBar) header.findViewById(R.id.progressCreditValue);
					progressCreditValue.setProgress( Integer.parseInt( user.getProfilePoint() ));

					if ( user.getBirthday() != null && !"".equals( user.getBirthday() ) )
					{
						String birthday = Util.getFormattedDateString(user.getBirthday(),"yyyy-MM-dd", "yyyy.MM.dd");
						TextView txtBirthday = (TextView) header.findViewById(R.id.txtBirthday );
						txtBirthday.setText( birthday );
					}

					for ( int i = 0; i < locationList.size(); i++ )
					{
						UserLocation loc = locationList.get(i);
						if ( "집".equals( loc.getLocationName() ) )
						{
							TextView txtHomeLocation = (TextView) header.findViewById(R.id.txtHomeLocation);
							txtHomeLocation.setText( loc.getAddress() );
						}
						else if ( "직장".equals( loc.getLocationName() ))
						{
							TextView txtOfficeLocation = (TextView) header.findViewById(R.id.txtOfficeLocation);
							txtOfficeLocation.setText( loc.getAddress() );
						}
					}

					ImageView imgSex = (ImageView) header.findViewById(R.id.imgSex);
					TextView txtSex = (TextView) header.findViewById(R.id.txtSex);
					imgSex.setVisibility(ViewGroup.VISIBLE);

					if ( "M".equals( user.getSex() ))
					{
						imgSex.setImageResource(R.drawable.ic_male);
						txtSex.setText("남자");
					}
					else if ( "F".equals( user.getSex() ))
					{
						imgSex.setImageResource(R.drawable.ic_female);
						txtSex.setText("여자");
					}
					else
						imgSex.setVisibility(ViewGroup.GONE);

					if ( user.getJobTitle() != null && !"".equals( user.getJobTitle() ))
					{
						TextView txtJobTitle = (TextView) header.findViewById(R.id.txtJobTitle);
						txtJobTitle.setText( user.getJobTitle() );
					}
					
					if ( !Util.isEmptyString( user.getKakaoID() ) )
						findViewById(R.id.imgKakaoIcon).setVisibility(ViewGroup.VISIBLE);
					else
						findViewById(R.id.imgKakaoIcon).setVisibility(ViewGroup.GONE);
					
					if ( !Util.isEmptyString( user.getFacebookID() ) )
						findViewById(R.id.imgFacebookIcon).setVisibility(ViewGroup.VISIBLE);
					else
						findViewById(R.id.imgFacebookIcon).setVisibility(ViewGroup.GONE);

					adapter.clear();
					adapter.addAll(postList);
					adapter.notifyDataSetChanged();

					if ( postList.size() == 0 )
						footer.findViewById(R.id.txtNone).setVisibility(ViewGroup.VISIBLE);
					
					LinearLayout layoutFacebook = (LinearLayout) header.findViewById(R.id.layoutFacebook);
					if ( Util.isEmptyString( user.getFacebookID() ) )
						layoutFacebook.setVisibility(ViewGroup.GONE);
					else
					{
						layoutFacebook.setVisibility(ViewGroup.VISIBLE);
						TextView txtFacebookURL = (TextView) header.findViewById(R.id.txtFacebookURL);
						txtFacebookURL.setTag( user.getFacebookURL() );
					}
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
	
	@Override
	public void onBackPressed() {

		finish();
		
		if ( MainActivity.active == false )
		{
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);			
		}
	}
	
	public void goFacebook( View v )
	{
		if ( v != null && v.getTag() != null )
		{
			try
			{
				String facebookURL = v.getTag().toString();
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse( facebookURL ));
				startActivity(browserIntent);
			}
			catch( Exception ex )
			{
				
			}
		}
	}
}
