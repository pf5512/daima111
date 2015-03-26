package com.iLoong.launcher.UI3DEngine;


import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.launcher.UI3DEngine.ParticleAnim.ParticleCallback;


/**
 * @author zhenNan.ye
 *
 */
public class ParticleManager
{
	
	/** 粒子特效总开关 */
	public static boolean particleManagerEnable;
	/** 对于特定主题的粒子特效开关 */
	public static boolean switchForTheme;
	public static boolean disableClickIcon;
	public static boolean dropEnable;
	/** 各种粒子特效类型名称 */
	public static final String PARTICLE_TYPE_NAME_START_DRAG = "startDrag"; // 抓起
	public static final String PARTICLE_TYPE_NAME_DRAG = "drag"; // 拖动
	public static final String PARTICLE_TYPE_NAME_DROP = "drop"; // 放下
	public static final String PARTICLE_TYPE_NAME_CLICK_ICON = "clickIcon"; // 点击图标
	public static final String PARTICLE_TYPE_NAME_CLICK_WORKSPACE = "clickWorkspace";// 点击桌面
	public static final String PARTICLE_TYPE_NAME_EDGE_LEFT = "edge_left"; // 左侧边效果
	public static final String PARTICLE_TYPE_NAME_EDGE_RIGHT = "edge_right"; // 右侧边效果
	public static final String PARTICLE_TYPE_NAME_FINGER_MOVING = "fingerMoving"; // 手指滑动
	public static final String PARTICLE_TYPE_NAME_HEART = "nightclub";
	public static final String PARTICLE_TYPE_NAME_STAR = "star";
	public static final String PARTICLE_TYPE_NAME_WATER = "clover";
	public static final String PARTICLE_TYPE_NAME_LOVE = "leaf";
	/** 实例对象 */
	private static ParticleManager particleManager = null;
	private HashMap<String , ParticleEffectPool> particlePoolsMap;
	private HashMap<String , ParticleEffect> particleEffectsMap;
	private ArrayList<ParticleAnim> particleAnimList;
	private boolean finishAllAnim = true;
	
	private ParticleManager()
	{
		particlePoolsMap = new HashMap<String , ParticleEffectPool>( 8 );
		particleEffectsMap = new HashMap<String , ParticleEffect>( 8 );
		particleAnimList = new ArrayList<ParticleAnim>( 3 );
	}
	
	/**
	 * 获得粒子特效管理的单实例对象
	 * @return
	 */
	public static synchronized ParticleManager getParticleManager()
	{
		if( particleManager == null )
		{
			particleManager = new ParticleManager();
		}
		return particleManager;
	}
	
	public void load(
			String particleType ,
			FileHandle effectFile ,
			FileHandle imagesDir )
	{
		ParticleEffect effect = new ParticleEffect();
		effect.load( effectFile , imagesDir );
		particleEffectsMap.put( particleType , effect );
		ParticleEffectPool effectPool = new ParticleEffectPool( effect , 2 , 5 );
		particlePoolsMap.put( particleType , effectPool );
	}
	
	public void free(
			String particleType )
	{
		ParticleEffect effect = particleEffectsMap.get( particleType );
		if( effect != null )
		{
			effect.dispose();
		}
		particleEffectsMap.remove( particleType );
		ParticleEffectPool effectPool = particlePoolsMap.get( particleType );
		if( effect != null )
		{
			effectPool.clear();
		}
		particlePoolsMap.remove( particleType );
	}
	
	public void clear()
	{
		particlePoolsMap.clear();
		particleEffectsMap.clear();
		particleAnimList.clear();
	}
	
	public boolean partilceCanRander()
	{
		return !particlePoolsMap.isEmpty();
	}
	
	public ParticleAnim start(
			Object target ,
			String particleType ,
			float positionX ,
			float positionY )
	{
		ParticleEffectPool effectPool = particlePoolsMap.get( particleType );
		if( effectPool != null )
		{
			ParticleEffect effect = effectPool.obtain();
			ParticleAnim anim = new ParticleAnim( target , effect , particleType );
			if( !particleAnimList.contains( anim ) )
			{
				anim.setPosition( positionX , positionY );
				anim.callCallback( ParticleCallback.START );
				particleAnimList.add( anim );
				Gdx.graphics.requestRendering();
				return anim;
			}
		}
		return null;
	}
	
