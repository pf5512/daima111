/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iLoong.launcher.desktop;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import com.iLoong.launcher.Desktop3D.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.theme.ThemeManager;
import com.umeng.analytics.MobclickAgent;

public class WallpaperChooser extends Activity implements
		AdapterView.OnItemSelectedListener, OnClickListener {
	

	private static final String TAG = "Launcher.WallpaperChooser";

	private String wallpaperPath = "launcher/wallpapers";
	private static String customWallpaperPath;
	private boolean useCustomWallpaper = false;
	private Gallery mGallery;
	private ImageView mImageView;
	private boolean mIsWallpaperSet;

	private Bitmap mBitmap;

	private ArrayList<String> mThumbs;
	private ArrayList<String> mImages;
	private WallpaperLoader mLoader;
	private Context mThemeContext;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		String tempWallPaperPath = "";

		tempWallPaperPath = ThemeManager.getInstance().getCurrentThemeFileDir(
				wallpaperPath, true);

		if (tempWallPaperPath == null) {
			tempWallPaperPath = ThemeManager.getInstance()
					.getSystemThemeFileDir(wallpaperPath, true);
			mThemeContext = ThemeManager.getInstance().getSystemContext();
		} else {
			mThemeContext = ThemeManager.getInstance().getCurrentThemeContext();
		}
		wallpaperPath = tempWallPaperPath;
		findWallpapers();

		setContentView(RR.layout.wallpaper_chooser);

		mGallery = (Gallery) findViewById(RR.id.gallery);
		mGallery.setAdapter(new ImageAdapter(this));
		mGallery.setOnItemSelectedListener(this);
		mGallery.setCallbackDuringFling(false);

		findViewById(RR.id.set).setOnClickListener(this);

		mImageView = (ImageView) findViewById(RR.id.wallpaper);
	}

	private void findWallpapers() {
		mThumbs = new ArrayList<String>(24);
		mImages = new ArrayList<String>(24);
		ArrayList<String> mTemp = new ArrayList<String>(24);
		ArrayList<String> mFound = new ArrayList<String>(24);
		final Resources resources = mThemeContext.getResources();
		// Context.getPackageName() may return the "original" package name,
		// com.coesns.launcher2; Resources needs the real package name,
		// com.coesns.launcher2. So we ask Resources for what it thinks the
		// package name should be.
		customWallpaperPath = DefaultLayout.custom_wallpapers_path;
		File dir = new File(customWallpaperPath);
		if (dir.exists() && dir.isDirectory()) {
			useCustomWallpaper = true;
		}

		AssetManager assManager = resources.getAssets();

		String[] wallpapers = null;
		try {
			if (useCustomWallpaper) {
				wallpapers = dir.list();
			} else
				wallpapers = assManager.list(wallpaperPath);
			for (String name : wallpapers) {
				Log.v("wallpaper", name);
				if (!name.contains("_small")) {
					mImages.add(name);
				} else {
					mTemp.add(name);
				}
			}

			for (String name : mImages) {
				for (String nameTmp : mTemp) {
					if (name.equals(nameTmp.replace("_small", ""))) {
						mThumbs.add(nameTmp);
						mFound.add(name);
						break;
					}
				}
			}
			mImages.clear();
			mImages.addAll(mFound);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// final String packageName =
		// resources.getResourcePackageName(R.array.wallpapers);
		//
		// addWallpapers(resources, packageName, R.array.wallpapers);
		// addWallpapers(resources, packageName, R.array.extra_wallpapers);
	}

	// private void addWallpapers(Resources resources, String packageName, int
	// list) {
	// final String[] extras = resources.getStringArray(list);
	// for (String extra : extras) {
	// int res = resources.getIdentifier(extra, "drawable", packageName);
	// if (res != 0) {
	// final int thumbRes = resources.getIdentifier(extra + "_small",
	// "drawable", packageName);
	//
	// if (thumbRes != 0) {
	// mThumbs.add(thumbRes);
	// mImages.add(res);
	// // Log.d(TAG, "addWallpapers: [" + packageName + "]: " + extra + " (" +
	// res + ")");
	// }
	// }
	// }
	// }

	@Override
	protected void onResume() {
		super.onResume();
		mIsWallpaperSet = false;
		//友盟统计
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//友盟统计
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mLoader != null
				&& mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
			mLoader.cancel(true);
			mLoader = null;
		}
	}

	public void onItemSelected(AdapterView parent, View v, int position, long id) {
		if (mLoader != null
				&& mLoader.getStatus() != WallpaperLoader.Status.FINISHED) {
			mLoader.cancel();
		}
		mLoader = (WallpaperLoader) new WallpaperLoader().execute(position);
	}
	
	public  static Bitmap adaptWallpaper(Bitmap orgBitmap){
	    int screenWidth;
	    int screenHeiht;
	    float scare;
	    Bitmap newBitmap;
	    int orgWidth =orgBitmap.getWidth();
	    int orgHeight=orgBitmap.getHeight();
	    int newWidth=0;
	    int newHeight=0;
	    DisplayMetrics displayMetrics = new DisplayMetrics();
        iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        screenWidth =displayMetrics.widthPixels;
        screenHeiht=displayMetrics.heightPixels;
	    //we assume that the width of wallpaper must be twice as long as screen width.
        if(screenWidth*2/screenHeiht>orgHeight){
            newHeight =orgHeight-screenWidth*2/screenHeiht;
            scare= orgWidth/screenWidth;
            newBitmap =Bitmap.createBitmap(orgBitmap,0, newHeight, orgWidth, orgHeight-newHeight);
        }
        else{
            newWidth=orgWidth- orgHeight/screenHeiht*2;
            scare=orgHeight/screenHeiht;
            newBitmap =Bitmap.createBitmap(orgBitmap,newWidth, 0, orgWidth-newWidth, orgHeight);
        }
        Log.v("wallpaper", "newHeight: "+newHeight+" ,newWidth: "+newWidth);
       
        newBitmap=Tools.resizeBitmap(newBitmap,scare);
        return newBitmap;
                
	}

	/*
	 * When using touch if you tap an image it triggers both the onItemClick and
	 * the onTouchEvent causing the wallpaper to be set twice. Ensure we only
	 * set the wallpaper once.
	 */
	private void selectWallpaper(int position) {
		if (mIsWallpaperSet) {
			return;
		}
		InputStream is = null;
		InputStream newDimIs=null;
		if (useCustomWallpaper) {
			try {
				is = new FileInputStream(customWallpaperPath + "/"
						+ mImages.get(position));
				newDimIs = new FileInputStream(customWallpaperPath + "/"
						+ mImages.get(position));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			AssetManager asset = mThemeContext.getResources().getAssets();

			try {
				is = asset.open(wallpaperPath + "/" + mImages.get(position));
				newDimIs = asset.open(wallpaperPath + "/" + mImages.get(position));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mIsWallpaperSet = true;
		try {
			SharedPreferences prefs = iLoongLauncher.getInstance()
					.getSharedPreferences("launcher", Activity.MODE_PRIVATE);
			WallpaperManager wpm = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
			
			// deleted by zhqihong ,its not necessary to suggest device fixing wallpaper
			//	setWallpaperNewDim(newDimIs,wpm);
			
			
			// if (wpm.getWallpaperInfo() != null) {
			// Utils3D.showTimeFromStart("apply wallpaper live");
			// long _time = System.currentTimeMillis();
			// long bootSpent = SystemClock.elapsedRealtime();
			// long bootTime = _time - bootSpent;
			// long lastApplyTime = prefs.getLong("apply_wallpaper_time", -1);
			// if (lastApplyTime < bootTime) {
			// Bitmap mWallpaper = Tools.getImageFromInStream(is);
			// wpm.setBitmap(mWallpaper);
			// mWallpaper.recycle();
			// mWallpaper = null;
			// }
			// }
			// else {
			// wpm.setStream((is));
			// }
			// wpm.setStream((is));
			Bitmap mWallpaper = Tools.getImageFromInStream(is);
			
			mWallpaper=adaptWallpaper(mWallpaper);
			is.close();
			float minWidth = (float) Math.ceil(wpm.getDesiredMinimumWidth());
			float minHeight = wpm.getDesiredMinimumHeight();
			float scale = 1f;
			//zqh delete start
//			if (mWallpaper.getHeight() <= Utils3D
//					.getScreenDisplayMetricsHeight()) {
//				wpm.setBitmap(mWallpaper);
//				scale = (float) Utils3D.getScreenDisplayMetricsHeight()
//						/ mWallpaper.getHeight();
//				minWidth = (float) Math.ceil(mWallpaper.getWidth() * scale);
//			} else {
//				scale = (float) Utils3D.getScreenDisplayMetricsHeight()
//						/ mWallpaper.getHeight();
//				Bitmap newWallpaper = Tools.resizeBitmap(mWallpaper, scale);
//				wpm.setBitmap(newWallpaper);
//				minWidth = (float) Math.ceil(newWallpaper.getWidth());
//
//			}
//			if (minWidth < Utils3D.getScreenWidth()) {
//				minWidth = Utils3D.getScreenWidth();
//			}
			//zqh delete end
			wpm.setBitmap(mWallpaper);
			
			
			//wpm.suggestDesiredDimensions((int) minWidth, (int) minHeight);
			prefs.edit()
					.putLong("apply_wallpaper_time", System.currentTimeMillis())
					.commit();
			setResult(RESULT_OK);
			finish();
		} catch (IOException e) {
			Log.e(TAG, "Failed to set wallpaper: " + e);
		}
	}
	
	private void setWallpaperNewDim(InputStream newDimIs, WallpaperManager wpm)
	 {
			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(newDimIs, null, options);
			DisplayMetrics displayMetrics = new DisplayMetrics();
			iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay()
					.getMetrics(displayMetrics);
			final int maxDim = Math.max(displayMetrics.widthPixels,
					displayMetrics.heightPixels);
			final int minDim = Math.min(displayMetrics.widthPixels,
					displayMetrics.heightPixels);
			int mWallpaperWidth = options.outWidth;
			int mWallpaperHeight = options.outHeight;

	  		float scale = 1;
	   		if (mWallpaperWidth < minDim){
	   			scale = (float)minDim/(float)mWallpaperWidth;
	   		}
	   		if (mWallpaperHeight*scale < maxDim){
	   			scale = (float)maxDim/(float)mWallpaperHeight;
	   		}

	   		wpm.suggestDesiredDimensions((int)(mWallpaperWidth*scale), (int)(mWallpaperHeight*scale));
			Log.v("test", "wallpaper chooser wpm.widht="+mWallpaperWidth + " mWallpaperHeight="+mWallpaperHeight);
		}

	public void onNothingSelected(AdapterView parent) {
	}

	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		ImageAdapter(WallpaperChooser context) {
			mLayoutInflater = context.getLayoutInflater();
		}

		public int getCount() {
			return mThumbs.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;

			if (convertView == null) {
				image = (ImageView) mLayoutInflater.inflate(
						RR.layout.wallpaper_item, parent, false);
			} else {
				image = (ImageView) convertView;
			}

			// int thumbRes = mThumbs.get(position);
			// image.setImageResource(thumbRes);
			InputStream is = null;
			if (useCustomWallpaper) {
				try {
					is = new FileInputStream(customWallpaperPath + "/"
							+ mThumbs.get(position));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				AssetManager asset = mThemeContext.getResources().getAssets();

				try {
					is = asset
							.open(wallpaperPath + "/" + mThumbs.get(position));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			image.setImageBitmap(BitmapFactory.decodeStream(is));
			Drawable thumbDrawable = image.getDrawable();
			if (thumbDrawable != null) {
				thumbDrawable.setDither(true);
			} else {
				Log.e(TAG,
						"Error decoding thumbnail resId="
								+ mThumbs.get(position) + " for wallpaper #"
								+ position);
			}

			return image;
		}
	}

	public void onClick(View v) {
		selectWallpaper(mGallery.getSelectedItemPosition());
	}

	class WallpaperLoader extends AsyncTask<Integer, Void, Bitmap> {
		BitmapFactory.Options mOptions;

		WallpaperLoader() {
			mOptions = new BitmapFactory.Options();
			mOptions.inDither = false;
			mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		}

		protected Bitmap doInBackground(Integer... params) {
			if (isCancelled())
				return null;
			try {

				// return BitmapFactory.decodeResource(getResources(),
				// mImages.get(params[0]), mOptions);
				InputStream is = null;
				if (useCustomWallpaper) {
					try {
						is = new FileInputStream(customWallpaperPath + "/"
								+ mImages.get(params[0]));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return null;
					}
				} else {
					AssetManager asset = mThemeContext.getResources()
							.getAssets();

					try {
						is = asset.open(wallpaperPath + "/"
								+ mImages.get(params[0]));

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				}

				return BitmapFactory.decodeStream(is);

			} catch (OutOfMemoryError e) {
				return null;
			}

		}

		@Override
		protected void onPostExecute(Bitmap b) {
			if (b == null)
				return;

			if (!isCancelled() && !mOptions.mCancel) {
				// Help the GC
				if (mBitmap != null) {
					mBitmap.recycle();
				}

				final ImageView view = mImageView;
				view.setImageBitmap(b);

				mBitmap = b;

				final Drawable drawable = view.getDrawable();
				drawable.setFilterBitmap(true);
				drawable.setDither(true);

				view.postInvalidate();

				mLoader = null;
			} else {
				b.recycle();
			}
		}

		void cancel() {
			mOptions.requestCancelDecode();
			super.cancel(true);
		}
	}
}
