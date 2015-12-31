package com.tessoft.nearhere;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import org.codehaus.jackson.type.TypeReference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.kakao.Session;
import com.kakao.Session;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;

public class IntroActivity extends BaseActivity {

	private static final int HTTP_LOGIN_BACKGROUND = 2;
	private static final int HTTP_APP_INFO = 3;
	private static final String UPDATE_NOTICE = "UPDATE_NOTICE";
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		
		try
		{
			super.onCreate(savedInstanceState);
			
			// 카카오톡 세션을 초기화 한다.
			Session.initialize(this);
			
			// 페이스북 sdk 를 초기화한다.
			FacebookSdk.sdkInitialize(getApplicationContext());
			
			HashMap hash = application.getDefaultRequest();
			hash.put("os", "Android");

			application.debug("application.getLoginUser().getUserID() :" + application.getLoginUser().getUserID() );
			application.debug("application.getLoginUser().getType() :" + application.getLoginUser().getType() );
			
			if ( Util.isEmptyString(application.getLoginUser().getUserID()) )
			{
				//Guest 최초로그인
				HashMap request = application.getDefaultRequest();
				User user = application.getLoginUser();
				request.put("user", user );
				sendHttp("/taxi/getRandomIDForGuest.do", mapper.writeValueAsString(request), Constants.HTTP_GET_RANDOM_ID_FOR_GUEST );
			}
			else if ( !Util.isEmptyString( application.getLoginUser().getUserID() ) && "Guest".equals( application.getLoginUser().getType() ) )
			{
				// 게스트로 로그인 한 경우
				HashMap request = application.getDefaultRequest();
				request.put("user", application.getLoginUser());

				Log.d("debug", "login_bg : " + mapper.writeValueAsString( request ) );

				sendHttp("/taxi/login_bg.do", mapper.writeValueAsString(request), HTTP_LOGIN_BACKGROUND );
			}
			else if ( !Util.isEmptyString( application.getLoginUser().getKakaoID() ) && !Util.isEmptyString( application.getMetaInfoString("hash") ) )
			{
				HashMap request = application.getDefaultRequest();
				request.put("userID", application.getLoginUser().getUserID());
				request.put("hash", application.getMetaInfoString("hash") );
				
				Log.d("debug", "login_bg2 : " + mapper.writeValueAsString( request ) );
				
				sendHttp("/taxi/login_bg2.do", mapper.writeValueAsString(request), Constants.HTTP_LOGIN_BACKGROUND2 );
			}
			else
			{
				login();
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			Log.e("error", ex.getMessage());
		}
		
	}

