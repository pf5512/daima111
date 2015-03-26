package com.iLoong.launcher.cling;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.min3d.Object3DBase;
import com.iLoong.launcher.theme.ThemeManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.SystemClock;
import com.iLoong.launcher.Desktop3D.Log;

public class FlipView extends Object3DBase {

	protected static int VIEW_WIDTH = 720;	
	protected static int VIEW_HEIGHT = 1280;
		
	private static final String TAG = new String("FlipView");
	
	private static final float GL_VIEW_ANGLE = 35.0f;			
	private static final float GL_VIEW_DISTANCE = 5.0f;			
	private static final float GL_VIEW_SHAPE_FACTOR = (float)Math.tan(Math.toRadians(GL_VIEW_ANGLE/2));
	
	private float VIEW_RATIO = (float)VIEW_WIDTH / (float)VIEW_HEIGHT;
	private float GL_HEIGHT = GL_VIEW_DISTANCE * GL_VIEW_SHAPE_FACTOR * 2;	
	private float GL_WIDTH = GL_HEIGHT * VIEW_RATIO;	
	private CoordinateTranslater coordTranslater;
	private FlipControl flipCtrl;
	private AutoFlip autoFlip;
	
	private TextureRegion[] textures;
	private TextureRegion flipBg;
//	private TextureRegion shadow;
	//private int[] textures;
	private TextureRegion textureCurrentPage;
	private TextureRegion textureNextPage;	
	private int curIndex = 0;
	private int bmpNum = 0;
	
	private float[] spriteVertices;
	private short[] spriteIndices;
	
	private Vector2 dragVector = new Vector2(-1, 0);
	private Vector2 dragPosition = new Vector2(VIEW_WIDTH, VIEW_HEIGHT);
	
	float lightPosition[] = { 0.0f, 0.0f, 5.0f, 1.0f };
	float bgTexCoor[] = {0,0,0,1,1,0,1,1};
	
	public float lastTurnToLeftPosition = 0;
	public int turnToLeftNum = 0;
	
	public FlipView(String name){
		super(name);
		init();
	}
	
	private void init(){
		VIEW_WIDTH = Utils3D.getScreenWidth();
		VIEW_HEIGHT = Utils3D.getScreenHeight();
		coordTranslater = new CoordinateTranslater();
		flipCtrl = new FlipControl();
		autoFlip = new AutoFlip();
		autoFlip.start();
		
		//updateUIParams();
		textures = new TextureRegion[5];
		
		textures[0] = new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/intro-1.jpg")));
		textures[1] = new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/intro-2.jpg")));
		textures[2] = new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/intro-3.jpg")));
		textures[3] = new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/intro-4.jpg")));
		textures[4] = new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/intro-5.jpg")));
		bmpNum = textures.length;
		
		flipBg = new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/flip-bg.png")));
//		Bitmap shadowBmp = Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(shadowBmp);
//		canvas.drawARGB(127, 0, 0, 0);
		
		spriteVertices = new float[500];
		spriteIndices = new short[500];
		
		VIEW_RATIO = (float)VIEW_WIDTH / (float)VIEW_HEIGHT;
		GL_HEIGHT = VIEW_HEIGHT;//GL_VIEW_DISTANCE * GL_VIEW_SHAPE_FACTOR * 2;	
		GL_WIDTH = VIEW_WIDTH;//GL_HEIGHT * VIEW_RATIO;
		
		//gl.glViewport(0, 0, width, height);
		//gl.glMatrixMode(GL10.GL_PROJECTION);
		//gl.glLoadIdentity();
		//GLU.gluPerspective(gl, GL_VIEW_ANGLE, VIEW_RATIO, 0.1f, 50.0f);
		//gl.glMatrixMode(GL10.GL_MODELVIEW);
		//gl.glLoadIdentity();
		
		coordTranslater.updateConverseParam();
		updateUIParams();
		
		//gl.glEnable(GL10.GL_DEPTH_TEST);
		//gl.glDepthFunc(GL10.GL_LESS);
		//gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//gl.glEnable(GL10.GL_LINE_SMOOTH);
		//gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
		//gl.glEnable(GL10.GL_POINT_SMOOTH);
		//gl.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_NICEST);
		//gl.glEnable(GL10.GL_BLEND);
		//gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		mesh = new Mesh(Mesh.VertexDataType.VertexArray, false,
				200 * 4, 200 * 6, new VertexAttribute[] {
						new VertexAttribute(0, 3, "a_position"),
						new VertexAttribute(5, 4, "a_color"),
						new VertexAttribute(3, 2, "a_texCoord0") });
	}
	
	@Override
	public void disposeTexture() {
//		for(int i = 0;i < 5;i++){
//			if(textures[i].getTexture() != null){
//				textures[i].getTexture().dispose();
//			}
//		}
//		if(flipBg.getTexture() != null){
//			flipBg.getTexture().dispose();
//		}
		super.disposeTexture();
	}

	public void draw(SpriteBatch batch,float parentAlpha){
		Gdx.gl.glDisable(GL10.GL_BLEND);
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL10.GL_LESS);
		if(shader == null)
		{
			shader = createDefaultShader();
		}
