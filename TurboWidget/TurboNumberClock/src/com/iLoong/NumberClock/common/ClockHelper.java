package com.iLoong.NumberClock.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class ClockHelper {

	public static float getSecondHandRotation() {
		Calendar calendar = Calendar.getInstance();
		int currentSecond = calendar.get(Calendar.SECOND);
		float secondRadians = (360 - ((currentSecond * 6) - 90)) % 360;
//		Log.e("**************", "currentSecond:" + currentSecond + " angle:"
//				+ secondRadians);
		return secondRadians;
	}

	public static float getSecondHandRotation(int currentSecond) {
		return (360 - ((currentSecond * 6) - 90)) % 360;
	}

	public static float getMinuteHandRotation() {
		Calendar calendar = Calendar.getInstance();
		int currentMinute = calendar.get(Calendar.MINUTE);

		float minuteRadians = (360 - ((currentMinute * 6) - 90)) % 360;
		return minuteRadians;
	}

	public static float getMinuteHandRotation(int currentMinute) {
		return (360 - ((currentMinute * 6) - 90)) % 360;
	}

	public static float getHourHandRotation() {
		Calendar calendar = Calendar.getInstance();
		int currentMinute = calendar.get(Calendar.MINUTE);
		int currentHour = calendar.get(Calendar.HOUR);
		float hourRadian = (360 - ((currentHour * 30) - 90)) % 360
				- (30 * currentMinute / 60);
		return hourRadian;
	}

	public static float getHourHandRotation(int currentMinute, int currentHour) {
		return (360 - ((currentHour * 30) - 90)) % 360
				- (30 * currentMinute / 60);
	}

	String SDPATH = "mnt/sdcard/";

	public static void saveMesh(Mesh mesh, String fileName, float offsetX,
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

	public static Mesh loadCompressedMesh(Gdx gdx, InputStream stream,
			float offsetX, float offsetY, float offsetZ, float scaleX,
			float scaleY, float scaleZ) throws IOException {
		Mesh mesh = null;

		DataInputStream oos = new DataInputStream(stream);
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>(
				verAttr_size);
		for (int i = 0; i < verAttr_size; i++) {
			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),
					oos.readUTF()));
		}
		int singleVertexSize = calculateOffsets(attributes) / 4;
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

	private static int calculateOffsets(ArrayList<VertexAttribute> attributes) {
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

	public static Mesh loadMesh(Gdx gdx, String fileName) throws IOException {
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
		// mesh = new Mesh(gdx, true, vertices.length, indices.length,
		// attributes.toArray(new VertexAttribute[attributes.size()]));

		VertexAttributes att = new VertexAttributes(
				attributes.toArray(new VertexAttribute[attributes.size()]));
		mesh = new Mesh(gdx, true, vertices.length, indices.length, att);

		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}

	public static Mesh loadCompressedMesh(Gdx gdx, Context context,
			String meshObjFileName, float offsetX, float offsetY,
			float offsetZ, float scaleX, float scaleY, float scaleZ) {
		String filePath = meshObjFileName;
		Mesh mesh = null;
		InputStream is = null;
		try {
			is = context.getAssets().open(filePath);
			mesh = loadCompressedMesh(gdx, is, offsetX, offsetY, offsetZ,
					scaleX, scaleY, scaleZ);
			is.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
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

	public static Mesh copyMesh(Mesh originalMesh, MainAppContext appContext) {
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
		/*
		return themeName;
		*/
		return "iLoong";
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
		/*
		return themeName;
		*/
		return "iLoong";
	}

	public static String getThemeImagePath(String themeName, String imageName) {
/*
		return themeName + "/image/" + imageName;
		*/
		return "iLoong/image/" + imageName;
	}

}
