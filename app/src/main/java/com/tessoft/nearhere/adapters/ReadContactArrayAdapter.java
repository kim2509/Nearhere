package com.tessoft.nearhere.adapters;

import com.tessoft.common.AdapterDelegate;
import com.tessoft.domain.Contact;

import com.tessoft.nearhere.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReadContactArrayAdapter extends ArrayAdapter<Contact> {

	private AdapterDelegate delegate = null;

	LayoutInflater inflater = null;

	public ReadContactArrayAdapter(Context context, int textViewResourceId, AdapterDelegate delegate) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.delegate = delegate;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			Contact item = getItem(position);

			if (row == null) {
				row = inflater.inflate(R.layout.list_contact_item, parent, false);
			}
			
			TextView txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			txtTitle.setText( item.getName() );
			
			TextView txtNumber = (TextView) row.findViewById(R.id.txtNumber);
			txtNumber.setText( item.getNumber() );
			
			row.setTag( item );
		}
		catch( Exception ex )
		{
			Log.e("error", ex.getMessage());
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