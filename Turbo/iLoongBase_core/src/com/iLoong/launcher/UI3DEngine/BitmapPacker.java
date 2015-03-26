package com.iLoong.launcher.UI3DEngine;


import java.nio.IntBuffer;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.OrderedMap;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.app.LauncherBase;


public class BitmapPacker implements Disposable
{
	
	private String FILEDIR;
	private Page curPage;
	private int hNum;
	private int vNum;
	private TextureFilter defaultFilter;
	private TextureFilter defaultMagFilter;
	private boolean enablePack = false;
	private int cellWidth;
	private int cellHeight;
	private int mainThreadId;
	private LauncherBase launcher;
	public static long packDelay = 3000;
	public static boolean write = true;
	public static final int minHNum = 3;
	public static final int minVNum = 2;
	public static final int DELETE_TEXTURE_MAX = 2;
	public static float texture3DSize = 0;
	public static float minPackSize = 0;
	public static float realPackSize = 0;
	
	static final class Node
	{
		
		public Node leftChild;
		public Node rightChild;
		public Rectangle rect;
		public String leaveName;
		
		public Node(
				int x ,
				int y ,
				int width ,
				int height ,
				Node leftChild ,
				Node rightChild ,
				String leaveName )
		{
			this.rect = new Rectangle( x , y , width , height );
			this.leftChild = leftChild;
			this.rightChild = rightChild;
			this.leaveName = leaveName;
		}
		
		public Node()
		{
			rect = new Rectangle();
		}
	}
	
	public class Page
	{
		
		Node root;
		OrderedMap<String , BitmapRect> rects;
		public Bitmap image;
		public BitmapTexture texture;
		Array<String> addedRects = new Array<String>();
		public FileHandle file;
		long fileTime;
		public boolean disposed = false;
		public boolean textureDeleted = false;
		
		public Bitmap getBitmap()
		{
			return image;
		}
	}
	
	public class BitmapRect
	{
		
		public BitmapRect(
				Rectangle rect2 ,
				boolean isDefault2 )
		{
			rect = rect2;
			isDefault = isDefault2;
		}
		
		public Rectangle rect;
		public boolean isDefault;
		public Bitmap bitmap;
		public BitmapTexture texture;
	}
	
	int pageWidth;
	int pageHeight;
	final int padding;
	final boolean duplicateBorder;
	public static final Array<Page> pages = new Array<Page>();
	//Page currPage;
	boolean disposed;
	
	public BitmapPacker(
			LauncherBase launcher ,
			int packageNum ,
			int padding ,
			boolean duplicateBorder ,
			TextureFilter defaultFilter ,
			TextureFilter defaultMagFilter ,
			int cellWidth ,
			int cellHeight ,
			boolean enablePack ,
			int mainThreadId ,
			String fileDir )
	{
		vNum = (int)Math.sqrt( packageNum );
		hNum = packageNum / vNum + 1;
		if( vNum < minVNum )
			vNum = minVNum;
		if( hNum < minHNum )
			hNum = minHNum;
		//this.pageWidth = width;
		//this.pageHeight = height;
		this.padding = padding;
		this.duplicateBorder = duplicateBorder;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.defaultFilter = defaultFilter;
		this.defaultMagFilter = defaultMagFilter;
		this.enablePack = enablePack;
		this.mainThreadId = mainThreadId;
		this.launcher = launcher;
		this.FILEDIR = fileDir;
		newPage();
	}
	
	public void drawBitmap(
			Bitmap image ,
			Bitmap bmp ,
			int x ,
			int y )
	{
		Canvas canvas = new Canvas( image );
		canvas.drawBitmap( bmp , x , y , null );
	}
	
	public void drawBitmap(
			Bitmap image ,
			Bitmap bmp ,
			int srcx ,
			int srcy ,
			int srcWidth ,
			int srcHeight ,
			int dstx ,
			int dsty ,
			int dstWidth ,
			int dstHeight )
	{
		Rect src = new Rect( srcx , srcy , srcWidth , srcHeight );
		Rect dst = new Rect( dstx , dsty , dstWidth , dstHeight );
		Canvas canvas = new Canvas( image );
		canvas.drawBitmap( bmp , src , dst , null );
	}
	
