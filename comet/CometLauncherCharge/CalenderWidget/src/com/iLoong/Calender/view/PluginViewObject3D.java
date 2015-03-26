package com.iLoong.Calender.view;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Widget3D.Theme.ThemeHelper;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;
public class PluginViewObject3D extends Object3DBase
{
	// 全局Context参数，包括从Launcher中传递过来的参数
	protected MainAppContext appContext;
	// 主题名称，通过主题名称确定皮肤资源
	protected String themeName;
	// 图片名称
	protected String textureName;
	// 模型名称
	protected String objName;
	// 加载的模型类型，目前分为原始大小模型与压缩过后的模型，压缩过后的模型可以提高读取速度
	protected TextureRegion originalRegion;
	// 模型坐标系中心需要往X方向移动距离，使模型居中
	private float mMoveOffsetX;
	// 模型坐标系中心需要往Y方向移动距离，使模型居中
	private float mMoveOffsetY;
	// 模型坐标系中心需要往Z方向移动距离，使模型居中
	private float mMoveOffsetZ;
	// obj模型X方向缩放比例
	protected float mScaleX = 1f;
	// obj模型X方向缩放比例
	protected float mScaleY = 1f;
	// obj模型X方向缩放比例
	protected float mScaleZ = 1f;
	public static LoadObjType objType = LoadObjType.compressed;
	/**
	 * 
	 * @param appContext
	 * @param name
	 * @param region
	 * @param objName
	 */
	public PluginViewObject3D(
			MainAppContext appContext ,
			String name ,
			TextureRegion region ,
			String objName )
	{
		super( appContext , name );
		originalRegion = region;
		if( region != null )
		{
			this.region.setRegion( region );
		}
		this.objName = objName;
		this.appContext = appContext;
		this.themeName = appContext.mThemeName;
		setDepthMode( true );
		initArgs();
	}
	
	private void initArgs()
	{
		boolean loadCompressedObj = WidgetThemeManager.getInstance().getBoolean( "load_compressed_obj" , false );
		objType = loadCompressedObj ? LoadObjType.compressed : LoadObjType.original;
	}
	protected void build()
	{
		initObjScale();
		initMesh();
	}
	
	protected void setDepthMode(
			boolean depthMode )
	{
		enableDepthMode( depthMode );
	}
	
	protected void setAppContext(
			MainAppContext appContext )
	{
		this.appContext = appContext;
	}
	
	public void setSize(
			float width ,
			float height )
	{
		super.setSize( width , height );
		this.setOrigin( width / 2 , height / 2 );
	}
	protected void move(
			float moveOffsetX ,
			float moveOffsetY ,
			float moveOffsetZ )
	{
		this.x += moveOffsetX;
		this.y += moveOffsetY;
		this.z += moveOffsetZ;
		move( (com.iLoong.launcher.UI3DEngine.adapter.Mesh)mesh , moveOffsetX , moveOffsetY , moveOffsetZ );
	}
	
