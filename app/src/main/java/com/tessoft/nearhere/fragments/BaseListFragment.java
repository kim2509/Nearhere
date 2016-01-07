package com.tessoft.nearhere.fragments;

import org.codehaus.jackson.map.ObjectMapper;

import com.tessoft.nearhere.R.id;
import com.tessoft.nearhere.R.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class BaseListFragment extends BaseFragment{

	protected View rootView = null;
	protected ListView listMain = null;
	protected View header = null;
	protected View footer = null;
	protected ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if ( rootView == null )
		{
			rootView = inflater.inflate(layout.fragment_base_list, container, false);
		}
		
		return rootView;
	}
	
	protected void setTitle( String title ) {
		TextView txtTitle = (TextView) rootView.findViewById(id.txtTitle);
		txtTitle.setVisibility(ViewGroup.VISIBLE);
		rootView.findViewById(id.imgTitle).setVisibility(ViewGroup.GONE);
		txtTitle.setText( title );
	}
}
