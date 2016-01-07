package com.tessoft.nearhere.activities;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
/*
import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.Session;
import com.kakao.UserManagement;
import com.kakao.template.loginbase.SampleLoginActivity;
*/

import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.UserManagement;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tessoft.common.Constants;
import com.tessoft.common.HttpTransactionReturningString;
import com.tessoft.common.OKDialogListener;
import com.tessoft.common.TransactionDelegate;
import com.tessoft.common.UploadTask;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;
import com.tessoft.nearhere.NearhereApplication;
import com.tessoft.nearhere.R;

public class KakaoLoginActivity extends KakaoSampleLoginActivity{

	static NearhereApplication application = null;
	
	ObjectMapper mapper = new ObjectMapper();
	
	CallbackManager callbackManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_kakao_login);

			// 카카오 로그인에 필요한 해쉬키 알아내기 위해
			getAppKeyHash();
			
			application = (NearhereApplication) getApplication();
			
			setTitle("SNS 계정 로그인");
			findViewById(R.id.btnRefresh).setVisibility(ViewGroup.GONE);
			
			setupFacebookLogin();
		    
		}
		catch( Exception ex )
		{
			application.showToastMessage("exception");
		}
	}

	private void setupFacebookLogin() {
		LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
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
		    		            	findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
		    		            	findViewById(R.id.layoutLogin).setVisibility(ViewGroup.GONE);
		    		            	
		    		            	registerUserProceed( object );
		    		            }

		    		        });
		    		Bundle parameters = new Bundle();
		    		parameters.putString("fields", "id,name,link,gender, first_name, last_name, picture.width(9999) ");
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
		    }

		    @Override
		    public void onError(FacebookException exception) {
		        // App code
		    }
		});
	}

	protected void setTitle( String title ) {
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		findViewById(R.id.txtTitle).setVisibility(ViewGroup.VISIBLE);
		txtTitle.setText( title );
	}
	
	private void getAppKeyHash() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));
				Log.d("debug", "hash key:[" + something + "]");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("name not found", e.toString());
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	protected void onSessionOpened(){
		
		Log.d("debug", "[KakaoLoginActivity] onSessionOpened. starting KakaoSignupActivity.");
		
		final Intent intent = new Intent( this, KakaoSignupActivity.class);
        startActivity(intent);
        finish();
	}

	public static void onClickLogout() {

		UserManagement.requestLogout(new LogoutResponseCallback() {
			@Override
			protected void onSuccess(final long userId) {
			}

			@Override
			protected void onFailure(final APIErrorResult apiErrorResult) {
			}
		});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
	
	private void registerUserProceed( JSONObject object )
	{
		try
		{
			if ( object == null )
			{
				showOKDialog("오류", "로그인 데이터가 올바르지 않습니다.\r\n다시 시도해 주십시오.", null, null );
				return;
			}
			
			String id = object.getString("id");
			String name = object.getString("name");
			String gender = object.getString("gender");
			String facebookURL = object.getString("link");
			String picture = "";
			
			if ( object.has("picture") && object.getJSONObject("picture") != null )
			{
				JSONObject pictureObj = object.getJSONObject("picture");
				if ( pictureObj.has("data") )
					picture = pictureObj.getJSONObject("data").getString("url");
			}
			
			getRandomIDV2( id , name, gender, picture , facebookURL );	
		}
		catch( Exception ex )
		{
			application.debug(ex.getMessage());
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
	
	private void getRandomIDV2( String facebookID, String name, String gender, String facebookProfileImageURL, String facebookURL ) throws IOException, JsonGenerationException,
	JsonMappingException {
		setProgressBarIndeterminateVisibility(true);
		HashMap request = application.getDefaultRequest();
		
		final User user = application.getLoginUser();
		user.setFacebookID(facebookID);
		user.setUserName( name );
		user.setSex( "male".equals( gender ) ? "M": ("femail".equals( gender ) ? "F" : "" ) );
		user.setFacebookProfileImageURL(facebookProfileImageURL);
		user.setFacebookURL(facebookURL);
		
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
		}, "/taxi/getRandomIDV2.do", Constants.HTTP_GET_RANDOM_ID_V2 ).execute( mapper.writeValueAsString(request) );
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
			imageLoader.loadImage( user.getFacebookProfileImageURL(), targetSize, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

					if (loadedImage != null )
					{
						Log.d("imageResult", "image loaded. " + loadedImage.getWidth() + " " + loadedImage.getHeight() );
						
						new UploadTask( getApplicationContext(), user.getUserID() , Constants.HTTP_PROFILE_IMAGE_UPLOAD, 
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
											}
										}, null );
									}
								}
								catch( Exception ex )
								{
									application.catchException(this, ex);
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
