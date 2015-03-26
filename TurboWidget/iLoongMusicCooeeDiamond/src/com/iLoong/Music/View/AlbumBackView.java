package com.iLoong.Music.View;

import java.util.List;

import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Music.R;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class AlbumBackView extends PluginViewObject3D implements MusicListener,
		LoadBitmapCallback {
	private MusicController musicController;

	public MusicController getMusicController() {
		return musicController;
	}

	public void setMusicController(MusicController musicController) {
		this.musicController = musicController;
		musicController.addMusicListener(this);
	}

	private TextureRegion defaultAlbumRegion = null;
	private MusicData currentMusic;

	public AlbumBackView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "album_back.obj", null);
		this.region = defaultAlbumRegion = new TextureRegion(WidgetThemeManager
				.getInstance().getThemeTexture("default_back_album.png"));
//		this.setSize(WidgetMusic.MODEL_WIDTH, WidgetMusic.MODEL_HEIGHT);
//		this.setMoveOffset(WidgetMusic.MODEL_WIDTH / 2,
//				WidgetMusic.MODEL_HEIGHT / 2, 0);
		super.build();
	}

	float drawAlpha = 0.5f;
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
	 cur_color.a *= drawAlpha;
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
		if (currentMusic != null ) {
			try {
				Intent intent = null;
				PackageManager packageManager = appContext.mContainerContext
						.getPackageManager();
				String[] activitys = appContext.mWidgetContext.getResources()
						.getStringArray(R.array.music_back_activity);
				for (int i = 0; i < activitys.length; i++) {
					String serviceStr = activitys[i];
					String[] serviceComponentarray = serviceStr.split(";");
					if (serviceComponentarray.length >= 2) {
						intent = new Intent();
						intent.setClassName(serviceComponentarray[0],
								serviceComponentarray[1]);
						List<ResolveInfo> resoveInfos = packageManager
								.queryIntentServices(intent, 0);
						if (resoveInfos.size() > 0) {
							break;
						}
					}
				}
				if (intent != null) {
					Uri personUri = ContentUris.withAppendedId(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							currentMusic.songId);
					intent.setDataAndType(personUri, "audio/*");
					intent.setData(personUri);
					appContext.mContainerContext.startActivity(intent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
		// return super.onClick(x, y);
	}

	@Override
	public void onNext(MusicData music) {
		// TODO Auto-generated method stub
		updateAlbumRegion(music);
	}

	@Override
	public void onPrevious(MusicData music) {
		// TODO Auto-generated method stub
		updateAlbumRegion(music);
	}

	@Override
	public void onPause(MusicData music) {
		// TODO Auto-generated method stub
		updateAlbumRegion(music);
	}

	@Override
	public void onPlay(MusicData music) {
		// TODO Auto-generated method stub
		updateAlbumRegion(music);
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return super.scroll(x, y, deltaX, deltaY);
	}

	@Override
	public void loadCompleted(final Bitmap bitmap) {
		// TODO Auto-generated method stub
		appContext.mGdxApplication.postRunnable(new Runnable() {

			@Override
			public void run() {
				if (bitmap != null) {
					// TODO Auto-generated method stub
					TextureRegion newRegion = new TextureRegion(
							new BitmapTexture(bitmap));
					if (!AlbumBackView.this.region.equals(defaultAlbumRegion)) {
						Texture oldTexture = AlbumBackView.this.region
								.getTexture();

						AlbumBackView.this.region = newRegion;
						if (oldTexture != null) {
							oldTexture.dispose();
						}
					} else {
						AlbumBackView.this.region = newRegion;
					}
				} else {
					if (!AlbumBackView.this.region.equals(defaultAlbumRegion)) {
						Texture oldTexture = AlbumBackView.this.region
								.getTexture();
						AlbumBackView.this.region = defaultAlbumRegion;
						if (oldTexture != null) {
							oldTexture.dispose();
						}
					} else {
						AlbumBackView.this.region = defaultAlbumRegion;
					}
				}
			}
		});
	}

	private void updateAlbumRegion(MusicData music) {
		if (music != null && music.id != -1) {
			if (currentMusic == null) {
				currentMusic = music;
				musicController.loadArtBitmap(music, this);
			} else {
				if (currentMusic.albumid != music.albumid) {
					currentMusic = music;
					musicController.loadArtBitmap(music, this);
				}
			}
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		if (region.getTexture() != null) {
			region.getTexture().dispose();
		}
		if (defaultAlbumRegion != null) {
			if (defaultAlbumRegion.getTexture() != null) {
				defaultAlbumRegion.getTexture().dispose();
			}
		}
	}

	@Override
	public void onMetaChanged(MusicData music) {
		// TODO Auto-generated method stub
		updateAlbumRegion(music);
	}
}
