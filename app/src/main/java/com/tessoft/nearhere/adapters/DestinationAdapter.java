package com.tessoft.nearhere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tessoft.nearhere.R;

import net.daum.android.map.openapi.search.Item;

import java.util.ArrayList;

/**
 * Created by Daeyong on 2016-01-17.
 */
public class DestinationAdapter extends ArrayAdapter<Item> {

    // View lookup cache
    public static class ViewHolder {
        TextView title;
        TextView address;
        public Item item;
    }

    public DestinationAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Item item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_destination_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.address = (TextView) convertView.findViewById(R.id.txtAddress);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(item.title);
        viewHolder.address.setText(item.address);
        viewHolder.item = item;
        // Return the completed view to render on screen
        return convertView;
    }
}
