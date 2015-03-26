package com.iLoong.launcher.HotSeat3D;


import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.menu3D.Menu3DActionSet;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class HotObjMenuFront extends ViewGroupOBJ3D
{
	
	Context mContext;
	private float depth;
	public HotObjBackGroundFront3D mBackViewUp = null;
	// protected HotObjBackGround3D mBackViewDown = null;
	ViewGroupOBJ3D ObjButtonGrp1;
	ViewGroupOBJ3D ObjButtonGrp2;
	private ObjButton objButton1;
	private ObjButton objButton2;
	private ObjButton objButton3;
	private ObjButton objButton4;
	private ObjButton objButton5;
	private ObjButton objButton6;
	private ObjButton objButton7;
	private ObjButton objButton8;
	private Menu3DAction btnAction;
	private final int buttonNum = 4;
	private Timeline animation_line = null;
	//  public boolean startAnimal=false;
	private Tween mUser2Tween = null;
	private float scaleW = 1;
	public HotObjMenuBack objMenuDown;
	public static boolean animalStart = false;
	public static HotObjMenuFront mInstance;
	
	public HotObjMenuFront(
			String name ,
			Context context ,
			float width ,
			float height )
	{
		super( name );
		mInstance = this;
		transform = true;
		this.mContext = context;
		this.width = width;
		this.height = height;
		scaleW = Utils3D.getScreenWidth() / 480f;
		this.setOrigin( width / 2 , height / 2 );
		ObjButtonGrp1 = new ViewGroupOBJ3D();
		ObjButtonGrp1.width = Utils3D.getScreenWidth();
		ObjButtonGrp1.height = this.height;
		ObjButtonGrp2 = new ViewGroupOBJ3D();
		ObjButtonGrp2.width = Utils3D.getScreenWidth();
		ObjButtonGrp2.height = this.height;
		init();
	}
	
	public static HotObjMenuFront getInstance()
	{
		return mInstance;
	}
	
	private ViewGroupOBJ3D parentGroup1;
	private ViewGroupOBJ3D parentGroup2;
	
	public ViewGroup3D getRootView()
	{
		return parentGroup1;
	}
	
	Tween myTween = null;
	
	public void closeMenu()
	{
		HotSeat3D.dockGroup.setOrigin( 0 , R3D.hot_grid_bottom_margin );
		HotSeat3D.dockGroup.setRotationVector( 1 , 0 , 0 );
		HotSeat3D.dockGroup.setOriginZ( -this.height );
		HotSeat3D.menuOpened = !HotSeat3D.menuOpened;
		HotSeat3D.dockGroup.setRotation( R3D.hot_obj_rot_deg - 90 );
		HotSeat3D.menuAnimalComplete = true;
		parentGroup2.setRotationX( 180 );
		parentGroup1.setRotationX( -75 );
		animalStart = false;
		HotSeat3D.menuOpened = !HotSeat3D.menuOpened;
		// HotSeat3D.showViews();
		Root3D.getInstance().releaseDockBarFocus();
		ObjButtonGrp2.hide();
		ObjButtonGrp1.hide();
	}
	
	public void startRotation(
			float duration )
	{
		Log.v( "Hotseat" , "startRotation .." );
		if( !HotSeat3D.menuAnimalComplete )
		{
			return;
		}
		animalStart = true;
		if( !HotSeat3D.menuOpened )
		{
			if( myTween != null )
			{
				myTween.free();
				myTween = null;
			}
			Log.v( "Hotseat " , " start anmial xxxxx" );
			addBtnGrp();
			this.bringToFront();
			Root3D.getInstance().requestDockbarFocus();
			HotSeat3D.menuAnimalComplete = false;
			HotSeat3D.menuOpened = !HotSeat3D.menuOpened;
			//   parentGroup2.setOriginZ(5);
			//HotSeat3D.dockGroup.setPosition(0, R3D.hot_grid_bottom_margin);
			//this.addView(HotSeat3D.dockGroup);
			HotSeat3D.dockGroup.setOrigin( 0 , R3D.hot_grid_bottom_margin );
			HotSeat3D.dockGroup.setRotationVector( 1 , 0 , 0 );
			HotSeat3D.dockGroup.setOriginZ( -this.height );
			animation_line = Timeline.createParallel();
			HotSeat3D.menuBg.show();
			HotSeat3D.menuBg.color.a = 0;
			animation_line.push( Tween.to( HotSeat3D.menuBg , View3DTweenAccessor.OPACITY , duration ).target( 0.5f , 0 , 0 ).ease( Cubic.OUT ) );
			parentGroup2.setUser( 180 );
			animation_line.push( Tween.to( parentGroup2 , View3DTweenAccessor.ROTATION , duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			//            animation_line.push(Tween
			//                    .to(parentGroup2, View3DTweenAccessor.ROTATION,
			//                            duration).target(0, 0, 0).ease(Cubic.OUT));
			animation_line.push( Tween.to( parentGroup1 , View3DTweenAccessor.ROTATION , duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( HotSeat3D.dockGroup , View3DTweenAccessor.ROTATION , duration ).target( R3D.hot_obj_rot_deg - 90 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( HotSeat3D.dockGroup , View3DTweenAccessor.OPACITY , duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		}
		else
		{
			Log.v( "Hotseat " , " start anmial xxxxx" );
			//            HotSeat3D.menuOpened =!HotSeat3D.menuOpened;
			//            addBtnGrp();
			//            animation_line = Timeline.createParallel();
			//            animation_line.push(Tween
			//                    .to(parentGroup2, View3DTweenAccessor.ROTATION,
			//                            duration).target(180, 0, 0).ease(Cubic.OUT));
			//            animation_line.push(Tween
			//                    .to(parentGroup1, View3DTweenAccessor.ROTATION, duration)
			//                    .target(-75, 0, 0).ease(Cubic.OUT));
			//            animation_line.push(Tween
			//                    .to(HotSeat3D.dockGroup, View3DTweenAccessor.OPACITY,duration )
			//                    .target(1, 0, 0).ease(Cubic.OUT));
			//            animation_line.push(Tween
			//                    .to(HotSeat3D.dockGroup, View3DTweenAccessor.ROTATION, duration)
			//                    .target(R3D.hot_obj_rot_deg, 0, 0).ease(Cubic.OUT));
			//            animation_line.start(View3DTweenAccessor.manager).setCallback(this);
			// this.bringToFront();
			Log.v( "Hotseat" , "animal start" );
			this.mBackViewUp.resetDepth( 0 );
			HotSeat3D.menuAnimalComplete = false;
			HotSeat3D.menuOpened = !HotSeat3D.menuOpened;
			//  this.mBackViewUp.resetDepth(0f);
			parentGroup2.setUser( 0f );
			Log.v( "Hotseat" , "start close" );
			myTween = Tween.to( parentGroup2 , View3DTweenAccessor.ROTATION , duration ).target( 180 , 0 , 0 ).ease( Cubic.OUT );
			addBtnGrp();
			animation_line = Timeline.createParallel();
			animation_line.push( Tween.to( HotSeat3D.menuBg , View3DTweenAccessor.OPACITY , duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( myTween );
			animation_line.push( Tween.to( parentGroup1 , View3DTweenAccessor.ROTATION , duration ).target( -75 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( HotSeat3D.dockGroup , View3DTweenAccessor.OPACITY , duration ).target( 1 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( HotSeat3D.dockGroup , View3DTweenAccessor.ROTATION , duration ).target( R3D.hot_obj_rot_deg , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		}
	}
	
	@Override
	public void onEvent(
			int type ,
			@SuppressWarnings( "rawtypes" ) BaseTween source )
	{
		if( source.equals( this.animation_line ) && type == TweenCallback.COMPLETE )
		{
			DockbarObjcetGroup.isStart = false;
			HotSeat3D.menuAnimalComplete = true;
			objMenuDown.show();
			if( HotSeat3D.menuOpened )
			{
				Log.v( "Hotseat" , "open complete" );
				this.mBackViewUp.resetDepth( 4 );
				Root3D.getInstance().addDockbarToRoot( ObjButtonGrp1 , ObjButtonGrp2 );
				HotSeat3D.dockGroup.childReleaseDark();
			}
			else
			{
				HotSeat3D.menuBg.hide();
				HotSeat3D.menuBg.color.a = 1;
				animalStart = false;
				ObjButtonGrp2.hide();
				ObjButtonGrp1.hide();
				objMenuDown.show();
				// HotSeat3D.showViews();
				Root3D.getInstance().releaseDockBarFocus();
				addBtnGrp();
				Log.v( "Hotseat" , "close complete" );
				if( btnAction != null )
				{
					Timer timer = new Timer();
					timer.schedule( new TimerTask() {
						
						@Override
						public void run()
						{
							Log.v( "Hotseat" , "excute action" );
							btnAction.onAction();
							btnAction = null;
						}
					} , 100 );
				}
			}
		}
	}
	
	public void init()
	{
		float depth;
		mBackViewUp = new HotObjBackGroundFront3D( "mBackViewUp" , mContext , "launcher/dock3dobj/dock_up.obj" , this.width , this.height , 0 );
		addView( mBackViewUp );
		// mBackViewUp.setOriginZ(-this.height/2);
		// mBackViewUp.setRotationX(15);
		// mBackViewUp.bringToFront();
		String menuName = iLoongLauncher.getInstance().getString( RR.string.menu_add );
		objButton1 = new ObjButton( menuName , this.mContext , this.width / buttonNum , this.height , "add_normal.png" , "add_press.png" , true );
		objButton1.setScale( scaleW , scaleW , scaleW );
		objButton1.setAction( new Menu3DActionSet.WidgetAdd() );
		objButton1.x = 0;
		objButton1.setMesh( "add.obj" , 0 , 0 , 0 , 4 );
		ObjButtonGrp1.addView( objButton1 );
		objButton1.bringToFront();
		menuName = iLoongLauncher.getInstance().getString( RR.string.menu_edit );
		objButton2 = new ObjButton( menuName , this.mContext , this.width / buttonNum , this.height , "screen_edit_normal.png" , "screen_edit_press.png" , true );
		objButton2.setScale( scaleW , scaleW , scaleW );
		objButton2.setAction( new Menu3DActionSet.ScreenCompiler() );
		objButton2.x = this.width / buttonNum;
		objButton2.setMesh( "edit.obj" , 0 , 0 , 0 , 4 );
		ObjButtonGrp1.addView( objButton2 );
		objButton2.bringToFront();
		menuName = iLoongLauncher.getInstance().getString( RR.string.menu_wallpaper );
		objButton3 = new ObjButton( menuName , this.mContext , this.width / buttonNum , this.height , "wallpaper_normal.png" , "wallpaper_press.png" , true );
		objButton3.setScale( scaleW , scaleW , scaleW );
		objButton3.setAction( new Menu3DActionSet.WallpagerSetting() );
		objButton3.x = this.width / buttonNum * 2;
		objButton3.setMesh( "wallpaper.obj" , 0 , 0 , 0 , 4 );
		ObjButtonGrp1.addView( objButton3 );
		objButton3.bringToFront();
		menuName = iLoongLauncher.getInstance().getString( RR.string.menu_share );
		objButton4 = new ObjButton( menuName , this.mContext , this.width / buttonNum , this.height , "share_normal.png" , "share_press.png" , true );
		objButton4.setScale( scaleW , scaleW , scaleW );
		objButton4.setAction( new Menu3DActionSet.DesktopShare() );
		objButton4.x = this.width / buttonNum * 3;
		objButton4.setMesh( "share.obj" , 0 , 0 , 0 , 4 );
		ObjButtonGrp1.addView( objButton4 );
		objButton4.bringToFront();
		// this.addView(ObjButtonGrp1);
		// ==================================
		parentGroup1 = new ViewGroupOBJ3D();
		parentGroup1.height = this.height;
		parentGroup1.width = this.width;
		parentGroup1.transform = true;
		parentGroup1.y = 0;
		parentGroup1.x = 0;
		// TextureRegion region = R3D.getTextureRegion("menu-tool-button1");
		// parentGroup1.setBackgroud(new NinePatch(region));
		parentGroup1.setOrigin( this.width / 2 , 0 );
		// parentGroup1.setOriginZ(-5);
		parentGroup1.setRotationVector( 1 , 0 , 0 );
		parentGroup1.setRotationX( -75 );
		parentGroup2 = new ViewGroupOBJ3D();
		parentGroup2.transform = true;
		parentGroup2.height = this.height;
		parentGroup2.width = this.width;
		parentGroup2.x = 0;
		parentGroup2.y = this.height;
		parentGroup2.setOrigin( this.width / 2 , 0 );
		// parentGroup2.setOriginZ(5);
		parentGroup2.setRotationVector( 1 , 0 , 0 );
		parentGroup2.setRotationX( 180 );
		// parentGroup2.setBackgroud(new NinePatch(R3D.screenBackRegion));
		parentGroup2.transform = true;
		parentGroup1.addView( parentGroup2 );
		parentGroup2.addView( this );
		objMenuDown = new HotObjMenuBack( "objMenuDown" , mContext , this.width , this.height );
		parentGroup1.addView( objMenuDown );
		// parentGroup2.bringToFront();
		// ================================================
		//
		menuName = iLoongLauncher.getInstance().getString( RR.string.menu_preferences );
		objButton5 = new ObjButton( menuName , this.mContext , this.width / buttonNum , this.height , "desktop_settings_normal.png" , "desktop_settings_press.png" , false );
		objButton5.setScale( scaleW , scaleW , scaleW );
		objButton5.setAction( new Menu3DActionSet.DesktopSetting() );
		objButton5.x = 0;
		objButton5.setMesh( "preferences.obj" , 0 , 0 , 0 , 4 );
		ObjButtonGrp2.addView( objButton5 );
		objButton5.bringToFront();
		menuName = iLoongLauncher.getInstance().getString( RR.string.menu_settings );
		objButton6 = new ObjButton( menuName , this.mContext , this.width / buttonNum , this.height , "system_settings_normal.png" , "system_settings_press.png" , false );
		objButton6.setScale( scaleW , scaleW , scaleW );
		objButton6.setAction( new Menu3DActionSet.SystemSetting() );
		objButton6.x = this.width / buttonNum;
		objButton6.setMesh( "settings.obj" , 0 , 0 , 0 , 4 );
		ObjButtonGrp2.addView( objButton6 );
		objButton6.bringToFront();
		menuName = iLoongLauncher.getInstance().getString( RR.string.menu_updtae );
		objButton7 = new ObjButton( menuName , this.mContext , this.width / buttonNum , this.height , "update_normal.png" , "update_press.png" , false );
		objButton7.setScale( scaleW , scaleW , scaleW );
		objButton7.setAction( new Menu3DActionSet.DesktopUpdate() );
		objButton7.x = this.width / buttonNum * 2;
		objButton7.setMesh( "update.obj" , 0 , 0 , 0 , 4 );
		ObjButtonGrp2.addView( objButton7 );
		objButton7.bringToFront();
		menuName = iLoongLauncher.getInstance().getString( RR.string.menu_feedback );
		objButton8 = new ObjButton( menuName , this.mContext , this.width / buttonNum , this.height , "suggest_normal.png" , "suggest_press.png" , false );
		objButton8.setScale( scaleW , scaleW , scaleW );
		objButton8.setAction( new Menu3DActionSet.Suggest() );
		objButton8.x = this.width / buttonNum * 3;
		objButton8.setMesh( "feedback.obj" , 0 , 0 , 0 , 4 );
		ObjButtonGrp2.addView( objButton8 );
		objButton8.bringToFront();
		objMenuDown.addView( ObjButtonGrp2 );
		// ==================================================
		ObjButtonGrp2.hide();
		ObjButtonGrp2.hide();
		this.bringToFront();
	}
	
	public void addBtnGrp()
	{
		parentGroup1.addView( parentGroup2 );
		ObjButtonGrp1.y = 0;
		this.addView( ObjButtonGrp1 );
		parentGroup2.addView( this );
		objMenuDown.addView( ObjButtonGrp2 );
		parentGroup1.addView( objMenuDown );
	}
	
	public void hideBtnGrp()
	{
		ObjButtonGrp1.hide();
		ObjButtonGrp2.hide();
	}
	
	public ViewGroupOBJ3D getButtonGrp()
	{
		return ObjButtonGrp1;
	}
	
	public void addBackGroundGroup(
			Menu3DAction action )
	{
		//        addBtnGrp();
		//        startRotation(0);
		btnAction = action;
		startRotation( 0.5f );
	}
	
	public float getDepth(
			Mesh mesh )
	{
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.Position );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		float max = vertices[0];
		float min = vertices[0];
		for( int i = 0 ; i < numVertices ; i++ )
		{
			if( vertices[idx + 2] > max )
				max = vertices[idx + 2];
			if( vertices[idx + 2] < min )
				min = vertices[idx + 2];
			idx += vertexSize;
		}
		return max - min;
	}
	
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( !HotSeat3D.menuAnimalComplete )
		{
			if( !HotSeat3D.menuOpened )
			{
				float rotation = parentGroup2.getRotation();
				if( rotation > 170 )
				{
					ObjButtonGrp2.hide();
					ObjButtonGrp1.hide();
				}
				if( rotation > 175 )
				{
					objMenuDown.hide();
				}
				//               // float userData[]= parentGroup2.getTween().getTargetValues();
				//               float userData =parentGroup2.getUser();
				//               // float value =myTween.getTargetValues()[0];
				//          
				//               
				//               if(userData>170){
				//                   ObjButtonGrp2.hide();
				//                   ObjButtonGrp1.hide();
				//               }
				//                 if(userData>179){
				//                   objMenuDown.hide();
				//                  // this.bringToFront();
				//                 
				//                  
				//               }
				//              //   Log.v("Hotseat", "close complete11111"); 
				//              parentGroup2.setRotation(userData);
			}
			else
			{
				if( parentGroup2.getRotation() < 175 )
				{
					objMenuDown.show();
					ObjButtonGrp1.show();
					ObjButtonGrp2.show();
				}
				//                Log.v("Hotseat", " rotation "+rotation); 
				//                
				//                float userData =parentGroup2.getUser();
				//                if(userData<175){
				//                  //  Log.v("Hotseat", "open complete");
				//                    objMenuDown.show();
				//                    ObjButtonGrp1.show();
				//                    ObjButtonGrp2.show();
				//                }
				//               parentGroup2.setRotation(userData);
			}
		}
		super.draw( batch , parentAlpha );
	}
	
	public ViewGroupOBJ3D getSwapedDock()
	{
		parentGroup1.removeView( parentGroup2 );
		parentGroup2.addView( parentGroup1 );
		return parentGroup2;
	}
	
	public static class Menu3DAction
	{
		
		public void onAction()
		{
			if( HotSeat3D.menuOpened )
			{
				Root3D.getInstance().releaseDockBarFocus();
				Root3D.getInstance().workspaceAnimWhenHotseatRotation();
				HotSeat3D.startModelAnimal( 0.0f );
			}
		}
		
		public void setParams(
				Object obj )
		{
			;
		}
	}
}
