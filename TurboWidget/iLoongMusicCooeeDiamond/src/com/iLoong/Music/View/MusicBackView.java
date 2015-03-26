package com.iLoong.Music.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class MusicBackView extends PluginViewObject3D {
	public MusicBackView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "music_back.obj", "music_back.png");
		super.build();
	}

	float drawAlpha = 1f;
	Color cur_color = new Color();
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		 if (shader == null) {
			 shader = createDefaultShader();
			 }
			 shader.begin();
			 combinedMatrix.set(batch.getProjectionMatrix()).mul(
			 batch.getTransformMatrix());
			 shader.setUniformMatrix("u_projTrans", combinedMatrix);
			 shader.setUniformi("u_texture", 0);
			 cur_color.r = color.r;
			 cur_color.g = color.g;
			 cur_color.b = color.b;
			 cur_color.a = color.a;
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
			 Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
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

	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
		return super.onClick(x, y);
	}

	public void changeSkin(String subTheme) {
		String textureName = subTheme + "/" + "music_back.png";
		Texture texture = WidgetThemeManager.getInstance().getThemeTexture(
				textureName);
		if (texture != null) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			TextureRegion oldRegion = this.region;
			this.region = new TextureRegion(texture);
			if (oldRegion != null && oldRegion.getTexture() != null) {
				oldRegion.getTexture().dispose();
			}
		}
	}
}
