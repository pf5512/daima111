package com.iLoong.Robot.View;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooeeui.cometrobot.R;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class ClearButtonRingView extends PluginViewObject3D {
	private Cache<String, Mesh> mMeshCache = null;
	private float mAnimationDuration = 2f;
	private RobotArmView mRobotArmView;
	private Timeline mButtonRingTimeline;

	public ClearButtonRingView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "robot_clear_button_ring.obj");
		x = appContext.mWidgetContext.getResources().getDimension(
				R.dimen.battery_clear_button_x);
		y = appContext.mWidgetContext.getResources().getDimension(
				R.dimen.battery_clear_button_y);
		this.width = appContext.mWidgetContext.getResources().getDimension(
				R.dimen.battery_clear_button_width);
		this.height = appContext.mWidgetContext.getResources().getDimension(
				R.dimen.battery_clear_button_height);
		this.setOrigin(width / 2, height / 2);
		this.setMoveOffset(WidgetRobot.MODEL_WIDTH / 2,
				WidgetRobot.MODEL_HEIGHT / 2, 0);
		super.build();
	}

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void setRobotArmView(RobotArmView robotArmView) {
		this.mRobotArmView = robotArmView;
	}

	public void renderMesh(float dx, float dy) {
		InputStream stream = null;
		Mesh mesh = null;
		try {
			if (!WidgetRobot.loadOriginalObj) {
				Mesh originalMesh = null;
				if (WidgetRobot.useCache) {
					originalMesh = (Mesh) mMeshCache
							.get(WidgetRobot.ROBOT_CLEAR_BUTTON_RING);
				}
				if (originalMesh == null) {
					originalMesh = RobotHelper.loadMesh(appContext, RobotHelper
							.getThemeObjPath(appContext.mThemeName,
									"robot_clear_button_ring.obj"));
					if (WidgetRobot.useCache) {
						mMeshCache.put(WidgetRobot.ROBOT_CLEAR_BUTTON_RING,
								originalMesh);
					}
				}
				if (WidgetRobot.useCache) {
					mesh = RobotHelper.copyMesh(originalMesh, appContext);
				} else {
					mesh = originalMesh;
				}

			} else {
				stream = RobotHelper.getThemeObjStream(appContext,
						"robot_clear_button_ring.obj");
				mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true);
				RobotHelper.move(mesh, 0, 0, WidgetRobot.MODEL_BACK_SCALE_Z);
				if (WidgetRobot.saveObj) {
					RobotHelper.saveMesh(mesh, "robot_clear_button_ring.obj");
				}
				stream.close();
			}
			if (WidgetRobot.SCALE_SIZE != 1) {
				mesh.scale(WidgetRobot.SCALE_X, WidgetRobot.SCALE_Y,
						WidgetRobot.SCALE_Z);
			}
			RobotHelper.move(mesh, (WidgetRobot.MODEL_WIDTH - dx) / 2,
					(WidgetRobot.MODEL_HEIGHT - dy) / 2, 0);
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

	public boolean isPointInSelf(float x, float y) {
		Log.e("point", "x=" + x + " y=" + y + " this.originX=" + this.originX
				+ " this.originY=" + this.originY);
		double distance = Math.sqrt((x - this.originX) * (x - this.originX)
				+ (y - this.originY) * (y - this.originY));
		Log.e("point", "r=" + distance);
		float radius = appContext.mWidgetContext.getResources().getDimension(
				R.dimen.battery_clear_button_width) / 2;
		if (distance > radius) {
			return false;
		} else {
			return true;
		}
	}

	long clearedMemory = 0;

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		if (!isPointInSelf(x, y)) {
			return false;
		} else {
			Log.v("clearbutton", "onTouchDown");
			return true;
		}
	}

	@Override
	public boolean onClick(float x, float y) {
		Log.v("Robot", "clearbuttonring onclick");
		if (!WidgetRobot.mIsAnimationStopped) {
			return true;
		}
		// TODO Auto-generated method stub
		if (mButtonRingTimeline != null || !this.mRobotArmView.isTweenStopped()) {
			return false;
		}
		// 全局标志动画开始
		WidgetRobot.mIsAnimationStopped = false;
		startAnimation();
		appContext.mGdxApplication.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final long beforeClearMemory = getAvailMemory();
				long start = System.currentTimeMillis();
				kill();
				long end = System.currentTimeMillis();
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(end - start);
				int seconds = c.get(Calendar.SECOND);
				final long afterClearMemory = getAvailMemory();
				clearedMemory = afterClearMemory - beforeClearMemory;
				if (seconds > mAnimationDuration) {
					Log.v("clear", "清理时间：" + seconds);
					if (mButtonRingTimeline != null) {
						mButtonRingTimeline.free();
					}
					showClearResult();
				}
			}
		});
		return true;
	}

	private long getAvailMemory() {
		// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) appContext.mWidgetContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存

		// return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
		return mi.availMem / (1024 * 1024);
	}

	@SuppressWarnings("unused")
	private long getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		// return Formatter.formatFileSize(context, initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
		return initial_memory / (1024 * 1024);
	}

	private int clearedAppCount = 0;

	private void kill() {
		clearedAppCount = 0;
		ActivityManager activityManger = (ActivityManager) appContext.mWidgetContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = activityManger
				.getRunningAppProcesses();

		if (list != null)
			for (int i = 0; i < list.size(); i++) {
				ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
				// Log.v("清理进程", "pid:" + apinfo.pid + " processName:"
				// + apinfo.processName + "importance:"
				// + apinfo.importance);
				String[] pkgList = apinfo.pkgList;
				if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					// Process.killProcess(apinfo.pid);
					for (int j = 0; j < pkgList.length; j++) {
						// 2.2以上是过时的,请用killBackgroundProcesses代替
						Integer sdkVersion = Integer
								.valueOf(android.os.Build.VERSION.SDK);
						if (sdkVersion > 8) {
							activityManger.killBackgroundProcesses(pkgList[j]);
						} else {
							activityManger.restartPackage(pkgList[j]);
						}
						// Log.e("kill", pkgList[j]);
						clearedAppCount++;
					}
				}
			}
	}

	@Override
	public float getRotation() {
		// TODO Auto-generated method stub
		// Log.e("getRotation", "rotation:" + super.getRotation());
		return super.getRotation();
	}

	@Override
	public void setRotation(float rotate) {
		// TODO Auto-generated method stub
		// Log.e("setRotation", "rotation:" + rotate);
		super.setRotation(rotate);
	}

	public void startAnimation() {
		// this.setRotationVector(0, 0, 1);
		mButtonRingTimeline = Timeline.createParallel();
		mButtonRingTimeline.push(Tween
				.to(this, View3DTweenAccessor.ROTATION, mAnimationDuration)
				.ease(Quad.INOUT).target(-360 * 3));
		mButtonRingTimeline.push(Tween.set(this, View3DTweenAccessor.SCALE_XY)
				.ease(Linear.INOUT).target(1, 1, 1).delay(mAnimationDuration));
		mButtonRingTimeline.start(View3DTweenAccessor.manager)
				.setCallback(this);
	}

	public void stopAnimation() {
		if (mButtonRingTimeline != null) {
			mButtonRingTimeline.free();
			mButtonRingTimeline = null;
		}
		this.rotation = 0;
	}

	private void showClearResult() {
		if (this.mRobotArmView != null) {
			String title = appContext.mWidgetContext.getResources()
					.getString(R.string.clear_success_msg)
					.replace("${count}", "" + clearedAppCount)
					.replace("${memory}", clearedMemory + "");

			if (clearedMemory == 0) {
				title = appContext.mWidgetContext.getResources().getString(
						R.string.clear_empty_msg);
			}
			WidgetRobot.REBOT_MESSAGE = title;
			this.mRobotArmView.startAinimation(title);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onEvent(int type, BaseTween source) {
		// Log.v("ClearButtonRing", "onEvent");
		// Log.e("onevent", "type:" + type + " TweenCallback.COMPLETE"
		// + TweenCallback.COMPLETE);
		// Log.e("onevent", "source:" + source);
		// Log.e("onevent", "finished:" + mButtonRingTimeline.isFinished());
		if (source.equals(mButtonRingTimeline)
				&& type == TweenCallback.COMPLETE) {
			stopAnimation();
			showClearResult();
			return;
		}
		// TODO Auto-generated method stub
		super.onEvent(type, source);
	}

	@Override
	public boolean is3dRotation() {
		// TODO Auto-generated method stub
		return true;
	}
}
