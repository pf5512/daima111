package com.iLoong.launcher.UI3DEngine;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Cullable;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectMap;
import com.iLoong.launcher.Desktop3D.Log;


public class ViewGroup3D extends View3D implements Cullable
{
	
	public static Texture debugTexture;
	public static boolean debug = false;
	protected final List<View3D> children;
	protected final List<View3D> immutableChildren;
	protected final List<ViewGroup3D> groups;
	protected final List<ViewGroup3D> immutableGroups;
	protected final ObjectMap<String , View3D> namesToActors;
	public boolean transform = false;
	public Actor lastTouchedChild;
	//teapotXu_20130325 add start: for roll effect
	public boolean children_drawn_in_reverse_order = false;
	//teapotXu_20130325 add end
	protected Rectangle cullingArea;
	protected final Vector2 point = new Vector2();
	
	public ViewGroup3D()
	{
		this( null );
	}
	
	/** Creates a new Group with the given name.
	 * @param name the name of the group */
	public ViewGroup3D(
			String name )
	{
		super( name );
		children = new ArrayList<View3D>();
		immutableChildren = Collections.unmodifiableList( children );
		groups = new ArrayList<ViewGroup3D>();
		immutableGroups = Collections.unmodifiableList( groups );
		namesToActors = new ObjectMap<String , View3D>();
	}
	