	public synchronized void repack(
			String name ,
			Bitmap image )
	{
		if( disposed || enablePack )
		{
			image.recycle();
			return;
		}
		BitmapRect tmpRect = null;
		for( Page tmp : pages )
		{
			tmpRect = tmp.rects.get( name );
			if( tmpRect != null )
			{
				if( tmpRect.bitmap != null )
					tmpRect.bitmap.recycle();
				tmpRect.bitmap = image.copy( Bitmap.Config.ARGB_8888 , false );
				if( !tmp.addedRects.contains( name , false ) )
					tmp.addedRects.add( name );
				//Log.i("theme", "repack:"+name);
			}
			else
			{
				Page page = tmp;
				BitmapRect rect = new BitmapRect( null , false );
				rect.bitmap = image.copy( Bitmap.Config.ARGB_8888 , false );
				page.rects.put( name , rect );
				page.addedRects.add( name );
			}
		}
		image.recycle();
		return;
	}
	
	public synchronized Rectangle pack(
			String name ,
			Bitmap image ,
			boolean isDefault )
	{
		if( disposed )
		{
			image.recycle();
			return null;
		}
		BitmapRect tmpRect = null;
		for( Page tmp : pages )
		{
			tmpRect = tmp.rects.get( name );
			if( tmpRect != null )
			{
				if( !tmpRect.isDefault || isDefault )
				{
					image.recycle();
					Log.d( "pack" , "Key with name '" + name + "' is already in map" );
					return null;
				}
				//update(name,image,isDefault);
				tmp.rects.remove( name );
				tmp.addedRects.removeValue( name , false );
				return pack( name , image , isDefault );
			}
		}
		if( !enablePack )
		{
			Page page = curPage;
			BitmapRect rect = new BitmapRect( null , isDefault );
			rect.bitmap = image.copy( Bitmap.Config.ARGB_8888 , false );
			page.rects.put( name , rect );
			page.addedRects.add( name );
			image.recycle();
			return null;
		}
		int borderPixels = padding + ( duplicateBorder ? 1 : 0 );
		borderPixels <<= 1;
		if( image.getWidth() >= pageWidth + borderPixels || image.getHeight() >= pageHeight + borderPixels )
		{
			//image.dispose();
			//throw new GdxRuntimeException("page size for '" + name + "' to small");
			Page tmp = curPage;
			newPage( ( image.getWidth() - borderPixels + 15 ) , ( image.getHeight() - borderPixels + 15 ) );
			Rectangle rect = pack( name , image , isDefault );
			curPage = tmp;
			return rect;
		}
		Rectangle rect = new Rectangle( 0 , 0 , image.getWidth() + borderPixels , image.getHeight() + borderPixels );
		Node node = null;
		Page page = curPage;
		node = insert( page.root , rect );
		if( node == null )
		{
			newPage();
			return pack( name , image , isDefault );
		}
		else if( page.disposed )
		{
			try
			{
				Bitmap origBmp = BitmapFactory.decodeFile( page.file.path() );
				page.image = origBmp.copy( Config.ARGB_8888 , true );
				page.disposed = false;
				origBmp.recycle();
			}
			catch( GdxRuntimeException e )
			{
				// TODO: handle exception
				Log.e( "pack" , " file read error:" + e.getMessage() );
			}
		}
		node.leaveName = name;
		rect = new Rectangle( node.rect );
		rect.width -= borderPixels;
		rect.height -= borderPixels;
		borderPixels >>= 1;
		rect.x += borderPixels;
		rect.y += borderPixels;
		page.rects.put( name , new BitmapRect( rect , isDefault ) );
		drawBitmap( page.image , image , (int)rect.x , (int)rect.y );
		// not terribly efficient (as the rest of the code) but will do :p
		if( duplicateBorder )
		{
			drawBitmap( page.image , image , (int)rect.x , (int)rect.y - 1 , (int)rect.x + (int)rect.width , (int)rect.y , 0 , 0 , image.getWidth() , 1 );
			drawBitmap(
					page.image ,
					image ,
					(int)rect.x ,
					(int)rect.y + (int)rect.height ,
					(int)rect.x + (int)rect.width ,
					(int)rect.y + (int)rect.height + 1 ,
					0 ,
					image.getHeight() - 1 ,
					image.getWidth() ,
					image.getHeight() );
			drawBitmap( page.image , image , (int)rect.x - 1 , (int)rect.y , (int)rect.x , (int)rect.y + (int)rect.height , 0 , 0 , 1 , image.getHeight() );
			drawBitmap(
					page.image ,
					image ,
					(int)rect.x + (int)rect.width ,
					(int)rect.y ,
					(int)rect.x + (int)rect.width + 1 ,
					(int)rect.y + (int)rect.height ,
					image.getWidth() - 1 ,
					0 ,
					image.getWidth() ,
					image.getHeight() );
			drawBitmap( page.image , image , (int)rect.x - 1 , (int)rect.y - 1 , (int)rect.x , (int)rect.y , 0 , 0 , 1 , 1 );
			drawBitmap( page.image , image , (int)rect.x + (int)rect.width , (int)rect.y - 1 , (int)rect.x + (int)rect.width + 1 , (int)rect.y , image.getWidth() - 1 , 0 , image.getWidth() , 1 );
			drawBitmap( page.image , image , (int)rect.x - 1 , (int)rect.y + (int)rect.height , (int)rect.x , (int)rect.y + (int)rect.height + 1 , 0 , image.getHeight() - 1 , 1 , image.getHeight() );
			drawBitmap(
					page.image ,
					image ,
					(int)rect.x + (int)rect.width ,
					(int)rect.y + (int)rect.height ,
					(int)rect.x + (int)rect.width + 1 ,
					(int)rect.y + (int)rect.height + 1 ,
					image.getWidth() - 1 ,
					image.getHeight() - 1 ,
					image.getWidth() ,
					image.getHeight() );
		}
		page.addedRects.add( name );
		image.recycle();
		return rect;
	}
	