	protected void initObjScale()
	{
		this.mScaleX = this.mScaleY = this.mScaleZ = WidgetCalender.scale;
	}
	protected void initMesh()
	{
		Mesh mesh = loadMesh();
		this.setMesh( mesh );
	}
	private boolean mSaveCompressedObj = true;
	private Mesh loadMesh() {
		String objPath = getThemeObjPath();
		if (objType == LoadObjType.original) {
			Mesh mesh = loadOriginalMesh(appContext, this.objName, objPath,
					mMoveOffsetX, mMoveOffsetY, mMoveOffsetZ, this.mScaleX,
					this.mScaleY, this.mScaleZ);
			if (mSaveCompressedObj) {
				try {
					saveMesh(mesh, objName, -mMoveOffsetX, -mMoveOffsetY,
							-mMoveOffsetZ, mScaleX, mScaleY, mScaleZ);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return mesh;
		} else if (objType == LoadObjType.compressed) {
			return loadCompressedMesh(appContext, objName, mMoveOffsetX,
					mMoveOffsetY, mMoveOffsetZ, this.mScaleX, this.mScaleY,
					this.mScaleZ);
		} else {
			return null;
		}
	}
	public Mesh loadCompressedMesh(MainAppContext appContext,
			String meshObjFileName, float offsetX, float offsetY,
			float offsetZ, float scaleX, float scaleY, float scaleZ) {
		Mesh mesh = null;
		InputStream is = null;
		try {
			is = ThemeHelper.getThemeObjStream(appContext, meshObjFileName,
					objType == LoadObjType.original ? true : false);
			mesh = loadCompressedMesh(appContext.gdx, is, offsetX, offsetY,
					offsetZ, scaleX, scaleY, scaleZ);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e1) {
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
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>(
				verAttr_size);
		for (int i = 0; i < verAttr_size; i++) {
			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),
					oos.readUTF()));
		}

		// 读取顶点数据
		int singleVertexSize = calculateOffsets(attributes) / 4;
		int vertices_size = oos.readInt();
		float vertices[] = new float[vertices_size];
		byte[] buf = new byte[2048];
		int length = buf.length;
		int readCount = vertices_size * 4 / length;
		if (readCount <= 0)
			length = vertices_size * 4;
		int read = 0;
		int num = -1;
		int pos = 0;
		int tmp = 0;
		int tmp2 = 0;
		while (read < readCount) {
			num = oos.read(buf, 0, length);
			tmp = num / 4;
			for (tmp2 = 0; tmp2 < tmp; tmp2++) {
				vertices[pos] = getFloat(buf, tmp2 * 4);
				pos++;
			}
			read++;
		}
		if (read == readCount) {
			length = vertices_size * 4 - length * readCount;
			num = oos.read(buf, 0, length);
			tmp = num / 4;
			for (tmp2 = 0; tmp2 < tmp; tmp2++) {
				vertices[pos] = getFloat(buf, tmp2 * 4);
				pos++;
			}
		}
		for (int i = 0; i < vertices_size; i += singleVertexSize) {
			vertices[i] = (float) vertices[i] * (float) scaleX;
			vertices[i] += offsetX;
			vertices[i + 1] = (float) vertices[i + 1] * (float) scaleY;
			vertices[i + 1] += offsetY;
			vertices[i + 2] = (float) vertices[i + 2] * (float) scaleZ;
			vertices[i + 2] += offsetZ;
		}
		oos.read(buf, 0, 4);
		int indices_size = getInt(buf, 0);
		short indices[] = new short[indices_size];
		length = buf.length;
		readCount = indices_size * 2 / length;
		if (readCount <= 0)
			length = indices_size * 2;
		read = 0;
		num = -1;
		pos = 0;
		tmp = 0;
		tmp2 = 0;
		while (read <= readCount) {
			num = oos.read(buf, 0, length);
			tmp = num / 2;
			for (tmp2 = 0; tmp2 < tmp; tmp2++) {
				indices[pos] = getShort(buf, tmp2 * 2);
				pos++;
			}
			if (read == readCount - 1) {
				length = indices_size * 2 - length * readCount;
			}
			read++;
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
	public static float getFloat(byte[] b, int offset) {
		// 4 bytes
		int accum = 0;
		for (int shiftBy = 0; shiftBy < 4; shiftBy++) {
			accum |= (b[offset + shiftBy] & 0xff) << (3 - shiftBy) * 8;
		}
		return Float.intBitsToFloat(accum);
	}
	public static int getInt(byte[] b, int offset) {
		// 4 bytes
		int accum = 0;
		for (int shiftBy = 0; shiftBy < 4; shiftBy++) {
			accum |= (b[offset + shiftBy] & 0xff) << (3 - shiftBy) * 8;
		}
		return accum;
	}
	public static short getShort(byte[] b, int offset) {
		// 2 bytes
		short accum = 0;
		for (int shiftBy = 0; shiftBy < 2; shiftBy++) {
			accum |= (b[offset + shiftBy] & 0xff) << (1 - shiftBy) * 8;
		}
		return accum;
	}
	public Mesh loadOriginalMesh(MainAppContext appContext, String objName,
			String objPath, float offsetX, float offsetY, float offsetZ,
			float scaleX, float scaleY, float scaleZ) {
		InputStream stream = null;
		Mesh mesh = null;
		try {
			stream = ThemeHelper.getThemeObjStream(appContext, objName,
					objType == LoadObjType.original ? true : false);
			mesh = (Mesh) ObjLoader.loadObj(appContext.gdx, stream, true,
					offsetX, offsetY, offsetZ, scaleX, scaleY, scaleZ);
			if (mSaveCompressedObj) {
				saveMesh(mesh, objName, -offsetX, -offsetY, -offsetZ, scaleX,
						scaleY, scaleZ);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mesh;
	}
	protected String getThemeName()
	{
		if( appContext.mThemeName == null )
		{
			return "iLoong";
		}
		return "iLoong";
	}
	
	protected String getThemeTexturePath()
	{
		return "theme/widget/cometcalendar/comet/image/" + textureName;
	}
	public static enum LoadObjType
	{
		original , compressed
	}
	
	protected String getThemeObjPath()
	{
		if( objType == LoadObjType.compressed )
		{
//			Log.d( "Calender" , "obj" );
			return themeName + "/obj/" + this.objName;
		}
		else if( objType == LoadObjType.original )
		{
//			Log.d( "Calender" , "original_obj" );
			return themeName + "/original_obj/" + objName;
		}
		else
		{
			return null;
		}
	}
	
	public boolean is3dRotation()
	{
		return true;
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
	private void move(
			Mesh mesh ,
			float dx ,
			float dy ,
			float dz )
	{
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.Position );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		for( int i = 0 ; i < numVertices ; i++ )
		{
			vertices[idx] += dx;
			vertices[idx + 1] += dy;
			vertices[idx + 2] += dz;
			idx += vertexSize;
		}
		mesh.setVertices( vertices );
	}
	
	public void dispose()
	{
		super.dispose();
		if( originalRegion != null && originalRegion.getTexture() != null )
		{
			originalRegion.getTexture().dispose();
			originalRegion = null;
		}
	}
}
