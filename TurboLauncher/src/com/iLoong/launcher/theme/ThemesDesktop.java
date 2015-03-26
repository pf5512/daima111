package com.iLoong.launcher.theme;


import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.SetupMenu.PageGridView;
import com.iLoong.launcher.SetupMenu.PagedView;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.desktop.iLoongApplication;


class ThemeItem
{
	
	public ImageView mSelectSwitch;
	public TextView mThemeName;
	public ImageView mDefaultBmp;
	public int id;
}

public class ThemesDesktop extends FrameLayout implements View.OnClickListener , PagedView.ViewSwitchListener
{
	
	private static final int PAGEROWS = 3;
	private static final int PAGECOLUMNS = 2;
	private static final int PAGECOUNT = PAGEROWS * PAGECOLUMNS;
	private static final int COMMON_PADDING = 6;
	private static final int TITLETXT_FONTSIZE = 18;
	private static final int BUTTON_FONTSIZE = 12;
	private static final int BUTTON_EVENT_CURRENTTHEME = 1;
	private static final int BUTTON_EVENT_ONLINETHEME = 2;
	private TextView mMyTitle;
	private LinearLayout mdesktop;
	private ThemePagePointer mIndicatorLayout;
	private ThemesGridLayout mThemesGridLayout;
	private Button mButtonOnlineTheme;
	private Context mContext;
	private Vector<ThemeDescription> mData;
	private LayoutInflater mLayoutInflater;
	private ArrayList<Bitmap> mbitmaps = new ArrayList<Bitmap>();
	
	public ThemesDesktop(
			Context context )
	{
		super( context );
		mContext = context;
		mLayoutInflater = LayoutInflater.from( mContext );
		mData = ThemeManager.getInstance().getThemeDescriptions();
		LoadLayout();
	}
	
	public void Release()
	{
		for( int i = 0 ; i < mbitmaps.size() ; i++ )
		{
			Bitmap bmp = mbitmaps.get( i );
			if( bmp != null )
				bmp.recycle();
		}
		mbitmaps.clear();
		if( mIndicatorLayout != null )
			mIndicatorLayout.Release();
	}
	
	private void LoadLayout()
	{
		mdesktop = new LinearLayout( mContext );
		LayoutParams desktoplp = new LayoutParams( LayoutParams.FILL_PARENT , LayoutParams.FILL_PARENT );
		mdesktop.setOrientation( LinearLayout.VERTICAL );
		mdesktop.setBackgroundColor( Color.WHITE );
		addView( mdesktop , desktoplp );
		LinearLayout Layout = new LinearLayout( mContext );
		Layout.setOrientation( LinearLayout.HORIZONTAL );
		Layout.setBackgroundColor( Color.rgb( 52 , 52 , 52 ) );
		mdesktop.addView( Layout , LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT );
		mMyTitle = new TextView( mContext );
		mMyTitle.setText( RR.string.localtheme );
		mMyTitle.setTextColor( Color.WHITE );
		mMyTitle.setTextSize( TITLETXT_FONTSIZE );
		mMyTitle.setPadding( (int)( COMMON_PADDING * SetupMenu.mScale ) , (int)( COMMON_PADDING * SetupMenu.mScale ) , 0 , (int)( COMMON_PADDING * SetupMenu.mScale ) );
		Layout.addView( mMyTitle );
		mIndicatorLayout = new ThemePagePointer( mContext , false );
		mIndicatorLayout.setOrientation( LinearLayout.HORIZONTAL );
		LayoutParams lp = new LayoutParams( LayoutParams.WRAP_CONTENT , mIndicatorLayout.getheight() );
		mdesktop.addView( mIndicatorLayout , lp );
		mThemesGridLayout = new ThemesGridLayout( mContext );
		mdesktop.addView( mThemesGridLayout , LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT );
		int bh = (int)( 84 * SetupMenu.mScale );
		int bw = (int)( 336 * SetupMenu.mScale );
		Layout = new LinearLayout( mContext );
		Layout.setOrientation( LinearLayout.HORIZONTAL );
		Layout.setBackgroundColor( Color.rgb( 0xb5 , 0xb5 , 0xb5 ) );
		LayoutParams flp = new LayoutParams( LayoutParams.FILL_PARENT , bh );
		Layout.setGravity( Gravity.CENTER );
		flp.gravity = Gravity.BOTTOM;
		addView( Layout , flp );
		if( !DefaultLayout.hide_online_theme_button )
		{
			mButtonOnlineTheme = new Button( mContext );
			mButtonOnlineTheme.setText( RR.string.onlinetheme );
			mButtonOnlineTheme.setTextColor( Color.BLACK );
			mButtonOnlineTheme.setTextSize( BUTTON_FONTSIZE );
			mButtonOnlineTheme.setTag( Integer.valueOf( BUTTON_EVENT_ONLINETHEME ) );
			mButtonOnlineTheme.setOnClickListener( this );
			LinearLayout.LayoutParams lpb = new LinearLayout.LayoutParams( bw , LayoutParams.WRAP_CONTENT );
			lpb.gravity = Gravity.CENTER;
			//		lpb.height = bh ;
			//		lpb.topMargin = (int) (3 * SetupMenu.mScale);
			Layout.addView( mButtonOnlineTheme , lpb );
		}
	}
	
