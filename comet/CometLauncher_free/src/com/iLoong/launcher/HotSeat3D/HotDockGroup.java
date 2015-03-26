package com.iLoong.launcher.HotSeat3D;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class HotDockGroup extends NPageBase implements ClingManager.ClingTarget
{
	
	public HotSeat3D hotSeat3D;
	private final int SCROLL_UP = 0;
	private final int SCROLL_DOWN = 1;
	// private final int SCROLL_LEFT = 2;
	// private final int SCROLL_RIGHT = 3;
	public static final int MAX_ICON_NUM = 5;
	private final float VELOCITY_DIV = 30f;
	protected HotGridView3D mShortcutView;
	protected HotGridView3D mShortcutView1;
	protected HotGridView3D mShortcutView2;
	private HotGridView3D mFocusGridView;
	//	private HotGridView3D mFocusGridView1;
	//	private HotGridView3D mFocusGridView2;
	View3D folder_icon;
	private int scrollDir = -1;
	private boolean isFling = false;
	private boolean bStartScrollDown = false;
	private float scaleY = 0;
	// private Tween flingTween = null;
	Vector2 IconPoint = new Vector2();
	public boolean dragAble = true;
	private boolean isTurning = false;
	// private int curFolderPos=-1;
	private Workspace3D workspace;
	public int clingR;
	public int clingX;
	public int clingY;
	//teapotXu add start for hotseat's middle icon
	private View3D mHotSeatMiddleImgView;
	//teapotXu add end for hotseat's middle icon
	private List<ResolveInfo> hotDialIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotMmsIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotContactIntentMap = new ArrayList<ResolveInfo>();
	private List<ResolveInfo> hotBorwserIntentMap = new ArrayList<ResolveInfo>();
	private FolderIcon3D folder;
	
	// static Texture t = new Texture(Gdx.files.internal("bgtest.png"));
	public HotDockGroup(
			String name )
	{
		super( name );
	}
	
	//	public boolean isInShortcutList() {
	//		return mFocusGridView == mShortcutView;
	//	}
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
		float space_height = bmpHeight / 10;
		vector.x = ( hotDockGroupWidth - bmpWidth ) / 2;
		vector.y = padding_bottom + space_height;
		return vector;
	}
	
	//teapotXu add end for hotseat's middle icon
	public HotDockGroup(
			String name ,
			int width ,
			int height )
	{
		super( name );
		transform = true;
		// float gridViewHeight = height - SideBar.SIDEBAR_OPCITY_WIDTH;
		int padding = (int)( ( height - R3D.workspace_cell_height ) / 2 );
		if( padding < 0 )
		{
			padding = 0;
		}
		mShortcutView = new HotGridView3D( "shortcutview" , width , height , R3D.hot_dock_icon_number , 1 );
		mShortcutView.transform = true;
		mShortcutView.setPageNum( 0 );
		//		mShortcutView.setPadding(R3D.hot_grid_left_margin,
		//				R3D.hot_grid_right_margin, 0, R3D.hot_grid_bottom_margin);
		mShortcutView.setPadding( 0 , 0 , 0 , R3D.hot_grid_bottom_margin );
		mShortcutView.setOrigin( width / 2 , height / 2 );
		mShortcutView.setPosition( 0 , 0 );
		addPage( mShortcutView );
		mShortcutView1 = new HotGridView3D( "shortcutview1" , width , height , R3D.hot_dock_icon_number , 1 );
		mShortcutView1.setPageNum( 1 );
		mShortcutView1.transform = true;
		mShortcutView1.setPadding( 0 , 0 , 0 , R3D.hot_grid_bottom_margin );
		mShortcutView1.setOrigin( width / 2 , height / 2 );
		mShortcutView1.setPosition( 0 , 0 );
		addPage( mShortcutView1 );
		mShortcutView2 = new HotGridView3D( "shortcutview2" , width , height , R3D.hot_dock_icon_number , 1 );
		mShortcutView2.setPageNum( 2 );
		mShortcutView2.transform = true;
		mShortcutView2.setPadding( 0 , 0 , 0 , R3D.hot_grid_bottom_margin );
		mShortcutView2.setOrigin( width / 2 , height / 2 );
		mShortcutView2.setPosition( 0 , 0 );
		addPage( mShortcutView2 );
		setVisible( HotSeat3D.TYPE_WIDGET );
		setSize( width , height );
		this.setOrigin( width / 2 , height / 2 );
		if( getCurrentPage() == 0 )
		{
			mFocusGridView = mShortcutView;
		}
		else if( getCurrentPage() == 1 )
		{
			mFocusGridView = mShortcutView1;
		}
		else if( getCurrentPage() == 2 )
		{
			mFocusGridView = mShortcutView2;
		}
		//		mFocusGridView1 = mShortcutView1;
		//		mFocusGridView2 = mShortcutView2;
		this.needXRotation = false;
		setWholePageList();
		setEffectType( 2 );
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
	
	private View3D createVirturFolder()
	{
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = R3D.folder3D_name;
		folderInfo.screen = workspace.getCurrentScreen();
		folderInfo.x = 0;
		folderInfo.y = 0;
		folderInfo.cellX = getCurrentPage();
		FolderIcon3D folderIcon3D = new FolderIcon3D( "FolderIcon3DView" , folderInfo );
		folderIcon3D.changeFolderFrontRegion( true );
		Log.v( "Folder" , "createVirturFolder" );
		return folderIcon3D;
	}
	
	public void removeVirtueFolderIcon()
	{
		if( folder_icon != null )
		{
			Log.v( "ondrop" , "removeVirtueFolderIcon" );
			this.removeView( folder_icon );
			folder_icon = null;
		}
	}
	
	@Override
	public void removeAllViews()
	{
		mFocusGridView.removeAllViews();
		//		mFocusGridView1.removeAllViews();
		//		mFocusGridView2.removeAllViews();
	}
	
	private void cellMakeFolder(
			float posx ,
			float posy ,
			boolean preview )
	{
		Log.v( "Folder" , "cellMakeFolder" );
		//		removeVirtueFolderIcon();
		if( folder_icon == null )
		{
			Log.v( "ondrop" , "cellMakeFolder" );
			folder_icon = createVirturFolder();
			folder_icon.setVisible( false );
		}
		folder_icon.x = posx;
		folder_icon.y = posy;
		//		if (preview) {
		//			Color color = folder_icon.getColor();
		//			color.a = 0.5f;
		//			folder_icon.setColor(color);
		//		}
		//		folder_icon.show();
		//		addViewAt(getChildCount(), folder_icon);
		super.addViewAt( 0 , folder_icon );
		if( mFocusGridView.getTag() instanceof FolderIcon3D )
		{
			Log.v( "ondrop" , "cellMakeFolder11111111" );
			return;
		}
		if( !folder_icon.getVisible() )
			FolderLargeAnim( 0.3f , folder_icon );
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
		// TODO Auto-generated method stub
		Log.v( "hotgridview" , "hotdockgroup onTouchDown" );
		int pageNum = getCurrentPage();
		if( pointer == 1 )
		{
			return false;
		}
		this.doubleClick = Desktop3D.doubleClick;
		Desktop3D.doubleClick = true;
		mFocusGridView.stopTween();
		mFocusGridView.setUser( 0 );
		//		mFocusGridView1.stopTween();
		//		mFocusGridView1.setUser(0);
		//		mFocusGridView2.stopTween();
		//		mFocusGridView2.setUser(0);
		isFling = false;
		super.onTouchDown( x , y , pointer );
		requestFocus();
		return true;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		Log.v( "hotgridview" , "hotdockgroup scroll" );
		if( !HotSeat3D.menuAnimalComplete )
		{
			Log.v( "hotgridview" , "hotdockgroup fail" );
			return true;
		}
		// TODO Auto-generated method stub
		if( mFocusGridView.lastTouchedChild != null && ( mFocusGridView.lastTouchedChild instanceof Icon3D || mFocusGridView.lastTouchedChild instanceof FolderIcon3D ) )
		{
			//Log.e("test", "focus:" + mFocusGridView.getFocusView());
			mFocusGridView.lastTouchedChild.color.a = 1f;
		}
		//requestFocus();
		scaleY += 2 * deltaY / this.height;
		// Log.v("hotobj", " hotdock scroll name:" + this.name + "  isScroll:" +
		// isScroll + " deltaY:"
		// + deltaY+" deltaX:" +deltaX + " y=" +y+ " x=" +x);
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
		// Log.e("hotobj", " scaleY="+scaleY);
		//		if (!isScroll && super.scroll(x, y, deltaX, deltaY)) {
		//			Log.v("hotobj", " hotdock scroll return true");
		//			return true;
		//		}
		if( !isTurning )
		{
			if( y < this.height && ( deltaY < 0 && Math.abs( deltaY ) / Math.abs( deltaX ) > 0.6 ) )
			{
				if( iLoongLauncher.getInstance().getD3dListener().getRoot().getDesktopEdit().isAnimation() )
				{
					return false;
				}
				Log.v( "hotgridview" , "hotdockgroup scroll1111" + bStartScrollDown );
				DockbarObjcetGroup.isStart = true;
				releaseFocus();
				HotSeat3D.startModelAnimal( 0.5F );
				return true;
			}
			//			if (bStartScrollDown == false) {
			//				if (!isScroll && y < this.height
			//						&& (deltaX == 0 || Math.abs(deltaY) / Math.abs(deltaX) > 0.6)) {
			//					if (iLoongLauncher.getInstance().getD3dListener().getRoot().getDesktopEdit().isAnimation()) {
			//						return false;
			//					}
			//					Log.v("hotgridview", "hotdockgroup scroll1111"+bStartScrollDown);
			//					this.setTag(deltaY);
			//					isScroll = true;
			//					viewParent.onCtrlEvent(this, HotSeat3D.MSG_DOCKGROUP_SCROLL_UP);
			//					return true;
			//				}
			//			} else {
			//				// Log.e("hotobj", " MSG_DOCKGROUP_SCROLL_DOWN=");
			//				if (!isScroll &&(deltaX == 0 || Math.abs(deltaY) / Math.abs(deltaX) > 1)) {
			//					if (iLoongLauncher.getInstance().getD3dListener().getRoot().getDesktopEdit().isAnimation()) {
			//						return false;
			//					}
			//					Log.v("hotgridview", "hotdockgroup scroll22222"+bStartScrollDown);
			//					this.setTag(scaleY);
			//					isScroll = true;
			//					viewParent.onCtrlEvent(this,
			//							HotSeat3D.MSG_DOCKGROUP_SCROLL_DOWN);
			//					return true;
			//				}
			//			}
		}
		isTurning = true;
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	private void initDock()
	{
		scaleY = 0;
		isFling = false;
	}
	
	boolean doubleClick = false;
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		Log.v( "hotgridview" , "hotdockgroup onTouchUp" );
		if( pointer == 1 )
		{
			return false;
		}
		//		
		//		if(!tweenFinish){
		//	         
		//            return true;
		//        }
		//		tweenFinish =false;
		Desktop3D.doubleClick = doubleClick;
		releaseFocus();
		if( bStartScrollDown )
		{
			//			this.setTag(-2.0f);
			bStartScrollDown = false;
		}
		if( DockbarObjcetGroup.isStart )
		{
			return true;
		}
		initDock();
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		mFocusGridView = (HotGridView3D)getCurrentView();
		Log.v( "hotgridview" , "onLongClick" );
		releaseFocus();
		SendMsgToAndroid.sendHideWorkspaceMsg();
		boolean ret = false;
		if( !dragAble )
		{
			if( mFocusGridView == getCurrentView() )
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
			if( mFocusGridView == getCurrentView() )
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
		// TODO Auto-generated method stub
		return true;
	}
	
	public View3D getFocusView()
	{
		return mFocusGridView.getFocusView();
	}
	
	private void addSingleItem(
			View3D view ,
			int page ,
			int index )
	{
		//		mFocusGridView = (HotGridView3D) view_list.get(page);
		if( view instanceof IconBase3D )
		{
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			info.screen = index;
			mFocusGridView.getViewPos( view , IconPoint );
			info.x = (int)IconPoint.x;
			info.y = (int)IconPoint.y;
			// Log.v("HotObj", "additems info.screen=" + info.screen);
			info.angle = HotSeat3D.TYPE_WIDGET;
			info.cellX = page;
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
		//		childPositonUpdate();
		shortcutCountUpdate();
		mFocusGridView.updateItemInfoDB();
	}
	
	private void createFolderItem(
			ArrayList<View3D> child_list ,
			int index )
	{
		mFocusGridView.getEmptyCellIndexPos( index , IconPoint , true );
		UserFolderInfo folderInfo = iLoongLauncher.getInstance().addHotSeatFolder( (int)IconPoint.x , (int)IconPoint.y , index );
		folderInfo.cellX = getCurrentPage();
		FolderIcon3D folderIcon3D = new FolderIcon3D( "FolderIcon3DHot" , folderInfo );
		if( DefaultLayout.hotseat_hide_title )
		{
			folderIcon3D.changeFolderFrontRegion( true );
		}
		ItemInfo info = ( (IconBase3D)folderIcon3D ).getItemInfo();
		info.screen = index;
		( (IconBase3D)folderIcon3D ).setItemInfo( info );
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
		mFocusGridView.updateItemInfoDB();
		Log.v( "Folder" , "createFolderItem" );
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
				addSingleItem( view , getCurrentPage() , itemInfo.screen );
			}
			else if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
			{
				// workspace.addInCurrenScreen(view, itemInfo.x, itemInfo.y);
				workspace.addBackInScreen( view , itemInfo.x , itemInfo.y );
				view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , itemInfo.x , itemInfo.y , 0 );
			}
			else if( itemInfo.container >= 0 )
			{
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
		// View3D view = child_list.get(0);
		int pageNum = getCurrentPage();
		mFocusGridView = (HotGridView3D)view_list.get( pageNum );
		int index = mFocusGridView.getIndex( (int)x , (int)y );
		// Log.v("HotObj", "dealDockGroupDropOver index ="+index);
		if( index < 0 )
		{
			removeVirtueFolderIcon();
			return;
		}
		View3D findView = mFocusGridView.findExistView( index );
		if( findView == null )
		{
			// Log.v("HotObj", "dealDockGroupDropOver findView==null ");
			removeVirtueFolderIcon();
			mFocusGridView.MyMoveInTween( x , y );
			return;
		}
		if( findView instanceof FolderIcon3D )
		{
			//			folder = (FolderIcon3D)findView;
			//			folder.onDropOver(child_list, 0, 0);
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
		// curFolderPos=index;
		cellMakeFolder( IconPoint.x , IconPoint.y , true );
		mFocusGridView.MyMoveInTween( x , y );
	}
	
	public void addItems(
			ArrayList<View3D> child_list ,
			int page )
	{
		//		mFocusGridView = (HotGridView3D) view_list.get(page);
		Log.v( "ondrop" , "hotdockgroup addItems333333333" );
		View3D view = child_list.get( child_list.size() - 1 );
		mFocusGridView.calcCoordinate( view );
		int index = mFocusGridView.getAutoIndex( (int)( view.x + view.width / 2 ) , (int)mFocusGridView.height / 2 );
		Log.v( "ondrop" , "hotdockgroup addItems" + index );
		// Log.v("HotObj", "addItems index ="+index);
		removeVirtueFolderIcon();
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
			Log.v( "ondrop" , "hotdockgroup addItems44444444" );
			/*
			 * 这里表示拖到的地方已经被占用,需要创建文件夹
			 */
			if( view instanceof FolderIcon3D && child_list.size() == 1 )
			{
				backtoOrig( child_list );
				childPositonUpdate();
				// SendMsgToAndroid.sendOurToastMsg("文件");
			}
			else
			{
				if( findView instanceof FolderIcon3D )
				{
					( (FolderIcon3D)findView ).onDrop( child_list , 0 , 0 );
					( (FolderIcon3D)findView ).setOnDropFalse();
					Log.v( "ondrop" , "hotdockgroup addItems222222" );
					childPositonUpdate();
					mFocusGridView.updateItemInfoDB();
				}
				else
				{
					if( DefaultLayout.hotseat_disable_make_folder )
					{
						backtoOrig( child_list );
						childPositonUpdate();
					}
					else
					{
						child_list.add( findView );
						mFocusGridView.removeView( findView );
						createFolderItem( child_list , index );
						Log.v( "ondrop" , "hotdockgroup addItems" );
					}
				}
			}
			return;
		}
		else
		{
			Log.v( "ondrop" , "hotdockgroup addItems5555555" );
			if( child_list.size() == 1 )
			{
				/* 直接插入 */
				if( DefaultLayout.hotseat_hide_title )
				{
					if( view instanceof Icon3D )
					{
						if( ThemeManager.getInstance().getCurrentThemeDescription().mSystem )
						{
							replaceIcon( (Icon3D)view );
						}
						Utils3D.changeTextureRegion( child_list , Utils3D.getIconBmpHeight() , true );
					}
					else if( view instanceof FolderIcon3D )
					{
						( (FolderIcon3D)view ).changeFolderFrontRegion( true );
					}
				}
				addSingleItem( view , page , index );
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
		// name = info.intent.toString();
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
		if( iconFlag == 0 )
		{
			try
			{
				Log.e( "test" , "before :" + getInfoName( sInfo ) );
				sInfo.intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.DIAL;end" , 0 );
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
				sInfo.intent = Intent.parseUri( "intent:content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;end" , 0 );
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
				sInfo.intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;end" , 0 );
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
				defaultUri = "http://www.google.cn";// 用百度代替空白网??about:blank"否则欧鹏浏览器无法识??
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
			sInfo.intent = intent;
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
		if( iconFlag == 0 )
		{
			try
			{
				Log.e( "test" , "before :" + getInfoName( sInfo ) );
				sInfo.intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.DIAL;end" , 0 );
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
				sInfo.intent = Intent.parseUri( "intent:content://com.android.contacts/contacts#Intent;action=android.intent.action.VIEW;end" , 0 );
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
				sInfo.intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.MAIN;type=vnd.android-dir/mms-sms;end" , 0 );
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
				defaultUri = "http://www.google.cn";// 用百度代替空白网??about:blank"否则欧鹏浏览器无法识??
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
			sInfo.intent = intent;
			// sInfo.intent = new Intent("android.intent.action.VIEW");
			// sInfo.intent.addCategory("android.intent.category.DEFAULT");
			// sInfo.intent.addCategory("android.intent.category.BROWSABLE");
			// Uri uri = Uri.parse("http://www.google.cn");
			// sInfo.intent.setDataAndType(uri, null);
			sInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
			icon.region.setRegion( R3D.findRegion( getInfoName( sInfo ) ) );
		}
	}
	
	public void bindItem(
			View3D view )
	{
		if( view instanceof Icon3D )
		{
			if( ThemeManager.getInstance().getCurrentThemeDescription().mSystem )
			{
				replaceIcon( (Icon3D)view );
			}
		}
		mShortcutView.addItem( view );
	}
	
	public void bindBackItem(
			View3D view )
	{
		if( view instanceof Icon3D )
		{
			if( ThemeManager.getInstance().getCurrentThemeDescription().mSystem )
			{
				replaceIcon( (Icon3D)view );
			}
		}
		mShortcutView.addBackItem( view );
	}
	
	public void bindItem1(
			View3D view )
	{
		if( view instanceof Icon3D )
		{
			if( ThemeManager.getInstance().getCurrentThemeDescription().mSystem )
			{
				replaceIcon( (Icon3D)view );
			}
		}
		mShortcutView1.addItem( view );
	}
	
	public void bindBackItem1(
			View3D view )
	{
		if( view instanceof Icon3D )
		{
			if( ThemeManager.getInstance().getCurrentThemeDescription().mSystem )
			{
				replaceIcon( (Icon3D)view );
			}
		}
		mShortcutView1.addBackItem( view );
	}
	
	public void bindItem2(
			View3D view )
	{
		if( view instanceof Icon3D )
		{
			if( ThemeManager.getInstance().getCurrentThemeDescription().mSystem )
			{
				replaceIcon( (Icon3D)view );
			}
		}
		mShortcutView2.addItem( view );
	}
	
	public void bindBackItem2(
			View3D view )
	{
		if( view instanceof Icon3D )
		{
			if( ThemeManager.getInstance().getCurrentThemeDescription().mSystem )
			{
				replaceIcon( (Icon3D)view );
			}
		}
		mShortcutView2.addBackItem( view );
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
		Log.v( "Folder" , "dealFolderIconMove" );
		View3D findView = (View3D)mFocusGridView.getTag();
		View3D focusView = mFocusGridView.getFocusView();
		int index = ( (IconBase3D)findView ).getItemInfo().screen;
		if( findView instanceof FolderIcon3D )
		{
			ArrayList<View3D> list = new ArrayList<View3D>();
			list.add( focusView );
			folder = (FolderIcon3D)findView;
			folder.onDropOver( list , 0 , 0 );
		}
		else
		{
			mFocusGridView.getIndexPos( index , IconPoint );
			cellMakeFolder( IconPoint.x , IconPoint.y , true );
		}
	}
	
	private void dealCreateFolder()
	{
		Log.v( "Folder" , "dealCreateFolder" );
		View3D findView = (View3D)mFocusGridView.getTag();
		View3D focusView = mFocusGridView.getFocusView();
		removeVirtueFolderIcon();
		int index = ( (Icon3D)findView ).getItemInfo().screen;
		mFocusGridView.getIndexPos( index , IconPoint );
		ArrayList<View3D> child_list = new ArrayList<View3D>();
		child_list.add( findView );
		child_list.add( focusView );
		mFocusGridView.removeView( findView );
		mFocusGridView.removeView( focusView );
		createFolderItem( child_list , index );
		// cellMakeFolder(IconPoint.x, IconPoint.y, true);
		//		shortcutCountUpdate(mFocusGridView.getChildCount());
		mFocusGridView.setCellCount( mFocusGridView.getChildCount() , 1 );
	}
	
	private void dealMergeFolder()
	{
		Log.v( "Folder" , "dealMergeFolder" );
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
					if( view == mFocusGridView )
					{
						if( mFocusGridView.getDragState() == HotGridView3D.State_ChangePosition )
						{
							if( folder_icon != null && folder_icon.getVisible() )
							{
								FolderSmallAnim( 0.01f , folder_icon );
							}
							else
							{
								removeVirtueFolderIcon();
							}
							if( folder != null )
							{
								folder.onDragOverLeave();
								folder = null;
							}
						}
						else
						{
							dealFolderIconMove();
							Log.v( "Folder" , "onCtrlEvent" );
						}
					}
					return false;
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
	
	public HotGridView3D getShortcutGridview1()
	{
		return mShortcutView1;
	}
	
	public HotGridView3D getShortcutGridview2()
	{
		return mShortcutView2;
	}
	
	@Override
	public boolean visible()
	{
		if( this.getParent() != null )
		{
			return visible && this.getParent().visible;
		}
		return visible;
	}
	
	@Override
	public int getClingPriority()
	{
		// TODO Auto-generated method stub
		return ClingManager.ALLAPP_CLING;
	}
	
	@Override
	public void dismissCling()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setPriority(
			int priority )
	{
		// TODO Auto-generated method stub
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
		view3D.color.a = 0;
		view3D.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 1 , 0 , 0 );
		View3D folderCoverContainer = ( (ViewGroup3D)view3D ).findView( "folder_cover_container" );
		if( folderCoverContainer != null )
		{
			folderCoverContainer.setRotation( 0 );
			folderCoverContainer.stopAllTween();
			folderCoverContainer.startTween( View3DTweenAccessor.ROTATION , Back.OUT , duration , FolderIcon3D.folderCoverRotion , 0 , 0 ).delay( duration - 0.1f );
		}
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
		view3D.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 0 , 0 , 0 );
		View3D folderCoverContainer = ( (ViewGroup3D)view3D ).findView( "folder_cover_container" );
		if( folderCoverContainer != null )
		{
			folderCoverContainer.stopAllTween();
			folderCoverContainer.startTween( View3DTweenAccessor.ROTATION , Cubic.OUT , duration , 0 , 0 , 0 );
		}
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
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
		if( isTurning )
		{
			isTurning = false;
		}
		super.onEvent( type , source );
	}
	
	public void childPositonUpdate()
	{
		mFocusGridView.setCount( mFocusGridView.getChildCount() );
	}
	
	public void childReleaseDark()
	{
		mFocusGridView = (HotGridView3D)view_list.get( getCurrentPage() );
		mFocusGridView.setReleaseDark();
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
}
