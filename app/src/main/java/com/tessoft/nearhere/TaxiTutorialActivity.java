package com.tessoft.nearhere;

import java.util.ArrayList;

import com.tessoft.nearhere.fragment.TaxiTutorialFragment0;
import com.tessoft.nearhere.fragment.TaxiTutorialFragment1;
import com.tessoft.nearhere.fragment.TaxiTutorialFragment2;
import com.tessoft.nearhere.fragment.TaxiTutorialFragment3;
import com.tessoft.nearhere.fragment.TaxiTutorialFragment4;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class TaxiTutorialActivity extends BaseActivity {

	static class MyAdapter extends FragmentPagerAdapter {
		
		private ArrayList<Fragment> fragments = null;
		
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0: fragments.size();
        }

        public void setItemList( ArrayList<Fragment> fragments )
        {
        	this.fragments = fragments;
        }
        
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
	
	ViewPager mPager = null;
	MyAdapter mAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_taxi_tutorial);
			
			mAdapter = new MyAdapter(getSupportFragmentManager());
			
			ArrayList<Fragment> fragments = new ArrayList<Fragment>();
			fragments.add( new TaxiTutorialFragment0() );
			fragments.add( new TaxiTutorialFragment1() );
			fragments.add( new TaxiTutorialFragment2() );
			fragments.add( new TaxiTutorialFragment4() );
			fragments.add( new TaxiTutorialFragment3() );
			
			mAdapter.setItemList( fragments );
			
			mPager = (ViewPager)findViewById(R.id.pager);
			mPager.setAdapter(mAdapter);
		}
		catch( Exception ex )
		{
			catchException(this, ex);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.taxi_tutorial, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void goMainActivity()
	{
		Intent intent = new Intent( this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		goMainActivity();
		super.finish();
	}
	
	public void closeTutorial( View v )
	{
		finish();
	}
}
