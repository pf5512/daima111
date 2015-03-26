package com.cooee.widget3D.JewelWeather.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.widget3D.JewelWeather.View.BitmapTexture;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;

public class PluginViewObject3D extends Object3DBase {
	// 全局Context参数，包括从Launcher中传递过来的参数
	protected MainAppContext appContext;

	// 图片缓存器名
	protected String textureCacheName;

	// 主题名称，通过主题名称确定皮肤资源
	protected String themeName;

	// 图片名称
	protected String textureName;

	// 模型名称
	protected String objName;

	// 是否使用图片缓存器
	protected boolean useTextureCache = false;

	// 是否使用模型缓存器
	protected boolean useMeshCache = false;

	// 缓存器是否已经设置
	protected boolean cacheAlreadySet = false;

	// 加载的模型类型，目前分为原始大小模型与压缩过后的模型，压缩过后的模型可以提高读取速度
	
	//weijie_20121128
	//protected LoadObjType objType = LoadObjType.original;
	protected LoadObjType objType = LoadObjType.compressed;
	

	protected TextureRegion originalRegion;

	// 模型坐标系中心需要往X方向移动距离，使模型居中
	private float mMoveOffsetX;

	// 模型坐标系中心需要往Y方向移动距离，使模型居中
	private float mMoveOffsetY;

	// 模型坐标系中心需要往Z方向移动距离，使模型居中
	private float mMoveOffsetZ;

	// obj模型X方向缩放比例
	private float mScaleX = 1f;

	// obj模型X方向缩放比例
	private float mScaleY = 1f;

	// obj模型X方向缩放比例
	private float mScaleZ = 1f;

	// 图片缓存器
	private Cache<String, TextureRegion> mTextureCache;

	// 模型缓存器
	private Cache<String, Mesh> mMeshCache;

	// 是否保存压缩过后的模型
	private boolean mSaveCompressedObj = true;
	//private boolean mSaveCompressedObj = false;

	// 动画
	protected Tween mAnimationTween = null;
	// 刷新
	protected IRefreshRender mRefreshRender;	
	/**
	 * 
	 * @param appContext
	 * @param name
	 * @param region
	 * @param objName
	 */
	
	
	
	
	
	
	public PluginViewObject3D(MainAppContext appContext, String name,
			TextureRegion region, String objName) {
		super(appContext, name);
		originalRegion = region;
		if (region != null) {
			this.region.setRegion(region);
		}
	//	Log.d("weijie", ">>>>PluginViewObject3D,objName="+objName);
		this.objName = objName;
		this.appContext = appContext;
		this.themeName = appContext.mThemeName;
		
		setDepthMode(true);
	}

	/**
	 * 
	 * @param appContext
	 * @param name
	 * @param textureName
	 * @param objName
	 */
	public PluginViewObject3D(MainAppContext appContext, String name,
			String textureName, String objName) {
		super(appContext, name);
		this.textureName = textureName;
		this.objName = objName;
		this.appContext = appContext;
		this.themeName = getThemeName();
		setDepthMode(true);
	}
	

	protected void build() {
//		Log.d("weijie", ">>>>PluginViewObject3D build start");
		initCache();
		
		initObjScale();
		Log.e("memorytest", "textureName="+textureName+" >>Texture start");
		displayMemory(appContext);
		initTexture();
		Log.e("memorytest", "textureName="+textureName+" >>Texture end");
		displayMemory(appContext);
		
		Log.e("memorytest", "objName="+objName+" >>Texture start");
		displayMemory(appContext);
		initMesh();
		Log.e("memorytest", "objName="+objName+" >>Texture end");
		displayMemory(appContext);
//		Log.d("weijie", ">>>>PluginViewObject3D build end");
	}

	protected void setDepthMode(boolean depthMode) {
		enableDepthMode(depthMode);
	}

	protected void setTextureCache(Cache<String, TextureRegion> textureCache) {
		this.mTextureCache = textureCache;
	}

	protected void setMeshCache(Cache<String, Mesh> meshCache) {
		this.mMeshCache = meshCache;
	}

	public void setTextureCacheName(String textureCacheName) {
		this.textureCacheName = textureCacheName;
	}

	protected String mMeshCacheName;

	public void setmMeshCacheName(String meshCacheName) {
		this.mMeshCacheName = meshCacheName;
	}

	protected void setAppContext(MainAppContext appContext) {
		this.appContext = appContext;
	}

