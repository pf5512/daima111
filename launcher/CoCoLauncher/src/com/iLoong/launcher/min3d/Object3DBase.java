package com.iLoong.launcher.min3d;

import android.opengl.GLES20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class Object3DBase extends View3D {
	protected Faces3D faces;
	protected Vertices3D vertices;
	protected Mesh mesh;
	protected ShaderProgram shader = null;
	private boolean depth_test = false;
	// 背面裁切
	protected boolean cullFace = false;
	
	protected int blendSrcAlpha=770;
	protected final Matrix4 combinedMatrix = new Matrix4();

	/**
	 * Returns a new instance of the default shader used by SpriteBatch for GL2
	 * when no shader is specified.
	 */
	static public ShaderProgram createDefaultShader() {
		String vertexShader = "attribute vec4 "
				+ ShaderProgram.POSITION_ATTRIBUTE
				+ ";\n" //
				+ "attribute vec4 "
				+ ShaderProgram.COLOR_ATTRIBUTE
				+ ";\n" //
				+ "attribute vec2 "
				+ ShaderProgram.TEXCOORD_ATTRIBUTE
				+ "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "uniform vec4 u_color;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = u_color * "
				+ ShaderProgram.COLOR_ATTRIBUTE
				+ ";\n" //
				+ "   v_texCoords = "
				+ ShaderProgram.TEXCOORD_ATTRIBUTE
				+ "0;\n" //
				+ "   gl_Position =  u_projTrans * "
				+ ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
				+ "}";

		// special shader string for samsung GALAXYS4
		String fragmentShader_s4 = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  vec4 l_color = v_color * texture2D(u_texture, v_texCoords);\n" //
				+ "  l_color.x = l_color.x * l_color.w;\n"
				+ "  l_color.y = l_color.y * l_color.w;\n"
				+ "  l_color.z = l_color.z * l_color.w;\n"
				+ "  gl_FragColor = l_color;\n" //
				+ "}";

		String GALAXY_S4_MODEL_DEFAULT_STR = "GT-I9500";
		if (DefaultLayout.s4_modle_name != null  
				&& DefaultLayout.s4_modle_name.length > 0) {
			for (String model_name : DefaultLayout.s4_modle_name) {
				if (model_name != null
						&& model_name.equals(android.os.Build.MODEL)) {
					fragmentShader = fragmentShader_s4;
					break;
				}
			}
		} else {
			if (GALAXY_S4_MODEL_DEFAULT_STR.equals(android.os.Build.MODEL)) {
				fragmentShader = fragmentShader_s4;
			}
		}

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false)
			throw new IllegalArgumentException("couldn't compile shader: "
					+ shader.getLog());
		return shader;
	}

	public Object3DBase(Gdx gdx, String name) {
		this(name);
	}

	public Object3DBase(MainAppContext appContext, String name) {
		this(name);
		// 只为编译通过，不需要对appContext做处理
	}

	public Object3DBase(String name) {
		super(name);
	}

	public void setMesh(int face_num, int vertices_num) {
		faces = new Faces3D(face_num);
		vertices = new Vertices3D(vertices_num);
		mesh = new Mesh(Mesh.VertexDataType.VertexArray, false,
				vertices_num * 2, face_num * 3, new VertexAttribute[] {
						new VertexAttribute(0, 3, "a_position"),
						new VertexAttribute(5, 4, "a_color"),
						new VertexAttribute(3, 2, "a_texCoord0") });

	}

	public void setTexture(Texture texture) {
		region.setRegion(texture);
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
		faces = null;
		vertices = null;

	}

	public void setVertices(float[] meshVertices) {

	}

	public void enableDepthMode(boolean depth_mode) {
		depth_test = depth_mode;
	}

	public void enableCullFace(boolean cullFace){
		this.cullFace=cullFace;
	}
	
	public void setBlendSrcAlpha(int blendSrcAlpha){
		this.blendSrcAlpha=blendSrcAlpha;
	}
	@Override
	public void setColor(float r, float g, float b, float a) {
		// TODO Auto-generated method stub
		color.r = r;
		color.g = g;
		color.b = b;
		color.a = a;
		// if (vertices != null) {
		// vertices.setColor(color);
		// } else {
		// if (mesh != null) {
		// meshVertices = new float[mesh.getNumVertices()
		// * mesh.getVertexSize() / 4];
		// mesh.getVertices(meshVertices);
		// int step = mesh.getVertexSize() / 4;
		// int index = 3;
		// for (int i = 0; i < mesh.getNumVertices(); i++) {
		// meshVertices[index] = color.toFloatBits();
		// index += step;
		// }
		// mesh.setVertices(meshVertices);
		// }
		//
		// }
	}

	Color cur_color = new Color();

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (shader == null) {
			shader = createDefaultShader();
		}
		shader.begin();
		combinedMatrix.set(batch.getProjectionMatrix()).mul(
				batch.getTransformMatrix());
		shader.setUniformMatrix("u_projTrans", combinedMatrix);
		shader.setUniformi("u_texture", 0);
		cur_color.r = color.r;
		cur_color.g = color.g;
		cur_color.b = color.b;
		cur_color.a = color.a;
		cur_color.a *= parentAlpha;
		shader.setUniformf("u_color", cur_color);

		if (depth_test) {
			Gdx.gl.glDepthMask(true);
			Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
			Gdx.gl.glDepthFunc(GL10.GL_LEQUAL);
		}

		if (region.getTexture() != null) {
			region.getTexture().bind();
		}
		if (cullFace) {
			if (Gdx.graphics.isGL20Available()) {
				Gdx.gl.glEnable(GLES20.GL_CULL_FACE);
			}else{
				Gdx.gl.glEnable(GL10.GL_CULL_FACE);
			}
		}
		Gdx.gl.glEnable(GL10.GL_BLEND);

		Gdx.gl.glBlendFunc(blendSrcAlpha, 771);
		if (faces != null)
			mesh.setIndices(faces.getIndices());
		if (vertices != null)
			mesh.setVertices(vertices.getVertices());
		if (Gdx.graphics.isGL20Available()) {
			mesh.render(shader, GL10.GL_TRIANGLES);
		} else {
			mesh.render(GL10.GL_TRIANGLES);
		}
		if (depth_test) {
			Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
			Gdx.gl.glDepthMask(false);
		}
		if (cullFace) {
			if (Gdx.graphics.isGL20Available()) {
				Gdx.gl.glDisable(GLES20.GL_CULL_FACE);
			}else{
				Gdx.gl.glDisable(GL10.GL_CULL_FACE);
			}
		}
		shader.end();

	}

	public void dispose() {
		super.dispose();
		if (this.mesh != null) {
			this.mesh.dispose();
		}
		if (shader != null) {
			shader.dispose();
		}
	}
}
