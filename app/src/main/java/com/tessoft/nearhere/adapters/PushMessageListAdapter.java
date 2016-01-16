package com.tessoft.nearhere.adapters;

import java.util.ArrayList;
import java.util.List;

import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.Util;
import com.tessoft.domain.UserPushMessage;
import com.tessoft.nearhere.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PushMessageListAdapter extends ArrayAdapter<UserPushMessage> {

	private List<UserPushMessage> itemList = new ArrayList<UserPushMessage>();
	private AdapterDelegate delegate = null;

	LayoutInflater inflater = null;

	@Override
	public void add(UserPushMessage object) {
		itemList.add(object);
		super.add(object);
	}

	public PushMessageListAdapter(Context context, AdapterDelegate delegate, int textViewResourceId) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.delegate = delegate;
	}

	public int getCount() {
		return this.itemList.size();
	}

	public UserPushMessage getItem(int index) {
		return this.itemList.get(index);
	}

	public void setItemList( List<UserPushMessage> itemList )
	{
		this.itemList = itemList;
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			UserPushMessage item = getItem(position);

			if (row == null) {
				row = inflater.inflate(R.layout.list_push_message_item, parent, false);
			}

			TextView txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			txtTitle.setText( item.getMessage() );
			TextView txtCreatedDate = (TextView) row.findViewById(R.id.txtCreatedDate);
			txtCreatedDate.setText( Util.getFormattedDateString(item.getCreatedDate(), "yyyy-MM-dd") );
			
			TextView txtNew = (TextView) row.findViewById(R.id.txtNew);
			if ( item.isRead() == false )
				txtNew.setVisibility(ViewGroup.VISIBLE);
			else
				txtNew.setVisibility(ViewGroup.GONE);
			
			row.setTag( item );
		}
		catch( Exception ex )
		{
			delegate.doAction("logException", ex );
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