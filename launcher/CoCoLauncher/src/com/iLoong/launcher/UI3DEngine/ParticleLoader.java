package com.iLoong.launcher.UI3DEngine;

import com.badlogic.gdx.files.FileHandle;
import com.iLoong.launcher.theme.ThemeManager;

/**
 * @author zhenNan.ye
 *
 */
public class ParticleLoader {
	
	/** 粒子特效类型名称数组
	 * 必须与粒子特效文件名称数组 PARTICLE_TYPE_FILE 顺序一致*/
	public static final String PARTICLE_TYPE[] = {"startDrag", 
		"drag", 
		"drop",
		"clickIcon",
		"clickWorkspace",
		"edge_left",
		"edge_right",
		"fingerMoving"
		};
	public static final String PARTICLE_TYPE_FILE[] = {"theme/particle/ZhuaQi.p",
		"theme/particle/TuoDong.p",
		"theme/particle/FangXia.p",
		"theme/particle/DianJiTuBiao.p",
		"theme/particle/DianJiZhuoMian.p",
		"theme/particle/CeBian_left.p",
		"theme/particle/CeBian_right.p",
		"theme/particle/ShouZhiHuaDong.p"
		};
	
	public static void loadAllParticleEffect()
	{
		ParticleManager manager = ParticleManager.getParticleManager();
  
		for (int i = 0, length = PARTICLE_TYPE.length; i < length; i++)
		{
			ParticleFileHandle fileHandle = new ParticleFileHandle();
			ThemeManager.getInstance().getFileHandle(PARTICLE_TYPE_FILE[i], fileHandle);
			if (fileHandle.effectFile != null && fileHandle.imagesDir != null)
			{
				manager.load(PARTICLE_TYPE[i], fileHandle.effectFile, fileHandle.imagesDir);
			}
		}
		
		ParticleManager.particleManagerEnable = ParticleManager.switchForTheme = manager.partilceCanRander();
	}
	 
	public static void freeAllParticleEffect()
	{
		ParticleManager manager = ParticleManager.getParticleManager();
		
		for (int i = 0, length = PARTICLE_TYPE.length; i < length; i++)
		{
			manager.free(PARTICLE_TYPE[i]);
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
