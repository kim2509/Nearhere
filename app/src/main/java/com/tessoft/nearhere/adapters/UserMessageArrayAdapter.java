package com.tessoft.nearhere.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tessoft.common.AdapterDelegate;
import com.tessoft.common.Constants;
import com.tessoft.common.Util;
import com.tessoft.domain.User;
import com.tessoft.domain.UserMessage;
import com.tessoft.nearhere.R;

import java.util.ArrayList;
import java.util.List;

public class UserMessageArrayAdapter extends ArrayAdapter<UserMessage> implements View.OnClickListener{

	private List<UserMessage> itemList = new ArrayList<UserMessage>();
	private AdapterDelegate delegate = null;
	private User me = null;

	LayoutInflater inflater = null;
	DisplayImageOptions options = null;

	@Override
	public void add(UserMessage object) {
		itemList.add(object);
		super.add(object);
	}

	public UserMessageArrayAdapter(Context context, AdapterDelegate delegate, User me, int textViewResourceId) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.delegate = delegate;
		this.me = me;

		options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true)
				.cacheInMemory(true)
				.showImageOnLoading(R.drawable.no_image)
				.showImageForEmptyUri(R.drawable.no_image)
				.showImageOnFail(R.drawable.no_image)
				.displayer(new RoundedBitmapDisplayer(20))
				.delayBeforeLoading(100)
				.build();
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

			if ( item.getFromUser().getUserID().equals( me.getUserID() ) )
				item.setMe( true );
			
			if (row == null) {
				if ( item.isMe() )
					row = inflater.inflate(R.layout.list_user_message_item_right, parent, false);
				else
					row = inflater.inflate(R.layout.list_user_message_item_left, parent, false);
			}
			else
			{
				UserMessage tmp = (UserMessage) row.getTag();
				if ( tmp.isMe() != item.isMe() )
				{
					if ( item.isMe() )
						row = inflater.inflate(R.layout.list_user_message_item_right, parent, false);
					else
						row = inflater.inflate(R.layout.list_user_message_item_left, parent, false);
				}
			}
			
			if ( item.isMe() == false )
			{
				ImageView imgProfile = (ImageView) row.findViewById(R.id.imgProfile);
				imgProfile.setImageResource(R.drawable.no_image);
				
				if ( item.getFromUser() != null && item.getFromUser().getProfileImageURL() != null && 
						!"".equals( item.getFromUser().getProfileImageURL() ) )
				{
					ImageLoader.getInstance().displayImage( Constants.getThumbnailImageURL() +
							item.getFromUser().getProfileImageURL() , imgProfile, options );
					
					TextView txtUserName = (TextView) row.findViewById(R.id.txtUserName);
					txtUserName.setText( item.getFromUser().getUserName() );
				}

				imgProfile.setOnClickListener( this );
				imgProfile.setTag( item.getFromUser().getUserID() );
			}
			
			TextView txtMessage = (TextView) row.findViewById(R.id.txtMessage);
			txtMessage.setText( item.getMessage() );
			TextView txtCreatedDate = (TextView) row.findViewById(R.id.txtCreatedDate);
			txtCreatedDate.setText( Util.getFormattedDateString(item.getCreatedDate(), "HH:mm") );
			
			row.setTag( item );
		}
		catch( Exception ex )
		{
			Log.e("error", ex.getMessage());
		}

		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public AdapterDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(AdapterDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onClick(View v) {
		delegate.doAction("openUserProfile", v.getTag().toString());
	}
}