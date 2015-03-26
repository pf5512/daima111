package com.iLoong.launcher.UI3DEngine;


import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.ParticleAnim.ParticleCallback;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class View3D extends Actor implements TweenCallback , ParticleCallback
{
	
	public TextureRegion region;
	public NinePatch background9;
	public boolean isDragging = false;
	public ViewGroup3D viewParent;
	public static final Vector2 point = new Vector2();
	private Object tag;
	protected final Matrix4 localTransform = new Matrix4();
	protected final Matrix4 worldTransform = new Matrix4();
	protected final Matrix4 batchTransform = new Matrix4();
	protected final Matrix4 oldBatchTransform = new Matrix4();
	protected Vector3 rotationVector = new Vector3( 0 , 0 , 1 );
	private Quaternion quaternion = new Quaternion();
	private Tween tween;
	private float tweenUserValue;
	private float tweenUserValue2;
	protected float z = 0f;
	protected float originZ = 0f;
	protected float scaleZ = 1f;
	/************************ added by zhenNan.ye begin ***************************/
	public boolean particleCanRender = false;
	public String particleType = null;
	private Matrix4 particleOldMatrix4 = new Matrix4();
	private Matrix4 particleTransformMatrix = new Matrix4();
	/************************ added by zhenNan.ye end ***************************/
	protected boolean isReflectView = false;//jbc add
	public boolean alwaysHided = false;
	protected IOnRemoveCallBack mOnRemoveCallBack;
	public static Timeline mMainTimeline = null;
	
	public View3D(
			String name )
	{
		// TODO Auto-generated constructor stub
		super( name );
		x = 0;
		y = 0;
		width = UtilsBase.getScreenWidth();
		height = UtilsBase.getScreenHeight();
		originX = width / 2.0f;
		originY = height / 2.0f;
		this.region = new TextureRegion();
	}
	
	public View3D(
			String name ,
			Texture texture )
	{
		super( name );
		this.originX = texture.getWidth() / 2.0f;
		this.originY = texture.getHeight() / 2.0f;
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.region = new TextureRegion( texture );
	}
	
	public View3D(
			String name ,
			TextureRegion region )
	{
		super( name );
		width = Math.abs( region.getRegionWidth() );
		height = Math.abs( region.getRegionHeight() );
		originX = width / 2.0f;
		originY = height / 2.0f;
		this.region = new TextureRegion( region );
	}
	
	public void setBackgroud(
			NinePatch ninePatch )
	{
		background9 = ninePatch;
	}
	
	public void act(
			float delta )
	{
		actions.iter();
		Action action;
		while( ( action = actions.next() ) != null )
		{
			action.act( delta );
			if( action.isDone() )
			{
				action.finish();
				actions.remove();
			}
		}
	}
	
	public boolean needAntiAlias()
	{
		if( rotation != 0 && ( getRotation3DVector().x != 0 || getRotation3DVector().y != 0 || getRotation3DVector().z != 0 ) )
			return true;
		if( scaleX != 1 || scaleY != 1 )
			return true;
		if( this.getParent() != null && this.getParent().needAntiAlias() )
			return true;
		return false;
	}
	
	public Matrix4 getWorldTransform()
	{
		return worldTransform;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		/************************ added by zhenNan.ye begin *************************/
		if( ParticleManager.particleManagerEnable )
		{
			drawParticle( batch );
		}
		/************************ added by zhenNan.ye end ***************************/
		int x = Math.round( this.x );
		int y = Math.round( this.y );
		if( region.getTexture() == null )
			return;
		if( region != null && region.getTexture() != null && region.getTexture() instanceof BitmapTexture )
			( (BitmapTexture)region.getTexture() ).dynamicLoad();
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		if( is3dRotation() )
			batch.draw( region , x , y , width , height );
		else
			batch.draw( region , x , y , originX , originY , width , height , scaleX , scaleY , rotation );
	}
	
	public void bringToFront()
	{
		if( this.viewParent != null )
		{
			this.viewParent.moveViewToLast( this );
		}
	}
	
	public final boolean touchDown(
			float x ,
			float y ,
			int pointer )
	{
		return false;
	}
	
	public final void touchUp(
			float x ,
			float y ,
			int pointer )
	{
	}
	
	public final void touchDragged(
			float x ,
			float y ,
			int pointer )
	{
	}
	
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		return false;
	}
	
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		return false;
	}
	
	//teapotXu add start for handle actions when touch area leaves current view
	/*
	 * handle the action when touch area leaves current view
	 * */
	public boolean handleActionWhenTouchLeave()
	{
		return false;
	}
	
	public final void addTouchedView()
	{
		Object obj = this.getStage();
		if( obj != null && obj instanceof Desktop3D )
		{
			Desktop3D desktop = (Desktop3D)obj;
			desktop.addTouchedView( this );
		}
	}
	
	public final void removeTouchedView()
	{
		Object obj = this.getStage();
		if( obj != null && obj instanceof Desktop3D )
		{
			Desktop3D desktop = (Desktop3D)obj;
			desktop.removeTouchedView( this );
		}
	}
	
	//teapotXu add end
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean keyTyped(
			char character )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean zoom(
			float originalDistance ,
			float currentDistance )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public View3D hit(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( x >= 0 && x < width )
			if( y >= 0 && y < height )
				return this;
		return null;
	}
	
	/*
	 * x,y in parent coordinate
	 */
	public boolean pointerInParent(
			float x ,
			float y )
	{
		Group.toChildCoordinates( this , x , y , point );
		return( ( point.x >= 0 && point.x < width ) && ( point.y >= 0 && point.y < height ) );
	}
	
	/*
	 * x,y in Absolute coordinate
	 */
	public boolean pointerInAbs(
			float x ,
			float y )
	{
		point.x = x;
		point.y = y;
		toLocalCoordinates( point );
		return( ( point.x >= 0 && point.x < width ) && ( point.y >= 0 && point.y < height ) );
	}
	
	/** Removes this view from the Stage */
	@Override
	public void remove()
	{
		if( viewParent != null )
			viewParent.removeView( this );
	}
	
	/**
	 * Transforms the given point in stage coordinates to the Actor's local
	 * coordinate system.
	 * 
	 * @param point
	 *            the point
	 */
	@Override
	public void toLocalCoordinates(
			Vector2 point )
	{
		if( viewParent == null )
			return;
		viewParent.toLocalCoordinates( point );
		Group.toChildCoordinates( this , point.x , point.y , point );
	}
	
	public ViewGroup3D getParent()
	{
		return this.viewParent;
	}
	
	public int getIndexInParent()
	{
		if( viewParent != null )
		{
			return viewParent.children.indexOf( this );
		}
		return -1;
	}
	
	public float getZ()
	{
		return z;
	}
	
	public void setZ(
			float f )
	{
		z = f;
	}
	
	public float getOriginZ()
	{
		return originZ;
	}
	
	public void setOriginZ(
			float f )
	{
		originZ = f;
	}
	
	public float getScaleZ()
	{
		return scaleZ;
	}
	
	public void setScaleZ(
			float f )
	{
		scaleZ = f;
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public void setSize(
			float w ,
			float h )
	{
		width = w;
		height = h;
	}
	
	public void setOrigin(
			float oriX ,
			float oriY )
	{
		this.originX = oriX;
		this.originY = oriY;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public float getHeight()
	{
		// TODO Auto-generated method stub
		return height;
	}
	
	public Color getColor()
	{
		// TODO Auto-generated method stub
		return color;
	}
	
	public float getRotation()
	{
		// TODO Auto-generated method stub
		return rotation;
	}
	
	public Vector3 getRotation3DVector()
	{
		// TODO Auto-generated method stub
		return rotationVector;
	}
	
	public float getScaleX()
	{
		// TODO Auto-generated method stub
		return scaleX;
	}
	
	public float getScaleY()
	{
		// TODO Auto-generated method stub
		return scaleY;
	}
	
	public void setPosition(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		this.x = x;
		this.y = y;
	}
	
	public void setColor(
			float r ,
			float g ,
			float b ,
			float a )
	{
		this.color.r = r;
		this.color.g = g;
		this.color.b = b;
		this.color.a = a;
	}
	
	public void setColor(
			Color color )
	{
		this.color.r = color.r;
		this.color.g = color.g;
		this.color.b = color.b;
		this.color.a = color.a;
	}
	
	public void setColor(
			int argb_8888 )
	{
		int a = ( argb_8888 >> 24 ) & 255;
		int r = ( argb_8888 >> 16 ) & 255;
		int g = ( argb_8888 >> 8 ) & 255;
		int b = ( argb_8888 >> 0 ) & 255;
		this.color.r = (float)r / 255;
		this.color.g = (float)g / 255;
		this.color.b = (float)b / 255;
		this.color.a = (float)a / 255;
	}
	
	public void setRotationX(
			float rotate )
	{
		setRotationVector( 1 , 0 , 0 );
		this.rotation = rotate;
	}
	
	public void setRotationY(
			float rotate )
	{
		setRotationVector( 0 , 1 , 0 );
		this.rotation = rotate;
	}
	
	public void setRotationZ(
			float rotate )
	{
		setRotationVector( 0 , 0 , 1 );
		this.rotation = rotate;
	}
	
	public void setRotation(
			float rotate )
	{
		this.rotation = rotate;
	}
	
	public void setRotationVector(
			float x ,
			float y ,
			float z )
	{
		rotationVector.x = x;
		rotationVector.y = y;
		rotationVector.z = z;
	}
	
	public void setRotationAngle(
			float x ,
			float y ,
			float z )
	{
		Quaternion q = quaternion;
		q.idt();
		q.setEulerAngles( x , y , z );
		float scale = (float)Math.sqrt( q.x * q.x + q.y * q.y + q.z * q.z );
		rotationVector.x = q.y / scale;
		rotationVector.y = q.x / scale;
		rotationVector.z = q.z / scale;
		rotation = (float)Math.toDegrees( ( Math.acos( q.w ) * 2.0f ) );
	}
	
	public void addRotation(
			float x ,
			float y ,
			float z ,
			float rotate )
	{
		Quaternion q = quaternion;
		Quaternion q1 = new Quaternion();
		Quaternion q2 = new Quaternion();
		q1.setFromAxis( rotationVector , rotation );
		q2.setFromAxis( x , y , z , rotate );
		q = q1.mulLeft( q2 );
		float scale = (float)Math.sqrt( q.x * q.x + q.y * q.y + q.z * q.z );
		rotationVector.x = q.x / scale;
		rotationVector.y = q.y / scale;
		rotationVector.z = q.z / scale;
		rotation = (float)Math.toDegrees( ( Math.acos( q.w ) * 2.0f ) );
	}
	
	public void addRotationX(
			float rotate )
	{
		addRotation( 1 , 0 , 0 , rotate );
	}
	
	public void addRotationY(
			float rotate )
	{
		addRotation( 0 , 1 , 0 , rotate );
	}
	
	public void addRotationZ(
			float rotate )
	{
		addRotation( 0 , 0 , 1 , rotate );
	}
	
	public void setScale(
			float x ,
			float y )
	{
		this.scaleX = x;
		this.scaleY = y;
	}
	
	// 用户自定义动画变�?
	public void setUser(
			float value )
	{
		tweenUserValue = value;
	}
	
	public float getUser()
	{
		return tweenUserValue;
	}
	
	public void setUser2(
			float value )
	{
		tweenUserValue2 = value;
	}
	
	public float getUser2()
	{
		return tweenUserValue2;
	}
	
	public void setTag(
			Object obj )
	{
		this.tag = obj;
	}
	
	public Object getTag()
	{
		return this.tag;
	}
	
	public void show()
	{
		if( alwaysHided )
			return;
		this.visible = true;
		this.touchable = true;
	}
	
	public void setVisible(
			boolean vis )
	{
		this.visible = vis;
	}
	
	public boolean getVisible()
	{
		return this.visible;
	}
	
	public void hide()
	{
		this.visible = false;
		this.touchable = false;
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public boolean isVisibleInParent()
	{
		boolean isVisible = this.visible;
		if( viewParent != null )
		{
			isVisible &= viewParent.isVisibleInParent();
		}
		return isVisible;
	}
	
	protected void setStage(
			Stage s )
	{
		this.stage = s;
	}
	
	@Override
	public View3D clone()
	{
		View3D view = new View3D( this.name , this.region );
		if( this.background9 != null )
		{
			view.setBackgroud( this.background9 );
		}
		return view;
	}
	
	public final void requestFocus()
	{
		Object obj = this.getStage();
		if( obj != null && obj instanceof Desktop3D )
		{
			Desktop3D desktop = (Desktop3D)obj;
			if( desktop.getFocus() == null )
				desktop.setFocus( this );
			else
			{
				Log.w( "cooee" , "requestFocus is not null:" + desktop.getFocus() );
			}
		}
	}
	
	public final void releaseFocus()
	{
		Object obj = this.getStage();
		if( obj != null && obj instanceof Desktop3D )
		{
			Desktop3D desktop = (Desktop3D)obj;
			if( desktop.getFocus() == this )
			{
				desktop.setFocus( null );
			}
		}
	}
	
	/*
	 * 获得绝对坐标
	 */
	public void toAbsoluteCoords(
			Vector2 point )
	{
		point.x = 0;
		point.y = 0;
		toAbsolute( point );
	}
	
	public void toAbsolute(
			Vector2 point )
	{
		if( this.viewParent == null )
			return;
		point.x += x;
		point.y += y;
		viewParent.toAbsolute( point );
	}
	
	/*
	 * tweenType: one of {@link
	 * View3DTweenAccessor.POS_XY|CPOS_XY|SCALE_XY|ROTATIONOPACITY|TINT}
	 * tweenEquation: one of package equations,ex:Bounce.OUT duration: the tween
	 * length of time target: params of the target,ex: if tweenType ==
	 * View3DTweenAccessor.POS_XY,(target1 == x,target2 == y)
	 */
	public Tween startTween(
			int tweenType ,
			TweenEquation tweenEquation ,
			float duration ,
			float target1 ,
			float target2 ,
			float target3 )
	{
		tween = Tween.to( this , tweenType , duration ).target( target1 , target2 , target3 ).ease( tweenEquation ).start( View3DTweenAccessor.manager );
		return tween;
	}
	
	public Tween obtainTween(
			int tweenType ,
			TweenEquation tweenEquation ,
			float duration ,
			float target1 ,
			float target2 ,
			float target3 )
	{
		tween = Tween.to( this , tweenType , duration ).target( target1 , target2 , target3 ).ease( tweenEquation );
		return tween;
	}
	
	public void stopTween()
	{
		if( tween != null )
			tween.free();
		tween = null;
	}
	
	public void stopAllTween()
	{
		View3DTweenAccessor.manager.killTarget( this );
		tween = null;
	}
	
	public Tween getTween()
	{
		return tween;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == mMainTimeline && TweenCallback.COMPLETE == type )
		{
			mOnRemoveCallBack.onRemoveCompleted();
		}
	}
	
	public void applyTransformChild(
			SpriteBatch batch )
	{
		Matrix4 newBatchTransform = updateTransform();
		batch.end();
		oldBatchTransform.set( batch.getTransformMatrix() );
		batch.setTransformMatrix( newBatchTransform );
		batch.begin();
	}
	
	private Matrix4 updateTransform()
	{
		Matrix4 temp = worldTransform;
		if( originX != 0 || originY != 0 || originZ != 0 )
			localTransform.setToTranslation( originX + x , originY + y , originZ + z );
		else
			localTransform.idt();
		if( rotation != 0 )
			localTransform.mul( temp.setToRotation( rotationVector.x , rotationVector.y , rotationVector.z , rotation ) );
		if( scaleX != 1 || scaleY != 1 || scaleZ != 1 )
			localTransform.mul( temp.setToScaling( scaleX , scaleY , scaleZ ) );
		if( originX != 0 || originY != 0 || originZ != 0 )
			localTransform.mul( temp.setToTranslation( -( originX + x ) , -( originY + y ) , -( originZ + z ) ) );
		// localTransform.trn(x, y,0);
		ViewGroup3D parentGroup = viewParent;
		while( parentGroup != null )
		{
			if( parentGroup.transform )
				break;
			parentGroup = parentGroup.viewParent;
		}
		if( parentGroup != null )
		{
			worldTransform.set( parentGroup.worldTransform );
			worldTransform.mul( localTransform );
		}
		else
		{
			worldTransform.set( localTransform );
		}
		batchTransform.set( worldTransform );
		return batchTransform;
	}
	
	public void resetTransformChild(
			SpriteBatch batch )
	{
		batch.end();
		batch.setTransformMatrix( oldBatchTransform );
		batch.begin();
	}
	
	public boolean is3dRotation()
	{
		return rotation != 0 && ( getRotation3DVector().x != 0 || getRotation3DVector().y != 0 );
	}
	
	public void disposeTexture()
	{
		if( this.region != null && this.region.getTexture() != null )
		{
			this.region.getTexture().dispose();
		}
		if( this.background9 != null )
		{
			TextureRegion[] tr = background9.getPatches();
			for( int i = 0 ; i < tr.length ; i++ )
			{
				if( tr[i] != null && tr[i].getTexture() != null )
				{
					tr[i].getTexture().dispose();
				}
			}
		}
	}
	
	public Vector2 getCenterSize()
	{
		return new Vector2( width * 0.5f , height * 0.5f );
	}
	
	public void measure(
			int childWidthMeasureSpec ,
			int childheightMeasureSpec )
	{
		// TODO Auto-generated method stub
	}
	
	public void dispose()
	{
		disposeTexture();
	}
	
	public void releaseDark()
	{
		// TODO Auto-generated method stub
	}
	
	/************************ added by zhenNan.ye begin *************************/
	public ParticleAnim startParticle(
			String particleType ,
			float positionX ,
			float positionY )
	{
		particleCanRender = true;
		this.particleType = particleType;
		ParticleManager manager = ParticleManager.getParticleManager();
		return manager.start( this , particleType , positionX , positionY );
	}
	
	public void stopParticle(
			String particleType )
	{
		ParticleManager manager = ParticleManager.getParticleManager();
		manager.stop( this , particleType );
	}
	
	public void pauseParticle(
			String particleType )
	{
		ParticleManager manager = ParticleManager.getParticleManager();
		manager.pause( this , particleType );
	}
	
	public void drawParticle(
			SpriteBatch batch )
	{
		if( particleCanRender )
		{
			ParticleManager manager = ParticleManager.getParticleManager();
			if( !manager.isFinish( this , this.particleType ) )
			{
				particleOldMatrix4.set( batch.getTransformMatrix() );
				batch.end();
				batch.setTransformMatrix( particleTransformMatrix );
				batch.begin();
				int srcBlendFunc = batch.getSrcBlendFunc();
				int dstBlendFunc = batch.getDstBlendFunc();
				batch.setBlendFunction( GL10.GL_SRC_ALPHA , GL10.GL_ONE_MINUS_SRC_ALPHA );
				manager.draw( this , this.particleType , batch , Gdx.graphics.getDeltaTime() );
				batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
				batch.end();
				batch.setTransformMatrix( particleOldMatrix4 );
				batch.begin();
			}
			else
			{
				particleCanRender = false;
				this.particleType = null;
			}
		}
	}
	
	public void updateParticle(
			String particleType ,
			float positionX ,
			float positionY )
	{
		particleCanRender = true;
		this.particleType = particleType;
		ParticleManager manager = ParticleManager.getParticleManager();
		manager.update( this , particleType , positionX , positionY );
	}
	
	@Override
	public void onParticleCallback(
			int type )
	{
		// TODO Auto-generated method stub
	}
	
	/************************ added by zhenNan.ye end ***************************/
	public void setReflect(
			boolean isReflect )
	{
		this.isReflectView = isReflect;
	}
	
	public boolean isReflectView()
	{
		return this.isReflectView;
	}
	
	public void newDrawParticle(
			SpriteBatch batch )
	{
		ParticleManager manager = ParticleManager.getParticleManager();
		particleOldMatrix4.set( batch.getTransformMatrix() );
		batch.end();
		batch.setTransformMatrix( particleTransformMatrix );
		batch.begin();
		int srcBlendFunc = batch.getSrcBlendFunc();
		int dstBlendFunc = batch.getDstBlendFunc();
		batch.setBlendFunction( GL10.GL_SRC_ALPHA , GL10.GL_ONE_MINUS_SRC_ALPHA );
		manager.newDraw( this , this.particleType , batch , Gdx.graphics.getDeltaTime() );
		batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
		batch.end();
		batch.setTransformMatrix( particleOldMatrix4 );
		batch.begin();
	}
	
	public void newUpdateParticle(
			String particleType ,
			float positionX ,
			float positionY )
	{
		if( particleType == null )
		{
			return;
		}
		particleCanRender = true;
		this.particleType = particleType;
		ParticleManager manager = ParticleManager.getParticleManager();
		manager.newUpdate( this , particleType , positionX , positionY );
	}
	
	public void newStopParticle(
			String particleType )
	{
		ParticleManager manager = ParticleManager.getParticleManager();
		manager.newStop( this , particleType );
	}
	
	public ParticleAnim newStartParticle(
			String particleType ,
			float positionX ,
			float positionY )
	{
		particleCanRender = true;
		this.particleType = particleType;
		if( particleType == null )
		{
			return null;
		}
		ParticleManager manager = ParticleManager.getParticleManager();
		return manager.newStart( this , particleType , positionX , positionY );
	}
	
	public void releaseRegion()
	{
		disposeTexture();
	}
	
	public static interface IOnRemoveCallBack
	{
		
		public void onRemoveCompleted();
	}
	
	public void onRecycled(
			float targetx ,
			float targety ,
			IOnRemoveCallBack mOnRemoveCallBack )
	{
		this.mOnRemoveCallBack = mOnRemoveCallBack;
		float duration = 0.5f;
		int tweenType = View3DTweenAccessor.CPOS_XY;
		if( mMainTimeline != null )
		{
			mMainTimeline.free();
		}
		mMainTimeline = Timeline.createParallel();
		mMainTimeline.push( Tween.to( this , tweenType , duration ).target( targetx , targety , 0 ).ease( Cubic.OUT ) );
		mMainTimeline.push( Tween.to( this , View3DTweenAccessor.OPACITY , duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
		mMainTimeline.start( View3DTweenAccessor.manager ).setCallback( this );
	}
}
