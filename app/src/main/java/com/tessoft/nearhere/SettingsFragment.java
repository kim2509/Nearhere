package com.tessoft.nearhere;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import com.tessoft.common.Constants;
import com.tessoft.common.SettingsAdapter;
import com.tessoft.domain.APIResponse;
import com.tessoft.domain.SettingListItem;
import com.tessoft.domain.User;
import com.tessoft.domain.UserSetting;

import com.tessoft.nearhere.fragment.BaseListFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class SettingsFragment extends BaseListFragment {

	SettingsAdapter adapter = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try
		{
			super.onCreateView(inflater, container, savedInstanceState);

			listMain = (ListView) rootView.findViewById(R.id.listMain);
			adapter = new SettingsAdapter(getActivity(), this, 0);

			SettingListItem setting = new SettingListItem();
			setting.setSettingName("쪽지알림받기");
			setting.setSettingValue("Y");
			adapter.add(setting);

			setting = new SettingListItem();
			setting.setSettingName("댓글알림받기");
			setting.setSettingValue("Y");
			adapter.add(setting);

			setting = new SettingListItem();
			setting.setSettingName("추천알림받기");
			setting.setSettingValue("Y");
			adapter.add(setting);
			
			setting = new SettingListItem();
			setting.setSettingName("프로필 조회 알림받기");
			setting.setSettingValue("Y");
			adapter.add(setting);
			
			setting = new SettingListItem();
			setting.setSettingName("신규회원 알림받기");
			setting.setSettingValue("Y");
			adapter.add(setting);

			listMain.setAdapter(adapter);

			inquirySettingInfo();
			
			Button btnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
			btnRefresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						inquirySettingInfo();
					} catch ( Exception ex ) {
						// TODO Auto-generated catch block
						catchException(this, ex);
					}
				}
			});
			
			setTitle("설정");
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}

		return rootView;
	}

	private void inquirySettingInfo() throws IOException,
			JsonGenerationException, JsonMappingException {
		User user = application.getLoginUser();

		rootView.findViewById(R.id.marker_progress).setVisibility(ViewGroup.VISIBLE);
		listMain.setVisibility(ViewGroup.GONE);
		
		sendHttp("/taxi/getUserSetting.do", mapper.writeValueAsString(user), 1 );
	}

	@Override
	public void doPostTransaction(int requestCode, Object result) {
		// TODO Auto-generated method stub
		try
		{
			rootView.findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
			
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
					String settingString = mapper.writeValueAsString( response.getData() );
					UserSetting setting = mapper.readValue( settingString , new TypeReference<UserSetting>(){});

					if ( setting == null )
					{
						setting = new UserSetting();
						setting.setMessagePushReceiveYN("Y");
						setting.setReplyPushReceiveYN("Y");
						setting.setRecommendPushReceiveYN("Y");
						setting.setInquiryUserPushReceiveYN("Y");
						setting.setNewUserPushReceiveYN("Y");
					}

					for ( int i = 0; i < adapter.getCount(); i++ )
					{
						SettingListItem item = adapter.getItem(i);
						if ("쪽지알림받기".equals( item.getSettingName() ))
							item.setSettingValue( setting.getMessagePushReceiveYN() );
						else if ("댓글알림받기".equals( item.getSettingName() ))
							item.setSettingValue( setting.getReplyPushReceiveYN() );
						else if ("추천알림받기".equals( item.getSettingName() ))
							item.setSettingValue( setting.getRecommendPushReceiveYN() );
						else if ("프로필 조회 알림받기".equals( item.getSettingName() ))
							item.setSettingValue( setting.getInquiryUserPushReceiveYN() );
						else if ("신규회원 알림받기".equals( item.getSettingName() ))
							item.setSettingValue( setting.getNewUserPushReceiveYN() );
					}

					adapter.notifyDataSetChanged();
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
		// TODO Auto-generated method stub
		try
		{
			super.doAction(actionName, param);

			UserSetting setting = new UserSetting();
			setting.setUserID( application.getLoginUser().getUserID() );

			for ( int i = 0; i < adapter.getCount(); i++ )
			{
				SettingListItem item = adapter.getItem(i);
				if ("쪽지알림받기".equals( item.getSettingName() ))
					setting.setMessagePushReceiveYN( item.getSettingValue() );
				else if ("댓글알림받기".equals( item.getSettingName() ))
					setting.setReplyPushReceiveYN(item.getSettingValue() );
				else if ("추천알림받기".equals( item.getSettingName() ))
					setting.setRecommendPushReceiveYN(item.getSettingValue() );
				else if ("프로필 조회 알림받기".equals( item.getSettingName() ))
					setting.setInquiryUserPushReceiveYN(item.getSettingValue() );
				else if ("신규회원 알림받기".equals( item.getSettingName() ))
					setting.setNewUserPushReceiveYN( item.getSettingValue() );
			}

			getActivity().setProgressBarIndeterminateVisibility(true);
			sendHttp("/taxi/updateUserSetting.do", mapper.writeValueAsString( setting ), 2);
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
				inquirySettingInfo();
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
		getActivity().registerReceiver(mMessageReceiver, new IntentFilter("refreshContents"));
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		getActivity().unregisterReceiver(mMessageReceiver);
	}
}
