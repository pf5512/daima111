package com.iLoong.Robot.View;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.util.Log;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;

public class BatteryInnerView extends PluginViewObject3D {
	private Cache<String, Mesh> mMeshCache = null;
	private int mCurrentBatteryPercent;
	Mesh mesh = null;
	public static final String WATCH_BACK_OBJ = "battery.obj";
	public static final String WATCH_BACK_TEXTURE = "watch_back.png";

	public BatteryInnerView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, WATCH_BACK_OBJ);
		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
	}

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void renderMesh(float dx, float dy) {
		InputStream stream = null;
		try {
			if (!WidgetRobot.loadOriginalObj) {
				Mesh originalMesh = null;
				if (WidgetRobot.useCache) {
					originalMesh = (Mesh) mMeshCache.get(WidgetRobot.BATTERY);
				}
				if (originalMesh == null) {
					originalMesh = RobotHelper.loadMesh(this.appContext,
							RobotHelper.getThemeObjPath(appContext.mThemeName,
									"battery.obj"));
					if (WidgetRobot.useCache) {
						mMeshCache.put(WidgetRobot.BATTERY, originalMesh);
					}
				}
				if (WidgetRobot.useCache) {
					mesh = RobotHelper.copyMesh(originalMesh, appContext);
				} else {
					mesh = originalMesh;
				}

			} else {
				stream = RobotHelper.getThemeObjStream(appContext,
						WATCH_BACK_OBJ);
				mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true);
				RobotHelper.move(mesh, 0, 0, WidgetRobot.MODEL_BACK_SCALE_Z);
				if (WidgetRobot.saveObj) {
					RobotHelper.saveMesh(mesh, "battery.obj");
				}
				stream.close();
			}
			if (WidgetRobot.SCALE_SIZE != 1) {
				mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
						WidgetRobot.SCALE_Z);
			}
			RobotHelper.move(mesh, (width - dx) / 2, (height - dy) / 2, 0);
			setMesh(mesh);
			enableDepthMode(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static TextureRegion getBatteryCapacityTexture(
			MainAppContext appContext, int percent) {
		Bitmap back = RobotHelper.getThemeBitmap(appContext, "battery.png");
		if (!back.isMutable()) {
			Bitmap temp = back.copy(Config.ARGB_8888, true);
			back.recycle();
			back = temp;
		}
		Bitmap front = RobotHelper.getThemeBitmap(appContext,
				"battery_full.png");

		Bitmap border = RobotHelper.getThemeBitmap(appContext,
				"battery_border.png");

		int x = 0;
		float factor = (((float) (100 - percent)) / ((float) 100));// 0.99
		int y = (int) (front.getHeight() * factor);

		int newWidth = front.getWidth();
		int newHeight = front.getHeight() - y;

		Bitmap newFrontBitmap = Bitmap.createBitmap(front, x, y, newWidth,
				newHeight);

		Paint mPaint = new Paint();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(-16777216);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(8.0F);

		Canvas c = new Canvas(back);
		// 5为上透明边高度，Y为 100-实际液体高度
		c.drawBitmap(newFrontBitmap, 0, 5 + y, mPaint);
		// 5为battery-border.png高度的一半值
		c.drawBitmap(border, 0, y + 5 - 5, mPaint);
		TextureRegion newTextureRegion = new TextureRegion(new Texture3D(
				appContext.gdx, ImageHelper.bmp2Pixmap(back)));
		back.recycle();
		front.recycle();
		newFrontBitmap.recycle();
		border.recycle();
		return newTextureRegion;
	}

	public void setBatteryPercent(int percent) {
		// TODO Auto-generated method stub
		mCurrentBatteryPercent = percent;
	}

	public void refreshBatteryInner(Intent batteryStickyIntent) {
		int level = batteryStickyIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,
				-1);
		int scale = batteryStickyIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
				-1);
		int percent = level * 100 / scale;
		showBatteryCapacity(percent);
	}

	public void showBatteryCapacity(final int percent) {
		if (mCurrentBatteryPercent != percent) {
			mCurrentBatteryPercent = percent;
			appContext.mGdxApplication.postRunnable(new Runnable() {
				@Override
				public void run() {
					mCurrentBatteryPercent = percent;
					Log.v("robot", "showBatteryCapacity:"
							+ mCurrentBatteryPercent);
					TextureRegion newTextureRegion = getBatteryCapacityTexture(
							appContext, percent);
					Texture3D oldTexture = (Texture3D) BatteryInnerView.this.region
							.getTexture();
					BatteryInnerView.this.region.setRegion(newTextureRegion);
					if (oldTexture != null) {
						oldTexture.dispose();
					}
				}
			});
		}
	}
}
