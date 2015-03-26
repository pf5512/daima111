package com.iLoong.launcher.SetupMenu.Actions;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooeecomet.launcher.R;


public class MyPreference extends Preference
{
	
	private Drawable itemDrawable;
	private ImageView img_new , img_pro;
	
	public MyPreference(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		setLayoutResource( R.layout.app_item );
		TypedArray a = context.obtainStyledAttributes( attrs , R.styleable.Preference );
		int icon = a.getResourceId( R.styleable.Preference_image , 0 );
		itemDrawable = context.getResources().getDrawable( icon );
		a.recycle();
	}
	
	@Override
	protected void onBindView(
			View view )
	{
		super.onBindView( view );
		ImageView icon = (ImageView)view.findViewById( R.id.img_preference );
		icon.setImageDrawable( itemDrawable );
		TextView title = (TextView)view.findViewById( R.id.txt_preference );
		title.setText( getTitle() );
	}
}
