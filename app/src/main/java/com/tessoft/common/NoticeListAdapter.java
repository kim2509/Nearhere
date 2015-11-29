package com.tessoft.common;

import java.util.ArrayList;
import java.util.List;

import com.tessoft.domain.Notice;
import com.tessoft.nearhere.R;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class NoticeListAdapter extends BaseExpandableListAdapter {

	private AdapterDelegate delegate = null;

	LayoutInflater inflater = null;
	
	private List<Notice> groupList = null;
	
	public NoticeListAdapter( Context context )
	{
		super();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.groupList = new ArrayList<Notice>();
	}
	
	public AdapterDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(AdapterDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View view = convertView;
		
		try
		{
			Notice item = (Notice) getGroup(groupPosition);
			
			if (view == null) {
				view = inflater.inflate(R.layout.list_notice_child_item, parent, false);
			}
			
			WebView webView = (WebView) view.findViewById(R.id.webView);
			webView.clearView();
			webView.setWebViewClient( webViewClient );
			webView.loadUrl( Constants.getServerURL() + "/taxi/getNotice.do?noticeID=" + item.getNoticeID() );
		}
		catch( Exception ex )
		{
			
		}
		
		return view;
	}
	
	WebViewClient webViewClient = new WebViewClient() {
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		}
		
		public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
			handler.proceed();
		};
	};

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return groupList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setGroupList( List<Notice> groupList )
	{
		this.groupList = groupList;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;

		try
		{
			Notice item = (Notice) getGroup(groupPosition);

			if (row == null) {
				row = inflater.inflate(R.layout.list_notice_item, parent, false);
			}

			String title = "<font>" + item.getTitle() + "</font>&nbsp;&nbsp;<font color='#ff0000'>";
			title += Util.getFormattedDateString(item.getCreatedDate(),"MM-dd") + "</font>";
			TextView txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			txtTitle.setText( Html.fromHtml(title) );
			
			if ( item.isRead() )
				row.findViewById(R.id.txtNew).setVisibility(ViewGroup.GONE);
			else
				row.findViewById(R.id.txtNew).setVisibility(ViewGroup.VISIBLE);
			
			row.setTag( item );
		}
		catch( Exception ex )
		{
			Log.e("error", ex.getMessage());
		}

		return row;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}