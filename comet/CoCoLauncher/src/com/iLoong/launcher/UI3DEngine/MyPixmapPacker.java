package com.iLoong.launcher.UI3DEngine;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.iLoong.launcher.Desktop3D.Log;
import android.webkit.WebSettings.ZoomDensity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.PixmapPacker.Page;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.ObjectMap.Keys;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class MyPixmapPacker implements Disposable {
	
	public static final String FILEDIR = iLoongLauncher.getInstance().getApplicationInfo().dataDir + "/";
	private int pack = R3D.getInteger("iloong_pack_tosd");
	private Page curPage;
	private int hNum;
	private int vNum;
	public static long packDelay = 3000;
	public static boolean write = true;
	public static final int minHNum = 3;
	public static final int minVNum = 2;
	public List<Texture> textures = new ArrayList<Texture>();
	
	public static float texture3DSize = 0;
	public static float minPackSize = 0; 
	public static float realPackSize = 0;
	
	static final class Node {
		public Node leftChild;
		public Node rightChild;
		public Rectangle rect;
		public String leaveName;

		public Node (int x, int y, int width, int height, Node leftChild, Node rightChild, String leaveName) {
			this.rect = new Rectangle(x, y, width, height);
			this.leftChild = leftChild;
			this.rightChild = rightChild;
			this.leaveName = leaveName;
		}

		public Node () {
			rect = new Rectangle();
		}
	}
	
	public class Page {
		Node root;
		OrderedMap<String, PixmapRect> rects;
		Pixmap image;
		FileTexture texture;
		Array<String> addedRects = new Array<String>();
		FileHandle file;
		long fileTime;
		boolean disposed = false;
		//public boolean needUpdate = false;
		
		public Pixmap getPixmap() {
			return image;
		}
	}
	
	public class PixmapRect{
		public PixmapRect(Rectangle rect2, boolean isDefault2) {
			rect = rect2;
			isDefault = isDefault2;
		}
		public Rectangle rect;
		public boolean isDefault;
	}

	int pageWidth;
	int pageHeight;
	final Format pageFormat;
	final int padding;
	final boolean duplicateBorder;
	final Array<Page> pages = new Array<Page>();
	//Page currPage;
	boolean disposed;

	/** <p>
	 * Creates a new ImagePacker which will insert all supplied images into a <code>width</code> by <code>height</code> image.
	 * <code>padding</code> specifies the minimum number of pixels to insert between images. <code>border</code> will duplicate the
	 * border pixels of the inserted images to avoid seams when rendering with bi-linear filtering on.
	 * </p>
	 * 
	 * @param width the width of the output image
	 * @param height the height of the output image
	 * @param padding the number of padding pixels
	 * @param duplicateBorder whether to duplicate the border */
	public MyPixmapPacker (int packageNum, Format format, int padding, boolean duplicateBorder) {
		vNum = (int) Math.sqrt(packageNum);
		hNum = packageNum/vNum+1;
		if (vNum < minVNum)
			vNum = minVNum;
		if(hNum < minHNum)
			hNum = minHNum;
		//this.pageWidth = width;
		//this.pageHeight = height;
		this.pageFormat = format;
		this.padding = padding;
		this.duplicateBorder = duplicateBorder;
		newPage();
	}

	/** <p>
	 * Inserts the given {@link Pixmap}. You can later on retrieve the images position in the output image via the supplied name and the
	 * method {@link #getRect(String)}.
	 * </p>
	 * 
	 * @param name the name of the image
	 * @param image the image
	 * @return Rectangle describing the area the pixmap was rendered to or null.
	 * @throws RuntimeException in case the image did not fit due to the page size being to small or providing a duplicate name */
	public synchronized Rectangle pack (String name, Pixmap image, boolean isDefault) {
		//Log.v("pack", "start pack:"+android.os.Process.myTid()+","+name);
		if(disposed) {
			image.dispose();
			return null;
		}
		
		PixmapRect tmpRect = null;
		for(Page tmp: pages) {
			tmpRect = tmp.rects.get(name);
			if(tmpRect != null) {
				if(!tmpRect.isDefault||isDefault){
					image.dispose();
					Log.d("pack","Key with name '" + name + "' is already in map");
					return null;
				}
				//update(name,image,isDefault);
				tmp.rects.remove(name);
				return pack(name,image,isDefault);
			}
		}
		
		int borderPixels = padding + (duplicateBorder ? 1 : 0);
		borderPixels <<= 1;

		if(image.getWidth() >= pageWidth + borderPixels|| image.getHeight() >= pageHeight + borderPixels) {
			//image.dispose();
			//throw new GdxRuntimeException("page size for '" + name + "' to small");
			Page tmp = curPage;
			newPage((image.getWidth() - borderPixels + 15),(image.getHeight()-borderPixels+15));
			Rectangle rect = pack(name,image,isDefault);
			curPage = tmp;
			return rect;
		}

		Rectangle rect = new Rectangle(0, 0, image.getWidth() + borderPixels, image.getHeight() + borderPixels);
		Node node = null;
		Page page = curPage;
		node = insert(page.root,rect);
//			if(node != null){
//				page = pages.get(i);
//				break;
//			}
//		}

		if (node == null) {
			//Log.v("pack", "pack 2");
			newPage();
			return pack(name, image,isDefault);
		}
		else if(page.disposed){
			try {
				long time = System.currentTimeMillis();
				Log.v("pack", "start read");
				page.image = PixmapIO.readCIM(page.file);
				Log.v("pack", "read time="+(System.currentTimeMillis()-time));
				page.disposed = false;
//				PixmapIO.writePNG(new FileHandle(FILEDIR + "packreadcim.png"), currPage.image);
				
			} catch (GdxRuntimeException e) {
				// TODO: handle exception
				Log.e("pack"," file read error:" + e.getMessage());
			}
		}
		//Log.v("pack", "pack 3");

		node.leaveName = name;
		rect = new Rectangle(node.rect);
		rect.width -= borderPixels;
		rect.height -= borderPixels;
		borderPixels >>= 1;
		rect.x += borderPixels;
		rect.y += borderPixels;
		page.rects.put(name, new PixmapRect(rect,isDefault));

		Blending blending = Pixmap.getBlending();
		Pixmap.setBlending(Blending.None);
		
		page.image.drawPixmap(image, (int)rect.x, (int)rect.y);
		Pixmap.setBlending(blending);
//		if(currPage.disposed){
//			this.currPage.disposed = false;
//			PixmapIO.writePNG(new FileHandle(FILEDIR + "packafterdraw.png"), currPage.image);
//		}
		
		// not terribly efficient (as the rest of the code) but will do :p
		if (duplicateBorder) {
			page.image.drawPixmap(image, (int)rect.x, (int)rect.y - 1, (int)rect.x + (int)rect.width, (int)rect.y, 0, 0, image.getWidth(), 1);
			page.image.drawPixmap(image, (int)rect.x, (int)rect.y + (int)rect.height, (int)rect.x + (int)rect.width, (int)rect.y + (int)rect.height + 1, 0,
				image.getHeight() - 1, image.getWidth(), image.getHeight());

			page.image.drawPixmap(image, (int)rect.x - 1, (int)rect.y, (int)rect.x, (int)rect.y + (int)rect.height, 0, 0, 1, image.getHeight());
			page.image.drawPixmap(image, (int)rect.x + (int)rect.width, (int)rect.y, (int)rect.x + (int)rect.width + 1, (int)rect.y + (int)rect.height, image.getWidth() - 1, 0,
				image.getWidth(), image.getHeight());

			page.image.drawPixmap(image, (int)rect.x - 1, (int)rect.y - 1, (int)rect.x, (int)rect.y, 0, 0, 1, 1);
			page.image.drawPixmap(image, (int)rect.x + (int)rect.width, (int)rect.y - 1, (int)rect.x + (int)rect.width + 1, (int)rect.y, image.getWidth() - 1, 0,
				image.getWidth(), 1);

			page.image.drawPixmap(image, (int)rect.x - 1, (int)rect.y + (int)rect.height, (int)rect.x, (int)rect.y + (int)rect.height + 1, 0, image.getHeight() - 1, 1,
				image.getHeight());
			page.image.drawPixmap(image, (int)rect.x + (int)rect.width, (int)rect.y + (int)rect.height, (int)rect.x + (int)rect.width + 1, (int)rect.y + (int)rect.height + 1,
				image.getWidth() - 1, image.getHeight() - 1, image.getWidth(), image.getHeight());
		}
		page.addedRects.add(name);
		//float tmp = ((float)image.getWidth()*image.getHeight()*4)/(float)1000/(float)1000;
		//Log.d("pack", "minPack:"+tmp);
		//MyPixmapPacker.minPackSize += tmp;
		image.dispose();
		return rect;
	}
	
	public synchronized void update (String name, Pixmap image, boolean isDefault) {
		Rectangle rect = null;
		Page page = null;
		for(Page tmp: pages) {
			rect = tmp.rects.get(name).rect;
			if(rect != null) {
				page = tmp;
				break;
			}
		}
		if(rect == null){
			pack(name, image,isDefault);
		}
		else{
			Blending blending = Pixmap.getBlending();
			Pixmap.setBlending(Blending.None);
			//page.image.setColor(0,0,0,0);
			//page.image.drawRectangle((int)rect.x, (int)rect.y,(int)rect.width,(int)rect.height);
			page.image.drawPixmap(image, (int)rect.x, (int)rect.y);
			Pixmap.setBlending(blending);
			if(name.contains("qq")){
				PixmapIO.writePNG(new FileHandle(FILEDIR +System.currentTimeMillis()+"qq.png"),image);
			}
			
			// not terribly efficient (as the rest of the code) but will do :p
			if (duplicateBorder) {
				page.image.drawPixmap(image, (int)rect.x, (int)rect.y - 1, (int)rect.x + (int)rect.width, (int)rect.y, 0, 0, image.getWidth(), 1);
				page.image.drawPixmap(image, (int)rect.x, (int)rect.y + (int)rect.height, (int)rect.x + (int)rect.width, (int)rect.y + (int)rect.height + 1, 0,
					image.getHeight() - 1, image.getWidth(), image.getHeight());

				page.image.drawPixmap(image, (int)rect.x - 1, (int)rect.y, (int)rect.x, (int)rect.y + (int)rect.height, 0, 0, 1, image.getHeight());
				page.image.drawPixmap(image, (int)rect.x + (int)rect.width, (int)rect.y, (int)rect.x + (int)rect.width + 1, (int)rect.y + (int)rect.height, image.getWidth() - 1, 0,
					image.getWidth(), image.getHeight());

				page.image.drawPixmap(image, (int)rect.x - 1, (int)rect.y - 1, (int)rect.x, (int)rect.y, 0, 0, 1, 1);
				page.image.drawPixmap(image, (int)rect.x + (int)rect.width, (int)rect.y - 1, (int)rect.x + (int)rect.width + 1, (int)rect.y, image.getWidth() - 1, 0,
					image.getWidth(), 1);

				page.image.drawPixmap(image, (int)rect.x - 1, (int)rect.y + (int)rect.height, (int)rect.x, (int)rect.y + (int)rect.height + 1, 0, image.getHeight() - 1, 1,
					image.getHeight());
				page.image.drawPixmap(image, (int)rect.x + (int)rect.width, (int)rect.y + (int)rect.height, (int)rect.x + (int)rect.width + 1, (int)rect.y + (int)rect.height + 1,
					image.getWidth() - 1, image.getHeight() - 1, image.getWidth(), image.getHeight());
			}
			image.dispose();
			page.rects.get(name).isDefault = isDefault;
			//page.needUpdate  = true;
		}
	}
	
	private void newPage() {
		Page page = new Page();
		pageWidth = (int) hNum * (R3D.workspace_cell_width+2*padding);
		pageHeight = (int) vNum * (R3D.workspace_cell_height+2*padding);
		page.image = new Pixmap(pageWidth, pageHeight, pageFormat);
		page.root =  new Node(0, 0, pageWidth, pageHeight, null, null, null);
		page.rects = new OrderedMap<String, PixmapRect>();
		pages.add(page);
		curPage = page;
		hNum -= 2;
		vNum -= 2;
		if (hNum < minHNum)
			hNum = minHNum;
		if (vNum < minVNum)
			vNum = minVNum;
		//float tmp = ((float)page.image.getWidth()*page.image.getHeight()*4)/(float)1000/(float)1000;
		//Log.d("pack", "realPack:"+tmp);
		//MyPixmapPacker.realPackSize += tmp;
	}
	
	private void newPage(int width,int height){
		Page page = new Page();
		pageWidth = width;
		pageHeight = height;
		page.image = new Pixmap(pageWidth, pageHeight, pageFormat);
		page.root =  new Node(0, 0, pageWidth, pageHeight, null, null, null);
		page.rects = new OrderedMap<String, PixmapRect>();
		pages.add(page);
		curPage = page;
		Log.v("pack", " pack size:" + pageWidth + "*" + pageHeight);
	}

	private Node insert (Node node, Rectangle rect) {
		if (node.leaveName == null && node.leftChild != null && node.rightChild != null) {
			Node newNode = null;

			newNode = insert(node.leftChild, rect);
			if (newNode == null) newNode = insert(node.rightChild, rect);

			return newNode;
		} else {
			if (node.leaveName != null) return null;

			if (node.rect.width == rect.width && node.rect.height == rect.height) return node;

			if (node.rect.width < rect.width || node.rect.height < rect.height) return null;

			node.leftChild = new Node();
			node.rightChild = new Node();

			int deltaWidth = (int)node.rect.width - (int)rect.width;
			int deltaHeight = (int)node.rect.height - (int)rect.height;

			if (deltaWidth > deltaHeight) {
				node.leftChild.rect.x = node.rect.x;
				node.leftChild.rect.y = node.rect.y;
				node.leftChild.rect.width = rect.width;
				node.leftChild.rect.height = node.rect.height;

				node.rightChild.rect.x = node.rect.x + rect.width;
				node.rightChild.rect.y = node.rect.y;
				node.rightChild.rect.width = node.rect.width - rect.width;
				node.rightChild.rect.height = node.rect.height;
			} else {
				node.leftChild.rect.x = node.rect.x;
				node.leftChild.rect.y = node.rect.y;
				node.leftChild.rect.width = node.rect.width;
				node.leftChild.rect.height = rect.height;

				node.rightChild.rect.x = node.rect.x;
				node.rightChild.rect.y = node.rect.y + rect.height;
				node.rightChild.rect.width = node.rect.width;
				node.rightChild.rect.height = node.rect.height - rect.height;
			}

			return insert(node.leftChild, rect);
		}
	}

	/** @return the {@link Page} instances created so far. This method is not thread safe! */
	public Array<Page> getPages () {
		return pages;
	}
	
	/**
	 * @param name the name of the image
	 * @return the rectangle for the image in the page it's stored in or null
	 */
	public synchronized PixmapRect getRect(String name) {
		for(Page page: pages) {
			PixmapRect rect = page.rects.get(name);
			if(rect != null) return rect;
		}
		return null;
	}
	
	/**
	 * @param name the name of the image
	 * @return the page the image is stored in or null
	 */
	public synchronized Page getPage(String name) {
		for(Page page: pages) {
			PixmapRect rect = page.rects.get(name);
			if(rect != null) return page;
		}
		return null;
	}
	
	/**
	 * Disposes all resources, including Pixmap instances for the pages
	 * created so far. These page Pixmap instances are shared with
	 * any {@link TextureAtlas} generated or updated by either {@link #generateTextureAtlas(TextureFilter, TextureFilter)}
	 * or {@link #updateTextureAtlas(TextureAtlas, TextureFilter, TextureFilter)}. Do
	 * not call this method if you generated or updated a TextureAtlas, instead
	 * dispose the TextureAtlas.
	 */
	public synchronized void dispose() {
		for(Page page: pages) {
			page.image.dispose();
		}
		disposed = true;
	}

	/**
	 * Generates a new {@link TextureAtlas} from the {@link Pixmap} instances inserted so far.
	 * @param minFilter
	 * @param magFilter
	 * @return the TextureAtlas
	 */
	public synchronized TextureAtlas generateTextureAtlas (TextureFilter minFilter, TextureFilter magFilter) {
		TextureAtlas atlas = new TextureAtlas();
		for(Page page: pages) {
			if(page.rects.size != 0) {
				Texture texture = new Texture(new FileTextureData(page.file,page.image, page.image.getFormat(), true)) {
					@Override
					public void dispose () {
						super.dispose();
						
					}
				};
				texture.setFilter(minFilter, magFilter);
				
				Keys<String> names = page.rects.keys();
				for(String name: names) {
					Rectangle rect = page.rects.get(name).rect;
					TextureRegion region = new TextureRegion(texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
					atlas.addRegion(name, region);
				}
			}
		}
		return atlas;
	}

	
	private static final IntBuffer buffer = BufferUtils.newIntBuffer(1);
	
	//有些机器上条件刷新无效，导致launcher stop之后gl线程继续运行，会调用updateTextureAtlas，而stop之后不会上传纹理，导致后面纹理找不到，这里要特殊处理
	/**
	 * Updates the given {@link TextureAtlas}, adding any new {@link Pixmap} instances packed since the last
	 * call to this method. This can be used to insert Pixmap instances on a separate thread via {@link #pack(String, Pixmap)}
	 * and update the TextureAtlas on the rendering thread. This method must be called on the rendering thread.
	 */
	public synchronized void updateTextureAtlas(TextureAtlas atlas, TextureFilter minFilter, TextureFilter magFilter) {
		//Log.e("pack", "update atlas");
		int threadId = android.os.Process.myTid();
		for(int j = 0;j < pages.size;j++) {
			boolean writeException = false;
			Page page = pages.get(j);
			
			if(page.texture == null && (threadId != iLoongLauncher.mainThreadId)) {
				if(page.rects.size != 0) {
					//Log.d("pack", "texture==null:"+page.toString());
					if(page.disposed){
						long time = System.currentTimeMillis();
						Log.v("pack", "disposed!read from file");
						page.image = PixmapIO.readCIM(page.file);
						Log.v("pack", "read time="+(System.currentTimeMillis()-time));
						page.disposed = false;
					}
					if(!page.disposed){
						page.texture = new FileTexture(new ManagedPixmapTextureData(page.image, page.image.getFormat(), false),page);
						//Log.e("pack", "new texture:"+page.toString()+",w,h="+page.image.getWidth()+","+page.image.getHeight());
					}
					page.texture.setFilter(R3D.filter, R3D.Magfilter);
					
					for(String name: page.addedRects) {
						Rectangle rect = page.rects.get(name).rect;
						TextureRegion region = new TextureRegion(page.texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
						atlas.addRegion(name+((page.rects.get(name).isDefault)?"default":""), region);
					}
					page.addedRects.clear();
					
				}
			} else {
				if(page.addedRects.size > 0 && (threadId != iLoongLauncher.mainThreadId)) {
					//Log.e("pack", "start reload texture:"+page.toString());
					//Log.d("pack", "addRect:"+page.toString());
					if(page.disposed){
						long time = System.currentTimeMillis();
						//Log.v("pack", "disposed!read from file");
						page.image = PixmapIO.readCIM(page.file);
						//Log.v("pack", "read time="+(System.currentTimeMillis()-time));
						page.disposed = false;
					}
					long time = System.currentTimeMillis();
					buffer.put(0, page.texture.getTextureObjectHandle());
					Gdx.gl.glDeleteTextures(1, buffer);
					((ManagedPixmapTextureData)page.texture.getTextureData()).setPixmap(page.image);
					page.texture.reload();
					page.texture.setFilter(R3D.filter, R3D.Magfilter);
					//Log.e("pack", "end reload texture:"+page.toString()+",w,h="+page.image.getWidth()+","+page.image.getHeight());
					for(String name: page.addedRects) {
						Rectangle rect = page.rects.get(name).rect;
						TextureRegion region = new TextureRegion(page.texture, (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
						atlas.addRegion(name+((page.rects.get(name).isDefault)?"default":""), region);
					}
					page.addedRects.clear();
				}
			}
			
			if(!page.disposed){
				if(page != curPage && !iLoongLauncher.releaseTexture && page.texture != null && page.addedRects.size == 0){
					page.image.dispose();
					page.disposed = true;
					//Log.d("pack", "dispose:"+page.toString());
					continue;
				}
				if(threadId != iLoongLauncher.mainThreadId){
					writeException = true;
				}
				else{ 
					if(write){
						if(page.file == null){
							//Log.e("pack", "new packfile exist="+new File(FILEDIR + "pack" + j + ".cim").exists());
							page.file = new FileHandle(FILEDIR + "pack" + System.currentTimeMillis() + ".cim");
							//page.file = new FileHandle("/mnt/sdcard/" + "pack" + System.currentTimeMillis() + ".png");
						}
						if(page.file != null){
							try {
								long time = System.currentTimeMillis();
								PixmapIO.writeCIM(page.file, page.image);
								Log.v("pack", "write time="+(System.currentTimeMillis()-time));
								//write = false;
							} catch (GdxRuntimeException e) {
								// TODO: handle exception
								Log.e("pack"," file write error:" + e.getMessage());
								writeException = true;
							}
						}
						else writeException = true;
					}
					else {
						SendMsgToAndroid.resetTextureWriteDelay(packDelay);
						writeException = true;
					}
				}
				if(!writeException){
					page.image.dispose();
					page.disposed = true;
				}
			}
		}
	}
	
	public void releaseTexture(){
		Log.d("pack", "releaseTexture");
		long time = System.currentTimeMillis();
		for(int j = 0;j < pages.size;j++) {
			Page page = pages.get(j);
			buffer.put(0, page.texture.getTextureObjectHandle());
			Gdx.gl.glDeleteTextures(1, buffer);
		}
		Log.d("pack", "release:"+(System.currentTimeMillis()-time));
	}
	
	public void reloadTexture(){
		Log.d("pack", "reloadTexture");
		long time = System.currentTimeMillis();
		for(int j = 0;j < pages.size;j++) {
			Page page = pages.get(j);
			if(page.disposed){
				//Log.v("pack", "disposed!read from file");
				page.image = PixmapIO.readCIM(page.file);
				//Log.v("pack", "read time="+(System.currentTimeMillis()-time));
				page.disposed = false;
			}
			((ManagedPixmapTextureData)page.texture.getTextureData()).setPixmap(page.image);
			page.texture.reload();
			page.texture.setFilter(R3D.filter, R3D.Magfilter);
		}
		Log.d("pack", "reloadTexture:"+(System.currentTimeMillis()-time));
	}
	
	public int getPageWidth () {
		return pageWidth;
	}

	public int getPageHeight () {
		return pageHeight;
	}
	
	public int getPadding() {
		return padding;
	}
	
	public boolean duplicateBoarder() {
		return duplicateBorder;
	}
	public String getTextureDir(){
		return FILEDIR;
	}
}