//		batch.setShader(shader);
		shader.begin();
		combinedMatrix.set(batch.getProjectionMatrix()).mul(
				batch.getTransformMatrix());
		shader.setUniformMatrix("u_projTrans", combinedMatrix);
		shader.setUniformi("u_texture", 0);
		Color cur_color = new Color(color);
		cur_color.a *= parentAlpha;
		shader.setUniformf("u_color", cur_color);
		
		float color = Color.WHITE.toFloatBits();
		float colorShadow = Color.GRAY.toFloatBits();
		long time = SystemClock.uptimeMillis();
		textureCurrentPage = textures[curIndex];
		if(curIndex < bmpNum -1 )textureNextPage = textures[curIndex+1];
		else {
			textureNextPage = null;
			//Log.e("launcher", "error");
		}
		
		flipCtrl.setDragVector(dragVector.x, dragVector.y);
		//flipCtrl.setDragVector(-1, 0);
		flipCtrl.setDragPostion(coordTranslater.transX(dragPosition.x), coordTranslater.transY(dragPosition.y));
		//Log.d("launcher", "dragVector,dragPosition="+flipCtrl.dragVector.x+","+flipCtrl.dragVector.y+","+flipCtrl.dragPosition.x+","+flipCtrl.dragPosition.y);
		flipCtrl.CalculateTurnParams();
		//Log.d("flip", "drawframe dragV,dragP="+dragVector.x+","+dragVector.y+","+dragPosition.x+","+dragPosition.y);	
		if (true) 
		{
			int verticesCount = 0,indicesCount = 0;
			for(int i = 0 ;i < flipCtrl.verticesNumNotTurn;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticesNotTurn[0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticesNotTurn[1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticesNotTurn[2+i*4];
				spriteVertices[3+i*6] = color;
				spriteVertices[4+i*6] = flipCtrl.fTextCoorNotTurn[0+i*2];
				spriteVertices[5+i*6] = flipCtrl.fTextCoorNotTurn[1+i*2];
				verticesCount += 6;
			}
			for(int i = 0;i < (flipCtrl.verticesNumNotTurn-2)*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(textureCurrentPage.getTexture(),indicesCount,verticesCount);
			//gl.glVertexPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbVerticesNotTurn);
			//gl.glNormalPointer(GL10.GL_FLOAT, 0, flipCtrl.fbNormalsNotTurn);
			//gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, flipCtrl.fbTexCoorNotTurn);
			//gl.glBindTexture(GL10.GL_TEXTURE_2D, textureCurrentPage);
			//gl.glDrawElements(GL10.GL_TRIANGLES, (flipCtrl.verticesNumNotTurn-2)*3, 
			//		GL10.GL_UNSIGNED_BYTE, flipCtrl.bbOrder);  	
		}
		
		int iTurn = 0;
		int firstSliceNum = 45<flipCtrl.sliceNumNeedDraw?45:flipCtrl.sliceNumNeedDraw;
		for (iTurn = 0; iTurn < firstSliceNum; iTurn++)
		{
			int verticesCount = 0,indicesCount = 0;
			for(int i = 0 ;i < 4;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticesTurn[iTurn][0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticesTurn[iTurn][1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticesTurn[iTurn][2+i*4];
				spriteVertices[3+i*6] = color;
				spriteVertices[4+i*6] = flipCtrl.fTextureCoorTurn[iTurn][0+i*2];
				spriteVertices[5+i*6] = flipCtrl.fTextureCoorTurn[iTurn][1+i*2];
				verticesCount += 6;
			}
			for(int i = 0;i < 2*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(textureCurrentPage.getTexture(),indicesCount,verticesCount);
//			batch.draw(textureCurrentPage.getTexture(), spriteVertices, 0, verticesCount);
//			gl.glVertexPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbVerticesTurn[iTurn]);
//			gl.glNormalPointer(GL10.GL_FLOAT, 0, flipCtrl.fbNormalsTurn[iTurn]);
//			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, flipCtrl.fbTexCoorTurn[iTurn]);
//			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureCurrentPage);
//			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, flipCtrl.bbOrder);			
		}
//		
		if (false)
		{
			Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
			Gdx.gl.glDepthMask(false);
			//gl.glDisable(GL10.GL_LIGHTING);
			Gdx.gl.glEnable(GL10.GL_BLEND);			
			Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			//gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			int verticesCount = 0,indicesCount = 0;
			for(int i = 0 ;i < 4;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticesShadowCurPageTop[0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticesShadowCurPageTop[1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticesShadowCurPageTop[2+i*4];
				spriteVertices[3+i*6] = colorShadow;
				spriteVertices[4+i*6] = 0;
				spriteVertices[5+i*6] = 0;
				verticesCount += 6;
			}
			for(int i = 0;i < 2*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(textureCurrentPage.getTexture(),indicesCount,verticesCount);
//			gl.glVertexPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbVerticesShadowCurPageTop);
//			gl.glColorPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbShadowColors);	
//			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, flipCtrl.bbOrder);
			
			verticesCount = 0;
			indicesCount = 0;
			for(int i = 0 ;i < 4;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticesShadowCurPageBottom[0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticesShadowCurPageBottom[1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticesShadowCurPageBottom[2+i*4];
				spriteVertices[3+i*6] = colorShadow;
				spriteVertices[4+i*6] = 0;
				spriteVertices[5+i*6] = 0;
				verticesCount += 6;
			}
			for(int i = 0;i < 2*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(textureCurrentPage.getTexture(),indicesCount,verticesCount);
//			gl.glVertexPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbVerticesShadowCurPageBottom);
//			gl.glColorPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbShadowColors);	
//			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, flipCtrl.bbOrder);
			
			Gdx.gl.glDisable(GL10.GL_BLEND);
			Gdx.gl.glDepthMask(true);
			Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
			Gdx.gl.glDepthFunc(GL10.GL_LESS);
			//gl.glEnable(GL10.GL_LIGHTING);
			//gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);			
		}
//		
		if (true && textureNextPage != null) 
		{
			int verticesCount = 0,indicesCount = 0;
			for(int i = 0 ;i < 4;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticesNextPage[0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticesNextPage[1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticesNextPage[2+i*4];
				spriteVertices[3+i*6] = color;
				spriteVertices[4+i*6] = flipCtrl.fTextCoorNextPage[0+i*2];
				spriteVertices[5+i*6] = flipCtrl.fTextCoorNextPage[1+i*2];
				verticesCount += 6;
			}
			for(int i = 0;i < 2*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(textureNextPage.getTexture(),indicesCount,verticesCount);
//			gl.glVertexPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbVerticesNextPage);
//			gl.glNormalPointer(GL10.GL_FLOAT, 0, flipCtrl.fbNormalsNextPage);
//			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, flipCtrl.fbTexCoorNextPage);
//			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureNextPage);
//			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, flipCtrl.bbOrder);			
		}
//		
//		if (true)
//		{
//			// gl.glDisable(GL10.GL_DEPTH_TEST);
//			gl.glDisable(GL10.GL_LIGHTING);
//			gl.glEnable(GL10.GL_BLEND);			
//			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//			
//			gl.glVertexPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbVerticesShadowNextPage);
//			gl.glColorPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbShadowColors);	
//			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, flipCtrl.bbOrder);
//			
//			gl.glDisable(GL10.GL_BLEND);
//			// gl.glEnable(GL10.GL_DEPTH_TEST);
//			gl.glEnable(GL10.GL_LIGHTING);
//			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//		}
//		
		for (; iTurn < flipCtrl.sliceNumNeedDraw; iTurn++) 
		{
			int verticesCount = 0,indicesCount = 0;
			for(int i = 0 ;i < 4;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticesTurn[iTurn][0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticesTurn[iTurn][1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticesTurn[iTurn][2+i*4];
				spriteVertices[3+i*6] = color;
				spriteVertices[4+i*6] = bgTexCoor[0+i*2];
				spriteVertices[5+i*6] = bgTexCoor[1+i*2];
				verticesCount += 6;
			}
			for(int i = 0;i < 2*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(flipBg.getTexture(),indicesCount,verticesCount);
			
			Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
			Gdx.gl.glDepthMask(false);
			Gdx.gl.glEnable(GL10.GL_BLEND);			
			Gdx.gl.glBlendFunc(GL10.GL_DST_ALPHA, GL10.GL_ONE);
			
			verticesCount = 0;
			indicesCount = 0;
			for(int i = 0 ;i < 4;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticesTurn[iTurn][0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticesTurn[iTurn][1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticesTurn[iTurn][2+i*4];
				spriteVertices[3+i*6] = color;
				spriteVertices[4+i*6] = flipCtrl.fTextureCoorTurn[iTurn][0+i*2];
				spriteVertices[5+i*6] = flipCtrl.fTextureCoorTurn[iTurn][1+i*2];
				verticesCount += 6;
			}
			for(int i = 0;i < 2*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(textureCurrentPage.getTexture(),indicesCount,verticesCount);
			Gdx.gl.glDisable(GL10.GL_BLEND);
			Gdx.gl.glDepthMask(true);
			Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
			Gdx.gl.glDepthFunc(GL10.GL_LESS);
//			batch.draw(textureCurrentPage.getTexture(), spriteVertices, 0, verticesCount);
//			gl.glVertexPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbVerticesTurn[iTurn]);
//			gl.glNormalPointer(GL10.GL_FLOAT, 0, flipCtrl.fbNormalsTurn[iTurn]);					
//			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, flipCtrl.fbTexCoorTurn[iTurn]);
//			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureCurrentPage);
//			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, flipCtrl.bbOrder);	
		}
//
		if (flipCtrl.bNeedDrawTurnLeft)
		{	
			int verticesCount = 0,indicesCount = 0;
			for(int i = 0 ;i < 4;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticersTurnLeft[0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticersTurnLeft[1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticersTurnLeft[2+i*4];
				spriteVertices[3+i*6] = color;
				spriteVertices[4+i*6] = bgTexCoor[0+i*2];
				spriteVertices[5+i*6] = bgTexCoor[1+i*2];
				verticesCount += 6;
			}
			for(int i = 0;i < 2*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(flipBg.getTexture(),indicesCount,verticesCount);
			
			Gdx.gl.glDisable(GL10.GL_DEPTH_TEST);
			Gdx.gl.glDepthMask(false);
			Gdx.gl.glEnable(GL10.GL_BLEND);			
			Gdx.gl.glBlendFunc(GL10.GL_DST_ALPHA, GL10.GL_ONE);
			
			verticesCount = 0;
			indicesCount = 0;
			for(int i = 0 ;i < 4;i++){
				spriteVertices[0+i*6] = flipCtrl.fVerticersTurnLeft[0+i*4]+VIEW_WIDTH/2;
				spriteVertices[1+i*6] = flipCtrl.fVerticersTurnLeft[1+i*4]+VIEW_HEIGHT/2;
				spriteVertices[2+i*6] = flipCtrl.fVerticersTurnLeft[2+i*4];
				spriteVertices[3+i*6] = color;
				spriteVertices[4+i*6] = flipCtrl.fTextureCoorTurnLeft[0+i*2];
				spriteVertices[5+i*6] = flipCtrl.fTextureCoorTurnLeft[1+i*2];
				verticesCount += 6;
			}
			for(int i = 0;i < 2*3;i++){
				spriteIndices[i] = flipCtrl.bOrder[i];
				indicesCount++;
			}
			renderMesh(textureCurrentPage.getTexture(),indicesCount,verticesCount);
//			gl.glVertexPointer(4, GL10.GL_FLOAT, 0, flipCtrl.fbVerticesTurnLeft);
//			gl.glNormalPointer(GL10.GL_FLOAT, 0, flipCtrl.fbNormalsTurnLeft);
//			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, flipCtrl.fbTexCoorTurnLeft);
//			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureCurrentPage);
//			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, flipCtrl.bbOrder); 			
		}		
			
		autoFlip.flipUpdated(true);
		
		//gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		//gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		//gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		time = SystemClock.uptimeMillis() - time;
		//gl.glDisable(GL10.GL_TEXTURE_2D);
//		batch.setShader(null);
		shader.end();
		
//		Gdx.gl.glDepthMask(true);
	}
	
	private void renderMesh(Texture texture,int indicesCount,int verticesCount){
		if (texture != null) {
			texture.bind();
		}
		if (spriteIndices != null)
			mesh.setIndices(spriteIndices,0,indicesCount);
		if (spriteVertices != null)
			mesh.setVertices(spriteVertices,0,verticesCount);
		if (Gdx.graphics.isGL20Available()) {
			mesh.render(shader, GL10.GL_TRIANGLES,0,indicesCount);
		} else {
			mesh.render(GL10.GL_TRIANGLES);
		}
	}

	private Point ptDown = new Point();
	private Point ptCurMove = new Point();
	private Point ptLastMove = new Point();
	private Point ptUp = new Point();
	private boolean bDown = false;
	private boolean bTouchLock = false;
	private boolean bMoved = false;
	private boolean bDraged = false;
	private boolean bAutoFliped = false;
	private boolean bToLast = false;
	private boolean bPickedLastPage = false;
	private long bDownTime = 0;
	private final int FLIP_LIMIT = 25;		
	private final int AUTO_FLIP_LIMIT = 40;
	private final int AUTO_FLIP_TIME_LIMIT = 250;

	private void updateUIParams()
	{			
		flipCtrl.updateControlParams();
	}
	
	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		y = Utils3D.getScreenHeight()-y;
		this.requestFocus();
		if (bDown)
		{
			return true;
		}			

		if (bTouchLock)
		{
			return true;			
		}
		
		bDown = true;
		bMoved = false;
		bDraged = false;
		bAutoFliped = false;
		bToLast = false;
		bPickedLastPage = false;
		curAutoPickLastPageStep = 0;
		
		ptDown.set((int)x, (int)y);	
		ptCurMove.set((int)x, (int)y);
		ptLastMove.set((int)x, (int)y);
		
		bDownTime = System.currentTimeMillis();
		
		turnToLeftNum = 0;
		lastTurnToLeftPosition = 0;
		
		return true;
	}
	
	public boolean onTouchDragged(float x,float y,int point)
	{
		y = Utils3D.getScreenHeight()-y;
		if (!bDown)
		{
			return true;			
		}		
		ptCurMove.set((int)x, (int)y);	
		
		if (!bMoved)
		{
			if (PointF.length(ptCurMove.x - ptDown.x, ptCurMove.y - ptDown.y) > FLIP_LIMIT)
			{
				bMoved = true;				
			}			
		}
		
		if (bMoved)
		{
			int dragX = (int)x - (int)ptDown.x;
			int dragY = -((int)y - (int)ptDown.y);	
			int tmpMaxY;
			int tmpMinY;		
			
			if (bAutoFliped)
			{
				
			}
			else
			{
				if (dragX > 0) 
				{		
					bToLast = true;
					tmpMaxY = (int)(-Math.abs(dragX) * DragVectorLimit.getMinLimit(VIEW_HEIGHT/2));
					tmpMinY = (int)(-Math.abs(dragX) * DragVectorLimit.getMaxLimit(VIEW_HEIGHT/2));
					if (dragY > tmpMaxY)
					{
						dragY = tmpMaxY;				
					}
					else if (dragY < tmpMinY)
					{
						dragY = tmpMinY;				
					}
					
					// To Last
					if (ptDown.x > VIEW_WIDTH/6)
					{
						if(ptCurMove.x - ptDown.x > AUTO_FLIP_LIMIT)
						{
							if(curIndex > 0){
								autoLast();
								bAutoFliped = true;
							}
						}
					}
					else
					{
						if (!bPickedLastPage) 
						{
							if(curIndex > 0 || curAutoPickLastPageStep > 0){
								if(curAutoPickLastPageStep == 0)curIndex--;
								autoPickLastPage(-dragX, -dragY, (int)x, (int)y);
								
							}
						}
						else
						{
							updateDragParams(-dragX, -dragY, (int)x, (int)y);		
							bAutoFliped = false;
						}
						
						bDraged = true;
					}		
				}				
				else 
				{
					bToLast = false;
					tmpMaxY = (int)(-Math.abs(dragX) * DragVectorLimit.getMinLimit((int)y));
					tmpMinY = (int)(-Math.abs(dragX) * DragVectorLimit.getMaxLimit((int)y));
					if (dragY > tmpMaxY)
					{
						dragY = tmpMaxY;				
					}
					else if (dragY < tmpMinY)
					{
						dragY = tmpMinY;				
					}
					
					// To Next
					if (ptDown.x < VIEW_WIDTH*5/6)
					{
						if(ptDown.x - ptCurMove.x > AUTO_FLIP_LIMIT)
						{
							if(curIndex < bmpNum - 1){
								autoNext();
								bAutoFliped = true;
							}
							
							Log.e("flip", "autoNext");
						}
					}
					else
					{
						if(curIndex < bmpNum - 1){
							updateDragParams(dragX, dragY, (int)x, (int)y);		
						}
						bAutoFliped = false;
						bDraged = true;
						//Log.d("flip", "dragV,dragP="+dragVector.x+","+dragVector.y+","+dragPosition.x+","+dragPosition.y);
					}			
				}
			}		
		}

		ptLastMove.set((int)x, (int)y);	
		return true;
	}
	
	public boolean inButtonRegion(float x,float y){
		if(x > VIEW_WIDTH*0.2f && x < VIEW_WIDTH*0.8f && y > VIEW_HEIGHT*0.55f && y < VIEW_HEIGHT*0.7f)
			return true;
		return false;
	}
	
	public boolean onTouchUp(float x,float y,int point){
		y = Utils3D.getScreenHeight()-y;
		this.releaseFocus();
		if (!bDown){
			return true;			
		}
		if(System.currentTimeMillis()-bDownTime < AUTO_FLIP_TIME_LIMIT &&
				ptUp.x - ptDown.x < AUTO_FLIP_LIMIT){
			if(curIndex == bmpNum - 1 && inButtonRegion(x,y)){
				launcher.dismissIntroduction();
				return true;
			}
		}
		bDown = false;
		ptUp.set(ptLastMove.x, ptLastMove.y);
		
		if (!bAutoFliped && bDraged) {
			Log.d("launcher", "time="+(System.currentTimeMillis()-bDownTime));
			if(System.currentTimeMillis()-bDownTime < AUTO_FLIP_TIME_LIMIT){
				if(ptLastMove.x > ptDown.x){
					//if(curIndex > 0){
						autoBackward();	
					//	curIndex --;
					//}
					
				}
				else{
					if(curIndex < bmpNum - 1){
						autoForward();	
					}
					
				}
			}
			else if (ptLastMove.x < VIEW_WIDTH/2 && !bToLast){
				if(curIndex < bmpNum - 1){
					autoForward();	
				}	
			}
			else{
				//if(curIndex > 0){
					autoBackward();	
				//	curIndex --;
				//}	
			}			
		}
			
		bMoved = false;
		bAutoFliped = false;
		bToLast = false;
		bDraged = false;
		bPickedLastPage = false;
		
		ptDown.set(0, 0);
		ptCurMove.set(0, 0);
		ptLastMove.set(0, 0);
		//dragVector.set(-1, 0);
		//dragPosition.set(VIEW_WIDTH, VIEW_HEIGHT);
		return true;
	}
	
	private synchronized void updateDragParams(float vX, float vY, float pX, float pY)
	{
		dragVector.set(vX, vY);
		dragPosition.set(pX, pY);
	}
	
	public void autoNext()
	{
		autoFlip.setStartDragVector(-1, 0);
		autoFlip.setStartDragPosition(VIEW_WIDTH, VIEW_HEIGHT/2);
		autoFlip.setEndDragVector(-1, 0);
		autoFlip.setEndDragPosition(-VIEW_WIDTH, VIEW_HEIGHT/2);
		autoFlip.autoStart();		
		autoFlip.setDirection(AutoFlip.DIR_NEXT);
		Log.e("launcher", "view width,view height="+VIEW_WIDTH+","+VIEW_HEIGHT);
	}
	
	public void autoLast()
	{
		autoFlip.setStartDragVector(-1, 0);
		autoFlip.setStartDragPosition(-VIEW_WIDTH, VIEW_HEIGHT/2);
		autoFlip.setEndDragVector(-1, 0);
		autoFlip.setEndDragPosition(VIEW_WIDTH, VIEW_HEIGHT/2);
		autoFlip.autoStart();			
		autoFlip.setDirection(AutoFlip.DIR_LAST);
	}
	
	public void autoForward()
	{
		autoFlip.setStartDragVector(dragVector.x, dragVector.y);
		autoFlip.setStartDragPosition(dragPosition.x, dragPosition.y);
		autoFlip.setEndDragVector(dragVector.x, 0f);
		autoFlip.setEndDragPosition(-VIEW_WIDTH, VIEW_HEIGHT/2);
		autoFlip.autoStart();	
		autoFlip.setDirection(AutoFlip.DIR_FORWORD);
	}
	
	public void autoBackward()
	{
		autoFlip.setStartDragVector(dragVector.x, dragVector.y);
		autoFlip.setStartDragPosition(dragPosition.x, dragPosition.y);
		autoFlip.setEndDragVector(dragVector.x, dragVector.y);
		
		if (dragVector.y > 0)
		{
			autoFlip.setEndDragPosition(VIEW_WIDTH, VIEW_HEIGHT);			
		}
		else if (dragVector.y < 0) 
		{
			autoFlip.setEndDragPosition(VIEW_WIDTH, 0);					
		}
		else 
		{
			autoFlip.setEndDragPosition(VIEW_WIDTH, VIEW_HEIGHT/2);				
		}
		
		autoFlip.autoStart();	
		autoFlip.setDirection(AutoFlip.DIR_BACKWORD);
	}
	
	private final float pickStep[] = {0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
	private final int startPosX = -100;
	private final int startPosY = VIEW_HEIGHT/2;
	private int curAutoPickLastPageStep = 0;
	private iLoongLauncher launcher;
	
	private void autoPickLastPage(int dragX, int dragY, int posX, int posY)
	{
		updateDragParams(
				dragX, 
				dragY, 
				(int)(startPosX + pickStep[curAutoPickLastPageStep]*(posX-startPosX)), 
				(int)(startPosY + pickStep[curAutoPickLastPageStep]*(posY-startPosY)));			
		
		curAutoPickLastPageStep++;		
		if (curAutoPickLastPageStep == pickStep.length)
		{
			curAutoPickLastPageStep = 0;
			bPickedLastPage = true;		
			
		}
	}
	
	/*
	private void swapPage()
	{
		Bitmap bmpTmp = bmpCurrentPage;
		bmpCurrentPage = bmpNextPage;
		bmpNextPage = bmpTmp;		
	}
	*/
	
	private class CoordinateTranslater
	{	
		private float fTransFactorX = 1.0f;
		private float fTransFactorY = 1.0f;
		
		public CoordinateTranslater()
		{
			updateConverseParam();
		}
		
		public void updateConverseParam()
		{
			fTransFactorX = VIEW_WIDTH / GL_WIDTH;
			fTransFactorY = -VIEW_HEIGHT / GL_HEIGHT;		
		}
		
		private float transX(float x)
		{
			return (float)(x/fTransFactorX - GL_WIDTH/2);
		}
		
		private float transY(float y)
		{
			return (float)(y/fTransFactorY + GL_HEIGHT/2);	
		}
	}
	
	// FlipControl
	private class FlipControl
	{
		public ByteBuffer bb = null;
		
		public ByteBuffer bbOrder = null;
		
		private float fShadowColors[];
		public FloatBuffer fbShadowColors;
		
		private float fVerticesShadowNextPage[];
		public FloatBuffer fbVerticesShadowNextPage = null;
		
		private float fVerticesShadowCurPageTop[];
		public FloatBuffer fbVerticesShadowCurPageTop = null;
		
		private float fVerticesShadowCurPageBottom[];
		public FloatBuffer fbVerticesShadowCurPageBottom;
		
		public FloatBuffer fbVerticesNextPage = null;
		public FloatBuffer fbNormalsNextPage = null;
		public FloatBuffer fbTexCoorNextPage = null;		
		
		public FloatBuffer fbVerticesNotTurn = null;
		public FloatBuffer fbNormalsNotTurn = null;
		public FloatBuffer fbTexCoorNotTurn = null;
		public int verticesNumNotTurn = 3;				
		
		public FloatBuffer fbVerticesTurn[];
		public FloatBuffer fbTexCoorTurn[];
		public FloatBuffer fbNormalsTurn[];
		
		public final int SLICE_NUM = 90;				
		public int sliceNumNeedDraw = 0;
		
		public FloatBuffer fbVerticesTurnLeft;
		public FloatBuffer fbTexCoorTurnLeft;
		public FloatBuffer fbNormalsTurnLeft;
		public boolean bNeedDrawTurnLeft = false;
						
		private float SHADOW_WIDTH = 40f;
		private float SHADOW_COLOR_DEFAULT = 0.3f;
		private byte bOrder[];	
		
		private float left;
		private float top;
		private float right;
		private float bottom;
		
		private PointF dragVector = new PointF(-1.0f, 0.0f);		
		private PointF dragPosition =  new PointF(0.0f, 0.0f);
		private PointF ptTurnAxisCenter;							
		private PointF ptTurnAxisBottom;							
		private PointF ptTurnAxisTop;								
		
		private float mapFactorX = 0;								
		private float mapFactorY = 0;
		
		private Line lineTurnAxis;									
		private Line lineTop;
		private Line lineRight;							
		private Line lineBottom;
				
		private float RADIUS = 50f;							
		private float TURN_LEN = (float)(RADIUS * Math.PI);	
			
		private float fVerticesNextPage[];
		private float fNormalsNextPage[];
		private float fTextCoorNextPage[];
		
		private float fVerticesNotTurn[];
		private float fNormalsNotTurn[];
		private float fTextCoorNotTurn[];
		
		private float fVerticesTurn[][];
		private float fTextureCoorTurn[][];
		private float fNormalsTurn[][];
		private float fRotateAnlges[];
		
		private float fVerticersTurnLeft[];
		private float fTextureCoorTurnLeft[];
		private float fNormalsTurnLeft[];
		private float fTurnLeftTextCoorStartX_Top = 0;			
		private float fTurnLeftTextCoorStartY_Top = 0;
		private float fTurnLeftTextCoorStartX_Bottom = 0;			
		private float fTurnLeftTextCoorStartY_Bottom = 0;			
		
		private float nextPosX = 0;						
		private float nextPosY = 0;
		private float nextPosZ = 0;
		
		public FlipControl()
		{
			bOrder = new byte[]{0,1,2, 0,2,3, 0,3,4};			
			bbOrder = ByteBuffer.allocate(bOrder.length);
			bbOrder.order(ByteOrder.nativeOrder());
			
			fShadowColors = new float[4*4];
			bb = ByteBuffer.allocateDirect(fShadowColors.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbShadowColors = bb.asFloatBuffer();
			
			fVerticesShadowCurPageTop = new float[4*4];
			bb = ByteBuffer.allocateDirect(fVerticesShadowCurPageTop.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbVerticesShadowCurPageTop = bb.asFloatBuffer();
			
			fVerticesShadowCurPageBottom = new float[4*4];
			bb = ByteBuffer.allocateDirect(fVerticesShadowCurPageBottom.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbVerticesShadowCurPageBottom = bb.asFloatBuffer();
			
			fVerticesNextPage = new float[4*4];				
			fNormalsNextPage = new float[4*3];
			fTextCoorNextPage = new float[4*2];
			fVerticesShadowNextPage = new float[4*4];
			
			bb = ByteBuffer.allocateDirect(fVerticesNextPage.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbVerticesNextPage = bb.asFloatBuffer();
			
			bb = ByteBuffer.allocateDirect(fNormalsNextPage.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbNormalsNextPage = bb.asFloatBuffer();

			bb = ByteBuffer.allocateDirect(fTextCoorNextPage.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbTexCoorNextPage = bb.asFloatBuffer();

			bb = ByteBuffer.allocateDirect(fVerticesShadowNextPage.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbVerticesShadowNextPage = bb.asFloatBuffer();
			
			fVerticesNotTurn = new float[5*4];
			fNormalsNotTurn = new float[5*3];
			fTextCoorNotTurn = new float[5*2];
			
			bb = ByteBuffer.allocateDirect(fVerticesNotTurn.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbVerticesNotTurn = bb.asFloatBuffer();
			
			bb = ByteBuffer.allocateDirect(fNormalsNotTurn.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbNormalsNotTurn = bb.asFloatBuffer();
			
			bb = ByteBuffer.allocateDirect(fTextCoorNotTurn.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbTexCoorNotTurn = bb.asFloatBuffer();
			
			fVerticesTurn = new float[SLICE_NUM][];
			fTextureCoorTurn = new float[SLICE_NUM][];
			fNormalsTurn = new float[SLICE_NUM][];

			fbVerticesTurn = new FloatBuffer[SLICE_NUM];
			fbTexCoorTurn = new FloatBuffer[SLICE_NUM];
			fbNormalsTurn = new FloatBuffer[SLICE_NUM];
			fRotateAnlges = new float[SLICE_NUM];
			
			for (int i = 0; i < SLICE_NUM; i++) 
			{
				fVerticesTurn[i] = new float[4*4];
				fNormalsTurn[i] = new float[4*3];
				fTextureCoorTurn[i] = new float[4*2];
				
				bb = ByteBuffer.allocateDirect(fVerticesTurn[i].length*4);
				bb.order(ByteOrder.nativeOrder());
				fbVerticesTurn[i] = bb.asFloatBuffer();

				bb = ByteBuffer.allocateDirect(fNormalsTurn[i].length*4);
				bb.order(ByteOrder.nativeOrder());
				fbNormalsTurn[i] = bb.asFloatBuffer();

				bb = ByteBuffer.allocateDirect(fTextureCoorTurn[i].length*4);
				bb.order(ByteOrder.nativeOrder());
				fbTexCoorTurn[i] = bb.asFloatBuffer();
			}
			
			float fStep = (float)(180.0/SLICE_NUM);
			for (int i = 0; i < SLICE_NUM; i++)
			{
				fRotateAnlges[i] = (i+0.5f) * fStep;
			}	
						
			fVerticersTurnLeft = new float[4*4];
			fTextureCoorTurnLeft = new float[4*2];
			fNormalsTurnLeft = new float[4*3];
			
			bb = ByteBuffer.allocateDirect(fVerticersTurnLeft.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbVerticesTurnLeft = bb.asFloatBuffer();

			bb = ByteBuffer.allocateDirect(fTextureCoorTurnLeft.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbTexCoorTurnLeft = bb.asFloatBuffer();

			bb = ByteBuffer.allocateDirect(fNormalsTurnLeft.length*4);
			bb.order(ByteOrder.nativeOrder());
			fbNormalsTurnLeft = bb.asFloatBuffer();
			
			lineTop = new Line();
			lineBottom = new Line();
			lineRight = new Line();
			lineTurnAxis = new Line();
			
			ptTurnAxisBottom = new PointF();
			ptTurnAxisTop = new PointF();
			
			updateControlParams();
		}
		
		public void setDragVector(float vectorX, float vectorY)
		{
			float len = (float)Math.sqrt(vectorX*vectorX + vectorY*vectorY);
			dragVector.set(vectorX/len, vectorY/len);
		}
		
		public void setDragPostion(float posX, float posY)
		{
			dragPosition.set(posX, posY);	
		}
		
		public void CalculateTurnParams()
		{	
			float distance;
			if (MathEx.isFloatZero(dragVector.y) && bMoved && !bAutoFliped 
					&& ((!bToLast && ptDown.x > VIEW_WIDTH*5/6)||(bToLast && ptDown.x < VIEW_WIDTH/6))) 
			{
				//distance = 	right - dragPosition.x;
				//Log.d("launcher", "1 VectorY==0!!!!!!");
				if(dragVector.y > 0)dragVector.y = 0.011f;
				else dragVector.y = -0.011f;
			}
//			if (MathEx.isFloatZero(dragVector.y)) 
//			{
//				Log.d("launcher", "2 VectorY==0!!!!!!");
				distance = 	right - dragPosition.x;
//			}
//			else if (dragVector.y > 0) 
//			{
//				distance = PointF.length(dragPosition.x - right, dragPosition.y - bottom);				
//			}
//			else
//			{
//				distance = PointF.length(dragPosition.x - right, dragPosition.y - top);
//			}
			//Log.d("launcher", "distance="+distance);	
			RADIUS = distance*1.2f;
			//Log.d("launcher", "radius="+RADIUS+"("+GL_WIDTH/4f+")"+"("+GL_WIDTH/16f+")");
			if (RADIUS > GL_WIDTH/6f)
			{
				RADIUS = GL_WIDTH/6f;				
			}
			if (RADIUS < GL_WIDTH/10f)
			{
				RADIUS = GL_WIDTH/10f;				
			}

			TURN_LEN = (float)(RADIUS * Math.PI);
			
			SHADOW_WIDTH = RADIUS * 2.5f;
			if (SHADOW_WIDTH < GL_WIDTH/25) 
			{
				SHADOW_WIDTH = GL_WIDTH/25;				
			}
			
			updateShadowColors(SHADOW_COLOR_DEFAULT);
			
			if (MathEx.isFloatZero(dragVector.y)) 
			{	
				makeTurnAxisLine_TurnToLeft();
				calculateParamsForNotTurn_TurnToLeft();
				calculateParamsForNexPage_TurnToLeft();
				calculateParamsForTurn_TurnToLeft();
				calculateParamsForTurnLeft_TurnToLeft();
				calculateShadowCurPage_ToLeft();
			}
			else if (dragVector.y > 0)
			{
				makeTurnAxisLine_TurnToLeftTop();
				calculateParamsForNotTurn_TurnToLeftTop();
				calculateParamsForNexPage_TurnToLeftTop();
				calculateParamsForTurn_TurnToLeftTop(); 
				calculateParamsForTurnLeft_TurnToLeftTop();
				calculateShadowCurPage_ToLeftTop();
			}
			else if (dragVector.y < 0)
			{
				makeTurnAxisLine_TurnToLeftBottom();
				calculateParamsForNotTurn_TurnToLeftBottom();
				calculateParamsForNextPage_TurnToLeftBottom();
				calculateParamsForTurn_TurnToLeftBottom();
				calculateParamsForTurnLeft_TurnToLeftBottom();
				calculateShadowCurPage_ToLeftBottom();
			}
			
			calculateShadowNextPage();
			updateBuffersForPublic();
		}
		
		private void makeTurnAxisLine_TurnToLeft()
		{
			PointF turnAxisVector = new PointF();
			PointF turnAxisFixPos = new PointF();
			float distanceFromDragPosToRight = right - dragPosition.x;
			
			turnAxisVector.set(dragVector.y, -dragVector.x);
			if (distanceFromDragPosToRight < 0)
			{
				turnAxisFixPos.set(right, top);		
			}
			else if (distanceFromDragPosToRight < TURN_LEN) 
			{
				turnAxisFixPos.set(dragPosition.x, top);
			}
			else
			{
				float tempX = dragPosition.x + (distanceFromDragPosToRight - TURN_LEN)/2;
				turnAxisFixPos.set(tempX, top);
			}
			ptTurnAxisBottom.set(turnAxisFixPos.x, bottom);
			
			if (ptTurnAxisBottom.x < left)
			{
				//Log.d("launcher", "turnAxisFixPos.x="+turnAxisFixPos.x+"("+left+")");
				if(ptTurnAxisBottom.x < left*1.5 && lastTurnToLeftPosition == 0){
					turnToLeftNum = 7;
					lastTurnToLeftPosition = ptTurnAxisBottom.x;
					Log.e("launcher", "<1.5!!!!!!");
				}
				else if(lastTurnToLeftPosition == 0){
					turnToLeftNum = 0;
					lastTurnToLeftPosition = ptTurnAxisBottom.x;
					Log.e("launcher", ">1.5!!!!!!");
				}
				if(lastTurnToLeftPosition != ptTurnAxisBottom.x){
					if(lastTurnToLeftPosition > ptTurnAxisBottom.x){
						turnToLeftNum++;
					}
					else turnToLeftNum--;
					lastTurnToLeftPosition = ptTurnAxisBottom.x;
				}
				float tmp = left-turnToLeftNum*GL_WIDTH/25;
				if(tmp > left)tmp = left;
				turnAxisFixPos.set(tmp, top);
				ptTurnAxisBottom.set(turnAxisFixPos.x, bottom);
				
			}
			
			ptTurnAxisTop.set(turnAxisFixPos.x, top);
			ptTurnAxisCenter = MathEx.getCenter(ptTurnAxisBottom, ptTurnAxisTop);
			
			lineTurnAxis.makeLineByDirVectorAndFixPoint(turnAxisVector, turnAxisFixPos);			
		}
		
		private void calculateParamsForNotTurn_TurnToLeft()
		{
			verticesNumNotTurn = 4;
			
			int countVertices = 0;
			int countTextCoor = 0;
			
			fVerticesNotTurn[countVertices++] = left;
			fVerticesNotTurn[countVertices++] = top;
			fVerticesNotTurn[countVertices++] = 0;
			fVerticesNotTurn[countVertices++] = 1;
			fVerticesNotTurn[countVertices++] = left;
			fVerticesNotTurn[countVertices++] = bottom;
			fVerticesNotTurn[countVertices++] = 0;
			fVerticesNotTurn[countVertices++] = 1;
			fVerticesNotTurn[countVertices++] = ptTurnAxisBottom.x;
			fVerticesNotTurn[countVertices++] = ptTurnAxisBottom.y;
			fVerticesNotTurn[countVertices++] = 0;
			fVerticesNotTurn[countVertices++] = 1;
			fVerticesNotTurn[countVertices++] = ptTurnAxisTop.x;
			fVerticesNotTurn[countVertices++] = ptTurnAxisTop.y;
			fVerticesNotTurn[countVertices++] = 0;
			fVerticesNotTurn[countVertices++] = 1;
			
			float fTextureRight = (ptTurnAxisBottom.x - left)/GL_WIDTH;
			
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 1.0f;
			fTextCoorNotTurn[countTextCoor++] = fTextureRight;
			fTextCoorNotTurn[countTextCoor++] = 1.0f;
			fTextCoorNotTurn[countTextCoor++] = fTextureRight;
			fTextCoorNotTurn[countTextCoor++] = 0;
		
		}
		
		private void calculateParamsForNexPage_TurnToLeft()
		{
			int countVertices = 0;
			int countTextCoor = 0;
			
			fVerticesNextPage[countVertices++] = ptTurnAxisTop.x;
			fVerticesNextPage[countVertices++] = ptTurnAxisTop.y;
			fVerticesNextPage[countVertices++] = 0;
			fVerticesNextPage[countVertices++] = 1;
			fVerticesNextPage[countVertices++] = ptTurnAxisBottom.x;
			fVerticesNextPage[countVertices++] = ptTurnAxisBottom.y;
			fVerticesNextPage[countVertices++] = 0;
			fVerticesNextPage[countVertices++] = 1;
			fVerticesNextPage[countVertices++] = right;
			fVerticesNextPage[countVertices++] = bottom;
			fVerticesNextPage[countVertices++] = 0;
			fVerticesNextPage[countVertices++] = 1;
			fVerticesNextPage[countVertices++] = right;
			fVerticesNextPage[countVertices++] = top;
			fVerticesNextPage[countVertices++] = 0;
			fVerticesNextPage[countVertices++] = 1;						
			
			float fTextureRight = (ptTurnAxisBottom.x - left)/GL_WIDTH;	
			fTextCoorNextPage[countTextCoor++] = fTextureRight;
			fTextCoorNextPage[countTextCoor++] = 0.0f;
			fTextCoorNextPage[countTextCoor++] = fTextureRight;
			fTextCoorNextPage[countTextCoor++] = 1.0f;
			fTextCoorNextPage[countTextCoor++] = 1.0f;
			fTextCoorNextPage[countTextCoor++] = 1.0f;
			fTextCoorNextPage[countTextCoor++] = 1.0f;
			fTextCoorNextPage[countTextCoor++] = 0;
		}
		
		private void calculateParamsForTurn_TurnToLeft()
		{
			float sliceWidth;
			float fThisTextCoorStart;
			float fNextTextCoorStart;
			float sliceTextCoorWidth;
			float sliceTextCoorStart;
			boolean bTextSliceEnd = false;
			
			sliceWidth = TURN_LEN/SLICE_NUM;		
			sliceTextCoorWidth = sliceWidth/GL_WIDTH;
			sliceTextCoorStart = (ptTurnAxisTop.x - left) / GL_WIDTH;
			
			float verticesInit[] = new float[16];
			verticesInit[0] = 0;
			verticesInit[1] = top;	
			verticesInit[2] = 0;	
			verticesInit[3] = 1;	
			verticesInit[4] = 0;
			verticesInit[5] = bottom;			
			verticesInit[6] = 0;	
			verticesInit[7] = 1;	
			verticesInit[8] = sliceWidth;	
			verticesInit[9] = bottom;	
			verticesInit[10] = 0;
			verticesInit[11] = 1;	
			verticesInit[12] = sliceWidth;	
			verticesInit[13] = top;	
			verticesInit[14] = 0;	
			verticesInit[15] = 1;	
			
			sliceNumNeedDraw = 0;
			
			nextPosX = ptTurnAxisCenter.x;
			nextPosY = ptTurnAxisCenter.y;
			nextPosZ = 0;
			
			for (int i = 0; i < SLICE_NUM; i++) 
			{
				fThisTextCoorStart = sliceTextCoorStart + i * sliceTextCoorWidth;
				fNextTextCoorStart = fThisTextCoorStart + sliceTextCoorWidth;
				
				if (fThisTextCoorStart >= 1.0f)
				{
					break;					
				}		
				
				if (fNextTextCoorStart >= 1.0f)
				{
					fNextTextCoorStart = 1.0f;
					verticesInit[8] = verticesInit[12] = 0 + (1-fThisTextCoorStart)/sliceTextCoorWidth * sliceWidth;
					bTextSliceEnd = true;
				}
				
				fTurnLeftTextCoorStartX_Top = fNextTextCoorStart;
				
				fTextureCoorTurn[i][0] = fThisTextCoorStart;
				fTextureCoorTurn[i][1] = 0;
				fTextureCoorTurn[i][2] = fThisTextCoorStart;
				fTextureCoorTurn[i][3] = 1;
				fTextureCoorTurn[i][4] = fNextTextCoorStart;
				fTextureCoorTurn[i][5] = 1;
				fTextureCoorTurn[i][6] = fNextTextCoorStart;
				fTextureCoorTurn[i][7] = 0;
				
				MatrixSW.rotateM(fVerticesTurn[i], 0, verticesInit, 0, -fRotateAnlges[i], 0, 1, 0);
				
				MatrixSW.rotate3M(fNormalsTurn[i], 0, fNormalsNextPage, 0, -fRotateAnlges[i], 0, 1, 0);
				
				MatrixSW.moveM(fVerticesTurn[i], 0, nextPosX , nextPosY, nextPosZ);
				nextPosX = (fVerticesTurn[i][8] + fVerticesTurn[i][12])/2;
				nextPosY = (fVerticesTurn[i][9] + fVerticesTurn[i][13])/2;
				nextPosZ = (fVerticesTurn[i][10] + fVerticesTurn[i][14])/2;
				
				sliceNumNeedDraw++;

				if (bTextSliceEnd)
				{
					break;					
				}
			}
		}
		
		private void calculateParamsForTurnLeft_TurnToLeft()
		{
			if (sliceNumNeedDraw < SLICE_NUM)
			{
				bNeedDrawTurnLeft = false;
				return;
			}			
			
			if (fTurnLeftTextCoorStartX_Top >= 1.0)
			{
				bNeedDrawTurnLeft = false;
				return;
			}
			
			bNeedDrawTurnLeft = true;
			
			fTextureCoorTurnLeft[0] = fTurnLeftTextCoorStartX_Top;
			fTextureCoorTurnLeft[1] = 0;
			fTextureCoorTurnLeft[2] = fTurnLeftTextCoorStartX_Top;
			fTextureCoorTurnLeft[3] = 1;
			fTextureCoorTurnLeft[4] = 1.0f;
			fTextureCoorTurnLeft[5] = 1;
			fTextureCoorTurnLeft[6] = 1.0f;
			fTextureCoorTurnLeft[7] = 0;
			
			float fVerticesRight = (1.0f - fTurnLeftTextCoorStartX_Top) * GL_WIDTH;
			
			fVerticersTurnLeft[0] = 0;
			fVerticersTurnLeft[1] = top;	
			fVerticersTurnLeft[2] = 0;	
			fVerticersTurnLeft[3] = 1;	
			fVerticersTurnLeft[4] = 0;
			fVerticersTurnLeft[5] = bottom;			
			fVerticersTurnLeft[6] = 0;	
			fVerticersTurnLeft[7] = 1;	
			fVerticersTurnLeft[8] = -fVerticesRight;	
			fVerticersTurnLeft[9] = bottom;	
			fVerticersTurnLeft[10] = 0;
			fVerticersTurnLeft[11] = 1;	
			fVerticersTurnLeft[12] = -fVerticesRight;	
			fVerticersTurnLeft[13] = top;	
			fVerticersTurnLeft[14] = 0;	
			fVerticersTurnLeft[15] = 1;
			
			MatrixSW.moveM(fVerticersTurnLeft, 0, nextPosX, nextPosY, nextPosZ);	
		}
		
		private void makeTurnAxisLine_TurnToLeftTop()
		{
			PointF turnAxisVector = new PointF();
			PointF turnAxisFixPos = new PointF();
			float distanceFromDragPosToRightBottom;
			
			
			Line lineDrag = new Line(dragVector, dragPosition);
			PointF ptTmpCrossRight = lineDrag.getCross(lineRight);
			if (ptTmpCrossRight.y < bottom)
			{
				ptTmpCrossRight = lineDrag.getCross(lineBottom);				
			}			
			distanceFromDragPosToRightBottom = PointF.length(dragPosition.x - ptTmpCrossRight.x, dragPosition.y - ptTmpCrossRight.y);
			
			
			turnAxisVector.set(dragVector.y, -dragVector.x);			
			mapFactorX = Math.abs(turnAxisVector.y/turnAxisVector.length());
			mapFactorY = Math.abs(turnAxisVector.x/turnAxisVector.length());
			
			if (dragPosition.x > right)
			{
				turnAxisFixPos.set(right, bottom);				
			}			
			else if (distanceFromDragPosToRightBottom < TURN_LEN)
			{
				turnAxisFixPos.set(dragPosition.x, dragPosition.y);								
			}
			else
			{
				float tmpLen = (distanceFromDragPosToRightBottom - TURN_LEN)/2;
				turnAxisFixPos.x = dragPosition.x + tmpLen * mapFactorX;
				turnAxisFixPos.y = dragPosition.y - tmpLen * mapFactorY;
			}
			
			// turn axis line
			lineTurnAxis.makeLineByDirVectorAndFixPoint(turnAxisVector, turnAxisFixPos);
			
			ptTurnAxisBottom = lineTurnAxis.getCross(lineBottom);
			if (ptTurnAxisBottom.x < left)
			{
				turnAxisFixPos.set(left, bottom);
				
				mapFactorX = Math.abs(turnAxisVector.y/turnAxisVector.length());
				mapFactorY = Math.abs(turnAxisVector.x/turnAxisVector.length());
				
				lineTurnAxis.makeLineByDirVectorAndFixPoint(turnAxisVector, turnAxisFixPos);
			}
			
			ptTurnAxisBottom = lineTurnAxis.getCross(lineBottom);
			ptTurnAxisTop = lineTurnAxis.getCross(lineRight);
			if (ptTurnAxisTop.y > top)
			{
				ptTurnAxisTop = lineTurnAxis.getCross(lineTop);
			}			
			ptTurnAxisCenter = MathEx.getCenter(ptTurnAxisBottom, ptTurnAxisTop);
			
		}
		
		private void calculateParamsForNotTurn_TurnToLeftTop()
		{
			int countVertices = 0;
			int countTextCoor = 0;
			
			verticesNumNotTurn = 3;
			
			fVerticesNotTurn[countVertices++] = left;
			fVerticesNotTurn[countVertices++] = top;
			fVerticesNotTurn[countVertices++] = 0;
			fVerticesNotTurn[countVertices++] = 1;
			fVerticesNotTurn[countVertices++] = left;
			fVerticesNotTurn[countVertices++] = bottom;
			fVerticesNotTurn[countVertices++] = 0;
			fVerticesNotTurn[countVertices++] = 1;
			
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 1.0f;
			
			if (ptTurnAxisBottom.x > left)
			{
				verticesNumNotTurn++;
				
				fVerticesNotTurn[countVertices++] = ptTurnAxisBottom.x;
				fVerticesNotTurn[countVertices++] = bottom;
				fVerticesNotTurn[countVertices++] = 0;			
				fVerticesNotTurn[countVertices++] = 1;			
				
				fTextCoorNotTurn[countTextCoor++] = (ptTurnAxisBottom.x - left) / GL_WIDTH;
				fTextCoorNotTurn[countTextCoor++] = 1.0f;				
			}
			
			if (ptTurnAxisTop.y < top && MathEx.isFloatEqual(ptTurnAxisTop.x, right))
			{
				verticesNumNotTurn++;
				
				fVerticesNotTurn[countVertices++] = ptTurnAxisTop.x;
				fVerticesNotTurn[countVertices++] = ptTurnAxisTop.y;
				fVerticesNotTurn[countVertices++] = 0;			
				fVerticesNotTurn[countVertices++] = 1;			
				fVerticesNotTurn[countVertices++] = right;
				fVerticesNotTurn[countVertices++] = top;
				fVerticesNotTurn[countVertices++] = 0;		
				fVerticesNotTurn[countVertices++] = 1;				
				
				fTextCoorNotTurn[countTextCoor++] = 1.0f;
				fTextCoorNotTurn[countTextCoor++] = (top - ptTurnAxisTop.y)/GL_HEIGHT;
				fTextCoorNotTurn[countTextCoor++] = 1.0f;
				fTextCoorNotTurn[countTextCoor++] = 0.0f;
				
			}
			else
			{
				fVerticesNotTurn[countVertices++] = ptTurnAxisTop.x;
				fVerticesNotTurn[countVertices++] = ptTurnAxisTop.y;
				fVerticesNotTurn[countVertices++] = 0;	
				fVerticesNotTurn[countVertices++] = 1;			
				
				fTextCoorNotTurn[countTextCoor++] = (ptTurnAxisTop.x - left)/GL_WIDTH;
				fTextCoorNotTurn[countTextCoor++] = 0.0f;
			}
		}
		
		private void calculateParamsForNexPage_TurnToLeftTop()
		{
			int countVertices = 0;
			int countTextCoor = 0;
			
			countVertices = 4;
			countTextCoor = 2;
			if (ptTurnAxisBottom.x > left)
			{
				fVerticesNextPage[countVertices++] = ptTurnAxisBottom.x;
				fVerticesNextPage[countVertices++] = bottom;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;			
				
				fTextCoorNextPage[countTextCoor++] = (ptTurnAxisBottom.x - left) / GL_WIDTH;
				fTextCoorNextPage[countTextCoor++] = 1.0f;				
			}
			else
			{
				fVerticesNextPage[countVertices++] = left;
				fVerticesNextPage[countVertices++] = bottom;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;			
				
				fTextCoorNextPage[countTextCoor++] = 0;
				fTextCoorNextPage[countTextCoor++] = 1.0f;									
			}
			
			if (ptTurnAxisTop.y < top && MathEx.isFloatEqual(ptTurnAxisTop.x, right))
			{
				fVerticesNextPage[countVertices++] = right;
				fVerticesNextPage[countVertices++] = bottom;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;		
				fVerticesNextPage[countVertices++] = right;
				fVerticesNextPage[countVertices++] = bottom;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;				
				
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 1.0f;	
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 1.0f;		
				
				countVertices = 0;
				countTextCoor = 0;
				fVerticesNextPage[countVertices++] = ptTurnAxisTop.x;
				fVerticesNextPage[countVertices++] = ptTurnAxisTop.y;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;	
				
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = (top - ptTurnAxisTop.y)/GL_HEIGHT;
			}
			else
			{
				fVerticesNextPage[countVertices++] = right;
				fVerticesNextPage[countVertices++] = bottom;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;		
				fVerticesNextPage[countVertices++] = right;
				fVerticesNextPage[countVertices++] = top;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;				
				
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 1.0f;	
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 0.0f;		
				
				countVertices = 0;
				countTextCoor = 0;
				fVerticesNextPage[countVertices++] = ptTurnAxisTop.x;
				fVerticesNextPage[countVertices++] = ptTurnAxisTop.y;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;	
				
				fTextCoorNextPage[countTextCoor++] = (ptTurnAxisTop.x - left)/GL_WIDTH;
				fTextCoorNextPage[countTextCoor++] = 0;
			}				
		}
		
		private void calculateParamsForTurn_TurnToLeftTop()
		{	
			int sliceNumLeftOfRightTopCorner = 0;
			int sliceNumRightOfRightTopCorner = SLICE_NUM;
			int sliceIndex = 0;
			float fsliceEndLeftOfRightTop = 0;
			
			if (ptTurnAxisTop.x < right && !MathEx.isFloatEqual(right, ptTurnAxisTop.x)) 
			{
				float distanceFromTurnAxisToRightTop = lineTurnAxis.distanceFromPoint(new PointF(right, top));
				
				if (distanceFromTurnAxisToRightTop > TURN_LEN)
				{
					sliceNumLeftOfRightTopCorner = SLICE_NUM;		
					fsliceEndLeftOfRightTop = ptTurnAxisTop.x + Math.abs(TURN_LEN/dragVector.x);
				}
				else
				{
					sliceNumLeftOfRightTopCorner = (int)(distanceFromTurnAxisToRightTop/TURN_LEN * SLICE_NUM + 0.5);	
					fsliceEndLeftOfRightTop = right;
				}
				
				
				if (0 == sliceNumLeftOfRightTopCorner) 			
				{
					sliceNumLeftOfRightTopCorner = 1;	
				}
				
				sliceNumRightOfRightTopCorner = SLICE_NUM - sliceNumLeftOfRightTopCorner;
				if (sliceNumRightOfRightTopCorner < 0)
				{
					sliceNumRightOfRightTopCorner = 0;			
				}		
			}
			
			float verticesInit[] = new float[16];	
			
			float sliceWidthX = 0;
			float sliceTextCoorWidthX = 0;
			float sliceWidthY = 0;
			float sliceTextCoorWidthY = 0;
			
			float sliceTextCoorStartTop = 0;
			float sliceTextCoorStartBottom = 0;
			
			float sliceTextCoorStartTopX_This = 0;
			float sliceTextCoorStartTopX_Next = 0;
			float sliceTextCoorStartTopY_This = 0;
			float sliceTextCoorStartTopY_Next = 0;
			
			float sliceTextCoorStartBottomX_This = 0;
			float sliceTextCoorStartBottomX_Next = 0;
			boolean bTextEnd = false;
			
			sliceNumNeedDraw = 0;
			
			nextPosX = ptTurnAxisCenter.x;
			nextPosY = ptTurnAxisCenter.y;
			nextPosZ = 0;
			
			if (sliceNumLeftOfRightTopCorner > 0)
			{
				sliceWidthX = (fsliceEndLeftOfRightTop - ptTurnAxisTop.x)/sliceNumLeftOfRightTopCorner;
				sliceTextCoorWidthX = sliceWidthX/GL_WIDTH;
				
				sliceTextCoorStartTop = (ptTurnAxisTop.x - left)/GL_WIDTH;
				sliceTextCoorStartBottom = (ptTurnAxisBottom.x - left)/GL_WIDTH;
				
				verticesInit[0] = ptTurnAxisTop.x - ptTurnAxisCenter.x;
				verticesInit[1] = top;	
				verticesInit[2] = 0;	
				verticesInit[3] = 1;	
				verticesInit[4] = ptTurnAxisBottom.x  - ptTurnAxisCenter.x;
				verticesInit[5] = bottom;			
				verticesInit[6] = 0;	
				verticesInit[7] = 1;	
				verticesInit[8] = verticesInit[4] + sliceWidthX;
				verticesInit[9] = bottom;	
				verticesInit[10] = 0;
				verticesInit[11] = 1;	
				verticesInit[12] = verticesInit[0] + sliceWidthX;
				verticesInit[13] = top;	
				verticesInit[14] = 0;	
				verticesInit[15] = 1;
				
				for (sliceIndex=0; sliceIndex < sliceNumLeftOfRightTopCorner; sliceIndex++) 
				{					
					sliceTextCoorStartTopX_This = sliceTextCoorStartTop + (sliceIndex)* sliceTextCoorWidthX;
					sliceTextCoorStartBottomX_This = sliceTextCoorStartBottom + (sliceIndex)* sliceTextCoorWidthX;
					sliceTextCoorStartTopX_Next = sliceTextCoorStartTopX_This + sliceTextCoorWidthX;
					sliceTextCoorStartBottomX_Next = sliceTextCoorStartBottomX_This + sliceTextCoorWidthX;
					
					fTurnLeftTextCoorStartX_Top = sliceTextCoorStartTopX_Next;
					fTurnLeftTextCoorStartX_Bottom = sliceTextCoorStartBottomX_Next;
					fTurnLeftTextCoorStartY_Top = 0;
					
					fTextureCoorTurn[sliceIndex][0] = sliceTextCoorStartTopX_This;
					fTextureCoorTurn[sliceIndex][1] = 0;
					fTextureCoorTurn[sliceIndex][2] = sliceTextCoorStartBottomX_This;
					fTextureCoorTurn[sliceIndex][3] = 1;
					fTextureCoorTurn[sliceIndex][4] = sliceTextCoorStartBottomX_Next;
					fTextureCoorTurn[sliceIndex][5] = 1;
					fTextureCoorTurn[sliceIndex][6] = sliceTextCoorStartTopX_Next;
					fTextureCoorTurn[sliceIndex][7] = 0;
					
					MatrixSW.rotateM(fVerticesTurn[sliceIndex], 0, verticesInit, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					
					MatrixSW.rotate3M(fNormalsTurn[sliceIndex], 0, fNormalsNextPage, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					
					MatrixSW.moveM(fVerticesTurn[sliceIndex], 0, nextPosX, nextPosY, nextPosZ);				
					nextPosX = (fVerticesTurn[sliceIndex][8] + fVerticesTurn[sliceIndex][12])/2;
					nextPosY = (fVerticesTurn[sliceIndex][9] + fVerticesTurn[sliceIndex][13])/2;
					nextPosZ = (fVerticesTurn[sliceIndex][10] + fVerticesTurn[sliceIndex][14])/2;
					
					sliceNumNeedDraw++;
				}
			}
			
			if (sliceNumRightOfRightTopCorner > 0)
			{
				float distanceRealLeft = 0;
				float sliceStartY = 0;
				float sliceStartX = 0;
				float moveVector[] = new float[4];
				
				Line lineVirtualAxis = new Line();
				PointF ptVirtualCenter = new PointF();
				PointF ptTmpBottom = new PointF();
				
				if (sliceNumRightOfRightTopCorner < SLICE_NUM) 
				{
					sliceTextCoorStartTop = 0;
					sliceStartY = top;
					
					sliceTextCoorStartBottom = sliceTextCoorStartBottomX_Next;
					sliceStartX = left +  sliceTextCoorStartBottom * GL_WIDTH;
					distanceRealLeft = TURN_LEN - lineTurnAxis.distanceFromPoint(new PointF(right, top));
				}
				else
				{
					sliceTextCoorStartTop = (top - ptTurnAxisTop.y)/GL_HEIGHT;
					sliceStartY = ptTurnAxisTop.y;
					
					sliceTextCoorStartBottom = (ptTurnAxisBottom.x - left)/GL_WIDTH;
					sliceStartX = ptTurnAxisBottom.x;
					distanceRealLeft = TURN_LEN;
					
					lineVirtualAxis.makeLineByDirVectorAndFixPoint(lineTurnAxis.dirVector, lineTurnAxis.fixPoint);
					ptTmpBottom = lineVirtualAxis.getCross(lineBottom);					
					ptVirtualCenter = MathEx.getCenter(ptTmpBottom, lineVirtualAxis.getCross(lineTop));
					nextPosX = ptVirtualCenter.x;
					nextPosY = ptVirtualCenter.y;
					nextPosZ = 0;
				}
				
				sliceWidthX = Math.abs((distanceRealLeft / dragVector.x)/sliceNumRightOfRightTopCorner);
				sliceTextCoorWidthX = sliceWidthX/GL_WIDTH;
				sliceWidthY = Math.abs((distanceRealLeft / dragVector.y)/sliceNumRightOfRightTopCorner);
				sliceTextCoorWidthY = sliceWidthY/GL_HEIGHT;		
								
				
				for (int i = 0; i < sliceNumRightOfRightTopCorner; i++, sliceIndex++) 
				{
					sliceTextCoorStartTopY_This = sliceTextCoorStartTop + (i)* sliceTextCoorWidthY;
					sliceTextCoorStartBottomX_This = sliceTextCoorStartBottom + (i)* sliceTextCoorWidthX;
					sliceTextCoorStartTopY_Next = sliceTextCoorStartTopY_This + sliceTextCoorWidthY;
					sliceTextCoorStartBottomX_Next = sliceTextCoorStartBottomX_This + sliceTextCoorWidthX;
					
					if (sliceTextCoorStartTopY_This >= 1.0f)
					{
						break;					
					}		
					
					if (sliceTextCoorStartTopY_Next >= 1.0f)
					{
						sliceTextCoorStartTopY_Next = 1.0f;
						sliceTextCoorStartBottomX_Next = 1.0f;
						bTextEnd = true;
					}
					
					fTurnLeftTextCoorStartX_Top = 1;
					fTurnLeftTextCoorStartX_Bottom = sliceTextCoorStartBottomX_Next;
					fTurnLeftTextCoorStartY_Top = sliceTextCoorStartTopY_Next;
					
					fTextureCoorTurn[sliceIndex][0] = 1;
					fTextureCoorTurn[sliceIndex][1] = sliceTextCoorStartTopY_This;
					fTextureCoorTurn[sliceIndex][2] = sliceTextCoorStartBottomX_This;
					fTextureCoorTurn[sliceIndex][3] = 1;
					fTextureCoorTurn[sliceIndex][4] = sliceTextCoorStartBottomX_Next;
					fTextureCoorTurn[sliceIndex][5] = 1;
					fTextureCoorTurn[sliceIndex][6] = 1;
					fTextureCoorTurn[sliceIndex][7] = sliceTextCoorStartTopY_Next;
					
					ptTmpBottom.x = sliceStartX + i*sliceWidthX;
					ptTmpBottom.y = bottom;					
					lineVirtualAxis.makeLineByDirVectorAndFixPoint(lineTurnAxis.dirVector, ptTmpBottom);
					ptVirtualCenter = MathEx.getCenter(ptTmpBottom, lineVirtualAxis.getCross(lineTop));
					
					moveVector[0] = sliceWidthX;
					moveVector[1] = 0;
					moveVector[2] = 0;
					moveVector[3] = 1;
					
					verticesInit[0] = right - ptVirtualCenter.x;
					verticesInit[1] = sliceStartY - i*sliceWidthY;
					verticesInit[2] = 0;	
					verticesInit[3] = 1;	
					verticesInit[4] = sliceStartX + i*sliceWidthX - ptVirtualCenter.x;	
					verticesInit[5] = bottom;				
					verticesInit[6] = 0;	
					verticesInit[7] = 1;		
					if (bTextEnd)
					{
						verticesInit[8] = right - ptVirtualCenter.x;	
						verticesInit[9] = bottom;		
						verticesInit[10] = 0;
						verticesInit[11] = 1;	
						verticesInit[12] = right - ptVirtualCenter.x;
						verticesInit[13] = bottom;	
						verticesInit[14] = 0;	
						verticesInit[15] = 1;												
					}
					else 
					{
						verticesInit[8] = sliceStartX + (i+1)*sliceWidthX - ptVirtualCenter.x;
						verticesInit[9] = bottom;		
						verticesInit[10] = 0;
						verticesInit[11] = 1;	
						verticesInit[12] = right - ptVirtualCenter.x;
						verticesInit[13] = verticesInit[1] - sliceWidthY;	
						verticesInit[14] = 0;	
						verticesInit[15] = 1;						
					}
				
					MatrixSW.rotateM(fVerticesTurn[sliceIndex], 0, verticesInit, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					MatrixSW.rotateV(moveVector, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					
					MatrixSW.rotate3M(fNormalsTurn[sliceIndex], 0, fNormalsNextPage, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					
					MatrixSW.moveM(fVerticesTurn[sliceIndex], 0, nextPosX, nextPosY, nextPosZ);	
					MatrixSW.moveV(moveVector, 0, nextPosX, nextPosY, nextPosZ);						
					nextPosX = moveVector[0];
					nextPosY = moveVector[1];
					nextPosZ = moveVector[2];
					
					sliceNumNeedDraw++;
					
					if (bTextEnd) 
					{
						break;
					}
				}				
			}			
		}		
		
		private void calculateParamsForTurnLeft_TurnToLeftTop()
		{
			if (sliceNumNeedDraw < SLICE_NUM)
			{
				bNeedDrawTurnLeft = false;
				return;
			}
			
			if (fTurnLeftTextCoorStartY_Top >= 1.0f)
			{
				bNeedDrawTurnLeft = false;
				return;
			}
						
			bNeedDrawTurnLeft = true;
			
			if (fTurnLeftTextCoorStartY_Top > 0 && !MathEx.isFloatEqual(0, fTurnLeftTextCoorStartY_Top))
			{			
				fTextureCoorTurnLeft[0] = 1;
				fTextureCoorTurnLeft[1] = fTurnLeftTextCoorStartY_Top;
				fTextureCoorTurnLeft[2] = fTurnLeftTextCoorStartX_Bottom;
				fTextureCoorTurnLeft[3] = 1;
				fTextureCoorTurnLeft[4] = 1.0f;
				fTextureCoorTurnLeft[5] = 1.0f;
				fTextureCoorTurnLeft[6] = 1.0f;
				fTextureCoorTurnLeft[7] = 1.0f;
				
				PointF ptBottom = new PointF(fTurnLeftTextCoorStartX_Bottom*GL_WIDTH + left, bottom);
				Line lineVirtualAxis = new Line(lineTurnAxis.dirVector, ptBottom);
				PointF ptVirtualCenter = MathEx.getCenter(lineVirtualAxis.getCross(lineTop), ptBottom);
				
				float fTopY = top - fTurnLeftTextCoorStartY_Top * GL_HEIGHT;
				float fBottomX = left + fTurnLeftTextCoorStartX_Bottom * GL_WIDTH;
				
				fVerticersTurnLeft[0] = right - ptVirtualCenter.x;
				fVerticersTurnLeft[1] = fTopY;	
				fVerticersTurnLeft[2] = 0;	
				fVerticersTurnLeft[3] = 1;	
				fVerticersTurnLeft[4] = fBottomX - ptVirtualCenter.x;
				fVerticersTurnLeft[5] = bottom;			
				fVerticersTurnLeft[6] = 0;	
				fVerticersTurnLeft[7] = 1;	
				fVerticersTurnLeft[8] = right - ptVirtualCenter.x;
				fVerticersTurnLeft[9] = bottom;	
				fVerticersTurnLeft[10] = 0;
				fVerticersTurnLeft[11] = 1;	
				fVerticersTurnLeft[12] = right - ptVirtualCenter.x;
				fVerticersTurnLeft[13] = bottom;	
				fVerticersTurnLeft[14] = 0;	
				fVerticersTurnLeft[15] = 1;			

				MatrixSW.rotateM(fVerticersTurnLeft, 0, -180, lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
				MatrixSW.moveM(fVerticersTurnLeft, 0, nextPosX, nextPosY, nextPosZ);	
			}
			else
			{						
				fTextureCoorTurnLeft[0] = fTurnLeftTextCoorStartX_Top;
				fTextureCoorTurnLeft[1] = 0;
				fTextureCoorTurnLeft[2] = fTurnLeftTextCoorStartX_Bottom;
				fTextureCoorTurnLeft[3] = 1;
				fTextureCoorTurnLeft[4] = 1.0f;
				fTextureCoorTurnLeft[5] = 1;
				fTextureCoorTurnLeft[6] = 1.0f;
				fTextureCoorTurnLeft[7] = 0;
				
				float fTopX = left + fTurnLeftTextCoorStartX_Top * GL_WIDTH;
				float fBottomX = left + fTurnLeftTextCoorStartX_Bottom * GL_WIDTH;
				float fCenter = (fTopX + fBottomX)/2;
				
				fVerticersTurnLeft[0] = fTopX - fCenter;
				fVerticersTurnLeft[1] = top;	
				fVerticersTurnLeft[2] = 0;	
				fVerticersTurnLeft[3] = 1;	
				fVerticersTurnLeft[4] = fBottomX - fCenter;
				fVerticersTurnLeft[5] = bottom;			
				fVerticersTurnLeft[6] = 0;	
				fVerticersTurnLeft[7] = 1;	
				fVerticersTurnLeft[8] = right - fCenter;	
				fVerticersTurnLeft[9] = bottom;	
				fVerticersTurnLeft[10] = 0;
				fVerticersTurnLeft[11] = 1;	
				fVerticersTurnLeft[12] = right - fCenter;	
				fVerticersTurnLeft[13] = top;	
				fVerticersTurnLeft[14] = 0;	
				fVerticersTurnLeft[15] = 1;
				
				MatrixSW.rotateM(fVerticersTurnLeft, 0, -180, lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
				MatrixSW.moveM(fVerticersTurnLeft, 0, nextPosX, nextPosY, nextPosZ);				
			}
		}
		
		private void makeTurnAxisLine_TurnToLeftBottom()
		{
			PointF turnAxisVector = new PointF();
			PointF turnAxisFixPos = new PointF();
			float distanceFromDragPosToRightTop;
			
			Line lineDrag = new Line(dragVector, dragPosition);
			PointF ptTmpCrossRight = lineDrag.getCross(lineRight);
			if (ptTmpCrossRight.y > top)
			{
				ptTmpCrossRight = lineDrag.getCross(lineTop);				
			}
			distanceFromDragPosToRightTop = PointF.length(dragPosition.x - ptTmpCrossRight.x, dragPosition.y - ptTmpCrossRight.y);
			
			
			turnAxisVector.set(dragVector.y, -dragVector.x);			
			mapFactorX = Math.abs(turnAxisVector.y/turnAxisVector.length());
			mapFactorY = Math.abs(turnAxisVector.x/turnAxisVector.length());
			
			if (dragPosition.x > right)
			{
				turnAxisFixPos.set(right, top);				
			}			
			else if (distanceFromDragPosToRightTop < TURN_LEN)
			{
				turnAxisFixPos.set(dragPosition.x, dragPosition.y);								
			}
			else
			{
				float tmpLen = (distanceFromDragPosToRightTop - TURN_LEN)/2;
				turnAxisFixPos.x = dragPosition.x + tmpLen * mapFactorX;
				turnAxisFixPos.y = dragPosition.y + tmpLen * mapFactorY;
			}
			
			lineTurnAxis.makeLineByDirVectorAndFixPoint(turnAxisVector, turnAxisFixPos);		// 锟斤拷时锟侥ｏ拷锟斤拷要锟斤拷锟斤拷撞锟斤拷慕锟斤拷锟斤拷欠癯锟絣eft
			
			ptTurnAxisTop = lineTurnAxis.getCross(lineTop);
			if (ptTurnAxisTop.x < left)
			{
				turnAxisFixPos.set(left, top);
				
				mapFactorX = Math.abs(turnAxisVector.y/turnAxisVector.length());
				mapFactorY = Math.abs(turnAxisVector.x/turnAxisVector.length());
				
				lineTurnAxis.makeLineByDirVectorAndFixPoint(turnAxisVector, turnAxisFixPos);
			}
			
			ptTurnAxisBottom = lineTurnAxis.getCross(lineRight);
			ptTurnAxisTop = lineTurnAxis.getCross(lineTop);
			if (ptTurnAxisBottom.y < bottom)
			{
				ptTurnAxisBottom = lineTurnAxis.getCross(lineBottom);					
			}			
			ptTurnAxisCenter = MathEx.getCenter(ptTurnAxisBottom, ptTurnAxisTop);
		}
		
		private void calculateParamsForNotTurn_TurnToLeftBottom()
		{
			int countVertices = 0;
			int countTextCoor = 0;
			
			verticesNumNotTurn = 3;
			
			fVerticesNotTurn[countVertices++] = left;
			fVerticesNotTurn[countVertices++] = top;
			fVerticesNotTurn[countVertices++] = 0;
			fVerticesNotTurn[countVertices++] = 1;
			fVerticesNotTurn[countVertices++] = left;
			fVerticesNotTurn[countVertices++] = bottom;
			fVerticesNotTurn[countVertices++] = 0;
			fVerticesNotTurn[countVertices++] = 1;
			
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 0.0f;
			fTextCoorNotTurn[countTextCoor++] = 1.0f;
			
			if (ptTurnAxisBottom.y > bottom && !MathEx.isFloatEqual(ptTurnAxisBottom.y, bottom)) 
			{
				verticesNumNotTurn++;
				
				fVerticesNotTurn[countVertices++] = right;
				fVerticesNotTurn[countVertices++] = bottom;
				fVerticesNotTurn[countVertices++] = 0;	
				fVerticesNotTurn[countVertices++] = 1;
				fVerticesNotTurn[countVertices++] = ptTurnAxisBottom.x;
				fVerticesNotTurn[countVertices++] = ptTurnAxisBottom.y;
				fVerticesNotTurn[countVertices++] = 0;	
				fVerticesNotTurn[countVertices++] = 1;
				
				fTextCoorNotTurn[countTextCoor++] = 1.0f;
				fTextCoorNotTurn[countTextCoor++] = 1.0f;
				fTextCoorNotTurn[countTextCoor++] = 1.0f;
				fTextCoorNotTurn[countTextCoor++] = (top - ptTurnAxisBottom.y)/GL_HEIGHT;
				
			}
			else
			{
				fVerticesNotTurn[countVertices++] = ptTurnAxisBottom.x;
				fVerticesNotTurn[countVertices++] = ptTurnAxisBottom.y;
				fVerticesNotTurn[countVertices++] = 0;	
				fVerticesNotTurn[countVertices++] = 1;
				fTextCoorNotTurn[countTextCoor++] = (ptTurnAxisBottom.x - left)/GL_WIDTH;
				fTextCoorNotTurn[countTextCoor++] = (top - ptTurnAxisBottom.y)/GL_HEIGHT;
			}
				
			if (ptTurnAxisTop.x > left)
			{
				verticesNumNotTurn++;
				
				fVerticesNotTurn[countVertices++] = ptTurnAxisTop.x;
				fVerticesNotTurn[countVertices++] = top;
				fVerticesNotTurn[countVertices++] = 0;			
				fVerticesNotTurn[countVertices++] = 1;
				
				fTextCoorNotTurn[countTextCoor++] = (ptTurnAxisTop.x - left) / GL_WIDTH;
				fTextCoorNotTurn[countTextCoor++] = 0;				
			}
		}
		
		private void calculateParamsForNextPage_TurnToLeftBottom()
		{
			int countVertices = 0;
			int countTextCoor = 0;

			countVertices = 4;
			countTextCoor = 2;
			if (ptTurnAxisBottom.y > bottom && !MathEx.isFloatEqual(ptTurnAxisBottom.y, bottom)) 
			{				
				fVerticesNextPage[countVertices++] = ptTurnAxisBottom.x;
				fVerticesNextPage[countVertices++] = ptTurnAxisBottom.y;
				fVerticesNextPage[countVertices++] = 0;	
				fVerticesNextPage[countVertices++] = 1;
				fVerticesNextPage[countVertices++] = right;
				fVerticesNextPage[countVertices++] = top;
				fVerticesNextPage[countVertices++] = 0;	
				fVerticesNextPage[countVertices++] = 1;
				fVerticesNextPage[countVertices++] = right;
				fVerticesNextPage[countVertices++] = top;
				fVerticesNextPage[countVertices++] = 0;	
				fVerticesNextPage[countVertices++] = 1;
				
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = (top - ptTurnAxisBottom.y)/GL_HEIGHT;
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 0;
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 0;
				
			}
			else
			{
				fVerticesNextPage[countVertices++] = ptTurnAxisBottom.x;
				fVerticesNextPage[countVertices++] = ptTurnAxisBottom.y;
				fVerticesNextPage[countVertices++] = 0;	
				fVerticesNextPage[countVertices++] = 1;
				fVerticesNextPage[countVertices++] = right;
				fVerticesNextPage[countVertices++] = bottom;
				fVerticesNextPage[countVertices++] = 0;	
				fVerticesNextPage[countVertices++] = 1;
				fVerticesNextPage[countVertices++] = right;
				fVerticesNextPage[countVertices++] = top;
				fVerticesNextPage[countVertices++] = 0;	
				fVerticesNextPage[countVertices++] = 1;
				
				fTextCoorNextPage[countTextCoor++] = (ptTurnAxisBottom.x - left)/GL_WIDTH;
				fTextCoorNextPage[countTextCoor++] = (top - ptTurnAxisBottom.y)/GL_HEIGHT;
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 1.0f;
				fTextCoorNextPage[countTextCoor++] = 0.0f;
			}
				
			countVertices = 0;
			countTextCoor = 0;
			if (ptTurnAxisTop.x > left)
			{				
				fVerticesNextPage[countVertices++] = ptTurnAxisTop.x;
				fVerticesNextPage[countVertices++] = top;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;
				
				fTextCoorNextPage[countTextCoor++] = (ptTurnAxisTop.x - left) / GL_WIDTH;
				fTextCoorNextPage[countTextCoor++] = 0;				
			}
			else
			{
				fVerticesNextPage[countVertices++] = left;
				fVerticesNextPage[countVertices++] = top;
				fVerticesNextPage[countVertices++] = 0;			
				fVerticesNextPage[countVertices++] = 1;
				
				fTextCoorNextPage[countTextCoor++] = 0;
				fTextCoorNextPage[countTextCoor++] = 0;					
			}			
		}
		
		private void calculateParamsForTurn_TurnToLeftBottom()
		{				
			int sliceNumLeftOfRightBottomCorner = 0;
			int sliceNumRightOfRightBottomCorner = SLICE_NUM;
			int sliceIndex = 0;
			float fsliceEndLeftOfRightBottom = 0;
			
			if (ptTurnAxisBottom.x < right && !MathEx.isFloatEqual(right, ptTurnAxisBottom.x)) 
			{
				float distanceFromTurnAxisToRightBottom = lineTurnAxis.distanceFromPoint(new PointF(right, bottom));
				if (distanceFromTurnAxisToRightBottom > TURN_LEN)
				{
					sliceNumLeftOfRightBottomCorner = SLICE_NUM;		
					fsliceEndLeftOfRightBottom = ptTurnAxisBottom.x + Math.abs(TURN_LEN/dragVector.x);
				}
				else
				{
					sliceNumLeftOfRightBottomCorner = (int)(distanceFromTurnAxisToRightBottom/TURN_LEN * SLICE_NUM + 0.5);	
					fsliceEndLeftOfRightBottom = right;
				}
				
				if (0 == sliceNumLeftOfRightBottomCorner) 			
				{
					sliceNumLeftOfRightBottomCorner = 1;	
				}
				
				sliceNumRightOfRightBottomCorner = SLICE_NUM - sliceNumLeftOfRightBottomCorner;
				if (sliceNumRightOfRightBottomCorner < 0)
				{
					sliceNumRightOfRightBottomCorner = 0;			
				}		
			}
			
			float verticesInit[] = new float[16];	
			
			float sliceWidthX = 0;
			float sliceTextCoorWidthX = 0;
			float sliceWidthY = 0;
			float sliceTextCoorWidthY = 0;
			
			float sliceTextCoorStartTop = 0;
			float sliceTextCoorStartBottom = 0;
			
			float sliceTextCoorStartTopX_This = 0;
			float sliceTextCoorStartTopX_Next = 0;
			
			float sliceTextCoorStartBottomX_This = 0;
			float sliceTextCoorStartBottomX_Next = 0;
			float sliceTextCoorStartBottomY_This = 0;
			float sliceTextCoorStartBottomY_Next = 0;
			boolean bTextEnd = false;
			
			sliceNumNeedDraw = 0;
			
			nextPosX = ptTurnAxisCenter.x;
			nextPosY = ptTurnAxisCenter.y;
			nextPosZ = 0;
			
			if (sliceNumLeftOfRightBottomCorner > 0)
			{
				sliceWidthX = (fsliceEndLeftOfRightBottom - ptTurnAxisBottom.x)/sliceNumLeftOfRightBottomCorner;
				sliceTextCoorWidthX = sliceWidthX/GL_WIDTH;
				
				sliceTextCoorStartTop = (ptTurnAxisTop.x - left)/GL_WIDTH;
				sliceTextCoorStartBottom = (ptTurnAxisBottom.x - left)/GL_WIDTH;
				
				verticesInit[0] = ptTurnAxisTop.x - ptTurnAxisCenter.x;
				verticesInit[1] = top;	
				verticesInit[2] = 0;	
				verticesInit[3] = 1;	
				verticesInit[4] = ptTurnAxisBottom.x  - ptTurnAxisCenter.x;
				verticesInit[5] = bottom;			
				verticesInit[6] = 0;	
				verticesInit[7] = 1;	
				verticesInit[8] = verticesInit[4] + sliceWidthX;
				verticesInit[9] = bottom;	
				verticesInit[10] = 0;
				verticesInit[11] = 1;	
				verticesInit[12] = verticesInit[0] + sliceWidthX;
				verticesInit[13] = top;	
				verticesInit[14] = 0;	
				verticesInit[15] = 1;
				
				for (sliceIndex=0; sliceIndex < sliceNumLeftOfRightBottomCorner; sliceIndex++) 
				{					
					sliceTextCoorStartTopX_This = sliceTextCoorStartTop + (sliceIndex)* sliceTextCoorWidthX;
					sliceTextCoorStartBottomX_This = sliceTextCoorStartBottom + (sliceIndex)* sliceTextCoorWidthX;
					sliceTextCoorStartTopX_Next = sliceTextCoorStartTopX_This + sliceTextCoorWidthX;
					sliceTextCoorStartBottomX_Next = sliceTextCoorStartBottomX_This + sliceTextCoorWidthX;
					
					fTurnLeftTextCoorStartX_Top = sliceTextCoorStartTopX_Next;			// 锟斤拷锟斤拷剩锟洁部锟斤拷锟斤拷图锟斤拷锟斤拷实位锟矫ｏ拷锟斤拷实锟斤拷循锟斤拷锟斤拷锟斤拷锟揭伙拷胃锟街碉拷锟斤拷桑锟矫匡拷味锟斤拷锟斤拷拢锟斤拷欠锟斤拷if锟叫讹拷没锟斤拷系
					fTurnLeftTextCoorStartX_Bottom = sliceTextCoorStartBottomX_Next;
					fTurnLeftTextCoorStartY_Top = 0;
					fTurnLeftTextCoorStartY_Bottom = 1;
					
					fTextureCoorTurn[sliceIndex][0] = sliceTextCoorStartTopX_This;
					fTextureCoorTurn[sliceIndex][1] = 0;
					fTextureCoorTurn[sliceIndex][2] = sliceTextCoorStartBottomX_This;
					fTextureCoorTurn[sliceIndex][3] = 1;
					fTextureCoorTurn[sliceIndex][4] = sliceTextCoorStartBottomX_Next;
					fTextureCoorTurn[sliceIndex][5] = 1;
					fTextureCoorTurn[sliceIndex][6] = sliceTextCoorStartTopX_Next;
					fTextureCoorTurn[sliceIndex][7] = 0;
					
					MatrixSW.rotateM(fVerticesTurn[sliceIndex], 0, verticesInit, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					
					MatrixSW.rotate3M(fNormalsTurn[sliceIndex], 0, fNormalsNextPage, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					
					MatrixSW.moveM(fVerticesTurn[sliceIndex], 0, nextPosX, nextPosY, nextPosZ);				
					nextPosX = (fVerticesTurn[sliceIndex][8] + fVerticesTurn[sliceIndex][12])/2;
					nextPosY = (fVerticesTurn[sliceIndex][9] + fVerticesTurn[sliceIndex][13])/2;
					nextPosZ = (fVerticesTurn[sliceIndex][10] + fVerticesTurn[sliceIndex][14])/2;
					
					sliceNumNeedDraw++;
				}
			}
			
			if (sliceNumRightOfRightBottomCorner > 0)
			{
				float distanceRealLeft = 0;
				float sliceStartY = 0;
				float sliceStartX = 0;
				float moveVector[] = new float[4];
				
				Line lineVirtualAxis = new Line();
				PointF ptVirtualCenter = new PointF();
				PointF ptTmpBottom = new PointF();
				
				if (sliceNumRightOfRightBottomCorner < SLICE_NUM) 
				{
					sliceTextCoorStartTop = sliceTextCoorStartTopX_Next;
					sliceStartX = left +  sliceTextCoorStartTop * GL_WIDTH;
					
					sliceTextCoorStartBottom = 1;
					sliceStartY = bottom;
					distanceRealLeft = TURN_LEN - lineTurnAxis.distanceFromPoint(new PointF(right, bottom));
				}
				else
				{
					sliceTextCoorStartTop = (ptTurnAxisTop.x - left)/GL_WIDTH;
					sliceStartX = left +  sliceTextCoorStartTop * GL_WIDTH;
									
					sliceTextCoorStartBottom = (top - ptTurnAxisBottom.y)/GL_HEIGHT;
					sliceStartY = ptTurnAxisBottom.y;
					distanceRealLeft = TURN_LEN;
					
					lineVirtualAxis.makeLineByDirVectorAndFixPoint(lineTurnAxis.dirVector, lineTurnAxis.fixPoint);
					ptTmpBottom = lineVirtualAxis.getCross(lineBottom);					
					ptVirtualCenter = MathEx.getCenter(ptTmpBottom, lineVirtualAxis.getCross(lineTop));
					nextPosX = ptVirtualCenter.x;
					nextPosY = ptVirtualCenter.y;
					nextPosZ = 0;
				}
				
				sliceWidthX = Math.abs((distanceRealLeft / dragVector.x)/sliceNumRightOfRightBottomCorner);
				sliceTextCoorWidthX = sliceWidthX/GL_WIDTH;
				sliceWidthY = Math.abs((distanceRealLeft / dragVector.y)/sliceNumRightOfRightBottomCorner);
				sliceTextCoorWidthY = sliceWidthY/GL_HEIGHT;		
								
				
				for (int i = 0; i < sliceNumRightOfRightBottomCorner; i++, sliceIndex++) 
				{
					sliceTextCoorStartBottomY_This = sliceTextCoorStartBottom - (i)* sliceTextCoorWidthY;
					sliceTextCoorStartBottomY_Next = sliceTextCoorStartBottomY_This - sliceTextCoorWidthY;
					sliceTextCoorStartTopX_This = sliceTextCoorStartTop + (i) * sliceTextCoorWidthX;
					sliceTextCoorStartTopX_Next = sliceTextCoorStartTopX_This + sliceTextCoorWidthX;
					
					if (sliceTextCoorStartBottomY_This <= 0)
					{
						break;					
					}		
					
					if (sliceTextCoorStartBottomY_Next <= 0)
					{
						sliceTextCoorStartBottomY_Next = 0;
						sliceTextCoorStartTopX_Next = 1.0f;
						bTextEnd = true;
					}
					
					fTurnLeftTextCoorStartX_Top = sliceTextCoorStartTopX_Next;
					fTurnLeftTextCoorStartY_Top = 0;
					fTurnLeftTextCoorStartX_Bottom = 1;
					fTurnLeftTextCoorStartY_Bottom = sliceTextCoorStartBottomY_Next;
					
					fTextureCoorTurn[sliceIndex][0] = sliceTextCoorStartTopX_This;
					fTextureCoorTurn[sliceIndex][1] = 0;
					fTextureCoorTurn[sliceIndex][2] = 1;
					fTextureCoorTurn[sliceIndex][3] = sliceTextCoorStartBottomY_This;
					fTextureCoorTurn[sliceIndex][4] = 1;
					fTextureCoorTurn[sliceIndex][5] = sliceTextCoorStartBottomY_Next;
					fTextureCoorTurn[sliceIndex][6] = sliceTextCoorStartTopX_Next;
					fTextureCoorTurn[sliceIndex][7] = 0;
					
					ptTmpBottom.x = sliceStartX + i*sliceWidthX;
					ptTmpBottom.y = top;					
					lineVirtualAxis.makeLineByDirVectorAndFixPoint(lineTurnAxis.dirVector, ptTmpBottom);
					ptVirtualCenter = MathEx.getCenter(ptTmpBottom, lineVirtualAxis.getCross(lineBottom));
					
					moveVector[0] = sliceWidthX;
					moveVector[1] = 0;
					moveVector[2] = 0;
					moveVector[3] = 1;
					
					verticesInit[0] = sliceStartX + i*sliceWidthX - ptVirtualCenter.x;	
					verticesInit[1] = top;
					verticesInit[2] = 0;	
					verticesInit[3] = 1;	
					verticesInit[4] = right - ptVirtualCenter.x;	
					verticesInit[5] = sliceStartY + i*sliceWidthY;				
					verticesInit[6] = 0;	
					verticesInit[7] = 1;		
					if (bTextEnd)
					{
						verticesInit[8] = right - ptVirtualCenter.x;	
						verticesInit[9] = top;		
						verticesInit[10] = 0;
						verticesInit[11] = 1;	
						verticesInit[12] = right - ptVirtualCenter.x;
						verticesInit[13] = top;	
						verticesInit[14] = 0;	
						verticesInit[15] = 1;												
					}
					else 
					{
						verticesInit[8] = right - ptVirtualCenter.x;
						verticesInit[9] = verticesInit[5] + sliceWidthY;		
						verticesInit[10] = 0;
						verticesInit[11] = 1;	
						verticesInit[12] = sliceStartX + (i+1)*sliceWidthX - ptVirtualCenter.x;	
						verticesInit[13] = top;	
						verticesInit[14] = 0;	
						verticesInit[15] = 1;						
					}
				
					MatrixSW.rotateM(fVerticesTurn[sliceIndex], 0, verticesInit, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					MatrixSW.rotateV(moveVector, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					
					MatrixSW.rotate3M(fNormalsTurn[sliceIndex], 0, fNormalsNextPage, 0, -fRotateAnlges[sliceIndex], lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
					
					MatrixSW.moveM(fVerticesTurn[sliceIndex], 0, nextPosX, nextPosY, nextPosZ);	
					MatrixSW.moveV(moveVector, 0, nextPosX, nextPosY, nextPosZ);						
					nextPosX = moveVector[0];
					nextPosY = moveVector[1];
					nextPosZ = moveVector[2];
					
					sliceNumNeedDraw++;
					
					if (bTextEnd) 
					{
						break;
					}
				}				
			}			
		}		
		
		private void calculateShadowNextPage()
		{
			float xFactor = 1.0f;
			
			for (int i = 0; i < 16; i++)
			{
				fVerticesShadowNextPage[i] = fVerticesNextPage[i%8];				
			}
			
			if (FlipView.this.dragPosition.x > -(VIEW_WIDTH-20))
			{
				fVerticesShadowNextPage[8] = (fVerticesNextPage[4] + SHADOW_WIDTH/Math.abs(dragVector.x)) * xFactor;
				fVerticesShadowNextPage[9] = fVerticesNextPage[5];
				fVerticesShadowNextPage[12] = (fVerticesNextPage[0] + SHADOW_WIDTH/Math.abs(dragVector.x)) * xFactor;
				fVerticesShadowNextPage[13] = fVerticesNextPage[1];					
			}
			else
			{
				fVerticesShadowNextPage[8] = fVerticesNextPage[4];
				fVerticesShadowNextPage[9] = fVerticesNextPage[5];
				fVerticesShadowNextPage[12] = fVerticesNextPage[0];	
				fVerticesShadowNextPage[13] = fVerticesNextPage[1];					
			}			
		}
		
		private void calculateShadowCurPage_ToLeft()
		{
			float yFactor = 1;
			float xFactor = 1;
			float shadowColor = 0.0f;
			
			if (dragPosition.x < 0)
			{
				xFactor = 1.2f;				
			}
			else 
			{
				xFactor = 1.05f;
			}

			if (bNeedDrawTurnLeft)
			{		
				fVerticesShadowCurPageTop[0] = Math.min((fVerticersTurnLeft[12] + SHADOW_WIDTH/2)*xFactor, fVerticesNotTurn[12]);
				fVerticesShadowCurPageTop[1] = fVerticersTurnLeft[13] * yFactor;
				fVerticesShadowCurPageTop[2] = 0;
				fVerticesShadowCurPageTop[3] = 1;	
				fVerticesShadowCurPageTop[4] = Math.min((fVerticersTurnLeft[8] + SHADOW_WIDTH/2) * xFactor, fVerticesNotTurn[12]);
				fVerticesShadowCurPageTop[5] = fVerticersTurnLeft[9] * yFactor;
				fVerticesShadowCurPageTop[6] = 0;
				fVerticesShadowCurPageTop[7] = 1;	
				fVerticesShadowCurPageTop[8] = (fVerticersTurnLeft[8] - SHADOW_WIDTH/2) * xFactor;
				fVerticesShadowCurPageTop[9] = fVerticersTurnLeft[9] * yFactor;
				fVerticesShadowCurPageTop[10] = 0;
				fVerticesShadowCurPageTop[11] = 1;	
				fVerticesShadowCurPageTop[12] = (fVerticersTurnLeft[12] - SHADOW_WIDTH/2) * xFactor;
				fVerticesShadowCurPageTop[13] = fVerticersTurnLeft[13] * yFactor;
				fVerticesShadowCurPageTop[14] = 0;
				fVerticesShadowCurPageTop[15] = 1;				
			}
			else if (FlipView.this.dragPosition.x < VIEW_WIDTH)
			{
				fVerticesShadowCurPageTop[0] = fVerticesNotTurn[12];
				fVerticesShadowCurPageTop[1] = fVerticesNotTurn[13] * yFactor;
				fVerticesShadowCurPageTop[2] = 0;
				fVerticesShadowCurPageTop[3] = 1;	
				fVerticesShadowCurPageTop[4] = fVerticesNotTurn[12];
				fVerticesShadowCurPageTop[5] = fVerticesNotTurn[9] * yFactor;
				fVerticesShadowCurPageTop[6] = 0;
				fVerticesShadowCurPageTop[7] = 1;	
				fVerticesShadowCurPageTop[8] = fVerticesNotTurn[12] - SHADOW_WIDTH/2; 
				fVerticesShadowCurPageTop[9] = fVerticesNotTurn[9] * yFactor;
				fVerticesShadowCurPageTop[10] = 0;
				fVerticesShadowCurPageTop[11] = 1;	
				fVerticesShadowCurPageTop[12] = fVerticesNotTurn[12] - SHADOW_WIDTH/2; 
				fVerticesShadowCurPageTop[13] = fVerticesNotTurn[13] * yFactor;
				fVerticesShadowCurPageTop[14] = 0;
				fVerticesShadowCurPageTop[15] = 1;			
			}
			else
			{
				for (int i = 0; i < fVerticesShadowCurPageTop.length; i++) 
				{
					fVerticesShadowCurPageTop[i] = 0;				
				}				
			}
			
			for (int i = 0; i < fVerticesShadowCurPageBottom.length; i++) 
			{
				fVerticesShadowCurPageBottom[i] = 0;				
			}
			
			shadowColor = (float)(0.9f - SHADOW_WIDTH);
			shadowColor = (shadowColor > 0.5) ? 0.5f : shadowColor;
			updateShadowColors(shadowColor);
		}
		
		private void calculateShadowCurPage_ToLeftTop()
		{
			float yFactor = 1 + 0.13f * RADIUS/0.3f;
			float xFactor = 1;
			float shadowColor = 0.0f;
			
			if (dragPosition.x < 0)
			{
				xFactor = 1.13f;				
			}
			else 
			{
				xFactor = 1.05f;
			}
			
			if (bNeedDrawTurnLeft)
			{
				Line lineTmp = new Line();
				PointF ptTmp;				

				if (!MathEx.isFloatEqual(fVerticersTurnLeft[9], fVerticersTurnLeft[13])) 
				{
					lineTmp.makeLineByTwoDifferentPoint(new PointF(fVerticersTurnLeft[8], fVerticersTurnLeft[9]),
							new PointF(fVerticersTurnLeft[12], fVerticersTurnLeft[13]));
					
					fVerticesShadowCurPageTop[12] = (fVerticersTurnLeft[8] - Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageTop[13] = (fVerticersTurnLeft[9] + Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageTop[14] = 0;
					fVerticesShadowCurPageTop[15] = 1;
					
					lineTmp.makeLineByDirVectorAndFixPoint(lineTmp.dirVector, new PointF(fVerticesShadowCurPageTop[12], fVerticesShadowCurPageTop[13]));
					ptTmp = lineTmp.getCross(lineRight);
					if (ptTmp.y > top)
					{
						ptTmp = lineTmp.getCross(lineTop);						
					}

					fVerticesShadowCurPageTop[8] = ptTmp.x;
					fVerticesShadowCurPageTop[9] = ptTmp.y;
					fVerticesShadowCurPageTop[10] = 0;
					fVerticesShadowCurPageTop[11] = 1;	
					
					fVerticesShadowCurPageTop[0] = (fVerticersTurnLeft[8] + Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageTop[1] = (fVerticersTurnLeft[9] - Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageTop[2] = 0;
					fVerticesShadowCurPageTop[3] = 1;	
					fVerticesShadowCurPageTop[4] = fVerticesShadowCurPageTop[8] + SHADOW_WIDTH;
					fVerticesShadowCurPageTop[5] = fVerticesShadowCurPageTop[9];
					fVerticesShadowCurPageTop[6] = 0;
					fVerticesShadowCurPageTop[7] = 1;	
					
					lineTmp.makeLineByTwoDifferentPoint(new PointF(fVerticersTurnLeft[8], fVerticersTurnLeft[9]),
							new PointF(fVerticersTurnLeft[4], fVerticersTurnLeft[5]));
					
					fVerticesShadowCurPageBottom[12] = (fVerticersTurnLeft[8] - Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageBottom[13] = (fVerticersTurnLeft[9] + Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageBottom[14] = 0;
					fVerticesShadowCurPageBottom[15] = 1;
					
					lineTmp.makeLineByDirVectorAndFixPoint(lineTmp.dirVector, new PointF(fVerticesShadowCurPageBottom[12], fVerticesShadowCurPageBottom[13]));
					ptTmp = lineTmp.getCross(lineRight);
					if (ptTmp.y < bottom)
					{
						ptTmp = lineTmp.getCross(lineBottom);						
					}
					
					fVerticesShadowCurPageBottom[8] = ptTmp.x;
					fVerticesShadowCurPageBottom[9] = ptTmp.y;
					fVerticesShadowCurPageBottom[10] = 0;
					fVerticesShadowCurPageBottom[11] = 1;	
					
					fVerticesShadowCurPageBottom[0] = (fVerticersTurnLeft[8] + Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageBottom[1] = (fVerticersTurnLeft[9] - Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageBottom[2] = 0;
					fVerticesShadowCurPageBottom[3] = 1;	
					fVerticesShadowCurPageBottom[4] = fVerticesShadowCurPageBottom[8] + SHADOW_WIDTH;
					fVerticesShadowCurPageBottom[5] = fVerticesShadowCurPageBottom[9];
					fVerticesShadowCurPageBottom[6] = 0;
					fVerticesShadowCurPageBottom[7] = 1;	
				}
				else
				{
					lineTmp.makeLineByTwoDifferentPoint(new PointF(fVerticersTurnLeft[8], fVerticersTurnLeft[9]),
							new PointF(fVerticersTurnLeft[0], fVerticersTurnLeft[1]));
					
					fVerticesShadowCurPageTop[12] = (fVerticersTurnLeft[8] - Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageTop[13] = (fVerticersTurnLeft[9] + Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageTop[14] = 0;
					fVerticesShadowCurPageTop[15] = 1;
					
					lineTmp.makeLineByDirVectorAndFixPoint(lineTmp.dirVector, new PointF(fVerticesShadowCurPageTop[12], fVerticesShadowCurPageTop[13]));
					ptTmp = lineTmp.getCross(lineRight);
					if (ptTmp.y > top)
					{
						ptTmp = lineTmp.getCross(lineTop);						
					}

					fVerticesShadowCurPageTop[8] = ptTmp.x;
					fVerticesShadowCurPageTop[9] = ptTmp.y;
					fVerticesShadowCurPageTop[10] = 0;
					fVerticesShadowCurPageTop[11] = 1;	
					
					fVerticesShadowCurPageTop[0] = (fVerticersTurnLeft[8] + Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageTop[1] = (fVerticersTurnLeft[9] - Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageTop[2] = 0;
					fVerticesShadowCurPageTop[3] = 1;	
					fVerticesShadowCurPageTop[4] = fVerticesShadowCurPageTop[8] + SHADOW_WIDTH;
					fVerticesShadowCurPageTop[5] = fVerticesShadowCurPageTop[9];
					fVerticesShadowCurPageTop[6] = 0;
					fVerticesShadowCurPageTop[7] = 1;	
					

					// Bottom
					lineTmp.makeLineByTwoDifferentPoint(new PointF(fVerticersTurnLeft[8], fVerticersTurnLeft[9]),
							new PointF(fVerticersTurnLeft[4], fVerticersTurnLeft[5]));
					
					fVerticesShadowCurPageBottom[12] = (fVerticersTurnLeft[8] - Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageBottom[13] = (fVerticersTurnLeft[9] + Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageBottom[14] = 0;
					fVerticesShadowCurPageBottom[15] = 1;
					
					lineTmp.makeLineByDirVectorAndFixPoint(lineTmp.dirVector, new PointF(fVerticesShadowCurPageBottom[12], fVerticesShadowCurPageBottom[13]));
					ptTmp = lineTmp.getCross(lineBottom);
					
					fVerticesShadowCurPageBottom[8] = ptTmp.x;
					fVerticesShadowCurPageBottom[9] = ptTmp.y;
					fVerticesShadowCurPageBottom[10] = 0;
					fVerticesShadowCurPageBottom[11] = 1;	
					
					fVerticesShadowCurPageBottom[0] = (fVerticersTurnLeft[8] + Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageBottom[1] = (fVerticersTurnLeft[9] - Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageBottom[2] = 0;
					fVerticesShadowCurPageBottom[3] = 1;	
					fVerticesShadowCurPageBottom[4] = fVerticesShadowCurPageBottom[8] + SHADOW_WIDTH;
					fVerticesShadowCurPageBottom[5] = fVerticesShadowCurPageBottom[9];
					fVerticesShadowCurPageBottom[6] = 0;
					fVerticesShadowCurPageBottom[7] = 1;	
				}				
			}
			else
			{
				for (int i = 0; i < fVerticesShadowCurPageTop.length; i++) 
				{
					fVerticesShadowCurPageTop[i] = 0;				
				}
				
				for (int i = 0; i < fVerticesShadowCurPageBottom.length; i++) 
				{
					fVerticesShadowCurPageBottom[i] = 0;				
				}				
			}
			
			shadowColor = (float)(0.9f - SHADOW_WIDTH);
			shadowColor = (shadowColor > 0.5) ? 0.5f : shadowColor;
			updateShadowColors(shadowColor);
		}
		
		private void calculateShadowCurPage_ToLeftBottom()
		{
			float yFactor = 1 + 0.13f * RADIUS/0.3f;
			float xFactor = 1;
			float shadowColor = 0.0f;
			
			if (dragPosition.x < 0)
			{
				xFactor = 1.13f;				
			}
			else 
			{
				xFactor = 1.05f;
			}
			
			if (bNeedDrawTurnLeft)
			{
				Line lineTmp = new Line();
				PointF ptTmp;				

				if (!MathEx.isFloatEqual(fVerticersTurnLeft[9], fVerticersTurnLeft[13])) 
				{
					lineTmp.makeLineByTwoDifferentPoint(new PointF(fVerticersTurnLeft[0], fVerticersTurnLeft[1]),
							new PointF(fVerticersTurnLeft[12], fVerticersTurnLeft[13]));
					
					fVerticesShadowCurPageTop[12] = (fVerticersTurnLeft[12] - Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageTop[13] = (fVerticersTurnLeft[13] - Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageTop[14] = 0;
					fVerticesShadowCurPageTop[15] = 1;
					
					lineTmp.makeLineByDirVectorAndFixPoint(lineTmp.dirVector, new PointF(fVerticesShadowCurPageTop[12], fVerticesShadowCurPageTop[13]));
					ptTmp = lineTmp.getCross(lineTop);

					fVerticesShadowCurPageTop[8] = ptTmp.x;
					fVerticesShadowCurPageTop[9] = ptTmp.y;
					fVerticesShadowCurPageTop[10] = 0;
					fVerticesShadowCurPageTop[11] = 1;	
					
					fVerticesShadowCurPageTop[0] = (fVerticersTurnLeft[12] + Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageTop[1] = (fVerticersTurnLeft[13] + Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageTop[2] = 0;
					fVerticesShadowCurPageTop[3] = 1;	
					fVerticesShadowCurPageTop[4] = fVerticesShadowCurPageTop[8] + SHADOW_WIDTH;
					fVerticesShadowCurPageTop[5] = fVerticesShadowCurPageTop[9];
					fVerticesShadowCurPageTop[6] = 0;
					fVerticesShadowCurPageTop[7] = 1;	
					
					// Bottom
					lineTmp.makeLineByTwoDifferentPoint(new PointF(fVerticersTurnLeft[8], fVerticersTurnLeft[9]),
							new PointF(fVerticersTurnLeft[12], fVerticersTurnLeft[13]));
					
					fVerticesShadowCurPageBottom[12] = (fVerticersTurnLeft[12] - Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageBottom[13] = (fVerticersTurnLeft[13] - Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageBottom[14] = 0;
					fVerticesShadowCurPageBottom[15] = 1;
					
					lineTmp.makeLineByDirVectorAndFixPoint(lineTmp.dirVector, new PointF(fVerticesShadowCurPageBottom[12], fVerticesShadowCurPageBottom[13]));
					ptTmp = lineTmp.getCross(lineRight);
					if (ptTmp.y < bottom)
					{
						ptTmp = lineTmp.getCross(lineBottom);						
					}
					
					fVerticesShadowCurPageBottom[8] = ptTmp.x;
					fVerticesShadowCurPageBottom[9] = ptTmp.y;
					fVerticesShadowCurPageBottom[10] = 0;
					fVerticesShadowCurPageBottom[11] = 1;	
					
					fVerticesShadowCurPageBottom[0] = (fVerticersTurnLeft[12] + Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageBottom[1] = (fVerticersTurnLeft[13] + Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageBottom[2] = 0;
					fVerticesShadowCurPageBottom[3] = 1;	
					fVerticesShadowCurPageBottom[4] = fVerticesShadowCurPageBottom[8] + SHADOW_WIDTH;
					fVerticesShadowCurPageBottom[5] = fVerticesShadowCurPageBottom[9];
					fVerticesShadowCurPageBottom[6] = 0;
					fVerticesShadowCurPageBottom[7] = 1;	
				}
				else{
					lineTmp.makeLineByTwoDifferentPoint(new PointF(fVerticersTurnLeft[8], fVerticersTurnLeft[9]),
							new PointF(fVerticersTurnLeft[0], fVerticersTurnLeft[1]));
					
					fVerticesShadowCurPageTop[12] = (fVerticersTurnLeft[8] - Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageTop[13] = (fVerticersTurnLeft[9] - Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageTop[14] = 0;
					fVerticesShadowCurPageTop[15] = 1;
					
					lineTmp.makeLineByDirVectorAndFixPoint(lineTmp.dirVector, new PointF(fVerticesShadowCurPageTop[12], fVerticesShadowCurPageTop[13]));
					ptTmp = lineTmp.getCross(lineTop);

					fVerticesShadowCurPageTop[8] = ptTmp.x;
					fVerticesShadowCurPageTop[9] = ptTmp.y;
					fVerticesShadowCurPageTop[10] = 0;
					fVerticesShadowCurPageTop[11] = 1;	
					
					fVerticesShadowCurPageTop[0] = (fVerticersTurnLeft[8] + Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageTop[1] = (fVerticersTurnLeft[9] + Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageTop[2] = 0;
					fVerticesShadowCurPageTop[3] = 1;	
					fVerticesShadowCurPageTop[4] = fVerticesShadowCurPageTop[8] + SHADOW_WIDTH;
					fVerticesShadowCurPageTop[5] = fVerticesShadowCurPageTop[9];
					fVerticesShadowCurPageTop[6] = 0;
					fVerticesShadowCurPageTop[7] = 1;	
					

					// Bottom
					lineTmp.makeLineByTwoDifferentPoint(new PointF(fVerticersTurnLeft[8], fVerticersTurnLeft[9]),
							new PointF(fVerticersTurnLeft[4], fVerticersTurnLeft[5]));
					
					fVerticesShadowCurPageBottom[12] = (fVerticersTurnLeft[8] - Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageBottom[13] = (fVerticersTurnLeft[9] - Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageBottom[14] = 0;
					fVerticesShadowCurPageBottom[15] = 1;
					
					lineTmp.makeLineByDirVectorAndFixPoint(lineTmp.dirVector, new PointF(fVerticesShadowCurPageBottom[12], fVerticesShadowCurPageBottom[13]));
					
					ptTmp = lineTmp.getCross(lineRight);
					if (ptTmp.y < bottom){
						ptTmp = lineTmp.getCross(lineBottom);
					}
					
					fVerticesShadowCurPageBottom[8] = ptTmp.x;
					fVerticesShadowCurPageBottom[9] = ptTmp.y;
					fVerticesShadowCurPageBottom[10] = 0;
					fVerticesShadowCurPageBottom[11] = 1;	
					
					fVerticesShadowCurPageBottom[0] = (fVerticersTurnLeft[8] + Math.abs(SHADOW_WIDTH/2 * dragVector.x)) * xFactor;
					fVerticesShadowCurPageBottom[1] = (fVerticersTurnLeft[9] + Math.abs(SHADOW_WIDTH/2 * dragVector.y)) * yFactor;
					fVerticesShadowCurPageBottom[2] = 0;
					fVerticesShadowCurPageBottom[3] = 1;	
					fVerticesShadowCurPageBottom[4] = fVerticesShadowCurPageBottom[8] + SHADOW_WIDTH;
					fVerticesShadowCurPageBottom[5] = fVerticesShadowCurPageBottom[9];
					fVerticesShadowCurPageBottom[6] = 0;
					fVerticesShadowCurPageBottom[7] = 1;	
				}				
			}
			else{
				for (int i = 0; i < fVerticesShadowCurPageTop.length; i++) {
					fVerticesShadowCurPageTop[i] = 0;				
				}
				
				for (int i = 0; i < fVerticesShadowCurPageBottom.length; i++) {
					fVerticesShadowCurPageBottom[i] = 0;				
				}				
			}
			
			shadowColor = (float)(0.9f - SHADOW_WIDTH);
			shadowColor = (shadowColor > 0.5) ? 0.5f : shadowColor;
			updateShadowColors(shadowColor);		
		}
		
		private void calculateParamsForTurnLeft_TurnToLeftBottom(){
			if (sliceNumNeedDraw < SLICE_NUM){
				bNeedDrawTurnLeft = false;
				return;
			}
			
			if (fTurnLeftTextCoorStartX_Top >= 1.0f){
				bNeedDrawTurnLeft = false;
				return;
			}
						
			bNeedDrawTurnLeft = true;
			
			if (fTurnLeftTextCoorStartY_Bottom < 1 && !MathEx.isFloatEqual(1, fTurnLeftTextCoorStartY_Bottom)){			
				fTextureCoorTurnLeft[0] = fTurnLeftTextCoorStartX_Top;
				fTextureCoorTurnLeft[1] = 0;
				fTextureCoorTurnLeft[2] = 1;
				fTextureCoorTurnLeft[3] = fTurnLeftTextCoorStartY_Bottom;
				fTextureCoorTurnLeft[4] = 1.0f;
				fTextureCoorTurnLeft[5] = 0;
				fTextureCoorTurnLeft[6] = 1.0f;
				fTextureCoorTurnLeft[7] = 0;
				
				PointF ptBottom = new PointF(fTurnLeftTextCoorStartX_Top*GL_WIDTH + left, top);
				Line lineVirtualAxis = new Line(lineTurnAxis.dirVector, ptBottom);
				PointF ptVirtualCenter = MathEx.getCenter(lineVirtualAxis.getCross(lineBottom), ptBottom);
				
				float fTopX = fTurnLeftTextCoorStartX_Top*GL_WIDTH + left;
				float fBottomY = top - fTurnLeftTextCoorStartY_Bottom*GL_HEIGHT;
				
				fVerticersTurnLeft[0] = fTopX - ptVirtualCenter.x;
				fVerticersTurnLeft[1] = top;	
				fVerticersTurnLeft[2] = 0;	
				fVerticersTurnLeft[3] = 1;	
				fVerticersTurnLeft[4] = right - ptVirtualCenter.x;
				fVerticersTurnLeft[5] = fBottomY;			
				fVerticersTurnLeft[6] = 0;	
				fVerticersTurnLeft[7] = 1;	
				fVerticersTurnLeft[8] = right - ptVirtualCenter.x;
				fVerticersTurnLeft[9] = top;	
				fVerticersTurnLeft[10] = 0;
				fVerticersTurnLeft[11] = 1;	
				fVerticersTurnLeft[12] = right - ptVirtualCenter.x;
				fVerticersTurnLeft[13] = top;	
				fVerticersTurnLeft[14] = 0;	
				fVerticersTurnLeft[15] = 1;			

				MatrixSW.rotateM(fVerticersTurnLeft, 0, -180, lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
				MatrixSW.moveM(fVerticersTurnLeft, 0, nextPosX, nextPosY, nextPosZ);	
			}
			else{						
				fTextureCoorTurnLeft[0] = fTurnLeftTextCoorStartX_Top;
				fTextureCoorTurnLeft[1] = 0;
				fTextureCoorTurnLeft[2] = fTurnLeftTextCoorStartX_Bottom;
				fTextureCoorTurnLeft[3] = 1;
				fTextureCoorTurnLeft[4] = 1.0f;
				fTextureCoorTurnLeft[5] = 1;
				fTextureCoorTurnLeft[6] = 1.0f;
				fTextureCoorTurnLeft[7] = 0;
				
				float fTopX = left + fTurnLeftTextCoorStartX_Top * GL_WIDTH;
				float fBottomX = left + fTurnLeftTextCoorStartX_Bottom * GL_WIDTH;
				float fCenter = (fTopX + fBottomX)/2;
				
				fVerticersTurnLeft[0] = fTopX - fCenter;
				fVerticersTurnLeft[1] = top;	
				fVerticersTurnLeft[2] = 0;	
				fVerticersTurnLeft[3] = 1;	
				fVerticersTurnLeft[4] = fBottomX - fCenter;
				fVerticersTurnLeft[5] = bottom;			
				fVerticersTurnLeft[6] = 0;	
				fVerticersTurnLeft[7] = 1;	
				fVerticersTurnLeft[8] = right - fCenter;	
				fVerticersTurnLeft[9] = bottom;	
				fVerticersTurnLeft[10] = 0;
				fVerticersTurnLeft[11] = 1;	
				fVerticersTurnLeft[12] = right - fCenter;	
				fVerticersTurnLeft[13] = top;	
				fVerticersTurnLeft[14] = 0;	
				fVerticersTurnLeft[15] = 1;
				
				MatrixSW.rotateM(fVerticersTurnLeft, 0, -180, lineTurnAxis.dirVector.x, lineTurnAxis.dirVector.y, 0);
				MatrixSW.moveM(fVerticersTurnLeft, 0, nextPosX, nextPosY, nextPosZ);				
			}
		}
				
		// updateControlParams
		public void updateControlParams(){
			top = GL_HEIGHT/2;
			left = -GL_WIDTH/2;
			right = GL_WIDTH/2;
			bottom = -GL_HEIGHT/2;		
			
			lineTop.makeLineByDirVectorAndFixPoint(new PointF(-1, 0), new PointF(0, top));
			lineBottom.makeLineByDirVectorAndFixPoint(new PointF(-1, 0), new PointF(0, bottom));
			lineRight.makeLineByDirVectorAndFixPoint(new PointF(0, 1), new PointF(right, 0));
			
			for (int i = 0; i < fNormalsNextPage.length; i++){
				if ((i+1)%3 == 0)	
					fNormalsNextPage[i] = 1;
				else	
					fNormalsNextPage[i] = 0;
			}
			
			for (int i = 0; i < fNormalsNotTurn.length; i++){
				if ((i+1)%3 == 0)	
					fNormalsNotTurn[i] = 1;
				else	
					fNormalsNotTurn[i] = 0;
			}
			
			for (int i = 0; i < fNormalsTurnLeft.length; i++){
				if ((i+1)%3 == 0)	
					fNormalsTurnLeft[i] = -1;
				else	
					fNormalsTurnLeft[i] = 0;
			}
			
			updateShadowColors(SHADOW_COLOR_DEFAULT);
			
			updateBuffersForPublic();
		}
		
		private void updateShadowColors(float shadowColor){
			fShadowColors[0] = shadowColor;
			fShadowColors[1] = shadowColor;
			fShadowColors[2] = shadowColor;
			fShadowColors[3] = 0.6f;
			fShadowColors[4] = shadowColor;
			fShadowColors[5] = shadowColor;
			fShadowColors[6] = shadowColor;
			fShadowColors[7] = 0.6f;
			fShadowColors[8] = shadowColor;
			fShadowColors[9] = shadowColor;
			fShadowColors[10] = shadowColor;
			fShadowColors[11] = 0.0f;
			fShadowColors[12] = shadowColor;
			fShadowColors[13] = shadowColor;
			fShadowColors[14] = shadowColor;
			fShadowColors[15] = 0.0f;	
		}
		
		private void updateBuffersForPublic(){
			bbOrder.put(bOrder);
			bbOrder.position(0);
			
			fbShadowColors.put(fShadowColors);
			fbShadowColors.position(0);
			
			fbVerticesShadowCurPageTop.put(fVerticesShadowCurPageTop);
			fbVerticesShadowCurPageTop.position(0);
			
			fbVerticesShadowCurPageBottom.put(fVerticesShadowCurPageBottom);
			fbVerticesShadowCurPageBottom.position(0);
			
			fbVerticesNextPage.put(fVerticesNextPage);
			fbVerticesNextPage.position(0);
			
			fbNormalsNextPage.put(fNormalsNextPage);
			fbNormalsNextPage.position(0);
			
			fbTexCoorNextPage.put(fTextCoorNextPage);
			fbTexCoorNextPage.position(0);			
			
			fbVerticesNotTurn.put(fVerticesNotTurn);
			fbVerticesNotTurn.position(0);
			
			fbNormalsNotTurn.put(fNormalsNotTurn);
			fbNormalsNotTurn.position(0);		

			fbTexCoorNotTurn.put(fTextCoorNotTurn);
			fbTexCoorNotTurn.position(0);	
			
			fbVerticesShadowNextPage.put(fVerticesShadowNextPage);
			fbVerticesShadowNextPage.position(0);
			
			for (int i = 0; i < SLICE_NUM; i++) {
				fbVerticesTurn[i].put(fVerticesTurn[i]);
				fbVerticesTurn[i].position(0);
				
				fbTexCoorTurn[i].put(fTextureCoorTurn[i]);
				fbTexCoorTurn[i].position(0);
				
				fbNormalsTurn[i].put(fNormalsTurn[i]);
				fbNormalsTurn[i].position(0);
			}
			
			fbVerticesTurnLeft.put(fVerticersTurnLeft);
			fbVerticesTurnLeft.position(0);
			
			fbTexCoorTurnLeft.put(fTextureCoorTurnLeft);
			fbTexCoorTurnLeft.position(0);
			
			fbNormalsTurnLeft.put(fNormalsTurnLeft);
			fbNormalsTurnLeft.position(0);
		}
	}
	
	private class AutoFlip extends Thread{
		private int STEP_NUM = 15;
		private PointF startDragVector;
		private PointF startDragPosition;
		private PointF endDragVector;
		private PointF endDragPosition;
		private int dir = DIR_INVALID;
		private int curStep = 0;
		private boolean bFlipUpdated = true;
		private boolean bWait = true;
		float fStepVector[] =  {
				  0.00f,    0.05f,    0.10f,    0.15f,    0.20f,    0.25f,  
				  0.30f,    0.35f,    0.40f,    0.45f,    0.50f,    0.55f,  
				  0.60f,    0.65f,    0.70f,    0.75f,    0.80f,    0.85f,  
				  0.90f,    0.95f,    1.00f,    1.00f,    1.00f,    1.00f,  
				  1.00f,    1.00f,    1.00f,    1.00f,    1.00f,    1.00f,  
 
		};
		float fStepPosition[] = {
				  0.00f,    0.03f,    0.07f,    0.10f,    0.14f,    0.17f,  
				  0.21f,    0.24f,    0.28f,    0.31f,    0.34f,    0.38f,  
				  0.41f,    0.45f,    0.48f,    0.52f,    0.55f,    0.59f,  
				  0.62f,    0.66f,    0.69f,    0.72f,    0.76f,    0.79f,  
				  0.83f,    0.86f,    0.90f,    0.93f,    0.97f,    1.00f,  
		};
		
		public static final int DIR_NEXT = 0;
		public static final int DIR_LAST = 1;
		public static final int DIR_FORWORD = 2;
		public static final int DIR_BACKWORD = 3;
		public static final int DIR_INVALID = -1;
		
		public AutoFlip(){
			startDragVector = new PointF(-1, 0);
			startDragPosition = new PointF(VIEW_WIDTH, VIEW_HEIGHT/2);
			endDragVector = new PointF(-1, 0);
			endDragPosition = new PointF(0, VIEW_HEIGHT/2);
			
			setStep();
		}
		
		private void setStep(){
			fStepVector[0] = 0;
			fStepPosition[0] = 0;
			for(int i = 1;i<STEP_NUM;i++){
				fStepVector[i] = 1.0f/(float)(STEP_NUM*2/3)*i;
				if(fStepVector[i]>1)fStepVector[i] = 1;
				fStepPosition[i] = 1.0f/(float)STEP_NUM*i;
				if(fStepPosition[i]>1)fStepPosition[i] = 1;
			}
			fStepVector[STEP_NUM-1] = 1;
			fStepPosition[STEP_NUM-1] = 1;
		}
		
		public void setStartDragVector(float x, float y){
			startDragVector.set(x, y);
		}
		
		public void setStartDragPosition(float x, float y){
			startDragPosition.set(x, y);
		}
		
		public void setEndDragVector(float x, float y){
			endDragVector.set(x, y);
		}
		
		public void setEndDragPosition(float x, float y){
			endDragPosition.set(x, y);
		}
		
		public void setDirection(int dir){
			this.dir = dir;
		}
		
		public synchronized void updateTouchLock(boolean lock){
			bTouchLock = lock;
		}
		
		public synchronized void updateWait(boolean wait){
			bWait = wait;
		}
		
		public synchronized void flipUpdated(boolean bUpdated){
			bFlipUpdated = bUpdated;
		}
		
		public void autoStart(){
			if (!bWait){
				return;				
			}
			
			updateTouchLock(true);
			updateWait(false);			
			curStep = 0;
			if(Gdx.graphics != null)Gdx.graphics.requestRendering();
		}

		@Override
		public void run(){
			while (true) {
				while (bWait){
					try {
						Thread.sleep(25);						
					}
					catch (Exception e){
					}		
				}
				
				while (curStep < STEP_NUM){					
					float vX = (int)(startDragVector.x + fStepVector[curStep]*(endDragVector.x - startDragVector.x));
					float vY = (int)(startDragVector.y + fStepVector[curStep]*(endDragVector.y - startDragVector.y));
					float pX = (int)(startDragPosition.x + fStepPosition[curStep]*(endDragPosition.x - startDragPosition.x));
					float pY = (int)(startDragPosition.y + fStepPosition[curStep]*(endDragPosition.y - startDragPosition.y));
					
					while (!bFlipUpdated){
						try {
							Thread.sleep(10);						
						}
						catch (Exception e){
						}		
					}
					if(dir == DIR_LAST && curStep == 0){
						curIndex--;
					}
//					if(curStep == STEP_NUM - 1){
//						vX = (int)vX;
//						vY = (int)vY;
//						pX = (int)pX;
//						pY = (int)pY;
//					}
//					Log.d("launcher", vX+","+vY+","+pX+","+pY);
					//vX = -712;vY = 0;pX = -480;pY = 772;
					updateDragParams(vX, vY, pX, pY);
					flipUpdated(false);
					curStep++;
					if(Gdx.graphics != null)Gdx.graphics.requestRendering();
				}
				if(dir == DIR_NEXT || dir == DIR_FORWORD){
					curIndex++;
					updateDragParams(-1, 0, VIEW_WIDTH, VIEW_HEIGHT);
				}
				dir = DIR_INVALID;
				updateTouchLock(false);
				updateWait(true);		
				
			}
		}
	}
	
	private static class DragVectorLimit{
		private static final float VECTOR_MAX_TOP = 3;
		private static final float VECTOR_MIN_TOP = -0.1f;
		private static final float VECTOR_MAX_BOTTOM = 0.1f;
		private static final float VECTOR_MIN_BOTTOM = -3;
		private static final float VECTOR_MAX_CENTER = 0.1f;
		private static final float VECTOR_MIN_CENTER = -0.1f;
		
		public static float getMaxLimit(int y){
			float factor = 0;
			
			if (y < VIEW_HEIGHT/2){
				factor = ((float)VIEW_HEIGHT/2 - y)/(VIEW_HEIGHT/2);
				return (VECTOR_MAX_CENTER + factor*(VECTOR_MAX_TOP-VECTOR_MAX_CENTER));				
			}			
			else{
				factor = ((float)y - VIEW_HEIGHT/2)/(VIEW_HEIGHT/2);		
				return (VECTOR_MAX_CENTER + factor*(VECTOR_MAX_BOTTOM-VECTOR_MAX_CENTER));							
			}			
		}
		
		public static float getMinLimit(int y){
			float factor = 0;
			
			if (y < VIEW_HEIGHT/2){
				factor = ((float)VIEW_HEIGHT/2 - y)/(VIEW_HEIGHT/2);
				return (VECTOR_MIN_CENTER + factor*(VECTOR_MIN_TOP-VECTOR_MIN_CENTER));				
			}			
			else{
				factor = ((float)y - VIEW_HEIGHT/2)/(VIEW_HEIGHT/2);		
				return (VECTOR_MIN_CENTER + factor*(VECTOR_MIN_BOTTOM-VECTOR_MIN_CENTER));							
			}				
		}
	}

	public void setLauncher(iLoongLauncher launcher) {
		this.launcher = launcher;
	}
}
