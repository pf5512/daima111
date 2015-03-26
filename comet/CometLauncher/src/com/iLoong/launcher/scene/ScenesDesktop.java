// wanghongjian add whole file //enable_DefaultScene
package com.iLoong.launcher.scene;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.PageGridView;
import com.iLoong.launcher.SetupMenu.PagedView;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;


class SceneItem
{
	
	// public ImageView mSelectSwitch;
	public TextView mThemeName;
	public ImageView mDefaultBmp;
	public ImageView mBackBmp;
	public int id;
}

public class ScenesDesktop extends FrameLayout implements View.OnClickListener , PagedView.ViewSwitchListener
{
	
	private static final int PAGEROWS = 2;
	private static final int PAGECOLUMNS = 2;
	private static final int PAGECOUNT = PAGEROWS * PAGECOLUMNS;
	private static final int COMMON_PADDING = 6;
	private static final int TITLETXT_FONTSIZE = 20;
	private static final int BUTTON_FONTSIZE = 12;
	private static final int BUTTON_EVENT_CURRENTTHEME = 1;
	private static final int BUTTON_EVENT_ONLINETHEME = 2;
	private TextView mMyTitle;
	private LinearLayout mdesktop;
	private ScenePagePointer mIndicatorLayout;
	private ScenesGridLayout mThemesGridLayout;
	private Button mButtonOnlineTheme;
	private Context mContext;
	private Vector<SceneDescription> mData;
	private LayoutInflater mLayoutInflater;
	private ArrayList<Bitmap> mbitmaps = new ArrayList<Bitmap>();
	
	public ScenesDesktop(
			Context context )
	{
		super( context );
		mContext = context;
		mLayoutInflater = LayoutInflater.from( mContext );
		mData = SceneManager.getInstance().getThemeDescriptions();
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
		if( mIndicatorLayout != null )
			mIndicatorLayout.Release();
	}
	
