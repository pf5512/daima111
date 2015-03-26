package com.iLoong.launcher.HotSeat3D;


import android.content.Context;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.theme.ThemeManager;


public class HotObjLoader3D extends ViewGroupOBJ3D
{
	
	private HotObjBackGround3D mBackView = null;
	private HotObjIcon3D mLeftView = null;
	private HotObjIcon3D mMiddleLeftView = null;
	private HotObjIcon3D mMiddleRightView = null;
	private HotObjIcon3D mRightView = null;
	private HotObjIcon3D mMiddleView = null;
	private Context mContext = null;
	
	public HotObjLoader3D(
			String name ,
			int width ,
			int height )
	{
		super( name );
		setPosition( 0 , 0 );
		setSize( width , height );
		this.setOrigin( width / 2 , height / 2 );
	}
	
	public void onThemeChanged()
	{
		this.setOrigin( width / 2 , height / 2 );
		//		mBackView.onThemeChanged();
	}
	
	public void HotObj_Loader()
	{
		mContext = iLoongApplication.getInstance();
		HotObj_addBackView();
		hideAllDockOBJViews();
	}
	
	public Context getContext()
	{
		return mContext;
	}
	
	//teapotXu add start 
	public void hideAllDockOBJViews()
	{
		if( mMiddleView != null )
			mMiddleView.hide();
		if( mLeftView != null )
			mLeftView.hide();
		if( mRightView != null )
			mRightView.hide();
		if( mMiddleRightView != null )
			mMiddleRightView.hide();
		if( mMiddleLeftView != null )
			mMiddleLeftView.hide();
	}
	
	public void ShowAllDockOBJViews()
	{
		if( mMiddleView != null )
		{
			if( DefaultLayout.same_spacing_btw_hotseat_icons == true )
				mMiddleView.hide();
			else
				mMiddleView.show();
		}
		if( mLeftView != null )
			mLeftView.show();
		if( mRightView != null )
			mRightView.show();
		if( mMiddleRightView != null )
			mMiddleRightView.show();
		if( mMiddleLeftView != null )
			mMiddleLeftView.show();
	}
	
	//teapotXu add end
	public void updateShowStatus(
			int focusIndex )
	{
		//teapotXu add start for hotseat's middle icon
		if( DefaultLayout.same_spacing_btw_hotseat_icons == true )
		{
			if( mMiddleView != null )
				mMiddleView.hide();
		}
		//teapotXu add end for hotseat's middle icon
		if( focusIndex == 0 )
		{
			if( mLeftView != null )
				mLeftView.show();
			//    		 mLeftView.stopTween();
			//    		 mLeftView.setScale(0.3f, 0.3f);
			//    		 mLeftView.startTween(View3DTweenAccessor.SCALE_XY, Quad.IN, 0.5f, 1f, 1f, 0f);
		}
		else if( focusIndex == 1 )
		{
			if( mMiddleLeftView != null )
				mMiddleLeftView.show();
			//    		mMiddleLeftView.stopTween();
			//    		mMiddleLeftView.setScale(0.3f, 0.3f);
			//    		mMiddleLeftView.startTween(View3DTweenAccessor.SCALE_XY, Quad.IN, 0.5f, 1f, 1f, 0f);
		}
		else if( focusIndex == 2 )
		{
			if( mMiddleRightView != null )
				mMiddleRightView.show();
			//    		mMiddleRightView.stopTween();
			//    		mMiddleRightView.setScale(0.3f, 0.3f);
			//    		mMiddleRightView.startTween(View3DTweenAccessor.SCALE_XY, Quad.IN, 0.5f, 1f, 1f, 0f);
		}
		else if( focusIndex == 3 )
		{
			if( mRightView != null )
				mRightView.show();
			//    		mRightView.stopTween();
			//    		mRightView.setScale(0.3f, 0.3f);
			//    		mRightView.startTween(View3DTweenAccessor.SCALE_XY, Quad.IN, 0.5f, 1f, 1f, 0f);
		}
	}
	
	public void updateShowStatus(
			boolean leftShow ,
			boolean middleLShow ,
			boolean middleRShow ,
			boolean rightShow )
	{
		//teapotXu add start for hotseat's middle icon
		if( DefaultLayout.same_spacing_btw_hotseat_icons == true )
		{
			if( mMiddleView != null )
				mMiddleView.hide();
		}
		//teapotXu add end for hotseat's middle icon
		if( leftShow == true )
		{
			updateShowStatus( 0 );
		}
		else
		{
			//		   mLeftView.stopTween();
			//		   mLeftView.startTween(View3DTweenAccessor.SCALE_XY, Quad.IN, 0.5f, 0f, 0f, 0f);
			if( mLeftView != null )
				mLeftView.hide();
		}
		if( middleLShow == true )
		{
			updateShowStatus( 1 );
		}
		else
		{
			//		   mMiddleLeftView.stopTween();
			//		   mMiddleLeftView.startTween(View3DTweenAccessor.SCALE_XY, Quad.IN, 0.5f, 0f, 0f, 0f);
			if( mMiddleLeftView != null )
				mMiddleLeftView.hide();
		}
		if( middleRShow == true )
		{
			updateShowStatus( 2 );
		}
		else
		{
			//		   mMiddleRightView.stopTween();
			//		   mMiddleRightView.startTween(View3DTweenAccessor.SCALE_XY, Quad.IN, 0.5f, 0f, 0f, 0f);
			if( mMiddleRightView != null )
				mMiddleRightView.hide();
		}
		if( rightShow == true )
		{
			updateShowStatus( 3 );
		}
		else
		{
			//		   mRightView.stopTween();
			//		   mRightView.startTween(View3DTweenAccessor.SCALE_XY, Quad.IN, 0.5f, 0f, 0f, 0f);
			if( mRightView != null )
				mRightView.hide();
		}
	}
	
