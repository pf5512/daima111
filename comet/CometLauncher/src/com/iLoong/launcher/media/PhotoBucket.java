/* Copyright Statement:
 * 
 * This software/firmware and related documentation ("MediaTek Software") are protected under relevant copyright laws. The information contained herein is confidential and proprietary to MediaTek
 * Inc. and/or its licensors. Without the prior written permission of MediaTek inc. and/or its licensors, any reproduction, modification, use or disclosure of MediaTek Software, and information
 * contained herein, in whole or in part, shall be strictly prohibited.
 * 
 * MediaTek Inc. (C) 2010. All rights reserved.
 * 
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE") RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES
 * ARE PROVIDED TO RECEIVER ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE
 * RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE RELEASES MADE TO RECEIVER'S
 * SPECIFICATION OR TO CONFORM TO A PARTICULAR STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH
 * MEDIATEK SOFTWARE AT ISSUE. */
/* Copyright (C) 2009 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.media;


import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.media.MediaCache.MediaDataObserver;
import com.iLoong.launcher.media.PhotoItem.PhotoView;
import com.iLoong.launcher.theme.ThemeManager;


public class PhotoBucket implements MediaDataObserver
{
	
	public static final int VISIBLE_PHOTO_NUM = 1;
	public static final int EVENT_PHOTO_BUCKET_CLICK = 0;
	public static final int EVENT_PHOTO_BUCKET_LONGCLICK = 1;
	public String title;
	public MediaCache mediaCache;
	public ArrayList<PhotoItem> photos;
	public PhotoBucketView vg;
	public int bucketId;
	private static TextureRegion bgRegion;
	public static TextureRegion leftBracket;
	public static TextureRegion rightBracket;
	private float bgWidth;
	private float bgHeight;
	private float titleHeight;
	static
	{
		bgRegion = R3D.findRegion( "default-photobucket" );
		leftBracket = R3D.findRegion( "left-bracket" );
		rightBracket = R3D.findRegion( "right-bracket" );
	}
	
	public PhotoBucket()
	{
		photos = new ArrayList<PhotoItem>();
		bgWidth = R3D.photo_bucket_width;
		bgHeight = R3D.photo_bucket_height;
		titleHeight = Utils3D.getTitleHeight( R3D.photo_title_size , 1 );
		//MediaCache.getInstance().attach(0, this);
	}
	
	public View3D obtainView()
	{
		if( vg == null )
		{
			vg = new PhotoBucketView( title );
			vg.bucket = this;
		}
		for( int i = 0 ; i < photos.size() && i < VISIBLE_PHOTO_NUM ; i++ )
		{
			View3D view = photos.get( i ).obtainView();
			vg.addView( view );
			view.setPosition( 0 , 0 );
		}
		return vg;
	}
	
	public void prepare(
			int priority )
	{
		for( int i = 0 ; i < photos.size() && i < VISIBLE_PHOTO_NUM ; i++ )
		{
			photos.get( i ).prepare( priority );
		}
	}
	
	public void free()
	{
		for( int i = 0 ; i < photos.size() && i < VISIBLE_PHOTO_NUM ; i++ )
		{
			photos.get( i ).free();
		}
	}
	
	public void onDelete()
	{
		for( int i = 0 ; i < photos.size() ; i++ )
		{
			photos.get( i ).onDelete();
		}
		mediaCache.deletePhotoBucket( this );
	}
	
	public class PhotoBucketView extends ViewGroup3D implements MediaView
	{
		
		public PhotoBucket bucket;
		public boolean selected = false;
		
		public PhotoBucketView(
				String name )
		{
			super( name );
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			if( AppHost3D.selectState )
			{
				selected = !selected;
			}
			else
				super.onCtrlEvent( this , EVENT_PHOTO_BUCKET_CLICK );
			return true;
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			if( AppHost3D.selectState )
				return true;
			else
			{
				super.onCtrlEvent( this , EVENT_PHOTO_BUCKET_LONGCLICK );
				selected = true;
			}
			return true;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			if( bgRegion != null )
			{
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				batch.draw( bgRegion , x + ( width - bgWidth ) / 2 , y + titleHeight + ( height - titleHeight - bgHeight ) / 2 , bgWidth , bgHeight );
			}
			super.draw( batch , parentAlpha );
			if( region.getTexture() != null )
			{
				float numberWidth = 0;// Utils3D.getNumberWidth(photos.size());
				float titleX = ( width - region.getRegionWidth() ) / 2;
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				batch.draw( region , x + titleX , y );
				// titleX += region.getRegionWidth();
				// batch.draw(leftBracket, x+titleX, y);
				// titleX += leftBracket.getRegionWidth();
				// TextureRegion number;
				// String s = photos.size()+"";
				// char c;
				// for(int i = 0;i < s.length();i++){
				// c = s.charAt(i);
				// number = R3D.findRegion("photo-"+c);
				// batch.draw(number, x+titleX, y);
				// titleX += number.getRegionWidth();
				// }
				// batch.draw(rightBracket, x+titleX, y);
			}
			if( AppHost3D.selectState )
			{
				if( selected )
					batch.draw( R3D.selectedRegion , x + ( width + bgWidth ) / 2 - 1 * R3D.selectedRegion.getRegionWidth() , y + titleHeight + ( height - titleHeight - bgHeight ) / 2 + 8 );
				else
					batch.draw( R3D.unselectRegion , x + ( width + bgWidth ) / 2 - 1 * R3D.unselectRegion.getRegionWidth() , y + titleHeight + ( height - titleHeight - bgHeight ) / 2 + 8 );
			}
		}
		
		@Override
		public void setSize(
				float w ,
				float h )
		{
			super.setSize( w , h );
			this.setOrigin( w / 2 , h / 2 );
			for( int i = 0 ; i < photos.size() && i < VISIBLE_PHOTO_NUM ; i++ )
			{
				View3D view = photos.get( i ).obtainView();
				view.setSize( w , h - titleHeight );
				view.y = titleHeight;
			}
			if( region.getTexture() == null )
			{
				float numberWidth = 0;// Utils3D.getTitleWidth("(0000)",R3D.photo_title_size);
				region.setRegion( new BitmapTexture(
						( AppBar3D.titleToPixmapWidthLimit( title + "(" + photos.size() + ")" , (int)( w - numberWidth - 4 ) , R3D.photo_title_size , Color.WHITE , true ) ) ) );
			}
		}
		
		@Override
		public void prepare(
				int priority )
		{
			bucket.prepare( priority );
		}
		
		@Override
		public void free()
		{
			bucket.free();
		}
		
		@Override
		public void refresh()
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void select()
		{
			selected = true;
		}
		
		@Override
		public void share(
				ArrayList<Uri> list )
		{
			if( !selected )
				return;
			for( int i = 0 ; i < bucket.photos.size() ; i++ )
			{
				PhotoItem photo = bucket.photos.get( i );
				if( photo.data != null )
				{
					list.add( Uri.parse( "file://" + photo.data ) );
				}
			}
		}
		
		@Override
		public void onDelete()
		{
			if( !selected )
				return;
			for( int i = 0 ; i < bucket.photos.size() ; i++ )
			{
				PhotoItem photo = bucket.photos.get( i );
				photo.onDelete();
			}
			bucket.onDelete();
		}
		
		@Override
		public void clearSelect()
		{
			selected = false;
		}
		
		public void updateRegion()
		{
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					Texture oldTexture = region.getTexture();
					if( oldTexture != null )
					{
						float numberWidth = 0;// Utils3D.getTitleWidth("(0000)",R3D.photo_title_size);
						region.setRegion( new BitmapTexture( ( AppBar3D.titleToPixmapWidthLimit(
								title + "(" + photos.size() + ")" ,
								(int)( width - numberWidth - 4 ) ,
								R3D.photo_title_size ,
								Color.WHITE ,
								true ) ) ) );
						oldTexture.dispose();
					}
				}
			} );
		}
	}
	
	@Override
	public void update(
			int type )
	{
		// TODO Auto-generated method stub
		PhotoBucket photoBucket = MediaCache.getInstance().photoBuckets.get( this.bucketId );
		if( photoBucket != null )
			this.photos = photoBucket.photos;
		else
		{
			this.photos.clear();
		}
		if( vg != null )
		{
			vg.updateRegion();
		}
		// Log.e("test", "new photobucket children:" +
		// photoBucket.photos.size());
		// Log.e("test", "old photobucket children:" + this.photos.size());
	}
}