	private void newPage()
	{
		Page page = new Page();
		if( enablePack )
		{
			pageWidth = (int)hNum * ( cellWidth + 2 * padding );
			pageHeight = (int)vNum * ( cellHeight + 2 * padding );
			page.image = Bitmap.createBitmap( pageWidth , pageHeight , Config.ARGB_8888 );
			page.root = new Node( 0 , 0 , pageWidth , pageHeight , null , null , null );
			hNum -= 2;
			vNum -= 2;
			if( hNum < minHNum )
				hNum = minHNum;
			if( vNum < minVNum )
				vNum = minVNum;
			Log.v( "pack" , " pack size:" + pageWidth + "*" + pageHeight );
		}
		page.rects = new OrderedMap<String , BitmapRect>();
		pages.add( page );
		curPage = page;
	}
	
	private void newPage(
			int width ,
			int height )
	{
		Page page = new Page();
		pageWidth = width;
		pageHeight = height;
		page.image = Bitmap.createBitmap( pageWidth , pageHeight , Config.ARGB_8888 );
		page.root = new Node( 0 , 0 , pageWidth , pageHeight , null , null , null );
		page.rects = new OrderedMap<String , BitmapRect>();
		pages.add( page );
		curPage = page;
		Log.v( "pack" , " pack size:" + pageWidth + "*" + pageHeight );
	}
	