	//    private Texture HotObj_LoadTexture(String imageFile)
	//    {
	//    	Texture texture = new Texture(
	//				new AndroidFiles(mContext.getAssets()).internal(imageFile));
	//		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	//		return texture;
	//    }
	private void HotObj_addBackView()
	{
		//		mBackView = new HotObjBackGround3D( "BackGround3D" , mContext , this.width , this.height , this.y );
		//		addView( mBackView );
		//		mBackView.bringToFront();
	}
	
	private void HotObj_addLeftView()
	{
		mLeftView = new HotObjIcon3D( "LeftView3D" , mContext , "launcher/dock3dobj/icon1.obj" , R3D.hot_dock_icon_size , R3D.hot_dock_icon_size , this.y );
		//		int cellWidth = (Utils3D.getScreenWidth()-R3D.hot_grid_left_margin-R3D.hot_grid_right_margin)/R3D.hot_dock_icon_number;
		//		mLeftView.x = R3D.hot_grid_left_margin+(cellWidth-mLeftView.width)/2;
		addView( mLeftView );
		mLeftView.bringToFront();
	}
	
	private TextureRegion loadTexture(
			Context context ,
			String imageFile )
	{
		//		Texture texture = new Texture(
		//				new AndroidFiles(context.getAssets()).internal(imageFile));
		Texture texture = new BitmapTexture( ThemeManager.getInstance().getBitmap( imageFile ) , true );
		texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		TextureRegion region = new TextureRegion( texture );
		return region;
	}
	
	private void HotObj_addMiddleView()
	{
		mMiddleView = new HotObjIcon3D( "MiddleView3D" , mContext , "launcher/dock3dobj/icon_middle.obj" , R3D.hot_dock_icon_size , R3D.hot_dock_icon_size , this.y );
		//teapotXu add start for hotseat's middle icon
		if( DefaultLayout.same_spacing_btw_hotseat_icons == true )
		{
			mMiddleView.hide();
		}
		else
		{
			mMiddleView.region.setRegion( loadTexture( mContext , "theme/dock3dbar/middle.png" ) );
		}
		//teapotXu add end for hotseat's middle icon
		addView( mMiddleView );
		mMiddleView.bringToFront();
	}
	
	private void HotObj_addMiddleLeftView()
	{
		mMiddleLeftView = new HotObjIcon3D( "MiddleLeftView3D" , mContext , "launcher/dock3dobj/icon2.obj" , R3D.hot_dock_icon_size , R3D.hot_dock_icon_size , this.y );
		//    	int cellWidth = (Utils3D.getScreenWidth()-R3D.hot_grid_left_margin-R3D.hot_grid_right_margin)/R3D.hot_dock_icon_number;
		//    	mMiddleLeftView.x = R3D.hot_grid_left_margin+cellWidth+(cellWidth-mMiddleLeftView.width)/2;
		addView( mMiddleLeftView );
		mMiddleLeftView.bringToFront();
	}
	
	private void HotObj_addMiddleRightView()
	{
		mMiddleRightView = new HotObjIcon3D( "MiddleRightView3D" , mContext , "launcher/dock3dobj/icon3.obj" , R3D.hot_dock_icon_size , R3D.hot_dock_icon_size , this.y );
		//    	int cellWidth = (Utils3D.getScreenWidth()-R3D.hot_grid_left_margin-R3D.hot_grid_right_margin)/R3D.hot_dock_icon_number;
		//    	mMiddleRightView.x = Utils3D.getScreenWidth()-R3D.hot_grid_right_margin-2*cellWidth+(cellWidth-mMiddleRightView.width)/2;
		addView( mMiddleRightView );
		mMiddleRightView.bringToFront();
	}
	
	private void HotObj_addRightView()
	{
		mRightView = new HotObjIcon3D( "RightView3D" , mContext , "launcher/dock3dobj/icon4.obj" , R3D.hot_dock_icon_size , R3D.hot_dock_icon_size , this.y );
		//    	int cellWidth = (Utils3D.getScreenWidth()-R3D.hot_grid_left_margin-R3D.hot_grid_right_margin)/R3D.hot_dock_icon_number;
		//    	mRightView.x = Utils3D.getScreenWidth()-R3D.hot_grid_right_margin-cellWidth+(cellWidth-mRightView.width)/2;
		addView( mRightView );
		mRightView.bringToFront();
	}
}
