package com.iLoong.launcher.cling;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.PageIndicator3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.HotDockGroup;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class ClingManager implements View.OnClickListener , MenuActionListener
{
	
	public static final String INTRODUCTION_DISMISSED_KEY = "cling.introduction.dismissed";
	public static final String WORKSPACE_DISMISSED_KEY = "cling.workspace";
	public static final int ALLAPP_CLING = 4;
	public static final int PAGEINDICATOR_CLING = 5;
	public static final int FOLDER_CLING = 6;
	//    public static final String PAGECONTAINER_DISMISSED_KEY = "cling.pagecontainer";
	//    public static final int PAGESELECT_CLING = 3;
	//    public static final int PAGEEDIT_CLING = 4;
	public static final String SELECT_DISMISSED_KEY = "cling.select.dismissed";
	public static final int SELECT_CLING = 2;
	//    public static final String SETTING_DISMISSED_KEY = "cling.setting.dismissed";
	//    public static final int SETTING_CLING = 0;
	//    public static final String WIDGET_DISMISSED_KEY = "cling.widget.dismissed";
	//    public static final int WIDGET_CLING = 1;
	public static final int END_CLING = 10;
	public static String ALLAPP = "allapp";
	public static String PAGEINDICATOR = "pageindicator";
	public static String FOLDER = "folder";
	//    public static String CIRCLE = "circle";
	//    public static String PAGESELECT = "pageselect";
	//    public static String PAGEEDIT = "pageedit";
	public static String SELECT = "select";
	//    public static String SETTING = "setting";
	//    public static String WIDGET = "widget";
	public static int CLING_STATE_DISMISSED = 0;
	public static int CLING_STATE_WAIT = 1;
	public static int CLING_STATE_SHOW = 2;
	public static int CLING_STATE_CANCEL = 3;
	public boolean pageIndicatorClingFired = false;
	public boolean folderClingFired = false;
	//    private boolean circleClingFired = false;
	private boolean selectClingFired = false;
	//    public boolean widgetClingFired = false;
	public static ClingManager clingManager;
	public iLoongLauncher launcher;
	public Context context;
	public SharedPreferences prefs;
	private Cling cling;
	private ImageView okButton;
	private ImageView clingPoint;
	private FrameLayout.LayoutParams pointParams;
	private FrameLayout.LayoutParams layoutParams;
	private int clingState = 1000;
	private List<ClingTarget> targets;
	private boolean hide = false;
	private boolean wait = false;
	private boolean showCling = false;
	
	public static ClingManager getInstance()
	{
		if( clingManager == null )
		{
			synchronized( ClingManager.class )
			{
				if( clingManager == null )
					clingManager = new ClingManager();
			}
		}
		return clingManager;
	}
	
	public void setLauncher(
			iLoongLauncher launcher )
	{
		if( !showCling )
			return;
		this.launcher = launcher;
		context = launcher.getApplicationContext();
		prefs = PreferenceManager.getDefaultSharedPreferences( context );
		layoutParams = new FrameLayout.LayoutParams( android.view.ViewGroup.LayoutParams.FILL_PARENT , android.view.ViewGroup.LayoutParams.FILL_PARENT );
		okButton = new ImageView( context );
		okButton.setTag( "ok" );
		okButton.setBackgroundResource( RR.drawable.cling_ok );
		FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams( android.view.ViewGroup.LayoutParams.WRAP_CONTENT , android.view.ViewGroup.LayoutParams.WRAP_CONTENT );
		buttonParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		buttonParams.setMargins( 0 , 20 , 20 , 0 );
		okButton.setLayoutParams( buttonParams );
		okButton.setOnClickListener( this );
		clingPoint = new ImageView( context );
		Drawable bg = launcher.getApplicationContext().getResources().getDrawable( RR.drawable.sh );
		clingPoint.setBackgroundDrawable( bg );
		clingPoint.setOnClickListener( this );
		pointParams = new FrameLayout.LayoutParams( android.view.ViewGroup.LayoutParams.WRAP_CONTENT , android.view.ViewGroup.LayoutParams.WRAP_CONTENT );
		pointParams.gravity = Gravity.TOP | Gravity.LEFT;
		int x = 15;
		int y = (int)( 50 * context.getResources().getDisplayMetrics().density );
		pointParams.setMargins( x , y , 0 , 0 );
		clingPoint.setLayoutParams( pointParams );
		clingPoint.setVisibility( View.GONE );
		launcher.addContentView( clingPoint , pointParams );
		//    	launcher.getWindowManager().addView(
		//    			clingPoint, pointParams);
		targets = new ArrayList<ClingTarget>();
		setActionListener();
	}
	
	public boolean checkCling()
	{
		return false;
	}
	
	public void refreshClingState()
	{
		//    	clingState = 1000;
		//    	ClingTarget target = null;
		//    	for(int i=0;i<targets.size();i++){
		//    		ClingTarget tmp = targets.get(i);
		//    		int priority = tmp.getClingPriority();
		//    		if(priority < clingState && tmp.visible()){
		//    			clingState = priority;
		//    			target = tmp;
		//    		}
		//    	}
		//    	if(clingState != 1000 && !hide && !wait){
		//    		if(clingPoint.getVisibility()==View.GONE){
		//    			clingPoint.setVisibility(View.VISIBLE);
		//    			launcher.mMainHandler.post(rotatePoint);
		//    		}
		//    		if(target instanceof AppList3D){
		//				pointParams.setMargins(15, (int) (50*context.getResources().getDisplayMetrics().density), 0, 0);
		//				clingPoint.setLayoutParams(pointParams);
		//			}
		//			else{
		//				pointParams.setMargins(15, 15, 0, 0);
		//				clingPoint.setLayoutParams(pointParams);
		//			}
		//    	}
		//    	else {
		//    		clingPoint.setVisibility(View.GONE);
		//    		clingPoint.clearAnimation();
		//    		launcher.mMainHandler.removeCallbacks(rotatePoint);
		//    	}
		//Log.d("launcher", "refreshState:"+clingState);
	}
	
	private Runnable rotatePoint = new Runnable() {
		
		@Override
		public void run()
		{
			//Log.d("launcher", "rotatePoint");
			applyRotation( 0 , 360 );
			launcher.mMainHandler.postDelayed( rotatePoint , 12000 );
		}
	};
	
	public void startWait()
	{
		if( !showCling )
			return;
		wait = true;
		refreshClingState();
	}
	
	public void cancelWait()
	{
		if( !showCling )
			return;
		wait = false;
		refreshClingState();
	}
	
	private void applyRotation(
			float start ,
			float end )
	{
		final float centerX = clingPoint.getWidth() / 2.0f;
		final float centerY = clingPoint.getHeight() / 2.0f;
		final Rotate3dAnimation rotation = new Rotate3dAnimation( start , end , centerX , centerY , 0.0f , false );
		rotation.setDuration( 4000 );
		rotation.setFillAfter( false );
		//rotation.setInterpolator(new AccelerateInterpolator());
		clingPoint.startAnimation( rotation );
	}
	
	public void removeTarget(
			int priority )
	{
		if( !showCling )
			return;
		for( int i = 0 ; i < targets.size() ; i++ )
		{
			ClingTarget target = targets.get( i );
			int _priority = target.getClingPriority();
			if( priority == _priority )
			{
				target.dismissCling();
				targets.remove( target );
				SendMsgToAndroid.sendRefreshClingStateMsg();
				return;
			}
		}
	}
	
	public ClingTarget getTarget(
			int priority )
	{
		if( !showCling )
			return null;
		for( int i = 0 ; i < targets.size() ; i++ )
		{
			ClingTarget target = targets.get( i );
			int _priority = target.getClingPriority();
			if( priority == _priority )
			{
				return target;
			}
		}
		return null;
	}
	
	public void hideClingPoint()
	{
		if( !showCling )
			return;
		hide = true;
		refreshClingState();
	}
	
	public void showClingPoint()
	{
		if( !showCling )
			return;
		hide = false;
		refreshClingState();
	}
	
	public void fireAllAppCling(
			ClingTarget target )
	{
		if( !showCling )
			return;
		if( prefs.getInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING ) == ALLAPP_CLING )
		{
			targets.add( target );
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
	public int firePageIndicatorCling(
			ClingTarget target )
	{
		if( !showCling )
			return -1;
		if( pageIndicatorClingFired )
			return CLING_STATE_DISMISSED;
		else if( prefs.getInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING ) == PAGEINDICATOR_CLING )
		{
			pageIndicatorClingFired = true;
			targets.add( target );
			SendMsgToAndroid.sendRefreshClingStateMsg();
			return CLING_STATE_SHOW;
		}
		else if( prefs.getInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING ) < PAGEINDICATOR_CLING )
		{
			return CLING_STATE_WAIT;
		}
		return CLING_STATE_DISMISSED;
	}
	
	public int fireFolderCling(
			ClingTarget target )
	{
		if( !showCling )
			return -1;
		if( folderClingFired )
			return CLING_STATE_DISMISSED;
		else if( prefs.getInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING ) == FOLDER_CLING )
		{
			folderClingFired = true;
			targets.add( target );
			SendMsgToAndroid.sendRefreshClingStateMsg();
			Log.d( "launcher" , "fire folder cling" );
			return CLING_STATE_SHOW;
		}
		else if( prefs.getInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING ) < FOLDER_CLING )
		{
			return CLING_STATE_WAIT;
		}
		return CLING_STATE_DISMISSED;
	}
	
	public void resetFolderCling(
			ClingTarget target )
	{
		if( !showCling )
			return;
		folderClingFired = false;
		targets.remove( target );
		SendMsgToAndroid.sendRefreshClingStateMsg();
	}
	
	//    public int fireCircleCling(ClingTarget target){
	//    	if(prefs.getInt(WORKSPACE_DISMISSED_KEY, ALLAPP_CLING) < CIRCLE_CLING){
	//    		return CLING_STATE_WAIT;
	//    	}
	//    	else if(prefs.getInt(WORKSPACE_DISMISSED_KEY, ALLAPP_CLING) == END_CLING){
	//    		return CLING_STATE_CANCEL;
	//    	}
	//    	else if (prefs.getInt(WORKSPACE_DISMISSED_KEY, ALLAPP_CLING) == CIRCLE_CLING && !circleClingFired) {
	//			circleClingFired = true;
	//			targets.add(target);
	//			SendMsgToAndroid.sendRefreshClingStateMsg();
	//        	return CLING_STATE_SHOW;
	//        }
	//    	return CLING_STATE_DISMISSED;
	//    }
	//    public int firePageSelectCling(ClingTarget target){
	//    	if (prefs.getInt(PAGECONTAINER_DISMISSED_KEY, PAGESELECT_CLING) == PAGESELECT_CLING
	//    			|| prefs.getInt(PAGECONTAINER_DISMISSED_KEY, PAGESELECT_CLING) == PAGEEDIT_CLING) {
	//    		targets.add(target);
	//    		SendMsgToAndroid.sendRefreshClingStateMsg();
	//			return CLING_STATE_SHOW;
	//        }
	//    	return CLING_STATE_DISMISSED;
	//    }
	public int fireSelectCling(
			ClingTarget target )
	{
		if( !showCling )
			return -1;
		if( prefs.getInt( SELECT_DISMISSED_KEY , SELECT_CLING ) == END_CLING )
		{
			return CLING_STATE_CANCEL;
		}
		else if( prefs.getInt( SELECT_DISMISSED_KEY , SELECT_CLING ) == SELECT_CLING && !selectClingFired )
		{
			selectClingFired = true;
			targets.add( target );
			SendMsgToAndroid.sendRefreshClingStateMsg();
			return CLING_STATE_SHOW;
		}
		return CLING_STATE_DISMISSED;
	}
	
	//    public int fireSettingCling(){
	//    	if (prefs.getInt(SETTING_DISMISSED_KEY, SETTING_CLING) == SETTING_CLING) {
	//			return CLING_STATE_SHOW;
	//        }
	//    	return CLING_STATE_DISMISSED;
	//    }
	//    public int fireWidgetCling(ClingTarget target){
	//    	if(prefs.getInt(WIDGET_DISMISSED_KEY, WIDGET_CLING) == END_CLING){
	//    		return CLING_STATE_CANCEL;
	//    	}
	//    	else if (prefs.getInt(WIDGET_DISMISSED_KEY, WIDGET_CLING) == WIDGET_CLING && !widgetClingFired) {
	//    		widgetClingFired = true;
	//    		targets.add(target);
	//    		SendMsgToAndroid.sendRefreshClingStateMsg();
	//			return CLING_STATE_SHOW;
	//        }
	//    	return CLING_STATE_DISMISSED;
	//    }
	public void cancelAllAppCling()
	{
		if( !showCling )
			return;
		if( prefs.getInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING ) == ALLAPP_CLING )
		{
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt( WORKSPACE_DISMISSED_KEY , PAGEINDICATOR_CLING );
			editor.commit();
			removeTarget( ALLAPP_CLING );
			wait = false;
		}
	}
	
	public void cancelPageIndicatorCling()
	{
		if( !showCling )
			return;
		if( prefs.getInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING ) == PAGEINDICATOR_CLING )
		{
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt( WORKSPACE_DISMISSED_KEY , FOLDER_CLING );
			editor.commit();
			removeTarget( PAGEINDICATOR_CLING );
			wait = false;
		}
	}
	
	public void cancelFolderCling()
	{
		if( !showCling )
			return;
		if( prefs.getInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING ) == FOLDER_CLING )
		{
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt( WORKSPACE_DISMISSED_KEY , END_CLING );
			editor.commit();
			removeTarget( FOLDER_CLING );
			folderClingFired = false;
			wait = false;
		}
	}
	
	//    public void cancelCircleCling(){
	//    	if (prefs.getInt(WORKSPACE_DISMISSED_KEY, PAGEINDICATOR_CLING) == CIRCLE_CLING) {
	//			SharedPreferences.Editor editor = prefs.edit();
	//			editor.putInt(WORKSPACE_DISMISSED_KEY, END_CLING);
	//			editor.commit();
	//			removeTarget(CIRCLE_CLING);
	//    	}
	//    }
	//    
	//    public void cancelPageContainerCling(int i){
	//    	if (prefs.getInt(PAGECONTAINER_DISMISSED_KEY, PAGESELECT_CLING) == i) {
	//			SharedPreferences.Editor editor = prefs.edit();
	//			if(i == PAGESELECT_CLING){
	//				editor.putInt(PAGECONTAINER_DISMISSED_KEY, PAGEEDIT_CLING);
	//				ClingTarget target = getTarget(PAGESELECT_CLING);
	//				if(target != null){
	//					target.setPriority(PAGEEDIT_CLING);
	//					SendMsgToAndroid.sendRefreshClingStateMsg();
	//				}
	//			}
	//			else {
	//				editor.putInt(PAGECONTAINER_DISMISSED_KEY, END_CLING);
	//				removeTarget(PAGEEDIT_CLING);
	//			}
	//			editor.commit();
	//    	}
	//    }
	public void cancelSelectCling()
	{
		if( !showCling )
			return;
		if( prefs.getInt( SELECT_DISMISSED_KEY , SELECT_CLING ) == SELECT_CLING )
		{
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt( SELECT_DISMISSED_KEY , END_CLING );
			editor.commit();
			removeTarget( SELECT_CLING );
			wait = false;
		}
	}
	
	//    public void cancelSettingCling(){
	//    	if (prefs.getInt(SETTING_DISMISSED_KEY, SETTING_CLING) == SETTING_CLING) {
	//			SharedPreferences.Editor editor = prefs.edit();
	//			editor.putInt(SETTING_DISMISSED_KEY, END_CLING);
	//			editor.commit();
	//    	}
	//    }
	//    
	//    public void cancelWidgetCling(){
	//    	if (prefs.getInt(WIDGET_DISMISSED_KEY, WIDGET_CLING) == WIDGET_CLING) {
	//			SharedPreferences.Editor editor = prefs.edit();
	//			editor.putInt(WIDGET_DISMISSED_KEY, END_CLING);
	//			editor.commit();
	//			removeTarget(WIDGET_CLING);
	//    	}
	//    }
	public void showAllAppCling(
			int x ,
			int y ,
			int r )
	{
		if( !showCling )
			return;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt( WORKSPACE_DISMISSED_KEY , PAGEINDICATOR_CLING );
		editor.commit();
		if( cling == null )
			cling = new Cling( context );
		cling.init( launcher , ALLAPP );
		int[] position = new int[3];
		position[0] = x;
		position[1] = y;
		position[2] = r;
		cling.setPosition( position );
		launcher.addContentView( cling , layoutParams );
		launcher.addContentView( okButton , okButton.getLayoutParams() );
		wait = true;
	}
	
	public void showPageIndicatorCling(
			int x ,
			int y ,
			int r )
	{
		if( !showCling )
			return;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt( WORKSPACE_DISMISSED_KEY , FOLDER_CLING );
		editor.commit();
		if( cling == null )
			cling = new Cling( context );
		cling.init( launcher , PAGEINDICATOR );
		int[] position = new int[3];
		position[0] = x;
		position[1] = y;
		position[2] = r;
		cling.setPosition( position );
		launcher.addContentView( cling , layoutParams );
		launcher.addContentView( okButton , okButton.getLayoutParams() );
		wait = true;
	}
	
	public void showFolderCling(
			int x ,
			int y ,
			int r )
	{
		if( !showCling )
			return;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt( WORKSPACE_DISMISSED_KEY , END_CLING );
		editor.commit();
		if( cling == null )
			cling = new Cling( context );
		cling.init( launcher , FOLDER );
		int[] position = new int[3];
		position[0] = x;
		position[1] = y;
		position[2] = r;
		cling.setPosition( position );
		launcher.addContentView( cling , layoutParams );
		launcher.addContentView( okButton , okButton.getLayoutParams() );
	}
	
	//    public void showCircleCling(){
	//    	SharedPreferences.Editor editor = prefs.edit();
	//		editor.putInt(WORKSPACE_DISMISSED_KEY, END_CLING);
	//		editor.commit();
	//    	if(cling == null)cling = new Cling(context);
	//        cling.init(launcher,CIRCLE);
	//    	launcher.addContentView(cling, layoutParams);
	//    	launcher.addContentView(okButton,okButton.getLayoutParams());
	//    }
	//    
	//    public void showPageContainerCling(int i){
	//    	SharedPreferences.Editor editor = prefs.edit();
	//    	if(i == PAGESELECT_CLING)editor.putInt(PAGECONTAINER_DISMISSED_KEY, PAGEEDIT_CLING);
	//		else editor.putInt(PAGECONTAINER_DISMISSED_KEY, END_CLING);
	//		editor.commit();
	//    	if(cling == null)cling = new Cling(context);
	//    	if(i == PAGESELECT_CLING)cling.init(launcher,PAGESELECT);
	//    	else cling.init(launcher, PAGEEDIT);
	//    	launcher.addContentView(cling, layoutParams);
	//    	launcher.addContentView(okButton,okButton.getLayoutParams());
	//    }
	public void showSelectCling(
			int x ,
			int y ,
			int r ,
			int gap )
	{
		if( !showCling )
			return;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt( SELECT_DISMISSED_KEY , END_CLING );
		editor.commit();
		if( cling == null )
			cling = new Cling( context );
		cling.init( launcher , SELECT );
		int[] position = new int[4];
		position[0] = x;
		position[1] = y;
		position[2] = r;
		position[3] = gap;
		cling.setPosition( position );
		launcher.addContentView( cling , layoutParams );
		launcher.addContentView( okButton , okButton.getLayoutParams() );
	}
	
	//    public void showWidgetCling(){
	//    	SharedPreferences.Editor editor = prefs.edit();
	//		editor.putInt(WIDGET_DISMISSED_KEY, END_CLING);
	//		editor.commit();
	//    	if(cling == null)cling = new Cling(context);
	//        cling.init(launcher,WIDGET);
	//    	launcher.addContentView(cling, layoutParams);
	//    	launcher.addContentView(okButton,okButton.getLayoutParams());
	//    }
	//    
	//    public View[] showSettingCling(){
	//    	View[] views = new View[2];
	//    	SharedPreferences.Editor editor = prefs.edit();
	//        editor.putInt(SETTING_DISMISSED_KEY, END_CLING);
	//        editor.commit();
	//        if(cling == null)cling = new Cling(context);
	//        cling.init(launcher,SETTING);
	//        cling.setLayoutParams(layoutParams);
	//    	views[0] = cling;
	//    	views[1] = okButton;
	//    	return views;
	//    }
	@Override
	public void onClick(
			View v )
	{
		if( !showCling )
			return;
		Log.d( "launcher" , "clingState=" + clingState );
		if( v == okButton )
		{
			if( cling != null )
			{
				cling.cleanup();
				removeView( cling );
				removeView( okButton );
			}
		}
		else if( clingState == ALLAPP_CLING )
		{
			ClingTarget target = getTarget( ALLAPP_CLING );
			int x = 0 , y = 0 , r = 0;
			if( target != null )
			{
				HotDockGroup hotdock = (HotDockGroup)target;
				x = hotdock.clingX;
				y = Utils3D.getScreenHeight() - hotdock.clingY;
				r = hotdock.clingR;
			}
			showAllAppCling( x , y , r );
			removeTarget( clingState );
		}
		else if( clingState == PAGEINDICATOR_CLING )
		{
			ClingTarget target = getTarget( PAGEINDICATOR_CLING );
			int x = 0 , y = 0 , r = 0;
			if( target != null )
			{
				PageIndicator3D pageIndicator = (PageIndicator3D)target;
				x = pageIndicator.clingX;
				y = Utils3D.getScreenHeight() - pageIndicator.clingY;
				r = pageIndicator.clingR;
			}
			showPageIndicatorCling( x , y , r );
			removeTarget( clingState );
		}
		else if( clingState == FOLDER_CLING )
		{
			ClingTarget target = getTarget( FOLDER_CLING );
			int x = 0 , y = 0 , r = 0;
			if( target != null )
			{
				FolderIcon3D folder = (FolderIcon3D)target;
				x = (int)( folder.x + folder.width / 2 );
				y = (int)( Utils3D.getScreenHeight() - ( folder.y + folder.height / 2 ) );
				r = (int)( folder.height / 2 );
			}
			showFolderCling( x , y , r );
			removeTarget( clingState );
			target.dismissCling();
			folderClingFired = false;
		}
		//		else if(clingState==CIRCLE_CLING){
		//			ClingTarget target = getTarget(CIRCLE_CLING);
		//			showCircleCling();
		//			removeTarget(clingState);
		//			target.dismissCling();
		//		}
		//		else if(clingState==PAGESELECT_CLING){
		//			ClingTarget target = getTarget(PAGESELECT_CLING);
		//			showPageContainerCling(PAGESELECT_CLING);
		//			target.setPriority(PAGEEDIT_CLING);
		//			SendMsgToAndroid.sendRefreshClingStateMsg();
		//		}
		//		else if(clingState==PAGEEDIT_CLING){
		//			ClingTarget target = getTarget(PAGEEDIT_CLING);
		//			showPageContainerCling(PAGEEDIT_CLING);
		//			removeTarget(clingState);
		//			target.dismissCling();
		//		}
		else if( clingState == SELECT_CLING )
		{
			ClingTarget target = getTarget( SELECT_CLING );
			int x = 100 , y = 100 , r = 50 , gap = 30;
			;
			if( target != null )
			{
				AppList3D app = (AppList3D)target;
				View3D view = app.getFirstIcon();
				if( view != null )
				{
					Icon3D icon = (Icon3D)view;
					x = icon.getClingX();
					y = Utils3D.getScreenHeight() - icon.getClingY();
					r = icon.getClingR();
					gap = app.getIconGap();
					if( gap == -1 )
						gap = 30;
				}
			}
			showSelectCling( x , y , r , gap );
			removeTarget( clingState );
			target.dismissCling();
		}
		//		else if(clingState==WIDGET_CLING){
		//			ClingTarget target = getTarget(WIDGET_CLING);
		//			showWidgetCling();
		//			removeTarget(clingState);
		//			target.dismissCling();
		//		}
	}
	
	public void removeView(
			final View v )
	{
		if( !showCling )
			return;
		if( v == null )
			return;
		final ViewGroup parent = (ViewGroup)v.getParent();
		if( parent == null )
			return;
		parent.post( new Runnable() {
			
			@Override
			public void run()
			{
				parent.removeView( v );
			}
		} );
	}
	
	public boolean hide()
	{
		if( !showCling )
			return false;
		if( cling != null && cling.isShown() )
		{
			cling.cleanup();
			removeView( cling );
			removeView( okButton );
			return true;
		}
		return false;
	}
	
	public interface ClingTarget
	{
		
		public boolean visible();
		
		public int getClingPriority();
		
		public void dismissCling();
		
		public void setPriority(
				int priority );
	}
	
	@Override
	public void setActionListener()
	{
		if( !showCling )
			return;
		SetupMenuActions.getInstance().RegisterListener( ActionSetting.ACTION_RESET_WIZARD , this );
	}
	
	@Override
	public void OnAction(
			int actionid ,
			Bundle bundle )
	{
		if( !showCling )
			return;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt( WORKSPACE_DISMISSED_KEY , ALLAPP_CLING );
		//		editor.putInt(PAGECONTAINER_DISMISSED_KEY, PAGESELECT_CLING);
		editor.putInt( SELECT_DISMISSED_KEY , SELECT_CLING );
		//		editor.putInt(SETTING_DISMISSED_KEY, SETTING_CLING);
		//		editor.putInt(WIDGET_DISMISSED_KEY, WIDGET_CLING);
		editor.commit();
	}
}
