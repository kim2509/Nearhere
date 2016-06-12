package com.tessoft.nearhere.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tessoft.common.Constants;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;
import com.tessoft.domain.UserMessage;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.adapters.UserMessageArrayAdapter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class UserMessageActivity extends BaseActivity {

	ListView listMain = null;
	View header = null;
	View footer = null;
	UserMessageArrayAdapter adapter = null;
	HashMap messageInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_user_message);

			findViewById(R.id.btnRefresh).setVisibility(ViewGroup.GONE);

			setTitle("메시지");

			header = getLayoutInflater().inflate(R.layout.list_header_user_message, null);

			listMain = (ListView) findViewById(R.id.listMain);
			//listMain.addHeaderView(header);

			adapter = new UserMessageArrayAdapter( getApplicationContext(), this, application.getLoginUser(), 0 );
			listMain.setAdapter(adapter);
			adapter.setDelegate(this);

			listMain.setDivider(null);

			listMain.setOnScrollListener( new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					bForceScrollDown = false;
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					// TODO Auto-generated method stub

				}
			});

			messageInfo = (HashMap) getIntent().getExtras().get("messageInfo");
			
			inquiryUserMessage();

			Button btnSend = (Button) findViewById(R.id.btnSend);
			btnSend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try
					{
						EditText edtMessage = (EditText) findViewById(R.id.edtMessage);
						String message = edtMessage.getText().toString();
						edtMessage.setText("");

						UserMessage userMessage = new UserMessage();
						userMessage.setFromUser( application.getLoginUser() );
						User toUser = new User();
						toUser.setUserID( messageInfo.get("fromUserID").toString() );
						userMessage.setToUser( toUser );
						userMessage.setMessage(message);
						setProgressBarIndeterminateVisibility(true);
						sendHttp("/taxi/sendUserMessage.do", mapper.writeValueAsString( userMessage ), 1);	
						bForceScrollDown = true;
					}
					catch( Exception ex )
					{
						catchException(this, ex);
					}
				}
			});
		}
		catch(Exception ex )
		{
			catchException(this, ex);
		}
	}

	private void inquiryUserMessage() throws IOException,
			JsonGenerationException, JsonMappingException {
		setProgressBarIndeterminateVisibility(true);
		sendHttp("/taxi/getUserMessage.do", mapper.writeValueAsString( messageInfo ), 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_chat, menu);
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

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		this.overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregisterReceiver(mMessageReceiver);
	}

	boolean bForceScrollDown = true;

	@Override
	public void doPostTransaction(int requestCode, Object result) {

		try
		{
			if ( Constants.FAIL.equals(result) )
			{
				setProgressBarIndeterminateVisibility(false);
				showOKDialog("통신중 오류가 발생했습니다.\r\n다시 시도해 주십시오.", null);
				return;
			}

			setProgressBarIndeterminateVisibility(false);

			// TODO Auto-generated method stub
			super.doPostTransaction(requestCode, result);	

			APIResponse response = mapper.readValue(result.toString(), new TypeReference<APIResponse>(){});

			if ( "0000".equals( response.getResCode() ) )
			{
				if ( requestCode == 1 )
				{
					String userMessageString = mapper.writeValueAsString( response.getData() );
					List<UserMessage> messageList = mapper.readValue( userMessageString , new TypeReference<List<UserMessage>>(){});
					adapter.setItemList(messageList);
					adapter.notifyDataSetChanged();

					if ( bForceScrollDown )
						listMain.setSelection(adapter.getCount() - 1);
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
	public void doAction(String actionName, Object param) {

		try
		{
			// TODO Auto-generated method stub
			super.doAction(actionName, param);

			if ( "openUserProfile".equals( actionName ) )
			{
				Intent intent = new Intent(this, PopupWebViewActivity.class);
				String url = Constants.getServerSSLURL() + "/user/userInfo.do?userID=" + param.toString() + "&isApp=Y";
				intent.putExtra("url", url);
				intent.putExtra("title", "사용자 정보" );
				startActivity(intent);
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
				inquiryUserMessage();
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
			inquiryUserMessage();
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
}
