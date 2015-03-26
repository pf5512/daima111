package com.iLoong.launcher.widget;


import java.util.ArrayList;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Workspace.Workspace;
import com.iLoong.launcher.app.LauncherBase;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.Widget2DInfo;


public class Widget extends View3D implements IconBase3D
{
	
	public static final int MSG_WIDGET_LONGCLICK = 0;
	public static final int MSG_UPDATE_VIEW = 1;
	private TextureRegion region = new TextureRegion();
	public BitmapTexture texture = null;
	public WidgetHostView sys_view = null;
	private long downTime = 0;
	public Widget2DInfo itemInfo;
	private int newHeight , newWidth;
	public static long time = 0;
	public static long time2 = 0;
	public int state = WidgetHostView.DRAW_RUN;
	public boolean hasRun = false;
	public boolean scroll;
	
	public Widget(
			String name ,
			Widget2DInfo itemInfo )
	{
		super( name );
		this.itemInfo = itemInfo;
		//		int cell_w = iLoongLauncher.getInstance().getResources().getDimensionPixelSize(RR.dimen.workspace_cell_width);
		//		int cell_h = iLoongLauncher.getInstance().getResources().getDimensionPixelSize(RR.dimen.workspace_cell_height);
		//		setSize(itemInfo.spanX*cell_w, itemInfo.spanY*cell_h);
		if( ConfigBase.widget_revise_complete )
		{
			setSize( itemInfo.spanX * ConfigBase.Workspace_cell_each_width_ori , itemInfo.spanY * ConfigBase.Workspace_cell_each_height_ori );
		}
		else
		{
			setSize( itemInfo.spanX * ConfigBase.Workspace_cell_each_width , itemInfo.spanY * ConfigBase.Workspace_cell_each_height );
		}
	}
	
	public boolean scrollVCapture()
	{
		if( ConfigBase.clock_widget_scrollV )
		{
			if( itemInfo.getPackageName().equals( "com.android.deskclock" ) || itemInfo.getPackageName().equals( "com.android.superdeskclock" ) )
			{
				return true;
			}
		}
		//		if(ConfigBase.widget_scrollV_pkgs.contains(itemInfo.getPackageName())){
		//			return true;
		//		}
		return false;
	}
	