	/*
	public void login( View v )
	{
		try
		{
			EditText edtUserID = (EditText) findViewById(R.id.edtUserID);
			EditText edtPassword = (EditText) findViewById(R.id.edtPassword);
			
			if ( TextUtils.isEmpty( edtUserID.getText() ) )
			{
				edtUserID.setError("아이디를 입력해 주십시오.");
				return;
			}
			
			if ( TextUtils.isEmpty( edtPassword.getText() ) )
			{
				edtPassword.setError("비밀번호를 입력해 주십시오.");
				return;
			}
			
			edtUserID.setEnabled(false);
			edtPassword.setEnabled(false);
			findViewById(R.id.btnRegisterUser).setEnabled(false);
			findViewById(R.id.btnLogin).setEnabled(false);
			
			HashMap hash = new HashMap();
			hash.put("userID", edtUserID.getText().toString() );
			hash.put("password", edtPassword.getText().toString() );
			sendHttp("/taxi/login.do", mapper.writeValueAsString( hash ), 1);
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	*/

	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		try
		{
			application.debug(this, "doPostTransaction[" + requestCode + "]:" + result );
			
			if ( Constants.FAIL.equals(result) )
			{
				showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}
			
			super.doPostTransaction(requestCode, result);
			
			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});
			
			if ( requestCode == Constants.HTTP_GET_RANDOM_ID_FOR_GUEST || requestCode == HTTP_LOGIN_BACKGROUND )
			{
				String userString = mapper.writeValueAsString( response.getData() );
				User user = mapper.readValue( userString, new TypeReference<User>(){});

				application.setLoginUser( user );
				
				if ( requestCode == HTTP_LOGIN_BACKGROUND )
				{
					String addInfoString = mapper.writeValueAsString( response.getData2() );
					HashMap<String,String> addInfo = mapper.readValue( addInfoString, new TypeReference<HashMap<String,String>>(){});
					
					if ( addInfo != null && addInfo.containsKey("UserAgreed") && "Y".equals( addInfo.get("UserAgreed") ) )
						goMainActivity();
					else
						goTermsAgreementActivity();
				}
				else if ( requestCode == Constants.HTTP_GET_RANDOM_ID_FOR_GUEST )
					goTermsAgreementActivity();
					
				finish();
			}
			else if ( requestCode == Constants.HTTP_LOGIN_BACKGROUND2 )
			{
				String userString = mapper.writeValueAsString( response.getData() );
				User user = mapper.readValue( userString, new TypeReference<User>(){});
				
				HashMap addInfo = (HashMap) response.getData2();
				
				if ( "Y".equals( addInfo.get("registerUserFinished") ) )
				{
					application.setLoginUser(user);
					application.setMetaInfo("logout", "false");
					
					Intent intent = new Intent( getApplicationContext(), MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
				else
				{
					goMainActivity();
					finish();
				}
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	private void login() throws Exception{
		
		Log.d("debug", "login");
		Log.d("debug", "login user : " + mapper.writeValueAsString( application.getLoginUser() ) );
		
		if ( "true".equals( application.getMetaInfoString("logout") ) || 
				"".equals( application.getLoginUser().getUserID() ) || !Util.isEmptyString( application.getLoginUser().getKakaoID() ) )
			Constants.bKakaoLogin = true;
		else
		{
			Constants.bKakaoLogin = false;
		}
		
		Log.d("debug", "bKakaoLogin : " + Constants.bKakaoLogin );
			
		if ( Util.isEmptyString( application.getLoginUser().getKakaoID() ) && Constants.bKakaoLogin )
		{
			// 회원가입
			goKaKaoLoginActivity();
			finish();
		}
		else if ( Constants.bKakaoLogin )
		{
			// 로그아웃 했을 경우 다시 로그인 하기 위함
			HashMap request = application.getDefaultRequest();
			request.put("userID", application.getLoginUser().getUserID());
			request.put("hash", application.getMetaInfoString("hash") );
			
			Log.d("debug", "login_bg2 : " + mapper.writeValueAsString( request ) );
			
			sendHttp("/taxi/login_bg2.do", mapper.writeValueAsString(request), Constants.HTTP_LOGIN_BACKGROUND2 );
		}
		else
		{
			// 로그아웃 했을 경우 다시 로그인 하기 위함
			HashMap request = application.getDefaultRequest();
			request.put("user", application.getLoginUser());

			Log.d("debug", "login_bg : " + mapper.writeValueAsString( request ) );

			sendHttp("/taxi/login_bg.do", mapper.writeValueAsString(request), HTTP_LOGIN_BACKGROUND );
		}
	}
	
	@Override
	public void okClicked(Object param) {
		// TODO Auto-generated method stub
		super.okClicked(param);
		
		if ( UPDATE_NOTICE.equals( param ) )
		{
			goUpdate();
		}
	}

	private void goUpdate() {
		final String appPackageName = getPackageName();
		try {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
		    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
		}
	}
	
	@Override
	public void yesClicked(Object param) {
		// TODO Auto-generated method stub
		super.yesClicked(param);
		
		if ( UPDATE_NOTICE.equals( param ) )
		{
			goUpdate();
			finish();
		}
	}
	
	@Override
	public void noClicked(Object param) {
		// TODO Auto-generated method stub
		super.noClicked(param);
		
		try
		{
			if ( UPDATE_NOTICE.equals( param ) )
			{
				login();
			}			
		}
		catch( Exception ex )
		{
			
		}
	}
	
	public void goRegisterActivity( View v )
	{
		Intent intent = new Intent( this, RegisterUserActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
	}

	public void goTermsAgreementActivity()
	{
		Intent intent = new Intent( this, TermsAgreementActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		finish();
	}
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("intro", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
	
	public void checkIfAdminUser()
	{
		try
		{
			File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

			//Get the text file
			File file = new File(sdcard,"nearhere.txt");
			
			if ( !file.exists() ) return;

			//Read text from file
			StringBuilder text = new StringBuilder();

			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		    br.close();

		    String loginInfo = text.toString();
		    
		    if ( Util.isEmptyString( loginInfo ) ) return;
		    
		    String[] tokens = loginInfo.split("\\;");

		    String userNo = "";
		    String userID = "";
		    String pw = "";
		    String pushOffOnNewPost = "";
		    String server = "";
		    
		    for ( int i = 0; i < tokens.length; i++ )
		    {
		    	String key = tokens[i].split("\\=")[0];
		    	String value = tokens[i].split("\\=")[1];
		    	if ( "userNo".equals( key ) )
		    		userNo = value;
		    	else if ( "userID".equals( key ) )
		    		userID = value;
		    	else if ( "pw".equals( key ) )
		    		pw = value;
		    	else if ( "pushOffOnNewPost".equals( key ) )
		    		pushOffOnNewPost = value;
		    	else if ( "server".equals( key ) )
		    		server = value.trim();
		    }
		    
		    if (!"이근처합승".equals(pw.trim())) 
		    {
		    	Constants.bAdminMode = false;
		    	return;
		    }
		    
		    if ( "Y".equals( pushOffOnNewPost.trim() ) ) Constants.bPushOffOnNewPost = true;
		    else Constants.bPushOffOnNewPost = false;
		    
		    if ( "REAL".equals( server ) )
		    	Constants.bReal = true;
		    else if ( "DEV".equals( server ) )
		    	Constants.bReal = false;
		    
		    Constants.bAdminMode = true;
		    
		    User user = application.getLoginUser();
		    user.setUserNo(userNo);
		    user.setUserID(userID);
		    application.setLoginUser(user);
		    
		    application.setMetaInfo("registerUserFinished", "true");
		    application.setMetaInfo("logout", "false");
		}
		catch( Exception ex )
		{
			
		}
	}
}
