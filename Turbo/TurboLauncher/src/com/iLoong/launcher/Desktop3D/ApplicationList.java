package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.cut;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ApplicationList extends NPageBase
{
	
	private static int mCountX = 4;
	private static int mCountY = 4;
	public ArrayList<AppIcon3D> mApps;
	private int pagenum = -1;
	private int mPaddingLeft = 0;
	private int mPaddingRight = 0;
	private int mPaddingTop = 20;
	private int mPaddingBottom = 55;
	private int itemWidth = 0;
	private int itemHeight = 0;
	public static ArrayList<View3D> mSelected = new ArrayList<View3D>();
	public int sortId = -1;
	private int[] sortArray;
	private ArrayList<AppIcon3D> iconMap;
	private int m_indicatorSize = 18;
	public static boolean sbHideIconInFolder;
	public static UserFolderInfo sUserFolderInfo;
	
	public ApplicationList(
			String name ,
			float width ,
			float height )
	{
		// TODO Auto-generated constructor stub
		super( name );
		indicatorView = new IndicatorView( "npage_indicator" , R3D.page_indicator_style );
		m_indicatorSize *= ApplicationListHost.scaleFactor;
		indicatorView.indicatorSize = m_indicatorSize * 2;
		indicatorView.indicatorNormalW = indicatorView.indicatorNormalH = indicatorView.indicatorFocusW = indicatorView.indicatorFocusH = m_indicatorSize;
		if( DefaultLayout.isScaleBitmap )
		{
			indicatorView.unselectedIndicator = Tools.getTextureByPicName( "theme/folder/folder_app_add_indicator.png" , indicatorView.indicatorNormalW , indicatorView.indicatorNormalH );
		}
		else
		{
			indicatorView.unselectedIndicator = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/folder/application_add_indicator.png" ) , true ) );
		}
		if( DefaultLayout.isScaleBitmap )
		{
			indicatorView.selectedIndicator = Tools.getTextureByPicName( "theme/folder/folder_app_add_indicator_focus.png" , indicatorView.indicatorFocusW , indicatorView.indicatorFocusH );
		}
		else
		{
			indicatorView.selectedIndicator = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/folder/application_add_indicator_focus.png" ) , true ) );
		}
		indicatorView.width = width;
		indicatorView.setOrigin( indicatorView.width / 2 , indicatorView.height / 2 );
		indicatorView.setPosition( ApplicationListHost.leftPadding , ApplicationListHost.bottomPadding * 2 + indicatorView.height / 3 );
		this.width = width;
		this.height = height;
		mPaddingLeft = ApplicationListHost.leftPadding;
		mPaddingRight = mPaddingLeft;
		mPaddingTop *= ApplicationListHost.scaleFactor;
		mPaddingBottom *= ApplicationListHost.scaleFactor;
		itemWidth = R3D.workspace_cell_width;
		itemHeight = R3D.workspace_cell_height;
		mCountX = (int)( ( width - mPaddingLeft - mPaddingRight ) / itemWidth );
		mCountY = (int)( ( height - mPaddingTop - mPaddingBottom ) / itemHeight );
		needXRotation = false;
		setEffectType( APageEase.COOLTOUCH_EFFECT_DEFAULT );
		sortId = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getInt( "sort_app_for_folder" , -1 );
		if( sortId == -1 )
		{
			if( DefaultLayout.getInstance().show_default_app_sort )
			{
				sortId = AppList3D.SORT_FACTORY;
			}
			else
			{
				sortId = AppList3D.SORT_INSTALL;// SORT_DEFAULT;
			}
		}
		iconMap = new ArrayList<AppIcon3D>();
	}
	
	@Override
	protected void finishAutoEffect()
	{
		// TODO Auto-generated method stub
		if( indicatorView != null )
		{
			indicatorView.finishAutoEffect();
		}
		super.finishAutoEffect();
	}
	
	public void removeAllChild(
			int num )
	{
		for( int i = 0 ; i < num ; i++ )
		{
			this.removeView( view_list.get( i ) );
		}
		view_list.clear();
		pagenum = 0;
	}
	
	@Override
	public void show()
	{
		// TODO Auto-generated method stub
		super.show();
		if( indicatorView != null )
		{
			indicatorView.stopTween();
			indicatorView.color.a = 0.0f;
			indicatorView.show();
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		boolean ret = false;
		if( getParent() instanceof ApplicationListHost )
		{
			getParent().releaseFocus();
		}
		ret = super.onTouchDown( x , y , pointer );
		if( ret )
		{
			return true;
		}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		super.onTouchUp( x , y , pointer );
		return false;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		boolean ret = super.onClick( x , y );
		if( ret )
		{
			return true;
		}
		return true;
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		View3D hitView = hit( x , y );
		if( hitView != null && hitView instanceof GridView3D )
		{
			return true;
		}
		return super.onTouchDragged( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		if( indicatorView != null )
		{
			indicatorView.stopTween();
			indicatorView.setUser( 1.0f );
			indicatorView.color.a = 1.0f;
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		// TODO Auto-generated method stub
		if( sender instanceof Icon3D )
		{
			Icon3D icon = (Icon3D)sender;
			switch( event_id )
			{
				case Icon3D.MSG_ICON_SELECTED:
					mSelected.add( icon );
					break;
				case Icon3D.MSG_ICON_UNSELECTED:
					int index = isHaveInlist( icon );
					if( index != -1 )
					{
						mSelected.remove( index );
					}
					break;
			}
			return true;
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	private int isHaveInlist(
			Icon3D icon )
	{
		ItemInfo info = null;
		ItemInfo tmp = null;
		for( int x = 0 ; x < mSelected.size() ; x++ )
		{
			if( mSelected.get( x ) instanceof Icon3D )
			{
				info = ( (Icon3D)mSelected.get( x ) ).getItemInfo();
				tmp = icon.getItemInfo();
				if( info instanceof ShortcutInfo && tmp instanceof ShortcutInfo )
				{
					if( info.title.equals( tmp.title ) )
					{
						return x;
					}
				}
			}
		}
		return -1;
	}
	
	public int getTotalAppsCount()
	{
		if( mApps == null )
		{
			return 0;
		}
		return mApps.size();
	}
	
	public void startAnimation()
	{
		Iterator iconIter = iconMap.iterator();
		tween = null;
		while( iconIter.hasNext() )
		{
			AppIcon3D icon = (AppIcon3D)iconIter.next();
			if( icon.getParent() != null )
			{
				if( !icon.oldVisible )
				{
					icon.setScale( 0 , 0 );
					tween = icon.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.8f , 1 , 1 , 0 );
					icon.oldVisible = true;
				}
				else if( icon.oldAppGridIndex != icon.newAppGridIndex || ( ( icon.oldX != icon.x || icon.oldY != icon.y ) && icon.newAppGridIndex == page_index ) )
				{
					float oldX = ( icon.oldAppGridIndex - page_index ) * Utils3D.getScreenWidth() + icon.oldX;
					float oldY = icon.oldY;
					float newX = ( icon.newAppGridIndex - page_index ) * Utils3D.getScreenWidth() + icon.x;
					float newY = icon.y;
					icon.setPosition( oldX , oldY );
					tween = icon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , newX , newY , 0 );
					icon.oldX = newX;
					icon.oldY = newY;
					icon.oldAppGridIndex = icon.newAppGridIndex;
				}
			}
			else
			{
				icon.oldVisible = false;
			}
		}
		//		needLayout = true;
		//		if( tween != null )
		//		{
		//			tween.setCallback( this );
		//		}
	}
	
	public void sortApp(
			int checkId ,
			boolean refresh )
	{
		if( checkId != sortId )
		{
			sortId = checkId;
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putInt( "sort_app_for_folder" , sortId ).commit();
		}
		sortArray = new int[mApps.size()];
		switch( sortId )
		{
			case AppList3D.SORT_DEFAULT:
				String[] titleKey = new String[mApps.size()];
				boolean[] priorityKey = new boolean[mApps.size()];
				for( int i = 0 ; i < mApps.size() ; i++ )
				{
					priorityKey[i] = false;
					if( ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
					{
						titleKey[i] = "0000000";
						priorityKey[i] = true;
					}
					else
					{
						titleKey[i] = ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一�?
						if( DefaultLayout.getInstance().hasReplaceIcon(
								( (ShortcutInfo)mApps.get( i ).getItemInfo() ).appInfo.componentName.getPackageName() ,
								( (ShortcutInfo)mApps.get( i ).getItemInfo() ).appInfo.componentName.getClassName() ) )
						{
							priorityKey[i] = true;
						}
					}
				}
				cut.sortByDefault( 1 , titleKey , priorityKey , sortArray );
				break;
			case AppList3D.SORT_NAME:
				String[] nameKey = new String[mApps.size()];
				for( int i = 0 ; i < mApps.size() ; i++ )
				{
					if( ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
						nameKey[i] = "0000000";
					else
						nameKey[i] = ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一�?
				}
				cut.sortByAlpha( 1 , nameKey , sortArray );
				break;
			case AppList3D.SORT_INSTALL:
				int[] facKey = new int[mApps.size()];
				ArrayList<ItemInfo> embededItemInfo = new ArrayList<ItemInfo>();
				int embeded_app_num = getEmbededAppInfos( iLoongLauncher.getInstance() , embededItemInfo , mApps );
				int[] embeded_facKey = new int[embeded_app_num];
				int[] embeded_sortKey = new int[embeded_app_num];
				int embeded_replace_icon_num = 0;
				for( int index = 0 ; index < embeded_app_num ; index++ )
				{
					ItemInfo appInfo = embededItemInfo.get( index );
					if( appInfo instanceof ApplicationInfo )
					{
						ApplicationInfo app_Info = (ApplicationInfo)appInfo;
						if( null != DefaultLayout.getInstance().getReplaceIcon( app_Info.packageName , app_Info.componentName.getClassName() ) )
						{
							embeded_facKey[index] = embeded_replace_icon_num - embeded_app_num;
							embeded_replace_icon_num++;
						}
						else
						{
							embeded_facKey[index] = (int)( ( (ApplicationInfo)appInfo ).lastUpdateTime / 1000 );
						}
					}
					else
					{
						embeded_facKey[index] = 0;
					}
				}
				cut.sort( 1 , embeded_facKey , embeded_sortKey );
				for( int i = 0 ; i < mApps.size() ; i++ )
				{
					ItemInfo appInfo = (ItemInfo)mApps.get( i ).getItemInfo();
					if( appInfo instanceof ShortcutInfo )
					{
						if( embededItemInfo.contains( ( (ShortcutInfo)appInfo ).appInfo ) )
						{
							int app_tmp_index = getEmbededIndex( embededItemInfo , ( (ShortcutInfo)appInfo ).appInfo , embeded_sortKey );
							facKey[i] = app_tmp_index - embeded_app_num;
						}
						else
						{
							ShortcutInfo appInfo1 = (ShortcutInfo)mApps.get( i ).getItemInfo();
							if( appInfo1.appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
								facKey[i] = 0;
							else
								facKey[i] = (int)( appInfo1.appInfo.lastUpdateTime / 1000 );
						}
					}
					else if( appInfo instanceof FolderInfo )
					{
						// when select item is a folder
						facKey[i] = -(int)( ( (FolderInfo)appInfo ).lastUpdateTime / 1000 );
					}
				}
				cut.sort( 1 , facKey , sortArray );
				break;
			case AppList3D.SORT_USE:
				int[] useKey = new int[mApps.size()];
				for( int i = 0 ; i < mApps.size() ; i++ )
				{
					if( ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
						useKey[i] = 2147483647;
					else
						useKey[i] = (int)( ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).getUseFrequency() );
				}
				cut.sort( 0 , useKey , sortArray );
				break;
			case AppList3D.SORT_FACTORY:
				int i ,
				j;
				int[] facKey1 = new int[mApps.size()];
				int facAppNum = DefaultLayout.facApp.size();
				boolean is_exist;
				for( i = 0 ; i < mApps.size() ; i++ )
				{
					is_exist = false;
					for( j = 0 ; j < facAppNum ; j++ )
					{
						for( int k = 0 ; k < DefaultLayout.facApp.get( j ).pkgNameArray.size() ; k++ )
						{
							if( ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).appInfo.packageName.equals( DefaultLayout.facApp.get( j ).pkgNameArray.get( k ) ) )
							{
								if( DefaultLayout.facApp.get( j ).className == null || ( (ApplicationInfo)mApps.get( i ).getItemInfo() ).componentName.getClassName().equals(
										DefaultLayout.facApp.get( j ).className ) )
								{
									facKey1[i] = j - facAppNum;
									is_exist = true;
									break;
								}
							}
						}
						if( is_exist )
							break;
					}
					if( j == facAppNum )
					{
						if( ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
							facKey1[i] = 0;
						else
							facKey1[i] = (int)( ( (ShortcutInfo)mApps.get( i ).getItemInfo() ).appInfo.lastUpdateTime / 1000 );
					}
				}
				cut.sort( 1 , facKey1 , sortArray );
				break;
		}
		if( refresh )
		{
			syncAppsPageItems();
			startAnimation();
		}
	}
	
	private int getEmbededAppInfos(
			Context mContext ,
			ArrayList<ItemInfo> des_arraylst ,
			ArrayList<AppIcon3D> allApps )
	{
		final PackageManager manager = mContext.getPackageManager();
		int embeded_app_num = 0;
		if( des_arraylst == null || allApps == null )
			return 0;
		des_arraylst.clear();
		ShortcutInfo itemInfo;
		for( AppIcon3D appIcon3D : allApps )
		{
			itemInfo = (ShortcutInfo)appIcon3D.getItemInfo();
			if( itemInfo.appInfo instanceof ApplicationInfo && ( itemInfo.appInfo ).intent != null )
			{
				ResolveInfo resolveInfo = manager.resolveActivity( ( itemInfo.appInfo ).intent , 0 );
				if( resolveInfo == null )
				{
					continue;
				}
				int flags = resolveInfo.activityInfo.applicationInfo.flags;
				if( ( flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 || ( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0 ) )
				{
					// this app is embeded item
					des_arraylst.add( itemInfo.appInfo );
					embeded_app_num++;
				}
			}
		}
		return embeded_app_num;
	}
	
	private int getEmbededIndex(
			ArrayList<ItemInfo> embededInfos ,
			ItemInfo itemInfo ,
			int[] sortArray )
	{
		if( embededInfos == null || sortArray == null || itemInfo == null )
			return 0;
		if( itemInfo instanceof ApplicationInfo )
		{
			for( int i = 0 ; i < embededInfos.size() ; i++ )
			{
				int sort_index = sortArray[i];
				ItemInfo cur_item = embededInfos.get( sort_index );
				if( cur_item != null && cur_item instanceof ApplicationInfo )
				{
					ApplicationInfo cur_app = (ApplicationInfo)cur_item;
					if( ( cur_app.packageName != null && cur_app.packageName.equals( ( (ApplicationInfo)itemInfo ).packageName ) ) && ( cur_app.componentName != null && cur_app.componentName
							.getClassName() != null && cur_app.componentName.getClassName().equals( ( (ApplicationInfo)itemInfo ).componentName.getClassName() ) ) )
					{
						return i;
					}
				}
			}
		}
		return 0;
	}
	
	public void syncAppsPage(
			ArrayList<View3D> selected )
	{
		mSelected.clear();
		mSelected.addAll( selected );
		mApps = (ArrayList<AppIcon3D>)getAllApp();
		sortApp( sortId , false );
		syncAppsPageItems();
	}
	
	public void forceSyncAppsPage()
	{
		mApps = (ArrayList<AppIcon3D>)getAllApp();
		sortApp( sortId , true );
		syncAppsPageItems();
	}
	
	public void syncAppsPageItems()
	{
		int allsize = mApps.size();
		int page = 0;
		if( ( allsize % ( mCountX * mCountY ) ) == 0 )
		{
			page = allsize / ( mCountX * mCountY );
		}
		else
		{
			page = allsize / ( mCountX * mCountY ) + 1;
		}
		if( pagenum > 0 )
		{
			removeAllChild( pagenum );
		}
		for( int i = 0 ; i < page ; i++ )
		{
			GridView3D appGridView = new GridView3D( "gridview" , this.width , this.height , mCountX , mCountY );
			appGridView.setAutoDrag( false );
			appGridView.enableAnimation( false );
			appGridView.setPadding( mPaddingLeft , mPaddingRight , mPaddingTop , mPaddingBottom );
			AppIcon3D appIcon3D = null;
			if( i < page - 1 )
			{
				for( int j = mCountX * mCountY * i ; j < mCountX * mCountY * ( i + 1 ) ; j++ )
				{
					appIcon3D = mApps.get( sortArray[j] );
					if( iconMap.contains( appIcon3D ) )
					{
						appIcon3D.newAppGridIndex = i;
						if( i != page_index && appIcon3D.oldAppGridIndex != page_index )
						{
							appIcon3D.oldAppGridIndex = page;
						}
						appIcon3D.oldX = appIcon3D.getX();
						appIcon3D.oldY = appIcon3D.getY();
					}
					else
					{
						appIcon3D.oldAppGridIndex = i;
						appIcon3D.newAppGridIndex = i;
						if( i == page_index )
						{
							appIcon3D.oldVisible = false;
						}
						else
						{
							appIcon3D.oldVisible = true;
						}
						iconMap.add( appIcon3D );
					}
					appGridView.addItem( appIcon3D );
					for( int x = 0 ; x < mSelected.size() ; x++ )
					{
						if( isHaveInlist( appIcon3D ) != -1 )
						{
							appIcon3D.selected();
							break;
						}
					}
				}
			}
			else if( i == page - 1 )
			{
				for( int j = mCountX * mCountY * i ; j < allsize ; j++ )
				{
					appIcon3D = mApps.get( sortArray[j] );
					appGridView.addItem( appIcon3D );
					for( int x = 0 ; x < mSelected.size() ; x++ )
					{
						if( isHaveInlist( appIcon3D ) != -1 )
						{
							appIcon3D.selected();
							break;
						}
					}
				}
			}
			addPage( i , appGridView );
		}
		pagenum = page;
		page_index = 0;
	}
	
	private List<AppIcon3D> getAllApp()
	{
		List<AppIcon3D> allApps = new ArrayList<AppIcon3D>();
		AppIcon3D appicon = null;
		if( AppHost3D.appList.mApps != null )
		{
			if( AppHost3D.appList.mApps.size() == 0 )
			{
				if( AppHost3D.appList.mItemInfos != null && AppHost3D.appList.mItemInfos.size() > 0 )
				{
					boolean hide = false;
					for( int i = 0 ; i < AppHost3D.appList.mItemInfos.size() ; i++ )
					{
						ItemInfo itemInfo = AppHost3D.appList.mItemInfos.get( i );
						if( itemInfo instanceof ApplicationInfo )
						{
							ApplicationInfo info = (ApplicationInfo)AppHost3D.appList.mItemInfos.get( i );
							if( info.intent.getComponent() != null )
							{
								ShortcutInfo sInfo = info.makeShortcut();
								String appName = R3D.getInfoName( sInfo );
								Icon3D icon = AppHost3D.appList.iconMap.get( appName );
								if( icon != null && !sInfo.appInfo.isHideIcon  )
								{
									appicon = new AppIcon3D( sInfo.title.toString() , R3D.findRegion( sInfo ) , hide );
									appicon.setItemInfo( new ShortcutInfo( sInfo ) );
									appicon.setSize( itemWidth , itemHeight );
									allApps.add( appicon );
								}
							}
						}
						else if( itemInfo instanceof UserFolderInfo )
						{
							if( !sbHideIconInFolder )
							{
								if( AppHost3D.appList != null && AppHost3D.appList.isVisible() )
								{
									if( sUserFolderInfo != null && sUserFolderInfo == (UserFolderInfo)itemInfo )
									{
										hide = true;
										ArrayList<ShortcutInfo> list = ( (UserFolderInfo)itemInfo ).contents;
										for( ShortcutInfo info : list )
										{
											if( !info.appInfo.isHideIcon )
											{
												appicon = new AppIcon3D( info.title.toString() , R3D.findRegion( info ) , hide );
												appicon.setItemInfo( new ShortcutInfo( info ) );
												appicon.setSize( itemWidth , itemHeight );
												allApps.add( appicon );
											}
										}
									}
								}
								else
								{
									hide = true;
									ArrayList<ShortcutInfo> list = ( (UserFolderInfo)itemInfo ).contents;
									for( ShortcutInfo info : list )
									{
										if( !info.appInfo.isHideIcon )
										{
											appicon = new AppIcon3D( info.title.toString() , R3D.findRegion( info ) , hide );
											appicon.setItemInfo( new ShortcutInfo( info ) );
											appicon.setSize( itemWidth , itemHeight );
											allApps.add( appicon );
										}
									}
								}
							}
						}
					}
				}
			}
			else
			{
				for( int i = 0 ; i < AppHost3D.appList.mApps.size() ; i++ )
				{
					ApplicationInfo info = AppHost3D.appList.mApps.get( i );
					if( info.intent.getComponent() != null )
					{
						ShortcutInfo sInfo = info.makeShortcut();
						String appName = R3D.getInfoName( sInfo );
						Icon3D icon = AppHost3D.appList.iconMap.get( appName );
						if( icon != null && !sInfo.appInfo.isHideIcon  )
						{
							appicon = new AppIcon3D( icon.name , icon.region , false );
							appicon.setItemInfo( new ShortcutInfo( (ShortcutInfo)icon.getItemInfo() ) );
							appicon.setSize( itemWidth , itemHeight );
							allApps.add( appicon );
						}
					}
				}
			}
		}
		return allApps;
	}
	
	class AppIcon3D extends Icon3D
	{
		
		public int oldAppGridIndex;
		public int newAppGridIndex;
		public float oldX;
		public float oldY;
		public boolean oldVisible = false;
		private boolean apphide = false;
		
		public AppIcon3D(
				String name ,
				TextureRegion t )
		{
			// TODO Auto-generated constructor stub
			super( name , t );
			this.apphide = false;
			// TODO Auto-generated constructor stub
		}
		
		public AppIcon3D(
				String name ,
				TextureRegion t ,
				boolean hide )
		{
			// TODO Auto-generated constructor stub
			super( name , t );
			this.apphide = hide;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// TODO Auto-generated method stub
			float alpha = color.a;
			if( isHide && apphide )
			{
				alpha *= 0.2;
			}
			super.draw( batch , alpha * parentAlpha );
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			super.onTouchDown( x , y , pointer );
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			super.onTouchUp( x , y , pointer );
			return true;
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			if( hide || uninstall )
				return true;
			if( isSelected() )
			{
				cancelSelected();
			}
			else
			{
				if( mSelected.size() >= R3D.folder_max_num )
				{
					SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.reach_max ) );
				}
				else
				{
					selected();
				}
			}
			return true;
		}
		
		@Override
		public boolean onDoubleClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			return true;
		}
	}
}
