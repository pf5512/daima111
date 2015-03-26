package com.cooee.searchbar;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class HistoryAdapter extends BaseAdapter
{
	
	private Context mContext;
	private List<HistoryBean> data;
	
	public HistoryAdapter(
			Context context ,
			List<HistoryBean> data )
	{
		this.mContext = context;
		this.data = data;
	}
	
	@Override
	public int getCount()
	{
		return data.size();
	}
	
	@Override
	public Object getItem(
			int position )
	{
		return data.get( position );
	}
	
	@Override
	public long getItemId(
			int position )
	{
		return position;
	}
	
	@Override
	public View getView(
			int position ,
			View convertView ,
			ViewGroup parent )
	{
		ViewHolder holder = null;
		if( convertView == null )
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from( mContext ).inflate( R.layout.list_item_history , null );
			holder.tv_keywords = (TextView)convertView.findViewById( R.id.tv_keywords );
			convertView.setTag( holder );
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tv_keywords.setText( data.get( position ).getKeywords() );
		return convertView;
	}
	
	class ViewHolder
	{
		
		TextView tv_keywords;
	}
}
