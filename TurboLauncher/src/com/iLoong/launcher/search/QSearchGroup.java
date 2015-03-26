package com.iLoong.launcher.search;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.google.analytics.tracking.android.Log;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.HotSeat3D.HotDockGroup;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class QSearchGroup extends ViewGroup3D
{
	
	private GuessGroup mGuessGroup;
	public AllAppList mAllAppList;
	private LettersGroup mLettersGroup;
	private SearchEditTextGroup mSearchEditTextGroup;
	public SearchResultGroup mSearchResultGroup;
	public View3D mTopFill;
	public View3D mButtonFill;
	public PopGroup mPopGroup;
	private boolean mLock = false;
	public static boolean mLoaded;
	public static List<SearchApp> allApps;
	private Timeline qs_timeline = null;
	public Timeline qs_pop_timeline = null;
	private Timeline qs_reset_timeline = null;
	public static boolean canShow = true;
	
	public QSearchGroup(
			String name )
	{
		super( name );
		setSize( QsearchConstants.W_QUICK_SEARCH , QsearchConstants.H_QUICK_SEARCH );
		initView();
	}
	
	private void initView()
	{
		try
		{
			Bitmap topFillBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_guess_bg.9.png" ) );
			NinePatch topFillNp = new NinePatch( new BitmapTexture( topFillBmp , true ) , 1 , 1 , 1 , 1 );
			mTopFill = new View3D( "topFill" );
			mTopFill.setBackgroud( topFillNp );
			mTopFill.setSize( QsearchConstants.W_QUICK_SEARCH_FILL_TOP , QsearchConstants.H_QUICK_SEARCH_FILL_TOP );
			mTopFill.setPosition( 0 , UtilsBase.getScreenHeight() - mTopFill.height );
			Bitmap bottomFillBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/search_edit_bg.png" ) );
			NinePatch bottomFillNp = new NinePatch( new BitmapTexture( bottomFillBmp , true ) , 1 , 1 , 0 , 0 );
			mButtonFill = new View3D( "bottomFill" );
			mButtonFill.setBackgroud( bottomFillNp );
			mButtonFill.setSize( QsearchConstants.W_SEARCH_EDIT_GROUP , QsearchConstants.H_SEARCH_EDIT_GROUP );
			mGuessGroup = new GuessGroup( "guessGroup" );
			mSearchEditTextGroup = new SearchEditTextGroup( "searchEditTextGroup" );
			mLettersGroup = new LettersGroup( "lettersGroup" );
			mAllAppList = new AllAppList( "allAppList" );
			mSearchResultGroup = new SearchResultGroup( "searchResultGroup" );
			mPopGroup = new PopGroup( "popGroup" );
			addView( mGuessGroup );
			addView( mAllAppList );
			addView( mLettersGroup );
			addView( mSearchResultGroup );
			addView( mButtonFill );
			addView( mSearchEditTextGroup );
			addView( mTopFill );
			addView( mPopGroup );
			resetPostion();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public void resetPostion()
	{
		mTopFill.setPosition( 0 , -mTopFill.height );
		mSearchEditTextGroup.setPosition( UtilsBase.getScreenWidth() , 0 );
		mGuessGroup.setPosition( 0 , -mGuessGroup.height );
		mGuessGroup.mGuessPage.indicatorView.setPosition( 0 , -mGuessGroup.height );
		mAllAppList.setPosition( 0 , -( mGuessGroup.height + mAllAppList.height ) );
		mLettersGroup.setPosition( UtilsBase.getScreenWidth() - mLettersGroup.width , -( mGuessGroup.height + mLettersGroup.height ) );
		mSearchResultGroup.setPosition( 0 , -mSearchResultGroup.height );
		mButtonFill.setPosition( 0 , -UtilsBase.getScreenHeight() );
		Root3D.hotseatBar.setPosition( 0 , 0 );
		mSearchEditTextGroup.resetPostion();
		Root3D.hotseatBar.dockGroup.backgroud.setPosition(0, 0);
		hide();
	}
	
	public void startPopFadeOutAnim()
	{
		if( qs_pop_timeline != null )
		{
			qs_pop_timeline.free();
			qs_pop_timeline = null;
		}
		qs_pop_timeline = Timeline.createParallel();
		float duration = 0.5f;
		qs_pop_timeline.push( Tween.to( mPopGroup , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		qs_pop_timeline.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	public void startResetAnim()
	{
		if( qs_reset_timeline != null )
		{
			qs_reset_timeline.free();
			qs_reset_timeline = null;
		}
		qs_reset_timeline = Timeline.createParallel();
		TweenEquation equation = Cubic.OUT;
		float duration = 0.5f;
		qs_reset_timeline.push( Tween.to(Root3D.hotseatBar.dockGroup.backgroud, View3DTweenAccessor.POS_XY , duration ).target( 0 , 0 ).ease( equation ) );
		qs_reset_timeline.push( Tween.to( mSearchEditTextGroup , View3DTweenAccessor.POS_XY , duration ).target( UtilsBase.getScreenWidth() , 0 ).ease( equation ) );
		qs_reset_timeline.push( Tween.to( Root3D.hotseatBar , View3DTweenAccessor.POS_XY , duration ).target( 0 , 0 ).ease( equation ) );
		qs_reset_timeline.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	public void startQuickSearchAnimIn()
	{
		if( mLock )
		{
			return;
		}
		if( qs_timeline != null )
		{
			qs_timeline.free();
			qs_timeline = null;
		}
		qs_timeline = Timeline.createParallel();
		TweenEquation equation = Cubic.OUT;
		float editGroupDuration = 0.5f;
		float displayGroupDuration = 0.4f;
		qs_timeline.push( Tween.to( Root3D.hotseatBar.dockGroup.backgroud , View3DTweenAccessor.POS_XY , editGroupDuration ).target( UtilsBase.getScreenWidth() , 0 ).ease( equation )  );
		qs_timeline.push( Tween.to( mSearchEditTextGroup , View3DTweenAccessor.POS_XY , editGroupDuration ).target( 0 , 0 ).ease( equation ) );
		qs_timeline.push( Tween.to( Root3D.hotseatBar , View3DTweenAccessor.POS_XY , editGroupDuration ).target( -UtilsBase.getScreenWidth() , 0 ).ease( equation ) );
		qs_timeline.push( Tween.to( mTopFill , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , UtilsBase.getScreenHeight() - mTopFill.height ).ease( equation ) );
		qs_timeline.push( Tween.to( mGuessGroup , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , QsearchConstants.H_QUICK_SEARCH - mGuessGroup.height ).ease( equation ) );
		qs_timeline.push( Tween.to( mGuessGroup.mGuessPage.indicatorView , View3DTweenAccessor.POS_XY , displayGroupDuration )
				.target( 0 , QsearchConstants.H_QUICK_SEARCH - QsearchConstants.H_GUESS_GROUP ).ease( equation ) );
		qs_timeline.push( Tween.to( mLettersGroup , View3DTweenAccessor.POS_XY , displayGroupDuration )
				.target( UtilsBase.getScreenWidth() - mLettersGroup.width , QsearchConstants.H_QUICK_SEARCH - mGuessGroup.height - mLettersGroup.height ).ease( equation ) );
		qs_timeline.push( Tween.to( mAllAppList , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , QsearchConstants.H_QUICK_SEARCH - mGuessGroup.height - mAllAppList.height )
				.ease( equation ) );
		qs_timeline.push( Tween.to( mButtonFill , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , 0 ).ease( equation ));
		qs_timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		mLock = true;
	}
	
	public void startQuickSearchAnimQuit(
			boolean manual )
	{
		if( mLock )
		{
			return;
		}
		if( qs_timeline != null )
		{
			qs_timeline.free();
			qs_timeline = null;
		}
		qs_timeline = Timeline.createParallel();
		TweenEquation equation = Cubic.OUT;
		float editGroupDuration = 0.5f;
		float displayGroupDuration = 0.3f;
		if( manual )
		{
			qs_timeline.push( Tween.to( mSearchEditTextGroup , View3DTweenAccessor.POS_XY , editGroupDuration ).target( UtilsBase.getScreenWidth() , 0 ).ease( equation ) );
			qs_timeline.push( Tween.to( Root3D.hotseatBar , View3DTweenAccessor.POS_XY , editGroupDuration ).target( 0 , 0 ).ease( equation ) );
			qs_timeline.push( Tween.to( mTopFill , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -mTopFill.height ).ease( equation ));
			qs_timeline.push( Tween.to( mGuessGroup , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -mGuessGroup.height ).ease( equation ));
			qs_timeline.push( Tween.to( mGuessGroup.mGuessPage.indicatorView , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -mGuessGroup.height ).ease( equation ));
			qs_timeline.push( Tween.to( mLettersGroup , View3DTweenAccessor.POS_XY , displayGroupDuration )
					.target( UtilsBase.getScreenWidth() - mLettersGroup.width , -( mGuessGroup.height + mLettersGroup.height ) ).ease( equation ));
			qs_timeline.push( Tween.to( mAllAppList , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -( mGuessGroup.height + mAllAppList.height ) ).ease( equation ));
			qs_timeline.push( Tween.to( mButtonFill , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -UtilsBase.getScreenHeight() ).ease( equation ) );
			qs_timeline.push( Tween.to( Root3D.hotseatBar.dockGroup.backgroud , View3DTweenAccessor.POS_XY , editGroupDuration ).target( 0 , 0 ).ease( equation ) );
		}
		else
		{
			qs_timeline.push( Tween.to( mTopFill , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -mTopFill.height ).ease( equation ) );
			qs_timeline.push( Tween.to( mGuessGroup , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -mGuessGroup.height ).ease( equation ) );
			qs_timeline.push( Tween.to( mGuessGroup.mGuessPage.indicatorView , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -mGuessGroup.height ).ease( equation ) );
			qs_timeline.push( Tween.to( mLettersGroup , View3DTweenAccessor.POS_XY , displayGroupDuration )
					.target( UtilsBase.getScreenWidth() - mLettersGroup.width , -( mGuessGroup.height + mLettersGroup.height ) ).ease( equation ) );
			qs_timeline.push( Tween.to( mAllAppList , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -( mGuessGroup.height + mAllAppList.height ) ).ease( equation ) );
			qs_timeline.push( Tween.to( mButtonFill , View3DTweenAccessor.POS_XY , displayGroupDuration ).target( 0 , -UtilsBase.getScreenHeight() ).ease( equation ) );
			qs_timeline
					.push( Tween.to( mSearchEditTextGroup , View3DTweenAccessor.POS_XY , editGroupDuration ).target( UtilsBase.getScreenWidth() , 0 ).ease( equation ).delay( displayGroupDuration ) );
			qs_timeline.push( Tween.to( Root3D.hotseatBar , View3DTweenAccessor.POS_XY , editGroupDuration ).target( 0 , 0 ).ease( equation ).delay( displayGroupDuration ) );
			qs_timeline.push( Tween.to( Root3D.hotseatBar.dockGroup.backgroud , View3DTweenAccessor.POS_XY , editGroupDuration ).target( 0 , 0 ).ease( equation ).delay( displayGroupDuration )  );
		}
		qs_timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		mLock = true;
	}
	
	@SuppressWarnings( "rawtypes" )
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source .equals( qs_timeline )&& type == TweenCallback.COMPLETE )
		{
			mLock = false;
			if( mTopFill.y > 0 )
			{
				Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
				if( ActionHolder.getInstance() != null )
					ActionHolder.getInstance().onQuickSearchStarted();
				mGuessGroup.mGuessPage.load();
			}
			else
			{
				hide();
				Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , null );
				iLoongLauncher.getInstance().fireupRecentApp();
			}
		}
		else if( source .equals( qs_reset_timeline ) && type == TweenCallback.COMPLETE )
		{
			//com.iLoong.launcher.Desktop3D.Log.v("Test", "消失了");   
			//hide();
		}
		super.onEvent( type , source );
	}
	public GuessGroup getGuessGroup()
	{
		return mGuessGroup;
	}
	
	public LettersGroup getLettersGroup()
	{
		return mLettersGroup;
	}
	
	public SearchEditTextGroup getSearchEditTextGroup()
	{
		return mSearchEditTextGroup;
	}
	
	public AllAppList getAllAppList()
	{
		return mAllAppList;
	}
	
	public boolean isLock()
	{
		return mLock;
	}
	
	public void load()
	{
		getAllApps();
		if( !mLoaded )
		{
			mGuessGroup.load();
			mAllAppList.load();
			mSearchResultGroup.load();
			mLoaded = true;
		}
	}
	
	public void reLoad()
	{
		if( allApps != null )
			allApps.clear();
		allApps = getAllApp( R3D.qs_app_list_item_title_text_color );
		mGuessGroup.reLoad();
		mAllAppList.reLoad();
		mSearchResultGroup.reLoad();
	}
	
	public static List<SearchApp> getAllApps()
	{
		if( allApps == null )
		{
			allApps = getAllApp( R3D.qs_app_list_item_title_text_color );
		}
		return allApps;
	}
	
	public static List<SearchApp> getAllApp(
			int textColor )
	{
		List<SearchApp> allApps = new ArrayList<SearchApp>();
		SearchApp cooeeicon = null;
		float iconWidth = DefaultLayout.app_icon_size;
		float iconHeight = DefaultLayout.app_icon_size;
		if( AppHost3D.appList.mApps != null )
		{
			if( AppHost3D.appList.mApps.size() == 0 )
			{
				if( AppHost3D.appList.mItemInfos != null && AppHost3D.appList.mItemInfos.size() > 0 )
				{
					for( int i = 0 ; i < AppHost3D.appList.mItemInfos.size() ; i++ )
					{
						if( AppHost3D.appList.mItemInfos.get( i ) instanceof ApplicationInfo )
						{
							ApplicationInfo info = (ApplicationInfo)AppHost3D.appList.mItemInfos.get( i );
							if( info.intent.getComponent() != null )
							{
								ShortcutInfo sInfo = info.makeShortcut();
								String appName = R3D.getInfoName( sInfo );
								Icon3D icon = AppHost3D.appList.iconMap.get( appName );
								if( icon != null && !icon.getHideStatus() )
								{
									cooeeicon = (SearchApp)createIcon( (ShortcutInfo)icon.getItemInfo() , false , textColor );
									cooeeicon.setSize( icon.getWidth() , icon.getHeight() );
									allApps.add( cooeeicon );
									if( iconWidth == DefaultLayout.app_icon_size || iconHeight == DefaultLayout.app_icon_size )
									{
										iconWidth = icon.getWidth();
										iconHeight = icon.getHeight();
									}
								}
							}
						}
						else if( AppHost3D.appList.mItemInfos.get( i ) instanceof FolderInfo )
						{
							FolderInfo info = (FolderInfo)AppHost3D.appList.mItemInfos.get( i );
							ArrayList<ShortcutInfo> contents = ( (UserFolderInfo)info ).contents;
							if( contents != null && contents.size() > 0 )
							{
								for( int j = 0 ; j < contents.size() ; j++ )
								{
									ShortcutInfo sInfo = contents.get( j );
									cooeeicon = (SearchApp)createIcon( sInfo , false , textColor );
									cooeeicon.setSize( DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
									allApps.add( cooeeicon );
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
						if( icon != null && !icon.getHideStatus() )
						{
							cooeeicon = (SearchApp)createIcon( (ShortcutInfo)icon.getItemInfo() , false , textColor );
							cooeeicon.setSize( icon.getWidth() , icon.getHeight() );
							allApps.add( cooeeicon );
						}
					}
				}
			}
		}
		for( int i = 0 ; i < allApps.size() ; i++ )
		{
			allApps.get( i ).setSize( iconWidth , iconHeight );
		}
		return allApps;
	}
	
	private static View3D createIcon(
			ShortcutInfo info ,
			boolean ifShadow ,
			int textColor )
	{
		SearchApp icon = null;
		if( ifShadow )
		{
		}
		else
		{
			Bitmap replaceIcon = null;
			Bitmap bmp = null;
			int findIndex = iLoongLauncher.getInstance().equalHotSeatIntent( info.intent );
			if( findIndex != -1 )
			{
				Bitmap findBmp = iLoongLauncher.getInstance().findHotSeatBitmap( findIndex );
				if( findBmp != null )
				{
					info.hotseatDefaultIcon = true;
					info.setIcon( findBmp );
					info.title = iLoongLauncher.getInstance().getHotSeatString( findIndex );
					info.usingFallbackIcon = false;
					bmp = findBmp;
				}
				else
				{
					iLoongApplication.mIconCache.flushIcon( info.intent );
					bmp = iLoongApplication.mIconCache.getIcon( info.intent );
				}
			}
			else
			{
				iLoongApplication.mIconCache.flushIcon( info.intent );
				bmp = iLoongApplication.mIconCache.getIcon( info.intent );
			}
			if( bmp == IconCache.mDefaultIcon )
			{
				info.usingFallbackIcon = true;
				bmp = IconCache.makeDefaultIcon();
			}
			Bitmap bg;
			bg = Icon3D.getIconBg();
			if( info.intent != null && info.intent.getComponent() != null && info.intent.getComponent().getPackageName() != null && info.intent.getComponent().getClassName() != null )
			{
				replaceIcon = DefaultLayout.getInstance().getDefaultShortcutIcon( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() );
				if( replaceIcon != null )
				{
					bmp = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
					info.setIcon( bmp );
					bg = null;
				}
				else if( DefaultLayout.getInstance().hasSysShortcutIcon( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
				{
					replaceIcon = info.getIcon( iLoongApplication.mIconCache );
					if( replaceIcon != null )
					{
						bmp = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
						info.setIcon( bmp );
						bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
					}
				}
			}
			Utils3D.textColor = textColor;
			Utils3D.textShadow = false;
			if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
			{
				if( !R3D.doNotNeedScale( null , null ) )
				{
					if( info.intent == null || info.intent.getAction() == null )
					{
						if( Icon3D.getIconBg() != null && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
						{
							bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
							bg = Icon3D.getIconBg();
						}
						else
						{
							bg = null;
						}
					}
					else
					{
						if( Icon3D.getIconBg() != null && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT && !iLoongLauncher.getInstance().isDefaultHotseats( info.intent ) )
						{
							bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
							bg = Icon3D.getIconBg();
						}
						else
						{
							bg = null;
						}
					}
					icon = new SearchApp( info.title.toString() , bmp , info.title.toString() , bg , false );
				}
				else
				{
					icon = new SearchApp( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() , false );
				}
			}
			else
			{
				if( !R3D.doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
				{
					if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT && replaceIcon == null )
					{
						bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
					}
					icon = new SearchApp( info.title.toString() , bmp , info.title.toString() , bg , false );
				}
				else
				{
					icon = new SearchApp( info.title.toString() , bmp , info.title.toString() , null , false );
				}
			}
		}
		if( icon != null )
		{
			icon.setItemInfo( info );
		}
		Utils3D.textColor = Color.WHITE;
		Utils3D.textShadow = true;
		return icon;
	}
	
	class PopGroup extends ViewGroup3D
	{
		
		private View3D mBg;
		private View3D mLetter;
		private ArrayList<TextureRegion> mLetterRegions;
		
		public PopGroup(
				String name )
		{
			super( name );
			try
			{
				initLetterRegions();
				setSize( QsearchConstants.W_LETTER_POP , QsearchConstants.H_LETTER_POP );
				setPosition( QsearchConstants.X_LETTER_POP , QsearchConstants.Y_LETTER_POP );
				Bitmap bgBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/qs_pop.png" ) );
				mBg = new View3D( "bg" , new BitmapTexture( bgBmp , true ) );
				mBg.setSize( QsearchConstants.W_LETTER_POP , QsearchConstants.H_LETTER_POP );
				mLetter = new View3D( "letter" );
				mLetter.setSize( width , height );
				mLetter.region = mLetterRegions.get( LettersGroup.b.length - 1 );
				addView( mBg );
				addView( mLetter );
				this.color.a = 0;
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		
		private void initLetterRegions()
		{
			mLetterRegions = new ArrayList<TextureRegion>();
			for( int i = 0 ; i < LettersGroup.b.length ; i++ )
			{
				mLetterRegions.add( drawNameTextureRegion( LettersGroup.b[i] , QsearchConstants.W_LETTER_POP , QsearchConstants.H_LETTER_POP ) );
			}
		}
		
		public View3D getLetter()
		{
			return mLetter;
		}
		
		public ArrayList<TextureRegion> getLetterRegions()
		{
			return mLetterRegions;
		}
	}
	
	public static TextureRegion drawNameTextureRegion(
			String name ,
			final float width ,
			final float height )
	{
		Bitmap backImage = null;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );
		Paint paint = new Paint();
		paint.setAntiAlias( true );
		paint.setDither( true );
		paint.setColor( Color.WHITE );
		paint.setSubpixelText( true );
		paint.setTextSize( QsearchConstants.S_LETTER_POP_TEXT );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		if( name != null )
		{
			canvas.drawText( name , ( width - paint.measureText( name ) - QsearchConstants.S_SCALE * 12 ) / 2 , posY + QsearchConstants.S_SCALE * 3 , paint );
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		return newTextureRegion;
	}
}
