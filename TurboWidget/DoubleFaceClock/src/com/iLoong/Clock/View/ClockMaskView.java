package com.iLoong.Clock.View;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.Clock.Common.ClockHelper;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ClockMaskView extends Object3DBase
{
	
	public static final String CLOCK_MASK_OBJ = "mask.obj";
	private Cache<String , Mesh> mMeshCache = null;
	public static Map<String , Texture> mTextures;
	private Context mContext;
	private MainAppContext mAppContext;
	private CooGdx cooGdx;
	
	public ClockMaskView(
			String name ,
			MainAppContext appContext ,
			CooGdx cooGdx )
	{
		super( appContext , name );
		this.mContext = appContext.mWidgetContext;
		this.mAppContext = appContext;
		this.cooGdx = cooGdx;
		this.x = 0;
		this.y = 0;
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin( width / 2 , height / 2 );
		initResource();
		this.region.setRegion( mTextures.get( CLOCK_MASK_OBJ ) );
	}
	
	public void initResource()
	{
		String path = ClockComponent.imgPath;
		mTextures = new HashMap<String , Texture>();
		try
		{
			BitmapTexture texture = new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + "mask.png" ) ) );
			texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			mTextures.put( CLOCK_MASK_OBJ , texture );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public void setMeshCache(
			Cache<String , Mesh> cache )
	{
		this.mMeshCache = cache;
	}
	
	public static void Move(
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
	}
	
	public void renderMesh(
			float dx ,
			float dy )
	{
		Mesh mesh = getMesh();
		setMesh( mesh );
		enableDepthMode( true );
	}
	
	public Mesh getMesh()
	{
		Mesh mesh = ClockHelper.getMesh( mMeshCache , mAppContext , CLOCK_MASK_OBJ , width / 2 , height / 2 , 0 , WidgetClock.SCALE_X , WidgetClock.SCALE_Y , WidgetClock.SCALE_Z );
		return mesh;
	}
	
	//    public void draw(SpriteBatch batch, float parentAlpha) {
	////        Vector2 tPoint =new Vector2();
	////        tPoint.x=x;
	////        tPoint.y=y;
	////        this.toAbsolute(tPoint);
	////        Log.v("clock", "pointx "+tPoint.x+" ,pionty "+tPoint.y);
	////        Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
	////    
	////        Gdx.gl.glScissor(Math.round(tPoint.x), Math.round(tPoint.y),
	////               Math.round( tPoint.x+this.width),
	////               Math.round( tPoint.y+this.height));
	////        
	////        Gdx.gl.glScissor(Math.round(tPoint.x+this.width/2),  Math.round(tPoint.y),
	////                Math.round( tPoint.x+this.width),
	////                Math.round( tPoint.y+this.height));
	//        super.draw(batch, parentAlpha);
	//
	//  //     Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
	//
	//    }
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
	}
}
