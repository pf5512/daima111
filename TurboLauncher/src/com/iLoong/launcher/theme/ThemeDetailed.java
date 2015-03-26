package com.iLoong.launcher.theme;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.PageGridView;
import com.iLoong.launcher.SetupMenu.PagedView;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.UtilsBase;


public class ThemeDetailed extends FrameLayout implements View.OnClickListener , PagedView.ViewSwitchListener
{
	
	private static final int COMMON_PADDING = 6;
	private static final int TITLETXT_FONTSIZE = 18;
	private static final int BUTTON_FONTSIZE = 12;
	private static final int BUTTON_EVENT_APPLYTHEME = 1;
	private static final int BUTTON_EVENT_DELETETHEME = 2;
	private Context mContext;
	private ThemeDescription mData;
	private LinearLayout mdesktop;
	private ThemePagePointer mIndicatorLayout;
	private TextView mMyTitle;
	private Button mButtonApplyTheme;
	private Button mButtonRemoveTheme;
	private ArrayList<Bitmap> mbitmaps = new ArrayList<Bitmap>();
	private ThemesGridLayout mThemesGridLayout;
	
	public ThemeDetailed(
			Context context )
	{
		super( context );
		mContext = context;
	}
	
	public void Release()
	{
		for( int i = 0 ; i < mbitmaps.size() ; i++ )
		{
			Bitmap bmp = mbitmaps.get( i );
			if( bmp != null )
				bmp.recycle();
		}
		if( mIndicatorLayout != null )
			mIndicatorLayout.Release();
	}
	
	public ThemeDescription getThemeDesc()
	{
		return mData;
	}
	
	@Override
	public void onSwitched(
			View view ,
			int position )
	{
		mIndicatorLayout.SelectPage( position );
	}
	
	protected void onAttachedToWindow()
	{
		mThemesGridLayout.InitToScreen( 1 , UtilsBase.getScreenWidth() );
	}
	
	private void LoadLayout()
	{
		mdesktop = new LinearLayout( mContext );
		LayoutParams desktoplp = new LayoutParams( LayoutParams.FILL_PARENT , LayoutParams.FILL_PARENT );
		mdesktop.setOrientation( LinearLayout.VERTICAL );
		mdesktop.setBackgroundColor( Color.rgb( 250 , 250 , 250 ) );
		addView( mdesktop , desktoplp );
		FrameLayout TitleLayout = new FrameLayout( mContext );
		TitleLayout.setBackgroundColor( Color.rgb( 52 , 52 , 52 ) );
		LayoutParams lp = new LayoutParams( LayoutParams.FILL_PARENT , LayoutParams.WRAP_CONTENT );
		mdesktop.addView( TitleLayout , lp );
		mMyTitle = new TextView( mContext );
		mMyTitle.setText( RR.string.localtheme );
		mMyTitle.setTextColor( Color.WHITE );
		mMyTitle.setTextSize( TITLETXT_FONTSIZE );
		mMyTitle.setPadding( (int)( COMMON_PADDING * SetupMenu.mScale ) , (int)( COMMON_PADDING * SetupMenu.mScale ) , 0 , (int)( COMMON_PADDING * SetupMenu.mScale ) );
		FrameLayout.LayoutParams titlelp = new FrameLayout.LayoutParams( LayoutParams.WRAP_CONTENT , LayoutParams.WRAP_CONTENT );
		titlelp.gravity = Gravity.NO_GRAVITY;
		titlelp.leftMargin = (int)( COMMON_PADDING * SetupMenu.mScale );
		TitleLayout.addView( mMyTitle , titlelp );
		mIndicatorLayout = new ThemePagePointer( mContext , true );
		mIndicatorLayout.setOrientation( LinearLayout.HORIZONTAL );
		lp = new LayoutParams( LayoutParams.WRAP_CONTENT , mIndicatorLayout.getheight() );
		mdesktop.addView( mIndicatorLayout , lp );
		mThemesGridLayout = new ThemesGridLayout( mContext );
		lp = new LayoutParams( LayoutParams.FILL_PARENT , LayoutParams.WRAP_CONTENT );
		mdesktop.addView( mThemesGridLayout , lp );
		int bh = (int)( 84 * SetupMenu.mScale );
		int bw = (int)( 168 * SetupMenu.mScale );
		LinearLayout Layout = new LinearLayout( mContext );
		Layout.setOrientation( LinearLayout.HORIZONTAL );
		Layout.setBackgroundColor( Color.rgb( 0xb5 , 0xb5 , 0xb5 ) );
		LayoutParams flp = new LayoutParams( LayoutParams.FILL_PARENT , bh );
		Layout.setGravity( Gravity.CENTER );
		flp.gravity = Gravity.BOTTOM;
		addView( Layout , flp );
		mButtonApplyTheme = new Button( mContext );
		mButtonApplyTheme.setText( RR.string.applytheme );
		mButtonApplyTheme.setTextColor( Color.BLACK );
		mButtonApplyTheme.setTextSize( BUTTON_FONTSIZE );
		mButtonApplyTheme.setTag( Integer.valueOf( BUTTON_EVENT_APPLYTHEME ) );
		mButtonApplyTheme.setOnClickListener( this );
		lp = new LayoutParams( bw , LayoutParams.WRAP_CONTENT );
		lp.gravity = Gravity.CENTER;
		// lp.height = (int) (bh * hScale);
		Layout.addView( mButtonApplyTheme , lp );
		mButtonRemoveTheme = new Button( mContext );
		mButtonRemoveTheme.setText( RR.string.removetheme );
		mButtonRemoveTheme.setTextColor( Color.BLACK );
		mButtonRemoveTheme.setTextSize( BUTTON_FONTSIZE );
		mButtonRemoveTheme.setTag( Integer.valueOf( BUTTON_EVENT_DELETETHEME ) );
		mButtonRemoveTheme.setOnClickListener( this );
		lp = new LayoutParams( bw , LayoutParams.WRAP_CONTENT );
		lp.gravity = Gravity.CENTER;
		// lp.height = (int) (bh * hScale);
		Layout.addView( mButtonRemoveTheme , lp );
	}
	
