package com.iLoong.launcher.UI3DEngine;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.badlogic.gdx.files.FileHandle;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


/**
 * @author zhenNan.ye
 * 
 */
public class ParticleLoader
{
	
	/**
	 * 粒子特效类型名称数组 必须与粒子特效文件名称数组 PARTICLE_TYPE_FILE 顺序一致
	 */
	public static final String PARTICLE_TYPE[] = { "startDrag" , "drag" , "drop" , "clickIcon" , "clickWorkspace" , "edge_left" , "edge_right" , "fingerMoving" };
	public static final String PARTICLE_TYPE_FILE[] = {
			"theme/particle/ZhuaQi.p" ,
			"theme/particle/TuoDong.p" ,
			"theme/particle/FangXia.p" ,
			"theme/particle/DianJiTuBiao.p" ,
			"theme/particle/DianJiZhuoMian.p" ,
			"theme/particle/CeBian_left.p" ,
			"theme/particle/CeBian_right.p" ,
			"theme/particle/ShouZhiHuaDong.p" };
	public static final String NEW_PARTICLE_TYPE[] = { "nightclub" , "star" , "clover" , "leaf" , };
	public static final String NEW_PARTICLE_TYPE_FILE[] = { "theme/newparticle/nightclub.p" , "theme/newparticle/star.p" , "theme/newparticle/clover.p" , "theme/newparticle/leaf.p" , };
	public static final float screenWidth = 1080;
	public static final float screenHeight = 1920;
	
	public static void loadAllParticleEffect()
	{
		ParticleManager manager = ParticleManager.getParticleManager();
		for( int i = 0 , length = PARTICLE_TYPE.length ; i < length ; i++ )
		{
			ParticleFileHandle fileHandle = new ParticleFileHandle();
			ThemeManager.getInstance().getFileHandle( PARTICLE_TYPE_FILE[i] , fileHandle );
			if( DefaultLayout.enable_new_particle )
			{
				ThemeManager.getInstance().getFileHandle( PARTICLE_TYPE_FILE[i] , fileHandle );
			}
			if( fileHandle.effectFile != null && fileHandle.imagesDir != null )
			{
				manager.load( PARTICLE_TYPE[i] , fileHandle.effectFile , fileHandle.imagesDir );
			}
		}
		if( DefaultLayout.enable_new_particle )
		{
			for( int i = 0 , length = NEW_PARTICLE_TYPE.length ; i < length ; i++ )
			{
				ParticleFileHandle fileHandle = new ParticleFileHandle();
				ThemeManager.getInstance().getFileHandle( NEW_PARTICLE_TYPE_FILE[i] , fileHandle );
				if( fileHandle.effectFile != null && fileHandle.imagesDir != null )
				{
					float wRate = Utils3D.getScreenWidth() / screenWidth;
					float hRate = Utils3D.getScreenHeight() / screenHeight;
					manager.newLoad( NEW_PARTICLE_TYPE[i] , fileHandle.effectFile , fileHandle.imagesDir , wRate , hRate );
				}
			}
		}
		// teapotXu add start
		ParticleManager.switchForTheme = manager.partilceCanRander();
		if( ParticleManager.switchForTheme )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
			ParticleManager.particleManagerEnable = prefs.getBoolean(
					iLoongLauncher.getInstance().getResources().getString( RR.string.setting_key_particle ) ,
					DefaultLayout.default_particle_settings_value );
		}
		else
		{
			ParticleManager.particleManagerEnable = false;
		}
		// teapotXu add end
		// ParticleManager.particleManagerEnable =
		// ParticleManager.switchForTheme = manager.partilceCanRander();
	}
	
	public static void OnThemeChanged()
	{
		ParticleManager manager = ParticleManager.getParticleManager();
		manager.clear();
		loadAllParticleEffect();
	}
	
	public static void freeAllParticleEffect()
	{
		ParticleManager manager = ParticleManager.getParticleManager();
		for( int i = 0 , length = PARTICLE_TYPE.length ; i < length ; i++ )
		{
			manager.free( PARTICLE_TYPE[i] );
		}
		for( int i = 0 , length = NEW_PARTICLE_TYPE_FILE.length ; i < length ; i++ )
		{
			manager.free( NEW_PARTICLE_TYPE_FILE[i] );
		}
		manager.clear();
	}
	
	public static class ParticleFileHandle
	{
		
		public FileHandle effectFile;
		public FileHandle imagesDir;
		
		public ParticleFileHandle()
		{
			effectFile = null;
			imagesDir = null;
		}
	}
}