	public void act(
			float delta )
	{
		super.act( delta );
		for( int i = 0 ; i < children.size() ; i++ )
		{
			View3D child = children.get( i );
			child.act( delta );
			if( child.isMarkedToRemove() )
			{
				child.markToRemove( false );
				removeView( child );
				i--;
			}
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( debug && debugTexture != null && parent != null )
			batch.draw(
					debugTexture ,
					x ,
					y ,
					originX ,
					originY ,
					width == 0 ? 200 : width ,
					height == 0 ? 200 : height ,
					scaleX ,
					scaleY ,
					rotation ,
					0 ,
					0 ,
					debugTexture.getWidth() ,
					debugTexture.getHeight() ,
					false ,
					false );
		if( transform )
			applyTransform( batch );
		drawChildren( batch , parentAlpha );
		if( transform )
			resetTransform( batch );
	}
	
	protected void drawChildren(
			SpriteBatch batch ,
			float parentAlpha )
	{
		parentAlpha *= color.a;
		if( batch == null )
		{
			return;
		}
		if( cullingArea != null )
		{
			if( transform )
			{
				for( int i = 0 ; i < children.size() ; i++ )
				{
					View3D child = children.get( i );
					if( child == null || !child.visible )
						continue;
					if( child.x <= cullingArea.x + cullingArea.width && child.x + child.width >= cullingArea.x && child.y <= cullingArea.y + cullingArea.height && child.y + child.height >= cullingArea.y )
					{
						if( child.background9 != null )
						{
							child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
						}
						child.draw( batch , parentAlpha );
					}
				}
				batch.flush();
			}
			else
			{
				float offsetX = x;
				float offsetY = y;
				x = 0;
				y = 0;
				for( int i = 0 ; i < children.size() ; i++ )
				{
					View3D child = children.get( i );
					if( child == null || !child.visible )
						continue;
					if( child.x <= cullingArea.x + cullingArea.width && child.x + child.width >= cullingArea.x && child.y <= cullingArea.y + cullingArea.height && child.y + child.height >= cullingArea.y )
					{
						child.x += offsetX;
						child.y += offsetY;
						if( child.background9 != null )
						{
							child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
						}
						child.draw( batch , parentAlpha );
						child.x -= offsetX;
						child.y -= offsetY;
					}
				}
				x = offsetX;
				y = offsetY;
			}
		}
		else
		{
			if( transform )
			{
				if( children_drawn_in_reverse_order == false )
				{
					for( int i = 0 ; i < children.size() ; i++ )
					{
						View3D child = children.get( i );
						if( child == null || !child.visible )
							continue;
						if( child instanceof ViewGroup3D )
						{
							if( child.background9 != null )
							{
								child.applyTransformChild( batch );
								batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
								child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
								child.resetTransformChild( batch );
							}
							child.draw( batch , parentAlpha );
							continue;
						}
						if( child.background9 != null )
						{
							child.applyTransformChild( batch );
							batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
							child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
							child.resetTransformChild( batch );
						}
						if( child.is3dRotation() )
						{
							child.applyTransformChild( batch );
						}
						child.draw( batch , parentAlpha );
						if( child.is3dRotation() )
						{
							child.resetTransformChild( batch );
						}
					}
				}
				else
				{
					for( int i = children.size() - 1 ; i >= 0 ; i-- )
					{
						View3D child = children.get( i );
						if( child == null || !child.visible )
							continue;
						if( child instanceof ViewGroup3D )
						{
							if( child.background9 != null )
							{
								child.applyTransformChild( batch );
								batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
								child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
								child.resetTransformChild( batch );
							}
							child.draw( batch , parentAlpha );
							continue;
						}
						if( child.background9 != null )
						{
							child.applyTransformChild( batch );
							batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
							child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
							child.resetTransformChild( batch );
						}
						if( child.is3dRotation() )
						{
							child.applyTransformChild( batch );
						}
						child.draw( batch , parentAlpha );
						if( child.is3dRotation() )
						{
							child.resetTransformChild( batch );
						}
					}
				}
				//				batch.flush();
			}
			else
			{
				float offsetX = x;
				float offsetY = y;
				x = 0;
				y = 0;
				if( children_drawn_in_reverse_order == false )
				{
					for( int i = 0 ; i < children.size() ; i++ )
					{
						View3D child = children.get( i );
						if( child == null || !child.visible )
							continue;
						child.x += offsetX;
						child.y += offsetY;
						if( child instanceof ViewGroup3D )
						{
							if( child.background9 != null )
							{
								child.applyTransformChild( batch );
								batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
								child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
								child.resetTransformChild( batch );
							}
							child.draw( batch , parentAlpha );
							child.x -= offsetX;
							child.y -= offsetY;
							continue;
						}
						if( child.background9 != null )
						{
							child.applyTransformChild( batch );
							batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
							child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
							child.resetTransformChild( batch );
						}
						if( child.is3dRotation() )
						{
							child.applyTransformChild( batch );
						}
						child.draw( batch , parentAlpha );
						if( child.is3dRotation() )
						{
							child.resetTransformChild( batch );
						}
						child.x -= offsetX;
						child.y -= offsetY;
					}
				}
				else
				{
					for( int i = children.size() - 1 ; i >= 0 ; i-- )
					{
						View3D child = children.get( i );
						if( child == null || !child.visible )
							continue;
						child.x += offsetX;
						child.y += offsetY;
						if( child instanceof ViewGroup3D )
						{
							if( child.background9 != null )
							{
								child.applyTransformChild( batch );
								batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
								child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
								child.resetTransformChild( batch );
							}
							child.draw( batch , parentAlpha );
							child.x -= offsetX;
							child.y -= offsetY;
							continue;
						}
						if( child.background9 != null )
						{
							child.applyTransformChild( batch );
							batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
							child.background9.draw( batch , Math.round( child.x ) , Math.round( child.y ) , child.width , child.height );
							child.resetTransformChild( batch );
						}
						if( child.is3dRotation() )
						{
							child.applyTransformChild( batch );
						}
						child.draw( batch , parentAlpha );
						if( child.is3dRotation() )
						{
							child.resetTransformChild( batch );
						}
						child.x -= offsetX;
						child.y -= offsetY;
					}
				}
				x = offsetX;
				y = offsetY;
			}
		}
	}
	
	protected void drawChild(
			Actor child ,
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( child.visible )
			child.draw( batch , parentAlpha * color.a );
		if( transform )
			batch.flush();
	}
	
	protected void applyTransform(
			SpriteBatch batch )
	{
		Matrix4 newBatchTransform = updateTransform();
		batch.end();
		oldBatchTransform.set( batch.getTransformMatrix() );
		batch.setTransformMatrix( newBatchTransform );
		batch.begin();
	}
	
	protected Matrix4 updateTransform()
	{
		Matrix4 temp = worldTransform;
		if( originX != 0 || originY != 0 || originZ != 0 )
			localTransform.setToTranslation( originX , originY , originZ );
		else
			localTransform.idt();
		if( rotation != 0 )
			localTransform.mul( temp.setToRotation( rotationVector.x , rotationVector.y , rotationVector.z , rotation ) );
		if( scaleX != 1 || scaleY != 1 || scaleZ != 1 )
			localTransform.mul( temp.setToScaling( scaleX , scaleY , scaleZ ) );
		if( originX != 0 || originY != 0 || originZ != 0 )
			localTransform.mul( temp.setToTranslation( -originX , -originY , -originZ ) );
		localTransform.trn( x , y , z );
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
	
	protected void resetTransform(
			SpriteBatch batch )
	{
		batch.end();
		batch.setTransformMatrix( oldBatchTransform );
		batch.begin();
	}
	
	public void setCullingArea(
			Rectangle cullingArea )
	{
		this.cullingArea = cullingArea;
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = children.get( i );
			if( !child.visible )
				continue;
			if( child.keyDown( keycode ) )
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean keyTyped(
			char character )
	{
		// TODO Auto-generated method stub
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = children.get( i );
			;
			if( !child.visible )
				continue;
			if( child.keyTyped( character ) )
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = children.get( i );
			;
			if( !child.visible )
				continue;
			if( child.keyUp( keycode ) )
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		//Log.d("jbc", "eee ViewG onTouchDown name="+name);
		if( !touchable || !visible )
			return false;
		int len = children.size() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = children.get( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			//			Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				toLocalCoordinates( child , point );
				if( child.onTouchDown( point.x , point.y , pointer ) )
				{
					if( child instanceof ViewGroup3D )
						lastTouchedChild = ( (ViewGroup3D)child ).lastTouchedChild;
					else
						lastTouchedChild = child;
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( !touchable || !visible )
			return false;
		int len = children.size() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = children.get( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			child.releaseDark();
			//			Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				toLocalCoordinates( child , point );
				if( child.onTouchUp( point.x , point.y , pointer ) )
					return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		if( !touchable || !visible )
			return false;
		int len = children.size() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = children.get( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			//			Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				if( ( toLocalCoordinates( child , point ) ) && ( child.onTouchDragged( point.x , point.y , pointer ) ) )
					return true;
			}
		}
		return false;
	}
	
	//	@Override
	//	public boolean touchMoved (float x, float y) {
	//		if (!touchable || !visible) return false;
	//
	//		int len = children.size() - 1;
	//		for (int i = len; i >= 0; i--) {
	//			Actor child = children.get(i);
	//			if (!child.touchable || !child.visible) continue;
	//
	//			toChildCoordinates(child, x, y, point);
	//
	//			if (child.touchMoved(point.x, point.y)) return true;
	//		}
	//		return false;
	//	}
	//	@Override 
	public boolean onClick(
			float x ,
			float y )
	{
		//Log.v("click","ViewGroup3D onClick:" + name +" x:" + x + " y:"+ y);
		if( !touchable || !visible )
			return false;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			//			Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				toLocalCoordinates( child , point );
				if( child.onClick( point.x , point.y ) )
				{
					lastTouchedChild = child;
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		//Log.v("click","ViewGroup3D onDoubleClick:" + name +" x:" + x + " y:"+ y);
		if( !touchable || !visible )
			return false;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			//			Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				toLocalCoordinates( child , point );
				if( child.onDoubleClick( point.x , point.y ) )
				{
					lastTouchedChild = child;
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		Log.v( "click" , "ViewGroup3D onLongClick:" + name + " x:" + x + " y:" + y );
		if( !touchable || !visible )
			return false;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			//			Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				toLocalCoordinates( child , point );
				if( child.onLongClick( point.x , point.y ) )
				{
					lastTouchedChild = child;
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		//Log.v("click","ViewGroup3D fling:" + name +" vx:" + velocityX + " vy:"+ velocityY);
		if( !touchable || !visible )
			return false;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			//			Group.toChildCoordinates(child, x, y, point);
			if( child.fling( velocityX , velocityY ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		//		Log.v("click","ViewGroup3D scroll:" + name +" x:" + x + " y:"+ y);
		if( !touchable || !visible )
			return false;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getChildAt( i );
			if( !( child instanceof View3D ) || !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			//			Group.toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( point.x , point.y ) )
			{
				toLocalCoordinates( child , point );
				if( child.scroll( point.x , point.y , deltaX , deltaY ) )
				{
					lastTouchedChild = child;
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean zoom(
			float originalDistance ,
			float currentDistance )
	{
		// TODO Auto-generated method stub
		Log.v( "click" , "ViewGroup3D zoom:" + name + " x:" + x + " y:" + y );
		if( !touchable || !visible )
			return false;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			if( child.zoom( originalDistance , currentDistance ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		if( !touchable || !visible )
			return false;
		// TODO Auto-generated method stub
		//	Log.v("click","ViewGroup3D multiTouch2:" + name);
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			if( child.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer ) )
			{
				return true;
			}
		}
		return false;
	}
	
	public View3D hit(
			float x ,
			float y )
	{
		int len = children.size() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			Actor child = children.get( i );
			if( !child.visible )
				continue;
			toChildCoordinates( child , x , y , point );
			View3D hit = (View3D)child.hit( point.x , point.y );
			if( hit != null )
			{
				return hit;
			}
		}
		return x >= 0 && x < width && y >= 0 && y < height ? this : null;
	}
	
	/** Called when actors are added to or removed from the group. */
	protected void childrenChanged()
	{
	}
	
	public void calcCoordinate(
			View3D actor )
	{
		Vector2 point1 = new Vector2();
		Vector2 point2 = new Vector2();
		actor.toAbsoluteCoords( point1 );
		toAbsoluteCoords( point2 );
		actor.x = point1.x - point2.x;
		actor.y = point1.y - point2.y;
	}
	
	/** Adds an {@link Actor} to this Group. The order Actors are added is reversed for hit testing.
	 * @param actor the Actor */
	public void addView(
			View3D actor )
	{
		actor.remove();
		children.add( actor );
		if( actor instanceof ViewGroup3D )
			groups.add( (ViewGroup3D)actor );
		if( actor.name != null )
			namesToActors.put( actor.name , actor );
		actor.viewParent = this;
		setStage( actor , stage );
		childrenChanged();
	}
	
	/** Adds an {@link Actor} at the given index in the group. The first Actor added will be at index 0 and so on. Throws an
	 * IndexOutOfBoundsException in case the index is invalid.
	 * @param index the index to add the actor at. */
	public void addViewAt(
			int index ,
			View3D actor )
	{
		actor.remove();
		children.add( index , actor );
		if( actor instanceof ViewGroup3D )
			groups.add( (ViewGroup3D)actor );
		if( actor.name != null )
			namesToActors.put( actor.name , actor );
		actor.viewParent = this;
		setStage( actor , stage );
		childrenChanged();
	}
	
	/** Adds an {@link Actor} before the given Actor.
	 * @param actorBefore the Actor to add the other actor in front of
	 * @param actor the Actor to add */
	public void addViewBefore(
			View3D actorBefore ,
			View3D actor )
	{
		actor.remove();
		int index = children.indexOf( actorBefore );
		children.add( index , actor );
		if( actor instanceof ViewGroup3D )
			groups.add( (ViewGroup3D)actor );
		if( actor.name != null )
			namesToActors.put( actor.name , actor );
		actor.viewParent = this;
		setStage( actor , stage );
		childrenChanged();
	}
	
	/** Adds an {@link Actor} after the given Actor.
	 * @param actorAfter the Actor to add the other Actor behind
	 * @param actor the Actor to add */
	public void addViewAfter(
			View3D actorAfter ,
			View3D actor )
	{
		actor.remove();
		int index = children.indexOf( actorAfter );
		if( index == children.size() )
			children.add( actor );
		else
			children.add( index + 1 , actor );
		if( actor instanceof ViewGroup3D )
			groups.add( (ViewGroup3D)actor );
		if( actor.name != null )
			namesToActors.put( actor.name , actor );
		actor.viewParent = this;
		setStage( actor , stage );
		childrenChanged();
	}
	
	/** Removes an {@link Actor} from this Group.
	 * @param actor */
	public void removeView(
			View3D actor )
	{
		children.remove( actor );
		if( actor instanceof ViewGroup3D )
			groups.remove( (ViewGroup3D)actor );
		if( actor.name != null )
			namesToActors.remove( actor.name );
		if( stage != null )
			stage.unfocus( actor );
		actor.viewParent = null;
		setStage( actor , null );
		childrenChanged();
	}
	
	/** Removes an {@link Actor} from this Group recursively by checking if the Actor is in this group or one of its child-groups.
	 * @param actor the Actor */
	public void removeViewRecursive(
			View3D actor )
	{
		if( children.remove( actor ) )
		{
			if( actor instanceof ViewGroup3D )
				groups.remove( (ViewGroup3D)actor );
			if( actor.name != null )
				namesToActors.remove( actor.name );
			if( stage != null )
				stage.unfocus( actor );
			actor.parent = null;
			setStage( actor , null );
			return;
		}
		for( int i = 0 ; i < groups.size() ; i++ )
		{
			groups.get( i ).removeViewRecursive( actor );
		}
		childrenChanged();
	}
	
	/*
	 * replace view with a new view
	 */
	public void replaceView(
			View3D replacedView ,
			View3D newView )
	{
		int index = replacedView.getIndexInParent();
		removeView( replacedView );
		addViewAt( index , newView );
	}
	
	private void setStage(
			View3D actor ,
			Stage stage )
	{
		//		Log.w("focus", "setStage focus == actor:" + actor + "setStage:" + stage);
		if( !( stage instanceof Desktop3D ) && actor.getStage() instanceof Desktop3D )
		{
			Desktop3D d3d = (Desktop3D)actor.getStage();
			if( d3d != null && d3d.getFocus() != null && d3d.getFocus() == actor )
			{
				//Log.w("focus", "setStage actor:" + actor + "setStage:" + stage + " original focus:" + d3d.getFocus());
				d3d.setFocus( null );
			}
		}
		actor.setStage( stage );
		if( actor instanceof ViewGroup3D )
		{
			List<View3D> children = ( (ViewGroup3D)actor ).getActors();
			for( int i = 0 ; i < children.size() ; i++ )
			{
				setStage( children.get( i ) , stage );
			}
		}
	}
	
	@Override
	public void setColor(
			float r ,
			float g ,
			float b ,
			float a )
	{
		// TODO Auto-generated method stub
		for( View3D view : children )
		{
			view.setColor( r , g , b , a );
		}
	}
	
	/** Finds the {@link Actor} with the given name in this Group and its children.
	 * @param name the name of the Actor
	 * @return the Actor or null */
	public View3D findView(
			String name )
	{
		View3D actor = namesToActors.get( name );
		if( actor == null )
		{
			int len = groups.size();
			for( int i = 0 ; i < len ; i++ )
			{
				actor = groups.get( i ).findView( name );
				if( actor != null )
					return actor;
			}
		}
		return actor;
	}
	
	/** Swap two actors' sort order by index. 0 is lowest while getActors().size() - 1 is largest.
	 * @param first first Actor index
	 * @param second second Actor index
	 * @return false if indices are out of bound. */
	public boolean swapView3D(
			int first ,
			int second )
	{
		int maxIndex = children.size();
		if( first < 0 || first >= maxIndex )
			return false;
		if( second < 0 || second >= maxIndex )
			return false;
		Collections.swap( children , first , second );
		return true;
	}
	
	/** Swap two actors' sort order by reference.
	 * @param first first Actor
	 * @param second second Actor
	 * @return false if any of the Actors is not the child of this Group. */
	public boolean swapView(
			View3D first ,
			View3D second )
	{
		int firstIndex = children.indexOf( first );
		int secondIndex = children.indexOf( second );
		if( firstIndex == -1 || secondIndex == -1 )
			return false;
		Collections.swap( children , firstIndex , secondIndex );
		return true;
	}
	
	/** @return all child {@link Actor}s as an ordered list. */
	public List<View3D> getActors()
	{
		return immutableChildren;
	}
	
	/** @return all child {@link Group}s as an unordered list. */
	public List<ViewGroup3D> getGroups()
	{
		return immutableGroups;
	}
	
	/** Clears this Group, removing all contained {@link Actor}s. */
	public void clear()
	{
		for( int i = 0 ; i < children.size() ; i++ )
		{
			View3D view = children.get( i );
			setStage( view , null );
			view.viewParent = null;
		}
		children.clear();
		groups.clear();
		namesToActors.clear();
		childrenChanged();
	}
	
	/** Sorts the children via the given {@link Comparator}.
	 * @param comparator the comparator. */
	public void sortChildren(
			Comparator<Actor> comparator )
	{
		Collections.sort( children , comparator );
	}
	
	/** Converts coordinates for this group to those of a descendant actor.
	 * @throws IllegalArgumentException if the specified actor is not a descendant of this group. */
	public boolean toLocalCoordinates(
			View3D descendant ,
			Vector2 point )
	{
		if( descendant.viewParent == null )
			return false;
		// First convert to the actor's parent coordinates.
		if( descendant.viewParent != this )
			toLocalCoordinates( descendant.viewParent , point );
		Group.toChildCoordinates( descendant , point.x , point.y , point );
		return true;
	}
	
	public boolean isDescendant(
			Actor actor )
	{
		while( true )
		{
			if( actor == null )
				return false;
			if( actor == this )
				return true;
			actor = actor.parent;
		}
	}
	
	/** Transforms the coordinates given in the child's parent coordinate system to the child {@link Actor}'s coordinate system.
	 * @param child the child Actor
	 * @param x the x-coordinate in the Group's coordinate system
	 * @param y the y-coordinate in the Group's coordinate system
	 * @param out the output {@link Vector2} */
	static public void toChildCoordinates(
			Actor child ,
			float x ,
			float y ,
			Vector2 out )
	{
		if( child.rotation == 0 )
		{
			if( child.scaleX == 1 && child.scaleY == 1 )
			{
				out.x = x - child.x;
				out.y = y - child.y;
			}
			else
			{
				if( child.originX == 0 && child.originY == 0 )
				{
					out.x = ( x - child.x ) / child.scaleX;
					out.y = ( y - child.y ) / child.scaleY;
				}
				else
				{
					out.x = ( x - child.x - child.originX ) / child.scaleX + child.originX;
					out.y = ( y - child.y - child.originY ) / child.scaleY + child.originY;
				}
			}
		}
		else
		{
			final float cos = (float)Math.cos( child.rotation * MathUtils.degreesToRadians );
			final float sin = (float)Math.sin( child.rotation * MathUtils.degreesToRadians );
			if( child.scaleX == 1 && child.scaleY == 1 )
			{
				if( child.originX == 0 && child.originY == 0 )
				{
					float tox = x - child.x;
					float toy = y - child.y;
					out.x = tox * cos + toy * sin;
					out.y = tox * -sin + toy * cos;
				}
				else
				{
					final float worldOriginX = child.x + child.originX;
					final float worldOriginY = child.y + child.originY;
					float fx = -child.originX;
					float fy = -child.originY;
					float x1 = cos * fx - sin * fy;
					float y1 = sin * fx + cos * fy;
					x1 += worldOriginX;
					y1 += worldOriginY;
					float tox = x - x1;
					float toy = y - y1;
					out.x = tox * cos + toy * sin;
					out.y = tox * -sin + toy * cos;
				}
			}
			else
			{
				if( child.originX == 0 && child.originY == 0 )
				{
					float tox = x - child.x;
					float toy = y - child.y;
					out.x = tox * cos + toy * sin;
					out.y = tox * -sin + toy * cos;
					out.x /= child.scaleX;
					out.y /= child.scaleY;
				}
				else
				{
					float srefX = child.originX * child.scaleX;
					float srefY = child.originY * child.scaleY;
					final float worldOriginX = child.x + child.originX;
					final float worldOriginY = child.y + child.originY;
					float fx = -srefX;
					float fy = -srefY;
					float x1 = cos * fx - sin * fy;
					float y1 = sin * fx + cos * fy;
					x1 += worldOriginX;
					y1 += worldOriginY;
					float tox = x - x1;
					float toy = y - y1;
					out.x = tox * cos + toy * sin;
					out.y = tox * -sin + toy * cos;
					out.x /= child.scaleX;
					out.y /= child.scaleY;
				}
			}
		}
	}
	
	public void removeAllViews()
	{
		clear();
	}
	
	public boolean moveViewTo(
			View3D view ,
			int i )
	{
		if( view != null && this.findView( view.name ) != null )
		{
			this.removeView( view );
			this.addViewAt( i , view );
			return true;
		}
		return false;
	}
	
	public boolean moveViewToLast(
			View3D view )
	{
		return moveViewTo( view , this.getChildCount() - 1 );
	}
	
	public int getChildCount()
	{
		return this.children.size();
	}
	
	public View3D getChildAt(
			int i )
	{
		return this.children.get( i );
	}
	
	public ArrayList getAllViews()
	{
		return (ArrayList)this.children;
	}
	
	public View3D getView(
			String name )
	{
		for( int i = 0 ; i < children.size() ; i++ )
		{
			if( children.get( i ).name.equals( name ) )
				return children.get( i );
		}
		return null;
	}
	
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( viewParent != null )
		{
			return viewParent.onCtrlEvent( sender , event_id );
		}
		return false;
	}
	
	static public void enableDebugging(
			String debugTextureFile )
	{
		debugTexture = new Texture( Gdx.files.internal( debugTextureFile ) , false );
		debug = true;
	}
	
	static public void disableDebugging()
	{
		if( debugTexture != null )
			debugTexture.dispose();
		debug = false;
	}
	
	//teapotXu_20130325 add start: for roll effect
	public void setChildrenDrawOrder(
			boolean is_drawn_in_reverse_order )
	{
		children_drawn_in_reverse_order = is_drawn_in_reverse_order;
	}
	
	public boolean getChildrenDrawOrder()
	{
		return children_drawn_in_reverse_order;
	}
	
	//teapotXu_20130325 add end
	/**
	 * Dispose self and all children.
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		// dispose all children
		for( int i = 0 ; i < this.children.size() ; i++ )
		{
			this.children.get( i ).dispose();
		}
	}
}
