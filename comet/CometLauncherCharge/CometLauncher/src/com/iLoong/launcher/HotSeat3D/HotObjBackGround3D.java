package com.iLoong.launcher.HotSeat3D;


import java.io.InputStream;
import android.content.Context;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class HotObjBackGround3D extends Object3DBase
{
	
	public HotObjBackGround3D(
			String name )
	{
		super( name );
	}
	
	public static final int MSG_HOTOBJ_OPEN = 1;
	private Context mContext = null;
	private float offset_y = 0;
	private static float SCALE_X = 1F;
	private static float SCALE_Y = 1F;
	private String backGroundName;
	private Tween mAutoScrollTween = null;
	private Timeline animation_line = null;
	private boolean animalStart = false;
	public static float rotationY;
	public static float rotationZ;
	public static double rotationAngle = 0;
	public int index = 0;
	
	public HotObjBackGround3D(
			String name ,
			Context context ,
			String objName ,
			float width ,
			float height ,
			float parent_pos_y )
	{
		super( name );
		this.mContext = context;
		backGroundName = name;
		InputStream stream = null;
		this.width = width;
		this.height = height;
		this.setOrigin( width / 2 , height / 2 );
		this.index = index;
		SCALE_X = Utils3D.getScreenWidth() / 480f;
		SCALE_Y = SCALE_X * 0.85f;
		parent_pos_y = SCALE_X * parent_pos_y;
		setParentPosY( parent_pos_y );
		float SCALE_W = SCALE_X * (float)( 1.11 );
		// SCALE_Y= Utils3D.getScreenHeight()/800f;
		// SCALE_X= Utils3D.getDensity()/1.5f;
		try
		{
			//			if (this.region == null) {
			this.region.setRegion( loadTexture( mContext , "theme/dock3dbar/dockbarUV2.png" ) );
			//			}
			stream = ThemeManager.getInstance().getInputStream( objName );
			Mesh mesh = ObjLoader.loadObj( stream , true );
			mesh.scale( SCALE_W , SCALE_Y , SCALE_X );
			move( mesh , this.width / 2 + 1 * SCALE_X , this.height / 2 , 0 );
			this.setMesh( mesh );
			enableDepthMode( true );
			stream.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		Log.v( "Hosteat" , "back offset_y:" + offset_y + "w: " + this.width + " h:" + this.height + " y:" + this.y + "x:" + this.x );
	}
	
	public void setParentPosY(
			float pos_y )
	{
		offset_y = pos_y;
	}
	
	public boolean is3dRotation()
	{
		return true;
	}
	
	private TextureRegion loadTexture(
			Context context ,
			String imageFile )
	{
		// Texture texture = new Texture(
		// new AndroidFiles(context.getAssets()).internal(imageFile));
		Texture texture = new BitmapTexture( ThemeManager.getInstance().getBitmap( imageFile ) );
		texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		TextureRegion region = new TextureRegion( texture );
		return region;
	}
	
	public void move(
			Mesh mesh ,
			float dx ,
			float dy ,
			float dz )
	{
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.Position );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		for( int i = 0 ; i < numVertices ; i++ )
		{
			vertices[idx] += dx;
			vertices[idx + 1] += dy;
			vertices[idx + 2] += dz;
			idx += vertexSize;
		}
		mesh.setVertices( vertices );
		//		texture_pos_x = vertices[0];
		//		texture_pos_y = vertices[1];
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		Gdx.gl.glEnable( GL10.GL_CULL_FACE );
		if( HotObjMenuFront.animalStart )
		{
			Gdx.gl.glCullFace( GL10.GL_BACK );
		}
		else
		{
			if( !HotSeat3D.isOnBackSide )
				Gdx.gl.glCullFace( GL10.GL_FRONT );
			else
				Gdx.gl.glCullFace( GL10.GL_BACK );
		}
		super.draw( batch , parentAlpha );
		Gdx.gl.glDisable( GL10.GL_CULL_FACE );
	}
}
