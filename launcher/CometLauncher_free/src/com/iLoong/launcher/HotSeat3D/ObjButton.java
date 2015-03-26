package com.iLoong.launcher.HotSeat3D;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.HotSeat3D.HotObjMenuFront.Menu3DAction;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;
import com.iLoong.launcher.theme.ThemeManager;


public class ObjButton extends Object3DBase
{
	
	float scaleX = 1;
	float scaleY = 1;
	float scaleZ = 1;
	private Texture norImg;
	private Texture pressImg;
	private String objDir = "launcher/dock3dobj/";
	private String imgDir = "theme/dock3dbar/";
	private Context mContext;
	private Menu3DAction action;
	private boolean hideFace = false;
	public static boolean hasTouchDown;
	
	public ObjButton(
			String name ,
			Context context ,
			float width ,
			float height ,
			String norImg ,
			String pressImg ,
			boolean hideFace )
	{
		super( name );
		this.width = width;
		this.height = height;
		this.hideFace = hideFace;
		this.mContext = context;
		this.setOrigin( this.width / 2 , this.height / 2 );
		this.norImg = getTexture( norImg );
		this.pressImg = getTexture( pressImg );
		Log.v( "Hotseat" , "width:  " + width + " height:  " + height + " x: " + x + "y :" + y );
	}
	
	public void setScale(
			float scaleX ,
			float scaleY ,
			float scaleZ )
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
	}
	
	public void setAction(
			Menu3DAction action )
	{
		this.action = action;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Log.v( "Hotseat" , "onClick" );
		if( action != null )
			HotObjMenuFront.getInstance().addBackGroundGroup( this.action );
		//action.onAction();
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "Hotseat" , "onTouchDown" );
		hasTouchDown = true;
		requestDark();
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "Hotseat" , "onTouchUp" );
		if( DockbarObjcetGroup.isStart )
		{
			releaseDark();
			return false;
		}
		if( hasTouchDown )
		{
			if( y > 10 && x > 10 && x < 113 )
			{
				HotObjMenuFront.getInstance().addBackGroundGroup( this.action );
			}
			releaseDark();
			return true;
		}
		releaseDark();
		return false;
	}
	
	public void requestDark()
	{
		this.setTexture( this.pressImg );
	}
	
	@Override
	public void releaseDark()
	{
		// TODO Auto-generated method stub
		hasTouchDown = false;
		this.setTexture( this.norImg );
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
	
	public Texture getTexture(
			String imgName )
	{
		String path = imgDir + imgName;
		Texture texture = new BitmapTexture( titleToBitmap( ThemeManager.getInstance().getBitmap( path ) ) );
		texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		return texture;
	}
	
	private Bitmap titleToBitmap(
			Bitmap img )
	{
		Bitmap bitmap = Bitmap.createBitmap( 128 , 128 , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bitmap );
		if( img != null && !img.isRecycled() )
		{
			canvas.drawBitmap( img , 0 , 0 , null );
			img.recycle();
		}
		Paint paint = new Paint();
		paint.setColor( Color.parseColor( "#79d0ff" ) );
		paint.setTextSize( 24f );
		paint.setAntiAlias( true );
		float textWidth = paint.measureText( this.name );
		float textPosX;
		if( textWidth > 128 )
		{
			textPosX = 0;
		}
		else
		{
			textPosX = ( 128 - textWidth ) / 2;
		}
		canvas.drawText( this.name , textPosX , 110 , paint );
		return bitmap;
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
		// texture_pos_x = vertices[0];
		// texture_pos_y = vertices[1];
	}
	
	public void setMesh(
			String objName ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float dz )
	{
		String path = objDir + objName;
		Mesh mesh = getMesh( path , this.width , this.y , offsetZ );
		mesh.scale( scaleX , scaleY * 0.9F , scaleZ );
		move( mesh , this.x + this.width / 2 , this.height / 2 , scaleX * dz );
		this.setMesh( mesh );
		this.setTexture( this.norImg );
		this.enableDepthMode( true );
		Log.v( "Hotseat" , "btn : x:" + x + ", y:" + y );
	}
	
	public Mesh getMesh(
			String path ,
			float offsetX ,
			float offsetY ,
			float offsetZ )
	{
		Mesh mesh = ObjLoader.loadObj( ThemeManager.getInstance().getInputStream( path ) , true );
		return mesh;
	}
	
	public boolean is3dRotation()
	{
		return true;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		Log.v( "Hotseat" , "objButton fling" );
		//  DockbarObjcetGroup.isStart=true;
		if( !DockbarObjcetGroup.isStart )
		{
			Root3D.releaseBtnDark();
			//releaseDark();
			DockbarObjcetGroup.isStart = true;
			HotObjMenuFront.getInstance().startRotation( 0.5f );
			return true;
		}
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( y < this.height && ( deltaY < 0 && Math.abs( deltaY ) / Math.abs( deltaX ) > 0.6 ) )
		{
			releaseFocus();
			HotSeat3D.startModelAnimal( 0.5F );
			return true;
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha) {
	//
	// if(hideFace){
	// Gdx.gl.glEnable(GL10.GL_CULL_FACE);
	//
	// // if(HotSeat3D.isOnBackSide)
	// // Gdx.gl.glCullFace(GL10.GL_FRONT);
	// // else
	//
	// Gdx.gl.glCullFace(GL10.GL_BACK);
	//
	//
	// super.draw(batch, parentAlpha);
	// Gdx.gl.glDisable(GL10.GL_CULL_FACE);
	// }
	// else{
	// super.draw(batch, parentAlpha);
	// }
	// }
}