	private void LoadLayout()
	{
		mdesktop = new LinearLayout( mContext );
		LayoutParams desktoplp = new LayoutParams( LayoutParams.FILL_PARENT , LayoutParams.FILL_PARENT );
		mdesktop.setOrientation( LinearLayout.VERTICAL );
		mdesktop.setBackgroundColor( Color.rgb( 234 , 233 , 231 ) );
		mdesktop.setGravity( Gravity.CENTER_HORIZONTAL );
		addView( mdesktop , desktoplp );
		LinearLayout Layout = new LinearLayout( mContext );
		Layout.setOrientation( LinearLayout.HORIZONTAL );
		Layout.setBackgroundColor( Color.rgb( 234 , 233 , 231 ) );
		mdesktop.addView( Layout , LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT );
		mMyTitle = new TextView( mContext );
		Bitmap titleBmp = null;
		byte[] bytes = null;
		try
		{
			if( Utils3D.getScreenWidth() < 500 )
			{
				titleBmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/scenetheme/title_bg_small.png" ) );
			}
			else if( Utils3D.getScreenWidth() > 500 && Utils3D.getScreenWidth() < 800 )
			{
				titleBmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/scenetheme/title_bg.png" ) );
			}
			else
			{
				titleBmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/scenetheme/title_bg_big.png" ) );
			}
			mbitmaps.add( titleBmp );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( titleBmp != null )
		{
			bytes = titleBmp.getNinePatchChunk();
		}
		// boolean result = NinePatch.isNinePatchChunk(bytes);
		// NinePatchDrawable drawable = new NinePatchDrawable(titleBmp, bytes,
		// new Rect(), null);
		BitmapDrawable drawable = new BitmapDrawable( titleBmp );
		// Texture texture = new BitmapTexture(titleBmp);
		// NinePatch patch = new NinePatch(bitmap, chunk, srcName)
		// NinePatchDrawable patchy = new NinePatchDrawable(patch);
		int titleSize = 0;
		int gridlayoutMarginTop = 0;
		int myTitleH = 0;
		if( Utils3D.getScreenWidth() < 500 )
		{
			titleSize = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_mMytitle_size );
			gridlayoutMarginTop = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginTop );
			myTitleH = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_mMytitle_height );
		}
		else if( Utils3D.getScreenWidth() > 500 && Utils3D.getScreenWidth() < 600 )
		{
			titleSize = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_mMytitle_size_five );
			gridlayoutMarginTop = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginTop_five );
			myTitleH = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_mMytitle_height_five );
		}
		else if( Utils3D.getScreenWidth() > 600 && Utils3D.getScreenWidth() < 800 )
		{
			titleSize = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_mMytitle_size_seven );
			gridlayoutMarginTop = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginTop_seven );
			myTitleH = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_mMytitle_height_seven );
		}
		else
		{
			titleSize = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_mMytitle_size_big );
			gridlayoutMarginTop = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginTop_big );
			myTitleH = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_mMytitle_height_big );
		}
		// Log.v("", " dime titleSize is " + titleSize +
		// " gridlayoutMarginTop is " + gridlayoutMarginTop);
		mMyTitle.setTextSize( titleSize );
		mMyTitle.setWidth( SetupMenu.getInstance().mWidth );
		mMyTitle.setBackgroundDrawable( drawable );
		mMyTitle.setHeight( myTitleH );
		mMyTitle.setText( RR.string.localScene );
		mMyTitle.setGravity( Gravity.CENTER );
		mMyTitle.setTextColor( Color.rgb( 61 , 61 , 61 ) );
		Layout.setGravity( Gravity.CENTER );
		Layout.addView( mMyTitle );
		mThemesGridLayout = new ScenesGridLayout( mContext );
		// mThemesGridLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT );
		lps.topMargin = gridlayoutMarginTop;
		mdesktop.addView( mThemesGridLayout , lps );
		mIndicatorLayout = new ScenePagePointer( mContext , false );
		mIndicatorLayout.setOrientation( LinearLayout.HORIZONTAL );
		// LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
		// mIndicatorLayout.getheight());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT , mIndicatorLayout.getheight() );
		mdesktop.addView( mIndicatorLayout , lp );
	}
	
	public void LoadData()
	{
		View CurrentView;
		SceneItem item;
		int count = mData.size();
		if( count < 5 )
		{
			mIndicatorLayout.setVisibility( INVISIBLE );
		}
		int page = (int)( (float)count / PAGECOUNT + 0.9f );
		mIndicatorLayout.Init( page );
		LinearLayout.LayoutParams nvlp = (LinearLayout.LayoutParams)mIndicatorLayout.getLayoutParams();
		int indicatorMarginTop = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_Indicator_marginTop );
		nvlp.topMargin = indicatorMarginTop;
		nvlp.gravity = Gravity.CENTER;
		mIndicatorLayout.setLayoutParams( nvlp );
		mThemesGridLayout.removeAllViews();
		// final int mHeight = (int) (512 * SetupMenu.mScale);
		int mHeight = 0;
		int cellwidth = 0;
		if( Utils3D.getScreenWidth() < 500 )
		{
			mHeight = (int)(float)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_gridView_height );
			cellwidth = (int)( (float)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_gridView_width ) / PAGEROWS );
		}
		else if( Utils3D.getScreenWidth() > 500 && Utils3D.getScreenWidth() < 600 )
		{
			mHeight = (int)(float)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_gridView_height_five );
			cellwidth = (int)( (float)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_gridView_width_five ) / PAGEROWS );
		}
		else if( Utils3D.getScreenWidth() > 500 && Utils3D.getScreenWidth() < 800 )
		{
			mHeight = (int)(float)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_gridView_height_seven );
			cellwidth = (int)( (float)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_gridView_width_seven ) / PAGEROWS );
		}
		else
		{
			mHeight = (int)(float)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_gridView_height_big );
			cellwidth = (int)( (float)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_gridView_width_big ) / PAGEROWS );
		}
		if( SetupMenu.getInstance() == null )
		{
			Log.v( "" , "SetupMenu.getInstance() is null" );
		}
		int cellheight = (int)( (float)mHeight / PAGECOLUMNS );
		// final int vwidth = (int)
		// iLoongLauncher.getInstance().getResources().getDimension(RR.dimen.scene_GridLayout_Width);
		// final int vheight = (int)
		// iLoongLauncher.getInstance().getResources().getDimension(RR.dimen.scene_GridLayout_Height);
		// Log.v("", "dime vwidth is " + vwidth + " vheight is " +
		// vheight+" cellwidth is " + cellwidth + " cellheight is " +
		// cellheight);
		int j = 0;
		for( int index = PAGECOUNT ; index < count + PAGECOUNT ; index += PAGECOUNT )
		{
			int cellX = 0;
			int cellY = -1;
			PageGridView ThemesGridView = new PageGridView( mContext );
			LinearLayout.LayoutParams tgvlp = new LinearLayout.LayoutParams( SetupMenu.getInstance().mWidth , mHeight );
			ThemesGridView.setCellDimensions( cellwidth , cellheight , -10 , 0 );
			int pagecount = index <= count ? PAGECOUNT : count + PAGECOUNT - index;
			for( int i = 0 ; i < pagecount ; i++ , j++ )
			{
				int titleSize = 0;
				if( Utils3D.getScreenWidth() < 500 )
				{
					CurrentView = mLayoutInflater.inflate( RR.layout.scene_itemdel , null );
					item = new SceneItem();
					item.mDefaultBmp = (ImageView)CurrentView.findViewById( RR.id.imageSceneThumb );
					item.mThemeName = (TextView)CurrentView.findViewById( RR.id.textAppName );
					titleSize = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_name_size );
				}
				else if( Utils3D.getScreenWidth() > 500 && Utils3D.getScreenWidth() < 600 )
				{
					CurrentView = mLayoutInflater.inflate( RR.layout.scene_itemdel_five , null );
					item = new SceneItem();
					item.mDefaultBmp = (ImageView)CurrentView.findViewById( RR.id.imageSceneThumbFive );
					item.mThemeName = (TextView)CurrentView.findViewById( RR.id.textAppNameFive );
					titleSize = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_name_size_five );
				}
				else if( Utils3D.getScreenWidth() > 600 && Utils3D.getScreenWidth() < 800 )
				{
					CurrentView = mLayoutInflater.inflate( RR.layout.scene_itemdel_seven , null );
					item = new SceneItem();
					item.mDefaultBmp = (ImageView)CurrentView.findViewById( RR.id.imageSceneThumbSeven );
					item.mThemeName = (TextView)CurrentView.findViewById( RR.id.textAppNameSeven );
					titleSize = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_name_size_seven );
				}
				else
				{
					CurrentView = mLayoutInflater.inflate( RR.layout.scene_itemdel_seven , null );
					item = new SceneItem();
					item.mDefaultBmp = (ImageView)CurrentView.findViewById( RR.id.imageSceneThumbSeven );
					item.mThemeName = (TextView)CurrentView.findViewById( RR.id.textAppNameSeven );
					titleSize = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_name_size_big );
				}
				SceneDescription themedesc = mData.elementAt( j );
				item.id = j;
				if( themedesc.mUse )
				{
					item.mThemeName.setBackgroundColor( Color.argb( 250 , 0x29 , 0x91 , 0xf1 ) );
				}
				else
				{
					item.mThemeName.setBackgroundColor( Color.argb( 122 , 0 , 0 , 0 ) );
				}
				item.mThemeName.setTextSize( titleSize );
				item.mThemeName.setTextColor( Color.WHITE );
				item.mThemeName.setText( themedesc.title );
				Bitmap bmp = themedesc.getDefaultBitmap();
				if( bmp != null )
				{
					// bmp = Tools.resizeBitmap(bmp, (int) (vwidth), (int)
					// (vheight));
					mbitmaps.add( bmp );
					item.mDefaultBmp.setImageBitmap( bmp );
				}
				int mThemeNamewidth = 0;
				if( Utils3D.getScreenWidth() < 500 )
				{
					mThemeNamewidth = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_name_width );
				}
				else if( Utils3D.getScreenWidth() > 500 && Utils3D.getScreenWidth() < 600 )
				{
					mThemeNamewidth = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_name_width_five );
				}
				else if( Utils3D.getScreenWidth() > 600 && Utils3D.getScreenWidth() < 800 )
				{
					mThemeNamewidth = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_name_width_seven );
				}
				else
				{
					mThemeNamewidth = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_name_width_big );
				}
				item.mThemeName.setWidth( mThemeNamewidth );
				CurrentView.setTag( item );
				CurrentView.setOnClickListener( this );
				cellX = i % PAGEROWS;
				cellY = ( cellX == 0 ) ? ++cellY : cellY;
				PageGridView.LayoutParams lp = new PageGridView.LayoutParams( cellX , cellY );
				int top = 0;
				int left = 0;
				int bottom = 0;
				int right = 0;
				if( Utils3D.getScreenWidth() < 500 )
				{
					top = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginBetween );
					left = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginLeft );
					bottom = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginBottom );
					right = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginRight );
				}
				else if( Utils3D.getScreenWidth() > 500 && Utils3D.getScreenWidth() < 800 )
				{
					top = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginBetween_seven );
					left = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginLeft_seven );
					bottom = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginBottom_seven );
					right = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginRight_seven );
				}
				else
				{
					top = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginBetween_big );
					left = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginLeft_big );
					bottom = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginBottom_big );
					right = (int)iLoongLauncher.getInstance().getResources().getDimension( RR.dimen.scene_GridLayout_marginRight_big );
				}
				lp.topMargin = top;
				lp.bottomMargin = bottom;
				lp.leftMargin = left;
				lp.rightMargin = right;
				// Log.v("", " dime top is " + top + " bottom is " + bottom+
				// " left is " + left + " right is " + right);
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
		Drawable backDown = null;
		Object obj = v.getTag();
		if( v instanceof Button )
		{
			Uri uri = Uri.parse( iLoongApplication.getInstance().getResources().getString( RR.string.setting_onlinetheme ) );
			mContext.startActivity( new Intent( Intent.ACTION_VIEW , uri ) );
		}
		else if( obj instanceof SceneItem )
		{
			Intent localIntent = new Intent();
			localIntent.setClass( mContext , SceneDetailedActivity.class );
			SceneItem item = (SceneItem)obj;
			if( backDown == null )
			{
				backDown = iLoongLauncher.getInstance().getResources().getDrawable( RR.drawable.back_down );
			}
			if( backDown != null )
				item.mDefaultBmp.setBackgroundDrawable( backDown );
			localIntent.putExtra( SceneDetailedActivity.class.getSimpleName() , item.id );
			mContext.startActivity( localIntent );
		}
	}
}
