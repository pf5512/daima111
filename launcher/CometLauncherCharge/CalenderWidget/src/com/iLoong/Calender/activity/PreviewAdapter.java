package com.iLoong.Calender.activity;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.cooeeui.cometcalendar.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PreviewAdapter extends PagerAdapter
{
	
	private Context mContext;
	private String PREVIEW_DIR = "theme/preview/";
	private Bitmap[] bmpArray;
	private View[] layoutArray;
	private final String TAG = "PreviewAdapter";
	public PreviewAdapter(
			Context context )
	{
		mContext = context;
		List<String> imgList = loadImageName();
		bmpArray = new Bitmap[imgList.size()];
		layoutArray = new LinearLayout[imgList.size()];
		for( int i = 0 ; i < imgList.size() ; i++ )
		{
			try
			{
				bmpArray[i] = BitmapFactory.decodeStream( mContext.getAssets().open( PREVIEW_DIR + imgList.get( i ) ) );
				layoutArray[i] = View.inflate( mContext , R.layout.viewpager_item_preview , null );
				ImageView imgView = (ImageView)layoutArray[i].findViewById( R.id.imageView );
				imgView.setImageBitmap( bmpArray[i] );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public List<String> loadImageName()
	{
		Log.d( TAG , "START_TAG=loadImageName" );
		List<String> nameList = new ArrayList<String>();
		List<String> tagList = new ArrayList<String>();
		try
		{
			InputStream input = mContext.getAssets().open( PREVIEW_DIR + "advertisement.xml" );
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware( true );
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput( new UnicodeReader( input , "UTF8" ) );
			int eventType = xpp.getEventType();
			while( eventType != XmlPullParser.END_DOCUMENT )
			{
				if( eventType == XmlPullParser.START_DOCUMENT )
				{
				}
				else if( eventType == XmlPullParser.START_TAG )
				{
					Log.d( TAG , "START_TAG=" + xpp.getName() );
					tagList.add( xpp.getName() );
					if( xpp.getName().equals( "item" ) && tagList.size() == 2 && tagList.get( 0 ).equals( "themepreview" ) )
					{
						String imageValue = xpp.getAttributeValue( null , "image" );
						if( imageValue != null )
						{
							nameList.add( imageValue );
							Log.d( TAG , "imageValue=" + imageValue );
						}
					}
				}
				else if( eventType == XmlPullParser.END_TAG )
				{
					tagList.remove( tagList.size() - 1 );
				}
				else if( eventType == XmlPullParser.TEXT )
				{
				}
				eventType = xpp.next();
			}
		}
		catch( XmlPullParserException e )
		{
			e.printStackTrace();
			Log.e( TAG , e.toString() );
		}
		catch( IOException e )
		{
			e.printStackTrace();
			Log.e( TAG , e.toString() );
		}
		return nameList;
	}
	
	@Override
	public int getCount()
	{
		return Integer.MAX_VALUE;
	}
	
	/** 
	 * 从指定的position创建page 
	 * 
	 * @param container ViewPager容器 
	 * @param position The page position to be instantiated. 
	 * @return 返回指定position的page，这里不需要是一个view，也可以是其他的视图容器. 
	 */
	@Override
	public Object instantiateItem(
			View collection ,
			int position )
	{
//		( (ViewPager)collection ).addView( layoutArray[position] );
//		return layoutArray[position];
		//		final int weizhi=position;
		//		View view=layoutArray[position % 4];
		//		view.setOnClickListener( new OnClickListener() {
		//			@Override
		//			public void onClick(
		//					View v )
		//			{
		//				Intent  intent=new Intent( mContext , PreviewFullActivity.class );
		//				intent.putExtra( "position" , weizhi );
		//				mContext.startActivity( intent );
		//			}
		//		} );
		   try {
		    ((ViewPager) collection).addView(layoutArray[position%4], 0);
		   } catch (Exception e) {
		         }
		   return layoutArray[position%4];
	}
	
	@Override
	public void destroyItem(
			View collection ,
			int position ,
			Object view )
	{
		
//		( (ViewPager)collection ).removeView( layoutArray[position] );
	}
	
	@Override
	public boolean isViewFromObject(
			View view ,
			Object object )
	{
		return view == ( object );
	}
	
	@Override
	public void finishUpdate(
			View arg0 )
	{
	}
	
	@Override
	public void restoreState(
			Parcelable arg0 ,
			ClassLoader arg1 )
	{
	}
	
	@Override
	public Parcelable saveState()
	{
		return null;
	}
	
	@Override
	public void startUpdate(
			View arg0 )
	{
	}
}
