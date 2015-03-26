package com.cooeecomet.launcher.key;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class PreviewAdapter extends PagerAdapter
{
	
	private Context mContext;
	private View[] layoutArray;
	
	//private final String TAG = "PreviewAdapter";
	public PreviewAdapter(
			Context context )
	{
		mContext = context;
		int cometIds[] = {
				R.drawable.scroll_1 ,
				R.drawable.scroll_2 ,
				R.drawable.scroll_3 ,
				R.drawable.scroll_4 ,
				R.drawable.scroll_5 ,
				R.drawable.scroll_6 ,
				R.drawable.scroll_7 ,
				R.drawable.scroll_8 };
		layoutArray = new LinearLayout[cometIds.length];
		for( int i = 0 ; i < layoutArray.length ; i++ )
		{
			layoutArray[i] = View.inflate( mContext , R.layout.viewpager_item_preview , null );
			ImageView imgView = (ImageView)layoutArray[i].findViewById( R.id.imageView );
			Drawable draw = context.getResources().getDrawable( cometIds[i] );
			imgView.setImageDrawable( draw );
		}
	}
	
	public int getOriginalCount()
	{
		return layoutArray.length;
	}
	
	@Override
	public int getCount()
	{
		return layoutArray.length * 200;
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
		int realPos = position % layoutArray.length;
		( (ViewPager)collection ).addView( layoutArray[realPos] );
		return layoutArray[realPos];
	}
	
	@Override
	public void destroyItem(
			View collection ,
			int position ,
			Object view )
	{
		int realPos = position % layoutArray.length;
		( (ViewPager)collection ).removeView( layoutArray[realPos] );
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
