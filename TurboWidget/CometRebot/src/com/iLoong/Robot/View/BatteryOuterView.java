package com.iLoong.Robot.View;

import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.BatteryManager;
import android.util.Log;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooeeui.cometrobot.R;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;

/**
 * 透明外表层
 * 
 * @author Administrator
 * 
 */
public class BatteryOuterView extends PluginViewObject3D {
	private Cache<String, Mesh> mMeshCache = null;
	private int mCurrentBatteryPercent = -1;

	public BatteryOuterView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "battery_outer.obj");
		this.setSize(WidgetRobot.MODEL_WIDTH, WidgetRobot.MODEL_HEIGHT);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
	}

	public void setBatteryPercent(int percent) {
		this.mCurrentBatteryPercent = percent;
	}

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void renderMesh(float dx, float dy) {
		InputStream stream = null;
		Mesh mesh = null;
		try {
			if (!WidgetRobot.loadOriginalObj) {
				Mesh originalMesh = null;
				if (WidgetRobot.useCache) {
					originalMesh = (Mesh) mMeshCache
							.get(WidgetRobot.BATTERY_OUTER);
				}
				if (originalMesh == null) {
					originalMesh = RobotHelper.loadMesh(appContext, RobotHelper
							.getThemeObjPath(appContext.mThemeName,
									"battery_outer.obj"));
					if (WidgetRobot.useCache) {
						mMeshCache.put(WidgetRobot.BATTERY_OUTER, originalMesh);
					}
				}
				if (WidgetRobot.useCache) {
					mesh = RobotHelper.copyMesh(originalMesh, appContext);
				} else {
					mesh = originalMesh;
				}

			} else {
				stream = RobotHelper.getThemeObjStream(appContext,
						"battery_outer.obj");
				mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true);
				RobotHelper.move(mesh, 0, 0, WidgetRobot.MODEL_BACK_SCALE_Z);
				if (WidgetRobot.saveObj) {
					RobotHelper.saveMesh(mesh, "battery_outer.obj");
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

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		super.draw(batch, parentAlpha);

	}

	public void refreshBatteryOuter(Intent batteryStickyIntent) {
		int level = batteryStickyIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,
				-1);
		int scale = batteryStickyIntent.getIntExtra(BatteryManager.EXTRA_SCALE,
				-1);
		int percent = level * 100 / scale;
		showBatteryCapacity(percent);
	}

	public void showBatteryCapacity(final int percent) {
		if (mCurrentBatteryPercent != percent) {
			appContext.mGdxApplication.postRunnable(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mCurrentBatteryPercent = percent;
					Log.v("robot", "showBatteryCapacity:"
							+ mCurrentBatteryPercent);
					TextureRegion newTextureRegion = getBatteryCapacityTexture(
							appContext, percent);
					Texture3D oldTexture = (Texture3D) BatteryOuterView.this.region
							.getTexture();
					BatteryOuterView.this.region.setRegion(newTextureRegion);
					if (oldTexture != null) {
						oldTexture.dispose();
					}
				}
			});
		}
	}

	public static TextureRegion getBatteryCapacityTexture(
			MainAppContext appContext, int percent) {
		int bitmapWidth = (int) appContext.mWidgetContext.getResources()
				.getDimension(R.dimen.battery_outer_temperature_width);
		int bitmapHeight = (int) appContext.mWidgetContext.getResources()
				.getDimension(R.dimen.battery_outer_temperature_height);
		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
//		if (appContext.mThemeName.equals("female")
//				|| appContext.mThemeName.equals("orange")) {
//			int tempColor = Color.rgb(107, 0, 60);
//			paint.setColor(tempColor);
//		} else {
			paint.setColor(Color.WHITE);
//		}
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setTextSize(appContext.mWidgetContext.getResources()
				.getDimension(R.dimen.battery_percent_font));
		String title = appContext.mWidgetContext.getResources().getString(
				R.string.battery_percent)
				+ percent + "%";
		FontMetrics fontMetrics = paint.getFontMetrics();
		// float lineHeight = fontMetrics.bottom - fontMetrics.top;
		// float lineHeight = getFontHeight(context.getResources().getDimension(
		// R.dimen.robot_message_font));
		float lineHeight = (float) Math.ceil(fontMetrics.descent
				- fontMetrics.ascent);
		float posY = bitmap.getHeight() - (bitmap.getHeight() - lineHeight) / 2
				- fontMetrics.bottom;
		canvas.drawText(title, bitmap.getWidth() * 0.05f, posY, paint);
		// canvas.drawText(title, bitmap.getWidth() * 0.09f,
		// bitmap.getHeight() * 0.4f, paint);

		Texture3D texture = new Texture3D(appContext.gdx,
				ImageHelper.bmp2Pixmap(bitmap));
		// FileStorageHelper.saveImages(appContext.mWidgetContext, 1, bitmap);
		TextureRegion newTextureRegion = new TextureRegion(texture);
		bitmap.recycle();
		return newTextureRegion;
	}
}
