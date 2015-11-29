package com.tessoft.common;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import com.tessoft.domain.User;
import com.tessoft.nearhere.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserArrayAdapter extends ArrayAdapter<User>{

	private AdapterDelegate delegate = null;
	LayoutInflater inflater = null;
	DisplayImageOptions options = null;
	
	public UserArrayAdapter(Context context, AdapterDelegate delegate, int textViewResourceId) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.delegate = delegate;
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

	public static class ViewHolder
	{
		public User user = null;
		public ImageView imgProfile = null;
		public TextView txtUserName = null;
		public ImageView imgSex = null;
		public TextView txtCurrLocation = null;
		public TextView txtAge = null;
		public TextView txtProfilePoint = null;
		public TextView txtJobTitle = null;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			User user = getItem(position);
			
			if ( user.isMoreFlag() )
			{
				row = inflater.inflate(R.layout.list_user_more_item, parent, false);
				row.setTag( user );
				this.delegate.doAction("loadMore", user);
				return row;
			}
			else if ( row != null )
			{
				if ( row.getTag() == null )
					row = null;
				else if ( row.getTag() instanceof User )
				{
					User tempItem = (User) row.getTag();
					if ( tempItem == null || tempItem.isMoreFlag() )
						row = null;
				}
			}
			
			ImageView imageView = null;
			ImageView imgSex = null;
			ViewHolder viewHolder = null;
			TextView txtUserName = null;
			TextView txtCurrLocation = null;
			TextView txtAge = null;
			TextView txtProfilePoint = null;
			TextView txtJobTitle = null;
			
			if (row == null || row.getTag() == null ) {
				row = inflater.inflate(R.layout.list_user_item, parent, false);
				
				viewHolder = new ViewHolder();
				
				viewHolder.imgProfile = (ImageView) row.findViewById(R.id.imgProfile);
				viewHolder.txtUserName = (TextView) row.findViewById(R.id.txtUserName);
				viewHolder.imgSex = (ImageView) row.findViewById(R.id.imgSex);
				viewHolder.txtCurrLocation = (TextView) row.findViewById(R.id.txtCurrLocation);
				viewHolder.txtAge = (TextView) row.findViewById(R.id.txtAge);
				viewHolder.txtProfilePoint = (TextView) row.findViewById(R.id.txtProfilePoint);
				viewHolder.txtJobTitle = (TextView) row.findViewById(R.id.txtJobTitle);
				row.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) row.getTag();
			}
			
			imageView = viewHolder.imgProfile;
			txtUserName = viewHolder.txtUserName;
			txtCurrLocation = viewHolder.txtCurrLocation;
			txtAge = viewHolder.txtAge;
			txtProfilePoint = viewHolder.txtProfilePoint;
			txtJobTitle = viewHolder.txtJobTitle;
			imgSex = viewHolder.imgSex;

			viewHolder.user = user;
			
			if ( !Util.isEmptyString( user.getProfileImageURL() ))
			{
				ImageLoader.getInstance().displayImage( 
						Constants.getThumbnailImageURL() + user.getProfileImageURL() , imageView, options );	
			}
			else
			{
				ImageLoader.getInstance().cancelDisplayTask(imageView);
				imageView.setImageResource(R.drawable.no_image);
			}
			
			txtUserName.setText( user.getUserName() );
			
			if ( "M".equals( user.getSex() ) )
				imgSex.setImageResource(R.drawable.ic_male);
			else
				imgSex.setImageResource(R.drawable.ic_female);
			
			if ( Util.isEmptyString( user.getAddress() ) )
				row.findViewById(R.id.layoutCurrLocation).setVisibility(ViewGroup.GONE);
			else
				row.findViewById(R.id.layoutCurrLocation).setVisibility(ViewGroup.VISIBLE);
				
			txtCurrLocation.setText( user.getAddress() );
			
			if ( Util.isEmptyString( user.getAge() ))
				txtAge.setText( "미입력" );
			else
				txtAge.setText( user.getAge() + "세" );
			
			txtProfilePoint.setText( user.getProfilePoint() + "%" );
			
			if ( Util.isEmptyString( user.getJobTitle() ) )
				txtJobTitle.setText("미입력");
			else
				txtJobTitle.setText(user.getJobTitle());
		}
		catch( Exception ex )
		{
			delegate.doAction("logException", ex);
		}

		return row;
	}
}