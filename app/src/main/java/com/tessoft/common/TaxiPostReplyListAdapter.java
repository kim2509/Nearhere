package com.tessoft.common;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tessoft.domain.PostReply;
import com.tessoft.domain.User;
import com.tessoft.nearhere.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TaxiPostReplyListAdapter extends ArrayAdapter<PostReply> implements OnClickListener{

	private List<PostReply> itemList = new ArrayList<PostReply>();
	private AdapterDelegate delegate = null;
	private User loginUser = null;

	LayoutInflater inflater = null;
	DisplayImageOptions options = null;

	@Override
	public void add(PostReply object) {
		itemList.add(object);
		super.add(object);
	}

	public TaxiPostReplyListAdapter(Context context, User user, int textViewResourceId) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.loginUser = user;
		
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

	public PostReply getItem(int index) {
		return this.itemList.get(index);
	}

	public void setItemList( List<PostReply> itemList )
	{
		this.itemList = itemList;
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			PostReply item = getItem(position);
			
			ImageView imageView = null;
			TextView txtUserName = null;
			TextView txtDeleteReply = null;
			
			if (row == null) {
				row = inflater.inflate(R.layout.list_taxi_post_reply_item, parent, false);
				
				imageView = (ImageView) row.findViewById(R.id.imgProfile);
				imageView.setOnClickListener( this );
				txtUserName = (TextView) row.findViewById(R.id.txtUserName);
				txtUserName.setOnClickListener( this );
				txtDeleteReply = (TextView) row.findViewById(R.id.txtDeleteReply);
				txtDeleteReply.setOnClickListener( this );
			}
			else
			{
				imageView = (ImageView) row.findViewById(R.id.imgProfile);
				txtUserName = (TextView) row.findViewById(R.id.txtUserName);
				txtDeleteReply = (TextView) row.findViewById(R.id.txtDeleteReply);
			}
			
			
			TextView txtMessage = (TextView) row.findViewById(R.id.txtMessage);
			txtMessage.setText( item.getMessage() );
			
			TextView txtDistance = (TextView) row.findViewById(R.id.txtDistance);
			if ( !Util.isEmptyString( item.getLatitude() ) && !Util.isEmptyString( item.getLongitude() ) )
			{
				txtDistance.setText( Util.getDistance( item.getDistance()) );
				txtDistance.setVisibility(ViewGroup.VISIBLE);
			}
			else
				txtDistance.setVisibility(ViewGroup.GONE);
			
			if ( item.getUser() != null )
			{
				if ( !Util.isEmptyString( item.getUser().getProfileImageURL() ) )
				{
					ImageLoader.getInstance().displayImage( Constants.getThumbnailImageURL() + 
							item.getUser().getProfileImageURL() , imageView, options );	
				}
				else
				{
					ImageLoader.getInstance().cancelDisplayTask(imageView);
					imageView.setImageResource(R.drawable.no_image);
				}
				
				if ( Util.isEmptyString( item.getUser().getUserName() ) )
					txtUserName.setText( item.getUser().getUserID() );
				else
					txtUserName.setText( item.getUser().getUserName() );
			}
			
			TextView txtCreatedDate = (TextView) row.findViewById(R.id.txtCreatedDate);
			txtCreatedDate.setText( Util.getFormattedDateString( item.getCreatedDate(), "HH:mm"));
			
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
		// TODO Auto-generated method stub
		if ( v.getId() == R.id.txtDeleteReply )
		{
			View row = (View) v.getParent().getParent();
			PostReply reply = (PostReply) row.getTag();
			delegate.doAction("deleteReply", reply );
		}
		else
		{
			View row = (View) v.getParent();
			PostReply reply = (PostReply) row.getTag();
			delegate.doAction("userProfile", reply.getUser().getUserID() );
		}
	}

}