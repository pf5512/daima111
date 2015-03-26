package com.iLoong.launcher.HotSeat3D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import com.iLoong.launcher.Desktop3D.Log;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class HotObjIcon3D  extends Object3DBase{
	
	
	private Context mContext = null;
	private  int screen_width;
	private float texture_pos_x;
	private float texture_pos_y;
	private float SCALE_X=1f;
	private float SCALE_Y=1f;
	public HotObjIcon3D(String name) {
		super(name);

	}
	public HotObjIcon3D(String name, Context context, String objName,float width,float height,float parent_pos_y) {
		super(name);
		this.mContext = context;
		screen_width= Utils3D.getScreenWidth();
		InputStream stream = null;
		this.width = width;
		this.height= height;
		this.setOrigin(width / 2, height / 2);
		SCALE_X   = Utils3D.getScreenWidth()/480f;
		//SCALE_Y= Utils3D.getScreenHeight()/800f;
		//SCALE_X= Utils3D.getDensity()/1.5f;
		try 
		{
			stream = ThemeManager.getInstance().getInputStream(objName);
			Mesh mesh=ObjLoader.loadObj(stream,true);
			if (SCALE_X != 1) {
				mesh.scale(SCALE_X, SCALE_X,
						SCALE_X);
			}
			move(mesh,screen_width/2,parent_pos_y,0);
			setMesh(mesh);
			enableDepthMode(true);
			stream.close();
			this.setPosition(texture_pos_x, texture_pos_y-parent_pos_y);
			//setBackgroud(new NinePatch(R3D.getTextureRegion("menu-bg"), 2, 2, 0, 0));
		}catch ( Exception e) {
			e.printStackTrace();
		}	
	}
	public void move(Mesh mesh, float dx, float dy, float dz) {
		VertexAttribute posAttr = mesh.getVertexAttribute(Usage.Position);
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;

		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices(vertices);

		int idx = offset;

		for (int i = 0; i < numVertices; i++) {
			vertices[idx] += dx;
			vertices[idx + 1] += dy;
			vertices[idx + 2] += dz;
			idx += vertexSize;
		}
		mesh.setVertices(vertices);
		
		texture_pos_x= vertices[0];
		texture_pos_y= vertices[1];
		
	}
    
}
