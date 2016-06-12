package com.tessoft.nearhere.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tessoft.common.Constants;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.User;
import com.tessoft.domain.UserMessage;
import com.tessoft.nearhere.R;
import com.tessoft.nearhere.R.anim;
import com.tessoft.nearhere.R.id;
import com.tessoft.nearhere.activities.UserMessageActivity;
import com.tessoft.nearhere.adapters.MessageBoxListAdapter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MessageBoxFragment extends BaseListFragment {

	MessageBoxListAdapter adapter = null;
	RelativeLayout layoutFooter = null;
	protected View fragmentRootView = null;

	// TODO: Rename and change types and number of parameters
	public static MessageBoxFragment newInstance() {
		MessageBoxFragment fragment = new MessageBoxFragment();
		Bundle args = new Bundle();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try
		{
			super.onCreateView(inflater, container, savedInstanceState);

			if ( fragmentRootView == null )
			{
				fragmentRootView = rootView;
				rootView.findViewById(id.titleBar).setVisibility(ViewGroup.GONE);

				footer = getActivity().getLayoutInflater().inflate(R.layout.fragment_messagebox_footer, null);

				layoutFooter = (RelativeLayout) footer.findViewById(id.layoutFooter);
				layoutFooter.setVisibility(ViewGroup.GONE);
				TextView txtView = (TextView) layoutFooter.findViewById(id.txtGuide);
				txtView.setText("메시지내역이 없습니다.");

				listMain = (ListView) rootView.findViewById(id.listMain);
				adapter = new MessageBoxListAdapter(getActivity(), 0);
				listMain.addFooterView(footer, null, false );
				listMain.setAdapter(adapter);

				inquiryMessage();

				listMain.setOnItemClickListener( new AdapterView.OnItemClickListener() {

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
				btnRefresh.setOnClickListener(new View.OnClickListener() {

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
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}

		return fragmentRootView;
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
					layoutFooter.setVisibility(ViewGroup.VISIBLE);
				else
					layoutFooter.setVisibility(ViewGroup.GONE);
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
