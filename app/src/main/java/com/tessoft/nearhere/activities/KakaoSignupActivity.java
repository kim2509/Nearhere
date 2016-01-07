package com.tessoft.nearhere.activities;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*
import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.Session;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.template.loginbase.SampleSignupActivity;
*/

import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.Session;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.OKDialogListener;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.UploadTask;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;

public class KakaoSignupActivity extends KakaoSampleSignupActivity{

	NearhereApplication application = null;
	private final int HTTP_REQUEST_GET_RANDOM_ID_V2 = 2;
	private int PROFILE_IMAGE_UPLOAD = 1;
	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_kakao_signup);
		
		application = (NearhereApplication) getApplication();
		
		setTitle("카카오계정 로그인");
		findViewById(R.id.btnRefresh).setVisibility(ViewGroup.GONE);
	}
	
	protected void setTitle( String title ) {
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		findViewById(R.id.txtTitle).setVisibility(ViewGroup.VISIBLE);
		txtTitle.setText( title );
	}
	
	protected void redirectLoginActivity() {
		
		application.debug(this, "redirectLoginActivity");
		
        final Intent intent = new Intent(this, KakaoLoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectMainActivity() {
//        final Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
    	
    	application.debug(this, "redirectMainActivity");
    	
    	application.debug(this, "starting UserManagement.requestMe ...");

    	UserManagement.requestMe(new MeResponseCallback() {

			@Override
			protected void onSuccess(final UserProfile userProfile) {

				application.debug(this, "onSuccess");

				try {
					getRandomIDV2(userProfile.getId() + "", userProfile.getNickname(),
							userProfile.getThumbnailImagePath(), userProfile.getProfileImagePath());
				} catch (Exception ex) {

				}
			}

			@Override
			protected void onNotSignedUp() {
				application.debug(this, "onNotSignedUp");
			}

			@Override
			protected void onSessionClosedFailure(final APIErrorResult errorResult) {
				application.debug(this, "onSessionClosedFailure : " + errorResult);
			}

			@Override
			protected void onFailure(final APIErrorResult errorResult) {
				String message = "failed to get user info. msg=" + errorResult;
				application.debug(this, "onFailure : " + message);
			}
		});

    }
    
    private void getRandomIDV2( String kakaoID, String nickName, String thumbnailImageURL, String profileImageURL ) throws IOException, JsonGenerationException,
	JsonMappingException {
		setProgressBarIndeterminateVisibility(true);
		HashMap request = application.getDefaultRequest();
		
		final User user = application.getLoginUser();
		user.setKakaoID(kakaoID);
		user.setUserName( nickName );
		user.setKakaoThumbnailImageURL(thumbnailImageURL);
		user.setKakaoProfileImageURL(profileImageURL);
		
		request.put("user", user );
		
		application.debug(this, "getRandomIDV2 : " + mapper.writeValueAsString( request ) );
		
		new HttpTransactionReturningString( new TransactionDelegate() {
			@Override
			public void doPostTransaction(int requestCode, Object result) {
				// TODO Auto-generated method stub
				try
				{
					application.debug(this, "getRandomIDV2 return : " + mapper.writeValueAsString( result ) );
					
					uploadProfileImage( result.toString() );
				}
				catch( Exception ex )
				{
					Log.e("error", "uploadProfileImage", ex );
				}
			}
		}, "/taxi/getRandomIDV2.do", HTTP_REQUEST_GET_RANDOM_ID_V2 ).execute( mapper.writeValueAsString(request) );
	}
    
    public void uploadProfileImage( String result ) throws Exception 
	{
    	application.debug(this, "uploadProfileImage" );
    	
		APIResponse response = mapper.readValue(result, new TypeReference<APIResponse>(){}); 
		String userString = mapper.writeValueAsString( response.getData() );
		final User user = mapper.readValue(userString, new TypeReference<User>(){}); 
		
		HashMap addInfo = null;
		if ( response.getData2() != null )
		{
			addInfo = (HashMap) response.getData2();
			
			Log.d("debug", "addInfo is not null." );
			Log.d("debug", addInfo.toString() );
			
			if ( addInfo.containsKey("hash") )
				application.setMetaInfo("hash", addInfo.get("hash").toString());
			
			if ("Y".equals( addInfo.get("alreadyExistsYN") ) && "Y".equals( addInfo.get("registerUserFinished") ) )
			{
				goMainActivity();
				application.setLoginUser(user);
				return;
			}
			
			Log.d("debug", "starting profile image downloading...." );
			
			ImageLoader imageLoader = ImageLoader.getInstance();

			ImageSize targetSize = new ImageSize(640, 640);
			imageLoader.loadImage( user.getKakaoProfileImageURL(), targetSize, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

					if (loadedImage != null )
					{
						Log.d("imageResult", "image loaded. " + loadedImage.getWidth() + " " + loadedImage.getHeight() );
						
						new UploadTask( getApplicationContext(), user.getUserID() , PROFILE_IMAGE_UPLOAD, 
								new TransactionDelegate() {
							
							@Override
							public void doPostTransaction(int requestCode, Object result) {
								// TODO Auto-generated method stub
							
								try
								{
									APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){}); 
									
									application.debug( "upload result :" + result.toString() );

									application.debug( "upload result code :" + response.getResCode() );
									
									if ( "0000".equals( response.getResCode() ) )
									{
										String userString = mapper.writeValueAsString( response.getData2() );
										User user = mapper.readValue(userString, new TypeReference<User>(){});
										application.setLoginUser(user);
										goMoreUserInfoActivity( null );	
									}
									else
									{
										showOKDialog("오류", "카카오 로그인 도중 오류가 발생했습니다.", new OKDialogListener() {
											
											@Override
											public void okClicked(Object param) {
												// TODO Auto-generated method stub
												Session.getCurrentSession().close();
												redirectLoginActivity();
											}
										}, null );
									}
								}
								catch( Exception ex )
								{
									application.catchException(KakaoSignupActivity.this, ex);
								}
							}
						}).execute( loadedImage );
					}
					else
					{
						Log.d("debug", "downloaded image is null." );
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
		else
		{
			Log.d("debug", "addInfo is null." );
		}
	}

    public void showOKDialog( String title, String message, final OKDialogListener listener, final Object param )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle( title )
			   .setMessage( message )
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   if ( listener != null )
		        		   listener.okClicked( param );
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
    
    public void goMainActivity()
    {
    	Intent intent = new Intent( this, MainActivity.class);
    	startActivity(intent);
    	overridePendingTransition(android.R.anim.fade_in, 
    			android.R.anim.fade_out);
    	finish();
    }
    
    public void goMoreUserInfoActivity( View v )
	{
		Intent intent = new Intent( this, MoreUserInfoActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		finish();
	}
}