	public void setSize(float width, float height) {
		super.setSize(width, height);
		this.setOrigin(width / 2, height / 2);
	}

	protected void setMoveOffset(float moveOffsetX, float moveOffsetY,
			float moveOffsetZ) {
		this.mMoveOffsetX = moveOffsetX;
		this.mMoveOffsetY = moveOffsetY;
		this.mMoveOffsetZ = moveOffsetZ;
	}

	protected void setObjScale(float scaleX, float scaleY, float scaleZ) {
		this.mScaleX = scaleX;
		this.mScaleY = scaleY;
		this.mScaleZ = scaleZ;
	}

	protected void move(float moveOffsetX, float moveOffsetY, float moveOffsetZ) {
		this.x += moveOffsetX;
		this.y += moveOffsetY;
		this.z += moveOffsetZ;
		move((com.iLoong.launcher.UI3DEngine.adapter.Mesh) mesh, moveOffsetX,
				moveOffsetY, moveOffsetZ);
	}

	protected void initObjScale() {
		this.mScaleX = this.mScaleY = this.mScaleZ = (((float) appContext.mWidgetContext
				.getResources().getDisplayMetrics().density / (float) 1.5))*1.1f;
	//	Log.d("weijie", ">>>>PluginViewObject3D initObjScale end mScaleX="+this.mScaleX);
	}

	@SuppressWarnings("unchecked")
	protected void initCache() {
//		Log.d("weijie", ">>>>PluginViewObject3D initCache useTextureCache="+useTextureCache+"useMeshCache="+useMeshCache);
		if (useTextureCache) {
			if (mTextureCache == null && textureCacheName != null) {
				mTextureCache = (Cache<String, TextureRegion>) CacheManager
						.getCache(textureCacheName);
			}
		}
		if (useMeshCache) {
			if (mMeshCache == null && mMeshCacheName != null) {
				mMeshCache = (Cache<String, Mesh>) CacheManager
						.getCache(mMeshCacheName);
			}
		}

	}

	protected void initTexture() {
		if (region == null || region.getTexture() == null) {
			if (useTextureCache) {
				originalRegion = mTextureCache.get(textureName);
			}
			if (originalRegion == null) {
				//weijie 20130305
				if(true)
				{
					AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
					String texturePath = getThemeTexturePath();
					FileHandle fileHandle = gdxFile.internal(getThemeTexturePath());
					Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
					BitmapTexture bt = new BitmapTexture(bm);
					bm.recycle();
					bt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					originalRegion = new TextureRegion(bt);
				}
				else
				{
					Texture texture = new Texture(appContext.gdx,
							new AndroidFiles(appContext.mWidgetContext.getAssets())
									.internal(getThemeTexturePath()));
					texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					originalRegion = new TextureRegion(texture);
				}
			}
			if (useTextureCache) {
				mTextureCache.put(textureName, originalRegion);
			}
			this.region.setRegion(originalRegion);
		}
	}

	protected void initMesh() {
		Mesh mesh = loadMesh();
		this.setMesh(mesh);
	}

