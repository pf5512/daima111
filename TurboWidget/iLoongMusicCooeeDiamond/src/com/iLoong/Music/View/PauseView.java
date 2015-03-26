package com.iLoong.Music.View;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Music.R;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.ThemeHelper;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class PauseView extends PluginViewObject3D implements MusicListener {
	private MusicController musicController = null;
	private TextureRegion pausePressedRegion;
	private TextureRegion pauseRegion = null;
	private TextureRegion playRegion = null;
	private TextureRegion playPressedRegion = null;
	public  int status = 0;// 0 暂停状态（暂停状态显示播放按钮） 1：播放状态（播放状态显示暂停按钮）
	private TimerTask mTimerTask;
	private Timer mTimer;

	public PauseView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "pause.obj", null);
		this.pausePressedRegion = new TextureRegion(WidgetThemeManager
				.getInstance().getThemeTexture("pause_press.png"));
		this.pauseRegion = new TextureRegion(WidgetThemeManager.getInstance()
				.getThemeTexture("pause.png"));
		this.playRegion = new TextureRegion(WidgetThemeManager.getInstance()
				.getThemeTexture("play.png"));
		this.playPressedRegion = new TextureRegion(WidgetThemeManager
				.getInstance().getThemeTexture("play_press.png"));
		this.region = this.playRegion;
		float pause_width = WidgetThemeManager.getInstance().getFloat(
				"play_width");
		float pause_height = WidgetThemeManager.getInstance().getFloat(
				"play_height");
		this.x = WidgetThemeManager.getInstance().getFloat("play_x");
		this.y = WidgetThemeManager.getInstance().getFloat("play_y");
		this.setSize(pause_width, pause_height);
		super.build();
		mTimer = new Timer();
		status = 0;
	}

	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
		if (musicController != null) {
			musicController.togglePlay();
		}
		return true;
	}

	@Override
	public boolean onLongClick(float x, float y) {
		// TODO Auto-generated method stub
		if (status == 0) {
			this.region = playRegion;
		} else if (status == 1) {
			this.region = pauseRegion;
		}
		return super.onLongClick(x, y);
	}

	public void setMusicController(MusicController musicController) {
		// TODO Auto-generated method stub
		this.musicController = musicController;
		musicController.addMusicListener(this);
	}

	@Override
	public void onNext(MusicData music) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrevious(MusicData music) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause(MusicData music) {
		// TODO Auto-generated method stub
		if (music.playing == true) {
			status = 1;
			this.region = pauseRegion;
		} else {
			status = 0;
			this.region = playRegion;
		}
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		if (status == 0) {
			this.region = playPressedRegion;
		} else if (status == 1) {
			this.region = pausePressedRegion;
		}
		releaseNextPressed();
		return super.onTouchDown(x, y, pointer);
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		if (status == 0) {
			this.region = playRegion;
		} else if (status == 1) {
			this.region = pauseRegion;
		}
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public void onPlay(MusicData music) {
		// TODO Auto-generated method stub
		if (music.playing == true) {
			status = 1;
			this.region = pauseRegion;
		} else {
			status = 0;
			this.region = playRegion;
		}
	}

	@Override
	public void onMetaChanged(MusicData music) {
		// TODO Auto-generated method stub
		if (music.playing == true) {
			status = 1;
			this.region = pauseRegion;
		} else {
			status = 0;
			this.region = playRegion;
		}
	}

	private void releaseNextPressed() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (status == 0) {
					PauseView.this.region = playRegion;
				} else if (status == 1) {
					PauseView.this.region = pauseRegion;
				}
			}
		};
		mTimer.schedule(mTimerTask, 50);
	}

	public void changeSkin(final String subTheme) {
		appContext.mGdxApplication.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Texture playTexture = ThemeHelper.getThemeSubTexture(
						appContext, subTheme, "play.png");
				if (playTexture != null) {
					playTexture.setFilter(TextureFilter.Linear,
							TextureFilter.Linear);
					if (PauseView.this.region.equals(playRegion)) {
						TextureRegion oldRegion = PauseView.this.region;
						PauseView.this.region = playRegion = new TextureRegion(
								playTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					} else {
						TextureRegion oldRegion = playRegion;
						playRegion = new TextureRegion(playTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					}
				}
				Texture playPressTexture = ThemeHelper.getThemeSubTexture(
						appContext, subTheme, "play_press.png");
				if (playPressTexture != null) {
					playPressTexture.setFilter(TextureFilter.Linear,
							TextureFilter.Linear);
					if (PauseView.this.region.equals(playPressedRegion)) {
						TextureRegion oldRegion = PauseView.this.region;
						PauseView.this.region = playPressedRegion = new TextureRegion(
								playPressTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					} else {
						TextureRegion oldRegion = playPressedRegion;
						playPressedRegion = new TextureRegion(playPressTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					}
				}

				Texture pauseTexture = ThemeHelper.getThemeSubTexture(
						appContext, subTheme, "pause.png");
				if (pauseTexture != null) {
					pauseTexture.setFilter(TextureFilter.Linear,
							TextureFilter.Linear);
					if (PauseView.this.region.equals(pauseRegion)) {
						TextureRegion oldRegion = PauseView.this.region;
						PauseView.this.region = pauseRegion = new TextureRegion(
								pauseTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					} else {
						TextureRegion oldRegion = pauseRegion;
						pauseRegion = new TextureRegion(pauseTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					}
				}

				Texture pausePressTexture = ThemeHelper.getThemeSubTexture(
						appContext, subTheme, "pause_press.png");
				if (pausePressTexture != null) {
					pausePressTexture.setFilter(TextureFilter.Linear,
							TextureFilter.Linear);
					if (PauseView.this.region.equals(pausePressedRegion)) {
						TextureRegion oldRegion = PauseView.this.region;
						PauseView.this.region = pausePressedRegion = new TextureRegion(
								pausePressTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					} else {
						TextureRegion oldRegion = pausePressedRegion;
						pausePressedRegion = new TextureRegion(
								pausePressTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					}
				}
			}

		});

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		if (pausePressedRegion != null) {
			if (pausePressedRegion.getTexture() != null) {
				pausePressedRegion.getTexture().dispose();
			}
		}
		if (pauseRegion != null) {
			if (pauseRegion.getTexture() != null) {
				pauseRegion.getTexture().dispose();
			}
		}
		if (playRegion != null) {
			if (playRegion.getTexture() != null) {
				playRegion.getTexture().dispose();
			}
		}
		if (playPressedRegion != null) {
			if (playPressedRegion.getTexture() != null) {
				playPressedRegion.getTexture().dispose();
			}
		}
	}

}
