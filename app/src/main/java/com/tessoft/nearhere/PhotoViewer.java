package com.tessoft.nearhere;

import com.tessoft.common.Constants;
import com.tessoft.nearhere.activities.BaseActivity;
import com.uk.co.senab.photoview.PhotoViewAttacher;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.view.View;
import android.graphics.Bitmap;

public class PhotoViewer extends BaseActivity {

	ImageView imgPhoto = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_photo_viewer);
			
			imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
			
			if ( getIntent().getExtras().containsKey("imageURL"))
			{
				String imgURL = getIntent().getExtras().getString("imageURL");

				ImageLoader imageLoader = ImageLoader.getInstance();
				
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				
				ImageSize targetSize = new ImageSize(size.x, size.y);
				imageLoader.loadImage( Constants.getImageURL() + imgURL, targetSize, new SimpleImageLoadingListener() {
				    @Override
				    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			
				    	imgPhoto.setImageBitmap(loadedImage);
				    	PhotoViewAttacher mAttacher = new PhotoViewAttacher(imgPhoto);
				        mAttacher.setScaleType(ScaleType.FIT_CENTER);
				        
				        findViewById(R.id.marker_progress).setVisibility(ViewGroup.GONE);
				    }
				});
			}
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.stay, R.anim.fade_out);
	}
}
