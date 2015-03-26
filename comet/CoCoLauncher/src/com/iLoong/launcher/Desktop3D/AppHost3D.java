package com.iLoong.launcher.Desktop3D;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.webkit.MimeTypeMap;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.media.AudioAdapter;
import com.iLoong.launcher.media.AudioAlbum;
import com.iLoong.launcher.media.AudioAlbum.AudioAlbumView;
import com.iLoong.launcher.media.AudioAlbumAdapter;
import com.iLoong.launcher.media.AudioItem;
import com.iLoong.launcher.media.AudioItem.AudioView;
import com.iLoong.launcher.media.BottomBar;
import com.iLoong.launcher.media.MediaCache;
import com.iLoong.launcher.media.MediaList;
import com.iLoong.launcher.media.PhotoAdapter;
import com.iLoong.launcher.media.PhotoBucket;
import com.iLoong.launcher.media.PhotoBucket.PhotoBucketView;
import com.iLoong.launcher.media.PhotoBucketAdapter;
import com.iLoong.launcher.media.PhotoItem;
import com.iLoong.launcher.media.PhotoItem.PhotoView;
import com.iLoong.launcher.media.VideoAdapter;
import com.iLoong.launcher.media.VideoItem;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class AppHost3D extends ViewGroup3D implements AppBar3D.OnTabChangeListener,DropTarget3D{
	public static final int CONTENT_TYPE_AUDIO_ALBUM = 0;
	public static final int CONTENT_TYPE_PHOTO_BUCKET = 1;
	public static final int CONTENT_TYPE_VIDEO = 2;
	public static final int CONTENT_TYPE_APP = 3;
	public static final int CONTENT_TYPE_PHOTO = 4;
	public static final int CONTENT_TYPE_AUDIO = 5;
	public static final int CONTENT_TYPE_WIDGET = 6;
	public static int currentContentType=CONTENT_TYPE_APP;

	private int photoBucketPageIndex = 0;
	private int audioAlbumPageIndex = 0;
	private int videoPageIndex = 0;
	public static AppBar3D appBar;
	public static AppList3D appList;
	public MediaList mediaList;
	public MediaCache mediaCache;
	public PhotoBucketAdapter photoBucketAdapter;
	public PhotoAdapter photoAdapter;
	public VideoAdapter videoAdater;//zqh
	public AudioAdapter audioAdapter;
	public AudioAlbumAdapter audioAlbumAdapter;
	public static AppPopMenu2 popMenu2;
	public BottomBar bottomBar;
	public static boolean selectState = false;
	public int applistIndex = 0;
	public AppHost3D(String name){
		super(name);
		
		appList = new AppList3D("applist");
		this.addView(appList);
		if(DefaultLayout.enable_explorer){
			mediaList = new MediaList("photolist");
			this.addView(mediaList);

			bottomBar = new BottomBar("bottombar");
			bottomBar.hide();
			this.addView(bottomBar);
			
			audioAlbumAdapter = new AudioAlbumAdapter(mediaList,mediaList.width,mediaList.height);
			videoAdater =new VideoAdapter(mediaList,mediaList.width,mediaList.height);
			audioAdapter = new AudioAdapter(mediaList.width,mediaList.height);
			photoBucketAdapter = new PhotoBucketAdapter(mediaList,mediaList.width,mediaList.height);
			photoAdapter = new PhotoAdapter(mediaList.width,mediaList.height);
		}
	
		
		if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4||DefaultLayout.setup_menu_support_scroll_page) {
		
			popMenu2 = new AppPopMenu2("apppopmenu2");
			popMenu2.setAppList(appList);
			popMenu2.hideNoAnim();
			if(!DefaultLayout.applist_style_classic && !DefaultLayout.hide_appbar){
				appBar = new AppBar3D("appbar");
				this.addView(appBar);
				appBar.setOnTabChangeListener(this);
				appList.setAppBar(appBar);
				
				appList.addScrollListener(appBar);
				appBar.setAppList(appList);		
				appBar.setAppPopMenu2(popMenu2);
				appBar.setAppHost(this);//zqh;
				if(DefaultLayout.enable_explorer){
					this.addView(appBar.popMenu);
				}
				
			}
			this.addView(popMenu2);
		} else {
			
			//xiatian add start	//is_demo_version
			if(DefaultLayout.is_demo_version)
			{
				popMenu2 = new AppPopMenu2("apppopmenu2");
				popMenu2.setAppList(appList);
				popMenu2.hideNoAnim();
			}
			//xiatian add end
			
			if(!DefaultLayout.applist_style_classic && !DefaultLayout.hide_appbar){
				appBar = new AppBar3D("appbar");
				this.addView(appBar);
				appBar.setOnTabChangeListener(this);
				appList.setAppBar(appBar);
				
				appList.addScrollListener(appBar);
				appBar.setAppList(appList);		
				appBar.setAppHost(this);//zqh
				if(DefaultLayout.enable_explorer){
					this.addView(appBar.popMenu);
				}
			}
			
			//xiatian add start	//is_demo_version
			if(DefaultLayout.is_demo_version)
			{
				this.addView(popMenu2);
			}
			//xiatian add end
			
		}
		selectState = false;
		if(DefaultLayout.enable_explorer){
			appBar.setMediaList(mediaList);
			//mediaList.hide();
		}
		currentContentType = CONTENT_TYPE_APP;
		if ( appList != null)
			applistIndex = appList.getIndexInParent();
	}
	
	public void show(){
		super.show();
		appList.show();
		if(DefaultLayout.enable_explorer){
			appBar.initContent();
			if(bottomBar != null && bottomBar.visible){
				mediaList.clearSelect();
				selectState = false;
				bottomBar.hide();
			}
		}
	}
	
	public void hide(){
		super.hide();
		appList.hide();
		if(appBar != null) {
			if (appBar.popMenu != null) {
				if (appBar.popMenu.isVisible()){
					appBar.popMenu.hide();
				}
			}			
		}
		
	}
	
	public void changeContentType(){
		if(bottomBar != null && bottomBar.visible){
			mediaList.clearSelect();
			selectState = false;
			bottomBar.hide();
		}
	}

	@Override
	public void onTabChange(int tabId) {
		if (tabId == AppBar3D.TAB_CONTENT){
			if(AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_APP){
				appList.setCurrentPage(0);
				appList.show();
				if(mediaList != null)mediaList.hide();
			}
			else{
				appList.justHide();
				appList.setCurrentPage(0);
				if(mediaList != null)mediaList.show();
				if(mediaList != null)mediaList.setCurrentPage(0);
			}
			if(currentContentType == CONTENT_TYPE_PHOTO||currentContentType == CONTENT_TYPE_AUDIO){
				bottomBar.curState = BottomBar.STATE_DISPLAY;
				bottomBar.show();
			}
		}
		else{
			appList.show();
			if(mediaList != null)mediaList.hide();
			if (appList.appPageCount < appList.view_list.size())appList.setCurrentPage(appList.appPageCount);
			if(bottomBar != null && bottomBar.visible){
				mediaList.clearSelect();
				selectState = false;
				bottomBar.hide();
			}
		}
	}

	public void clearDragObjs() {
		appList.clearDragObjs();
	}

	public void setIconCache(IconCache iconCache) {
		appList.setIconCache(iconCache);
	}

	public void addApps(ArrayList<ApplicationInfo> apps) {
		appList.addApps(apps);
	}
	
	public void addFolders(ArrayList<FolderInfo> folders) {
	    appList.addFolders(folders);
	}
	
	public void addWidget(Widget3DShortcut widget) {
		appList.addWidget(widget);
	}

	public void setWidgets(ArrayList<WidgetShortcutInfo> widgets) {
		appList.setWidgets(widgets);
	}

	public void setApps(ArrayList<ApplicationInfo> apps) {
		appList.setApps(apps);
	}
	
	public void setFolders(ArrayList<FolderInfo> folders) {
		appList.setFolders(folders);
	}
	
	public void showVideos(){
		savePageIndex();
		currentContentType = CONTENT_TYPE_VIDEO;
		mediaList.indicatorPaddingBottom = 0;
		mediaList.setAdapter(videoAdater);
		mediaList.page_index = videoPageIndex;
		mediaList.syncPages();// begin to show video page
	}
	
	public void showPhotos(ArrayList<PhotoItem> photos) {
		savePageIndex();
		currentContentType = CONTENT_TYPE_PHOTO;
		mediaList.indicatorPaddingBottom = R3D.bottom_bar_height;
		mediaList.setAdapter(photoAdapter);
		photoAdapter.setPhotos(photos);
		mediaList.syncPages();
	}
	
	public void showPhotoBuckets(){
		savePageIndex();
		currentContentType = CONTENT_TYPE_PHOTO_BUCKET;
		mediaList.indicatorPaddingBottom = 0;
		mediaList.setAdapter(photoBucketAdapter);
		mediaList.page_index = photoBucketPageIndex;
		mediaList.syncPages();
	}
	
	public void showAudioAlbums(){
		savePageIndex();
		currentContentType = CONTENT_TYPE_AUDIO_ALBUM;
		mediaList.indicatorPaddingBottom = 0;
		mediaList.setAdapter(audioAlbumAdapter);
		mediaList.page_index = audioAlbumPageIndex;
		mediaList.syncPages();
	}
	
	public void showAudios(ArrayList<AudioItem> audios) {
		savePageIndex();
		currentContentType = CONTENT_TYPE_AUDIO;
		mediaList.indicatorPaddingBottom = R3D.bottom_bar_height;
		mediaList.setAdapter(audioAdapter);
		audioAdapter.setAudios(audios);
		mediaList.syncPages();
	}
	
	private void savePageIndex(){
		switch(currentContentType){
		case CONTENT_TYPE_AUDIO_ALBUM:
			audioAlbumPageIndex = mediaList.page_index;
			break;
		case CONTENT_TYPE_PHOTO_BUCKET:
			photoBucketPageIndex = mediaList.page_index;
			break;
		case CONTENT_TYPE_VIDEO:
			videoPageIndex = mediaList.page_index;
			break;
		}
	}

	public synchronized void reomveApps(ArrayList<ApplicationInfo> apps,boolean permanent) {
		appList.reomveApps(apps,permanent);
	}
	
	public void updateApps(ArrayList<ApplicationInfo> apps) {
		appList.updateApps(apps);
	}
	
	public void removeWidget(String packageName) {
		appList.removeWidget(packageName);
	}

	public int estimateWidgetCellWidth(int cellHSpan) {
		// TODO Auto-generated method stub
		return appList.estimateWidgetCellWidth(cellHSpan);
	}
	
	public int estimateWidgetCellHeight(int cellVSpan) {
		// TODO Auto-generated method stub
		return appList.estimateWidgetCellHeight(cellVSpan);
	}
	
	public NPageBase getContentList(){
		if(!DefaultLayout.enable_explorer)return appList;
		if(appList.visible)return appList;
		return mediaList;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		if(keycode == KeyEvent.KEYCODE_BACK && appList.xScale == 0)
		{
			if(popMenu2 != null && popMenu2.isVisible() && popMenu2.touchable) {
				popMenu2.hide();
				return true;
			}
			else if(appBar != null && appBar.popMenu != null && appBar.popMenu.isVisible() && appBar.popMenu.touchable){
				appBar.popMenu.hide();
				return true;
			}
			else if(currentContentType == CONTENT_TYPE_PHOTO){
				if(bottomBar.curState == BottomBar.STATE_EDIT){
					mediaList.clearSelect();
					selectState = false;
					bottomBar.curState = BottomBar.STATE_DISPLAY;
					return true;
				}
				showPhotoBuckets();
				bottomBar.hide();
				return true;
			}
			else if(currentContentType == CONTENT_TYPE_AUDIO){
				if(bottomBar.curState == BottomBar.STATE_EDIT){
					mediaList.clearSelect();
					selectState = false;
					bottomBar.curState = BottomBar.STATE_DISPLAY;
					return true;
				}
				showAudioAlbums();
				bottomBar.hide();
				return true;
			}
			else if(bottomBar != null && bottomBar.visible){
				mediaList.clearSelect();
				selectState = false;
				bottomBar.hide();
				return true;
			}
			else if(mediaList != null && mediaList.isVisible()){
				viewParent.onCtrlEvent(appList, AppList3D.APP_LIST3D_KEY_BACK);
				return true;
			}
		} else if (keycode == KeyEvent.KEYCODE_MENU && appList.xScale == 0) {
			if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4||DefaultLayout.setup_menu_support_scroll_page) {
				if (popMenu2 != null && popMenu2.isVisible()) {
					popMenu2.hide();
					return true;
				}
				else if (appBar != null && appBar.popMenu != null && appBar.popMenu.isVisible()) {
					appBar.popMenu.hide();
					return true;
				}
				else if (popMenu2 != null && !popMenu2.isVisible() && appList.canPopMenu()) {
					popMenu2.show();
					return true;
				}
				else if(currentContentType==CONTENT_TYPE_AUDIO_ALBUM && (mediaList!=null&&mediaList.visible)){
					if(bottomBar.visible){
						bottomBar.hideAudio();
						bottomBar.hide();
					}
					else{
						bottomBar.curState = BottomBar.STATE_EDIT;
						bottomBar.showAudio();
						bottomBar.show();
					}
				}
			} else {
				
				//xiatian start	//is_demo_version
				//xiatian del start
//				if (appBar != null && appBar.popMenu != null) {
//					if (appBar.popMenu.isVisible())
//						appBar.popMenu.hide();
//					else if (appList.canPopMenu())
//						appBar.popMenu.show();
//					return true;
//				}
				//xiatian del end
				//xiatian add start
				if(DefaultLayout.is_demo_version)
				{
					if (popMenu2 != null && popMenu2.isVisible()) {
						popMenu2.hide();
						return true;
					}
					else if (appBar != null && appBar.popMenu != null && appBar.popMenu.isVisible()) {
						appBar.popMenu.hide();
						return true;
					}
					else if (popMenu2 != null && !popMenu2.isVisible() && appList.canPopMenu()) {
						popMenu2.show();
						return true;
					}
					else if(currentContentType==CONTENT_TYPE_AUDIO_ALBUM && (mediaList!=null&&mediaList.visible)){
						if(bottomBar.visible){
							bottomBar.hideAudio();
							bottomBar.hide();
						}
						else{
							bottomBar.curState = BottomBar.STATE_EDIT;
							bottomBar.showAudio();
							bottomBar.show();
						}
					}
				}
				else
				{
					if (appBar != null && appBar.popMenu != null) {
						if (appBar.popMenu.isVisible())
							appBar.popMenu.hide();
						else if (appList.canPopMenu())
							appBar.popMenu.show();
						return true;
					}
				}
				//xiatian add end
				//xiatian end
					
			}	
				
		}
		return super.keyUp(keycode);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		//teapotXu add start for merge MIUI_V5_folder style	
		if ((ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder ) && DefaultLayout.blur_enable && (folderOpened  && appHost_folder != null))
		{
			MiuiV5FolderBoxBlurInAppHost(batch, parentAlpha);
		} else{	
			super.draw(batch, parentAlpha);
		}		
		//super.draw(batch, parentAlpha);
	//teapotXu add end 
	}

	
	public void sortApp(int checkId) {
		if(appList.sortId != checkId)appList.sortApp(checkId,true);
	}

	public void resume() {
		if(appList != null)appList.resume();
	}

	
	@Override
	public boolean onCtrlEvent(View3D sender, int event_id) {
		if((sender instanceof PhotoBucketView)){
			switch(event_id){
			case PhotoBucket.EVENT_PHOTO_BUCKET_CLICK:
				PhotoBucketView v = (PhotoBucketView)sender;
				photoAdapter.setPhotoBucketView(v);
				showPhotos(v.bucket.photos);
				bottomBar.curState = BottomBar.STATE_DISPLAY;
				bottomBar.displayRegion = v.region;
				bottomBar.show();
				return true;
			case PhotoBucket.EVENT_PHOTO_BUCKET_LONGCLICK:
				bottomBar.content.put(BottomBar.SELECT_ALL, true);
				bottomBar.content.put(BottomBar.SHARE, true);
				bottomBar.content.put(BottomBar.DELETE, true);
				bottomBar.content.put(BottomBar.SETTING, false);
				bottomBar.curState = BottomBar.STATE_EDIT;
				bottomBar.hideAudio();
				bottomBar.show();
				mediaList.clearSelect();
				selectState = true;
				return true;
			}
		}
		if((sender instanceof AudioAlbumView)){
			switch(event_id){
			case AudioAlbum.EVENT_AUDIO_ALBUM_CLICK:
				AudioAlbumView v = (AudioAlbumView)sender;
				showAudios(v.album.audios);
				bottomBar.curState = BottomBar.STATE_DISPLAY;
				bottomBar.displayRegion = v.region;
				bottomBar.show();
				return true;
				
			case AudioAlbum.EVENT_AUDIO_ALBUM_LONGCLICK:
				bottomBar.content.put(BottomBar.SELECT_ALL, true);
				bottomBar.content.put(BottomBar.SHARE, false);
				bottomBar.content.put(BottomBar.DELETE, true);
				bottomBar.content.put(BottomBar.SETTING, false);
				bottomBar.curState = BottomBar.STATE_EDIT;
				bottomBar.hideAudio();
				bottomBar.show();
				mediaList.clearSelect();
				selectState = true;
				return true;
			}
		}
		else if(sender instanceof PhotoView){
			switch(event_id){
			case PhotoItem.EVENT_PHOTO_LONGCLICK:
				bottomBar.content.put(BottomBar.SELECT_ALL, true);
				bottomBar.content.put(BottomBar.SHARE, true);
				bottomBar.content.put(BottomBar.DELETE, true);
				bottomBar.content.put(BottomBar.SETTING, true);
				bottomBar.curState = BottomBar.STATE_EDIT;
				bottomBar.hideAudio();
				mediaList.clearSelect();
				selectState = true;
				return true;
			}
		}
		else if(sender instanceof AudioView){
			switch(event_id){
			case AudioItem.EVENT_AUDIO_LONGCLICK:
				bottomBar.content.put(BottomBar.SELECT_ALL, true);
				bottomBar.content.put(BottomBar.SHARE, true);
				bottomBar.content.put(BottomBar.DELETE, true);
				bottomBar.content.put(BottomBar.SETTING, true);
				bottomBar.curState = BottomBar.STATE_EDIT;
				bottomBar.hideAudio();
				mediaList.clearSelect();
				selectState = true;
				return true;
			}
		}
		else if(sender instanceof BottomBar){
			switch(event_id){
			case BottomBar.SELECT_ALL:
				mediaList.selectAll();
				return true;
			case BottomBar.SHARE:
				ArrayList<Uri> data = mediaList.getSelectedData();
				if(data.size()==0)return true;
				if(currentContentType == CONTENT_TYPE_AUDIO || currentContentType == CONTENT_TYPE_PHOTO)bottomBar.curState = BottomBar.STATE_DISPLAY;
				else bottomBar.hide();
				selectState = false;
				Intent shareIntent =new Intent();
				shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
				shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, data);
				if(currentContentType == CONTENT_TYPE_PHOTO || currentContentType == CONTENT_TYPE_PHOTO_BUCKET)
					shareIntent.setType("image/*");
				if(currentContentType == CONTENT_TYPE_AUDIO || currentContentType == CONTENT_TYPE_AUDIO_ALBUM)
					shareIntent.setType("audio/*");
				if(currentContentType == CONTENT_TYPE_VIDEO)
					shareIntent.setType("video/*");
				SendMsgToAndroid.startActivity(Intent.createChooser(shareIntent,iLoongLauncher.getInstance().getResources().getString(RR.string.media_share)));
				return true;
			case BottomBar.DELETE:
				ArrayList<File> files = MediaCache.getInstance().startDelete();
				mediaList.onDelete();
				MediaCache.getInstance().exeDelete(files);
				mediaList.syncPages();
				if(currentContentType == CONTENT_TYPE_AUDIO || currentContentType == CONTENT_TYPE_PHOTO)bottomBar.curState = BottomBar.STATE_DISPLAY;
				else bottomBar.hide();
				selectState = false;
				return true;
			case BottomBar.SETTING:
				ArrayList<Uri> data2 = mediaList.getSelectedData();
				if(data2.size()!=1)return true;
				if(currentContentType == CONTENT_TYPE_AUDIO || currentContentType == CONTENT_TYPE_PHOTO)bottomBar.curState = BottomBar.STATE_DISPLAY;
				else bottomBar.hide();
				selectState = false;
				String mimeType = "";
				if(currentContentType == CONTENT_TYPE_PHOTO || currentContentType == CONTENT_TYPE_PHOTO_BUCKET)
					mimeType = "image/*";
				if(currentContentType == CONTENT_TYPE_AUDIO || currentContentType == CONTENT_TYPE_AUDIO_ALBUM)
					mimeType = "audio/*";
				if(currentContentType == CONTENT_TYPE_VIDEO)
					mimeType = "video/*";
				Intent settingIntent = createSetAsIntent(data2.get(0), mimeType);
				SendMsgToAndroid.startActivity(settingIntent);
				return true;
			case BottomBar.ALBUM:
				if(currentContentType==CONTENT_TYPE_AUDIO_ALBUM && audioAlbumAdapter != null){
					if(audioAlbumAdapter.setCurrentAlbums(AudioAlbum.TYPE_ALBUM))
						mediaList.syncPages();
				}
				bottomBar.hideAudio();
				bottomBar.hide();
				return true;
			case BottomBar.ARTIST:
				if(currentContentType==CONTENT_TYPE_AUDIO_ALBUM && audioAlbumAdapter != null){
					if(audioAlbumAdapter.setCurrentAlbums(AudioAlbum.TYPE_ARTIST))
						mediaList.syncPages();
				}
				bottomBar.hideAudio();
				bottomBar.hide();
				return true;
			case BottomBar.FOLDER:
				if(currentContentType==CONTENT_TYPE_AUDIO_ALBUM && audioAlbumAdapter != null){
					if(audioAlbumAdapter.setCurrentAlbums(AudioAlbum.TYPE_FOLDER))
						mediaList.syncPages();
				}
				bottomBar.hideAudio();
				bottomBar.hide();
				return true;
			case BottomBar.BACK:
				if(currentContentType == CONTENT_TYPE_PHOTO){
					showPhotoBuckets();
					bottomBar.hide();
					return true;
				}
				else if(currentContentType == CONTENT_TYPE_AUDIO){
					showAudioAlbums();
					bottomBar.hide();
					return true;
				}
				return true;
			case BottomBar.EDIT:
				bottomBar.content.put(BottomBar.SELECT_ALL, true);
				bottomBar.content.put(BottomBar.SHARE, true);
				bottomBar.content.put(BottomBar.DELETE, true);
				bottomBar.content.put(BottomBar.SETTING, true);
				bottomBar.curState = BottomBar.STATE_EDIT;
				mediaList.clearSelect();
				selectState = true;
				return true;
			}
		}
		else if((sender instanceof VideoItem.VideoView)){
			switch(event_id){
				
				case VideoItem.EVENT_VIDEO_LONGCLICK:
					bottomBar.content.put(BottomBar.SELECT_ALL, true);
					bottomBar.content.put(BottomBar.SHARE, true);
					bottomBar.content.put(BottomBar.DELETE, true);
					bottomBar.content.put(BottomBar.SETTING, false);
					bottomBar.curState = BottomBar.STATE_EDIT;
					bottomBar.hideAudio();
					bottomBar.show();
					mediaList.clearSelect();
					selectState = true;
				return true;
			}
		}
	  //teapotXu add start for Folder in Mainmenu
		else if(DefaultLayout.mainmenu_folder_function == true && sender instanceof DragLayer3D)
			{
				switch(event_id)
				{
				case DragLayer3D.MSG_DRAG_INBORDER:
					appList.onCtrlEvent(sender, event_id);
					return true;
				}
			}
			else if (sender instanceof FolderIcon3D) {
				FolderIcon3D folderIcon3D = (FolderIcon3D)sender;
				switch(event_id)
				{
				case FolderIcon3D.MSG_FOLDERICON_FROME_APPLIST:

					if(ThemeManager.getInstance().getBoolean("miui_v5_folder")||DefaultLayout.miui_v5_folder == true)
					{
						//小米V5文件夹风格
						this.touchable = false;
					}
					else
					{
						appList.setAppListHideReason(AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN);
						appList.hide();
						appBar.hide();
					}
					
					//?????FolderIcon3D
					//this.touchable = false;
					appHost_folder = folderIcon3D;
					
					appHost_dragLayer.removeDropTarget(folderIcon3D);
					folderOpened = true;
					appList.is_maimenufolder_open = true;
					appList.mOpenFolderIcon = folderIcon3D;
					folderIcon3D.is_applist_folder_no_refresh = true;
					
					//??addview???????????addView????folderIcon3D ??viewParent??
					addView(folderIcon3D);
					return true;
				case FolderIcon3D.MSG_FOLDERICON_TO_APPLIST:

					if((ThemeManager.getInstance().getBoolean("miui_v5_folder")||DefaultLayout.miui_v5_folder == true))
					{
						Tween tween = this.startTween(
								View3DTweenAccessor.SCALE_XY, 
								Cubic.OUT, 
								0.3f, 
								1f,
								1f, 
								0f).setCallback(this); //todo：这里有可能收不到回调！！！！！
						
						
						//removeView(folderIcon3D);
						appHost_tag = folderIcon3D;
						folderIcon3D.is_applist_folder_no_refresh = false;
						
						//teapotXu Add 类似Root3D: 
						/*避免这里的动画有时候收不到回调，启动定时器�?
						 * 定时器到时长度一定要大于这里启动动画的时长，保证动画的回调函数能跑到一次，
						 * 避免回调跑不到，root3d.touchable为False，触摸无法反应，定屏问题的解�?
						 * added by zfshi 2012-08-19*/
						startProtectedTimer((long)(appHostFolderTweenDuration*4*1000));
						
						
						//appHost.setAppHostTag(folderIcon3D);
						//appHost.show();
					}
					else
					{
						//before appList show, add folder backto applist
						
						appHost_tag = folderIcon3D;
						folderIcon3D.is_applist_folder_no_refresh = false;
						dealFolderInAppListTweenFinish();
						appList.setAppListHideReason(AppList3D.APP_HIDE_REASON_FOR_NONE);
						
						appList.show();
						appBar.show();
						
					}
					return true;
				case FolderIcon3D.MSG_FOLDERICON_TO_CELLLAYOUT:
					if(ThemeManager.getInstance().getBoolean("miui_v5_folder")||DefaultLayout.miui_v5_folder == true)
					{
						//none
					}
					else
					{
						appList.show();
						appBar.show();
					}
					break;
				case FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE: {
					
					//In the folder of Mainmenu, this message do nothing now
						return true;
				}					
				case FolderIcon3D.MSG_FOLDERICON_BACKTO_ORIG:
					ArrayList<View3D> child_list= (ArrayList<View3D>) folderIcon3D.getTag();
					appHost_folder_DropList_backtoOrig( child_list);
					return true;
				}
			}
			//teapotXu add end for Folder in Mainmenu	
		
		return super.onCtrlEvent(sender, event_id);
	}
	
	public static Intent createSetAsIntent(Uri uri, String mimeType) {
        // Infer MIME type if missing for file URLs.
        if (uri.getScheme().equals("file")) {
            String path = uri.getPath();
            int lastDotIndex = path.lastIndexOf('.');
            if (lastDotIndex != -1) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        uri.getPath().substring(lastDotIndex + 1).toLowerCase());
            }
        }

        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.setDataAndType(uri, mimeType);
        intent.putExtra("mimeType", mimeType);
        return intent;
    }
	
