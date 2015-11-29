package com.tessoft.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tessoft.domain.ListItemModel;
import com.tessoft.domain.Post;
import com.tessoft.domain.PostReply;
import com.tessoft.domain.User;
import com.tessoft.nearhere.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainArrayAdapter extends ArrayAdapter<ListItemModel> {

	private TextView txtUserID;
	private List<ListItemModel> itemList = new ArrayList<ListItemModel>();
	private AdapterDelegate delegate = null;

	LayoutInflater inflater = null;

	@Override
	public void add(ListItemModel object) {
		itemList.add(object);
		super.add(object);
	}

	public MainArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return this.itemList.size();
	}

	public ListItemModel getItem(int index) {
		return this.itemList.get(index);
	}

	public void setItemList( List<ListItemModel> itemList )
	{
		this.itemList = itemList;
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			ListItemModel item = getItem(position);

			if (row == null) {

				if ( item instanceof Post)
					row = inflater.inflate(R.layout.list_post_item, parent, false);
				else if ( item instanceof Map )
					row = inflater.inflate(R.layout.list_map_item, parent, false);
				else if ( item instanceof PostReply )
					row = inflater.inflate(R.layout.list_post_reply_item, parent, false);
			}
			else
			{
				if (  item instanceof Post && row.getTag() instanceof Post == false )
					row = inflater.inflate(R.layout.list_post_item, parent, false);
				else if ( item instanceof Map && row.getTag() instanceof Map == false )
					row = inflater.inflate(R.layout.list_map_item, parent, false);
				else if ( item instanceof PostReply && row.getTag() instanceof PostReply == false )
					row = inflater.inflate(R.layout.list_post_reply_item, parent, false);
			}

			if ( item instanceof Post )
			{
				Post post = (Post) item;
				txtUserID = (TextView) row.findViewById(R.id.txtDistance);
				txtUserID.setText( Util.getDistance( post.getFromDistance()) );
				TextView txtMsg = (TextView) row.findViewById(R.id.txtMessage);
				txtMsg.setText( post.getMessage() );
				ImageView imageView = (ImageView) row.findViewById(R.id.imgProfile);
				ImageLoader.getInstance().displayImage( Constants.getThumbnailImageURL() + post.getUser().getProfileImageURL() , imageView);

				TextView txtUserName = (TextView) row.findViewById(R.id.txtUserName);
				txtUserName.setText( post.getUser().getUserName() );
				txtUserName.setTag( post.getUser() );
				txtUserName.setOnClickListener( new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						User user = (User) v.getTag();
						if ( delegate != null )
							delegate.doAction("showUserInfo", user);
					}
				});
				
				TextView txtShowDetail = (TextView) row.findViewById(R.id.txtShowDetail);
				txtShowDetail.setTag(post);
				txtShowDetail.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Post post = (Post) v.getTag();
						if ( delegate != null )
							delegate.doAction("showDetail", post);
					}
				});
				
				TextView txtReward = (TextView) row.findViewById(R.id.txtReward);
				txtReward.setText( post.getReward() + "원");
			}
			else if ( item instanceof PostReply )
			{
				PostReply postReply = (PostReply) item;
				TextView txtPostReply = (TextView) row.findViewById(R.id.txtPostReply);
				txtPostReply.setText( postReply.getMessage() );
				TextView txtUserName = (TextView) row.findViewById(R.id.txtUserName);

				txtUserName.setOnClickListener( new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						User user = (User) v.getTag();
						if ( delegate != null )
							delegate.doAction("showUserInfo", user);
					}
				});

				txtUserName.setTag( postReply.getUser() );
				txtUserName.setText( postReply.getUser().getUserName() );
				ImageView imageProfile = (ImageView) row.findViewById(R.id.imgProfile);
				ImageLoader.getInstance().displayImage(Constants.getThumbnailImageURL() + postReply.getUser().getProfileImageURL(), imageProfile);

				TextView txtDistance = (TextView) row.findViewById(R.id.txtDistance);
				txtDistance.setText( Util.getDistance( postReply.getDistance() ) );
			}

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

}