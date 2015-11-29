package com.tessoft.nearhere.fragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import com.tessoft.common.Constants;
import com.tessoft.common.MessageBoxListAdapter;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;
import com.tessoft.domain.UserMessage;
import com.tessoft.nearhere.UserMessageActivity;
import com.tessoft.nearhere.R.anim;
import com.tessoft.nearhere.R.id;
import com.tessoft.nearhere.R.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MessageBoxFragment extends BaseListFragment {

	MessageBoxListAdapter adapter = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try
		{
			super.onCreateView(inflater, container, savedInstanceState);

			footer = getActivity().getLayoutInflater().inflate(layout.fragment_messagebox_footer, null);

			listMain = (ListView) rootView.findViewById(id.listMain);
			adapter = new MessageBoxListAdapter(getActivity(), 0);
			listMain.addFooterView(footer, null, false );
			listMain.setAdapter(adapter);

			inquiryMessage();

			listMain.setOnItemClickListener( new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					UserMessage um = (UserMessage) arg1.getTag();
					goUserChatActivity( um );
				}
			});
			
			setTitle("쪽지함");
			
			Button btnRefresh = (Button) rootView.findViewById(id.btnRefresh);
			btnRefresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						inquiryMessage();
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

		return rootView;
	}

	private void inquiryMessage() throws IOException, JsonGenerationException,
	JsonMappingException {
		User user = application.getLoginUser();

		rootView.findViewById(id.marker_progress).setVisibility(ViewGroup.VISIBLE);
		listMain.setVisibility(ViewGroup.GONE);
		
		sendHttp("/taxi/getUserMessageList.do", mapper.writeValueAsString(user), 1);
	}

	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		try
		{
			rootView.findViewById(id.marker_progress).setVisibility(ViewGroup.GONE);
			
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
				String noticeListString = mapper.writeValueAsString( response.getData() );
				List<UserMessage> messageList = mapper.readValue( noticeListString , new TypeReference<List<UserMessage>>(){});
				adapter.setItemList(messageList);
				adapter.notifyDataSetChanged();

				if ( messageList.size() == 0 )
				{
					listMain.removeFooterView(footer);
					listMain.addFooterView(footer, null, false );
					TextView txtView = (TextView) footer.findViewById(id.txtGuide);
					txtView.setText("메시지내역이 없습니다.");
				}
				else
					listMain.removeFooterView(footer);
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

	public void goUserChatActivity( UserMessage message )
	{
		try
		{
			HashMap hash = new HashMap();
			hash.put("fromUserID",  message.getUser().getUserID() );
			hash.put("userID",  application.getLoginUser().getUserID() );
			Intent intent = new Intent( getActivity(), UserMessageActivity.class);
			intent.putExtra("messageInfo", hash );
			startActivity(intent);
			getActivity().overridePendingTransition(anim.slide_in_from_right, anim.slide_out_to_left);
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
				inquiryMessage();
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
			getActivity().registerReceiver(mMessageReceiver, new IntentFilter("updateUnreadCount"));
			inquiryMessage();
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		getActivity().unregisterReceiver(mMessageReceiver);
	}
}
