package com.iLoong.launcher.HotSeat3D;


import java.io.InputStream;

import android.content.Context;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;
import com.iLoong.launcher.theme.ThemeManager;


public class HotObjBackGround3D extends Object3DBase
{
	
	public HotObjBackGround3D(
			String name )
	{
		super( name );
	}
	
	private Context mContext = null;
	private float offset_y = 0;
	private float texture_pos_x;
	private float texture_pos_y;
	private static float SCALE_X = 1F;
	
	public HotObjBackGround3D(
			String name ,
			Context context ,
			float width ,
			float height ,
			float parent_pos_y )
	{
		super( name );
		this.mContext = context;
		InputStream stream = null;
		this.width = width;
		this.height = height;
		this.setOrigin( width / 2 , height / 2 );
		setParentPosY( parent_pos_y );
		SCALE_X = Utils3D.getScreenWidth() / 480f;
		// SCALE_Y= Utils3D.getScreenHeight()/800f;
		// SCALE_X= Utils3D.getDensity()/1.5f;
		try
		{
			this.region.setRegion( loadTexture( mContext , "theme/dock3dbar/3ddockUV.png" ) );
			stream = ThemeManager.getInstance().getInputStream( "launcher/dock3dobj/dock.obj" );
			Mesh mesh = ObjLoader.loadObj( stream , true );
			if( SCALE_X != 1 )
			{
				mesh.scale( SCALE_X , SCALE_X , SCALE_X );
			}
			move( mesh , this.width / 2 , offset_y , 0 );
			this.setMesh( mesh );
			// this.setOriginZ(-24);
			enableDepthMode( true );
			stream.close();
			// setBackgroud(new NinePatch(R3D.getTextureRegion("menu-bg"), 2, 2,
			// 0, 0));
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public void onThemeChanged()
	{
		( (BitmapTexture)this.region.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/dock3dbar/3ddockUV.png" ) , true );
	}
	
	public void setParentPosY(
			float pos_y )
	{
		offset_y = pos_y;
	}
	
	private TextureRegion loadTexture(
			Context context ,
			String imageFile )
	{
		// Texture texture = new Texture(
		// new AndroidFiles(context.getAssets()).internal(imageFile));
		Texture texture = new BitmapTexture( ThemeManager.getInstance().getBitmap( imageFile ) , true );
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
		texture_pos_x = vertices[0];
		texture_pos_y = vertices[1];
	}
}
