package com.iLoong.Music.Common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class WidgetHelper {
	public static Mesh copyMesh(Mesh originalMesh, MainAppContext appContext) {
		// 创建Mesh的副本
		float vertices[] = new float[originalMesh.getNumVertices()
				* originalMesh.getVertexSize() / 4];
		short indices[] = new short[originalMesh.getNumIndices()];
		originalMesh.getVertices(vertices);
		originalMesh.getIndices(indices);
		Mesh mesh = new Mesh(appContext.gdx, true, vertices.length,
				indices.length, originalMesh.getVertexAttributes());
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		return mesh;
	}

	public static String findThemeDir(Context widgetContext, String themeName) {
		try {
			boolean foundTheme = false;
			String[] themeArray = widgetContext.getAssets().list("");
			for (String tmpTheme : themeArray) {
				if (tmpTheme.equals(themeName)) {
					foundTheme = true;
					break;
				}
			}
			if (!foundTheme) {
				themeName = "iLoong";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return themeName;
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

	// public static Mesh loadMesh(MainAppContext mainAppContext,
	// String meshObjFileName) {
	// Gdx gdx = mainAppContext.gdx;
	// Context widget_context = mainAppContext.mWidgetContext;
	// AndroidFiles gdxFile = new AndroidFiles(widget_context.getAssets());
	// String filePath = meshObjFileName;
	// Mesh mesh = null;
	// try {
	// FileHandle fileHandle = gdxFile.internal(filePath);
	//
	// if (!fileHandle.exists()) {
	// filePath = "iLoong" + File.separator + meshObjFileName;
	// }
	// InputStream is = null;
	// try {
	// is = RobotHelper.getThemeObjStream(mainAppContext,
	// meshObjFileName);
	// mesh = loadMesh(gdx, is);
	// is.close();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// try {
	// is.close();
	// } catch (IOException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return mesh;
	// }

	// public static Mesh loadCompressedMesh(MainAppContext mainAppContext,
	// String meshObjFileName, float offsetX, float offsetY, float offsetZ) {
	// Gdx gdx = mainAppContext.gdx;
	// Mesh mesh = null;
	// InputStream is = null;
	// try {
	// is = RobotHelper.getThemeObjStream(mainAppContext, meshObjFileName);
	// mesh = loadCompressedMesh(gdx, is, offsetX, offsetY, offsetZ);
	// is.close();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// try {
	// is.close();
	// } catch (IOException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// }
	// return mesh;
	// }

	// public static BitmapTexture getThemeTexture(MainAppContext appContext,
	// String fileName) {
	// InputStream stream = null;
	// BitmapTexture texture = null;
	// if (fileName == null || fileName.isEmpty()) {
	// return null;
	// }
	// String bitmapPath = getLauncherThemeImagePath(
	// appContext.mWidgetContext.getPackageName(),
	// appContext.mThemeName, fileName);
	// stream = WidgetThemeManager.getInstance().getCurrentThemeInputStream(
	// bitmapPath);
	// if (stream != null) {
	// texture = new BitmapTexture(WidgetThemeManager.getInstance()
	// .getBitmap(stream));
	// } else {
	// if (appContext.mWidgetContext.equals(appContext.mContainerContext)) {
	// stream = WidgetThemeManager.getInstance()
	// .getSysteThemeInputStream(bitmapPath);
	// if (stream == null) {
	// bitmapPath = getLauncherThemeImagePath(
	// appContext.mWidgetContext.getPackageName(),
	// "iLoong", fileName);
	// stream = WidgetThemeManager.getInstance()
	// .getCurrentThemeInputStream(bitmapPath);
	// if (stream == null) {
	// stream = WidgetThemeManager.getInstance()
	// .getSysteThemeInputStream(bitmapPath);
	// }
	// }
	// if (stream != null) {
	// texture = new BitmapTexture(WidgetThemeManager
	// .getInstance().getBitmap(stream));
	// }
	// } else {
	// AndroidFiles widgetAsset = new AndroidFiles(
	// appContext.mWidgetContext.getAssets());
	// String filePath = getThemeImagePath(appContext.mThemeName,
	// fileName);
	// FileHandle file = widgetAsset.internal(filePath);
	// if (!file.exists()) {
	// filePath = getLauncherThemeImagePath(
	// appContext.mWidgetContext.getPackageName(),
	// "iLoong", fileName);
	// stream = WidgetThemeManager.getInstance()
	// .getCurrentThemeInputStream(filePath);
	// if (stream == null) {
	// filePath = getThemeImagePath("iLoong", fileName);
	// file = widgetAsset.internal(filePath);
	// if (file != null) {
	// texture = new BitmapTexture(WidgetThemeManager
	// .getInstance().getBitmap(file.read()));
	// }
	// } else {
	// texture = new BitmapTexture(WidgetThemeManager
	// .getInstance().getBitmap(stream));
	// }
	// } else {
	// texture = new BitmapTexture(WidgetThemeManager
	// .getInstance().getBitmap(file.read()));
	// }
	// }
	// }
	// return texture;
	// }

	public static String getWidgetsResPathFromLauncherThemeMgr(
			String widgetName, String widget_resPath) {

		return "theme/widget/" + widgetName + "/" + widget_resPath;
	}

	public static Mesh loadCompressedMesh(Gdx gdx, InputStream stream,
			float offsetX, float offsetY, float offsetZ) throws IOException {
		Mesh mesh = null;

		DataInputStream oos = new DataInputStream(stream);
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>(
				verAttr_size);
		for (int i = 0; i < verAttr_size; i++) {
			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),
					oos.readUTF()));
		}

		int vertices_size = oos.readInt();
		float vertices[] = new float[vertices_size];
		int index = 0;
		int vertex_size = 6;
		for (int i = 0; i < vertices_size; i++) {
			vertices[i] = oos.readFloat();
			if (index == 0) {
				vertices[i] += offsetX;
			} else if (index == 1) {
				vertices[i] += offsetY;
			} else if (index == 2) {
				vertices[i] += offsetZ;
			}
			index++;
			if (index == vertex_size) {
				index = 0;
			}
		}
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

	public static Mesh loadMesh(Gdx gdx, InputStream stream) throws IOException {
		Mesh mesh = null;
		Log.e("robot", "loadMesh");
		DataInputStream oos = new DataInputStream(stream);
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>(
				verAttr_size);
		for (int i = 0; i < verAttr_size; i++) {
			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),
					oos.readUTF()));
		}

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
		int indices_size = oos.readInt();
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

	public static Mesh loadMesh(String fileName, MainAppContext appContext)
			throws IOException {
		Mesh mesh = null;
		File file = new File("/mnt/sdcard/" + fileName);
		file.createNewFile();

		FileInputStream fileInStream = new FileInputStream(file);

		DataInputStream oos = new DataInputStream(fileInStream);
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>();
		for (int i = 0; i < verAttr_size; i++) {
			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),
					oos.readUTF()));
		}

		int vertices_size = oos.readInt();
		float vertices[] = new float[vertices_size];
		for (int i = 0; i < vertices_size; i++) {
			vertices[i] = oos.readFloat();
		}
		int indices_size = oos.readInt();
		short indices[] = new short[indices_size];
		for (int i = 0; i < indices_size; i++) {
			indices[i] = oos.readShort();
		}

		oos.close();
		fileInStream.close();
		mesh = new Mesh(appContext.gdx, true, vertices.length, indices.length,
				attributes.toArray(new VertexAttribute[attributes.size()]));
		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}

	public static Mesh loadMeshOpt(Gdx gdx, InputStream stream)
			throws IOException {
		Mesh mesh = null;

		DataInputStream oos = new DataInputStream(stream);
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>(
				verAttr_size);
		for (int i = 0; i < verAttr_size; i++) {
			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),
					oos.readUTF()));
		}

		int vertices_size = oos.readInt();
		float vertices[] = new float[vertices_size];
		for (int i = 0; i < vertices_size; i++) {
			vertices[i] = oos.readFloat();
		}
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

	public static void move(Mesh mesh, float dx, float dy, float dz) {
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

	public static void saveMesh(Mesh mesh, String fileName) throws IOException {
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
		for (int i = 0; i < vertices.length; i++) {
			oos.writeFloat(vertices[i]);
		}
		oos.writeInt(indices.length);
		for (int i = 0; i < indices.length; i++) {
			oos.writeShort(indices[i]);
		}
		oos.close();
		fileOutStream.close();

	}

	public static String transformThemeName(Context widgetContext,
			ComponentName themeComponentName) {
		if (themeComponentName == null) {
			return "iLoong";
		}
		String themeName = themeComponentName.getPackageName().substring(
				themeComponentName.getPackageName().lastIndexOf(".") + 1);
		if (themeName == null || themeName.length() == 0) {
			return "iLoong";
		}
		themeName = findThemeDir(widgetContext, themeName);
		return themeName;
	}
}
