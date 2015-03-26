package com.iLoong.Music.View;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Music.R;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class LyricsView extends PluginViewObject3D implements MusicListener,
		LoadBitmapCallback {
	private MusicController musicController;
	private TextureRegion lyricsRegion = null;
	private MusicData currentMusic;
	private int lyricsWidth = 512;
	private int lyricsHeight = 42;

	public LyricsView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "lyrics.obj", null);
		this.region = lyricsRegion = new TextureRegion(WidgetThemeManager
				.getInstance().getThemeTexture("default_lyrics.png"));
		// this.setSize(WidgetMusic.MODEL_WIDTH, WidgetMusic.MODEL_HEIGHT);
		// this.setMoveOffset(WidgetMusic.MODEL_WIDTH / 2,
		// WidgetMusic.MODEL_HEIGHT / 2, 0);
		super.build();

		// lyricsWidth = appContext.mWidgetContext.getResources()
		// .getDimensionPixelSize(R.dimen.lyrics_width);
		// lyricsHeight = appContext.mWidgetContext.getResources()
		// .getDimensionPixelSize(R.dimen.lyrics_height);
	}

	@Override
	public void onNext(MusicData music) {
		// TODO Auto-generated method stub
		updateLyricsRegion(music);
	}

	@Override
	public void onPrevious(MusicData music) {
		// TODO Auto-generated method stub
		updateLyricsRegion(music);
	}

	@Override
	public void onPause(MusicData music) {
		// TODO Auto-generated method stub
		updateLyricsRegion(music);
	}

	@Override
	public void onPlay(MusicData music) {
		// TODO Auto-generated method stub
		updateLyricsRegion(music);
	}

	@Override
	public void onMetaChanged(MusicData music) {
		// TODO Auto-generated method stub
		updateLyricsRegion(music);
	}

	public void setMusicController(MusicController musicController) {
		// TODO Auto-generated method stub
		this.musicController = musicController;
		musicController.addMusicListener(this);
	}

	private void updateLyricsRegion(MusicData music) {
		Log.e("LyricsView", "updateLyricsRegion 1: data:" + music);
		if (music != null && music.id != -1) {
			if (currentMusic == null) {
				Log.e("LyricsView", "updateLyricsRegion 2: data:" + music);
				currentMusic = music;
				LoadLyricsTask task = new LoadLyricsTask();
				task.music = music;
				task.callback = this;
				musicController.postRunnable(task);
			} else {
				if (currentMusic.albumid != music.albumid) {
					currentMusic = music;
					Log.e("LyricsView", "updateLyricsRegion 3: data:" + music);
					LoadLyricsTask task = new LoadLyricsTask();
					task.music = music;
					task.callback = this;
					musicController.postRunnable(task);
				}
			}
		}else{
			currentMusic = null;
			LoadLyricsTask task = new LoadLyricsTask();
			task.music = music;
			task.callback = this;
			musicController.postRunnable(task);
		}
	}

	@Override
	public void loadCompleted(final Bitmap bitmap) {
		// TODO Auto-generated method stub
		Log.e("AlbumFrontView", "loadCompleted 1: bitmap:" + bitmap);
		appContext.mGdxApplication.postRunnable(new Runnable() {

			@Override
			public void run() {
				if (bitmap != null) {
					// TODO Auto-generated method stub
					TextureRegion newRegion = new TextureRegion(
							new BitmapTexture(bitmap));
					Log.e("AlbumFrontView", "loadCompleted 2: bitmap:" + bitmap);
					if (!LyricsView.this.region.equals(lyricsRegion)) {
						Texture oldTexture = LyricsView.this.region
								.getTexture();

						LyricsView.this.region = newRegion;
						if (oldTexture != null) {
							oldTexture.dispose();
						}
					} else {
						LyricsView.this.region = newRegion;
					}
				} else {
					Log.e("AlbumFrontView", "loadCompleted 3: bitmap:" + bitmap);
					if (!LyricsView.this.region.equals(lyricsRegion)) {
						Log.e("AlbumFrontView", "loadCompleted 4: bitmap:"
								+ bitmap);
						Texture oldTexture = LyricsView.this.region
								.getTexture();
						LyricsView.this.region = lyricsRegion;
						if (oldTexture != null) {
							oldTexture.dispose();
						}
					} else {
						Log.e("AlbumFrontView", "loadCompleted 5: bitmap:"
								+ bitmap);
						LyricsView.this.region = lyricsRegion;
					}
				}
			}
		});
	}

	public class LoadLyricsTask implements Runnable {
		public MusicData music;
		public LoadBitmapCallback callback;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String title = music.track + "  " + music.album + "  "
					+ music.album_artist;
			Bitmap bitmap = getLyricsBitmap(title);
			callback.loadCompleted(bitmap);
		}
	};

	public Bitmap getLyricsBitmap(String title) {
		Bitmap backImage = Bitmap.createBitmap(lyricsWidth, lyricsHeight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(backImage);
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		// paint.setTextSize(appContext.mWidgetContext.getResources()
		// .getDimension(R.dimen.lyrics_font_size));
		paint.setTextAlign(Align.CENTER);

		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float) Math.ceil(fontMetrics.descent
				- fontMetrics.ascent);
		float posX = backImage.getWidth() / 2;
		float posY = backImage.getHeight()
				- (backImage.getHeight() - lineHeight) / 2 - fontMetrics.bottom;
		canvas.drawText(title, posX, posY, paint);
		return backImage;
	}

	public int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

}