	protected Mesh loadMesh() {
		String objPath = getThemeObjPath();
//		Log.d("weijie", ">>>>PluginViewObject3D loadMesh start objPath="+objPath);
		if (objType == LoadObjType.original) {
//			Log.d("weijie", "objType == LoadObjType.original useMeshCache="+useMeshCache);
			if (this.useMeshCache) {
				Mesh mesh = mMeshCache.get(this.objName);
				if (mesh == null) {
					mesh = loadOriginalMesh(appContext, this.objName, objPath,
							mMoveOffsetX, mMoveOffsetY, mMoveOffsetZ,
							this.mScaleX, this.mScaleY, this.mScaleZ);
					mMeshCache.put(objName, mesh);
				}
				// 如果使用缓存，返回值必须copy一份mesh，防止修改缓存中内容
				return copyMesh(mesh);
			} else {
				Mesh mesh = loadOriginalMesh(appContext, this.objName, objPath,
						mMoveOffsetX, mMoveOffsetY, mMoveOffsetZ, this.mScaleX,
						this.mScaleY, this.mScaleZ);
//				Log.d("weijie", ">>>>PluginViewObject3D loadMesh start objPath1="+objPath);
				if(mesh!=null)
//					Log.d("weijie", ">>>>PluginViewObject3D loadMesh start objPath21="+objPath);
				if (mSaveCompressedObj) {
					try {
						saveMesh(mesh, objName, -mMoveOffsetX, -mMoveOffsetY,
								-mMoveOffsetZ, mScaleX, mScaleY, mScaleZ);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return mesh;
			}
		} else if (objType == LoadObjType.compressed) {
	//		Log.d("weijie", "objType == LoadObjType.compressed useMeshCache="+useMeshCache);
			if (useMeshCache) {
				Mesh mesh = mMeshCache.get(objName);
				if (mesh == null) {
					mesh = loadCompressedMesh(appContext, objPath,
							mMoveOffsetX, mMoveOffsetY, mMoveOffsetZ,
							this.mScaleX, this.mScaleY, this.mScaleZ);
					mMeshCache.put(objName, mesh);
				}
				// 如果使用缓存，返回值必须copy一份mesh，防止修改缓存中内容
				return copyMesh(mesh);
			} else {
				return loadCompressedMesh(appContext, objPath, mMoveOffsetX,
						mMoveOffsetY, mMoveOffsetZ, this.mScaleX, this.mScaleY,
						this.mScaleZ);
			}
		} else {
			return null;
		}
	}

	public Mesh loadOriginalMesh(MainAppContext appContext, String objName,
			String objPath, float offsetX, float offsetY, float offsetZ,
			float scaleX, float scaleY, float scaleZ) {
		InputStream stream = null;
		Mesh mesh = null;
		try {
			stream = appContext.mWidgetContext.getAssets().open(objPath);
			mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true,
					offsetX, offsetY, offsetZ, scaleX, scaleY, scaleZ);
			if (mSaveCompressedObj) {
				saveMesh(mesh, objName, -offsetX, -offsetY, -offsetZ, scaleX,
						scaleY, scaleZ);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mesh;
	}

	
	protected String getThemeName() {
		if (appContext.mThemeName == null) {
			return "iLoong";
		}
		String themeName = appContext.mThemeName;
		if (themeName == null || themeName.length() == 0) {
			return "iLoong";
		}
	//zqh deleted ,we should specify a unique name for current widget if we want to put the widget into launcher as an embed plugin.
//		if (!themeName.equals("iLoong")) 
//		{
//			themeName = "iLoong";
//		}
    //zqh deleted end
		return themeName;
	}

	protected String getThemeTexturePath() {
		return themeName + "/image/" + textureName;
	}
	
	protected String getThemeTexturePath(String name) {
		return themeName + "/image/" + name;
	}

	protected String getThemeObjPath() {
		if (objType == LoadObjType.compressed) {
			return themeName + "/obj/" + this.objName;
		} else if (objType == LoadObjType.original) {
			return themeName + "/original_obj/" + objName;
		} else {
			return null;
		}
	}

	public static enum LoadObjType {
		original, compressed
	}

	public boolean is3dRotation() {
		return true;
	}

	public Mesh getMesh(Cache<String, Mesh> meshCache,
			MainAppContext appContext, String objName, float offsetX,
			float offsetY, float offsetZ, float scaleX, float scaleY,
			float scaleZ) {
		String objPath = getThemeObjPath();
		if (objType == LoadObjType.original) {
			if (useMeshCache) {
				Mesh mesh = meshCache.get(objName);
				if (mesh == null) {
					mesh = loadOriginalMesh(appContext, objName, objPath,
							offsetX, offsetY, offsetZ, scaleX, scaleY, scaleZ);
					meshCache.put(objName, mesh);
				}
				// 如果使用缓存，返回值必须copy一份mesh，防止修改缓存中内容
				return copyMesh(mesh);
			} else {
				return loadOriginalMesh(appContext, objName, objPath, offsetX,
						offsetY, offsetZ, scaleX, scaleY, scaleZ);
			}
		} else {
			if (useMeshCache) {
				Mesh mesh = meshCache.get(objName);
				if (mesh == null) {
					mesh = loadCompressedMesh(appContext, objPath, offsetX,
							offsetY, offsetZ, scaleX, scaleY, scaleZ);
					meshCache.put(objName, mesh);
				}
				// 如果使用缓存，返回值必须copy一份mesh，防止修改缓存中内容
				return copyMesh(mesh);
			} else {
				return loadCompressedMesh(appContext, objPath, offsetX,
						offsetY, offsetZ, scaleX, scaleY, scaleZ);
			}
		}
	}

	public void saveMesh(Mesh mesh, String fileName, float offsetX,
			float offsetY, float offsetZ, float scaleX, float scaleY,
			float scaleZ) throws IOException {
		float vertices[] = new float[mesh.getNumVertices()
				* mesh.getVertexSize() / 4];
		short indices[] = new short[mesh.getNumIndices()];
		VertexAttributes verAttr = mesh.getVertexAttributes();
		mesh.getVertices(vertices);
		mesh.getIndices(indices);
		File file = new File("mnt/sdcard/" + fileName);
		file.createNewFile();

		FileOutputStream fileOutStream = new FileOutputStream(file);
		DataOutputStream oos = new DataOutputStream(fileOutStream);

		mesh.getVertexAttributes();
//		Log.v("weijie", "saveMesh verAttr.size=" +verAttr.size());
		oos.writeInt(verAttr.size());
		for (int i = 0; i < verAttr.size(); i++) {
			VertexAttribute curattr = verAttr.get(i);
			oos.writeInt(curattr.usage);
			oos.writeInt(curattr.numComponents);
			oos.writeUTF(curattr.alias);
		}
//		Log.v("weijie", "saveMesh vertices.length=" +vertices.length);
		oos.writeInt(vertices.length);
		int index = 0;
		int vertex_size = verAttr.vertexSize / 4;
		for (int i = 0; i < vertices.length; i++) {
			if (index == 0) {
				oos.writeFloat((vertices[i] + offsetX) / scaleX);
			} else if (index == 1) {
				oos.writeFloat((vertices[i] + offsetY) / scaleY);
			} else if (index == 2) {
				oos.writeFloat((vertices[i] + offsetZ) / scaleZ);
			} else {
				oos.writeFloat(vertices[i]);
			}
			index++;
			if (index == vertex_size) {
				index = 0;
			}
		}
		oos.writeInt(indices.length);
		for (int i = 0; i < indices.length; i++) {
			oos.writeShort(indices[i]);
		}
		oos.close();
		fileOutStream.close();

	}

	public Mesh copyMesh(Mesh originalMesh) {
		Mesh mesh = null;
		// 创建Mesh的副本
		float vertices[] = new float[originalMesh.getNumVertices()
				* originalMesh.getVertexSize() / 4];
		short indices[] = new short[originalMesh.getNumIndices()];
		originalMesh.getVertices(vertices);
		originalMesh.getIndices(indices);
		mesh = new Mesh(appContext.gdx, true, vertices.length, indices.length,
				originalMesh.getVertexAttributes());
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		return mesh;
	}

	public Mesh loadCompressedMesh(MainAppContext appContext,
			String meshObjFileName, float offsetX, float offsetY,
			float offsetZ, float scaleX, float scaleY, float scaleZ) {
		String filePath = meshObjFileName;
		Mesh mesh = null;
		InputStream is = null;
		try {
//			Log.v("weijie","loadCompressedMesh filePath="+filePath );
			is = appContext.mWidgetContext.getAssets().open(filePath);
			//Log.v("weijie","loadCompressedMesh is.read="+is.read() );
			mesh = loadCompressedMesh(appContext.gdx, is, offsetX, offsetY,
					offsetZ, scaleX, scaleY, scaleZ);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return mesh;
	}

	public Mesh loadCompressedMesh(Gdx gdx, InputStream stream, float offsetX,
			float offsetY, float offsetZ, float scaleX, float scaleY,
			float scaleZ) throws IOException {
//		Log.v("weijie", "loadCompressedMesh:enter");
		Mesh mesh = null;
		DataInputStream oos = new DataInputStream(stream);
//		Log.v("weijie", "oos  = "+ oos);
		// 读取属性值
		int verAttr_size = oos.readInt();
//		Log.v("weijie", "verAttr_size  = "+ verAttr_size);
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>(
				verAttr_size);
		for (int i = 0; i < verAttr_size; i++) {
//			Log.v("weijie", "attributes.add  start i= "+ i);
			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),oos.readUTF()));
//			Log.v("weijie", "attributes.add  end i= "+ i);
		}

//		Log.v("weijie", ":::::1:::::");
		// 读取顶点数据
		int singleVertexSize = calculateOffsets(attributes) / 4;
//		Log.v("weijie", "singleVertexSize:" + singleVertexSize);
//		Log.v("loadCompressedMesh", "singleVertexSize:" + singleVertexSize);
		int vertices_size = oos.readInt();
		float vertices[] = new float[vertices_size];
		int index = 0;
		for (int i = 0; i < vertices_size; i++) {
			float vertex_v = oos.readFloat();
			if (index == 0) {
				if (scaleX != 1f) {
					vertex_v = (float) vertex_v * (float) scaleX;
				}
				vertex_v += offsetX;
			} else if (index == 1) {
				if (scaleY != 1f) {
					vertex_v = (float) vertex_v * (float) scaleY;
				}
				vertex_v += offsetY;
			} else if (index == 2) {
				if (scaleZ != 1f) {
					vertex_v = (float) vertex_v * (float) scaleZ;
				}
				vertex_v += offsetZ;
			}
			vertices[i] = vertex_v;
			index++;
			if (index == singleVertexSize) {
				index = 0;
			}
		}

		// 读取索引数据
		int indices_size = oos.readInt();

		short indices[] = new short[indices_size];
		for (int i = 0; i < indices_size; i++) {
			indices[i] = oos.readShort();
		}

		oos.close();
		stream.close();
		mesh = new Mesh(gdx, true, vertices.length, indices.length,
				attributes.toArray(new VertexAttribute[attributes.size()]));
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		return mesh;
	}

	private int calculateOffsets(ArrayList<VertexAttribute> attributes) {
		int count = 0;
		for (int i = 0; i < attributes.size(); i++) {
			VertexAttribute attribute = attributes.get(i);
			attribute.offset = count;
			if (attribute.usage == VertexAttributes.Usage.ColorPacked)
				count += 4;
			else
				count += 4 * attribute.numComponents;
		}
		return count;
	}

	private void move(Mesh mesh, float dx, float dy, float dz) {
		VertexAttribute posAttr = mesh.getVertexAttribute(Usage.Position);
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;

		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices(vertices);

		int idx = offset;

		for (int i = 0; i < numVertices; i++) {
			vertices[idx] += dx;
			vertices[idx + 1] += dy;
			vertices[idx + 2] += dz;
			idx += vertexSize;
		}
		mesh.setVertices(vertices);
	}

	public void dispose() {
		super.dispose();
		if (originalRegion != null && originalRegion.getTexture() != null) {
			originalRegion.getTexture().dispose();
			originalRegion = null;
		}
	}
	
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	//////////////////////////////////////////////////
	public static String getThemeName(MainAppContext appContext) {
		if (appContext.mThemeName.equals("")) {
			return "iLoong";
		} else {
			return appContext.mThemeName;
		}
	}
	public void updateTexture(TextureRegion newRegion) {
		if(newRegion == null)
			return;
		com.badlogic.gdx.graphics.Texture oldTexture = region.getTexture();
		region = newRegion;
//		Log.v("3D_WEATHER", "updateTexture mRegion = " + region);
		this.region.setRegion(newRegion);
		if (oldTexture != null) {
			oldTexture.dispose();
		}
	}
	public void setOpacity(float opacity) {
		color.a = opacity;
	}

	public void activate() {
		setVisible(true);
	if (mAnimationTween == null) {
			startAnimation();
		}
	}

	public void deactivate() {
		setVisible(false);
		stopAnimation();
	}
	public void startAnimation() {
		stopAnimation();
	}

	public void stopAnimation() {
		if (mAnimationTween != null) {
			mAnimationTween.kill();
			mAnimationTween.free();
			mAnimationTween = null;
		}
	}

	public void pauseAnimation() {
		if (mAnimationTween != null) {
			mAnimationTween.pause();
		}
	}

	public void resumeAnimation() {
		if (mAnimationTween != null) {
			mAnimationTween.resume();
		}
	}

	@Override
	public void onEvent(int type, @SuppressWarnings("rawtypes") BaseTween source) {
		if (source.equals(this.mAnimationTween)
				&& type == TweenCallback.COMPLETE) {
			stopAnimation();
		}
	}
	
	public void setRefreshRender(IRefreshRender refreshRender) {
		this.mRefreshRender = refreshRender;
	}

	/**
	 * 调用mRefreshRender.RefreshRender
	 */
	public void RefreshRender() {
		if (mRefreshRender != null) {
			mRefreshRender.RefreshRender();
		}
	}
	
	public void displayMemory(MainAppContext context) 
	{        
		ActivityManager am = (ActivityManager) context.mWidgetContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo   info = new ActivityManager.MemoryInfo();   
		am.getMemoryInfo(info);    
        Log.i("memorytest","系统剩余内存:"+(info.availMem >> 10)+"k");   
  //      Log.i("memorytest","系统是否处于低内存运行："+info.lowMemory);
  //      Log.i("memorytest","当系统剩余内存低于"+info.threshold+"时就看成低内存运行");

    }
}
