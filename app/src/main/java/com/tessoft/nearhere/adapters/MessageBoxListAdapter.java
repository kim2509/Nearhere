package com.tessoft.nearhere.adapters;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.UserMessage;
import com.tessoft.nearhere.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageBoxListAdapter extends ArrayAdapter<UserMessage> {

	private List<UserMessage> itemList = new ArrayList<UserMessage>();
	private AdapterDelegate delegate = null;

	LayoutInflater inflater = null;

	@Override
	public void add(UserMessage object) {
		itemList.add(object);
		super.add(object);
	}

	public MessageBoxListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return this.itemList.size();
	}

	public UserMessage getItem(int index) {
		return this.itemList.get(index);
	}

	public void setItemList( List<UserMessage> itemList )
	{
		this.itemList = itemList;
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			UserMessage item = getItem(position);

			if (row == null) {
				row = inflater.inflate(R.layout.list_message_box_item, parent, false);
			}

			ImageView imgProfile = (ImageView) row.findViewById(R.id.imgProfile);
			imgProfile.setImageResource(R.drawable.no_image);
			
			if ( item.getUser() != null && item.getUser().getProfileImageURL() != null && 
					!"".equals( item.getUser().getProfileImageURL() ) )
			{
				ImageLoader.getInstance().displayImage( Constants.getThumbnailImageURL() +
						item.getUser().getProfileImageURL() , imgProfile);
			}
			
			if ( item.getUser() != null && Util.isEmptyString(item.getUser().getUserName()) == false )
			{
				TextView txtUserName = (TextView) row.findViewById(R.id.txtUserName);
				txtUserName.setText( item.getUser().getUserName() );
			}
			
			TextView txtMessage = (TextView) row.findViewById(R.id.txtMessage);
			txtMessage.setText( item.getMessage() );
			TextView txtCreatedDate = (TextView) row.findViewById(R.id.txtCreatedDate);
			txtCreatedDate.setText( Util.getFormattedDateString( item.getCreatedDate(), "yyyy-MM-dd HH:mm") );
			
			TextView txtNew = (TextView) row.findViewById(R.id.txtNew);
			if ( item.isRead() == false )
				txtNew.setVisibility(ViewGroup.VISIBLE);
			else
				txtNew.setVisibility(ViewGroup.GONE);
			
			row.setTag( item );
		}
		catch( Exception ex )
		{
			Log.e("error", ex.getMessage());
		}

		return row;
	}

	public AdapterDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(AdapterDelegate delegate) {
		this.delegate = delegate;
	}

}