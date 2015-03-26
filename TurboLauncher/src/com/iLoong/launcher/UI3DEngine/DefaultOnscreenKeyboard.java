package com.iLoong.launcher.UI3DEngine;


import com.badlogic.gdx.Gdx;


public class DefaultOnscreenKeyboard implements OnscreenKeyboard
{
	
	@Override
	public void show(
			boolean visible )
	{
		Gdx.input.setOnscreenKeyboardVisible( visible );
	}
}