	private Node insert(
			Node node ,
			Rectangle rect )
	{
		if( node.leaveName == null && node.leftChild != null && node.rightChild != null )
		{
			Node newNode = null;
			newNode = insert( node.leftChild , rect );
			if( newNode == null )
				newNode = insert( node.rightChild , rect );
			return newNode;
		}
		else
		{
			if( node.leaveName != null )
				return null;
			if( node.rect.width == rect.width && node.rect.height == rect.height )
				return node;
			if( node.rect.width < rect.width || node.rect.height < rect.height )
				return null;
			node.leftChild = new Node();
			node.rightChild = new Node();
			int deltaWidth = (int)node.rect.width - (int)rect.width;
			int deltaHeight = (int)node.rect.height - (int)rect.height;
			if( deltaWidth > deltaHeight )
			{
				node.leftChild.rect.x = node.rect.x;
				node.leftChild.rect.y = node.rect.y;
				node.leftChild.rect.width = rect.width;
				node.leftChild.rect.height = node.rect.height;
				node.rightChild.rect.x = node.rect.x + rect.width;
				node.rightChild.rect.y = node.rect.y;
				node.rightChild.rect.width = node.rect.width - rect.width;
				node.rightChild.rect.height = node.rect.height;
			}
			else
			{
				node.leftChild.rect.x = node.rect.x;
				node.leftChild.rect.y = node.rect.y;
				node.leftChild.rect.width = node.rect.width;
				node.leftChild.rect.height = rect.height;
				node.rightChild.rect.x = node.rect.x;
				node.rightChild.rect.y = node.rect.y + rect.height;
				node.rightChild.rect.width = node.rect.width;
				node.rightChild.rect.height = node.rect.height - rect.height;
			}
			return insert( node.leftChild , rect );
		}
	}
	
	public void setTextureLineFilter(
			boolean bLineFilter )
	{
		if( !enablePack )
		{
			final Page page = curPage;
			BitmapTexture texture = null;
			Iterator<BitmapRect> ite = page.rects.values().iterator();
			while( ite.hasNext() )
			{
				texture = ite.next().texture;
				if( texture != null )
				{
					if( bLineFilter )
					{
						texture.changeFilter( TextureFilter.Linear , TextureFilter.Linear );
					}
					else
					{
						texture.changeFilter( defaultFilter , defaultMagFilter );
					}
				}
			}
		}
		else
		{
			for( int j = 0 ; j < pages.size ; j++ )
			{
				Page page = pages.get( j );
				if( page.texture != null )
				{
					if( bLineFilter )
					{
						page.texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					}
					else
					{
						page.texture.setFilter( defaultFilter , defaultMagFilter );
					}
				}
			}
		}
	}
	
	/** @return the {@link Page} instances created so far. This method is not thread safe! */
	public Array<Page> getPages()
	{
		return pages;
	}
	
	/**
	 * @param name the name of the image
	 * @return the rectangle for the image in the page it's stored in or null
	 */
	public synchronized BitmapRect getRect(
			String name )
	{
		for( Page page : pages )
		{
			BitmapRect rect = page.rects.get( name );
			if( rect != null )
				return rect;
		}
		return null;
	}
	
	/**
	 * @param name the name of the image
	 * @return the page the image is stored in or null
	 */
	public synchronized Page getPage(
			String name )
	{
		for( Page page : pages )
		{
			BitmapRect rect = page.rects.get( name );
			if( rect != null )
				return page;
		}
		return null;
	}
	
	public synchronized void dispose()
	{
		for( Page page : pages )
		{
			page.image.recycle();
		}
		disposed = true;
	}
	
	private static final IntBuffer buffer = BufferUtils.newIntBuffer( 1 );
	