	//支持垂直方向滑动
	public boolean canScrollV()
	{
		if( ConfigBase.clock_widget_scrollV )
		{
			if( itemInfo.getPackageName().equals( "com.android.deskclock" ) || itemInfo.getPackageName().equals( "com.android.superdeskclock" ) )
			{
				return true;
			}
		}
		if( ConfigBase.widget_scrollV_pkgs.contains( itemInfo.getPackageName() ) )
		{
			return true;
		}
		int sysVersion = Integer.parseInt( VERSION.SDK );
		if( sysVersion < 11 )
		{
			return false;
		}
		if( itemInfo.getPackageName().equals( "com.android.browser" ) || itemInfo.getPackageName().equals( "com.android.email" ) || itemInfo.getPackageName().equals( "com.android.contacts" ) || itemInfo
				.getPackageName().equals( "com.miui.gallery" ) || itemInfo.getPackageName().equals( "com.mediatek.weather3dwidget" ) || itemInfo.getPackageName().equals(
				"com.mediatek.appwidget.weather" ) )
		{
			return true;
		}
		if( ConfigBase.gallery3d_support_scrollV == true )
		{
			if( itemInfo.getPackageName().equals( "com.android.gallery3d" ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public String getState()
	{
		switch( state )
		{
			case WidgetHostView.DRAW_NONE:
				return "none";
			case WidgetHostView.DRAW_START:
				return "start";
			case WidgetHostView.DRAW_RUN:
				return "run";
			case WidgetHostView.DRAW_STOP:
				return "stop";
		}
		return "";
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		//if(sys_view.checkScrollV)Log.d("widget", "gl draw start");
		//int x = Math.round(this.x);
		//int y = Math.round(this.y);
		//if(mCustomCache!=null&&!mCustomCache.isRecycled())
		//Log.d("launcher", "draw:"+itemInfo.appWidgetId);
		/************************ added by zhenNan.ye begin *************************/
		if( ParticleManager.particleManagerEnable )
		{
			//drawParticle(batch);
			drawParticleEffect( batch );//zhujieping
		}
		/************************ added by zhenNan.ye end ***************************/
		boolean cache = true;
//		if( canScrollV() && scrollVCapture() )
//		{
//			if( sys_view == null )
//				return;
//			cache = false;
//			synchronized( sys_view.bmpLock )
//			{
//				if( sys_view.update_suc == 2 && sys_view.mCustomCache != null )
//				{
//					time2 = System.currentTimeMillis();
//					sys_view.update_suc = 0;
//					if( texture == null )
//						texture = new BitmapTexture( sys_view.mCustomCache );
//					else
//						texture.changeBitmap( sys_view.mCustomCache );
//					region.setTexture( texture );
//					region.setRegion( 0 , 0 , texture.getWidth() , texture.getHeight() );
//					newWidth = sys_view.mCustomCache.getWidth();
//					newHeight = sys_view.mCustomCache.getHeight();
//					int temp_w = itemInfo.spanX * ConfigBase.Workspace_cell_each_width;
//					int temp_h = itemInfo.spanY * ConfigBase.Workspace_cell_each_height;
//					if( ConfigBase.widget_revise_complete )
//					{
//						temp_w = itemInfo.spanX * ConfigBase.Workspace_cell_each_width_ori;
//						temp_h = itemInfo.spanY * ConfigBase.Workspace_cell_each_height_ori;
//					}
//					int r_w = newWidth;
//					int r_h = newHeight;
//					if( r_w > temp_w )
//					{
//						r_w = temp_w;
//						r_h = (int)( (float)newHeight * r_w / newWidth );
//					}
//					if( r_h > temp_h )
//					{
//						r_h = temp_h;
//						r_w = (int)( (float)newWidth * r_h / newHeight );
//					}
//					this.x += ( width - r_w ) / 2;
//					this.y += ( height - r_h ) / 2;
//					setSize( r_w , r_h );
//					setOrigin( getWidth() / 2 , getHeight() / 2 );
//				}
//			}
//		}
//		else
//		{
			synchronized( WidgetHostView.drawLock )
			{
				switch( state )
				{
					case WidgetHostView.DRAW_NONE:
						int i = 0;
						while( !sys_view.hasRun && i < 20 )
						{
							try
							{
								Thread.sleep( 20 );
								i++;
								//Log.i("opt", "sleep...");
							}
							catch( InterruptedException e )
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if( sys_view.hasDraw )
							return;
						else
							break;
					case WidgetHostView.DRAW_START:
						if( sys_view != null && ( sys_view.state == WidgetHostView.DRAW_STOP || sys_view.state == WidgetHostView.DRAW_NONE ) )
						{
							state = WidgetHostView.DRAW_RUN;
						}
						break;
					case WidgetHostView.DRAW_RUN:
						hasRun = true;
						if( sys_view != null )
						{
							sys_view.state = WidgetHostView.DRAW_NONE;
						}
						break;
					case WidgetHostView.DRAW_STOP:
						//cache = false;
						if( sys_view != null && sys_view.state == WidgetHostView.DRAW_START )
						{
							state = WidgetHostView.DRAW_NONE;
							sys_view.state = WidgetHostView.DRAW_RUN;
							//Log.i("widget", "gl draw return!");
							//return;
						}
						break;
				}
				//Log.d("opt", "gl  draw state2="+getState());
			}
//		}
		if( cache && sys_view != null && ( sys_view.mUpdated > 0 || texture == null || texture.isDisposed() || sys_view.update_suc == 2 ) )
		{
			//Log.e("widget", "update:"+itemInfo.appWidgetId);
			if( sys_view.update_suc == 0 && !sys_view.isWidget3D )
			{
				boolean ignore = false;
				if( sys_view.getParent() != null && sys_view.getParent().getParent() != null )
				{
					ViewParent v = sys_view.getParent().getParent();
					if( v instanceof Workspace )
					{
						Workspace workspace = (Workspace)v;
						if( ( workspace.getVisibility() != View.VISIBLE || workspace.getLauncher().getDesktop().hasDown() ) && sys_view.mCustomCache != null && texture != null )
						{
							ignore = true;
						}
					}
				}
				if( !ignore )
				{
					sys_view.update_suc = 1;
					time = System.currentTimeMillis();
					//Log.e("widget", "request capture");
					Messenger.sendMsg( Messenger.EVENT_WIDGET_GET_VIEW , sys_view );
					if( texture == null || texture.isDisposed() )
					{
						int i = 0;
						while( i < 10 && sys_view.update_suc != 2 )
						{
							try
							{
								//Log.e("launcher", "texture==null wait:"+itemInfo.appWidgetId);
								Thread.sleep( 20 );
							}
							catch( InterruptedException e )
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							i++;
						}
					}
				}
			}
			synchronized( sys_view.bmpLock )
			{
				if( sys_view.update_suc == 2 && sys_view.mCustomCache != null )
				{
					time2 = System.currentTimeMillis();
					sys_view.update_suc = 0;
					// 
					if( texture != null )
						texture.dispose();
					//
					texture = new BitmapTexture( sys_view.mCustomCache );
					/***
					 *NOTE: the following codes may cause assert while on move event,it should be replaced with  before.
					 */
					//if(texture == null)texture = new BitmapTexture(sys_view.mCustomCache);
					//else texture.changeBitmap(sys_view.mCustomCache);
					texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					region.setTexture( texture );
					region.setRegion( 0 , 0 , texture.getWidth() , texture.getHeight() );
					newWidth = sys_view.mCustomCache.getWidth();
					newHeight = sys_view.mCustomCache.getHeight();
					int temp_w = itemInfo.spanX * ConfigBase.Workspace_cell_each_width;
					int temp_h = itemInfo.spanY * ConfigBase.Workspace_cell_each_height;
					if( ConfigBase.widget_revise_complete )
					{
						temp_w = itemInfo.spanX * ConfigBase.Workspace_cell_each_width_ori;
						temp_h = itemInfo.spanY * ConfigBase.Workspace_cell_each_height_ori;
					}
					int r_w = newWidth;
					int r_h = newHeight;
					if( r_w > temp_w )
					{
						r_w = temp_w;
						r_h = (int)( (float)newHeight * r_w / newWidth );
					}
					if( r_h > temp_h )
					{
						r_h = temp_h;
						r_w = (int)( (float)newWidth * r_h / newHeight );
					}
					this.x += ( width - r_w ) / 2;
					this.y += ( height - r_h ) / 2;
					setSize( r_w , r_h );
					setOrigin( getWidth() / 2 , getHeight() / 2 );
					if( !canScrollV() )
					{
						sys_view.mCustomCache.recycle();
						sys_view.mCustomCache = null;
						sys_view.localCanvas = null;
						System.gc();
					}
					//Log.e("widget", "end 1:         " + (System.currentTimeMillis() - time)+" "+
					//		"render:"+(System.currentTimeMillis()-time2));
					//Log.i("launcher", "cacheSysView:"+itemInfo.appWidgetId);
				}
			}
		}
		//Log.v("icon", "alpha:" + (color.a * parentAlpha) + " sx" + this.scaleX + " sy" + this.scaleY + " w:" + width + " h:" + height + "r:" + rotation );
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		if( texture != null && !texture.isDisposed() )
		{
			//Log.e("launcher", "widget:"+name+"x,y="+this.x+","+this.y);
			if( is3dRotation() )
				batch.draw( region , (int)x , (int)y , width , height );
			else
				batch.draw( region , (int)x , (int)y , originX , originY , width , height , scaleX , scaleY , rotation );
			//Log.e("widget", "end 2:         " + (System.currentTimeMillis() - time)+" "+
			//		"render:"+(System.currentTimeMillis()-time2));
			//if(sys_view.checkScrollV)Log.d("widget", "gl draw end");
		}
		else
		{
			//Log.e("launcher", "texture==null:"+itemInfo.appWidgetId);
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "widget" , "onLongClick:x,y=" + x + "," + y + "---cancel" );
		//if(!sys_view.longClickable())return true;
		itemInfo.checkMove();
		Messenger.sendMsg( Messenger.MSG_START_COVER_MTKWIDGET , 0 , 0 );
		int index = MotionEventPool.get( 0 , 0 , MotionEvent.ACTION_CANCEL , 0 , 0 );
		//		Log.v("jbc","widgetko onLongClick index="+index);
		Messenger.sendMsg( Messenger.EVENT_UPDATE_SYS_WIDGET , itemInfo.hostView , index , 0 );
		Messenger.sendMsg( Messenger.EVENT_WIDGET_GET_VIEW , sys_view );
		if( !this.isDragging )
		{
			//			Log.v("jbc","widgetko onLongClick point: x="+point.x+" y="+point.y);
			Rect rect = new Rect();
			this.toAbsoluteCoords( point );
			rect.left = (int)point.x;
			rect.top = (int)point.y;
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			rect.right = (int)point.x;
			rect.bottom = (int)point.y;
			this.setTag( rect );
			//			DragLayer3D.dragStartX = point.x;
			//			DragLayer3D.dragStartY = point.y;
			return viewParent.onCtrlEvent( this , MSG_WIDGET_LONGCLICK );
		}
		return false;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Log.v( "widget" , "widgetabc onClick:x=" + x + ",y=" + y );
		if( itemInfo.hostView != null )
		{
			ViewGroup parent = (ViewGroup)itemInfo.hostView.getParent().getParent();
			if( parent instanceof Workspace )
			{
				Workspace workspace = (Workspace)parent;
				LauncherBase launcher = workspace.getLauncher();
				if( launcher != null )
				{
					if( !launcher.isWorkspace3DTouchable() )
						return true;
				}
			}
		}
		float xx = x / ( this.width / newWidth );
		float yy = newHeight - 1 - ( y / ( this.height / newHeight ) );
		int index = MotionEventPool.get( downTime , SystemClock.uptimeMillis() - downTime , MotionEvent.ACTION_UP , xx , yy );
		//Log.v("onclick","this:" + this +" xx:" + xx + " yy:" + yy + " x:" + x + " y:" + y);
		//itemInfo.hostView.dispatchTouchEvent(ev);
		//		Log.v("launcher","onClick:x,y,xx,yy=" + x+","+y+"," + xx+","+yy+
		//				"---up");
		Messenger.sendMsg( Messenger.EVENT_UPDATE_SYS_WIDGET , itemInfo.hostView , index , 0 );
		return true;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		//Log.v("widget","widget onDoubleClick:x="+x+",y="+y);
		return true;
	}
	
	public Widget2DInfo getItemInfo()
	{
		return itemInfo;
	}
	
	@Override
	public void setItemInfo(
			ItemInfo info )
	{
		// TODO Auto-generated method stub
		itemInfo = (Widget2DInfo)info;
	}
	
	public void dispose()
	{
		if( texture != null )
		{
			Log.i( "widget" , "widget release texture:" + texture.getWidth() * texture.getHeight() * 4 / 1000 );
			texture.dispose();
			texture = null;
		}
		if( sys_view != null && sys_view.mCustomCache != null )
		{
			if( !sys_view.mCustomCache.isRecycled() )
				Log.i( "widget" , "widget release bitmap:" + sys_view.mCustomCache.getWidth() * sys_view.mCustomCache.getHeight() * 4 / 1000 );
			sys_view.mCustomCache.recycle();
			sys_view.mCustomCache = null;
			sys_view.localCanvas = null;
		}
		if( sys_view != null )
		{
			sys_view.mUpdated = 0;
			sys_view.update_suc = 0;
		}
	}
	
	//	@Override
	//	public void setScale(float x, float y) {
	//		// TODO Auto-generated method stub
	////		super.setScale(x, y);
	//	}
	public static class MotionEventPool
	{
		
		private static ArrayList<MotionEvent> events = new ArrayList<MotionEvent>( 10 );
		private static long downTime = 0;
		
		private static MotionEvent create(
				long downTime ,
				long eventTime ,
				int action ,
				float x ,
				float y )
		{
			MotionEvent ev = MotionEvent.obtain( downTime , eventTime , action , x , y , 0 );
			events.add( ev );
			return ev;
		}
		
		public static int get(
				long downTime ,
				long eventTime ,
				int action ,
				float x ,
				float y )
		{
			if( action == MotionEvent.ACTION_DOWN )
				MotionEventPool.downTime = SystemClock.uptimeMillis();
			MotionEvent event = create( MotionEventPool.downTime , SystemClock.uptimeMillis() , action , x , y );
			return events.indexOf( event );
		}
		
		public static MotionEvent getEvent(
				int index )
		{
			return events.get( index );
		}
	}
	
	public void focus()
	{
		if( itemInfo.hostView != null )
		{
			Messenger.sendMsg( Messenger.MSG_SYS_WIDGET_FOCUS , itemInfo.hostView );
		}
	}
	
	/************************ added by zhujieping begin ***************************/
	private void drawParticleEffect(
			SpriteBatch batch )
	{
		if( ParticleManager.particleManagerEnable )
		{
			if( ParticleManager.dropEnable )
			{
				Tween tween = getTween();
				if( tween != null )
				{
					float targetValues[] = tween.getTargetValues();
					float targetCenterX = targetValues[0] + width / 2;
					float targetCenterY = targetValues[1] + height / 2;
					float curCenterX = x + width / 2;
					float curCenterY = y + height / 2;
					if( Math.abs( curCenterX - targetCenterX ) < 2 || Math.abs( curCenterY - targetCenterY ) < 2 )
					{
						stopParticle( ParticleManager.PARTICLE_TYPE_NAME_DROP );
						startParticle( ParticleManager.PARTICLE_TYPE_NAME_DROP , targetCenterX , targetCenterY );
						ParticleManager.dropEnable = false;
					}
				}
			}
			drawParticle( batch );
		}
	}
	
	/************************ added by zhujieping end ***************************/
	public void disttach()
	{
		if( sys_view != null && sys_view.getParent() != null )
			( (ViewGroup)( sys_view.getParent() ) ).removeView( sys_view );
	}
}
