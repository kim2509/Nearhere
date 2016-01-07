package com.tessoft.nearhere.fragments;

import com.tessoft.nearhere.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TaxiTutorialFragment4 extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v = null;
		
		try
		{
			v = inflater.inflate(layout.fragment_taxi_tutorial4, container, false);
		}
		catch( Exception ex )
		{
		}
		
		return v;
	}
}