	public void LoadData()
	{
		View CurrentView;
		ThemeItem item;
		int count = mData.size();
		int page = (int)( (float)count / PAGECOUNT + 0.9f );
		mIndicatorLayout.Init( page );
		LinearLayout.LayoutParams nvlp = (LinearLayout.LayoutParams)mIndicatorLayout.getLayoutParams();
		nvlp.topMargin = (int)( COMMON_PADDING * SetupMenu.mScale );
		nvlp.gravity = Gravity.CENTER;
		mIndicatorLayout.setLayoutParams( nvlp );
		mThemesGridLayout.removeAllViews();
		final int mHeight = (int)( 512 * SetupMenu.mScale );
		final int cellwidth = (int)( (float)UtilsBase.getScreenWidth() / PAGEROWS );
		final int cellheight = (int)( (float)mHeight / PAGECOLUMNS );
		final int vwidth = (int)( 170 * SetupMenu.mScale );
		final int vheight = (int)( 256 * SetupMenu.mScale );
		int j = 0;
		for( int index = PAGECOUNT ; index < count + PAGECOUNT ; index += PAGECOUNT )
		{
			int cellX = 0;
			int cellY = -1;
			PageGridView ThemesGridView = new PageGridView( mContext );
			LinearLayout.LayoutParams tgvlp = new LinearLayout.LayoutParams( UtilsBase.getScreenWidth() , mHeight );
			ThemesGridView.setCellDimensions( cellwidth , cellheight , 0 , 0 );
			int pagecount = index <= count ? PAGECOUNT : count + PAGECOUNT - index;
			for( int i = 0 ; i < pagecount ; i++ , j++ )
			{
				CurrentView = mLayoutInflater.inflate( RR.layout.theme_item , null );
				item = new ThemeItem();
				item.mThemeName = (TextView)CurrentView.findViewById( RR.id.themename );
				item.mSelectSwitch = (ImageView)CurrentView.findViewById( RR.id.selectswitch );
				item.mDefaultBmp = (ImageView)CurrentView.findViewById( RR.id.defaultbmp );
				ThemeDescription themedesc = mData.elementAt( j );
				item.id = j;
				item.mThemeName.setText( themedesc.title );
				item.mThemeName.setTextColor( 0xFF5a6063 );
				Bitmap bmp = themedesc.getDefaultBitmap();
				if( bmp != null )
				{
					bmp = Tools.resizeBitmap( bmp , (int)( vwidth * 0.7 ) , (int)( vwidth * 0.7 / bmp.getWidth() * bmp.getHeight() ) );
					mbitmaps.add( bmp );
					item.mDefaultBmp.setImageBitmap( bmp );
				}
				if( !themedesc.mUse )
					item.mSelectSwitch.setVisibility( View.INVISIBLE );
				else
					item.mSelectSwitch.setVisibility( View.VISIBLE );
				CurrentView.setTag( item );
				CurrentView.setOnClickListener( this );
				cellX = i % PAGEROWS;
				cellY = ( cellX == 0 ) ? ++cellY : cellY;
				PageGridView.LayoutParams lp = new PageGridView.LayoutParams( cellX , cellY );
				ThemesGridView.addView( CurrentView , lp );
			}
			mThemesGridLayout.addView( ThemesGridView , tgvlp );
		}
		mThemesGridLayout.setLoop( false );
		mThemesGridLayout.setOverScroll( false );
		mThemesGridLayout.setScrollingSpeed( 2.0f );
		mThemesGridLayout.setSwitchListener( this );
		mThemesGridLayout.OnClicksnapToScreen( 0 );
	}
	
	@Override
	public void onSwitched(
			View view ,
			int position )
	{
		mIndicatorLayout.SelectPage( position );
	}
	
	@Override
	public void onClick(
			View v )
	{
		if( v == null || v.getTag() == null )
			return;
		Object obj = v.getTag();
		if( v instanceof Button )
		{
			Uri uri = Uri.parse( iLoongApplication.getInstance().getResources().getString( RR.string.setting_onlinetheme ) );
			mContext.startActivity( new Intent( Intent.ACTION_VIEW , uri ) );
		}
		else if( obj instanceof ThemeItem )
		{
			Intent localIntent = new Intent();
			localIntent.setClass( mContext , ThemeDetailedActivity.class );
			ThemeItem item = (ThemeItem)obj;
			localIntent.putExtra( ThemeDetailedActivity.class.getSimpleName() , item.id );
			mContext.startActivity( localIntent );
		}
	}
}
