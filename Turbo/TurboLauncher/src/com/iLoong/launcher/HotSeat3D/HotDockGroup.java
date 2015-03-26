package com.iLoong.launcher.HotSeat3D;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.action.ActionData;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.search.QSearchGroup;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class HotDockGroup extends ViewGroupOBJ3D
{
	
	public static int CELL_MAX_NUM_X = 5;
	public HotSeat3D hotSeat3D;
	private final int SCROLL_UP = 0;
	private final int SCROLL_DOWN = 1;
	// private final int SCROLL_LEFT = 2;
	// private final int SCROLL_RIGHT = 3;
	public static final int MAX_ICON_NUM = 5;
	private final float VELOCITY_DIV = 30f;
	private ArrayList<View3D> mHotseatItems;
	private HotGridView3D mShortcutView;
	private HotGridView3D mFocusGridView;
	View3D folder_icon;
	private int scrollDir = -1;
	private boolean isFling = false;
	private boolean bStartScrollDown = false;
	private float scaleY = 0;
	// private Tween flingTween = null;
	Vector2 IconPoint = new Vector2();
	public boolean dragAble = true;
	private boolean isScroll = false;
	// private int curFolderPos=-1;
	private Workspace3D workspace;
	public int clingR;
	public int clingX;
	public int clingY;
	//teapotXu add start for hotseat's middle icon
	public View3D mHotSeatMiddleImgView;
	//teapotXu add end for hotseat's middle icon
	private List<ResolveInfo> hotDialIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotMmsIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotContactIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotBorwserIntentMap = new ArrayList<ResolveInfo>();
	public boolean originalTouch = false;
	public ImageView3D backgroud = null;
	public static ImageView3D dialogBg = null;
	public static View3D outView = null;
	
	// static Texture t = new Texture(Gdx.files.internal("bgtest.png"));
	public HotDockGroup(
			String name )
	{
		super( name );
	}
	
	public boolean isInShortcutList()
	{
		return mFocusGridView == mShortcutView;
	}
	
	public void setWorkspace(
			View3D v )
	{
		this.workspace = (Workspace3D)v;
	}
	
	//teapotXu add start for hotseat's middle icon
	private TextureRegion loadTexture(
			Context context ,
			String imageFile )
	{
		Texture texture = new BitmapTexture( ThemeManager.getInstance().getBitmap( imageFile ) , true );
		texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		TextureRegion region = new TextureRegion( texture );
		return region;
	}
	
	private Vector2 getHotseatMiddleImgPos(
			float bmpWidth ,
			float bmpHeight ,
			int hotDockGroupWidth ,
			int padding_left ,
			int padding_right ,
			int padding_top ,
			int padding_bottom )
	{
		Vector2 vector = new Vector2();
		if( DefaultLayout.hot_dock_icon_number == 4 )
		{
			int cellWidth = ( hotDockGroupWidth - padding_left - padding_right ) / 4;
			vector.x = padding_left + 3 * cellWidth + ( cellWidth - bmpWidth ) / 2;
		}
		//		else if( DefaultLayout.mainmenu_pos == HotGridView3D.MAINMENU_RIGHT )
		//		{
		//			int cellWidth = ( hotDockGroupWidth - padding_left - padding_right ) / 5;
		//			vector.x = padding_left + 4 * cellWidth + ( cellWidth - bmpWidth ) / 2;
		//		}
		else
			vector.x = ( hotDockGroupWidth - bmpWidth ) / 2;
		if( DefaultLayout.enable_hotseat_middle_icon_horizontal )
		{
			if( DefaultLayout.hotseat_hide_title )
			{
				vector.y = padding_bottom;
			}
			else
			{
				float space_height = R3D.workspace_cell_height - bmpHeight;
				vector.y = padding_bottom + space_height;
			}
		}
		else
		{
			float space_height = bmpHeight / R3D.icon_title_gap;
			if( DefaultLayout.hotseat_hide_title )
			{
				vector.y = padding_bottom + space_height;
			}
			else
			{
				space_height = R3D.workspace_cell_height - Utils3D.getIconBmpHeight();
				vector.y = padding_bottom + space_height;
			}
		}
		return vector;
	}
	
	//teapotXu add end for hotseat's middle icon
	public HotDockGroup(
			String name ,
			int width ,
			int height )
	{
		super( name );
		Bitmap bg = ThemeManager.getInstance().getBitmap( "theme/dock3dbar/dockbg.png" );
		if( bg != null )
		{
			bg = Tools.resizeBitmap( bg , width , height );
			backgroud = new ImageView3D( "backgroud" , bg );
			if( !bg.isRecycled() )
			{
				bg.recycle();
			}
			this.addView( backgroud );
		}
		// float gridViewHeight = height - SideBar.SIDEBAR_OPCITY_WIDTH;
		int padding = (int)( ( height - R3D.workspace_cell_height ) / 2 );
		if( padding < 0 )
		{
			padding = 0;
		}
		mHotseatItems = new ArrayList<View3D>();
		mShortcutView = new HotGridView3D( "shortcutview" , width , height , R3D.hot_dock_icon_number , 1 );
		mShortcutView.setPadding( 0 , 0 , 0 , R3D.hot_grid_bottom_margin );
		//mShortcutView.setPadding( R3D.hot_grid_left_margin , R3D.hot_grid_right_margin , 0 , R3D.hot_grid_bottom_margin );
		mShortcutView.setPosition( 0 , 0 );
		addView( mShortcutView );
		setVisible( HotSeat3D.TYPE_WIDGET );
		setSize( width , height );
		this.setOrigin( width / 2 , height / 2 );
		mFocusGridView = mShortcutView;
		clingR = R3D.workspace_cell_width / 2;
		clingX = Utils3D.getScreenWidth() / 2;
		clingY = (int)( R3D.hot_grid_bottom_margin * 1.5f + ( height - R3D.hot_grid_bottom_margin ) / 2 );
		initHotIntentMap();
	}
	
	public int getIntentType(
			Intent intent )
	{
		if( intent == null || intent.getComponent() == null )
		{
			return -1;
		}
		String packageName = intent.getComponent().getPackageName();
		String className = intent.getComponent().getClassName();
		for( int i = 0 ; i < hotDialIntentMap.size() ; i++ )
		{
			if( hotDialIntentMap.get( i ).activityInfo.packageName.equals( packageName ) && hotDialIntentMap.get( i ).activityInfo.name.equals( className ) )
			{
				return 0;
			}
		}
		for( int i = 0 ; i < hotContactIntentMap.size() ; i++ )
		{
			if( hotContactIntentMap.get( i ).activityInfo.packageName.equals( packageName ) && hotContactIntentMap.get( i ).activityInfo.name.equals( className ) )
			{
				return 1;
			}
		}
		for( int i = 0 ; i < hotMmsIntentMap.size() ; i++ )
		{
			if( hotMmsIntentMap.get( i ).activityInfo.packageName.equals( packageName ) && hotMmsIntentMap.get( i ).activityInfo.name.equals( className ) )
			{
				return 2;
			}
		}
		for( int i = 0 ; i < hotBorwserIntentMap.size() ; i++ )
		{
			if( hotBorwserIntentMap.get( i ).activityInfo.packageName.equals( packageName ) && hotBorwserIntentMap.get( i ).activityInfo.name.equals( className ) )
			{
				return 3;
			}
		}
		return -1;
	}
	
	private void initHotIntentMap()
	{
		Intent mainIntent = null;
		List<ResolveInfo> list = null;
		PackageManager pm = iLoongLauncher.getInstance().getPackageManager();
		try
		{
			mainIntent = Intent.parseUri( "intent:#Intent;action=android.intent.action.DIAL;end" , 0 );
			list = pm.queryIntentActivities( mainIntent , 0 );
			for( int i = 0 ; i < list.size() ; i++ )
			{
				int flags = list.get( i ).activityInfo.applicationInfo.flags;
				if( ( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0 ) )
				{
					hotDialIntentMap.add( list.get( i ) );
				}
			}
		}
		catch( URISyntaxException e )
		{
			e.printStackTrace();
		}
		try
		{
			mainIntent = Intent.parseUri( "intent:content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;end" , 0 );
			list = pm.queryIntentActivities( mainIntent , 0 );
			for( int i = 0 ; i < list.size() ; i++ )
			{
				int flags = list.get( i ).activityInfo.applicationInfo.flags;
				if( ( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0 ) )
				{
					hotContactIntentMap.add( list.get( i ) );
				}
			}
		}
		catch( URISyntaxException e )
		{
			e.printStackTrace();
		}
		try
		{
			mainIntent = Intent.parseUri( "intent:#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;end" , 0 );
			list = pm.queryIntentActivities( mainIntent , 0 );
			for( int i = 0 ; i < list.size() ; i++ )
			{
				int flags = list.get( i ).activityInfo.applicationInfo.flags;
				if( ( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0 ) )
				{
					hotMmsIntentMap.add( list.get( i ) );
				}
			}
		}
		catch( URISyntaxException e )
		{
			e.printStackTrace();
		}
		try
		{
			mainIntent = new Intent( "android.intent.action.VIEW" );
			mainIntent.addCategory( "android.intent.category.DEFAULT" );
			mainIntent.addCategory( "android.intent.category.BROWSABLE" );
			Uri uri = Uri.parse( "http://" );
			mainIntent.setDataAndType( uri , null );
			// mainIntent.addCategory(Intent.CATEGORY_BROWSABLE);
			list = pm.queryIntentActivities( mainIntent , 0 );
			for( int i = 0 ; i < list.size() ; i++ )
			{
				int flags = list.get( i ).activityInfo.applicationInfo.flags;
				if( ( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0 ) )
				{
					hotBorwserIntentMap.add( list.get( i ) );
				}
			}
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void findBrowser()
	{
		// PackageManager packageManager = iLoongLauncher.getInstance()
		// .getPackageManager();
		// String str1 = "android.intent.category.DEFAULT";
		// String str2 = "android.intent.category.BROWSABLE";
		// String str3 = "android.intent.action.VIEW";
		//
		// // 设置默认项的必须参数之一,用户的操作符合该过滤器时,默认设置起效
		// IntentFilter filter = new IntentFilter(str3);
		// filter.addCategory(str1);
		// filter.addCategory(str2);
		// filter.addDataScheme("http");
		// // 设置浏览页面用的Activity
		// ComponentName component = new ComponentName(context.getPackageName(),
		// BrowserActivity.class.getName());
		//
		// Intent intent = new Intent(str3);
		// intent.addCategory(str2);
		// intent.addCategory(str1);
		// Uri uri = Uri.parse("http://");
		// intent.setDataAndType(uri, null);
		//
		// // 找出手机当前安装的所有浏览器程序
		// List<ResolveInfo> resolveInfoList = packageManager
		// .queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		//
		// int size = resolveInfoList.size();
		// ComponentName[] arrayOfComponentName = new ComponentName[size];
		// for (int i = 0; i < size; i++) {
		// ActivityInfo activityInfo = resolveInfoList.get(i).activityInfo;
		// String packageName = activityInfo.packageName;
		// String className = activityInfo.name;
		// // 清除之前的默认设置
		// packageManager.clearPackagePreferredActivities(packageName);
		// ComponentName componentName = new ComponentName(packageName,
		// className);
		// arrayOfComponentName[i] = componentName;
		// }
		// packageManager.addPreferredActivity(filter,
		// IntentFilter.MATCH_CATEGORY_SCHEME, arrayOfComponentName,
		// component);
	}
	
	public void setTakeinBitmap()
	{
		if( DefaultLayout.enable_takein_workspace_by_longclick )
		{
			if( DefaultLayout.same_spacing_btw_hotseat_icons == true )
			{
				String path = null;
				if( DefaultLayout.enable_hotseat_middle_icon_horizontal )
				{
					Bitmap middleImgView;
					if( Workspace3D.isHideAll )
					{
						path = "theme/dock3dbar/middle_2.png";
					}
					else
					{
						path = "theme/dock3dbar/middle.png";
					}
					Bitmap tmp = ThemeManager.getInstance().getBitmap( path );
					if( tmp.getWidth() == DefaultLayout.app_icon_size && tmp.getHeight() == DefaultLayout.app_icon_size )
					{
						middleImgView = tmp;
					}
					else
					{
						middleImgView = Bitmap.createScaledBitmap( tmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
						tmp.recycle();
					}
					Bitmap middleImgView2 = Utils3D.titleToBitmap(
							middleImgView ,
							iLoongLauncher.getInstance().getResources().getString( RR.string.main_menu ) ,
							null ,
							null ,
							R3D.workspace_cell_width ,
							R3D.folder_front_height );
					( (BitmapTexture)mHotSeatMiddleImgView.region.getTexture() ).changeBitmap( middleImgView2 , true );
					mHotSeatMiddleImgView.region = new TextureRegion( mHotSeatMiddleImgView.region.getTexture() );
					if( DefaultLayout.hotseat_hide_title )
					{
						float scale = Utils3D.getIconBmpHeight() / R3D.workspace_cell_height;
						float V2 = mHotSeatMiddleImgView.region.getV() + ( mHotSeatMiddleImgView.region.getV2() - mHotSeatMiddleImgView.region.getV() ) * scale;
						mHotSeatMiddleImgView.region.setV2( V2 );
						mHotSeatMiddleImgView.setSize( R3D.workspace_cell_width , Utils3D.getIconBmpHeight() );
					}
				}
				else
				{
					if( Workspace3D.isHideAll )
					{
						path = "theme/dock3dbar/middle_2.png";
					}
					else
					{
						path = "theme/dock3dbar/middle.png";
					}
					( (BitmapTexture)mHotSeatMiddleImgView.region.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( path ) , true );
					mHotSeatMiddleImgView.setSize( Utilities.sIconTextureHeight , Utilities.sIconTextureHeight );
				}
				Vector2 middleImgViewPos = getHotseatMiddleImgPos(
						mHotSeatMiddleImgView.getWidth() ,
						mHotSeatMiddleImgView.getHeight() ,
						(int)width ,
						R3D.hot_grid_left_margin ,
						R3D.hot_grid_right_margin ,
						0 ,
						R3D.hot_grid_bottom_margin );
				mHotSeatMiddleImgView.setPosition( middleImgViewPos.x , middleImgViewPos.y );
			}
		}
	}
	
	public void onThemeChanged()
	{
		int padding = (int)( height - R3D.workspace_cell_height );
		if( padding < 0 )
		{
			padding = 0;
		}
		mShortcutView.setSize( width , height );
		mShortcutView.onThemeChanged();
		setSize( width , height );
		if( backgroud != null )
		{
			backgroud.region.getTexture().dispose();
			backgroud.remove();
		}
		Bitmap bg = ThemeManager.getInstance().getBitmap( "theme/dock3dbar/dockbg.png" );
		if( bg != null )
		{
			bg = Tools.resizeBitmap( bg , (int)width , (int)height );
			backgroud = new ImageView3D( "backgroud" , bg );
			if( !bg.isRecycled() )
			{
				bg.recycle();
			}
			this.addView( backgroud );
		}
		this.setOrigin( width / 2 , height / 2 );
		clingR = R3D.workspace_cell_width / 2;
		clingX = Utils3D.getScreenWidth() / 2;
		clingY = (int)( R3D.hot_grid_bottom_margin * 1.5f + ( height - R3D.hot_grid_bottom_margin ) / 2 );
		//teapotXu add start for hotseat's middle icon
		if( DefaultLayout.same_spacing_btw_hotseat_icons == true )
		{
			if( DefaultLayout.enable_hotseat_middle_icon_horizontal )
			{
				Bitmap middleImgView;
				Bitmap tmp = ThemeManager.getInstance().getBitmap( "theme/dock3dbar/middle.png" );
				if( tmp.getWidth() == DefaultLayout.app_icon_size && tmp.getHeight() == DefaultLayout.app_icon_size )
				{
					middleImgView = tmp;
				}
				else
				{
					middleImgView = Bitmap.createScaledBitmap( tmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
					tmp.recycle();
				}
				Bitmap middleImgView2 = Utils3D.titleToBitmap(
						middleImgView ,
						iLoongLauncher.getInstance().getResources().getString( RR.string.main_menu ) ,
						null ,
						null ,
						R3D.workspace_cell_width ,
						R3D.folder_front_height );
				( (BitmapTexture)mHotSeatMiddleImgView.region.getTexture() ).changeBitmap( middleImgView2 , true );
				mHotSeatMiddleImgView.region = new TextureRegion( mHotSeatMiddleImgView.region.getTexture() );
				if( DefaultLayout.hotseat_hide_title )
				{
					float scale = Utils3D.getIconBmpHeight() / R3D.workspace_cell_height;
					float V2 = mHotSeatMiddleImgView.region.getV() + ( mHotSeatMiddleImgView.region.getV2() - mHotSeatMiddleImgView.region.getV() ) * scale;
					mHotSeatMiddleImgView.region.setV2( V2 );
					mHotSeatMiddleImgView.setSize( R3D.workspace_cell_width , Utils3D.getIconBmpHeight() );
				}
			}
			else
			{
				//				( (BitmapTexture)mHotSeatMiddleImgView.region.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/dock3dbar/middle.png" ) , true );
				//				mHotSeatMiddleImgView.setSize( Utilities.sIconTextureHeight , Utilities.sIconTextureHeight );
			}
			//			Vector2 middleImgViewPos = getHotseatMiddleImgPos(
			//					mHotSeatMiddleImgView.getWidth() ,
			//					mHotSeatMiddleImgView.getHeight() ,
			//					(int)width ,
			//					R3D.hot_grid_left_margin ,
			//					R3D.hot_grid_right_margin ,
			//					0 ,
			//					R3D.hot_grid_bottom_margin );
			//			mHotSeatMiddleImgView.setPosition( middleImgViewPos.x , middleImgViewPos.y );
		}
		//teapotXu add end for hotseat's hotseat's middle icon
	}
	
	private View3D createVirturFolder()
	{
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = R3D.folder3D_name;
		folderInfo.screen = workspace.getCurrentScreen();
		folderInfo.x = 0;
		folderInfo.y = 0;
		FolderIcon3D folderIcon3D = new FolderIcon3D( "FolderIcon3DView" , folderInfo );
		folderIcon3D.changeFolderFrontRegion( true );
		return folderIcon3D;
	}
	
	public void removeVirtueFolderIcon()
	{
		if( folder_icon != null )
		{
			this.removeView( folder_icon );
			folder_icon = null;
		}
	}
	
	@Override
	public void removeAllViews()
	{
		mFocusGridView.removeAllViews();
	}
	
	private void cellMakeFolder(
			float posx ,
			float posy ,
			boolean preview )
	{
		//		if( folder_icon == null )
		//		{
		//			folder_icon = createVirturFolder();
		//			folder_icon.setVisible( false );
		//		}
		//		if( DefaultLayout.hotseat_hide_title )
		//		{
		//			folder_icon.x = posx;
		//			folder_icon.y = posy;
		//		}
		//		else
		//		{
		//			folder_icon.x = posx;
		//			folder_icon.y = posy + ( R3D.workspace_cell_height - Utils3D.getIconBmpHeight() );
		//		}
		//
		//		super.addViewAt( 0 , folder_icon );
		//		if( !folder_icon.getVisible() )
		//			FolderLargeAnim( 0.3f , folder_icon );
	}
	
	public void setVisible(
			int type )
	{
		// if(ClingManager.getInstance().widgetClingFired)SendMsgToAndroid.sendRefreshClingStateMsg();
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 1 )
		{
			return false;
		}
		originalTouch = true;
		mFocusGridView.stopTween();
		mFocusGridView.setUser( 0 );
		isFling = false;
		isScroll = false;
		super.onTouchDown( x , y , pointer );
		requestFocus();
		return true;
	}
	
	private float mVelocityX;
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		Log.i( "HotDockGroup" , velocityX + "" );
		mVelocityX = velocityX;
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( deltaX == 0 || Math.abs( deltaY ) / Math.abs( deltaX ) > 1 )
		{
			if( Desktop3DListener.d3d.mScrollTempDir == Desktop3D.SCROLL_UNINITED )
			{
				Desktop3DListener.d3d.mScrollTempDir = Desktop3D.SCROLL_VERTICAL;
			}
		}
		else
		{
			if( Desktop3DListener.d3d.mScrollTempDir == Desktop3D.SCROLL_UNINITED )
			{
				Desktop3DListener.d3d.mScrollTempDir = Desktop3D.SCROLL_HORIZ0TAL;
			}
		}
		if( mFocusGridView.lastTouchedChild != null && ( mFocusGridView.lastTouchedChild instanceof Icon3D || mFocusGridView.lastTouchedChild instanceof FolderIcon3D ) )
		{
			mFocusGridView.lastTouchedChild.color.a = 1f;
		}
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleSetRepeatPosition( this.x , this.y , x , y );
		}
		scaleY += 2 * deltaY / this.height;
		if( scaleY > 0 && bStartScrollDown == false )
		{
			bStartScrollDown = true;
		}
		if( scaleY >= 1 )
		{
			scaleY = 1;
		}
		if( scaleY <= -0.5f )
		{
			scaleY = -0.5f;
		}
		if( !isScroll && super.scroll( x , y , deltaX , deltaY ) )
		{
			return true;
		}
		if( originalTouch && ( Desktop3DListener.d3d.mScrollTempDir == Desktop3D.SCROLL_VERTICAL ) )
		{
			if( bStartScrollDown == false )
			{
				if( !isScroll && y < this.height && ( deltaX == 0 || Math.abs( deltaY ) / Math.abs( deltaX ) > 1 ) )
				{
					this.setTag( deltaY );
					isScroll = true;
					viewParent.onCtrlEvent( this , HotSeat3D.MSG_DOCKGROUP_SCROLL_UP );
				}
			}
			else
			{
				if( ( deltaX == 0 || Math.abs( deltaY ) / Math.abs( deltaX ) > 1 ) )
				{
					this.setTag( scaleY );
					isScroll = true;
					viewParent.onCtrlEvent( this , HotSeat3D.MSG_DOCKGROUP_SCROLL_DOWN );
				}
			}
		}
		else if( originalTouch && ( Desktop3DListener.d3d.mScrollTempDir == Desktop3D.SCROLL_HORIZ0TAL ) )
		{
			if( DefaultLayout.enable_quick_search )
			{
				if( !iLoongLauncher.finishLoad )
				{
					SendMsgToAndroid.sendToastMsg( iLoongLauncher.getInstance().getString( RR.string.qs_loading_tip_text ) );
					return true;
				}
				if( !QSearchGroup.canShow )
				{
					return true;
				}
				if( Desktop3DListener.root.qSearchGroup != null )
				{
					Desktop3DListener.root.qSearchGroup.doAnimPrepare();
					Desktop3DListener.root.qSearchGroup.show();
				}
				else
				{
					return true;
				}
				if( deltaX > 0 )
				{
					if( Root3D.hotseatBar.x < 0 )
					{
						backgroud.setPosition( backgroud.x - deltaX , 0 );
						Root3D.hotseatBar.setPosition( Root3D.hotseatBar.x + deltaX , 0 );
						Desktop3DListener.root.qSearchGroup.getSearchEditTextGroup().setPosition( Desktop3DListener.root.qSearchGroup.getSearchEditTextGroup().x + deltaX , 0 );
					}
				}
				else
				{
					backgroud.setPosition( backgroud.x - deltaX , 0 );
					Root3D.hotseatBar.setPosition( Root3D.hotseatBar.x + deltaX , 0 );
					Desktop3DListener.root.qSearchGroup.getSearchEditTextGroup().setPosition( Desktop3DListener.root.qSearchGroup.getSearchEditTextGroup().x + deltaX , 0 );
				}
			}
		}
		return true;
	}
	
	private void initDock()
	{
		scaleY = 0;
		isScroll = false;
		isFling = false;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 1 )
		{
			return false;
		}
		originalTouch = false;
		releaseFocus();
		if( DefaultLayout.enable_quick_search && Desktop3DListener.root != null && Desktop3DListener.root.qSearchGroup != null )
		{
			if( ( Root3D.hotseatBar.x <= -UtilsBase.getScreenWidth() / 2 || mVelocityX < -1000 && Root3D.hotseatBar.x < 0 ) )
			{
				if( Desktop3DListener.root.qSearchGroup.mLoaded )
				{
					Desktop3DListener.root.qSearchGroup.startQuickSearchAnimIn();
				}
				else
				{
					int marginTop = (int)( ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() ) / 2 );
					SendMsgToAndroid.showCustomDialog( (int)( Utils3D.getScreenWidth() - 40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 , marginTop );
					Bitmap bitmap = ThemeManager.getInstance().getBitmap( "theme/desktopAction/black.png" );
					Bitmap b = Tools.resizeBitmap( bitmap , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
					dialogBg = new ImageView3D( "dialogBg" , b );
					Desktop3DListener.root.addView( dialogBg );
					dialogBg.bringToFront();
					b.recycle();
					Desktop3DListener.root.addView( QSearchGroup.mSearchEditTextGroup );
					dialogBg.color.a = 0;
					DialogFadeIn = dialogBg.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 1f , 1 , 0 , 0 );
					Desktop3DListener.root.qSearchGroup.searchEditAni();
				}
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EntertheQuickFind" );
			}
			else
			{
				Desktop3DListener.root.qSearchGroup.startResetAnim( true );
			}
		}
		mVelocityX = 0;
		if( bStartScrollDown && isScroll == true )
		{
			this.setTag( -2.0f );
			bStartScrollDown = false;
			initDock();
			return true;
		}
		{
			initDock();
			return super.onTouchUp( x , y , pointer );
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( this.getParent() instanceof HotSeat3D && false == ( (HotSeat3D)this.getParent() ).getHotseatClickPermition() )
		{
			return true;
		}
		return super.onClick( x , y );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( !Workspace3D.getInstance().getDragList().isEmpty() )
		{
			return true;
		}
		if( DefaultLayout.WorkspaceActionGuide )
		{
			if( ActionHolder.getInstance() != null && (
					ActionData.getInstance().checkValidity() == ActionData.VALIDITY_LOCK_DOCKBAR
					//为了屏蔽快速搜索的引导手势会翻转dockbar
					||ActionData.getInstance().checkValidity() == ActionData.VALIDITY_LOCK_HOTMAINGROUP))
				return true;
		}
		if( iLoongLauncher.isShowNews )
		{
			return false;
		}
		releaseFocus();
		SendMsgToAndroid.sendHideWorkspaceMsg();
		boolean ret = false;
		if( !dragAble )
		{
			if( mFocusGridView == mShortcutView )
			{
				mFocusGridView.setAutoDrag( false );
			}
			// viewParent.onCtrlEvent(this, HotSeat3D.MSG_LONGCLICK_INAPPLIST);
		}
		ret = super.onLongClick( x , y );
		if( ret )
		{
			if( mFocusGridView.getFocusView() != null )
			{
				mFocusGridView.getFocusView().color.a = 1f;
			}
		}
		if( !dragAble )
		{
			if( mFocusGridView == mShortcutView )
			{
				mFocusGridView.setAutoDrag( true );
			}
		}
		return ret;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
	
	public View3D getFocusView()
	{
		return mFocusGridView.getFocusView();
	}
	
	public void addSingleItem(
			View3D view ,
			int index )
	{
		if( view instanceof IconBase3D )
		{
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			info.screen = index;
			mFocusGridView.getViewPos( view , IconPoint );
			info.x = (int)IconPoint.x;
			info.y = (int)IconPoint.y;
			// Log.v("HotObj", "additems info.screen=" + info.screen);
			info.angle = HotSeat3D.TYPE_WIDGET;
			Root3D.addOrMoveDB( info , LauncherSettings.Favorites.CONTAINER_HOTSEAT );
			if( view instanceof Icon3D )
			{
				// Icon3D iconView = (Icon3D)view;
				// iconView.setItemInfo(iconView.getItemInfo());
			}
		}
		if( index != -1 && index < mFocusGridView.getChildCount() )
		{
			mFocusGridView.addItem( view , index );
		}
		else
		{
			mFocusGridView.addItem( view );
		}
		shortcutCountUpdate();
		mFocusGridView.updateItemInfoDB();
	}
	
	private void createFolderItem(
			ArrayList<View3D> child_list ,
			int index )
	{
		mFocusGridView.getEmptyCellIndexPos( index , IconPoint , true );
		UserFolderInfo folderInfo = iLoongLauncher.getInstance().addHotSeatFolder( (int)IconPoint.x , (int)IconPoint.y , index );
		FolderIcon3D folderIcon3D = new FolderIcon3D( "FolderIcon3DHot" , folderInfo );
		if( DefaultLayout.hotseat_hide_title )
		{
			folderIcon3D.folder_front.region.setTexture( FolderIcon3D.folderFrontWithoutShadowRegion.getTexture() );
			folderIcon3D.changeFolderFrontRegion( true );
		}
		if( index < mFocusGridView.getChildCount() )
		{
			mFocusGridView.addItem( folderIcon3D , index );
		}
		else
		{
			mFocusGridView.addItem( folderIcon3D );
		}
		folderIcon3D.onDrop( child_list , 0 , 0 );
		folderIcon3D.setOnDropFalse();
	}
	
	public void backtoOrig(
			ArrayList<View3D> child_list )
	{
		ItemInfo itemInfo;
		View3D findView = null;
		for( int i = 0 ; i < child_list.size() ; i++ )
		{
			View3D view = child_list.get( i );
			if( view instanceof FolderIcon3D )
			{
				( (FolderIcon3D)view ).setLongClick( false );
			}
			view.stopTween();
			if( ( view instanceof IconBase3D ) == false )
				return;
			itemInfo = ( (IconBase3D)view ).getItemInfo();
			if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
			{
				if( DefaultLayout.hotseat_hide_title )
				{
					if( view instanceof Icon3D )
					{
						Utils3D.changeTextureRegion( view , Utils3D.getIconBmpHeight() , true );
					}
					else if( view instanceof FolderIcon3D )
					{
						( (FolderIcon3D)view ).changeFolderFrontRegion( true );
					}
				}
				addSingleItem( view , itemInfo.screen );
			}
			else if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
			{
				// workspace.addInCurrenScreen(view, itemInfo.x, itemInfo.y);
				workspace.addBackInScreen( view , itemInfo.x , itemInfo.y );
				view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , itemInfo.x , itemInfo.y , 0 );
			}
			else if( itemInfo.container >= 0 )
			{
				//xiatian add start	//for mainmenu sort by user
				if( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_sort_by_user_fun && itemInfo.container >= LauncherSettings.Favorites.CONTAINER_APPLIST )
					return;
				//xiatian add end
				UserFolderInfo folderInfo = iLoongLauncher.getInstance().getFolderInfo( itemInfo.container );
				if( folderInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
				{
					findView = mFocusGridView.findExistView( folderInfo.screen );
				}
				else if( folderInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
				{
					findView = workspace.getViewByItemInfo( folderInfo );
				}
				if( findView != null && findView instanceof FolderIcon3D )
				{
					( (FolderIcon3D)findView ).onDrop( child_list , 0 , 0 );
				}
			}
		}
	}
	
	public void dealDockGroupDropOver(
			ArrayList<View3D> child_list ,
			float x ,
			float y )
	{

		if(mFocusGridView.getDockBarCount()>=CELL_MAX_NUM_X||child_list.size()>1)
			return ;
	
		outView = child_list.get(0);


		int index = mFocusGridView.getIndex( (int)x , (int)y );
		if( index < 0 )
		{
			removeVirtueFolderIcon();
			return;
		}
		View3D findView = mFocusGridView.findExistView( index );
		if( findView == null )
		{
			removeVirtueFolderIcon();
			mFocusGridView.MyMoveInTween( x , y );
			return;
		}
		if( findView instanceof FolderIcon3D )
		{
			removeVirtueFolderIcon();
			mFocusGridView.MyMoveInTween( x , y );
			return;
		}
		if( child_list.size() == 1 && child_list.get( 0 ) instanceof FolderIcon3D )
		{
			removeVirtueFolderIcon();
			mFocusGridView.MyMoveInTween( x , y );
			return;
		}
		mFocusGridView.getIndexPos( index , IconPoint );
		cellMakeFolder( IconPoint.x , IconPoint.y , true );
		mFocusGridView.MyMoveInTween( x , y );
	}
	
	public void addItems(
			ArrayList<View3D> child_list )
	{
		if( child_list.size() <= 0 )
		{
			return;
		}
		View3D view = child_list.get( child_list.size() - 1 );
		mFocusGridView.calcCoordinate( view );
		int index = mFocusGridView.getIndex( (int)( view.x + view.width / 2 ) , (int)mFocusGridView.height / 2 );
		// Log.v("HotObj", "addItems index ="+index);
		removeVirtueFolderIcon();
		//		if( index == -1 )
		//		{
		//			backtoOrig( child_list );
		//			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.add_failure ) );
		//			return;
		//		}
		//		if( index == -2 )
		//		{
		//			backtoOrig( child_list );
		//			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.deny_add_on_mainmenu ) );
		//			return;
		//		}
		//		View3D findView = mFocusGridView.findExistView( index );
		if( mFocusGridView.getChildCount() >= 5 )
		{
			if( index == -1 )
			{
				backtoOrig( child_list );
				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.add_failure ) );
				return;
			}
		}
		else
		{
			if( index == -1 )
			{
				backtoOrig( child_list );
				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.add_failure ) );
				return;
			}
		}
		View3D findView = mFocusGridView.findExistView( index );
		if( mFocusGridView.getChildCount() >= 5 )
		{
			if( findView == null )
			{
				backtoOrig( child_list );
				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.add_failure ) );
				return;
			}
		}
		if( findView != null )
		{
			/*
			 * 这里表示拖到的地方已经被占用�? 需要创建文件夹
			 */
			if( view instanceof FolderIcon3D && child_list.size() == 1 )
			{
				backtoOrig( child_list );
				// SendMsgToAndroid.sendOurToastMsg("文件");
			}
			else
			{
				if( findView instanceof FolderIcon3D )
				{
					( (FolderIcon3D)findView ).onDrop( child_list , 0 , 0 );
					( (FolderIcon3D)findView ).setOnDropFalse();
				}
				else
				{
					if( DefaultLayout.hotseat_disable_make_folder )
						backtoOrig( child_list );
					else
					{
						child_list.add( findView );
						mFocusGridView.removeView( findView );
						createFolderItem( child_list , index );
					}
				}
			}
			return;
		}
		else
		{
			if( child_list.size() == 1 )
			{
				/* 直接插入 */
				if( DefaultLayout.hotseat_hide_title )
				{
					if( view instanceof Icon3D )
					{
						//teapotXu deleted start: 去除当拖动其他图标到底边栏时，底边栏根据类别替换成默认图标的功能，
						//						if (ThemeManager.getInstance()
						//								.getCurrentThemeDescription().mSystem) {
						//							replaceIcon((Icon3D) view);
						//						}
						//teapotXu deleted end
						Icon3D icon = (Icon3D)view;
						ShortcutInfo info = (ShortcutInfo)icon.getItemInfo();
						String bitmapPath = info.getBitmapPath();
						if( bitmapPath != null )
						{
						}
						Utils3D.changeTextureRegion( child_list , Utils3D.getIconBmpHeight() , true );
					}
					else if( view instanceof FolderIcon3D )
					{
						( (FolderIcon3D)view ).changeFolderFrontRegion( true );
					}
				}
				addSingleItem( view , index );
			}
			else
			{
				// Utils3D.changeTextureRegion(child_list,Utils3D.getIconBmpHeight(),true);
				createFolderItem( child_list , index );
			}
		}
	}
	
	public static String getInfoName(
			ShortcutInfo info )
	{
		ComponentName cmp = info.intent.getComponent();
		String name = "coco";
		if( cmp != null )
			name = cmp.toString();
		else
		{
			String temp = null;
			if( info.intent.getAction() != null )
			{
				name = info.intent.getAction().toString();
			}
			if( info.intent.getType() != null )
			{
				temp = info.intent.getType().toString();
				name += temp;
			}
			if( info.intent.getDataString() != null )
			{
				temp = info.intent.getDataString();
				name += temp;
			}
		}
		name += info.itemType;
		info.title = info.title.subSequence( 0 , info.title.length() );
		name += info.title.toString().replaceAll( " " , "" ).replaceAll( " " , "" ).trim();
		return name;
	}
	
	private Uri getDefaultBrowserUri()
	{
		String url = iLoongLauncher.getInstance().getString( RR.string.default_browser_url );
		if( url.indexOf( "{CID}" ) != -1 )
		{
			url = url.replace( "{CID}" , "android-google" );
		}
		return Uri.parse( url );
	}
	
	public void replaceIntent(
			ShortcutInfo sInfo )
	{
		int iconFlag = getIntentType( sInfo.intent );
		Intent replace_intent = null;
		if( iconFlag != -1 )
		{
			for( int i = 0 ; i < iLoongLauncher.getInstance().getHotSeatLength() ; i++ )
			{
				Intent intent = iLoongLauncher.getInstance().getHotSeatIntent( i );
				if( getIntentType( intent ) == iconFlag )
				{
					replace_intent = intent;
					break;
				}
			}
		}
		if( iconFlag == 0 )
		{
			try
			{
				Log.e( "test" , "before :" + getInfoName( sInfo ) );
				if( replace_intent != null )
				{
					sInfo.intent = replace_intent;
				}
				else
				{
					sInfo.intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.DIAL;end" , 0 );
				}
				Log.e( "test" , "after :" + getInfoName( sInfo ) );
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
			}
			catch( URISyntaxException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 电话
		}
		else if( iconFlag == 1 )
		{
			// 联系人
			try
			{
				Log.e( "test" , "before :" + getInfoName( sInfo ) );
				if( replace_intent != null )
				{
					sInfo.intent = replace_intent;
				}
				else
				{
					sInfo.intent = Intent.parseUri( "intent:content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;end" , 0 );
				}
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
				Log.e( "test" , "after :" + getInfoName( sInfo ) );
			}
			catch( URISyntaxException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if( iconFlag == 2 )
		{
			// 短信
			try
			{
				if( replace_intent != null )
				{
					sInfo.intent = replace_intent;
				}
				else
				{
					sInfo.intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;end" , 0 );
				}
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
			}
			catch( URISyntaxException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if( iconFlag == 3 )
		{
			String customUri = DefaultLayout.defaultUri;
			String defaultUri;// = getString(RR.string.default_browser_url);
			if( customUri != null )
			{
				defaultUri = customUri;
			}
			else
			{
				defaultUri = DefaultLayout.googleHomePage;// 用百度代替空白网�?about:blank"否则欧鹏浏览器无法识�?
			}
			Intent intent = new Intent( Intent.ACTION_VIEW , ( ( defaultUri != null ) ? Uri.parse( defaultUri ) : getDefaultBrowserUri() ) ).addCategory( Intent.CATEGORY_BROWSABLE );
			if( DefaultLayout.default_explorer != null )
			{
				intent.setPackage( DefaultLayout.default_explorer );
				List<ResolveInfo> allMatches = iLoongLauncher.getInstance().getPackageManager().queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
				if( allMatches == null || allMatches.size() == 0 )
				{
					intent.setPackage( null );
				}
			}
			if( replace_intent != null )
			{
				sInfo.intent = replace_intent;
			}
			else
			{
				sInfo.intent = intent;
			}
			// sInfo.intent = new Intent("android.intent.action.VIEW");
			// sInfo.intent.addCategory("android.intent.category.DEFAULT");
			// sInfo.intent.addCategory("android.intent.category.BROWSABLE");
			// Uri uri = Uri.parse("http://www.google.cn");
			// sInfo.intent.setDataAndType(uri, null);
			sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
		}
	}
	
	public void replaceIcon(
			Icon3D icon )
	{
		ShortcutInfo sInfo = (ShortcutInfo)icon.getItemInfo();
		int iconFlag = getIntentType( sInfo.intent );
		Intent intent_bak = new Intent( sInfo.intent );
		int item_type_bak = sInfo.itemType;
		Intent replace_intent = null;
		if( iconFlag != -1 )
		{
			for( int i = 0 ; i < iLoongLauncher.getInstance().getHotSeatLength() ; i++ )
			{
				Intent intent = iLoongLauncher.getInstance().getHotSeatIntent( i );
				if( getIntentType( intent ) == iconFlag )
				{
					replace_intent = intent;
					break;
				}
			}
		}
		if( iconFlag == 0 )
		{
			try
			{
				Log.e( "test" , "before :" + getInfoName( sInfo ) );
				if( replace_intent != null )
				{
					sInfo.intent = replace_intent;
				}
				else
				{
					sInfo.intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.DIAL;end" , 0 );
				}
				Log.e( "test" , "after :" + getInfoName( sInfo ) );
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
				icon.region.setRegion( R3D.findRegion( getInfoName( sInfo ) ) );
			}
			catch( URISyntaxException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 电话
		}
		else if( iconFlag == 1 )
		{
			// 联系人
			try
			{
				Log.e( "test" , "before :" + getInfoName( sInfo ) );
				if( replace_intent != null )
				{
					sInfo.intent = replace_intent;
				}
				else
				{
					sInfo.intent = Intent.parseUri( "intent:content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;end" , 0 );
				}
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
				Log.e( "test" , "after :" + getInfoName( sInfo ) );
				icon.region.setRegion( R3D.findRegion( getInfoName( sInfo ) ) );
			}
			catch( URISyntaxException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if( iconFlag == 2 )
		{
			// 短信
			try
			{
				if( replace_intent != null )
				{
					sInfo.intent = replace_intent;
				}
				else
				{
					sInfo.intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;end" , 0 );
				}
				sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
				icon.region.setRegion( R3D.findRegion( getInfoName( sInfo ) ) );
			}
			catch( URISyntaxException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if( iconFlag == 3 )
		{
			String customUri = DefaultLayout.defaultUri;
			String defaultUri;// = getString(RR.string.default_browser_url);
			if( customUri != null )
			{
				defaultUri = customUri;
			}
			else
			{
				defaultUri = DefaultLayout.googleHomePage;// 用百度代替空白网�?about:blank"否则欧鹏浏览器无法识�?
			}
			Intent intent = new Intent( Intent.ACTION_VIEW , ( ( defaultUri != null ) ? Uri.parse( defaultUri ) : getDefaultBrowserUri() ) ).addCategory( Intent.CATEGORY_BROWSABLE );
			if( DefaultLayout.default_explorer != null )
			{
				intent.setPackage( DefaultLayout.default_explorer );
				List<ResolveInfo> allMatches = iLoongLauncher.getInstance().getPackageManager().queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
				if( allMatches == null || allMatches.size() == 0 )
				{
					intent.setPackage( null );
				}
			}
			if( replace_intent != null )
			{
				sInfo.intent = replace_intent;
			}
			else
			{
				sInfo.intent = intent;
			}
			// sInfo.intent = new Intent("android.intent.action.VIEW");
			// sInfo.intent.addCategory("android.intent.category.DEFAULT");
			// sInfo.intent.addCategory("android.intent.category.BROWSABLE");
			// Uri uri = Uri.parse("http://www.google.cn");
			// sInfo.intent.setDataAndType(uri, null);
			sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
			icon.region.setRegion( R3D.findRegion( getInfoName( sInfo ) ) );
		}
		if( iconFlag != -1 )
		{
			//need restore the intent
			if( intent_bak != null )
				sInfo.intent = intent_bak;
			sInfo.itemType = item_type_bak;
		}
	}
	
	public void bindItem(
			View3D view ,
			int index )
	{
		//teapotXu deleted: 适配default_layout.xml中配置hoseat功能，此时不需要更换ICON--20130913
		//if (view instanceof Icon3D) {
		//	if (ThemeManager.getInstance().getCurrentThemeDescription().mSystem) {
		//		replaceIcon((Icon3D) view);
		//	}
		//}
		//teapotXu deleted end
		//		if( index == R3D.hot_dock_icon_number - 1 )
		//		{
		//			return;
		//		}
		mShortcutView.addItem( view );
	}
	
	public void bindItem(
			View3D view )
	{
		mShortcutView.addItem( view );
	}
	
	public void removeItem(
			View3D view )
	{
		mShortcutView.removeView( view );
	}
	
	public void bindItem(
			FolderIcon3D view )
	{
		// View3D findView=mFocusGridView.findExistView(view.mInfo.screen);
		// if (findView==null)
		// {
		// mShortcutView.addFolder(view);
		// }
	}
	
	public void show()
	{
		this.visible = true;
		this.touchable = true;
		mFocusGridView.show();
	}
	
	public void hide()
	{
		this.visible = false;
		this.touchable = false;
		mFocusGridView.hide();
	}
	
	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha) {
	// // TODO Auto-generated method stub
	// super.draw(batch, parentAlpha);
	//
	// if (isScrollEnd()) {
	// // if (flingTween != null && !flingTween.isFinished()) {
	// // mFocusGridView.stopTween();
	// // flingTween = null;
	// // }
	// if (!mFocusGridView.isAutoMove)
	// mFocusGridView.setUser(mFocusGridView.getUser() / 1.5f);
	// }
	//
	// if (mFocusGridView.isAutoMove) {
	// if (mFocusGridView.isScrollToEnd(-(int)mFocusGridView.getUser())) {
	// mFocusGridView.x += mFocusGridView.getUser();
	// mFocusGridView.getFocusView().x += -mFocusGridView.getUser();
	// }
	// } else {
	// if (Math.abs(mFocusGridView.getUser()) > 2) {
	// mFocusGridView.x += mFocusGridView.getUser();
	// } else {
	// // if (isFling) {
	// // startScrollTween();
	// // }
	// }
	// }
	// }
	private boolean isScrollEnd()
	{ // first or last in screen
		if( mFocusGridView.getChildCount() == 0 )
			return false;
		float mFocusViewX = mFocusGridView.x;
		float mFocusViewWidth = mFocusGridView.getEffectiveWidth();
		if( mFocusViewX + mFocusViewWidth < width || mFocusViewX > 0 )
		{
			return true;
		}
		return false;
	}
	
	public void onDragOverLeave()
	{
		removeVirtueFolderIcon();
		shortcutCountUpdate();
	}
	
	private void dealFolderIconMove()
	{
		View3D findView = (View3D)mFocusGridView.getTag();
		//View3D focusView = mFocusGridView.getFocusView();
		int index = ( (IconBase3D)findView ).getItemInfo().screen;
		mFocusGridView.getIndexPos( index , IconPoint );
		if( !( findView instanceof FolderIcon3D ) )
			cellMakeFolder( IconPoint.x , IconPoint.y , true );
	}
	
	private void dealCreateFolder()
	{
		View3D findView = (View3D)mFocusGridView.getTag();
		View3D focusView = mFocusGridView.getFocusView();
		removeVirtueFolderIcon();
		if( findView != null && focusView != null )
		{
			int index = ( (Icon3D)findView ).getItemInfo().screen;
			mFocusGridView.getIndexPos( index , IconPoint );
			ArrayList<View3D> child_list = new ArrayList<View3D>();
			child_list.add( findView );
			child_list.add( focusView );
			mFocusGridView.removeView( findView );
			mFocusGridView.removeView( focusView );
			createFolderItem( child_list , index );
			mFocusGridView.setCellCount( mFocusGridView.getChildCount() , 1 );
		}
		// cellMakeFolder(IconPoint.x, IconPoint.y, true);
	}
	
	private void dealMergeFolder()
	{
		FolderIcon3D findView = (FolderIcon3D)mFocusGridView.getTag();
		View3D focusView = mFocusGridView.getFocusView();
		removeVirtueFolderIcon();
		// int index = ((IconBase3D)findView).getItemInfo().screen;
		// mFocusGridView.getIndexPos(index,IconPoint);
		ArrayList<View3D> child_list = new ArrayList<View3D>();
		child_list.add( focusView );
		mFocusGridView.removeView( focusView );
		// createFolderItem(child_list,index);
		findView.onDrop( child_list , 0 , 0 );
		findView.setOnDropFalse();
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		// TODO Auto-generated method stub
		if( sender instanceof HotGridView3D )
		{
			HotGridView3D view = (HotGridView3D)sender;
			switch( event_id )
			{
				case HotGridView3D.MSG_VIEW_CREATE_FOLDER:
					dealCreateFolder();
					return true;
				case HotGridView3D.MSG_VIEW_MERGE_FOLDER:
					dealMergeFolder();
					return true;
				case HotGridView3D.MSG_VIEW_START_MAIN:
					return viewParent.onCtrlEvent( this , HotSeat3D.MSG_VIEW_START_MAIN );
				case HotGridView3D.MSG_VIEW_OUTREGION:
					return viewParent.onCtrlEvent( this , HotSeat3D.MSG_ON_DROP );
				case HotGridView3D.MSG_VIEW_MOVED:
					if( view == mShortcutView )
					{
						if( mShortcutView.getDragState() == HotGridView3D.State_ChangePosition )
						{
							if( folder_icon != null && folder_icon.getVisible() )
							{
								FolderSmallAnim( 0.3f , folder_icon );
							}
							else
							{
								removeVirtueFolderIcon();
							}
						}
						else
						{
							dealFolderIconMove();
						}
					}
					return false;
					//				case HotGridView3D.MSG_VIEW_HIDE_MAIN:
					//					if( DefaultLayout.enable_takein_workspace_by_longclick )
					//					{
					//						return viewParent.onCtrlEvent( this , HotSeat3D.MSG_VIEW_HIDE_MAIN );
					//					}
					//					else
					//					{
					//						break;
					//					}
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	public int getShortcutCount()
	{
		// TODO Auto-generated method stub
		return mFocusGridView.getChildCount();
	}
	
	public HotGridView3D getShortcutGridview()
	{
		return mShortcutView;
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( Gdx.graphics.getDensity() > 1 )
		{
			Group.toChildCoordinates( this , x , y , point );
			float offsetY = 20 * Gdx.graphics.getDensity();
			return( ( point.x >= 0 && point.x < width ) && point.y < ( height + offsetY ) );
		}
		else
		{
			return super.pointerInAbs( x , y );
		}
	}
	
	private Tween FolderLargeTween = null;
	private Tween FolderSmallTween = null;
	private Tween DialogFadeIn = null;
	
	private void FolderLargeAnim(
			float duration ,
			View3D view3D )
	{
		if( view3D == null || duration == 0 )
		{
			return;
		}
		if( FolderLargeTween != null )
		{
			return;
		}
		view3D.stopTween();
		view3D.setVisible( true );
		view3D.setScale( 0 , 0 );
		FolderLargeTween = view3D.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1.2f , 1.2f , 0 ).setUserData( view3D ).setCallback( this );
	}
	
	private void FolderSmallAnim(
			float duration ,
			View3D view3D )
	{
		if( view3D == null || duration == 0 )
		{
			return;
		}
		if( FolderSmallTween != null )
		{
			return;
		}
		view3D.stopTween();
		FolderSmallTween = view3D.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 0 , 0 , 0 ).setUserData( view3D ).setCallback( this );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == DialogFadeIn )
		{
			if( ActionHolder.getInstance() != null )
				ActionHolder.getInstance().onQuickSearchStarted();
		}
		if( FolderSmallTween != null && type == TweenCallback.COMPLETE )
		{
			View3D view3D = (View3D)source.getUserData();
			// Log.e("testFolder", "why FolderSmallTween onEvent me");
			if( view3D instanceof FolderIcon3D )
			{
				FolderIcon3D tempFolder = (FolderIcon3D)view3D;
				if( tempFolder.mInfo.contents.size() == 0 )
				{
					view3D.setVisible( false );
				}
			}
			FolderSmallTween.free();
			FolderSmallTween = null;
		}
		if( FolderLargeTween != null && type == TweenCallback.COMPLETE )
		{
			FolderLargeTween.free();
			FolderLargeTween = null;
		}
	}
	
	public void childPositonUpdate()
	{
		mFocusGridView.setCount( mFocusGridView.getChildCount() );
	}
	
	public void shortcutCountUpdate()
	{
		if( mFocusGridView.getChildCount() != mFocusGridView.getCellCountX() )
		{
			mFocusGridView.setCount( mFocusGridView.getCellCountX() );
		}
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		//		int index = mFocusGridView.getIndex((int) x, (int) y);
		//		// Log.v("HotObj", "dealDockGroupDropOver index ="+index);
		//		if (index < 0) {
		//			removeVirtueFolderIcon();
		////			mFocusGridView.MyMoveOutTween(index);
		//		}
		return super.onTouchDragged( x , y , pointer );
	}
	
	public void addToHotseatItemsList(
			View3D view )
	{
		mHotseatItems.add( view );
	}
	
	public void deleteFromHotseatItemsList(
			View3D view )
	{
		if( mHotseatItems.contains( view ) )
		{
			mHotseatItems.remove( view );
		}
	}
}