	public void LoadData(
			int index )
	{
		mData = ThemeManager.getInstance().getThemeDescriptions().elementAt( index );
		LoadLayout();
		ArrayList<String> bitmaps = mData.getBitmaps();
		int cellX = 0;
		int cellY = 0;
		int cellwidth = (int)( UtilsBase.getScreenWidth() );
		int cellheight = (int)( UtilsBase.getScreenHeight() * 0.7f );
		int count = bitmaps.size();
		mIndicatorLayout.Init( count );
		LinearLayout.LayoutParams nvlp = (LinearLayout.LayoutParams)mIndicatorLayout.getLayoutParams();
		nvlp.topMargin = (int)( COMMON_PADDING * SetupMenu.mScale );
		nvlp.gravity = Gravity.CENTER;
		mIndicatorLayout.setLayoutParams( nvlp );
		LinearLayout Layout;
		PageGridView ThemesGridView;
		LinearLayout.LayoutParams mglp;
		LayoutParams lp;
		PageGridView.LayoutParams pagelp;
		ThemesGridView = new PageGridView( mContext );
		mglp = new LinearLayout.LayoutParams( cellwidth , cellheight );
		ThemesGridView.setCellDimensions( cellwidth , cellheight , 0 , 0 );
		TextView info = new TextView( mContext );
		mData.getInfo( info );
		info.setTextColor( Color.BLACK );
		Layout = new LinearLayout( mContext );
		Layout.addView( info );
		int textMargin = (int)( 80 * SetupMenu.mScale );
		pagelp = new PageGridView.LayoutParams( cellX , cellY );
		pagelp.topMargin = textMargin;
		pagelp.leftMargin = textMargin;
		ThemesGridView.addView( Layout , pagelp );
		mThemesGridLayout.addView( ThemesGridView , mglp );
		for( int i = 0 ; i < count ; i++ )
		{
			ThemesGridView = new PageGridView( mContext );
			mglp = new LinearLayout.LayoutParams( cellwidth , cellheight );
			ThemesGridView.setCellDimensions( cellwidth , cellheight , 0 , 0 );
			ImageView image = new ImageView( mContext );
			Bitmap bmp = null;
			try
			{
				// bmp =
				// Tools.getImageFromInStream(mData.getContext().getAssets()
				// .open(ThemeDescription.PREVIEW_DIR + bitmaps.get(i)));
				bmp = Tools.getImageFromInStream(
						mData.getContext().getAssets().open( mData.autoAdaptThemeDir + File.separator + "preview" + File.separator + bitmaps.get( i ) ) ,
						Bitmap.Config.RGB_565 );
				bmp = Tools.resizeBitmap( bmp , cellwidth * cellheight / bmp.getHeight() , cellheight );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			if( bmp != null )
			{
				image.setImageBitmap( bmp );
				mbitmaps.add( bmp );
				bmp = null;
			}
			Layout = new LinearLayout( mContext );
			Layout.setOrientation( LinearLayout.HORIZONTAL );
			int imagew = cellwidth;
			int imageh = cellheight;
			lp = new LayoutParams( imagew , imageh );
			lp.gravity = Gravity.CENTER;
			Layout.addView( image , lp );
			pagelp = new PageGridView.LayoutParams( cellX , cellY );
			pagelp.topMargin = (int)( COMMON_PADDING * SetupMenu.mScale );
			ThemesGridView.addView( Layout , pagelp );
			mThemesGridLayout.addView( ThemesGridView , mglp );
		}
		if( mData.mUse )
		{
			// mButtonApplyTheme.setEnabled(false);
			// mButtonApplyTheme.setTextColor(Color.GRAY);
			mButtonRemoveTheme.setEnabled( false );
			mButtonRemoveTheme.setTextColor( Color.GRAY );
		}
		else if( mData.mSystem )
		{
			mButtonRemoveTheme.setEnabled( false );
			mButtonRemoveTheme.setTextColor( Color.GRAY );
		}
		else if( mData.mBuiltIn )
		{
			mButtonRemoveTheme.setEnabled( false );
			mButtonRemoveTheme.setTextColor( Color.GRAY );
		}
		mThemesGridLayout.setLoop( false );
		mThemesGridLayout.setOverScroll( false );
		mThemesGridLayout.setScrollingSpeed( 2.0f );
		mThemesGridLayout.setSwitchListener( this );
	}
	
	@Override
	public void onClick(
			View v )
	{
		if( v instanceof Button )
		{
			Integer event = (Integer)v.getTag();
			switch( event.intValue() )
			{
				case BUTTON_EVENT_APPLYTHEME:
					ThemeManager.getInstance().ApplyTheme( mData );
					break;
				case BUTTON_EVENT_DELETETHEME:
					ThemeManager.getInstance().RemoveTheme( mData );
					break;
				default:
					break;
			}
		}
	}
}
