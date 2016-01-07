package com.tessoft.nearhere.activities;

import java.util.HashMap;

import org.apache.http.util.TextUtils;
import org.codehaus.jackson.type.TypeReference;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tessoft.common.Constants;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;
import com.tessoft.nearhere.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class MoreUserInfoActivity extends BaseActivity {

	private static final int UPDATE_USER_INFO = 1;
	DisplayImageOptions options = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try
		{
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_more_user_info);
			
			options = new DisplayImageOptions.Builder()
			.resetViewBeforeLoading(true)
			.cacheInMemory(true)
			.showImageOnLoading(R.drawable.no_image)
			.showImageForEmptyUri(R.drawable.no_image)
			.showImageOnFail(R.drawable.no_image)
			.displayer(new RoundedBitmapDisplayer(50))
			.delayBeforeLoading(100)
			.build();
			
			Spinner spSex = (Spinner) findViewById(R.id.spSex);
			ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource( this,
			        R.array.sex_list, android.R.layout.simple_spinner_item);
			sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spSex.setAdapter(sexAdapter);
			
			if ( !"".equals( application.getLoginUser().getUserName() ) )
			{
				EditText edtUserName = (EditText) findViewById(R.id.edtUserName);
				edtUserName.setText( application.getLoginUser().getUserName() );
			}
			
			if ( !"".equals( application.getLoginUser().getProfileImageURL() ) )
			{
				findViewById(R.id.layoutProfileImage).setVisibility(ViewGroup.VISIBLE);
				ImageView imgProfile = (ImageView) findViewById(R.id.imgProfile);
				ImageLoader.getInstance().displayImage(Constants.getThumbnailImageURL() + application.getLoginUser().getProfileImageURL(), imgProfile, options );
			}
			else
				findViewById(R.id.layoutProfileImage).setVisibility(ViewGroup.GONE);
			
			if ( "M".equals( application.getLoginUser().getSex() ) )
				spSex.setSelection( 1 );
			else if ( "F".equals( application.getLoginUser().getSex() ) )
				spSex.setSelection( 2 );

			setTitle("사용자 정보입력");
			findViewById(R.id.btnRefresh).setVisibility(ViewGroup.GONE);
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.more_user_info, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void start(View v )
	{
		try
		{
			Spinner spSex = (Spinner) findViewById(R.id.spSex);
			
			if ( "미지정".equals( spSex.getSelectedItem() ) )
			{
				showOKDialog("확인", "성별을 선택해 주십시오.", null);
				return;
			}
			
			EditText edtUserName = (EditText) findViewById(R.id.edtUserName);
			if ( TextUtils.isEmpty( edtUserName.getText() ) )
			{
				edtUserName.setError("이름을 입력해 주십시오.");
				return;
			}
			
			User user = application.getLoginUser();
			
			if ( "남".equals( spSex.getSelectedItem() ))
				user.setSex("M");
			else if ( "여".equals( spSex.getSelectedItem() ))
				user.setSex("F");
			
			user.setUserName( edtUserName.getText().toString() );
			
			setProgressBarIndeterminateVisibility(true);
			sendHttp("/taxi/updateUserInfo.do", mapper.writeValueAsString( user ), UPDATE_USER_INFO );
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
			setProgressBarIndeterminateVisibility(false);
			
			super.doPostTransaction(requestCode, result);
			
			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});
			
			if ( requestCode == UPDATE_USER_INFO )
			{
				if ( "0000".equals( response.getResCode() ) )
				{
					String addInfoString = mapper.writeValueAsString( response.getData2() );
					HashMap addInfo = mapper.readValue( addInfoString, new TypeReference<HashMap>(){});
					String userString = mapper.writeValueAsString( addInfo.get("user") );
					User user = mapper.readValue( userString, new TypeReference<User>(){});
					application.setLoginUser(user);
					
					goMainActivity();
					finish();
				}
				else
				{
					showOKDialog("경고", response.getResMsg(), null);
					return;
				}
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	public void goTaxiTutorialActivity()
	{
		Intent intent = new Intent( this, TaxiTutorialActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		super.finish();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
}