	public void stop(
			Object target ,
			String particleType )
	{
		int size = particleAnimList.size();
		for( int i = size - 1 ; i >= 0 ; i-- )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim.getTarget().equals( target ) && anim.getParticleType().equals( particleType ) )
			{
				particleAnimList.remove( i );
				break;
			}
		}
	}
	
	public void pause(
			Object target ,
			String particleType )
	{
		int size = particleAnimList.size();
		for( int i = size - 1 ; i >= 0 ; i-- )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim.getTarget().equals( target ) && anim.getParticleType().equals( particleType ) )
			{
				anim.stop();
				break;
			}
		}
	}
	
	public boolean isFinish(
			Object target ,
			String particleType )
	{
		int size = particleAnimList.size();
		for( int i = size - 1 ; i >= 0 ; i-- )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim.getTarget().equals( target ) && anim.getParticleType().equals( particleType ) )
			{
				if( anim.isFinish() )
				{
					particleAnimList.remove( i );
					anim.callCallback( ParticleCallback.END );
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
	
	//teapotXu add start
	/*
	 * stop all the animations of particleManager 
	*/
	public void stopAllAnims()
	{
		synchronized( particleAnimList )
		{
			int size = particleAnimList.size();
			for( int i = size - 1 ; i >= 0 ; i-- )
			{
				ParticleAnim anim = particleAnimList.get( i );
				if( anim != null )
				{
					anim.stop();
					particleAnimList.remove( i );
					size = particleAnimList.size();
					i = size - 1;
				}
			}
		}
	}
	
	/*
	 * Whether there is any particle animation running except its own.
	 * 
	 * */
	public boolean isAnyParticleAnimPlayingExpSelf(
			Object self_target ,
			String particleType )
	{
		boolean exist_anim_playing = false;
		int size = particleAnimList.size();
		for( int i = size - 1 ; i >= 0 ; i-- )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim == null )
				continue;
			if( ( self_target == null && particleType == null ) || ( self_target == null && anim.getParticleType().equals( particleType ) ) || ( particleType == null && !anim.getTarget().equals(
					self_target ) ) || ( !anim.getTarget().equals( self_target ) && anim.getParticleType().equals( particleType ) ) )
			{
				if( anim.isFinish() )
				{
					continue;
				}
				else
				{
					exist_anim_playing = true;
					break;
				}
			}
		}
		return exist_anim_playing;
	}
	
	public void draw(
			Object target ,
			String particleType ,
			SpriteBatch batch ,
			float delta )
	{
		boolean hasAnim = false;
		int size = particleAnimList.size();
		for( int i = size - 1 ; i >= 0 ; i-- )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim.getTarget().equals( target ) && anim.getParticleType().equals( particleType ) )
			{
				hasAnim = true;
				if( finishAllAnim )
				{
					finishAllAnim = false;
				}
				anim.draw( batch , delta );
				break;
			}
		}
		if( !hasAnim )
		{
			finishAllAnim = true;
		}
		if( !finishAllAnim )
		{
			Gdx.graphics.requestRendering();
		}
	}
	
	public void update(
			Object target ,
			String particleType ,
			float positionX ,
			float positionY )
	{
		do
		{
			int size = particleAnimList.size();
			if( size <= 0 )
			{
				start( target , particleType , positionX , positionY );
				break;
			}
			for( int i = size - 1 ; i >= 0 ; i-- )
			{
				ParticleAnim anim = particleAnimList.get( i );
				if( anim.getTarget().equals( target ) && anim.getParticleType().equals( particleType ) )
				{
					anim.start();
					anim.setPosition( positionX , positionY );
					break;
				}
				else
				{
					start( target , particleType , positionX , positionY );
					break;
				}
			}
		}
		while( false );
	}
	
	public void newLoad(
			String particleType ,
			FileHandle effectFile ,
			FileHandle imagesDir ,
			float wRate ,
			float hRate )
	{
		ParticleEffect effect = new ParticleEffect();
		effect.load( effectFile , imagesDir );
		matchParticle( effect , wRate , hRate );
		particleEffectsMap.put( particleType , effect );
		ParticleEffectPool effectPool = new ParticleEffectPool( effect , 2 , 5 );
		particlePoolsMap.put( particleType , effectPool );
	}
	
	public void newDraw(
			Object target ,
			String particleType ,
			SpriteBatch batch ,
			float delta )
	{
		int size = particleAnimList.size();
		for( int i = size - 1 ; i >= 0 ; i-- )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim.getTarget().equals( target ) && anim.getParticleType().equals( particleType ) )
			{
				anim.draw( batch , delta );
			}
		}
		if( particleAnimList != null && particleAnimList.size() != 0 )
		{
			Gdx.graphics.requestRendering();
		}
	}
	
	public ParticleAnim newStart(
			Object target ,
			String particleType ,
			float positionX ,
			float positionY )
	{
		ParticleEffectPool effectPool = particlePoolsMap.get( particleType );
		if( effectPool != null && particleAnimList.size() < ConfigBase.particle_max_num )
		{
			ParticleEffect effect = effectPool.obtain();
			ParticleAnim anim = new ParticleAnim( target , effect , particleType );
			{
				anim.start();
				anim.setPosition( positionX , positionY );
				anim.callCallback( ParticleCallback.START );
				anim.particleStartTime = System.currentTimeMillis();
				particleAnimList.add( anim );
				Gdx.graphics.requestRendering();
				return anim;
			}
		}
		return null;
	}
	
	public void newStop(
			Object target ,
			String particleType )
	{
		int size = particleAnimList.size();
		for( int i = 0 ; i < size ; i++ )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim.getTarget().equals( target ) && anim.getParticleType().equals( particleType ) )
			{
				particleAnimList.remove( i );
				break;
			}
		}
	}
	
	public void newUpdate(
			Object target ,
			String particleType ,
			float positionX ,
			float positionY )
	{
		int size = particleAnimList.size();
		for( int i = size - 1 ; i >= 0 ; i-- )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim.getTarget().equals( target ) && anim.getParticleType().equals( particleType ) )
			{
				anim.setPosition( positionX , positionY );
				break;
			}
		}
	}
	
	public void clearParticleAnimList()
	{
		int size = particleAnimList.size();
		for( int i = 0 ; i < size ; i++ )
		{
			ParticleAnim anim = particleAnimList.get( i );
			if( anim.getParticleEffect().isComplete() || ( System.currentTimeMillis() - anim.particleStartTime > 3000 ) )
			{
				anim.stop();
				anim = null;
			}
			if( anim == null )
			{
				particleAnimList.remove( i );
				i = 0;
				size = particleAnimList.size();
			}
		}
	}
	
	public boolean particleAnimListNeedStart()
	{
		clearParticleAnimList();
		if( particleAnimList.size() == 0 )
		{
			return true;
		}
		else if( particleAnimList.size() > 0 )
		{
			int psize = particleAnimList.size();
			if( psize < ConfigBase.longpress_particle_max_num + 1 )
			{
				ParticleEffect iparticleEffect = particleAnimList.get( psize - 1 ).getParticleEffect();
				float maxlifetime = 0;
				float curlifetime = 0;
				for( int j = 0 ; j < iparticleEffect.getEmitters().size ; j++ )
				{
					curlifetime = iparticleEffect.getEmitters().get( j ).getLife().getHighMax();
					if( curlifetime > maxlifetime )
					{
						maxlifetime = curlifetime;
					}
				}
				if( System.currentTimeMillis() - particleAnimList.get( psize - 1 ).particleStartTime > maxlifetime / ( ConfigBase.longpress_particle_max_num ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private void matchParticle(
			ParticleEffect particleEffect ,
			float wRate ,
			float hRate )
	{
		for( int i = 0 ; i < particleEffect.getEmitters().size ; i++ )
		{
			float XLowMin = particleEffect.getEmitters().get( i ).getXOffsetValue().getLowMin() * wRate;
			float XLowMax = particleEffect.getEmitters().get( i ).getXOffsetValue().getLowMax() * wRate;
			float YLowMin = particleEffect.getEmitters().get( i ).getYOffsetValue().getLowMin() * hRate;
			float YLowMax = particleEffect.getEmitters().get( i ).getYOffsetValue().getLowMax() * hRate;
			particleEffect.getEmitters().get( i ).getXOffsetValue().setLow( XLowMin , XLowMax );
			particleEffect.getEmitters().get( i ).getYOffsetValue().setLow( YLowMin , YLowMax );
			if( particleEffect.getEmitters().get( i ).getName().equalsIgnoreCase( "petal1" ) )
			{
				particleEffect.getEmitters().get( i ).getWind().setActive( false );
			}
			particleEffect.getEmitters().get( i ).getVelocity().setHighMax( particleEffect.getEmitters().get( i ).getVelocity().getHighMax() * hRate );
			particleEffect.getEmitters().get( i ).getVelocity().setHighMin( particleEffect.getEmitters().get( i ).getVelocity().getHighMin() * hRate );
			particleEffect.getEmitters().get( i ).getDuration().setLow( 200 , 200 );
			particleEffect.getEmitters().get( i ).getSprite().getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
	}
}