	public synchronized void updateTextureAtlas(
			TextureAtlas atlas ,
			TextureFilter minFilter ,
			TextureFilter magFilter )
	{
		int threadId = android.os.Process.myTid();
		if( !enablePack )
		{
			final Page page = curPage;
			if( page.addedRects.size > 0 && ( threadId != mainThreadId ) )
			{
				Iterator<String> ite = page.addedRects.iterator();
				while( ite.hasNext() )
				{
					String name = ite.next();
					BitmapRect rect = page.rects.get( name );
					if( rect.bitmap != null && !rect.bitmap.isRecycled() )
					{
						TextureRegion region = atlas.findRegion( name + ( ( rect.isDefault ) ? "default" : "" ) );
						BitmapTexture texture = null;
						if( region == null )
						{
							texture = new BitmapTexture( rect.bitmap );
							region = new TextureRegion( texture );
							atlas.addRegion( name + ( ( rect.isDefault ) ? "default" : "" ) , region );
						}
						else
						{
							texture = (BitmapTexture)region.getTexture();
							if( texture.getTextureObjectHandle() == 0 )
							{
								texture = new BitmapTexture( rect.bitmap );
								region.setTexture( texture );
							}
							else
								texture.changeBitmap( rect.bitmap );
							//Log.i("theme", "updateTextureAtlas:"+name+((rect.isDefault)?"default":""));
						}
						rect.bitmap.recycle();
						rect.bitmap = null;
						rect.texture = texture;
					}
				}
				page.addedRects.clear();
			}
			return;
		}
		for( int j = 0 ; j < pages.size ; j++ )
		{
			boolean writeException = false;
			final Page page = pages.get( j );
			if( page.texture == null && ( threadId != mainThreadId ) )
			{
				if( page.rects.size != 0 )
				{
					if( page.disposed )
					{
						long time = System.currentTimeMillis();
						Log.v( "pack" , "disposed!read from file" );
						Bitmap origBmp = BitmapFactory.decodeFile( page.file.path() );
						page.image = origBmp.copy( Config.ARGB_8888 , true );
						page.disposed = false;
						origBmp.recycle();
						Log.v( "pack" , "read time=" + ( System.currentTimeMillis() - time ) );
					}
					if( !page.disposed )
						page.texture = new BitmapTexture( page.image );
					page.texture.setFilter( defaultFilter , defaultMagFilter );
					for( String name : page.addedRects )
					{
						BitmapRect bitmapRect = page.rects.get( name );
						if( bitmapRect == null )
							continue;
						Rectangle rect = bitmapRect.rect;
						TextureRegion region = new TextureRegion( page.texture , (int)rect.x , (int)rect.y , (int)rect.width , (int)rect.height );
						atlas.addRegion( name + ( ( page.rects.get( name ).isDefault ) ? "default" : "" ) , region );
					}
					page.addedRects.clear();
				}
			}
			else
			{
				if( page.addedRects.size > 0 && ( threadId != mainThreadId ) )
				{
					if( page.disposed )
					{
						long time = System.currentTimeMillis();
						Log.v( "pack" , "disposed!read from file" );
						Bitmap origBmp = BitmapFactory.decodeFile( page.file.path() );
						page.image = origBmp.copy( Config.ARGB_8888 , true );
						page.disposed = false;
						origBmp.recycle();
						Log.v( "pack" , "read time=" + ( System.currentTimeMillis() - time ) );
					}
					long time = System.currentTimeMillis();
					buffer.put( 0 , page.texture.getTextureObjectHandle() );
					Gdx.gl.glDeleteTextures( 1 , buffer );
					( (BitmapTextureData)page.texture.getTextureData() ).setBitmap( page.image );
					page.texture.reload();
					page.texture.setFilter( defaultFilter , defaultMagFilter );
					for( String name : page.addedRects )
					{
						BitmapRect bitmapRect = page.rects.get( name );
						if( bitmapRect == null )
							continue;
						Rectangle rect = bitmapRect.rect;
						TextureRegion region = new TextureRegion( page.texture , (int)rect.x , (int)rect.y , (int)rect.width , (int)rect.height );
						atlas.addRegion( name + ( ( page.rects.get( name ).isDefault ) ? "default" : "" ) , region );
					}
					page.addedRects.clear();
				}
			}
			if( !page.disposed )
			{
				if( page != curPage && page.texture != null && page.addedRects.size == 0 )
				{
					page.image.recycle();
					page.image = null;
					page.disposed = true;
					//Log.d("pack", "dispose:"+page.toString());
					continue;
				}
				if( !launcher.hasCancelDialog() || ( threadId != mainThreadId ) )
				{
					writeException = true;
				}
				else
				{
					if( write )
					{
						if( page.file == null )
						{
							page.file = new FileHandle( FILEDIR + "pack" + System.currentTimeMillis() + ".cim" );
						}
						if( page.file != null )
						{
							try
							{
								long time = System.currentTimeMillis();
								page.image.compress( Bitmap.CompressFormat.PNG , 100 , page.file.write( false ) );
								Log.v( "pack" , "write time=" + ( System.currentTimeMillis() - time ) );
							}
							catch( GdxRuntimeException e )
							{
								// TODO: handle exception
								Log.e( "pack" , " file write error:" + e.getMessage() );
								writeException = true;
							}
						}
						else
							writeException = true;
					}
					else
					{
						//SendMsgToAndroid.resetTextureWriteDelay(packDelay);
						writeException = true;
					}
				}
				if( !writeException )
				{
					page.image.recycle();
					page.disposed = true;
				}
			}
		}
	}
	
