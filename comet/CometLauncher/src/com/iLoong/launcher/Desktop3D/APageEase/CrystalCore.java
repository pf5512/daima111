package com.iLoong.launcher.Desktop3D.APageEase;


import java.util.HashMap;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class CrystalCore extends ViewGroupOBJ3D
{
	
	private String TAG = "Crystal";
	private String[] s = { "D" , "L" , "R" , "U" , "B" , "F" };
	private String[] sLeft = { "L" , "U" , "D" , "F" , "B" , "R" };
	private String[] sRight = { "R" , "U" , "D" , "F" , "B" , "L" };
	private float faceScaleX = 1f , faceScaleY = 1f;
	private boolean dispose = false;
	
	public enum Face
	{
		Front , Verso
	}
	
	// 当前翻转的角�?
	private float mLastRotationY = 0;
	private HashMap<String , Object3DBase> faceList = new HashMap<String , Object3DBase>();
	private float UVBeginOffset = 0.25f;
	private float UVEndOffset = 0.75f;
	private float totalUVOffset = 0.25f;
	private float currentUVDeltaOffset = 0f;
	private float xVScale = 0;
	private float yUScale = 0;
	private float scaleTo = 0.7f;
	private Timeline startTimeline;
	private Timeline stopTimeline;
	private Color c = new Color();
	static public boolean rotating = false;
	private float stopRate = 0.8f;
	private View3D celllayoutF;
	private View3D celllayoutB;
	private boolean faceInited;
	
	public CrystalCore(
			String name ,
			float width ,
			float height )
	{
		super( name );
		dispose = false;
		Object3DBase Object3D = null;
		Mesh mesh = null;
		Texture tFB = new BitmapTexture( Tools.getImageFromInStream( objHandle( "launcher/crystal3/test_1.png" ).read() ) , true );
		Texture tDLRU = new BitmapTexture( Tools.getImageFromInStream( objHandle( "launcher/crystal3/test_2.png" ).read() ) , true );
		for( String ss : s )
		{
			Object3D = new Object3DBase( ss );
			mesh = ObjLoader.loadObj( objHandle( "launcher/crystal3/" + ss + ".obj" ).read() , true );
			//			Object3D.moveObj(mesh, Utils3D.getScreenWidth() / 2,
			//					Utils3D.getScreenHeight() / 2, 0);
			Object3D.setMesh( mesh );
			if( "DLRU".contains( ss ) )
			{
				Object3D.setTexture( tDLRU );
				this.addView( Object3D );
			}
			else
			{
				Object3D.setTexture( tFB );
				if( ss.equals( "B" ) )
				{
					flipH( mesh );
					ViewGroupOBJ3D vg = new ViewGroupOBJ3D( ss );
					vg.addView( Object3D );
					this.addView( vg );
				}
				if( ss.equals( "F" ) )
				{
					ViewGroupOBJ3D vg = new ViewGroupOBJ3D( ss );
					vg.addView( Object3D );
					this.addView( vg );
				}
			}
			faceList.put( ss , Object3D );
			//			Object3D.region.setRegion(0, UVBeginOffset, 1f, UVEndOffset);
			// scrollRegion(mesh, Object3D.region, 0, 0.25f);
		}
		Object3DBase front = faceList.get( "F" );
		Mesh fMesh = front.getMesh();
		BoundingBox bb = fMesh.calculateBoundingBox();
		float bbWidth = bb.getMax().x - bb.getMin().x;
		float bbHeight = bb.getMax().y - bb.getMin().y;
		float scaleX = width / bbWidth;
		float scaleY = height / bbHeight;
		this.scale( scaleX , scaleY , 1f );
		move( width / 2 , height / 2 );
		setAlpha( 0f );
	}
	
	public View3D getFace(
			Face f )
	{
		ViewGroup3D viewParent = null;
		View3D vg = null;
		if( f == Face.Front )
		{
			vg = this.findView( "F" );
		}
		else if( f == Face.Verso )
		{
			vg = this.findView( "B" );
		}
		if( vg instanceof ViewGroup3D )
		{
			viewParent = (ViewGroup3D)vg;
			View3D v = viewParent.getChildAt( 0 );
			if( v instanceof Object3DBase )
				return null;
			return v;
		}
		else
			return null;
	}
	
	//	public View3D removeFace(Face f){
	//
	//		View3D v = this.getFace(f);
	//		ViewGroup3D vg = v.getParent();
	//		if(vg != null){
	//			v.remove();
	//			Object obj = vg.getTag();
	//			if(obj instanceof ViewGroup3D){
	//				((ViewGroup3D)obj).addView(v);
	//			}
	//		}
	//		
	//		return v;
	//		
	//	}
	public void setFace(
			Face f ,
			View3D view )
	{
		Object3DBase obj = null;
		ViewGroup3D viewParent = null;
		if( f == Face.Front )
		{
			obj = faceList.get( "F" );
			View3D vg = this.findView( "F" );
			if( vg instanceof ViewGroup3D )
			{
				viewParent = (ViewGroup3D)vg;
			}
		}
		else if( f == Face.Verso )
		{
			obj = faceList.get( "B" );
			View3D vg = this.findView( "B" );
			if( vg instanceof ViewGroup3D )
			{
				viewParent = (ViewGroup3D)vg;
			}
		}
		else
		{
			//error
			return;
		}
		if( getFace( f ) == view )
			return;
		faceScaleX = view.getWidth() / this.getWidth();
		faceScaleY = view.getHeight() / this.getHeight();
		view.setScale( 1f / faceScaleX , 1f / faceScaleY );
		if( !faceInited )
		{
			//			this.setPosition(view.x, view.y);
			//			faceScaleX = view.getWidth()/this.getWidth();
			//			faceScaleY = view.getHeight()/this.getHeight();
			syncView( view );
			faceInited = true;
			Log.v( "xp" , "set Scale:" + this.getScaleX() + "," + this.getScaleY() );
		}
		syncFace( view , obj.getMesh() );
		if( f == Face.Verso )
		{
			view.setRotationY( 180 );
		}
		else
		{
			view.setRotationY( 0 );
		}
		if( viewParent != null )
		{
			viewParent.addViewAt( 0 , view );
		}
	}
	
	public void syncView(
			View3D view )
	{
		float faceScaleX = view.getWidth() / this.getWidth();
		float faceScaleY = view.getHeight() / this.getHeight();
		this.setScale( faceScaleX , faceScaleY );
		view.toAbsoluteCoords( point );
		this.setPosition( point.x , point.y + R3D.Workspace_celllayout_bottompadding );
		this.setOrigin( point.x + view.getWidth() / 2 - this.x , point.y + view.getHeight() / 2 - this.y );
	}
	
	private void syncFaces()
	{
		Object3DBase obj = faceList.get( "B" );
		View3D view = this.getFace( Face.Verso );
		if( view != null )
		{
			syncFace( view , obj.getMesh() );
		}
		obj = faceList.get( "F" );
		view = this.getFace( Face.Front );
		if( view != null )
		{
			syncFace( view , obj.getMesh() );
		}
	}
	
	private void syncFace(
			View3D view ,
			Mesh mesh )
	{
		BoundingBox bb = mesh.calculateBoundingBox();
		float width = bb.getMax().x - bb.getMin().x;
		float height = bb.getMax().y - bb.getMin().y;
		view.setPosition(
				bb.getMin().x + ( width - view.getWidth() * view.scaleX ) / 2 ,
				bb.getMin().y + ( height - view.getHeight() * view.scaleY - R3D.Workspace_celllayout_bottompadding * view.scaleY ) / 2 );
		view.setOrigin( view.getWidth() / 2 , view.getHeight() / 2 );
		view.setOriginZ( 0 );
		view.setZ( bb.getCenter().z );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		celllayoutB = null;
		celllayoutF = null;
		mLastRotationY = this.rotation;
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		mLastRotationY = this.rotation;
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		if( celllayoutF != null )
		{
			celllayoutF.setZ( celllayoutF.getUser() );
		}
		if( celllayoutB != null )
		{
			celllayoutB.setZ( celllayoutB.getUser() );
		}
		super.draw( batch , parentAlpha );
	}
	
	public void addPage(
			View3D page )
	{
		this.addView( page );
		page.x = ( this.width - page.width ) / 2;
		page.y = ( this.height - page.height ) / 2;
	}
	
	public void setRotate(
			float rotateY )
	{
		View3D face = null;
		if( Math.abs( rotateY ) < 90 )
		{
			face = this.findView( "B" );
			face.hide();
			face = this.findView( "F" );
			face.show();
		}
		else
		{
			face = this.findView( "B" );
			face.show();
			face = this.findView( "F" );
			face.hide();
		}
		if( rotateY <= 0 && rotateY >= -180 )
		{
			for( String s : sLeft )
			{
				face = this.findView( s );
				face.bringToFront();
			}
		}
		else
		{
			for( String s : sRight )
			{
				face = this.findView( s );
				face.bringToFront();
			}
		}
		shiftTexture( rotateY );
		super.setRotationY( rotateY );
	}
	
	private void shiftTexture(
			float rotate )
	{
		Object3DBase obj = null;
		boolean flipX = false , flipY = false;
		for( String name : s )
		{
			//			if(view instanceof ViewGroup3D){
			//				ViewGroup3D vg = (ViewGroup3D) view;
			//				for(int i = 0; i < vg.getChildCount(); i++ ){
			//					View obj = vg.getChildAt(i);
			//				}
			//			}
			obj = faceList.get( name );
			xVScale = 0;
			float abs = Math.abs( rotate );
			if( name.equals( "F" ) || name.equals( "B" ) )
			{
				xVScale = ( abs <= 90 ? abs : abs - 90 ) / 90;
				totalUVOffset = UVBeginOffset = 0.25f;
			}
			else
			{
				xVScale = abs / 180;
				totalUVOffset = UVBeginOffset = 0.375f;
			}
			if( rotate > 0 )
			{
				xVScale = -xVScale;
			}
			scrollRegion( obj.getMesh() , obj.region , 0 , totalUVOffset * xVScale , flipX , flipY );
		}
	}
	
	public void scrollRegion(
			Mesh mesh ,
			TextureRegion region ,
			float xAmount ,
			float yAmount ,
			boolean flipX ,
			boolean flipY )
	{
		float u = region.getU();
		float v = region.getV();
		float u2 = region.getU2();
		float v2 = region.getV2();
		float dv = 0;
		float du = 0;
		Texture texture = region.getTexture();
		if( xAmount != 0 )
		{
			float width = ( u2 - u ) * texture.getWidth();
			du = xAmount % 1 - u;
			u = ( xAmount ) % 1;
			u2 = u + width / texture.getWidth();
		}
		if( yAmount != 0 )
		{
			float height = ( v2 - v ) * texture.getHeight();
			dv = yAmount % 1 - v;
			v = ( yAmount ) % 1;
			v2 = v + height / texture.getHeight();
		}
		region.setRegion( u , v , u2 , v2 );
		//		Log.v(TAG,"du,dv:" + du + "," + dv);
		//		setTextureRegion(mesh, region, flipX, flipY);
		setTextureRegion( mesh , du , dv );
	}
	
	private void setTextureRegion(
			Mesh mesh ,
			float du ,
			float dv )
	{
		// TODO Auto-generated method stub
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.TextureCoordinates );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		for( int i = 0 ; i < numVertices ; i++ )
		{
			vertices[idx] += du;
			vertices[idx + 1] += dv;
			idx += vertexSize;
		}
		mesh.setVertices( vertices );
	}
	
	public static void setTextureRegion(
			com.badlogic.gdx.graphics.Mesh mesh ,
			TextureRegion region ,
			boolean flipX ,
			boolean flipY )
	{
		float u = region.getU();
		float v = region.getV();
		float u1 = region.getU2();
		float v1 = region.getV2();
		if( flipX )
		{
			u = region.getU2();
			u1 = region.getU();
		}
		if( flipY )
		{
			v = region.getV2();
			v1 = region.getV();
		}
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.TextureCoordinates );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		vertices[idx] = u1;
		vertices[idx + 1] = v;
		idx += vertexSize;
		vertices[idx] = u;
		vertices[idx + 1] = v;
		idx += vertexSize;
		vertices[idx] = u1;
		vertices[idx + 1] = v1;
		idx += vertexSize;
		vertices[idx] = u;
		vertices[idx + 1] = v;
		idx += vertexSize;
		vertices[idx] = u;
		vertices[idx + 1] = v1;
		idx += vertexSize;
		vertices[idx] = u1;
		vertices[idx + 1] = v1;
		idx += vertexSize;
		mesh.setVertices( vertices );
	}
	
	public FileHandle objHandle(
			String name )
	{
		// FileHandle handle = Gdx.files.external(name);
		// if(handle.exists()){
		// return handle;
		// }
		return Gdx.files.internal( name );
	}
	
	private void flipH(
			Mesh mesh )
	{
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.TextureCoordinates );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		for( int i = 0 ; i < numVertices ; i++ )
		{
			vertices[idx] = 1 - vertices[idx];
			//			vertices[idx + 1] += dv;
			idx += vertexSize;
		}
		mesh.setVertices( vertices );
	}
	
	public void move(
			float dx ,
			float dy )
	{
		for( String ss : s )
		{
			Object3DBase obj = faceList.get( ss );
			Mesh mesh = obj.getMesh();
			obj.moveObj( mesh , dx , dy , 0 );
		}
	}
	
	public void scale(
			float x ,
			float y ,
			float z )
	{
		for( String ss : s )
		{
			Object3DBase obj = faceList.get( ss );
			Mesh mesh = obj.getMesh();
			mesh.scale( x , y , z );
		}
	}
	
	public void setAlpha(
			float alpha )
	{
		for( String ss : s )
		{
			Object3DBase obj = faceList.get( ss );
			obj.color.a = alpha;
		}
		c.a = alpha;
	}
	
	@Override
	public Color getColor()
	{
		// TODO Auto-generated method stub
		return c;
	}
	
	@Override
	public void setColor(
			Color color )
	{
		// TODO Auto-generated method stub
		setAlpha( color.a );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		Log.v( "xp" , "crystal core onEvent" );
		if( source == startTimeline && type == TweenCallback.COMPLETE )
		{
			startTimeline = null;
			return;
		}
		if( source == stopTimeline && type == TweenCallback.COMPLETE )
		{
			stopTimeline = null;
			rotating = false;
			celllayoutB = null;
			celllayoutF = null;
			faceInited = false;
			return;
		}
		super.onEvent( type , source );
	}
	
	public void start()
	{
		Log.v( "xp" , "crystal core start before" );
		if( stopTimeline != null )
		{
			stopTimeline.free();
			stopTimeline.kill();
			stopTimeline = null;
			rotating = false;
		}
		if( !rotating )
		{
			celllayoutB = null;
			celllayoutF = null;
			startTimeline = Timeline.createParallel();
			startTimeline.push( Tween.to( this , View3DTweenAccessor.SCALE_XY , 0.2f ).ease( Cubic.OUT ).target( scaleTo , scaleTo ) );
			startTimeline.push( Tween.to( this , View3DTweenAccessor.OPACITY , 0.15f ).ease( Cubic.IN ).target( 1 ) );
			startTimeline.start( View3DTweenAccessor.manager ).setCallback( this );
			rotating = true;
			Log.v( "xp" , "crystal core start" );
		}
	}
	
	public void start(
			float delay )
	{
		Log.v( "xp" , "crystal core start before" );
		if( stopTimeline != null )
		{
			stopTimeline.free();
			stopTimeline.kill();
			stopTimeline = null;
			rotating = false;
		}
		if( !rotating )
		{
			celllayoutB = null;
			celllayoutF = null;
			startTimeline = Timeline.createParallel();
			startTimeline.push( Tween.to( this , View3DTweenAccessor.SCALE_XY , 0.2f ).ease( Cubic.OUT ).target( scaleTo , scaleTo ) );
			startTimeline.push( Tween.to( this , View3DTweenAccessor.OPACITY , 0.15f ).ease( Cubic.IN ).target( 1 ) );
			startTimeline.start( View3DTweenAccessor.manager ).setCallback( this );
			rotating = true;
			Log.v( "xp" , "crystal core start" );
		}
	}
	
	public void stop(
			float duration ,
			TweenEquation easeEquation )
	{
		if( rotating )
		{
			if( stopTimeline == null )
			{
				stopTimeline = Timeline.createParallel();
				stopTimeline.push( Tween.to( this , View3DTweenAccessor.SCALE_XY , duration ).ease( easeEquation ).target( faceScaleX , faceScaleY ) );
				stopTimeline.push( Tween.to( this , View3DTweenAccessor.OPACITY , duration ).ease( easeEquation ).target( 0 ).delay( 0.2f ) );
				stopTimeline.start( View3DTweenAccessor.manager ).setCallback( this );
				ViewGroupOBJ3D Fvg = (ViewGroupOBJ3D)this.findView( "F" );
				if( Fvg != null )
				{
					for( int i = 0 ; i < Fvg.getChildCount() ; i++ )
					{
						if( Fvg.getChildAt( i ) instanceof CellLayout3D )
						{
							celllayoutF = (CellLayout3D)Fvg.getChildAt( i );
							celllayoutF.setUser( celllayoutF.getZ() );
							celllayoutF.startTween( View3DTweenAccessor.USER , easeEquation , duration , 0 , 0 , 0 );
						}
					}
				}
				ViewGroupOBJ3D Bvg = (ViewGroupOBJ3D)this.findView( "B" );
				if( Bvg != null )
				{
					for( int i = 0 ; i < Bvg.getChildCount() ; i++ )
					{
						if( Bvg.getChildAt( i ) instanceof CellLayout3D )
						{
							celllayoutB = (CellLayout3D)Bvg.getChildAt( i );
							celllayoutB.setUser( celllayoutB.getZ() );
							celllayoutB.startTween( View3DTweenAccessor.USER , easeEquation , duration , 0 , 0 , 0 );
						}
					}
				}
			}
			//		float rate = (degree-stopRate)/(1-stopRate);
			//			this.setScale(this.scaleX+(1-this.scaleX)*rate, this.scaleY+(1-this.scaleY)*rate);
			//			this.setAlpha(this.c.a - this.c.a * rate);
			//			if(degree == 1){
			//				rotating = false;
			//				removeFace(Face.Front);
			//				removeFace(Face.Verso);
			//			}
			//			rotating = false;
			//			this.setScale(1f, 1f);
			//			this.setAlpha(0f);
			Log.v( "xp" , "crystal core stop:" + faceScaleX + "," + faceScaleY );
			//			removeFace(Face.Front);
			//			removeFace(Face.Verso);
		}
	}
	
	public void dispose()
	{
		if( !dispose )
		{
			dispose = true;
			for( String ss : s )
			{
				Object3DBase obj = faceList.get( ss );
				Mesh mesh = obj.getMesh();
				mesh.dispose();
				TextureRegion tr = obj.region;
				if( tr != null )
				{
					tr.getTexture().dispose();
				}
			}
		}
	}
}

class CrystalFace extends View3D
{
	
	public CrystalFace(
			String name ,
			Texture texture )
	{
		super( name , texture );
		// TODO Auto-generated constructor stub
	}
	
	public CrystalFace(
			String string )
	{
		super( string );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean is3dRotation()
	{
		// TODO Auto-generated method stub
		return true;
	}
}
