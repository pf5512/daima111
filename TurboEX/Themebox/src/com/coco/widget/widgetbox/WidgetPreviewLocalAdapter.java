package com.coco.widget.widgetbox;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.coco.theme.themebox.util.Tools;

public class WidgetPreviewLocalAdapter extends BaseAdapter {

	// 定义Content
	private Context mContext;
	private Bitmap previewImages[] = new Bitmap[] {};
	private boolean isPreview = false;

	// 构造
	public WidgetPreviewLocalAdapter(Context c, String pkg, Context dstContext) {
		mContext = c;
		getpreviewImages(pkg, dstContext);
	}

	public void onDestory() {
		for (Bitmap bmp : previewImages) {
			if (bmp != null && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
			}
		}
	}

	private void getpreviewImages(String pkg, Context dstContext) {
		String paths[] = PathTool.getPreviewLists(pkg);
		if (paths != null && paths.length != 0) {
			previewImages = new Bitmap[paths.length];
			for (int i = 0; i < paths.length; i++) {
				try {
					DisplayMetrics dis = mContext.getResources()
							.getDisplayMetrics();
					previewImages[i] = Tools.getPurgeableBitmap(paths[i],
							dis.widthPixels / 2, dis.heightPixels / 2);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
			isPreview = true;
			return;
		}
		previewImages = new Bitmap[1];
		InputStream stream = null;
		String iconPath = null;
		try {
			if (stream == null) {
				try {
					DisplayMetrics displayMetrics = mContext.getResources()
							.getDisplayMetrics();
					iconPath = "iLoong/image/" + displayMetrics.widthPixels
							+ "x" + displayMetrics.heightPixels
							+ "/widget_ico.png";
					stream = dstContext.getAssets().open(iconPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (stream == null) {
					iconPath = "iLoong/image/widget_ico.png";
					stream = dstContext.getAssets().open(iconPath);
				}
				isPreview = false;
				previewImages[0] = BitmapFactory.decodeStream(stream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取图片的个数
	@Override
	public int getCount() {
		return previewImages.length;
	}

	// 获取图片在库中的位置
	@Override
	public Object getItem(int position) {
		return position;
	}

	// 获取图片在库中的ID
	@Override
	public long getItemId(int position) {
		return position;
	}

	// 将图片取出来
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new ImageView(mContext);
		}
		// 要取出图片，即要定义一个ImageView来存
		// ImageView imageView = new ImageView(mContext);
		// imageView.setImageResource(mImageIds[position]);
		((ImageView) convertView).setImageBitmap(previewImages[position]);
		// 设置显示比例类型
		if (isPreview)
			convertView.setLayoutParams(new Gallery.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
		else
			convertView.setLayoutParams(new Gallery.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
		((ImageView) convertView).setScaleType(ImageView.ScaleType.FIT_CENTER);
		return convertView;
	}
}
