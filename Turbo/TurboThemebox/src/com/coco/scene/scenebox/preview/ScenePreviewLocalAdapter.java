package com.coco.scene.scenebox.preview;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.coco.theme.themebox.util.ContentConfig;


public class ScenePreviewLocalAdapter extends BaseAdapter
{
	
	// 定义Content
	private Context mContext;
	private Bitmap previewImages[] = new Bitmap[]{};
	
	// 构造
	public ScenePreviewLocalAdapter(
			Context c ,
			ContentConfig cfg ,
			Context dstContext )
	{
		mContext = c;
		getConfig( cfg , dstContext );
	}
	
	public void onDestory()
	{
		for( Bitmap bmp : previewImages )
		{
			if( bmp != null && !bmp.isRecycled() )
			{
				bmp.recycle();
				bmp = null;
			}
		}
	}
	
	private void getConfig(
			ContentConfig cfg ,
			Context dstContext )
	{
		Context icontext = dstContext;
		int imagesize = cfg.getPreviewArrayLength();
		previewImages = new Bitmap[imagesize];
		for( int i = 0 ; i < imagesize ; i++ )
		{
			previewImages[i] = cfg.loadPreviewImage( icontext , i );
		}
	}
	
	// 获取图片的个数
	@Override
	public int getCount()
	{
		return previewImages.length;
	}
	
	// 获取图片在库中的位置
	@Override
	public Object getItem(
			int position )
	{
		return position;
	}
	
	// 获取图片在库中的ID
	@Override
	public long getItemId(
			int position )
	{
		return position;
	}
	
	// 将图片取出来
	@Override
	public View getView(
			int position ,
			View convertView ,
			ViewGroup parent )
	{
		if( convertView != null )
		{
			return convertView;
		}
		// 要取出图片，即要定义一个ImageView来存
		ImageView imageView = new ImageView( mContext );
		// imageView.setImageResource(mImageIds[position]);
		imageView.setImageBitmap( previewImages[position] );
		// 设置显示比例类型
		imageView.setLayoutParams( new Gallery.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT ) );
		imageView.setScaleType( ImageView.ScaleType.FIT_CENTER );
		return imageView;
	}
}
