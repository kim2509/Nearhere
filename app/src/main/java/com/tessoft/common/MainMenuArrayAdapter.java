package com.tessoft.common;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tessoft.domain.MainMenuItem;
import com.tessoft.nearhere.R;

public class MainMenuArrayAdapter extends ArrayAdapter<MainMenuItem>{

	LayoutInflater inflater = null;
	
	public MainMenuArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			MainMenuItem item = getItem(position);

			if (row == null) {
				row = inflater.inflate(R.layout.list_main_menu_item, parent, false);
			}

			ImageView icon_menu = (ImageView) row.findViewById(R.id.icon_menu);
			
			if ( "홈".equals( item.getMenuName() ) )
				icon_menu.setImageResource(R.drawable.ic_home_off);
			else if ( "내 정보".equals( item.getMenuName() ) )
				icon_menu.setImageResource(R.drawable.ic_myinfo_off);
			else if ( "알림메시지".equals( item.getMenuName() ) )
				icon_menu.setImageResource(R.drawable.ic_push_off);
			else if ( "쪽지함".equals( item.getMenuName() ) )
				icon_menu.setImageResource(R.drawable.ic_message_off);
			else if ( "공지사항".equals( item.getMenuName() ) )
				icon_menu.setImageResource(R.drawable.ic_notice_off);
			else if ( "설정".equals( item.getMenuName() ) )
				icon_menu.setImageResource(R.drawable.ic_setting_off);
			else if ( "로그아웃".equals( item.getMenuName() ) )
				icon_menu.setImageResource(R.drawable.ic_logout_off);
			
			TextView txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			txtTitle.setText( item.getMenuName() );
			
			TextView txtCount = (TextView) row.findViewById(R.id.txtCount);
			if ( item.getNotiCount() > 0 )
			{
				txtCount.setVisibility(ViewGroup.VISIBLE);
				txtCount.setText( String.valueOf(item.getNotiCount()) );
			}
			else
			{
				txtCount.setVisibility(ViewGroup.GONE);
				txtCount.setText( String.valueOf(item.getNotiCount()) );
			}
			
			row.setTag( item );
		}
		catch( Exception ex )
		{
			Log.e("error", ex.getMessage());
		}

		return row;
	}
}
