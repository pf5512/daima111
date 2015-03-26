package com.iLoong.launcher.HotSeat3D;


import android.content.Context;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class HotObjMenuBack extends ViewGroupOBJ3D
{
	
	Context mContext;
	protected HotObjBackGroundFront3D mBackViewUp = null;
	protected HotObjBackGround3D mBackViewDown = null;
	private ObjButton objButton1;
	private ObjButton objButton2;
	private ObjButton objButton3;
	private ObjButton objButton4;
	private ObjButton objButton5;
	private final int buttonNum = 4;
	private float scaleW = 1;
	private Tween mAutoScrollTween = null;
	private Timeline animation_line = null;
	// private boolean animalStart =false;
	public static float rotationY;
	public static float rotationZ;
	public static double rotationAngle = 0;
	
	public HotObjMenuBack(
			String name ,
			Context context ,
			float width ,
			float height )
	{
		super( name );
		transform = true;
		this.mContext = context;
		this.width = width;
		this.height = height;
		scaleW = Utils3D.getScreenWidth() / 480f;
		this.setOrigin( width / 2 , height / 2 );
		init();
	}
	
	//    public void startRotation(){
	//        mBackViewDown.startRotation();
	////        animalStart =true;
	////        this.setOrigin(width/2 ,0);
	////        mBackViewDown.setOrigin(width/2 ,0);
	////        setUser(15);
	////        mAutoScrollTween = Tween
	////                    .to(this, View3DTweenAccessor.USER,6f)
	////                    .ease(Quint.OUT).target(90).delay(0)
	////                    .start(View3DTweenAccessor.manager).setCallback(this);
	//    }
	public void init()
	{
		mBackViewDown = new HotObjBackGround3D( "mBackViewDown" , mContext , "launcher/dock3dobj/dock_down.obj" , this.width , this.height , 0 );
		addView( mBackViewDown );
		// mBackViewDown.bringToFront();
		//        mBackViewDown.setOriginZ(-this.height/2);
		//        mBackViewDown.setRotationX(15);
		//        mBackViewDown.bringToFront();
		//        objButton1 = new ObjButton("button1", this.mContext, this.width / buttonNum, this.height,
		//                "button_normal.png", "button_press.png");
		//        objButton1.setScale(scaleW, scaleW, scaleW);
		//        objButton1.setMesh("button.obj", 0, 0, 0);
		//        this.addView(objButton1);
		//        objButton1.bringToFront();
	}
	//    @Override
	//    public void onEvent(int type, @SuppressWarnings("rawtypes") BaseTween source) {
	//             if (source.equals(this.mAutoScrollTween)&&type==TweenCallback.COMPLETE ){
	//                animalStart=false;
	//            }
	//            
	//    }
	//    @Override
	//    public void draw(SpriteBatch batch, float parentAlpha) {
	//
	////        if(animalStart)
	////        {
	////            rotationAngle =getUser();
	////      //      float realRotation =(float)(45-rotationAngle);
	////            setRotationAngle((float)(rotationAngle), 0, 0);
	//////            float radian= (float)(Math.PI*realRotation/180);
	//////            rotationZ  =(float)(this.height*SCALE_X*Math.sin(radian));
	//////            rotationY=(float)(this.height*SCALE_X*Math.cos(radian));
	//////            Log.v("Hotseat", "this.height: "+this.height+" SCALE_X: "+SCALE_X+" ,cos"+realRotation+":"+Math.cos(radian)+" ,sin:"+Math.sin(radian));
	//////            Log.v("Hotseat", "rotation y: "+rotationY+" z: "+rotationZ+" ,rotationAngle "+rotationAngle);
	////       }
	//
	//        super.draw(batch, parentAlpha);
	//
	//    }
}
