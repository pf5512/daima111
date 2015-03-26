package com.iLoong.ThemeClock.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;

public class TextureUtil {
	/**
	 * 读取图片
	 * 
	 * @param context
	 *            上下文环境
	 * @param textureFile
	 *            贴图路径
	 * @return 图片
	 */
	public static BitmapTexture loadTexture(Context context, String textureFile) {
		AndroidFiles gdxFile = new AndroidFiles(context.getAssets());
		FileHandle fileHandle = gdxFile.internal(textureFile);
		Bitmap bitmap = BitmapFactory.decodeStream(fileHandle.read());
		BitmapTexture texture = new BitmapTexture(bitmap);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		// 回收bitmap
		bitmap.recycle();
		return texture;
	}

	/**
	 * 将字体转换为texture
	 */
	public static Texture createFontsTexture(String s, Paint paint) {
		Texture bmpTexture = null;
		if (s != null && !s.equals("")) {
			paint.setAntiAlias(true);
			paint.setDither(true);

			FontMetrics fm = paint.getFontMetrics();
			int height = (int) Math.ceil(fm.bottom - fm.top);
			int count = s.length();
			int width = 0;
			char ch;

			Canvas canvas = new Canvas();
			for (int i = 0; i < count; i++) {
				ch = s.charAt(i);
				float[] widths = new float[1];
				String str = String.valueOf(ch);
				paint.getTextWidths(str, widths);
				width += (int) Math.ceil(widths[0]);
			}

			Bitmap newbmp = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			canvas.setBitmap(newbmp);
			canvas.drawText(s, 1, -fm.top, paint);
			bmpTexture = new BitmapTexture(newbmp);// 每张back背景图
			bmpTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);// 纹理滤波器
			newbmp.recycle();
		}
		return bmpTexture;
	}

	/**
	 * 将字体转换为texture
	 * 
	 * @param s
	 *            文字信息
	 * @param paint
	 *            画笔
	 * @param w
	 *            图片宽
	 * @param h
	 *            图片高
	 * @return 贴图
	 */
	public static Texture createFontsTexture(String s, Paint paint, int w, int h) {
		Texture bmpTexture = null;
		if (s != null && !s.equals("")) {
			int width = 0;
			int count = s.length();
			char ch = '\0';
			// 确保所有文字都能显示
			for (int i = 0; i < count; i++) {
				ch = s.charAt(i);
				float[] widths = new float[1];
				String str = String.valueOf(ch);
				paint.getTextWidths(str, widths);
				width += (int) Math.ceil(widths[0]);
			}

			if (w < width) {
				w = width;
			}
			// 确保所有文字都能显示
			FontMetrics fm = paint.getFontMetrics();
			int height = (int) Math.ceil(fm.bottom - fm.top);
			if (h < height) {
				h = height;
			}

			Bitmap newbmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(newbmp);
			canvas.drawColor(Color.TRANSPARENT);
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setTextAlign(Align.CENTER);

			FontMetrics fontMetrics = paint.getFontMetrics();
			float lineHeight = (float) Math.ceil(fontMetrics.descent
					- fontMetrics.ascent);
			float posX = newbmp.getWidth() / 2;
			float posY = newbmp.getHeight() - (newbmp.getHeight() - lineHeight)
					/ 2 - fontMetrics.bottom;
			canvas.drawText(s, posX, posY, paint);
			bmpTexture = new BitmapTexture(newbmp);// 每张back背景图
			bmpTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);// 纹理滤波器
			newbmp.recycle();

		}
		return bmpTexture;
	}

	/**
	 * 创建一张透明图片
	 * 
	 * @return 一张透明图
	 */
	public static BitmapTexture createTransparentTexture() {
		Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(android.graphics.Color.TRANSPARENT);
		BitmapTexture bitmapTexture = new BitmapTexture(bitmap);
		// 释放bitmap
		bitmap.recycle();
		return bitmapTexture;
	}
}