	//	public void deleteTexture(){
	//		iLoongLauncher.getInstance().postUrgentRunnable(new Runnable(){
	//
	//			@Override
	//			public void run() {
	//				if(iLoongLauncher.getInstance().d3dListener.mPaused){
	//					iLoongLauncher.swapSurface = false;
	//					synchronized(BitmapPacker.this){
	//						for(int x = 0;x < pages.size && x < DELETE_TEXTURE_MAX;x++) {
	//							Page page2 = pages.get(x);
	//							if(page2.texture == null)continue;
	//							if(page2.file == null && page2.disposed)continue;
	//							buffer.put(0, page2.texture.getTextureObjectHandle());
	//							Gdx.gl.glDeleteTextures(1, buffer);
	//							page2.textureDeleted = true;
	//							Log.e("mem", "delete texture:"+page2.texture.getTextureObjectHandle());
	//						}
	//					}
	//				}
	//			}
	//			
	//		});
	//	}
	//	
	//	public void reloadTexture(){
	//		iLoongLauncher.getInstance().postRunnable(new Runnable(){
	//
	//			@Override
	//			public void run() {
	//				if(!iLoongLauncher.getInstance().d3dListener.mPaused){
	//					synchronized(BitmapPacker.this){
	//						for(int x = 0;x < pages.size && x < DELETE_TEXTURE_MAX;x++) {
	//					    	long time = System.currentTimeMillis();
	//							Page page2 = pages.get(x);
	//							if(!page2.textureDeleted)continue;
	//							if(page2.image != null && page2.image.isRecycled() && page2.file != null){
	//								Bitmap origBmp = BitmapFactory.decodeFile(page2.file.path());
	//								page2.image = origBmp.copy(Config.ARGB_8888,true);
	//								origBmp.recycle();
	//							}
	//							if(page2.image != null && !page2.image.isRecycled()){
	//								((BitmapTextureData)page2.texture.getTextureData()).setBitmap(page2.image);
	//								page2.texture.reload();
	//								page2.texture.setFilter(R3D.filter, R3D.Magfilter);
	//								page2.textureDeleted = false;
	//								if(page2.file != null){
	//									page2.image.recycle();
	//									page2.disposed = true;
	//								}
	//							}
	//							Log.e("mem", "reload time="+(System.currentTimeMillis()-time));
	//						}
	//					}
	//				}
	//				iLoongLauncher.swapSurface = true;
	//			}
	//			
	//		});
	//	    
	//	}
	public int getPageWidth()
	{
		return pageWidth;
	}
	
	public int getPageHeight()
	{
		return pageHeight;
	}
	
	public int getPadding()
	{
		return padding;
	}
	
	public boolean duplicateBoarder()
	{
		return duplicateBorder;
	}
}