//teapotXu add start for Folder in Mainmenu
	
		public View3D appHost_tag = null;
		public boolean folderOpened = false;
		public boolean closeFolder2goHome = false;
		private FolderIcon3D appHost_folder = null;
		private DragLayer3D appHost_dragLayer = null;
		
		
		private Timer timer=null;
		private appHostFolderTweenTask TweenTimerTask;	
		private static float appHostFolderTweenDuration=0.1f;
		
		
		public void setDragLayer(View3D v){
			this.appHost_dragLayer = (DragLayer3D) v;
		}
		
		public FolderIcon3D getFolderIconOpendInAppHost()
		{
			return appHost_folder;
		}
		
		public void setFolderIconOpenedInAppHost(FolderIcon3D folder)
		{
			appHost_folder = folder;
		}
		
		public int getAppList3DMode()
		{
			return appList.mode;
		}

	    @Override
		public void onEvent(int type, BaseTween source)
		{

			//teapotXu add start for Folder in Mainmenu
			if ( DefaultLayout.mainmenu_folder_function == true 
					 && source == this.getTween()
					 && type == TweenCallback.COMPLETE) 
			{
				dealFolderInAppListTweenFinish();
			}
			//teapotXu add end for Folder in Mainmenu
		}
		
			
		
		
		@Override
		public boolean onDrop(ArrayList<View3D> list, float x, float y) {
			// TODO Auto-generated method stub
			Log.v("cooee","AppHost3D---onDrop ---enter --- ");
			if(appList.pointerInAbs(x, y) && (appList.isVisibleInParent() || (appList.isVisibleInParent() == false && appList.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN)))
			{
				Log.v("cooee","AppHost3D---onDrop --- in AppList area --- ");
				appList.onDrop(list, x, y);
			}
			else if(appBar.pointerInAbs(x, y) && (appBar.isVisibleInParent() || (appList.isVisibleInParent() == false && appList.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN)))
			{
				//
				Log.v("cooee","AppHost3D---onDrop --- in AppBar area ,and deal with Applist --- ");
				appList.cellDropType = AppList3D.CELL_DROPTYPE_SINGLE_DROP;
				appList.onDrop(list, x, appList.y + appList.getHeight() -1);
			}
			else
			{
				Log.v("cooee","AppHost3D---onDrop --- in error area --- x:" + x + "---y: " + y);
				return false;
			}
			return true;
		}

		@Override
		public boolean onDropOver(ArrayList<View3D> list, float x, float y) {
			// TODO Auto-generated method stub
			
			if(appList.pointerInAbs(x, y) && (appList.isVisibleInParent() || (appList.isVisibleInParent() == false && appList.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN)))
			{
				appList.onDropOver(list, x, y);
			}
			else if(appBar.pointerInAbs(x, y) && (appBar.isVisibleInParent() || (appList.isVisibleInParent() == false && appList.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN)))
			{
				//appList.onDropOver(list, x, y);
			}
			else
			{
				return false;
			}
			return true;
		}

	    public boolean onDropLeave() {
	    	//CleanDropStatus();
	    	return true;
	    }
	    
	    public void addBackInScreen(View3D child, int x, int y) {
	    	appList.addBackInScreen(child, x, y);
	    }

	    public void removeDragViews(ArrayList<View3D> removedViewList)
	    {
	    	appList.removeDragViews(removedViewList);
	    }

	    public void setDragviewAddFromFolderStatus(boolean status)
	    {
	    	appList.is_dragview_from_folder = status;
	    }
	    
	    public boolean getDragviewAddFromFolderStatus()
	    {
	    	return appList.is_dragview_from_folder;
	    }     
	    
		private void dealFolderInAppListTweenFinish() {
		{
			if((ThemeManager.getInstance().getBoolean("miui_v5_folder")||DefaultLayout.miui_v5_folder == true))
			{
				if(this.touchable)
					return;
				
			}
			
				View3D view = appHost_tag;
				
			 stopProtectedTimer();
				if (view == null) {
					return;
				}
				else if (view instanceof FolderIcon3D) 
				{
					FolderIcon3D folderIcon3D = (com.iLoong.launcher.Folder3D.FolderIcon3D) view;
					
					if(folderIcon3D.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D)
					{
						addBackInScreen(appHost_folder, appHost_folder.mInfo.x,
								appHost_folder.mInfo.y);
					}
					
					folderOpened = false;
					appList.is_maimenufolder_open = false;
					appList.mOpenFolderIcon = null;
					if (folderIcon3D.getParent() == this)
						folderIcon3D.remove();
					folderIcon3D = null;
					
				if (appHost_dragLayer.isVisible() && !appHost_dragLayer.draging) 
				{
					if (appHost_dragLayer.getDragList().size() > 0
							&& appHost_dragLayer.onDrop() == null) {
//						dropListBacktoOrig(dragLayer.getDragList());
					}
					
					appHost_dragLayer.removeAllViews();
					appHost_dragLayer.hide();
				}
					
				if (closeFolder2goHome) {
					closeFolder2goHome = false;
//					workspace.scrollTo(workspace.getHomePage());
				}
//				hotseatBar.touchable = true;
//				workspace.touchable = true;
				}
			}
			this.touchable = true;
		}	    
		
		private void appHost_folder_DropList_backtoOrig(ArrayList<View3D> child_list)
		{
			View3D view = child_list.get(0);

			View3D parent = view.getParent();
			ItemInfo info = null;
			
			if (view instanceof IconBase3D)
				info = ((IconBase3D)view).getItemInfo();
			
			if (info != null /*&& info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP*/) /* is not come from mainmenu*/
			{
				for (int i = 0; i < child_list.size(); i++) {
					view = child_list.get(i);
					addBackInScreen(view, 0, 0);
				}
			}
		}
		
		
		//teapotXu add timer function
		class appHostFolderTweenTask extends TimerTask{
			@Override
			public void run() {
				dealFolderInAppListTweenFinish();
			}
		}
		private void startProtectedTimer(long duration)
		{
			//Log.d("launcher", "start root timer");
			 if(timer != null)timer.cancel();   
			   TweenTimerTask = new appHostFolderTweenTask();
				timer = new Timer();
				timer.schedule(TweenTimerTask, duration);
		}
		private void stopProtectedTimer()
		{
			//Log.d("launcher", "stop root timer");
			if(timer != null)
			{
				TweenTimerTask.cancel();
				timer.cancel();
				timer=null;
			}
		}	
		

		//	zhujieping add begin
		private void MiuiV5FolderBoxBlurInAppHost(SpriteBatch batch, float parentAlpha)
		{
			
			if (appHost_folder.captureCurScreen) 
			{	
				super.draw(batch, parentAlpha);
				
				// draw to fbo
				appHost_folder.fbo.begin();
				Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			 	
				// 先绘制壁纸到fbo上
				if (FolderIcon3D.wallpaperTextureRegion != null)
				{	
					int wpWidth = FolderIcon3D.wallpaperTextureRegion.getTexture().getWidth();
					int wpHeight = FolderIcon3D.wallpaperTextureRegion.getTexture().getHeight();
					batch.draw(FolderIcon3D.wallpaperTextureRegion, appHost_folder.wpOffsetX,
						0, wpWidth, wpHeight);
				}
				//if (Workspace3D.WorkspaceStatus != WorkspaceStatusEnum.EditMode)
				{
					this.setScale(0.8f, 0.8f);
					this.transform = true;
				}
				// 绘制当前屏幕画面到fbo上
				super.draw(batch, parentAlpha);
				appHost_folder.fbo.end();		
				//if (Workspace3D.WorkspaceStatus != WorkspaceStatusEnum.EditMode)
				{
					appHost_folder.mFolderIndex = appHost_folder.getViewIndex(appHost_folder.mFolderMIUI3D);
					appHost_folder.mFolderMIUI3D.remove();
					
					this.setScale(1.0f, 1.0f);
					this.startTween(View3DTweenAccessor.SCALE_XY, Quint.IN, 
							DefaultLayout.blurDuration, 0.8f, 0.8f, 0).setCallback(new TweenCallback() {
								
								@Override
								public void onEvent(int arg0, BaseTween arg1) {
									// TODO Auto-generated method stub
									if(arg0==TweenCallback.COMPLETE){
										setScale(1.0f, 1.0f);
									}
								}
							});	
					
				} 
//				else {
//					this.transform = true;
//					
//					folder.mFolderIndex = folder.getViewIndex(folder.mFolderMIUI3D);
//					folder.mFolderMIUI3D.remove();
//				}
				
				appHost_folder.captureCurScreen = false;
				appHost_folder.blurBegin = true;
			} else {
				super.draw(batch, parentAlpha);
				if (transform)
				{
					this.appHost_folder.mFolderMIUI3D.draw(batch, parentAlpha);
				}

				if (appHost_folder.blurBegin)
				{
					if ((appHost_folder.blurCount % 2) == 0)
					{
						appHost_folder.renderTo(appHost_folder.fbo, appHost_folder.fbo2);
					} else {
						appHost_folder.renderTo(appHost_folder.fbo2, appHost_folder.fbo);
					}
					appHost_folder.blurCount++;

					if (appHost_folder.blurCount >= (DefaultLayout.blurInterate<<1))
					{
						appHost_folder.blurBegin = false;
					}
				}
				
				if (!appHost_folder.blurCompleted)
				{
					int count = (DefaultLayout.blurInterate<<1) - appHost_folder.blurCount;
					for (int i = 0; i < count; i++)
					{
						if ((appHost_folder.blurCount % 2) == 0)
						{
							appHost_folder.renderTo(appHost_folder.fbo, appHost_folder.fbo2);
						} else {
							appHost_folder.renderTo(appHost_folder.fbo2, appHost_folder.fbo);
						}
						appHost_folder.blurCount++;
					}
					
					if (appHost_folder.blurredView == null)
					{
						float scaleFactor = 1/DefaultLayout.fboScale;
						appHost_folder.blurredView = new ImageView3D("blurredView", appHost_folder.fboRegion);
						appHost_folder.blurredView.show();
						appHost_folder.blurredView.setScale(scaleFactor, scaleFactor);
						appHost_folder.blurredView.setPosition(appHost_folder.blurredView.getWidth()/2+(Gdx.graphics.getWidth()/2-appHost_folder.blurredView.getWidth()), 
								appHost_folder.blurredView.getHeight()/2+(Gdx.graphics.getHeight()/2-appHost_folder.blurredView.getHeight()));
						Log.v("blur", "aaaa "+appHost_folder.blurredView);
						//if (Workspace3D.WorkspaceStatus != WorkspaceStatusEnum.EditMode)
						{
							this.transform = false;
							this.setScale(1.0f, 1.0f);
							
							appHost_folder.addViewAt(appHost_folder.mFolderIndex, appHost_folder.mFolderMIUI3D);
							appHost_folder.addView(appHost_folder.blurredView);
//							this.hideOtherView();
							//appBar.hide();
						} 
//						else {
//							this.transform = false;
//							
////							this.addViewBefore(workspace, folder.blurredView);
//							this.addView(appHost_folder.mFolderMIUI3D);
//						}
					}
					
					appHost_folder.blurCompleted = true;
				}
			}
		}			
	//teapotXu add end for Folder in Mainmenu	
}
