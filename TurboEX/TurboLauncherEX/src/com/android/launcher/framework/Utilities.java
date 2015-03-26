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

package com.android.launcher.framework;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.DisplayMetrics;

import com.cooeeui.brand.turbolauncher.R;

/**
 * Various utilities shared amongst the Launcher's classes.
 */
final public class Utilities {
	@SuppressWarnings("unused")
	private static final String TAG = "Launcher.Utilities";

	private static int sIconWidth = -1;
	private static int sIconHeight = -1;
	private static int sIconTextureWidth = -1;
	private static int sIconTextureHeight = -1;

	private static final Paint sBlurPaint = new Paint();
	private static final Paint sGlowColorPressedPaint = new Paint();
	private static final Paint sGlowColorFocusedPaint = new Paint();
	private static final Paint sDisabledPaint = new Paint();
	private static final Rect sOldBounds = new Rect();
	private static final Canvas sCanvas = new Canvas();

	static {
		sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
				Paint.FILTER_BITMAP_FLAG));
	}
	static int sColors[] = { 0xffff0000, 0xff00ff00, 0xff0000ff };
	static int sColorIndex = 0;

	/* SPRD: Fix bug288618, @{ */
	private static final int COMPOSE_SHOULD_INIT = -1;
	private static final int DONT_COMPOSE_ICON = 0;
	private static final int NEED_COMPOSE_ICON = 1;
	private static int sNeedComposeIcon = COMPOSE_SHOULD_INIT;
	/* SPRD: Feature 253522, Remove the application drawer view @{ */
	private static final int[] icons_back = new int[] { R.drawable.icon_back, };
	private static final int icons_pre = R.drawable.icon_pre;

	/* @} */

	/**
	 * Returns a bitmap suitable for the all apps view. Used to convert pre-ICS
	 * icon bitmaps that are stored in the database (which were 74x74 pixels at
	 * hdpi size) to the proper size (48dp)
	 */
	public static Bitmap createIconBitmap(Bitmap icon, Context context) {
		int textureWidth = sIconTextureWidth;
		int textureHeight = sIconTextureHeight;
		int sourceWidth = icon.getWidth();
		int sourceHeight = icon.getHeight();
		if (sourceWidth > textureWidth && sourceHeight > textureHeight) {
			// Icon is bigger than it should be; clip it (solves the GB->ICS
			// migration case)
			return Bitmap.createBitmap(icon, (sourceWidth - textureWidth) / 2,
					(sourceHeight - textureHeight) / 2, textureWidth,
					textureHeight);
		} else if (sourceWidth == textureWidth && sourceHeight == textureHeight) {
			// Icon is the right size, no need to change it
			return icon;
		} else {
			// Icon is too small, render to a larger bitmap
			final Resources resources = context.getResources();
			return createIconBitmap(new BitmapDrawable(resources, icon),
					context);
		}
	}

	public static Bitmap getImageFromInStream(InputStream is) {
		Bitmap image = null;
		try {
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
		}
		return image;
	}

	/**
	 * Returns a bitmap suitable for the all apps view.
	 */
	public static Bitmap createIconBitmap(Drawable icon, Context context) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				initStatics(context);
			}

			int width = sIconWidth;
			int height = sIconHeight;

			if (icon instanceof PaintDrawable) {
				PaintDrawable painter = (PaintDrawable) icon;
				painter.setIntrinsicWidth(width);
				painter.setIntrinsicHeight(height);
			} else if (icon instanceof BitmapDrawable) {
				// Ensure the bitmap has a density.
				BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
					bitmapDrawable.setTargetDensity(context.getResources()
							.getDisplayMetrics());
				}
			}
			int sourceWidth = icon.getIntrinsicWidth();
			int sourceHeight = icon.getIntrinsicHeight();
			if (sourceWidth > 0 && sourceHeight > 0) {
				// There are intrinsic sizes.
				if (width < sourceWidth || height < sourceHeight) {
					// It's too big, scale it down.
					final float ratio = (float) sourceWidth / sourceHeight;
					if (sourceWidth > sourceHeight) {
						height = (int) (width / ratio);
					} else if (sourceHeight > sourceWidth) {
						width = (int) (height * ratio);
					}
				} else if (sourceWidth < width && sourceHeight < height) {
					// Don't scale up the icon
					width = sourceWidth;
					height = sourceHeight;
				}
			}

			// no intrinsic size --> use default size
			int textureWidth = sIconTextureWidth;
			int textureHeight = sIconTextureHeight;

			final Bitmap bitmap = Bitmap.createBitmap(textureWidth,
					textureHeight, Bitmap.Config.ARGB_8888);
			final Canvas canvas = sCanvas;
			canvas.setBitmap(bitmap);

			final int left = (textureWidth - width) / 2;
			final int top = (textureHeight - height) / 2;

			@SuppressWarnings("all")
			// suppress dead code warning
			final boolean debug = false;
			if (debug) {
				// draw a big box for the icon for debugging
				canvas.drawColor(sColors[sColorIndex]);
				if (++sColorIndex >= sColors.length)
					sColorIndex = 0;
				Paint debugPaint = new Paint();
				debugPaint.setColor(0xffcccc00);
				canvas.drawRect(left, top, left + width, top + height,
						debugPaint);
			}

			sOldBounds.set(icon.getBounds());
			icon.setBounds(left, top, left + width, top + height);
			icon.draw(canvas);
			icon.setBounds(sOldBounds);
			canvas.setBitmap(null);

			return bitmap;
		}
	}

	private static void initShouldComposeIcon(Context context) {
		Drawable preDrawable = context.getResources().getDrawable(icons_pre);
		Bitmap preBitmap = ((BitmapDrawable) preDrawable).getBitmap();
		int preWidth = preBitmap.getWidth();
		int preHeight = preBitmap.getHeight();
		int[] preRGB = new int[preWidth * preHeight];
		preBitmap.getPixels(preRGB, 0, preWidth, 0, 0, preWidth, preHeight);
		int length = preRGB.length;
		for (int i = 0; i < length; i++) {
			if (preRGB[i] == 0) {
				sNeedComposeIcon = NEED_COMPOSE_ICON;
				return;
			}
		}
		sNeedComposeIcon = DONT_COMPOSE_ICON;
	}

	public static Bitmap resizeImageAsIcon(Bitmap bitmap, Context context) {

		if (sIconWidth == -1) {
			initStatics(context);
		}

		int width = sIconWidth;
		int height = sIconHeight;
		// int width = 110;
		// int height = 110;
		return resizeImage(bitmap, width, height);
	}

	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {

		// load the origial Bitmap
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		if (width < w && height < h || width <= 0 || height <= 0 || w <= 0
				|| h <= 0) {
			return bitmap;
		}
		int newWidth = w;
		int newHeight = h;
		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);
		return resizedBitmap;
	}

	public static Drawable getCompoundedDrawable(Bitmap src1, Bitmap src2) {
		int src1W = src1.getWidth();
		int src1H = src1.getHeight();
		int src2W = src2.getWidth();
		int src2H = src2.getHeight();
		int destW = src2W;
		int destH = src2H;
		float src1X = 0;
		float src1Y = 0;
		float src2X = 0;
		float src2Y = 0;
		if (src2W < src1W) {
			src2X = (src1W - src2W) / 2;
			destW = src1W;
		}
		if (src2H < src1H) {
			src2Y = (src1H - src2H) / 2;
			destH = src1H;
		}
		if (src1W < src2W) {
			src1X = (src2W - src1W) / 2;
		}
		if (src1H < src2H) {
			src1Y = (src2H - src1H) / 2;
		}
		Bitmap mBitmap = Bitmap.createBitmap(destW, destH, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		canvas.drawBitmap(src1, src1X, src1Y, mPaint);
		canvas.drawBitmap(src2, src2X, src2Y, mPaint);
		return new BitmapDrawable(mBitmap);
	}

	static public Drawable getDestDrawale(Drawable drawable, int width,
			int height) {
		if (drawable == null) {
			return null;
		}
		int sourceWidth = ((BitmapDrawable) drawable).getBitmap().getWidth();
		int sourceHeight = ((BitmapDrawable) drawable).getBitmap().getHeight();
		if (sourceWidth < width || sourceHeight < height) {
			return drawable;
		}
		int destX = (sourceWidth - width) / 2;
		int dextY = (sourceHeight - height) / 2;
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		Bitmap bitmap = bitmapDrawable.getBitmap();
		Bitmap destBitmap = Bitmap.createBitmap(bitmap, destX, dextY, width,
				height);
		return new BitmapDrawable(destBitmap);
	}

	public static Drawable getDestBitmap(Bitmap src, Bitmap pre) {
		if (src == null) {
			return null;
		}
		int srcW = src.getWidth();
		int srcH = src.getHeight();
		int preW = pre.getWidth();
		int preH = pre.getHeight();
		int rgb1[] = new int[srcW * srcH];
		int rgb2[] = new int[preW * preH];
		src.getPixels(rgb1, 0, srcW, 0, 0, srcW, srcH);
		pre.getPixels(rgb2, 0, preW, 0, 0, preW, preH);
		/* SPRD: fix bug 277351 @{ */
		for (int i = 0; i < rgb1.length; i++) {
			if (i < rgb2.length) {
				/* SPRD: fix bug 210598 @{ */
				if (rgb2[i] == 0) {
					rgb1[i] = 0x000000;
				}
				// rgb2[i] = rgb1[i];
				/* @} */
			} else {
				rgb1[i] = 0x000000;
			}
		}
		Bitmap dest = Bitmap.createBitmap(rgb1, srcW, srcH, Config.ARGB_8888);
		/* @} */
		return new BitmapDrawable(dest);
	}

	/* @} */
	/* SPRD: Feature 253522, Remove the application drawer view @{ */
	static Bitmap createIconBitmap(Drawable icon, Context context, boolean back) {
		/* SPRD: Fix bug288618, @{ */
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				initStatics(context);
			}
			if (sNeedComposeIcon == COMPOSE_SHOULD_INIT) {
				initShouldComposeIcon(context);
			}
			int width = sIconWidth;
			int height = sIconHeight;
			if (icon instanceof PaintDrawable) {
				PaintDrawable painter = (PaintDrawable) icon;
				painter.setIntrinsicWidth(width);
				painter.setIntrinsicHeight(height);
			} else if (icon instanceof BitmapDrawable) {
				// Ensure the bitmap has a density.
				BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
					bitmapDrawable.setTargetDensity(context.getResources()
							.getDisplayMetrics());
				}
			}
			// no intrinsic size --> use default size
			int textureWidth = sIconTextureWidth;
			int textureHeight = sIconTextureHeight;
			final Bitmap bitmap = Bitmap.createBitmap(textureWidth,
					textureHeight, Bitmap.Config.ARGB_8888);
			final Canvas canvas = sCanvas;
			canvas.setBitmap(bitmap);
			final int left = (textureWidth - width) / 2;
			final int top = (textureHeight - height) / 2;
			sOldBounds.set(icon.getBounds());
			// if not support cucc , to draw background
			if (back && sNeedComposeIcon == NEED_COMPOSE_ICON) {
				int drawable_id = icons_back[new Random()
						.nextInt(icons_back.length)];
				/* SPRD: fix bug 279827 @{ */
				Drawable drawable_back = context.getResources().getDrawable(
						drawable_id);
				/* SPRD: fix bug216214 bkgd to be transparent @{ */
				// Bitmap drawableBitmap =
				// ((BitmapDrawable)drawable).getBitmap().copy(Config.ARGB_4444,
				// true);
				// drawableBitmap.eraseColor(0);
				Bitmap bitmap_pre = resizeImage(((BitmapDrawable) context
						.getResources().getDrawable(icons_pre)).getBitmap(),
						width, height);
				int preHeight = bitmap_pre.getHeight();
				int preWidth = bitmap_pre.getWidth();
				Bitmap bitmap_back = resizeImage(
						((BitmapDrawable) drawable_back).getBitmap(), preWidth,
						preHeight);
				Bitmap tempBitmap = resizeImage(
						((BitmapDrawable) icon).getBitmap(), preWidth,
						preHeight);
				icon = getCompoundedDrawable(bitmap_back, tempBitmap);
				/* @} */
				/* SPRD: fix bug 277351 @{ */
				Drawable clippingDrawable = getDestDrawale(icon, preWidth,
						preHeight);
				// Drawable drawablePre =
				// getDestDrawale(context.getResources().getDrawable(icons_pre),
				// width, height);
				icon = getDestBitmap(
						((BitmapDrawable) clippingDrawable).getBitmap(),
						bitmap_pre);
				Drawable drawable_top = context.getResources().getDrawable(
						R.drawable.icon_top);
				Bitmap bitmap_top = resizeImage(
						((BitmapDrawable) drawable_top).getBitmap(), preWidth,
						preHeight);
				/* @} */
				icon = getCompoundedDrawable(
						((BitmapDrawable) icon).getBitmap(), bitmap_top);
				/* @} */
			}
			/* @} */
			icon.setBounds(left, top, left + width, top + height);
			icon.draw(canvas);
			icon.setBounds(sOldBounds);
			canvas.setBitmap(null);
			return bitmap;
		}
	}

	static void drawSelectedAllAppsBitmap(Canvas dest, int destWidth,
			int destHeight, boolean pressed, Bitmap src) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				// We can't have gotten to here without src being initialized,
				// which
				// comes from this file already. So just assert.
				// initStatics(context);
				throw new RuntimeException(
						"Assertion failed: Utilities not initialized");
			}

			dest.drawColor(0, PorterDuff.Mode.CLEAR);

			int[] xy = new int[2];
			Bitmap mask = src.extractAlpha(sBlurPaint, xy);

			float px = (destWidth - src.getWidth()) / 2;
			float py = (destHeight - src.getHeight()) / 2;
			dest.drawBitmap(mask, px + xy[0], py + xy[1],
					pressed ? sGlowColorPressedPaint : sGlowColorFocusedPaint);

			mask.recycle();
		}
	}

	/**
	 * Returns a Bitmap representing the thumbnail of the specified Bitmap. The
	 * size of the thumbnail is defined by the dimension
	 * android.R.dimen.launcher_application_icon_size.
	 * 
	 * @param bitmap
	 *            The bitmap to get a thumbnail of.
	 * @param context
	 *            The application's context.
	 * 
	 * @return A thumbnail for the specified bitmap or the bitmap itself if the
	 *         thumbnail could not be created.
	 */
	static Bitmap resampleIconBitmap(Bitmap bitmap, Context context) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				initStatics(context);
			}

			if (bitmap.getWidth() == sIconWidth
					&& bitmap.getHeight() == sIconHeight) {
				return bitmap;
			} else {
				final Resources resources = context.getResources();
				return createIconBitmap(new BitmapDrawable(resources, bitmap),
						context);
			}
		}
	}

	static Bitmap drawDisabledBitmap(Bitmap bitmap, Context context) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				initStatics(context);
			}
			final Bitmap disabled = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			final Canvas canvas = sCanvas;
			canvas.setBitmap(disabled);

			canvas.drawBitmap(bitmap, 0.0f, 0.0f, sDisabledPaint);

			canvas.setBitmap(null);

			return disabled;
		}
	}

	private static void initStatics(Context context) {
		final Resources resources = context.getResources();
		final DisplayMetrics metrics = resources.getDisplayMetrics();
		final float density = metrics.density;

		sIconWidth = sIconHeight = (int) resources
				.getDimension(R.dimen.app_icon_size);
		sIconTextureWidth = sIconTextureHeight = sIconWidth;

		sBlurPaint.setMaskFilter(new BlurMaskFilter(5 * density,
				BlurMaskFilter.Blur.NORMAL));
		sGlowColorPressedPaint.setColor(0xffffc300);
		sGlowColorFocusedPaint.setColor(0xffff8e00);

		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0.2f);
		sDisabledPaint.setColorFilter(new ColorMatrixColorFilter(cm));
		sDisabledPaint.setAlpha(0x88);
	}

	/* @} */
	// icon bg mask cover , change by shlt@2014/11/11 ADD START
	public static Bitmap combineIcon(Context context, Bitmap icon, Bitmap bg,
			Bitmap mask, Bitmap cover) {
		if (icon == null)
			throw new RuntimeException("icon must not be empty");
		Bitmap result = null;
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				initStatics(context);
			}
			int textureWidth = sIconTextureWidth;
			int textureHeight = sIconTextureHeight;
			final Bitmap bitmap = Bitmap.createBitmap(textureWidth,
					textureHeight, Bitmap.Config.ARGB_8888);
			final Canvas canvas = sCanvas;
			canvas.setBitmap(bitmap);
			Paint paint = new Paint();
			//
			if (bg != null)
				canvas.drawBitmap(bg, 0, 0, paint);
			//
			if (mask != null) {
				Canvas canvasMask = new Canvas(icon);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
				canvasMask.drawBitmap(mask, 0, 0, paint);
				paint.setXfermode(null);
			}
			canvas.drawBitmap(icon, 0, 0, paint);
			//
			if (cover != null)
				canvas.drawBitmap(cover, 0, 0, paint);
			//
			result = bitmap.copy(Config.ARGB_8888, true);
			//
			if (bitmap != null && !bitmap.isRecycled())
				bitmap.recycle();
			if (icon != null && !icon.isRecycled())
				icon.recycle();
			return result;
		}
	}

	// icon bg mask cover , change by shlt@2014/11/11 ADD END
	/** Only works for positive numbers. */
	static int roundToPow2(int n) {
		int orig = n;
		n >>= 1;
		int mask = 0x8000000;
		while (mask != 0 && (n & mask) == 0) {
			mask >>= 1;
		}
		while (mask != 0) {
			n |= mask;
			mask >>= 1;
		}
		n += 1;
		if (n != orig) {
			n <<= 1;
		}
		return n;
	}

	static int generateRandomId() {
		return new Random(System.currentTimeMillis()).nextInt(1 << 24);
	}

	/**
	 * Added by cooee Hugo.ye
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusbarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return sbar;
	}
}
