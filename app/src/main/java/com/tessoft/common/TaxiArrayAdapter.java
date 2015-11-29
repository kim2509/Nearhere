package com.tessoft.common;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import com.tessoft.domain.Post;
import com.tessoft.nearhere.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaxiArrayAdapter extends ArrayAdapter<Post> implements OnClickListener, OnTouchListener{

	private AdapterDelegate delegate = null;

	LayoutInflater inflater = null;
	DisplayImageOptions options = null;
	
	public TaxiArrayAdapter(Context context, AdapterDelegate delegate, int textViewResourceId) {
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

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			Post item = getItem(position);
			ImageView imgStatus = null;
			ImageView imageView = null;
			
			if ( item.isMoreFlag() )
			{
				row = inflater.inflate(R.layout.list_user_more_item, parent, false);
				row.setTag( item );
				this.delegate.doAction("loadMore", item);
				return row;
			}
			else if ( row != null )
			{
				Post tempItem = (Post) row.getTag();
				if ( tempItem == null || tempItem.isMoreFlag() )
					row = null;
			}
			
			if (row == null) {
				row = inflater.inflate(R.layout.list_taxi_post_item, parent, false);
				imgStatus = (ImageView) row.findViewById(R.id.imgStatus);
				imgStatus.setOnTouchListener( this );

				imageView = (ImageView) row.findViewById(R.id.imgProfile);
				FrameLayout flImgProfile = (FrameLayout) row.findViewById(R.id.flImgProfile);
				flImgProfile.setOnClickListener( this );
			}
			else
			{
				imageView = (ImageView) row.findViewById(R.id.imgProfile);
				imgStatus = (ImageView) row.findViewById(R.id.imgStatus);
			}

			ImageView imgSex = (ImageView) row.findViewById(R.id.imgSex);
			imgSex.setVisibility(ViewGroup.VISIBLE);
			
			if("M".equals( item.getUser().getSex() ) )
				imgSex.setImageResource(R.drawable.ic_male);
			else if("F".equals( item.getUser().getSex() ) )
				imgSex.setImageResource(R.drawable.ic_female);
			else
				imgSex.setVisibility(ViewGroup.GONE);
			
			TextView txtUserName = (TextView) row.findViewById(R.id.txtUserName);
			if ( Util.isEmptyString( item.getUser().getUserName() ) )
			{
				txtUserName.setText( item.getUser().getUserID() );
			}
			else
				txtUserName.setText( item.getUser().getUserName() );
			
			if ( !Util.isEmptyString( item.getUser().getKakaoID() ) )
				row.findViewById(R.id.imgKakaoIcon).setVisibility(ViewGroup.VISIBLE);
			else
				row.findViewById(R.id.imgKakaoIcon).setVisibility(ViewGroup.GONE);
			
			TextView txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			txtTitle.setText( item.getMessage() );

			TextView txtDepartureDateTime = (TextView) row.findViewById(R.id.txtDepartureDateTime);
			
			if ( item.getMessage().indexOf("매일") < 0 )
				txtDepartureDateTime.setText( Util.getDepartureDateTime( item.getDepartureDateTime() ) );
			else
				txtDepartureDateTime.setText("매일");
			
			if ( item.getUser() != null && !Util.isEmptyString( item.getUser().getProfileImageURL() ) )
			{
				ImageLoader.getInstance().displayImage( Constants.getThumbnailImageURL() + 
						item.getUser().getProfileImageURL() , imageView, options );
			}
			else
			{
				ImageLoader.getInstance().cancelDisplayTask(imageView);
				imageView.setImageResource(R.drawable.no_image);
			}

			setControlsVisibility(row, item, imgStatus);
			
			row.setTag( item );
		}
		catch( Exception ex )
		{
			delegate.doAction("logException", ex);
		}

		return row;
	}

	private void setControlsVisibility(View row, Post item, ImageView imgStatus) {
		imgStatus.setVisibility(ViewGroup.VISIBLE);
		
		if ( "진행중".equals( item.getStatus() ) )
			imgStatus.setImageResource(R.drawable.progressing);
		else
			imgStatus.setImageResource(R.drawable.finished);
		
		LinearLayout layoutComment = (LinearLayout) row.findViewById(R.id.layoutComment);
		if ( item.getReplyCount() > 0 )
		{
			layoutComment.setVisibility(ViewGroup.VISIBLE);
			TextView txtReplyCount = (TextView) row.findViewById(R.id.txtReplyCount);
			txtReplyCount.setText( String.valueOf( item.getReplyCount() ) );
		}
		else
			layoutComment.setVisibility(ViewGroup.GONE);
		
		if ( !Util.isEmptyString( item.getVehicle() ) )
		{
			TextView txtVehicle = (TextView) row.findViewById(R.id.txtVehicle);
			txtVehicle.setVisibility(ViewGroup.VISIBLE);
			txtVehicle.setText( item.getVehicle() );
		}
		else
			row.findViewById(R.id.txtVehicle).setVisibility(ViewGroup.GONE);
		
		if ( !Util.isEmptyString( item.getFareOption() ) )
		{
			TextView txtFareOption = (TextView) row.findViewById(R.id.txtFareOption);
			txtFareOption.setVisibility(ViewGroup.VISIBLE);
			txtFareOption.setText( item.getFareOption() );
		}
		else
			row.findViewById(R.id.txtFareOption).setVisibility(ViewGroup.GONE);
		
		if ( "Y".equals( item.getRepetitiveYN() ) )
		{
			TextView txtRepeat = (TextView) row.findViewById(R.id.txtRepeat);
			txtRepeat.setVisibility(ViewGroup.VISIBLE);
		}
		else
			row.findViewById(R.id.txtRepeat).setVisibility(ViewGroup.GONE);
		
		if ( !"상관없음".equals( item.getSexInfo() ) && !Util.isEmptyString( item.getSexInfo() ) )
		{
			TextView txtSex = (TextView) row.findViewById(R.id.txtSex);
			txtSex.setVisibility(ViewGroup.VISIBLE);
			txtSex.setText( item.getSexInfo() );
		}
		else
			row.findViewById(R.id.txtSex).setVisibility(ViewGroup.GONE);
		
		if ( !"상관없음".equals( item.getNumOfUsers() ) && !Util.isEmptyString( item.getNumOfUsers() ) )
		{
			TextView txtNOP = (TextView) row.findViewById(R.id.txtNOP);
			txtNOP.setVisibility(ViewGroup.VISIBLE);
			txtNOP.setText( item.getNumOfUsers() );
		}
		else
			row.findViewById(R.id.txtNOP).setVisibility(ViewGroup.GONE);
		
		TextView readCount = (TextView) row.findViewById(R.id.txtReadCount);
		if ( item.getReadCount() > 0 )
		{
			readCount.setVisibility(ViewGroup.VISIBLE);
			readCount.setText( "조회 : " + item.getReadCount() );
		}
		else
			readCount.setVisibility(ViewGroup.GONE);
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public AdapterDelegate getDelegate() {
		return delegate;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try
		{
			if ( v != null && v.getId() == R.id.flImgProfile )
			{
				Post post = (Post) ( (View) v.getParent().getParent() ).getTag();
				delegate.doAction("userProfile", post.getUser().getUserID() );	
			}
		}
		catch( Exception ex )
		{
			delegate.doAction("logException", null);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
}