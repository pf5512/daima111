package com.iLoong.launcher.UI3DEngine.adapter;


import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.GLU;


public class CooGdx
{
	
	public AndroidApplication app;
	public Graphics graphics;
	public Audio audio;
	public Input input;
	public Files files;
	public GLCommon gl;
	public GL10 gl10;
	public GL11 gl11;
	public GL20 gl20;
	public GLU glu;
	public Gdx gdx;
	
	public CooGdx(
			AndroidApplication app )
	{
		this.app = app;
		this.graphics = Gdx.graphics;
		this.audio = Gdx.audio;
		this.input = Gdx.input;
		this.files = Gdx.files;
		this.gl = Gdx.gl;
		this.gl10 = Gdx.gl10;
		this.gl11 = Gdx.gl11;
		this.gl20 = Gdx.gl20;
		this.glu = Gdx.glu;
	}
}
