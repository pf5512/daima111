package com.coco.theme.themebox.preview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.PathTool;
import com.iLoong.base.themebox.R;

public class ThemePreviewHotAdapter extends BaseAdapter {

	// 定义Content
	private Context mContext;
	private DownModule downModule;
	private String packageName;
	private List<Bitmap> previewImages = new ArrayList<Bitmap>();
	public boolean needDownImage = false;
	private final int VIEW_TYPE_IMAGE = 0;
	private final int VIEW_TYPE_DOWNLOADING = 1;
	private final int VIEW_TYPE_COUNT = 2;
	private Bitmap imgThumb;

	// 构造
	public ThemePreviewHotAdapter(Context cxt, String pkgName, DownModule down) {
		mContext = cxt;
		downModule = down;
		packageName = pkgName;
		loadImage();
	}

	public void onDestory() {
		for (Bitmap bmp : previewImages) {
			if (bmp != null && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
			}
		}
		if (imgThumb != null && !imgThumb.isRecycled()) {
			imgThumb.recycle();
			imgThumb = null;
		}
	}

	private void loadImage() {
		String[] strArray = PathTool.getPreviewLists(packageName);
		previewImages.clear();
		if (strArray != null && strArray.length != 0) {
			needDownImage = false;
			for (String imgPath : strArray) {
				try {
					Bitmap tmp = BitmapFactory.decodeFile(imgPath);
					if (tmp == null) {
						needDownImage = true;
						break;
					}
					previewImages.add(tmp);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
		} else {
			needDownImage = true;
		}
	}

	public void reload() {
		loadImage();
		needDownImage = false;
		notifyDataSetChanged();
	}

	// 获取图片的个数
	@Override
	public int getCount() {
		if (previewImages.size() == 0) {
			return 1;
		}
		return previewImages.size();
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

	@Override
	public int getItemViewType(int position) {
		if (previewImages.size() == 0) {
			return VIEW_TYPE_DOWNLOADING;
		}
		return VIEW_TYPE_IMAGE;
	}

	@Override
	public int getViewTypeCount() {
		super.getViewTypeCount();
		return VIEW_TYPE_COUNT;
	}

	// 将图片取出来
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (previewImages.size() == 0) {
			if (needDownImage) {
				downModule
						.downloadPreview(packageName, DownloadList.Theme_Type);
			}
		}
		int viewType = getItemViewType(position);
		if (viewType == VIEW_TYPE_DOWNLOADING) {
			if (convertView != null) {
				return convertView;
			}
			View retView = View.inflate(mContext,
					R.layout.gallery_item_downloading, null);
			ImageView imgView = (ImageView) retView.findViewById(R.id.img);
			if (imgThumb == null)
				imgThumb = BitmapFactory.decodeFile(PathTool
						.getThumbFile(packageName));
			if (imgThumb != null) {
				imgView.setImageBitmap(imgThumb);
				System.out.println("imgThumb = " + imgThumb + " position = "
						+ position);
			}
			return retView;
		}
		ImageView imageView = (ImageView) convertView;
		if (imageView == null) {
			// 要取出图片，即要定义一个ImageView来存
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new Gallery.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
		// 设置显示比例类型
		imageView.setImageBitmap(previewImages.get(position));
		return imageView;
	}
}
