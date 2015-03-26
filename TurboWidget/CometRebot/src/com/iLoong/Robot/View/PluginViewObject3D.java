package com.iLoong.Robot.View;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Robot.RobotHelper;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
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
	private boolean mSaveCompressedObj = false;

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
		initCache();
		initObjScale();
		initTexture();
		initMesh();
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
		this.mScaleX = this.mScaleY = this.mScaleZ = ((float) appContext.mWidgetContext
				.getResources().getDisplayMetrics().density / (float) 1.85);
	}

	@SuppressWarnings("unchecked")
	protected void initCache() {
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
				Texture texture = RobotHelper.getThemeTexture(appContext,
						textureName);
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				originalRegion = new TextureRegion(texture);
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

	private Mesh loadMesh() {
		String objPath = getThemeObjPath();
		if (objType == LoadObjType.original) {
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
			if (useMeshCache) {
				Mesh mesh = mMeshCache.get(objName);
				if (mesh == null) {
					mesh = loadCompressedMesh(appContext, objName,
							mMoveOffsetX, mMoveOffsetY, mMoveOffsetZ,
							this.mScaleX, this.mScaleY, this.mScaleZ);
					mMeshCache.put(objName, mesh);
				}
				// 如果使用缓存，返回值必须copy一份mesh，防止修改缓存中内容
				return copyMesh(mesh);
			} else {
				return loadCompressedMesh(appContext, objName, mMoveOffsetX,
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
			stream = RobotHelper.getThemeObjStream(appContext, objName);
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
//		if (appContext.mThemeName == null) {
//			return "iLoong";
//		}
//		String themeName = appContext.mThemeName;
//		if (themeName == null || themeName.length() == 0) {
//			return "iLoong";
//		}
//		if (!themeName.equals("iLoong") && !themeName.equals("female")) {
//			themeName = "iLoong";
//		}
		return "iLoong";
	}

	protected String getThemeTexturePath() {
		return themeName + "/image/" + textureName;
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
		oos.writeInt(verAttr.size());
		for (int i = 0; i < verAttr.size(); i++) {
			VertexAttribute curattr = verAttr.get(i);
			oos.writeInt(curattr.usage);
			oos.writeInt(curattr.numComponents);
			oos.writeUTF(curattr.alias);
		}
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
		Log.v("robot", "robotbot filePath is " + filePath);
		Mesh mesh = null;
		InputStream is = null;
		try {
			is = RobotHelper.getThemeObjStream(appContext, meshObjFileName);

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

		Mesh mesh = null;
		DataInputStream oos = new DataInputStream(stream);
		// 读取属性值
		int verAttr_size = oos.readInt();
		Log.v("robot", "robotbot verAttr_size is " + verAttr_size);
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>(
				verAttr_size);
		for (int i = 0; i < verAttr_size; i++) {
			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),
					oos.readUTF()));
		}

		// 读取顶点数据
		int singleVertexSize = calculateOffsets(attributes) / 4;
		Log.v("loadCompressedMesh", "singleVertexSize:" + singleVertexSize);
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
}
