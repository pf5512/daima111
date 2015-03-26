package com.iLoong.ThemeClock.View;

import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.ThemeClock.Common.ClockHelper;
import com.iLoong.ThemeClock.Common.ThemeParse;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class ClockBackView extends Object3DBase {
	public static final String WATCH_BACK_OBJ = "watch_back.obj";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;

	public ClockBackView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name);
		this.mAppContext = appContext;
		x = 0;
		y = 0;
		this.region.setRegion(region);
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin(width / 2, height / 2);
	}

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void renderMesh(float dx, float dy) {
		Mesh mesh = getMesh();
		// if (WidgetClock.SCALE_SIZE != 1f) {
		// mesh.scale(WidgetClock.SCALE_X, WidgetClock.SCALE_Y,
		// WidgetClock.SCALE_Z);
		// }
		setMesh(mesh);
		enableDepthMode(false);
	}

	public Mesh getMesh() {
		Mesh mesh = ClockHelper.getMesh(mMeshCache, mAppContext,
				WATCH_BACK_OBJ, width / 2, height / 2, 0, WidgetClock.SCALE_X,
				WidgetClock.SCALE_Y, WidgetClock.SCALE_Z);
		return mesh;
	}
	
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		if (shader == null) {
			shader = createDefaultShader();
		}
		shader.begin();
		combinedMatrix.set(batch.getProjectionMatrix()).mul(
				batch.getTransformMatrix());
		shader.setUniformMatrix("u_projTrans", combinedMatrix);
		shader.setUniformi("u_texture", 0);
		Color cur_color = new Color(color);
		cur_color.a *= parentAlpha;
		shader.setUniformf("u_color", cur_color);

		if (true) {
			Gdx.gl.glDepthMask(true);
			Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
			Gdx.gl.glDepthFunc(GL10.GL_LEQUAL);
		}
		
		if (region.getTexture() != null) {
			region.getTexture().bind();
		}
		Gdx.gl.glEnable(GL10.GL_BLEND);
	//	Gdx.gl.glBlendFunc(1, 771);GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
		Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	//	setBlendFunction(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (faces != null)
			mesh.setIndices(faces.getIndices());
		if (vertices != null)
			mesh.setVertices(vertices.getVertices());
		if (Gdx.graphics.isGL20Available()) {
			mesh.render(shader, GL10.GL_TRIANGLES);
		} else {
			mesh.render(GL10.GL_TRIANGLES);
		}
		
		if (true) {
			Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
			Gdx.gl.glDepthMask(false);
		}
		shader.end();	
	}
	
}
