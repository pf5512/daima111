package com.iLoong.Clock.View;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Clock.Common.ClockHelper;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

public class ClockBackView extends Object3DBase {
	public static final String WATCH_BACK_OBJ = "watch_back.obj";
	private Cache<String, Mesh> mMeshCache = null;
	private MainAppContext mAppContext;

	public ClockBackView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name);
		this.mAppContext = appContext;
		x = 0;
		y = 0;
		this.region.setRegion(region);
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin(width / 2, height / 2);
	}

	public void setMeshCache(Cache<String, Mesh> cache) {
		this.mMeshCache = cache;
	}

	public void renderMesh(float dx, float dy) {
		Mesh mesh = getMesh();
		// if (WidgetClock.SCALE_SIZE != 1f) {
		// mesh.scale(WidgetClock.SCALE_X, WidgetClock.SCALE_Y,
		// WidgetClock.SCALE_Z);
		// }
		setMesh(mesh);
		enableDepthMode(true);
	}

	public Mesh getMesh() {
		Mesh mesh = ClockHelper.getMesh(mMeshCache, mAppContext,
				WATCH_BACK_OBJ, width / 2, height / 2, 0, WidgetClock.SCALE_X,
				WidgetClock.SCALE_Y, WidgetClock.SCALE_Z);
		return mesh;
	}

	public void move(Mesh mesh, float dx, float dy, float dz) {
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
}
