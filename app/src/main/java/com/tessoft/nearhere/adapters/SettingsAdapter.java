package com.tessoft.nearhere.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.tessoft.common.AdapterDelegate;
import com.tessoft.domain.SettingListItem;
import com.tessoft.nearhere.R;

public class SettingsAdapter extends ArrayAdapter<SettingListItem> implements OnCheckedChangeListener{

	private AdapterDelegate delegate = null;

	LayoutInflater inflater = null;

	@Override
	public void add(SettingListItem object) {
		super.add(object);
	}

	public SettingsAdapter(Context context, AdapterDelegate delegate, int textViewResourceId) {
		super(context, textViewResourceId);
		inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.delegate = delegate;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		try
		{
			SettingListItem item = getItem(position);

			CheckBox setting = null;

			if (row == null) {
				row = inflater.inflate(R.layout.list_setting_item, parent, false);
				setting = (CheckBox) row.findViewById(R.id.checkSetting);
				setting.setOnCheckedChangeListener(this);
			}

			if ( setting == null )
			setting = (CheckBox) row.findViewById(R.id.checkSetting);

			setting.setText( item.getSettingName() );

			if ( "Y".equals( item.getSettingValue() ) )
				setting.setChecked(true);
			else
				setting.setChecked(false);
			
			setting.setTag( item );
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		try
		{
			if ( buttonView.getTag() != null )
			{
				SettingListItem setting = (SettingListItem) buttonView.getTag();
				setting.setSettingValue( isChecked ? "Y":"N" );
				delegate.doAction( "updateSetting", setting );	
			}			
		}
		catch( Exception ex )
		{
			Log.e("error", ex.getMessage());
		}
	}

}