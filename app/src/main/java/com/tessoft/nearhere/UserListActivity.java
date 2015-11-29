package com.tessoft.nearhere;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import com.tessoft.common.Constants;
import com.tessoft.common.UserArrayAdapter;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class UserListActivity extends BaseActivity implements OnItemClickListener, OnItemSelectedListener{

	private static final int HTTP_GET_USERS_NEAR_HERE = 1;
	UserArrayAdapter adapter = null;
	ListView listMain = null;
	Spinner spDistance = null;
	ArrayAdapter<CharSequence> adapterDistance = null;
	int pageNo = 1;
	String distance = "5";
	User moreFlag = new User();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try
		{
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_user_list);
			
			adapter = new UserArrayAdapter(this, this, 0);
			
			View header = getLayoutInflater().inflate(R.layout.user_list_header, null);
			
			listMain = (ListView) findViewById(R.id.listMain);
			listMain.addHeaderView(header, null, false );
			listMain.setHeaderDividersEnabled(true);
			listMain.setAdapter(adapter);

			listMain.setOnItemClickListener(this);
			
			findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
			listMain.setVisibility(ViewGroup.GONE);
			
			inquiryList();
			
			spDistance = (Spinner) findViewById(R.id.spDistance);
			adapterDistance = ArrayAdapter.createFromResource( this, R.array.user_by_distance, android.R.layout.simple_spinner_item);
			adapterDistance.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spDistance.setAdapter(adapterDistance);
			spDistance.setSelection(2, false);
			spDistance.setOnItemSelectedListener( this );
			
			TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
			txtTitle.setText("5km 이내의 사용자");
			
			moreFlag.setMoreFlag(true);
			
			setTitle("사용자 목록");
			
			Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
			btnRefresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
						listMain.setVisibility(ViewGroup.GONE);
						inquiryList();
					} catch ( Exception ex ) {
						// TODO Auto-generated catch block
						catchException(this, ex);
					}
				}
			});
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	protected void setTitle( String title ) {
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText( title );
	}
	
	private void inquiryList() throws IOException, JsonGenerationException,
			JsonMappingException {
		HashMap hash = application.getDefaultRequest();
		hash.put("fromLatitude", getIntent().getExtras().getString("latitude"));
		hash.put("fromLongitude", getIntent().getExtras().getString("longitude"));
		hash.put("userID", application.getLoginUser().getUserID() );
		hash.put("pageNo", pageNo );
		hash.put("distance", distance );
		
		sendHttp("/taxi/getUsersNearHere.do", mapper.writeValueAsString( hash ), HTTP_GET_USERS_NEAR_HERE );
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
	
	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		
		findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
		
		if ( Constants.FAIL.equals(result) )
		{
			showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
			return;
		}
		
		super.doPostTransaction(requestCode, result);
		
		listMain.setVisibility(ViewGroup.VISIBLE);
		
		try
		{
			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				if ( requestCode == HTTP_GET_USERS_NEAR_HERE )
				{
					adapter.remove(moreFlag);
					
					String postData = mapper.writeValueAsString( response.getData() );
					List<User> userList = mapper.readValue( postData, new TypeReference<List<User>>(){});
					
					String moreFlagString = response.getData2().toString().split("\\|")[0];
					String totalCount = response.getData2().toString().split("\\|")[1];
					
					if ( "true".equals( moreFlagString ) )
					{
						userList.add( moreFlag );
					}
					
					adapter.addAll(userList);
					adapter.notifyDataSetChanged();
					
					TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
					txtSubTitle.setText( distance + "km 이내의 사용자(" + totalCount + "명)");
				}
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		try
		{
			UserArrayAdapter.ViewHolder viewHolder = (UserArrayAdapter.ViewHolder) arg1.getTag();
			
			Intent intent = new Intent( this, UserProfileActivity.class);
			intent.putExtra("userID", viewHolder.user.getUserID() );
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		try
		{
			TextView txtView = (TextView) arg1;
			
			TextView txtSubTitle = (TextView) findViewById(R.id.txtSubTitle);
			txtSubTitle.setText( txtView.getText().toString() + " 이내의 사용자");
			
			distance = txtView.getText().toString().replaceAll("km", "");
			
			pageNo = 1;
			adapter.clear();
			
			findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
			listMain.setVisibility(ViewGroup.GONE);
			
			inquiryList();
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		try
		{
			 
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	@Override
	public void doAction(String actionName, Object param) {
		// TODO Auto-generated method stub
		super.doAction(actionName, param);
		
		try
		{
			if ( "loadMore".equals( actionName ) )
			{
				pageNo++;
				inquiryList();
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
}
