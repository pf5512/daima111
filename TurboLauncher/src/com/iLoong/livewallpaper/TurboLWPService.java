package com.iLoong.livewallpaper;

import java.io.IOException;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class TurboLWPService extends WallpaperService {
	// message handler, primary use is to update the screen
	private static final Handler mDemoHandler = new Handler();

	// custom log tag, used to filter output
	//private static final String LOG_TAG = "DemoLiveWallpaper";
	// quick flag to disable all debug output
	//private static boolean mDebugOutput = true;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new DigiFrameEngine();
	}
	
	class DigiFrameEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {
		
		private SharedPreferences mSharedPreferences;
		
		// is the screen currently visible?
		private boolean mVisible;
		
		// singluar paint object, don't re-create this unnecessarily
		private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		private Bitmap background = null;
		// wallpaper dimensions, these span many screens
		private int mWallpaperWidth;
		private int mWallpaperHeight;
		// pixel dimensions of actual screen
		private int mScreenWidth;
		private int mScreenHeight;
		private int mOffsetX;
		
		public DigiFrameEngine() {
			try
			{
				background = BitmapFactory.decodeStream( getAssets().open( "launcher/wallpapers/wallpaper00.jpg" ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		}	
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mDemoHandler.removeCallbacks(mDrawDigiFrame);
			background.recycle();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				drawFrame();
			} else {
				mDemoHandler.removeCallbacks(mDrawDigiFrame);
			}
		}
		
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			mScreenWidth = width;
			mScreenHeight = height;
			mWallpaperWidth = getDesiredMinimumWidth();
			mWallpaperHeight = getDesiredMinimumHeight();
			drawFrame();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mDemoHandler.removeCallbacks(mDrawDigiFrame);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
			mOffsetX = xPixels;
		}
		
		private final Runnable mDrawDigiFrame = new Runnable() {
			public void run() {
				drawFrame();
			}
		};
		
		public void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    draw(c);
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            // Reschedule the next redraw
            mDemoHandler.removeCallbacks(mDrawDigiFrame);
		}
		
		public void draw(Canvas canvas) {
			
			if (background != null) {
				Matrix m = new Matrix();
				float scale;
				if (mWallpaperHeight > mWallpaperWidth) {
					scale = (float) mWallpaperHeight / (float) background.getHeight();	
				} else {
					scale = (float) mWallpaperWidth / (float) background.getWidth();
				}
				m.setScale( scale, scale );
				canvas.drawBitmap( background , m , null );
			}
				

		}
	}
}