package com.iLoong.scan;


import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class HistoryListAdapter extends BaseAdapter
{
	
	private Context context;
	private List<HistoryBean> historyList;
	
	public HistoryListAdapter(
			Context context ,
			List<HistoryBean> historyList )
	{
		this.context = context;
		this.historyList = historyList;
	}
	
	@Override
	public int getCount()
	{
		return historyList.size();
	}
	
	@Override
	public Object getItem(
			int position )
	{
		return historyList.get( position );
	}
	
	@Override
	public long getItemId(
			int position )
	{
		return position;
	}
	
	@Override
	public View getView(
			final int position ,
			View convertView ,
			ViewGroup parent )
	{
		ViewHolder holder = null;
		if( convertView == null )
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from( context ).inflate( R.layout.item_history_list , null );
			holder.ll = (LinearLayout)convertView.findViewById( R.id.ll );
			holder.iv_icon = (ImageView)convertView.findViewById( R.id.iv_icon );
			holder.tv_title = (TextView)convertView.findViewById( R.id.tv_title );
			holder.tv_content = (TextView)convertView.findViewById( R.id.tv_content );
			holder.btn_share = (Button)convertView.findViewById( R.id.btn_share );
			convertView.setTag( holder );
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		if( historyList.get( getCount() - position - 1 ).getType() == 1 )
		{
			holder.iv_icon.setImageResource( R.drawable.browser_icon );
			//holder.tv_title.setText( context.getString( R.string.string_interlinkage ) );
		}
		else
		{
			holder.iv_icon.setImageResource( R.drawable.text_icon );
			//holder.tv_title.setText( context.getString( R.string.string_text ) );
		}
		holder.tv_title.setText( historyList.get( getCount() - position - 1 ).getCurrtime() );
		holder.tv_content.setText( historyList.get( getCount() - position - 1 ).getText() );
		holder.ll.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( historyList.get( getCount() - position - 1 ).getType() == 1 )
				{
					Uri uri = Uri.parse( historyList.get( getCount() - position - 1 ).getText() );
					Intent intent = new Intent( Intent.ACTION_VIEW , uri );
					context.startActivity( intent );
				}
				else
				{
					AlertDialog.Builder builder = new Builder( context );
					builder.setMessage( historyList.get( getCount() - position - 1 ).getText() );
					builder.setTitle( context.getResources().getString( R.string.string_text ) );
					builder.setNegativeButton( android.R.string.ok , null );
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		} );
		holder.btn_share.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				Log.v( "QrCode" , "share click" );
				Intent shareIntent = new Intent( Intent.ACTION_SEND );
				shareIntent.setType( "text/plain" );
				shareIntent.putExtra( Intent.EXTRA_SUBJECT , context.getString( R.string.share_title ) );
				shareIntent.putExtra( Intent.EXTRA_TEXT , historyList.get( getCount() - position - 1 ).getText() );
				shareIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				context.startActivity( shareIntent );
			}
		} );
		return convertView;
	}
	
	class ViewHolder
	{
		
		LinearLayout ll;
		ImageView iv_icon;
		TextView tv_title;
		Button btn_share;
		TextView tv_content;
	}
}
