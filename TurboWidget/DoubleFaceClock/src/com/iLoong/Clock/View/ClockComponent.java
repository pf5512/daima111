package com.iLoong.Clock.View;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.Clock.Common.ClockHelper;
import com.iLoong.Clock.Timer.ClockTimerListener;
import com.iLoong.Widget.theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;
import com.iLoong.launcher.widget.Widget;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ClockComponent extends ViewGroup3D implements ClockTimerListener
{
	
	//save 
	private Map<String , View3D> mViews;
	private Map<Integer , View3D> mColorBlock;
	public static Map<String , Texture> mTextures;
	private Map<String , Mesh> mMeshs;
	private Context mContext;
	public static float mScaleX , mScaleY , mScaleZ;
	public static ClockComponent mInstance;
	private final String MAP_NAME_AM = "am";
	private final String MAP_NAME_PM = "pm";
	private final String MAP_NAME_SEPERATOR = "seperator";
	//private final String MAP_NAME_TIME_HAND="timeHand";
	private static final String MAP_NAME_BACK_DOT = "back_dot";
	private static final String MAP_NAME_BACK_MINUTE_HAND = "back_minute_hand";
	private static final String MAP_NAME_BACK_HOUR_HAND = "back_hour_hand";
	private static final String MAP_NAME_BACK_COLOR = "back_color";
	private static final String MAP_NAME_BACK_SECONDDOT = "back_sencond_dot";
	private final String MAP_NAME_BACK_MINUTE_SHADOW = "back_minute_shadow";
	private final String MAP_NAME_BACK_HOUR_SHADOW = "back_hour_shadow";
	private final String MAP_NAME_DATE = "date";
	// private final String MAP_NAME_DEFAULT="timeHand";
	public static final String imgPath = "theme/widget/clock/comet/image/";
	//  public static final String objPath = "theme/widget/clock/comet/orig_obj/";
	private final CooGdx cooGdx;
	private final float mScale;
	private static MainAppContext mAppContext = null;;
	private static Cache<String , Mesh> mMeshCache = null;
	private final int offsetX = 40;
	private int mCurrentHour;
	private int mCurrentMinute;
	private int mCurrentSecond;
	//    private int mCurrentMeridiem;
	//    private int mPresentMeridiem;
	private int mPresentHour;
	private int mPresentMinute = 0;
	private int mPresentSecond = 0;
	private float mSecondHandRotation = 0;
	private boolean isCoverColor = false;
	private float timeWidth = 0;
	private float timeHeight = 0;
	private float monthHeight = 0;
	private float horizontalGap = -10;
	private float verticalGap = 5;
	private float meridiemX = 0;
	
	@SuppressWarnings( "unchecked" )
	public ClockComponent(
			String name ,
			MainAppContext globalContext ,
			CooGdx cooGdx ,
			Cache<String , Mesh> meshCache )
	{
		super( name );
		mInstance = this;
		mAppContext = globalContext;
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.x = 0;
		this.y = 0;
		this.mScale = this.width / 450;
		this.cooGdx = cooGdx;
		this.transform = true;
		this.mContext = globalContext.mWidgetContext;
		this.setOrigin( this.width / 2 , this.height / 2 );
		mViews = new HashMap<String , View3D>();
		mColorBlock = new HashMap<Integer , View3D>();
		mTextures = new HashMap<String , Texture>();
		mMeshs = new HashMap<String , Mesh>();
		mScaleX = mScaleY = mScaleY = WidgetClock.SCALE_X;
		this.setMeshCache( meshCache );
		//        try{
		//            initResource();
		//        }catch(IOException e){
		//            e.printStackTrace();
		//        }
		//   initFrontViews();
		//   initBackColor();
		//    initBackViews();
	}
	
	public static ClockComponent getInstance()
	{
		return mInstance;
	}
	
	//    private void initResource() throws IOException{
	//        Log.v("DBClock", "start init");
	//        String path =ClockComponent.imgPath;
	//        for(int i=0;i<10;i++){
	//            mTextures.put(MAP_NAME_DATE+"_"+i, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"day_"+i+".png"))));
	//        }
	//      
	//        for(int i=1;i<13;i++){
	//            mTextures.put("month_"+i, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"month_"+i+".png"))));
	//            
	//        }
	//  
	//        for(int i=1;i<=7;i++){
	//            mTextures.put("week_"+i, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"week_"+i+".png"))));
	//            
	//        }
	//        for(int i=0;i<10;i++){
	//            BitmapTexture texture  = new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"time_"+i+".png")));
	//            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	//            mTextures.put("time_"+i,texture );
	//        }
	// mTextures.put(MAP_NAME_AM, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"secondDot.png"))));
	//        mTextures.put(MAP_NAME_AM, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"am.png"))));
	//        mTextures.put(MAP_NAME_PM, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"pm.png"))));
	//       // mTextures.put(MAP_NAME_SEPERATOR, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"seperator.png"))));
	// mTextures.put(MAP_NAME_TIME_HAND, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"time_hand.png"))));
	//mTextures.put(MAP_NAME_BACK_DOT, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"back_dot.png"))));
	//   mTextures.put(MAP_NAME_BACK_MINUTE_HAND, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"back_minute_hand.png"))));
	// mTextures.put(MAP_NAME_BACK_HOUR_HAND, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"back_hour_hand.png"))));
	//mTextures.put(MAP_NAME_DEFAULT, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"am.png"))));
	// mTextures.put(MAP_NAME_BACK_COLOR, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"color.png"))));
	//  mTextures.put(MAP_NAME_BACK_SECONDDOT, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"seconddot.png"))));
	//        mTextures.put(MAP_NAME_BACK_MINUTE_SHADOW, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"minute_shadow.png"))));
	//        mTextures.put(MAP_NAME_BACK_HOUR_SHADOW, new BitmapTexture(BitmapFactory.decodeStream(mContext.getAssets().open(path+"hour_shadow.png"))));
	//        
	//   }
	//    private void initBackColor(){
	//        int angle =0;
	//        for(int i=0;i<60;i++){
	//            ObjBaseView view =new ObjBaseView("color_"+i, mAppContext, mMeshCache,"color.png","color.obj",(float)(i*0.1));
	//            int r =(360- angle+i*6)%360;
	//            view.setRotationAngle(0, 0,r);
	//            view.enableDepthMode(true);
	//            
	//            view.hide();
	//            mColorBlock.put(i, view);
	//            this.addView(view);
	//        }
	//        
	//        
	//    }
	//    
	//    private void initFrontViews(){
	//       addTime();
	//       addClockMediriemView("meridiem",MAP_NAME_AM,MAP_NAME_AM);
	//       addClockMonthView("month","month","month_1");
	//        addClockDateView("date","day","date_1",0);
	//        addClockDateView("date1","day","date_1",1);
	//        addClockWeekView("week","week","week_1");
	//        addClockView("meridiem",MAP_NAME_AM,MAP_NAME_AM,115+2,160+1*mScale);
	//        addClockView("month","month","month_1",162+offsetX*mScale,250+25*mScale);
	//        addClockView("date","day","date_1",227+offsetX*mScale,250+25*mScale);
	//        addClockView("date1","day","date_1",250+offsetX*mScale,250+25*mScale);
	//        addClockView("week","week","week_1",195+offsetX*mScale,291+14*mScale);
	//
	//      
	//    }
	//    private void addClockWeekView(String name ,String objName,String textureName){
	//        ObjBaseView view =new ObjBaseView(name);
	//        Mesh mesh=getMesh(objName+".obj",this.width/2,this.height/2);
	//        float size[]=getMeshSize(mesh);
	//        mesh.dispose();
	//        
	//        Mesh mesh1 = getMesh(objName+".obj",this.width/2,this.height/2-timeHeight/2-monthHeight/2-verticalGap*3);
	//        moveTo(mesh1,0,0,1);
	//        view.setTexture(mTextures.get(textureName));
	//        view.setMesh(mesh1);
	//        
	//        view.enableDepthMode(true);
	//        mViews.put(name, view);
	//        this.addView(view);
	//    }
	//    
	//    private void addClockDateView(String name ,String objName,String textureName,int index){
	//        ObjBaseView view =new ObjBaseView(name);
	//        int gap=0;
	//        Mesh mesh1=null;
	//        Mesh mesh=getMesh(objName+".obj",this.width/2,this.height/2);
	//        float size[]=getMeshSize(mesh);
	//        mesh.dispose();
	//        if(index==0){
	//            mesh1  = getMesh(objName+".obj",this.width/2+size[0]/2+gap,this.height/2-timeHeight/2-verticalGap*2);
	//            
	//        }
	//        else if(index ==1){
	//            mesh1  = getMesh(objName+".obj",this.width/2+size[0]+gap*2,this.height/2-timeHeight/2-verticalGap*2);
	//            moveTo(mesh1,0,0,1);
	//        }
	//       view.setTexture(mTextures.get(textureName));
	//        view.setMesh(mesh1);
	//        
	//        view.enableDepthMode(true);
	//        mViews.put(name, view);
	//        this.addView(view);
	//    }
	//    private void addClockMediriemView(String name ,String objName,String textureName){
	//        ObjBaseView view =new ObjBaseView(name);
	//        Mesh mesh=getMesh(objName+".obj",meridiemX/*this.width/2-(timeWidth-horizontalGap)*2*/,this.height/2+timeHeight/2+verticalGap);
	//        view.setTexture(mTextures.get(textureName));
	//        view.setMesh(mesh);
	//        
	//        view.enableDepthMode(true);
	//        mViews.put(name, view);
	//        this.addView(view);
	//    }
	//    private void addClockMonthView(String name ,String objName,String textureName){
	//        ObjBaseView view =new ObjBaseView(name);
	//        int gap=5;
	//        Mesh mesh=getMesh(objName+".obj",this.width/2,this.height/2);
	//        float size[]=getMeshSize(mesh);
	//        mesh.dispose();
	//        monthHeight =size[1];
	//        //-size[1]+verticalGap
	//        Mesh mesh1 = getMesh(objName+".obj",this.width/2-size[0]/2-gap,this.height/2-timeHeight/2-verticalGap*2);
	//      
	//        view.setTexture(mTextures.get(textureName));
	//        view.setMesh(mesh1);
	//        
	//        view.enableDepthMode(true);
	//        mViews.put(name, view);
	//        this.addView(view);
	//    }
	//    private void initBackViews(){
	//
	//        addClockHandView(MAP_NAME_BACK_HOUR_SHADOW,"hour_shadow",MAP_NAME_BACK_HOUR_SHADOW,this.width/2,this.height/2);
	//        addClockHandView(MAP_NAME_BACK_MINUTE_SHADOW,"minute_shadow",MAP_NAME_BACK_MINUTE_SHADOW,this.width/2,this.height/2);
	//        
	//        addClockHandView(MAP_NAME_BACK_HOUR_HAND,"hourhand",MAP_NAME_BACK_HOUR_HAND,this.width/2,this.height/2);
	//        addClockHandView(MAP_NAME_BACK_MINUTE_HAND,"minutehand",MAP_NAME_BACK_MINUTE_HAND,this.width/2,this.height/2);
	//        addClockViewEx(MAP_NAME_BACK_DOT,"dot",MAP_NAME_BACK_DOT,this.width/2,this.height/2);
	//        addClockViewEx(MAP_NAME_BACK_SECONDDOT,"seconddot",MAP_NAME_BACK_SECONDDOT,this.width/2,this.height/2);
	//   
	//        
	//    }
	public void setMeshCache(
			Cache<String , Mesh> cache )
	{
		this.mMeshCache = cache;
	}
	
	//    public void addTime(){
	//        
	//        addClockTimeView("time1","time1","time_0",0);
	//        addClockTimeView("time2","time2","time_0",1);
	//        addClockTimeView("seperator","seperator","seperator",2);
	//        addClockTimeView("time3","time3","time_0",3);
	//        addClockTimeView("time4","time4","time_0",4);
	//    }
	public static Mesh getMesh(
			String objName ,
			float x ,
			float y )
	{
		Mesh mesh = ClockHelper.getMesh( mMeshCache , mAppContext , objName , x , y , 0 , WidgetClock.SCALE_X , WidgetClock.SCALE_Y , WidgetClock.SCALE_Z );
		return mesh;
	}
	
	public float[] getMeshSize(
			Mesh mesh )
	{
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.Position );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] ret = new float[2];
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		float maxX = vertices[idx];
		float minX = vertices[idx];
		float maxY = vertices[idx + 1];
		float minY = vertices[idx + 1];
		for( int i = 0 ; i < numVertices ; i++ )
		{
			if( vertices[idx] > maxX )
			{
				maxX = vertices[idx];
			}
			if( vertices[idx] < minX )
			{
				minX = vertices[idx];
			}
			if( vertices[idx + 1] > maxY )
			{
				maxY = vertices[idx + 1];
			}
			if( vertices[idx + 1] < minY )
			{
				minY = vertices[idx + 1];
			}
			idx += vertexSize;
		}
		ret[0] = Math.abs( maxX - minX );
		ret[1] = Math.abs( maxY - minY );
		return ret;
	}
	
	//    public void draw(SpriteBatch batch, float parentAlpha) {
	//        // TODO Auto-generated method stub
	//        // batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
	//        cooGdx.gl.glDepthMask(false);
	//        cooGdx.gl.glEnable(GL10.GL_DEPTH_TEST);
	//        cooGdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
	//        cooGdx.gl.glDepthFunc(GL10.GL_LEQUAL);
	//        super.draw(batch, parentAlpha);
	//        cooGdx.gl.glDisable(GL10.GL_DEPTH_TEST);
	//        cooGdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
	//        cooGdx.gl.glDepthMask(true);
	//        
	//    }
	//    private void addClockView(String name ,String objName,String textureName,float x,float y){
	//        ObjBaseView view =new ObjBaseView(name);
	//       // Mesh mesh=getMesh(objName+".obj",x*,(450-y)*mScale);
	//        Mesh mesh=getMesh(objName+".obj",x*mScale,(450-y)*mScale);
	//       // ClockHelper.move(mesh, 0, 0, 15);
	//        view.setTexture(mTextures.get(textureName));
	//        view.setMesh(mesh);
	//        
	//        view.enableDepthMode(true);
	//        mViews.put(name, view);
	//        this.addView(view);
	//    }
	//    private void addClockTimeView(String name ,String objName,String textureName,int index){
	//        ObjBaseView view =new ObjBaseView(name);
	//        Mesh mesh1=null;
	//        Mesh mesh=getMesh(objName+".obj",x,(450-y));
	//        float distance[]=getMeshSize(mesh);
	//        timeWidth=distance[0];
	//        timeHeight=distance[1];
	//        mesh.dispose();
	//        Log.v("meshSize", "distance[0] "+distance[0]+" distance[1]:"+distance[1]);
	//        if(index==0){
	//            mesh1=getMesh(objName+".obj",this.width/2-(distance[0]+horizontalGap)*2,this.height/2);
	//            meridiemX =this.width/2-(distance[0]+horizontalGap)*2;
	//        }
	//        else if(index ==1){
	//            mesh1=getMesh(objName+".obj",this.width/2-(distance[0]+horizontalGap),this.height/2);
	//        }
	//        else if(index ==2){
	//            mesh1=getMesh(objName+".obj",this.width/2,this.height/2);
	//        }
	//        else if(index ==3){
	//            mesh1=getMesh(objName+".obj",this.width/2+(distance[0]+horizontalGap),this.height/2);
	//        }
	//        else if(index ==4){
	//            mesh1=getMesh(objName+".obj",this.width/2+(distance[0]+horizontalGap)*2,this.height/2);
	//            moveTo(mesh1,0,0,1);
	//        }
	//        view.setTexture(mTextures.get(textureName));
	//        view.setMesh(mesh1);
	//        
	//        view.enableDepthMode(true);
	//        mViews.put(name, view);
	//        this.addView(view);
	//    }
	//    private void addClockHandView(String name ,String objName,String textureName,float x,float y){
	//        ObjHandView view =new ObjHandView(name);
	//        view.x=0;
	//        view.y=0;
	//        view.setSize(this.width,this.height);
	//        Mesh mesh=getMesh(objName+".obj",x,y);
	//        view.setTexture(mTextures.get(textureName));
	//      
	//        view.setMesh(mesh);
	//       // view.enableDepthMode(true);
	//        view.setOrigin(this.width/2, this.height/2);
	//        mViews.put(name, view);
	//        this.addView(view);
	//       view.bringToFront();
	//    }
	//    private void addClockViewEx(String name ,String objName,String textureName,float x,float y){
	//        ObjBaseView view =new ObjBaseView(name);
	//        view.x=0;
	//        view.y=0;
	//        view.setSize(this.width,this.height);
	//        Mesh mesh=getMesh(objName+".obj",x,y);
	//        view.setTexture(mTextures.get(textureName));
	//        view.setMesh(mesh);
	//        view.enableDepthMode(true);
	//        view.setOrigin(this.width/2, this.height/2);
	//        mViews.put(name, view);
	//        this.addView(view);
	//    }
	//    private void addClockViewMinute(String name ,String objName,String textureName,float x,float y){
	//        ObjBaseView view =new ObjBaseView(name);
	//        Log.v("clock", "width: "+this.width+" ,height:"+this.height);
	//        this.addView(view);
	//        mViews.put(name, view);
	//        view.x=this.width/2;
	//        view.y=this.height/2;
	//        view.setSize(this.width/2,this.height/2);
	//        Mesh mesh=getMesh(objName+".obj",this.width/4,this.height/4);
	//        view.setTexture(mTextures.get(textureName));
	//        view.setMesh(mesh);
	//        view.enableDepthMode(true);
	//        view.setOrigin(0, 0);
	//    
	//    }
	//    public static  void moveTo(Mesh mesh, float dx, float dy, float dz) {
	//        VertexAttribute posAttr = mesh.getVertexAttribute(Usage.Position);
	//        int offset = posAttr.offset / 4;
	//        int numVertices = mesh.getNumVertices();
	//        int vertexSize = mesh.getVertexSize() / 4;
	//
	//        float[] vertices = new float[numVertices * vertexSize];
	//        mesh.getVertices(vertices);
	//
	//        int idx = offset;
	//
	//        for (int i = 0; i < numVertices; i++) {
	//            vertices[idx] += dx;
	//            vertices[idx + 1] += dy;
	//            vertices[idx + 2] += dz;
	//            idx += vertexSize;
	//        }
	//        mesh.setVertices(vertices);
	//    }
	@Override
	public void clockTimeChanged()
	{
		Calendar mCalendar = Calendar.getInstance();
		long milliseconds = System.currentTimeMillis();
		mCalendar.setTimeInMillis( milliseconds );
		mCurrentHour = mCalendar.get( Calendar.HOUR );
		mCurrentMinute = mCalendar.get( Calendar.MINUTE );
		mCurrentSecond = mCalendar.get( Calendar.SECOND );
		//  mCurrentMonth = mCalendar.get(Calendar.MONTH)+1;
		// Log.v("Clock", "clockTimeChanged mCurrentMonth: "+mCurrentMonth+"mPresentMonth "+mPresentMonth);
		// mCurrentDate =mCalendar.get(Calendar.DATE);
		//  Log.v("Clock", "clockTimeChanged mCurrentDate: "+mCurrentDate+"mPresentDate "+mPresentDate);
		//  mCurrentWeek =mCalendar.get(Calendar.DAY_OF_WEEK);
		// mCurrentMeridiem =mCalendar.get(Calendar.AM_PM);
		// updateConponents();
	}
	
	//    public void updateConponents(){
	//       updateHourView();
	//        updateMinuteView();
	//        updateSecondView();
	//       updateMonth();
	//        updateDate();
	//        updateWeek();
	//        updateMeridiem();
	//    }
	//    public void updateWeek(){
	//        if(mPresentWeek!=mCurrentWeek){
	//            ObjBaseView view3= (ObjBaseView)mViews.get("week");
	//            if(view3!=null){
	//                switch(mCurrentWeek){
	//                    case 1:view3.setTexture(mTextures.get("week_"+7));break;
	//                    case 2:view3.setTexture(mTextures.get("week_"+1));break;
	//                    case 3:view3.setTexture(mTextures.get("week_"+2));break;  
	//                    case 4:view3.setTexture(mTextures.get("week_"+3));break;
	//                    case 5:view3.setTexture(mTextures.get("week_"+4));break;
	//                    case 6:view3.setTexture(mTextures.get("week_"+5));break;
	//                    case 7:view3.setTexture(mTextures.get("week_"+6));break;
	//                    default:view3.setTexture(mTextures.get("week_"+1));break;
	//                  
	//                }
	//                
	//             }
	//            mPresentWeek =mCurrentWeek;
	//        }
	//   }
	//    public void updateMonth(){
	//     
	//        if(mPresentMonth!=mCurrentMonth){
	//         
	//            ObjBaseView view3= (ObjBaseView)mViews.get("month");
	//            if(view3!=null){
	//                
	//                view3.setTexture(mTextures.get("month_"+mCurrentMonth));
	//             }
	//            mPresentMonth=mCurrentMonth ;
	//        }
	//    }
	//    public void updateDate(){
	//       // Log.v("Clock", "mPresentDate "+mPresentDate+" mCurrentDate: "+mCurrentDate);
	//        if(mPresentDate!=mCurrentDate){
	//            String hour=String.valueOf( mCurrentDate);
	//            int cur1=0;
	//            int cur2=0;
	//            
	//            if(hour.length()>1){
	//                char[]str=hour.toCharArray();
	//                cur1 =Integer.parseInt(String.valueOf(str[0]));
	//                cur2 =Integer.parseInt(String.valueOf(str[1]));
	//                
	//            }
	//            else{
	//                cur2 =Integer.parseInt(hour);
	//            }
	//            Log.v("Clock", "cur1 "+cur1+" cur2: "+cur2);
	//          
	//            if(cur1==0){
	//                ObjBaseView view3= (ObjBaseView)mViews.get("date");
	//              
	//                if(view3!=null){
	//                    view3.setTexture(mTextures.get("date_"+cur2));
	//                 }
	//                
	//                ObjBaseView view= (ObjBaseView)mViews.get("date1");
	//                
	//                if(view!=null){
	//          
	//                   view.hide();
	//                
	//                }
	//            }
	//            else{
	//                ObjBaseView view3= (ObjBaseView)mViews.get("date");
	//                if(view3!=null){
	//                    view3.show();
	//                    view3.setTexture(mTextures.get("date_"+cur1));
	//                 }
	//                
	//                ObjBaseView view= (ObjBaseView)mViews.get("date1");
	//                
	//                if(view!=null){
	//                    view.show();
	//                    view.setTexture(mTextures.get("date_"+cur2));
	//                
	//                }
	//            }
	//            
	//            
	//          
	//           
	//            mPresentDate =mCurrentDate;
	//        }
	//    }
	//    public void updateMeridiem(){
	//        
	//        if(mPresentMeridiem!= mCurrentMeridiem) {
	//            ObjBaseView view3= (ObjBaseView)mViews.get("meridiem");
	//            if(view3!=null){
	//                if(mCurrentMeridiem==0)
	//                    view3.setTexture(mTextures.get("am"));
	//                else if(mCurrentMeridiem==1){
	//                    view3.setTexture(mTextures.get("pm"));
	//                }
	//             }
	//            mPresentMeridiem= mCurrentMeridiem;
	//        }
	//    }
	public static float getHourRotation()
	{
		Calendar calendar = Calendar.getInstance();
		int currentMinute = calendar.get( Calendar.MINUTE );
		int currentHour = calendar.get( Calendar.HOUR );
		float hourRadian = ( 360 - ( ( currentHour * 30 ) ) ) % 360 - ( 30 * currentMinute / 60 );
		return hourRadian;
	}
	
	//    public void updateHourView(){
	//        String hour=String.valueOf( mCurrentHour);
	//        int cur1=0;
	//        int cur2=0;
	//        if(hour.length()>1){
	//            char[]str=hour.toCharArray();
	//            cur1 =Integer.parseInt(String.valueOf(str[0]));
	//            cur2 =Integer.parseInt(String.valueOf(str[1]));
	//            
	//        }
	//        else{
	//            cur2 =Integer.parseInt(hour);
	//        }
	//        
	//        ObjBaseView view= (ObjBaseView)mViews.get("time2");
	//       
	//        if(view!=null){
	//           this.removeView(view);
	//           view.setTexture(mTextures.get("time_"+cur2));
	//           this.addView(view);
	//        }
	//      
	//        ObjBaseView view2 = (ObjBaseView) mViews.get("time1");
	//        if (view2 != null) {
	//            this.removeView(view2);
	//            view2.setTexture(mTextures.get("time_" + cur1));
	//            this.addView(view2);
	//        }
	//        
	//        // update back hour view
	//        if(mPresentHour!=mCurrentHour){
	//           
	//            Calendar calendar = Calendar.getInstance();
	//            int currentMinute = calendar.get(Calendar.MINUTE);
	//           
	//            ObjBaseView view3 = (ObjBaseView) mViews.get(MAP_NAME_BACK_HOUR_HAND);
	//           float rotation = (((mCurrentHour )* 30)) % 360+30*currentMinute/60;
	//           Log.v("Clock", "currentMinute: "+currentMinute+" hour "+mCurrentHour+" rotation: "+rotation);
	//            if (view3 != null) {
	//                
	//                view3.setRotationAngle(0, 0,rotation);
	//               
	//                mPresentHour=mCurrentHour;
	//            }
	//            ObjHandView view4= (ObjHandView)mViews.get(MAP_NAME_BACK_HOUR_SHADOW);
	//            if(mCurrentHour<30){
	//
	//                view4.setRotationAngle(0, 0, rotation+2); 
	//            }
	//            else{
	//
	//                view4.setRotationAngle(0, 0, rotation-2); 
	//            }
	//        }
	//        
	//    }
	public static float getHourHandRotation()
	{
		Calendar calendar = Calendar.getInstance();
		int currentMinute = calendar.get( Calendar.MINUTE );
		int currentHour = calendar.get( Calendar.HOUR );
		float hourRadian = ( 360 - ( ( currentHour * 30 ) - 90 ) ) % 360 - ( 30 * currentMinute / 60 );
		return hourRadian;
	}
	
	//    public void updateMinuteView(){
	//        String minute=String.valueOf( mCurrentMinute);
	//        int cur1=0;
	//        int cur2=0;
	//        if(minute.length()>1){
	//            char[]str=minute.toCharArray();
	//            cur1 =Integer.parseInt(String.valueOf(str[0]));
	//            cur2 =Integer.parseInt(String.valueOf(str[1]));
	//            
	//        }
	//        else{
	//            cur2 =Integer.parseInt(minute);
	//        }
	//        
	//        ObjBaseView view= (ObjBaseView)mViews.get("time4");
	//       
	//        if(view!=null){
	//           
	//           view.setTexture(mTextures.get("time_"+cur2));
	//           
	//        }
	//      
	//        ObjBaseView view2 = (ObjBaseView) mViews.get("time3");
	//        if (view2 != null) {
	//            
	//            view2.setTexture(mTextures.get("time_" + cur1));
	//            
	//        }
	//        
	////        Calendar calendar = Calendar.getInstance();
	////        int currentMinute = calendar.get(Calendar.MINUTE);
	//        
	////        if(currentMinute!=mPresentMinute){
	////            ObjHandView view3= (ObjHandView)mViews.get(MAP_NAME_BACK_MINUTE_HAND);
	////            
	////            int rotation = (((currentMinute )* 6)) % 360;
	////            mPresentMinute=currentMinute;
	////            if(view3!=null){
	////              
	////               
	////                view3.setRotationAngle(0, 0, rotation);
	////                
	////        
	////            }
	////            ObjHandView view4= (ObjHandView)mViews.get(MAP_NAME_BACK_MINUTE_SHADOW);
	////            if(currentMinute<30){
	////
	////                view4.setRotationAngle(0, 0, rotation+2); 
	////            }
	////            else{
	////
	////                view4.setRotationAngle(0, 0, rotation-2); 
	////            }
	////            
	////        }
	//    }
	//    public void updateSecondView(){
	//       
	//        Calendar calendar = Calendar.getInstance();
	//        mCurrentHour = calendar.get(Calendar.HOUR);
	//        int currentSecond = calendar.get(Calendar.SECOND);
	//        
	//        if(currentSecond!=mPresentSecond){
	//            ObjBaseView view= (ObjBaseView)mViews.get(MAP_NAME_BACK_SECONDDOT);//MAP_NAME_BACK_MINUTE_HAND);
	//            mSecondHandRotation = (((currentSecond +1)* 6)+180) % 360;
	//            mPresentSecond=currentSecond;
	//            if(view!=null){
	//              
	//               
	//                view.setRotationAngle(0, 0, mSecondHandRotation);
	//                
	//                
	//        
	//            }
	//        }
	//
	//     
	//    }
	@Override
	public void dispose()
	{
		Iterator it = mTextures.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry entry = (Map.Entry)it.next();
			( (Texture)entry.getValue() ).dispose();
		}
	}
	//    private class ClockBackComponent extends ViewGroup3D implements
	//    ClockTimerListener{
	//     
	//        public ClockBackComponent(){
	//           
	//            this.width=WidgetClock.MODEL_WIDTH;
	//            this.height=WidgetClock.MODEL_HEIGHT;
	//            this.x=0;
	//            this.y=0;
	//            initBackViews();
	//        }
	//        private void initBackViews(){
	//            addClockView(MAP_NAME_BACK_HOUR_HAND,"hourhand",MAP_NAME_BACK_HOUR_HAND,this.width/2,this.height/2);
	//            addClockView(MAP_NAME_BACK_MINUTE_HAND,"minutehand",MAP_NAME_BACK_MINUTE_HAND,this.width/2,this.height/2);
	//            addClockView(MAP_NAME_BACK_DOT,"dot",MAP_NAME_BACK_DOT,this.width/2,this.height/2);
	//        }
	//        private void addClockView(String name ,String objName,String textureName,float x,float y){
	//            Object3DBase view =new Object3DBase(name);
	//            Mesh mesh=getMesh(objName+".obj",x,y);
	//            view.setTexture(mTextures.get(textureName));
	//            view.setMesh(mesh);
	//           // view.setRotationAngle(0, 0, 100);
	//            view.enableDepthMode(true);
	//            mViews.put(name, view);
	//            this.addView(view);
	//        }
	//        @Override
	//        public void clockTimeChanged() {
	//            mSecondHandRotation=ClockHelper.getSecondHandRotation();
	//          
	//            Object3DBase view3= (Object3DBase)this.getChildAt(1);//mViews.get(MAP_NAME_BACK_MINUTE_HAND);
	//            Log.v("update", "mSecondHandRotation "+mSecondHandRotation);
	//            if(view3!=null){
	//                Log.v("update", "mSecondHandRotation 2222"+mSecondHandRotation);
	//              // this.removeView(view3);
	//               //view3.setRotationAngle(0, 0, mSecondHandRotation - 90);
	//              // this.addView(view3);
	//            }
	//        }
	//    }
}
